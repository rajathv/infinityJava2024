/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.resource.api.IssuedGuaranteeClaimsResource;

import java.util.HashMap;

public class GetIssuedGuaranteeClaimByIdOperation implements JavaService2 {
    @Override
    public Object invoke(String methodId, Object[] objects, DataControllerRequest dataControllerRequest, DataControllerResponse dataControllerResponse) throws Exception {
        HashMap<String, Object> inputParams = (HashMap<String, Object>) objects[1];
        IssuedGuaranteeClaimsResource claimsResource = DBPAPIAbstractFactoryImpl.getResource(IssuedGuaranteeClaimsResource.class);
        return claimsResource.getClaimById(inputParams, dataControllerRequest);
    }
}