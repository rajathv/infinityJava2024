package com.kony.dbputilities.memorymanagement;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.ehcache.ResultCache;

public class MemoryManager {

    private MemoryManager() {

    }

    private static final Logger LOG = LogManager.getLogger(MemoryManager.class);

    public static void saveIntoCache(String key, String value, int cacheTime) {

        ResultCache resultCache = null;
        try {
            resultCache = ServicesManagerHelper.getServicesManager().getResultCache();
        } catch (Exception e) {
            LOG.error("Exception occured while fetching ResultCache instance from Services Manager API", e);
        }

        if (resultCache != null && StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {

            resultCache.insertIntoCache(key, value, cacheTime);
        }

    }
    
    public static void saveIntoCache(String key, byte[] value, int cacheTime) {

        ResultCache resultCache = null;
        try {
            resultCache = ServicesManagerHelper.getServicesManager().getResultCache();
        } catch (Exception e) {
            LOG.error("Exception occured while fetching ResultCache instance from Services Manager API", e);
        }

        if (resultCache != null && StringUtils.isNotBlank(key) && value!=null) {
            resultCache.insertIntoCache(key, value, cacheTime);
        }

    }

    public static void saveIntoCache(String key, String value) {
        int cacheTime = DBPUtilitiesConstants.DEFAULT_CACHE_TIME;
        ResultCache resultCache = null;
        try {
            resultCache = ServicesManagerHelper.getServicesManager().getResultCache();
        } catch (Exception e) {
            LOG.error("Exception occured while fetching ResultCache instance from Services Manager API", e);
        }

        if (resultCache != null && StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {

            resultCache.insertIntoCache(key, value, cacheTime);
        }

    }

    public static void saveIntoCache(String key, byte[] value) {
        int cacheTime = DBPUtilitiesConstants.DEFAULT_CACHE_TIME;
        ResultCache resultCache = null;
        try {
            resultCache = ServicesManagerHelper.getServicesManager().getResultCache();
        } catch (Exception e) {
            LOG.error("Exception occured while fetching ResultCache instance from Services Manager API", e);
        }

        if (resultCache != null && StringUtils.isNotBlank(key)) {

            resultCache.insertIntoCache(key, value, cacheTime);
        }

    }

    public static Object getFromCache(String key) {
        ResultCache resultCache = null;
        try {
            resultCache = ServicesManagerHelper.getServicesManager().getResultCache();
        } catch (Exception e) {
            LOG.error("Exception occured while fetching ResultCache instance from Services Manager API", e);
        }
        if (resultCache != null && StringUtils.isNotBlank(key)) {

            return resultCache.retrieveFromCache(key);
        }

        return new Object();

    }

    public static void removeFromCache(String key) {
        ResultCache resultCache = null;
        try {
            resultCache = ServicesManagerHelper.getServicesManager().getResultCache();
        } catch (Exception e) {
            LOG.error("Exception occured while fetching ResultCache instance from Services Manager API", e);
        }
        if (resultCache != null && StringUtils.isNotBlank(key)) {
            resultCache.removeFromCache(key);
        }

    }

}