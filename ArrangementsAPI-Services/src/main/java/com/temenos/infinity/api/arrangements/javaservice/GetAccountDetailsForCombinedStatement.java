package com.temenos.infinity.api.arrangements.javaservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.TokenUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.resource.api.ArrangementsResource;
import com.temenos.infinity.api.arrangements.utils.CommonUtils;

public class GetAccountDetailsForCombinedStatement implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetAccountDetailsForCombinedStatement.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			Map<String, String> inputMap = (HashMap<String, String>) inputArray[1];
			String accountID = inputMap.get("accountID");
			String userId = inputMap.get("customerID");
			String customerType = inputMap.get("customerType");
			String companyId=inputMap.get("companyId");
			Map<String, String> inputparam = new HashMap<>();
			inputparam.put("customerId",userId);
			String authToken = TokenUtils.getAMSAuthToken(inputparam);
			CommonUtils.setCompanyIdToRequest(request);
			ArrangementsResource AccountsResource = DBPAPIAbstractFactoryImpl.getResource(ArrangementsResource.class);
			result = AccountsResource.getAccountDetailsForCombinedStatements(accountID, customerType, authToken,companyId,request);
		} catch (Exception e) {
			LOG.error("Error while fetching account Details");
		}
		return result;
	}

}
