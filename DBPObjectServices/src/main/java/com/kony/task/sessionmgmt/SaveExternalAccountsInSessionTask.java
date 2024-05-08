package com.kony.task.sessionmgmt;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.memorymgmt.AccountsManager;
import com.kony.memorymgmt.SessionMap;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveExternalAccountsInSessionTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SaveExternalAccountsInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if (null != response && !response.isJsonNull()) {
				JsonArray extAccounts = response.getAsJsonArray("ExternalAccounts");
				SessionMap extAccountsMap = getExtAccountsMap(extAccounts);
				AccountsManager accntManager = new AccountsManager(fabricRequestManager, fabricResponseManager);
				accntManager.saveExternalBankAccountsIntoSession(extAccountsMap);
			}
		} catch (Exception e) {
			LOG.error("Exception while caching external accounts in session", e);
		}
		return true;
	}

	private SessionMap getExtAccountsMap(JsonArray extAccounts) {
		SessionMap extAccountsMap = new SessionMap();
		if (null != extAccounts && !extAccounts.isJsonNull() && extAccounts.size() > 0) {
			Iterator<JsonElement> itr = extAccounts.iterator();
			while (itr.hasNext()) {
				JsonObject extAccount = itr.next().getAsJsonObject();
				if (HelperMethods.isJsonNotNull(extAccount.get("accountNumber"))) {
					if (HelperMethods.isJsonNotNull(extAccount.get("isSameBankAccount"))) {
						extAccountsMap.addAttributeForKey(extAccount.get("accountNumber").getAsString(),
								"isSameBankAccount", extAccount.get("isSameBankAccount").getAsString());
					}
					if (HelperMethods.isJsonNotNull(extAccount.get("isInternationalAccount"))) {
						extAccountsMap.addAttributeForKey(extAccount.get("accountNumber").getAsString(),
								"isInternationalAccount", extAccount.get("isInternationalAccount").getAsString());
					}
					if (HelperMethods.isJsonNotNull(extAccount.get("Id"))) {
						extAccountsMap.addKey(extAccount.get("Id").getAsString());
					}
					if (HelperMethods.isJsonNotNull(extAccount.get("beneficiaryName"))) {
						extAccountsMap.addAttributeForKey(extAccount.get("accountNumber").getAsString(),
								"beneficiaryName", extAccount.get("beneficiaryName").getAsString());
					}
				}
			}
		}
		return extAccountsMap;
	}

}
