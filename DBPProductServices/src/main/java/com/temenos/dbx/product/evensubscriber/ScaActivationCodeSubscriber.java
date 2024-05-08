package com.temenos.dbx.product.evensubscriber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.api.events.EventData;
import com.konylabs.middleware.api.events.EventSubscriber;
import com.konylabs.middleware.api.events.IntegrationEventSubscriber;

@IntegrationEventSubscriber(topics = { "/events/scaactivationcode" })
public class ScaActivationCodeSubscriber implements EventSubscriber {
    private LoggerUtil logger;

    @Override
    public void onEvent(EventData eventData) {
        logger = new LoggerUtil(ScaActivationCodeSubscriber.class);
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
                String userId = data.has("userId") ? data.get("userId").getAsString() : "";
                String activationCode = data.has("activationCode") ? data.get("activationCode").getAsString() : "";
                logger.debug(data.toString());
                logger.debug("userId value is :" + userId);
                logger.debug("activationCode value is :" + activationCode);
            }
        }
    }

}