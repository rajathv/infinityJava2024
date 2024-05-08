package com.kony.postprocessors;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

public class GetNickNameObjectServicePostProcessor
		implements ObjectServicePostProcessor, ObjectServicesConstants, ObjectProcessorTask {

	private static final Logger logger = LogManager.getLogger(GetNickNameObjectServicePostProcessor.class);

	@Override
	public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager) throws Exception {

		try {
			String customerid = HelperMethods.getCustomerIdFromSession(requestManager);
			PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();

			JsonObject responsePayload = responsePayloadHandler.getPayloadAsJson().getAsJsonObject();
			JsonArray accountsArr = responsePayload.getAsJsonArray("Accounts");
			if (accountsArr != null && accountsArr.size() > 0) {
				HashMap<String, Integer> mapAccountToIndex = new HashMap<>();
				StringBuilder accountIds = new StringBuilder();
				for (int i = 0; i < accountsArr.size(); i++) {
					String accountId = ((JsonObject) accountsArr.get(i)).get("accountID").getAsString();
					mapAccountToIndex.put(accountId, i);
					accountIds.append(accountId).append(",");
				}
				accountIds.deleteCharAt(accountIds.length() - 1);
				
				String response = getNickNameFromDBXDB(customerid, accountIds.toString());
				JSONObject responseJSON = new JSONObject(response);
				if (responseJSON != null) {
					JSONArray customerAccounts = responseJSON.getJSONArray("records");
					if (customerAccounts != null && customerAccounts.length() > 0) {
						for (int i = 0; i < customerAccounts.length(); i++) {
							JSONObject customerAccount = customerAccounts.getJSONObject(i);
							if (customerAccount.has("NickName")) {
								String nickName = customerAccount.getString("NickName");
								String accountId = customerAccount.getString("Account_id");
								int index = mapAccountToIndex.get(accountId);
								((JsonObject) accountsArr.get(index)).addProperty("nickName", nickName);
							}
						}
						responsePayload.add("Accounts", accountsArr);
						responsePayloadHandler.updatePayloadAsJson(responsePayload);
					}
				}

			}
		} catch (Exception ex) {
			logger.error("exception occured in GetNickNameObjectServicePostProcessor ", ex);
		}

	}

	private String getNickNameFromDBXDB(String customerid, String accountID) {
		// TODO Auto-generated method stub
		HashMap<String, Object> requestParameters = new HashMap<>();
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_NICKNAMES_GET;
		String response = null;

		requestParameters.put("_accountIds", accountID);
		requestParameters.put("_customerid", customerid);
		try {
			response = DBPServiceExecutorBuilder.builder().withServiceId(serviceName).withObjectId(null)
					.withOperationId(operationName).withRequestParameters(requestParameters).build().getResponse();

		} catch (JSONException e) {
			logger.error("Exception caught at GetNickNameObjectServicePostProcessor: " + e);
		} catch (Exception e) {
			logger.error("Exception caught at GetNickNameObjectServicePostProcessor: " + e);
		}
		return response;
	}

	@Override
	public boolean process(FabricRequestManager requestManager, FabricResponseManager responseManager)
			throws Exception {
		execute(requestManager, responseManager);
		return true;
	}

}
