package com.temenos.infinity.api.wealth.preandpostprocessors;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.TokenUtils;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

/**
 * (INFO) If status is set as a part of the request , the operation is exited 
 * 		  else operation is executed.
 *
 */

public class GetHoldingsListPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(GetHoldingsListPreProcessor.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			Map<String, String> inputparamMap = new HashMap<>();
			inputparamMap.put("userName",PortfolioWealthUtils.getUserAttributeFromIdentity(request, "UserName"));
			inputparamMap.put("customerId",PortfolioWealthUtils.getUserAttributeFromIdentity(request, "customer_id"));
			String backendToken = TokenUtils.getPortfolioWealthMSAuthToken(inputparamMap);
			 
			request.addRequestParam_(TemenosConstants.AUTHORIZATION, backendToken);
			request.addRequestParam_("onlineUpdate", "Y");
			return true;
		}
		 catch (Exception e) {
				LOG.error("Error in GetHoldingsListPreProcessor" + e);
				return false;
			}
	}

}
