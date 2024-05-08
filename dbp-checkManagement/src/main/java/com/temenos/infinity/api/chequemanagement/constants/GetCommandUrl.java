package com.temenos.infinity.api.chequemanagement.constants;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.URLFinder;
import com.temenos.infinity.api.chequemanagement.utils.ChequeManagementProperties;


public class GetCommandUrl {

	private static Properties urlProps = new Properties();
	private static final Logger LOG = LogManager.getLogger(ChequeManagementProperties.class);
	
	public void loadURL() {
		String fileName=new String();
		try {
        String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getValue("PAYMENT_BACKEND");
        if ("MOCK".equalsIgnoreCase(PAYMENT_BACKEND)||"SRMS_MOCK".equalsIgnoreCase(PAYMENT_BACKEND)) {
         fileName = "MockChequeManagement.properties";
        }
        else {
         fileName = "T24ChequeManagement.properties";
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
		try {
		LOG.debug("entering the getURL class ");
		    String command=urlProps.getProperty(type);
		   LOG.debug("command name fetched from properties file"+command);
	       return command;
		}
		catch(Exception e) {
			LOG.error("error in fetching the command name in getURL"+ e);
			return null;
		}
	    }
}
