package com.temenos.infinity.api.accountsweeps.task;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author naveen.yerra
 */
public class EditAccountSweepTask implements ObjectProcessorTask {

    private static final Logger LOG = LogManager.getLogger(DeleteAccountSweepTask.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
        try {
            JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
            if(response.has("serviceRequestId")) {
                response.addProperty("isSweepCreated","true");
                response.addProperty("isEdit",true);
                fabricResponseManager.getPayloadHandler().updatePayloadAsJson(response);
            }
        } catch (Exception e) {
            LOG.error("Exception while updating account sweep information into customer account", e);
            return false;
        }
        return true;
    }

}