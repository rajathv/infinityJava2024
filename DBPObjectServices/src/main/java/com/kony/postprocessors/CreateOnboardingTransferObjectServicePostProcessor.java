package com.kony.postprocessors;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class CreateOnboardingTransferObjectServicePostProcessor implements ObjectServicePostProcessor, ObjectServicesConstants  {
	
	private static final Logger logger = LogManager.getLogger(CreateOnboardingTransferObjectServicePostProcessor.class);
	@Override
	public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager)
			throws Exception {
		try {
			PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
			PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();
			JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
			logger.info("Value of responsePayload"+ responsePayload);
			JsonObject requestPayload = requestPayloadHandler.getPayloadAsJson().getAsJsonObject();
			logger.info("Value of requestPayload"+ requestPayload);
			JsonObject customParams = new JsonObject();
			String statusId = PARAM_SID_EVENT_FAILURE;
			String opstatus = "";
			String customerid = HelperMethods.getStringFromJsonObject(requestPayload, "customerId", true);
			logger.info("Value of customerid"+ customerid);
			String eventSubType = "";
			String eventType = PARAM_MAKE_TRANSFER;
			String producer = "RBObjects/OnboardingTransactions/createOnboardingTransfer";
			String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS,
					requestManager);
			logger.info("Value of enableEvents"+ enableEvents);
			String fromAccountNumber = "";
			String paymentStatus = HelperMethods.getStringFromJsonObject(responsePayload, "status", true);
			logger.info("Value of paymentStatus"+ paymentStatus);
			if (ObjectServiceHelperMethods.hasKey(requestPayload, "debitAccountId"))
				fromAccountNumber = HelperMethods.getStringFromJsonObject(requestPayload, "debitAccountId",
						true);
				logger.info("Value of fromAccountNumber"+ fromAccountNumber);
			if (ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_OP_STATUS)) {
				opstatus = HelperMethods.getStringFromJsonObject(responsePayload, PARAM_OP_STATUS, true);
				logger.info("Value of opstatus"+ opstatus);
			}

			if (opstatus.equals("0") && !ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE) && (paymentStatus.equalsIgnoreCase("success"))) {
				statusId = PARAM_SID_EVENT_SUCCESS;
				logger.info("Value of StatusId: "+ statusId);
			}
			if (enableEvents != null && enableEvents.equalsIgnoreCase(PARAM_TRUE)) {
				if (statusId.equals(PARAM_SID_EVENT_SUCCESS)) {
					customParams = buildCustomParamsForAlertEngine(requestPayload, responsePayload);
				}
				eventSubType = PARAM_ONETIME_OWN_ACCOUNT_TRANSFER;
				customParams = this.addTransactionDate(customParams);
				//customParams.addProperty("externalphone", "+91-123456789");
				//customParams.addProperty("externalemail", "abc@temenos.com");
				logger.info("Value of customParams"+ customParams);
				if (!eventSubType.equals("")) {
					try {
						
						AdminUtil.addAdminUserNameRoleIfAvailable(customParams, requestManager);
						
						ObjectServiceHelperMethods
								.execute(new ObjectServiceHelperMethods(requestManager, responseManager, eventType,
										eventSubType, producer, statusId, fromAccountNumber, customerid, customParams));
						logger.info("Payment Notification sent successfully");
					} catch (Exception e2) {
						logger.error("Exception Occured while invoking objectServiceHelperMethods");
					}
				}
			}
			
		} catch (Exception ex) {
			logger.debug("exception occured in Create Onboarding transfer object service post processor", ex);
		}
	}
	
	JsonObject buildCustomParamsForAlertEngine(JsonObject requestPayload, JsonObject responsePayload) {
		JsonObject customParams = new JsonObject();
		if (ObjectServiceHelperMethods.hasKey(requestPayload, "debitAccountId")) {

			String fromAccountNumber = HelperMethods.getStringFromJsonObject(requestPayload, "debitAccountId", true);
			if (fromAccountNumber != null && !fromAccountNumber.isEmpty()) {

				String lastFourDigits ;
				if(fromAccountNumber.length() > 4)
					lastFourDigits= fromAccountNumber.substring(fromAccountNumber.length() - 4);
				else
					lastFourDigits= fromAccountNumber;
				
				customParams.addProperty("MaskedFromAccount", "XXXX" + lastFourDigits);
			}
		}

		if (ObjectServiceHelperMethods.hasKey(requestPayload, "creditAccountId")) {
			String toAccountNumber = HelperMethods.getStringFromJsonObject(requestPayload, "creditAccountId",
					true);
			if (toAccountNumber != null && !toAccountNumber.isEmpty()) {
				
				String lastFourDigits ;
				if(toAccountNumber.length() > 4)
					lastFourDigits= toAccountNumber.substring(toAccountNumber.length() - 4);
				else
					lastFourDigits= toAccountNumber;
				
				customParams.addProperty("MaskedToAccount", "XXXX" + lastFourDigits);
			}
		}

		customParams.addProperty(PARAM_SERVER_DATE, new Date().toString());
		if (ObjectServiceHelperMethods.hasKey(responsePayload, "referenceId")) {
			customParams.addProperty("RefNumber",
					HelperMethods.getStringFromJsonObject(responsePayload, "referenceId", true));
		}
		// code Works for If it needs to be pushed to alert engine
		return customParams;
	}
	
	private JsonObject addTransactionDate(JsonObject customParams) {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		try {
			customParams.addProperty("transactionDate", formatter.format(new Date()));
		} catch (Exception ez) {

			logger.error("dateFormatter failed" + ez);
		}
		return customParams;
	}
}
