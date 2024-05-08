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
import com.temenos.infinity.tradefinanceservices.dto.DrawingsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.LetterOfCreditDrawingsResource;

public class GetLetterOfCreditsDrawingsOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetLetterOfCreditsDrawingsOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {
        try {
            LetterOfCreditDrawingsResource drawingsResource = DBPAPIAbstractFactoryImpl
                    .getResource(LetterOfCreditDrawingsResource.class);
            DrawingsDTO drawings = new DrawingsDTO();
            Result result = drawingsResource.getImportDrawings(inputArray, drawings, request);
            return result;
        } catch (Exception e) {
            LOG.error("Unable to get Drawings Requests from OMS: " + e);
            return ErrorCodeEnum.ERRTF_29061.setErrorCode(new Result());
        }
    }
}
