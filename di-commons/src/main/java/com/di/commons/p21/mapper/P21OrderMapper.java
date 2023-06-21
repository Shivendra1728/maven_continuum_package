package com.di.commons.p21.mapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.di.commons.dto.CustomerDTO;
import com.di.commons.dto.OrderAddressDTO;
import com.di.commons.dto.OrderDTO;
import com.di.commons.helper.P21OrderData;
import com.di.commons.helper.P21OrderDataHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class P21OrderMapper {
	@Autowired
	private final ObjectMapper objectMapper;

	public P21OrderMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public OrderDTO convertP21OrderObjectToOrderDTO(String order) {
		try {
			
			P21OrderDataHelper p21OrderDataHelper = objectMapper.readValue(order, P21OrderDataHelper.class);

			OrderDTO orderDTO = new OrderDTO();
			
			//orderDTO.setId(p21OrderData.getId());
			//orderDTO.setUserId(p21OrderData.getUserId());
			//orderDTO.setORMOrder(p21OrderData.getORMOrder());
			//orderDTO.setcontactId(p21OrderData.getcontactId());
			//orderDTO.setStatus(p21OrderData.getStatus());
			//orderDTO.setorderDate(p21orderData.getorder_date());
			List<P21OrderData> p21OrderDataList=p21OrderDataHelper.getValue();
			P21OrderData p21OrderData=p21OrderDataList.get(0);
			String requestedDate = p21OrderData.getRequested_date();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			//Date correctrequestedDate = dateFormat.parse(requestedDate);
		//orderDTO.setRequestedDate(correctrequestedDate);
			
			String orderDate = p21OrderData.getOrder_date();
			Date correctorderDate = dateFormat.parse(orderDate);
			orderDTO.setOrderDate(correctorderDate);

			orderDTO.setPONumber(p21OrderData.getPo_number());
			orderDTO.setOrderNo(p21OrderData.getOrder_no());
			orderDTO.setCurrency(p21OrderData.getCurrency_desc());
			orderDTO.setInvoiceNo(p21OrderData.getOriginal_invoice_no());
			
			String salesLocationIdString = p21OrderData.getLocation_id();
			Long salesLocationId = Long.parseLong(salesLocationIdString);
			orderDTO.setSalesLocationId(salesLocationId);
			
			
			CustomerDTO customerDTO=new CustomerDTO();
			
			customerDTO.setCustomerId(p21OrderData.getCustomer_id());
			//customerDTO.setCustomerType(p21OrderData.getCustomerType());
			//customerDTO.setStatus(p21OrderData.getStatus());
			//customerDTO.setId(p21OrderData.getid());
			customerDTO.setFirstName(p21OrderData.getOrder_contact_first_name());
			customerDTO.setLastname(p21OrderData.getOrder_contact_last_name());
			customerDTO.setEmail(p21OrderData.getContact_email_address());
			customerDTO.setDisplayName(p21OrderData.getOrder_contact_name());
			customerDTO.setPhone(p21OrderData.getContact_phone_number());
			orderDTO.setCustomer(customerDTO);
			
			
			OrderAddressDTO orderAddressShipTODTO=new OrderAddressDTO();
			
			//orderAddressShipTODTO.setId(p21OrderData.getid()); 
			//orderAddressShipTODTO.setcreatedDate(p21OrderData.getdate_created());
			//orderAddressShipTODTO.setupdatedDate(p21OrderData.getdate_last_modified());
			//orderAddressShipTODTO.setPhoneNumber(p21OrderData.getphone_number());//No keys mapped here yet 
			orderAddressShipTODTO.setFax(p21OrderData.getContact_fax_number());
			orderAddressShipTODTO.setStreet1(p21OrderData.getShip2_add1());
			orderAddressShipTODTO.setStreet2(p21OrderData.getShip2_add2());
			orderAddressShipTODTO.setCountry(p21OrderData.getShip2_country());
			orderAddressShipTODTO.setProvince(p21OrderData.getShip2_state());
			orderAddressShipTODTO.setCity(p21OrderData.getShip2_city());
			orderAddressShipTODTO.setZipcode(p21OrderData.getShip2_zip());
			//orderAddressShipTODTO.setAddressType(p21OrderData.getaddresstype());
			orderDTO.setShipTo(orderAddressShipTODTO);
			
			

			OrderAddressDTO orderAddressBillTODTO=new OrderAddressDTO();
			
			//orderAddressShipTODTO.setId(p21OrderData.getid()); 
			//orderAddressShipTODTO.setcreatedDate(p21OrderData.getdate_created());
			//orderAddressShipTODTO.setupdatedDate(p21OrderData.getdate_last_modified());
			//orderAddressShipTODTO.setPhoneNumber(p21OrderData.getphone_number());//No keys mapped here yet 
			//orderAddressBillTODTO.setFax(p21OrderData.getContact_fax_number());
			orderAddressBillTODTO.setStreet1(p21OrderData.getMail_address1_a());
			orderAddressShipTODTO.setStreet2(p21OrderData.getMail_address2_a());
			orderAddressShipTODTO.setCountry(p21OrderData.getMail_country_a());
			orderAddressShipTODTO.setProvince(p21OrderData.getMail_state_a());
			orderAddressShipTODTO.setCity(p21OrderData.getMail_city_a());
			orderAddressShipTODTO.setZipcode(p21OrderData.getMail_postal_code_a());
			//orderAddressShipTODTO.setAddressType(p21OrderData.getaddresstype());
			orderDTO.setBillTo(orderAddressBillTODTO);
			
			
			return orderDTO;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

