package com.temenos.infinity.api.chequemanagement.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.ehcache.ResultCache;
import com.temenos.infinity.api.chequemanagement.dto.SessionMap;

public class MemoryManagerUtils {

    private static final Logger LOG = LogManager.getLogger(MemoryManagerUtils.class);

    private MemoryManagerUtils() {
    }
    
    
    static Object retrieve(String key) {
        ResultCache resultCache = null;
        try {
            resultCache = ServicesManagerHelper.getServicesManager().getResultCache();
            if (resultCache != null && StringUtils.isNotBlank(key)) {
                String value = (String) resultCache.retrieveFromCache(key);
                LOG.debug("retrieving cacheData {}", value);
                SessionMap sessionData = new SessionMap();
                sessionData.setData(value);
                return sessionData; 
            }
        } catch (Exception e) {
            LOG.error("Exception occured while fetching ResultCache instance from Services Manager API", e);
        }
        return null;

    }

}