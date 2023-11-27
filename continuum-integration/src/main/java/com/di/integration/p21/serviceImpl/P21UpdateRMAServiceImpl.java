package com.di.integration.p21.serviceImpl;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.di.commons.helper.OrderSearchParameters;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21UpdateRMAService;

@Service
public class P21UpdateRMAServiceImpl implements P21UpdateRMAService{
	
	private static final Logger logger = LoggerFactory.getLogger(P21OrderServiceImpl.class);
	
	@Value(IntegrationConstants.ERP_RMA_UPDATE_RESTOCKING_API)
	String RMA_UPDATE_RESTOCKING_API;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;
	
	@Override
	public String updateRMARestocking(Integer rmaNumber, Integer poNumber, Double totalRestocking) throws Exception {
		
		totalRestocking = -totalRestocking;
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(p21TokenServiceImpl.getToken());
		headers.setContentType(MediaType.APPLICATION_XML);
		
		String updateRestockingXml = getXml(rmaNumber, poNumber, totalRestocking);
		HttpEntity<String> requestEntity = new HttpEntity<>(updateRestockingXml, headers);
		
		logger.info("Order Search URI:" + RMA_UPDATE_RESTOCKING_API);
		
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(RMA_UPDATE_RESTOCKING_API, requestEntity, String.class);

		return responseEntity.getBody();
		
	}
	
	public String getXml(Integer rmaNumber, Integer poNumber, Double totalRestocking) {
		String str = "<TransactionSet xmlns=\"http://schemas.datacontract.org/2004/07/P21.Transactions.Model.V2\">\r\n"
				+ "    <IgnoreDisabled>true</IgnoreDisabled>\r\n"
				+ "    <Name>RMA</Name>\r\n"
				+ "    <Transactions>\r\n"
				+ "        <Transaction>\r\n"
				+ "            <DataElements>\r\n"
				+ "                <DataElement>\r\n"
				+ "                    <Keys xmlns:a=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\"/>\r\n"
				+ "                    <Name>TABPAGE_1.order</Name>\r\n"
				+ "                    <Rows>\r\n"
				+ "                        <Row>\r\n"
				+ "                            <Edits>\r\n"
				+ "								<Edit>\r\n"
				+ "                                            <Name>order_no</Name>\r\n"
				+ "                                            <Value>"+rmaNumber+"</Value>\r\n"
				+ "                                        </Edit>\r\n"
				+ "										<Edit>\r\n"
				+ "                                            <Name>po_no</Name>\r\n"
				+ "                                            <Value>"+poNumber+"</Value>\r\n"
				+ "                                        </Edit>\r\n"
				+ "                            </Edits>\r\n"
				+ "                            <RelativeDateEdits/>\r\n"
				+ "                        </Row>\r\n"
				+ "                    </Rows>\r\n"
				+ "                    <Type>Form</Type>\r\n"
				+ "                </DataElement>\r\n"
				+ "                <DataElement>\r\n"
				+ "						<Keys\r\n"
				+ "							xmlns:a=\"http://schemas.microsoft.com/2003/10/Serialization/Arrays\"/>\r\n"
				+ "							<Name>TP_ITEMS.items</Name>\r\n"
				+ "							<Rows>\r\n"
				+ "								<Row>\r\n"
				+ "									<Edits>\r\n"
				+ "										<Edit>\r\n"
				+ "											<Name>oe_order_item_id</Name>\r\n"
				+ "											<Value>RESTOCKING CHARGE</Value>\r\n"
				+ "										</Edit>\r\n"
				+ "										<Edit>\r\n"
				+ "											<Name>unit_quantity</Name>\r\n"
				+ "											<Value>1</Value>\r\n"
				+ "										</Edit>\r\n"
				+ "                                        <Edit>\r\n"
				+ "											<Name>unit_price</Name>\r\n"
				+ "											<Value>"+totalRestocking+"</Value>\r\n"
				+ "										</Edit>\r\n"
				+ "                                        <Edit>\r\n"
				+ "											<Name>extended_price</Name>\r\n"
				+ "											<Value>"+totalRestocking+"</Value>\r\n"
				+ "										</Edit>\r\n"
				+ "									</Edits>\r\n"
				+ "									<RelativeDateEdits/>\r\n"
				+ "								</Row>\r\n"
				+ "							</Rows>\r\n"
				+ "							<Type>List</Type>\r\n"
				+ "						</DataElement>\r\n"
				+ "            </DataElements>\r\n"
				+ "        </Transaction>\r\n"
				+ "    </Transactions>\r\n"
				+ "</TransactionSet>";
		return str;
	}
}
