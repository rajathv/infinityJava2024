package com.kony.dbputilities.mfa;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetLoginMFAConcurrent implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetLoginMFAConcurrent.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        if (preProcess(inputParams, dcRequest)) {
            try {
                result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
                        URLConstants.LOGIN_MFA_CONCURRENT_ORCHESTRATION);
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }

            return orchestrationResultProcess(result);
        }

        return result;
    }

    private Result orchestrationResultProcess(Result result) {
        Result returnResult = new Result();

        Record mfaService = result.getRecordById("mfaservice");

        if (mfaService != null) {
            String customerId = result.getParamValueByName("customerId");
            if (StringUtils.isNotBlank(customerId) && customerId.equals(mfaService.getParamValueByName("User_id"))) {
                return result;
            }
        }

        return returnResult;

    }

    private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest) {
        String serviceKey = inputParams.get(MFAConstants.SERVICE_KEY);
        String serviceName = inputParams.get(MFAConstants.SERVICE_NAME);
        if (StringUtils.isBlank(serviceKey) || StringUtils.isBlank(serviceName)) {
            return false;
        }

        return true;
    }
}