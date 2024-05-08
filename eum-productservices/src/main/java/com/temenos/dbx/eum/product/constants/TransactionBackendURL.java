package com.temenos.dbx.eum.product.constants;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.URLFinder;
import com.temenos.dbx.eum.product.constants.TransactionBackendURL;
/**
 * Contains method to get transactionBackend service URL values
 * @author kh2624
 *
 */
public class TransactionBackendURL{

	private static final Logger LOG = LogManager.getLogger(TransactionBackendURL.class);
    private static Properties urlProps = new Properties();

    static {
        try (InputStream inputStream = URLFinder.class.getClassLoader()
                .getResourceAsStream("TransactionBackendURLs.properties");) {
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