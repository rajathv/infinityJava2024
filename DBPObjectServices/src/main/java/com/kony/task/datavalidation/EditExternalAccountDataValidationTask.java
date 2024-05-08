package com.kony.task.datavalidation;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.memorymgmt.AccountsManager;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class EditExternalAccountDataValidationTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(EditExternalAccountDataValidationTask.class);

	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		if (!HelperMethods.isDACEnabled()) {
			LOG.debug("data access control is disabled");
			return true;
		}
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
		String accountNumber = fabricRequestManager.getQueryParamsHandler().getParameter("accountNumber");
		String iban = fabricRequestManager.getQueryParamsHandler().getParameter("IBAN");
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			if (HelperMethods.isMFAVerify(requestPayload)) {
				LOG.debug("This is MFA verification call");
				return true;
			}
			accountNumber = HelperMethods.getStringFromJsonObject(requestPayload, "accountNumber");
			iban = HelperMethods.getStringFromJsonObject(requestPayload, "IBAN");
		}
		LOG.debug("validating external accountNumber {}", accountNumber);

		if ((StringUtils.isNotBlank(accountNumber))) {
			AccountsManager accountManager = new AccountsManager(fabricRequestManager, fabricResponseManager);
			
			//TODO: Fix this for Domestic Payee Validation.
			//TODO : Validate validateExternalBankToAccount function call.
			if (!(accountManager.validateExternalBankToAccount(null, accountNumber, true, false)
					|| accountManager.validateExternalBankToAccount(null, accountNumber, false, true))) {
				JsonObject resPayload = null;
				if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
					resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
				}
				resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
				fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
				return false;
			}
		} else if (StringUtils.isNotBlank(iban)) {
			AccountsManager accountManager = new AccountsManager(fabricRequestManager, fabricResponseManager);
			if (!(accountManager.validateExternalBankToAccount(null, iban, false, true)))
			{
				JsonObject resPayload = null;
				if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
					resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
				}
				resPayload = ErrorCodeEnum.ERR_12403.setErrorCode(resPayload);
				fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
				return false;
			}

		}
		return true;
	}
}
