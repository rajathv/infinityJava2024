package com.kony.task.datavalidation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.memorymgmt.AccountsManager;
import com.kony.memorymgmt.PayeeManager;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class BulkBillPayValidationTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(BulkBillPayValidationTask.class);
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
			if (HelperMethods.isMFAVerify(requestPayload)) {
				LOG.debug("This is MFA verification call");
				return true;
			}
			PayeeManager payeeManager = new PayeeManager(fabricRequestManager, fabricResponseManager);
			AccountsManager accountManager = new AccountsManager(fabricRequestManager, fabricResponseManager);
			String payeeLi = HelperMethods.getStringFromJsonObject(requestPayload, "bulkPayString");
			LOG.debug("bulkPayString {}",payeeLi);
			List<Map<String, String>> payees = getPayees(payeeLi);
			for (Map<String, String> payee : payees) {
				String payeeId = payee.get("payeeId");
				String accountNumber = payee.get("accountNumber");
				String toAccountNumber = payee.get("toAccountNumber");
				LOG.debug("validating payeeId {}, accountNumber {}",payeeId, accountNumber);
				if ((payeeId != null && !"".equals(payeeId) && !payeeManager.validatePayee(null, payeeId))
						&& accountManager.validateInternalAccount(null, accountNumber)) {
					JsonObject resPayload = null;
					if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
						resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
					}
					resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
					fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
					return false;
				}
				 if(!payeeManager.validateBillPayPayeeAccountNumber(HelperMethods.getCustomerIdFromSession(fabricRequestManager),payeeId,toAccountNumber)){
	                    JsonObject resPayload = null;
	                    resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
	                    fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
	                    return false;
	                }
			}
		}
		return true;
	}

	public List<Map<String, String>> getPayees(String payeeLi) {
		List<Map<String, String>> inputs = new ArrayList<>();
		JsonArray jArray = getJsonArray(payeeLi);
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Gson gson = new Gson();
		if (jArray.isJsonArray()) {
			for (int i = 0; i < jArray.size(); i++) {
				Map<String, String> temp = gson.fromJson(jArray.get(i), type);
				inputs.add(temp);
			}
		}
		return inputs;
	}

	private JsonArray getJsonArray(String jsonString) {
		JsonParser jsonParser = new JsonParser();
		return (JsonArray) jsonParser.parse(jsonString);
	}

}
