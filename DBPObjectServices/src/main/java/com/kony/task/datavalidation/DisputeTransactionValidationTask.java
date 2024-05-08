package com.kony.task.datavalidation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.kony.dbputilities.util.URLFinder;

public class DisputeTransactionValidationTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(DisputeTransactionValidationTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		if (!HelperMethods.isDACEnabled()) {
			LOG.debug("data access control is disabled");
			return true;
		}
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			//String type = URLFinder.getPropertyValue("disputeTransactionType", "objectservice.properties");
			//String subtype = URLFinder.getPropertyValue("disputeTransactionSubType", "objectservice.properties");
			requestPayload.addProperty("type", "DisputeTransaction");
			requestPayload.addProperty("subtype", "CancelDisputeTransaction");
			fabricRequestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
		}
		return true;
	}
}
