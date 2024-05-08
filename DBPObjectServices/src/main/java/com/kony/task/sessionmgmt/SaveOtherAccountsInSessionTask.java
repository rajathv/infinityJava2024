package com.kony.task.sessionmgmt;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.memorymgmt.AccountsManager;
import com.kony.memorymgmt.SessionMap;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveOtherAccountsInSessionTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SaveOtherAccountsInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if (null != response && !response.isJsonNull()) {
				JsonArray accounts = response.getAsJsonArray("ExternalAccounts");
				SessionMap accountsMap = getAccountsMap(accounts);
				AccountsManager acctManager = new AccountsManager(fabricRequestManager, fabricResponseManager);
				acctManager.saveOtherBankAccountsIntoSession(accountsMap);
			}
		} catch (Exception e) {
			LOG.error("Exception while caching accounts in session", e);
		}
		return true;
	}

	private SessionMap getAccountsMap(JsonArray accounts) {
		SessionMap accountsMap = new SessionMap();
		if (null != accounts && !accounts.isJsonNull() && accounts.size() > 0) {
			Iterator<JsonElement> itr = accounts.iterator();
			while (itr.hasNext()) {
				JsonElement ele = itr.next();
				accountsMap.addKey(ele.getAsJsonObject().get("accountNumber").getAsString());
			}
		}
		return accountsMap;
	}

}
