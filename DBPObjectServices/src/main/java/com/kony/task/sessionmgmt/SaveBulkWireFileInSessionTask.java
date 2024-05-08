package com.kony.task.sessionmgmt;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.memorymgmt.PayeeManager;
import com.kony.memorymgmt.SessionMap;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveBulkWireFileInSessionTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SaveWireTransferPayeesInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			SessionMap BulkWiresMap = new SessionMap();
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if (null != response && !response.isJsonNull()) {
				if (response.has("BulkWires")) {
					JsonArray BulkWires = response.getAsJsonArray("BulkWires");
                    BulkWiresMap = getPayeesMap(BulkWires);
                }
                if (response.has("bulkWireFileID")) {
                    BulkWiresMap.addKey(response.get("bulkWireFileID").getAsString());
                }
				PayeeManager payeemanager = new PayeeManager(fabricRequestManager, fabricResponseManager);
				payeemanager.saveBulkWireFileIntoSession(BulkWiresMap);
			}
		} catch (Exception e) {
			LOG.error("Exception while caching payees in session", e);
		}
		return true;
	}

	private SessionMap getPayeesMap(JsonArray payees) {
		SessionMap payeesMap = new SessionMap();
		if (null != payees && !payees.isJsonNull() && payees.size() > 0) {
			Iterator<JsonElement> itr = payees.iterator();
			while (itr.hasNext()) {
				JsonObject payee = itr.next().getAsJsonObject();
				payeesMap.addKey(payee.get("bulkWireID").getAsString());
			}
		}
		return payeesMap;
	}

}
