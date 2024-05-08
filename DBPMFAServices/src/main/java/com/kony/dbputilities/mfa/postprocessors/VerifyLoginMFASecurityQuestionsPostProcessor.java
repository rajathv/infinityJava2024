package com.kony.dbputilities.mfa.postprocessors;

import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.mfa.MFAConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.utils.TokenUtils;
import com.kony.utils.URLConstants;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class VerifyLoginMFASecurityQuestionsPostProcessor implements ObjectServicePostProcessor {

    private static final Logger LOG = LogManager.getLogger(VerifyLoginMFASecurityQuestionsPostProcessor.class);

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        JsonObject jsonObject = responseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        String enableEvents = EnvironmentConfigurationsHandler.getValue("ENABLE_EVENTS", requestManager);
        JsonObject response = new JsonObject();
        JsonObject mfaAttributes = new JsonObject();
        String statusId = MFAConstants.SID_EVENT_SUCCESS;
        for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (entry.getKey().equals(DBPUtilitiesConstants.SUCCESS)) {
                response.addProperty(DBPUtilitiesConstants.SUCCESS,
                        jsonObject.get(DBPUtilitiesConstants.SUCCESS).getAsString());
            } else if (entry.getKey().equals(ErrorCodeEnum.ERROR_CODE_KEY)) {
                response.addProperty(ErrorCodeEnum.ERROR_CODE_KEY,
                        jsonObject.get(ErrorCodeEnum.ERROR_CODE_KEY).getAsString());
                statusId = MFAConstants.SID_EVENT_FAILURE;
            } else if (entry.getKey().equals(ErrorCodeEnum.ERROR_MESSAGE_KEY)) {
                response.addProperty(ErrorCodeEnum.ERROR_MESSAGE_KEY,
                        jsonObject.get(ErrorCodeEnum.ERROR_MESSAGE_KEY).getAsString());
            } else if (entry.getKey().equals(DBPConstants.FABRIC_HTTP_STATUS_CODE_KEY)) {
                response.addProperty(DBPConstants.FABRIC_HTTP_STATUS_CODE_KEY,
                        jsonObject.get(DBPConstants.FABRIC_HTTP_STATUS_CODE_KEY).getAsString());
            } else if (entry.getKey().equals(DBPConstants.FABRIC_OPSTATUS_KEY)) {
                response.addProperty(DBPConstants.FABRIC_OPSTATUS_KEY,
                        jsonObject.get(DBPConstants.FABRIC_OPSTATUS_KEY).getAsString());
            } else {
                mfaAttributes.addProperty(entry.getKey(), entry.getValue().getAsString());
            }
        }

        if (!mfaAttributes.entrySet().isEmpty()) {
            response.add(MFAConstants.MFA_ATTRIBUTES, mfaAttributes);
        }

        responseManager.getPayloadHandler().updatePayloadAsJson(response);

        String eventType = MFAConstants.LOGIN;
        String eventSubType = MFAConstants.LOGIN_ATTEMPT_MFA;
        String producer = "RBObjects/DbxUser/verifyLoginMFASecurityQuestions";
        JsonObject customParams = new JsonObject();
        customParams.addProperty(MFAConstants.MFA_STATE, MFAConstants.VERIFY);
        customParams.addProperty(MFAConstants.MFA_TYPE, MFAConstants.SECURITY_QUESTIONS);
        String customerid = null;
        String sessionid = null;

        try {
            if (jsonObject != null && jsonObject.get("lockUser") != null
                    && jsonObject.get("lockUser").getAsString().equals("true")) {
                eventSubType = MFAConstants.ACCOUNT_LOCKED;
                statusId = MFAConstants.SID_EVENT_SUCCESS;
            }
        } catch (Exception e) {
            LOG.error("Error while setting eventsubtype as ACCOUNT_LOCKED", e);
        }

        try {
            String authkey = requestManager.getHeadersHandler().getHeader("X-Kony-Authorization");
            if (authkey != null && !authkey.equals("")) {
            	TokenUtils tokenobj = new TokenUtils(authkey);

				customerid = tokenobj.getValue(URLConstants.PROVIDER_USER_ID);
				sessionid = tokenobj.getValue(URLConstants.SESSIONID);
            }
        } catch (Exception e) {
            LOG.error("Error while fetching cutomerid", e);
        }

        customParams.addProperty("sessionId", sessionid);
        if (enableEvents != null && enableEvents.equalsIgnoreCase("true")) {
            try {
                EventsDispatcher.dispatch(requestManager, responseManager, eventType, eventSubType, producer, statusId,
                        null, customerid, customParams);
            } catch (Exception e) {
                LOG.error("Error while pushing to Audit Engine", e);
            }
        }

    }
}
