/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.CreateLetterOfCreditsResource;
import org.json.JSONObject;

public class AmendLetterOfCreditOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(CreateLetterOfCreditsOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {
        try {
            CreateLetterOfCreditsResource letterOfCreditsResource = DBPAPIAbstractFactoryImpl
                    .getResource(CreateLetterOfCreditsResource.class);
            LetterOfCreditsDTO letterOfCredit = constructPayload(inputArray);
            Result result = letterOfCreditsResource.amendLetterOfCredits(letterOfCredit, request);
            return result;
        } catch (Exception e) {
            LOG.error("Unable to amend Letter Of Credit " + e);
            return ErrorCodeEnum.ERRTF_29045.setErrorCode(new Result());
        }

    }

    private static LetterOfCreditsDTO constructPayload(Object[] inputArray) {
        LetterOfCreditsDTO letterOfCredit = new LetterOfCreditsDTO();

        @SuppressWarnings("unchecked")
        HashMap<String, Object> requestParameters = (HashMap<String, Object>) inputArray[1];

        try {
            LetterOfCreditsDTO parseInput = JSONUtils.parse(new JSONObject(requestParameters).toString(),
                    LetterOfCreditsDTO.class);
            return parseInput;
        } catch (IOException e) {

            letterOfCredit.setMsg("Error occurred while parsing the input.");
        }
        return letterOfCredit;

    }

}
