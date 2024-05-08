package com.temenos.dbx.product.transactionservices.backenddelegate.impl;

import java.io.IOException;
import java.util.ArrayList;
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

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRequestDTO;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.InterBankFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.IntraBankFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferDTO;
import com.temenos.dbx.product.transactionservices.dto.IntraBankFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.IntraBankFundTransferDTO;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferDTO;

/**
 * 
 * @author KH2624
 * @version 1.0
 * implements {@link InterBankFundTransferBackendDelegate}
 */

public class IntraBankFundTransferBackendDelegateImpl implements IntraBankFundTransferBackendDelegate {

	private static final Logger LOG = LogManager.getLogger(IntraBankFundTransferBackendDelegateImpl.class);

	@Override
	public IntraBankFundTransferDTO createTransactionWithoutApproval(IntraBankFundTransferBackendDTO intrabankfundtransferbackenddto, DataControllerRequest request) {

		String serviceName = ServiceId.INTRA_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTRA_BANK_FUND_TRANSFER_BACKEND_WITHOUT_AAPROVER;

		String createResponse = null;
		IntraBankFundTransferDTO intrabankfundtransferdto = null;

		Map<String, Object> requestParameters;
		try {
			intrabankfundtransferbackenddto.setStatus(DBPUtilitiesConstants.TRANSACTION_STATUS_SUCCESSFUL);
			requestParameters = JSONUtils.parseAsMap(new JSONObject(intrabankfundtransferbackenddto).toString(), String.class, Object.class);			
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
					withRequestHeaders(request.getHeaderMap()).
					withDataControllerRequest(request).
					build().getResponse();
			intrabankfundtransferdto = JSONUtils.parse(createResponse, IntraBankFundTransferDTO.class);
			if(intrabankfundtransferdto.getTransactionId() != null && !"".equals(intrabankfundtransferdto.getTransactionId())) {
				intrabankfundtransferdto.setReferenceId(intrabankfundtransferdto.getTransactionId());
			}
		}
		catch (JSONException e) {
			LOG.error("Failed to create intrabank transaction: ", e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at create intrabank transaction: ", e);
			return null;
		}

		return intrabankfundtransferdto;
	}

	@Override
	public IntraBankFundTransferDTO editTransactionWithoutApproval(IntraBankFundTransferBackendDTO intrabankTransactionBackendDTO, DataControllerRequest request) {

		String serviceName = ServiceId.INTRA_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTRA_BANK_FUND_TRANSFER_BACKEND_EDIT_WITHOUT_APPROVER;

		String createResponse = null;
		IntraBankFundTransferDTO intrabankTransactionDTO = null;

		Map<String, Object> requestParameters;
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(intrabankTransactionBackendDTO).toString(), String.class, Object.class);
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
					withRequestHeaders(request.getHeaderMap()).
					withDataControllerRequest(request).
					build().getResponse();
			JSONObject editResponse = new JSONObject(createResponse);
			if(editResponse.has(Constants.OPSTATUS) && editResponse.getInt(Constants.OPSTATUS) ==0 && (editResponse.has("transactionId") || editResponse.has("referenceId"))) {
				if(editResponse.has("transactionId") && !"".equals(editResponse.getString("transactionId"))) {
					editResponse.put("referenceId", editResponse.getString("transactionId"));
				}
			}
			if(editResponse != null) 
				intrabankTransactionDTO = JSONUtils.parse(editResponse.toString(), IntraBankFundTransferDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to edit intrabank transaction: ", e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at edit intrabank transaction: ", e);
			return null;
		}

		return intrabankTransactionDTO;
	}

	@Override
	public IntraBankFundTransferDTO deleteTransactionWithoutApproval(String transactionId, String transactionType, String frequencyType, DataControllerRequest dataControllerRequest) {

		String serviceName = ServiceId.INTRA_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTRA_BANK_FUND_TRANSFER_BACKEND_DELETE_WITHOUT_APPROVER;

		String deleteResponse = null;
		IntraBankFundTransferDTO intrabankDTO = new IntraBankFundTransferDTO();
		intrabankDTO.setDbpErrMsg(ErrorConstants.TRANSACTION_DELETE_FAILED_AT_BACKEND);

		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put("transactionId", transactionId);
		requestParameters.put("transactionType", transactionType);
		requestParameters.put("frequencyType", frequencyType);

		try {

			deleteResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(dataControllerRequest.getHeaderMap()).
					withDataControllerRequest(dataControllerRequest).
					build().getResponse();

			JSONObject deleteResponseObj = new JSONObject(deleteResponse);
			if(deleteResponseObj.has(Constants.OPSTATUS) && deleteResponseObj.getInt(Constants.OPSTATUS) ==0 && (deleteResponseObj.has("transactionId") || deleteResponseObj.has("referenceId"))) {
				if(deleteResponseObj.has("transactionId") && !"".equals(deleteResponseObj.getString("transactionId"))) {
					deleteResponseObj.put("referenceId", deleteResponseObj.getString("transactionId"));
				}
			}
			if(deleteResponseObj != null) 
				intrabankDTO = JSONUtils.parse(deleteResponseObj.toString(), IntraBankFundTransferDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to delete intra bank fund transaction: ", e);
			return intrabankDTO;
		}
		catch (Exception e) {
			LOG.error("Caught exception at delete intra bank fund transaction: ", e);
			return intrabankDTO;
		}

		return intrabankDTO;
	}

	@Override
	public IntraBankFundTransferDTO cancelTransactionWithoutApproval(String transactionId, DataControllerRequest dataControllerRequest) {

		String serviceName = ServiceId.INTRA_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTRA_BANK_FUND_TRANSFER_BACKEND_CANCEL_OCCURRENCE_WITHOUT_APPROVER;

		String cancelResponse = null;
		IntraBankFundTransferDTO intrabankDTO = new IntraBankFundTransferDTO();
		intrabankDTO.setDbpErrMsg(ErrorConstants.TRANSACTION_CANCEL_OCCURRENCE_FAILED_AT_BACKEND);

		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put("transactionId", transactionId);

		try {

			cancelResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(dataControllerRequest.getHeaderMap()).
					withDataControllerRequest(dataControllerRequest).
					build().getResponse();

			JSONObject cancelResponseObj = new JSONObject(cancelResponse);
			if(cancelResponseObj.has(Constants.OPSTATUS) && cancelResponseObj.getInt(Constants.OPSTATUS) ==0 && (cancelResponseObj.has("transactionId") || cancelResponseObj.has("referenceId"))) {
				if(cancelResponseObj.has("transactionId") && !"".equals(cancelResponseObj.getString("transactionId"))) {
					cancelResponseObj.put("referenceId", cancelResponseObj.getString("transactionId"));
				}
			}
			if(cancelResponseObj != null)
				intrabankDTO = JSONUtils.parse(cancelResponseObj.toString(), IntraBankFundTransferDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to cancel intra bank transaction occurrence: ", e);
			return intrabankDTO;
		}
		catch (Exception e) {
			LOG.error("Caught exception at cancel intra bank transaction occurrence: ", e);
			return intrabankDTO;
		}

		return intrabankDTO;
	}
	
	@Override
	public IntraBankFundTransferDTO createPendingTransaction(IntraBankFundTransferBackendDTO intraBankFundTransferBackendDTO, DataControllerRequest dcr) {

		IntraBankFundTransferDTO intraBankFundTransferDTO = null;

		String serviceName = ServiceId.INTRA_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTRA_BANK_FUND_TRANSFER_BACKEND_WITH_AAPROVER;

		String createResponse = null;
		Map<String, Object> requestParameters;
		try {
			
			intraBankFundTransferBackendDTO.setStatus(DBPUtilitiesConstants.TRANSACTION_STATUS_PENDING);
			requestParameters = JSONUtils.parseAsMap(new JSONObject(intraBankFundTransferBackendDTO).toString(), String.class, Object.class);
			requestParameters.put("isPending", "1");
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: ", e);
			return null;
		}

		try {
			createResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(requestParameters)
					.withRequestHeaders(dcr.getHeaderMap())
					.withDataControllerRequest(dcr)
					.build().getResponse();
			intraBankFundTransferDTO = JSONUtils.parse(createResponse, IntraBankFundTransferDTO.class);
			if (StringUtils.isNotEmpty(intraBankFundTransferDTO.getReferenceId())) {
				intraBankFundTransferDTO.setTransactionId(intraBankFundTransferDTO.getReferenceId());
			}
		} catch (JSONException e) {
			LOG.error("Failed to create Intra Bank transaction with pending status: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at create Intra bank transaction with pending status: ", e);
			return null;
		}		

		return intraBankFundTransferDTO;

	}

	@Override
	public IntraBankFundTransferDTO approveTransaction(String referenceId, DataControllerRequest dcr, String frequency) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			intraBankFundTransferDTO = null;
			IntraBankFundTransferBackendDTO backendObj = new IntraBankFundTransferBackendDTO();
			
			backendObj.setTransactionId(referenceId);
			backendObj.setStatus(DBPUtilitiesConstants.TRANSACTION_STATUS_SUCCESSFUL);
			intraBankFundTransferDTO = updateTransactionStatus(backendObj, dcr);
	
			return intraBankFundTransferDTO;
		}
		else {			
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; 
			String operationName = OperationName.APPROVE_TRANSACTION; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", frequency);
			try {
				String approveresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(dcr.getHeaderMap())
						.withDataControllerRequest(dcr)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(approveresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to create withdraw intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to create withdraw intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;
	}

	@Override
	public IntraBankFundTransferDTO rejectTransaction(String referenceId, String transactionType, DataControllerRequest request, String frequency) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			return deleteTransactionWithoutApproval(referenceId, transactionType, frequency, request);
		}
		else {			
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; 
			String operationName = OperationName.REJECT_TRANSACTION; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("transactionType", transactionType);
			requestParameters.put("frequencyType", frequency);
			try {
				String rejectresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(rejectresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to create withdraw intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to create withdraw intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;
	}

	@Override
	public IntraBankFundTransferDTO withdrawTransaction(String referenceId, String transactionType, DataControllerRequest request, String frequency) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			return deleteTransactionWithoutApproval(referenceId, transactionType, frequency, request);
		}
		else {			
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; 
			String operationName = OperationName.WITHDRAW_TRANSACTION; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("transactionType", transactionType);
			requestParameters.put("frequencyType", frequency);
			try {
				String withdrawresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(withdrawresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to create withdraw intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to create withdraw intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;
	}

	@Override
	public IntraBankFundTransferDTO fetchTransactionById(String referenceId, DataControllerRequest dcr) {

		List<IntraBankFundTransferDTO> intraBankFundTransferDTO = null;
		
		String serviceName = ServiceId.	INTRA_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTRABANK_FUND_TRANSFER_BACKEND_GET;

		String response = null;
		Map<String, Object> requestParameters;
		try {
			IntraBankFundTransferBackendDTO backendObj = new IntraBankFundTransferBackendDTO();
			backendObj.setTransactionId(referenceId);
			requestParameters = JSONUtils.parseAsMap(new JSONObject(backendObj).toString(), String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: ", e);
			return null;
		}

		try {
			response = DBPServiceExecutorBuilder.builder()
					.withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(requestParameters)
					.withRequestHeaders(dcr.getHeaderMap())
					.withDataControllerRequest(dcr)
					.build().getResponse();
			JSONObject jsonRsponse = new JSONObject(response);
			JSONArray trJsonArray = CommonUtils.getFirstOccuringArray(jsonRsponse);
			intraBankFundTransferDTO = JSONUtils.parseAsList(trJsonArray.toString(), IntraBankFundTransferDTO.class);
		} catch (JSONException e) {
			LOG.error("Failed to create Intra bank transaction: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at create Intra bank transaction: ", e);
			return null;
		}

		if(intraBankFundTransferDTO != null && intraBankFundTransferDTO.size() != 0)
			return intraBankFundTransferDTO.get(0);
		
		return null;
	}

	private IntraBankFundTransferDTO updateTransactionStatus(IntraBankFundTransferBackendDTO input, DataControllerRequest request) {

		String serviceName = ServiceId.INTRA_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTRABANK_FUND_TRANSFER_BACKEND_UPDATE_STATUS;

		String updateStatus = null;
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;

		Map<String, Object> headerParams = request.getHeaderMap();
		Map<String, Object> requestParameters;
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(input).toString(),
					String.class, Object.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: ", e);
			return null;
		}
		try {
			updateStatus = DBPServiceExecutorBuilder.builder()
					.withServiceId(serviceName)
					.withObjectId(null)
					.withOperationId(operationName)
					.withRequestParameters(requestParameters)
					.withRequestHeaders(headerParams)
					.withDataControllerRequest(request)
					.build().getResponse();
			JSONObject editResponse = new JSONObject(updateStatus);

			if (editResponse != null)
				intraBankFundTransferDTO = JSONUtils.parse(editResponse.toString(), IntraBankFundTransferDTO.class);
		} catch (JSONException e) {
			LOG.error("Failed to update status of Intra Bank transaction: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at update status of Intra bank transaction: ", e);
			return null;
		}

		return intraBankFundTransferDTO;
	}
	
	@Override
	public IntraBankFundTransferDTO validateTransaction(IntraBankFundTransferBackendDTO input, DataControllerRequest dcr) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			IntraBankFundTransferDTO output = new IntraBankFundTransferDTO();
			try {
				output = JSONUtils.parse(new JSONObject(input).toString(), IntraBankFundTransferDTO.class);
			} catch (IOException e) {
				LOG.error("Caught exception at converting backenddto to dbxdto. ", e);
			}
			return output;
		}
		else {
			Map<String, Object> requestParameters;
			try {
				requestParameters = JSONUtils.parseAsMap(new JSONObject(input).toString(), String.class, Object.class);
			} catch (IOException e) {
				LOG.error("Error occured while fetching the input params: ", e);
				return null;
			}
			try {
				String serviceName = ServiceId.VALIDATE_TRANSACTION_SERVICE;
				String operationName = OperationName.VALIDATE_TRANSACTION;
				requestParameters.put("validate", "true");
				String response = DBPServiceExecutorBuilder.builder()
									.withServiceId(serviceName)
									.withObjectId(null)
									.withOperationId(operationName)
									.withRequestParameters(requestParameters)
									.withRequestHeaders(dcr.getHeaderMap())
									.withDataControllerRequest(dcr)
									.build().getResponse();
						
						//TransactionBackendDelegateimpl.validateTransaction(requestParameters, request);
				return JSONUtils.parse(response, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException | DBPApplicationException e) {
				LOG.error("Failed to validate own account fund transfer: ", e);
				return null;
			}
		}
	}

	@Override
	public List<ApprovalRequestDTO> fetchBackendTransactionsForApproval(Set<String> intraBankTransIds,
			DataControllerRequest dcr) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_INTRABANKTRANSFERS_GET;
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		List<ApprovalRequestDTO> transactions = new ArrayList<ApprovalRequestDTO>();

		String filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + 
				String.join(DBPUtilitiesConstants.OR + "confirmationNumber" + DBPUtilitiesConstants.EQUAL, intraBankTransIds);
		requestParameters.put(DBPUtilitiesConstants.FILTER, filter);
		
		try {
			String intraBankResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			
			JSONObject responseObj = new JSONObject(intraBankResponse);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
			transactions = JSONUtils.parseAsList(jsonArray.toString(), ApprovalRequestDTO.class);
			
			transactions.forEach((transaction) ->{
				transaction.setTransactionId(transaction.getConfirmationNumber());
			});
		} 
		catch (JSONException je) {
			LOG.error("Failed to fetch IntraBankTransactions : ", je);
		} 
		catch (Exception e) {
			LOG.error("Caught exception while fetching IntraBankTransactions: ", e);
		}
		
		return transactions;
	}
	
	@Override
	public IntraBankFundTransferDTO editTransactionWithApproval(IntraBankFundTransferBackendDTO backendObj, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			intraBankFundTransferDTO = null;
			backendObj.setStatus(DBPUtilitiesConstants.TRANSACTION_STATUS_PENDING);
			intraBankFundTransferDTO = updateTransactionStatus(backendObj, request);
			return intraBankFundTransferDTO;
		}
		else {			
			String serviceName = ServiceId.INTRA_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE; 
			String operationName = OperationName.INTRA_BANK_FUND_TRANSFER_BACKEND_EDIT_WITH_APPROVER; 
			Map<String, Object> requestParameters;
			try {
				requestParameters = JSONUtils.parseAsMap(new JSONObject(backendObj).toString(), String.class, Object.class);
			} catch (IOException e) {
				LOG.error("Error occured while fetching the input params: ", e);
				return null;
			}
			try {
				String editresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(editresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to edit intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to edit intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;		
	}

	@Override
	public IntraBankFundTransferDTO cancelTransactionWithApproval(String referenceId, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			intraBankFundTransferDTO = new IntraBankFundTransferDTO();
			intraBankFundTransferDTO.setReferenceId(referenceId);
			return intraBankFundTransferDTO;
		}
		else {			
			String serviceName = ServiceId.INTRA_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE; 
			String operationName = OperationName.INTRA_BANK_FUND_TRANSFER_BACKEND_CANCEL_OCCURRENCE_WITH_APPROVER; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			try {
				String cancelresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(cancelresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to cancel intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to cancel intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;			
	}

	@Override
	public IntraBankFundTransferDTO approveCancellation(String referenceId, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			return cancelTransactionWithoutApproval(referenceId, request);
		}
		else {			
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; 
			String operationName = OperationName.APPROVE_TRANSACTION; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", "Once");
			try {
				String approveresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(approveresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to approve intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to approve intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;		
	}

	@Override
	public IntraBankFundTransferDTO rejectCancellation(String referenceId, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			intraBankFundTransferDTO = new IntraBankFundTransferDTO();
			intraBankFundTransferDTO.setReferenceId(referenceId);
			return intraBankFundTransferDTO;
		}
		else {			
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; 
			String operationName = OperationName.REJECT_TRANSACTION; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", "Once");
			try {
				String rejectresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(rejectresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to reject intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to reject intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;		
	}

	@Override
	public IntraBankFundTransferDTO withdrawCancellation(String referenceId, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			intraBankFundTransferDTO = new IntraBankFundTransferDTO();
			intraBankFundTransferDTO.setReferenceId(referenceId);
			return intraBankFundTransferDTO;
		}
		else {			
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; 
			String operationName = OperationName.WITHDRAW_TRANSACTION; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", "Once");
			try {
				String withdrawresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(withdrawresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to withdraw intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to withdraw intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;		
	}

	@Override
	public IntraBankFundTransferDTO deleteTransactionWithApproval(String referenceId, String transactionType, String frequencyType, DataControllerRequest dataControllerRequest) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			intraBankFundTransferDTO = new IntraBankFundTransferDTO();
			intraBankFundTransferDTO.setReferenceId(referenceId);
			return intraBankFundTransferDTO;
		}
		else {			
			String serviceName = ServiceId.INTRA_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE; 
			String operationName = OperationName.INTRA_BANK_FUND_TRANSFER_BACKEND_DELETE_WITH_APPROVER; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("transactionType", transactionType);
			requestParameters.put("frequencyType", frequencyType);
			try {
				String deleteresponse = DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(dataControllerRequest.getHeaderMap())
						.withDataControllerRequest(dataControllerRequest)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(deleteresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to delete intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to delete intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;	
	}

	@Override
	public IntraBankFundTransferDTO approveDeletion(String referenceId, String transactionType,	String frequencyType, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			return deleteTransactionWithoutApproval(referenceId, transactionType, frequencyType, request);
		}
		else {
			
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; 
			String operationName = OperationName.APPROVE_TRANSACTION; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("transactionType", transactionType);
			requestParameters.put("frequencyType", frequencyType);
			try {
				String approveresponse = DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(approveresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to approve intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to approve intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;		
	}

	@Override
	public IntraBankFundTransferDTO rejectDeletion(String referenceId, String frequencyType, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			intraBankFundTransferDTO = new IntraBankFundTransferDTO();
			intraBankFundTransferDTO.setReferenceId(referenceId);
			return intraBankFundTransferDTO;
		}
		else {			
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; 
			String operationName = OperationName.REJECT_TRANSACTION; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", frequencyType);
			try {
				String rejectresponse = DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(rejectresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to reject intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to reject intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;		
	}

	@Override
	public IntraBankFundTransferDTO withdrawDeletion(String referenceId, String frequencyType, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		IntraBankFundTransferDTO intraBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			intraBankFundTransferDTO = new IntraBankFundTransferDTO();
			intraBankFundTransferDTO.setReferenceId(referenceId);
			return intraBankFundTransferDTO;
		}
		else {		
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; 
			String operationName = OperationName.WITHDRAW_TRANSACTION; 
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", frequencyType);
			try {
				String withdrawresponse = DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				intraBankFundTransferDTO =  JSONUtils.parse(withdrawresponse, IntraBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to withdraw intra account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to withdraw intra account fund transfer: ", e);
			}
		}
		return intraBankFundTransferDTO;
	}	
}