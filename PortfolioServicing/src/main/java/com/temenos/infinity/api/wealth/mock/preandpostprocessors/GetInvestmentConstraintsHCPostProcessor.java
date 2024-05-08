package com.temenos.infinity.api.wealth.mock.preandpostprocessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.IntStream;

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

public class GetInvestmentConstraintsHCPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(GetInvestmentConstraintsHCPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		
		JSONObject response2 = new JSONObject();
		String portfolioId = request.getParameterValues(TemenosConstants.PORTFOLIOID)[0];
		
		try {
			if (portfolioId.equalsIgnoreCase("100777-5")) {

				String investmentConstraintDetails[] = new String[] {
						"Sector concentration of 26.98% for Retail exceeds maximum recommended of 20% of the portfolio",
						"Sector concentration of Recommended Instruments 20.06% for Computer Services exceeds maximum recommended of 15% of the portfolio" };
				String investmentConstraintComment = "Some issues with your portfolio health";
				String investmentConstraintStatus = "1";
				ArrayList<HashMap<String, String>> alList = new ArrayList<HashMap<String, String>>();
				IntStream.range(0, investmentConstraintDetails.length).forEach(index -> {
					HashMap<String, String> hMap = new HashMap<String, String>();
					hMap.put("investmentConstraintDetails", investmentConstraintDetails[index]);
					alList.add(hMap);

				});
				response2.put("portfolioID", portfolioId);
				response2.put("investmentConstraintComment", investmentConstraintComment);
				response2.put("investmentConstraintStatus", investmentConstraintStatus);
				response2.put("investmentConstraintDetails", alList);
				Result final_result = Utilities.constructResultFromJSONObject(response2);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);

			} else if (portfolioId.equalsIgnoreCase("100777-4")) {

				String investmentConstraintDetails[] = new String[] {};
				String investmentConstraintComment = "No issues";
				String investmentConstraintStatus = "0";
				ArrayList<HashMap<String, String>> alList = new ArrayList<HashMap<String, String>>();
				IntStream.range(0, investmentConstraintDetails.length).forEach(index -> {
					HashMap<String, String> hMap = new HashMap<String, String>();
					hMap.put("investmentConstraintDetails", investmentConstraintDetails[index]);
					alList.add(hMap);
				});

				response2.put("portfolioID", portfolioId);
				response2.put("investmentConstraintComment", investmentConstraintComment);
				response2.put("investmentConstraintStatus", investmentConstraintStatus);
				response2.put("investmentConstraintDetails", alList);
				Result final_result = Utilities.constructResultFromJSONObject(response2);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);
				
			} else if (portfolioId.equalsIgnoreCase("100777-1") || portfolioId.equalsIgnoreCase("100777-2")
					|| portfolioId.equalsIgnoreCase("100777-3")) {
				String investmentConstraintDetails[] = new String[] {};
				String errorText = "This operation not supported for non advisiory portfolio";

				ArrayList<HashMap<String, String>> alList = new ArrayList<HashMap<String, String>>();
				IntStream.range(0, investmentConstraintDetails.length).forEach(index -> {
					HashMap<String, String> hMap = new HashMap<String, String>();
					hMap.put("investmentConstraintDetails", investmentConstraintDetails[index]);
					alList.add(hMap);
				});

				response2.put("portfolioID", portfolioId);
				response2.put("errorText", errorText);
				Result final_result = Utilities.constructResultFromJSONObject(response2);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);

			} else {

				String investmentConstraintDetails[] = new String[] {};
				String investmentConstraintComment = "Not a Valid Portfolio";

				ArrayList<HashMap<String, String>> alList = new ArrayList<HashMap<String, String>>();
				IntStream.range(0, investmentConstraintDetails.length).forEach(index -> {
					HashMap<String, String> hMap = new HashMap<String, String>();
					hMap.put("investmentConstraintDetails", investmentConstraintDetails[index]);
					alList.add(hMap);
				});

				response2.put("portfolioID", portfolioId);
				response2.put("investmentConstraintComment", investmentConstraintComment);
				response2.put("investmentConstraintDetails", alList);
				Result final_result = Utilities.constructResultFromJSONObject(response2);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);
			}
		} catch (Exception e) {
			e.getMessage();
			LOG.error("Error while invoking GetInvestmentConstraintsHCPostProcessor MOCK - "
					+ PortfolioWealthAPIServices.WEALTH_GETPORTFOLIOHEALTHALLOCATION.getOperationName() + "  : " + e);
			
		}
		return result;
	}

}
