package com.temenos.dbx.product.integration;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.URLConstants;

public class IntegrationMappings {
	private static final Logger LOG = LogManager.getLogger(IntegrationMappings.class);

	private Map<String, IntegrationMappings> integrationMap = new HashMap<>();

	private static IntegrationMappings integrationMappings;

	public static IntegrationMappings getInstance() {
		if (integrationMappings == null) {
			integrationMappings = new IntegrationMappings();
		}

		return integrationMappings;
	}

	public String getIntegrationName() {
		try {
			return EnvironmentConfigurationsHandler.getServerProperty(URLConstants.INTEGRATION_NAME);
		} catch (Exception e) {
			LOG.error("Exception while fetching INTEGRATION_NAME from server property", e);
		}
		return "t24";
	}

	public void addIntegration(String integartion, IntegrationMappings integrationClass) {
		integrationMap.put(integartion, integrationClass);
	}

	public IntegrationMappings getIntegration(String integartion) {
		return integrationMap.get(integartion.toLowerCase());
	}

	public IntegrationMappings getIntegrations() {
		return getIntegration(getIntegrationName());
	}

	public boolean containsIntegration(String integartion) {
		return integrationMap.containsKey(integartion.toLowerCase());
	}

}