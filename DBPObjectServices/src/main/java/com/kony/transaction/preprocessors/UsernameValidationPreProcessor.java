package com.kony.transaction.preprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;

public class UsernameValidationPreProcessor implements ObjectServicePreProcessor {
    
    private static final Logger LOG = LogManager.getLogger(UsernameValidationPreProcessor.class);

    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
        if(!HelperMethods.isDACEnabled()) {
            LOG.debug("data access control is disabled");
            return true;
        }
        JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
        if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
            JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
            String username = fabricRequestManager.getServicesManager().getIdentityHandler().getUserAttributes()
                    .get("Username").toString();

            String user = JSONUtil.getString(requestPayload, "username");
            if (!user.equals(username)) {
                updateErrorCode(fabricResponseManager);
                return false;
            }
        }
        return true;

    }

    @Override
    public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager,
            FabricRequestChain fabricRequestChain) throws Exception {
        if(process(fabricRequestManager,fabricResponseManager)){
            fabricRequestChain.execute();
        }
    }
    
    private void updateErrorCode(FabricResponseManager fabricResponseManager) {
        JsonObject resPayload = null;
        if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
            resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        }
        resPayload = ErrorCodeEnum.ERR_12007.setErrorCode(resPayload);
        fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
    }
}
