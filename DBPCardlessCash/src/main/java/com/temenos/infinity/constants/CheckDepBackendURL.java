package com.temenos.infinity.constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.URLFinder;
/**
 * Contains method to get transactionBackend service URL values
 * @author eivanov
 *
 */
public class CheckDepBackendURL{

	private static final Logger LOG = LogManager.getLogger(CheckDepBackendURL.class);
    private static Properties urlProps = new Properties();
    private static String PAYMENT_BACKEND_MOCK = "MOCK";
    
    /*
     * This method loads the properties based on the backend
     */
    public void loadTransactionBackendURLs() {
        String Payment_Backend = EnvironmentConfigurationsHandler.getValue("PAYMENT_BACKEND");
        if (StringUtils.isBlank(Payment_Backend))
            Payment_Backend = PAYMENT_BACKEND_MOCK;
        String fileName = "config/" + Payment_Backend + "_TransactionBackendURLs.properties";
        try (InputStream inputStream = URLFinder.class.getClassLoader().getResourceAsStream(fileName);) {
            urlProps.load(inputStream);
        } catch (FileNotFoundException e) {
            LOG.info("error while reading properties file", e);
        } catch (IOException e) {
            LOG.info("error while reading properties file", e);
        }
    }
/**
 * method to get serviceId for given service key
 * @author kh2624
 * @param serviceKey
 * @return serviceId
 *
 */
public static final String getBackendURL (String serviceId) {
	  if (urlProps.containsKey(serviceId)) {
          return urlProps.getProperty(serviceId).trim();
      }
		return serviceId;
    }
}