package com.temenos.infinity.product.bulkpaymentservices.sessionmanagement;

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

public class SavePaymentHistoryIntoSession implements ObjectProcessorTask {

    private static final Logger LOG = LogManager.getLogger(SavePaymentHistoryIntoSession.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) throws Exception {
        try {
            SessionMap paymentHistoryMap = new SessionMap();
            JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
            if (null != response && !response.isJsonNull()) {
                if (response.has("history")) {
                    JsonArray paymentHistory = response.getAsJsonArray("history");
                    paymentHistoryMap = getPaymentHistoryMap(paymentHistory);
                }
                if (response.has("onGoingPayments")) {
                    JsonArray paymentHistory = response.getAsJsonArray("onGoingPayments");
                    paymentHistoryMap = getPaymentHistoryMap(paymentHistory);
                }
                CorporateManager corporateManager = new CorporateManager(fabricRequestManager, fabricResponseManager);
                corporateManager.savePaymentHistoryIntoSession(paymentHistoryMap);
            }
        } catch (Exception e) {
            LOG.error("Exception while caching payment history in session", e);
        }
        return true;
    }

    private SessionMap getPaymentHistoryMap(JsonArray paymentHistory) {
        SessionMap paymentHistoryMap = new SessionMap();
        if (null != paymentHistory && !paymentHistory.isJsonNull() && paymentHistory.size() > 0) {
            for (JsonElement jsonElement : paymentHistory) {
                JsonObject template = jsonElement.getAsJsonObject();
                paymentHistoryMap.addKey(template.get("recordId").getAsString());
            }
        }
        return paymentHistoryMap;
    }
}
