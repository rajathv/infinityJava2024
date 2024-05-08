package com.temenos.infinity.api.arrangements.prop;

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
public class TransactionTypeProperties {

    private static final Logger LOG = LogManager.getLogger(TransactionTypeProperties.class);

    private static final Properties PROPS = loadProps();

    private TransactionTypeProperties() {
        // Private Constructor
    }

    private static Properties loadProps() {
        Properties properties = new Properties();
        try (InputStream inputStream = TransactionTypeProperties.class.getClassLoader()
                .getResourceAsStream("transactiontype.properties")) {
            properties.load(inputStream);
            return properties;
        } catch (Exception e) {
            LOG.error("Error while loading transactiontype.properties", e);
        }
        return properties;
    }

    /**
     * Returns the transaction type associated with this key
     * 
     * @param key
     * @return
     */
    public static String getValue(String transactionTypeCode) {
        return PROPS.getProperty(transactionTypeCode);
    }

}
