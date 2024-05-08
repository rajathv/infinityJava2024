package com.kony.dbputilities.customersecurityservices.postprocessor;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.postprocessors.ObjectServicesConstants;
import com.kony.utilities.HelperMethods;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class CreateBusinessUserPostProcessor implements ObjectServicePostProcessor, ObjectServicesConstants {

    LoggerUtil logger = new LoggerUtil(CreateBusinessUserPostProcessor.class);

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        try {
            PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
            PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();
            JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
            JsonObject requestPayload = requestPayloadHandler.getPayloadAsJson().getAsJsonObject();

            ServicesManager servicesManager = requestManager.getServicesManager();
            OperationData op = servicesManager.getOperationData();
            String producer = op.getServiceId() + "/operations/" + op.getObjectId() + "/" + op.getOperationId();

            String opstatus = com.kony.utilities.HelperMethods.getStringFromJsonObject(responsePayload,
                    DBPConstants.FABRIC_OPSTATUS_KEY, true);

            Map<String, String> userDetails = com.kony.dbputilities.util.HelperMethods
                    .getCustomerFromIdentityService(requestManager);
            com.kony.dbputilities.util.HelperMethods.removeNullValues(userDetails);

            boolean isSuperAdmin = false;
            if (!userDetails.isEmpty())
                isSuperAdmin = Boolean.parseBoolean(userDetails.get("isC360Admin"));

            String producerId = null;
            String producerName = null;
            String eventType = PARAM_USER;
            String eventSubType = null;
            String statusId = PARAM_SID_EVENT_SUCCESS;
            String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters("ENABLE_EVENTS", requestManager);
            if (!opstatus.equals("0") || ObjectServiceHelperMethods.hasKey(responsePayload, PARAM_DBP_ERR_CODE)) {
                statusId = PARAM_SID_EVENT_FAILURE;
            }
            String sessionid = HelperMethods.getSessionId(requestManager);

            String consumerName = null;
            String consumerId = null;
            if (requestPayload.has("UserName") && !requestPayload.get("UserName").isJsonNull()) {
                consumerName = requestPayload.get("UserName").getAsString();
            }
            if (responsePayload.has("id") && !responsePayload.get("id").isJsonNull()) {
                consumerId = responsePayload.get("id").getAsString();
            }

            // if user creation is failed , no auditing should be made
            if (StringUtils.isBlank(consumerId))
                return;

            if (!userDetails.isEmpty()) {
                if (!isSuperAdmin) {
                    producerName = userDetails.get("UserName");
                    producerId = userDetails.get("customer_id");
                } else {
                    producerName = "ADMIN";
                }
            }

            logger.debug("ENABLE_EVENTS=" + enableEvents);

            if (enableEvents != null && enableEvents.equals("true")) {
                if (!isSuperAdmin && servicesManager.getIdentityHandler() != null) {
                    eventSubType = getEventSubType(true);
                    JsonObject producerCustomerParams = new JsonObject();
                    producerCustomerParams.addProperty("createUserId", consumerId);
                    producerCustomerParams.addProperty("createdUserName", consumerName);
                    producerCustomerParams.addProperty("statusId", DBPUtilitiesConstants.CUSTOMER_STATUS_NEW);
                    producerCustomerParams.addProperty("sessionId", sessionid);
                    EventsDispatcher.dispatch(requestManager, responseManager, eventType, eventSubType, producer,
                            statusId, null, producerId, producerCustomerParams);
                }
                eventSubType = getEventSubType(false);
                JsonObject consumerCustomerParams = new JsonObject();
                if (StringUtils.isNotBlank(producerId))
                    consumerCustomerParams.addProperty("createdbyId", producerId);
                consumerCustomerParams.addProperty("createdByName", producerName);
                consumerCustomerParams.addProperty("statusId", DBPUtilitiesConstants.CUSTOMER_STATUS_NEW);
                consumerCustomerParams.addProperty("sessionId", sessionid);

                EventsDispatcher.dispatch(requestManager, responseManager, eventType, eventSubType, producer, statusId,
                        null, consumerId, consumerCustomerParams);
            }
        } catch (Exception e) {
            logger.error("Exception occured while create business user auditing :" + e.getStackTrace());
        }
    }

    private String getEventSubType(boolean isProducerEventGenerator) {

        if (isProducerEventGenerator)
            return PARAM_BO_USER_CREATE;
        else
            return PARAM_USER_CREATE;
    }
}
