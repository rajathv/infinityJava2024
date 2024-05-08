/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.dto.InwardCollectionsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.InwardCollectionsResource;
import org.json.JSONObject;

import java.util.HashMap;

public class updateInwardCollectionByBank implements JavaService2 {
    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) throws Exception {
        InwardCollectionsResource collectionsResource = DBPAPIAbstractFactoryImpl.getResource(InwardCollectionsResource.class);
        HashMap<String, Object> inputParams = (HashMap<String, Object>) objects[1];
        InwardCollectionsDTO collectionsDTO = JSONUtils.parse(new JSONObject(inputParams).toString(), InwardCollectionsDTO.class);
        return collectionsResource.updateInwardCollectionByBank(collectionsDTO, dataControllerRequest);
    }
}
