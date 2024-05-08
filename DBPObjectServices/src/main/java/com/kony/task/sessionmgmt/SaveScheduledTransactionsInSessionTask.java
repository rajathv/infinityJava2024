package com.kony.task.sessionmgmt;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.memorymgmt.SessionMap;
import com.kony.memorymgmt.TransactionManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SaveScheduledTransactionsInSessionTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(SaveScheduledTransactionsInSessionTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		try {
			JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
			if (null != response && !response.isJsonNull()) {
				//JsonArray accounts = response.getAsJsonArray("Transactions");
			    JsonArray accounts;
			    if(response.has("StandingInstruction"))
                    accounts = JSONUtil.getJsonArrary(response, "StandingInstruction");
			    else if (response.has("Payment_BillPayList"))
			    	accounts = JSONUtil.getJsonArrary(response, "Payment_BillPayList");
                else
                    accounts = JSONUtil.getJsonArrary(response, "Transactions");
				SessionMap transactionsMap = getAccountsSet(accounts);
				TransactionManager transManager = new TransactionManager(fabricRequestManager, fabricResponseManager);
				transManager.saveScheduledTransactionsIntoSession(transactionsMap);
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
				accountsMap.addKey(itr.next().getAsJsonObject().get("transactionId").getAsString());
			}
		}
		return accountsMap;
	}

}
