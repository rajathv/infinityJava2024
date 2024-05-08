package com.temenos.infinity.api.wealth.preandpostprocessors;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.sessionmanager.SessionScope;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

/**
 * Validate the permission & payload then allow the operation
 * 
 * @author Lakshminarayanan
 *
 */

public class ConfirmChangeStratFromQuestionOrchPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(ConfirmChangeStratFromQuestionOrchPreProcessor.class);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			Set<String> userPermissions = SessionScope.getAllPermissionsFromIdentityScope(request);
			userPermissions.add("CHOOSE_STRATEGY_ACKNOWLEDGEMENT");
			if (!userPermissions.contains(TemenosConstants.CHANGE_STRATEGY_CONFIRMATION)
					&& !userPermissions.contains(TemenosConstants.CHOOSE_STRATEGY_ACKNOWLEDGEMENT)
					&& !userPermissions.contains(TemenosConstants.CHOOSE_STRATEGY_ACKNOWLEDGEMENT_CHART)) {
				result.addParam("opstatus", "1582");
				result.addParam("status", TemenosConstants.FAILURE);
				result.addParam("error", "Logged in user not authorized to perform this action");
				return false;
			}

			String portfolioId = null;
			String portfolioservicetype = "";

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
				return unauthAccess(result, TemenosConstants.PORTFOLIOSERVICETYPE);
			}

			if (!(inputMap.get(TemenosConstants.STRATEGYID) != null
					&& inputMap.get(TemenosConstants.STRATEGYID).toString().trim().length() > 0)) {
				return unauthAccess(result, TemenosConstants.STRATEGYID);
			}

			if (!(inputMap.get(TemenosConstants.STRATEGYNAME) != null
					&& inputMap.get(TemenosConstants.STRATEGYNAME).toString().trim().length() > 0)) {
				return unauthAccess(result, TemenosConstants.STRATEGYNAME);
			}

			if (!(inputMap.get(TemenosConstants.QUESTIONNAIREHISTOCODE) != null
					&& inputMap.get(TemenosConstants.QUESTIONNAIREHISTOCODE).toString().trim().length() > 0)) {
				return unauthAccess(result, TemenosConstants.QUESTIONNAIREHISTOCODE);
			}

			List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);
			String wealthCore = "";
			if (allportfoliosList.contains(portfolioId) && portfolioservicetype.equalsIgnoreCase("Advisory")) {
				wealthCore = PortfolioWealthUtils.getWealthCoreFromCache(request);
				request.addRequestParam_(TemenosConstants.WEALTH_CORE, wealthCore);
				
				inputMap.put(TemenosConstants.ISQUESTIONFLOW, TemenosConstants.FALSE);
				
				return true;
			} else {
				LOG.error("Invalid request");
				result.addParam("status", "Failure");
				result.addParam("error", "Unauthorized Access");
				return false;
			}

		} catch (Exception e) {
			LOG.error("Error in ConfirmChangeStratOrchPreProcessor" + e);
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
