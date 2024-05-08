/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.sessionmanagement;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kony.memorymgmt.CorporateManager;
import com.kony.memorymgmt.SessionMap;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveLimitInstructionsIntoSession implements ObjectProcessorTask {

    private static final Logger LOG = LogManager.getLogger(SaveLimitInstructionsIntoSession.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) throws Exception {
        try {
            SessionMap limitsMap = new SessionMap();
            JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
            if (null != response && !response.isJsonNull()) {
                if (response.has("Limits")) {
                    JsonObject limitsData = new JsonObject();
                    JsonArray limitsArray = response.getAsJsonArray("Limits");
                    limitsArray.forEach(jsonElement -> {
                        limitsData.add(jsonElement.getAsJsonObject().get("Limit iD").getAsString(), jsonElement);
                    });
                    limitsMap.setData(limitsData.toString());
                }
                CorporateManager corporateManager = new CorporateManager(fabricRequestManager, fabricResponseManager);
                corporateManager.saveLimitsIntoSession(limitsMap);
            }
        } catch (Exception e) {
            LOG.error("Exception while caching payees in session", e);
        }
        return true;
    }
}