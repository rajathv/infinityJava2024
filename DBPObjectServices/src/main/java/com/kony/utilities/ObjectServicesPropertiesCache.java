package com.kony.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObjectServicesPropertiesCache {
    private final Properties configprop = new Properties();
    private static final Logger logger = LogManager.getLogger(ObjectServicesPropertiesCache.class);

    private ObjectServicesPropertiesCache() {
        // Private constructor to restrict new instances
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("objectservice.properties");

        try {
            configprop.load(in);
        } catch (IOException e) {
            logger.debug("Exception occured:" + e);
        }
		finally {
	        	if (in!=null) {
	        		try {
	        			in.close();			
	        		}
	        		catch(Exception e)
	        		{
	        			logger.error(e);
	        		}
	        	}
		}
    }

    // Bill Pugh Solution for singleton pattern
    private static class LazyHolder {
        private static final ObjectServicesPropertiesCache instance = new ObjectServicesPropertiesCache();
    }

    public static ObjectServicesPropertiesCache getInstance() {
        return LazyHolder.instance;
    }

    public String getProperty(String key) {
        return configprop.getProperty(key);
    }

    public Set<String> getAllPropertyNames() {
        return configprop.stringPropertyNames();
    }

    public boolean containsKey(String key) {
        return configprop.containsKey(key);
    }

}