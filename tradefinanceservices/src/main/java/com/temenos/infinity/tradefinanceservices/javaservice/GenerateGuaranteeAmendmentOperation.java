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
import com.temenos.infinity.tradefinanceservices.resource.api.GuaranteeLCAmendmentsResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenerateGuaranteeAmendmentOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(GenerateGuaranteeAmendmentOperation.class);

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        GuaranteeLCAmendmentsResource requestResource = DBPAPIAbstractFactoryImpl.getResource(GuaranteeLCAmendmentsResource.class);
        try {
            return requestResource.generatePdfGuaranteeLcAmendment(request);
        } catch (Exception e) {
            LOG.error("Error occurred while generating pdf. Error: ", e);
            return ErrorCodeEnum.ERRTF_29054.setErrorCode(new Result());
        }
    }
}