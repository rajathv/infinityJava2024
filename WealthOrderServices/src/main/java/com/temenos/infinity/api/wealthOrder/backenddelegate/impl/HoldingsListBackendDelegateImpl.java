package com.temenos.infinity.api.wealthOrder.backenddelegate.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthOrder.backenddelegate.api.HoldingsListBackendDelegate;
import com.temenos.infinity.api.wealthOrder.config.WealthAPIServices;
import com.temenos.infinity.api.wealthOrder.constants.OperationName;
import com.temenos.infinity.api.wealthOrder.constants.ServiceId;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;
import com.temenos.infinity.api.wealthOrder.util.WealthMockUtil;

public class HoldingsListBackendDelegateImpl implements HoldingsListBackendDelegate {

	private static final Logger LOG = LogManager.getLogger(HoldingsListBackendDelegateImpl.class);
	WealthMockUtil wealthMockUtil = new WealthMockUtil();

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	@Override
	public Result getHoldingsList(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response, Map<String, Object> headersMap) {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> headerParams = new HashMap<>();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Object portfolioIdobj = inputParams.get(TemenosConstants.PORTFOLIOID);
		Object sortByObj = inputParams.get(TemenosConstants.SORTBY);
		Object searchObj = inputParams.get(TemenosConstants.SEARCHBYINSTRUMENTNAME);
		Object navPageObj = inputParams.get("navPage");
		Object sortTypeObj = inputParams.get(TemenosConstants.SORTORDER);
		Object limitObj = inputParams.get(TemenosConstants.PAGESIZE);
		Object offsetObj = inputParams.get(TemenosConstants.PAGEOFFSET);
		Object instidObj = inputParams.get(TemenosConstants.INSTRUMENTID);

		String portfolioId = null;
		String sortBy = null;
		String search = null, instid = null;
		String sortType = null, limit = null, offset = null;
		List sortValues = Arrays.asList("", "description", "marketPrice", "quantity", "marketValue", "costPrice",
				"unrealPLMkt", "weightPercentage", "assestClass", "region", "sector", "secCCy", "exchangeRate",
				"marketValPOS", "costValue", "costExchangeRate", "unRealizedPLPercentage", "dailyPL",
				"dailyPLPercentage", "costValueSecCcy", "unrealizedProfitLossPercentageSecCcy",
				"unrealizedProfitLossSecCcy", "subAssetClass");

		if (!sortValues.contains(sortByObj)) {
			LOG.error("Invalid request");
			JSONObject result = new JSONObject();
			result.put("status", "Failure");
			result.put("error", TemenosConstants.SORTBY + " value is not valid");
			return Utilities.constructResultFromJSONObject(result);
		} else {
			if (inputParams.get(TemenosConstants.PORTFOLIOID) != null
					&& inputParams.get(TemenosConstants.PORTFOLIOID).toString().trim().length() > 0) {
				portfolioId = inputParams.get(TemenosConstants.PORTFOLIOID).toString();
				inputMap.put(TemenosConstants.PORTFOLIOID, portfolioId);
			} else {
				return PortfolioWealthUtils.validateMandatoryFields(TemenosConstants.PORTFOLIOID);
			}

			if (sortByObj != null) {
				sortBy = inputParams.get(TemenosConstants.SORTBY).toString();
				inputMap.put(TemenosConstants.SORTBY, sortBy);
			} else {
				return PortfolioWealthUtils.validateMandatoryFields(TemenosConstants.SORTBY);
			}
			if (navPageObj == null || navPageObj.toString().trim().equals("")) {
				return PortfolioWealthUtils.validateMandatoryFields(TemenosConstants.NAVPAGE);
			}
			if (searchObj != null) {
				search = inputParams.get(TemenosConstants.SEARCHBYINSTRUMENTNAME).toString();
				inputMap.put(TemenosConstants.SEARCHBYINSTRUMENTNAME, search);
			}

			if (instidObj != null) {
				instid = inputParams.get(TemenosConstants.INSTRUMENTID).toString();
				inputMap.put(TemenosConstants.INSTRUMENTID, instid);
			}

			if (sortTypeObj != null) {
				sortType = inputParams.get(TemenosConstants.SORTORDER).toString();
				inputMap.put(TemenosConstants.SORTORDER, sortType);
			}
			if (limitObj != null) {
				limit = inputParams.get(TemenosConstants.PAGESIZE).toString();
				inputMap.put(TemenosConstants.PAGESIZE, limit);
			}
			if (offsetObj != null) {
				offset = inputParams.get(TemenosConstants.PAGEOFFSET).toString();
				inputMap.put(TemenosConstants.PAGEOFFSET, offset);
			}

		}
		List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);

		if (allportfoliosList.contains(portfolioId)) {
			try {
				String serviceName = ServiceId.WEALTHORCHESTRATION;
				String operationName = OperationName.GETHOLDINGSlIST;

				return PortfolioWealthUtils.backendResponse(serviceName, operationName, inputMap, headersMap, request);

			} catch (Exception e) {
				LOG.error("Error while invoking Transact - "
						+ WealthAPIServices.WEALTH_GETPORTFOLIOHOLDINGS.getOperationName() + "  : " + e);
				return null;

			}
		} else {

			return PortfolioWealthUtils.UnauthorizedAccess();

		}
	}

	public JSONArray returnSearch(JSONArray array, String searchValue, String inp_instrumentId) {
		JSONArray filtedArray = new JSONArray();
		String search = searchValue.toLowerCase();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = null;
			try {
				obj = array.getJSONObject(i);
				String desc = obj.getString("description").toLowerCase();
				String instrumentId = (obj.has("holdingsId") ? obj.getString("holdingsId"):
						(obj.has("instrumentId") ? obj.getString("instrumentId") : ""));
				if (inp_instrumentId.length() > 0 && instrumentId != null && instrumentId.length() > 0) {
					if (desc.contains(search) && inp_instrumentId.equals((instrumentId).toString())) {
						filtedArray.put(obj);
					}
				} else {
					if (desc.contains(search)) {
						filtedArray.put(obj);
					}
				}
			} catch (Exception e) {

				LOG.error("Error while invoking Transact - "
						+ WealthAPIServices.WEALTH_GETPORTFOLIOHOLDINGS.getOperationName() + "  : " + e);
				return null;
			}
		}
		return filtedArray;
	}

	public JSONArray returnInstrumentID(JSONArray array, String instrumentidInp) {
		JSONArray filtedArray = new JSONArray();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = null;
			try {
				obj = array.getJSONObject(i);
				String instId = obj.getString(TemenosConstants.HOLDINGSID).toLowerCase();
				if (instId.contains(instrumentidInp) || instId.equalsIgnoreCase(instrumentidInp)) {
					// equalsIgnoreCase added for instrument IDs like EUR_GBP
					filtedArray.put(obj);
				}
			} catch (Exception e) {

				LOG.error("Error while invoking Transact - "
						+ WealthAPIServices.WEALTH_GETPORTFOLIOHOLDINGS.getOperationName() + "  : " + e);
				return null;
			}
		}
		return filtedArray;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public Result getSearchInstrumentList(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response, Map<String, Object> headersMap) {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Object searchObj = inputParams.get(TemenosConstants.SEARCHBYINSTRUMENTNAME);
		Object sortByObj = inputParams.get(TemenosConstants.SORTBY);
		Object portfolioIdobj = inputParams.get(TemenosConstants.PORTFOLIOID);
		Object natureOfInsObj = inputParams.get(TemenosConstants.INSTRUMENTTYPE);
		String search = null;
		String sortBy = null;
		String portfolioId = null;
		String natureOfIns = null;
		
		if (inputParams.get(TemenosConstants.PORTFOLIOID) != null
				&& inputParams.get(TemenosConstants.PORTFOLIOID).toString().trim().length() > 0) {
			portfolioId = inputParams.get(TemenosConstants.PORTFOLIOID).toString();
			inputMap.put(TemenosConstants.PORTFOLIOID, portfolioId);
		} else {
				if (request.getParameter("isFavouriteSearch") == null
				|| !request.getParameter("isFavouriteSearch").toString().equalsIgnoreCase("true")) {
			return PortfolioWealthUtils.validateMandatoryFields(TemenosConstants.PORTFOLIOID);
		}
		}
		if (sortByObj != null) {
			sortBy = inputParams.get(TemenosConstants.SORTBY).toString();
			inputMap.put(TemenosConstants.SORTBY, sortBy);
		} else {
			return PortfolioWealthUtils.validateMandatoryFields(TemenosConstants.SORTBY);
		}
		if (inputParams.get(TemenosConstants.SEARCHBYINSTRUMENTNAME) != null
				&& inputParams.get(TemenosConstants.SEARCHBYINSTRUMENTNAME).toString().trim().length() > 0) {
			search = inputParams.get(TemenosConstants.SEARCHBYINSTRUMENTNAME).toString();
			inputMap.put(TemenosConstants.SEARCHBYINSTRUMENTNAME, search);
			inputMap.put("instrumentName", search);
			inputMap.put("paramValue", ("%27" + search.trim() + "%27").replace(" ", "%20"));
		} else {
			return PortfolioWealthUtils.validateMandatoryFields(TemenosConstants.SEARCHBYINSTRUMENTNAME);
		}

		try {

			List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);

			if ((request.getParameter("isFavouriteSearch") != null && request.getParameter("isFavouriteSearch").equalsIgnoreCase("true"))
					|| allportfoliosList.contains(portfolioId)) {
				try {
					String serviceName = ServiceId.WEALTHORCHESTRATION;
					String operationName = OperationName.GETSEARCHINSTRUMENTLIST;

					return PortfolioWealthUtils.backendResponse(serviceName, operationName, inputMap, headersMap, request);

				} catch (Exception e) {
					LOG.error("Error while invoking Transact - "
							+ WealthAPIServices.WEALTH_GETSEARCHINSTRUMENTLIST.getOperationName() + "  : " + e);
					return null;

				}
			} else {

				return PortfolioWealthUtils.UnauthorizedAccess();

			}

		} catch (Exception e) {
			LOG.error("Error while invoking Transact - "
					+ WealthAPIServices.WEALTH_GETSEARCHINSTRUMENTLIST.getOperationName() + "  : " + e);
			return null;

		}

	}

	public JSONArray sortArray(JSONArray holdingsArr, String sortBy, String sortType) {
		JSONArray sortedJSON = new JSONArray();
		if (sortBy.equals("")) {
			sortBy = TemenosConstants.DESCRIPTION;
		}
		final String finalSortBy = sortBy;
		if (finalSortBy.equalsIgnoreCase(TemenosConstants.DESCRIPTION)
				|| finalSortBy.equalsIgnoreCase(TemenosConstants.ASSESTCLASS)
				|| finalSortBy.equalsIgnoreCase(TemenosConstants.REGION)
				|| finalSortBy.equalsIgnoreCase(TemenosConstants.SECTOR)
				|| finalSortBy.equalsIgnoreCase(TemenosConstants.SECCCY)
				|| finalSortBy.equalsIgnoreCase(TemenosConstants.SUBASSETCLASS)) {
			List<JSONObject> jsonValues = new ArrayList<JSONObject>();

			for (int i = 0; i < holdingsArr.length(); i++) {
				jsonValues.add(holdingsArr.getJSONObject(i));
			}
			Collections.sort(jsonValues, new Comparator<JSONObject>() {

				private final String KEY_NAME = finalSortBy;

				@Override
				public int compare(JSONObject a, JSONObject b) {
					String str1 = new String();
					String str2 = new String();
					str1 = a.has(KEY_NAME) ? (String) a.get(KEY_NAME) : "";
					str2 = b.has(KEY_NAME) ? (String) b.get(KEY_NAME) : "";
					return str1.compareToIgnoreCase(str2);
				}

			});

			if (sortType != null && sortType.equalsIgnoreCase(TemenosConstants.DESCENDING)) {
				Collections.reverse(jsonValues);
			}
			sortedJSON = new JSONArray(jsonValues);
		} else if (finalSortBy.equalsIgnoreCase(TemenosConstants.MARKETVALPOS)
				|| finalSortBy.equalsIgnoreCase(TemenosConstants.UNREALIZEDPLPERCENTAGE)) {
			List<JSONObject> jsonValues = new ArrayList<JSONObject>();
			for (int i = 0; i < holdingsArr.length(); i++) {
				jsonValues.add(holdingsArr.getJSONObject(i));
			}
			Collections.sort(jsonValues, new Comparator<JSONObject>() {
				private final String KEY_NAME = finalSortBy;

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
					str1 = Double.parseDouble(a.getString(KEY_NAME).replace(",", ""));
					str2 = Double.parseDouble(b.getString(KEY_NAME).replace(",", ""));
					return str1.compareTo(str2);
				}
			});
			if (sortType != null && sortType.equalsIgnoreCase(TemenosConstants.DESCENDING)) {
				Collections.reverse(jsonValues);
			}
			sortedJSON = new JSONArray(jsonValues);

		} else {
			List<JSONObject> jsonValues = new ArrayList<JSONObject>();
			for (int i = 0; i < holdingsArr.length(); i++) {
				jsonValues.add(holdingsArr.getJSONObject(i));
			}
			Collections.sort(jsonValues, new Comparator<JSONObject>() {
				private final String KEY_NAME = finalSortBy;

				@Override
				public int compare(JSONObject a, JSONObject b) {

					if (a.has(KEY_NAME) && b.has(KEY_NAME) && a.get(KEY_NAME).toString().equalsIgnoreCase("")) {
						return (b.get(KEY_NAME).toString().equalsIgnoreCase("")) ? 0 : -1;
					}
					if (b.has(KEY_NAME) && b.get(KEY_NAME).toString().equalsIgnoreCase("")) {
						return 1;
					}

					Double dbl1 = null;
					Double dbl2 = null;
					dbl1 = (a.has(KEY_NAME) && a.get(KEY_NAME).toString().length() > 0)
							? Double.parseDouble((a.get(KEY_NAME)).toString())
							: 0;
					dbl2 = (b.has(KEY_NAME) && b.get(KEY_NAME).toString().length() > 0)
							? Double.parseDouble((b.get(KEY_NAME)).toString())
							: 0;
					return dbl1.compareTo(dbl2);
				}
			});
			if (sortType != null && sortType.equalsIgnoreCase(TemenosConstants.DESCENDING)) {
				Collections.reverse(jsonValues);
			}
			sortedJSON = new JSONArray(jsonValues);
		}
		return sortedJSON;
	}
}
