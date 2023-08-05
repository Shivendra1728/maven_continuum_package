package com.di.commons.p21.mapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.continuum.tenant.repos.entity.Customer;
import com.continuum.tenant.repos.repositories.CustomerRepository;
import com.di.commons.dto.CustomerDTO;
import com.di.commons.dto.OrderAddressDTO;
import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.P21OrderData;
import com.di.commons.helper.P21OrderDataHelper;
import com.di.commons.mapper.CustomerMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class P21OrderMapper {
	@Autowired
	private final ObjectMapper objectMapper;

	@Autowired
	CustomerMapper customerMapper;
	
	@Autowired
	CustomerRepository customerRepository;

	public P21OrderMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public List<OrderDTO> convertP21OrderObjectToOrderDTO(String order)
			throws JsonMappingException, JsonProcessingException, ParseException {
		P21OrderDataHelper p21OrderDataHelper = objectMapper.readValue(order, P21OrderDataHelper.class);

		OrderDTO orderDTO = new OrderDTO();

		// orderDTO.setId(p21OrderData.getId());
		// orderDTO.setUserId(p21OrderData.getUserId());
		// orderDTO.setORMOrder(p21OrderData.getORMOrder());
		// orderDTO.setcontactId(p21OrderData.getcontactId());
		// orderDTO.setStatus(p21OrderData.getStatus());
		// orderDTO.setorderDate(p21orderData.getorder_date());
		List<OrderDTO> orderDTOList = new ArrayList<>();
		List<P21OrderData> p21OrderDataList = p21OrderDataHelper.getValue();
		for (P21OrderData p21OrderData : p21OrderDataList) {

			String requestedDate = p21OrderData.getRequested_date();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			// Date correctrequestedDate = dateFormat.parse(requestedDate);
			// orderDTO.setRequestedDate(correctrequestedDate);

			String orderDate = p21OrderData.getOrder_date();
			Date correctorderDate = dateFormat.parse(orderDate);
			orderDTO.setOrderDate(correctorderDate);
			orderDTO.setCreatedDate(correctorderDate);
			orderDTO.setContactEmailId(p21OrderData.getContact_email_address());
			orderDTO.setPONumber(p21OrderData.getPo_number());
			orderDTO.setOrderNo(p21OrderData.getOrder_no());
			orderDTO.setCompanyId(p21OrderData.getCompany_id());
			orderDTO.setCurrency(p21OrderData.getCurrency_desc());
			// orderDTO.setContactId(p21OrderData.getCustomer_id()); //TODO need to fetch
			// contact Id
			orderDTO.setInvoiceNo(p21OrderData.getOriginal_invoice_no());

			String salesLocationIdString = p21OrderData.getLocation_id();
			Long salesLocationId = Long.parseLong(salesLocationIdString);
			orderDTO.setSalesLocationId(salesLocationId);

			CustomerDTO customerDTO = new CustomerDTO();

			customerDTO.setCustomerId(p21OrderData.getCustomer_id());
			// customerDTO.setCustomerType(p21OrderData.getCustomerType());
			// customerDTO.setStatus(p21OrderData.getStatus());
			// customerDTO.setId(p21OrderData.getid());
			customerDTO.setFirstName(p21OrderData.getOrder_contact_first_name());
			customerDTO.setLastname(p21OrderData.getOrder_contact_last_name());
			customerDTO.setEmail(p21OrderData.getContact_email_address());
			customerDTO.setDisplayName(
					p21OrderData.getOrder_contact_first_name() + " " + p21OrderData.getOrder_contact_last_name());
			customerDTO.setPhone(p21OrderData.getContact_phone_number());
			orderDTO.setCustomer(customerDTO);
			
			
			
			
			Customer existingCustomer = customerRepository.findByCustomerId(customerDTO.getCustomerId());

			if (existingCustomer != null) {
			    // Customer already exists, update the customer entity with new data.
			    existingCustomer.setFirstName(customerDTO.getFirstName());
			    existingCustomer.setLastname(customerDTO.getLastname());
			    existingCustomer.setEmail(customerDTO.getEmail());
			    existingCustomer.setDisplayName(customerDTO.getDisplayName());
			    existingCustomer.setPhone(customerDTO.getPhone());
			   
			    
			    // Save the updated customer entity back to the database.
			    customerRepository.save(existingCustomer);
			} else {
			    // Customer doesn't exist, create a new customer entity and save it.
			    Customer newCustomer = customerMapper.cusotmerDTOTocusotmer(customerDTO);
			    customerRepository.save(newCustomer);
			}



			

			OrderAddressDTO orderAddressShipTODTO = new OrderAddressDTO();

			// orderAddressShipTODTO.setId(p21OrderData.getid());
			// orderAddressShipTODTO.setcreatedDate(p21OrderData.getdate_created());
			// orderAddressShipTODTO.setupdatedDate(p21OrderData.getdate_last_modified());
			// orderAddressShipTODTO.setPhoneNumber(p21OrderData.getphone_number());//No
			// keys mapped here yet
			orderAddressShipTODTO.setAddressId(p21OrderData.getAddress_id()); // TO Do Addrress API
			orderAddressShipTODTO.setFax(p21OrderData.getContact_fax_number());
			orderAddressShipTODTO.setStreet1(p21OrderData.getShip2_add1());
			orderAddressShipTODTO.setStreet2(p21OrderData.getShip2_add2());
			orderAddressShipTODTO.setCountry(p21OrderData.getShip2_country());
			orderAddressShipTODTO.setProvince(p21OrderData.getShip2_state());
			orderAddressShipTODTO.setCity(p21OrderData.getShip2_city());
			orderAddressShipTODTO.setZipcode(p21OrderData.getShip2_zip());
			// orderAddressShipTODTO.setAddressType(p21OrderData.getaddresstype());
			orderDTO.setShipTo(orderAddressShipTODTO);

			OrderAddressDTO orderAddressBillTODTO = new OrderAddressDTO();

			// orderAddressShipTODTO.setId(p21OrderData.getid());
			// orderAddressShipTODTO.setcreatedDate(p21OrderData.getdate_created());
			// orderAddressShipTODTO.setupdatedDate(p21OrderData.getdate_last_modified());
			// orderAddressShipTODTO.setPhoneNumber(p21OrderData.getphone_number());//No
			// keys mapped here yet
			// orderAddressBillTODTO.setFax(p21OrderData.getContact_fax_number());
			orderAddressBillTODTO.setStreet1(p21OrderData.getMail_address1_a());
			orderAddressShipTODTO.setStreet2(p21OrderData.getMail_address2_a());
			orderAddressShipTODTO.setCountry(p21OrderData.getMail_country_a());
			orderAddressShipTODTO.setProvince(p21OrderData.getMail_state_a());
			orderAddressShipTODTO.setCity(p21OrderData.getMail_city_a());
			orderAddressShipTODTO.setZipcode(p21OrderData.getMail_postal_code_a());
			// orderAddressShipTODTO.setAddressType(p21OrderData.getaddresstype());
			orderDTO.setBillTo(orderAddressBillTODTO);
			orderDTO.setCompanyName(p21OrderData.getCompany_name());
			orderDTOList.add(orderDTO);
		}

		return orderDTOList;
	}
}
