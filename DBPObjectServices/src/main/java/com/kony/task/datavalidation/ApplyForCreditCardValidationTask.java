package com.kony.task.datavalidation;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class ApplyForCreditCardValidationTask implements ObjectProcessorTask {
	private static final Logger LOG = LogManager.getLogger(ApplyForCreditCardValidationTask.class);
	@Override
	public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
			throws Exception {
		if(!HelperMethods.isDACEnabled()) {
			LOG.debug("data access control is disabled");
			return true;
		}
		
		JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
    	JsonObject resPayload = null;
    	resPayload = ErrorCodeEnum.ERR_10152.setErrorCode(resPayload);
		String userIdFromSession = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
		if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
			JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
			fabricResponseManager.getPayloadHandler().updatePayloadAsJson(requestPayload);
			String	cardProductName = HelperMethods.getStringFromJsonObject(requestPayload, "cardProductName");
			String	creditLimit = HelperMethods.getStringFromJsonObject(requestPayload, "creditLimit");
			String	cardHolderName = HelperMethods.getStringFromJsonObject(requestPayload, "cardHolderName");
			String	billingAddress = HelperMethods.getStringFromJsonObject(requestPayload, "billingAddress");
			String	userId = HelperMethods.getStringFromJsonObject(requestPayload, "userId");
			if(userId!=null && userId.equals(userIdFromSession)) {
				if (StringUtils.isNotBlank(cardProductName) && StringUtils.isNotBlank(creditLimit)
						&& StringUtils.isNotBlank(cardHolderName) && StringUtils.isNotBlank(billingAddress)) {
					return true;
				}
			}
		}
		fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);	
		return false;
	}
}