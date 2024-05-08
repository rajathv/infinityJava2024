package com.kony.memorymgmt;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.ehcache.ResultCache;

public class MemoryManager {

    private static final Logger LOG = LogManager.getLogger(MemoryManager.class);

    private MemoryManager() {
    }

    static void save(FabricRequestManager fabricReqManager, String key, Object value) {

        try {
            ResultCache resultCache = null;
            SessionMap sessionData = (SessionMap) value;
            LOG.debug("saving cacheData {}", sessionData);
            resultCache = ServicesManagerHelper.getServicesManager().getResultCache();
            if (resultCache != null && StringUtils.isNotBlank(key) && null != sessionData
                    && StringUtils.isNotBlank(sessionData.toString())) {

                resultCache.insertIntoCache(key, sessionData.toString(), DBPUtilitiesConstants.DEFAULT_CACHE_TIME);
            }
        } catch (Exception e) {
            LOG.error("Exception occured while fetching ResultCache instance from Services Manager API", e);
        }

    }

    static Object retrieve(FabricRequestManager fabricReqManager, String key) {
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