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
import com.temenos.dbx.product.transactionservices.backenddelegate.api.IntraBankFundTransferBackendDelegate;
import com.temenos.dbx.product.transactionservices.businessdelegate.api.IntraBankFundTransferBusinessDelegate;
import com.temenos.dbx.product.transactionservices.dto.IntraBankFundTransferBackendDTO;
import com.temenos.dbx.product.transactionservices.dto.IntraBankFundTransferDTO;
import com.temenos.dbx.product.transactionservices.resource.api.IntraBankFundTransferResource;

public class IntraBankFundTransferResourceImpl implements IntraBankFundTransferResource {

	private static final Logger LOG = LogManager.getLogger(IntraBankFundTransferResourceImpl.class);
	CustomerBusinessDelegate customerDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomerBusinessDelegate.class);
	ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApplicationBusinessDelegate.class);
	TransactionLimitsBusinessDelegate limitsDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(TransactionLimitsBusinessDelegate.class);
	IntraBankFundTransferBusinessDelegate intrabankTransactionDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(IntraBankFundTransferBusinessDelegate.class);
	AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AccountBusinessDelegate.class);
	ApprovalQueueBusinessDelegate approvalQueueDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
	AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
	IntraBankFundTransferBackendDelegate intrabankfundBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(IntraBankFundTransferBackendDelegate.class);

    
	public Result createTransaction(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams =  (HashMap<String, Object>)inputArray[1];
		
		IntraBankFundTransferDTO intrabankDTO = null;
		Result result = new Result();
		Double amount = null;
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String createdby = CustomerSession.getCustomerId(customer);
		String legalEntityId = (String) customer.get("legalEntityId");
		String featureActionId = null;
		
		String amountValue = inputParams.get("amount").toString();
		String fromAccountNumber = inputParams.get("fromAccountNumber").toString();
		
		CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, fromAccountNumber);
		String contractId = account.getContractId();
		String coreCustomerId = account.getCoreCustomerId();
		String companyId = account.getOrganizationId();
		String baseCurrency  = application.getBaseCurrencyFromCache();
		String transactionCurrency = inputParams.get("transactionCurrency") != null ?inputParams.get("transactionCurrency").toString() : baseCurrency;
	    String serviceCharge = inputParams.get("serviceCharge") != null ? inputParams.get("serviceCharge").toString() : null;
											 
		if(amountValue == null || amountValue == "") {
			return ErrorCodeEnum.ERR_12031.setErrorCode(new Result());
		}
		
		featureActionId = FeatureAction.INTRA_BANK_FUND_TRANSFER_CREATE;
		
		if(! authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction (createdby, featureActionId, fromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		
		try {
			amount = Double.parseDouble(amountValue);
		}
		catch(NumberFormatException e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_10624.setErrorCode(new Result());
		}
		
		fromAccountNumber = inputParams.get("fromAccountNumber").toString();
		inputParams.put("featureActionId", featureActionId);
		inputParams.put("companyId", companyId);
		inputParams.put("roleId", customerDelegate.getUserContractCustomerRole(contractId, coreCustomerId, createdby));
		inputParams.put("createdby", createdby);
		
		try {
			intrabankDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), IntraBankFundTransferDTO.class);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: ", e);
			return ErrorCodeEnum.ERR_28021.setErrorCode(new Result());
		}
		
		String date = intrabankDTO.getScheduledDate() == null ? 
				(intrabankDTO.getProcessingDate() == null ? 
						(intrabankDTO.getFrequencyStartDate() == null ? 
								application.getServerTimeStamp()
								: intrabankDTO.getFrequencyStartDate())
						: intrabankDTO.getProcessingDate()) 
				: intrabankDTO.getScheduledDate();
								
		String validate = inputParams.get("validate") == null ? null : inputParams.get("validate").toString();
		String backendid = inputParams.get("transactionId") == null || (StringUtils.isEmpty(inputParams.get("transactionId").toString())) ? null : inputParams.get("transactionId").toString();
		String beneficiaryId = inputParams.get("beneficiaryId") == null ? null : inputParams.get("beneficiaryId").toString();
		String beneficiaryName = inputParams.get("beneficiaryName") == null ? null : inputParams.get("beneficiaryName").toString();
		String paidBy = inputParams.get("paidBy") == null ? null : inputParams.get("paidBy").toString();
		String frequencyType = inputParams.get("frequencyType") == null ? null : inputParams.get("frequencyType").toString();
		String requestid = "";

		if("true".equalsIgnoreCase(validate)) {
			
			IntraBankFundTransferBackendDTO intraBankFundTransferBackendDTO = new IntraBankFundTransferBackendDTO();
			intraBankFundTransferBackendDTO = intraBankFundTransferBackendDTO.convert(intrabankDTO);
			
			IntraBankFundTransferDTO validateintrabankDTO = intrabankfundBackendDelegate.validateTransaction(intraBankFundTransferBackendDTO,request);
			try {
				 result = JSONToResult.convert(new JSONObject(validateintrabankDTO).toString());
				 return result;
			} catch (JSONException e) {
				LOG.error("Error occured while converting the response from Line of Business service for intrabank transfer: ", e);
				return ErrorCodeEnum.ERR_21217.setErrorCode(new Result());
			}
		}	 
		
		//these things should come from limits Engine after proper integration with validate call
		
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

		intrabankDTO.setStatus(transactionStatus.getStatus());
		intrabankDTO.setTransactionAmount(transactionStatusDTO.getTransactionAmount());
		try {
			intrabankDTO.setAmount(transactionStatusDTO.getAmount().doubleValue());
		} catch (NumberFormatException e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}
		intrabankDTO.setServiceCharge(transactionStatusDTO.getServiceCharge());
		//intrabankDTO.setTransactionId(null);
		intrabankDTO.setRequestId(transactionStatusDTO.getRequestId());
		String confirmationNumber = (StringUtils.isEmpty(backendid)) ? Constants.REFERENCE_KEY + transactionStatusDTO.getRequestId() : backendid;
		intrabankDTO.setConfirmationNumber(confirmationNumber);
		intrabankDTO.setLegalEntityId(legalEntityId);
		
		IntraBankFundTransferDTO intrabankdbxDTO = intrabankTransactionDelegate.createTransactionAtDBX(intrabankDTO);
		if(intrabankdbxDTO == null) {
			LOG.error("Error occured while creating entry into the DBX table: ");
			return ErrorCodeEnum.ERR_29016.setErrorCode(new Result());
		}
		if(intrabankdbxDTO.getDbpErrCode() != null || intrabankdbxDTO.getDbpErrMsg() != null) {
			result.addParam(new Param("dbpErrCode", intrabankdbxDTO.getDbpErrCode()));
			result.addParam(new Param("dbpErrMsg", intrabankdbxDTO.getDbpErrMsg()));
			return result;
		}
		
		intrabankdbxDTO.setValidate(validate);
		intrabankdbxDTO.setPaidBy(paidBy);
		
		IntraBankFundTransferBackendDTO intrabankBackendDTO = new IntraBankFundTransferBackendDTO();
		intrabankBackendDTO = intrabankBackendDTO.convert(intrabankdbxDTO);
		
		String creditValueDate = inputParams.get("creditValueDate") == null ? "" : inputParams.get("creditValueDate").toString(); 
        String totalAmount = inputParams.get("totalAmount") == null ? "" : inputParams.get("totalAmount").toString();
        String exchangeRate = inputParams.get("exchangeRate") == null ? "" : inputParams.get("exchangeRate").toString();
        String overrides = inputParams.get("overrides") == null ? "" : inputParams.get("overrides").toString();
        String beneficiaryBankName = inputParams.get("beneficiaryBankName") == null ? "" : inputParams.get("beneficiaryBankName").toString();
        String beneficiaryAddressLine1 = inputParams.get("beneficiaryAddressLine1") == null ? "" : inputParams.get("beneficiaryAddressLine1").toString();
        String beneficiaryAddressLine2 = inputParams.get("beneficiaryAddressLine2") == null ? "" : inputParams.get("beneficiaryAddressLine2").toString();
        String beneficiaryPhone = inputParams.get("beneficiaryPhone") == null ? "" : inputParams.get("beneficiaryPhone").toString();
        String beneficiaryEmail = inputParams.get("beneficiaryEmail") == null ? "" : inputParams.get("beneficiaryEmail").toString();
        String beneficiaryState = inputParams.get("beneficiaryState") == null ? "" : inputParams.get("beneficiaryState").toString();
        String beneficiaryCity = inputParams.get("beneficiaryCity") == null ? "" : inputParams.get("beneficiaryCity").toString();
        String beneficiaryZipcode = inputParams.get("beneficiaryZipcode") == null ? "" : inputParams.get("beneficiaryZipcode").toString();
        String beneficiarycountry = inputParams.get("beneficiarycountry") == null ? "" : inputParams.get("beneficiarycountry").toString();
       
        intrabankBackendDTO.setCreditValueDate(creditValueDate);
        intrabankBackendDTO.setTotalAmount(totalAmount);
        intrabankBackendDTO.setExchangeRate(exchangeRate);
		intrabankBackendDTO.setBeneficiaryId(beneficiaryId);
        intrabankBackendDTO.setBeneficiaryName(beneficiaryName);
        intrabankBackendDTO.setOverrides(overrides);
        intrabankBackendDTO.setBeneficiaryBankName(beneficiaryBankName);
        intrabankBackendDTO.setBeneficiaryAddressLine2(beneficiaryAddressLine2);
        intrabankBackendDTO.setBeneficiaryPhone(beneficiaryPhone);
        intrabankBackendDTO.setBeneficiaryEmail(beneficiaryEmail);
        intrabankBackendDTO.setBeneficiaryState(beneficiaryState);
        intrabankBackendDTO.setBeneficiaryAddressLine1(beneficiaryAddressLine1);
        intrabankBackendDTO.setBeneficiaryCity(beneficiaryCity);
        intrabankBackendDTO.setBeneficiarycountry(beneficiarycountry);
        intrabankBackendDTO.setBeneficiaryZipcode(beneficiaryZipcode);
		
        try {
        	intrabankBackendDTO.setAmount(Double.parseDouble(intrabankBackendDTO.getTransactionAmount()));
        	intrabankdbxDTO.setAmount(Double.parseDouble(intrabankBackendDTO.getTransactionAmount()));
		} catch (Exception e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}
        
		try {
			String responseObj = new JSONObject(intrabankBackendDTO).toString();
			result = JSONToResult.convert(responseObj);
		} catch (JSONException e) {
			LOG.error("Error occured while converting the response from Line of Business service for intrabank transfer: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		IntraBankFundTransferDTO intrabanktransactionDTO = new IntraBankFundTransferDTO();
		String transactionid = intrabankdbxDTO.getTransactionId();
        String createWithPaymentId = inputParams.get("createWithPaymentId") == null ? ""
                : inputParams.get("createWithPaymentId").toString();
		
		if(transactionStatus == TransactionStatusEnum.SENT ) {
            if (StringUtils.isEmpty(backendid)
                    || (StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true"))) {
                if(StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true")){
                    intrabankBackendDTO.setTransactionId(backendid);
                    String charges = inputParams.get("charges") == null ? null : inputParams.get("charges").toString();
                    intrabankBackendDTO.setCharges(charges);
                }else {
                    intrabankBackendDTO.setTransactionId(null);
                }
				intrabanktransactionDTO = intrabankfundBackendDelegate.createTransactionWithoutApproval(intrabankBackendDTO, request);					
				if(intrabanktransactionDTO == null) {	
					intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					return ErrorCodeEnum.ERR_12601.setErrorCode(result); 
				}
			}
			else {
				String frequency = StringUtils.isEmpty(intrabankDTO.getFrequencyTypeId()) ? null : intrabankDTO.getFrequencyTypeId();
				intrabanktransactionDTO  = intrabankTransactionDelegate.approveTransaction(backendid, request,frequency);
				if(intrabanktransactionDTO == null) {	
					intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					return ErrorCodeEnum.ERR_29020.setErrorCode(result);
				}
			}
			if(intrabanktransactionDTO.getDbpErrCode() != null || intrabanktransactionDTO.getDbpErrMsg() != null) {
				intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				result.addParam(new Param("errorDetails", intrabanktransactionDTO.getErrorDetails()));
                return ErrorCodeEnum.ERR_00000.setErrorCode(result, intrabanktransactionDTO.getDbpErrMsg());
			}
			if(intrabanktransactionDTO.getReferenceId() == null || "".equals(intrabanktransactionDTO.getReferenceId())) {
				intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				return ErrorCodeEnum.ERR_12601.setErrorCode(result);
			}
			intrabankDTO=intrabanktransactionDTO;
			intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.EXECUTED.getStatus(), intrabanktransactionDTO.getReferenceId());
			result.addParam(new Param("referenceId", intrabanktransactionDTO.getReferenceId()));
			result.addParam(new Param("status", transactionStatus.getStatus()));
	        result.addParam(new Param("message", transactionStatus.getMessage()));
		}
		else if(transactionStatus == TransactionStatusEnum.PENDING){
			intrabankdbxDTO.setCreditValueDate(creditValueDate);
			intrabankdbxDTO.setTotalAmount(totalAmount);
			intrabankdbxDTO.setExchangeRate(exchangeRate);
			requestid = transactionStatusDTO.getRequestId();
			String pendingrefId = null;
			if (StringUtils.isEmpty(backendid)
                    || (StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true"))) {
			    if(StringUtils.isNotBlank(backendid) && createWithPaymentId.equalsIgnoreCase("true")){
			        intrabankdbxDTO.setTransactionId(backendid);
			        String charges = inputParams.get("charges") == null ? null : inputParams.get("charges").toString();
			        intrabankdbxDTO.setCharges(charges);
			    } 
				IntraBankFundTransferDTO intrabankpendingtransactionDTO = intrabankTransactionDelegate.createPendingTransaction(intrabankdbxDTO, request);
				if(intrabankpendingtransactionDTO == null)
				{
					intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					LOG.error("Error occured while creating entry into the backend table: ");
					return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
				}
				if(intrabankpendingtransactionDTO.getDbpErrCode() != null || intrabankpendingtransactionDTO.getDbpErrMsg() != null) {
					intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
					result.addParam(new Param("errorDetails", intrabankpendingtransactionDTO.getErrorDetails()));
					return ErrorCodeEnum.ERR_00000.setErrorCode(result, intrabankpendingtransactionDTO.getDbpErrMsg());
				}
				backendid = intrabankpendingtransactionDTO.getReferenceId();
				intrabankDTO=intrabankpendingtransactionDTO;
			}
				pendingrefId = backendid;
				intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, transactionStatus.toString(), backendid);
				transactionStatusDTO = approvalQueueDelegate.updateBackendIdInApprovalQueue(requestid, backendid, isSelfApproved, featureActionId, request);
				if(transactionStatusDTO == null) 
				{							
					intrabankfundBackendDelegate.deleteTransactionWithoutApproval(backendid, null, frequencyType, request);
					intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), backendid);
					return ErrorCodeEnum.ERR_29019.setErrorCode(new Result());
				}	
				if(transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
					intrabankfundBackendDelegate.deleteTransactionWithoutApproval(backendid, null, frequencyType, request);
					intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, TransactionStatusEnum.FAILED.getStatus(), backendid);
					result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
					result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
					return result;
				}
				
				transactionStatus = transactionStatusDTO.getStatus();
				backendid = transactionStatusDTO.getConfirmationNumber();
				

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
				intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionid, transactionStatus.toString(), pendingrefId);
			}
			
			//code snippet being added for alerts on intra transfer
			try {
				LogEvents.pushAlertsForApprovalRequests( featureActionId, request, response,inputParams, null,  
						backendid, requestid, CustomerSession.getCustomerName(customer),null);
			} catch (Exception e) {
				LOG.error("Failed at pushAlertsForApprovalRequests "+e);
			}
		}

		else if(transactionStatus == TransactionStatusEnum.APPROVED){
			result.addParam(new Param("referenceId", transactionStatusDTO.getConfirmationNumber()));
	        result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
	        result.addParam(new Param("message", TransactionStatusEnum.SENT.getMessage()));
		}
		
		if(intrabankDTO.getOverrides() != null) {
			result.addParam(new Param("overrides",intrabankDTO.getOverrides()));
		}
		if(intrabankDTO.getOverrideList() != null) {
			result.addParam(new Param("overrideList",intrabankDTO.getOverrideList()));
		}
		if(intrabankDTO.getCharges() != null) {
			result.addParam(new Param("charges",intrabankDTO.getCharges()));
		}
		if(intrabankDTO.getExchangeRate() != null) {
			result.addParam(new Param("exchangeRate",intrabankDTO.getExchangeRate()));
		}
		if(intrabankDTO.getTotalAmount() != null) {
			result.addParam(new Param("totalAmount",intrabankDTO.getTotalAmount()));
		}	
		if(intrabankDTO.getMessageDetails() != null) {
            result.addParam(new Param("messageDetails", intrabankDTO.getMessageDetails()));
        }
		if(intrabankDTO.getQuoteCurrency() != null) {
            result.addParam(new Param("quoteCurrency", intrabankDTO.getQuoteCurrency()));
        }
		
		try {
			_logTransaction(request,response,inputArray,result,transactionStatus,transactionStatusDTO.getConfirmationNumber(),intrabankdbxDTO,requestid);
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
	public Result updateStatus(String MethodID, Object[] inputArray,
			DataControllerRequest dcRequest, DataControllerResponse dcResponse) {
		
		Result result = new Result();
		IntraBankFundTransferDTO intrabankTransactionDTO = null;
		
		@SuppressWarnings("unchecked")
		HashMap<String, Object> requestParams = (HashMap<String, Object>)inputArray[1];
		
		String transactionId = (String)requestParams.get("transactionId");
		String confirmationNumber = (String)requestParams.get("confirmationNumber");
		String status = (String)requestParams.get("status");
		
		if(transactionId == null || transactionId == ""){
			return ErrorCodeEnum.ERR_12026.setErrorCode(result);
		}
		if(confirmationNumber == null || confirmationNumber == "") {	
			return ErrorCodeEnum.ERR_20698.setErrorCode(result);
		}
		 if(status == null || status == "") {
			return ErrorCodeEnum.ERR_20699.setErrorCode(result);
		}
		
		try {
			// Initialize the required BusinessDelegate class to perform the operation
			IntraBankFundTransferBusinessDelegate intrabankTransactionDelegate = 
					DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(BusinessDelegateFactory.class)
					.getBusinessDelegate(IntraBankFundTransferBusinessDelegate.class);
			intrabankTransactionDTO = intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId, status, confirmationNumber);
			if(intrabankTransactionDTO == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(result);
			}
			JSONObject intrabankTransactionJSON = new JSONObject(intrabankTransactionDTO);
			result = JSONToResult.convert(intrabankTransactionJSON.toString());
		}
		catch(Exception exp) {
			LOG.error("Exception occured while invoking intrabanktransaction business delegate",exp);
			return ErrorCodeEnum.ERR_12000.setErrorCode(result);
		}
		return result;
	}

    public Result processResponseFromLineOfBusiness(Result result, DataControllerRequest request,
            DataControllerResponse response) {
    	/*
    	HashMap<String, Object>inputParams = new HashMap<String, Object>();
    	
    	String transactionId = request.getParameter("dbxtransactionId");
    	Param referenceId = result.getParamByName("referenceId");
    	TransactionStatusEnum status = null;
    	String confirmationNumber = "";
    	
    	IntraBankFundTransferBusinessDelegate intrabankBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(IntraBankFundTransferBusinessDelegate.class);
    	
    	if(referenceId != null) {
    		status = TransactionStatusEnum.EXECUTED;
    		confirmationNumber = referenceId.getValue();
    	}
    	else {
    		status = TransactionStatusEnum.FAILED;
    	}
    	inputParams.put("status", status.getStatus());
    	intrabankBusinessDelegate.updateStatusUsingTransactionId(transactionId, status.getStatus(), confirmationNumber);
    	
    	result.addParam(new Param("dbxtransactionId", transactionId));
    	*/
    	return result;
    }
    
    @Override
	public Result editTransaction(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
    	@SuppressWarnings("unchecked")
		Map<String, Object> inputParams =  (HashMap<String, Object>)inputArray[1];
		
		IntraBankFundTransferBusinessDelegate intrabankTransactionDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(IntraBankFundTransferBusinessDelegate.class);
		IntraBankFundTransferBackendDelegate intrabankfundBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(IntraBankFundTransferBackendDelegate.class);
		AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
		ApprovalQueueBusinessDelegate approvalQueueDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
		AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AccountBusinessDelegate.class);
		
		String featureActionId = FeatureAction.INTRA_BANK_FUND_TRANSFER_CREATE;
		IntraBankFundTransferDTO intrabankDTO = null;
		Result result = new Result();
		String confirmationNumber = null;
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		
		String createdby = CustomerSession.getCustomerId(customer);
		String legalEntityId = (String) customer.get("legalEntityId");
		boolean isBusinessUser = CustomerSession.IsBusinessUser(customer);
		
		Object transactionIdParam = inputParams.get("confirmationNumber");
		Object confirmationNumberParam = inputParams.get("transactionId") == null ? transactionIdParam : inputParams.get("transactionId");
		
		String fromAccountNumber = inputParams.get("fromAccountNumber").toString();
		
		CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, fromAccountNumber);
		String contractId = account.getContractId();
		String coreCustomerId = account.getCoreCustomerId();
		String companyId = account.getOrganizationId();
		
		if(confirmationNumberParam != null && !"".equals(confirmationNumberParam.toString())) {
			confirmationNumber = confirmationNumberParam.toString();
			intrabankDTO = intrabankTransactionDelegate.fetchExecutedTranscationEntry(confirmationNumber+"", Arrays.asList(companyId), createdby , legalEntityId);
			if(intrabankDTO == null) {
				intrabankDTO = intrabankfundBackendDelegate.fetchTransactionById(confirmationNumber, request);
				if(intrabankDTO == null) {
					LOG.error("Record doesn't exist");
					return ErrorCodeEnum.ERR_12003.setErrorCode(result);
				}else{
				    intrabankDTO.setCompanyId(companyId);
				    intrabankDTO.setCreatedby(createdby);
				    intrabankDTO.setFeatureActionId(featureActionId);
                    if (StringUtils.isNotBlank(inputParams.get("amount").toString())) {
                        intrabankDTO.setAmount(Double.parseDouble(inputParams.get("amount").toString()));
                        intrabankDTO.setTransactionAmount(inputParams.get("amount").toString());
                    }
				    intrabankDTO = intrabankTransactionDelegate.createTransactionAtDBX(intrabankDTO);
				}
			}
		}
		else {
			LOG.error("confirmationNumber or transactionId is missing in the payload which is mandatory for edit");
			return ErrorCodeEnum.ERR_12026.setErrorCode(new Result());
		}
		
		String transactionId = intrabankDTO.getTransactionId();
		String oldRequestId = intrabankDTO.getRequestId();
		
		//Authorization checks on transcationId
		/*
		if(intrabankDTO.getCompanyId() != null && ! intrabankDTO.getCompanyId().equals(companyId)) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		else if(!isBusinessUser && ! CustomerSession.IsCombinedUser(customer) && ! intrabankDTO.getCreatedby().equals(createdby)) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}*/
		
		//Authorization checks on prevfromAccountNumber and featureactionId
		String prevfromAccountNumber = intrabankDTO.getFromAccountNumber();
		
		if(! authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction (createdby, featureActionId, prevfromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		
		IntraBankFundTransferDTO requestDTO = null;
		inputParams.put("createdby", createdby);
		inputParams.put("companyId", companyId);
		inputParams.put("roleId", customerDelegate.getUserContractCustomerRole(contractId, coreCustomerId, createdby));
		
		try {
			requestDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), IntraBankFundTransferDTO.class);
			requestDTO.setConfirmationNumber(null);
			requestDTO.setTransactionId(null);
			intrabankDTO = intrabankDTO.updateValues(requestDTO);
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: ", e);
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}
		
		if(intrabankDTO == null) {
			LOG.error("Error occured while fetching the input params: " );
			return ErrorCodeEnum.ERR_10549.setErrorCode(new Result());
		}
		
		//Authorization checks on newfromAccountNumber and featureactionId
		String newfromAccountNumber = intrabankDTO.getFromAccountNumber();
		
		if(! authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction (createdby, featureActionId, newfromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		
		String date = intrabankDTO.getScheduledDate() == null ? 
				(intrabankDTO.getProcessingDate() == null ? 
						(intrabankDTO.getFrequencyStartDate() == null ? 
								application.getServerTimeStamp()
								: intrabankDTO.getFrequencyStartDate())
						: intrabankDTO.getProcessingDate()) 
				: intrabankDTO.getScheduledDate();
		
		//Transaction limit checks on amount and featureactionId
		Double amount = intrabankDTO.getAmount();
		String transactionAmount = inputParams.get("transactionAmount") != null ? inputParams.get("transactionAmount").toString() : null;
	    String serviceCharge = inputParams.get("serviceCharge") != null ? inputParams.get("serviceCharge").toString() : null;
	    
	    if("0".equals(transactionAmount) || "0.0".equals(transactionAmount) || "0.00".equals(transactionAmount)) {
			transactionAmount = amount + "";
		}
	    
		String validate = inputParams.get("validate") == null ? null : inputParams.get("validate").toString();
		
		if("true".equalsIgnoreCase(validate)) {
			IntraBankFundTransferBackendDTO backendObj = new IntraBankFundTransferBackendDTO();
			backendObj =  backendObj.convert(intrabankDTO);
			IntraBankFundTransferDTO validateownaccountDTO = intrabankfundBackendDelegate.validateTransaction(backendObj,request);
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
		transactionStatusDTO.setTransactionCurrency(intrabankDTO.getTransactionCurrency());
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

		intrabankDTO.setConfirmationNumber(confirmationNumber+"");
		intrabankDTO.setTransactionAmount(transactionStatusDTO.getTransactionAmount());
		try {
			intrabankDTO.setAmount(transactionStatusDTO.getAmount().doubleValue());
		}
		catch(NumberFormatException e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}
		intrabankDTO.setServiceCharge(transactionStatusDTO.getServiceCharge());
		
		IntraBankFundTransferBackendDTO intrabankBackendDTO = new IntraBankFundTransferBackendDTO();
		intrabankBackendDTO = intrabankBackendDTO.convert(intrabankDTO);
		
		try {
        	intrabankBackendDTO.setAmount(Double.parseDouble(intrabankBackendDTO.getTransactionAmount()));
		} catch (Exception e) {
			LOG.error("Invalid amount value", e);
			return ErrorCodeEnum.ERR_27017.setErrorCode(new Result());
		}
		
		String requestObj = null;
		try {
			requestObj = new JSONObject(intrabankBackendDTO).toString();
			result = JSONToResult.convert(requestObj);
		} catch (JSONException e) {
			LOG.error("Error occured while converting the response from Line of Business service for intrabank transfer: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		String referenceId = null;
		IntraBankFundTransferDTO resDTO = null;
		
		String overrides = inputParams.get("overrides") == null ? "" : inputParams.get("overrides").toString();
		intrabankBackendDTO.setOverrides(overrides);
		
		if (transactionStatus == TransactionStatusEnum.SENT) {
			if (confirmationNumber != null && !confirmationNumber.startsWith(Constants.REFERENCE_KEY)) {
				intrabankBackendDTO.setTransactionId(confirmationNumber);
				resDTO = intrabankfundBackendDelegate.editTransactionWithoutApproval(intrabankBackendDTO, request);
			}
			
			if (resDTO == null) {
				intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				return ErrorCodeEnum.ERR_12600.setErrorCode(result);
			}

			if (resDTO.getDbpErrMsg() != null && !resDTO.getDbpErrMsg().isEmpty()) {
				intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				result.addParam(new Param("errorDetails", resDTO.getErrorDetails()));
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, resDTO.getDbpErrMsg());
			}
			
			referenceId = resDTO.getReferenceId();

			if(StringUtils.isEmpty(referenceId)) {
				intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
				return ErrorCodeEnum.ERR_12602.setErrorCode(result);
			}
			
			intrabankDTO = resDTO;
			intrabankDTO.setConfirmationNumber(referenceId);
			intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.EXECUTED.getStatus(), intrabankDTO.getReferenceId());
			
			result.addParam(new Param("referenceId", resDTO.getReferenceId()));
			result.addParam(new Param("status", transactionStatus.getStatus()));
	        result.addParam(new Param("message", transactionStatus.getMessage()));
			
		}
		else if(transactionStatus == TransactionStatusEnum.PENDING){

    		String requestId = transactionStatusDTO.getRequestId();
    		referenceId = confirmationNumber;
    		intrabankBackendDTO.setTransactionId(confirmationNumber);
    		IntraBankFundTransferDTO pendingtransactionDTO = intrabankfundBackendDelegate.editTransactionWithApproval(intrabankBackendDTO, request);
    		if(pendingtransactionDTO == null)
    		{
    			intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
    			LOG.error("Error occured while creating entry into the backend table: ");
    			return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
    		}
    		if(pendingtransactionDTO.getDbpErrCode() != null || pendingtransactionDTO.getDbpErrMsg() != null) {
    			intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber);
    			result.addParam(new Param("errorDetails", pendingtransactionDTO.getErrorDetails()));
    			return ErrorCodeEnum.ERR_00000.setErrorCode(result, pendingtransactionDTO.getDbpErrMsg());
    		}
    		
    		String backendid = pendingtransactionDTO.getReferenceId();
            intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId,transactionStatus.toString(), backendid);
            transactionStatusDTO = approvalQueueDelegate.updateBackendIdInApprovalQueue(requestId, backendid, transactionStatusDTO.isSelfApproved(), featureActionId, request);
            if(transactionStatusDTO == null) 
			{							
				intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), backendid);
				return ErrorCodeEnum.ERR_29019.setErrorCode(new Result());
			}	
			if(transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
				intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId, TransactionStatusEnum.FAILED.getStatus(), backendid);
				result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
				result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
				return result;
			}
            transactionStatus=transactionStatusDTO.getStatus();
    		intrabankTransactionDelegate.updateStatusUsingTransactionId(transactionId,transactionStatus.toString(), backendid);
    		intrabankDTO.setConfirmationNumber(backendid);
    		intrabankDTO.setStatus(transactionStatus.toString());
    		intrabankDTO.setRequestId(requestId);
    		result.addParam(new Param("requestId", requestId));
    		result.addParam(new Param("referenceId", backendid));
    		
    		if(pendingtransactionDTO.getMessageDetails() != null) {
                result.addParam(new Param("messageDetails", pendingtransactionDTO.getMessageDetails()));
            }

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
    		intrabankDTO.setTransactionId(transactionId);
    		intrabankTransactionDelegate.updateTransactionAtDBX(intrabankDTO);
    	}
    	
    	if(intrabankDTO.getMessageDetails() != null) {
            result.addParam(new Param("messageDetails", intrabankDTO.getMessageDetails()));
        }
        
    	if(intrabankDTO.getQuoteCurrency() != null) {
            result.addParam(new Param("quoteCurrency", intrabankDTO.getQuoteCurrency()));
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
	public Result cancelScheduledTransactionOccurrence(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		Result result = new Result();
		
		String featureActionId = FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL;
		String requestId = "";
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		
		String createdby = CustomerSession.getCustomerId(customer);
		boolean isSMEUser = CustomerSession.IsBusinessUser(customer);
		
		IntraBankFundTransferDTO intrabankDTO = null;
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams =  (HashMap<String, Object>)inputArray[1];

		Object transactionIdParam = inputParams.get("confirmationNumber");
		Object confirmationNumberParam = inputParams.get("transactionId") == null ? transactionIdParam : inputParams.get("transactionId");
		String confirmationNumber = null;
		
		ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);
		List<String> contracts = contractDelegate.fetchContractCustomers(createdby);
		contracts.add(CustomerSession.getCompanyId(customer));
		
		if(confirmationNumberParam != null && !"".equals(confirmationNumberParam.toString())) {
			confirmationNumber = confirmationNumberParam.toString();
			intrabankDTO = intrabankTransactionDelegate.fetchExecutedTranscationEntry(confirmationNumber, contracts, createdby , null);
			if(intrabankDTO == null) {
				intrabankDTO = intrabankfundBackendDelegate.fetchTransactionById(confirmationNumber, request);
				if(intrabankDTO == null) {
					LOG.error("Record doesn't exist");
					return ErrorCodeEnum.ERR_12003.setErrorCode(result);
				}
				intrabankDTO.setFeatureActionId(FeatureAction.INTRA_BANK_FUND_TRANSFER_CREATE);
				intrabankDTO.setCreatedby(createdby);
				CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, intrabankDTO.getFromAccountNumber());
				String companyId = account.getOrganizationId();
				intrabankDTO.setCompanyId(companyId);
				String transactionId = intrabankDTO.getTransactionId();
				intrabankDTO.setConfirmationNumber(transactionId);
				intrabankDTO.setTransactionId(null);
				IntraBankFundTransferDTO dbxDTO = intrabankTransactionDelegate.createTransactionAtDBX(intrabankDTO);
				if(dbxDTO != null) {
					intrabankDTO.setTransactionId(dbxDTO.getTransactionId());
					intrabankDTO.setRequestId(dbxDTO.getRequestId());
				}
			}
		}
		else {
			LOG.error("confirmationNumber or transactionId is missing in the payload which is mandatory for cancel occurrence");
			return ErrorCodeEnum.ERR_12026.setErrorCode(result);
		}
		
		String oldrequestId = intrabankDTO.getRequestId();
		String transactionId = intrabankDTO.getTransactionId();
		
		//Authorization checks on transcationId
		/*
		if(intrabankDTO.getCompanyId() != null && ! contracts.contains(intrabankDTO.getCompanyId())) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		else if(!isSMEUser && ! CustomerSession.IsCombinedUser(customer) && ! intrabankDTO.getCreatedby().equals(createdby)) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}*/
		
		//Authorization checks on prevfromAccountNumber and featureactionId
		String prevfromAccountNumber = intrabankDTO.getFromAccountNumber();
		
		if(! authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction (createdby, featureActionId, prevfromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
			
		TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
		transactionStatusDTO.setCustomerId(createdby);
		transactionStatusDTO.setCompanyId(intrabankDTO.getCompanyId());
		transactionStatusDTO.setAccountId(intrabankDTO.getFromAccountNumber());
		transactionStatusDTO.setFeatureActionID(featureActionId);
		transactionStatusDTO.setRequestId(oldrequestId);
		transactionStatusDTO.setAmount(intrabankDTO.getAmount());
		transactionStatusDTO.setServiceCharge(intrabankDTO.getServiceCharge());
		transactionStatusDTO.setTransactionCurrency(intrabankDTO.getTransactionCurrency());
		
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
			IntraBankFundTransferDTO intrabanktransactionDTO = new IntraBankFundTransferDTO();
			intrabanktransactionDTO = intrabankfundBackendDelegate.cancelTransactionWithoutApproval(confirmationNumber, request);					
			if(intrabanktransactionDTO == null) {	
				return ErrorCodeEnum.ERR_12600.setErrorCode(result);
			}
			if(intrabanktransactionDTO.getDbpErrCode() != null || intrabanktransactionDTO.getDbpErrMsg() != null) {
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, intrabanktransactionDTO.getDbpErrMsg());
			}
			String referenceId = intrabanktransactionDTO.getReferenceId();
			if(referenceId == null || "".equals(referenceId)) {
				return ErrorCodeEnum.ERR_12601.setErrorCode(result);
			}
			result.addParam(new Param("referenceId", referenceId));
			result.addParam(new Param("status", transactionStatus.getStatus()));
	        result.addParam(new Param("message", transactionStatus.getMessage()));
		}
		else if(transactionStatus == TransactionStatusEnum.PENDING){
			requestId = transactionStatusDTO.getRequestId();
			IntraBankFundTransferDTO intrabankpendingtransactionDTO = new IntraBankFundTransferDTO();
			intrabankpendingtransactionDTO = intrabankTransactionDelegate.cancelTransactionWithApproval(confirmationNumber, transactionId, request);					
			if(intrabankpendingtransactionDTO == null)
			{
				LOG.error("Error occured while creating entry into the backend table: ");
				return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
			}
			if(intrabankpendingtransactionDTO.getDbpErrCode() != null || intrabankpendingtransactionDTO.getDbpErrMsg() != null) {
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, intrabankpendingtransactionDTO.getDbpErrMsg());
			}
			confirmationNumber = intrabankpendingtransactionDTO.getReferenceId();
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
			intrabankTransactionDelegate.updateRequestId(transactionId, requestId);
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
		
		String featureActionId = FeatureAction.INTRA_BANK_FUND_TRANSFER_CANCEL;
		String requestId = "";
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		
		String createdby = CustomerSession.getCustomerId(customer);
		String legalEntityId = (String) customer.get("legalEntityId");
		boolean isSMEUser = CustomerSession.IsBusinessUser(customer);
		
		IntraBankFundTransferDTO intrabankDTO = null;
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams =  (HashMap<String, Object>)inputArray[1];

		Object transactionIdParam = inputParams.get("confirmationNumber");
		Object confirmationNumberParam = inputParams.get("transactionId") == null ? transactionIdParam : inputParams.get("transactionId");
		
		String transactionId = null;
		String confirmationNumber = null;
		String transactionType = null;
		String frequencyType = inputParams.get("frequencyType") == null ? null : inputParams.get("frequencyType").toString();
		
		ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);
		List<String> contracts = contractDelegate.fetchContractCustomers(createdby);
		contracts.add(CustomerSession.getCompanyId(customer));
		
		if(confirmationNumberParam != null && !"".equals(confirmationNumberParam.toString())) {
			confirmationNumber = confirmationNumberParam.toString();
			intrabankDTO = intrabankTransactionDelegate.fetchExecutedTranscationEntry(confirmationNumber, contracts, createdby ,legalEntityId);
			if(intrabankDTO == null) {
				intrabankDTO = intrabankfundBackendDelegate.fetchTransactionById(confirmationNumber, request);
				if(intrabankDTO == null) {
					LOG.error("Record doesn't exist");
					return ErrorCodeEnum.ERR_12003.setErrorCode(result);
				}
				intrabankDTO.setFeatureActionId(FeatureAction.INTRA_BANK_FUND_TRANSFER_CREATE);
				intrabankDTO.setCreatedby(createdby);
				CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, intrabankDTO.getFromAccountNumber());
				String companyId = account.getOrganizationId();
				intrabankDTO.setCompanyId(companyId);
				transactionId = intrabankDTO.getTransactionId();
				intrabankDTO.setConfirmationNumber(transactionId);
				intrabankDTO.setTransactionId(null);
				IntraBankFundTransferDTO dbxDTO = intrabankTransactionDelegate.createTransactionAtDBX(intrabankDTO);
				if(dbxDTO != null) {
					intrabankDTO.setTransactionId(dbxDTO.getTransactionId());
					intrabankDTO.setRequestId(dbxDTO.getRequestId());
				}
			}
		}
		else {
			LOG.error("confirmationNumber or transactionId is missing in the payload which is mandatory for delete");
			return ErrorCodeEnum.ERR_12026.setErrorCode(result);
		}
		
		transactionId = intrabankDTO.getTransactionId();
		transactionType = intrabankDTO.getTransactionType();
		String oldrequestId = intrabankDTO.getRequestId();
		
		//Authorization checks on transcationId
		/*
		if(intrabankDTO.getCompanyId() != null && ! contracts.contains(intrabankDTO.getCompanyId())) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		else if(!isSMEUser && ! CustomerSession.IsCombinedUser(customer) && ! intrabankDTO.getCreatedby().equals(createdby)) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		*/
		//Authorization checks on prevfromAccountNumber and featureactionId
		String prevfromAccountNumber = intrabankDTO.getFromAccountNumber();
		
		if(! authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction (createdby, featureActionId, prevfromAccountNumber, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(result);
		}
		
		TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
		transactionStatusDTO.setCustomerId(createdby);
		transactionStatusDTO.setCompanyId(intrabankDTO.getCompanyId());
		transactionStatusDTO.setAccountId(intrabankDTO.getFromAccountNumber());
		transactionStatusDTO.setFeatureActionID(featureActionId);
		transactionStatusDTO.setRequestId(oldrequestId);
		transactionStatusDTO.setAmount(intrabankDTO.getAmount());
		transactionStatusDTO.setServiceCharge(intrabankDTO.getServiceCharge());
		transactionStatusDTO.setTransactionCurrency(intrabankDTO.getTransactionCurrency());
		
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
			IntraBankFundTransferDTO intrabanktransactionDTO = new IntraBankFundTransferDTO();
			//rename to deleteTransactionOccurrenceWithoutApproval
			intrabanktransactionDTO = intrabankfundBackendDelegate.deleteTransactionWithoutApproval(confirmationNumber, transactionType, frequencyType, request);					
			if(intrabanktransactionDTO == null) {	
				return ErrorCodeEnum.ERR_12600.setErrorCode(result);
			}
			if(intrabanktransactionDTO.getDbpErrCode() != null || intrabanktransactionDTO.getDbpErrMsg() != null) {
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, intrabanktransactionDTO.getDbpErrMsg());
			}
			String referenceId = intrabanktransactionDTO.getReferenceId();
			if(referenceId == null || "".equals(referenceId)) {
				return ErrorCodeEnum.ERR_12601.setErrorCode(result);
			}
			intrabankTransactionDelegate.deleteTransactionAtDBX(transactionId);
			result.addParam(new Param("referenceId", referenceId));
			result.addParam(new Param("status", transactionStatus.getStatus()));
	        result.addParam(new Param("message", transactionStatus.getMessage()));
		}
		else if(transactionStatus == TransactionStatusEnum.PENDING){
			requestId = transactionStatusDTO.getRequestId();
			IntraBankFundTransferDTO intrabankpendingtransactionDTO = new IntraBankFundTransferDTO();
			intrabankpendingtransactionDTO = intrabankTransactionDelegate.deleteTransactionWithApproval(confirmationNumber, transactionType, frequencyType, transactionId, request);					
			if(intrabankpendingtransactionDTO == null)
			{
				LOG.error("Error occured while creating entry into the backend table: ");
				return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
			}
			if(intrabankpendingtransactionDTO.getDbpErrCode() != null || intrabankpendingtransactionDTO.getDbpErrMsg() != null) {
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, intrabankpendingtransactionDTO.getDbpErrMsg());
			}
			confirmationNumber = intrabankpendingtransactionDTO.getReferenceId();
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
			
			result.addParam(new Param("requestId", requestId));
			result.addParam(new Param("referenceId", confirmationNumber));
			intrabankTransactionDelegate.updateRequestId(transactionId, requestId);
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
		inputParams.put("amount", intrabankDTO.getAmount());
		inputParams.put("scheduledDate", intrabankDTO.getScheduledDate());
		inputParams.put("fromAccountNumber", intrabankDTO.getFromAccountNumber());
		inputParams.put("toAccountNumber", intrabankDTO.getToAccountNumber());
		inputParams.put("isScheduled", intrabankDTO.getIsScheduled());
		inputParams.put("Frequency", intrabankDTO.getFrequencyTypeId());
		inputParams.put("NoofRecurrences", intrabankDTO.getNumberOfRecurrences());
		inputParams.put("TransferDate", intrabankDTO.getScheduledDate());
		inputParams.put("EndBy", intrabankDTO.getFrequencyEndDate());
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
	 * Logs Own Account Transfer status in auditactivity
	 * @param request
	 * @param response
	 * @param result
	 * @param transactionStatus
	 * @param intrabankDTO
	 * @param referenceId
	 * @param requestId
	 */
	private void _logTransaction(DataControllerRequest request,DataControllerResponse response,Object[] inputArray,Result result, TransactionStatusEnum transactionStatus, String referenceId,IntraBankFundTransferDTO intrabankDTO,String requestId) {
		
		String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", request);
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE)) return;
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
					FeatureAction.INTRA_BANK_FUND_TRANSFER_CREATE);
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
					if (intrabankDTO == null) {
						statusID = Constants.SID_EVENT_FAILURE;
					}
					if (intrabankDTO.getDbpErrMsg() != null && !intrabankDTO.getDbpErrMsg().isEmpty()) {
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
