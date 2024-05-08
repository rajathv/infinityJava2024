package com.temenos.dbx.product.transactionservices.resource.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.GeneralTransactionsBusinessDelegate;
import com.temenos.dbx.product.transactionservices.dto.GeneralTransactionDTO;
import com.temenos.dbx.product.transactionservices.resource.api.GeneralTransactionsResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

public class GeneralTransactionsResourceImpl implements GeneralTransactionsResource{

	private static final Logger LOG = LogManager.getLogger(GeneralTransactionsResourceImpl.class);
	
	@Override
	public Result fetchAllGeneralTransactions(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		Result result = null;
		GeneralTransactionsBusinessDelegate generalTransactionBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(GeneralTransactionsBusinessDelegate.class);
		
		try {
			
			Map<String, Object> customer = CustomerSession.getCustomerMap(request);
			String userId = CustomerSession.getCustomerId(customer);
			
			List<String> requiredActionIds = Arrays.asList(
					FeatureAction.BILL_PAY_VIEW_PAYMENTS,
					FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_VIEW,
					FeatureAction.INTRA_BANK_FUND_TRANSFER_VIEW,
					FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_VIEW,
					FeatureAction.P2P_VIEW,
					FeatureAction.DOMESTIC_WIRE_TRANSFER_VIEW,
					FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_VIEW,
					FeatureAction.INTERNATIONAL_WIRE_TRANSFER_VIEW
					);
			
			String featureActionId = CustomerSession.getPermittedActionIds(request, requiredActionIds);
			
			if(featureActionId == null) {
	     		return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}
	
			@SuppressWarnings("unchecked")
			Map<String, Object> filterParamsMap = (HashMap<String, Object>) inputArray[1];
			filterParamsMap.put(Constants._FEATURE_ACTION_LIST, featureActionId);
			
			JSONObject requestObj = new JSONObject(filterParamsMap);
			FilterDTO params = JSONUtils.parse(requestObj.toString(), FilterDTO.class);
			
			List<GeneralTransactionDTO> transactions = generalTransactionBusinessDelegate.fetchGeneralTransactions(userId, "", "", params);
			
			if(transactions != null) {
				String listResponse = JSONUtils.stringifyCollectionWithTypeInfo(transactions, GeneralTransactionDTO.class);
				JSONArray resArray = new JSONArray(listResponse);
				JSONObject resultObj = new JSONObject();
				resultObj.put(Constants.RECORDS, resArray);
				result = JSONToResult.convert(resultObj.toString());
			}
			else
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		catch(Exception exp) {
			LOG.error("Error occurred while defining resources for fetch all general transactions", exp);
			return null;
		}
		return result;
	}
	
	@Override
	public Result fetchGeneralTransactionById(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		Result result = null;
		GeneralTransactionsBusinessDelegate generalTransactionBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(GeneralTransactionsBusinessDelegate.class);
		
		try {
			
			@SuppressWarnings("unchecked")
			Map<String, Object> filterParamsMap = (HashMap<String, Object>) inputArray[1];
			String transactionId = filterParamsMap.get("transactionId").toString();
			String featureActionId = filterParamsMap.get("featureActionId").toString();
			
			if(transactionId == null || "".equals(transactionId)) {
				LOG.error("transactionId is missing");
				return ErrorCodeEnum.ERR_12026.setErrorCode(new Result());
			}
			
			if(featureActionId == null || "".equals(featureActionId)) {
				LOG.error("featureActionId is missing");
				return ErrorCodeEnum.ERR_12040.setErrorCode(new Result());
			}
			
			Map<String, Object> customer = CustomerSession.getCustomerMap(request);
			String userId = CustomerSession.getCustomerId(customer);
			
			List<String> requiredActionIds = Arrays.asList(
					FeatureAction.BILL_PAY_VIEW_PAYMENTS,
					FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_VIEW,
					FeatureAction.INTRA_BANK_FUND_TRANSFER_VIEW,
					FeatureAction.TRANSFER_BETWEEN_OWN_ACCOUNT_VIEW,
					FeatureAction.P2P_VIEW,
					FeatureAction.DOMESTIC_WIRE_TRANSFER_VIEW,
					FeatureAction.INTERNATIONAL_ACCOUNT_FUND_TRANSFER_VIEW,
					FeatureAction.INTERNATIONAL_WIRE_TRANSFER_VIEW
					);
			
			String permittedActionId = CustomerSession.getPermittedActionIds(request, requiredActionIds);
			
			if(permittedActionId == null) {
	     		return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}
	
			filterParamsMap.put(Constants._FEATURE_ACTION_LIST, permittedActionId);
			
			JSONObject requestObj = new JSONObject(filterParamsMap);
			FilterDTO params = JSONUtils.parse(requestObj.toString(), FilterDTO.class);
			
			//To Fetch the transaction entry based on given transactionId/confirmationNumber
			List<GeneralTransactionDTO> transactionDTO = generalTransactionBusinessDelegate.fetchTransactionById(transactionId, featureActionId, request);
			if(transactionDTO == null || transactionDTO.isEmpty()) {
				LOG.error("Record Doesn't Exist");
	            return ErrorCodeEnum.ERR_12606.setErrorCode(new Result());
			}
			
			transactionId = transactionDTO.get(0).getTransactionId();
			
			//To fetch transaction details from proc
			List<GeneralTransactionDTO> transactions = generalTransactionBusinessDelegate.fetchGeneralTransactions(userId, transactionId, featureActionId, params);
			
			if(transactions != null) {
				String listResponse = JSONUtils.stringifyCollectionWithTypeInfo(transactions, GeneralTransactionDTO.class);
				JSONArray resArray = new JSONArray(listResponse);
				JSONObject resultObj = new JSONObject();
				resultObj.put(Constants.RECORDS, resArray);
				result = JSONToResult.convert(resultObj.toString());
			}
			else
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		catch(Exception exp) {
			LOG.error("Error occurred while fetching general transaction by id", exp);
			return null;
		}
		return result;
	}
	
}