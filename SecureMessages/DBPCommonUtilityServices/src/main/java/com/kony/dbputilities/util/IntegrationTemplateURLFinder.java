package com.kony.dbputilities.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegrationTemplateURLFinder {

    private static final Logger LOG = LogManager.getLogger(IntegrationTemplateURLFinder.class);
    private static Properties urlProps = new Properties();
    public static boolean isIntegrated = false;

    static {
        try (InputStream inputStream = URLFinder.class.getClassLoader()
                .getResourceAsStream("DBXIntegrationURLs.properties");) {
            urlProps.load(inputStream);
            if (urlProps.size() > 0
                    && "true".equalsIgnoreCase(urlProps.getProperty(DBPUtilitiesConstants.IS_INTEGRATED))) {
                isIntegrated = true;
            } else {
                isIntegrated = false;
            }

        } catch (FileNotFoundException e) {
            LOG.info("error while reading properties file", e);
        } catch (IOException e) {
            LOG.info("error while reading properties file", e);
        } catch (Exception e) {
            LOG.info("error while reading properties file", e);
        }

    }

    public static final String getBackendURL(String serviceId) {
        if (urlProps.containsKey(serviceId)) {
            return urlProps.getProperty(serviceId).trim();
        }
        return "";
    }

    public static String getAccountTypeName(String productId) {

        String accountTypePropertyFilename =
                IntegrationTemplateURLFinder.getBackendURL(DBPUtilitiesConstants.ACCOUNTTYPE_PROPERTIES_FILE_NAME);
        return URLFinder.getPropertyValue(productId,
                accountTypePropertyFilename);
    }
}