package com.temenos.infinity.api.arrangements.task;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.temenos.infinity.api.arrangements.constants.ObjectServicesConstants;
import com.temenos.infinity.api.arrangements.utils.ObjectServiceHelperMethods;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TriggerAccountClosureAlerts implements ObjectProcessorTask, ObjectServicesConstants {
  private static final Logger logger = LogManager.getLogger(TriggerAccountClosureAlerts.class);
  
  public boolean process(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager) throws Exception {
    JsonObject responsePayloadJson = fabricResManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
    JsonObject requestPayloadJson = (fabricReqManager.getPayloadHandler().getPayloadAsJson() != null) ? fabricReqManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject() : new JsonObject();
    String statusId = "SID_EVENT_SUCCESS";
    String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters("ENABLE_EVENTS", fabricReqManager);
    if(responsePayloadJson.has("errmsg")) {
    	return true;
    }
    String eventType = "ACCOUNT_CLOSURE";
    String status = responsePayloadJson.get("Status").getAsString();
    String eventSubType = null;
    JsonObject customParams = new JsonObject();
    if (enableEvents != null && enableEvents.equalsIgnoreCase("true")) {
      String producer = "ArrangementsJavaServices/closeAccountAck";
      switch (status) {
        case "CLOSED":
          eventSubType = "ACCOUNT_CLOSURE_APPROVED";
          break;
        case "ACTIVE":
          eventSubType = "ACCOUNT_CLOSURE_REJECTED";
          break;
        case "SUSPENDED":
        	eventSubType = "ACCOUNT_CLOSURE_USER_SUSPENDED";
        	break;
        default:
        	break;
      } 
      String customerid = requestPayloadJson.get("customerId").getAsString();
      String accountNumber = requestPayloadJson.get("accountNumber").getAsString();
      String accountName = requestPayloadJson.get("accountName").getAsString();
      customParams.addProperty("Account Name-Account Number", accountName + "-" + accountNumber);
         
      if (enableEvents != null && enableEvents.equals("true"))
        try {
          ObjectServiceHelperMethods.execute(new ObjectServiceHelperMethods(fabricReqManager, fabricResManager, eventType, eventSubType, producer, statusId, null, customerid, customParams));
        } catch (Exception e2) {
          logger.error("Exception Occured while invoking objectServiceHelperMethods", e2);
        }  
    } 
    return true;
  }
}
