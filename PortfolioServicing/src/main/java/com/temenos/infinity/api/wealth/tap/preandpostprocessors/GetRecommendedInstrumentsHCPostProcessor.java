package com.temenos.infinity.api.wealth.tap.preandpostprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealth.config.PortfolioWealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

public class GetRecommendedInstrumentsHCPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(GetRecommendedInstrumentsHCPostProcessor.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		
		try {		
			JSONObject recommedJSON = new JSONObject();
			Dataset bodyData = result.getDatasetById("body");
			JSONArray recommedArr = new JSONArray();
			String healthStatus = "", healthMessage = "";
			
			if(bodyData != null) {
				JSONArray recommeded = ResultToJSON.convertDataset(bodyData);
				for (int i = 0; i < recommeded.length(); i++) {
					JSONObject recomJSON = new JSONObject();
					JSONObject recommededJSON = recommeded.getJSONObject(i);
					if(recommededJSON.has("recomNatE")) {
						healthStatus = "1" ;
						healthMessage = "Some issues with your portfolio health";
						if(recommededJSON.has("valueN")) {
							String recommendation =  recommededJSON.getString("recomNatL");
							String instrumentName =  recommededJSON.getString("instrumentName");
							String value = recommededJSON.getString("valueN").concat("%");
							recomJSON.put(TemenosConstants.INSTRUMENTNAME,instrumentName);
							String instDet = recommendation.concat(" recommendation for ").concat(instrumentName).concat(" for a max weight of ").concat(value);
							recomJSON.put(TemenosConstants.INSTRUMENTDETAILS,instDet);
						}
						else {
							String recommendation =  recommededJSON.getString("recomNatL");
							String instrumentName =  recommededJSON.getString("instrumentName");
							recomJSON.put(TemenosConstants.INSTRUMENTNAME,instrumentName);
							String instDet = recommendation.concat(" recommendation for ").concat(instrumentName);
							recomJSON.put(TemenosConstants.INSTRUMENTDETAILS,instDet);
						}
					} 
					else {
						if(healthStatus.equals("1")) {
						}
						else {
							healthStatus = "0";	
							healthMessage = "No issues";
						}
						continue;
					}
					recommedArr.put(recomJSON);
					
				}
				recommedJSON.put("recommendedInstrumentStatus", healthStatus);
				recommedJSON.put("recommendedInstrumentComment", healthMessage);
				recommedJSON.put("recommendedInstrumentDetails", recommedArr);
				
			} 
			else {
				recommedJSON.put("recommedId", "");
			}
			Result recommedRes = Utilities.constructResultFromJSONObject(recommedJSON);
			recommedRes.addOpstatusParam("0");
			recommedRes.addHttpStatusCodeParam("200");
			recommedRes.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
			recommedRes.addParam("portfolioID",request.getParameter("portfolioId"));
			return recommedRes;
			
		} catch (Exception e) {
			LOG.error("Error while invoking GetRecommededInstrumentsHCPostProcessor - "
					+ PortfolioWealthAPIServices.WEALTHSERVICESORCHESTRATION.getOperationName() + "  : " + e);
		}
		return response;
	}


}
