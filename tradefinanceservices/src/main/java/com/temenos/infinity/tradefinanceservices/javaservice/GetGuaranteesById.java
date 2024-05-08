/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.resource.api.GuaranteesResource;

import java.util.HashMap;

public class GetGuaranteesById implements JavaService2 {
    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        GuaranteesResource guaranteesResource = DBPAPIAbstractFactoryImpl
                .getResource(GuaranteesResource.class);
        HashMap<String, Object> inoutParams = (HashMap<String, Object>) objects[1];
        return guaranteesResource.getGuaranteesById(inoutParams, dataControllerRequest);
    }
}
