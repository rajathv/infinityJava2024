package com.kony.dbputilities.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.URLProvider;
import com.konylabs.middleware.controller.DataControllerRequest;

public class ServiceURLProvider implements URLProvider {
    private static final Logger LOG = LogManager.getLogger(ServiceURLProvider.class);
    private static final String KEY_ENDS_MARKER = "_$_";

    @Override
    public String execute(String operationURL, DataControllerRequest requestInstance) {
        try {
            String targetURL = null;
            String propertyKey = operationURL.substring(
                    operationURL.indexOf(KEY_ENDS_MARKER) + KEY_ENDS_MARKER.length(),
                    operationURL.lastIndexOf(KEY_ENDS_MARKER));
            String baseURL = operationURL.substring(0,
                    operationURL.lastIndexOf(KEY_ENDS_MARKER) + KEY_ENDS_MARKER.length());
            targetURL = operationURL.replace(baseURL,
                    EnvironmentConfigurationsHandler.getValue(propertyKey, requestInstance));
            return targetURL;
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return null;
    }
}
