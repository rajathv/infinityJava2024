/**
 * 
 */
package com.temenos.infinity.api.wealth.tap.preandpostprocessors;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

public class GetAllocationHCPostProcessor implements DataPostProcessor2 {

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		Dataset assetSet = result.getDatasetById("body");
		JSONArray assetArr = ResultToJSON.convertDataset(assetSet);
		for(int i=0;i<assetArr.length();i++) {
			if(!assetArr.getJSONObject(i).has("parentId")) {
				assetArr.remove(i);
			}
			if(assetArr.getJSONObject(i).has("strategyWeight")) {
				JSONObject assetObj = assetArr.getJSONObject(i);
		       	 String valType = assetObj.getString("strategyWeight");
		       	 Double wght = Double.parseDouble(valType);
		       	 Double wght1 = (double) Math.round(wght * 100) / 100;
					assetArr.getJSONObject(i).put("strategyWeight",wght1.toString());
			}
			if(assetArr.getJSONObject(i).has("healthStatus")) {
				if(Integer.parseInt(assetArr.getJSONObject(i).get("healthStatus").toString()) < 3) {
					assetArr.getJSONObject(i).put("healthStatus", "0");
				} else {
					assetArr.getJSONObject(i).put("healthStatus", "1");
				}
			}
		}
		
		
		JSONObject assetObj = new JSONObject();
		assetObj.put("Assets", assetArr);
		result.removeDatasetById("Assets");
		Result allocationHC = Utilities.constructResultFromJSONObject(assetObj);
		allocationHC.addOpstatusParam("0");
		allocationHC.addHttpStatusCodeParam("200");
		allocationHC.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
	    return allocationHC;
}
}
