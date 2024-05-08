package com.kony.dbputilities.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.controller.DataControllerRequest;

public class ServiceConfig {
    private static final Logger LOG = LogManager.getLogger(ServiceConfig.class);
    private static Properties props = loadProps();

    private static Properties loadProps() {
        InputStream serviceConfigInputStream = ServiceConfig.class.getClassLoader()
                .getResourceAsStream("ServiceConfig.properties");
        props = new Properties();
        try {
            props.load(serviceConfigInputStream);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
		finally {
        	if (serviceConfigInputStream!=null) {
        		try {
        			serviceConfigInputStream.close();
        		}
        		catch(Exception e)
        		{
        			LOG.error(e);
        		}
        	}
        }
        return props;
    }

    private ServiceConfig() {
    }

    public static String getValue(String key) {
        return props.getProperty(key);
    }

    public static void setValue(String key, String value) {
        props.setProperty(key, value);
    }

    public static String getValueFromRunTime(String pathKey, DataControllerRequest dcRequest) {
        return EnvironmentConfigurationsHandler.getValue(pathKey, dcRequest);
    }

}
