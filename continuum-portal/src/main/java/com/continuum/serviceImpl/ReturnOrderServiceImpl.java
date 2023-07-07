package com.continuum.serviceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.continuum.repos.entity.OrderAddress;
import com.continuum.repos.entity.ReturnOrder;
import com.continuum.repos.repositories.ReturnOrderRepository;
import com.continuum.service.CustomerService;
import com.continuum.service.ReturnOrderService;
import com.di.commons.dto.CustomerDTO;
import com.di.commons.dto.ReturnOrderDTO;
import com.di.commons.helper.OrderSearchParameters;
import com.di.commons.mapper.ReturnOrderMapper;
import com.di.integration.p21.service.P21ReturnOrderService;
import com.di.integration.p21.transaction.P21RMAResponse;

@Service
public class ReturnOrderServiceImpl implements ReturnOrderService {

	private static final Logger logger = LoggerFactory.getLogger(ReturnOrderServiceImpl.class);

	@Autowired
	ReturnOrderRepository repository;

	@Autowired
	ReturnOrderMapper returnOrderMapper;

	@Autowired
	P21ReturnOrderService p21Service;

	@Autowired
	CustomerService customerService;

	@Autowired
	emailSender sender;

	public P21RMAResponse createReturnOrder(ReturnOrderDTO returnOrderDTO) throws Exception {
		// Create RMA in p21
		P21RMAResponse p21RMARespo = p21Service.createReturnOrder(returnOrderDTO);
		returnOrderDTO.setRmaOrderNo(p21RMARespo.getRmaOrderNo());
		returnOrderDTO.setStatus("Under Review");
		returnOrderDTO.setOrderDate(new Date());
		returnOrderDTO.setCreatedDate(new Date());
		returnOrderDTO.setRequestedDate(new Date());
		// returnOrderDTO.setStatus(p21RMARespo.getStatus());

		String Status = p21RMARespo.getStatus();
		if (Status.equals("Success")) {
			returnOrderDTO.setStatus("Under Review");
		} else {
			returnOrderDTO.setStatus("Failed");
		}

		CustomerDTO customerDTO = customerService.findbyCustomerId(returnOrderDTO.getCustomer().getCustomerId());
		if (customerDTO == null) {
			customerDTO = new CustomerDTO();
			customerDTO.setCustomerId(returnOrderDTO.getCustomer().getCustomerId());
			customerDTO = customerService.createCustomer(customerDTO);
		}
		returnOrderDTO.setCustomer(customerDTO);

		ReturnOrder returnOrder = returnOrderMapper.returnOrderDTOToReturnOrder(returnOrderDTO);
		repository.save(returnOrder);

		// Trigger email based on RMA status
		String recipient = "operations@techexprt.com"; // Replace with actual recipient email
		String subject = "Your RMA Return Order " + returnOrderDTO.getRmaOrderNo() + " : " + returnOrderDTO.getStatus();
		String body = "Your RMA status is:" + returnOrderDTO.getStatus();

//     	emailSender emailSender1 = new emailSender(returnOrderDTO);
//     	emailSender1.
		sender.sendEmail(recipient, subject, body, returnOrderDTO);

		// p21RMARespo.setStatus("Success");
		// p21RMARespo.setStatus(returnOrderDTO.getStatus());
		// p21RMARespo.setRmaOrderNo(returnOrderDTO.getRmaOrderNo());
		logger.info("orderNo: " + p21RMARespo.getRmaOrderNo() + " status: " + p21RMARespo.getStatus());
		return p21RMARespo;
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

}
