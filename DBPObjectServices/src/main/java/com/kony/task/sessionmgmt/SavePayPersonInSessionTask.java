package com.kony.task.sessionmgmt;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.memorymgmt.PayPersonManager;
import com.kony.memorymgmt.SessionMap;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SavePayPersonInSessionTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SavePayPersonInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if (null != response && !response.isJsonNull()) {
				JsonArray payPersons = response.getAsJsonArray("PayPerson");
				SessionMap payPersonMap = getPayPersonsMap(payPersons);
				PayPersonManager payPersonManager = new PayPersonManager(fabricRequestManager, fabricResponseManager);
				payPersonManager.savePayPersonsIntoSession(payPersonMap);
			}
		} catch (Exception e) {
			LOG.error("Exception while caching pay person in session", e);
		}
		return true;
	}

	private SessionMap getPayPersonsMap(JsonArray payPersons) {
		SessionMap payPersonMap = new SessionMap();
		if (null != payPersons && !payPersons.isJsonNull() && payPersons.size() > 0) {
			Iterator<JsonElement> itr = payPersons.iterator();
			while (itr.hasNext()) {
				payPersonMap.addKey(itr.next().getAsJsonObject().get("PayPersonId").getAsString());
			}
		}
		return payPersonMap;
	}

}
