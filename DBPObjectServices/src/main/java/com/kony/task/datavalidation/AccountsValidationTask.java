package com.kony.task.datavalidation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.memorymgmt.AccountsManager;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class AccountsValidationTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(AccountsValidationTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		if(!HelperMethods.isDACEnabled()) {
			LOG.debug("data access control is disabled");
			return true;
		}
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			AccountsManager accountManager = new AccountsManager(fabricRequestManager, fabricResponseManager);
			String accountsLi = HelperMethods.getStringFromJsonObject(requestPayload, "accountli");
			LOG.debug("accountsLi in payload is {}",accountsLi);
			List<String> accounts = getAccounts(accountsLi);
			for (String accountNumber : accounts) {
				LOG.debug("validating account {}",accountNumber);
				if (!accountManager.validateInternalAccount(null, accountNumber)) {
					JsonObject resPayload = null;
					if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
						resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
					}
					resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
					fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
					return false;
				}
			}
		}
		return true;
	}

	private List<String> getAccounts(String accountLi) {
		List<String> list = new ArrayList<>();
		JsonArray jArray = getJsonArray(accountLi);
		if (jArray.isJsonArray()) {
			for (int i = 0; i < jArray.size(); i++) {
				JsonObject account = (JsonObject) jArray.get(i);
				list.add(account.getAsString());
			}
		}
		return list;
	}

	private JsonArray getJsonArray(String jsonString) {
		JsonParser jsonParser = new JsonParser();
		return (JsonArray) jsonParser.parse(jsonString);
	}

}
