package com.di.integration.p21.serviceImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.continuum.tenant.repos.entity.ReturnOrder;
import com.continuum.tenant.repos.repositories.ReturnOrderRepository;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21TaxService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class P21TaxServiceImpl implements P21TaxService {
	private static final Logger logger = LoggerFactory.getLogger(P21ShipInfoServiceImpl.class);

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;
	
	@Autowired
    private ReturnOrderRepository returnOrderRepository;

	@Override
	public Map<String, String> getTax(String rmaNo, MasterTenant masterTenantObject) throws Exception {

		Map<String, String> taxInfoMap = new HashMap<>();

		MasterTenant masterTenant;

		if (masterTenantObject == null) {
			String tenantId = httpServletRequest.getHeader("tenant");
			masterTenant = masterTenantRepository.findByDbName(tenantId);
			masterTenantObject = masterTenant;
		} else {
			masterTenant = masterTenantObject;
		}

		String getTaxInfo = masterTenant.getSubdomain() + IntegrationConstants.ERP_TRANSACTION_GET;

		logger.info("URI to get Tax Info : " + getTaxInfo);

		String token = p21TokenServiceImpl.findToken(masterTenant);
		logger.info("#### TOKEN #### {}", token);

		try {
			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

			String jsonBody = "{" + "\"ServiceName\": \"RMA\"," + "\"TransactionStates\": [" + "{"
					+ "\"DataElementName\": \"TABPAGE_1.order\"," + "\"Keys\": [" + "{" + "\"Name\": \"order_no\","
					+ "\"Value\": \"" + rmaNo + "\"" + "}" + "]" + "}" + "]," + "\"UseCodeValues\": true" + "}";

			HttpPost getTaxInfoRequest = new HttpPost(getTaxInfo);

			getTaxInfoRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			getTaxInfoRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			getTaxInfoRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			StringEntity entity = new StringEntity(jsonBody);
			getTaxInfoRequest.setEntity(entity);

			CloseableHttpResponse getTaxInfoResponse = httpClient.execute(getTaxInfoRequest);
			String getTaxInfoResponseBody = EntityUtils.toString(getTaxInfoResponse.getEntity());
			logger.info("Response from transaction get :" + getTaxInfoResponseBody);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(getTaxInfoResponseBody);

			JsonNode transactionsNode = rootNode.get("Transactions");
			if (transactionsNode != null && transactionsNode.isArray() && transactionsNode.size() > 0) {
				JsonNode firstTransactionNode = transactionsNode.get(0);
				JsonNode dataElementsNode = firstTransactionNode.get("DataElements");

				if (dataElementsNode != null && dataElementsNode.isArray() && dataElementsNode.size() > 6) {
					JsonNode seventhDataElementNode = dataElementsNode.get(6);
					JsonNode rowsNode = seventhDataElementNode.get("Rows");

					if (rowsNode != null && rowsNode.isArray() && rowsNode.size() > 0) {
						JsonNode firstRowNode = rowsNode.get(0);
						JsonNode editsNode = firstRowNode.get("Edits");

						if (editsNode != null && editsNode.isArray()) {
							for (JsonNode editNode : editsNode) {
								String name = editNode.get("Name").asText();
								String value = editNode.get("Value").asText();

								
								if (name.equals("open_sub_total")) {
									taxInfoMap.put("Initial Total", value);
								} else if (name.equals("open_sales_tax_total")) {
									taxInfoMap.put("Tax", value);
								} else if (name.equals("sales_total")) {
									taxInfoMap.put("Final Total", value);
								}
							}
						}

					}
				}
			}
			
			// Update return order with tax info
			Optional<ReturnOrder> returnOrderOptional = returnOrderRepository.findByRmaOrderNo(rmaNo);
			if (returnOrderOptional.isPresent()) {
			    ReturnOrder returnOrder = returnOrderOptional.get();
			    returnOrder.setInitialTotal(taxInfoMap.get("Initial Total"));
			    returnOrder.setTax(taxInfoMap.get("Tax"));
			    returnOrder.setFinalTotal(taxInfoMap.get("Final Total"));
			    
			    logger.info("Now saving tax Data against RMA !! ");
			    returnOrderRepository.save(returnOrder);
			}
			else {
				logger.info("Couldn't find the RMA Number .....! ");
			}

			return taxInfoMap;

		} catch (Exception e) {

			e.printStackTrace();
		}

		return taxInfoMap;
	}

}
