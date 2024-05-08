package com.temenos.dbx.product.businessdelegate.impl;

import java.util.Map;

import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;

import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.product.businessdelegate.api.KMSBusinessDelegate;

public class KMSBusinessDelegateImpl implements KMSBusinessDelegate {

    LoggerUtil logger = new LoggerUtil(KMSBusinessDelegateImpl.class);

    @Override
    public JsonObject sendKMSEmail(Map<String, Object> inputParams, Map<String, Object> headersMap)
            throws ApplicationException {

        JsonObject response = new JsonObject();
        headersMap.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        inputParams.put("Subscribe", "true");
        try {
            response = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headersMap,
                    URLConstants.DBX_SEND_EMAIL_ORCH);
        } catch (Exception e) {
            logger.error("Exception occured while calling the KMS Orchestration service" + e.getMessage());
        }
        return response;
    }

    @Override
    public JsonObject sendKMSSMS(Map<String, Object> inputParams, Map<String, Object> headersMap)
            throws ApplicationException {
        JsonObject response = new JsonObject();
        headersMap.put(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        inputParams.put("Subscribe", "true");
        if (inputParams.containsKey("Phone")) {
            String phone = inputParams.get("Phone").toString();
            if (phone.contains("+"))
                phone = phone.replace("+", "");
            if (phone.contains("-"))
                phone = phone.replace("-", "");
            inputParams.put("SendToMobiles", phone);
        }
        if (inputParams.containsKey("otp")) {
            inputParams.put("Content", "otp:" + inputParams.get("otp"));
        }
        try {
            response = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headersMap,
                    URLConstants.DBX_SEND_SMS_ORCH);

        } catch (Exception e) {
            logger.error("Exception occured while sending OTP" + e.getMessage());
        }
        return response;
    }

}
