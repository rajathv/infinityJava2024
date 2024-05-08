package com.kony.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class RegisterCustomerDeviceObjectServicePostProcessor
        implements ObjectServicePostProcessor, ObjectServicesConstants {

    private static final Logger logger = LogManager.getLogger(RegisterCustomerDeviceObjectServicePostProcessor.class);

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        try {

            PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();

            JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();

            JsonObject customParams = new JsonObject();

            String opstatus = HelperMethods.getStringFromJsonObject(responsePayload, PARAM_OP_STATUS, true);
            String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS,
                    requestManager);

            logger.debug(PARAM_ENABLE_EVENTS + enableEvents);

            if (enableEvents != null && enableEvents.equalsIgnoreCase(PARAM_TRUE) && opstatus.equals("0")) {

                String eventType = PARAM_REGISTER_DEVICE;
                String eventSubType = PARAM_REGISTER_CUSTOMER_DEVICE;
                String producer = "DeviceRegistration/RegisterCustomerDevice";
                String statusId = PARAM_SID_EVENT_SUCCESS;

                try {
                    ObjectServiceHelperMethods.execute(new ObjectServiceHelperMethods(requestManager, responseManager,
                            eventType, eventSubType, producer, statusId, null, null, customParams));
                } catch (Exception e2) {
                    logger.error("Exception Occured while invoking objectServiceHelperMethods", e2);
                }
            }

        } catch (Exception ex) {
            logger.error("exception occured in ResetDbxUserPasswordObjectServicePostProcessor", ex);
        }

    }

}
