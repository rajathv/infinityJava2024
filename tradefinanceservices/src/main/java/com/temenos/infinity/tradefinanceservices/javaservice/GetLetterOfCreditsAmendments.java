/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.GetAmendmentsLetterOfCreditsResource;


public class GetLetterOfCreditsAmendments implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetLetterOfCreditsOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {
        try {

            GetAmendmentsLetterOfCreditsResource letterOfCreditsResource = DBPAPIAbstractFactoryImpl
                    .getResource(GetAmendmentsLetterOfCreditsResource.class);
            LetterOfCreditsDTO letterOfCredits = new LetterOfCreditsDTO();
            Result result = letterOfCreditsResource.getAmendLetterOfCredits(inputArray, letterOfCredits, request);
            return result;
        } catch (Exception e) {
            LOG.error("Unable to get Amend Letter Of Credits Requests from OMS: " + e);
            return ErrorCodeEnum.ERRTF_29046.setErrorCode(new Result());
        }
    }
}
