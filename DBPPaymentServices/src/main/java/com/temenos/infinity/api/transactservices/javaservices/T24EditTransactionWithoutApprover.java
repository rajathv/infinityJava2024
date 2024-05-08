package com.temenos.infinity.api.transactservices.javaservices;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.kony.dbx.util.Constants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class T24EditTransactionWithoutApprover implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(T24EditTransactionWithoutApprover.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> requestparameters = (Map<String, Object>) inputArray[1];
		String frequency = (requestparameters.get(Constants.PARAM_FREQUENCY_TYPE) != null)
				? requestparameters.get(Constants.PARAM_FREQUENCY_TYPE).toString()
				: null;
		if (Constants.FREQUENCY_ONCE.equalsIgnoreCase(frequency)) {
			return createOneTimeTransactionWithoutApproval(requestparameters, request);
		} else {
			return editRecurringTransactionWithoutApproval(requestparameters, request);
		}
	}

	private static Result createOneTimeTransactionWithoutApproval(Map<String, Object> requestParameters,
			DataControllerRequest request) {
		Result result = new Result();
		try {
			LOG.error("INPUT to BACKEND: " + new JSONObject(requestParameters).toString());
			String response = DBPServiceExecutorBuilder.builder()
					.withServiceId(TemenosConstants.SERVICE_T24IS_PAYMENTORDERS).withObjectId(null)
					.withOperationId(TemenosConstants.OP_CREATE_PAYMENT_WITHOUT_APPROVER)
					.withRequestParameters(requestParameters).withRequestHeaders(request.getHeaderMap())
					.withDataControllerRequest(request).build().getResponse();
			result = JSONToResult.convert(response);
			return result;
		} catch (Exception e) {
			LOG.error("Caught exception at create transaction without approval: ", e);
			result.addParam(new Param("dbpErrCode", "{\"errormsg\":\"" + e.getMessage() + "\"}"));
			return result;
		}
	}

	private static Result editRecurringTransactionWithoutApproval(Map<String, Object> requestParameters,
			DataControllerRequest request) {
		Result result = new Result();
		try {
			LOG.error("INPUT to BACKEND: " + new JSONObject(requestParameters).toString());
			String response = DBPServiceExecutorBuilder.builder()
					.withServiceId(TemenosConstants.SERVICE_T24IS_STANDINGORDERS).withObjectId(null)
					.withOperationId(TemenosConstants.OP_EDIT_STANDINGORDER_WITHOUT_APPROVER)
					.withRequestParameters(requestParameters).withRequestHeaders(request.getHeaderMap())
					.withDataControllerRequest(request).build().getResponse();
			result = JSONToResult.convert(response);
			return result;
		} catch (Exception e) {
			LOG.error("Caught exception at create transaction without approval: ", e);
			result.addParam(new Param("dbpErrCode", "{\"errormsg\":\"" + e.getMessage() + "\"}"));
			return result;
		}
	}

}