/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.dto.OutwardCollectionsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.OutwardCollectionsResource;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author k.meiyazhagan
 */
public class CreateOutwardCollectionOperation implements JavaService2 {

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        OutwardCollectionsResource requestResource = DBPAPIAbstractFactoryImpl.getResource(OutwardCollectionsResource.class);
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        OutwardCollectionsDTO inputDto = JSONUtils.parse(new JSONObject(inputParams).toString(), OutwardCollectionsDTO.class);
        return requestResource.createCollection(inputDto, request);
    }
}
