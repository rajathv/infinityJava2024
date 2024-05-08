package com.kony.task.datavalidation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class CancelDirectDebitOrderManagementTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(CancelDirectDebitOrderManagementTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			requestPayload.addProperty("type", "DirectDebit");
			requestPayload.addProperty("subtype", "CancelDirectDebit");
			if(requestPayload.has("directDebitId")) {
				requestPayload.remove("directDebitId");
			}
			if(requestPayload.has("directDebitStatus")) {
				requestPayload.remove("directDebitStatus");
			}
			fabricRequestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
		}
		return true; 
	}
}
