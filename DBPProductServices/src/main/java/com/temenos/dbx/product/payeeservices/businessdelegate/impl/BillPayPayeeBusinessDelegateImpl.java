package com.temenos.dbx.product.payeeservices.businessdelegate.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.temenos.dbx.product.payeeservices.businessdelegate.api.BillPayPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.dto.BillPayPayeeDTO;

/**
 * 
 * @author KH2638
 * @version 1.0
 * implements {@link BillPayPayeeBusinessDelegate}
 */

public class BillPayPayeeBusinessDelegateImpl implements BillPayPayeeBusinessDelegate {
	
	private static final Logger LOG = LogManager.getLogger(BillPayPayeeBusinessDelegateImpl.class);
	
	@Override
	public List<BillPayPayeeDTO> fetchPayeesFromDBX(Set<String> associatedCifs, String legalEntityId) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_BILLPAYPAYEE_GET;
	        
        Map<String, Object> requestParameters = new HashMap<String, Object>();
        
        String filter = "";
        for (String cif : associatedCifs) {
			if(filter.isEmpty()) 
				filter = "cif" + DBPUtilitiesConstants.EQUAL + cif ;
			else
				filter = filter +  DBPUtilitiesConstants.OR +"cif" + DBPUtilitiesConstants.EQUAL + cif ;
		}
        if (StringUtils.isNotBlank(legalEntityId)) {
            filter = DBPUtilitiesConstants.OPEN_BRACE + filter + DBPUtilitiesConstants.CLOSE_BRACE + DBPUtilitiesConstants.AND + "legalEntityId" + DBPUtilitiesConstants.EQUAL + legalEntityId;
        }
        requestParameters.put(DBPUtilitiesConstants.FILTER, filter);
        
		List<BillPayPayeeDTO> billPayPayeeDTOs = null;
		String payeeResponse = null;
		try {
			payeeResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			JSONObject responseObj = new JSONObject(payeeResponse);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
			billPayPayeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), BillPayPayeeDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch billpay payees from billpaypayee table: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at fetchPayeesFromDBX: " + e);
			return null;
		}
		
		return billPayPayeeDTOs;
	}
	
	@Override
	public BillPayPayeeDTO createPayeeAtDBX(BillPayPayeeDTO billPayPayeeDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_BILLPAYPAYEE_CREATE;
	        
        Map<String, Object> requestParameters;
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(billPayPayeeDTO).toString(), String.class, Object.class);
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
			billPayPayeeDTO = JSONUtils.parse(responseArray.getJSONObject(0).toString(), BillPayPayeeDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to create billpay payee at billpaypayee table: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at createPayeeAtDBX: " + e);
			return null;
		}
		
		return billPayPayeeDTO;
	}
	
	@Override
	public List<BillPayPayeeDTO> fetchPayeeByIdAtDBX(BillPayPayeeDTO billPayPayeeDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_BILLPAYPAYEE_GET;
	        
        Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "payeeId" + DBPUtilitiesConstants.EQUAL + billPayPayeeDTO.getPayeeId();
		
		if(billPayPayeeDTO.getContractId() != null) {
			filter += DBPUtilitiesConstants.AND +"contractId" + DBPUtilitiesConstants.EQUAL + billPayPayeeDTO.getContractId();
		}
		
		if(billPayPayeeDTO.getCif() != null) {
			filter += DBPUtilitiesConstants.AND +"cif" + DBPUtilitiesConstants.EQUAL + billPayPayeeDTO.getCif();
		}
		
		filter += DBPUtilitiesConstants.AND + "legalEntityId" + DBPUtilitiesConstants.EQUAL + billPayPayeeDTO.getLegalEntityId();
		
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		
		List<BillPayPayeeDTO> billPayPayeeDTOs = null;
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
			billPayPayeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), BillPayPayeeDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch billpay payee by id: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at fetchPayeeByIdAtDBX: " + e);
			return null;
		}
		
		return billPayPayeeDTOs;
	}
	
	@Override
	public boolean deletePayeeAtDBX(BillPayPayeeDTO billPayPayeeDTO) {
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BILLPAYPAYEE_DELETE;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "payeeId" + DBPUtilitiesConstants.EQUAL + billPayPayeeDTO.getPayeeId();
		
		if(billPayPayeeDTO.getContractId() != null) {
			filter += DBPUtilitiesConstants.AND +"contractId" + DBPUtilitiesConstants.EQUAL + billPayPayeeDTO.getContractId();
		}
		
		if(billPayPayeeDTO.getCif() != null) {
			filter += DBPUtilitiesConstants.AND +"cif" + DBPUtilitiesConstants.EQUAL + billPayPayeeDTO.getCif();
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
			LOG.error("JSONExcpetion occured while deleting the bill pay payee",jsonExp);
			return false;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while deleting the bill pay payee",exp);
			return false;
		}
		
		return false;	
	}
}
