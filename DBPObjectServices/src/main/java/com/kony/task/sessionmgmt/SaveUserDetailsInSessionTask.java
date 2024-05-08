package com.kony.task.sessionmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.model.UserDetailsHelper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveUserDetailsInSessionTask implements ObjectProcessorTask{
	private static final Logger LOG = LogManager.getLogger(SaveUserDetailsInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			UserDetailsHelper.saveUserDetailsIntoSession(response, fabricRequestManager);
		} catch (Exception e) {
			LOG.error("Exception while caching userdetails in session", e);
		}
		return true;
	}

}
