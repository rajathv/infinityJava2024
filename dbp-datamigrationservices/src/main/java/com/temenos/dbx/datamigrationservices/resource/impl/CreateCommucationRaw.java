package com.temenos.dbx.datamigrationservices.resource.impl;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.utils.DTOConstants;

public class CreateCommucationRaw {
    private static final Logger LOG = LogManager.getLogger(CreateCommucationRaw.class);

    public static void invoke(Map<String, String> inputParams, DataControllerRequest dcRequest) {
        Map<String, String> input = null;
        Map<String, String> communicationTypes = HelperMethods.getCommunicationTypes();
        String id = inputParams.get("id");
        inputParams = getCommunicationInformation(inputParams);
        for (String key : inputParams.keySet()) {
            String value = inputParams.get(key);
            if (!StringUtils.isBlank(value)) {
                input = new HashMap<>();
                SimpleDateFormat idformatter = new SimpleDateFormat("yyMMddHHmmssSSS");
                input.put("id", "CUS_" + idformatter.format(new Date()));
                input.put("Customer_id", id);
                input.put("Type_id", communicationTypes.get(key));
                if (DBPUtilitiesConstants.COMM_TYPE_PHONE.equalsIgnoreCase(input.get("Type_id"))) {
                    input.put("Extension", DBPUtilitiesConstants.CUSTOMER_COMMUNICATION_PHONE_DEFAULT_EXTENSION);
                    if (inputParams.containsKey(DTOConstants.PHONECOUNTRYCODE)) {
                        input.put(DTOConstants.PHONECOUNTRYCODE, inputParams.get(DTOConstants.PHONECOUNTRYCODE));
                    }
                }
                input.put("isPrimary", "1");
                input.put("Value", value);
                input.put("Description", key);
                input.put("IsPreferredContactMethod", "1");
                if ("Phone".equals(key)) {
                    input.put("countryType", "domestic");
                }
                input.put("type", "Home");
                try {
                    HelperMethods.callApi(dcRequest, input, HelperMethods.getHeaders(dcRequest),
                            URLConstants.CUSTOMERCOMMUNICATION_CREATE);
                } catch (HttpCallException e) {

                    LOG.error(e.getMessage());
                }
            }
        }
    }

    private static Map<String, String> getCommunicationInformation(Map<String, String> inputParams) {

        Map<String, String> map = new HashMap<>();
        map.put("Phone", inputParams.get("Phone"));
        map.put("Email", inputParams.get("Email"));
        return map;
    }

}