package com.kony.dbputilities.customersecurityservices;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetCustomerAddressFromDB implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetCustomerAddressFromDB.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		Result returnResult = new Result();
		if (preProcess(inputParams, dcRequest)) {
			try {
				returnResult = HelperMethods.callGetApi(dcRequest, inputParams.get(DBPUtilitiesConstants.FILTER),
						HelperMethods.getHeaders(dcRequest), URLConstants.CUSTOMER_ADDRESS_GET);

				if (HelperMethods.hasRecords(returnResult)) {

					String filter = "id" + DBPUtilitiesConstants.EQUAL
							+ HelperMethods.getFieldValue(returnResult, "Address_id");

					inputParams.put(DBPUtilitiesConstants.FILTER, filter);

					returnResult = HelperMethods.callGetApi(dcRequest, inputParams.get(DBPUtilitiesConstants.FILTER),
							HelperMethods.getHeaders(dcRequest), URLConstants.ADDRESS_GET);

				}

			} catch (HttpCallException e) {
				LOG.error(e.getMessage());
			}
		}

		return returnResult;

	}

	private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest) {
		String customer_id = inputParams.get("id");

		if (StringUtils.isBlank(customer_id)) {
			return false;
		}

		String filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + customer_id + DBPUtilitiesConstants.AND
				+ "isPrimary" + DBPUtilitiesConstants.EQUAL + "1";

		inputParams.put(DBPUtilitiesConstants.FILTER, filter);

		return true;
	}
}