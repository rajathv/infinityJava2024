package com.temenos.dbx.product.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public class CustomerSessionsUtil {
	public static Result deleteActiveUserSessionsIfAny(String customerId, String userName, DataControllerRequest dcRequest) {
		String quantumAuthToken = AdminUtil.getQuantumAuthToken(dcRequest);
		Result result = new Result();
		if (StringUtils.isBlank(quantumAuthToken) || StringUtils.isBlank(customerId)) {
			result.addStringParam("errmsg", "Mandatory input empty for delete active sessions");
			return result;
		}
		Map<String, Object> inputParams = new HashMap<String, Object>();
		Map<String, Object> headerMap = dcRequest.getHeaderMap();
		headerMap.put("claims_token", quantumAuthToken);
		String userIds = "[\"" + customerId + "\",\"" + userName +"\"]";
		inputParams.put("userIds", userIds);
		inputParams.put("providerName", "DbxUserLogin");
		inputParams.put("providerType", "custom");
		try {
			JsonObject response = ServiceCallHelper.invokePassThroughServiceAndGetJson(dcRequest, inputParams,
					headerMap, URLConstants.DELETE_ACTIVE_USER_SESSIONS);
			if (response != null && !response.isJsonNull() && response.has("userSessionsCount")) {
				return result;
			} else {
				result.addStringParam("errmsg", "Failed to delete active user sessions");
				return result;
			}
		} catch (Exception e) {
			result.addStringParam("errmsg", "Failed to delete active user sessions");
			return result;
		}
	}
}
