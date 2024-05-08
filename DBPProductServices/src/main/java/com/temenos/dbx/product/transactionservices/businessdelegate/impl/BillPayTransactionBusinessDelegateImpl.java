package com.temenos.dbx.product.transactionservices.businessdelegate.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApprovalQueueBusinessDelegate;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRequestDTO;
import com.temenos.dbx.product.approvalservices.dto.BBRequestDTO;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.BillPayTransactionBackendDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.BillPayTransactionBusinessDelegate;
import com.temenos.dbx.product.transactionservices.dto.BillPayTransactionBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.BillPayTransactionDTO;

/**
 * 
 * @author KH1755
 * @version 1.0
 * implements {@link BillPayTransactionBusinessDelegate}
 */

public class BillPayTransactionBusinessDelegateImpl implements BillPayTransactionBusinessDelegate {
	
	private static final Logger LOG = LogManager.getLogger(BillPayTransactionBusinessDelegateImpl.class);
	ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApplicationBusinessDelegate.class);
	BillPayTransactionBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(BillPayTransactionBackendDelegate.class);
	
	public  BillPayTransactionDTO createTransactionAtDBX(BillPayTransactionDTO billpayTransactionDTO) {
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BILLPAYTRANSFERS_CREATE;
		String createResponse = null;
		
		Map<String, Object> requestParameters;
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(billpayTransactionDTO).toString(), String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: ", e);
			return null;
		}
		try {
			requestParameters.put("createdts", new SimpleDateFormat(Constants.TIMESTAMP_FORMAT).parse(application.getServerTimeStamp()));
			requestParameters.put("transactionId", HelperMethods.getUniqueNumericString(13));
			createResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			JSONObject response = new JSONObject(createResponse);
			JSONArray resposneArray = CommonUtils.getFirstOccuringArray(response);
			
			billpayTransactionDTO = JSONUtils.parse(resposneArray.getJSONObject(0).toString(), BillPayTransactionDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to create billpay transaction entry into billpaytransfers table: ", e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at create billpay transaction entry: ", e);
			return null;
		}
		
		return billpayTransactionDTO;
	}
	
	@Override
	public  BillPayTransactionDTO updateTransactionAtDBX(BillPayTransactionDTO billpayTransactionDTO) {
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BILLPAYTRANSFERS_UPDATE;

		String createResponse = null;
		
		Map<String, Object> requestParameters;
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(billpayTransactionDTO).toString(), String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: ", e);
			return null;
		}
		try {
			createResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			JSONObject response = new JSONObject(createResponse);
			JSONArray resposneArray = CommonUtils.getFirstOccuringArray(response);
			
			billpayTransactionDTO = JSONUtils.parse(resposneArray.getJSONObject(0).toString(), BillPayTransactionDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to create billpay transaction entry into billpaytransfers table: ", e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at create billpay transaction entry: ", e);
			return null;
		}
		
		return billpayTransactionDTO;
	}
	
	@Override
	public BillPayTransactionDTO updateStatus(String transactionId, String status, String confirmationNumber) {

		if(application.getIsStateManagementAvailableFromCache()) {
			return updateStatusUsingConfirmationNumber(transactionId, status);
		}
		else {
			return updateStatusUsingTransactionId(transactionId, status, confirmationNumber);
		}
	}
	
	@Override
	public BillPayTransactionDTO updateStatusUsingTransactionId(String transactionId, String status, String confirmationNumber) {

		List<BillPayTransactionDTO> billpayTransactionDTO = null;
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BILLPAYTRANSFERS_UPDATE;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("transactionId", transactionId);
		requestParams.put("status", status);
		requestParams.put("confirmationNumber", confirmationNumber);
		
		try {
			String updateResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject jsonRsponse = new JSONObject(updateResponse);
			JSONArray billpayJsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);
			billpayTransactionDTO = JSONUtils.parseAsList(billpayJsonArray.toString(), BillPayTransactionDTO.class);
		}
		
		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while updating the billpaytransaction",jsonExp);
			return null;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while updating the billpaytransaction",exp);
			return null;
		}
		
		if(billpayTransactionDTO != null && billpayTransactionDTO.size() != 0)
			return billpayTransactionDTO.get(0);
		
		return null;
	}
	
	private BillPayTransactionDTO updateStatusUsingConfirmationNumber(String confirmationNumber, String status) {
		BillPayTransactionDTO transactionDTO = null;
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_UPDATE_TRANSACTION_STATUS_PROC;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("_featureId", Constants.FEATURE_BILL_PAY);
		requestParams.put("_status", status);
		requestParams.put("_confirmationNumber", confirmationNumber);
		
		try {
			String updateResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject jsonRsponse = new JSONObject(updateResponse);
			JSONArray jsonArr = CommonUtils.getFirstOccuringArray(jsonRsponse);
			if(jsonArr != null && jsonArr.length() > 0) {
				JSONObject record = jsonArr.getJSONObject(0);
				if(record != null && record.has("isSuccess") && "true".equals(record.get("isSuccess"))) {
					transactionDTO = new BillPayTransactionDTO();
					transactionDTO.setConfirmationNumber(confirmationNumber);
					transactionDTO.setStatus(status);
				}
			}
		}
		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured in updateStatusUsingConfirmationNumber" + jsonExp);
			return null;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured in updateStatusUsingConfirmationNumber" + exp);
			return null;
		}
		
		return transactionDTO;
	}
	
	@Override
	public boolean updateRequestId(String transactionId, String requestId) {

		List<BillPayTransactionDTO> billpayTransactionDTO = null;
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BILLPAYTRANSFERS_UPDATE;
		Map<String, Object> requestParams = new HashMap<String, Object>();
		
		requestParams.put("transactionId", transactionId);
		requestParams.put("requestId", requestId);
		
		try {
			String updateResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject jsonRsponse = new JSONObject(updateResponse);
			JSONArray billpayJsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);
			billpayTransactionDTO = JSONUtils.parseAsList(billpayJsonArray.toString(), BillPayTransactionDTO.class);
		}
		
		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while updating the billpaytransaction",jsonExp);
			return false;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while updating the billpaytransaction",exp);
			return false;
		}
		
		if(billpayTransactionDTO != null && billpayTransactionDTO.size() != 0)
			return true;
		
		return false;
	}

	public List<BillPayTransactionDTO> createBulkBillPay(Map<String, Object> requestParameters) {

    	List<BillPayTransactionDTO> billpayDTOs;

        String serviceName = ServiceId.BULK_BILL_PAY_SERVICE;
        String operationName = OperationName.CREATE_BULK_BILL_PAY;

        String bulkBillPayResponse = null;
        try {
            bulkBillPayResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
            JSONObject bulkBillPayJSON = new JSONObject(bulkBillPayResponse);
			JSONArray records = CommonUtils.getFirstOccuringArray(bulkBillPayJSON);
			billpayDTOs = JSONUtils.parseAsList(records.toString(), BillPayTransactionDTO.class);
        }
        catch(JSONException e) {
            LOG.error("Failed to create billpay transaction: ", e);
            return null;
        }
        catch(Exception e) {
            LOG.error("Caught exception at create billpay transaction: ", e);
            return null;
        }

        return billpayDTOs;
    }

	@Override
	public BillPayTransactionDTO fetchTranscationEntry(String transactionId) {
		
		List<BillPayTransactionDTO> billpayTransactionDTO = null;
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BILLPAYTRANSFERS_GET;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "transactionId" + DBPUtilitiesConstants.EQUAL + transactionId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		
		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject jsonRsponse = new JSONObject(fetchResponse);
			JSONArray billpayJsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);
			billpayTransactionDTO = JSONUtils.parseAsList(billpayJsonArray.toString(), BillPayTransactionDTO.class);
		}
		
		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching the billpaytransaction",jsonExp);
			return null;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while fetching the billpaytransaction",exp);
			return null;
		}
		
		if(billpayTransactionDTO != null && billpayTransactionDTO.size() != 0)
			return billpayTransactionDTO.get(0);
		
		return null;
	}

	@Override
	public boolean deleteTransactionAtDBX(String transactionId) {
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BILLPAYTRANSFERS_DELETE;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("transactionId", transactionId);
		
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
			LOG.error("JSONExcpetion occured while deleting the billpaytransaction",jsonExp);
			return false;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while deleting the billpaytransaction",exp);
			return false;
		}
		
		return false;	
	}

	@Override
	public BillPayTransactionDTO fetchExecutedTranscationEntry(String confirmationNumber, List<String> companyIds, String createdby, String legalEntityId) {
		
		List<BillPayTransactionDTO> billpayTransactionDTO = null;
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BILLPAYTRANSFERS_GET;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + confirmationNumber;
		
		String innerFilter = "";
		
		if (CollectionUtils.isNotEmpty(companyIds))
			innerFilter = innerFilter + DBPUtilitiesConstants.OPEN_BRACE + 
			"companyId" + DBPUtilitiesConstants.EQUAL + 
			String.join(DBPUtilitiesConstants.OR + "companyId" + DBPUtilitiesConstants.EQUAL, companyIds) 
			+ DBPUtilitiesConstants.CLOSE_BRACE;
		
		if(StringUtils.isNotBlank(innerFilter) && StringUtils.isNotBlank(createdby))
			innerFilter = innerFilter + DBPUtilitiesConstants.OR ;
		
		if(StringUtils.isNotBlank(createdby))
			innerFilter = innerFilter + "createdby" + DBPUtilitiesConstants.EQUAL + createdby;
		
		if (StringUtils.isNotBlank(legalEntityId))
			innerFilter = innerFilter + DBPUtilitiesConstants.AND + DBPUtilitiesConstants.OPEN_BRACE + "legalEntityId" 
		    + DBPUtilitiesConstants.EQUAL + legalEntityId + DBPUtilitiesConstants.CLOSE_BRACE;
		
		if(StringUtils.isNotBlank(innerFilter))
			filter = filter + DBPUtilitiesConstants.AND + DBPUtilitiesConstants.OPEN_BRACE + innerFilter + DBPUtilitiesConstants.CLOSE_BRACE;
		
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);
		
		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject jsonRsponse = new JSONObject(fetchResponse);
			JSONArray billpayJsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);
			billpayTransactionDTO = JSONUtils.parseAsList(billpayJsonArray.toString(), BillPayTransactionDTO.class);
		}
		
		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching the billpaytransaction",jsonExp);
			return null;
		}
		catch(Exception exp) {
			LOG.error("Excpetion occured while fetching the billpaytransaction",exp);
			return null;
		}
		
		if(billpayTransactionDTO != null && billpayTransactionDTO.size() != 0)
			return billpayTransactionDTO.get(0);
		
		return null;
	}
	
	@Override
	public void executeTransactionAfterApproval(String transactionId, String featureActionId, DataControllerRequest request) {
		
		ApprovalQueueBusinessDelegate approvalDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);			
		String customerId = CustomerSession.getCustomerId(customer);
		
		BillPayTransactionDTO transactionDTO = fetchTransactionById(transactionId, request);
		if(transactionDTO == null) {
			LOG.error("Failed to fetch the wire transaction entry from table: ");
			updateStatus(transactionId, TransactionStatusEnum.FAILED.getStatus(), null);
			return;
		}
		/*if(! transactionDTO.getStatus().equals(TransactionStatusEnum.APPROVED.getStatus())) {
			LOG.error("Transaction is not Approved ");
			return;
		}*/
		String confirmationNumber = transactionDTO.getConfirmationNumber();
		String companyId = transactionDTO.getCompanyId();
		String requestId = transactionDTO.getRequestId();
		String date = transactionDTO.getScheduledDate() == null ? 
				(transactionDTO.getProcessingDate() == null ? 
						(transactionDTO.getFrequencystartdate() == null ? 
								application.getServerTimeStamp()
								: transactionDTO.getFrequencystartdate())
						: transactionDTO.getProcessingDate()) 
				: transactionDTO.getScheduledDate();
		
		try {
			transactionDTO.setAmount(Double.parseDouble(transactionDTO.getTransactionAmount()));
		} catch (Exception e) {
			LOG.error("Invalid amount value", e);
			return;
		}
		
		//Transaction limit checks on amount and featureactionId
		TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
		transactionStatusDTO.setCustomerId(transactionDTO.getCreatedby());
		transactionStatusDTO.setCompanyId(companyId);
		transactionStatusDTO.setAccountId(transactionDTO.getFromAccountNumber());
		transactionStatusDTO.setAmount(transactionDTO.getAmount());
		transactionStatusDTO.setStatus(TransactionStatusEnum.APPROVED);
		transactionStatusDTO.setDate(date);
		transactionStatusDTO.setFeatureActionID(featureActionId);
		transactionStatusDTO.setConfirmationNumber(confirmationNumber);
		transactionStatusDTO.setServiceCharge(null);
		transactionStatusDTO.setTransactionCurrency(null);
		
		transactionStatusDTO = approvalDelegate.validateForApprovals(transactionStatusDTO, request);
		if(transactionStatusDTO == null) {			
			LOG.error("Failed to validate limits ");
			updateStatus(transactionId, TransactionStatusEnum.FAILED.getStatus(), null);
			if(requestId != null) {
				approvalDelegate.updateBBRequestStatus(requestId, TransactionStatusEnum.FAILED.getStatus(), request);
				approvalDelegate.logActedRequest(requestId, companyId, TransactionStatusEnum.FAILED.getStatus(), "Failed to validate limits Before executing at core", customerId,
					TransactionStatusEnum.FAILED.getStatus());
			}
			return;
		}
		
		if (transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
			LOG.error("Error occured while validating limits");
			updateStatus(transactionId, TransactionStatusEnum.FAILED.getStatus(), null);
			if(requestId != null) {
				approvalDelegate.updateBBRequestStatus(requestId, TransactionStatusEnum.FAILED.getStatus(), request);
				approvalDelegate.logActedRequest(requestId, companyId, TransactionStatusEnum.FAILED.getStatus(), transactionStatusDTO.getDbpErrMsg(), customerId,
					TransactionStatusEnum.FAILED.getStatus());
			}
			return;
		}
		
		TransactionStatusEnum transactionStatus = transactionStatusDTO.getStatus();
		if(!TransactionStatusEnum.SENT.getStatus().equals(transactionStatus.getStatus())) {
			LOG.error("Not a valid status");
			updateStatus(transactionId, TransactionStatusEnum.FAILED.getStatus(), null);
			if(requestId != null) {
				approvalDelegate.updateBBRequestStatus(requestId, TransactionStatusEnum.FAILED.getStatus(), request);
				approvalDelegate.logActedRequest(requestId, companyId, TransactionStatusEnum.FAILED.getStatus(), "Not a valid status: " + transactionStatusDTO.getStatus(), customerId,
					TransactionStatusEnum.FAILED.getStatus());
			}
			return;
		}
		
		transactionDTO = approveTransaction(transactionId, request, transactionDTO.getFrequencyTypeId());
		
		String referenceId = null;
		
		String reason = null;
		if(transactionDTO == null) {
			reason = "Failed at backend";
		}
		else if(transactionDTO.getDbpErrMsg() != null && !transactionDTO.getDbpErrMsg().isEmpty()) {
			reason = transactionDTO.getDbpErrMsg();
		}
		else {
			referenceId = transactionDTO.getReferenceId();
		}
		
		if(referenceId == null || referenceId.isEmpty()) {
			LOG.error("create or edit transaction failed ");
			updateStatus(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
			if(requestId != null) {
				approvalDelegate.updateBBRequestStatus(requestId, TransactionStatusEnum.FAILED.getStatus(), request);
				approvalDelegate.logActedRequest(requestId, companyId, TransactionStatusEnum.FAILED.getStatus(), "Create transaction failed at backend", customerId,
						TransactionStatusEnum.FAILED.getStatus());
			}
		}
		else {
			updateStatus(transactionId, TransactionStatusEnum.EXECUTED.getStatus(), referenceId);
		}

		// ADP-7058 update additional meta data
		try{
			approvalDelegate.updateAdditionalMetaForApprovalRequest(transactionStatusDTO.getRequestId(), request);
		} catch(Exception e){
			LOG.error(e);
		}
	}

	@Override
	public List<ApprovalRequestDTO> fetchBillPayTransactionsWithApprovalInfo(List<BBRequestDTO> requests, DataControllerRequest dcr) {

		Set<String> billPayTransIds = new HashSet<String>();
		List<ApprovalRequestDTO> billPayTransactions = new ArrayList<ApprovalRequestDTO>();

		if (CollectionUtils.isEmpty(requests))
			return billPayTransactions;

		for (BBRequestDTO bBRequestDTO : requests) {
			if(StringUtils.isNotBlank(bBRequestDTO.getTransactionId()))
				billPayTransIds.add(bBRequestDTO.getTransactionId());
		}
		
		String filter = "";
		if (!application.getIsStateManagementAvailableFromCache()) {
			filter = "transactionId" + DBPUtilitiesConstants.EQUAL + 
					String.join(DBPUtilitiesConstants.OR + "transactionId" + DBPUtilitiesConstants.EQUAL, billPayTransIds);
			billPayTransactions = fetchBillPayTransactionsForApprovalInfo(filter, dcr);
			billPayTransactions = (new FilterDTO()).merge(billPayTransactions, requests, "transactionId=transactionId", "requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,featureActionId");
		}
		else {
			filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + 
					String.join(DBPUtilitiesConstants.OR + "confirmationNumber" + DBPUtilitiesConstants.EQUAL, billPayTransIds);
			billPayTransactions = fetchBillPayTransactionsForApprovalInfo(filter,dcr);
			List<ApprovalRequestDTO> backendData = backendDelegate.fetchBackendTransactionsForApproval(billPayTransIds, dcr);
			if(CollectionUtils.isNotEmpty(backendData)) {
				billPayTransactions = (new FilterDTO()).merge(billPayTransactions, backendData,"confirmationNumber=transactionId", "");
			}
			billPayTransactions = (new FilterDTO()).merge(billPayTransactions, requests, "confirmationNumber=transactionId", "transactionId,requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,featureActionId");
		}
		
		
		return billPayTransactions;
	}
	
	@Override
	public List<ApprovalRequestDTO> fetchBillPayTransactionsForApprovalInfo(String filter, DataControllerRequest dcr) {

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_BILLPAYTRANSFERS_GET;
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		List<ApprovalRequestDTO> billPayTransactions = new ArrayList<ApprovalRequestDTO>();

		requestParameters.put(DBPUtilitiesConstants.FILTER, filter);

		try {
			String billPayResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			
			JSONObject responseObj = new JSONObject(billPayResponse);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
			billPayTransactions = JSONUtils.parseAsList(jsonArray.toString(), ApprovalRequestDTO.class);
		} 
		catch (JSONException je) {
			LOG.error("Failed to fetch BillPayTransactions : ", je);
		} 
		catch (Exception e) {
			LOG.error("Caught exception while fetching BillPayTransactions: ", e);
		}
		
		return billPayTransactions;
	}

	@Override
	public BillPayTransactionDTO createPendingTransaction(BillPayTransactionDTO billpaytransferdto,
			DataControllerRequest request) {
		
		if (!application.getIsStateManagementAvailableFromCache()) {
			billpaytransferdto.setReferenceId(Constants.REFERENCE_KEY + billpaytransferdto.getTransactionId());
			return billpaytransferdto;
		}
		billpaytransferdto.setTransactionId(null);
		
		BillPayTransactionBackendDTO billpayBackendDTO = new BillPayTransactionBackendDTO();
		billpayBackendDTO = billpayBackendDTO.convert(billpaytransferdto);
		
		return backendDelegate.createPendingTransaction(billpayBackendDTO, request);
	}

	@Override
	public BillPayTransactionDTO approveTransaction(String referenceId, DataControllerRequest request, String frequency) {

		if (!application.getIsStateManagementAvailableFromCache()) {
			BillPayTransactionBackendDTO backendObj= new BillPayTransactionBackendDTO();
			BillPayTransactionBusinessDelegate billpayTransactionDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(BillPayTransactionBusinessDelegate.class);
			BillPayTransactionDTO dbxObj = billpayTransactionDelegate.fetchTransactionById(referenceId, request);
			backendObj = backendObj.convert(dbxObj);
			try {
				backendObj.setAmount(Double.parseDouble(backendObj.getTransactionAmount()));
			} catch (Exception e) {
				LOG.error("Invalid amount value", e);
				return null;
			}
			String confirmationNumber = dbxObj.getConfirmationNumber();
			
			if(!StringUtils.isEmpty(confirmationNumber) && !confirmationNumber.startsWith(Constants.REFERENCE_KEY)) {
				backendObj.setTransactionId(dbxObj.getConfirmationNumber());
				return backendDelegate.editTransactionWithoutApproval(backendObj, request);
			}
			else {
				return backendDelegate.createTransactionWithoutApproval(backendObj, request);
			}
		}
		updateStatus(referenceId, TransactionStatusEnum.APPROVED.getStatus(), null);
		return backendDelegate.approveTransaction(referenceId, request, frequency);
	}

	@Override
	public BillPayTransactionDTO rejectTransaction(String referenceId, String transactionType, DataControllerRequest request) {
		if (!application.getIsStateManagementAvailableFromCache()) {
			return updateStatus(referenceId, TransactionStatusEnum.REJECTED.getStatus(), null);
		}
		BillPayTransactionDTO transactionObj =  fetchTransactionById(referenceId, request);
		
		if(transactionObj == null) {
			LOG.error("Failed to fetch the transaction entry from table: ");
			updateStatus(referenceId, TransactionStatusEnum.FAILED.getStatus(), null);
			return null;
		}
		
		updateStatus(referenceId, TransactionStatusEnum.REJECTED.getStatus(), null);
		return backendDelegate.rejectTransaction(referenceId, transactionObj.getTransactionType(), request, transactionObj.getFrequencyTypeId());
	}

	@Override
	public BillPayTransactionDTO withdrawTransaction(String referenceId, String transactionType, DataControllerRequest request) {
		if (!application.getIsStateManagementAvailableFromCache()) {
			return updateStatus(referenceId, TransactionStatusEnum.WITHDRAWN.getStatus(), null);
		}
		BillPayTransactionDTO transactionObj =  fetchTransactionById(referenceId, request);
		
		if(transactionObj == null) {
			LOG.error("Failed to fetch the transaction entry from table: ");
			updateStatus(referenceId, TransactionStatusEnum.FAILED.getStatus(), null);
			return null;
		}
		
		updateStatus(referenceId, TransactionStatusEnum.WITHDRAWN.getStatus(), null);
		return backendDelegate.withdrawTransaction(referenceId, transactionObj.getTransactionType(), request, transactionObj.getFrequencyTypeId());
	}

	@Override
	public BillPayTransactionDTO fetchTransactionById(String referenceId, DataControllerRequest dataControllerRequest) {
		if (!application.getIsStateManagementAvailableFromCache()) {
			return fetchTranscationEntry(referenceId);
		}
		
		BillPayTransactionDTO backendData = backendDelegate.fetchTransactionById(referenceId, dataControllerRequest);
		BillPayTransactionDTO dbxData = fetchExecutedTranscationEntry(referenceId, null, null, null);
		
		if(backendData == null || StringUtils.isEmpty(backendData.getDbpErrMsg())) {
			return dbxData;
		}
		
		return (new FilterDTO()).merge(Arrays.asList(dbxData),Arrays.asList(backendData),"confirmationNumber=transactionId", "").get(0);
	}
	
	@Override
	public BillPayTransactionDTO editPendingTransaction(BillPayTransactionDTO input, DataControllerRequest request) {

		if (!application.getIsStateManagementAvailableFromCache()) {
			input.setReferenceId(input.getTransactionId());
			return input;
		}
		input.setTransactionId(null);
		BillPayTransactionBackendDTO backenObj = new BillPayTransactionBackendDTO();
		backenObj = backenObj.convert(input);
		return backendDelegate.editTransactionWithApproval(backenObj, request);
	}

}
