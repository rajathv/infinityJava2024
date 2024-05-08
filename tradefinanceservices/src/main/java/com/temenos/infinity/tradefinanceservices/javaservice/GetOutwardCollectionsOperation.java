/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.OutwardCollectionsResource;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author k.meiyazhagan
 */
public class GetOutwardCollectionsOperation implements JavaService2 {

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        OutwardCollectionsResource requestResource = DBPAPIAbstractFactoryImpl.getResource(OutwardCollectionsResource.class);
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        FilterDTO filterDto = JSONUtils.parse(new JSONObject(inputParams).toString(), FilterDTO.class);
        return requestResource.getCollections(filterDto, request);
    }
}