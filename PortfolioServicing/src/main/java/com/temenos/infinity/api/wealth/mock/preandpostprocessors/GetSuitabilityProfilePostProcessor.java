package com.temenos.infinity.api.wealth.mock.preandpostprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealth.config.PortfolioWealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

public class GetSuitabilityProfilePostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(GetSuitabilityProfilePostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		try {

			String portfolioId = request.getParameterValues(TemenosConstants.PORTFOLIOID)[0];
			
			if (portfolioId.equalsIgnoreCase("100777-4")) {

				JSONObject returnObj = new JSONObject();

				returnObj.put("expiryDate", "31/03/2023");
				returnObj.put("isValid", "true");
				returnObj.put("message", "Valid");
				returnObj.put(TemenosConstants.PORTFOLIOID, portfolioId);

				Result final_result = Utilities.constructResultFromJSONObject(returnObj);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);

			} else if (portfolioId.equalsIgnoreCase("100777-5")) {
				JSONObject returnObj = new JSONObject();

				returnObj.put("expiryDate", "10/09/2022");
				returnObj.put("isValid", "false");
				returnObj.put("message", "Current profile expired");
				returnObj.put(TemenosConstants.PORTFOLIOID, portfolioId);

				Result final_result = Utilities.constructResultFromJSONObject(returnObj);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);
			}

		} catch (Exception e) {
			e.getMessage();
			LOG.error("Error while invoking GetMockMyStrategyPostProcessor - "
					+ PortfolioWealthAPIServices.WEALTHSERVICESORCHESTRATION.getOperationName() + "  : " + e);
		}
		return result;
	}

}
