/**
 * 
 */
package com.temenos.infinity.api.wealth.tap.preandpostprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * @author himaja.sridhar
 *
 */
public class GetActivePortfolioStrategyPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager.getLogger(GetActivePortfolioStrategyPostProcessor.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		try {
		Dataset bodySet = result.getDatasetById("body");
		JSONObject linkJSON = new JSONObject();
		int idCnt=0;
		if(bodySet != null) {
		JSONArray bodyArr = ResultToJSON.convertDataset(bodySet);		
		String idVal = "";
		for(int i =0 ;i<bodyArr.length();i++) {
			JSONObject bodyObj = bodyArr.getJSONObject(i);
			if(!bodyObj.get("strategyNatureE").toString().equalsIgnoreCase("Investment profile"))
			{
				bodyArr.remove(i);
			}
			else {
				idCnt++;
				String id = bodyObj.getString("id");
				idVal = idVal.equals("") ? id : idVal.concat(",").concat(id);
			}
		}
		linkJSON.put("idCnt", idCnt);
		linkJSON.put("idVal", idVal);
		Result linkRes = Utilities.constructResultFromJSONObject(linkJSON);
		linkRes.addOpstatusParam("0");
		linkRes.addHttpStatusCodeParam("200");
		linkRes.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
		return linkRes;
		}
		else {
			linkJSON.put("idCnt", idCnt);
			Result linkRes = Utilities.constructResultFromJSONObject(linkJSON);
			linkRes.addOpstatusParam("0");
			linkRes.addHttpStatusCodeParam("200");
			linkRes.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
			return linkRes;
		}
		}
		catch (Exception e) {
			LOG.error("Error while invoking GetActivePortfolioStrategyLinksPostProcessor - " + e);
		}
		return null;
	}

}
