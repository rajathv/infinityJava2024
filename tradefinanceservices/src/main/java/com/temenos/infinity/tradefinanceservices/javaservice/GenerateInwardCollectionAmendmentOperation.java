/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.resource.api.InwardCollectionAmendmentsResource;

public class GenerateInwardCollectionAmendmentOperation implements JavaService2 {
    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        InwardCollectionAmendmentsResource resource = DBPAPIAbstractFactoryImpl.getResource(InwardCollectionAmendmentsResource.class);
        return resource.generateInwardCollectionAmendment(request);
    }
}
