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
import com.infinity.dbx.temenos.constants.TemenosConstants;
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
import com.temenos.dbx.product.transactionservices.businessdelegate.impl.InterBankFundTransferBusinessDelegateImpl;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferDTO;
import com.temenos.dbx.product.transactionservices.dto.InternationalFundTransferDTO;
import com.temenos.dbx.product.transactionservices.dto.OwnAccountFundTransferDTO;

/**
 * 
 * @author KH2624
 * @version 1.0
 * implements {@link InterBankFundTransferBackendDelegate}
 */

public class InterBankFundTransferBackendDelegateImpl implements InterBankFundTransferBackendDelegate {

	private static final Logger LOG = LogManager.getLogger(InterBankFundTransferBusinessDelegateImpl.class);
	
	@Override
	public InterBankFundTransferDTO createTransactionWithoutApproval(InterBankFundTransferBackendDTO interBankFundTransferBackendDTO, DataControllerRequest request) {

		String serviceName = ServiceId.INTER_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTER_BANK_FUND_TRANSFER_BACKEND_WITHOUT_APPROVER;

		String createResponse = null;
		InterBankFundTransferDTO interbankfundtransferdto = null;
		
		Map<String, Object> requestParameters;
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(interBankFundTransferBackendDTO).toString(), 
					String.class, Object.class);
			
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
			interbankfundtransferdto = JSONUtils.parse(createResponse, InterBankFundTransferDTO.class);
			if(interbankfundtransferdto.getTransactionId() != null && !"".equals(interbankfundtransferdto.getTransactionId())) {
				interbankfundtransferdto.setReferenceId(interbankfundtransferdto.getTransactionId());
			}
		}
		catch (JSONException e) {
			LOG.error("Failed to create interbank transaction: ", e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at create interbank transaction: ", e);
			return null;
		}
		
		return interbankfundtransferdto;
	}

	@Override
	public InterBankFundTransferDTO editTransactionWithoutApproval(
			InterBankFundTransferBackendDTO interbankTransactionBackendDTO, DataControllerRequest request) {
		
		String serviceName = ServiceId.INTER_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTER_BANK_FUND_TRANSFER_BACKEND_EDIT_WITHOUT_APPROVER;

		String createResponse = null;
		InterBankFundTransferDTO interbankTransactionDTO = null;
		
		Map<String, Object> requestParameters;
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(interbankTransactionBackendDTO).toString(), String.class, Object.class);
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
				interbankTransactionDTO = JSONUtils.parse(editResponse.toString(), InterBankFundTransferDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to edit interbank transaction: ", e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at edit interbank transaction: ", e);
			return null;
		}
		
		return interbankTransactionDTO;
	}

	@Override
	public InterBankFundTransferDTO deleteTransactionWithoutApproval(String transactionId, String transactionType, String frequencyType, 
			DataControllerRequest dataControllerRequest) {

		String serviceName = ServiceId.INTER_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTER_BANK_FUND_TRANSFER_BACKEND_DELETE_WITHOUT_APPROVER;

		String deleteResponse = null;
		InterBankFundTransferDTO interbankDTO = new InterBankFundTransferDTO();
		interbankDTO.setDbpErrMsg(ErrorConstants.TRANSACTION_DELETE_FAILED_AT_BACKEND);

		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put("transactionId", transactionId);
		requestParameters.put("transactionType", transactionType);
		requestParameters.put("frequencyType", frequencyType);

		try {

			deleteResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(dataControllerRequest.getHeaderMap())
					.withDataControllerRequest(dataControllerRequest).build().getResponse();

			JSONObject deleteResponseObj = new JSONObject(deleteResponse);
			if (deleteResponseObj.has(Constants.OPSTATUS) && deleteResponseObj.getInt(Constants.OPSTATUS) == 0
					&& (deleteResponseObj.has("transactionId") || deleteResponseObj.has("referenceId"))) {
				if (deleteResponseObj.has("transactionId")
						&& !"".equals(deleteResponseObj.getString("transactionId"))) {
					deleteResponseObj.put("referenceId", deleteResponseObj.getString("transactionId"));
				}
			}
			if (deleteResponseObj != null)
				interbankDTO = JSONUtils.parse(deleteResponseObj.toString(), InterBankFundTransferDTO.class);
		} catch (JSONException e) {
			LOG.error("Failed to delete inter bank transaction: ", e);
			return interbankDTO;
		} catch (Exception e) {
			LOG.error("Caught exception at delete inter bank transaction: ", e);
			return interbankDTO;
		}

		return interbankDTO;
	}

	@Override
	public InterBankFundTransferDTO cancelTransactionWithoutApproval(String transactionId,
			DataControllerRequest dataControllerRequest) {
		String serviceName = ServiceId.INTER_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTER_BANK_FUND_TRANSFER_BACKEND_CANCEL_OCCURRENCE_WITHOUT_APPROVER;

		String cancelResponse = null;
		InterBankFundTransferDTO interbankDTO = new InterBankFundTransferDTO();
		interbankDTO.setDbpErrMsg(ErrorConstants.TRANSACTION_CANCEL_OCCURRENCE_FAILED_AT_BACKEND);

		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put("transactionId", transactionId);

		try {

			cancelResponse = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters)
					.withRequestHeaders(dataControllerRequest.getHeaderMap())
					.withDataControllerRequest(dataControllerRequest).build().getResponse();

			JSONObject cancelResponseObj = new JSONObject(cancelResponse);
			if (cancelResponseObj.has(Constants.OPSTATUS) && cancelResponseObj.getInt(Constants.OPSTATUS) == 0
					&& (cancelResponseObj.has("transactionId") || cancelResponseObj.has("referenceId"))) {
				if (cancelResponseObj.has("transactionId")
						&& !"".equals(cancelResponseObj.getString("transactionId"))) {
					cancelResponseObj.put("referenceId", cancelResponseObj.getString("transactionId"));
				}
			}
			if (cancelResponseObj != null)
				interbankDTO = JSONUtils.parse(cancelResponseObj.toString(), InterBankFundTransferDTO.class);
		} catch (JSONException e) {
			LOG.error("Failed to cancel inter bank transaction occurrence: ", e);
			return interbankDTO;
		} catch (Exception e) {
			LOG.error("Caught exception at cancel inter bank transaction occurrence: ", e);
			return interbankDTO;
		}

		return interbankDTO;
	}
	
	@Override
	public InterBankFundTransferDTO createPendingTransaction(InterBankFundTransferBackendDTO interBankFundTransferBackendDTO, DataControllerRequest dcr) {

		InterBankFundTransferDTO interBankFundTransferDTO = null;
		
		String serviceName = ServiceId.INTER_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTER_BANK_FUND_TRANSFER_BACKEND_WITH_APPROVER;

		String createResponse = null;
		Map<String, Object> requestParameters;
		try {
			
			interBankFundTransferBackendDTO.setStatus(DBPUtilitiesConstants.TRANSACTION_STATUS_PENDING);
			requestParameters = JSONUtils.parseAsMap(new JSONObject(interBankFundTransferBackendDTO).toString(), String.class, Object.class);
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
			interBankFundTransferDTO = JSONUtils.parse(createResponse, InterBankFundTransferDTO.class);
			if (StringUtils.isNotEmpty(interBankFundTransferDTO.getReferenceId())) {
				interBankFundTransferDTO.setTransactionId(interBankFundTransferDTO.getReferenceId());
			}
		} catch (JSONException e) {
			LOG.error("Failed to create Inter Bank transaction with pending status: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at create Inter bank transaction with pending status: ", e);
			return null;
		}		

		return interBankFundTransferDTO;

	}

	@Override
	public InterBankFundTransferDTO approveTransaction(String referenceId, DataControllerRequest dcr, String frequency) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			interBankFundTransferDTO = null;
			InterBankFundTransferBackendDTO backendObj = new InterBankFundTransferBackendDTO();
			
			backendObj.setTransactionId(referenceId);
			backendObj.setStatus(DBPUtilitiesConstants.TRANSACTION_STATUS_SUCCESSFUL);
			interBankFundTransferDTO = updateTransactionStatus(backendObj, dcr);
	
			return interBankFundTransferDTO;	
		}
		else {
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; //serviceName to be changed
			String operationName = OperationName.APPROVE_TRANSACTION; //operationName to be changed
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
				interBankFundTransferDTO =  JSONUtils.parse(approveresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to approve inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to approve inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;
		
	}

	@Override
	public InterBankFundTransferDTO rejectTransaction(String referenceId, String transactionType, DataControllerRequest request, String frequency) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			return deleteTransactionWithoutApproval(referenceId, transactionType, frequency, request);
		}
		else {
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; //serviceName to be changed
			String operationName = OperationName.REJECT_TRANSACTION; //operationName to be changed
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
				interBankFundTransferDTO =  JSONUtils.parse(withdrawresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to approve inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to approve inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;
	}

	@Override
	public InterBankFundTransferDTO withdrawTransaction(String referenceId, String transactionType, DataControllerRequest request, String frequency) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			return deleteTransactionWithoutApproval(referenceId, transactionType, frequency, request);
		}
		else {
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; //serviceName to be changed
			String operationName = OperationName.WITHDRAW_TRANSACTION; //operationName to be changed
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", frequency);
			requestParameters.put("transactionType", transactionType);
			try {
				String withdrawresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				interBankFundTransferDTO =  JSONUtils.parse(withdrawresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to approve inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to approve inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;
	}

	@Override
	public InterBankFundTransferDTO fetchTransactionById(String referenceId, DataControllerRequest dcr) {

		List<InterBankFundTransferDTO> interBankFundTransferDTO = null;
		
		String serviceName = ServiceId.INTER_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTERBANK_FUND_TRANSFER_BACKEND_GET;

		String response = null;
		Map<String, Object> requestParameters;
		try {
			InterBankFundTransferBackendDTO backendObj = new InterBankFundTransferBackendDTO();
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
			interBankFundTransferDTO = JSONUtils.parseAsList(trJsonArray.toString(), InterBankFundTransferDTO.class);
		} catch (JSONException e) {
			LOG.error("Failed to create Inter bank transaction: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at create Inter bank transaction: ", e);
			return null;
		}

		if(interBankFundTransferDTO != null && interBankFundTransferDTO.size() != 0)
			return interBankFundTransferDTO.get(0);
		
		return null;
	}

	private InterBankFundTransferDTO updateTransactionStatus(InterBankFundTransferBackendDTO input, DataControllerRequest request) {

		String serviceName = ServiceId.INTER_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE;
		String operationName = OperationName.INTERBANK_FUND_TRANSFER_BACKEND_UPDATE_STATUS;

		String updateStatus = null;
		InterBankFundTransferDTO interBankFundTransferDTO = null;

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
				interBankFundTransferDTO = JSONUtils.parse(editResponse.toString(), InterBankFundTransferDTO.class);
		} catch (JSONException e) {
			LOG.error("Failed to update status of Inter Bank transaction: ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception at update status of Inter bank transaction: ", e);
			return null;
		}

		return interBankFundTransferDTO;
	}	

	@Override
	public InterBankFundTransferDTO validateTransaction(InterBankFundTransferBackendDTO input, DataControllerRequest dataControllerRequest) {	
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
			if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			InterBankFundTransferDTO output = new InterBankFundTransferDTO();
			try {
				output = JSONUtils.parse(new JSONObject(input).toString(), InterBankFundTransferDTO.class);
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
										.withRequestHeaders(dataControllerRequest.getHeaderMap())
										.withDataControllerRequest(dataControllerRequest)
										.build().getResponse();
							//TransactionBackendDelegateimpl.validateTransaction(requestParameters, request);
					return JSONUtils.parse(response, InterBankFundTransferDTO.class);
				}
				catch (JSONException | IOException | DBPApplicationException e) {
					LOG.error("Failed to validate own account fund transfer: ", e);
					return null;
				}
			}
	}

	@Override
	public List<ApprovalRequestDTO> fetchBackendTransactionsForApproval(Set<String> interBankTransIds,
			DataControllerRequest dcr) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_INTERBANKFUNDTRANSFERS_GET;
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		List<ApprovalRequestDTO> transactions = new ArrayList<ApprovalRequestDTO>();
		
		String filter = "confirmationNumber" + DBPUtilitiesConstants.EQUAL + 
				String.join(DBPUtilitiesConstants.OR + "confirmationNumber" + DBPUtilitiesConstants.EQUAL, interBankTransIds);
		requestParameters.put(DBPUtilitiesConstants.FILTER, filter);

		try {
			String interBankResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			
			JSONObject responseObj = new JSONObject(interBankResponse);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
			transactions = JSONUtils.parseAsList(jsonArray.toString(), ApprovalRequestDTO.class);
			
			transactions.forEach((transaction) ->{
				transaction.setTransactionId(transaction.getConfirmationNumber());
			});
		} 
		catch (JSONException je) {
			LOG.error("Failed to fetch InterBankTransactions : ", je);
		} 
		catch (Exception e) {
			LOG.error("Caught exception while fetching InterBankTransactions: ", e);
		}
		
		return transactions;
	}

	@Override
	public InterBankFundTransferDTO editTransactionWithApproval(InterBankFundTransferBackendDTO backendObj, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			interBankFundTransferDTO = null;
			backendObj.setStatus(DBPUtilitiesConstants.TRANSACTION_STATUS_PENDING);
			interBankFundTransferDTO = updateTransactionStatus(backendObj, request);
			return interBankFundTransferDTO;
		}
		else {
			String serviceName = ServiceId.INTER_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE; //serviceName to be changed
			String operationName = OperationName.INTER_BANK_FUND_TRANSFER_BACKEND_EDIT_WITH_APPROVER; //operationName to be changed
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
				interBankFundTransferDTO =  JSONUtils.parse(editresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to edit inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to edit inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;
	}

	@Override
	public InterBankFundTransferDTO cancelTransactionWithApproval(String referenceId, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			interBankFundTransferDTO = new InterBankFundTransferDTO();
			interBankFundTransferDTO.setReferenceId(referenceId);
			return interBankFundTransferDTO;
		}
		else {
			String serviceName = ServiceId.INTER_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE; //serviceName to be changed
			String operationName = OperationName.INTER_BANK_FUND_TRANSFER_BACKEND_CANCEL_OCCURRENCE_WITH_APPROVER; //operationName to be changed
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			try {
				String cancelresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				interBankFundTransferDTO =  JSONUtils.parse(cancelresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to cancel inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to cancel inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;		
	}

	@Override
	public InterBankFundTransferDTO approveCancellation(String referenceId, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			return cancelTransactionWithoutApproval(referenceId,request);
		}
		else {
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; //serviceName to be changed
			String operationName = OperationName.APPROVE_TRANSACTION; //operationName to be changed
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
				interBankFundTransferDTO =  JSONUtils.parse(approveresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to approve inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to approve inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;		
	}

	@Override
	public InterBankFundTransferDTO rejectCancellation(String referenceId, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			interBankFundTransferDTO = new InterBankFundTransferDTO();
			interBankFundTransferDTO.setReferenceId(referenceId);
			return interBankFundTransferDTO;
		}
		else {
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; //serviceName to be changed
			String operationName = OperationName.REJECT_TRANSACTION; //operationName to be changed
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
				interBankFundTransferDTO =  JSONUtils.parse(rejectresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to reject inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to reject inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;		
	}

	@Override
	public InterBankFundTransferDTO withdrawCancellation(String referenceId, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			interBankFundTransferDTO = new InterBankFundTransferDTO();
			interBankFundTransferDTO.setReferenceId(referenceId);
			return interBankFundTransferDTO;
		}
		else {
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; //serviceName to be changed
			String operationName = OperationName.WITHDRAW_TRANSACTION; //operationName to be changed
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", "Once");
			try {
				String withdrawresponse = DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				interBankFundTransferDTO =  JSONUtils.parse(withdrawresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to withdraw inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to withdraw inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;
	}

	@Override
	public InterBankFundTransferDTO deleteTransactionWithApproval(String referenceId, String transactionType, String frequencyType, DataControllerRequest dataControllerRequest) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			interBankFundTransferDTO = new InterBankFundTransferDTO();
			interBankFundTransferDTO.setReferenceId(referenceId);
			return interBankFundTransferDTO;
		}
		else {
			String serviceName = ServiceId.INTER_BANK_FUND_TRANSFER_LINE_OF_BUSINESS_SERVICE; //serviceName to be changed
			String operationName = OperationName.INTER_BANK_FUND_TRANSFER_BACKEND_DELETE_WITH_APPROVER; //operationName to be changed
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", frequencyType);
			requestParameters.put("transactionType", transactionType);
			try {
				String deleteresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(dataControllerRequest.getHeaderMap())
						.withDataControllerRequest(dataControllerRequest)
						.build().getResponse();
				interBankFundTransferDTO =  JSONUtils.parse(deleteresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to delete inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to delete inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;		
	}

	@Override
	public InterBankFundTransferDTO approveDeletion(String referenceId, String transactionType, String frequencyType, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
		return deleteTransactionWithoutApproval(referenceId, transactionType, frequencyType, request);
		}
		else {
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; //serviceName to be changed
			String operationName = OperationName.APPROVE_TRANSACTION; //operationName to be changed
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", frequencyType);
			requestParameters.put("transactionType", transactionType);
			try {
				String approveresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				interBankFundTransferDTO =  JSONUtils.parse(approveresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to approve inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to approve inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;
	}

	@Override
	public InterBankFundTransferDTO rejectDeletion(String referenceId, String frequencyType, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			interBankFundTransferDTO = new InterBankFundTransferDTO();
			interBankFundTransferDTO.setReferenceId(referenceId);
			return interBankFundTransferDTO;
		}
		else {
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; //serviceName to be changed
			String operationName = OperationName.REJECT_TRANSACTION; //operationName to be changed
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("referenceId", referenceId);
			requestParameters.put("frequencyType", frequencyType);
			try {
				String rejectresponse =  DBPServiceExecutorBuilder.builder()
						.withServiceId(serviceName)
						.withObjectId(null)
						.withOperationId(operationName)
						.withRequestParameters(requestParameters)
						.withRequestHeaders(request.getHeaderMap())
						.withDataControllerRequest(request)
						.build().getResponse();
				interBankFundTransferDTO =  JSONUtils.parse(rejectresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to cancel inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to cancel inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;	
	}

	@Override
	public InterBankFundTransferDTO withdrawDeletion(String referenceId, String frequencyType, DataControllerRequest request) {
		String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue(Constants.PAYMENT_BACKEND);
		InterBankFundTransferDTO interBankFundTransferDTO = null;
		if(PAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			interBankFundTransferDTO = new InterBankFundTransferDTO();
			interBankFundTransferDTO.setReferenceId(referenceId);
			return interBankFundTransferDTO;
		}
		else {
			String serviceName = ServiceId.APPROVAL_TRANSACTION_SERVICE; //serviceName to be changed
			String operationName = OperationName.WITHDRAW_TRANSACTION; //operationName to be changed
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
				interBankFundTransferDTO =  JSONUtils.parse(withdrawresponse, InterBankFundTransferDTO.class);
			}
			catch (JSONException | IOException e) {
				LOG.error("Failed to cancel inter account fund transfer: ", e);
				return null;
			}
			catch (Exception e) {
				LOG.error("Failed to cancel inter account fund transfer: ", e);
				return null;
			}
		}
		return interBankFundTransferDTO;			
	}

	@Override
	public List<ApprovalRequestDTO> getBeneBankAddress(List<ApprovalRequestDTO> transactions,
			DataControllerRequest request) {

		String serviceName = ServiceId.T24_IS_PAYMENTS_VIEW;
		String operationName = OperationName.GET_BANK_DETAILS_BY_SWIFTCODE;
		if (StringUtils.isBlank(operationName)) {
			return transactions;
		}
		String swiftCode;
		Map<String, String> bankDetailsMap = new HashMap<String, String>();
		for (ApprovalRequestDTO dto : transactions) {
			swiftCode = dto.getSwiftCode();
			if (StringUtils.isNotBlank(swiftCode)) {
				if (bankDetailsMap.get(swiftCode) != null) {
					dto.setBeneficiaryBankAddress(bankDetailsMap.get(swiftCode));
				} else {
					String bank;
					try {
						Map<String, Object> requestParameters = new HashMap<String, Object>();
						requestParameters.put("SwiftCode", swiftCode);
						LOG.error("INPUT to BACKEND: " + new JSONObject(requestParameters).toString());
						bank = DBPServiceExecutorBuilder.builder().
								withServiceId(serviceName).
								withObjectId(null).
								withOperationId(operationName).
								withRequestParameters(requestParameters).
								withRequestHeaders(request.getHeaderMap()).
								withDataControllerRequest(request).build().
								getResponse();
					} catch (Exception e) {
						LOG.error("Caught exception in fetching bank details: ", e);
						return null;
					}
					JSONObject bankObj = new JSONObject(bank);
					String address = bankObj.getString("bankName") + ", " + bankObj.getString("addrLine1") + ", "
							+ bankObj.getString("addrLine2");
					dto.setBeneficiaryBankAddress(address);
					bankDetailsMap.put(swiftCode, address);
				}
			}
		}
		return transactions;
	}
}