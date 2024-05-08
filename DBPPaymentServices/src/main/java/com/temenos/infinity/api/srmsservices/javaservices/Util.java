package com.temenos.infinity.api.srmsservices.javaservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.srmstransactions.config.SrmsTransactionsAPIServices;

public class Util  {
	private static final Logger LOG = LogManager.getLogger(Util.class);

	public static String createOrder(Map<String, Object> requestParameters, DataControllerRequest request) {

		String createOrderResponse = "";

		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		// Making a call to order request API
		try {
			createOrderResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(SrmsTransactionsAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
					.withOperationId(SrmsTransactionsAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
					.withRequestParameters(requestParameters).withRequestHeaders(headerMap)
					.withDataControllerRequest(request).build().getResponse();
			JSONObject responseObject = null;
			if (StringUtils.isNotBlank(createOrderResponse)) {
				responseObject = new JSONObject(createOrderResponse);
				if (!responseObject.has("dbpErrMsg")) {
					responseObject.put("referenceId", responseObject.get("orderId").toString());
				}
				if (responseObject.has("additionalInfo")
						&& StringUtils.isNotBlank(responseObject.getString("additionalInfo"))) {
					responseObject.put("messageDetails", responseObject.get("additionalInfo").toString());
				}
				if (responseObject.has("errorDetail")
						&& StringUtils.isNotBlank(responseObject.getString("errorDetail"))) {
					responseObject.put("errorDetails", responseObject.get("errorDetail").toString());
				}
			}
			createOrderResponse = responseObject.toString();

		} catch (Exception e) {
			LOG.error("Caught exception at create transaction without approval: ", e);
			return "{\"errormsg\":\"" + e.getMessage() + "\"}";
		}
		return createOrderResponse;
	}

}
