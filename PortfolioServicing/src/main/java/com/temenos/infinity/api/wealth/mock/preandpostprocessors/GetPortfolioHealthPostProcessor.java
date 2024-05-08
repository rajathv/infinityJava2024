package com.temenos.infinity.api.wealth.mock.preandpostprocessors;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealth.config.PortfolioWealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

public class GetPortfolioHealthPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(GetMyStrategyPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		try {

			String portfolioId = request.getParameterValues(TemenosConstants.PORTFOLIOID)[0];
			String portfolioServiceType = request.getParameterValues(TemenosConstants.PORTFOLIOSERVICETYPE)[0];
		
			if (portfolioId.equalsIgnoreCase("100777-4")) {

				JSONObject returnObj = new JSONObject();
				returnObj.put(TemenosConstants.PORTFOLIOHEALTH, assets(portfolioId));
				returnObj.put(TemenosConstants.PORTFOLIOID, portfolioId);
				returnObj.put(TemenosConstants.PORTFOLIOSERVICETYPE, portfolioServiceType);
				Result final_result = Utilities.constructResultFromJSONObject(returnObj);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);

			} else if (portfolioId.equalsIgnoreCase("100777-5")) {
				JSONObject returnObj = new JSONObject();
				returnObj.put(TemenosConstants.PORTFOLIOHEALTH, assets(portfolioId));
				returnObj.put(TemenosConstants.PORTFOLIOID, portfolioId);
				returnObj.put(TemenosConstants.PORTFOLIOSERVICETYPE, portfolioServiceType);
				Result final_result = Utilities.constructResultFromJSONObject(returnObj);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);
			} else if (portfolioId.equalsIgnoreCase("100777-1") || portfolioId.equalsIgnoreCase("100777-2")
					|| portfolioId.equalsIgnoreCase("100777-3")) {
				String errorText = "This operation not supported for non advisiory portfolio";
				JSONObject returnObj = new JSONObject();
				returnObj.put(TemenosConstants.PORTFOLIOID, portfolioId);
				Result final_result = Utilities.constructResultFromJSONObject(returnObj);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				final_result.addParam("errorText", errorText);
				result.appendResult(final_result);
			}

		} catch (Exception e) {
			e.getMessage();
			LOG.error("Error while invoking GetMockMyStrategyPostProcessor - "
					+ PortfolioWealthAPIServices.WEALTHSERVICESORCHESTRATION.getOperationName() + "  : " + e);
		}
		return result;
	}

	private JSONArray assets(String portfolioId) {
		String healthParameter[] = new String[] { "Asset Allocation", "Risk Analysis", "Investment Constraints",
		"Recommended Instruments" };
		String healthStatus[] = new String[] { "1", "0", "1", "1" };
		
		if (portfolioId.equalsIgnoreCase("100777-4")) {
			healthParameter = new String[] { "Asset Allocation", "Risk Analysis", "Investment Constraints",
			"Recommended Instruments" };
			healthStatus = new String[] { "0", "0", "0", "0" };
	
		}
		else if (portfolioId.equalsIgnoreCase("100777-5")) {	
		}
		JSONArray al1 = new JSONArray();
		for (int i = 0; i < healthParameter.length; i++) {
			HashMap<String, String> assets_hm = new HashMap<String, String>();
			assets_hm.put(TemenosConstants.HEALTHPARAMETER, healthParameter[i]);
			assets_hm.put(TemenosConstants.HEALTHSTATUS, healthStatus[i]);
			al1.put(assets_hm);
		}
		return al1;
	}
}
