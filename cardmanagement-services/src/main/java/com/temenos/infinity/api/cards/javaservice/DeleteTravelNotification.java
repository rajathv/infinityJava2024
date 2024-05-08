package com.temenos.infinity.api.cards.javaservice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.constants.DBPConstants;
import com.kony.AdminConsole.Utilities.AdminConsoleOperations;
import com.temenos.infinity.api.cards.utils.ServiceConfig;
import com.kony.dbputilities.utils.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class DeleteTravelNotification implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(DeleteTravelNotification.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
            DataControllerResponse responseInstance) throws Exception {

        try {
            String request_id = requestInstance.getParameter("request_id");
            JSONObject getResponse = deleteTravelNotification(request_id, requestInstance);
            if (getResponse.getInt(DBPConstants.FABRIC_OPSTATUS_KEY) != 0) {
                String authToken = AdminConsoleOperations.login(requestInstance);
                ServiceConfig.setValue("Auth_Token", authToken);
                getResponse = deleteTravelNotification(request_id, requestInstance);
            }
            Result processedResult = HelperMethods.constructResultFromJSONObject(getResponse);
            return processedResult;

        } catch (Exception e) {
            Result res = new Result();
            StringWriter stringWriterInstance = new StringWriter();
            PrintWriter printWriterInstance = new PrintWriter(stringWriterInstance);
            LOG.error(e);
            res.addParam(new Param("errmsgg", e.getMessage(), "string"));
            return res;
        }

    }

    public JSONObject deleteTravelNotification(String request_id, DataControllerRequest dcRequest) {
        Map<String, Object> postParametersMap = new HashMap<>();
        postParametersMap.put("request_id", request_id);
        String getResponseString = HelperMethods.invokeC360ServiceAndGetString(dcRequest, postParametersMap,
                new HashMap<>(), "deleteTravelNotification");
        return com.temenos.infinity.api.cards.utils.HelperMethods.getStringAsJSONObject(getResponseString);
    }

}
