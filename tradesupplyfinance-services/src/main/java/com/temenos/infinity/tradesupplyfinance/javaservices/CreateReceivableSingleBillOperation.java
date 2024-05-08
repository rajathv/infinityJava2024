/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;
import com.temenos.infinity.tradesupplyfinance.resource.api.ReceivableSingleBillResource;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author k.meiyazhagan
 */
public class CreateReceivableSingleBillOperation implements JavaService2 {
    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws IOException {
        ReceivableSingleBillResource requestResource = DBPAPIAbstractFactoryImpl.getResource(ReceivableSingleBillResource.class);
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        ReceivableSingleBillDTO inputDto = JSONUtils.parse(new JSONObject(inputParams).toString(), ReceivableSingleBillDTO.class);
        return requestResource.createSingleBill(inputDto, request);
    }
}
