/**
 * 
 */
package com.temenos.infinity.api.wealth.backenddelegate.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealth.backenddelegate.api.PortfolioDetailsBackendDelegate;
import com.temenos.infinity.api.wealth.config.PortfolioWealthAPIServices;
import com.temenos.infinity.api.wealth.constants.OperationName;
import com.temenos.infinity.api.wealth.constants.ServiceId;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealth.util.WealthMockUtil;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

public class PortfolioDetailsBackendDelegateImpl implements PortfolioDetailsBackendDelegate {
	private static final Logger LOG = LogManager.getLogger(PortfolioDetailsBackendDelegateImpl.class);
	WealthMockUtil wealthMockUtil = new WealthMockUtil();

	@SuppressWarnings("unchecked")
	@Override
	public Result getInstrumentTotal(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response, Map<String, Object> headersMap) {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Object portfolioIdobj = inputParams.get(TemenosConstants.PORTFOLIOID);
		Object graphDurationObj = inputParams.get(TemenosConstants.GRAPHDURATION);
		Object navPageObj = inputParams.get("navPage");
		String portfolioId = null;
		String navPage = null;
		String graphDuration = null;
		if (navPageObj != null && !navPageObj.equals("Portfolio") && !navPageObj.equals("")) {
			if (portfolioIdobj == null || portfolioIdobj.equals("")) {
				LOG.error("Invalid request");
				JSONObject result = new JSONObject();
				result.put("status", "Failure");
				result.put("error", "Invalid Input Params!");
				return Utilities.constructResultFromJSONObject(result);
			} else {
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
			}
		} else if (navPageObj == null || navPageObj.equals("") || portfolioIdobj == null || portfolioIdobj.equals("")
				|| graphDurationObj == null || graphDurationObj.equals("")) {
			LOG.error("Invalid request");
			JSONObject result = new JSONObject();
			result.put("status", "Failure");
			result.put("error", "Invalid Input Params!");
			return Utilities.constructResultFromJSONObject(result);
		} else {
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
		}

		try {

			List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);

			if (allportfoliosList.contains(portfolioId)) {
				String serviceName = ServiceId.WEALTHORCHESTRATION;
				String operationName = OperationName.GETINSTRUMENTTOTAL;
				try {
					return PortfolioWealthUtils.backendResponse(serviceName, operationName, inputMap, headersMap, request);
				} catch (Exception e) {
					LOG.error("Error while invoking Service - "
							+ PortfolioWealthAPIServices.WEALTH_GETINSTRUMENTTOTAL.getOperationName() + "  : " + e);
				}
			} else {
				return PortfolioWealthUtils.UnauthorizedAccess();
			}
			return null;

		} catch (Exception e) {
			LOG.error("Error while invoking Transact - "
					+ PortfolioWealthAPIServices.WEALTH_GETINSTRUMENTTOTAL.getOperationName() + "  : " + e);
			return null;

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result getAssetAllocation(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response, Map<String, Object> headersMap) {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Object portfolioIdobj = inputParams.get(TemenosConstants.PORTFOLIOID);
		String portfolioId = null;
		if (portfolioIdobj == null || portfolioIdobj.equals("")) {
			LOG.error("Invalid request");
			JSONObject result = new JSONObject();
			result.put("status", "Failure");
			result.put("error", "Invalid Input! Portfolio ID cannot be null or empty");
			return Utilities.constructResultFromJSONObject(result);
		} else {
			portfolioId = inputParams.get(TemenosConstants.PORTFOLIOID).toString();
			inputMap.put(TemenosConstants.PORTFOLIOID, portfolioId);
		}
		try {

			List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);

			if (allportfoliosList.contains(portfolioId)) {
				String serviceName = ServiceId.WEALTHORCHESTRATION;
				String operationName = OperationName.GETASSETALLOCATION;

				try {
					return PortfolioWealthUtils.backendResponse(serviceName, operationName, inputMap, headersMap, request);

				} catch (Exception e) {
					LOG.error("Error while invoking getAssetAllocation - " + OperationName.GETASSETALLOCATION + "  : "
							+ e);
				}
			} else {
				return PortfolioWealthUtils.UnauthorizedAccess();

			}

			return null;

		} catch (Exception e) {
			LOG.error("Error while invoking getAssetAllocation - " + OperationName.GETASSETALLOCATION + "  : " + e);
			return null;

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result getCashBalance(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response, Map<String, Object> headersMap) {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Object portfolioIdobj = inputParams.get(TemenosConstants.PORTFOLIOID);
		String portfolioId = null;
		if (portfolioIdobj == null || portfolioIdobj.equals("")) {
			LOG.error("Invalid request");
			JSONObject result = new JSONObject();
			result.put("status", "Failure");
			result.put("error", "Invalid Input! Portfolio ID cannot be null or empty");
			return Utilities.constructResultFromJSONObject(result);

		} else {
			portfolioId = inputParams.get(TemenosConstants.PORTFOLIOID).toString();
			inputMap.put(TemenosConstants.PORTFOLIOID, portfolioId);
		}
		try {

			List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);

			if (allportfoliosList.contains(portfolioId)) {
				String serviceName = ServiceId.WEALTHORCHESTRATION;
				String operationName = OperationName.GETCASHACCOUNTS;

				try {

					return PortfolioWealthUtils.backendResponse(serviceName, operationName, inputMap, headersMap, request);

				} catch (Exception e) {
					LOG.error("Error while invoking getCashBalance - " + OperationName.GETCASHACCOUNTS + "  : " + e);
				}
			} else {
				return PortfolioWealthUtils.UnauthorizedAccess();

			}
		} catch (Exception e) {
			LOG.error("Error while invoking getCashBalance - " + OperationName.GETCASHACCOUNTS + "  : " + e);
			return null;

		}
		return null;
	}

	public String[] nextMonths() {
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

		String[] finalArr = null;

		String a[] = new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV",
				"DEC" };
		List<String> al = Arrays.asList(a);

		List<String> finalList = new ArrayList<String>();
		finalList.addAll(al.subList(currentMonth + 1, al.size()));
		finalList.addAll(al.subList(0, currentMonth + 1));

		finalArr = finalList.toArray(new String[0]);

		return finalArr;

	}

	public String[] prevMonths() {
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

		String[] finalArr = null;

		String a[] = new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV",
				"DEC" };
		List<String> al = Arrays.asList(a);

		List<String> finalList = new ArrayList<String>();
		if (currentMonth == 0) {
			finalList.addAll(al.subList(0, 1));
		} else {
			finalList.addAll(al.subList(0, currentMonth));
		}

		finalArr = finalList.toArray(new String[0]);

		return finalArr;

	}

	public String[] getMonths(String[] arr) {
		String[] resultArray = new String[46];
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();

//		if (arr[0].equalsIgnoreCase("Jan")) {
//			startDate.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1);
//			//startDate.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
//		} else {
//			startDate.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1);
//			startDate.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH) + 1);
//		}
		startDate.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR) - 1);
		startDate.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
		// startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	//	startDate.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		for (int i = 0; i < 3; i++) {
			startDate.add(Calendar.DATE, 4);
			resultArray[i] = df.format(startDate.getTime());
		}
		for (int i = 3; i < 45; i++) {
			startDate.add(Calendar.DATE, 8);
			resultArray[i] = df.format(startDate.getTime());
		}
		endDate.add(Calendar.DATE, -1);
		resultArray[45] = df.format(endDate.getTime());

		return resultArray;
	}

	public String[] getPrevMonths(String[] arr) {
		String[] resultArray = new String[48];
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();

		if (arr[0].equalsIgnoreCase("Jan")) {
			startDate.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
			startDate.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
		} else {
			startDate.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
			startDate.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
		}
		startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		for (int i = 47; i >= 0; i--) {
			startDate.add(Calendar.DATE, -8);
			resultArray[i] = df.format(startDate.getTime());
		}
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		endDate.add(Calendar.DATE, -1);
		resultArray[47] = df.format(endDate.getTime());
		resultArray[0] = df.format(cal.getTime());

		return resultArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result getPortfolioDetails(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response, Map<String, Object> headersMap) {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Object portfolioIdobj = inputParams.get(TemenosConstants.PORTFOLIOID);
		Object graphDurationObj = inputParams.get(TemenosConstants.GRAPHDURATION);
		Object navPageObj = inputParams.get("navPage");
		String portfolioId = null;
		String navPage = null;
		String graphDuration = null;

		if (portfolioIdobj == null || portfolioIdobj.equals("")) {
			LOG.error("Invalid request");
			JSONObject result = new JSONObject();
			result.put("status", "Failure");
			result.put("error", "Invalid Input! Portfolio ID cannot be null or empty");
			return Utilities.constructResultFromJSONObject(result);
		} else {
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
		}
		List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);

		if (allportfoliosList.contains(portfolioId)) {
			try {
				String serviceName = ServiceId.WEALTHORCHESTRATION;
				String operationName = OperationName.GETPORTFOLIODETAILS;
				
				Result portfolioRes = PortfolioWealthUtils.backendResponse(serviceName, operationName, inputMap, headersMap, request);
				Dataset portDataset = portfolioRes.getDatasetById("cashAccounts");
				JSONArray cashAcc = ResultToJSON.convertDataset(portDataset);
				String allAccounts="";
				for(int i = 0;i<cashAcc.length();i++)
				{
					String accId = cashAcc.getJSONObject(i).get("accountNumber").toString();
					allAccounts = allAccounts.equals("")?allAccounts.concat(accId):allAccounts.concat(",").concat(accId);
				}
				PortfolioWealthUtils.saveAccountIntoSession(allAccounts, portfolioId);
				return PortfolioWealthUtils.backendResponse(serviceName, operationName, inputMap, headersMap, request);

			} catch (Exception e) {
				LOG.error("Error while invoking Transact - "
						+ PortfolioWealthAPIServices.WEALTH_GETINSTRUMENTTOTAL.getOperationName() + "  : " + e);
				return null;

			}
		} else {

			return PortfolioWealthUtils.UnauthorizedAccess();

		}

	}

	@Override
	@SuppressWarnings("unchecked")
	
	public Result getPortfolioHealth(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response, Map<String, Object> headersMap) {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Object portfolioIdobj = inputParams.get(TemenosConstants.PORTFOLIOID);
		Object portfolioTypeObj = inputParams.get(TemenosConstants.PORTFOLIOSERVICETYPE);
		Object navPageObj = inputParams.get("navPage");
		String portfolioId = null;
		String portfolioType = null;
		String navPage = null;
		
		if (portfolioIdobj == null || portfolioIdobj.equals("") || portfolioTypeObj == null || portfolioTypeObj.equals("")) {
			if(portfolioIdobj == null || portfolioIdobj.equals("")) {
				return PortfolioWealthUtils.validateMandatoryFields(TemenosConstants.PORTFOLIOID);
			}
			else if (portfolioTypeObj == null || portfolioTypeObj.equals("")) {
				return PortfolioWealthUtils.validateMandatoryFields(TemenosConstants.PORTFOLIOSERVICETYPE);
			}
		} 
		else {
			if (portfolioIdobj != null) {
				portfolioId = inputParams.get(TemenosConstants.PORTFOLIOID).toString();
				inputMap.put(TemenosConstants.PORTFOLIOID, portfolioId);
			}
			if (portfolioTypeObj != null) {
				portfolioType = inputParams.get(TemenosConstants.PORTFOLIOSERVICETYPE).toString();
				inputMap.put(TemenosConstants.PORTFOLIOSERVICETYPE, portfolioType);
			}
			if (navPageObj != null) {
				navPage = inputParams.get("navPage").toString();
				inputMap.put("navPage", navPage);
			}
			List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);
			if (allportfoliosList.contains(portfolioId) && portfolioType.equals(TemenosConstants.ADVISORY)) {
				String serviceName = ServiceId.WEALTHORCHESTRATION;
				String operationName = OperationName.GETPORTFOLIOHEALTH;
				try {
					return PortfolioWealthUtils.backendResponse(serviceName, operationName, inputMap, headersMap, request);
				} catch (Exception e) {
					LOG.error("Error while invoking Transact - "
							+ PortfolioWealthAPIServices.WEALTH_GETPORTFOLIOHEALTH.getOperationName() + "  : " + e);
					return null;
				}
			}
			else {
				return PortfolioWealthUtils.UnauthorizedAccess();
			}
			
		}
		return null;
			}
	@SuppressWarnings("unchecked")
	@Override
	public Result getAllocation(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response, Map<String, Object> headersMap) {
		Map<String, Object> inputMap = new HashMap<>();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Object portfolioIdobj = inputParams.get(TemenosConstants.PORTFOLIOID);
		String portfolioId = null;
		if (portfolioIdobj == null || portfolioIdobj.equals("")  ) {
			LOG.error("Invalid request");
			JSONObject result = new JSONObject();
			result.put("status", "Failure");
			result.put("error", "Invalid Input! Portfolio ID cannot be null or empty");
			return Utilities.constructResultFromJSONObject(result);
		}
		else {
			if (portfolioIdobj != null) {
				portfolioId = inputParams.get(TemenosConstants.PORTFOLIOID).toString();
				inputMap.put(TemenosConstants.PORTFOLIOID, portfolioId);
			}
		}
		List<String> allportfoliosList = PortfolioWealthUtils.getAllPortfoliosFromCache(request);

		if (allportfoliosList.contains(portfolioId)) {
			try {
				String serviceName = ServiceId.WEALTHORCHESTRATION;
				String operationName = OperationName.GETALLOCATION;
				Result portfolioRes = PortfolioWealthUtils.backendResponse(serviceName, operationName, inputMap, headersMap, request);
				return portfolioRes;

			} catch (Exception e) {
				LOG.error("Error while invoking Transact - "
						+ PortfolioWealthAPIServices.WEALTH_GETALLOCATION.getOperationName() + "  : " + e);
				return null;

			}
		} else {

			return PortfolioWealthUtils.UnauthorizedAccess();

		}

	}

}

