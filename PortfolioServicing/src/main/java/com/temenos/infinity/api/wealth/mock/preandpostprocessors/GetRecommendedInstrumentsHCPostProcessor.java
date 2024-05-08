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

public class GetRecommendedInstrumentsHCPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(GetAllStrategiesPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		
		JSONObject responseN = new JSONObject();
		String portfolioId = request.getParameterValues(TemenosConstants.PORTFOLIOID)[0];
		
		try {
			if (portfolioId.equalsIgnoreCase("100777-5")) {
				String instrumentName[] = new String[] { "Alphabet", "Pfizer Inc" };
				String instrumentDetails[] = { "Alphabet position held; has Sell recommendation",
						"Pfizer Inc position held; not included within Bank recommendation" };
				
				String recommendedInstrumentComment = "Some issues with your portfolio health";
				String recomendedInstrumentStatus = "1";
				ArrayList<HashMap<String, String>> alList = new ArrayList<HashMap<String, String>>();
				IntStream.range(0, instrumentName.length).forEach(index -> {
					HashMap<String, String> hMap = new HashMap<String, String>();
					//String instrumentNew[] = { instrumentName[index] + " " + instrumentDetails[index] };
					hMap.put("instrumentName", instrumentName[index]);
					hMap.put("instrumentDetails", instrumentDetails[index]);
					alList.add(hMap);
				});
				responseN.put("portfolioID", portfolioId);
				responseN.put("recommendedInstrumentStatus", recomendedInstrumentStatus);
				responseN.put("recommendedInstrumentComment", recommendedInstrumentComment);
				responseN.put("recommendedInstrumentDetails", alList);
				Result final_result = Utilities.constructResultFromJSONObject(responseN);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);
				

			} else if (portfolioId.equalsIgnoreCase("100777-4")) {

				String instrumentName[] = new String[] {};
				String instrumentDetails[] = new String[] {};

				String recommendedInstrumentComment = "No issues";
				String recomendedInstrumentStatus = "0";
				ArrayList<HashMap<String, String>> alList = new ArrayList<HashMap<String, String>>();
				IntStream.range(0, instrumentName.length).forEach(index -> {
					HashMap<String, String> hMap = new HashMap<String, String>();
					hMap.put("instrumentName", instrumentName[index]);
					hMap.put("instrumentDetails", instrumentDetails[index]);
					alList.add(hMap);
				});

				responseN.put("portfolioID", portfolioId);
				responseN.put("recommendedInstrumentStatus", recomendedInstrumentStatus);
				responseN.put("recommendedInstrumentComment", recommendedInstrumentComment);
				responseN.put("recommendedInstrumentDetails", alList);
				Result final_result = Utilities.constructResultFromJSONObject(responseN);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);
				
				
			} else if (portfolioId.equalsIgnoreCase("100777-1") || portfolioId.equalsIgnoreCase("100777-2")
					|| portfolioId.equalsIgnoreCase("100777-3")) {
				String instrumentName[] = new String[] {};
				String errorText = "This operation not supported for non advisiory portfolio";

				ArrayList<HashMap<String, String>> alList = new ArrayList<HashMap<String, String>>();
				IntStream.range(0, instrumentName.length).forEach(index -> {
					HashMap<String, String> hMap = new HashMap<String, String>();
					hMap.put("instrumentName", instrumentName[index]);
					alList.add(hMap);
				});

				responseN.put("portfolioID", portfolioId);
				responseN.put("errorText", errorText);
				Result final_result = Utilities.constructResultFromJSONObject(responseN);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				result.appendResult(final_result);
				
			} else {

				String instrumentName[] = new String[] {};
				String instrumentDetails[] = new String[] {};

				String recommendedInstrumentComment = "Not a Valid Portfolio";

				ArrayList<HashMap<String, String>> alList = new ArrayList<HashMap<String, String>>();
				IntStream.range(0, instrumentName.length).forEach(index -> {
					HashMap<String, String> hMap = new HashMap<String, String>();
					hMap.put("instrumentName", instrumentName[index]);
					hMap.put("instrumentDetails", instrumentDetails[index]);
					alList.add(hMap);
				});

				responseN.put("portfolioID", portfolioId);
				responseN.put("recommendedInstrumentComment", recommendedInstrumentComment);
				Result final_result = Utilities.constructResultFromJSONObject(responseN);
				final_result.addOpstatusParam("0");
				final_result.addHttpStatusCodeParam("200");
				final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				result.appendResult(final_result);

			}
		} catch (Exception e) {
			e.getMessage();
			LOG.error("Error while invoking GetRecommendedInstrumentsHCPostProcessor MOCK - "
					+ PortfolioWealthAPIServices.WEALTH_GETPORTFOLIOHEALTHRECOMMENDEDINSTRUMENTS.getOperationName() + "  : " + e);
		}
		return result;
	}


}
