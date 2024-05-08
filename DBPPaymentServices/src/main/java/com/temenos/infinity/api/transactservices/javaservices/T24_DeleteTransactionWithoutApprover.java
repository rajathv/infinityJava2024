package com.temenos.infinity.api.transactservices.javaservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.transactservices.constants.Constants;

public class T24_DeleteTransactionWithoutApprover implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(T24_DeleteTransactionWithoutApprover.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> requestparameters = (Map<String, Object>)inputArray[1];
		String frequencyType = (requestparameters.get(Constants.PARAM_FREQUENCY_TYPE) != null) ? requestparameters.get(Constants.PARAM_FREQUENCY_TYPE).toString() : null;
        String transactionId=(requestparameters.get(Constants.PARAM_TRANSACTION_ID) != null) ? requestparameters.get(Constants.PARAM_TRANSACTION_ID).toString() : null;
		if (Constants.FREQUENCY_ONCE.equalsIgnoreCase(frequencyType)) {
			return cancelOneTimeTransactionWithoutApproval(transactionId, request);
		} else {
			return cancelRecurringTransactionWithoutApproval(transactionId, request);
		}
	}

	private static Result cancelOneTimeTransactionWithoutApproval(String transactionId, DataControllerRequest request) {
		Result result = new Result();
		try {
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			String operationName = TemenosConstants.OP_REVERSE_PAYMENT_WITHOUT_APPROVER;
			String fetchTransactionbyId = callfetchTransactionById(transactionId, request);
			JSONObject responseObj = new JSONObject(fetchTransactionbyId);
			JSONArray jsonArray = TemenosUtils.getFirstOccuringArray(responseObj);
			if(jsonArray == null || jsonArray.length()<1) {
				LOG.error("Transaction not found");
				result.addParam(new Param("errormsg", "{\"errormsg\":\"Transaction not found\"}"));
				return result;
			}
			
			requestParameters.put("transactionId",transactionId);
			LOG.error("INPUT to BACKEND: "+new JSONObject(requestParameters).toString());
			String response = DBPServiceExecutorBuilder.builder().
					withServiceId(TemenosConstants.SERVICE_T24IS_PAYMENTORDERS).  
					withObjectId(null).
					withOperationId(operationName). 
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

	private static Result cancelRecurringTransactionWithoutApproval(String transactionId,
			DataControllerRequest request) {
		Result result = new Result();
		try {
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("transactionId", transactionId);
			LOG.error("INPUT to BACKEND: " + new JSONObject(requestParameters).toString());
			String response = DBPServiceExecutorBuilder.builder().withServiceId(TemenosConstants.SERVICE_T24IS_STANDINGORDERS)
					.withObjectId(null).withOperationId(TemenosConstants.OP_REVERSE_STANDINGORDER_WITHOUT_APPROVER)
					.withRequestParameters(requestParameters).withRequestHeaders(request.getHeaderMap())
					.withDataControllerRequest(request).build().getResponse();
			result = JSONToResult.convert(response);
			return result;
		} catch (Exception e) {
			LOG.error("Caught exception at approve transaction: ", e);
			result.addParam(new Param("dbpErrCode", "{\"errormsg\":\""+e.getMessage()+"\"}"));
			return result;
		}
	}

	private static String callfetchTransactionById(String referenceId, DataControllerRequest request) {

		try {
			referenceId = referenceId.replaceAll("_PSD2", "");
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("transactionId", referenceId);
			LOG.error("INPUT to BACKEND: " + new JSONObject(requestParameters).toString());
			String response = DBPServiceExecutorBuilder.builder().withServiceId(TemenosConstants.T24_IS_PAYMENTS_VIEW)
					.withObjectId(null).withOperationId(TemenosConstants.OP_GET_PAYMENT_ORDER_TRANSACTION_DETAILS)
					.withRequestParameters(requestParameters).withRequestHeaders(request.getHeaderMap())
					.withDataControllerRequest(request).build().getResponse();

			JSONObject resObj = new JSONObject(response);
			JSONArray transArray = TemenosUtils.getFirstOccuringArray(resObj);
			stringifyCharges(transArray);
			return resObj.toString();
		} catch (Exception e) {
			LOG.error("Caught exception fetching transaction details by Id: ", e);
			return "{\"errormsg\":\"" + e.getMessage() + "\"}";
		}
	}

	private static JSONArray stringifyCharges(JSONArray jsonArray) {
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject js = (jsonArray.getJSONObject(i));
			String charges = js.optString("charges");
			if (!StringUtils.isBlank(charges)) {
				js.put("charges", charges.toString());
			}
		}
		return jsonArray;
	}
}
