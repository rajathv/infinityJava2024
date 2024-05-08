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

public class saveBulkPaymentTemplateIntoSession implements ObjectProcessorTask {

    private static final Logger LOG = LogManager.getLogger(saveBulkPaymentTemplateIntoSession.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) throws Exception {
        try {
            SessionMap bulkPaymentTemplatesMap = new SessionMap();
            JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
            if (null != response && !response.isJsonNull()) {
                if (response.has("bulkPaymentTemplates")) {
                    JsonArray bulkPaymentTemplates = response.getAsJsonArray("bulkPaymentTemplates");
                    bulkPaymentTemplatesMap = getBulkPaymentTemplatesMap(bulkPaymentTemplates);
                }
                CorporateManager corporateManager = new CorporateManager(fabricRequestManager, fabricResponseManager);
                corporateManager.saveBulkPaymentTemplateIntoSession(bulkPaymentTemplatesMap);
            }
        } catch (Exception e) {
            LOG.error("Exception while caching payees in session", e);
        }
        return true;
    }

    private SessionMap getBulkPaymentTemplatesMap(JsonArray bulkPaymentTemplates) {
        SessionMap bulkPaymentTemplatesMap = new SessionMap();
        if (null != bulkPaymentTemplates && !bulkPaymentTemplates.isJsonNull() && bulkPaymentTemplates.size() > 0) {
            for (JsonElement jsonElement : bulkPaymentTemplates) {
                JsonObject template = jsonElement.getAsJsonObject();
                bulkPaymentTemplatesMap.addKey(template.get("templateId").getAsString());
            }
        }
        return bulkPaymentTemplatesMap;
    }
}
