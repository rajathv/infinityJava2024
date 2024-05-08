package com.temenos.dbx.product.payeeservices.resource.impl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.BackendDelegateFactory;
import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.BillPayPayeeBackendDelegate;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.BillPayPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.dto.BillPayPayeeBackendDTO;
import com.temenos.dbx.product.payeeservices.dto.BillPayPayeeDTO;
import com.temenos.dbx.product.payeeservices.resource.api.BillPayPayeeResource;

public class BillPayPayeeResourceImpl implements BillPayPayeeResource {

	private static final Logger LOG = LogManager.getLogger(BillPayPayeeResourceImpl.class);
	
	BillPayPayeeBusinessDelegate billPayPayeeDelegate = DBPAPIAbstractFactoryImpl.getInstance()
			.getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(BillPayPayeeBusinessDelegate.class);
	
	BillPayPayeeBackendDelegate billPayPayeeBackendDelegate = DBPAPIAbstractFactoryImpl.getInstance()
			.getFactoryInstance(BackendDelegateFactory.class).getBackendDelegate(BillPayPayeeBackendDelegate.class);
	
	AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
			.getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
	
	@Override
	public Result fetchAllMyPayees(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		//TODO - Fetch cif of logged user 
		//String userId = CustomerSession.getCustomerId(customer);
		String legalEntityId = (String) customer.get("legalEntityId");
		//To get cifs which are authorized for the user
		List<String> featureActions = new ArrayList<String>();
		featureActions.add(FeatureAction.BILL_PAY_VIEW_PAYEES);
		Set<String> authorizedCifs = authorizationChecksBusinessDelegate.getUserAuthorizedCifsForFeatureAction(featureActions, request.getHeaderMap(), request);
		if(authorizedCifs == null || authorizedCifs.isEmpty()) {
			LOG.error("The logged in user doesn't have permission to perform this action");
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		
		//To fetch payees for authorized cifs
		List<BillPayPayeeDTO> billPayPayeeDTOs = billPayPayeeDelegate.fetchPayeesFromDBX(authorizedCifs, legalEntityId);
		if(billPayPayeeDTOs == null) {
			LOG.error("Error occurred while fetching payees from dbx ");
			return ErrorCodeEnum.ERR_12057.setErrorCode(new Result());
		}
		if(billPayPayeeDTOs.isEmpty()) {
			LOG.error("No Payees Found");
	        return JSONToResult.convert(new JSONObject().put(Constants.PAYEE, new JSONArray()).toString());
		}
		
		//Getting unique payees with comma seperated cif list
		billPayPayeeDTOs = _getUniquePayees(billPayPayeeDTOs);
		
		//Getting a list of unique payeeIds
		Set<String> payeeIds = billPayPayeeDTOs.stream().map(BillPayPayeeDTO::getPayeeId).collect(Collectors.toSet());
		
		//Fetching backend records using payeeId
		List<BillPayPayeeBackendDTO> billPayPayeeBackendDTOs = billPayPayeeBackendDelegate.fetchPayees(payeeIds, request.getHeaderMap(), request);
		if(billPayPayeeBackendDTOs == null) {
			LOG.error("Error occurred while fetching payees from backend");
			return ErrorCodeEnum.ERR_12058.setErrorCode(new Result());
		}
		
		if(billPayPayeeBackendDTOs.size() == 0){
			LOG.error("No Payees Found");
	        return JSONToResult.convert(new JSONObject().put(Constants.PAYEE, new JSONArray()).toString());
        }
		
		if(billPayPayeeBackendDTOs.get(0).getDbpErrMsg() != null && !billPayPayeeBackendDTOs.get(0).getDbpErrMsg().isEmpty()) {
			return ErrorCodeEnum.ERR_00000.setErrorCode(result, billPayPayeeBackendDTOs.get(0).getDbpErrMsg());
		}
		
		//Merge bankend payees with dbx payees using Id and payeeId fields and populating cif information from dbx payee
		billPayPayeeBackendDTOs = (new FilterDTO()).merge(billPayPayeeBackendDTOs, billPayPayeeDTOs, "id=payeeId", "cif");
		if(billPayPayeeBackendDTOs == null) {
			LOG.error("Error occurred while merging payee details");
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		//Applying filters like offset,limit,sort etc
		FilterDTO filterDTO = new FilterDTO();
		try {
			filterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ",e);
            return ErrorCodeEnum.ERR_21220.setErrorCode(new Result());
		}
		billPayPayeeBackendDTOs = filterDTO.filter(billPayPayeeBackendDTOs);
		
		try {
	        JSONArray records = new JSONArray(billPayPayeeBackendDTOs);
	        JSONObject resultObject = new JSONObject();
	        resultObject.put(Constants.PAYEE,records);
	        result = JSONToResult.convert(resultObject.toString());
		}
        catch(Exception exp) {
            LOG.error("Exception occurred while converting DTO to result: ",exp);
            return ErrorCodeEnum.ERR_12048.setErrorCode(new Result());
        }
		
		return result;
	}
	
	@Override
	public Result createPayee(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String userId = CustomerSession.getCustomerId(customer);
		String legalEntityId = (String) customer.get("legalEntityId");
		Map<String, List<String>> sharedCifMap = new HashMap<String, List<String>>();
		String sharedCifs = (String) inputParams.get("cif");
		List<String> featureActions = new ArrayList<String>();
		featureActions.add(FeatureAction.BILL_PAY_CREATE_PAYEES);
		if(StringUtils.isBlank(sharedCifs)) {
			sharedCifMap = authorizationChecksBusinessDelegate.getAuthorizedCifs(featureActions, request.getHeaderMap(), request);
			if(sharedCifMap == null || sharedCifMap.isEmpty()) {
				LOG.error("The logged in user doesn't have permission to perform this action");
				return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}
		}
		else {
			sharedCifMap = _getContractCifMap(sharedCifs);
					
			//Authorization for all the cifs present in input for which the payee needs to be shared
			if(!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(featureActions, sharedCifMap, request.getHeaderMap(), request)) {
				LOG.error("The logged in user doesn't have permission to perform this action");
				return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}
		}
		
		BillPayPayeeBackendDTO billPayPayeeBackendDTO = new BillPayPayeeBackendDTO();
		try {
			billPayPayeeBackendDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), BillPayPayeeBackendDTO.class);
			billPayPayeeBackendDTO.setUserId(userId);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: " + e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}
		
		if(!billPayPayeeBackendDTO.isValidInput()) {
			LOG.error("Not a valid input");
			return ErrorCodeEnum.ERR_10710.setErrorCode(new Result());
		}
		
		if(StringUtils.isBlank(billPayPayeeBackendDTO.getName())) {
			billPayPayeeBackendDTO.setName(billPayPayeeBackendDTO.getCompanyName());
		}
		
		if(StringUtils.isBlank(billPayPayeeBackendDTO.getName()) || billPayPayeeBackendDTO.getAccountNumber() == null) {
			return ErrorCodeEnum.ERR_10348.setErrorCode(new Result());
		}
		
		if(!_isUniquePayee(request, billPayPayeeBackendDTO, sharedCifMap))
		{
			LOG.error("Duplicate Payee or Error occured while checking for uniqueness.");
			return ErrorCodeEnum.ERR_12062.setErrorCode(new Result());
		}
		
		//Create one payee at the backend
		billPayPayeeBackendDTO = billPayPayeeBackendDelegate.createPayee(billPayPayeeBackendDTO, request.getHeaderMap(), request);
		if(billPayPayeeBackendDTO == null) {
			LOG.error("Error occured while creating payee at backend");
			return ErrorCodeEnum.ERR_12053.setErrorCode(new Result());
		}
		
		if(billPayPayeeBackendDTO.getDbpErrMsg() != null && !billPayPayeeBackendDTO.getDbpErrMsg().isEmpty()) {
			return ErrorCodeEnum.ERR_00000.setErrorCode(result, billPayPayeeBackendDTO.getDbpErrMsg());
		}
		
		//Getting payeeId of created payee
		String payeeId = billPayPayeeBackendDTO.getId();
		if(payeeId == null || payeeId.isEmpty()) {
			LOG.error("PayeeId is empty or null");
			return ErrorCodeEnum.ERR_12053.setErrorCode(result);
		}
		
		BillPayPayeeDTO billPayPayeeDTO = new BillPayPayeeDTO();
		billPayPayeeDTO.setPayeeId(payeeId);
		billPayPayeeDTO.setCreatedBy(userId);
		billPayPayeeDTO.setLegalEntityId(legalEntityId);
		//Creating payee and cif mappings at dbx table
		for (Map.Entry<String,List<String>> contractCif : sharedCifMap.entrySet())  {
			billPayPayeeDTO.setContractId((String) contractCif.getKey());
            List<String> coreCustomerIds = contractCif.getValue(); 
            for(int j = 0; j < coreCustomerIds.size(); j++) {
				billPayPayeeDTO.setCif(coreCustomerIds.get(j));
				billPayPayeeDelegate.createPayeeAtDBX(billPayPayeeDTO);
			}
    	} 
	
		try {
			JSONObject requestObj = new JSONObject(billPayPayeeBackendDTO);
			result = JSONToResult.convert(requestObj.toString());
		} catch (JSONException e) {
			LOG.error("Error occured while converting the response to Result: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}
	
	@Override
	public Result editPayee(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();
		
		//Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		//String userId = CustomerSession.getCustomerId(customer);
		
		String payeeId =  (String) inputParams.get("payeeId");
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String legalEntityId = (String) customer.get("legalEntityId");
		if(StringUtils.isBlank(payeeId)) {
			LOG.error("Missing payeeId");
			return ErrorCodeEnum.ERR_12054.setErrorCode(new Result());
		}
		
		String featureAction = FeatureAction.BILL_PAY_CREATE_PAYEES;
		String eBillEnable = null;
		if(inputParams.get("EBillEnable") != null) {
			eBillEnable = inputParams.get("EBillEnable").toString();
		}
		if(eBillEnable != null) {
			featureAction = FeatureAction.BILL_PAY_ACTIVATE_OR_DEACTIVATE_EBILL;
		}
		
		Map<String, List<String>> editedCifsMap = new HashMap<String, List<String>>();
		Map<String, List<String>> removedCifsMap = new HashMap<String, List<String>>();
		Map<String, List<String>> addedCifsMap = new HashMap<String, List<String>>();
		Map<String, List<String>> associatedCifsMap = new HashMap<String, List<String>>();
		String editedCifs = (String) inputParams.get("cif");
		if(StringUtils.isBlank(editedCifs)) {
			inputParams.put("cif", "{}");
			editedCifsMap = authorizationChecksBusinessDelegate.getAuthorizedCifs(Arrays.asList(featureAction), request.getHeaderMap(), request);
			if(editedCifsMap == null || editedCifsMap.isEmpty()) {
				LOG.error("The logged in user doesn't have permission to perform this action");
				return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}
		}
		else {
			editedCifsMap = _getContractCifMap(editedCifs);
			//Authorization check for cifs which are edited
			if(!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(Arrays.asList(featureAction), editedCifsMap, request.getHeaderMap(), request)) {
				LOG.error("The logged in user doesn't have permission to perform this action");
				return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
			}
		}
		
		//Fetch payees using payeeId from dbx
		BillPayPayeeDTO billPayPayeeDTO = new BillPayPayeeDTO();
		billPayPayeeDTO.setPayeeId(payeeId);
		billPayPayeeDTO.setLegalEntityId(legalEntityId);
		List<BillPayPayeeDTO> billPayPayeeDTOs = billPayPayeeDelegate.fetchPayeeByIdAtDBX(billPayPayeeDTO);
		if(billPayPayeeDTOs == null) {
			LOG.error("Failed to fetch payee");
			return ErrorCodeEnum.ERR_12057.setErrorCode(new Result());
		}
		
		if(billPayPayeeDTOs.isEmpty()) {
			LOG.error("Records doesn't exist");
			return ErrorCodeEnum.ERR_12606.setErrorCode(new Result());
		}
		
		Set<String> payeeIds = new HashSet<String>();
		payeeIds.add(payeeId);
		//Fetching backend records using payeeId
		List<BillPayPayeeBackendDTO> backendDTOs = billPayPayeeBackendDelegate.fetchPayees(payeeIds, request.getHeaderMap(), request);
		if(backendDTOs == null) {
			LOG.error("Error occurred while fetching payees from backend");
			return ErrorCodeEnum.ERR_12058.setErrorCode(new Result());
		}
		
		if(backendDTOs.size() == 0){
			LOG.error("No Payees Found");
			return ErrorCodeEnum.ERR_12606.setErrorCode(new Result());
        }
		
		if(backendDTOs.get(0).getDbpErrMsg() != null && !backendDTOs.get(0).getDbpErrMsg().isEmpty()) {
			return ErrorCodeEnum.ERR_00000.setErrorCode(result, backendDTOs.get(0).getDbpErrMsg());
		}
		
		associatedCifsMap = _getAssociatedCifsMap(billPayPayeeDTOs, request);
		if(StringUtils.isNotBlank(editedCifs)) {
			_setAddedAndRemovedCifMaps(editedCifsMap, addedCifsMap, removedCifsMap, associatedCifsMap);
			if(addedCifsMap.isEmpty() && removedCifsMap.isEmpty()) {
				editedCifs = null;
			}
		}
		
		String createdBy = billPayPayeeDTOs.get(0).getCreatedBy();
		BillPayPayeeBackendDTO billPayPayeeBackendDTO = new BillPayPayeeBackendDTO();
		try {
			billPayPayeeBackendDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), BillPayPayeeBackendDTO.class);
			billPayPayeeBackendDTO.setUserId(createdBy);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: " + e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}
		
		if(!billPayPayeeBackendDTO.isValidInput()) {
			LOG.error("Not a valid input");
			return ErrorCodeEnum.ERR_10710.setErrorCode(new Result());
		}
		
		if(!_isUniquePayeeForEdit(billPayPayeeBackendDTO, backendDTOs.get(0), addedCifsMap, associatedCifsMap, editedCifsMap, editedCifs, request)) {
			LOG.error("Duplicate Payee or Error occured while checking for uniqueness.");
			return ErrorCodeEnum.ERR_12062.setErrorCode(new Result());
		}
		
		inputParams.values().removeAll(Collections.singleton(null));
		if(inputParams.size() > 2) {
			//Edit backend payee details
			billPayPayeeBackendDTO = billPayPayeeBackendDelegate.editPayee(billPayPayeeBackendDTO, request.getHeaderMap(), request);
			if(billPayPayeeBackendDTO == null) {
				LOG.error("Error occured while updating payee at backend");
				return ErrorCodeEnum.ERR_12055.setErrorCode(new Result());
			}
			
			if(billPayPayeeBackendDTO.getDbpErrMsg() != null && !billPayPayeeBackendDTO.getDbpErrMsg().isEmpty()) {
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, billPayPayeeBackendDTO.getDbpErrMsg());
			}
		}
		else {
			billPayPayeeBackendDTO.setId(payeeId);
		}
		
		//deleting mappings from dbx table which are present in removedCifsMap
		for (Map.Entry<String,List<String>> contractCif : removedCifsMap.entrySet()) {
			billPayPayeeDTO.setContractId((String) contractCif.getKey());
            List<String> coreCustomerIds = contractCif.getValue(); 
            for(int j = 0; j < coreCustomerIds.size(); j++) {
				billPayPayeeDTO.setCif(coreCustomerIds.get(j));
				billPayPayeeDelegate.deletePayeeAtDBX(billPayPayeeDTO);
			}
    	} 
		
		//adding mappings at dbx table which are present in addedCifsMap
		billPayPayeeDTO.setCreatedBy(createdBy);
		for (Map.Entry<String,List<String>> contractCif : addedCifsMap.entrySet()) {
			billPayPayeeDTO.setContractId((String) contractCif.getKey());
            List<String> coreCustomerIds = contractCif.getValue(); 
            for(int j = 0; j < coreCustomerIds.size(); j++) {
				billPayPayeeDTO.setCif(coreCustomerIds.get(j));
				billPayPayeeDelegate.createPayeeAtDBX(billPayPayeeDTO);
			}
    	} 
		
		try {
			JSONObject requestObj = new JSONObject(billPayPayeeBackendDTO);
			result = JSONToResult.convert(requestObj.toString());
		} catch (JSONException e) {
			LOG.error("Error occured while converting the response to Result: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}

	@Override
	public Result deletePayee(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		Result result = new Result();		
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		//String userId = CustomerSession.getCustomerId(customer);
		String legalEntityId = (String) customer.get("legalEntityId");
		
		String payeeId = null;
		if(inputParams.get("payeeId") != null) {
			payeeId = inputParams.get("payeeId").toString();
		}
		
		if(payeeId == null || payeeId.isEmpty()) {
			LOG.error("Missing payeeId");
			return ErrorCodeEnum.ERR_12054.setErrorCode(new Result());
		}
		
		// Authorization check for cifs for which the payee is deleted
		Set<String> authorizedCifs = authorizationChecksBusinessDelegate.getUserAuthorizedCifsForFeatureAction(Arrays.asList(FeatureAction.BILL_PAY_DELETE_PAYEES), request.getHeaderMap(), request);
		if(authorizedCifs == null || authorizedCifs.isEmpty()) {
			LOG.error("The logged in user doesn't have permission to perform this action");
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		
		//Checking if payee exists with that payeeId or not
		BillPayPayeeDTO billPayPayeeDTO = new BillPayPayeeDTO();
		billPayPayeeDTO.setPayeeId(payeeId);
		billPayPayeeDTO.setLegalEntityId(legalEntityId);
		
		List<BillPayPayeeDTO> billPayPayeeDTOs = billPayPayeeDelegate.fetchPayeeByIdAtDBX(billPayPayeeDTO);
		if(billPayPayeeDTOs == null) {
			LOG.error("Failed to fetch payee");
			return ErrorCodeEnum.ERR_12057.setErrorCode(new Result());
		}
		
		if(billPayPayeeDTOs.isEmpty()) {
			LOG.error("Records doesn't exist");
			return ErrorCodeEnum.ERR_12606.setErrorCode(new Result());
		}

		//Need to uncomment if we have to check permission for all the cifs associated with that payeeId
		/*
		Map<String, List<String>> removedCifsMap = new HashMap<String, List<String>>();
		Map<String, List<String>> addedCifsMap = new HashMap<String, List<String>>();
		_setAddedAndRemovedCifMaps(billPayPayeeDTOs, deletedCifsMap, addedCifsMap, removedCifsMap);
		if(!addedCifsMap.isEmpty() || removedCifsMap.isEmpty()) {
			LOG.error("The logged in user doesn't have permission to perform this action");
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		*/
		
		Set<String> cifs = billPayPayeeDTOs.stream().map(BillPayPayeeDTO::getCif).collect(Collectors.toSet());
		cifs.retainAll(authorizedCifs);
		if(cifs.isEmpty()) {
			LOG.error("The logged in user doesn't have permission to perform this action");
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		
		//Deleting payee at the backend
		BillPayPayeeBackendDTO billPayPayeeBackendDTO = new BillPayPayeeBackendDTO();
		try {
			billPayPayeeBackendDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), BillPayPayeeBackendDTO.class);
			billPayPayeeBackendDTO.setUserId(billPayPayeeDTOs.get(0).getCreatedBy());
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: " + e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}
		billPayPayeeBackendDTO = billPayPayeeBackendDelegate.deletePayee(billPayPayeeBackendDTO, request.getHeaderMap(), request);
		if(billPayPayeeBackendDTO == null) {
			LOG.error("Error occured while deleting payee at backend");
			return ErrorCodeEnum.ERR_12056.setErrorCode(new Result());
		}
		
		if(billPayPayeeBackendDTO.getDbpErrMsg() != null && !billPayPayeeBackendDTO.getDbpErrMsg().isEmpty()) {
			return ErrorCodeEnum.ERR_00000.setErrorCode(result, billPayPayeeBackendDTO.getDbpErrMsg());
		}
		
		
		//Deleting payee at dbx
		billPayPayeeDelegate.deletePayeeAtDBX(billPayPayeeDTO);
		
		//need to uncomment if we want to delete partially based on authorization
		/*
		for (Map.Entry<String,List<String>> contractCif : removedCifsMap.entrySet()) {
			billPayPayeeDTO.setContractId((String) contractCif.getKey());
            List<String> coreCustomerIds = contractCif.getValue(); 
            for(int j = 0; j < coreCustomerIds.size(); j++) {
				billPayPayeeDTO.setCif(coreCustomerIds.get(j));
				billPayPayeeDelegate.deletePayeeAtDBX(billPayPayeeDTO);
			}
    	}
    	*/ 
		
		try {
			JSONObject requestObj = new JSONObject(billPayPayeeBackendDTO);
			result = JSONToResult.convert(requestObj.toString());
		} catch (JSONException e) {
			LOG.error("Error occured while converting the response to Result: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}
	
	/*
	 * Method to set added and removed cifs into map with contract and cif information by comparing with already existing cifs which are fetched from dbx table
	 */
	private void _setAddedAndRemovedCifMaps(Map<String, List<String>> inputCifsMap, Map<String, List<String>> addedCifsMap, 
			Map<String, List<String>> removedCifsMap, Map<String, List<String>> associatedCifsMap) {
		
		for (Map.Entry<String,List<String>> contractCif : inputCifsMap.entrySet())  {
			if(associatedCifsMap.containsKey(contractCif.getKey()))
			{
				List<String> associatedCifs = associatedCifsMap.get(contractCif.getKey());
				List<String> inputCifs = contractCif.getValue();
				
				List<String> addedCifs = new ArrayList<>(inputCifs);
				addedCifs.removeAll(associatedCifs);
				if(addedCifs.size()> 0) {
					addedCifsMap.put(contractCif.getKey(), addedCifs);
				}
				
				List<String> removedCifs = new ArrayList<>(associatedCifs);
				removedCifs.removeAll(inputCifs);
				if(removedCifs.size()> 0) {
					removedCifsMap.put(contractCif.getKey(), removedCifs);
				}
				
			}
			else {
				addedCifsMap.put(contractCif.getKey(), contractCif.getValue());
			}
    	}
		
		for (Map.Entry<String,List<String>> associatedcontractCif : associatedCifsMap.entrySet()) {
            if(!(inputCifsMap.containsKey(associatedcontractCif.getKey()))) {
                removedCifsMap.put(associatedcontractCif.getKey(),associatedcontractCif.getValue());
            }           
        }
		
	}
	
	/*
	 * Method to set added and removed cifs into map with contract and cif information by comparing with already existing cifs which are fetched from dbx table
	 */
	private Map<String, List<String>> _getAssociatedCifsMap(List<BillPayPayeeDTO> payeeDTOs, DataControllerRequest request) {
		Set<String> authorizedCifs = authorizationChecksBusinessDelegate.getUserAuthorizedCifsForFeatureAction(Arrays.asList(FeatureAction.BILL_PAY_CREATE_PAYEES), request.getHeaderMap(), request);
		
		Map<String, List<String>> associatedCifsMap = new HashMap<String, List<String>>();
		for(int i = 0; i < payeeDTOs.size(); i++) {
			if(!authorizedCifs.contains(payeeDTOs.get(i).getCif())) {
				continue;
			}
			if(associatedCifsMap.containsKey(payeeDTOs.get(i).getContractId())) {
				List<String> cifs = new ArrayList<>(associatedCifsMap.get(payeeDTOs.get(i).getContractId()));
				cifs.add(payeeDTOs.get(i).getCif());
				associatedCifsMap.put(payeeDTOs.get(i).getContractId(), cifs);
			}
			else {
				associatedCifsMap.put(payeeDTOs.get(i).getContractId(), Arrays.asList(payeeDTOs.get(i).getCif().split(",")));
			}
		}
		
		return associatedCifsMap;
	}
	
	/*
	 * Method to get contract cif list map from input cifs
	 */
	private Map<String, List<String>> _getContractCifMap(String inputCifs) {
		Map<String, List<String>> contractCifMap = new HashMap<String, List<String>>();
        /*
         "sharedCifs": [
        	{
            "contractId": "1324",
            "coreCustomerId":  "1,2"
        	},
        	{
            "contractId": "1234",
            "coreCustomerId":  "3,4"
        	}
			]
         */
        JsonParser jsonParser = new JsonParser();
        JsonArray contractCifArray = (JsonArray) jsonParser.parse(inputCifs);
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Gson gson = new Gson();
        if (contractCifArray.isJsonArray()) {
            for (int i = 0; i < contractCifArray.size(); i++) {
                @SuppressWarnings("unchecked")
                Map<String, String> contractObject = (Map<String, String>) gson.fromJson(contractCifArray.get(i), type);
                if(StringUtils.isNotBlank(contractObject.get("coreCustomerId"))) {
                	List<String> coreCustomerIds = Arrays.asList(contractObject.get("coreCustomerId").split(","));
                    contractCifMap.put(contractObject.get("contractId"), coreCustomerIds);
                }
            }
        }
        return contractCifMap;
        /*result will be
         {
         "1324": ["1","2"],
         "1234": ["3","4"]
         }
         */
	}
	
	/*
	 * Method to get unique payees based on payeeId and setting comma seperated cif for shared payees
	 */
	private List<BillPayPayeeDTO> _getUniquePayees(List<BillPayPayeeDTO> billPayPayeeDTOs) {
		Map<String, HashMap<String,String>> payeeMap = new HashMap<>();
		
		for(BillPayPayeeDTO payee: billPayPayeeDTOs){
			if(payeeMap.containsKey(payee.getPayeeId())) {
				HashMap<String,String> cifMap = payeeMap.get(payee.getPayeeId());
				String cifIds = payee.getCif();
				if(cifMap.containsKey(payee.getContractId())) {
					cifIds = cifMap.get(payee.getContractId()) + "," + payee.getCif();
				}
				cifMap.put(payee.getContractId(), cifIds);
				payeeMap.put(payee.getPayeeId(), cifMap);
			}
			else {
				HashMap<String,String> cifMap = new HashMap<String,String>();
				cifMap.put(payee.getContractId(), payee.getCif());
				payeeMap.put(payee.getPayeeId(), cifMap);
			}
		}
		
		for(int i = 0; i < billPayPayeeDTOs.size(); i++){
			BillPayPayeeDTO payee = billPayPayeeDTOs.get(i);
			if(payeeMap.containsKey(payee.getPayeeId())) {
				HashMap<String,String> cifMap = payeeMap.get(payee.getPayeeId());
				JSONArray cifArray = new JSONArray();
				int noOfCustomersLinked = 0;
				for (Map.Entry<String,String> entry : cifMap.entrySet()) {
					JSONObject cifObj = new JSONObject();
					cifObj.put("contractId", entry.getKey());
					cifObj.put("coreCustomerId", entry.getValue());
					noOfCustomersLinked += entry.getValue().split(",").length;
					cifArray.put(cifObj);
				}
				payee.setCif(cifArray.toString());
				payee.setNoOfCustomersLinked(Integer.toString(noOfCustomersLinked));
				payeeMap.remove(payee.getPayeeId());
			}
			else {
				billPayPayeeDTOs.remove(i);
				i--;
			}
		}
		return billPayPayeeDTOs;
	}
	
	/*
	 * Method to check if payee details are unique for given cifs
	 */
	private boolean _isUniquePayee(DataControllerRequest request, BillPayPayeeBackendDTO inputDTO, Map<String, List<String>> cifMap) {
		
		if(StringUtils.isBlank(inputDTO.getAccountNumber())){
			return true;
		}
		
		Set<String> inputCifs = new HashSet<>();
		for(Map.Entry<String, List<String>> map : cifMap.entrySet()){
			inputCifs.addAll(map.getValue());
		}
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String legalEntityId = (String) customer.get("legalEntityId");
		
		List<BillPayPayeeDTO> payeeDTOs = billPayPayeeDelegate.fetchPayeesFromDBX(inputCifs, legalEntityId);
		if(payeeDTOs == null) {
			LOG.error("Error occurred while fetching payees from dbx ");
			return false;
		}
		if(payeeDTOs.isEmpty()) {
			LOG.error("No Payees Found");
	        return true;
		}
		
		Set<String> payeeIds = payeeDTOs.stream().map(BillPayPayeeDTO::getPayeeId).distinct().collect(Collectors.toSet());
		
		List<BillPayPayeeBackendDTO> backendDTOs = billPayPayeeBackendDelegate.fetchPayees(payeeIds, request.getHeaderMap(), request);
		if(backendDTOs == null) {
			LOG.error("Error occurred while fetching payees from backend");
			return false;
		}
		
		if(backendDTOs.size() == 0){
			LOG.error("No Payees Found");
	        return true;
        }
		
		if(backendDTOs.get(0).getDbpErrMsg() != null && !backendDTOs.get(0).getDbpErrMsg().isEmpty()) {
			return false;
		}
		
		String inputAccountNumber = inputDTO.getAccountNumber();
		for(int i = 0; i < backendDTOs.size(); i++) {
			if(inputAccountNumber.equals(backendDTOs.get(i).getAccountNumber())) {
				return false;
			}
		}
		
		return true;
	}
	
	private boolean _isUniquePayeeForEdit(BillPayPayeeBackendDTO inputDTO, BillPayPayeeBackendDTO backendDTO, 
			Map<String, List<String>> addedCifsMap, Map<String, List<String>> associatedCifsMap, 
			Map<String, List<String>> editedCifsMap, String editedCifs, DataControllerRequest request) {
		boolean validationFieldsChanged = false;
		
		String backendAccountNumber = backendDTO.getAccountNumber();
		if(StringUtils.isBlank(inputDTO.getAccountNumber()) || inputDTO.getAccountNumber().equals(backendAccountNumber)) {
			inputDTO.setAccountNumber(backendAccountNumber);
			validationFieldsChanged = false;
		}
		else {
			validationFieldsChanged = true;
		}
		
		
		if(validationFieldsChanged) {
			if(StringUtils.isBlank(editedCifs)) {
				if(!associatedCifsMap.isEmpty() && !_isUniquePayee(request, inputDTO, associatedCifsMap))
				{
					return false;
				}
			}
			else {
				if(!editedCifsMap.isEmpty() && !_isUniquePayee(request, inputDTO, editedCifsMap))
				{
					return false;
				}
			}
			
		}
		else {
			if(StringUtils.isNotBlank(editedCifs)) {
				if(!addedCifsMap.isEmpty()  && !_isUniquePayee(request, inputDTO, addedCifsMap))
				{
					return false;
				}
			}
		}
		
		return true;
	}
}
