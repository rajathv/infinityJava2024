package com.temenos.infinity.api.srmstransactions.utils;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class GetBeneficiaryNameMockOperation implements ObjectProcessorTask {
    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) throws Exception {
        try {
            String paymentBackend = EnvironmentConfigurationsHandler.getServerProperty("PAYMENT_BACKEND");
            if("MOCK".equals(paymentBackend) || "SRMS_MOCK".equals(paymentBackend)) {
                JsonObject requestPayload = new JsonObject();
                requestPayload.addProperty("beneficiaryName","Max");
                requestPayload.addProperty("currency","EUR");
                requestPayload.addProperty("opstatus", Integer.parseInt("0"));
                requestPayload.addProperty("httpStatusCode", Integer.parseInt("200"));
                fabricResponseManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
                return false;
            }
        } catch (Exception e){
            return false;
        }
        return true;
    }

}
