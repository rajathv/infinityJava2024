package com.kony.dbputilities.usersecurityservices;

import java.util.Map;

import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CheckSecureAccessCode implements JavaService2 {
	@SuppressWarnings("rawtypes")
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		Map inputParams = HelperMethods.getInputParamMap(inputArray);
		if (preProcess(inputParams, dcRequest, result)) {
			result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
					URLConstants.USER_GET);
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean preProcess(Map inputParams, DataControllerRequest dcRequest, Result result) {
		String userId = HelperMethods.getUserIdFromSession(dcRequest);
		String filter = "Id" + DBPUtilitiesConstants.EQUAL + userId;
		inputParams.put(DBPUtilitiesConstants.FILTER, filter);
		return true;
	}
}