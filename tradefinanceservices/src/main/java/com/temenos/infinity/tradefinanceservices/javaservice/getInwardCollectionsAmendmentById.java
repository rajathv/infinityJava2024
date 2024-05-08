/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.resource.api.InwardCollectionAmendmentsResource;

public class getInwardCollectionsAmendmentById implements JavaService2 {
    private final InwardCollectionAmendmentsResource requestBusinessDelegate = DBPAPIAbstractFactoryImpl.getResource(InwardCollectionAmendmentsResource.class);

    @Override
    public Object invoke(String methodId, Object[] objects, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) throws Exception {
        return requestBusinessDelegate.getInwardCollectionAmendmentById(dataControllerRequest);
    }
}
