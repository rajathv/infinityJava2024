package com.kony.dbputilities.customersecurityservices.postprocessor;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonObject;
import com.kony.dbputilities.mfa.MFAConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.objectserviceutils.EventsDispatcher;
import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class ResetDbxUserPasswordPostProcessor implements ObjectServicePostProcessor {

	private static final Logger logger = LogManager.getLogger(ResetDbxUserPasswordPostProcessor.class);

	@Override
	public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

		logger.error("inside ResetDbxUserPasswordPostProcessor");
		JsonObject response = responseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
		JsonObject request = requestManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();

		if (response.has(ErrorCodeEnum.ERROR_MESSAGE_KEY) || response.has(ErrorCodeEnum.ERROR_CODE_KEY)) {
			responseManager.getPayloadHandler().updatePayloadAsJson(response);
			logger.error("false case=");
			pushAlert(requestManager, responseManager, false);
			return;
		}
		logger.error("true case=");
		pushAlert(requestManager, responseManager, true);
		removeServiceKey(requestManager, response, request);
		responseManager.getPayloadHandler().updatePayloadAsJson(response);
		return;
	}

	private String getConfigurableParameters(String key, FabricRequestManager requestInstance) {
		try {
			ServicesManager serviceManager = requestInstance.getServicesManager();
			ConfigurableParametersHelper configurableParametersHelper = serviceManager
					.getConfigurableParametersHelper();
			String requiredURL = configurableParametersHelper.getServerProperty(key);
			return requiredURL;
		} catch (Exception are) {
			logger.debug("Error occured while fetching environment configuration parameter. Attempted Key:" + key, are);
		}

		return "true";
	}

	private void pushAlert(FabricRequestManager requestManager, FabricResponseManager responseManager, Boolean flag) {
		try {

			PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
			PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();

			JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
			JsonObject requestPayload = requestPayloadHandler.getPayloadAsJson().getAsJsonObject();
			JsonObject customParams = new JsonObject();

			String opstatus = com.kony.utilities.HelperMethods.getStringFromJsonObject(responsePayload,
					DBPConstants.FABRIC_OPSTATUS_KEY, true);
			String enableEvents = getConfigurableParameters("ENABLE_EVENTS", requestManager);

			logger.debug("ENABLE_EVENTS=" + enableEvents + flag.toString());
			try {
				com.kony.utilities.HelperMethods.getStringFromJsonObject(responsePayload,
						DBPConstants.DBP_ERROR_CODE_KEY, true);
			} catch (Exception e) {
				if (enableEvents != null && enableEvents.equals("true") && opstatus.equals("0")) {
					String customerid = null;
					String username = null;
					if (responsePayload != null) {
						try {
							customerid = responsePayload.get(DBPUtilitiesConstants.CUSTOMTER_ID).getAsString();
						} catch (Exception e2) {
							logger.error("customerid can't be fetched", e2);
						}
					}

					try {
						username = requestPayload.get("UserName").getAsString();
						customParams.addProperty("user", username);
					} catch (Exception e3) {
						logger.error("username can't be fetched", e3);
					}
					String eventType = "CREDENTIAL_CHANGE";
					String eventSubType = "PASSWORD_RESET";
					String producer = "DbxUser/ResetDbxUserPasswordPostProcessor";
					String StatusId = "SID_EVENT_FAILURE";
					if (flag == true) {
						StatusId = "SID_EVENT_SUCCESS";
					}

					String password = null;
					if (requestPayload.has("Password")) {
						password = requestPayload.get("Password").getAsString();
						requestPayload.remove("Password");
						requestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
					}
					try {
						EventsDispatcher.dispatch(requestManager, responseManager, eventType, eventSubType, producer,
								StatusId, null, customerid, customParams);

					} catch (Exception e2) {
						logger.error("Error while calling EventDispatcher", e2);
					}

					if (password != null) {
						requestPayload.addProperty("Password", password);
						requestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
					}
				}
			}

		} catch (Exception ex) {
			logger.debug("exception occured in ObjectService PostProcessor while resetting DBX User Password =", ex);
		}
	}

	private void removeServiceKey(FabricRequestManager requestManager, JsonObject response, JsonObject request) {

		String serviceKey = new String();

		if (request.has(MFAConstants.SERVICE_KEY) && !request.get(MFAConstants.SERVICE_KEY).isJsonNull()) {
			serviceKey = request.get(MFAConstants.SERVICE_KEY).getAsString();
		}

		Map<String, String> hashMap = new HashMap<>();
		hashMap.put("serviceKey", serviceKey);

		HelperMethods.callApi(requestManager, hashMap, HelperMethods.getHeaders(requestManager),
				URLConstants.MFA_SERVICE_DELETE);

		return;

	}
}
