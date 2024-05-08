/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.resource.api.ReceivedGuaranteeClaimsResource;

import java.util.HashMap;

public class DeleteReceivedGuaranteeClaim implements JavaService2 {
    @Override
    public Object invoke(String methodId, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        HashMap<String, Object> inputParams = (HashMap<String, Object>) objects[1];
        ReceivedGuaranteeClaimsResource claimsResource = DBPAPIAbstractFactoryImpl.getResource(ReceivedGuaranteeClaimsResource.class);
        return claimsResource.createClaim(methodId, inputParams, dataControllerRequest);
    }
}
