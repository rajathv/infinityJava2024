package com.kony.dbputilities.accountservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class UpdateAccountPreference implements JavaService2 {
	private static final Logger logger = LogManager
			.getLogger(com.kony.dbputilities.accountservices.UpdateAccountPreference.class);
    @SuppressWarnings("rawtypes")
    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
    	logger.error("UpdateAccountPreference start request::"+dcRequest + "::response::"+dcResponse);
        Map inputParams = HelperMethods.getInputParamMap(inputArray);
        processHelper(dcRequest, inputParams);
        Result result = new Result();
        Param success = new Param(DBPUtilitiesConstants.RESULT_MSG, "Successful",
                DBPConstants.FABRIC_STRING_CONSTANT_KEY);
        result.addParam(success);
        logger.error("UpdateAccountPreference end request::"+dcRequest + "::response::"+dcResponse);
        return result;
    }

    @SuppressWarnings("rawtypes")
    private void processHelper(DataControllerRequest dcRequest, Map inputParams) throws HttpCallException {
        String accountLi = (String) inputParams.get("accountli");
        JsonArray jArray = getJsonArray(accountLi);
        for (int i = 0; i < jArray.size(); i++) {
            updateAccount(dcRequest, jArray.get(i).getAsJsonObject());
        }
    }

    private void updateAccount(DataControllerRequest dcRequest, JsonObject account) throws HttpCallException {
        Map<String, String> input = new HashMap<>();
        input.put("Account_id", account.get("accountNumber").getAsString());
        input.put("AccountPreference", account.get("accountPreference").getAsString());
        HelperMethods.callApi(dcRequest, input, HelperMethods.getHeaders(dcRequest), URLConstants.ACCOUNTS_UPDATE);
    }

    private JsonArray getJsonArray(String jsonString) {
        JsonParser jsonParser = new JsonParser();
        return (JsonArray) jsonParser.parse(jsonString);
    }
}
