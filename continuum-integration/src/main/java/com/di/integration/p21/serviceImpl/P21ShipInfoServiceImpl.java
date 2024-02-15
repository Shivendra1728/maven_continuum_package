package com.di.integration.p21.serviceImpl;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.continuum.multitenant.mastertenant.entity.MasterTenant;
import com.continuum.multitenant.mastertenant.repository.MasterTenantRepository;
import com.di.integration.p21.service.P21ShipInfoService;

@Service
public class P21ShipInfoServiceImpl implements P21ShipInfoService {
	private static final Logger logger = LoggerFactory.getLogger(P21ShipInfoServiceImpl.class);

	@Autowired
	MasterTenantRepository masterTenantRepository;

	@Autowired
	HttpServletRequest httpServletRequest;

	@Autowired
	P21TokenServiceImpl p21TokenServiceImpl;

	@Override
	public Map<String, String> getShipInfo(String orderNo, MasterTenant masterTenantObject) throws Exception {

		Map<String, String> freightInfoMap = new HashMap<>();

		MasterTenant masterTenant;

		if (masterTenantObject == null) {
			String tenantId = httpServletRequest.getHeader("tenant");
			masterTenant = masterTenantRepository.findByDbName(tenantId);
			masterTenantObject = masterTenant;
		} else {
			masterTenant = masterTenantObject;
		}

		String getShipInfo = masterTenant.getSubdomain() + "/data/erp/views/v1/p21_view_oe_hdr";
		logger.info("URI to get Ship Info : " + getShipInfo);

		URI firstURI = new URIBuilder(getShipInfo).addParameter("$format", "json")
				.addParameter("$filter", "order_no eq '" + orderNo + "'").build();

		logger.info("Full First URI: " + firstURI);

		String token = p21TokenServiceImpl.findToken(masterTenant);
		logger.info("#### TOKEN #### {}", token);
		try {
			CloseableHttpClient httpClient1 = HttpClients.custom()
					.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
					.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
			HttpGet getShipInfoRequest = new HttpGet(firstURI);

			getShipInfoRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			getShipInfoRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
			getShipInfoRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

			CloseableHttpResponse getShipInfoResponse = httpClient1.execute(getShipInfoRequest);
			String getShipInfoResponseBody = EntityUtils.toString(getShipInfoResponse.getEntity());
			logger.info("Ship Info response :" + getShipInfoResponseBody);

			JSONObject jsonResponse = new JSONObject(getShipInfoResponseBody);
			JSONArray valueArray = jsonResponse.getJSONArray("value");
			if (valueArray.length() > 0) {
				JSONObject firstObject = valueArray.getJSONObject(0);
				int freightCodeUid = firstObject.getInt("freight_code_uid");
				logger.info("Freight Code UID: " + freightCodeUid);

				// Freight UID found , Change the view to search

				String getFreightInfoBase = masterTenant.getSubdomain() + "/data/erp/views/v1/p21_view_freight_code";

				URI fullFreightInfoURI = new URIBuilder(getFreightInfoBase)
				        .addParameter("$format", "json")
				        .addParameter("$filter", "freight_code_uid eq " + freightCodeUid)
				        .build();

				logger.info("Full Freight Info URI: " + fullFreightInfoURI);

				CloseableHttpClient httpClient2 = HttpClients.custom()
						.setSSLContext(SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build())
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
				HttpGet getFreightInfoRequest = new HttpGet(fullFreightInfoURI);

				getFreightInfoRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
				getFreightInfoRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
				getFreightInfoRequest.addHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

				CloseableHttpResponse feightInfoResponse = httpClient2.execute(getFreightInfoRequest);
				String freightInfoResponseBody = EntityUtils.toString(feightInfoResponse.getEntity());
				logger.info("Freight Info response : : " + freightInfoResponseBody);

				JSONObject jsonResponse1 = new JSONObject(freightInfoResponseBody);
				JSONArray valueArray2 = jsonResponse1.getJSONArray("value");
				if (valueArray2.length() > 0) {
					 JSONObject secondObject = valueArray2.getJSONObject(0);
				        if (secondObject.has("freight_cd")) {
				            String freightCd = secondObject.getString("freight_cd");
				            logger.info("Freight CD : " + freightCd);
				            freightInfoMap.put("freight_cd", freightCd);
				        } else {
				        	String freightCd = null ;
				            logger.info("Freight CD not found in the response.");
				            freightInfoMap.put("freight_cd", freightCd);
				        }

				        if (secondObject.has("freight_desc")) {
				            String freightDesc = secondObject.getString("freight_desc");
				            logger.info("Freight Desc : " + freightDesc);
				            freightInfoMap.put("freight_desc", freightDesc);
				        } else {
				        	String freightDesc = null ;
				            logger.info("Freight Desc not found in the response.");
				            freightInfoMap.put("freight_cd", freightDesc);
				        }
				        
				        return freightInfoMap;
				        
				} else {
					logger.info("We couldn't find anything in second api call. ");
					return null;
				}

			} else {
				logger.info("No data found in first api response.");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
