package com.temenos.infinity.api.holdings.javaservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.TokenUtils;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.config.InfinityServices;
import com.temenos.infinity.api.commons.invocation.Executor;
import com.temenos.infinity.api.holdings.config.HoldingsAPIServices;
import com.temenos.infinity.api.holdings.config.ServerConfigurations;
import com.temenos.infinity.api.holdings.constants.ErrorCodeEnum;
import com.temenos.infinity.api.holdings.constants.MSCertificateConstants;
import com.temenos.infinity.api.holdings.resource.api.AccountTransactionsResource;
import com.temenos.infinity.api.holdings.util.HoldingsUtils;
import com.temenos.infinity.api.holdings.util.TransactionTypeProperties;
import com.temenos.infinity.transact.tokenmanager.jwt.TokenGenerator;

public class GetSearchTransactionsOperation implements JavaService2 {

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		//Load Transaction Types
        TransactionTypeProperties props = new TransactionTypeProperties(request);
		try {
			// Initializing of AccountTransactions through Abstract factory method
			 Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			AccountTransactionsResource AccountTransactionsResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(AccountTransactionsResource.class);
			
			Map<String, String> inputParamMap = new HashMap<>();
			inputParamMap.put("customerId",HoldingsUtils.getUserAttributeFromIdentity(request, "customer_id"));
			String authToken = TokenUtils.getHoldingsMSAuthToken(inputParamMap);
			HoldingsUtils.setCompanyIdToRequest(request);
			String ARRANGEMENTS_BACKEND = ServerConfigurations.ARRANGEMENTS_BACKEND.getValueIfExists();
			if (StringUtils.isNotBlank(ARRANGEMENTS_BACKEND)) {
			if (ARRANGEMENTS_BACKEND.equals("t24")) {
				HashMap<String, Object> inputParams1 = new HashMap<String, Object>();
				for ( Map.Entry<String, String> entry : inputParams.entrySet()) {
				    String key = entry.getKey();
				    String value = entry.getValue();
				    inputParams1.put(key, value);    
				}
        		
				String transactions = DBPServiceExecutorBuilder.builder()
                        .withServiceId("ArrangementsT24ISTransactions")
                        .withOperationId("searchTransactions")
                        .withRequestParameters(inputParams1).withRequestHeaders(request.getHeaderMap())
                        .withDataControllerRequest(request).build().getResponse();
				return JSONToResult.convert(transactions);
			}
			
			else if (ARRANGEMENTS_BACKEND.equals("MOCK")) {
	        	Map<String, Object> inputMap = new HashMap<>();
	            Map<String, Object> headerMap = new HashMap<>();
	     
	            String transactionResponse = Executor.invokePassThroughServiceAndGetString((InfinityServices)
	            		HoldingsAPIServices.DOWNLOAD_STATEMENTS_MOCK, inputMap, headerMap);
				return JSONToResult.convert(transactionResponse);
	            
	    	}
		}
			 result = AccountTransactionsResource.searchAccountTransactions((Map<String, Object>) inputArray[1], request,authToken);
		} catch (Exception exception) {
			return ErrorCodeEnum.ERR_20041.setErrorCode(new Result());
		}
		return result;
	}
}
