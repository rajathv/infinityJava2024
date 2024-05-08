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

public class GeneratePastProposalPreProcessor implements DataPreProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(GeneratePastProposalPreProcessor.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			String wealthCore = EnvironmentConfigurationsHandler.getValue(TemenosConstants.WEALTH_CORE, request);
			result.addParam("wealthCore", wealthCore);
			
			String portfolioId = null, portfolioservicetype = "", navPage = "";
			if (inputMap.get(TemenosConstants.PORTFOLIOID) != null
					&& inputMap.get(TemenosConstants.PORTFOLIOID).toString().trim().length() > 0) {
				portfolioId = inputMap.get(TemenosConstants.PORTFOLIOID).toString();
			} else {
				unauthAccess(result,TemenosConstants.PORTFOLIOID);
			}

			if (inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE) != null) {
				portfolioservicetype = inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE).toString();
			} else {
				unauthAccess(result,TemenosConstants.PORTFOLIOSERVICETYPE);
			}
			
			if (inputMap.get(TemenosConstants.NAVPAGE) != null) {
				navPage = inputMap.get(TemenosConstants.NAVPAGE).toString();
			} else {
				unauthAccess(result,TemenosConstants.NAVPAGE);
			}

			List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);

			if (allportfoliosList.contains(portfolioId) && portfolioservicetype.equalsIgnoreCase("Advisory")) {
				
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
		catch (Exception e) {
			LOG.error("Error in ConfirmRecomStratPreProcessor" + e);
			e.getMessage();
			return false;
		}
	}
	private boolean unauthAccess(Result result, String param) {
		LOG.error("Error:Invalid input. Mandatory fields not given");
		result.addParam("status", "Failure");
		result.addParam("error", "Invalid Input! " + param + " is mandatory.");
		result.addHttpStatusCodeParam("0");
		return false;
		
	}
}
