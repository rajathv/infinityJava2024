/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.preprocessor.validations;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.memorymgmt.CorporateManager;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ClauseValidation implements ObjectProcessorTask {
    private static final Logger LOG = LogManager.getLogger(ClauseValidation.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
        if (!HelperMethods.isDACEnabled()) {
            LOG.debug("data access control is disabled");
            return true;
        }
        JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
        if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
            JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
            String clauseString = HelperMethods.getStringFromJsonObject(requestPayload, "ClauseString");
            JSONArray jsonArray;
            if (clauseString != null) {
                jsonArray = new JSONArray(clauseString);
            } else {
                return updateErrorResult(fabricResponseManager);
            }
            List<String> clauseList = new ArrayList<>();
            if (jsonArray.length() != 0) {
                CorporateManager corporateManager = new CorporateManager(fabricRequestManager, fabricResponseManager);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String title = jsonArray.getJSONObject(i).getString("clauseTitle");
                    if (corporateManager.validateClauseTitle(title) || clauseList.contains(title)) {
                        return updateErrorResult(fabricResponseManager);
                    }
                    clauseList.add(title);
                }
            }
        }
        return true;
    }

    private static boolean updateErrorResult(FabricResponseManager fabricResponseManager) {
        JsonObject resPayload = null;
        if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
            resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        }
        resPayload = ErrorCodeEnum.ERR_29037.setErrorCode(resPayload);
        fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
        return false;
    }

}