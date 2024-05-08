package com.temenos.dbx.product.javaservice;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.api.events.EventData;
import com.konylabs.middleware.api.events.EventSubscriber;
import com.konylabs.middleware.api.events.IntegrationEventSubscriber;

@IntegrationEventSubscriber(topics = { "/event/accounts" })
public class CreateNewAccountSubscriber implements EventSubscriber {
    private LoggerUtil logger;

    @Override
    public void onEvent(EventData eventData) {
        logger = new LoggerUtil(CreateNewAccountSubscriber.class);
        logger.error("eventData.getData()" + eventData.getData());
        logger.error("eventData.getAdditionalMetadata()" + eventData.getAdditionalMetadata());
        JsonObject data;
        try {
            data = new JsonParser().parse(eventData.getData().toString()).getAsJsonObject();
        } catch (Exception e) {
            logger.error("error in parsing Event Data", e);
            return;
        }
        try {
            logger.debug("eventData.userProfile() : " + (logger.isDebugModeEnabled()
                    ? new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(eventData.getUserProfile())
                    : null));
        } catch (JsonProcessingException e) {
            logger.error("error in printing Event Data", e);
        }
        if (data.has("events")) {
            data = data.get("events").getAsJsonObject();
            if (data.has("eventData")) {
                data = data.get("eventData").getAsJsonObject();
                Map<String, Object> inputParams = new HashMap<>();
                inputParams.put("partyEventData", data.toString());
                ServiceCallHelper.invokeServiceAndGetJson(inputParams, null, URLConstants.CREATE_RETAIL_CONTRACT);
            }
        }
    }

}