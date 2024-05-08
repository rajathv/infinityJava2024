package com.kony.dbputilities.customerfeedback;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CreateUserFeedback implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(CreateUserFeedback.class);

    @SuppressWarnings("rawtypes")
    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
        Result result = new Result();
        Map inputParams = HelperMethods.getInputParamMap(inputArray);
        if (preProcess(inputParams, dcRequest, result)) {
            result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
                    URLConstants.FEEDBACK_CREATE);
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean preProcess(Map inputParams, DataControllerRequest dcRequest, Result result) {
        if (inputParams.containsKey("user_id") && StringUtils.isNotBlank((String) inputParams.get("user_id"))) {
            return true;
        }
        if (inputParams.containsKey("userName") && StringUtils.isNotBlank((String) inputParams.get("userName"))) {
            String userName = (String) inputParams.get("userName");
            String filterQuery = "UserName" + DBPUtilitiesConstants.EQUAL + userName;
            Result customer = new Result();
            try {
                customer = HelperMethods.callGetApi(dcRequest, filterQuery, HelperMethods.getHeaders(dcRequest),
                        URLConstants.CUSTOMERVERIFY_GET);
            } catch (HttpCallException e) {
                LOG.error(e.getMessage());
            }
            if (HelperMethods.hasRecords(customer)) {
                inputParams.put("user_id", HelperMethods.getFieldValue(customer, "id"));
            }
            return true;
        }
        inputParams.put("user_id", HelperMethods.getUserIdFromSession(dcRequest));
        return true;
    }
}