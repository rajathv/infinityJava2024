package com.kony.postprocessors;

import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.QueryParamsHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class DeleteBillPayeeObjectServicePostProcessor
        implements ObjectServicePostProcessor, ObjectServicesConstants, ObjectProcessorTask {

    private static final Logger logger = LogManager.getLogger(DeleteBillPayeeObjectServicePostProcessor.class);

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        try {

            PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
            PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();
            JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
            JsonObject customParams = new JsonObject();

            String opstatus = HelperMethods.getStringFromJsonObject(responsePayload, PARAM_OP_STATUS, true);
            String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS,
                    requestManager);
            String customerid = HelperMethods.getCustomerIdFromSession(requestManager);
            String eventType = PARAM_BILL_PAYEE;
            String eventSubType = PARAM_BILL_PAYEE_DELETED;
            String producer = "";
            String statusId = PARAM_SID_EVENT_FAILURE;
            String isWiredRecepient = "";
            JsonObject queryParamjsonObject = new JsonObject();
            QueryParamsHandler queryParamsHandler = requestManager.getQueryParamsHandler();
            Set<String> parameterNames = queryParamsHandler.getParameterNames();
            for (String queryParamName : parameterNames) {
                queryParamjsonObject.addProperty(queryParamName, queryParamsHandler.getParameter(queryParamName));
            }
            requestPayloadHandler.updatePayloadAsJson(queryParamjsonObject);
            if (opstatus.equals("0") && !ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE)) {
                statusId = PARAM_SID_EVENT_SUCCESS;
            }

            logger.debug("ENABLE_EVENTS=" + enableEvents);

            if (enableEvents != null && enableEvents.equalsIgnoreCase(PARAM_TRUE)) {

                if (ObjectServiceHelperMethods.hasKey(responsePayload, "isWiredRecepient")) {
                    isWiredRecepient = HelperMethods.getStringFromJsonObject(responsePayload, "isWiredRecepient");
                    if (isWiredRecepient.equalsIgnoreCase("true")) {
                        eventSubType = PARAM_WIRE_RECIPIENT_DELETED;
                    }
                }
                try {

                    ObjectServiceHelperMethods.execute(new ObjectServiceHelperMethods(requestManager, responseManager,
                            eventType, eventSubType, producer, statusId, null, customerid, customParams));
                } catch (Exception e2) {
                    logger.error("Exception Occured while invoking objectServiceHelperMethods");
                }

            }

        } catch (Exception ex) {
            logger.debug("exception occured in ObjectService PostProcessor while Adding Bill Payee Recipient=", ex);
        }

    }

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
        try {
            execute(fabricRequestManager, fabricResponseManager);
        } catch (Exception e) {
            logger.error("exception occured in execute method of delete bill payee", e);

        }
        return true;
    }

}
