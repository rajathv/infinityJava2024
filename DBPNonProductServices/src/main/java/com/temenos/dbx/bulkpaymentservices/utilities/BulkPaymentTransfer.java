package com.temenos.dbx.bulkpaymentservices.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.bulkpaymentservices.dto.BulkPaymentPODTO;
import com.temenos.dbx.product.bulkpaymentservices.dto.BulkPaymentRecordDTO;

public class BulkPaymentTransfer {

	private static final Logger LOG = LogManager.getLogger(BulkPaymentTransfer.class);
	
	BulkPaymentRecordDBOperations bulkPaymentRecordDBOperations = new BulkPaymentRecordDBOperations();
	BulkPaymentPODBOperations bulkPaymentPODBOperations = new BulkPaymentPODBOperations();
	
	public boolean initiatePayment(String recordId, DataControllerRequest dcRequest) {

		BulkPaymentRecordDTO recordResultDTO = null;
		List<BulkPaymentPODTO> subRecordResultDTO = null;				
        String featureActionId = null;
		recordResultDTO = bulkPaymentRecordDBOperations.fetchBulkPaymentRecordDetailsById(recordId);

		if(recordResultDTO != null) {		
			subRecordResultDTO = bulkPaymentPODBOperations.fetchBulkPaymentSubRecords(recordResultDTO.getRecordId());
		} else {
			LOG.error("Error occured while fetching Bulk payment record");
			return false;
		}

		if(subRecordResultDTO == null) {
			LOG.error("Error occured while fetching Bulk payment sub records");
			return false;
		}
	    if(recordResultDTO.getBatchMode().equalsIgnoreCase("SINGLE")){
		featureActionId = FeatureAction.BULK_PAYMENT_SINGLE_SUBMIT;
	    } else if(recordResultDTO.getBatchMode().equalsIgnoreCase("MULTIPLE")){
		featureActionId = FeatureAction.BULK_PAYMENT_MULTIPLE_SUBMIT;
		}
		Map<String, Object> requestParameters = new HashMap<String, Object>();;
		requestParameters.put("fromAccountNumber", recordResultDTO.getFromAccount());		
		requestParameters.put("transactionsNotes", recordResultDTO.getDescription());
		requestParameters.put("serviceName", featureActionId);
		requestParameters.put("transactionType",DBPUtilitiesConstants.TRANSACTION_TYPE_EXTERNAL_TRANSFER);
		boolean paymentStatus = false;
		String paymentRecordStatus = TransactionStatusEnum.EXECUTED.getStatus();

		for(int i = 0; i < subRecordResultDTO.size(); i++) {
			requestParameters.put("amount", subRecordResultDTO.get(i).getAmount());
			requestParameters.put("toAccountNumber", subRecordResultDTO.get(i).getAccountNumber());
			requestParameters.put("transactionCurrency", subRecordResultDTO.get(i).getCurrency());
			requestParameters.put("payeeName", subRecordResultDTO.get(i).getRecipientName());
			requestParameters.put("swiftCode", subRecordResultDTO.get(i).getSwift());			
			requestParameters.put("bankName", subRecordResultDTO.get(i).getBankName());
			requestParameters.put("IBAN", subRecordResultDTO.get(i).getAccountNumber());	
			
			boolean updateStatus = false;
			
			if(!createTransfer(requestParameters,dcRequest)) {
				updateStatus = bulkPaymentPODBOperations.updateBulkPaymentSubRecordStatus(subRecordResultDTO.get(i).getPaymentOrderId()
						, TransactionStatusEnum.FAILED.getStatus());
				paymentRecordStatus = TransactionStatusEnum.FAILED.getStatus();
			} else {
				updateStatus = bulkPaymentPODBOperations.updateBulkPaymentSubRecordStatus(subRecordResultDTO.get(i).getPaymentOrderId()
						, TransactionStatusEnum.EXECUTED.getStatus());
			}
			
			if(!updateStatus) {
				LOG.error("Error occured while updating the status of Bulk payment sub record");
			}
			
		}
		paymentStatus = bulkPaymentRecordDBOperations.updateBulkPaymentRecordPaymentStatus(recordId, paymentRecordStatus);
		bulkPaymentRecordDBOperations.updateBulkPaymentRecordStatus(recordId, paymentRecordStatus);
		if(!paymentStatus) {
			LOG.error("Error occured while updating the status of Bulk payment record");
		}
		return paymentStatus;
	}


	/*
	 * Processing transactions for bulk payments
	 * */
	public boolean createTransfer(Map<String, Object> requestParameters, DataControllerRequest dcRequest) {

		String serviceName = ServiceId.DBPNONPRODUCTSERVICES;
		String operationName = OperationName.CREATE_TRANSFER;			

		String uploadResponse = null;
		try {
			uploadResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(dcRequest.getHeaderMap()).
					withDataControllerRequest(dcRequest).
					build().getResponse();

			JSONObject responseObj = new JSONObject(uploadResponse);
			if(responseObj.getInt("opstatus") == 0 && responseObj.getInt("httpStatusCode") == 0) {
				return true;
			}
		}
		catch(Exception exp) {
			LOG.error("Error Occurred while creating a fund transfer",exp);
			return false;
		}
		return false;
	}
}
