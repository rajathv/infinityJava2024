package com.kony.consent.task.sessionmgmt;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.memorymgmt.ConsentsManager;
import com.kony.memorymgmt.SessionMap;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveCDPConsentArrangementID implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SaveCDPConsentArrangementID.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if (null != response && !response.isJsonNull()) {
				JsonArray consents = response.getAsJsonArray("consentTypes");
				SessionMap consentsMap = getConsentsMap(consents);
				ConsentsManager consentsManager = new ConsentsManager(fabricRequestManager, fabricResponseManager);
				consentsManager.saveCDPConsentIntoSession(consentsMap);
			}
		} catch (Exception e) {
			LOG.error("Exception while caching CDP consents in session", e);
		}
		return true;
	}
	
	private SessionMap getConsentsMap(JsonArray consents) {
		SessionMap consentsMap = new SessionMap();
		if (null != consents && !consents.isJsonNull() && consents.size() > 0) {
			Iterator<JsonElement> itr = consents.iterator();
			while (itr.hasNext()) {
				consentsMap.addAttributeForKey("CDP_CONSENTS", "CDP_CONSENTS_ARRANGEMENT_ID", itr.next().getAsJsonObject().get("arrangementId").getAsString());
			}
		}
		//consentsMap.addKey(consents.getAsJsonObject().get("arrangementId").getAsString());
		return consentsMap;
	}

}
