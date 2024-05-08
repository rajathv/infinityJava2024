package com.temenos.infinity.api.chequemanagement.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * Class to load and access values of transactiontype.properties file
 * </p>
 * 
 * @author Aditya Mankal
 *
 */
public class ChequeManagementProperties {

    private static final Logger LOG = LogManager.getLogger(ChequeManagementProperties.class);

    public ChequeManagementProperties() {
        // Private Constructor
    }

    public static Properties loadProps(String fileName) {
        Properties properties = new Properties();
        try (InputStream inputStream = ChequeManagementProperties.class.getClassLoader()
                .getResourceAsStream(fileName+".properties")) {
            properties.load(inputStream);
            return properties;
        } catch (Exception e) {
            LOG.error("Error while loading properties", e);
        }
        return properties; 
    }

}
