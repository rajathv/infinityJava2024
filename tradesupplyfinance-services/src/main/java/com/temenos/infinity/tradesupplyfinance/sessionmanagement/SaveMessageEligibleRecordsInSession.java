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

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.PARAM_SERVICE_REQUEST_SRMS_ID;
import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceStatus.PARAM_STATUS_APPROVED;

/**
 * @author k.meiyazhagan
 */
public class SaveMessageEligibleRecordsInSession implements ObjectProcessorTask {

    private static final Logger LOG = LogManager.getLogger(SaveMessageEligibleRecordsInSession.class);

    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) {
        try {
            JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
            if (null != response && !response.isJsonNull()) {
                String collectionName = response.keySet().toArray()[0].toString();
                if (response.has(collectionName)) {
                    SessionMap eligibleRecordsMap = getRecords(response.getAsJsonArray(collectionName));
                    new CorporateManager(fabricRequestManager, fabricResponseManager).saveMessageEligibleRecordsIntoSession(eligibleRecordsMap);
                }
            }
        } catch (Exception e) {
            LOG.error("Error occurred while saving records in session", e);
        }

        return true;
    }

    private SessionMap getRecords(JsonArray records) {
        SessionMap eligibleRecordsMap = new SessionMap();
        if (null != records && !records.isJsonNull() && records.size() > 0) {
            for (JsonElement jsonElement : records) {
                JsonObject record = jsonElement.getAsJsonObject();
                if (record.get("status").getAsString().equalsIgnoreCase(PARAM_STATUS_APPROVED)) {
                    eligibleRecordsMap.addKey(record.get(PARAM_SERVICE_REQUEST_SRMS_ID).getAsString());
                }
            }
        }
        return eligibleRecordsMap;
    }
}
