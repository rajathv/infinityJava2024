package com.temenos.infinity.product.bulkpaymentservices.resource.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.ErrorConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.objectserviceutils.EventsDispatcher;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.konylabs.middleware.exceptions.MiddlewareException;
import com.temenos.dbx.constants.EventSubType;
import com.temenos.dbx.constants.EventType;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApprovalQueueBusinessDelegate;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApproversBusinessDelegate;
import com.temenos.dbx.product.approvalservices.dto.BBRequestDTO;
import com.temenos.dbx.product.commons.businessdelegate.api.*;
import com.temenos.dbx.product.commons.dto.ApplicationDTO;
import com.temenos.dbx.product.commons.dto.CustomerAccountsDTO;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.commons.dto.TransactionStatusDTO;
import com.temenos.dbx.product.commonsutils.AuditLog;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.temenos.dbx.product.dto.CustomerActionDTO;
import com.temenos.infinity.product.bulkpaymentservices.backenddelegate.api.BulkPaymentPOBackendDelegate;
import com.temenos.infinity.product.bulkpaymentservices.backenddelegate.api.BulkPaymentRecordBackendDelegate;
import com.temenos.infinity.product.bulkpaymentservices.businessdelegate.api.BulkPaymentFileBusinessDelegate;
import com.temenos.infinity.product.bulkpaymentservices.businessdelegate.api.BulkPaymentRecordBusinessDelegate;
import com.temenos.infinity.product.bulkpaymentservices.dto.BulkPaymentPODTO;
import com.temenos.infinity.product.bulkpaymentservices.dto.BulkPaymentRecordDTO;
import com.temenos.infinity.product.bulkpaymentservices.resource.api.BulkPaymentRecordResource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BulkPaymentRecordResourceImpl implements BulkPaymentRecordResource{

	private static final Logger LOG = LogManager.getLogger(BulkPaymentRecordResourceImpl.class);
	private static final String RANDOM_GENERATOR_ALGORITHM = "SHA1PRNG";

	BulkPaymentRecordBackendDelegate bulkPaymentRecordBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(BulkPaymentRecordBackendDelegate.class);
	BulkPaymentRecordBusinessDelegate bulkPaymentRecordBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(BulkPaymentRecordBusinessDelegate.class);
	AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
	ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
	CustomerBusinessDelegate customerDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomerBusinessDelegate.class);
	BulkPaymentFileBusinessDelegate bulkPaymentFileBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(BulkPaymentFileBusinessDelegate.class);
	ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApplicationBusinessDelegate.class);
	private final String BACKEND_DATE_FORMAT = "yyyyMMdd";
	@Override
	public Result fetchOnGoingBulkPayments(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {

		Result result;
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);			
		String customerId = CustomerSession.getCustomerId(customer);
		boolean isCombinedUser = CustomerSession.IsCombinedUser(customer);

		List<String> requiredActionIds = Arrays.asList(FeatureAction.BULK_PAYMENT_REQUEST_VIEW);
		String features = CustomerSession.getPermittedActionIds(request, requiredActionIds);
		if(features == null) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParamsMap = (HashMap<String, Object>) inputArray[1];
		String batchMode = inputParamsMap.get("batchMode") != null ? inputParamsMap.get("batchMode").toString() : null;
		String timeValue = inputParamsMap.get("timeValue") != null ? inputParamsMap.get("timeValue").toString() : null;
		String timeParam = inputParamsMap.get("timeParam") != null ? inputParamsMap.get("timeParam").toString() : null;
		Result executionDates = fetchExecutionDates(timeValue, request, response);
		String fromDate = executionDates.getParamValueByName("fromDate");
		String toDate = executionDates.getParamValueByName("toDate");
		FilterDTO filterDTO = null;
		try {
			filterDTO = JSONUtils.parse(new JSONObject(inputParamsMap).toString(), FilterDTO.class);
		} catch (Exception e) {
			LOG.error("Exception occurred while fetching params: ", e);
			return ErrorCodeEnum.ERR_21223.setErrorCode(new Result());
		}
		
		Set<String> fromAccounts = authorizationChecksBusinessDelegate.fetchMyAccounts(customerId).keySet();
		
		List<BBRequestDTO> requests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "", FeatureAction.BULK_PAYMENT_REQUEST_APPROVE);
		List<BulkPaymentRecordDTO> dbxrecordsWithApprovalInfo = _getRequestsWithCompleteDetails(requests);
		
		for(BulkPaymentRecordDTO record:dbxrecordsWithApprovalInfo) {
			if(record.getStatus().equals(TransactionStatusEnum.APPROVED.getStatus())) {
				record.setStatus(null);
			}
		}
		
		List<BulkPaymentRecordDTO> records = bulkPaymentRecordBusinessDelegate.fetchOnGoingPaymentsfromBackend(fromAccounts, batchMode, fromDate, toDate, request);
		if(bulkPaymentRecordBackendDelegate.getClass().getName().toString().contains("BulkPaymentRecordBackendDelegateImpl"))
		{
			filterDTO.set_timeParam("");
		}
		records = (new FilterDTO()).merge(records, dbxrecordsWithApprovalInfo, "recordId=confirmationNumber", "requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready");

		if(records == null) {
			LOG.error("Error occurred while fetching OnGoing bulk payment records from backend");
			return ErrorCodeEnum.ERR_21223.setErrorCode(new Result());
		}
		if(records.size() > 0 && StringUtils.isNotBlank(records.get(0).getDbpErrMsg())) {
			LOG.error("Error occurred while fetching ongoing bulk payments from backend");
			return ErrorCodeEnum.ERR_00000.setErrorCode(new Result(), records.get(0).getDbpErrMsg());
		}
		if(records.size() == 0){
			LOG.error("No Records Found");
		}

		Set<String> unAuthorizedAccounts = authorizationChecksBusinessDelegate.fetchUnAuthorizedAccounts(
				customerId, FeatureAction.BULK_PAYMENT_REQUEST_VIEW, isCombinedUser);
		filterDTO.set_removeByParam("fromAccount");
		filterDTO.set_removeByValue(unAuthorizedAccounts);
		List<BulkPaymentRecordDTO> filteredRecords = filterDTO.filter(records);


		String BULKPAYMENT_BACKEND= EnvironmentConfigurationsHandler.getValue("BULKPAYMENT_BACKEND");
		if (BULKPAYMENT_BACKEND != null && !BULKPAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			List<CustomerActionDTO> permissionDto = bulkPaymentRecordBusinessDelegate.fetchPermissionFromDBX(fromAccounts);

			//Creating ArrayListValuedHashMap to store duplicate key vallue pair
			ArrayListValuedHashMap<String, String> permissions = new ArrayListValuedHashMap<>();

			//Creating iterator on permission and fiteredrecord Dto
			Iterator itr = permissionDto.iterator();
			Iterator itr2 = filteredRecords.iterator();

			//iterating over permission dto and saving the account id and action id in ArrayListValuedHashMap
			while (itr.hasNext()) {
				CustomerActionDTO newDto = (CustomerActionDTO) itr.next();
				permissions.put(newDto.getAccountId(), newDto.getActionId());
			}

			//iterating over filtered records and removing records according to permission
			while (itr2.hasNext()) {
				BulkPaymentRecordDTO po = (BulkPaymentRecordDTO) itr2.next();
				String accId = po.getFromAccount();
				if(!permissions.containsKey(accId)){
					itr2.remove();
				}
				else if ((po.getBatchMode() == "SINGLE" && (!permissions.containsMapping(accId, "BULK_PAYMENT_TEMPLATE_SINGLE_CREATE")
						|| !permissions.containsMapping(accId, "BULK_PAYMENT_FILES_SINGLE_UPLOAD_CSV")
						|| !permissions.containsMapping(accId, "BULK_PAYMENT_FILES_SINGLE_UPLOAD_XML")))
						|| (po.getBatchMode() == "MULTIPLE" && (!permissions.containsMapping(accId, "BULK_PAYMENT_TEMPLATE_MULTIPLE_CREATE")
						|| !permissions.containsMapping(accId, "BULK_PAYMENT_FILES_MULTI_UPLOAD_CSV")
						|| !permissions.containsMapping(accId, "BULK_PAYMENT_FILES_MULTI_UPLOAD_XML") ))) {
					itr2.remove();
				}
			}
		}
		
		try {
			JSONArray resultRecords = new JSONArray(filteredRecords);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.ONGOINGPAYMENTS, resultRecords);
			result = JSONToResult.convert(resultObject.toString());
		}
		catch(Exception e) {
			LOG.error("Exception occurred while converting DTO to result: ", e);
			return ErrorCodeEnum.ERR_21223.setErrorCode(new Result());
		}

		return result;
	}

	public Result fetchExecutionDates(String timeValue, DataControllerRequest request, 
			DataControllerResponse response) {

		Result result = new Result();
		
		SimpleDateFormat backEndFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
		Date currentDate = new Date();
		Date requiredDate = new Date();
		try {
			currentDate = backEndFormat.parse(bulkPaymentFileBusinessDelegate.getCurrentDateForUpload(request));
		} catch (ParseException e2) {			
			return ErrorCodeEnum.ERR_21228.setErrorCode(new Result(), "Error occured while parsing date");
		}
		if(timeValue.isEmpty() == true)
		{
			timeValue = "6, MONTH";
		}
		String[] timeValues = timeValue.split("\\,");
		if(!(timeValues.length<2 || !StringUtils.isNumeric(timeValues[0]) || StringUtils.isEmpty(timeValues[1]))) {
		
		int period = Integer.parseInt(timeValues[0]) * (-1);
		if(timeValues[1].toUpperCase().contains(Constants.DAY)) {
			requiredDate = DateUtils.addDays(currentDate, period);
		} else if(timeValues[1].toUpperCase().contains(Constants.WEEK)) {
			requiredDate = DateUtils.addWeeks(currentDate, period);
		}else if(timeValues[1].toUpperCase().contains(Constants.MONTH)) {
			requiredDate = DateUtils.addMonths(currentDate, period);
		}else if(timeValues[1].toUpperCase().contains(Constants.YEAR)) {
			requiredDate = DateUtils.addYears(currentDate, period);
		}
		
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		result.addStringParam("fromDate", formatter.format(requiredDate));
		result.addStringParam("toDate", formatter.format(currentDate));
		}
		else
		{
			result.addStringParam("fromDate", "");
			result.addStringParam("toDate", "");
		}
		
		return result;
	}

	@Override
	public Result review(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws MiddlewareException {

		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];

		BulkPaymentRecordBusinessDelegate bulkPaymentRecordBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(BulkPaymentRecordBusinessDelegate.class);
		BulkPaymentRecordBackendDelegate bulkPaymentRecordBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(BulkPaymentRecordBackendDelegate.class);
		AccountBusinessDelegate accountBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AccountBusinessDelegate.class);
		
		BulkPaymentRecordDTO backendRecord = null;
		Result result = new Result();

		String recordId = inputParams.get("recordId") != null ? inputParams.get("recordId").toString() : null;

		if(StringUtils.isEmpty(recordId)) {
			LOG.error("Record ID is Empty.");
			return ErrorCodeEnum.ERR_21227.setErrorCode(new Result());
		}

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String createdby = CustomerSession.getCustomerId(customer);
		String featureActionId = null;
		Double amount = null;
		String fromAccount = null;

		String BULKPAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue("BULKPAYMENT_BACKEND");
		if (BULKPAYMENT_BACKEND != null && BULKPAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			try {
				backendRecord = bulkPaymentRecordBackendDelegate.mockReviewBulkPayment(recordId, request);
				JSONObject bulkPaymentRecordObject = new JSONObject(backendRecord);
				result = JSONToResult.convert(bulkPaymentRecordObject.toString());
			} catch (JSONException e) {
				LOG.error("Error occured while converting the file to JSON", e);
				return ErrorCodeEnum.ERR_21216.setErrorCode(result);
			} catch(Exception e){
				LOG.error("Error while reviewing bulk payment",e);
			}

		} else {
			backendRecord = bulkPaymentRecordBackendDelegate.fetchBulkPaymentRecordDetailsById(recordId, request);

			if (backendRecord == null) {
				return ErrorCodeEnum.ERR_21230.setErrorCode(result);
			}

			if (StringUtils.isNotEmpty((backendRecord.getDbpErrMsg()))) {
				return ErrorCodeEnum.ERR_00000.setErrorCode(result, backendRecord.getDbpErrMsg());
			}

			boolean isRejected = false;
			if (StringUtils.equalsIgnoreCase(backendRecord.getStatus(), "REJECTED")) {
				isRejected = true;
			}

			fromAccount = backendRecord.getFromAccount();
			if(backendRecord.getBatchMode().equalsIgnoreCase("SINGLE")){
				featureActionId = FeatureAction.BULK_PAYMENT_SINGLE_SUBMIT;
			} else if(backendRecord.getBatchMode().equalsIgnoreCase("MULTI")){
				featureActionId = FeatureAction.BULK_PAYMENT_MULTIPLE_SUBMIT;
			}
			CustomerAccountsDTO account = accountBusinessDelegate.getAccountDetails(createdby, fromAccount);
			String contractId = account.getContractId();
			String coreCustomerId = account.getCoreCustomerId();
			String companyId = account.getOrganizationId();
			String baseCurrency = application.getBaseCurrencyFromCache();
			String transactionCurrency = backendRecord.getCurrency() == null ? baseCurrency : backendRecord.getCurrency();
			String serviceCharge = backendRecord.getServiceCharge() == null ? "0.0" : backendRecord.getServiceCharge();

			AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);

			if (!authorizationChecksBusinessDelegate.isOneOfMyAccounts(createdby, fromAccount)) {
				return ErrorCodeEnum.ERR_21222.setErrorCode(result);
			}

			if (!authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(createdby, featureActionId, fromAccount, CustomerSession.IsCombinedUser(customer))) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(result);
			}

			try {
				amount = backendRecord.getTotalAmount();
			} catch (NumberFormatException e) {
				LOG.error("Invalid amount value", e);
				return ErrorCodeEnum.ERR_21232.setErrorCode(new Result());
			}

			String paymentDate = backendRecord.getPaymentDate();

			TransactionStatusDTO transactionStatusDTO = new TransactionStatusDTO();
			transactionStatusDTO.setCustomerId(createdby);
			transactionStatusDTO.setCompanyId(companyId);
			transactionStatusDTO.setAccountId(fromAccount);
			transactionStatusDTO.setAmount(amount);
			transactionStatusDTO.setStatus(TransactionStatusEnum.NEW);
			transactionStatusDTO.setDate(paymentDate);
			transactionStatusDTO.setTransactionCurrency(null);
			transactionStatusDTO.setFeatureActionID(featureActionId);
			transactionStatusDTO.setTransactionCurrency(transactionCurrency);
			transactionStatusDTO.setServiceCharge(serviceCharge);

			BulkPaymentPOBackendDelegate bulkPaymentPOBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(BulkPaymentPOBackendDelegate.class);
			List<BulkPaymentPODTO> paymentOrders = bulkPaymentPOBackendDelegate.fetchPaymentOrders(recordId, request);
			if (paymentOrders == null) {
				return null;
			}

			for (BulkPaymentPODTO dto : paymentOrders) {
				if (dto.getStatus().equalsIgnoreCase(Constants.STATUS_ERROR)
						|| dto.getStatus().equalsIgnoreCase(Constants.STATUS_INERROR)) {
					return ErrorCodeEnum.ERR_28034.setErrorCode(new Result());
				}
			}

			transactionStatusDTO = approvalQueueBusinessDelegate.validateForApprovals(transactionStatusDTO, request);

			if (transactionStatusDTO == null) {
				return ErrorCodeEnum.ERR_29018.setErrorCode(new Result());
			}
			if (transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
				result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
				result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
				return result;
			}

			boolean isSelfApproved = transactionStatusDTO.isSelfApproved();
			TransactionStatusEnum transactionStatus = transactionStatusDTO.getStatus();
			backendRecord.setStatus(transactionStatus.getStatus());
			backendRecord.setFeatureActionId(featureActionId);
			backendRecord.setCreatedby(createdby);
			backendRecord.setCompanyId(companyId);
			backendRecord.setRoleId(customerDelegate.getUserContractCustomerRole(contractId, coreCustomerId, createdby));

			String confirmationNumber = backendRecord.getRecordId();
			backendRecord.setConfirmationNumber(confirmationNumber);
			backendRecord.setRequestId(transactionStatusDTO.getRequestId());
			try {
				backendRecord.setTotalAmount(transactionStatusDTO.getAmount().doubleValue());
			} catch (NumberFormatException e) {
				LOG.error("Invalid amount value", e);
				return ErrorCodeEnum.ERR_10624.setErrorCode(new Result());
			}
			backendRecord.setServiceCharge(transactionStatusDTO.getServiceCharge());
			backendRecord.setTransactionAmount(transactionStatusDTO.getTransactionAmount());
			backendRecord.setTransactionCurrency(transactionCurrency);

			BulkPaymentRecordDTO dbxOldRecord = bulkPaymentRecordBusinessDelegate.fetchBulkPaymentRecordBybackendId(backendRecord.getConfirmationNumber());
			BulkPaymentRecordDTO dbxRecord = null;

			if (dbxOldRecord == null) {
				recordId = String.valueOf(HelperMethods.getIdUsingCurrentTimeStamp());
				backendRecord.setRecordId(recordId);
				dbxRecord = bulkPaymentRecordBusinessDelegate.createBulkPaymentRecord(backendRecord);
			} else {
				backendRecord.setRecordId(dbxOldRecord.getRecordId());
				dbxRecord = bulkPaymentRecordBusinessDelegate.editBulkPaymentRecord(backendRecord);
			}

			String paymentId = null;

			if (dbxRecord == null) {
				LOG.error("Error occured while creating entry into the DBX table: ");
				return ErrorCodeEnum.ERR_29016.setErrorCode(new Result());
			}
			if (dbxRecord.getDbpErrorCode() != null || dbxRecord.getDbpErrMsg() != null) {
				result.addParam(new Param("dbpErrCode", dbxRecord.getDbpErrorCode().getErrorCodeAsString()));
				result.addParam(new Param("dbpErrMsg", dbxRecord.getDbpErrMsg()));
				return result;
			}

			recordId = dbxRecord.getRecordId();

			BulkPaymentRecordDTO initiateResponse = null;
			BulkPaymentRecordDTO updateBMRResponse = new BulkPaymentRecordDTO(dbxRecord);
			String msgDetails = "";
			String requestid = "";
			if (transactionStatus == TransactionStatusEnum.SENT) {
				if (isRejected) {
					try {
						updateBMRResponse = bulkPaymentRecordBackendDelegate.updateBulkPaymentRecordStatus(dbxRecord.getConfirmationNumber(), TransactionStatusEnum.CREATED.getStatus(), request);
					} catch (Exception e) {
						LOG.error("Error while updating the status of Bulk Payment record", e);
					}
				}
				try {
					updateBMRResponse = bulkPaymentRecordBackendDelegate.updateBulkPaymentRecordStatus(dbxRecord.getConfirmationNumber(), TransactionStatusEnum.SENT.getStatus(), request);
				} catch (Exception e) {
					LOG.error("Error while updating the status of Bulk Payment record", e);
				}

				BulkPaymentRecordDTO initiateRequest = new BulkPaymentRecordDTO(dbxRecord);
				initiateRequest.setRecordId(dbxRecord.getConfirmationNumber());

				initiateResponse = bulkPaymentRecordBackendDelegate.initiateBulkPayment(initiateRequest, request);
				if (initiateResponse == null) {
					bulkPaymentRecordBusinessDelegate.updateStatus(recordId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber, ErrorConstants.BACKEND_INVOCATION_FAILED);
					return ErrorCodeEnum.ERR_12600.setErrorCode(result);
				}
				if (initiateResponse.getDbpErrorCode() != null || initiateResponse.getDbpErrMsg() != null) {
					bulkPaymentRecordBusinessDelegate.updateStatus(recordId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber, initiateResponse.getDbpErrMsg());
					return ErrorCodeEnum.ERR_12611.setErrorCodewithErrorDetails(result, initiateResponse.getDbpErrMsg(), initiateResponse.getErrorDetails());
				}
				paymentId = initiateResponse.getPaymentId();

				if (StringUtils.isEmpty(paymentId)) {
					bulkPaymentRecordBusinessDelegate.updateStatus(recordId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber, ErrorConstants.TRANSACTION_CREATION_FAILED_AT_BACKEND);
					return ErrorCodeEnum.ERR_12601.setErrorCode(result);
				}

				bulkPaymentRecordBusinessDelegate.updateStatus(recordId, TransactionStatusEnum.EXECUTED.getStatus(), paymentId, null);
				if (updateBMRResponse.getMessageDetails() != null)
					initiateResponse.setMessageDetails(updateBMRResponse.getMessageDetails());
				initiateResponse.setPaymentDate(dbxRecord.getPaymentDate());
				initiateResponse.setFromAccount(dbxRecord.getFromAccount());
				initiateResponse.setRecordId(dbxRecord.getRecordId());
				initiateResponse.setTotalAmount(dbxRecord.getTotalAmount());
				dbxRecord = initiateResponse;
				result.addParam(new Param("status", transactionStatus.getStatus()));
			} else if (transactionStatus == TransactionStatusEnum.PENDING) {
				if (isRejected) {
					try {
						updateBMRResponse = bulkPaymentRecordBackendDelegate.updateBulkPaymentRecordStatus(dbxRecord.getConfirmationNumber(), TransactionStatusEnum.CREATED.getStatus(), request);
					} catch (Exception e) {
						LOG.error("Error while updating the status of Bulk Payment record", e);
					}
				}
				BulkPaymentRecordDTO bulkpayrecordDTO = bulkPaymentRecordBackendDelegate.updateBulkPaymentRecordStatus(dbxRecord.getConfirmationNumber(), TransactionStatusEnum.PENDING.getStatus(), request);
				if (bulkpayrecordDTO == null) {
					bulkPaymentRecordBusinessDelegate.updateStatus(recordId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber, ErrorConstants.ERROR_UPDATING_BACKENDID);
					LOG.error("Error occured while creating entry into the backend table: ");
					return ErrorCodeEnum.ERR_29017.setErrorCode(new Result());
				}
				if (bulkpayrecordDTO.getDbpErrorCode() != null || bulkpayrecordDTO.getDbpErrMsg() != null) {
					bulkPaymentRecordBusinessDelegate.updateStatus(recordId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber, bulkpayrecordDTO.getDbpErrMsg());
					return ErrorCodeEnum.ERR_00000.setErrorCodewithErrorDetails(result, bulkpayrecordDTO.getDbpErrMsg(), bulkpayrecordDTO.getErrorDetails());
				}
				if (bulkpayrecordDTO.getMessageDetails() != null) {
					msgDetails = bulkpayrecordDTO.getMessageDetails();
				}
				requestid = transactionStatusDTO.getRequestId();

				transactionStatusDTO = approvalQueueBusinessDelegate.updateBackendIdInApprovalQueue(requestid, recordId, isSelfApproved, featureActionId, request);

				if (transactionStatusDTO == null) {
					bulkPaymentRecordBusinessDelegate.updateStatus(recordId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber, ErrorConstants.ERROR_UPDATING_BACKENDID);
					return ErrorCodeEnum.ERR_29019.setErrorCode(new Result());
				}
				if (transactionStatusDTO.getDbpErrCode() != null || transactionStatusDTO.getDbpErrMsg() != null) {
					bulkPaymentRecordBusinessDelegate.updateStatus(recordId, TransactionStatusEnum.FAILED.getStatus(), confirmationNumber, bulkpayrecordDTO.getDbpErrMsg());
					result.addParam(new Param("dbpErrCode", transactionStatusDTO.getDbpErrCode()));
					result.addParam(new Param("dbpErrMsg", transactionStatusDTO.getDbpErrMsg()));
					return result;
				}
				paymentId = transactionStatusDTO.getConfirmationNumber();
				transactionStatus = transactionStatusDTO.getStatus();

				ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
				List<BBRequestDTO> requests = approvalQueueBusinessDelegate.fetchRequests(createdby, "", requestid, featureActionId);

				if (CollectionUtils.isNotEmpty(requests)) {
					try {
						result = JSONToResult.convert((new JSONObject(requests.get(0))).toString());
					} catch (JSONException e) {
						LOG.error("Error occured while converting the file to JSON", e);
					}
				}
				result.addParam(new Param("requestId", requestid));

				if (transactionStatus == TransactionStatusEnum.APPROVED) {
					result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
				} else {
					result.addParam(new Param("status", transactionStatus.getStatus()));
				}

				bulkPaymentRecordBusinessDelegate.updateStatus(recordId, transactionStatus.getStatus(), bulkpayrecordDTO.getConfirmationNumber(), null);
			} else if (transactionStatus == TransactionStatusEnum.APPROVED) {
				result.addParam(new Param("status", TransactionStatusEnum.SENT.getStatus()));
			}

			result.addParam(new Param("paymentId", paymentId));
			if (updateBMRResponse.getMessageDetails() != null)
				result.addParam(new Param("messageDetails", updateBMRResponse.getMessageDetails()));
			else if (msgDetails != null)
				result.addParam(new Param("messageDetails", msgDetails));

			try {
				_logTransaction(request, response, result, EventSubType.BULK_PAYMENT_REQUEST_INITIATE, transactionStatus, dbxRecord, paymentId, requestid);
			} catch (Exception exp) {
				LOG.error("Error occured while audit logging: ", exp);
			}
			// ADP-7058 update additional meta data
			try{
				approvalQueueBusinessDelegate.updateAdditionalMetaForApprovalRequest(transactionStatusDTO.getRequestId(), request);
			} catch(Exception e){
				LOG.error(e);
			}
		}
		return result;
	}

	@Override
	public Result fetchBulkPaymentHistory(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result;
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);			
		String customerId = CustomerSession.getCustomerId(customer);
		boolean isCombinedUser = CustomerSession.IsCombinedUser(customer);

		List<String> requiredActionIds = Arrays.asList(FeatureAction.BULK_PAYMENT_REQUEST_VIEW);
		String features = CustomerSession.getPermittedActionIds(request, requiredActionIds);
		if(features == null) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParamsMap = (HashMap<String, Object>) inputArray[1];
		String timeValue = inputParamsMap.get("timeValue") != null ? inputParamsMap.get("timeValue").toString() : null;
		String timeParam = inputParamsMap.get("timeParam") != null ? inputParamsMap.get("timeParam").toString() : null;
		Result executionDates = fetchExecutionDates(timeValue, request, response);
		String fromDate = executionDates.getParamValueByName("fromDate");
		String toDate = executionDates.getParamValueByName("toDate");
		Set<String> fromAccounts = authorizationChecksBusinessDelegate.fetchMyAccounts(customerId).keySet();
		
		List<BBRequestDTO> requests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "", FeatureAction.BULK_PAYMENT_REQUEST_APPROVE);
		List<BulkPaymentRecordDTO> dbxrecordsWithApprovalInfo = _getRequestsWithCompleteDetails(requests);
		
		List<BulkPaymentRecordDTO> historyRecords = bulkPaymentRecordBusinessDelegate.fetchBulkPaymentHistoryfromBackend(fromAccounts, fromDate, toDate, request);
		
		historyRecords = (new FilterDTO()).merge(historyRecords, dbxrecordsWithApprovalInfo, "recordId=confirmationNumber", "requestId,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready");
		
		if(historyRecords == null) {
			LOG.error("Error occurred while fetching bulk payment history from backend");
			return ErrorCodeEnum.ERR_21214.setErrorCode(new Result());
		}
		if(historyRecords.size() > 0 && StringUtils.isNotBlank(historyRecords.get(0).getDbpErrMsg())) {
			LOG.error("Error occurred while fetching bulk payment history from backend");
			return ErrorCodeEnum.ERR_00000.setErrorCode(new Result(), historyRecords.get(0).getDbpErrMsg());
		}

		FilterDTO filterDTO;
		try {
			filterDTO = JSONUtils.parse(new JSONObject(inputParamsMap).toString(), FilterDTO.class);
		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ",e);
			return ErrorCodeEnum.ERR_21220.setErrorCode(new Result());
		}
		
		if(bulkPaymentRecordBackendDelegate.getClass().getName().toString().contains("BulkPaymentRecordBackendDelegateImpl"))
		{
			filterDTO.set_timeParam("");
		}
		Set<String> unAuthorizedAccounts = authorizationChecksBusinessDelegate.fetchUnAuthorizedAccounts(
				customerId, FeatureAction.BULK_PAYMENT_REQUEST_VIEW, isCombinedUser);
		
		filterDTO.set_removeByParam("fromAccount");
		filterDTO.set_removeByValue(unAuthorizedAccounts);
		List<BulkPaymentRecordDTO> filteredHistory = filterDTO.filter(historyRecords);

		String BULKPAYMENT_BACKEND= EnvironmentConfigurationsHandler.getValue("BULKPAYMENT_BACKEND");
		if (BULKPAYMENT_BACKEND != null && !BULKPAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
			List<CustomerActionDTO> permissionDto = bulkPaymentRecordBusinessDelegate.fetchPermissionFromDBX(fromAccounts);

			//Creating ArrayListValuedHashMap to store duplicate key vallue pair
			ArrayListValuedHashMap<String, String> permissions = new ArrayListValuedHashMap<>();

			//Creating iterator on permission and fiteredhistory Dto
			Iterator itr = permissionDto.iterator();
			Iterator itr2 = filteredHistory.iterator();

			//iterating over permission dto and saving the account id and action id in ArrayListValuedHashMap
			while (itr.hasNext()) {
				CustomerActionDTO newDto = (CustomerActionDTO) itr.next();
				permissions.put(newDto.getAccountId(), newDto.getActionId());
			}

			//iterating over filtered records and removing records according to permission
			while (itr2.hasNext()) {
				BulkPaymentRecordDTO po = (BulkPaymentRecordDTO) itr2.next();
				String accId = po.getFromAccount();
				if(!permissions.containsKey(accId)){
					itr2.remove();
				}
				else if ((po.getBatchMode() == "SINGLE" && (!permissions.containsMapping(accId, "BULK_PAYMENT_TEMPLATE_SINGLE_CREATE")
						|| !permissions.containsMapping(accId, "BULK_PAYMENT_FILES_SINGLE_UPLOAD_CSV")
						|| !permissions.containsMapping(accId, "BULK_PAYMENT_FILES_SINGLE_UPLOAD_XML")))
						|| (po.getBatchMode() == "MULTIPLE" && (!permissions.containsMapping(accId, "BULK_PAYMENT_TEMPLATE_MULTIPLE_CREATE")
						|| !permissions.containsMapping(accId, "BULK_PAYMENT_FILES_MULTI_UPLOAD_CSV")
						|| !permissions.containsMapping(accId, "BULK_PAYMENT_FILES_MULTI_UPLOAD_XML") ))) {
					itr2.remove();
				}
			}
		}

		try {
			JSONArray historyArray = new JSONArray(filteredHistory);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.HISTORY,historyArray);
			result = JSONToResult.convert(resultObject.toString());
		}
		catch(Exception exp) {
			LOG.error("Exception occurred while converting response to result: ",exp);
			return ErrorCodeEnum.ERR_21214.setErrorCode(new Result());
		}
		return result;
	}

	@Override
	public Result approveBulkPaymentRecord(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();
		Object res = null;
		try {
			Map<String, Object> customer = CustomerSession.getCustomerMap(request);

			String customerId = CustomerSession.getCustomerId(customer);
			String requestId = request.getParameter("requestId");
			String comments = request.getParameter("comments");
			if (StringUtils.isEmpty(comments)) {
				comments = "Approving Bulk Payment Record";
			}
			String BULKPAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue("BULKPAYMENT_BACKEND");
			if (BULKPAYMENT_BACKEND != null && BULKPAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
				SecureRandom random = SecureRandom.getInstance(RANDOM_GENERATOR_ALGORITHM);
				long number = (long) (random.nextDouble() * (1e18 - 1e17) + 1e17);
				result.addStringParam("recordId",String.valueOf(number));
				result.addStringParam("featureActionId",FeatureAction.BULK_PAYMENT_REQUEST_SUBMIT);
				result.addStringParam("success", "Successful");
				result.addStringParam("actedBy", customerId);
				result.addStringParam("status", TransactionStatusEnum.APPROVED.getStatus());
			} else {
				/**
				 * @author sribarani.vasthan Start: Added as part of ADP-2810
				 */
				ApplicationDTO applicationDTO = new ApplicationDTO();
				ApplicationBusinessDelegate applicationBusinessDelegate = DBPAPIAbstractFactoryImpl
						.getBusinessDelegate(ApplicationBusinessDelegate.class);
				applicationDTO = applicationBusinessDelegate.properties();
				String createdby = null;

				BBRequestDTO requestDTO = approvalQueueBusinessDelegate.getBbRequest(requestId);

				if (requestDTO != null && StringUtils.isNotEmpty(requestDTO.getCreatedby())) {
					LOG.info("Fetch BBRequest details success");
					createdby = requestDTO.getCreatedby();
				} else {
					LOG.error("Error while fetching BBRequest details");
					return ErrorCodeEnum.ERR_14008.setErrorCode(result);
				}
				if (applicationDTO == null) {
					LOG.info("Error while fetching Application record details");
					return ErrorCodeEnum.ERR_12114.setErrorCode(result);
			}
			else if (createdby != null && !applicationDTO.isSelfApprovalEnabled()
						&& createdby.equalsIgnoreCase(customerId)) {
					return ErrorCodeEnum.ERR_12113.setErrorCode(result);
				}
				/**
				 * End: Added as part of ADP-2810
				 */
				ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);

				if (approvalQueueBusinessDelegate.checkIfUserAlreadyApproved(requestId, customerId)) {
					return ErrorCodeEnum.ERR_21010.setErrorCode(result);
				}

				BulkPaymentRecordDTO recordDTO = bulkPaymentRecordBusinessDelegate.fetchBulkPaymentRecordByRequestId(requestId, request);
				if (recordDTO == null) {
					LOG.error("No record found with given requestId");
					return ErrorCodeEnum.ERR_12000.setErrorCode(result);
				}
//			ApplicationBusinessDelegate application = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApplicationBusinessDelegate.class);
//			SimpleDateFormat fileFormat = new SimpleDateFormat(Constants.SHORT_DATE_FORMAT);
//			fileFormat.setLenient(false);
//			Date paymentDate;
//			Date currentDate;
//			try {
//				if(recordDTO.getPaymentDate() == null) {
//					LOG.error("Record do not have payment Date");
//					return ErrorCodeEnum.ERR_28015.setErrorCode(result);
//				}
//				paymentDate = fileFormat.parse(recordDTO.getPaymentDate());
//				currentDate = fileFormat.parse(application.getServerTimeStamp());
//				if(currentDate!=null && currentDate.after(paymentDate)) {
//					LOG.error("The execution date has expired and this cannot be processed further");
//					return ErrorCodeEnum.ERR_28014.setErrorCode(result);
//				}
//			} catch (ParseException e) {
//				LOG.error("Caught exception at approveBulkPaymentRecords method: " + e);
//				return ErrorCodeEnum.ERR_12000.setErrorCode(result);
//			}

			res = bulkPaymentRecordBusinessDelegate.approveBulkPaymentRecord(requestId, customerId, comments, requestDTO.getCompanyId(), request);
			if(res == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(result);
			}
			else if(res instanceof BulkPaymentRecordDTO) {
					((BulkPaymentRecordDTO) res).setConfirmationNumber(recordDTO.getRecordId());
					((BulkPaymentRecordDTO) res).setRequestId(requestId);
					((BulkPaymentRecordDTO) res).setTotalAmount(recordDTO.getTotalAmount());
					((BulkPaymentRecordDTO) res).setFromAccount(recordDTO.getFromAccount());
					result.addStringParam("recordId", ((BulkPaymentRecordDTO) res).getRecordId() + "");
					result.addStringParam("featureActionId", ((BulkPaymentRecordDTO) res).getFeatureActionId());
					bulkPaymentRecordBusinessDelegate.executeRecordAfterApproval(String.valueOf(((BulkPaymentRecordDTO) res).getRecordId()), request, response, result);
			}
			else if(res instanceof BBRequestDTO) {
					((BBRequestDTO) res).setRequestId(requestId);
					result.addStringParam("recordId", ((BBRequestDTO) res).getTransactionId());
					result.addStringParam("featureActionId", ((BBRequestDTO) res).getFeatureActionId());
				}
				result.addStringParam("success", "Successful");
				result.addStringParam("actedBy", customerId);
				result.addStringParam("status", TransactionStatusEnum.APPROVED.getStatus());
			}
		      }catch(Exception e){
				LOG.error("Caught exception at approveBulkPaymentRecords method: " + e);
				return ErrorCodeEnum.ERR_12000.setErrorCode(result);
			}

		logApproveOrRejectTransaction( request, response, res, EventSubType.BULK_PAYMENT_REQUEST_APPROVE, result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result cancelBulkPaymentRecord(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		String createdby = CustomerSession.getCustomerId(customer);
		String featureActionId = FeatureAction.BULK_PAYMENT_REQUEST_CANCEL;
		String recordId = inputParams.get("recordId") != null ? inputParams.get("recordId").toString() : null;
		String comments = inputParams.get("comments") != null ? inputParams.get("comments").toString() : null;
		String cancellationreason = inputParams.get("cancellationreason") != null ? inputParams.get("cancellationreason").toString() : null;
		String cancellationReasonId = inputParams.get("cancellationReasonId") != null ? inputParams.get("cancellationReasonId").toString() : null;
		String statusCode = inputParams.get("statusCode") != null ? inputParams.get("statusCode").toString() : null;
		
		if (StringUtils.isEmpty(recordId)) {
			LOG.error("recordId is missing");
			return ErrorCodeEnum.ERR_21227.setErrorCode(new Result());
		}

		if (StringUtils.isEmpty(cancellationreason)) {
			LOG.error("cancellationreason is missing");
			return ErrorCodeEnum.ERR_21227.setErrorCode(new Result());
		}
		
		if (StringUtils.isEmpty(cancellationReasonId)) {
			LOG.error("cancellationreasonId is missing");
			return ErrorCodeEnum.ERR_21227.setErrorCode(new Result());
		}

		if (StringUtils.isEmpty(comments)) {
			LOG.error("comments is missing");
			return ErrorCodeEnum.ERR_21227.setErrorCode(new Result());
		}
		
		BulkPaymentRecordDTO bulkPaymentRecordDto =  bulkPaymentRecordBackendDelegate.fetchBulkPaymentRecordDetailsById(recordId, request);
		if(bulkPaymentRecordDto == null) {
			return ErrorCodeEnum.ERR_21230.setErrorCode(result);
		}

		if(StringUtils.isNotEmpty((bulkPaymentRecordDto.getDbpErrMsg()))) {
			return ErrorCodeEnum.ERR_00000.setErrorCode(result, bulkPaymentRecordDto.getDbpErrMsg());
		}

		AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);

		if(! authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction (createdby, featureActionId, null, CustomerSession.IsCombinedUser(customer))) {
			return ErrorCodeEnum.ERR_21236.setErrorCode(result);
		}

		BulkPaymentRecordDTO record = bulkPaymentRecordBackendDelegate.cancelBulkPaymentRecord(recordId, comments, cancellationreason,cancellationReasonId, statusCode, request);

		if (record == null) {
			LOG.error(" Error occured while cancelling Bulk Payment record");
			return ErrorCodeEnum.ERR_21227.setErrorCode(result);			
		}
		
		if(StringUtils.equalsIgnoreCase(record.getStatus(), "DISCARDED")) {
			
			BulkPaymentRecordDTO dbxRecord = bulkPaymentRecordBusinessDelegate.fetchBulkPaymentRecordBybackendId(recordId);
			if(dbxRecord != null) {
				bulkPaymentRecordBusinessDelegate.updateStatus(dbxRecord.getRecordId(), TransactionStatusEnum.CANCELLED.getStatus(),"", cancellationreason);
				
				if(StringUtils.isNotEmpty(dbxRecord.getRequestId())) {
					
					BBRequestDTO bBRequestDTO = approvalQueueBusinessDelegate.updateBBRequestStatus(dbxRecord.getRequestId(), TransactionStatusEnum.CANCELLED.getStatus(), request);
					if(bBRequestDTO == null) LOG.error(" Error occured while updating Status in approvals table");
				}								
				
			}
		}

		try {
			JSONObject bulkPaymentRecordObject = new JSONObject(record);
			result = JSONToResult.convert(bulkPaymentRecordObject.toString());
		} catch (JSONException e) {
			LOG.error("Error occured while converting the file to JSON", e);
			return ErrorCodeEnum.ERR_21200.setErrorCode(result);			
		}

		_logTransaction(request, response, result, EventSubType.BULK_PAYMENT_REQUEST_CANCEL, TransactionStatusEnum.CANCELLED, bulkPaymentRecordDto, bulkPaymentRecordDto.getRecordId(), null);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result updateBulkPaymentRecord(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		String createdby = CustomerSession.getCustomerId(customer);
		String featureActionId = FeatureAction.BULK_PAYMENT_REQUEST_EDIT;
		String recordId = inputParams.get("recordId") != null ? inputParams.get("recordId").toString() : null;
		String fromAccount = inputParams.get("fromAccount") != null ? inputParams.get("fromAccount").toString() : null;
		String description = inputParams.get("description") != null ? inputParams.get("description").toString() : null;

		if (StringUtils.isEmpty(recordId)) {
			LOG.error("recordId is missing");
			return ErrorCodeEnum.ERR_21227.setErrorCode(new Result());
		}

		if (StringUtils.isEmpty(fromAccount) && StringUtils.isEmpty(description)) {
			LOG.error("fromAccount is missing");
			return ErrorCodeEnum.ERR_21242.setErrorCode(new Result());
		}

		AuthorizationChecksBusinessDelegate authorizationChecksBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
		if(! authorizationChecksBusinessDelegate.isUserAuthorizedForFeatureAction(createdby, featureActionId, fromAccount, CustomerSession.IsCombinedUser(customer))) { 
			return ErrorCodeEnum.ERR_21236.setErrorCode(result); 
		}

		BulkPaymentRecordDTO record = bulkPaymentRecordBackendDelegate.updateBulkPaymentRecord(recordId, fromAccount, description, request);// pass both fromAcc and desc
		
		if (record == null) {
			LOG.error(" Error occured while updating Bulk Payment record");
			return ErrorCodeEnum.ERR_21216.setErrorCode(result);			
		}
		
		if(StringUtils.isNotEmpty((record.getDbpErrMsg()))) {
			return ErrorCodeEnum.ERR_00000.setErrorCode(result, record.getDbpErrMsg());
		}

		try {
			JSONObject bulkPaymentRecordObject = new JSONObject(record);
			result = JSONToResult.convert(bulkPaymentRecordObject.toString());
		} catch (JSONException e) {
			LOG.error("Error occured while converting the file to JSON", e);
			return ErrorCodeEnum.ERR_21216.setErrorCode(result);			
		}

		_logTransaction(request, response, result, EventSubType.BULK_PAYMENT_REQUEST_EDIT, TransactionStatusEnum.EDITED, record, record.getRecordId(), null);
		return result;
	}

	@Override
	public Result rejectBulkPaymentRecord(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();

		ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);

		try {
			List<String> requiredApproveActionIds = Arrays.asList(FeatureAction.BULK_PAYMENT_REQUEST_APPROVE);

			String approveActionList = CustomerSession.getPermittedActionIds(request, requiredApproveActionIds);

			String requestId = request.getParameter("requestId");
			Map<String, Object> customer = CustomerSession.getCustomerMap(request);			
			String customerId = CustomerSession.getCustomerId(customer);
			
			ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);
			List<String> contracts = contractDelegate.fetchContractCustomers(customerId);
			contracts.add(CustomerSession.getCompanyId(customer));
			String BULKPAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue("BULKPAYMENT_BACKEND");
			if (BULKPAYMENT_BACKEND != null && BULKPAYMENT_BACKEND.equalsIgnoreCase("MOCK")) {
				SecureRandom random = SecureRandom.getInstance(RANDOM_GENERATOR_ALGORITHM);
				long number = (long) (random.nextDouble() * (1e18 - 1e17) + 1e17);
				result.addStringParam("recordId", String.valueOf(number));
				result.addStringParam("actedBy", customerId);
				result.addStringParam("status", TransactionStatusEnum.REJECTED.getStatus());
			}else {
			BBRequestDTO bbrequestObject = approvalQueueBusinessDelegate.authorizationCheckForRejectAndWithdrawl(requestId, String.join(",", contracts), approveActionList);

			if(bbrequestObject == null) {
				return ErrorCodeEnum.ERR_12001.setErrorCode(result);
			}	

			String comments = request.getParameter("comments");
			String rejectionreason = request.getParameter("rejectionreason");
			if(comments == null || comments == "") {
				comments = "Rejecting Bulk Payment Record";
			}

			if (StringUtils.isEmpty(rejectionreason)) {
				LOG.error("rejectionreason is missing");
				return ErrorCodeEnum.ERR_21227.setErrorCode(new Result());
			}
			String status = approvalQueueBusinessDelegate.getRequestStatus(requestId);			
			if(status == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			}
			else if(!(status.equals(TransactionStatusEnum.PENDING.getStatus()))) {
				return ErrorCodeEnum.ERR_21012.setErrorCode(new Result());
			}

			status = TransactionStatusEnum.REJECTED.getStatus();

			BBRequestDTO bBRequestDTO = approvalQueueBusinessDelegate.updateBBRequestStatus(requestId, status, request);

			if (bBRequestDTO == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			}
			bulkPaymentRecordBusinessDelegate.updateStatus(bBRequestDTO.getTransactionId(), bBRequestDTO.getStatus(),"", rejectionreason); //confirmationNumber not needed			

			result.addStringParam("recordId", bBRequestDTO.getTransactionId());
			result.addStringParam("featureActionId", bBRequestDTO.getFeatureActionId());
			result.addStringParam("actedBy", customerId);
			result.addStringParam("status", TransactionStatusEnum.REJECTED.getStatus());
			BulkPaymentRecordDTO rejectObj= new BulkPaymentRecordDTO();
			try {
				BulkPaymentRecordDTO record = bulkPaymentRecordBusinessDelegate.fetchBulkPaymentRecordDetailsById(bBRequestDTO.getTransactionId());
				 rejectObj=bulkPaymentRecordBackendDelegate.rejectBulkPaymentRecord(record.getConfirmationNumber(), comments, rejectionreason, request);
			} catch(Exception e) {
				LOG.error("Error while updating the status of Bulk Payment record", e);
			}
			if(rejectObj.getDbpErrorCode() != null || rejectObj.getDbpErrMsg() != null) {
				
				BBRequestDTO bBRequestDTONew = approvalQueueBusinessDelegate.updateBBRequestStatus(requestId, TransactionStatusEnum.PENDING.getStatus(), request);
				if(bBRequestDTONew == null) LOG.error(" Error occured while updating Status in approvals table");
				
				bulkPaymentRecordBusinessDelegate.updateStatus(bBRequestDTO.getTransactionId(), TransactionStatusEnum.PENDING.getStatus(),"", "");
				
				return ErrorCodeEnum.ERR_12610.setErrorCodewithErrorDetails(result, rejectObj.getDbpErrMsg(), rejectObj.getErrorDetails());
			}
			
			approvalQueueBusinessDelegate.logActedRequest(
					bBRequestDTO.getRequestId(),
					bBRequestDTO.getCompanyId(), 
					bBRequestDTO.getStatus(), 
					comments, 
					customerId, 
					bBRequestDTO.getStatus());
			
			logApproveOrRejectTransaction( request, response, bBRequestDTO, EventSubType.BULK_PAYMENT_REQUEST_REJECT, result);
			}
			return result;
		} catch (Exception e) {
			LOG.error("Caught exception at rejectBulkPaymentRecord method: " + e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(result);
		}
	}

	@Override
	public Result fetchRecordsWaitingForMyApproval(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();

		ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);
		
		List<String> requiredActionIds = Arrays.asList(FeatureAction.BULK_PAYMENT_REQUEST_APPROVE);
		String featureActionlist =CustomerSession.getPermittedActionIds(request, requiredActionIds);

		if (StringUtils.isEmpty(featureActionlist)) {
			LOG.error("feature List is missing");
			return ErrorCodeEnum.ERR_10227.setErrorCode(new Result());
		}
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];

		FilterDTO newFilterDTO = new FilterDTO();
		List<BulkPaymentRecordDTO> records = null;

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);			
		String customerId = CustomerSession.getCustomerId(customer);
		
		try {
			newFilterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ",e);
            return ErrorCodeEnum.ERR_21220.setErrorCode(new Result());
		}
		List<BBRequestDTO> mainRequests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "", featureActionlist);
		
		if(mainRequests == null) {
			LOG.error("Error occurred while fetching bulk payment requests from Approval Queue");
			return ErrorCodeEnum.ERR_21246.setErrorCode(new Result());
		}
		
		FilterDTO filterDTO = new FilterDTO();
		filterDTO.set_filterByParam("amIApprover,status");
		filterDTO.set_filterByValue("true,Pending");
		/**
		 * @author sribarani.vasthan 
		 * Start: Added as part of ADP-2810
		 */
		ApplicationDTO applicationDTO = new ApplicationDTO();
		ApplicationBusinessDelegate applicationBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(ApplicationBusinessDelegate.class);
		applicationDTO = applicationBusinessDelegate.properties();
		if (applicationDTO != null && !applicationDTO.isSelfApprovalEnabled()) {
			filterDTO.set_removeByParam("amICreator");
			filterDTO.set_removeByValue(new HashSet<String>(Arrays.asList("true")));
		}
		/**
		 * End: Added as part of ADP-2810
		 */
		List<BBRequestDTO> subRequests = filterDTO.filter(mainRequests);
		
		records = fetchBackendResponseAndMergeApprovalInfo(subRequests, request);
		
		if (records == null) {
			LOG.error(" Error occured while fetching Bulk Payment record");
			return ErrorCodeEnum.ERR_21230.setErrorCode(result);			
		}

		records = newFilterDTO.filter(records);
		try {
			JSONArray resultRecords = new JSONArray(records);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.RECORDS, resultRecords);
			result = JSONToResult.convert(resultObject.toString());
			
		} catch (JSONException e) {
			LOG.error("Error occured while converting the file to JSON", e);
			return ErrorCodeEnum.ERR_21200.setErrorCode(result);			
		}

		return result;
	}

	@Override
	public Result fetchRecordsReviewedByMeAndInApprovalQueue(String methodID, Object[] inputArray,
			DataControllerRequest request, DataControllerResponse response) {

		Result result = new Result();

		ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);

		List<String> requiredActionIds = Arrays.asList(FeatureAction.BULK_PAYMENT_REQUEST_APPROVE);
		String featureActionlist =CustomerSession.getPermittedActionIds(request, requiredActionIds);
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];

		FilterDTO newFilterDTO = new FilterDTO();
		List<BulkPaymentRecordDTO> records = null;

		if (StringUtils.isEmpty(featureActionlist)) {
			LOG.error("feature List is missing");
			return ErrorCodeEnum.ERR_10227.setErrorCode(new Result());
		}

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);			
		String customerId = CustomerSession.getCustomerId(customer);
		
		try {
			newFilterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ",e);
            return ErrorCodeEnum.ERR_21220.setErrorCode(new Result());
		}

		List<BBRequestDTO> mainRequests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "", featureActionlist);

		if(mainRequests == null) {
			LOG.error("Error occurred while fetching bulk payment requests from Approval Queue");
			return ErrorCodeEnum.ERR_21246.setErrorCode(new Result());
		}

		FilterDTO filterDTO = new FilterDTO();
		filterDTO.set_filterByParam("status,amICreator");
		filterDTO.set_filterByValue("Pending,true");

		List<BBRequestDTO> subRequests = filterDTO.filter(mainRequests);

		records = fetchBackendResponseAndMergeApprovalInfo(subRequests, request);
		
		if (records == null) {
			LOG.error(" Error occured while fetching Bulk Payment record");
			return ErrorCodeEnum.ERR_21230.setErrorCode(result);			
		}

		records = newFilterDTO.filter(records);
		try {
			JSONArray resultRecords = new JSONArray(records);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.RECORDS, resultRecords);
			result = JSONToResult.convert(resultObject.toString());
		} catch (JSONException e) {
			LOG.error("Error occured while converting the file to JSON", e);
			return ErrorCodeEnum.ERR_21200.setErrorCode(result);			
		}

		return result;	
	}

	@Override
	public Result fetchRecordHistoryReviewedByMe(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();

		List<String> requiredActionIds = Arrays.asList(FeatureAction.BULK_PAYMENT_REQUEST_VIEW);
		String featureActionlist = CustomerSession.getPermittedActionIds(request, requiredActionIds);
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];

		FilterDTO newFilterDTO = new FilterDTO();
		List<BulkPaymentRecordDTO> records = null;
		ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);

		if (StringUtils.isEmpty(featureActionlist)) {
			LOG.error("feature List is missing");
			return ErrorCodeEnum.ERR_10227.setErrorCode(new Result());
		}

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);			
		String customerId = CustomerSession.getCustomerId(customer);
		
		try {
			newFilterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ",e);
            return ErrorCodeEnum.ERR_21220.setErrorCode(new Result());
		}
		
		List<BBRequestDTO> mainRequests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "", featureActionlist);

		if(mainRequests == null) {
			LOG.error("Error occurred while fetching bulk payment requests from Approval Queue");
			return ErrorCodeEnum.ERR_21246.setErrorCode(new Result());
		}
		
		FilterDTO filterDTO = new FilterDTO();
		
		filterDTO.set_removeByParam("status");
		filterDTO.set_removeByValue(new HashSet<>(Arrays.asList(TransactionStatusEnum.PENDING.getStatus())));
		
		filterDTO.set_filterByParam("amICreator");
		filterDTO.set_filterByValue("true");

		List<BBRequestDTO> subRequests = filterDTO.filter(mainRequests);

		records = fetchBackendResponseAndMergeApprovalInfo(subRequests, request);

		if (records == null) {
			LOG.error(" Error occured while fetching Bulk Payment record");
			return ErrorCodeEnum.ERR_21230.setErrorCode(result);			
		}
		
		records = newFilterDTO.filter(records);
		try {
			JSONArray resultRecords = new JSONArray(records);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.RECORDS, resultRecords);
			result = JSONToResult.convert(resultObject.toString());
		} catch (JSONException e) {
			LOG.error("Error occured while converting the file to JSON", e);
			return ErrorCodeEnum.ERR_21200.setErrorCode(result);			
		}

		return result;
	}

	@Override
	public Result FetchRecordsHistoryActedByMe(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();

		List<String> requiredActionIds = Arrays.asList(FeatureAction.BULK_PAYMENT_REQUEST_VIEW);
		String featureActionlist = CustomerSession.getPermittedActionIds(request, requiredActionIds);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>)inputArray[1];
		
		List<BulkPaymentRecordDTO> records = null;
		FilterDTO newFilterDTO = new FilterDTO();
		ApprovalQueueBusinessDelegate approvalQueueBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ApprovalQueueBusinessDelegate.class);

		if (StringUtils.isEmpty(featureActionlist)) {
			LOG.error("feature List is missing");
			return ErrorCodeEnum.ERR_10227.setErrorCode(new Result());
		}

		Map<String, Object> customer = CustomerSession.getCustomerMap(request);			
		String customerId = CustomerSession.getCustomerId(customer);
		
		try {
			newFilterDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
		} catch (IOException e) {
			LOG.error("Exception occurred while fetching params: ",e);
            return ErrorCodeEnum.ERR_21220.setErrorCode(new Result());
		}
		
		List<BBRequestDTO> mainRequests = approvalQueueBusinessDelegate.fetchRequests(customerId, "", "", featureActionlist);

		if(mainRequests == null) {
			LOG.error("Error occurred while fetching bulk payment requests from Approval Queue");
			return ErrorCodeEnum.ERR_21246.setErrorCode(new Result());
		}

		FilterDTO filterDTO = new FilterDTO();

		filterDTO.set_filterByParam("actedByMeAlready");
		filterDTO.set_filterByValue("true");

		List<BBRequestDTO> subRequests = filterDTO.filter(mainRequests);

		records = fetchBackendResponseAndMergeApprovalInfo(subRequests, request);

		if (records == null) {
			LOG.error(" Error occured while fetching Bulk Payment record");
			return ErrorCodeEnum.ERR_21230.setErrorCode(result);			
		}
		
		records = newFilterDTO.filter(records);
		try {
			JSONArray resultRecords = new JSONArray(records);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.RECORDS, resultRecords);
			result = JSONToResult.convert(resultObject.toString());
		} catch (JSONException e) {
			LOG.error("Error occured while converting the file to JSON", e);
			return ErrorCodeEnum.ERR_21200.setErrorCode(result);			
		}

		return result;
	}
	
	@Override
	public List<BulkPaymentRecordDTO> fetchBackendResponseAndMergeApprovalInfo(List<BBRequestDTO> requests, DataControllerRequest dcr) {

		Set<String> backendrecordIdList = new HashSet<String>();
		List<BulkPaymentRecordDTO> backendrecords = new ArrayList<BulkPaymentRecordDTO>();
		
		List<BulkPaymentRecordDTO> dbxrecordsWithApprovalInfo = _getRequestsWithCompleteDetails(requests);
		
		if(dbxrecordsWithApprovalInfo == null) {
			return backendrecords;
		}
		
		for(BulkPaymentRecordDTO dto : dbxrecordsWithApprovalInfo)
			backendrecordIdList.add(dto.getConfirmationNumber());
		
		backendrecords = bulkPaymentRecordBusinessDelegate.fetchRecordsFromBackend(backendrecordIdList, dcr);

		if (backendrecords == null) {
			LOG.error(" Error occured while fetching Bulk Payment record");
			return backendrecords;
		}
		
		backendrecords = (new FilterDTO()).merge(backendrecords, dbxrecordsWithApprovalInfo, "recordId=confirmationNumber", "requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,featureActionId,isGroupMatrix");

		return backendrecords;
		
	}
	
	private List<BulkPaymentRecordDTO> _getRequestsWithCompleteDetails(List<BBRequestDTO> requests) {
		
		Set<String> dbxRecordIdList = new HashSet<String>();
		List<BulkPaymentRecordDTO> dbxRecords = null;
		
		if(requests == null)
			return null;
		
		for(BBRequestDTO bBRequestDTO : requests)
			dbxRecordIdList.add(bBRequestDTO.getTransactionId());
		
		if(dbxRecordIdList.size() == 0) {
			dbxRecords = new ArrayList<BulkPaymentRecordDTO>();
		}
		else {
			dbxRecords = bulkPaymentRecordBusinessDelegate.fetchBulkPaymentRecords(dbxRecordIdList);
		}
		
		if(dbxRecords == null) {
			LOG.error(" Error occured while fetching Bulk Payment record");
			return null;
		}
	
		List<BulkPaymentRecordDTO> dbxrecordsWithApprovalInfo = (new FilterDTO()).merge(dbxRecords, requests, "recordId=transactionId", "requestId,status,amIApprover,amICreator,requiredApprovals,receivedApprovals,actedByMeAlready,isGroupMatrix");
		
		return dbxrecordsWithApprovalInfo;
	}
	
	/**
	 * Logs bulkpayment request's status in auditactivity
	 * @param request
	 * @param response
	 * @param result
	 * @param transactionStatus
	 * @param bulkPaymentRecordDTO
	 * @param referenceId
	 * @param requestId
	 */
	private void _logTransaction(DataControllerRequest request, DataControllerResponse response, Result result, String eventSubType,
			TransactionStatusEnum transactionStatus, BulkPaymentRecordDTO bulkPaymentRecordDTO, String referenceId, String requestId) {
		
		String enableEvents = EnvironmentConfigurationsHandler.getValue(Constants.ENABLE_EVENTS, request);
		if (enableEvents == null || enableEvents.equalsIgnoreCase(Constants.FALSE)) return;
		try {
			ApproversBusinessDelegate approversBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(ApproversBusinessDelegate.class);
			Map<String, Object> customer = CustomerSession.getCustomerMap(request);
			String userName = CustomerSession.getCustomerName(customer);

			String eventType = EventType.BULK_PAYMENT_REQUEST;
			String producer = "";
			String statusID = "";	
			boolean isSMEUser = CustomerSession.IsBusinessUser(customer);
			
			JsonObject customParams = new JsonObject();
			AuditLog auditLog = new AuditLog();
			customParams = auditLog.buildCustomParamsForAlertEngine(bulkPaymentRecordDTO.getFromAccount(), "", customParams);
			customParams.addProperty("fromAccountNumber", bulkPaymentRecordDTO.getFromAccount());
			customParams.addProperty("createdBy", userName);
			customParams.addProperty(Constants.AMOUNT, bulkPaymentRecordDTO.getTotalAmount());
			customParams.addProperty("executiondate", bulkPaymentRecordDTO.getPaymentDate());

			List<Param> params = result.getAllParams();
			for (Param param : params) {
				if (request.containsKeyInRequest(param.getName())) {
					continue;
				} else {
					customParams.addProperty(param.getName(), param.getValue());
				}
			}
			
			switch (eventSubType) {
				case EventSubType.BULK_PAYMENT_REQUEST_INITIATE:
					producer = "BulkPaymentObjects/review";
					break;
				case EventSubType.BULK_PAYMENT_REQUEST_EDIT:
					producer = "BulkPaymentObjects/updateBulkPaymentRecord";
					break;
				case EventSubType.BULK_PAYMENT_REQUEST_CANCEL:
					producer = "BulkPaymentObjects/cancelBulkPaymentRecord";
					break;
				default:
					break;
			}
			
			customParams.addProperty("approvedBy", "N/A");
			customParams.addProperty("rejectedBy", "N/A");
			if (transactionStatus.toString().contains("DENIED")) {
				statusID = Constants.SID_EVENT_FAILURE;
				customParams.addProperty(Constants.REFERENCEID, StringUtils.isNotEmpty(referenceId) ? referenceId.replace("REF-", "") : "");
			} else {
				switch (transactionStatus) {
				case SENT:
					if(bulkPaymentRecordDTO.getDbpErrMsg() != null && !bulkPaymentRecordDTO.getDbpErrMsg().isEmpty()) {
						statusID = Constants.SID_EVENT_FAILURE;
					}
					if(StringUtils.isEmpty(referenceId)) {
						statusID = Constants.SID_EVENT_FAILURE;
					} else {
						statusID = Constants.SID_EVENT_SUCCESS;
						customParams.addProperty(Constants.REFERENCEID, StringUtils.isNotEmpty(referenceId) ? referenceId.replace("REF-", "") : "");
						if (isSMEUser) {
							customParams.addProperty(Constants.APPROVERS, "Pre-Approved");
							customParams.addProperty("approvedBy", "Pre-Approved");
						}
					}
					break;
					
				case PENDING:
					statusID = Constants.SID_EVENT_SUCCESS;
					customParams.addProperty(Constants.REFERENCEID, StringUtils.isNotEmpty(referenceId) ? referenceId.replace("REF-", "") : "");
					List<String> approvers = approversBusinessDelegate.getRequestApproversList(requestId);
					if (approvers == null) {
						customParams.addProperty(Constants.APPROVERS, "");
					} else {
						customParams.addProperty(Constants.APPROVERS, approvers.toString());
					}
					break;
					
				case CANCELLED:
				case EDITED:
					if(bulkPaymentRecordDTO.getDbpErrMsg() != null && !bulkPaymentRecordDTO.getDbpErrMsg().isEmpty()) {
						statusID = Constants.SID_EVENT_FAILURE;
					}
					if(StringUtils.isEmpty(referenceId)) {
						statusID = Constants.SID_EVENT_FAILURE;
					} else {
						statusID = Constants.SID_EVENT_SUCCESS;
						customParams.addProperty(Constants.REFERENCEID, StringUtils.isNotEmpty(referenceId) ? referenceId.replace("REF-", "") : "");
					}
					break;
					
				default:
					break;
				}
				if(TransactionStatusEnum.PENDING.equals(transactionStatus)) {
					EventsDispatcher.dispatch(request, response, eventType, EventSubType.BULK_PAYMENT_REQUEST_WAITING, producer, statusID, null, null, customParams);
					EventsDispatcher.dispatch(request, response, eventType, EventSubType.BULK_PAYMENT_REQUEST_WAITING_ACK, producer, statusID, null, null, customParams);
				} else {
					customParams.addProperty("recordId", bulkPaymentRecordDTO.getRecordId());
					EventsDispatcher.dispatch(request, response, eventType, eventSubType, producer, statusID, null, null, customParams);
				}
			}
		} catch (Exception e) {
			LOG.error("Error while triggering an event for Event Manager",e);
		}
	}
	
	public void logApproveOrRejectTransaction (DataControllerRequest request, DataControllerResponse response, Object res, String eventSubType, Result result)
	{
		
		try {
			String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", request);
			if (enableEvents == null || enableEvents.equalsIgnoreCase("false")) return; 
			
			 ApproversBusinessDelegate approversBusinessDelegate = DBPAPIAbstractFactoryImpl 
					 .getBusinessDelegate(ApproversBusinessDelegate.class);
			 BulkPaymentPOBackendDelegate bulkPaymentPOBackendDelegate = DBPAPIAbstractFactoryImpl.
					 getBackendDelegate(BulkPaymentPOBackendDelegate.class);
				
			 AuditLog auditLog = new AuditLog();
				String transactionId = null;
				String eventType = EventType.BULK_PAYMENT_REQUEST;
				String producer = null;
				List<String> approvers,approvedby = null ;
				String createdBy = "";
				String status = Constants.SID_EVENT_SUCCESS;
				JsonObject resultObject = new JsonParser().parse(ResultToJSON.convert(result)).getAsJsonObject();
				JsonObject customparams = new JsonObject();
				
				if(eventSubType == EventSubType.BULK_PAYMENT_REQUEST_APPROVE)
					producer = "BulkPaymentObjects/approveBulkPaymentRecord";
				else if(eventSubType == EventSubType.BULK_PAYMENT_REQUEST_REJECT)
					producer = "BulkPaymentObjects/rejectBulkPaymentRecord";
				if (res instanceof BBRequestDTO)
				{
					BBRequestDTO dto = (BBRequestDTO) res;
					transactionId = dto.getTransactionId();
					BulkPaymentRecordDTO recordDTO = bulkPaymentRecordBusinessDelegate.fetchBulkPaymentRecordDetailsById(transactionId);
					customparams.addProperty("executiondate", recordDTO.getPaymentDate());
					customparams.addProperty(Constants.AMOUNT, recordDTO.getTotalAmount());
					customparams.addProperty("fromAccountNumber", recordDTO.getFromAccount());
					createdBy = recordDTO.getCreatedby();
					customparams = auditLog.buildCustomParamsForAlertEngine(recordDTO.getFromAccount(), "", customparams);
				}
				else if (res instanceof BulkPaymentRecordDTO )
				{
					BulkPaymentRecordDTO dto = (BulkPaymentRecordDTO) res;
					transactionId = dto.getConfirmationNumber();
					customparams.addProperty("executiondate", ((BulkPaymentRecordDTO) res).getPaymentDate());
					dto = bulkPaymentRecordBusinessDelegate.fetchBulkPaymentRecordDetailsById(dto.getRecordId());
					createdBy = dto.getCreatedby();
				}
				List<BulkPaymentPODTO> poList = bulkPaymentPOBackendDelegate.fetchPaymentOrders(transactionId, request);
				
			    if (poList == null)
			    {
			    	LOG.error("error while logging the rejection/approval of BulkPaymentRequest");
			    	return;
			    }
			    JSONArray transactionDetails = new JSONArray(poList);
				customparams.addProperty(Constants.REFERENCEID, transactionId );
				if (res instanceof BulkPaymentRecordDTO ) {
					customparams.addProperty(Constants.AMOUNT, ((BulkPaymentRecordDTO) res).getTotalAmount());
					customparams.addProperty("fromAccountNumber", ((BulkPaymentRecordDTO) res).getFromAccount());
					customparams = auditLog.buildCustomParamsForAlertEngine(((BulkPaymentRecordDTO) res).getFromAccount(), "", customparams);
				}
				customparams.addProperty("transactionDetails",transactionDetails.toString());
				customparams.add("Response",resultObject);
				String requestId = request.getParameter("requestId");
       			approvers = approversBusinessDelegate.getRequestApproversList(requestId);
				customparams.addProperty(Constants.APPROVERS, approvers.toString());
				String dtFormat = "yyyy-MM-dd'T'hh:mm:ss";
				SimpleDateFormat formatter = new SimpleDateFormat(dtFormat);
				Date currdate = new Date();
				String date = formatter.format(currdate);
				customparams.addProperty("executedOn",date);
				List<String> rejectedBy = null;
				if (res instanceof BBRequestDTO)
				{
					if(Constants.PENDING.equalsIgnoreCase(((BBRequestDTO)res).getStatus())) {
						approvedby = approversBusinessDelegate.getRequestActedApproversList(((BBRequestDTO) res).getRequestId(), TransactionStatusEnum.APPROVED.getStatus());
					} else {
						rejectedBy = approversBusinessDelegate.getRequestActedApproversList(((BBRequestDTO) res).getRequestId(), TransactionStatusEnum.REJECTED.getStatus());
					}
				}
				else if (res instanceof BulkPaymentRecordDTO )
				{
					approvedby = approversBusinessDelegate.getRequestActedApproversList(((BulkPaymentRecordDTO) res).getRequestId(),TransactionStatusEnum.APPROVED.getStatus());
				}
				customparams.addProperty("rejectedBy", CollectionUtils.isNotEmpty(rejectedBy) ? rejectedBy.toString() : "N/A");
				customparams.addProperty("approvedby", CollectionUtils.isNotEmpty(approvedby) ? approvedby.toString() : "N/A");
				
				EventsDispatcher.dispatch(request, response, eventType, eventSubType, producer, status, null, createdBy, customparams);
		}
		catch(Exception exp) {
			LOG.error("error while logging the rejection/approval of ACHrequest");
			return ;
		}
	}
	
	@Override
	public Result fetchBulkPaymentRecordById(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {
		
		Result result;
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams =  (HashMap<String, Object>)inputArray[1];
		String recordId = inputParams.get("recordId").toString();
		List<String> requiredActionIds = Arrays.asList(FeatureAction.BULK_PAYMENT_REQUEST_VIEW);
		String features = CustomerSession.getPermittedActionIds(request, requiredActionIds);
		if(features == null) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		
		BulkPaymentRecordDTO record = bulkPaymentRecordBackendDelegate.fetchBulkPaymentRecordDetailsById(recordId, request);
		if(record == null || StringUtils.isNotBlank(record.getDbpErrMsg())) {
			LOG.error("Error occurred while fetching ongoing bulk payments from backend");
			return ErrorCodeEnum.ERR_00000.setErrorCode(new Result(), record.getDbpErrMsg());
		}
		
		try {
			JSONObject resultObject = new JSONObject(record);
			result = JSONToResult.convert(resultObject.toString());
		}
		catch(Exception e) {
			LOG.error("Exception occurred while converting DTO to result: ", e);
			return ErrorCodeEnum.ERR_21223.setErrorCode(new Result());
		}

		return result;
	}
	
}