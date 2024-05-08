package com.temenos.infinity.api.wealthOrder.mockdata;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthservices.constants.ErrorCodeEnum;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthOrder.util.WealthMockUtil;

/**
 * (INFO) Prepares Result object from the mock data returned by the Util.
 *
 */
public class GetPortfolioDetailsMock implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetPortfolioDetailsMock.class);

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		WealthMockUtil mockUtil = new WealthMockUtil();
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Object portfolioIdobj = inputParams.get(TemenosConstants.PORTFOLIOID);
		Object graphDurationObj = inputParams.get(TemenosConstants.GRAPHDURATION);
		Object navPageObj = inputParams.get("navPage");
		String portfolioId = null;
		String navPage = null;
		String graphDuration = null;
		if (portfolioIdobj != null) {
			portfolioId = inputParams.get(TemenosConstants.PORTFOLIOID).toString();
			inputMap.put(TemenosConstants.PORTFOLIOID, portfolioId);
		}
		if (graphDurationObj != null) {
			graphDuration = inputParams.get(TemenosConstants.GRAPHDURATION).toString();
			inputMap.put(TemenosConstants.GRAPHDURATION, graphDuration);
		}
		if (navPageObj != null) {
			navPage = inputParams.get("navPage").toString();
			inputMap.put("navPage", navPage);
		}

		try {
			JSONObject portfolioDetails = mockUtil.mockportfolioDetails(inputMap);

			return Utilities.constructResultFromJSONObject(portfolioDetails);

		} catch (Exception e) {
			LOG.error("Caught exception at invoke of GetPortfolioDetailsMock: ", e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}

	}

}
