package com.kony.dbputilities.customersecurityservices.preprocessor;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.utils.DTOConstants;

public class CorePreProcessor implements DataPreProcessor2 {

    private LoggerUtil logger;

    @Override
    public boolean execute(HashMap inputParams, DataControllerRequest dcRequest, DataControllerResponse response,
            Result result) throws Exception {

        String partyEventData = (String) inputParams.get("partyEventData");
        if(StringUtils.isBlank(partyEventData)) {
            partyEventData = dcRequest.getParameter("partyEventData");
        }
        logger = new LoggerUtil(CorePreProcessor.class);

        String partyID = null;

        if(StringUtils.isNotBlank(partyEventData)) {
            JsonObject partyEventDataJson =  new JsonParser().parse(partyEventData).getAsJsonObject();
            if(partyEventDataJson.has(DTOConstants.PARTY_ID) && !partyEventDataJson.get(DTOConstants.PARTY_ID).isJsonNull()) {
                partyID = partyEventDataJson.get(DTOConstants.PARTY_ID).getAsString();
            }
        }


        String filter = "BackendId" +DBPUtilitiesConstants.EQUAL + partyID + DBPUtilitiesConstants.AND + "BackendType" + DBPUtilitiesConstants.EQUAL + DTOConstants.PARTY;

        try {
            result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest), URLConstants.BACKENDIDENTIFIER_GET);
        } catch (HttpCallException e) {

            logger.error("Caught exception while creating Party: ", e);
        }

        if(HelperMethods.hasRecords(result)) {

            partyID = HelperMethods.getFieldValue(result, "BackendId");


            try {
                result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest), URLConstants.BACKENDIDENTIFIER_GET);
            } catch (HttpCallException e) {

                logger.error("Caught exception while creating Party: ", e);
            }

            if(HelperMethods.hasRecords(result)) {

                String customerID = HelperMethods.getFieldValue(result, "Customer_id");
                filter = "Customer_id" +DBPUtilitiesConstants.EQUAL + customerID + DBPUtilitiesConstants.AND + "BackendType" + DBPUtilitiesConstants.EQUAL + DTOConstants.PARTY;

                try {
                    result = HelperMethods.callGetApi(dcRequest, filter, HelperMethods.getHeaders(dcRequest), URLConstants.BACKENDIDENTIFIER_GET);
                } catch (HttpCallException e) {
                    logger.error("Caught exception while creating Party: ", e);
                }

                if(HelperMethods.hasRecords(result)) {
                    inputParams.put("customerId", HelperMethods.getFieldValue(result, "BackendId"));
                    return true;
                }
            }
        }

        result = new Result();
        ErrorCodeEnum.ERR_10209.setErrorCode(result);
        
        return false;
    }


}
