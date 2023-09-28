package com.continuum.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.continuum.constants.PortalConstants;
import com.continuum.service.CustomerService;
import com.continuum.service.ReturnOrderService;
import com.continuum.tenant.repos.entity.AuditLog;
import com.continuum.tenant.repos.entity.OrderAddress;
import com.continuum.tenant.repos.entity.QuestionMap;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.entity.ReturnOrderItem;
import com.continuum.tenant.repos.entity.RmaInvoiceInfo;
import com.continuum.tenant.repos.entity.User;
import com.continuum.tenant.repos.repositories.AuditLogRepository;
import com.continuum.tenant.repos.repositories.QuestionRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderItemRepository;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
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
	ReturnOrderRepository repository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	ReturnOrderItemRepository returnOrderItemRepository;

	@Autowired
	AuditLogRepository audrepo;

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
	P21ReturnOrderService p21Service;

	@Autowired
	CustomerService customerService;

	@Autowired
	EmailSender sender;

	ReturnOrder returnOrder;

	public P21RMAResponse createReturnOrder(ReturnOrderDTO returnOrderDTO) throws Exception {
		// Create RMA in p21
		P21RMAResponse p21RMARespo = p21Service.createReturnOrder(returnOrderDTO);
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
		

			ReturnOrder returnOrder = returnOrderMapper.returnOrderDTOToReturnOrder(returnOrderDTO);
			repository.save(returnOrder);

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
			auditlog.setHighlight("rma request");
			auditlog.setRmaNo(p21RMARespo.getRmaOrderNo());
			String described = customerDTO.getDisplayName() + " submitted the new rma request. Order ID- TLD-"
					+ returnOrderDTO.getRmaOrderNo();
			auditlog.setDescription(described);
			auditlog.setStatus("Inbox");
			auditlog.setTitle("Return Order");
			auditlog.setRmaNo(p21RMARespo.getRmaOrderNo());
			auditlog.setUserName(customerDTO.getDisplayName());
			audrepo.save(auditlog);

			String recipient = PortalConstants.EMAIL_RECIPIENT;
			String subject = PortalConstants.EMAIL_SUBJECT_PREFIX + returnOrderDTO.getRmaOrderNo() + " : "
					+ returnOrderDTO.getStatus();
			String body = PortalConstants.EMAIL_BODY_PREFIX + returnOrderDTO.getStatus();

			sender.sendEmail(recipient, subject, body, returnOrderDTO, customerDTO);
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

		List<ReturnOrder> poList = repository.findAll(spec);
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
			if (user.getRoles().getId() == 1 || user.getRoles().getId() == 2) {
				List<ReturnOrder> returnOrderEntities = repository.findAll();

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
				List<ReturnOrder> returnOrder = repository.findByUserId(userId);
				List<ReturnOrder> returnOrder1 = repository.findAll();
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
		List<ReturnOrder> returnOrder = repository.findByrmaOrderNo(rmaOrderNo);

		List<ReturnOrderDTO> returnOrderDTO = returnOrder.stream().map(returnOrderMapper::returnOrderToReturnOrderDTO)
				.collect(Collectors.toList());

		return returnOrderDTO;
	}

	@Override

	public String updateReturnOrder(String rmaNo, String updateBy, String status) {
		Optional<ReturnOrder> optionalItem = repository.findByRmaOrderNo(rmaNo);

		if (optionalItem.isPresent()) {
			ReturnOrder returnOrder = optionalItem.get();

			returnOrder.setStatus(status);

			repository.save(returnOrder);

			// audit log saving

			AuditLog auditlog = new AuditLog();
			auditlog.setHighlight("rma status");
			String described = updateBy + " has changed rma status to " + returnOrder.getStatus();
			auditlog.setDescription(described);
			auditlog.setStatus("RMA");
			auditlog.setTitle("Return Order");
			auditlog.setRmaNo(returnOrder.getRmaOrderNo());
			auditlog.setUserName(updateBy);
			audrepo.save(auditlog);

			// send email to customer-RMA processor
			String recipient = PortalConstants.EMAIL_RECIPIENT;
			try {

				sender.sendEmail2(recipient, returnOrder.getStatus());
			} catch (MessagingException e) {
				e.printStackTrace();
			}

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
				boolean bln = p21InvoiceService.linkInvoice(rmaOrderNo);
				if (bln) {
					Optional<ReturnOrder> ro = repository.findById(rmaInvoiceInfo.getReturnOrder().getId());

					if (ro.isPresent()) {

						ReturnOrder returnOrder = ro.get();
						returnOrder.setISInvoiceLinked(true);
						repository.save(returnOrder);

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
	public String assignRMA(String rmaNo, Long assignToId, String updateBy, ReturnOrderDTO note) {
		Optional<ReturnOrder> optionalReturnOrder = repository.findByRmaOrderNo(rmaNo);
		Optional<User> optionalUser = userRepository.findById(assignToId);
		if (optionalReturnOrder.isPresent() && optionalUser.isPresent()) {
			ReturnOrder returnOrder = optionalReturnOrder.get();
			User user = optionalUser.get();
			List<ReturnOrderItem> returnOrderItemList = returnOrder.getReturnOrderItem();
			for (ReturnOrderItem returnOrderItem : returnOrderItemList) {
				if (returnOrderItem.getUser() == null) {
					returnOrderItem.setUser(user);
					returnOrderItemRepository.save(returnOrderItem);
				}
			}
			returnOrder.setUser(user);
			returnOrder.setNote(note.getNote());
			returnOrder.setStatus(PortalConstants.UNDER_REVIEW);
			repository.save(returnOrder);
			
//			apply email functionality.
			String recipient = PortalConstants.EMAIL_RECIPIENT;
			try {

				sender.sendEmail3(recipient, returnOrder.getStatus());
			} catch (MessagingException e) {
				e.printStackTrace();
			}

			AuditLog auditLog = new AuditLog();
			auditLog.setTitle("Assign RMA");
			auditLog.setDescription(updateBy + " assign RMA to " + user.getFirstName() + " " + user.getLastName());
			auditLog.setHighlight("assign");
			auditLog.setStatus("RMA");
			auditLog.setRmaNo(returnOrder.getRmaOrderNo());
			auditLog.setUserName(updateBy);
			audrepo.save(auditLog);

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
}