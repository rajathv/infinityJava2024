package com.kony.task.datavalidation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.mfa.MFAConstants;
import com.kony.dbputilities.mfa.PostLoginMFAUtil;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class UpdateFeatureActionIdTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(UpdateFeatureActionIdTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
			if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
				JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
				PostLoginMFAUtil mfaUtil = new PostLoginMFAUtil(fabricRequestManager, "");
				ServicesManager sm = fabricRequestManager.getServicesManager();
				String appendedString = HelperMethods.getOperationString(sm);
				String featureActionId = mfaUtil.getValidServiceName(requestPayload, appendedString.toLowerCase());
				requestPayload.addProperty(MFAConstants.SERVICE_NAME, featureActionId);
				fabricRequestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
			}
		} catch (Exception e) {
			LOG.error("Error while loading payees into session", e);
		}
		return true;
	}

}