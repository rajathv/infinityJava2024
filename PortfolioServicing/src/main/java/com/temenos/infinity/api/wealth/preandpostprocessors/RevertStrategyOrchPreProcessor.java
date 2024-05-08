/**
 * 
 */
package com.temenos.infinity.api.wealth.preandpostprocessors;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

/**
 * @author himaja.sridhar
 *
 */
public class RevertStrategyOrchPreProcessor implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(RevertStrategyOrchPreProcessor.class);

	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			String portfolioId = null, portfolioservicetype = "";
			if (inputMap.get(TemenosConstants.PORTFOLIOID) != null
					&& inputMap.get(TemenosConstants.PORTFOLIOID).toString().trim().length() > 0) {
				portfolioId = inputMap.get(TemenosConstants.PORTFOLIOID).toString();
			} else {
				return unauthAccess(result, TemenosConstants.PORTFOLIOID);
			}
			if (inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE) != null 
					&& inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE).toString().trim().length() > 0) {
				portfolioservicetype = inputMap.get(TemenosConstants.PORTFOLIOSERVICETYPE).toString();
			} else {
				return unauthAccess(result,TemenosConstants.PORTFOLIOSERVICETYPE);
			}
			List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);
			String wealthCore = "";
			if (allportfoliosList.contains(portfolioId)  && portfolioservicetype.equalsIgnoreCase("Advisory")) {
				wealthCore = PortfolioWealthUtils.getWealthCoreFromCache(request);
				request.addRequestParam_(TemenosConstants.WEALTH_CORE, wealthCore);
				return true;
			} else {
				LOG.error("Invalid request");
				result.addParam("status", "Failure");
				result.addParam("error", "Unauthorized Access");
				return false;
			}

		} catch (Exception e) {
			LOG.error("Error in RevertStrategyOrchPreProcessor" + e);
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
