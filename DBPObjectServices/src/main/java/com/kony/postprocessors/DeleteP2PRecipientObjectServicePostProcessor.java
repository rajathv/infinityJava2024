package com.kony.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class DeleteP2PRecipientObjectServicePostProcessor
        implements ObjectServicePostProcessor, ObjectServicesConstants, ObjectProcessorTask {

    private static final Logger logger = LogManager.getLogger(DeleteP2PRecipientObjectServicePostProcessor.class);

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        try {

            PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
            JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();

            JsonObject customParams = new JsonObject();

            String opstatus = "";
            String customerid = HelperMethods.getCustomerIdFromSession(requestManager);
            String eventType = PARAM_P2P_RECIPIENT;
            String eventSubType = PARAM_P2P_RECIPIENT_DELETED;
            String producer = "RBObjects/objects/PayPerson/deletePayPerson";
            String statusId = PARAM_SID_EVENT_FAILURE;
            String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters("ENABLE_EVENTS", requestManager);

            if (ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_OP_STATUS)) {
                opstatus = HelperMethods.getStringFromJsonObject(responsePayload, PARAM_OP_STATUS, true);
            }

            if (opstatus.equals("0") && !ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE)) {
                statusId = PARAM_SID_EVENT_SUCCESS;
            }

            logger.debug("ENABLE_EVENTS=" + enableEvents);

            if (enableEvents != null && enableEvents.equalsIgnoreCase(PARAM_TRUE)) {
                try {

                    ObjectServiceHelperMethods.execute(new ObjectServiceHelperMethods(requestManager, responseManager,
                            eventType, eventSubType, producer, statusId, null, customerid, customParams));
                } catch (Exception e2) {
                    logger.error("Exception Occured while invoking objectServiceHelperMethods");
                }
            }

        }

        catch (Exception ex) {
            logger.debug("exception occured in ObjectService PostProcessor while Adding Payee Recipient=", ex);
        }

    }

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
        try {
            execute(fabricRequestManager, fabricResponseManager);
        } catch (Exception e) {
            logger.error("exception occuted in delete p2p recipient execute method", e);
        }
        return true;
    }

}
