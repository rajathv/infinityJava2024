package com.temenos.dbx.product.businessdelegate.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.temenos.dbx.product.businessdelegate.api.SystemConfigurationBusinessDelegate;

public class SystemConfigurationBusinessDelegateImpl
        implements SystemConfigurationBusinessDelegate {

    private static final Logger LOG = LogManager.getLogger(SystemConfigurationBusinessDelegateImpl.class);

    @Override
    public String getSystemConfigurationValue(String key, Map<String, Object> headersMap) {

        String filterQuery = "PropertyName" + DBPUtilitiesConstants.EQUAL + key;
        Map<String, Object> inputParams = new HashMap<>();
        inputParams.put(DBPUtilitiesConstants.FILTER, filterQuery);
        JsonObject response = new JsonObject();
        try {
            response = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headersMap,
                    URLConstants.SYSTEM_CONFIGURATION_GET);
            return response.get(DBPDatasetConstants.DATASET_SYSTEMCONFIGURATION).getAsJsonArray().get(0)
                    .getAsJsonObject()
                    .get("PropertyValue").getAsString();
        } catch (Exception e) {
            LOG.error("Exception occured while fetching system configurations " + e.getMessage());
        }
        return null;
    }
}
