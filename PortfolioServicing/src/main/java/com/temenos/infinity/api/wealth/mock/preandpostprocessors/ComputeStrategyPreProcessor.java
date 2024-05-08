package com.temenos.infinity.api.wealth.mock.preandpostprocessors;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

public class ComputeStrategyPreProcessor implements DataPreProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(ComputeStrategyPreProcessor.class);

/**
 * @author vinayranjan.sharma
 *
 */
 
	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			String wealthCore = EnvironmentConfigurationsHandler.getValue(TemenosConstants.WEALTH_CORE, request);
			result.addParam(TemenosConstants.WEALTH_CORE, wealthCore);
			if (request.getParameter(TemenosConstants.WEALTH_CORE) != null
					&& (request.getParameter(TemenosConstants.WEALTH_CORE).equalsIgnoreCase("Mock"))) {
			String portfolioId = null, ID = null, Name = "",targetWeight=null, recommendedWeight= null,
					UpdateCount = null;
			if (inputMap.get(TemenosConstants.PORTFOLIOID) != null
					&& inputMap.get(TemenosConstants.PORTFOLIOID).toString().trim().length() > 0) {
				portfolioId = inputMap.get(TemenosConstants.PORTFOLIOID).toString();
			} else {
				return unauthAccess(result,TemenosConstants.PORTFOLIOID);
			}

			if (inputMap.get(TemenosConstants.ID) != null && inputMap.get(TemenosConstants.ID).toString().trim().length() > 0) {
				ID = inputMap.get(TemenosConstants.ID).toString();
			} else {
				return unauthAccess(result,TemenosConstants.ID);
			}
			
			if (inputMap.get(TemenosConstants.NAME) != null && inputMap.get(TemenosConstants.NAME).toString().trim().length() > 0){
				Name = inputMap.get(TemenosConstants.NAME).toString();
			} else {
				return unauthAccess(result,TemenosConstants.NAME);
			}
			 if (inputMap.get("targetWeight") != null && inputMap.get("targetWeight").toString().trim().length() > 0) {
				targetWeight = inputMap.get("targetWeight").toString();
			} else {
				return unauthAccess(result,"targetWeight");
			}
			if (inputMap.get("recommendedWeight") != null && inputMap.get("recommendedWeight").toString().trim().length() > 0) {
				recommendedWeight = inputMap.get("recommendedWeight").toString();
			} else {
				return unauthAccess(result,"recommendedWeight");
			}
			if (inputMap.get("UpdateCount") != null && inputMap.get("UpdateCount").toString().trim().length() > 0  ) {
				UpdateCount = inputMap.get("UpdateCount").toString();
			} else {
				return unauthAccess(result,"UpdateCount");
			}

			List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);

			if (allportfoliosList.contains(portfolioId) ) {
				if (wealthCore.equals("Mock")) {
					return true;
				} else {
					result.addOpstatusParam("0");
					result.addHttpStatusCodeParam("200");
					result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
					return false;
				}
			} else {
				LOG.error("Invalid request");
				result.addParam("status", "Failure");
				result.addParam("error", "Unauthorized Access");
				return false;
			}
			}
			else {
				result.addOpstatusParam("0");
				result.addHttpStatusCodeParam("200");
				result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error in ComputeStrategyPreProcessor" + e);
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

}
