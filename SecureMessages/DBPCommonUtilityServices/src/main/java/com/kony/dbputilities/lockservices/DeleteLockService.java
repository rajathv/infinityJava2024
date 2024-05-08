package com.kony.dbputilities.lockservices;

import java.util.Map;

import com.kony.dbputilities.preprocessors.DeleteLockPreProcessor;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class DeleteLockService implements JavaService2 {

    @Override
    @SuppressWarnings("rawtypes")
    public Object invoke(String paramString, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
        Result result = new Result();
        Map inputParams = HelperMethods.getInputParamMap(inputArray);

        if (callPreProcessor(inputParams, dcRequest, result)) {
            result = HelperMethods.callApi(dcRequest, inputParams, null, URLConstants.LOCK_DELETE);
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    private boolean callPreProcessor(Map inputParams, DataControllerRequest dcRequest, Result result) {
        DeleteLockPreProcessor preprocessor = new DeleteLockPreProcessor();
        return preprocessor.execute(inputParams, dcRequest, result);
    }
}
