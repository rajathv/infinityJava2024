package com.temenos.infinity.product.bulkpaymentservices.validations;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.memorymgmt.CorporateManager;
import com.kony.utilities.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaymentOrderIdValidation implements ObjectProcessorTask {
    private static final Logger LOG = LogManager.getLogger(PaymentOrderIdValidation.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager)
            throws Exception {
        if (!HelperMethods.isDACEnabled()) {
            LOG.debug("data access control is disabled");
            return true;
        }
        JsonElement reqPayloadJEle = fabricRequestManager.getPayloadHandler().getPayloadAsJson();
        if (!HelperMethods.isJsonEleNull(reqPayloadJEle)) {
            JsonObject requestPayload = reqPayloadJEle.getAsJsonObject();
            String recordId = HelperMethods.getStringFromJsonObject(requestPayload, "recordId");

            CorporateManager corporateManager = new CorporateManager(fabricRequestManager, fabricResponseManager);

            if (!corporateManager.validateBulkPaymentOrderId(recordId)) {
                return updateErrorResult(fabricResponseManager);
            }
        }
        return true;
    }

    private boolean updateErrorResult(FabricResponseManager fabricResponseManager){
        JsonObject resPayload = null;
        if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
            resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        }
        resPayload = ErrorCodeEnum.ERR_10149.setErrorCode(resPayload);
        fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
        return false;
    }
}