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
import com.temenos.dbx.product.payeeservices.businessdelegate.api.WireTransfersPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.dto.WireTransfersPayeeDTO;

/**
 * 
 * @author KH2660
 * @version 1.0
 * implements {@link WireTransfersPayeeBusinessDelegate}
 */
public class WireTransfersPayeeBusinessDelegateImpl implements WireTransfersPayeeBusinessDelegate{
	
	private static final Logger LOG = LogManager.getLogger(WireTransfersPayeeBusinessDelegateImpl.class);

	@Override
	public List<WireTransfersPayeeDTO> fetchPayeesFromDBX(Set<String> associatedCifs, String typeId) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_WIRETRANSFERSPAYEE_GET;
        
        Map<String, Object> requestParameters = new HashMap<String, Object>();
        
        String filter = "";
        for (String cif : associatedCifs) {
			if(filter.isEmpty()) 
				filter = "cif" + DBPUtilitiesConstants.EQUAL + cif;
			else
				filter = filter +  DBPUtilitiesConstants.OR +"cif" + DBPUtilitiesConstants.EQUAL + cif;
		}
        
        if(StringUtils.isNotBlank(typeId)) {
        	filter = " ( " + filter + " ) " + DBPUtilitiesConstants.AND +"typeId" + DBPUtilitiesConstants.EQUAL + typeId;
        }
        requestParameters.put(DBPUtilitiesConstants.FILTER, filter);
			
		List<WireTransfersPayeeDTO> wireTransfersPayeeDTOs = null;
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
			wireTransfersPayeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), WireTransfersPayeeDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch wire transfers payee Ids: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at fetch wire transfers payee Ids: " + e);
			return null;
		}
		
		return wireTransfersPayeeDTOs;

	}

	@Override
	public WireTransfersPayeeDTO createPayeeAtDBX(WireTransfersPayeeDTO wireTransfersPayeeDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_WIRETRANSFERSPAYEE_CREATE;
	        
        Map<String, Object> requestParameters;
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(wireTransfersPayeeDTO).toString(), String.class, Object.class);
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
			wireTransfersPayeeDTO = JSONUtils.parse(responseArray.getJSONObject(0).toString(), WireTransfersPayeeDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to create wiretransfers payee at wiretransferspayee table: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at createPayeeAtDBX: " + e);
			return null;
		}
		
		return wireTransfersPayeeDTO;
	
	}

	@Override
	public List<WireTransfersPayeeDTO> fetchPayeeByIdAtDBX(WireTransfersPayeeDTO wireTransfersPayeeDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_WIRETRANSFERSPAYEE_GET;
	        
        Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "payeeId" + DBPUtilitiesConstants.EQUAL + wireTransfersPayeeDTO.getPayeeId();
		
		if(wireTransfersPayeeDTO.getContractId() != null) {
			filter += DBPUtilitiesConstants.AND +"contractId" + DBPUtilitiesConstants.EQUAL + wireTransfersPayeeDTO.getContractId();
		}
		
		if(wireTransfersPayeeDTO.getCif() != null) {
			filter += DBPUtilitiesConstants.AND +"cif" + DBPUtilitiesConstants.EQUAL + wireTransfersPayeeDTO.getCif();
		}
		
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
			
		List<WireTransfersPayeeDTO> wireTransfersPayeeDTOs = null;
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
			wireTransfersPayeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), WireTransfersPayeeDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch wire transfers payee by id: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at fetchPayeeByIdAtDBX: " + e);
			return null;
		}
		
		return wireTransfersPayeeDTOs;
	}

	@Override
	public boolean deletePayeeAtDBX(WireTransfersPayeeDTO wireTransfersPayeeDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_WIRETRANSFERSPAYEE_DELETE;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "payeeId" + DBPUtilitiesConstants.EQUAL + wireTransfersPayeeDTO.getPayeeId();
		
		if(wireTransfersPayeeDTO.getContractId() != null) {
			filter += DBPUtilitiesConstants.AND +"contractId" + DBPUtilitiesConstants.EQUAL + wireTransfersPayeeDTO.getContractId();
		}
		
		if(wireTransfersPayeeDTO.getCif() != null) {
			filter += DBPUtilitiesConstants.AND +"cif" + DBPUtilitiesConstants.EQUAL + wireTransfersPayeeDTO.getCif();
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
			LOG.error("JSONExcpetion occured while deleting the wiretransfers payee",jsonExp);
			return false;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while deleting the wiretransfers payee",exp);
			return false;
		}
		
		return false;	
	}

}
