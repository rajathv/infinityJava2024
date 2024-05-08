package com.kony.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class GetDownloadTransactionObjectServicePostProcessor
        implements ObjectServicePostProcessor, ObjectServicesConstants {

    private static final Logger logger = LogManager.getLogger(GetDownloadTransactionObjectServicePostProcessor.class);

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        try {

            JsonObject responsePayload = new JsonObject();
            JsonObject customParams = new JsonObject();
            String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS,
                    requestManager);
            String statusId = PARAM_SID_EVENT_SUCCESS;

            if (ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_OP_STATUS)) {
                statusId = PARAM_SID_EVENT_FAILURE;
            }

            if (enableEvents != null && enableEvents.equalsIgnoreCase(PARAM_TRUE)) {

                String eventType = PARAM_ACCOUNT_ACTION;
                String eventSubType = PARAM_INTACC_TRANSACTIONS_DOWNLOAD;
                String producer = "DownloadTransaction/GET";
                try {

                    ObjectServiceHelperMethods.execute(new ObjectServiceHelperMethods(requestManager, responseManager,
                            eventType, eventSubType, producer, statusId, null, "", customParams));
                } catch (Exception e2) {
                    logger.error("Exception Occured while invoking objectServiceHelperMethods", e2);
                }
            }

        } catch (Exception ex) {
            logger.error("exception occured in GetDownloadTransactionObjectServicePostProcessor ", ex);
        }

    }

}
