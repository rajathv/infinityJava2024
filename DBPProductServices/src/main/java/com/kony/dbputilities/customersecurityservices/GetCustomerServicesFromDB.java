package com.kony.dbputilities.customersecurityservices;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetCustomerServicesFromDB implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetCustomerServicesFromDB.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);

		Result returnResult = new Result();
		if (preProcess(inputParams, dcRequest)) {
			try {
				returnResult = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
						URLConstants.CUSTOMER_ENTITLEMENTS_PROC_GET);

			} catch (HttpCallException e) {
				LOG.error(e.getMessage());
			}
		}

		if (HelperMethods.hasRecords(returnResult)) {
			returnResult.getAllDatasets().get(0).setId("services");
		}

		return returnResult;

	}

	private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest) {
		String customer_id = dcRequest.getParameter("id");

		if (StringUtils.isBlank(customer_id)) {
			return false;
		}

		inputParams.put("_customerID", customer_id);

		return true;
	}

}