package com.kony.task.datavalidation;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.memorymgmt.AccountsManager;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class BulkTransferDataValidationTask extends BulkTransferValidationTask{
	private static final Logger LOG = LogManager.getLogger(BulkWireTransferDataValidationTask.class);
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
			AccountsManager accountManager = new AccountsManager(fabricRequestManager, fabricResponseManager);

			String bulkTransferString = requestPayload.get("bulkTransferString").toString();
			JsonPrimitive convertedObject = new Gson().fromJson(bulkTransferString,JsonPrimitive.class);
			JsonArray resultarray = new Gson().fromJson(convertedObject.getAsString(),JsonArray.class);
			
			LOG.debug("bulkTransferString {}",resultarray);
			List<Map<String, Object>> inputs = null;
			if(StringUtils.isNotBlank(requestPayload.get("bulkTransferString").toString())) {
					inputs = getFormattedInput(resultarray);			
			}
			if(inputs != null) {
				for (Map<String, Object> input : inputs) {
					LOG.debug("validating fromAccountNumber {}",input.get("fromAccountNumber"));
					String toAccountNumber = input.get("toAccountNumber").toString();
					String extAccountNumber = input.get("ExternalAccountNumber") != null ? input.get("ExternalAccountNumber").toString():"";
			        
			        //TODO : Validate validateExternalBankToAccount function call.
					if (!accountManager.validateInternalAccount(null, input.get("fromAccountNumber").toString()) ||
							!(accountManager.validateExternalBankToAccount(null, toAccountNumber, false, true)
			                        && accountManager.validateExternalBankToAccount(null, extAccountNumber, false, true))) {
						JsonObject resPayload = null;
						if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
							resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
						}
						resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
						fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
						return false;
					}
				}
		        fabricRequestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);	
			}
		}
		return true;
	}

}
