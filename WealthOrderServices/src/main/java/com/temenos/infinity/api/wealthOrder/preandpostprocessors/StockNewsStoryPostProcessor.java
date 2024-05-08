/**
 * 
 */
package com.temenos.infinity.api.wealthOrder.preandpostprocessors;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kony.dbputilities.util.CommonUtils;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * (INFO) Builds the Result in the desired format for the Refinitiv service.
 * 
 * @author himaja.sridhar
 *
 */
public class StockNewsStoryPostProcessor implements DataPostProcessor2 {

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		JSONArray stockNews = new JSONArray();
		List<Dataset> dataset = result.getAllDatasets();
		List<Record> drecords = dataset.get(0).getAllRecords();
		for (int j = 0; j < drecords.size(); j++) {
			JSONObject actListObj = new JSONObject();
			Record drecord = drecords.get(j);
			actListObj = CommonUtils.convertRecordToJSONObject(drecord);
			try {
				String formattedTe = actListObj.get("TE").toString().replaceAll("<pre>|</pre>", "");
				actListObj.put("TE", formattedTe);
			} catch (Exception e) {
			}
			;
			stockNews.put(actListObj);
		}

		if (stockNews != null && stockNews.length() > 0) {

			List<JSONObject> stockList = new ArrayList<JSONObject>();
			for (int i = 0; i < stockNews.length(); i++)
				stockList.add(stockNews.getJSONObject(i));

			Collections.sort(stockList, new Comparator<JSONObject>() {
				private final String KEY_NAME = "CT";

				@Override
				public int compare(JSONObject a, JSONObject b) {
					Date d1 = null;
					Date d2 = null;
					try {
						String str = a.get(KEY_NAME).toString();
						d1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(str.substring(0, str.length() - 6));
						str = b.get(KEY_NAME).toString();
						d2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(str.substring(0, str.length() - 6));
					} catch (JSONException e) {
						e.getMessage();
					} catch (ParseException e) {
						e.getMessage();
					}
					return d1.compareTo(d2);
				}

			});
			JSONArray sortedJSON = new JSONArray();
			for (int i = stockList.size() - 1; i >= 0; i--) {
				sortedJSON.put(stockList.get(i));
			}
			JSONObject responseJSON = new JSONObject();
			responseJSON.put("stockNewsDetails", sortedJSON);
			int totalCount = sortedJSON.length();
			responseJSON.put("totalCount", totalCount);
			if (request.getParameterValues(TemenosConstants.PAGESIZE) != null
					&& request.getParameterValues(TemenosConstants.PAGEOFFSET) != null) {
				String pageSizeVal = request.getParameterValues(TemenosConstants.PAGESIZE)[0].toString();
				String offsetVal = request.getParameterValues(TemenosConstants.PAGEOFFSET)[0].toString();
				int pageSize = (pageSizeVal != null && pageSizeVal.trim().length() > 0) ? Integer.parseInt(pageSizeVal)
						: 0;
				int offset = (offsetVal != null && offsetVal.trim().length() > 0) ? Integer.parseInt(offsetVal) : 0;
				if (pageSize > 0 && offset >= 0) {
					JSONObject jsonPagination = pagination(responseJSON, pageSize, offset);
					return Utilities.constructResultFromJSONObject(jsonPagination);
				}
			} else {
				return Utilities.constructResultFromJSONObject(responseJSON);
			}

		}
		return result;

	}

	private JSONObject pagination(JSONObject jsonResult, int limit, int offset) {
		JSONArray jsonArray = jsonResult.getJSONArray("stockNewsDetails");
		JSONObject response = new JSONObject();
		JSONArray paginationJSON = new JSONArray();

		int j = 0;
		for (int i = offset; i < jsonArray.length(); i++) {
			if (j == limit) {
				break;
			} else {
				paginationJSON.put(jsonArray.get(i));
			}
			j++;
		}
		response.put("stockNewsDetails", paginationJSON);
		int totalCount = jsonArray.length();
		response.put("totalCount", totalCount);
		return response;
	}
}
