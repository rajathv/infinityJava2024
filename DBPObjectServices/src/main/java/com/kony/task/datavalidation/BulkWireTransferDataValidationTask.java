package com.kony.task.datavalidation;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.memorymgmt.AccountsManager;
import com.kony.memorymgmt.PayeeManager;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class BulkWireTransferDataValidationTask extends BulkWireTransferValidationTask{
	private static final Logger LOG = LogManager.getLogger(BulkWireTransferDataValidationTask.class);
	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		boolean isDACEnabled = HelperMethods.isDACEnabled();
		
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			if (HelperMethods.isMFAVerify(requestPayload)) {
				LOG.debug("This is MFA verification call");
				return true;
			}
			AccountsManager accountManager = new AccountsManager(fabricRequestManager, fabricResponseManager);
			String bulkWirePayload = HelperMethods.getStringFromJsonObject(requestPayload, "bulkPayString");
			LOG.debug("bulkWireTransferString {}",bulkWirePayload);
			List<Map<String, Object>> inputs = null;
			int numOfDomesticTransactions = 0;
			int numOfInternationalTransactions = 0;
			String firstTransactionType = "";
			String fileId = null;
			if(StringUtils.isNotBlank(bulkWirePayload)) {
				JsonObject bulkWireJsonObject = new Gson().fromJson(bulkWirePayload, JsonObject.class);
				LOG.debug("bwRecords {}",bulkWireJsonObject.get("BWrecords"));
				if(JSONUtil.hasKey(bulkWireJsonObject, "BWrecords")) {
					JsonArray recordsArray = bulkWireJsonObject.getAsJsonArray("BWrecords");
					JsonObject firstRecord = recordsArray.get(0).getAsJsonObject();
					firstTransactionType = firstRecord.get("wireAccountType").toString();
					inputs = getFormattedInput(recordsArray);
				}
				fileId = bulkWireJsonObject.has("fileID")? bulkWireJsonObject.get("fileID").getAsString():null;
			}
			
			String customerId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
			if(inputs != null) {
				for (Map<String, Object> input : inputs) {
					if (isDACEnabled && !accountManager.validateInternalAccount(null, input.get("fromAccountNumber").toString())) {
						LOG.debug("validating fromAccountNumber {}",input.get("fromAccountNumber"));
						JsonObject resPayload = null;
						if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
							resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
						}
						resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
						fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
						return false;
					}

					String toAccountNumber = (String)input.get("payeeAccountNumber");
					String transactionType = (String)input.get("transactionType");
					String payeeId = (String)input.get("payeeId");
					PayeeManager payeeManager = new PayeeManager(fabricRequestManager, fabricResponseManager);
					if (fileId == null) {
						if (StringUtils.isNotBlank(toAccountNumber) && transactionType.equalsIgnoreCase("wire")) {
							if (!payeeManager.validateWireTransferPayeeAccountNumber(customerId, payeeId,
									toAccountNumber)) {
								if (!payeeManager.ValidateBulkwireFileLineItemsIntoSession(customerId, toAccountNumber))
									return updateErrorResult(fabricResponseManager);
							}
						}
					} else if (StringUtils.isNotBlank(toAccountNumber) && transactionType.equalsIgnoreCase("wire")) {
						if (!payeeManager.ValidateBulkwireFileItemsIntoSession(customerId, toAccountNumber)) {
							return updateErrorResult(fabricResponseManager);
						}
					}
					
					if(input.get("wireAccountType").toString().equalsIgnoreCase("Domestic")) {
						numOfDomesticTransactions++;
					}else {
						numOfInternationalTransactions++;
					}
				}
				requestPayload.addProperty("firstTransactionType", firstTransactionType);
			    requestPayload.addProperty("numOfDomesticTransactions", numOfDomesticTransactions);
			    requestPayload.addProperty("numOfInternationalTransactions", numOfInternationalTransactions);
		        fabricRequestManager.getPayloadHandler().updatePayloadAsJson(requestPayload);	
			}
		}
		return true;
	}
	private static boolean updateErrorResult(FabricResponseManager fabricResponseManager){
	    JsonObject resPayload = null;
        if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
            resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        }
        resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
        fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
        return false;
	}

}
