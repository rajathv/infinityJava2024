package com.temenos.dbx.consents.javaservice;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.URLFinder;


public class GetUrl {

	private static Properties urlProps = new Properties();
	private static final Logger LOG = LogManager.getLogger(com.temenos.dbx.consents.javaservice.GetCDPConsents.class);
	
	public void loadProperties() {
		String fileName=new String();
		try {
        String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue("PAYMENT_BACKEND");
        if ("MOCK".equalsIgnoreCase(PAYMENT_BACKEND)||"SRMS_MOCK".equalsIgnoreCase(PAYMENT_BACKEND)) {
         fileName = "MockProperties.properties";
        }
        else {
         fileName = "T24Properties.properties";
        }
        try (InputStream inputStream = URLFinder.class.getClassLoader().getResourceAsStream(fileName);) {
            urlProps.load(inputStream);
        } catch (FileNotFoundException e) {
            LOG.info("error while loading properties file", e);
        } 
		}
		catch (IOException e) {
            LOG.info("error while reading properties file", e);
        
        }
    }
	public static final String getURL (String type) {
		   String command=urlProps.getProperty(type);
	       return command;
	    }
}
