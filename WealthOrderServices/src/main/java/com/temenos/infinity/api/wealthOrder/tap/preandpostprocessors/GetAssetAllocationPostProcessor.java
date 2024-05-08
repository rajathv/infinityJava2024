/**
 * 
 */
package com.temenos.infinity.api.wealthOrder.tap.preandpostprocessors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthOrder.config.WealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * (INFO) Builds the result in desired format for the TAP service
 * 
 * @author himaja.sridhar
 *
 */
public class GetAssetAllocationPostProcessor implements DataPostProcessor2 {
	private static final Logger LOG = LogManager.getLogger(GetAssetAllocationPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		try {
			JSONObject AssetAllJSON = new JSONObject();
			Dataset bodyData = result.getDatasetById("body");

			JSONArray assetAllArr = new JSONArray();
			JSONObject portJSON = new JSONObject();
			String referenceCurr = null;
			Double marketVal = 0.0, totalMarketVal = 0.0;

			if (bodyData != null) {
				JSONArray assetAll = ResultToJSON.convertDataset(bodyData);
				String unrealPl= String.format("%.2f",Double.parseDouble(assetAll.getJSONObject(0).getString("unrealPL").toString()));
				String portVal= String.format("%.2f",Double.parseDouble(assetAll.getJSONObject(0).getString("portfolioVal").toString()));
				portJSON.put("unrealPl", unrealPl);
				portJSON.put("portVal", portVal);
				
				for (int i = 0; i < assetAll.length(); i++) {
					JSONObject assetJSON = assetAll.getJSONObject(i);
					assetJSON.remove("unrealPL");
					 assetJSON.remove("portfolioVal");	 
					if (!assetJSON.getString("assetDesc").equalsIgnoreCase("True")) {
					} else {
						assetJSON.remove("assetDesc");
						referenceCurr = assetJSON.getString("referenceCurrency");
						if(assetJSON.get("marketValue").toString().equals("0") || assetJSON.get("marketValue")== null)
						{
							
						}
						else {
						marketVal = marketVal
								+ Double.parseDouble(assetJSON.get("marketValue").toString().trim().length() > 0
										? assetJSON.get("marketValue").toString()
										: "0");
						assetJSON.remove("referenceCurrency");
						assetAllArr.put(assetJSON);

					}
					}
				}
				totalMarketVal = totalMarketVal + marketVal;
			} else {
				assetAllArr = new JSONArray();
			}
			assetAllArr = sortArray(assetAllArr);

			AssetAllJSON.put("assets", assetAllArr);
			AssetAllJSON.put("referenceCurrency", referenceCurr);
			AssetAllJSON.put("totalMarketValue", String.format("%.2f", totalMarketVal));
			AssetAllJSON.put("portfolioValues", portJSON);
			Result AssetRes = Utilities.constructResultFromJSONObject(AssetAllJSON);
			AssetRes.addOpstatusParam("0");
			AssetRes.addHttpStatusCodeParam("200");
			AssetRes.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
			return AssetRes;

		} catch (Exception e) {
			LOG.error("Error while invoking GetAssetAllocationPostProcessor - "
					+ WealthAPIServices.WEALTHSERVICESORCHESTRATION.getOperationName() + "  : " + e);
		}
		return null;
	}

	public JSONArray sortArray(JSONArray array) {
		JSONArray sortedJSONArray = new JSONArray();
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < array.length(); i++) {
			jsonValues.add(array.getJSONObject(i));
		}

		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			private final String KEY_NAME = "marketValue";

			@Override
			public int compare(JSONObject a, JSONObject b) {
				if (a.has(KEY_NAME) && b.has(KEY_NAME) && a.get(KEY_NAME).toString().equalsIgnoreCase("")) {
					return (b.get(KEY_NAME).toString().equalsIgnoreCase("")) ? 0 : -1;
				}
				if (b.has(KEY_NAME) && b.get(KEY_NAME).toString().equalsIgnoreCase("")) {
					return 1;
				}
				Double str1 = null;
				Double str2 = null;
				str1 = Math.abs(Double.parseDouble(a.get(KEY_NAME).toString()));
				str2 = Math.abs(Double.parseDouble(b.get(KEY_NAME).toString()));
				return str1.compareTo(str2);
			}
		});

		for (int i = array.length() - 1; i >= 0; i--) {
			sortedJSONArray.put(jsonValues.get(i));
		}
		return sortedJSONArray;

	}
}
