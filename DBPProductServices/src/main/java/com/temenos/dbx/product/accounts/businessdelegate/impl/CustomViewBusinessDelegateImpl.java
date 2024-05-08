package com.temenos.dbx.product.accounts.businessdelegate.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.kony.dbp.dto.CustomViewDTO;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.temenos.dbx.product.accounts.businessdelegate.api.CustomViewBusinessDelegate;

public class CustomViewBusinessDelegateImpl implements CustomViewBusinessDelegate {

	private static final Logger LOG = LogManager.getLogger(CustomViewBusinessDelegateImpl.class);
	
	@Override
	public List<CustomViewDTO> getCustomView(CustomViewDTO customViewDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_CUSTOMVIEW_GET;
	        
        Map<String, Object> requestParameters = new HashMap<String, Object>();
		String filter = "";
		String customerId = customViewDTO.getCustomerId();
		String id = customViewDTO.getId();
		if(customerId != null && !customerId.isEmpty()) {
			filter = "customerId" + DBPUtilitiesConstants.EQUAL + customerId;
		}
		if(id != null && !id.isEmpty()) {
			if(!filter.isEmpty()) {
				filter = filter + DBPUtilitiesConstants.AND;
			}
			filter = filter + "id" + DBPUtilitiesConstants.EQUAL + id;
		}
		requestParameters.put(DBPUtilitiesConstants.FILTER, filter);
			
		List<CustomViewDTO> customViewDTOs = null;
		String customViewResponse = null;
		try {
			customViewResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			JSONObject responseObj = new JSONObject(customViewResponse);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
			customViewDTOs = JSONUtils.parseAsList(jsonArray.toString(), CustomViewDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch custom views from table: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at getCustomViews: " + e);
			return null;
		}
		
		return customViewDTOs;
	}

	@Override
	public CustomViewDTO createCustomView(CustomViewDTO customViewDTO) {

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_CUSTOMVIEW_CREATE;

		Map<String, Object> requestParameters =  new HashMap<String, Object>();
		
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(customViewDTO).toString(), String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: " + e);
			return null;
		}
		
		String response = null;
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			JSONObject responseObj = new JSONObject(response);
			JSONArray responseArray = responseObj.getJSONArray(Constants.CUSTOMVIEW);
			customViewDTO = JSONUtils.parse(responseArray.getJSONObject(0).toString(), CustomViewDTO.class);
		} catch (JSONException e) {
			LOG.error("Unable to Create Custom View: " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at createCustomView method: " + e);
			return null;
		}

		return customViewDTO;
	}

	@Override
	public boolean deleteCustomView(String id) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_CUSTOMVIEW_DELETE;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("id", id);
		
		try {
			String deleteResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject jsonRsponse = new JSONObject(deleteResponse);
			if(jsonRsponse.getInt("opstatus") == 0 && jsonRsponse.getInt("httpStatusCode") == 0 && jsonRsponse.getInt("deletedRecords") == 1) {
				return true;
			}
		}
		
		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while deleting the custom view",jsonExp);
			return false;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while deleting the custom view",exp);
			return false;
		}
		
		return false;	
	}

	@Override
	public CustomViewDTO editCustomView(CustomViewDTO customViewDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_CUSTOMVIEW_EDIT;

		Map<String, Object> requestParameters =  new HashMap<String, Object>();
		
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(customViewDTO).toString(), String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: " + e);
			return null;
		}
		
		String response = null;
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			JSONObject responseObj = new JSONObject(response);
			JSONArray responseArray = responseObj.getJSONArray(Constants.CUSTOMVIEW);
			customViewDTO = JSONUtils.parse(responseArray.getJSONObject(0).toString(), CustomViewDTO.class);
		} catch (JSONException e) {
			LOG.error("Unable to edit Custom View: " + e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at editCustomView method: " + e);
			return null;
		}

		return customViewDTO;
	}

}
