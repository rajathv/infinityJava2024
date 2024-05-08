package com.kony.achservices;

import java.util.Map;

import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class DeleteBBTemplate implements JavaService2 {
    /**
     * @param s
     * @param objects
     * @param dataControllerRequest
     * @param dataControllerResponse
     * @return
     * @throws Exception
     * @description Base invoke method
     */
    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
            DataControllerResponse dataControllerResponse) throws Exception {
        Map<String, String> inputParams = HelperMethods.getInputParamMap(objects);
        return deleteBBTemplateHelper(inputParams, dataControllerRequest);
    }

    /**
     * @param inputParams
     * @param dcr
     * @return
     * @throws Exception
     * @description Helper function for removing template of a BB User
     */
    public Result deleteBBTemplateHelper(Map<String, String> inputParams, DataControllerRequest dcr)
            throws Exception {

        inputParams.put("softDelete", "1");

        return HelperMethods.callApi(dcr, inputParams, HelperMethods.getHeaders(dcr), URLConstants.BB_TEMPLATE_UPDATE);

    }
}
