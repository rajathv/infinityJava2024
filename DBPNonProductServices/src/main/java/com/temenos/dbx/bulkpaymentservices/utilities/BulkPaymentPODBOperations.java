package com.temenos.dbx.bulkpaymentservices.utilities;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.bulkpaymentservices.dto.BulkPaymentPODTO;
import com.temenos.dbx.product.bulkpaymentservices.dto.BulkPaymentRecordDTO;

public class BulkPaymentPODBOperations {

	ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApplicationBusinessDelegate.class);
	private static final Logger LOG = LogManager.getLogger(BulkPaymentPODBOperations.class);	
	public static final String SCHEMA_NAME = EnvironmentConfigurationsHandler.getValue("DBX_SCHEMA_NAME");

	public static final String DB_BULKPAYMENTPO_CREATE = SCHEMA_NAME + "_bulkpaymentsubrecordmock_create";
	public static final String DB_BULKPAYMENTPO_GET = SCHEMA_NAME + "_bulkpaymentsubrecordmock_get";
	public static final String DB_BULKPAYMENTPO_DELETE = SCHEMA_NAME + "_bulkpaymentsubrecordmock_delete";
	public static final String DB_BULKPAYMENTPO_UPDATE = SCHEMA_NAME + "_bulkpaymentsubrecordmock_update";


	public BulkPaymentPODTO createBulkPaymentPOEntry(BulkPaymentPODTO bulkpaymentPODTO) {

		BulkPaymentPODTO bulkPaymentPODTO = null;
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = DB_BULKPAYMENTPO_CREATE;
		Map<String, Object> requestParameters;

		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(bulkpaymentPODTO).toString(), String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occurred while fetching the request params: ", e);
			return null;
		}

		String createResponse = null;
		try {
			createResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(requestParameters)
					.build().getResponse();
			JSONObject response = new JSONObject(createResponse);
			JSONArray responseArray = CommonUtils.getFirstOccuringArray(response);
			List<BulkPaymentPODTO> poList = JSONUtils.parseAsList(responseArray.toString(), BulkPaymentPODTO.class);
			
			if(CollectionUtils.isNotEmpty(poList))
				return poList.get(0);
			
		} catch (JSONException e) {
			LOG.error("Failed to create payment order at bulkpaymentPO table: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at createPaymentOrder: ", e);
			return null;
		}
		return bulkPaymentPODTO;
	}
	
	public List<BulkPaymentPODTO> fetchBulkPaymentSubRecords(String recordId) {

		List<BulkPaymentPODTO> resultDTO = null;
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = DB_BULKPAYMENTPO_GET;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "recordId" + DBPUtilitiesConstants.EQUAL + recordId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);

		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject responseObj = new JSONObject(fetchResponse);
			JSONArray responseArray = CommonUtils.getFirstOccuringArray(responseObj);
			resultDTO = JSONUtils.parseAsList(responseArray.toString(), BulkPaymentPODTO.class);

		} catch (JSONException e) {
			LOG.error("Failed to fetch bulk payment po records", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception while fetching bulkpayment po records: ", e);
			return null;
		}
		return resultDTO;
	}
	
	public boolean updateBulkPaymentSubRecordStatus(String paymentOrderId, String status) {

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = DB_BULKPAYMENTPO_UPDATE;

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("paymentOrderId", paymentOrderId);
		requestParams.put("status", status);
		
		String editResponse = null;

		try {
			editResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(requestParams)
					.build().getResponse();
			JSONObject responseObj = new JSONObject(editResponse);
			if(responseObj.getInt("opstatus") == 0 && responseObj.getInt("httpStatusCode") == 0) {
				return true;
			}
		} catch (JSONException je) {
			LOG.error("Failed to update bulk payment file status", je);
			return false;
		} catch (Exception e) {
			LOG.error("Caught exception at while updating bulk payment file", e);
			return false;
		}
		return false;
	}
	
	public BulkPaymentPODTO updatePaymentOrder(BulkPaymentPODTO bulkPaymentPODTO) {

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = DB_BULKPAYMENTPO_UPDATE;
		
		BulkPaymentPODTO outputObj;
		BulkPaymentPODTO oldPO = fetchBulkPaymentOrderbyId(bulkPaymentPODTO.getPaymentOrderId());
		Map<String, Object> requestParameters;
		Double oldAmount = oldPO.getAmount();
		Double newAmount = bulkPaymentPODTO.getAmount();
		
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(bulkPaymentPODTO).toString(), String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occurred while fetching the request params: ", e);
			return null;
		}
		String editResponse = null;

		try {
			editResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(requestParameters)
					.build().getResponse();

			JSONObject response = new JSONObject(editResponse);
			JSONArray responseArray = CommonUtils.getFirstOccuringArray(response);
			outputObj = JSONUtils.parse(responseArray.get(0).toString(), BulkPaymentPODTO.class);
			if(outputObj != null && outputObj.getDbpErrMsg() == null && outputObj.getDbpErrorCode() == null) {
				BulkPaymentRecordDBOperations operations = new BulkPaymentRecordDBOperations();
				operations.updateBulkPaymentRecordAmountAndTotalTransactions(bulkPaymentPODTO.getRecordId(),
						newAmount-oldAmount, new Double(0));
			}
		} catch (JSONException je) {
			LOG.error("Failed to edit payment order: ", je);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at updatePaymentOrder: ", e);
			return null;
		}
		return outputObj;
	}
	
	public Result updateBulkPaymentOrderStatus(String paymentOrderId, String status) {		
		Result result = null;		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = DB_BULKPAYMENTPO_UPDATE;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("paymentOrderId", paymentOrderId);
		requestParams.put("status", status);		
		String response = null;
		try {
			response = DBPServiceExecutorBuilder.builder()
					.withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(requestParams)
					.build().getResponse();
			JSONObject responseObj = new JSONObject(response);
			JSONArray responseArray = CommonUtils.getFirstOccuringArray(responseObj);
			result= JSONToResult.convert(responseArray.getJSONObject(0).toString());
		} catch (JSONException je) {
			LOG.error("Failed to update bulk payment order status", je);
			return ErrorCodeEnum.ERR_21212.setErrorCode(result);
		} catch (Exception e) {
			LOG.error("Caught exception at while updating bulk payment order", e);
			return ErrorCodeEnum.ERR_21212.setErrorCode(result);
		}
		return result;
	}
	
	public BulkPaymentPODTO deletePaymentOrder(String recordId, String paymentOrderId) {

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = DB_BULKPAYMENTPO_DELETE;

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("recordId", recordId);
		requestParams.put("paymentOrderId", paymentOrderId);
		
		String deleteResponse = null;
		BulkPaymentPODTO outputObj;
		BulkPaymentPODTO paymentOrder = fetchBulkPaymentOrderbyId(paymentOrderId);

		try {
			deleteResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(requestParams)
					.build().getResponse();
			
			JSONObject responseObject = new JSONObject(deleteResponse);
			Integer records = (Integer) responseObject.getInt("deletedRecords");
			outputObj = JSONUtils.parse(responseObject.toString(), BulkPaymentPODTO.class);
			if (records != null) {
				outputObj.setStatus(TransactionStatusEnum.EXECUTED.getStatus());
			}
			if(outputObj != null && outputObj.getDbpErrMsg() == null && outputObj.getDbpErrorCode() == null) {
				BulkPaymentRecordDBOperations operations = new BulkPaymentRecordDBOperations();
				operations.updateBulkPaymentRecordAmountAndTotalTransactions(recordId,
						paymentOrder.getAmount()*(-1), new Double(-1));
			}
		} catch (JSONException je) {
			LOG.error("Failed to delete bulk payment payment Order", je);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at deletePaymentOrder", e);
			return null;
		}
		return outputObj;
	}
	
	public BulkPaymentPODTO fetchBulkPaymentOrderbyId(String paymentOrderId) {

		BulkPaymentPODTO resultDTO = null;
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = DB_BULKPAYMENTPO_GET;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "paymentOrderId" + DBPUtilitiesConstants.EQUAL + paymentOrderId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);

		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject responseObj = new JSONObject(fetchResponse);
			JSONArray responseArray = CommonUtils.getFirstOccuringArray(responseObj);
			resultDTO = JSONUtils.parse(responseArray.getJSONObject(0).toString(), BulkPaymentPODTO.class);

		} catch (JSONException e) {
			LOG.error("Failed to fetch bulk payment po records", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception while fetching bulkpayment po records: ", e);
			return null;
		}
		return resultDTO;
	}
}
