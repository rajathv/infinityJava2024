package com.temenos.infinity.api.arrangements.resource.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.temenos.infinity.api.arrangements.businessdelegate.api.CustomViewBusinessDelegate;
import com.temenos.infinity.api.arrangements.constants.Constants;
import com.temenos.infinity.api.arrangements.dto.CustomViewDTO;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.javaservice.CreateCustomViewOperation;
import com.temenos.infinity.api.arrangements.resource.api.CustomViewResource;
import com.temenos.infinity.api.arrangements.utils.CommonUtils;
import com.temenos.infinity.api.arrangements.utils.CustomerSession;

public class CustomViewResourceImpl implements CustomViewResource {
	private static final Logger LOG = LogManager.getLogger(CreateCustomViewOperation.class);

	CustomViewBusinessDelegate customViewBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomViewBusinessDelegate.class);
	
	@Override
	public Result getCustomView(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
        // permission check before fetching custom view
        List<String> featureActionIdList = new ArrayList<>();
        featureActionIdList.add("CUSTOM_VIEW_MANAGE");
        String featureActionId = CustomerSession.getPermittedActionIds(request, featureActionIdList);
        if (featureActionId == null) {
            return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
        }
        
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);
        String id = null;
		if(inputParams.get("id") != null) {
			id = inputParams.get("id").toString();
		}
		
		CustomViewDTO customFilterDTO = new CustomViewDTO();
		customFilterDTO.setId(id);
		customFilterDTO.setCustomerId(customerId);
		
		List<CustomViewDTO> customViewDTOs = customViewBusinessDelegate.getCustomView(customFilterDTO);
		if(customViewDTOs == null) {
			LOG.error("Error occurred while fetching custom views");
			return ErrorCodeEnum.ERR_12515.setErrorCode(new Result());
		}
		
		if(customViewDTOs.size() == 0){
			LOG.error("No custom views Found");
	        return JSONToResult.convert(new JSONObject().put(Constants.CUSTOMVIEW, new JSONArray()).toString());
        }
		
		try {
	        JSONArray records = new JSONArray(customViewDTOs);
	        JSONObject resultObject = new JSONObject();
	        resultObject.put(Constants.CUSTOMVIEW,records);
	        result = JSONToResult.convert(resultObject.toString());
		}
        catch(Exception exp) {
            LOG.error("Exception occurred while converting DTO to result: ",exp);
            return ErrorCodeEnum.ERR_12515.setErrorCode(new Result());
        }
		
		return result;
	}

	@Override
	public Result createCustomView(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

        // permission check before creating custom view
        List<String> featureActionIdList = new ArrayList<>();
        featureActionIdList.add("CUSTOM_VIEW_MANAGE");
        String featureActionId = CustomerSession.getPermittedActionIds(request, featureActionIdList);
        if (featureActionId == null) {
            return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
        }
        
		Result result = new Result();
		CustomViewDTO customViewDTO = new CustomViewDTO();
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);
		String companyId = CommonUtils.getCompanyId(request);
		inputParams.put("legalEntityId", companyId);
		String accountIds = (String) inputParams.get("accountIds");
		if (accountIds.isEmpty()) {
			return ErrorCodeEnum.ERR_12002.setErrorCode(new Result());
		}		
		try {
			customViewDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), CustomViewDTO.class);
			customViewDTO.setCustomerId(customerId);
		} catch (IOException e) {
			LOG.error("Caught exception while converting input params to DTO: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		customViewDTO = customViewBusinessDelegate.createCustomView(customViewDTO);
		if(customViewDTO == null) {
			LOG.error("Failed to create custom view: ");
			return ErrorCodeEnum.ERR_12513.setErrorCode(new Result());
		}
		
		result = JSONToResult.convert(new JSONObject(customViewDTO).toString()); 
		return result;
	}

	@Override
	public Result deleteCustomView(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
        // permission check before deleting custom view
        List<String> featureActionIdList = new ArrayList<>();
        featureActionIdList.add("CUSTOM_VIEW_MANAGE");
        String featureActionId = CustomerSession.getPermittedActionIds(request, featureActionIdList);
        if (featureActionId == null) {
            return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
        }
        
		Result result = new Result();
		CustomViewDTO customViewDTO = new CustomViewDTO();
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);
		
		try {
			customViewDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), CustomViewDTO.class);
			customViewDTO.setCustomerId(customerId);
		} catch (IOException e) {
			LOG.error("Caught exception while converting input params to DTO: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		//Checking whether custom view with given id is present or not for given customerId
		
		List<CustomViewDTO> customViewGetDTO = customViewBusinessDelegate.getCustomView(customViewDTO);
		if(customViewGetDTO == null || customViewGetDTO.size() == 0) {
			LOG.error("Failed to get custom view: ");
			//TODO - Error code for get failure
			return ErrorCodeEnum.ERR_12515.setErrorCode(new Result());
		}
		
		
		if(!customViewBusinessDelegate.deleteCustomView(customViewDTO.getId())) {
			LOG.error("Failed to delete custom view: ");
			return ErrorCodeEnum.ERR_12514.setErrorCode(new Result());
		}
		
		result = JSONToResult.convert(new JSONObject(customViewDTO).toString()); 
		return result;
	}

	@Override
	public Result editCustomView(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

        // permission check before updating custom view
        List<String> featureActionIdList = new ArrayList<>();
        featureActionIdList.add("CUSTOM_VIEW_MANAGE");
        String featureActionId = CustomerSession.getPermittedActionIds(request, featureActionIdList);
        if (featureActionId == null) {
            return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
        }
        
		Result result = new Result();
		CustomViewDTO customViewDTO = new CustomViewDTO();
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);
	    String id = null;
		if(inputParams.get("id") != null) {
			id = inputParams.get("id").toString();
		}
		try {
			customViewDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), CustomViewDTO.class);
			customViewDTO.setCustomerId(customerId);
			customViewDTO.setId(id);
		} catch (IOException e) {
			LOG.error("Caught exception while converting input params to DTO: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		customViewDTO = customViewBusinessDelegate.editCustomView(customViewDTO);
		if(customViewDTO == null) {
			LOG.error("Failed to edit custom view: ");
			return ErrorCodeEnum.ERR_12516.setErrorCode(new Result());
		}
		
		result = JSONToResult.convert(new JSONObject(customViewDTO).toString()); 
		return result;
	}

}
