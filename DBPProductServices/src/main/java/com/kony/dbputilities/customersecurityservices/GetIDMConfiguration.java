package com.kony.dbputilities.customersecurityservices;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetIDMConfiguration implements JavaService2 {

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);

		if (preProcess(inputParams, dcRequest, result)) {
			result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
					URLConstants.IDMCONFIGURATION_GET);
		}
		return result;
	}

	private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result) {
		String id = inputParams.get("id");
		String idmKey = inputParams.get("IDMKey");
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(id)) {
			sb.append("id").append(DBPUtilitiesConstants.EQUAL).append(id);
		} else if (StringUtils.isNotBlank(idmKey)) {
			sb.append("IDMKey").append(DBPUtilitiesConstants.EQUAL).append(idmKey);
		}

		inputParams.put(DBPUtilitiesConstants.FILTER, sb.toString());
		return true;
	}
}