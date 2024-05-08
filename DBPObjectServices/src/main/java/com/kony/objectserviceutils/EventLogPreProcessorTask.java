package com.kony.objectserviceutils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.postprocessors.ObjectServicesConstants;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class EventLogPreProcessorTask implements ObjectProcessorTask, ObjectServicesConstants {
    private static final Logger LOG = LogManager.getLogger(EventLogPreProcessorTask.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
        
 
        // Check events enabled globally
        String enableEvents = EnvironmentConfigurationsHandler.getValue(PARAM_ENABLE_EVENTS, fabricRequestManager);
        if (enableEvents == null || enableEvents.equalsIgnoreCase(PARAM_FALSE)) {
            LOG.error("Events not enabled globally");
            return true;
        }

        try {

            EventLogUtils EventLogUtils = new EventLogUtils(); 
            JsonObject request = (JsonObject) fabricRequestManager.getPayloadHandler().getPayloadAsJson();
            JsonObject OldRequest = new Gson().fromJson(request.toString(), JsonObject.class);
            JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
            JsonObject EventLog = EventLogUtils.buildEventData(fabricRequestManager, request, response);
            

            // Initialize Variables
            JsonObject requestPayload = new JsonObject(); 
            JsonObject responsePayload = new JsonObject(); 
            JsonObject customParams = new JsonObject();
            String eventType = StringUtils.EMPTY;
            String eventSubType = StringUtils.EMPTY;
            String producer = StringUtils.EMPTY;
            String customerName = StringUtils.EMPTY;

            if (EventLog == null || EventLog.entrySet().isEmpty()) {
                LOG.error("Event Log Process aborted");
                return true;
            }

            if (!EventLog.get("ProcessError").getAsString().equalsIgnoreCase(PARAM_FALSE)) {
                LOG.error("Error Occured while processing the logs. Audit Log Process aborted");
                return true;
            }

            // Set Event Type
            if (EventLog.has(PARAM_EVENT_TYPE))
                eventType = EventLog.get(PARAM_EVENT_TYPE).getAsString();

            // Set Event Sub Type
            if (EventLog.has(PARAM_EVENT_SUBTYPE))
                eventSubType = EventLog.get(PARAM_EVENT_SUBTYPE).getAsString();

            // Set Producer
            if (EventLog.has(PARAM_PRODUCER))
                producer = EventLog.get(PARAM_PRODUCER).getAsString();

            // Set Customer Name
            if (EventLog.has(PARAM_CUSTOMER_NAME))
                customerName = EventLog.get(PARAM_CUSTOMER_NAME).getAsString();

            // Set Request Payload
            if (EventLog.has("request") && !EventLog.get("request").isJsonNull())
                requestPayload = EventLog.get("request").getAsJsonObject();

            // Set Request Payload
            if (EventLog.has("response") && !EventLog.get("response").isJsonNull())
                responsePayload = EventLog.get("response").getAsJsonObject();

            // Set Request Payload
            if (EventLog.has("customParams"))
                customParams = EventLog.get("customParams").getAsJsonObject();

            customParams.addProperty("Producer", producer);

            // Push the event via event dispatcher
            EventsDispatcher.dispatch(fabricRequestManager, requestPayload, responsePayload, eventType, eventSubType,
                    producer, PARAM_SID_EVENT_SUCCESS, null, customerName, customParams);
            
            fabricRequestManager.getPayloadHandler().updatePayloadAsJson(OldRequest); 

        } catch (Exception e) {
            LOG.error("Error while pushing to Audit Engine.");  
        }
        return true;
    }

}
