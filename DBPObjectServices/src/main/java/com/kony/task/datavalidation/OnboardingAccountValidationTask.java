package com.kony.task.datavalidation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;

import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class OnboardingAccountValidationTask implements ObjectProcessorTask{
	private static final Logger LOG = LogManager.getLogger(OnboardingAccountValidationTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		JsonElement requestPayloadelement = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		
		String debitAccountId = null;
		String creditAccountId = null;
		String userName = null;
		if (!HelperMethods.isJsonEleNull(requestPayloadelement)) {
			JsonObject requestPayload = requestPayloadelement.getAsJsonObject();
			debitAccountId = HelperMethods.getStringFromJsonObject(requestPayload, "debitAccountId");
			creditAccountId = HelperMethods.getStringFromJsonObject(requestPayload, "creditAccountId");
			userName = HelperMethods.getStringFromJsonObject(requestPayload, "userName");
		}
		String authToken = fabricRequestManager.getHeadersHandler().getHeader(DBPUtilitiesConstants.X_KONY_AUTHORIZATION);
		LOG.info("Inputted Auth Token = "+ authToken);
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put("userName", userName);
		Map<String, Object> requestHeaders = new HashMap<String, Object>();
		requestHeaders.put(DBPUtilitiesConstants.X_KONY_AUTHORIZATION, authToken);
		LOG.info("Request headers for the getAccountsForAdmin = "+ requestHeaders);
		JSONObject Accountsresponse = new JSONObject(DBPServiceExecutorBuilder.builder()
				.withServiceId("Holdings").withObjectId("ArranagementDetails").withOperationId("getAccountsForAdmin")
				.withRequestParameters(requestParameters).withRequestHeaders(requestHeaders).build().getResponse());
		JSONArray AccountsArray = Accountsresponse.getJSONArray("Accounts");
		LOG.info("AccountsArray"+ AccountsArray);
		List<String> accountIds = new ArrayList<String>();
		AccountsArray.forEach(item -> {
			JSONObject accountObj = (JSONObject)item;
			accountIds.add(accountObj.getString("accountID"));
		});
		LOG.info("accountIds"+ accountIds);

		if(!accountIds.contains(debitAccountId)) {
			JsonObject resPayload = null;
			resPayload = ErrorCodeEnum.ERR_21133.setErrorCode(resPayload);
			fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
			return false;
		}
		if(!accountIds.contains(creditAccountId)) {
			JsonObject resPayload = null;
			resPayload = ErrorCodeEnum.ERR_21134.setErrorCode(resPayload);
			fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
			return false;
		}
		return true;
	}

}
