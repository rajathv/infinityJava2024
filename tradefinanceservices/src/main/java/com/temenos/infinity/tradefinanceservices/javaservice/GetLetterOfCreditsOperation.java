package com.temenos.infinity.tradefinanceservices.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import  com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
//import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.GetLetterOfCreditsResource;


/**
 *
 *
 */
public class GetLetterOfCreditsOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetLetterOfCreditsOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
    	try {
            GetLetterOfCreditsResource letterOfCreditsResource = DBPAPIAbstractFactoryImpl
                    .getResource(GetLetterOfCreditsResource.class);
            LetterOfCreditsDTO letterOfCredits = new LetterOfCreditsDTO();                      
            Result result = letterOfCreditsResource.getLetterOfCredits(inputArray,letterOfCredits, request);
            return result;
        } catch (Exception e) { 
            LOG.error("Unable to get Letter Of Credits Requests from OMS: "+e);
            return ErrorCodeEnum.ERRTF_29046.setErrorCode(new Result()); 
        }
    }
}
