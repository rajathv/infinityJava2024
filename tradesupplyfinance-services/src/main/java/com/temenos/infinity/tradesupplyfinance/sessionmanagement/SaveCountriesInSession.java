/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.sessionmanagement;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.memorymgmt.CorporateManager;
import com.kony.memorymgmt.SessionMap;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author k.meiyazhagan
 */
public class SaveCountriesInSession implements ObjectProcessorTask {
    private static final Logger LOG = LogManager.getLogger(SaveMessageEligibleRecordsInSession.class);

    @Override
    public boolean process(FabricRequestManager requestManager, FabricResponseManager responseManager) {
        try {
            SessionMap countriesMap = new SessionMap();
            JsonObject countries = responseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
            if (countries.has("records")) {
                JsonArray countriesArray = countries.get("records").getAsJsonArray();
                for (JsonElement country : countriesArray) {
                    countriesMap.addKey(country.getAsJsonObject().get("Name").getAsString());
                }
                new CorporateManager(requestManager, responseManager).saveCountriesListToSession(countriesMap);
            }
        } catch (Exception e) {
            LOG.error("Error occurred while processing countries request ", e);
        }
        return true;
    }
}
