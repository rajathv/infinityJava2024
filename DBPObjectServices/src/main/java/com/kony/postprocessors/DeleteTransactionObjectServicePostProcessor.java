package com.kony.postprocessors;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class DeleteTransactionObjectServicePostProcessor
        implements ObjectServicePostProcessor, ObjectServicesConstants {

    private static final Logger logger = LogManager.getLogger(DeleteTransactionObjectServicePostProcessor.class);

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        try {

            PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
            PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();
            JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
            JsonObject requestPayload = requestPayloadHandler.getPayloadAsJson().getAsJsonObject();
            JsonObject customParams = new JsonObject();

            String opstatus = "";
            String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS,
                    requestManager);
            String statusId = PARAM_SID_EVENT_FAILURE;
            String customerid = HelperMethods.getCustomerIdFromSession(requestManager);
            String eventType = PARAM_MAKE_TRANSFER;
            String eventSubType = "";
            String producer = "";
            String transactionType = "";

            if (ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_OP_STATUS)) {
                opstatus = HelperMethods.getStringFromJsonObject(responsePayload, PARAM_OP_STATUS, true);
            }

            if (opstatus.equals("0") && !ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE)) {
                statusId = PARAM_SID_EVENT_SUCCESS;
            }

            logger.debug("ENABLE_EVENTS=" + enableEvents);

            if (enableEvents != null && enableEvents.equalsIgnoreCase(PARAM_TRUE)) {

                if (ObjectServiceHelperMethods.hasKey(requestPayload, PARAM_TRANSACTION_TYPE)) {
                    transactionType = HelperMethods
                            .getStringFromJsonObject(requestPayload, PARAM_TRANSACTION_TYPE, true).toLowerCase();
                }

                switch (transactionType) {
                    case PARAM_INTERNAL_TRANSFER:
                        eventSubType = PARAM_DELETE_INTERNAL_TRANSFER;
                        break;

                    case PARAM_EXTERNAL_TRANSFER:
                        eventSubType = PARAM_DELETE_EXTERNAL_TRANSFER;
                        break;

                    case PARAM_P2P_TRANSFER:
                        eventSubType = PARAM_DELETE_P2P_TRANSFER;
                        break;

                    case PARAM_WIRE_TRANSFER:
                        eventSubType = PARAM_DELETE_WIRE_TRANSFER;
                        break;

                    case PARAM_BILLPAY_TRANSFER:
                        eventSubType = PARAM_DELETE_BILL_PAYMENT;
                        break;

                    case PARAM_DEPOSIT_TRANSFER:
                        eventSubType = PARAM_DELETE_RDC;
                        break;

                    default:
                        break;
                }

                customParams.addProperty("transactionDate",
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
            }

            if (!eventSubType.equals("")) {
                try {
                	
                	AdminUtil.addAdminUserNameRoleIfAvailable(customParams, requestManager);
                	
                    ObjectServiceHelperMethods.execute(new ObjectServiceHelperMethods(requestManager, responseManager,
                            eventType, eventSubType, producer, statusId, null, customerid, customParams));
                } catch (Exception e2) {
                    logger.error("Exception Occured while invoking objectServiceHelperMethods");
                }
            }
        } catch (Exception ex) {
            logger.error("exception occured in ObjectService PostProcessor while deleting Transaction=", ex);
        }

    }

}
