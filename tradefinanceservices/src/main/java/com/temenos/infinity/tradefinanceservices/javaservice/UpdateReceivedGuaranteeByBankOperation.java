/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.dto.ReceivedGuaranteesDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.ReceivedGuaranteesResource;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateReceivedGuaranteeByBankOperation implements JavaService2 {
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        ReceivedGuaranteesResource requestResource = DBPAPIAbstractFactoryImpl.getResource(ReceivedGuaranteesResource.class);
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        ReceivedGuaranteesDTO inputDto = JSONUtils.parse(new JSONObject(inputParams).toString(), ReceivedGuaranteesDTO.class);
        return requestResource.updateReceivedGuaranteeByBank(inputDto, request);
    }
}
