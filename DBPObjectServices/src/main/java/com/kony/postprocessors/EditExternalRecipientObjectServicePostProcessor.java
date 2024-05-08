package com.kony.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class EditExternalRecipientObjectServicePostProcessor
        implements ObjectServicePostProcessor, ObjectServicesConstants {

    private static final Logger logger = LogManager.getLogger(EditExternalRecipientObjectServicePostProcessor.class);

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        try {

            PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
            JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
            
            PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();
            JsonObject requestPayload = requestPayloadHandler.getPayloadAsJson().getAsJsonObject();

            JsonObject customParams = new JsonObject(); 

            String opstatus = "";
            String customerid = HelperMethods.getCustomerIdFromSession(requestManager);
            String eventType = PARAM_TRANSFER_RECIPIENT;
            String eventSubType = PARAM_TRANSFER_RECIPIENT_UPDATED;
            String producer = "RBObjects/objects/ExternalAccounts/editExternalAccount";
            String statusId = PARAM_SID_EVENT_FAILURE;
            String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS,
                    requestManager);
            
            //Set Account Number
            if (!ObjectServiceHelperMethods.hasKey(requestPayload, PARAM_ACCOUNT_NUMBER)) {
                if (ObjectServiceHelperMethods.hasKey(requestPayload, PARAM_IBAN)) {
                    String IBAN = HelperMethods.getStringFromJsonObject(requestPayload, PARAM_IBAN, true);
                    requestPayload.addProperty(PARAM_ACCOUNT_NUMBER, IBAN);
                }
            }

            logger.debug("ENABLE_EVENTS=" + enableEvents);
            if (ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_OP_STATUS)) {
                opstatus = HelperMethods.getStringFromJsonObject(responsePayload, PARAM_OP_STATUS, true);
            }

            if (opstatus.equals("0") && !ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE)) {
                statusId = PARAM_SID_EVENT_SUCCESS;
            }

            if (enableEvents != null && enableEvents.equalsIgnoreCase(PARAM_TRUE)) {
                try {
                 // Push the event via event dispatcher
                    EventsDispatcher.dispatch(requestManager, requestPayload, responsePayload, eventType, eventSubType,
                            producer, statusId, null, null, customParams);

                } catch (Exception e2) {
                    logger.error("Exception Occured while invoking objectServiceHelperMethods");
                }
            }

        } catch (Exception ex) {
            logger.debug("exception occured in ObjectService PostProcessor while Adding Payee Recipient=", ex);
        }

    }

}