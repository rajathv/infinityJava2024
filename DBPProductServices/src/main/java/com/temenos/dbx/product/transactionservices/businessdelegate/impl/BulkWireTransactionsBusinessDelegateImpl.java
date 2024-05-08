package com.temenos.dbx.product.transactionservices.businessdelegate.impl;

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
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.BulkWireTransactionsBusinessDelegate;
import com.temenos.dbx.product.transactionservices.dto.BulkWireFileTransactionsDetailDTO;
import com.temenos.dbx.product.transactionservices.dto.BulkWireTemplateTransactionsDetailDTO;
import com.temenos.dbx.product.transactionservices.dto.BulkWireFileDTO;

/**
 * 
 * 
 * @version 1.0
 * implements {@link BulkWireTransactionsBusinessDelegate}
 */
public class BulkWireTransactionsBusinessDelegateImpl implements BulkWireTransactionsBusinessDelegate {
	private static final Logger LOG = LogManager.getLogger(BulkWireTransactionsBusinessDelegateImpl.class);


	public BulkWireFileTransactionsDetailDTO createBulkWireTransactionDetails(Map<String, Object> requestParameters) {

		BulkWireFileTransactionsDetailDTO bulkwirefileTransactionsDetailDTO;

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BULKWIREFILETRANSACTDETAILS_CREATE;

		String bulkWireTransferResponse = null;
		try {
			LOG.debug("In Business Delegate Method");
			bulkWireTransferResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters).build().getResponse();                  
			JSONObject jsonRsponse = new JSONObject(bulkWireTransferResponse);
			JSONArray countJsonArray = jsonRsponse.getJSONArray("bulkwirefiletransactdetails");
			bulkwirefileTransactionsDetailDTO = JSONUtils.parse(countJsonArray.getJSONObject(0).toString(), BulkWireFileTransactionsDetailDTO.class);
		}
		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while creating the bulkwiretransfer: ",jsonExp);
			return null;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while creating the bulkwiretransfer: ",exp);
			return null;
		}

		return bulkwirefileTransactionsDetailDTO;
	}
	
	public BulkWireFileDTO updateBulkWireFiles(Map<String, Object> requestParameters) {

		BulkWireFileDTO bulkwirefileDTO;

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BULKWIREFILES_UPDATE;

		String bulkWirfileResponse = null;
		try {
			LOG.debug("In Business Delegate Method");
			bulkWirfileResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters).build().getResponse();
			JSONObject jsonRsponse = new JSONObject(bulkWirfileResponse);
			JSONArray countJsonArray = jsonRsponse.getJSONArray("bulkwirefiletransactdetails");
			bulkwirefileDTO = JSONUtils.parse(countJsonArray.getJSONObject(0).toString(), BulkWireFileDTO.class);
		}
		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while creating the bulkwiretransfer: ",jsonExp);
			return null;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while creating the bulkwiretransfer: ",exp);
			return null;
		}

		return bulkwirefileDTO;
	}


	public List<BulkWireFileTransactionsDetailDTO> getBulkWireFileTransactionsDetail(String bulkWireFileId,String sortByParam,String sortOrder,Object pageOffset,Object pageSize,String searchString) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BULKWIREFILETRANSACTDETAILS_FETCH; 
		Map<String, Object> queryParams = new HashMap<String, Object>();
		String response = null;
		
		queryParams.put(Constants.BULKWIREFILE_ID, bulkWireFileId);
        queryParams.put(Constants.SEARCHSTRING, searchString);
        queryParams.put(Constants.SORTBYPARAM, sortByParam);
        queryParams.put(Constants.SORTORDER, sortOrder);
        queryParams.put(Constants.PAGEOFFSET, pageOffset);
        queryParams.put(Constants.PAGESIZE, pageSize);
		
		List <BulkWireFileTransactionsDetailDTO> bulkWireFileTransactionsDetailDTO = null;
		try {
			
			response = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(queryParams).build().getResponse();
		} catch (Exception e) {
			LOG.error("Failed to fetch bulk wire file transaction details: " + e);
			return null;
		}
		try {
			JSONObject jsonResponse = new JSONObject(response);
			JSONArray bulkWireFileTransact =  jsonResponse.getJSONArray(Constants.RECORDS);
			bulkWireFileTransactionsDetailDTO = JSONUtils.parseAsList(bulkWireFileTransact.toString(), BulkWireFileTransactionsDetailDTO.class);
		} catch (IOException e1) {
			LOG.error("Failed to fetch bulk wire files details: " + e1);
			return null;
		}

		return bulkWireFileTransactionsDetailDTO;
	}

	@Override
	public BulkWireTemplateTransactionsDetailDTO createBulkWireTemplateTransactionDetails(
			Map<String, Object> requestParameters) {
		BulkWireTemplateTransactionsDetailDTO bulkwireTemplateTransactionsDetailDTO;

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BULKWIRETEMPLATETRANSACTDETAILS_CREATE;

		String bulkWireTransferResponse = null;
		try {
			LOG.debug("In Business Delegate Method");
			bulkWireTransferResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters).build().getResponse();
			JSONObject jsonRsponse = new JSONObject(bulkWireTransferResponse);
			JSONArray countJsonArray = jsonRsponse.getJSONArray("bulkwiretemplatetransactdetails");
			bulkwireTemplateTransactionsDetailDTO = JSONUtils.parse(countJsonArray.getJSONObject(0).toString(), BulkWireTemplateTransactionsDetailDTO.class);
		}
		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while creating the bulkwiretransfer: ",jsonExp);
			return null;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while creating the bulkwiretransfer: ",exp);
			return null;
		}
		return bulkwireTemplateTransactionsDetailDTO;
	}

	@Override
	public List<BulkWireTemplateTransactionsDetailDTO> getBulkWireTemplateTransactionDetail(String bulkWireTemplateID,
			String sortByParam, String sortOrder, Object pageOffset, Object pageSize, String searchString) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BULKWIRETEMPLATETRANSACTDETAILS_FETCH; 
		Map<String, Object> queryParams = new HashMap<String, Object>();
		String response = null;
		
		queryParams.put(Constants.BULKWIRETEMPLATEID, bulkWireTemplateID);
        queryParams.put(Constants.SEARCHSTRING, searchString);
        queryParams.put(Constants.SORTBYPARAM, sortByParam);
        queryParams.put(Constants.SORTORDER, sortOrder);
        queryParams.put(Constants.PAGEOFFSET, pageOffset);
        queryParams.put(Constants.PAGESIZE, pageSize);
		
		List <BulkWireTemplateTransactionsDetailDTO> bulkWireTemplateTransactionsDetailDTO = null;
		try {
			
			response = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(queryParams).build().getResponse();
		} catch (Exception e) {
			LOG.error("Failed to fetch bulk wire file transaction details: " + e);
			return null;
		}
		try {
			JSONObject jsonResponse = new JSONObject(response);
			JSONArray bulkWireTemplateTransact =  jsonResponse.getJSONArray(Constants.RECORDS);
			bulkWireTemplateTransactionsDetailDTO = JSONUtils.parseAsList(bulkWireTemplateTransact.toString(), BulkWireTemplateTransactionsDetailDTO.class);
		} catch (IOException e1) {
			LOG.error("Failed to fetch bulk wire files details: " + e1);
			return null;
		}

		return bulkWireTemplateTransactionsDetailDTO;
	}

}
