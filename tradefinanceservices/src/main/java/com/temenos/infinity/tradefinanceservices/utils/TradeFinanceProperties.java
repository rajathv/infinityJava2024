package com.temenos.infinity.tradefinanceservices.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * Class to load and access values of transactiontype.properties file
 * </p>
 * 
 *
 */
public class TradeFinanceProperties {

    private static final Logger LOG = LogManager.getLogger(TradeFinanceProperties.class);

    public TradeFinanceProperties() {
        // Private Constructor
    }

    public static Properties loadProps(String fileName) {
        Properties properties = new Properties();
        try (InputStream inputStream = TradeFinanceProperties.class.getClassLoader()
                .getResourceAsStream(fileName+".properties")) {
            properties.load(inputStream);
            return properties;
        } catch (Exception e) {
            LOG.error("Error while loading properties", e);
        }
        return properties; 
    }

}
