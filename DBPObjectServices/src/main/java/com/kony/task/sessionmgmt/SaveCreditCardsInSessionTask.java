package com.kony.task.sessionmgmt;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.memorymgmt.SessionMap;
import com.kony.memorymgmt.TransactionManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class SaveCreditCardsInSessionTask implements ObjectProcessorTask {
    private static final Logger LOG = LogManager.getLogger(SaveCreditCardsInSessionTask.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
        try {
            JsonObject response = (JsonObject) fabricResponseManager.getPayloadHandler().getPayloadAsJson();
            saveCreditCards(response, fabricRequestManager, fabricResponseManager);
        } catch (Exception e) {
            LOG.error("Exception while caching accounts in session", e);
        }
        return true;
    }

    public static void saveCreditCards(JsonObject response, FabricRequestManager fabricRequestManager,
            FabricResponseManager fabricResponseManager) {
        String AccountsObject = "Accounts";
        LOG.debug("Account Helper: Accounts Response: " + response.toString());
        if (null != response && !response.isJsonNull()) {
            if (response.has(AccountsObject)) {
                JsonArray accounts = response.getAsJsonArray(AccountsObject);
                SessionMap creditCards = new SessionMap();
                if (null != accounts && !accounts.isJsonNull() && accounts.size() > 0) {
                    Iterator<JsonElement> itr = accounts.iterator();
                    while (itr.hasNext()) {
                        creditCards.addKey(itr.next().getAsJsonObject().get("accountID").getAsString());
                    }
                }
                LOG.error("SessionMap of Credit Cards: " + creditCards.toString());
                TransactionManager transManager = new TransactionManager(fabricRequestManager, fabricResponseManager);
                transManager.saveCreditCardsIntoSession(creditCards);
                LOG.error("Credit Cards Saved Successfully");
            }
        }
    }

}
