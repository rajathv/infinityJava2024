package com.kony.task.sessionmgmt;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.memorymgmt.ConsentsManager;
import com.kony.memorymgmt.DirectDebitManager;
import com.kony.memorymgmt.SessionMap;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveDirectDebitIds implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SaveDirectDebitIds.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if (null != response && !response.isJsonNull()) {
				JsonArray directDebits = response.getAsJsonArray("Transactions");
				SessionMap directDebitsMap = getDirectDebitsMap(directDebits);
				DirectDebitManager directDebitManager = new DirectDebitManager(fabricRequestManager, fabricResponseManager);
				directDebitManager.saveDirectDebitIntoSession(directDebitsMap);
			}
		} catch (Exception e) {
			LOG.error("Exception while caching CDP consents in session", e);
		}
		return true;
	}
	
	private SessionMap getDirectDebitsMap(JsonArray directDebits) {
		SessionMap directDebitsMap = new SessionMap();
		if (null != directDebits && !directDebits.isJsonNull() && directDebits.size() > 0) {
			Iterator<JsonElement> itr = directDebits.iterator();
			while (itr.hasNext()) {
				directDebitsMap.addKey(itr.next().getAsJsonObject().get("directDebitId").getAsString());
			}
		}
		return directDebitsMap;
	}

}
