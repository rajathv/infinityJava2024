/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.sessionmanagement;

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
 * @author naveen.yerra
 */
public class SaveDocumentInfoIntoSession implements ObjectProcessorTask {

    private static final Logger LOG = LogManager.getLogger(SaveDocumentInfoIntoSession.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) throws Exception {
        try {
            CorporateManager corporateManager = new CorporateManager(fabricRequestManager, fabricResponseManager);
            SessionMap documentsMap = corporateManager.getDocumentsFromSession();
            JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
            if (null != response && !response.isJsonNull()) {
                if (response.has("LCDocuments")) {
                    JsonArray documents = response.getAsJsonArray("LCDocuments");
                    documentsMap = getDocumentsMap(documents, documentsMap);
                }
                corporateManager.saveDocumentsIntoSession(documentsMap);
            }
        } catch (Exception e) {
            LOG.error("Exception while caching document info into session", e);
        }
        return true;
    }

    private SessionMap getDocumentsMap(JsonArray documents, SessionMap documentsMap) {
        if (null != documents && !documents.isJsonNull() && documents.size() > 0) {
            for (JsonElement jsonElement : documents) {
                JsonObject document = jsonElement.getAsJsonObject();
                documentsMap.addKey(document.get("documentReference").getAsString());
                documentsMap.addAttributeForKey(document.get("documentReference").getAsString(), "documentName", document.get("successfulUploads").getAsString());
            }
        }
        return documentsMap;
    }
}
