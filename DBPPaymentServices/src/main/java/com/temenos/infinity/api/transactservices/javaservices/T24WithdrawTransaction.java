package com.temenos.infinity.api.transactservices.javaservices;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.temenos.infinity.api.transactservices.constants.Constants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class T24WithdrawTransaction implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(T24WithdrawTransaction.class);
	
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> requestparameters = (Map<String, Object>) inputArray[1];
		String transactionId=(requestparameters.get("referenceId") != null) ? requestparameters.get("referenceId").toString() : null;
		String frequency=(requestparameters.get(Constants.FREQUENCYTYPE) != null) ? requestparameters.get(Constants.FREQUENCYTYPE).toString() : null;
		if (Constants.FREQUENCY_ONCE.equalsIgnoreCase(frequency)) {
			return withdrawOneTimeTransaction(transactionId,request);
		} else {
			return withdrawRecurringTransaction(transactionId,request);
		}
	}
	private static Result withdrawOneTimeTransaction(String referenceId, DataControllerRequest request) {
		Result result = new Result();
		try {
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("transactionId",referenceId);
			LOG.error("INPUT to BACKEND: "+new JSONObject(requestParameters).toString());
			String response = DBPServiceExecutorBuilder.builder().
					withServiceId(TemenosConstants.SERVICE_T24IS_PAYMENTORDERS).
					withObjectId(null).
					withOperationId(TemenosConstants.OP_WITHDRAW_PAYMENT).
					withRequestParameters(requestParameters).
					withRequestHeaders(request.getHeaderMap()).
					withDataControllerRequest(request).
					build().getResponse();
			result = JSONToResult.convert(response);
			return result;
		}
		catch (Exception e) {
			LOG.error("Caught exception at withdraw transaction: ", e);
			result.addParam(new Param("dbpErrCode", "{\"errormsg\":\""+e.getMessage()+"\"}"));
			return result;
		}
	}
	
	private static Result withdrawRecurringTransaction(String referenceId, DataControllerRequest request) {
		Result result = new Result();
		try {
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("transactionId",referenceId);
			LOG.error("INPUT to BACKEND: "+new JSONObject(requestParameters).toString());
			String response = DBPServiceExecutorBuilder.builder().
					withServiceId(TemenosConstants.SERVICE_T24IS_STANDINGORDERS).
					withObjectId(null).
					withOperationId(TemenosConstants.OP_WITHDRAW_STANDINGORDER).
					withRequestParameters(requestParameters).
					withRequestHeaders(request.getHeaderMap()).
					withDataControllerRequest(request).
					build().getResponse();
			result = JSONToResult.convert(response);
			return result;
		}
		catch (Exception e) {
			LOG.error("Caught exception at withdraw transaction: ", e);
			result.addParam(new Param("dbpErrCode", "{\"errormsg\":\""+e.getMessage()+"\"}"));
			return result;
		}
	}
}
