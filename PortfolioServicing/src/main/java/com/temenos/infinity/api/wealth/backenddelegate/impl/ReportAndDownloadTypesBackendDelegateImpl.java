package com.temenos.infinity.api.wealth.backenddelegate.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealth.backenddelegate.api.ReportAndDownloadTypesBackendDelegate;
import com.temenos.infinity.api.wealth.config.PortfolioWealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealth.util.WealthMockUtil;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

public class ReportAndDownloadTypesBackendDelegateImpl implements ReportAndDownloadTypesBackendDelegate {
	private static final Logger LOG = LogManager.getLogger(WealthDashboardBackendDelegateImpl.class);
	WealthMockUtil wealthMockUtil = new WealthMockUtil();

	@SuppressWarnings("unchecked")
	public Result getReportAndDownloadTypes(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response, Map<String, Object> headersMap) {

		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Object portfolioIdObject = inputParams.get(TemenosConstants.PORTFOLIOID);
		Object navigationPageObject = inputParams.get(TemenosConstants.NAVPAGE);
		String portfolioId = "";
		String navPage = "";

		if (portfolioIdObject == null || portfolioIdObject.equals("")) {
			LOG.error("Invalid request");
			JSONObject result = new JSONObject();
			result.put("status", "Failure");
			result.put("error", "Invalid Input! Portfolio ID cannot be null or empty");
			return Utilities.constructResultFromJSONObject(result);
		} else {
			portfolioId = inputParams.get(TemenosConstants.PORTFOLIOID).toString();
			inputMap.put(TemenosConstants.PORTFOLIOID, portfolioId);
		}

		if (navigationPageObject == null || navigationPageObject.equals("")) {
			LOG.error("Invalid request");
			JSONObject result = new JSONObject();
			result.put("status", "Failure");
			result.put("error", "Invalid Input! Nav Page cannot be null or empty");
			return Utilities.constructResultFromJSONObject(result);
		} else {
			navPage = inputParams.get(TemenosConstants.NAVPAGE).toString();
			inputMap.put(TemenosConstants.NAVPAGE, navPage);
		}

		List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);

		if (allportfoliosList.contains(portfolioId)) {
			try {

				JSONObject jsonResult = wealthMockUtil.getReportAndDownloadTypes(inputMap);
				return Utilities.constructResultFromJSONObject(jsonResult);

			} catch (Exception e) {
				LOG.error("Error while invoking OrdersList - "
						+ PortfolioWealthAPIServices.WEALTH_GETREPORTDOWNLOADTYPES.getOperationName() + "  : " + e);
				return null;

			}
		} else {
			LOG.error("Portfolio ID " + portfolioId + " does not exist for the Customer");
			return PortfolioWealthUtils.UnauthorizedAccess();
		}

	}
}
