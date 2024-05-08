package com.temenos.msArrangement.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.controller.DataControllerRequest;

public class EnvironmentConfigurationsHandler {
    private static final Logger LOG = LogManager.getLogger(EnvironmentConfigurationsHandler.class);

    public static String getValue(String key, DataControllerRequest requestInstance) {
        try {
            ServicesManager serviceManager = null;
            if (null != requestInstance) {
                serviceManager = requestInstance.getServicesManager();
            } else {
                serviceManager = ServicesManagerHelper.getServicesManager();
            }

            ConfigurableParametersHelper configurableParametersHelper = serviceManager
                    .getConfigurableParametersHelper();
            String requiredURL = configurableParametersHelper.getServerProperty(key);
            return requiredURL;
        } catch (Exception are) {
            LOG.error(are.getMessage());
        }
        return null;
    }

    public static String getValue(String key, FabricRequestManager requestManager) {
        ServicesManager serviceManager = requestManager.getServicesManager();
        ConfigurableParametersHelper configurableParametersHelper = serviceManager.getConfigurableParametersHelper();
        String requiredURL = configurableParametersHelper.getServerProperty(key);
        return requiredURL;
    }

    public static String getValue(String key) {
        ServicesManager serviceManager;
        try {
            serviceManager = ServicesManagerHelper.getServicesManager();
            ConfigurableParametersHelper configurableParametersHelper = serviceManager
                    .getConfigurableParametersHelper();
            return configurableParametersHelper.getServerProperty(key);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return "";
    }
}