package com.temenos.dbx.product.transactionservices.javaservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.constants.TransactionBackendServicesHelper;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class GetStandingInstructions implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetStandingInstructions.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result transactionResult = new Result();
        HashMap<String, Object> serviceHeaders = new HashMap<String, Object>();
        @SuppressWarnings("unchecked")
        HashMap<String, Object> params = (HashMap<String, Object>) inputArray[1];
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        String serviceName = inputParams.get("serviceName");
        transactionResult = TransactionBackendServicesHelper.fetchBackendResponse(serviceName, request, serviceHeaders,
                params);
        if (transactionResult == null) {
            LOG.error("Error occured while invoking GetStandingInstructions: ");
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
        return transactionResult;
    }
}