package com.kony.dbputilities.customersecurityservices;

import java.util.List;
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
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetCustomerMobileAndMailIdsFromDB implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetCustomerMobileAndMailIdsFromDB.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);

		Result result = new Result();
		Result returnResult = new Result();
		if (preProcess(inputParams, dcRequest)) {
			try {
				result = HelperMethods.callGetApi(dcRequest, inputParams.get(DBPUtilitiesConstants.FILTER),
						HelperMethods.getHeaders(dcRequest), URLConstants.CUSTOMER_COMMUNICATION_GET);

			} catch (HttpCallException e) {
				LOG.error(e.getMessage());
			}
		}

		if (HelperMethods.hasRecords(result)) {

			List<Record> list = result.getAllDatasets().get(0).getAllRecords();
			Dataset emailIds = new Dataset();
			emailIds.setId("EmailIds");
			Dataset contactNumbers = new Dataset();
			contactNumbers.setId("ContactNumbers");

			for (Record record : list) {
				if (record.getNameOfAllParams().contains("Type_id")
						&& record.getParamValueByName("Type_id").equals(DBPUtilitiesConstants.COMM_TYPE_EMAIL)) {
					emailIds.addRecord(record);
				} else if (record.getNameOfAllParams().contains("Type_id")
						&& record.getParamValueByName("Type_id").equals(DBPUtilitiesConstants.COMM_TYPE_PHONE)) {
					contactNumbers.addRecord(record);
				}
			}

			returnResult.addDataset(emailIds);
			returnResult.addDataset(contactNumbers);
		}

		return returnResult;

	}

	private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest) {
		String customer_id = dcRequest.getParameter("id");

		if (StringUtils.isBlank(customer_id)) {
			return false;
		}

		String filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + customer_id + DBPUtilitiesConstants.AND
				+ "Type_id " + DBPUtilitiesConstants.EQUAL + DBPUtilitiesConstants.COMM_TYPE_EMAIL
				+ DBPUtilitiesConstants.OR + "Type_id" + DBPUtilitiesConstants.EQUAL
				+ DBPUtilitiesConstants.COMM_TYPE_PHONE;

		inputParams.put(DBPUtilitiesConstants.FILTER, filter);

		return true;
	}

}