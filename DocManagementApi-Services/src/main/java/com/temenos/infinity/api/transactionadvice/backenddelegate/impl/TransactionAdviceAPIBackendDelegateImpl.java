package com.temenos.infinity.api.transactionadvice.backenddelegate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.temenos.infinity.api.commons.constants.FabricConstants;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.api.commons.invocation.Executor;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.transactionadvice.backenddelegate.api.TransactionAdviceAPIBackendDelegate;
import com.temenos.infinity.api.transactionadvice.config.TransactionAdviceAPIServices;
import com.temenos.infinity.api.transactionadvice.constants.ErrorCodeEnum;
import com.temenos.dbx.product.utils.DTOConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.ServerConfigurations;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.exceptions.HttpCallException;

public class TransactionAdviceAPIBackendDelegateImpl implements TransactionAdviceAPIBackendDelegate {
	private static final Logger LOG = LogManager.getLogger(TransactionAdviceAPIBackendDelegateImpl.class);
	public static final String ERROR_CODE_KEY = "dbpErrCode";
	public static final String ERROR_MESSAGE_KEY = "dbpErrMsg";
	public static final String UNAUTHORIZED = "Unauthorized";
	public static volatile JSONObject tokenInfo;

	@Override
	public byte[] download(String documentId, String revision, String authToken) {
		byte[] serviceResponse = null;
		try {
			Map<String, Object> inputMap = new HashMap<>();
			Map<String, Object> headerMap = new HashMap<>();
			inputMap.put("documentId", documentId);
			inputMap.put("revision", revision);
			// Login Operation
			JSONObject loginServiceResponseJSON = getToken(authToken,false);
			headerMap=getHeadersFromLoginService(loginServiceResponseJSON,authToken);
			LOG.debug("Header=" + headerMap.toString());
			serviceResponse = Executor.invokePassThroughServiceAndGetBytes(
					TransactionAdviceAPIServices.TRANSACTIONADVICEJSON_DOWNLOAD, inputMap, headerMap);
			String serviceResponseStr=new String(serviceResponse);
			LOG.debug("ServiceResponse=" + serviceResponse);
			if (serviceResponseStr.contains(UNAUTHORIZED)) {
				LOG.debug("login token expired genarating again");
				loginServiceResponseJSON = getToken(authToken,true);
				headerMap=getHeadersFromLoginService(loginServiceResponseJSON,authToken);
				serviceResponse = Executor.invokePassThroughServiceAndGetBytes(
						TransactionAdviceAPIServices.TRANSACTIONADVICEJSON_DOWNLOAD, inputMap, headerMap);
			}
			if (serviceResponse == null)
				LOG.error("efs file download failed");
		} catch (ApplicationException e) {
			LOG.error("efs file download failed" + e);
		} catch (Exception e) {
			LOG.error("efs file download failed" + e);
		}
		return serviceResponse;
	}

	@Override
	public ArrayList<JSONObject> search(DataControllerRequest request,HashMap<String, Object> paramMap, String authToken)
			throws ApplicationException {
		ArrayList<JSONObject> docsList = new ArrayList<JSONObject>();
		try {
			Map<String, Object> inputMap = paramMap;
			String serviceResponse = new String();
			JSONObject loginServiceResponseJSON;
			Map<String, Object> headerMap = new HashMap<>();
//			 String accountNnumberConfigValue = EnvironmentConfigurationsHandler.getValue("EFS_ACCOUNT_NUMBER");
//			 String customerNumberConfigValue = EnvironmentConfigurationsHandler.getValue("EFS_CUSTOMERID");
			
			 String corecustomerId = getBackendId(request,inputMap.get("customerNumber").toString());
//			 if (accountNnumberConfigValue != null && accountNnumberConfigValue != ""
//			 		&& customerNumberConfigValue != null && customerNumberConfigValue != "") {
//				 inputMap.replace("accountNumber", accountNnumberConfigValue);
//				 inputMap.replace("customerNumber", customerNumberConfigValue);
			 if(corecustomerId != null && corecustomerId != "") {
			     inputMap.replace("customerNumber",corecustomerId);
				// Login Operation
				loginServiceResponseJSON = getToken(authToken,false);
				headerMap=getHeadersFromLoginService(loginServiceResponseJSON,authToken);
				serviceResponse = Executor.invokePassThroughServiceAndGetString(
						TransactionAdviceAPIServices.TRANSACTIONADVICEJSON_SEARCH, inputMap, headerMap);
				LOG.debug("EFS doc search service response=" + serviceResponse);
				if (serviceResponse.contains(UNAUTHORIZED)) {
					LOG.debug("login token expired genarating again");
					loginServiceResponseJSON = getToken(authToken,true);
					headerMap=getHeadersFromLoginService(loginServiceResponseJSON,authToken);
					serviceResponse = Executor.invokePassThroughServiceAndGetString(
							TransactionAdviceAPIServices.TRANSACTIONADVICEJSON_SEARCH, inputMap, headerMap);
				}
				docsList = getSearchResults(serviceResponse);
				return docsList;
			 }
			 else {
				 LOG.error("core customerNumber are not present");
				 return docsList;
			 }
//			 } else {
//			 	LOG.error("efs accountNumber customerNumber are not present");
//			 	return docsList;
//			 }
		} catch (ApplicationException e) {
			LOG.error(e);
			throw e;
		} catch (Exception e) {
			LOG.error("efs search failed" + e);
			throw new ApplicationException(ErrorCodeEnum.ERR_25001);
		}
	}

	String getBackendId(DataControllerRequest request, String customerId) {
		String filter = DTOConstants.CUSTOMER_ID + DBPUtilitiesConstants.EQUAL + customerId
		+ DBPUtilitiesConstants.AND + DTOConstants.BACKENDTYPE + DBPUtilitiesConstants.EQUAL;
	String ARRANGEMENTS_BACKEND = ServerConfigurations.ARRANGEMENTS_BACKEND.getValueIfExists();
	String btype = DTOConstants.T24;
	if (StringUtils.isNotBlank(ARRANGEMENTS_BACKEND)) {
		if (ARRANGEMENTS_BACKEND.equals("MOCK")) {
			btype=DTOConstants.CORE;
		}
	}
	filter+=btype;
	

		Result result = new Result();
		try {
			result = HelperMethods.callGetApi(request, filter, HelperMethods.getHeaders(request),
					URLConstants.BACKENDIDENTIFIER_GET);
		} catch (HttpCallException e) {
			LOG.error("Caught exception while getting backend identifier: ", e);
		}
		return HelperMethods.getFieldValue(result, DTOConstants.BACKENDID);
    }
	
	private Map<String, Object> getHeadersFromLoginService(JSONObject loginServiceResponseJSON,String authToken) throws ApplicationException {
		String xsrf;
		String jsessionid;
		String cookie;
		Map<String, Object> headerMap = new HashMap<>();
		xsrf = loginServiceResponseJSON.getString("DM-XSRF-TOKEN");
		//xsrf="f70fbc90-b050-491d-a137-468b60c84749" //Expired one.
		jsessionid = loginServiceResponseJSON.getString("JSESSIONID");
		if (StringUtils.isBlank(xsrf) || StringUtils.isBlank(jsessionid)) {
			LOG.debug("EFS--xsrf jsessionid are blank Login Failed");
			throw new ApplicationException(ErrorCodeEnum.ERR_25004);
		}
		headerMap.put("X-XSRF-TOKEN", xsrf);
		cookie = "DM-XSRF-TOKEN=" + xsrf + "; " + "JSESSIONID=" + jsessionid;
		//cookie="DM-XSRF-TOKEN=f70fbc90-b050-491d-a137-468b60c84749; JSESSIONID=D3Z6gC0tSXoQc5Uu-I2xSU4qlFQISghD4ayk2B7F.standalone-dm"//Expired one
		headerMap.put("COOKIE", cookie);
		if (!StringUtils.isBlank(authToken)) {
			headerMap.put(FabricConstants.X_KONY_AUTHORIZATION_HEADER, authToken);
		}
		return headerMap;
	}

	private ArrayList<JSONObject> getSearchResults(String serviceResponse) throws ApplicationException {

		try {
			ArrayList<JSONObject> docsList = new ArrayList<JSONObject>();
			JSONObject outputJSONObject;
			LOG.debug("EFS-search service response=" + serviceResponse);
			if(serviceResponse.contains("Unauthorized")) {
				LOG.error("EFS login Unauthorized" +serviceResponse);
				throw new ApplicationException(ErrorCodeEnum.ERR_25004);
			}
			if (serviceResponse == null || serviceResponse.equals("[]")) {
				LOG.error("efs search  service failed"+serviceResponse);
				throw new ApplicationException(ErrorCodeEnum.ERR_25003);
			} else {
				LOG.debug("SearchServiceResponse=" + serviceResponse);
				JSONArray responseArray = new JSONArray(serviceResponse);
				int size = responseArray.length();
				for (int i = 0; i < size; i++) {
					JSONObject obj = responseArray.getJSONObject(i);
					if (obj.has("fileProperties")) {
						outputJSONObject = new JSONObject();
						JSONObject properties = obj.getJSONObject("properties");
						JSONObject fileProperties = obj.getJSONObject("fileProperties");
						JSONObject keys = obj.getJSONObject("keys");
						outputJSONObject.put("documentId", properties.get("id").toString());
						outputJSONObject.put("revision", properties.get("revision").toString());
						outputJSONObject.put("month", keys.get("month").toString());
						outputJSONObject.put("year", keys.get("year").toString());
						outputJSONObject.put("documentDate", keys.get("document-date").toString());
						outputJSONObject.put("accountNumber", keys.get("account-number").toString());
						outputJSONObject.put("customerNumber", keys.get("customer-number").toString());
						outputJSONObject.put("fileExtension", fileProperties.get("extension").toString());
						outputJSONObject.put("subType", keys.get("sub-type").toString());
						docsList.add(outputJSONObject);
					}
				}
				if (docsList.size() <= 0) {
					LOG.error("efs search  no recordds found");
					throw new ApplicationException(ErrorCodeEnum.ERR_25003);
				} else {
					return docsList;
				}
			}
		} catch (ApplicationException e) {
			LOG.error(e);
			throw e;
		}catch (Exception e) {
			LOG.error("efs search failed" + e);
			throw new ApplicationException(ErrorCodeEnum.ERR_25001);
		}
	}

	private static JSONObject getToken(String authToken,boolean isGenarateNewToken) throws ApplicationException,Exception {
		boolean isTokenAvailable=(tokenInfo != null) ? true:false;
		boolean newTokenRequired= !isTokenAvailable || isGenarateNewToken;
		if (newTokenRequired) {
			synchronized (TransactionAdviceAPIBackendDelegateImpl.class) {
					Map<String, Object> inputMap = new HashMap<>();
					Map<String, Object> headerMap = new HashMap<>();
					String userNameValue = EnvironmentConfigurationsHandler.getValue("EFS_USERNAME");
					String passwordValue = EnvironmentConfigurationsHandler.getValue("EFS_PASSWORD");
					if (userNameValue != null && passwordValue != null) {
						inputMap.put("username", userNameValue);
						inputMap.put("password", passwordValue);
						headerMap.put("X-XSRF-TOKEN", "");
						if (!StringUtils.isBlank(authToken)) {
							LOG.debug("efs login -Using Auth Token from Param-Login");
							headerMap.put(FabricConstants.X_KONY_AUTHORIZATION_HEADER, authToken);
						}
						LOG.debug("Attempting for efs login -Using Auth Token from Param-Login");
						String serviceResponse = Executor.invokeService(
								TransactionAdviceAPIServices.TRANSACTIONADVICEJSON_LOGIN, inputMap, headerMap);
						tokenInfo = Utilities.convertStringToJSON(serviceResponse);
						if (tokenInfo == null) {
							LOG.error("efs login failed");
						} else {
							return tokenInfo;
						}
					}
					else {
						LOG.error("efs userName and passowrd are null");
						throw new ApplicationException(ErrorCodeEnum.ERR_25005);
				}
			}
		}
		return tokenInfo;
	}

}
