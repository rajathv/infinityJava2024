/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.dto.ExportLCAmendmentsDTO;

import com.temenos.infinity.tradefinanceservices.resource.api.ExportLCAmendmentResource;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

public class CreateExportLetterOfCreditsAmendment implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(CreateExportLetterOfCreditsAmendment.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {
        try {
            ExportLCAmendmentResource exportLCAmendmentResource = DBPAPIAbstractFactoryImpl
                    .getResource(ExportLCAmendmentResource.class);
            ExportLCAmendmentsDTO letterOfCredit = constructPayload(inputArray);
            Result result = exportLCAmendmentResource.amendExportLetterOfCredits(letterOfCredit, request);
            return result;
        } catch (Exception e) {
            LOG.error("Unable to amend Letter Of Credit " + e);
            return ErrorCodeEnum.ERRTF_29045.setErrorCode(new Result());
        }

    }

    public static ExportLCAmendmentsDTO constructPayload(Object[] inputArray) {
        ExportLCAmendmentsDTO letterOfCredit = new ExportLCAmendmentsDTO();

        @SuppressWarnings("unchecked")
        HashMap<String, Object> requestParameters = (HashMap<String, Object>) inputArray[1];

        try {
            ExportLCAmendmentsDTO parseInput = JSONUtils.parse(new JSONObject(requestParameters).toString(),
                    ExportLCAmendmentsDTO.class);
            return parseInput;
        } catch (IOException e) {
            letterOfCredit.setErrorMessage("Error occurred while parsing the input.");
        }
        return letterOfCredit;

    }
}
