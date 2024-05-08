package com.kony.utilityadminconsoleservices;

import java.util.HashMap;
import java.util.Map;

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

public class GetSupportedBrowserMatrix implements JavaService2 {

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
            DataControllerResponse responseInstance) throws Exception {
        JSONObject getResponse = getSupportedBrowserMatrix(requestInstance);
        if (getResponse.getInt(DBPConstants.FABRIC_OPSTATUS_KEY) != 0) {
            String authToken = AdminConsoleOperations.login(requestInstance);
            ServiceConfig.setValue("Auth_Token", authToken);
            getResponse = getSupportedBrowserMatrix(requestInstance);
        }
        Result processedResult = HelperMethods.constructResultFromJSONObject(getResponse);
        return processedResult;

    }

    public JSONObject getSupportedBrowserMatrix(DataControllerRequest dcRequest) {

        Map<String, Object> headerParams = new HashMap<>();
        headerParams.put("Accept-Language", dcRequest.getParameter("Accept-Language"));

        String getResponseString = HelperMethods.invokeC360ServiceAndGetString(dcRequest, null, headerParams,
                "getSupportedBrowserMatrix");
        return CommonUtilities.getStringAsJSONObject(getResponseString);
    }

}
