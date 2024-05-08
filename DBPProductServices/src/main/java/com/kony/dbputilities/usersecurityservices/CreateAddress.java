package com.kony.dbputilities.usersecurityservices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class CreateAddress implements JavaService2 {
	@SuppressWarnings("rawtypes")
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		Map inputParams = HelperMethods.getInputParamMap(inputArray);
		if (preProcess(inputParams, dcRequest, result)) {
			result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
					URLConstants.ADDRESS_CREATE);
		}
		return result;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean preProcess(Map inputParams, DataControllerRequest dcRequest, Result result)
			throws HttpCallException {
		String userId = HelperMethods.getUserIdFromSession(dcRequest);
		String isPrimary = (String) inputParams.get("isPreferredAddress");
		if ("1".equals(isPrimary) || "true".equals(isPrimary)) {
			modifyOldPrefferedAddress(dcRequest, userId);
		}

		String id = UUID.randomUUID().toString();
		inputParams.put("id", id);

		inputParams.put("User_id", userId);
		inputParams.put("type", inputParams.get("addressType"));
		inputParams.put("cityName", inputParams.get("city"));
		inputParams.put("zipCode", inputParams.get("zipcode"));
		return true;
	}

	private void modifyOldPrefferedAddress(DataControllerRequest dcRequest, String userId) throws HttpCallException {
		String filter = "User_id" + DBPUtilitiesConstants.EQUAL + userId + DBPUtilitiesConstants.AND
				+ "isPreferredAddress" + DBPUtilitiesConstants.EQUAL + "1";
		Result preferredAddresses = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest),
				URLConstants.ADDRESS_GET);
		if (HelperMethods.hasRecords(preferredAddresses)) {
			List<Record> addresses = preferredAddresses.getAllDatasets().get(0).getAllRecords();
			for (Record address : addresses) {
				updateAddress(dcRequest, address);
			}
		}
	}

	private void updateAddress(DataControllerRequest dcRequest, Record address) throws HttpCallException {
		Map<String, String> input = new HashMap<>();
		input.put("id", HelperMethods.getFieldValue(address, "id"));
		input.put("isPreferredAddress", "0");
		HelperMethods.callApi(dcRequest, input, HelperMethods.getHeaders(dcRequest), URLConstants.ADDRESS_UPDATE);
	}
}