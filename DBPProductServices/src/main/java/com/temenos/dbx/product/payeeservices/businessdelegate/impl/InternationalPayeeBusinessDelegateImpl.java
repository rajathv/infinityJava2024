package com.temenos.dbx.product.payeeservices.businessdelegate.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.InternationalPayeeBackendDelegate;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.InternationalPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.dto.InternationalPayeeDTO;

/**
 * 
 * @author 
 * @version 1.0
 * implements {@link InternationalPayeeBusinessDelegate}
 */

public class InternationalPayeeBusinessDelegateImpl implements InternationalPayeeBusinessDelegate {
	private static final Logger LOG = LogManager.getLogger(InternationalPayeeBusinessDelegateImpl.class);
	@Override
	public List<InternationalPayeeDTO> fetchPayeesFromDBX(Set<String> associatedCifs,String legalEntityId) {
		
		final Logger LOG = LogManager.getLogger(InternationalPayeeBusinessDelegateImpl.class);
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_INTERNATIONALPAYEE_GET;
	
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		String filter = "";
        for (String cif : associatedCifs) {
			if(filter.isEmpty()) 
				filter = "cif" + DBPUtilitiesConstants.EQUAL + cif;
			else
				filter = filter +  DBPUtilitiesConstants.OR +"cif" + DBPUtilitiesConstants.EQUAL + cif;
		}
        
		if (StringUtils.isNotBlank(legalEntityId)) {
			filter = DBPUtilitiesConstants.OPEN_BRACE + filter + DBPUtilitiesConstants.CLOSE_BRACE + DBPUtilitiesConstants.AND + "legalEntityId" + DBPUtilitiesConstants.EQUAL + legalEntityId;
		}
        requestParameters.put(DBPUtilitiesConstants.FILTER, filter);
		
		String payeeResponse = null;
		List<InternationalPayeeDTO> InternationalPayeeDTOs = null;
		
		try {
			payeeResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			JSONObject responseObj = new JSONObject(payeeResponse);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
			InternationalPayeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), InternationalPayeeDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch international payee Ids: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at fetch international payee Ids: " + e);
			return null;
		}
		return InternationalPayeeDTOs;
	}

	@Override
	public InternationalPayeeDTO createPayeeAtDBX(InternationalPayeeDTO InternationalPayeeDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_INTERNATIONALPAYEE_CREATE;
	        
        Map<String, Object> requestParameters;
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(InternationalPayeeDTO).toString(), String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the request params: " + e);
			return null;
		}
			
		String createResponse = null;
		try {
			createResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			
			JSONObject response = new JSONObject(createResponse);
			JSONArray responseArray = CommonUtils.getFirstOccuringArray(response);
			InternationalPayeeDTO = JSONUtils.parse(responseArray.getJSONObject(0).toString(), InternationalPayeeDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to create payee at internationalpayee table: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at createPayeeAtDBX: " + e);
			return null;
		}
		
		return InternationalPayeeDTO;
	}
	
	@Override
	public List<InternationalPayeeDTO> fetchPayeeByIdAtDBX(InternationalPayeeDTO internationalpayeeDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_INTERNATIONALPAYEE_GET;
	        
        Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "payeeId" + DBPUtilitiesConstants.EQUAL + internationalpayeeDTO.getPayeeId();
		
		if(internationalpayeeDTO.getContractId() != null) {
			filter += DBPUtilitiesConstants.AND +"contractId" + DBPUtilitiesConstants.EQUAL + internationalpayeeDTO.getContractId();
		}
		
		if(internationalpayeeDTO.getCif() != null) {
			filter += DBPUtilitiesConstants.AND +"cif" + DBPUtilitiesConstants.EQUAL + internationalpayeeDTO.getCif();
		}
		
		if(StringUtils.isNotBlank(internationalpayeeDTO.getLegalEntityId())) {
			filter += DBPUtilitiesConstants.AND +"legalEntityId" + DBPUtilitiesConstants.EQUAL + internationalpayeeDTO.getLegalEntityId();
		}
		
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
			
		List<InternationalPayeeDTO> internationalPayeeDTOs = null;
		String payeeResponse = null;
		try {
			payeeResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject responseObj = new JSONObject(payeeResponse);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
			internationalPayeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), InternationalPayeeDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch intra bank payee by id: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at fetchPayeeByIdAtDBX: " + e);
			return null;
		}
		
		return internationalPayeeDTOs;
	}

	@Override
	public boolean deletePayeeAtDBX(InternationalPayeeDTO internationalpayeeDTO) {
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_INTERNATIONALPAYEE_DELETE;
	
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "payeeId" + DBPUtilitiesConstants.EQUAL + internationalpayeeDTO.getPayeeId();
		
		if(internationalpayeeDTO.getContractId() != null) {
			filter += DBPUtilitiesConstants.AND +"contractId" + DBPUtilitiesConstants.EQUAL + internationalpayeeDTO.getContractId();
		}
		
		if(internationalpayeeDTO.getCif() != null) {
			filter += DBPUtilitiesConstants.AND +"cif" + DBPUtilitiesConstants.EQUAL + internationalpayeeDTO.getCif();
		}
		
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		
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
			LOG.error("JSONExcpetion occured while deleting the international payee",jsonExp);
			return false;
		}
		catch(Exception e) {
			LOG.error("Excpetion occured while deleting the international payee",e);
			return false;
		}
		
		return false;	
	}

	@Override
	public JSONObject validateForApprovals( DataControllerRequest request, TransactionStatusDTO payloadForValidateForApprovals ) throws ApplicationException {
		Map<String, Object> payload;
		payload = new HashMap<>();
		payload.put("confirmationNumber", payloadForValidateForApprovals.getConfirmationNumber());
		payload.put("featureActionID", payloadForValidateForApprovals.getFeatureActionID());
		payload.put("status", new JSONObject().put("status", TransactionStatusEnum.NEW.getStatus()).put("message", ""));
		payload.put("additionalMetaInfo", payloadForValidateForApprovals.getAdditionalMetaInfo());
		payload.put("contractCifMap", payloadForValidateForApprovals.getContractCifMap());
		payload.put("customerId", payloadForValidateForApprovals.getCustomerId());
		InternationalPayeeBackendDelegate internationalPayeeBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(InternationalPayeeBackendDelegate.class);
		String jsonObject = internationalPayeeBackendDelegate.validateForApprovals(request, payload);
		if(StringUtils.isBlank(jsonObject)) {
			throw new ApplicationException(ErrorCodeEnum.ERR_29018);
		}
		JSONObject transactionResponse = new JSONObject(jsonObject);
		if(0 != transactionResponse.getInt("httpStatusCode") || 0 != transactionResponse.getInt("opstatus")) {
			throw new ApplicationException(ErrorCodeEnum.ERR_29018);
		}
		return transactionResponse;
	}

	@Override
	public boolean checkIfPayeeStatusInPending(DataControllerRequest request, String payeeId ) throws ApplicationException {
		InternationalPayeeBackendDelegate internationalPayeeBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(InternationalPayeeBackendDelegate.class);
		String filterQuery = "transactionId" + DBPUtilitiesConstants.EQUAL + payeeId + DBPUtilitiesConstants.AND + "status" + DBPUtilitiesConstants.EQUAL + "Pending";
		HashMap<String, Object> requestParameters = new HashMap<>();
		requestParameters.put("$filter", filterQuery);
		String jsonString = internationalPayeeBackendDelegate.checkIfPayeeStatusInPending(request, requestParameters);
		if(jsonString == null) {
			throw new ApplicationException(ErrorCodeEnum.ERR_12612);
		}
		JSONObject obj = new JSONObject(jsonString);
		if(obj.has("bbrequest")) {
			return !obj.getJSONArray("bbrequest").isEmpty();
		}
		return false;
	}
}
