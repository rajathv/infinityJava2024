/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradesupplyfinance.dto.TsfFilterDTO;
import com.temenos.infinity.tradesupplyfinance.resource.api.ReceivableCsvImportResource;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author k.meiyazhagan
 */
public class GetCsvImportsOperation implements JavaService2 {
    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        ReceivableCsvImportResource requestResource = DBPAPIAbstractFactoryImpl.getResource(ReceivableCsvImportResource.class);
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        TsfFilterDTO filterDto = JSONUtils.parse(new JSONObject(inputParams).toString(), TsfFilterDTO.class);
        return requestResource.getCsvImports(filterDto, request);
    }
}