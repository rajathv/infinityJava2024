/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.resource.api.CreateLetterOfCreditsResource;
import com.temenos.infinity.tradefinanceservices.resource.api.GuaranteesResource;

import java.util.HashMap;

public class createClauseOperation implements JavaService2 {
    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        GuaranteesResource guaranteesResource = DBPAPIAbstractFactoryImpl
                .getResource(GuaranteesResource.class);
        HashMap inputParams = (HashMap) objects[1];
        return guaranteesResource.createClause(inputParams, dataControllerRequest);

    }
}
