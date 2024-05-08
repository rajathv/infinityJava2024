package com.infinity.dbx.temenos.bulkpaymentservices.backenddelegate.api.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.bulkpaymentservices.backenddelegate.api.BulkPaymentPOBackendDelegate;
import com.temenos.dbx.product.bulkpaymentservices.backenddelegate.impl.BulkPaymentRecordBackendDelegateImpl;
import com.temenos.dbx.product.bulkpaymentservices.dto.BulkPaymentRecordDTO;
import com.temenos.dbx.product.constants.TransactionStatusEnum;

public class BulkPaymentRecordBackendDelegateImplExtn extends BulkPaymentRecordBackendDelegateImpl {

	private static final Logger LOG = LogManager.getLogger(BulkPaymentRecordBackendDelegateImplExtn.class);

	@Override
	public BulkPaymentRecordDTO initiateBulkPayment(BulkPaymentRecordDTO bulkPaymentRecordDTO,
			DataControllerRequest dcr) {
		
		BulkPaymentPOBackendDelegate bulkPaymentPOBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(BulkPaymentPOBackendDelegate.class);
		try {
			bulkPaymentPOBackendDelegate.approvePaymentOrders(bulkPaymentRecordDTO.getRecordId(),dcr);
		}catch(Exception e){
			LOG.error("Error in approving bulk payment orders", e);
		}
		
		String serviceName = TemenosConstants.SERVICE_T24IS_BULK_PAYMENTS;
		String operationName = TemenosConstants.OP_INITIATE_BULK_PAYMENT;
		BulkPaymentRecordDTO recordDTO;
		String response;
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put("recordId", bulkPaymentRecordDTO.getConfirmationNumber());

		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(dcr.getHeaderMap()).
					withDataControllerRequest(dcr).
					build().getResponse();

			JSONObject responseObj = new JSONObject(response);
			recordDTO = JSONUtils.parse(responseObj.toString(), BulkPaymentRecordDTO.class);
		} catch (JSONException e) {
			LOG.error("Error in initiating Bulk Payment", e);
			return null;
		} catch (Exception e) {
			LOG.error("Error occured in initiateBulkPayment", e);
			return null;
		}
		return recordDTO;
	}

	public BulkPaymentRecordDTO cancelBulkPaymentRecord(String bulkPaymentRecordId, String comments, String cancellationreason,
			String cancellationReasonId, String statusCode, DataControllerRequest dcr) {

		String serviceName = TemenosConstants.SERVICE_T24IS_BULK_PAYMENTS;
		String operationName = TemenosConstants.OP_CANCEL_BULK_PAYMENT;
		String response = null;
		BulkPaymentRecordDTO bulkPaymentRecordDTO = null;

		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put("recordId", bulkPaymentRecordId);
		requestParameters.put("comments", comments);
		requestParameters.put("cancellationreason", cancellationReasonId);
		requestParameters.put("statusCode", statusCode);

		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(dcr.getHeaderMap()).
					withDataControllerRequest(dcr).
					build().getResponse();

			JSONObject record = new JSONObject(response);
			bulkPaymentRecordDTO = JSONUtils.parse(record.toString(), BulkPaymentRecordDTO.class);

		} catch (JSONException e) {
			LOG.error("Error in cancelling Bulk Payments record", e);
			return null;
		} catch (Exception e) {
			LOG.error("Error occured in cancelBulkPaymentRecord", e);
			return null;
		}

		return bulkPaymentRecordDTO;
	}

	@Override
	public BulkPaymentRecordDTO updateBulkPaymentRecord(String recordId, String fromAccount, String description,
			DataControllerRequest request) {
		String serviceName = TemenosConstants.SERVICE_T24IS_BULK_PAYMENTS;
		String operationName = TemenosConstants.OP_EDIT_BULK_PAYMENT;
		String response = null;
		BulkPaymentRecordDTO bulkPaymentRecordDTO = null;

		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put("recordId", recordId);
		requestParameters.put("fromAccount", fromAccount);
		requestParameters.put("description", description);

		try {
			response = DBPServiceExecutorBuilder.
					builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(request.getHeaderMap()).
					withDataControllerRequest(request).build().getResponse();
			JSONObject record = new JSONObject(response);
			bulkPaymentRecordDTO = JSONUtils.parse(record.toString(), BulkPaymentRecordDTO.class);

		} catch (JSONException e) {
			LOG.error("Error in updating Bulk Payments record", e);
			return null;
		} catch (Exception e) {
			LOG.error("Error occured in updateBulkPaymentRecord", e);
			return null;
		}

		return bulkPaymentRecordDTO;
	}
	
	@Override
	public List<BulkPaymentRecordDTO> fetchOnGoingPayments(Set<String> fromAccounts, String batchMode, String fromDate, String toDate, DataControllerRequest dcr) {
		String serviceName = TemenosConstants.SERVICE_T24IS_BULK_PAYMENTS;
		String operationName = TemenosConstants.OP_FETCH_ONGOING_BULK_PAYMENT;
		String response;
		/*String executionDate = null;*/
		List<BulkPaymentRecordDTO> records;
		Map<String, Object> headers = dcr.getHeaderMap();
		headers.put("debitAccount", String.join(" ", fromAccounts));
		
		/*Commenting as part of removing date filter
		 * if(StringUtils.isNotEmpty(fromDate) && StringUtils.isNotEmpty(toDate)) {
		 * executionDate = String.join(" ", fromDate, toDate);
		 * headers.put("executionDate", executionDate); } else {
		 * headers.remove(executionDate); }
		 */
        
        if(StringUtils.isNotEmpty(batchMode))
        {
            headers.put("bulkTypeId", batchMode);
        }
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(null).
					withRequestHeaders(headers).
					withDataControllerRequest(dcr).
					build().getResponse();
			
			JSONObject responseObj = new JSONObject(response);
            JSONArray jsonArray = TemenosUtils.getFirstOccuringArray(responseObj);
            records = JSONUtils.parseAsList(jsonArray.toString(), BulkPaymentRecordDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Error in Downloading OnGoing Bulk Payments", e);			
			return null;
		} 
		catch (Exception e) {
			LOG.error("Error occured in fetchOnGoingBulkPayments", e);			
			return null;
		}
		return records;
	}
	
	@Override
	public List<BulkPaymentRecordDTO> fetchBulkPaymentHistory(Set<String> fromAccounts, String fromDate, String toDate, DataControllerRequest dcr) {
		String serviceName = TemenosConstants.SERVICE_T24IS_BULK_PAYMENTS;
		String operationName = TemenosConstants.OP_FETCH_BULK_PAYMENT_HISTORY;
		String response;
		/*String executionDate=null;*/
		List<BulkPaymentRecordDTO> historyRecords;
		Map<String, Object> headers = dcr.getHeaderMap();
		headers.put("debitAccount", String.join(" ", fromAccounts));

		/*Commenting as part of removing date filter
		 * if(StringUtils.isNotEmpty(fromDate) && StringUtils.isNotEmpty(toDate)) {
		 * executionDate = String.join(" ", fromDate, toDate);
		 * headers.put("executionDate", executionDate); } else {
		 * headers.remove(executionDate); }
		 */
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(null).
					withRequestHeaders(headers).
					withDataControllerRequest(dcr).
					build().getResponse();
			
			JSONObject responseObj = new JSONObject(response);
            JSONArray jsonArray = TemenosUtils.getFirstOccuringArray(responseObj);
            historyRecords = JSONUtils.parseAsList(jsonArray.toString(), BulkPaymentRecordDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Error in fetching Bulk Payments history", e);			
			return null;
		} 
		catch (Exception e) {
			LOG.error("Error occured in fetchBulkPaymentHistory", e);			
			return null;
		}
		return historyRecords;
	}
	
	@Override
	public BulkPaymentRecordDTO updateBulkPaymentRecordStatus(String recordId, String status, DataControllerRequest request) {
		
		String serviceName = TemenosConstants.SERVICE_T24IS_BULK_PAYMENTS;
		String operationName = TemenosConstants.OP_UPDATE_BULK_PAYMENT_STATUS;
		
		String response = null;
		BulkPaymentRecordDTO bulkPaymentRecordDTO = null;
		
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		if(status.toString().equals(TransactionStatusEnum.CREATED.getStatus()))
		{
			status=TransactionStatusEnum.CREATED.getStatus();
		}
		else
		{
			status=TransactionStatusEnum.READY.getStatus();
			
		}
		requestParameters.put("recordId", recordId);
		requestParameters.put("status", status.toUpperCase());
		
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(request.getHeaderMap()).
					withDataControllerRequest(request).
					build().getResponse();
			JSONObject record = new JSONObject(response);			
			bulkPaymentRecordDTO = JSONUtils.parse(record.toString(), BulkPaymentRecordDTO.class);
			
		} catch (JSONException e) {
			LOG.error("Error while updating the status of Bulk Payment record", e); 
			return null;
		}catch (Exception e) {
			LOG.error("Error while updating the status of Bulk Payment record", e);			
			return null;
		}
	
		return bulkPaymentRecordDTO;
	}
	
	@Override
	public BulkPaymentRecordDTO fetchBulkPaymentRecordDetailsById(String recordId, DataControllerRequest dcr) {
		String serviceName = TemenosConstants.SERVICE_T24IS_BULK_PAYMENTS;
		String operationName = TemenosConstants.OP_FETCH_BULKPAYMENT_RECORDDETAILS_BY_ID;
		String response;
		BulkPaymentRecordDTO record;

		Map<String, Object> requestParameters = new HashMap<String, Object>();
		
		requestParameters.put("recordId", recordId);
		
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(dcr.getHeaderMap()).
					withDataControllerRequest(dcr).
					build().getResponse();
			
			JSONObject responseObj = new JSONObject(response);
            record = JSONUtils.parse(responseObj.toString(), BulkPaymentRecordDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Error in fetching Bulk Payment record", e);			
			return null;
		} 
		catch (Exception e) {
			LOG.error("Error occured in fetching Bulk Payment record", e);	
			return null;
		}
		return record;
	}
	
	@Override
	public List<BulkPaymentRecordDTO> fetchRecords(Set<String> recordIdList, DataControllerRequest dcr) {
		
		String serviceName =  TemenosConstants.SERVICE_T24OS_BULK_PAYMENTS;
		String operationName = TemenosConstants.OP_FETCHRECORDS;

		Map<String, Object> inputParams = new HashMap<String, Object>();
		inputParams.put("recordId", String.join(",", recordIdList));
		inputParams.put("loop_count", String.valueOf(recordIdList.size()));
		List<BulkPaymentRecordDTO> output = new ArrayList<BulkPaymentRecordDTO>();
		
		try {
			String createResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(inputParams)
					.withRequestHeaders(dcr.getHeaderMap())
					.withDataControllerRequest(dcr)
					.build().getResponse();
			
			JSONObject response = new JSONObject(createResponse);
			JSONArray responseArray = TemenosUtils.getFirstOccuringArray(response);
			output = JSONUtils.parseAsList(responseArray.toString(), BulkPaymentRecordDTO.class);
		} catch (Exception e) {
			LOG.error("Caught exception at " + operationName + " entry: " + e);
			return null;
		}
		return output;
	}
	
	public BulkPaymentRecordDTO rejectBulkPaymentRecord(String recordId, String comments, String rejectionreason, DataControllerRequest request) {
		
		String serviceName = TemenosConstants.SERVICE_T24IS_BULK_PAYMENTS;
		String operationName = TemenosConstants.OP_REJECT_BULKPAYMENT_RECORD;
		String response = null;
		BulkPaymentRecordDTO bulkPaymentRecordDTO = null;
		
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put("recordId", recordId);
		requestParameters.put("comments", request.getParameter("comments"));
		requestParameters.put("rejectionreason", request.getParameter("rejectionreason"));
						
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(request.getHeaderMap()).
					withDataControllerRequest(request).
					build().getResponse();
			JSONObject record = new JSONObject(response);			
			bulkPaymentRecordDTO = JSONUtils.parse(record.toString(), BulkPaymentRecordDTO.class);
			
		} catch (JSONException e) {
			LOG.error("Error while updating the status of Bulk Payment record", e); 
			return null;
		}catch (Exception e) {
			LOG.error("Error while updating the status of Bulk Payment record", e);			
			return null;
		}
	
		return bulkPaymentRecordDTO;
	}

}
