package com.di.integration.p21.serviceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.di.commons.dto.OrderDTO;
import com.di.commons.dto.OrderItemDTO;
import com.di.commons.helper.P21ProductItem;
import com.di.commons.helper.P21ProductItemHelper;
import com.di.integration.constants.IntegrationConstants;
import com.di.integration.p21.service.P21ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class P21ProductServiceImpl implements P21ProductService {

	private static final Logger logger = LoggerFactory.getLogger(P21ReturnOrderServiceImpl.class);

	@Value(IntegrationConstants.ERP_DATA_API_BASE_URL)
	String ERP_BASE_URL;

	@Value(IntegrationConstants.ERP_PRODUCT_API_BASE_URL)
	String ERP_PRODUCT_API_BASE_URL;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Override
	public OrderDTO getProductByProductId(String productId) {
		OrderDTO orderDTO = new OrderDTO();
		String tenentId = httpServletRequest.getHeader("host").split("\\.")[0];

		MasterTenant masterTenant = masterTenantRepository.findByDbName(tenentId);
		try {
			CloseableHttpClient httpClient = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
			String baseUri = masterTenant.getSubdomain() + ERP_BASE_URL + ERP_PRODUCT_API_BASE_URL;
			String filter = "item_id eq '" + productId + "'";
			URIBuilder uriBuilder = new URIBuilder(baseUri);
			uriBuilder.addParameter("$format", "json");
			uriBuilder.addParameter("$filter", filter);

			URI fullURI = uriBuilder.build();
			HttpGet request = new HttpGet(fullURI);

			try {
				request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + p21TokenServiceImpl.findToken(null));
				logger.info("Token : " + p21TokenServiceImpl.findToken(null));

			} catch (Exception e) {
				e.printStackTrace();
			}
			request.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			HttpResponse response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			String responseBody = EntityUtils.toString(entity);
			logger.info("response :" + responseBody);

			ObjectMapper objectMapper = new ObjectMapper();
			P21ProductItemHelper p21ProductLineItemHelper = objectMapper.readValue(responseBody,
					P21ProductItemHelper.class);

			List<P21ProductItem> p21ProductLineItems = p21ProductLineItemHelper.getValue();
			List<OrderItemDTO> orderItems = new ArrayList<OrderItemDTO>();
			for (P21ProductItem p21ProductItem : p21ProductLineItems) {
				OrderItemDTO orderItemDTO = new OrderItemDTO();
				orderItemDTO.setItemName(p21ProductItem.getItem_id());
				orderItemDTO.setDescription(p21ProductItem.getItem_desc());
				orderItemDTO.setQuantity(0);
				orderItemDTO.setAmount(new BigDecimal(p21ProductItem.getPrice1()));
				orderItems.add(orderItemDTO);
			}
			orderDTO.setOrderItems(orderItems);

		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		} catch (KeyStoreException | NoSuchAlgorithmException | KeyManagementException e) {
			e.printStackTrace();
		}
		return orderDTO;
	}
}
