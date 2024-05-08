/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.resource.api.ReceivedGuaranteeAmendmentsResource;

public class GetReceivedAmendmentByIdOperation implements JavaService2 {
    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        ReceivedGuaranteeAmendmentsResource requestResource = DBPAPIAbstractFactoryImpl.getResource(ReceivedGuaranteeAmendmentsResource.class);
        return requestResource.getReceivedAmendmentById(request);
    }
}
