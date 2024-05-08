package com.kony.AdminConsole.BLProcessor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.constants.DBPConstants;
import com.kony.AdminConsole.Utilities.AdminConsoleOperations;
import com.kony.AdminConsole.Utilities.CommonUtilities;
import com.kony.AdminConsole.Utilities.ServiceConfig;
import com.kony.dbputilities.utils.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetOrgEmployeeServiceLimits implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetOrgEmployeeServiceLimits.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
            DataControllerResponse responseInstance) throws Exception {

        String username = requestInstance.getParameter("Username");

        try {
            String reqErrorCode = requestInstance.getParameter(DBPConstants.DBP_ERROR_CODE_KEY);
            if (StringUtils.isNotBlank(reqErrorCode)) {
                Result res = new Result();
                return res;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        JSONObject getResponse = createTransactionLimits(username, requestInstance);

        if (getResponse == null || !getResponse.has(DBPConstants.FABRIC_OPSTATUS_KEY)
                || getResponse.getInt(DBPConstants.FABRIC_OPSTATUS_KEY) != 0) {
            String authToken = AdminConsoleOperations.login(requestInstance);
            ServiceConfig.setValue("Auth_Token", authToken);
            getResponse = createTransactionLimits(username, requestInstance);
        }

        Result processedResult = HelperMethods.constructResultFromJSONObject(getResponse);

        return processedResult;
    }

    public JSONObject createTransactionLimits(String username, DataControllerRequest dcRequest) {
        Map<String, Object> postParametersMap = new HashMap<>();

        postParametersMap.put("Username", "'" + username + "'");
        String getResponseString = HelperMethods.invokeC360ServiceAndGetString(dcRequest, postParametersMap,
                new HashMap<>(), "getBBCustomerServiceLimit");
        return CommonUtilities.getStringAsJSONObject(getResponseString);
    }
}
