package com.temenos.infinity.api.accountsweeps.task;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.temenos.infinity.api.accountsweeps.utils.AccountSweepCommonUtils.fetchCustomerIdFromSession;
import static com.temenos.infinity.api.accountsweeps.utils.AccountSweepsAPIServices.DB_UPDATEACCOUNTSWEEPINFO;

/**
 * @author naveen.yerra
 */
public class AccountSweepUpdateTask implements ObjectProcessorTask {

    private static final Logger LOG = LogManager.getLogger(AccountSweepUpdateTask.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
        try {
            JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
            if(response.has("serviceRequestId") && response.has("isSweepCreated")
                    && response.has("primaryAccountNumber") && response.has("secondaryAccountNumber") ) {
                return updateAccountSweepInfo(fabricRequestManager, response);
            }
        } catch (Exception e) {
            LOG.error("Exception while updating account sweep information into customer account", e);
            return false;
        }
        return true;
    }

    private boolean updateAccountSweepInfo(FabricRequestManager requestManager, JsonObject response) {
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("_customerId", fetchCustomerIdFromSession(requestManager));
        if(!response.has("isEdit")) {
            inputMap.put("_primaryAccount", response.get("primaryAccountNumber").getAsString());
            inputMap.put("_previousAccount", "");
        } else {
            inputMap.put("_primaryAccount", "");
            inputMap.put("_previousAccount", response.has("previousSecondaryAccountNumber") ? response.get("previousSecondaryAccountNumber").getAsString() : "");
        }
        inputMap.put("_secondaryAccount", response.get("secondaryAccountNumber").getAsString());
        inputMap.put("_sweepFlag", response.get("isSweepCreated").getAsString());
        try {
            DBPServiceExecutorBuilder.builder()
                    .withServiceId(DB_UPDATEACCOUNTSWEEPINFO.getServiceName())
                    .withOperationId(DB_UPDATEACCOUNTSWEEPINFO.getOperationName())
                    .withRequestParameters(inputMap)
                    .withFabricRequestManager(requestManager)
                    .build().getResponse();
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}