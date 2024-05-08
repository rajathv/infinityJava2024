package com.kony.AdminConsole.BLProcessor;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.dbp.core.constants.DBPConstants;
import com.kony.AdminConsole.Utilities.AdminConsoleOperations;
import com.kony.AdminConsole.Utilities.CommonUtilities;
import com.kony.AdminConsole.Utilities.HTTPOperations;
import com.kony.AdminConsole.Utilities.ServiceConfig;
import com.kony.dbputilities.utils.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class GetCustomerContact implements JavaService2 {

    HTTPOperations httpOperationInstance = new HTTPOperations();

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
            DataControllerResponse responseInstance) throws Exception {
        try {
            Map<String, Object> inputparams = CommonUtilities.getInputMapFromInputArray(inputArray);
            String username = (String) inputparams.get("userName");

            JSONObject getResponse = getCustomerContact(username, requestInstance);

            if (getResponse.getInt(DBPConstants.FABRIC_OPSTATUS_KEY) != 0) {
                String authToken = AdminConsoleOperations.login(requestInstance);
                ServiceConfig.setValue("Auth_Token", authToken);
                getResponse = getCustomerContact(username, requestInstance);
            }
            Result processedResult = HelperMethods.constructResultFromJSONObject(getResponse);
            return processedResult;
        } catch (Exception e) {
            Result processedResult = new Result();
            processedResult.addParam(new Param("Error", e.getMessage(), "String"));
            return processedResult;
        }

    }

    public JSONObject getCustomerContact(String username, DataControllerRequest dcRequest) {

        Map<String, Object> postParametersMap = new HashMap<>();

        postParametersMap.put("username", username);

        String getResponseString = HelperMethods.invokeC360ServiceAndGetString(dcRequest, postParametersMap,
                new HashMap<>(), "getCustomerContact");
        return CommonUtilities.getStringAsJSONObject(getResponseString);
    }

}