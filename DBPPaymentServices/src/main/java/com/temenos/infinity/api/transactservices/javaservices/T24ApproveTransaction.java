package com.temenos.infinity.api.transactservices.javaservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.transactservices.constants.Constants;
public class T24ApproveTransaction implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(T24ApproveTransaction.class);
	
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		//we should send an requestparams map  with frequency and reference id from base class
		//but only reference id should be sent to t24
		@SuppressWarnings("unchecked")
		Map<String, Object> requestparameters =(Map<String, Object> )inputArray[1];
		String transactionId = (requestparameters.get("referenceId") != null) ? requestparameters.get("referenceId").toString() : null;
		String frequencyType = (requestparameters.get(Constants.FREQUENCYTYPE) != null) ? requestparameters.get(Constants.FREQUENCYTYPE).toString() : null;
		if (Constants.FREQUENCY_ONCE.equalsIgnoreCase(frequencyType)) {
			return approveOneTimeTransaction(transactionId,request);
		} else {
			return approveRecurringTransaction(transactionId,request);
		}
	}
	private static Result approveOneTimeTransaction(String referenceId, DataControllerRequest request) {
		Result result = new Result();
		try {
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("transactionId",referenceId);
			LOG.error("INPUT to BACKEND: "+new JSONObject(requestParameters).toString());
			String response =  DBPServiceExecutorBuilder.builder().
					withServiceId(TemenosConstants.SERVICE_T24IS_PAYMENTORDERS).
					withObjectId(null).
					withOperationId(TemenosConstants.OP_APPROVE_PAYMENT).
					withRequestParameters(requestParameters).
					withRequestHeaders(request.getHeaderMap()).
					withDataControllerRequest(request).
					build().getResponse();
			result = JSONToResult.convert(response);
			return result;
		}
		catch (Exception e) {
			LOG.error("Caught exception at approve transaction: ", e);
			result.addParam(new Param("dbpErrCode", "{\"errormsg\":\""+e.getMessage()+"\"}"));
			return result;
		}
	}
	private static Result approveRecurringTransaction(String referenceId, DataControllerRequest request) {
		Result result = new Result();
		try {
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("transactionId",referenceId);
			LOG.error("INPUT to BACKEND: "+new JSONObject(requestParameters).toString());
			String response = DBPServiceExecutorBuilder.builder().
					withServiceId(TemenosConstants.SERVICE_T24IS_STANDINGORDERS).
					withObjectId(null).
					withOperationId(TemenosConstants.OP_APPROVE_STANDINGORDER).
					withRequestParameters(requestParameters).
					withRequestHeaders(request.getHeaderMap()).
					withDataControllerRequest(request).
					build().getResponse();
			result = JSONToResult.convert(response);
			return result;
		}
		catch (Exception e) {
			LOG.error("Caught exception at approve transaction: ", e);
			result.addParam(new Param("dbpErrCode", "{\"errormsg\":\""+e.getMessage()+"\"}"));
			return result;
		}
	}
}