package com.kony.dbputilities.customersecurityservices.postprocessor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class ActivationCodeServiceKeyDeletionPostProcessor implements ObjectServicePostProcessor {
    private static final Logger LOG = LogManager.getLogger(ActivationCodeServiceKeyDeletionPostProcessor.class);

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager)
            throws Exception {

        JsonObject jsonObject = responseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        if (jsonObject.has(DBPConstants.DBP_ERROR_CODE_KEY) || jsonObject.has(DBPConstants.DBP_ERROR_MESSAGE_KEY)) {
            responseManager.getPayloadHandler().updatePayloadAsJson(jsonObject);
            return;
        }

        String status = jsonObject.get("status").getAsString();
        JsonObject jsoninput = requestManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();

        if (StringUtils.isNotBlank(status)) {
            Map<String, String> inputParams = new HashMap<>();
            inputParams.put("serviceKey", JSONUtil.getString(jsoninput, "serviceKey"));
            HelperMethods.callApi(requestManager, inputParams, HelperMethods.getHeaders(requestManager),
                    URLConstants.MFA_SERVICE_DELETE);
        }
    }
}
