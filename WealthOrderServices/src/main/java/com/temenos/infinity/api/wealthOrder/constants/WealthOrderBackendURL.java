/**
 * 
 */
package com.temenos.infinity.api.wealthOrder.constants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import java.util.Properties;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.kony.dbputilities.util.URLFinder;
import com.konylabs.middleware.controller.DataControllerRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author muthukumarv
 *
 */
public class WealthOrderBackendURL {

	private static final Logger LOG = LogManager.getLogger(WealthOrderBackendURL.class);

    private static Properties urlProps = new Properties();
    private static Properties urlPropsMkt = new Properties();
    private static DataControllerRequest request = null;
    
    static {
    	
    	String wealthCore =EnvironmentConfigurationsHandler.getValue(TemenosConstants.INF_WLTH_CORE,
    			request);
    	if(wealthCore.equals("T24")) {
    		try (InputStream inputStream = URLFinder.class.getClassLoader()
                    .getResourceAsStream("OrderT24BackendURLs.properties");) {
                urlProps.load(inputStream);
            } catch (FileNotFoundException e) {
                LOG.info("error while reading properties file", e);
            } catch (IOException e) {
                LOG.info("error while reading properties file", e);
            }
    	}
    	else if (wealthCore.equals("T24,Refinitiv"))
    	{
    		try (InputStream inputStream = URLFinder.class.getClassLoader()
                    .getResourceAsStream("OrderT24BackendURLs.properties");) {
                urlProps.load(inputStream);
            } catch (FileNotFoundException e) {
                LOG.info("error while reading properties file", e);
            } catch (IOException e) {
                LOG.info("error while reading properties file", e);
            }
    		try (InputStream inputStreamMkt = URLFinder.class.getClassLoader()
                    .getResourceAsStream("OrderRefBackendURLs.properties");) {
    			urlPropsMkt.load(inputStreamMkt);
            } catch (FileNotFoundException e) {
                LOG.info("error while reading properties file", e);
            } catch (IOException e) {
                LOG.info("error while reading properties file", e);
            }
    		
    	}
    	else if(wealthCore.equals("TAP")) {
    		try (InputStream inputStream = URLFinder.class.getClassLoader()
                    .getResourceAsStream("OrderTAPBackendURLs.properties");) {
                urlProps.load(inputStream);
            } catch (FileNotFoundException e) {
                LOG.info("error while reading properties file", e);
            } catch (IOException e) {
                LOG.info("error while reading properties file", e);
            }
    	}
    	else if (wealthCore.equals("TAP,Refinitiv"))
    	{
    		try (InputStream inputStream = URLFinder.class.getClassLoader()
                    .getResourceAsStream("OrderTAPBackendURLs.properties");) {
                urlProps.load(inputStream);
            } catch (FileNotFoundException e) {
                LOG.info("error while reading properties file", e);
            } catch (IOException e) {
                LOG.info("error while reading properties file", e);
            }
    		try (InputStream inputStreamMkt = URLFinder.class.getClassLoader()
                    .getResourceAsStream("OrderRefBackendURLs.properties");) {
    			urlPropsMkt.load(inputStreamMkt);
            } catch (FileNotFoundException e) {
                LOG.info("error while reading properties file", e);
            } catch (IOException e) {
                LOG.info("error while reading properties file", e);
            }
    		
    	}
    	else if(wealthCore.equals("Mock")) {
    		try (InputStream inputStream = URLFinder.class.getClassLoader()
                    .getResourceAsStream("OrderMockBackendURLs.properties");) {
                urlProps.load(inputStream);
            } catch (FileNotFoundException e) {
                LOG.info("error while reading properties file", e);
            } catch (IOException e) {
                LOG.info("error while reading properties file", e);
            }
    	}
    	else if (wealthCore.equals("Mock,Refinitiv"))
    	{
    		try (InputStream inputStream = URLFinder.class.getClassLoader()
                    .getResourceAsStream("OrderMockBackendURLs.properties");) {
                urlProps.load(inputStream);
            } catch (FileNotFoundException e) {
                LOG.info("error while reading properties file", e);
            } catch (IOException e) {
                LOG.info("error while reading properties file", e);
            }
    		try (InputStream inputStreamMkt = URLFinder.class.getClassLoader()
                    .getResourceAsStream("OrderRefBackendURLs.properties");) {
    			urlPropsMkt.load(inputStreamMkt);
            } catch (FileNotFoundException e) {
                LOG.info("error while reading properties file", e);
            } catch (IOException e) {
                LOG.info("error while reading properties file", e);
            }
    		
    	}
    	
        
    }
/**
 * method to get PortfolioServiceId for given service key
 * @author kh2624
 * @param serviceKey
 * @return PortfolioServiceId
 *
 */
public static final String getBackendURL (String PortfolioServiceId) {
	  if (urlProps.containsKey(PortfolioServiceId)) {
          return urlProps.getProperty(PortfolioServiceId).trim();
      }
	  else if (urlPropsMkt.containsKey(PortfolioServiceId)) {
          return urlPropsMkt.getProperty(PortfolioServiceId).trim();
      }
		return PortfolioServiceId;
    }

}