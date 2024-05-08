/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.resource.api.CorporatePayeesResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class getCorporatePayeesOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(getCorporatePayeesOperation.class);

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) throws Exception {
        CorporatePayeesResource payeesResource = DBPAPIAbstractFactoryImpl.getResource(CorporatePayeesResource.class);
        HashMap<String, Object> inputParams = (HashMap<String, Object>) objects[1];
        try {
            return payeesResource.getCorporatePayees(inputParams, dataControllerRequest);
        } catch (Exception e) {
            LOG.error("Caught exception at invoke : " + e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
    }
}
