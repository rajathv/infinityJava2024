package com.temenos.infinity.wealth.common.util;

import org.apache.log4j.Logger;

import com.konylabs.middleware.common.URLProvider;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.commons.config.EnvironmentConfigurationsHandler;
import com.temenos.infinity.wealth.common.constants.InfWlthConstants;

/**
 * URL provider class to resolve the relevant base URLs for T24 & TAP backend systems. 
 * Resolves the URL by fetching values from Runtime Configurations.
 *
 * @author Rajesh Kappera
 */
public class InfWlthURLProvider implements URLProvider {
	private static final Logger LOG = Logger.getLogger(InfWlthURLProvider.class);
    private static final String KEY_ENDS_MARKER = "_$_";
	
    @Override
    public String execute(String operationURL, DataControllerRequest requestInstance) {
        LOG.info("==========> InfWlthURLProvider - execute - Begin");
        LOG.debug("==========> operationURL : " + operationURL);
        
        String targetURL = null;
        
        try {
        	String propertyKey = operationURL.substring(operationURL.indexOf(KEY_ENDS_MARKER) + KEY_ENDS_MARKER.length(), operationURL.lastIndexOf(KEY_ENDS_MARKER));
            String baseURL = operationURL.substring(0, operationURL.lastIndexOf(KEY_ENDS_MARKER) + KEY_ENDS_MARKER.length());
            //targetURL = operationURL.replace(baseURL, EnvironmentConfigurationsHandler.getValue(propertyKey, requestInstance));
            
            // Get INF_WLTH_CORE
            String infWlthCore = EnvironmentConfigurationsHandler.getServerAppProperty(InfWlthConstants.INF_WLTH_CORE, requestInstance);
            LOG.debug("==========> infWlthCore : " + infWlthCore);
            if (null != infWlthCore && !infWlthCore.isEmpty()) {
            	 
            	if (infWlthCore.toUpperCase().contains(InfWlthConstants.TAP)) { // Check if the backend system is TAP
            		propertyKey = InfWlthConstants.INF_WLTH_TAP_HOST_URL;
            	} else if (infWlthCore.toUpperCase().contains(InfWlthConstants.T24)) { // Check if the backend system is T24
            		propertyKey = InfWlthConstants.INF_WLTH_T24_HOST_URL;
            	}
            }
            
            targetURL = operationURL.replace(baseURL, EnvironmentConfigurationsHandler.getServerAppProperty(propertyKey, requestInstance));
            LOG.debug("==========> targetURL : " + targetURL);
        } catch (Exception ex) {
            LOG.error("==========> Exception occured while resolving operation URL. Attempted Operation URL:" + operationURL);
            LOG.error(ex);
        }

        LOG.info("==========> InfWlthURLProvider - execute - End");
        return targetURL;
    }
}
