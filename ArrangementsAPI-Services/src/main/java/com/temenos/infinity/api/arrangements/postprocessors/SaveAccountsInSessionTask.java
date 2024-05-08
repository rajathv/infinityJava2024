package com.temenos.infinity.api.arrangements.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.temenos.infinity.api.arrangements.utils.AccountHelper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveAccountsInSessionTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SaveAccountsInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			AccountHelper.saveInternalBankAccountsIntoSession(response, fabricRequestManager);
		} catch (Exception e) {
			LOG.error("Exception while caching accounts in session", e);
		}
		return true;
	}

}
