package com.kony.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;

public class CardHelper {
    private static final Logger LOG = LogManager.getLogger(CardHelper.class);

    private CardHelper() {
    }

    public static void reloadCardsIntoSession(FabricRequestManager fabricRequestManager) {
        try {
            ServiceCallHelper.invokeServiceAndGetJson(fabricRequestManager, null, null, URLConstants.CARDS_OS_GET);
        } catch (Exception e) {
            LOG.error("Error while reloading cards:", e);
        }
    }
}
