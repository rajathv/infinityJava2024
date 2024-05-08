package com.kony.cards.preprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.dbputilities.mfa.preprocessors.CardUpdateMFAPreProcessor;
import com.kony.postprocessors.ObjectServicesConstants;
import com.kony.scaintegration.helper.Constants;
import com.kony.scaintegration.helper.ErrorCodeEnum;
import com.kony.scaintegration.helper.GetConfigParams;
import com.kony.scaintegration.helper.Helper;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class CardUpdatePushAuditPreProcessor implements ObjectProcessorTask {

	private static final Logger LOG = LogManager.getLogger(CardUpdatePushAuditPreProcessor.class);

	@Override
	public boolean process(FabricRequestManager requestManager, FabricResponseManager responseManager)
			throws Exception {
		if (GetConfigParams.getIsScaEnabled() == null)
			GetConfigParams.setIsScaEnabled(Helper.getConfigProperty(Constants.ISSCAENABLED));
		if (GetConfigParams.getIsScaEnabled() == null) {
			LOG.error("IS_SCA_ENABLED runtime param must be set");
		}
		if (Boolean.parseBoolean(GetConfigParams.getIsScaEnabled()))
			return true;
		
		LOG.debug("MFA");
		boolean status = new CardUpdateMFAPreProcessor().process(requestManager, responseManager);
		String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters("ENABLE_EVENTS", requestManager);

		try {

			JsonObject requestPayload = new JsonObject();
			JsonObject responsePayload = new JsonObject();
			PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
			PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();

			try {
				responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
			} catch (Exception e) {
				LOG.error("response is null");
			}
			try {
				requestPayload = requestPayloadHandler.getPayloadAsJson().getAsJsonObject();
			} catch (Exception e) {
				LOG.error("request is null");
			}

			JsonObject customParams = new JsonObject();

			String statusId = ObjectServicesConstants.PARAM_SID_EVENT_SUCCESS;
			String eventType = ObjectServicesConstants.CREDIT_CARD;
			String eventSubType = getEventSubType(requestPayload);
			String producer = "RBObjects/Cards/updateCard";

			if (responsePayload != null && responsePayload.has(ObjectServicesConstants.PARAM_DBP_ERR_CODE)) {
				statusId = ObjectServicesConstants.PARAM_SID_EVENT_FAILURE;
			}

			String customerid = null;
			JsonObject mfaAttr = new JsonObject();
			if (responsePayload != null && responsePayload.has(ObjectServicesConstants.PARAM_MFA_ATRIBUTES)
					&& responsePayload.get(ObjectServicesConstants.PARAM_MFA_ATRIBUTES) != null) {
				mfaAttr = requestPayload.get(ObjectServicesConstants.PARAM_MFA_ATRIBUTES).getAsJsonObject();
			}

			try {
				if (mfaAttr != null && mfaAttr.has("lockUser") && mfaAttr.get("lockUser") != null
						&& mfaAttr.get("lockUser").getAsString().equals("true")) {
					eventType = ObjectServicesConstants.PARAM_LOGIN;
					eventSubType = ObjectServicesConstants.PARAM_ACCOUNT_LOCKED;
					statusId = ObjectServicesConstants.PARAM_SID_EVENT_SUCCESS;
				}
			} catch (Exception e) {
				LOG.error("Error while setting eventsubtype as ACCOUNT_LOCKED", e);
			}
			String newPin = "";
			LOG.error("requestPayload" + requestPayload.toString());
			if (eventSubType.equals(ObjectServicesConstants.CARD_PIN_CHANGE_MFA) && requestPayload.has("newPin")) {
				newPin = requestPayload.get("newPin").getAsString();
				requestPayload.remove("newPin");
			}
			requestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
			if (enableEvents != null && enableEvents.equalsIgnoreCase("true")) {

				try {
					ObjectServiceHelperMethods.execute(new ObjectServiceHelperMethods(requestManager, responseManager,
							eventType, eventSubType, producer, statusId, null, customerid, customParams));
				} catch (Exception e) {
					LOG.error("Error while pushing to Audit Engine", e);
				}
			}
			if (eventSubType.equals(ObjectServicesConstants.CARD_PIN_CHANGE_MFA)) {
				requestPayload.addProperty("newPin", newPin);
				requestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
			}
		} catch (Exception e) {
			LOG.error("error while pushing alert for cards mfa", e);
		}

		return status;
	}

	private static String getEventSubType(JsonObject requestPayload) {

		String eventsubtype = ObjectServicesConstants.CARD_UPDATE_MFA;
		try {
			if (requestPayload != null && requestPayload.has("Action") && requestPayload.get("Action") != null) {
				if (requestPayload.get("Action").getAsString().equals("Activate")) {
					eventsubtype = ObjectServicesConstants.CARD_ACTIVATE_MFA;
				} else if (requestPayload.get("Action").getAsString().equals("Lock")) {
					eventsubtype = ObjectServicesConstants.CARD_LOCK_MFA;
				} else if (requestPayload.get("Action").getAsString().equals("Replace Request")) {
					eventsubtype = ObjectServicesConstants.CARD_REPLACE_MFA;
				} else if (requestPayload.get("Action").getAsString().equals("Lost")) {
					eventsubtype = ObjectServicesConstants.CARD_LOST_MFA;
				} else if (requestPayload.get("Action").getAsString().equals("Cancel")) {
					eventsubtype = ObjectServicesConstants.CARD_CANCEL_MFA;
				} else if (requestPayload.get("Action").getAsString().equals("PinChange")) {
					eventsubtype = ObjectServicesConstants.CARD_PIN_CHANGE_MFA;
				}
			}
		} catch (Exception e) {
			LOG.error("unable to fetch action from request", e);
		}

		return eventsubtype;
	}
}
