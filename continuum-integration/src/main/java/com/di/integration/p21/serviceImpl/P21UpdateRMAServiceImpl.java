package com.di.integration.p21.serviceImpl;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
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
	
	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Override
	public String updateRMARestocking(Integer rmaNumber, Integer poNumber, Double totalRestocking) throws Exception {
		

		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
		
		totalRestocking = -totalRestocking;		
		String updateRestockingXml = getXml(rmaNumber, poNumber, totalRestocking);
		CloseableHttpClient httpClient = HttpClients.custom()
				.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
		HttpPost request = new HttpPost(masterTenant.getSubdomain()+RMA_UPDATE_RESTOCKING_API);

		// Set request headers
		request.addHeader(HttpHeaders.CONTENT_TYPE, "application/xml");
		String token = p21TokenServiceImpl.getToken(masterTenant);
		logger.info("#### TOKEN #### {}", token);

		request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
		request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		StringEntity entity = new StringEntity(updateRestockingXml);
		request.setEntity(entity);
		CloseableHttpResponse response = httpClient.execute(request);
		return EntityUtils.toString(response.getEntity());

		
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
