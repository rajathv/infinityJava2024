package com.temenos.infinity.api.wealth.preandpostprocessors;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.sessionmanager.SessionScope;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

/**
 * (INFO) If status is set as a part of the request , the operation is exited
 * else operation is executed.
 * 
 * @author balaji.krishnan
 *
 */

public class GetAllStrategiesOrchPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(GetAllStrategiesOrchPreProcessor.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			Set<String> userPermissions = SessionScope.getAllPermissionsFromIdentityScope(request);
            if (!userPermissions.contains(TemenosConstants.MY_STRATEGY_CHANGE_STRATEGY_VIEW)) {
            	result.addParam("opstatus", "1582");
            	result.addParam("status", TemenosConstants.FAILURE);
				result.addParam("error", "Logged in user not authorized to perform this action");
                return false;
            }
            
            String portfolioId = null, portfolioservicetype = "";
			if (inputMap.get(TemenosConstants.PORTFOLIOID) != null
					&& inputMap.get(TemenosConstants.PORTFOLIOID).toString().trim().length() > 0) {
				portfolioId = inputMap.get(TemenosConstants.PORTFOLIOID).toString();
			} else {
				return unauthAccess(result,TemenosConstants.PORTFOLIOID);
			}

			if (inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE) != null 
					&& inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE).toString().trim().length() > 0) {
				portfolioservicetype = inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE).toString();
			} else {
				return unauthAccess(result,TemenosConstants.PORTFOLIOSERVICETYPE);
			}

			List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);
			String wealthCore = "";
			if (allportfoliosList.contains(portfolioId) && portfolioservicetype.equalsIgnoreCase("Advisory")) {
				wealthCore = PortfolioWealthUtils.getWealthCoreFromCache(request);
				if(wealthCore.contains("TAP") && (!request.containsKeyInRequest("score") || inputMap.get("score").toString().trim().length() == 0)&& (!request.containsKeyInRequest("strategyName")  || inputMap.get("strategyName").toString().trim().length() == 0)) {
					return unauthStratAccess(result);
				}
				request.addRequestParam_(TemenosConstants.WEALTH_CORE, wealthCore);
				return true;
				
			} else {
				LOG.error("Invalid request");
				result.addParam("status", "Failure");
				result.addParam("error", "Unauthorized Access");
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error in GetAllStrategiesOrchPreProcessor" + e);
			e.getMessage();
			return false;
		}
	}

	private boolean unauthAccess(Result result, String param) {
		LOG.error("Error:Invalid input. Mandatory fields not given");
		result.addParam("status", "Failure");
		result.addParam("error", "Invalid Input! " + param + " is mandatory.");
		return false;
		
	}
	private boolean unauthStratAccess(Result result) {
		LOG.error("Error:Invalid input. Mandatory fields not given");
		result.addParam("status", "Failure");
		result.addParam("error", "Error:Invalid input. Mandatory fields(score/strategyName) not given");
		return false;
		
	}

}
