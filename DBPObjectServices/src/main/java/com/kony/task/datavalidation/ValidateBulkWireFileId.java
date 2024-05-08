package com.kony.task.datavalidation;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.memorymgmt.PayeeManager;
import com.kony.memorymgmt.TransactionManager;
import com.kony.scaintegration.helper.Helper;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class ValidateBulkWireFileId implements ObjectProcessorTask {
    private static final Logger LOG = LogManager.getLogger(ValidateBulkWireFileId.class);
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
            if(HelperMethods.isMFAVerify(requestPayload) || Helper.isScaVerify(requestPayload)) {
                LOG.debug("This is MFA verification call");
                return true;
            }
            
            String customerId = HelperMethods.getCustomerIdFromSession(fabricRequestManager);
            
            TransactionManager transactionManager = new TransactionManager(fabricRequestManager, fabricResponseManager);
            String bulkWireFileID = HelperMethods.getStringFromJsonObject(requestPayload, "bulkWireFileID");
            
            if (StringUtils.isNotBlank(bulkWireFileID) ) {
                PayeeManager payeeManager = new PayeeManager(fabricRequestManager, fabricResponseManager);
                if (!payeeManager.validateBulkWireFileId(customerId, bulkWireFileID)) {
                    return updateErrorResult(fabricResponseManager);
                }
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
