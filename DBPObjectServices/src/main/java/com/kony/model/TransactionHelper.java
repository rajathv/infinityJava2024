package com.kony.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.memorymgmt.SessionMap;
import com.kony.memorymgmt.TransactionManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;

public class TransactionHelper {
	private static final Logger LOG = LogManager.getLogger(TransactionHelper.class);
	
    public static SessionMap reloadScheduledTransactionsIntoSession(FabricRequestManager fabricRequestManager) {
    	SessionMap transactionsMap = new SessionMap();
    	try {
    		Map<String, Object> input = new HashMap<>();
    		input.put("firstRecordNumber", "0");
    		input.put("lastRecordNumber", "10");
    		input.put("isScheduled", "1");
    		/* scheduledDate - scheduledDate is future date and transactionDate is today.
    		 * recent scheduled transactions may not come in 15 records.
    		 */
    		input.put("sortBy", "transactionDate");
    		input.put("order", "desc");
            JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(fabricRequestManager, input, null,
                    URLConstants.RECENT_SCHEDULED_USER_TRANSACTIONS_OS_GET);
            LOG.debug("response in reloadScheduledTransactionsIntoSession: " + response.toString());
            transactionsMap = getTransactionsMapFromResponse(response);
            LOG.debug("SessionMap of Transactions: " + transactionsMap.toString());
            //not saving to memory since on ui transactions sorted by scheduledDate. not with transactionDate
            //TransactionManager tm = new TransactionManager(fabricRequestManager, null);
            //tm.saveScheduledTransactionsIntoSession(transactionsMap);
        } catch (Exception e) {
            LOG.error("Error while reloading internal accounts:", e);
        }
    	return transactionsMap;
    }
    
    public static SessionMap reloadPostedTransactionsIntoSession(FabricRequestManager fabricRequestManager) {
    	SessionMap transactionsMap = new SessionMap();
        try {
        	Map<String, Object> input = new HashMap<>();
    		input.put("firstRecordNumber", "0");
    		input.put("lastRecordNumber", "10");
    		input.put("isScheduled", "0");
    		input.put("sortBy", "transactionDate");
    		input.put("order", "desc");
            JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(fabricRequestManager, input, null,
                    URLConstants.RECENT_POSTED_USER_TRANSACTIONS_OS_GET);
            LOG.debug("response in reloadPostedTransactionsIntoSession: " + response.toString());
            transactionsMap = getTransactionsMapFromResponse(response);
            LOG.debug("SessionMap of Transactions: " + transactionsMap.toString());
            TransactionManager tm = new TransactionManager(fabricRequestManager, null);
            tm.saveScheduledTransactionsIntoSession(transactionsMap);

        } catch (Exception e) {
            LOG.error("Error while reloading internal accounts:", e);
        }
        return transactionsMap;
    }
    
    public static SessionMap getTransactionsMapFromResponse(JsonObject response) {
        String transactionsObject = "Transactions";
        LOG.debug("Transactions Helper: Transactions Response: " + response.toString());
        if (response != null && !response.isJsonNull()) {
            if (response.has(transactionsObject)) {
                JsonArray transactions = response.getAsJsonArray(transactionsObject);
                return getTransactionsMap(transactions);
            }
        }
        return new SessionMap();
    }

    public static String getBankDate(FabricRequestManager fabricRequestManager){
        try {
            String date = DBPServiceExecutorBuilder.builder().
                    withServiceId("TransactionObjects").
                    withObjectId("BankDate").
                    withOperationId("getBankDate").
                    withFabricRequestManager(fabricRequestManager).
                    withRequestParameters(new HashMap<>()).
                    build().getResponse();
            JsonParser parser = new JsonParser();
            JsonObject dateObj = parser.parse(date).getAsJsonObject().getAsJsonArray("date").get(0).getAsJsonObject();
            return dateObj.has("currentWorkingDate") ? dateObj.get("currentWorkingDate").getAsString() : null;

        } catch (Exception e) {
            LOG.error("Error occurred while fetching bank date");
        }
        return null;
    }
    
    private static SessionMap getTransactionsMap(JsonArray transactions) {
		SessionMap transactionsMap = new SessionMap();
		if (null != transactions && !transactions.isJsonNull() && transactions.size() > 0) {
			Iterator<JsonElement> itr = transactions.iterator();
			while (itr.hasNext()) {
				transactionsMap.addKey(itr.next().
						getAsJsonObject().get("transactionId")
						.getAsString());
			}
		}
		return transactionsMap;
	}

}
