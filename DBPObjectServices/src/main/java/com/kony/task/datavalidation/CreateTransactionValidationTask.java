package com.kony.task.datavalidation;

import com.mysql.cj.log.Log;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.mfa.MFAConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.memorymgmt.PayeeManager;
import com.kony.memorymgmt.TransactionManager;
import com.kony.scaintegration.helper.Helper;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

import java.util.regex.Pattern;

public class CreateTransactionValidationTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(CreateTransactionValidationTask.class);
	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		if(!HelperMethods.isDACEnabled()) {
			LOG.debug("data access control is disabled");
			return true;
		}
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			if(HelperMethods.isMFAVerify(requestPayload) || Helper.isScaVerify(requestPayload)) {
				LOG.debug("This is MFA verification call");
				return true;
			}
			
			String customerId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
			
			TransactionManager transactionManager = new TransactionManager(fabricRequestManager, fabricResponseManager);
			String fromAccountNumber = HelperMethods.getStringFromJsonObject(requestPayload, "fromAccountNumber");
			String toAccountNumber = HelperMethods.getStringFromJsonObject(requestPayload, "toAccountNumber");
			String extAccountNumber = HelperMethods.getStringFromJsonObject(requestPayload, "ExternalAccountNumber");
			String payeeId = HelperMethods.getStringFromJsonObject(requestPayload, "payeeId");
			String payPersonId = HelperMethods.getStringFromJsonObject(requestPayload, "personId");
			String payeeAccountNumber = HelperMethods.getStringFromJsonObject(requestPayload, "payeeAccountNumber");
			String featureActionId = HelperMethods.getStringFromJsonObject(requestPayload, MFAConstants.SERVICE_NAME);
			String frequencyStartDate = HelperMethods.getStringFromJsonObject(requestPayload, "frequencyStartDate");
			String scheduledDate = HelperMethods.getStringFromJsonObject(requestPayload, "scheduledDate");
			String transactionsNotes = HelperMethods.getStringFromJsonObject(requestPayload, "transactionsNotes");
			double amount = Double.parseDouble(HelperMethods.getStringFromJsonObject(requestPayload, "amount"));
			double transactionAmount=Double.parseDouble(HelperMethods.getStringFromJsonObject(requestPayload, "transactionAmount")!=null?
					HelperMethods.getStringFromJsonObject(requestPayload, "transactionAmount"):"0.0");

			String transactionType = HelperMethods.getStringFromJsonObject(requestPayload,
					DBPUtilitiesConstants.TRANSACTION_TYPE);
			if (DBPUtilitiesConstants.TRANSACTION_TYPE_DEPOSIT.equals(transactionType)) {
				featureActionId = DBPUtilitiesConstants.TRANSACTION_TYPE_DEPOSIT;
			}
			if(StringUtils.isNotBlank(transactionsNotes)) {
				Pattern specialCharUnacceptablePattern = Pattern.compile("^[a-zA-Z\\d+\\-()?. ]{0,200}$");
				if(!specialCharUnacceptablePattern.matcher(transactionsNotes).matches())
					return updateErrorResult(fabricResponseManager);
			}
            if(amount < 0 || transactionAmount<0){
				LOG.error("Amount and transactionAmount value cannot be negative");
				return updateErrorResult(fabricResponseManager);
			}
			LOG.debug("validating fromAccountNumber {},toAccountNumber {}, extAccountNumber {},"
					+ " payeeId {},payPersonId {},featureActionId {},transactionType {}",
					fromAccountNumber,toAccountNumber,extAccountNumber,payeeId,payPersonId,featureActionId,transactionType);
			if (!transactionManager.validateTransaction(null, featureActionId, fromAccountNumber, toAccountNumber,
					extAccountNumber, payeeId, payPersonId)) {
			    return updateErrorResult(fabricResponseManager);
			}

			if(!(transactionType.equalsIgnoreCase("wire") || transactionType.equalsIgnoreCase("BillPay")) && !transactionManager.validateTransactionDate(frequencyStartDate, scheduledDate))
				return updateErrorResult(fabricResponseManager);
			
            // Additional security check on wire transfer payee account number
            if (StringUtils.isNotBlank(payeeId) && StringUtils.isNotBlank(payeeAccountNumber) && transactionType.equalsIgnoreCase("wire")) {
                PayeeManager payeeManager = new PayeeManager(fabricRequestManager, fabricResponseManager);
                if (!payeeManager.validateWireTransferPayeeAccountNumber(customerId, payeeId, payeeAccountNumber)) {
                    return updateErrorResult(fabricResponseManager);
                }
            }
            
         // Additional security check on Bill Pay Payee account number
            if (StringUtils.isNotBlank(payeeId) && StringUtils.isNotBlank(toAccountNumber) && transactionType.equalsIgnoreCase("BillPay")) {
                PayeeManager payeeManager = new PayeeManager(fabricRequestManager, fabricResponseManager);
                if (!payeeManager.validateBillPayPayeeAccountNumber(customerId, payeeId, toAccountNumber)) {
                    return updateErrorResult(fabricResponseManager);
                }
            }
			
		}
		return true;
	}
	
	private static boolean updateErrorResult(FabricResponseManager fabricResponseManager){
	    JsonObject resPayload = null;
        if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
            resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        }
        resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
        fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
        return false;
	}

}
