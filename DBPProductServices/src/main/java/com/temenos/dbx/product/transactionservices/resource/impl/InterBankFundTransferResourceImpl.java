package com.temenos.dbx.product.transactionservices.resource.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.objectserviceutils.EventsDispatcher;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApprovalQueueBusinessDelegate;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApproversBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.AccountBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.ApplicationBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.ContractBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.CustomerBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.TransactionLimitsBusinessDelegate;
import com.temenos.dbx.product.commons.dto.CustomerAccountsDTO;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.commonsutils.AuditLog;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.commonsutils.LogEvents;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.temenos.dbx.product.transactionservices.backenddelegate.api.InterBankFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.InterBankFundTransferBusinessDelegate;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.InterBankFundTransferDTO;
import com.temenos.dbx.product.transactionservices.resource.api.InterBankFundTransferResource;

public class InterBankFundTransferResourceImpl implements InterBankFundTransferResource {

	private static final Logger LOG = LogManager.getLogger(InterBankFundTransferResourceImpl.class);
	CustomerBusinessDelegate customerDelegate = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(CustomerBusinessDelegate.class);
	ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl
			.getBusinessDelegate(ApplicationBusinessDelegate.class);
	TransactionLimitsBusinessDelegate limitsDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(TransactionLimitsBusinessDelegate.class);
	InterBankFundTransferBusinessDelegate interbanktransferDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(InterBankFundTransferBusinessDelegate.class);
	InterBankFundTransferBackendDelegate interbankBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(InterBankFundTransferBackendDelegate.class);
	AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AccountBusinessDelegate.class);
	ApprovalQueueBusinessDelegate approvalQueueDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
	AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
	
    
	public Result createTransaction(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		
		InterBankFundTransferDTO interbankDTO = null;
		Result result = new Result();
		Double amount = null;

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String createdby = CustomerSession.getCustomerId(customer);
		String legalEntityId = (String) customer.get("legalEntityId");
		String featureActionId = null;

		String amountValue = inputParams.get("amount").toString();
		String fromAccountNumber = inputParams.get("fromAccountNumber").toString();
		String frequencyType = inputParams.get("frequencyType") == null ? null : inputParams.get("frequencyType").toString();
		
		CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, fromAccountNumber);
		String contractId = account.getContractId();
		String coreCustomerId = account.getCoreCustomerId();
		String companyId = account.getOrganizationId();
		
		String baseCurrency  = application.getBaseCurrencyFromCache();
		String transactionCurrency = inputParams.get("transactionCurrency") != null ?inputParams.get("transactionCurrency").toString() : baseCurrency;
	    String serviceCharge = inputParams.get("serviceCharge") != null ? inputParams.get("serviceCharge").toString() : null;
		String validate = inputParams.get("validate") == null ? null : inputParams.get("validate").toString();
		if (amountValue == null || amountValue == "") {
			return ErrorCodeEnum.ERR_12031.setErrorCode(new Result());
		}

		featureActionId = FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE;

		if (!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(createdby, featureActionId,
				fromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}

		try {
			amount = Double.parseDouble(amountValue);
		} catch (NumberFormatException e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_10624.setErrorCode(new Result());
		}
		
		fromAccountNumber = inputParams.get("fromAccountNumber").toString();
		inputParams.put("featureActionId", featureActionId);
		inputParams.put("companyId", companyId);
		inputParams.put("roleId", customerDelegate.getUserContractCustomerRole(contractId, coreCustomerId, createdby));
		inputParams.put("createdby", createdby);

		try {
			interbankDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), InterBankFundTransferDTO.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: ", e);
			return ErrorCodeEnum.ERR_28021.setErrorCode(new Result());
		}

		String date = interbankDTO.getScheduledDate() == null
				? (interbankDTO.getProcessingDate() == null
						? (interbankDTO.getFrequencyStartDate() == null ? application.getServerTimeStamp()
								: interbankDTO.getFrequencyStartDate())
						: interbankDTO.getProcessingDate())
				: interbankDTO.getScheduledDate();

		String backendid = inputParams.get("transactionId") == null || (StringUtils.isEmpty(inputParams.get("transactionId").toString())) ? null : inputParams.get("transactionId").toString();
		String beneficiaryId = inputParams.get("beneficiaryId") == null ? null : inputParams.get("beneficiaryId").toString();
		String requestid = "";
					
		if("true".equalsIgnoreCase(validate)) {
			InterBankFundTransferBackendDTO interBankFundTransferBackendDTO = new InterBankFundTransferBackendDTO();
			interBankFundTransferBackendDTO = interBankFundTransferBackendDTO.convert(interbankDTO);
			
			InterBankFundTransferDTO validateinterbankDTO = interbankBackendDelegate.validateTransaction(interBankFundTransferBackendDTO,request);
			try {
				 result = JSONToResult.convert(new JSONObject(validateinterbankDTO).toString());
				 return result;
			} catch (JSONException e) {
				LOG.error("Error occured while converting the response from Line of Business service for interbank transfer: ", e);
				return ErrorCodeEnum.ERR_21217.setErrorCode(new Result());
			}
		}
		
		TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
		transactionStatusDTO.setCustomerId(createdby);
		transactionStatusDTO.setCompanyId(companyId);
		transactionStatusDTO.setAccountId(fromAccountNumber);
		transactionStatusDTO.setAmount(amount);
		transactionStatusDTO.setStatus(TransactionStatusEnum.NEW);
		transactionStatusDTO.setDate(date);
		transactionStatusDTO.setTransactionCurrency(transactionCurrency);
		transactionStatusDTO.setFeatureActionID(featureActionId);
		transactionStatusDTO.setConfirmationNumber(backendid);
		transactionStatusDTO.setServiceCharge(serviceCharge);

		transactionStatusDTO = approvalQueueDelegate.validateForApprovals(transactionStatusDTO, request);			
		if(transactionStatusDTO == null) {			
			return ErrorCodeEnum.ERR_29018.setErrorCode(new Result());
		}
		if (transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
			result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
			result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
			return result;
		}
		TransactionStatusEnum transactionStatus = transactionStatusDTO.getStatus();
		boolean isSelfApproved = transactionStatusDTO.isSelfApproved();

		interbankDTO.setStatus(transactionStatus.getStatus());
		interbankDTO.setTransactionAmount(transactionStatusDTO.getTransactionAmount());
		interbankDTO.setRequestId(transactionStatusDTO.getRequestId());
		String confirmationNumber = (StringUtils.isEmpty(backendid)) ? Constants.REFERENCE_KEY + transactionStatusDTO.getRequestId() : backendid;
		interbankDTO.setConfirmationNumber(confirmationNumber);
		try {
			interbankDTO.setAmount(transactionStatusDTO.getAmount().doubleValue());
		} catch (NumberFormatException e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}
		interbankDTO.setServiceCharge(transactionStatusDTO.getServiceCharge());
		interbankDTO.setLegalEntityId(legalEntityId);
		//interbankDTO.setTransactionId(null);
		
		InterBankFundTransferDTO interbankdbxDTO = interbanktransferDelegate.createTransactionAtDBX(interbankDTO);
		if(interbankdbxDTO == null) {
			LOG.error("Error occured while creating entry into the DBX table: ");
			return ErrorCodeEnum.ERR_29016.setErrorCode(new Result());
		}
		if(interbankdbxDTO.getDbpErrCode() != null || interbankdbxDTO.getDbpErrMsg() != null) {
			result.addParam(new Param("dbpErrCode", interbankdbxDTO.getDbpErrCode()));
			result.addParam(new Param("dbpErrMsg", interbankdbxDTO.getDbpErrMsg()));
			return result;
		}
		
		interbankdbxDTO.setValidate(validate);
		
		InterBankFundTransferBackendDTO interbankBackendDTO = new InterBankFundTransferBackendDTO();
		interbankBackendDTO = interbankBackendDTO.convert(interbankdbxDTO);
				
		String creditValueDate = inputParams.get("creditValueDate") == null ? "" : inputParams.get("creditValueDate").toString();
        String totalAmount = inputParams.get("totalAmount") == null ? "" : inputParams.get("totalAmount").toString(); 
        String exchangeRate = inputParams.get("exchangeRate") == null ? "" : inputParams.get("exchangeRate").toString();
        String intermediaryBicCode = inputParams.get("intermediaryBicCode") == null ? "" : inputParams.get("intermediaryBicCode").toString();
        String clearingCode = inputParams.get("clearingCode") == null ? "" : inputParams.get("clearingCode").toString();
        String e2eReference = inputParams.get("e2eReference") == null ? "" : inputParams.get("e2eReference").toString();
        String overrides = inputParams.get("overrides") == null ? "" : inputParams.get("overrides").toString();
        String beneficiaryBankName = inputParams.get("beneficiaryBankName") == null ? "" : inputParams.get("beneficiaryBankName").toString();
        String beneficiaryAddressLine2 = inputParams.get("beneficiaryAddressLine2") == null ? "" : inputParams.get("beneficiaryAddressLine2").toString();
        String beneficiaryPhone = inputParams.get("beneficiaryPhone") == null ? "" : inputParams.get("beneficiaryPhone").toString();
        String beneficiaryEmail = inputParams.get("beneficiaryEmail") == null ? "" : inputParams.get("beneficiaryEmail").toString();
        String beneficiaryState = inputParams.get("beneficiaryState") == null ? "" : inputParams.get("beneficiaryState").toString();
       
		interbankBackendDTO.setCreditValueDate(creditValueDate);
		interbankBackendDTO.setTotalAmount(totalAmount);
		interbankBackendDTO.setExchangeRate(exchangeRate);
		interbankBackendDTO.setIntermediaryBicCode(intermediaryBicCode);
		interbankBackendDTO.setClearingCode(clearingCode);
		interbankBackendDTO.setE2eReference(e2eReference);
		interbankBackendDTO.setBeneficiaryId(beneficiaryId);
		interbankBackendDTO.setOverrides(overrides);
		interbankBackendDTO.setBeneficiaryBankName(beneficiaryBankName);
		interbankBackendDTO.setBeneficiaryAddressLine2(beneficiaryAddressLine2);
		interbankBackendDTO.setBeneficiaryPhone(beneficiaryPhone);
		interbankBackendDTO.setBeneficiaryEmail(beneficiaryEmail);
		interbankBackendDTO.setBeneficiaryState(beneficiaryState);

		try {
			interbankBackendDTO.setAmount(Double.parseDouble(interbankBackendDTO.getTransactionAmount()));
			interbankdbxDTO.setAmount(Double.parseDouble(interbankBackendDTO.getTransactionAmount()));
		} catch (Exception e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}
		
		try {
			String responseObj = new JSONObject(interbankBackendDTO).toString();
			result = JSONToResult.convert(responseObj);
		} catch (JSONException e) {
			LOG.error(
					"Error occured while converting the response from Line of Business service for interbank transfer: ",e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		String transactionid = interbankdbxDTO.getTransactionId();
        InterBankFundTransferDTO interbanktransactionDTO = new InterBankFundTransferDTO();

        String createWithPaymentId = inputParams.get("createWithPaymentId") == null ? ""
                : inputParams.get("createWithPaymentId").toString();

		if(transactionStatus == TransactionStatusEnum.SENT ) {
		    if (StringUtils.isEmpty(backendid)
                    || (StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true"))) {
		        if(StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true")){
		            interbankBackendDTO.setTransactionId(backendid);
		            String charges = inputParams.get("charges") == null ? null : inputParams.get("charges").toString();
		            interbankBackendDTO.setCharges(charges);
                }else{
                    interbankBackendDTO.setTransactionId(null);
                }
				interbanktransactionDTO = interbankBackendDelegate.createTransactionWithoutApproval(interbankBackendDTO, request);					
				if(interbanktransactionDTO == null) {	
					interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					return ErrorCodeEnum.ERR_12601.setErrorCode(result);
				}
			}
			else {
				String frequency = StringUtils.isEmpty(interbankDTO.getFrequencyTypeId()) ? null : interbankDTO.getFrequencyTypeId();
				interbanktransactionDTO  = interbanktransferDelegate.approveTransaction(backendid, request, frequency);
				if(interbanktransactionDTO == null) {	
					interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(),confirmationNumber);
					return ErrorCodeEnum.ERR_29020.setErrorCode(result);
				}		
			}
			
			if(interbanktransactionDTO.getDbpErrCode() != null || interbanktransactionDTO.getDbpErrMsg() != null) {
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(),confirmationNumber);
				result.addParam(new Param("errorDetails", interbanktransactionDTO.getErrorDetails()));
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, interbanktransactionDTO.getDbpErrMsg());
			}	
			
			if(interbanktransactionDTO.getReferenceId() == null || "".equals(interbanktransactionDTO.getReferenceId())) {
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				return ErrorCodeEnum.ERR_12601.setErrorCode(result);
			}
			interbankDTO = interbanktransactionDTO;
			interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.EXECUTED.getStatus(), interbanktransactionDTO.getReferenceId());
			result.addParam(new Param("referenceId", interbanktransactionDTO.getReferenceId()));
			result.addParam(new Param("status", transactionStatus.getStatus()));
	        result.addParam(new Param("message", transactionStatus.getMessage()));
		}
		else if(transactionStatus == TransactionStatusEnum.PENDING){
			requestid = transactionStatusDTO.getRequestId();
			String pendingrefId = null;
			interbankdbxDTO.setCreditValueDate(creditValueDate);
			interbankdbxDTO.setTotalAmount(totalAmount);
			interbankdbxDTO.setExchangeRate(exchangeRate);
			interbankdbxDTO.setIntermediaryBicCode(intermediaryBicCode);
			interbankdbxDTO.setClearingCode(clearingCode);
			interbankdbxDTO.setE2eReference(e2eReference);
			if (StringUtils.isEmpty(backendid)
                    || (StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true"))) {
			    if(StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true")){
			        interbankdbxDTO.setTransactionId(backendid);
			        String charges = inputParams.get("charges") == null ? null : inputParams.get("charges").toString();
			        interbankdbxDTO.setCharges(charges);
                } 
			    InterBankFundTransferDTO interbankpendingtransactionDTO = interbanktransferDelegate.createPendingTransaction(interbankdbxDTO, request);
				if(interbankpendingtransactionDTO == null)
				{
					interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					LOG.error("Error occured while creating entry into the backend table: ");
					return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
				}
				if(interbankpendingtransactionDTO.getDbpErrCode() != null || interbankpendingtransactionDTO.getDbpErrMsg() != null) {
					interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(),confirmationNumber);
					result.addParam(new Param("errorDetails", interbankpendingtransactionDTO.getErrorDetails()));
					return ErrorCodeEnum.ERR_00000.setErrorCode(result, interbankpendingtransactionDTO.getDbpErrMsg());
				}
				backendid = interbankpendingtransactionDTO.getReferenceId();
				interbankDTO= interbankpendingtransactionDTO;
			}
				pendingrefId = backendid;
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,transactionStatus.toString(), backendid);
				transactionStatusDTO = approvalQueueDelegate.updateBackendIdInApprovalQueue(requestid, backendid, isSelfApproved, featureActionId, request);
				if(transactionStatusDTO == null) 
				{							
					interbankBackendDelegate.deleteTransactionWithoutApproval(backendid, null, frequencyType, request);
					interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(),backendid);
					return ErrorCodeEnum.ERR_29019.setErrorCode(new Result());
				}	
				if(transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
					interbankBackendDelegate.deleteTransactionWithoutApproval(backendid, null, frequencyType, request);
					interbanktransferDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), backendid);
					result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
					result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
					return result;
				}
				
				transactionStatus = transactionStatusDTO.getStatus();
				backendid = transactionStatusDTO.getConfirmationNumber();
				
			
			//code snippet being added for alerts on inter transfer
			try {
				LogEvents.pushAlertsForApprovalRequests( featureActionId, request, response,inputParams, null,  
						backendid, requestid, CustomerSession.getCustomerName(customer),null);
			} catch (Exception e) {
				LOG.error("Failed at pushAlertsForApprovalRequests "+e);
			}
			
			result.addParam(new Param("requestId", requestid));
			
			
			if (transactionStatus == TransactionStatusEnum.APPROVED) {
				result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
				result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
				result.addParam(new Param("referenceId", backendid));
			}
			else {
				result.addParam(new Param("status", transactionStatus.getStatus()));
				result.addParam(new Param("message", transactionStatus.getMessage()));
				result.addParam(new Param("referenceId", pendingrefId));
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionid,transactionStatus.toString(), pendingrefId);
			}
		}
		else if(transactionStatus == TransactionStatusEnum.APPROVED){
			result.addParam(new Param("referenceId", transactionStatusDTO.getConfirmationNumber()));
	        result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
	        result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
		}

		if(interbankDTO.getOverrides() != null) {
			result.addParam(new Param("overrides",interbankDTO.getOverrides()));
		}
		if(interbankDTO.getOverrideList() != null) {
			result.addParam(new Param("overrideList",interbankDTO.getOverrideList()));
		}
			
		if(interbankDTO.getCharges() != null) {
			result.addParam(new Param("charges",interbankDTO.getCharges()));
		}
		if(interbankDTO.getExchangeRate() != null) {
			result.addParam(new Param("exchangeRate",interbankDTO.getExchangeRate()));
		}
		if(interbankDTO.getTotalAmount() != null) {
			result.addParam(new Param("totalAmount",interbankDTO.getTotalAmount()));
		}
		if(interbankDTO.getMessageDetails() != null) {
            result.addParam(new Param("messageDetails", interbankDTO.getMessageDetails()));
        }
		if(interbankDTO.getQuoteCurrency() != null) {
            result.addParam(new Param("quoteCurrency", interbankDTO.getQuoteCurrency()));
        }
		try {
			_logTransaction(request,response,inputArray,result,transactionStatus,transactionStatusDTO.getConfirmationNumber(),interbankdbxDTO,requestid);
		} catch(Exception e) {
			LOG.error("Error occured while audit logging.",e);
		}

		// ADP-7058 update additional meta data
		try{
			approvalQueueDelegate.updateAdditionalMetaForApprovalRequest(transactionStatusDTO.getRequestId(), request);
		} catch(Exception e){
			LOG.error(e);
		}

		return result;
	}

	@Override
	public Result updateStatus(String MethodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {

		Result result = new Result();
		InterBankFundTransferDTO interbanktransferDTO = null;

		@SuppressWarnings("unchecked")
		HashMap<String, Object> requestParams = (HashMap<String, Object>) inputArray[1];

		String transactionId = (String) requestParams.get("transactionId");
		String confirmationNumber = (String) requestParams.get("confirmationNumber");
		String status = (String) requestParams.get("status");

		if (transactionId == null || transactionId == "") {
			return ErrorCodeEnum.ERR_12026.setErrorCode(result);
		}
		if (confirmationNumber == null || confirmationNumber == "") {
			return ErrorCodeEnum.ERR_20698.setErrorCode(result);
		}
		if (status == null || status == "") {
			return ErrorCodeEnum.ERR_20699.setErrorCode(result);
		}

		try {
			// Initialize the required BusinessDelegate class to perform the operation
			InterBankFundTransferBusinessDelegate interbanktransferDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(InterBankFundTransferBusinessDelegate.class);
			interbanktransferDTO = interbanktransferDelegate.updateStatusUsingTransactionId(transactionId, status, confirmationNumber);
			if (interbanktransferDTO == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(result);
			}
			JSONObject interbanktransferJSON = new JSONObject(interbanktransferDTO);
			result = JSONToResult.convert(interbanktransferJSON.toString());
		} catch (Exception exp) {
			LOG.error("Exception occured while invoking interbanktransaction business delegate", exp);
			return ErrorCodeEnum.ERR_12000.setErrorCode(result);
		}
		return result;
	}

	@Override
	public Result processResponseFromLineOfBusiness(Result result, DataControllerRequest request,
			DataControllerResponse response) {
		/*
		HashMap<String, Object> inputParams = new HashMap<String, Object>();

		String transactionId = request.getParameter("dbxtransactionId");
		Param referenceId = result.getParamByName("referenceId");
		TransactionStatusEnum status = null;
		String confirmationNumber = "";

		InterBankFundTransferBusinessDelegate interbankBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(InterBankFundTransferBusinessDelegate.class);

		if (referenceId != null) {
			status = TransactionStatusEnum.EXECUTED;
			confirmationNumber = referenceId.getValue();
		} else {
			status = TransactionStatusEnum.FAILED;
		}
		inputParams.put("status", status.getStatus());
		interbankBusinessDelegate.updateStatusUsingTransactionId(transactionId, status.getStatus(), confirmationNumber);

		result.addParam(new Param("dbxtransactionId", transactionId));
		*/
		return result;
	}

	@Override
	public Result editTransaction(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		InterBankFundTransferBusinessDelegate interbankTransactionDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(InterBankFundTransferBusinessDelegate.class);
		InterBankFundTransferBackendDelegate interbankBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(InterBankFundTransferBackendDelegate.class);
		AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
		ApprovalQueueBusinessDelegate approvalQueueDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
		AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AccountBusinessDelegate.class);
		
		String featureActionId = FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE;
		InterBankFundTransferDTO interbankDTO = null;
		Result result = new Result();
		String confirmationNumber = null;

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);

		String createdby = CustomerSession.getCustomerId(customer);
		String legalEntityId = (String) customer.get("legalEntityId");
		boolean isBusinessUser = CustomerSession.IsBusinessUser(customer);

		Object transactionIdParam = inputParams.get("confirmationNumber");
		Object confirmationNumberParam = inputParams.get("transactionId") == null ? transactionIdParam
				: inputParams.get("transactionId");

		String fromAccountNumber = inputParams.get("fromAccountNumber").toString();
		
		CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, fromAccountNumber);
		String contractId = account.getContractId();
		String coreCustomerId = account.getCoreCustomerId();
		String companyId = account.getOrganizationId();
		
		if (confirmationNumberParam != null && !"".equals(confirmationNumberParam.toString())) {
			confirmationNumber = confirmationNumberParam.toString();
			interbankDTO = interbankTransactionDelegate.fetchExecutedTranscationEntry(confirmationNumber + "", Arrays.asList(companyId), createdby, legalEntityId);
			if (interbankDTO == null) {
				interbankDTO = interbankBackendDelegate.fetchTransactionById(confirmationNumber, request);
				if(interbankDTO == null) {
					LOG.error("Record doesn't exist");
					return ErrorCodeEnum.ERR_12003.setErrorCode(result);
				}else{
				    interbankDTO.setCompanyId(companyId);
				    interbankDTO.setCreatedby(createdby);
				    interbankDTO.setFeatureActionId(featureActionId);
                    if (StringUtils.isNotBlank(inputParams.get("amount").toString())) {
                        interbankDTO.setAmount(Double.parseDouble(inputParams.get("amount").toString()));
                        interbankDTO.setTransactionAmount(inputParams.get("amount").toString());
                    }
				    interbankDTO = interbankTransactionDelegate.createTransactionAtDBX(interbankDTO);
				}
			}
		} else {
			LOG.error("confirmationNumber is missing in the payload which is mandatory for edit");
			return ErrorCodeEnum.ERR_12026.setErrorCode(new Result());
		}

		String transactionId = interbankDTO.getTransactionId();
		String oldRequestId = interbankDTO.getRequestId();

		// Authorization checks on transcationId
		/*
		if (interbankDTO.getCompanyId() != null && !interbankDTO.getCompanyId().equals(companyId)) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		} else if (!isBusinessUser && !CustomerSession.IsCombinedUser(customer)
				&& !interbankDTO.getCreatedby().equals(createdby)) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		*/
		// Authorization checks on prevfromAccountNumber and featureactionId
		String prevfromAccountNumber = interbankDTO.getFromAccountNumber();

		if (!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(createdby, featureActionId,
				prevfromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}

		InterBankFundTransferDTO requestDTO = null;
		inputParams.put("createdby", createdby);
		inputParams.put("companyId", companyId);
		inputParams.put("roleId", customerDelegate.getUserContractCustomerRole(contractId, coreCustomerId, createdby));

		try {
			requestDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), InterBankFundTransferDTO.class);
			requestDTO.setConfirmationNumber(null);
			requestDTO.setTransactionId(null);
			interbankDTO = interbankDTO.updateValues(requestDTO);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: " , e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}

		if (interbankDTO == null) {
			LOG.error("Error occured while fetching the input params: ");
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}

		// Authorization checks on newfromAccountNumber and featureactionId
		String newfromAccountNumber = interbankDTO.getFromAccountNumber();

		if (!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(createdby, featureActionId,
				newfromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}

		String date = interbankDTO.getScheduledDate() == null
				? (interbankDTO.getProcessingDate() == null
						? (interbankDTO.getFrequencyStartDate() == null ? application.getServerTimeStamp()
								: interbankDTO.getFrequencyStartDate())
						: interbankDTO.getProcessingDate())
				: interbankDTO.getScheduledDate();

		// Transaction limit checks on amount and featureactionId
		Double amount = interbankDTO.getAmount();
		String transactionAmount = inputParams.get("transactionAmount") != null ? inputParams.get("transactionAmount").toString() : null;
	    String serviceCharge = inputParams.get("serviceCharge") != null ? inputParams.get("serviceCharge").toString() : null;
	    
	    if("0".equals(transactionAmount) || "0.0".equals(transactionAmount) || "0.00".equals(transactionAmount)) {
			transactionAmount = amount + "";
		}
		
		String validate = inputParams.get("validate") == null ? null : inputParams.get("validate").toString();
		
		if("true".equalsIgnoreCase(validate)) {
			InterBankFundTransferBackendDTO backendObj = new InterBankFundTransferBackendDTO();
			backendObj =  backendObj.convert(interbankDTO);
			InterBankFundTransferDTO validateownaccountDTO = interbankBackendDelegate.validateTransaction(backendObj,request);
			try {
				 result = JSONToResult.convert(new JSONObject(validateownaccountDTO).toString());
				 return result;
			} catch (JSONException e) {
				LOG.error("Error occured while converting the response from Line of Business service for ownaccount transfer: ", e);
				return ErrorCodeEnum.ERR_21217.setErrorCode(new Result());
			}
		}
		
		TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
		transactionStatusDTO.setCustomerId(createdby);
		transactionStatusDTO.setCompanyId(companyId);
		transactionStatusDTO.setAccountId(fromAccountNumber);
		transactionStatusDTO.setAmount(amount);
		transactionStatusDTO.setStatus(TransactionStatusEnum.NEW);
		transactionStatusDTO.setDate(date);
		transactionStatusDTO.setTransactionCurrency(interbankDTO.getTransactionCurrency());
		transactionStatusDTO.setFeatureActionID(featureActionId);
		transactionStatusDTO.setRequestId(oldRequestId);
		transactionStatusDTO.setServiceCharge(serviceCharge);
		
		transactionStatusDTO = approvalQueueDelegate.validateForApprovals(transactionStatusDTO, request);
		
		if(transactionStatusDTO == null)
			return ErrorCodeEnum.ERR_29018.setErrorCode(new Result());
		
		if(transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null){
			result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
			result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
			return result;
		}

		TransactionStatusEnum transactionStatus = transactionStatusDTO.getStatus();
		
		interbankDTO.setStatus(transactionStatus.getStatus());
		//interbankDTO.setRequestId(null);
		interbankDTO.setConfirmationNumber(confirmationNumber + "");
		interbankDTO.setTransactionAmount(transactionStatusDTO.getTransactionAmount());
		interbankDTO.setServiceCharge(transactionStatusDTO.getServiceCharge());
		try {
			interbankDTO.setAmount(transactionStatusDTO.getAmount().doubleValue());
		} catch (NumberFormatException e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}

		InterBankFundTransferBackendDTO interbankBackendDTO = new InterBankFundTransferBackendDTO();
		interbankBackendDTO = interbankBackendDTO.convert(interbankDTO);

		try {
			interbankBackendDTO.setAmount(Double.parseDouble(interbankBackendDTO.getTransactionAmount()));
		} catch (Exception e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}
		
		String requestObj = null;
		try {
			requestObj = new JSONObject(interbankBackendDTO).toString();
			result = JSONToResult.convert(requestObj);
		} catch (JSONException e) {
			LOG.error("Error occured while converting the response from Line of Business service for interbank transfer: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		String referenceId = null;
		InterBankFundTransferDTO resDTO = null;
		
		String overrides = inputParams.get("overrides") == null ? "" : inputParams.get("overrides").toString();
		interbankBackendDTO.setOverrides(overrides);

		if (transactionStatus == TransactionStatusEnum.SENT) {
			if (confirmationNumber != null && !confirmationNumber.startsWith(Constants.REFERENCE_KEY)) {
				interbankBackendDTO.setTransactionId(confirmationNumber);
				resDTO = interbankBackendDelegate.editTransactionWithoutApproval(interbankBackendDTO, request);
			}
			
			if (resDTO == null) {
				interbankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				return ErrorCodeEnum.ERR_12600.setErrorCode(result);
			}

			if (resDTO.getDbpErrMsg() != null && !resDTO.getDbpErrMsg().isEmpty()) {
				interbankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				result.addParam(new Param("errorDetails", resDTO.getErrorDetails()));
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, resDTO.getDbpErrMsg());
			}
			
			referenceId = resDTO.getReferenceId();

			if(StringUtils.isEmpty(referenceId)) {
				interbankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				return ErrorCodeEnum.ERR_12602.setErrorCode(result);
			}
			
			interbankDTO = resDTO;
			interbankDTO.setConfirmationNumber(referenceId);
			interbankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.EXECUTED.getStatus(), interbankDTO.getReferenceId());
			
			result.addParam(new Param("referenceId", resDTO.getReferenceId()));
			result.addParam(new Param("status", transactionStatus.getStatus()));
	        result.addParam(new Param("message", transactionStatus.getMessage()));
			
		}
		else if(transactionStatus == TransactionStatusEnum.PENDING){

    		String requestId = transactionStatusDTO.getRequestId();
    		referenceId = confirmationNumber;
    		interbankBackendDTO.setTransactionId(confirmationNumber);
    		InterBankFundTransferDTO pendingtransactionDTO = interbankBackendDelegate.editTransactionWithApproval(interbankBackendDTO, request);
    		if(pendingtransactionDTO == null)
    		{
    			interbankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
    			LOG.error("Error occured while creating entry into the backend table: ");
    			return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
    		}
    		if(pendingtransactionDTO.getDbpErrCode() != null || pendingtransactionDTO.getDbpErrMsg() != null) {
    			interbankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
    			result.addParam(new Param("errorDetails", pendingtransactionDTO.getErrorDetails()));
    			return ErrorCodeEnum.ERR_00000.setErrorCode(result, pendingtransactionDTO.getDbpErrMsg());
    		}
    		
    		String backendid = pendingtransactionDTO.getReferenceId();
            interbankTransactionDelegate.updateStatusUsingTransactionId(transactionId,transactionStatus.toString(), backendid);
            transactionStatusDTO = approvalQueueDelegate.updateBackendIdInApprovalQueue(requestId, backendid, transactionStatusDTO.isSelfApproved(), featureActionId, request);
            if(transactionStatusDTO == null) 
			{							
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(),backendid);
				return ErrorCodeEnum.ERR_29019.setErrorCode(new Result());
			}	
			if(transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
				interbanktransferDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), backendid);
				result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
				result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
				return result;
			}
			transactionStatus=transactionStatusDTO.getStatus();
    		interbankTransactionDelegate.updateStatusUsingTransactionId(transactionId,transactionStatus.toString(), backendid);
    		interbankDTO.setConfirmationNumber(backendid);
    		interbankDTO.setStatus(transactionStatus.toString());
    		
    		result.addParam(new Param("requestId", requestId));
    		result.addParam(new Param("referenceId", backendid));
    		if(pendingtransactionDTO.getMessageDetails() != null) {
                result.addParam(new Param("messageDetails", pendingtransactionDTO.getMessageDetails()));
            }
    		
    		interbankDTO.setRequestId(requestId);
    		if (transactionStatus == TransactionStatusEnum.APPROVED) {
				result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
				result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
			}
			else {
				result.addParam(new Param("status", transactionStatus.getStatus()));
				result.addParam(new Param("message", transactionStatus.getMessage()));
			}

    	}
    	else if(transactionStatus == TransactionStatusEnum.APPROVED){
    		result.addParam(new Param("referenceId", transactionStatusDTO.getConfirmationNumber()));
    		result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
    		result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
    	}
    	if(StringUtils.isNotEmpty(referenceId)) {
    		interbankDTO.setTransactionId(transactionId);
    		interbankTransactionDelegate.updateTransactionAtDBX(interbankDTO);
    	}
    	if(interbankDTO.getMessageDetails() != null) {
            result.addParam(new Param("messageDetails", interbankDTO.getMessageDetails()));
        }
    	if(interbankDTO.getQuoteCurrency() != null) {
            result.addParam(new Param("quoteCurrency", interbankDTO.getQuoteCurrency()));
        }

		// ADP-7058 update additional meta data
		try{
			approvalQueueDelegate.updateAdditionalMetaForApprovalRequest(transactionStatusDTO.getRequestId(), request);
		} catch(Exception e){
			LOG.error(e);
		}

    	return result;
	}

	@Override
	public Result cancelScheduledTransactionOccurrence(String methodID, Object[] inputArray,
			DataControllerRequest request, DataControllerResponse response) {

		Result result = new Result();
		String requestId = "";

		String featureActionId = FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL;

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);

		String createdby = CustomerSession.getCustomerId(customer);
		boolean isSMEUser = CustomerSession.IsBusinessUser(customer);

		InterBankFundTransferDTO interbankDTO = null;

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		Object transactionIdParam = inputParams.get("confirmationNumber");
		Object confirmationNumberParam = inputParams.get("transactionId") == null ? transactionIdParam
				: inputParams.get("transactionId");
		String confirmationNumber = null;
		
		ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);
		List<String> contracts = contractDelegate.fetchContractCustomers(createdby);
		contracts.add(CustomerSession.getCompanyId(customer));

		if (confirmationNumberParam != null && !"".equals(confirmationNumberParam.toString())) {
			confirmationNumber = confirmationNumberParam.toString();
			interbankDTO = interbanktransferDelegate.fetchExecutedTranscationEntry(confirmationNumber, contracts, createdby,null);
			if (interbankDTO == null) {
				interbankDTO = interbankBackendDelegate.fetchTransactionById(confirmationNumber, request);
				if(interbankDTO == null) {
					LOG.error("Record doesn't exist");
					return ErrorCodeEnum.ERR_12003.setErrorCode(result);
				}
				interbankDTO.setFeatureActionId(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);
				interbankDTO.setCreatedby(createdby);
				CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, interbankDTO.getFromAccountNumber());
				String companyId = account.getOrganizationId();
				interbankDTO.setCompanyId(companyId);
				String transactionId = interbankDTO.getTransactionId();
				interbankDTO.setConfirmationNumber(transactionId);
				interbankDTO.setTransactionId(null);
				InterBankFundTransferDTO dbxDTO = interbanktransferDelegate.createTransactionAtDBX(interbankDTO);
				if(dbxDTO != null) {
					interbankDTO.setTransactionId(dbxDTO.getTransactionId());
					interbankDTO.setRequestId(dbxDTO.getRequestId());
				}
			}
		} else {
			LOG.error(
					"confirmationNumber or transactionId is missing in the payload which is mandatory for cancel occurrence");
			return ErrorCodeEnum.ERR_12026.setErrorCode(result);
		}

		String oldrequestId = interbankDTO.getRequestId();
		String transactionId = interbankDTO.getTransactionId();

		// Authorization checks on transcationId
		/*
		if (interbankDTO.getCompanyId() != null && ! contracts.contains(interbankDTO.getCompanyId())) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		} else if (!isSMEUser && !CustomerSession.IsCombinedUser(customer)
				&& !interbankDTO.getCreatedby().equals(createdby)) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}*/

		// Authorization checks on prevfromAccountNumber and featureactionId
		String prevfromAccountNumber = interbankDTO.getFromAccountNumber();

		if (!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(createdby, featureActionId,
				prevfromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		
		TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
		transactionStatusDTO.setCustomerId(createdby);
		transactionStatusDTO.setCompanyId(interbankDTO.getCompanyId());
		transactionStatusDTO.setAccountId(interbankDTO.getFromAccountNumber());
		transactionStatusDTO.setFeatureActionID(featureActionId);
		transactionStatusDTO.setRequestId(oldrequestId);
		transactionStatusDTO.setAmount(interbankDTO.getAmount());
		transactionStatusDTO.setServiceCharge(interbankDTO.getServiceCharge());
		transactionStatusDTO.setTransactionCurrency(interbankDTO.getTransactionCurrency());
		
		transactionStatusDTO = approvalQueueDelegate.validateForApprovals(transactionStatusDTO, request);			
		if(transactionStatusDTO == null) {			
			return ErrorCodeEnum.ERR_29018.setErrorCode(new Result());
		}
		if(transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null){
			result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
			result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
			return result;
		}
		TransactionStatusEnum transactionStatus = transactionStatusDTO.getStatus();
		boolean isSelfApproved = transactionStatusDTO.isSelfApproved();
		
		if (transactionStatus == TransactionStatusEnum.SENT) {
			InterBankFundTransferDTO interbanktransactionDTO = new InterBankFundTransferDTO();
			interbanktransactionDTO = interbankBackendDelegate.cancelTransactionWithoutApproval(confirmationNumber, request);					
			if(interbanktransactionDTO == null) {	
				return ErrorCodeEnum.ERR_12600.setErrorCode(result);
			}
			if(interbanktransactionDTO.getDbpErrCode() != null || interbanktransactionDTO.getDbpErrMsg() != null) {
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, interbanktransactionDTO.getDbpErrMsg());
			}
			String referenceId = interbanktransactionDTO.getReferenceId();
			if(referenceId == null || "".equals(referenceId)) {
				return ErrorCodeEnum.ERR_12601.setErrorCode(result);
			}
			result.addParam(new Param("referenceId", referenceId));
			result.addParam(new Param("status", transactionStatus.getStatus()));
	        result.addParam(new Param("message", transactionStatus.getMessage()));
		}
		else if(transactionStatus == TransactionStatusEnum.PENDING){
			requestId = transactionStatusDTO.getRequestId();
			InterBankFundTransferDTO interbankpendingtransactionDTO = new InterBankFundTransferDTO();
			interbankpendingtransactionDTO = interbanktransferDelegate.cancelTransactionWithApproval(confirmationNumber, transactionId, request);					
			if(interbankpendingtransactionDTO == null)
			{
				LOG.error("Error occured while creating entry into the backend table: ");
				return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
			}
			if(interbankpendingtransactionDTO.getDbpErrCode() != null || interbankpendingtransactionDTO.getDbpErrMsg() != null) {
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, interbankpendingtransactionDTO.getDbpErrMsg());
			}
			confirmationNumber = interbankpendingtransactionDTO.getReferenceId();
			transactionStatusDTO = approvalQueueDelegate.updateBackendIdInApprovalQueue(requestId, confirmationNumber, isSelfApproved, featureActionId, request);
			if(transactionStatusDTO == null) 
			{							
				return ErrorCodeEnum.ERR_29019.setErrorCode(new Result());
			}	
			if(transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
				result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
				result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
				return result;
			}
			transactionStatus = transactionStatusDTO.getStatus();
			confirmationNumber = transactionStatusDTO.getConfirmationNumber();
			interbanktransferDelegate.updateRequestId(transactionId, requestId);
			result.addParam(new Param("requestId", requestId));
			result.addParam(new Param("referenceId", confirmationNumber));
			
	        if (transactionStatus == TransactionStatusEnum.APPROVED) {
				result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
				result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
			}
			else {
				result.addParam(new Param("status", transactionStatus.getStatus()));
				result.addParam(new Param("message", transactionStatus.getMessage()));
			}

		}
		
		result.addParam(new Param("transactionId", confirmationNumber+""));
		try {
			LogEvents.pushAlertsForApprovalRequests( featureActionId, request, response,inputParams, null, confirmationNumber, requestId, CustomerSession.getCustomerName(customer),null);
		} catch (Exception e) {
			LOG.error("Failed at pushAlertsForApprovalRequests "+e);
		}

		// ADP-7058 update additional meta data
		try{
			approvalQueueDelegate.updateAdditionalMetaForApprovalRequest(transactionStatusDTO.getRequestId(), request);
		} catch(Exception e){
			LOG.error(e);
		}
		
		return result;
	}

	@Override
	public Result deleteTransaction(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();

		String featureActionId = FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CANCEL;
		String requestId = "";

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);

		String createdby = CustomerSession.getCustomerId(customer);
		String legalEntityId = (String) customer.get("legalEntityId");
		boolean isSMEUser = CustomerSession.IsBusinessUser(customer);

		InterBankFundTransferDTO interbankDTO = null;

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		Object transactionIdParam = inputParams.get("confirmationNumber");
		Object confirmationNumberParam = inputParams.get("transactionId") == null ? transactionIdParam : inputParams.get("transactionId");
		String transactionId = null;
		String confirmationNumber = null;
		String transactionType = null;
		String frequencyType = inputParams.get("frequencyType") == null ? null : inputParams.get("frequencyType").toString();
		
		ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);
		List<String> contracts = contractDelegate.fetchContractCustomers(createdby);
		contracts.add(CustomerSession.getCompanyId(customer));

		if (confirmationNumberParam != null && !"".equals(confirmationNumberParam.toString())) {
			confirmationNumber = confirmationNumberParam.toString();
			interbankDTO = interbanktransferDelegate.fetchExecutedTranscationEntry(confirmationNumber, contracts, createdby, legalEntityId);
			if (interbankDTO == null) {
				interbankDTO = interbankBackendDelegate.fetchTransactionById(confirmationNumber, request);
				if(interbankDTO == null) {
					LOG.error("Record doesn't exist");
					return ErrorCodeEnum.ERR_12003.setErrorCode(result);
				}
				interbankDTO.setFeatureActionId(FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);
				interbankDTO.setCreatedby(createdby);
				CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, interbankDTO.getFromAccountNumber());
				String companyId = account.getOrganizationId();
				interbankDTO.setCompanyId(companyId);
				transactionId = interbankDTO.getTransactionId();
				interbankDTO.setConfirmationNumber(transactionId);
				interbankDTO.setTransactionId(null);
				InterBankFundTransferDTO dbxDTO = interbanktransferDelegate.createTransactionAtDBX(interbankDTO);
				if(dbxDTO != null) {
					interbankDTO.setTransactionId(dbxDTO.getTransactionId());
					interbankDTO.setRequestId(dbxDTO.getRequestId());
				}
			}

		} else {
			LOG.error("confirmationNumber or transactionId is missing in the payload which is mandatory for delete");
			return ErrorCodeEnum.ERR_12026.setErrorCode(result);
		}

		transactionId = interbankDTO.getTransactionId();
		transactionType = interbankDTO.getTransactionType();
		String oldrequestId = interbankDTO.getRequestId();

		// Authorization checks on transcationId
		/*
		if (interbankDTO.getCompanyId() != null && ! contracts.contains(interbankDTO.getCompanyId())) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		} else if (!isSMEUser && !CustomerSession.IsCombinedUser(customer)
				&& !interbankDTO.getCreatedby().equals(createdby)) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		*/
		
		// Authorization checks on prevfromAccountNumber and featureactionId
		String prevfromAccountNumber = interbankDTO.getFromAccountNumber();

		if (!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(createdby, featureActionId,
				prevfromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		
		TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
		transactionStatusDTO.setCustomerId(createdby);
		transactionStatusDTO.setCompanyId(interbankDTO.getCompanyId());
		transactionStatusDTO.setAccountId(interbankDTO.getFromAccountNumber());
		transactionStatusDTO.setFeatureActionID(featureActionId);
		transactionStatusDTO.setRequestId(oldrequestId);
		transactionStatusDTO.setAmount(interbankDTO.getAmount());
		transactionStatusDTO.setServiceCharge(interbankDTO.getServiceCharge());
		transactionStatusDTO.setTransactionCurrency(interbankDTO.getTransactionCurrency());
		
		transactionStatusDTO = approvalQueueDelegate.validateForApprovals(transactionStatusDTO, request);			
		if(transactionStatusDTO == null) {			
			return ErrorCodeEnum.ERR_29018.setErrorCode(new Result());
		}
		if(transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null){
			result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
			result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
			return result;
		}
		TransactionStatusEnum transactionStatus = transactionStatusDTO.getStatus();
		boolean isSelfApproved = transactionStatusDTO.isSelfApproved();
		
		if (transactionStatus == TransactionStatusEnum.SENT) {
			InterBankFundTransferDTO interbanktransactionDTO = new InterBankFundTransferDTO();
			interbanktransactionDTO = interbankBackendDelegate.deleteTransactionWithoutApproval(confirmationNumber, transactionType, frequencyType, request);					
			if(interbanktransactionDTO == null) {	
				return ErrorCodeEnum.ERR_12600.setErrorCode(result);
			}
			if(interbanktransactionDTO.getDbpErrCode() != null || interbanktransactionDTO.getDbpErrMsg() != null) {
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, interbanktransactionDTO.getDbpErrMsg());
			}
			String referenceId = interbanktransactionDTO.getReferenceId();
			if(referenceId == null || "".equals(referenceId)) {
				return ErrorCodeEnum.ERR_12601.setErrorCode(result);
			}
			interbanktransferDelegate.deleteTransactionAtDBX(transactionId);
			result.addParam(new Param("referenceId", referenceId));
			result.addParam(new Param("status", transactionStatus.getStatus()));
	        result.addParam(new Param("message", transactionStatus.getMessage()));
		}
		else if(transactionStatus == TransactionStatusEnum.PENDING){
			requestId = transactionStatusDTO.getRequestId();
			InterBankFundTransferDTO interbankpendingtransactionDTO = new InterBankFundTransferDTO();
			interbankpendingtransactionDTO = interbanktransferDelegate.deleteTransactionWithApproval(confirmationNumber, transactionType, frequencyType, transactionId, request);					
			if(interbankpendingtransactionDTO == null)
			{
				LOG.error("Error occured while creating entry into the backend table: ");
				return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
			}
			if(interbankpendingtransactionDTO.getDbpErrCode() != null || interbankpendingtransactionDTO.getDbpErrMsg() != null) {
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, interbankpendingtransactionDTO.getDbpErrMsg());
			}
			confirmationNumber = interbankpendingtransactionDTO.getReferenceId();
			transactionStatusDTO = approvalQueueDelegate.updateBackendIdInApprovalQueue(requestId, confirmationNumber, isSelfApproved, featureActionId, request);
			if(transactionStatusDTO == null) 
			{							
				return ErrorCodeEnum.ERR_29019.setErrorCode(new Result());
			}	
			if(transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
				result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
				result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
				return result;
			}
			transactionStatus = transactionStatusDTO.getStatus();
			confirmationNumber = transactionStatusDTO.getConfirmationNumber();
			interbanktransferDelegate.updateRequestId(transactionId, requestId);
			result.addParam(new Param("requestId", requestId));
			result.addParam(new Param("referenceId", confirmationNumber));

	        if (transactionStatus == TransactionStatusEnum.APPROVED) {
				result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
				result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
			}
			else {
				result.addParam(new Param("status", transactionStatus.getStatus()));
				result.addParam(new Param("message", transactionStatus.getMessage()));
			}
		}
		 
		result.addParam(new Param("transactionId", confirmationNumber+""));
		inputParams.put("amount", interbankDTO.getAmount());
		inputParams.put("scheduledDate", interbankDTO.getScheduledDate());
		inputParams.put("fromAccountNumber", interbankDTO.getFromAccountNumber());
		inputParams.put("toAccountNumber", interbankDTO.getToAccountNumber());
		inputParams.put("isScheduled", interbankDTO.getIsScheduled());
		inputParams.put("Frequency", interbankDTO.getFrequencyTypeId());
		inputParams.put("NoofRecurrences", interbankDTO.getNumberOfRecurrences());
		inputParams.put("TransferDate", interbankDTO.getScheduledDate());
		inputParams.put("EndBy", interbankDTO.getFrequencyEndDate());
		try {
			LogEvents.pushAlertsForApprovalRequests( featureActionId, request, response,inputParams, null, confirmationNumber, requestId, CustomerSession.getCustomerName(customer),null);
		} catch (Exception e) {
			LOG.error("Failed at pushAlertsForApprovalRequests "+e);
		}

		// ADP-7058 update additional meta data
		try{
			approvalQueueDelegate.updateAdditionalMetaForApprovalRequest(transactionStatusDTO.getRequestId(), request);
		} catch(Exception e){
			LOG.error(e);
		}
		return result;
	}

	/**
	 * Logs Other Bank Transfer status in auditactivity
	 * 
	 * @param request
	 * @param response
	 * @param result
	 * @param transactionStatus
	 * @param interbankDTO
	 * @param referenceId
	 * @param requestId
	 */
	private void _logTransaction(DataControllerRequest request, DataControllerResponse response, Object[] inputArray,
			Result result, TransactionStatusEnum transactionStatus, String referenceId,
			InterBankFundTransferDTO interbankDTO, String requestId) {

		String enableEvents = EnvironmentConfigurationsHandler.getValue(Constants.ENABLE_EVENTS, request);
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE))
			return;
		try {
			ApproversBusinessDelegate approversBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(ApproversBusinessDelegate.class);
			Map<String, Object> customer = CustomerSession.getCustomerMap(request);

			AuditLog auditLog = new AuditLog();

			String eventType = Constants.MAKE_TRANSFER;
			String eventSubType = "";
			String producer = "Transactions/POST(createTransfer)";
			String statusID = "";
			String frequencyType = result.getParamValueByName(Constants.FREQUENCYTYPE);
			String isScheduled = result.getParamValueByName(Constants.ISSCHEDULED);
			boolean isSMEUser = CustomerSession.IsBusinessUser(customer);
			String fromAccountNumber = "";
			String toAccountNumber = "";
			
			if (request.containsKeyInRequest("validate")) {
                String validate = request.getParameter("validate");
                if(StringUtils.isNotBlank(validate) && validate.equalsIgnoreCase("true"))
                    return;
            }

			if (request.containsKeyInRequest("fromAccountNumber")) {
				fromAccountNumber = request.getParameter("fromAccountNumber");
			}
			if (request.containsKeyInRequest("toAccountNumber")) {
				toAccountNumber = request.getParameter("toAccountNumber");
			}
			;

			JsonObject customParams = new JsonObject();
			customParams = auditLog.buildCustomParamsForAlertEngine(fromAccountNumber, toAccountNumber, customParams);

			eventSubType = auditLog.deriveSubTypeForExternalTransfer(isScheduled, frequencyType,
					FeatureAction.INTER_BANK_ACCOUNT_FUND_TRANSFER_CREATE);
			List<Param> params = result.getAllParams();
			for (Param param : params) {
				if (request.containsKeyInRequest(param.getName())) {
					continue;
				} else {
					customParams.addProperty(param.getName(), param.getValue());
				}
			}
			if (transactionStatus.toString().contains("DENIED")) {
				statusID = Constants.SID_EVENT_FAILURE;
				customParams.addProperty(Constants.REFERENCEID, result.getParamValueByName(Constants.REFERENCEID));
			} else {
				switch (transactionStatus) {
				case SENT:
					referenceId = customParams.get("referenceId").toString();
					if (interbankDTO == null) {
						statusID = Constants.SID_EVENT_FAILURE;
					}
					if (interbankDTO.getDbpErrMsg() != null && !interbankDTO.getDbpErrMsg().isEmpty()) {
						statusID = Constants.SID_EVENT_FAILURE;
					}
					if (referenceId == null || "".equals(referenceId)) {
						statusID = Constants.SID_EVENT_FAILURE;
					} else {
						statusID = Constants.SID_EVENT_SUCCESS;
						customParams.addProperty(Constants.REFERENCEID, referenceId);
						if (isSMEUser) {
							customParams.addProperty(Constants.APPROVERS, "Pre-Approved");
							customParams.addProperty("approvedBy", "Pre-Approved");
						}
					}
					break;
				case PENDING:
					statusID = Constants.SID_EVENT_SUCCESS;
					customParams.addProperty(Constants.REFERENCEID, referenceId);
					eventSubType = Constants.PENDING_APPROVAL_ + eventSubType;
					List<String> approvers = approversBusinessDelegate.getRequestApproversList(requestId);
					if (approvers == null) {
						customParams.addProperty(Constants.APPROVERS, "");
					} else {
						customParams.addProperty(Constants.APPROVERS, approvers.toString());
					}
					break;
				default:
					break;
				}
			}
			if (isSMEUser) {
				customParams.addProperty("approvedBy", "N/A");
				customParams.addProperty("rejectedBy", "N/A");
			}
			
			AdminUtil.addAdminUserNameRoleIfAvailable(customParams, request);
			
			EventsDispatcher.dispatch(request, response, eventType, eventSubType, producer, statusID, null,
                    CustomerSession.getCustomerId(customer), null,  customParams);
		} catch (Exception e) {
			LOG.error("Error while pushing to Audit Engine.");
		}
	}

}
