package com.temenos.infinity.api.arrangements.javaservice;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class PartialRepaymentOperation implements JavaService2 {
	
	private static final Logger LOG = LogManager.getLogger(PartialRepaymentOperation.class);
    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        LOG.debug("Input Params from SRFMS CurrentAccountClose",inputParams.toString());
            result.addParam(DBPUtilitiesConstants.MSG_STATUS, "SUCCESS");
            result.addOpstatusParam(0);
			result.addHttpStatusCodeParam(200);
       
        return result;
    }
}
