/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.dto.ReceivedAmendmentsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.ReceivedGuaranteeAmendmentsResource;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateReceivedAmendmentOperation implements JavaService2 {
    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        ReceivedGuaranteeAmendmentsResource requestResource = DBPAPIAbstractFactoryImpl.getResource(ReceivedGuaranteeAmendmentsResource.class);
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        ReceivedAmendmentsDTO inputDto = JSONUtils.parse(new JSONObject(inputParams).toString(), ReceivedAmendmentsDTO.class);
        return requestResource.createReceivedAmendment(inputDto, request);
    }
}
