package com.kony.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class GetCreditCardsObjectPostProcessor
        implements ObjectServicePostProcessor, ObjectServicesConstants, ObjectProcessorTask {

    private static final Logger logger = LogManager.getLogger(GetCreditCardsObjectPostProcessor.class);

    private static int getNoOfCardsFetched(JsonObject responsePayload) {
        int noOfcards = 0;
        logger.error("inside get getNoOfCardsFetched response=" + responsePayload.toString());

        if (responsePayload.has("records")) {
            JsonElement records = responsePayload.get("records");
            if (records.isJsonArray()) {
                noOfcards = records.getAsJsonArray().size();
            }
        }

        logger.error("getNoOfCardsFetched=" + noOfcards);
        return noOfcards;
    }

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        try {

            PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
            PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();
            JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
            if(responsePayload != null && responsePayload.toString().equals("{}")) {
            	throw new Exception("Skipping the execution for empty payload");
            }  
            JsonObject customParams = new JsonObject();

            String opstatus = HelperMethods.getStringFromJsonObject(responsePayload, PARAM_OP_STATUS, true);
            String customerid = HelperMethods.getCustomerIdFromSession(requestManager);
            String eventType = CREDIT_CARD;
            String eventSubType = FETCH_CREDIT_CARDS;
            String producer = "TransactionObjects/objects/getCreditCardAccounts";
            String statusId = PARAM_SID_EVENT_SUCCESS;
            String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS,
                    requestManager);

            try {
                customParams.addProperty("NumberOfCards", getNoOfCardsFetched(responsePayload));
            } catch (Exception e) {
                logger.error("Exception occured while fetching the card count", e);
            }
            logger.debug("ENABLE_EVENTS=" + enableEvents);
            if (!opstatus.equals("0") || ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE)) {
                statusId = PARAM_SID_EVENT_FAILURE;
            }

            if (enableEvents != null && enableEvents.equalsIgnoreCase(PARAM_TRUE)) {

                EventsDispatcher.dispatch(requestManager, responseManager, eventType, eventSubType, producer, statusId,
                        null, customerid, customParams);
            }

        } catch (Exception ex) {
            logger.debug("exception occured in ObjectService PostProcessor while Fetching number of cards", ex);
        }

    }

    @Override
    public boolean process(FabricRequestManager requestManager, FabricResponseManager responseManager)
            throws Exception {
        try {
            execute(requestManager, responseManager);
        } catch (Exception e) {
            logger.error("exception occured n get credit cards execute method", e);
        }
        return true;
    }

}
