package com.kony.task.sessionmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.model.SavingsPotHelper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveSavingsPotsInSessionTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SaveSavingsPotsInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			SavingsPotHelper.saveSavingsPotsofUserIntoSession(response, fabricRequestManager);
		} catch (Exception e) {
			LOG.error("Exception while caching SavingsPot in session", e);
		}
		return true;
	}

}
