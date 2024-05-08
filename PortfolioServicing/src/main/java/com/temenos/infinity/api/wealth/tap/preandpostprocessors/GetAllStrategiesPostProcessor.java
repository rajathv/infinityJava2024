package com.temenos.infinity.api.wealth.tap.preandpostprocessors;

import java.security.SecureRandom;

import org.json.JSONArray;
import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * (INFO) Logic for building the response.
 * 
 * @author himaja.sridhar
 *
 */


public class GetAllStrategiesPostProcessor implements DataPostProcessor2 {

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		Dataset bodySet = result.getDatasetById("body");
		JSONArray resultArr = ResultToJSON.convertDataset(bodySet);
		JSONArray recStrategy = new JSONArray();
		JSONObject recObject = new JSONObject();
		JSONArray assetArr = new JSONArray();
		for(int i=0;i<resultArr.length();i++) {
			JSONObject resultObj = resultArr.getJSONObject(i);
			if(!resultObj.getString("LEVEL_N").equals("1")) {
			}
			else {
				if(resultObj.getString("MAIN_STRATEGY").equalsIgnoreCase("false")) {	
				}
				else {
				JSONObject assetCompo = new JSONObject();
				assetCompo.put("assetName",resultObj.getString("structureDenom"));
				String value = resultObj.has("valueN") ? resultObj.getString("valueN"):"0";
				assetCompo.put("weight",value);
				assetArr.put(assetCompo);
				}
			}
		}
		recObject.put("assetsCompo", assetArr);
		recObject.put("strategyName", request.getAttribute("name").toString());
		SecureRandom sr = new SecureRandom();
		double id = sr.nextInt(1000);
		recObject.put("strategyId",request.getParameter("strategyCode").toString());
		recStrategy.put(recObject);
		recObject = new JSONObject();
		recObject.put("recStrategy", recStrategy);
		Result final_result = Utilities.constructResultFromJSONObject(recObject);
		final_result.addOpstatusParam("0");
		final_result.addHttpStatusCodeParam("200");
		final_result.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
		return final_result;

	}

}
