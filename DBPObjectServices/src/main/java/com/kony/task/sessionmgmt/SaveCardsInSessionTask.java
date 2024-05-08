package com.kony.task.sessionmgmt;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.memorymgmt.CardsManager;
import com.kony.memorymgmt.SessionMap;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveCardsInSessionTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SaveCardsInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if (null != response && !response.isJsonNull()) {
				JsonArray cards = response.getAsJsonArray("records");
				SessionMap cardsMap = getCardsMap(cards);
				SessionMap cardsMapWithCardType = getCardsMapWithCardType(cards);
				CardsManager cardManager = new CardsManager(fabricRequestManager, fabricResponseManager);
				cardManager.saveCardsIntoSession(cardsMap);
				cardManager.saveCardsIntoSessionWithType(cardsMapWithCardType);
			}
		} catch (Exception e) {
			LOG.error("Exception while caching accounts in session", e);
		}
		return true;
	}

	private SessionMap getCardsMap(JsonArray cards) {
		SessionMap cardsMap = new SessionMap();
		if (null != cards && !cards.isJsonNull() && cards.size() > 0) {
			Iterator<JsonElement> itr = cards.iterator();
			while (itr.hasNext()) {
				cardsMap.addKey(itr.next().getAsJsonObject().get("cardId").getAsString());
						}
		}
		return cardsMap;
	}
	
	private SessionMap getCardsMapWithCardType(JsonArray cards) {
		SessionMap cardsMap = new SessionMap();
		if (null != cards && !cards.isJsonNull() && cards.size() > 0) {
			Iterator<JsonElement> itr = cards.iterator();
			while (itr.hasNext()) {
				JsonObject card=itr.next().getAsJsonObject();
				cardsMap.addKey(card.get("cardId").getAsString()+"-"+card.get("cardType").getAsString());
						}
		}
		return cardsMap;
	}
	
	
}
