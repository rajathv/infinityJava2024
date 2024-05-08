package com.kony.dbputilities.customerentitlement;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.MWConstants;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class EntitlementsGetIsSecurityQuestionsConfigured implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(EntitlementsGetIsSecurityQuestionsConfigured.class);
	private static final String INPUT_CUSTOMER_ID = "customerId";

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
			DataControllerResponse responseInstance) throws Exception {
		Result processedResult = new Result();

		try {
			String customerId = requestInstance.getParameter(INPUT_CUSTOMER_ID);
			String authToken = HelperMethods.getAuthToken(requestInstance);

			// Validate Service Inputs
			if (StringUtils.isBlank(customerId)) {
				ErrorCodeEnum.ERR_10688.setErrorCode(processedResult);
				return processedResult;
			}

			// Fetch security questions information
			JSONObject securityQuestionsResponse = getSecurityQuestionsForGivenCustomer(customerId, authToken,
					requestInstance);
			if (securityQuestionsResponse == null || !securityQuestionsResponse.has(MWConstants.OPSTATUS)
					|| securityQuestionsResponse.getInt(MWConstants.OPSTATUS) != 0
					|| !securityQuestionsResponse.has("customersecurityquestion_view")) {
				LOG.error("Failed read operation on view customersecurityquestion_view");
				ErrorCodeEnum.ERR_10691.setErrorCode(processedResult);
				return processedResult;
			}
			JSONArray securityQuestionsForCustomer = (JSONArray) securityQuestionsResponse
					.get("customersecurityquestion_view");
			Boolean isSecurityQuestionConfigured = false;
			if (securityQuestionsForCustomer.length() != 0) {
				isSecurityQuestionConfigured = true;
			}
			processedResult.addParam(new Param("isSecurityQuestionConfigured",
					String.valueOf(isSecurityQuestionConfigured), MWConstants.STRING));
			return processedResult;

		} catch (Exception e) {
			LOG.error("Exception occurred while processing customer entitlements service ", e);
			ErrorCodeEnum.ERR_10700.setErrorCode(processedResult);
		}
		return processedResult;
	}

	public JSONObject getSecurityQuestionsForGivenCustomer(String customerId, String authToken,
			DataControllerRequest requestInstance) throws HttpCallException {
		Map<String, String> queryMap = new HashMap<>();
		queryMap.put(DBPUtilitiesConstants.FILTER, "Customer_id eq'" + customerId + "'");
		String readEndpointResponse = HelperMethods.callApiAndGetString(requestInstance, queryMap, null,
				URLConstants.CUSTOMERSECURITYQUESTION_VIEW_READ);
		return HelperMethods.getStringAsJSONObject(readEndpointResponse);
	}
}
