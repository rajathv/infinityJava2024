/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.preprocessor.validations;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum;
import org.json.JSONObject;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;
import static com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceCommonUtils.updateErrorResponse;

/**
 * @author k.meiyazhagan
 */
public class ProcessReceivableSingleBillsTask implements ObjectProcessorTask {
    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) {
        ReceivableSingleBillValidation billValidation = new ReceivableSingleBillValidation();
        try {
            JSONObject payload = new JSONObject(PAYLOAD_EMPTY_CSV_BILL);
            JsonArray inputBills = fabricRequestManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject().get(PARAM_BILLS).getAsJsonArray();

            if (inputBills.size() > PARAM_MAX_CSV_RECORDS) {
                return updateErrorResponse(fabricResponseManager, ErrorCodeEnum.ERR_30015);
            }

            JsonObject jsonObject;
            for (int i = 0; i < inputBills.size(); i++) {
                jsonObject = inputBills.get(i).getAsJsonObject();
                fabricRequestManager.getPayloadHandler().updatePayloadAsJson(jsonObject);
                if (!billValidation.process(fabricRequestManager, fabricResponseManager)) {
                    return updateErrorResponse(fabricResponseManager, ErrorCodeEnum.ERR_30001);
                }
                for (String key : payload.keySet()) {
                    payload.put(key, payload.getString(key) + (jsonObject.has(key) ? jsonObject.get(key).getAsString() : "") + SEPARATOR_BILLS_ORCHESTRATION);
                }
            }

            fabricRequestManager.getPayloadHandler().updatePayloadAsJson(JsonParser.parseString(new JSONObject().put(PARAM_BILLS, payload.toString()).toString()).getAsJsonObject());
        } catch (Exception e) {
            return updateErrorResponse(fabricResponseManager, ErrorCodeEnum.ERR_30016);
        }
        return true;
    }
}
