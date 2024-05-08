package com.temenos.infinity.api.cards.datavalidation;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.temenos.infinity.api.cards.utils.SessionMap;
import com.temenos.infinity.api.cards.utils.CardsManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveCardTransactionsInSessionTask  implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SaveCardTransactionsInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if (null != response && !response.isJsonNull()) {
				JsonArray accounts = response.getAsJsonArray("cardTransactions");
				SessionMap transactionsMap = getAccountsSet(accounts);
				CardsManager transManager = new CardsManager(fabricRequestManager, fabricResponseManager);
				transManager.saveCardTransactionsIntoSession(transactionsMap);
			}
		} catch (Exception e) {
			LOG.error("Exception while caching accounts in session", e);
		}
		return true;
	}

	private SessionMap getAccountsSet(JsonArray accounts) {
		SessionMap accountsMap = new SessionMap();
		if (null != accounts && !accounts.isJsonNull() && accounts.size() > 0) {
			Iterator<JsonElement> itr = accounts.iterator();
			while (itr.hasNext()) {
				accountsMap.addKey(itr.next().getAsJsonObject().get("transactionReferenceNumber").getAsString());
			}
		}
		return accountsMap;
	}

}
