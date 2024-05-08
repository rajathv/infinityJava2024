package com.kony.task.datavalidation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.memorymgmt.AccountsManager;
import com.kony.utilities.Constants;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class SavingsPotFundingAccountValidationTask implements ObjectProcessorTask {
    private static final Logger LOG = LogManager.getLogger(SavingsPotFundingAccountValidationTask.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
    throws Exception {
        JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
        if (HelperMethods.isJsonNotNull(reqPayloadJEle)) {
            JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
            AccountsManager accountManager = new AccountsManager(fabricRequestManager, fabricResponseManager);
            String fundingAccountHoldingId = requestPayload.get(Constants.FUNDINGACCOUNTID).toString();
            String fundingAccountId = fundingAccountHoldingId.replaceAll("\"", "");
            String custId = HelperMethods.getAPIUserIdFromSession(fabricRequestManager).toString();
            if (accountManager.validateInternalAccount(custId, fundingAccountId)) {
                if(!accountManager.isBusinessInternalAccount(custId, fundingAccountId))
                		return true;
                else {
                	JsonObject resPayload = null;
                	resPayload = ErrorCodeEnum.ERR_20048.setErrorCode(resPayload);
                    fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
                    return false;
                }
            } else {
            	JsonObject resPayload = null;
            	resPayload = ErrorCodeEnum.ERR_20043.setErrorCode(resPayload);
                fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
                return false;
            }
            
        }
        return true;
    }
}