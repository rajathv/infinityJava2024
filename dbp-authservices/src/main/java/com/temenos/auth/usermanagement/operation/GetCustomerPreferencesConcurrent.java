package com.temenos.auth.usermanagement.operation;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetCustomerPreferencesConcurrent implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetCustomerPreferencesConcurrent.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
		if (preProcess(inputParams, dcRequest)) {
			try {
				result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
						URLConstants.CUSTOMER_PREFERENCES_CONCURRENT_ORCHESTRATION);
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
			return orchestrationResultProcess(result);
		}
		return result;
	}

	private Result orchestrationResultProcess(Result result) {
		Result returnResult = new Result();
		
		Dataset dataset = result.getDatasetById("address");
		if (dataset != null) {
			Record record = dataset.getAllRecords().get(0);
			for (Param param : record.getAllParams()) {
				returnResult.addParam(param);
			}
		}
		Record commRecord = result.getRecordById("customercommunication");
        if (commRecord != null) {
            for (Param param : commRecord.getAllParams()) {
                returnResult.addParam(param);
            }
        }
		Record customerpreferences = result.getRecordById("customerpreferences");
		if (customerpreferences != null) {
			for (Param param : customerpreferences.getAllParams()) {
				returnResult.addParam(param);
			}
		}
		return returnResult;
	}

	private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest) {
		String id = inputParams.get("id");
		if (StringUtils.isBlank(id)) {
			return false;
		}
		return true;
	}
}