package com.kony.AdminConsole.BLProcessor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.constants.DBPConstants;
import com.kony.AdminConsole.Utilities.AdminConsoleOperations;
import com.kony.AdminConsole.Utilities.CommonUtilities;
import com.kony.AdminConsole.Utilities.ServiceConfig;
import com.kony.dbputilities.utils.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class UpdateTravelNotification implements JavaService2 {

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
            DataControllerResponse responseInstance) throws Exception {

        try {
            String destinations = requestInstance.getParameter("Destinations");
            JSONArray Destinations = null;
            if (destinations != null) {
                Destinations = new JSONArray(destinations);
            }
            String Channel_id = requestInstance.getParameter("Channel_id");
            String StartDate = requestInstance.getParameter("StartDate");

            Map<String, Object> inputParams = CommonUtilities.getInputMapFromInputArray(inputArray);
            String Username = (String) inputParams.get("userName");

            String additionNotes = requestInstance.getParameter("additionNotes");
            String EndDate = requestInstance.getParameter("EndDate");
            String phonenumber = requestInstance.getParameter("phonenumber");
            String request_id = requestInstance.getParameter("request_id");
            String cards = requestInstance.getParameter("Cards");
            JSONArray Cards = null;
            if (cards != null) {
                Cards = new JSONArray(cards);
            }
            JSONObject getResponse = updateTravelNotification(Destinations, Channel_id, StartDate, Username,
                    additionNotes, EndDate, phonenumber, request_id, Cards, requestInstance);

            if (getResponse.getInt(DBPConstants.FABRIC_OPSTATUS_KEY) != 0) {
                String authToken = AdminConsoleOperations.login(requestInstance);
                ServiceConfig.setValue("Auth_Token", authToken);
                getResponse = updateTravelNotification(Destinations, Channel_id, StartDate, Username, additionNotes,
                        EndDate, phonenumber, request_id, Cards, requestInstance);
            }
            Result processedResult = HelperMethods.constructResultFromJSONObject(getResponse);
            return processedResult;

        } catch (Exception e) {
            Result res = new Result();
            res.addParam(new Param("errmsgg", e.getMessage(), "string"));
            return res;
        }

    }

    public JSONObject updateTravelNotification(JSONArray Destinations, String Channel_id, String StartDate,
            String Username, String additionNotes, String EndDate, String phonenumber, String request_id,
            JSONArray Cards, DataControllerRequest dcRequest) {

        Map<String, Object> postParametersMap = new HashMap<>();
        postParametersMap.put("Destinations", Destinations);
        postParametersMap.put("Channel_id", Channel_id);
        if (StartDate != null) {
            postParametersMap.put("StartDate", StartDate);
        }
        if (Username != null) {
            postParametersMap.put("Username", Username);
        }
        if (additionNotes != null) {
            postParametersMap.put("additionNotes", additionNotes);
        }
        if (EndDate != null) {
            postParametersMap.put("EndDate", EndDate);
        }
        if (phonenumber != null) {
            postParametersMap.put("phonenumber", phonenumber);
        }
        if (request_id != null) {
            postParametersMap.put("request_id", request_id);
        }
        if (Cards != null) {
            postParametersMap.put("Cards", Cards);
        }

        String getResponseString = HelperMethods.invokeC360ServiceAndGetString(dcRequest, postParametersMap,
                new HashMap<>(), "updateTravelNotification");
        return CommonUtilities.getStringAsJSONObject(getResponseString);
    }

}
