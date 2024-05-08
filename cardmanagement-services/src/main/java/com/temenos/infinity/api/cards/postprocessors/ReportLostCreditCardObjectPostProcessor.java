package com.temenos.infinity.api.cards.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.postprocessors.ObjectServicesConstants;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class ReportLostCreditCardObjectPostProcessor implements ObjectServicePostProcessor, ObjectServicesConstants {

    private static final Logger logger = LogManager.getLogger(ReportLostCreditCardObjectPostProcessor.class);

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        try {
            PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
            PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();
            JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
            requestPayloadHandler.getPayloadAsJson().getAsJsonObject();
            JsonObject customParams = new JsonObject();

            String opstatus = HelperMethods.getStringFromJsonObject(responsePayload, PARAM_OP_STATUS, true);
            String customerid = HelperMethods.getCustomerIdFromSession(requestManager);
            String eventType = CREDIT_CARD;
            String eventSubType = REPORT_LOST_CARD;
            String producer = "RBObjects/objects/Cards/repostLost";
            String statusId = PARAM_SID_EVENT_SUCCESS;
            String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS,
                    requestManager);

            logger.debug("ENABLE_EVENTS=" + enableEvents);
            if (!opstatus.equals("0") || ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE)) {
                statusId = PARAM_SID_EVENT_FAILURE;
            }

            if (enableEvents != null && enableEvents.equalsIgnoreCase(PARAM_TRUE)) {

                try {
                    ObjectServiceHelperMethods.execute(new ObjectServiceHelperMethods(requestManager, responseManager,
                            eventType, eventSubType, producer, statusId, null, customerid, customParams));
                } catch (Exception e2) {
                    logger.error("Exception Occured while invoking objectServiceHelperMethods");
                }
            }

        } catch (Exception ex) {
            logger.debug("exception occured in ObjectService PostProcessor while Fetching number of cards", ex);
        }

    }

}
