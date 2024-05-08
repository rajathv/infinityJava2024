package com.temenos.infinity.api.savingspot.task.sessionmgmt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.memorymgmt.SavingsPotManager;
import com.kony.utilities.Constants;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.temenos.infinity.api.savingspot.model.SavingsPotHelper;

public class CloseSavingsPotInSessionTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(CloseSavingsPotInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		JsonElement savingsPotId = null;
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if(!response.has("dbpErrMsg") && !response.has("dbpErrCode")) {
			JsonObject request = (JsonObject) fabricRequestManager.getPayloadHandler().getPayloadAsJson();
			String customerId = HelperMethods.getAPIUserIdFromSession(fabricRequestManager).toString();
		    savingsPotId =  request.get(Constants.SAVINGSPOTID);
			SavingsPotManager savingsPotManager = new SavingsPotManager(fabricRequestManager, fabricResponseManager);
			savingsPotManager.closeSavingsPotInSession(customerId, savingsPotId);
			}
		} catch (Exception e) {
			LOG.error("Exception while closing SavingsPot with id"+savingsPotId.toString()+" in session", e);
		}
		return true;
	}

}