package com.kony.dbx.util;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.URLProvider;
import com.konylabs.middleware.controller.DataControllerRequest;

public class ServiceURLProvider implements URLProvider {
	private static final Logger LOG = LogManager.getLogger(ServiceURLProvider.class);
	private static final String KEY_ENDS_MARKER = "_$_";

	public String execute(String operationURL, DataControllerRequest requestInstance) {
		try {
			String targetURL = operationURL;
			int startIndex = operationURL.indexOf(KEY_ENDS_MARKER);
			int lastIndex = operationURL.lastIndexOf(KEY_ENDS_MARKER);
			if (startIndex < lastIndex) {
				String propertyKey = operationURL.substring(startIndex + KEY_ENDS_MARKER.length(), lastIndex);
				String baseURL = operationURL.substring(0, lastIndex + KEY_ENDS_MARKER.length());
				String propertyValue = EnvironmentConfigurationsHandler.getValue(propertyKey, requestInstance);
				if (StringUtils.isNotBlank(propertyValue)) {
					targetURL = operationURL.replace(baseURL, propertyValue);
				}

			}

			LOG.debug("URL resolved from server configurations :" + targetURL);
			return targetURL;
		} catch (Exception e) {
			LOG.error("Exception occured while resolving operationURL:" + operationURL + "with exception:" + e);
		}
		return null;
	}
}
