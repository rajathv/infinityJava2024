package com.temenos.dbx.party.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class PartyURLFinder {

    private static final Logger LOG = LogManager.getLogger(PartyURLFinder.class);
    private static Properties serviceURLProps = new Properties();

    static {
        try (InputStream inputStream = PartyURLFinder.class.getClassLoader()
                .getResourceAsStream("PartyServiceURLs.properties");) {
            serviceURLProps.load(inputStream);
        } catch (FileNotFoundException e) {
            LOG.info("error while reading properties file", e);
        } catch (IOException e) {
            LOG.info("error while reading properties file", e);
        }
    }
    
    public PartyURLFinder() {
        // TODO Auto-generated constructor stub
    }

    public static String getServiceUrl(String pathKey) {
        if (serviceURLProps.containsKey(pathKey)) {
            return serviceURLProps.getProperty(pathKey).trim();
        }
        return "";
    }
    
    
    public static String getServiceUrl(String pathKey, String party_id) {
        if (serviceURLProps.containsKey(pathKey)) {
            return serviceURLProps.getProperty(pathKey).trim().replace("{party_id}", party_id);
        }
        
        return "";
    }

}