package com.continuum.serviceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.continuum.constants.PortalConstants;
import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.service.CustomerService;
import com.continuum.service.ReturnOrderService;
import com.continuum.tenant.repos.entity.AuditLog;
import com.continuum.tenant.repos.entity.ClientConfig;
import com.continuum.tenant.repos.entity.OrderAddress;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.entity.ReturnType;
import com.continuum.tenant.repos.entity.RmaInvoiceInfo;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.AuditLogRepository;
import com.continuum.tenant.repos.repositories.ClientConfigRepository;
import com.continuum.tenant.repos.repositories.QuestionRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderItemRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
import com.continuum.tenant.repos.repositories.ReturnTypeRepository;
import com.continuum.tenant.repos.repositories.RmaInvoiceInfoRepository;
import com.continuum.tenant.repos.repositories.UserRepository;
import com.di.commons.dto.CustomerDTO;
import com.di.commons.dto.ReturnOrderDTO;
import com.di.commons.dto.ReturnOrderItemDTO;
import com.di.commons.dto.RmaInvoiceInfoDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.mapper.ReturnOrderMapper;
import com.di.commons.mapper.RmaInvoiceInfoMapper;
import com.di.integration.p21.service.P21InvoiceService;
import com.di.integration.p21.service.P21ReturnOrderService;
import com.di.integration.p21.transaction.P21RMAResponse;

@Service
public class ReturnOrderServiceImpl implements ReturnOrderService {

	private static final Logger logger = LoggerFactory.getLogger(ReturnOrderServiceImpl.class);

	@Autowired
	ReturnOrderRepository returnOrderRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	ReturnOrderItemRepository returnOrderItemRepository;

	@Autowired
	AuditLogRepository auditLogRepository;

	@Autowired
	ClientConfigRepository clientConfigRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	QuestionRepository questionRepository;

	@Autowired
	RmaInvoiceInfoMapper rmaInvoiceInfoMapper;

	@Autowired
	RmaInvoiceInfoRepository rmaInvoiceInfoRepository;

	@Autowired
	P21InvoiceService p21InvoiceService;

	@Autowired
	ReturnOrderMapper returnOrderMapper;

	@Autowired
	P21ReturnOrderService p21ReturnOrderService;

	@Autowired
	CustomerService customerService;

	@Autowired
	EmailSender emailSender;

	@Autowired
	ReturnTypeRepository returnTypeRepository;
	
	@Autowired
	EmailTemplateRenderer emailTemplateRenderer ;

	ReturnOrder returnOrder;

	@Autowired
	ClientConfig clientConfig;

	@Autowired
	AuditLogServiceImpl auditLogServiceImpl;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	public P21RMAResponse createReturnOrder(ReturnOrderDTO returnOrderDTO) throws Exception {
		// Create RMA in p21
		P21RMAResponse p21RMARespo = p21ReturnOrderService.createReturnOrder(returnOrderDTO);

		logger.info("orderNo::: " + p21RMARespo.getRmaOrderNo() + " status: " + p21RMARespo.getStatus());

		return p21RMARespo;
	}

	// @Async
	public void crateReturnOrderInDB(ReturnOrderDTO returnOrderDTO, P21RMAResponse p21RMARespo)
			throws MessagingException {
		returnOrderDTO.setRmaOrderNo(p21RMARespo.getRmaOrderNo());
		returnOrderDTO.setStatus(PortalConstants.RETURN_REQUESTED);
		returnOrderDTO.setOrderDate(new Date());
		returnOrderDTO.setCreatedDate(new Date());
		returnOrderDTO.setRequestedDate(new Date());
		// returnOrderDTO.setStatus(p21RMARespo.getStatus());

		String Status = p21RMARespo.getStatus();
		if (Status.equals(PortalConstants.SUCCESS)) {
			returnOrderDTO.setStatus(PortalConstants.RETURN_REQUESTED);
			logger.info("Setting status to:: '{}'", PortalConstants.RETURN_REQUESTED);

		} else {
			returnOrderDTO.setStatus(PortalConstants.FAILED);
			returnOrderDTO.setIsAuthorized(false);
			returnOrderDTO.setIsEditable(false);
			for (ReturnOrderItemDTO returnOrderItem : returnOrderDTO.getReturnOrderItem()) {
				returnOrderItem.setIsAuthorized(false);
				returnOrderItem.setIsEditable(false);
				returnOrderItem.setStatus(PortalConstants.FAILED);
			}
			logger.info("Setting status to:: '{}'", PortalConstants.FAILED);
		}
		logger.info(returnOrderDTO.getCustomer().getCustomerId());
		CustomerDTO customerDTO = customerService.findbyCustomerId(returnOrderDTO.getCustomer().getCustomerId());
		if (customerDTO == null) {
			customerDTO = new CustomerDTO();
			customerDTO.setCustomerId(returnOrderDTO.getCustomer().getCustomerId());
			try {
				customerDTO = customerService.createCustomer(customerDTO);
			} catch (Exception e) {
				logger.error("Customer");
			}
		}
		returnOrderDTO.setCustomer(customerDTO);
		returnOrderDTO.setIsSalesRepLinked(false);
		ReturnOrder returnOrder = returnOrderMapper.returnOrderDTOToReturnOrder(returnOrderDTO);

		for (ReturnOrderItem returnOrderItem : returnOrder.getReturnOrderItem()) {
			returnOrderItem.setShipTo(null);
			returnOrderItem.setIsActive(true);
			returnOrderItem.setInvoiceNo(returnOrderItem.getInvoiceNo());
			if (returnOrderItem.getReturnAmount() == null && returnOrderItem.getReStockingAmount() == null) {

				returnOrderItem.setReStockingAmount(new BigDecimal(0));
				returnOrderItem
						.setReturnAmount(returnOrderItem.getAmount().subtract(returnOrderItem.getReStockingAmount()));
			}
		}
		returnOrderRepository.save(returnOrder);
		RmaInvoiceInfo rmaInvoiceInfo = new RmaInvoiceInfo();

		rmaInvoiceInfo.setRmaOrderNo(returnOrderDTO.getRmaOrderNo());
		rmaInvoiceInfo.setInvoiceLinked(false);
		rmaInvoiceInfo.setDescription("none");
		rmaInvoiceInfo.setRetryCount(0);
		rmaInvoiceInfo.setDocumentLinked(false);
		rmaInvoiceInfo.setReturnOrder(returnOrder);
		rmaInvoiceInfoRepository.save(rmaInvoiceInfo);

		// audit log

		AuditLog auditlog = new AuditLog();

		auditlog.setRmaNo(p21RMARespo.getRmaOrderNo());
		String described = getRmaaQualifier() + " " + returnOrder.getRmaOrderNo()
				+ " has been updated to 'Return Requested'." + ";" + "Email has been sent to the "
				+ returnOrderDTO.getContact().getContactEmailId();
		auditlog.setDescription(described);
		auditlog.setHighlight("Return Requested");
		auditlog.setStatus("Inbox");
		auditlog.setTitle("Return Order");
		auditlog.setRmaNo(p21RMARespo.getRmaOrderNo());
		auditlog.setUserName(customerDTO.getDisplayName());
		auditLogRepository.save(auditlog);

		// String recipient = returnOrder.getCustomer().getEmail();

//		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
		String tenentId= httpServletRequest.getHeader("tenant");
		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
		String recipient = "";

		if (masterTenant.getIsProd()) {
			recipient = returnOrderDTO.getContact().getContactEmailId();
		} else {
			recipient = PortalConstants.EMAIL_RECIPIENT;

		}

//		String email = returnOrderDTO.getContact().getContactEmailId();
//		if(email.equalsIgnoreCase("alex@gocontinuum.ai")) {
//			recipient="alex@gocontinuum.ai";
//		}
		String subject = PortalConstants.EMAIL_SUBJECT_PREFIX + " " + getRmaaQualifier() + " "
				+ returnOrderDTO.getRmaOrderNo() + " has been Requested";

//		emailSender.sendEmail(recipient, subject, body, returnOrderDTO, customerDTO);
		HashMap<String, String> map = new HashMap<>();
		if (returnOrderDTO.getStatus().equalsIgnoreCase(PortalConstants.RETURN_REQUESTED)) {
			map.put("RMA_QUALIFIER", getRmaaQualifier());
			map.put("RMA_NO", returnOrderDTO.getRmaOrderNo());
			map.put("CUST_NAME", returnOrderDTO.getCustomer().getDisplayName());
			map.put("CLIENT_MAIL", getClientConfig().getEmailFrom());
			map.put("CLIENT_PHONE", String.valueOf(getClientConfig().getClient().getContactNo()));
		} else {
			map.put("status", returnOrderDTO.getStatus());
			map.put("rma_order_no", null);
		}

		map.put("order_contact_name", customerDTO.getDisplayName());
		map.put("order_no", returnOrderDTO.getOrderNo());
		String template = emailTemplateRenderer.getTemplateContent();
		emailSender.sendEmail(recipient, template, subject, map);

	}

	@Override
	public List<ReturnOrderDTO> getReturnOrdersBySearchCriteria(OrderSearchParameters orderSearchParameters) {

		Specification<ReturnOrder> spec = Specification.where(null);

		if (isNotNullAndNotEmpty(orderSearchParameters.getZipcode())) {
			Specification<ReturnOrder> zipcodeSpec = (root, query, builder) -> {
				Join<ReturnOrder, OrderAddress> addressJoin = root.join("billTo");
				Predicate zipcodePredicate = builder.equal(addressJoin.get("zipcode"),
						orderSearchParameters.getZipcode());
				return builder.and(zipcodePredicate);
			};
			spec = spec.and(zipcodeSpec);
		}
		if (isNotNullAndNotEmpty(orderSearchParameters.getCustomerId())) {
			Specification<ReturnOrder> customerIdSpec = (root, query, builder) -> {
				Join<ReturnOrder, OrderAddress> addressJoin = root.join("customer");
				Predicate customerIdPredicate = builder.equal(addressJoin.get("customerId"),
						orderSearchParameters.getCustomerId());
				return builder.and(customerIdPredicate);
			};
			spec = spec.and(customerIdSpec);
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getPoNo())) {
			Specification<ReturnOrder> poNoSpec = (root, query, builder) -> builder.equal(root.get("PONumber"),
					orderSearchParameters.getPoNo());
			spec = spec.and(poNoSpec);
		}

		if (isNotNullAndNotEmpty(orderSearchParameters.getInvoiceNo())) {
			Specification<ReturnOrder> poNoSpec = (root, query, builder) -> builder.equal(root.get("invoiceNo"),
					orderSearchParameters.getInvoiceNo());
			spec = spec.and(poNoSpec);
		}

		List<ReturnOrder> poList = returnOrderRepository.findAll(spec);
		List<ReturnOrderDTO> poDTOList = new ArrayList<>();
		poList.forEach(returnOrder -> {
			poDTOList.add(returnOrderMapper.returnOrderToReturnOrderDTO(returnOrder));
		});
		return poDTOList;
	}

	public boolean isNotNullAndNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}

//	@Override
//	public List<ReturnOrderDTO> getAllReturnOrder() {
//		List<ReturnOrder> returnOrderEntities = repository.findAll();
//
//		List<ReturnOrderDTO> returnOrderDTOs = returnOrderEntities.stream()
//				.map(returnOrderMapper::returnOrderToReturnOrderDTO).collect(Collectors.toList());
//
//		return returnOrderDTOs;
//	}

	@Override
	public List<ReturnOrderDTO> getAllReturnOrder(Long userId) {

		Optional<User> optionalUser = userRepository.findById(userId);
		List<ReturnOrderDTO> returnOrderDTOs = null;
		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			if (user.getRole().getId() == 1 || user.getRole().getId() == 2) {
				List<ReturnOrder> returnOrderEntities = returnOrderRepository.findAll();

				returnOrderDTOs = returnOrderEntities.stream()

						.map(returnOrderMapper::returnOrderToReturnOrderDTO).collect(Collectors.toList());

//				List<ReturnOrderDTO> returnOrderDTOList = returnOrder.stream()

//			            .map(returnOrderMapper::returnOrderToReturnOrderDTO)

//			            .collect(Collectors.toList());

				for (ReturnOrderDTO returnOrderDTO : returnOrderDTOs) {

					List<ReturnOrderItemDTO> returnOrderItems = returnOrderDTO.getReturnOrderItem();

					if (returnOrderItems != null && !returnOrderItems.isEmpty()) {

						Date currentDate = new Date(); // Current date

						List<Date> upcomingDates = returnOrderItems.stream()

								.map(returnOrderItemDTO -> returnOrderItemDTO.getFollowUpDate()) // Use
																									// ReturnOrderItemDTO

								.filter(date -> date != null && date.after(currentDate))

								.collect(Collectors.toList());

						upcomingDates.sort(Date::compareTo);

						if (!upcomingDates.isEmpty()) {

							returnOrderDTO.setNextActivityDate(upcomingDates.get(0));

						}

					}

				}
			} else {
				List<ReturnOrder> returnOrder = returnOrderRepository.findByUserId(userId);
				List<ReturnOrder> returnOrder1 = returnOrderRepository.findAll();
				for (ReturnOrder ro : returnOrder1) {
					if (ro.getUser() == null) {
						returnOrder.add(ro);
					}
				}
				returnOrderDTOs = returnOrder.stream()

						.map(returnOrderMapper::returnOrderToReturnOrderDTO).collect(Collectors.toList());

				for (ReturnOrderDTO returnOrderDTO : returnOrderDTOs) {

					List<ReturnOrderItemDTO> returnOrderItems = returnOrderDTO.getReturnOrderItem();

					if (returnOrderItems != null && !returnOrderItems.isEmpty()) {

						Date currentDate = new Date(); // Current date

						List<Date> upcomingDates = returnOrderItems.stream()

								.map(returnOrderItemDTO -> returnOrderItemDTO.getFollowUpDate()) // Use
																									// ReturnOrderItemDTO

								.filter(date -> date != null && date.after(currentDate))

								.collect(Collectors.toList());

						upcomingDates.sort(Date::compareTo);

						if (!upcomingDates.isEmpty()) {

							returnOrderDTO.setNextActivityDate(upcomingDates.get(0));

						}

					}

				}

			}

		}
		return returnOrderDTOs;

	}

	@Override
	public List<ReturnOrderDTO> getAllReturnOrderByRmaNo(String rmaOrderNo) {
		List<ReturnOrder> returnOrder = returnOrderRepository.findByrmaOrderNo(rmaOrderNo);

		List<ReturnOrderDTO> returnOrderDTO = returnOrder.stream().map(returnOrderMapper::returnOrderToReturnOrderDTO)
				.collect(Collectors.toList());

		return returnOrderDTO;
	}

	@Override

	public String updateReturnOrder(String rmaNo, String updateBy, String status) {
		Optional<ReturnOrder> optionalItem = returnOrderRepository.findByRmaOrderNo(rmaNo);

		if (optionalItem.isPresent()) {
			ReturnOrder returnOrder = optionalItem.get();
			String existingStatus = returnOrder.getStatus();

			List<ReturnOrderItem> returnOrderItems = returnOrderItemRepository.findByReturnOrderId(returnOrder.getId());
			boolean hasRMCI = false;
			boolean hasCarrier = false;
			boolean hasUnderReview = false;
			boolean allAuthorized = true;
			boolean allDenied = true;
			boolean allUnderReview = true;
			boolean hasCancelled = false;
			boolean hasDenied = false;
			boolean allCancelled = true;

			for (ReturnOrderItem returnOrderItem : returnOrderItems) {
				if (PortalConstants.RMCI.equalsIgnoreCase(returnOrderItem.getStatus())) {
					hasRMCI = true;
					break;
				}
				if (PortalConstants.AWAITING_CARRIER_APPROVAL.equalsIgnoreCase(returnOrderItem.getStatus())
						|| PortalConstants.AWAITING_VENDOR_APPROVAL.equalsIgnoreCase(returnOrderItem.getStatus())) {
					hasCarrier = true;

				}
				if (PortalConstants.UNDER_REVIEW.equalsIgnoreCase(returnOrderItem.getStatus())) {
					hasUnderReview = true;
				}
				if (!PortalConstants.RMA_DENIED.equalsIgnoreCase(returnOrderItem.getStatus())) {
					allDenied = false;

				}
				if (!PortalConstants.UNDER_REVIEW.equalsIgnoreCase(returnOrderItem.getStatus())) {
					allUnderReview = false;

				}
				if (PortalConstants.RMA_CANCLED.equals(returnOrderItem.getStatus())) {
					hasCancelled = true;
				}
				if (PortalConstants.RMA_DENIED.equals(returnOrderItem.getStatus())) {
					hasDenied = true;
				}

				if (!PortalConstants.RMA_CANCLED.equalsIgnoreCase(returnOrderItem.getStatus())) {
					allCancelled = false;
				}
				if (!PortalConstants.AUTHORIZED_IN_TRANSIT.equalsIgnoreCase(returnOrderItem.getStatus())) {
					if (!PortalConstants.AUTHORIZED_AWAITING_TRANSIT.equalsIgnoreCase(returnOrderItem.getStatus())) {
						if (!PortalConstants.RMA_DENIED.equalsIgnoreCase(returnOrderItem.getStatus())) {
							if (!PortalConstants.RMA_CANCLED.equalsIgnoreCase(returnOrderItem.getStatus())) {
								allAuthorized = false;
							}
						}
					}
				}

			}
			if (hasRMCI && !status.equalsIgnoreCase(PortalConstants.RMCI)) {
				return "Change Line Item Status";
			}
			if (!hasRMCI && status.equalsIgnoreCase(PortalConstants.RMCI)) {
				return "Change Line Item Status";
			}
			if (hasCarrier && !status.equalsIgnoreCase(PortalConstants.UNDER_REVIEW)) {
				return "Change Line Item Status";
			}

			if (!allDenied && status.equalsIgnoreCase(PortalConstants.RMA_DENIED)) {
				return "Change Line Item Status";

			}
			if (allDenied && !status.equalsIgnoreCase(PortalConstants.RMA_DENIED)) {
				return "Change Line Item Status";

			}
			if (allUnderReview && !status.equalsIgnoreCase(PortalConstants.UNDER_REVIEW)) {
				return "Change Line Item Status";

			}
			if (allAuthorized && !status.equalsIgnoreCase(PortalConstants.AUTHORIZED)) {
				return "Change Line Item Status";
			}
			if (hasUnderReview && status.equalsIgnoreCase(PortalConstants.AUTHORIZED)) {
				return "Change Line Item Status";
			}
			if (hasCancelled && hasDenied && status.equalsIgnoreCase(PortalConstants.AUTHORIZED)) {
				return "Change Line Item Status";
			}
			if (!hasCancelled && status.equalsIgnoreCase(PortalConstants.RMA_CANCLED)) {
				return "Change Line Item Status";
			}
			if (allCancelled && !status.equalsIgnoreCase(PortalConstants.RMA_CANCLED)) {
				return "Change Line Item Status";
			}

			returnOrder.setStatus(status);

			returnOrderRepository.save(returnOrder);

			// audit log saving

			String title = "Return Order";
			String auditLogStatus = "";
			String description = "";
			if (returnOrder.getStatus().equalsIgnoreCase(PortalConstants.UNDER_REVIEW)) {
				auditLogStatus = "RMA Header";
				description = getRmaaQualifier() + rmaNo + " has been updated from " + existingStatus + " to "
						+ returnOrder.getStatus() + " by " + updateBy + ".";
			} else {
				auditLogStatus = "Inbox";
				description = getRmaaQualifier() + rmaNo + " has been updated from " + existingStatus + " to "
						+ returnOrder.getStatus() + " by " + updateBy + ".; Email has been sent to "
						+ returnOrder.getContact().getContactEmailId() + ".";
			}
			auditLogServiceImpl.setAuditLog(description, title, auditLogStatus, rmaNo, updateBy,
					returnOrder.getStatus());
			return "RMA Status Updated Successfully.";

		} else {

			throw new EntityNotFoundException("ReturnOrder with rma  " + rmaNo + " not found");
		}

	}

	@Override
	public String getSearchRmaInvoiceinfo() throws Exception {
		List<RmaInvoiceInfo> rma = rmaInvoiceInfoRepository.findAll();

		List<RmaInvoiceInfoDTO> rmaDTOList = new ArrayList<>();

		for (RmaInvoiceInfo rmaInvoiceInfo : rma) {
			RmaInvoiceInfoDTO rmaInvoiceInfoDTO = new RmaInvoiceInfoDTO();

			// Manually set properties of rmaInvoiceInfoDTO based on rmaInvoiceInfo
			rmaInvoiceInfoDTO.setRmaOrderNo(rmaInvoiceInfo.getRmaOrderNo());
			rmaInvoiceInfoDTO.setRetryCount(rmaInvoiceInfo.getRetryCount());
			// Set other properties as needed

			rmaDTOList.add(rmaInvoiceInfoDTO);

			String rmaOrderNo = rmaInvoiceInfoDTO.getRmaOrderNo();
			Integer retryCount = rmaInvoiceInfoDTO.getRetryCount();
			if (retryCount < 3) {
				boolean bln = p21InvoiceService.linkInvoice(rmaOrderNo, null);
				if (bln) {
					Optional<ReturnOrder> ro = returnOrderRepository.findById(rmaInvoiceInfo.getReturnOrder().getId());

					if (ro.isPresent()) {

						ReturnOrder returnOrder = ro.get();
						returnOrder.setISInvoiceLinked(true);
						returnOrderRepository.save(returnOrder);

						rmaInvoiceInfoRepository.delete(rmaInvoiceInfo);
						// Here you can remove the corresponding DTO from the list

					}
				} else {
					rmaInvoiceInfoDTO.setRetryCount(rmaInvoiceInfoDTO.getRetryCount() + 1);
					rmaInvoiceInfoDTO.setInvoiceLinked(false);

					RmaInvoiceInfo rii = rmaInvoiceInfoMapper.RmaInvoiceInfoToRmaInvoiceInfoDTO(rmaInvoiceInfoDTO);
					rmaInvoiceInfoRepository.save(rii);
				}
			}
		}
		return "Link Invoice API Ran";
	}

	@Override
	public String assignRMA(String rmaNo, Long assignToId, String updateBy, Long returnTypeId, ReturnOrderDTO note) {
		Optional<ReturnOrder> optionalReturnOrder = returnOrderRepository.findByRmaOrderNo(rmaNo);
		Optional<User> optionalUser = userRepository.findById(assignToId);
		Optional<ReturnType> optionalReturnType = returnTypeRepository.findById(returnTypeId);
		if (optionalReturnOrder.isPresent() && optionalUser.isPresent()) {
			ReturnOrder returnOrder = optionalReturnOrder.get();
			String previousNote = "";
			Long previousReturnType = 0L;
			Long previousAssignTo = 0L;
			if (returnOrder.getNote() != null && returnOrder.getReturnType().getId() != null) {
				previousNote = returnOrder.getNote();
				previousReturnType = returnOrder.getReturnType().getId();
				previousAssignTo = returnOrder.getUser().getId();
			}

			Optional<ReturnType> returnTypes = returnTypeRepository.findById(returnTypeId);
			User user = optionalUser.get();
			ReturnType returnType = optionalReturnType.get();
			List<ReturnOrderItem> returnOrderItemList = returnOrder.getReturnOrderItem();
			for (ReturnOrderItem returnOrderItem : returnOrderItemList) {
				if (returnOrderItem.getUser() == null) {
					returnOrderItem.setUser(user);
					returnOrderItem.setStatus(PortalConstants.UNDER_REVIEW);
					returnOrderItem.setIsEditable(true);
					returnOrderItem.setIsAuthorized(false);
					returnOrderItemRepository.save(returnOrderItem);
				}
			}
			returnOrder.setUser(user);
			returnOrder.setNote(note.getNote());
			returnOrder.setStatus(PortalConstants.UNDER_REVIEW);
			returnOrder.setReturnType(returnType);
			returnOrder.setIsEditable(true);
			returnOrder.setIsAuthorized(false);
			returnOrderRepository.save(returnOrder);

//			apply email functionality.
			// String recipient = user.getEmail();
//			String recipient = PortalConstants.EMAIL_RECIPIENT;
//			String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];
			String tenentId = httpServletRequest.getHeader("tenant");
			MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
			String recipient = "";
			if (masterTenant.getIsProd()) {
				recipient = note.getContact().getContactEmailId();
			} else {
				recipient = PortalConstants.EMAIL_RECIPIENT;

			}
			String subject = PortalConstants.ASSIGN_RMA + getRmaaQualifier() + " " + returnOrder.getRmaOrderNo();
			HashMap<String, String> map = new HashMap<>();
			map.put("RMA_QUALIFIER", getRmaaQualifier());
			map.put("RMA_NO", rmaNo);
			map.put("STATUS", PortalConstants.UNDER_REVIEW);
			map.put("CLIENT_MAIL", getClientConfig().getEmailFrom());
			map.put("CLIENT_PHONE", String.valueOf(getClientConfig().getClient().getContactNo()));
			String template = emailTemplateRenderer.getAssignRMATemplate();
			try {
				emailSender.sendEmail(recipient, template, subject, map);
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			String recipient1 = PortalConstants.EMAIL_RECIPIENT;
			String subject1 = "Your Return " + getRmaaQualifier() + " " + rmaNo + " is Under Review";
			HashMap<String, String> map1 = new HashMap<>();
			map1.put("RMA_QUALIFIER", getRmaaQualifier());
			map1.put("RMA_NO", rmaNo);
			map1.put("CUST_NAME", returnOrder.getCustomer().getDisplayName());
			map1.put("RP_NAME", returnOrder.getUser().getFullName());
			map1.put("CLIENT_MAIL", getClientConfig().getEmailFrom());
			map1.put("CLIENT_PHONE", String.valueOf(getClientConfig().getClient().getContactNo()));
			String template1 = emailTemplateRenderer.getCUSTMER_UNDER_REVIEW_TEMPLATE();
			try {
				emailSender.sendEmail(recipient1, template1, subject1, map1);
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			String highlight = "";
			String description = "";
			String status = "RMA Header";
			String title = "Assign RMA";
			List<String> updates = new ArrayList<>();
			if (!previousNote.equalsIgnoreCase(note.getNote())) {
				updates.add("Note '" + note.getNote() + "'" + " added, while assigning RMA " + getRmaaQualifier() + " "
						+ rmaNo + ".");
				highlight = note.getNote();

			}
			if (previousReturnType != returnTypeId) {
				updates.add("Return type of the " + getRmaaQualifier() + " " + rmaNo + " is set to as '"
						+ returnTypes.get().getType() + "'");
				highlight = "Return Type";

			}
			if (previousAssignTo != assignToId) {
				updates.add(getRmaaQualifier() + " " + returnOrder.getRmaOrderNo() + " has been assigned to the "
						+ user.getFirstName() + " " + user.getLastName() + " by " + updateBy + "." + ";"
						+ getRmaaQualifier() + " " + rmaNo + " is now at 'Under Review'.;" + "Email has been sent to "
						+ note.getContact().getContactEmailId());
				highlight = "assigned";
				status = "Inbox";

			}

			description = String.join(".;", updates) + ".";

			auditLogServiceImpl.setAuditLog(description, title, status, rmaNo, updateBy, highlight);

			return "Assiged RMA to User";

		}
		return "Invalid User";

	}

	public List<ReturnOrderItem> returnOrderItemDTOToReturnOrderItem(List<ReturnOrderItemDTO> returnOrderItemDTOList) {
		List<ReturnOrderItem> returnOrderItemList = new ArrayList<>();

		for (ReturnOrderItemDTO returnOrderItemDTO : returnOrderItemDTOList) {
			ReturnOrderItem returnOrderItem = modelMapper.map(returnOrderItemDTO, ReturnOrderItem.class);
			returnOrderItemList.add(returnOrderItem);
		}

		return returnOrderItemList;
	}

	public String getRmaaQualifier() {
		// Fetch the single row of data
		ClientConfig clientConfig = clientConfigRepository.findById(1L).orElse(null);

		if (clientConfig != null) {
			return clientConfig.getRmaQualifier();
		} else {

			return "No RMA Qualifier";
		}
	}

	public ClientConfig getClientConfig() {
		ClientConfig clientConfig = clientConfigRepository.findById(1L).orElse(null);

		if (clientConfig != null) {
			return clientConfig;
		} else {

			return null;
		}
	}
}
