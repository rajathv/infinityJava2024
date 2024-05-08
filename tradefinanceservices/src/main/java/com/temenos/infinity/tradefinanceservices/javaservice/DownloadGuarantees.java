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
import com.temenos.infinity.tradefinanceservices.resource.api.DownloadGuaranteesPdfResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadGuarantees implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(DownloadGuarantees.class);

    @Override
    public Object invoke(String s, Object[] inputArray, DataControllerRequest request, DataControllerResponse dataControllerResponse) throws Exception {
        Result result = new Result();
        try {
            DownloadGuaranteesPdfResource downloadGuaranteesPdfResource = DBPAPIAbstractFactoryImpl
                    .getResource(DownloadGuaranteesPdfResource.class);

            result = downloadGuaranteesPdfResource.initiateDownloadGuaranteesPdf(inputArray, request);
        } catch (Exception e) {
            LOG.error("Error occurred while invoking initiate download for trade finance details pdf: ", e);
            return ErrorCodeEnum.ERRTF_29054.setErrorCode(new Result());
        }
        return result;
    }
}
