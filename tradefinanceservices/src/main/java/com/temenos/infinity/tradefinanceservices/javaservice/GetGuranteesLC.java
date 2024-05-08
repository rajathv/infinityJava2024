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
import com.temenos.infinity.tradefinanceservices.dto.GuranteesDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.GuaranteesResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GetGuranteesLC implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetGuranteesLC.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            GuaranteesResource letterOfCreditsResource = DBPAPIAbstractFactoryImpl
                    .getResource(GuaranteesResource.class);
            GuranteesDTO letterOfCredits = new GuranteesDTO();
            result = letterOfCreditsResource.getGurantees(inputArray, letterOfCredits, request);
        } catch (Exception e) {
            LOG.error("Error occured while invoking Get Gurantees LC: ", e);
            return ErrorCodeEnum.ERRTF_29054.setErrorCode(new Result());
        }
        return result;
    }
}

