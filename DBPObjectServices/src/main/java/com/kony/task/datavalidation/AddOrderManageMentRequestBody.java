package com.kony.task.datavalidation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class AddOrderManageMentRequestBody implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(AddOrderManageMentRequestBody.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			if(requestPayload.has("serviceName")) {
				requestPayload.remove("serviceName");
			}
			if(requestPayload.has("serviceKey")) {
				requestPayload.remove("serviceKey");
			}
			if(requestPayload.has("MFAAttributes")) {
				requestPayload.remove("MFAAttributes");
			}
			if(requestPayload.has("mfaState")) {
				requestPayload.remove("mfaState");
			}
			if(requestPayload.has("mfaType")) {
				requestPayload.remove("mfaType");
			}
			if(requestPayload.has("mfaMode")) {
				requestPayload.remove("mfaMode");
			}
			String requestBody =  requestPayload.toString().replace("'","\\'");
			requestBody = requestBody.replace("\"", "'");
			requestPayload.addProperty("requestBody", requestBody);
			fabricRequestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
		}
		return true;
	}
}