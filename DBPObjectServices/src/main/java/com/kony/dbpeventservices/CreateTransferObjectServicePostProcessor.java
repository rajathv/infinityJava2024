package com.kony.dbpeventservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.api.ServiceRequest;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class CreateTransferObjectServicePostProcessor implements ObjectServicePostProcessor {

    private static final Logger logger = LogManager.getLogger(CreateTransferObjectServicePostProcessor.class);

    private void callQueueMasterService(JsonArray events, Map<String, Object> inputMap, Map<String, Object> headerMap,
            FabricRequestManager requestManager, FabricResponseManager responseManager) {
        logger.error("callingQueueMasterService--");
        try {
            ServicesManager servicesManager = requestManager.getServicesManager();
            OperationData operationData = requestManager.getServicesManager().getOperationDataBuilder()
                    .withServiceId("QueueMaster").withOperationId("PushEventQueue").build();
            ServiceRequest serviceRequest = servicesManager.getRequestBuilder(operationData).withInputs(inputMap)
                    .withHeaders(headerMap).build();

            serviceRequest.invokeServiceAndGetResult();
            logger.error("result fetched");
        } catch (Exception ex) {
            logger.error("exception occured while sending alert for transfer", ex);
        }

    }

    private String getConfigurableParameters(String key, FabricRequestManager requestInstance) {
        try {
            ServicesManager serviceManager = requestInstance.getServicesManager();
            ConfigurableParametersHelper configurableParametersHelper = serviceManager
                    .getConfigurableParametersHelper();
            String requiredURL = configurableParametersHelper.getServerProperty(key);
            return requiredURL;
        } catch (Exception are) {
            logger.error("Error occured while fetching environment configuration parameter. Attempted Key:" + key, are);
        }

        return "true";
    }

    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

        try {
            // requestManager.getSessionHandler().getAttribute(arg0)

            PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();
            PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();

            JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
            JsonObject requestPayload = requestPayloadHandler.getPayloadAsJson().getAsJsonObject();

            String opstatus = getStringFromJsonObject(responsePayload, DBPConstants.FABRIC_OPSTATUS_KEY, true);
            String reportingParamsString = requestManager.getHeadersHandler().getHeader("X-Kony-ReportingParams");
            JsonObject reportingParams = new JsonParser().parse(reportingParamsString).getAsJsonObject();
            String username = getStringFromJsonObject(reportingParams, "kuid", true);

            logger.error("opstatus:" + opstatus);
            logger.error("X-Kony-ReportingParams: " + reportingParamsString);
            logger.error("username: " + username);

            String enableEvents = getConfigurableParameters("ENABLE_EVENTS", requestManager);

            logger.error("ENABLE_EVENTS=" + enableEvents);

            if (enableEvents != null && enableEvents.equals("true") && opstatus.equals("0")) {
                String transactionType = getStringFromJsonObject(requestPayload, "transactionType", true);
                String eventType = "";
                String eventSubType = "";

                if (transactionType.equals("InternalTransfer")) {
                    eventType = "INT_TRANS";
                    eventSubType = "INT_TRANS_SUCCESS";
                } else if (transactionType.equals("ExternalTransfer")) {
                    eventType = "EXT_TRANS";
                    eventSubType = "EXT_TRANS_SUCCESS";
                }

                JsonObject eventDetails = new JsonObject();
                eventDetails.addProperty("eventType", eventType);
                eventDetails.addProperty("username", username);
                eventDetails.addProperty("eventSubType", eventSubType);
                eventDetails.add("eventData", requestPayload);

                JsonArray events = new JsonArray();

                events.add(eventDetails);
                logger.error("eventsArray:" + eventSubType.toString());

                Map<String, Object> inputMap = new HashMap<>();
                Map<String, Object> headerMap = new HashMap<>();

                inputMap.put("events", events);
                inputMap.put("producer", "CreateTransferObjectServicePostProcessor");

                callQueueMasterService(events, inputMap, headerMap, requestManager, responseManager);

            }

        } catch (Exception ex) {
            logger.error("exception occured while sending alert for transfer=", ex);
        }

    }

    public static String getStringFromJsonObject(JsonObject object, String key, boolean required) {
        JsonElement element = getElementFromJsonObject(object, key, required);
        return element == null ? null : element.getAsString();
    }

    public static JsonElement getElementFromJsonObject(JsonObject object, String key, boolean required) {
        JsonElement element = object.get(key);
        if ((element == null) && (required)) {
            throw new IllegalArgumentException("Required attribute '" + key + "' was not present");
        }
        return element;
    }

    public static JsonObject getJsonObjectFromJsonObject(JsonObject object, String key, boolean required) {
        JsonElement element = getElementFromJsonObject(object, key, required);
        if (element == null) {
            return null;
        }
        if (!element.isJsonObject()) {
            throw new IllegalArgumentException("Value for attribute '" + key + "' is not a JSON object");
        }
        return element.getAsJsonObject();
    }

}
