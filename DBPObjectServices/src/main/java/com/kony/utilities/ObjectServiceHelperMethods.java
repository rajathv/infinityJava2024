/**
 * 
 */
package com.kony.utilities;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.auth.DBPAuthorizationHelper;
import com.dbp.core.fabric.extn.DBPServiceExecutor;
import com.google.gson.JsonObject;
import com.kony.objectserviceutils.EventsDispatcher;
import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.temenos.dbx.product.utils.ThreadExecutor;

/**
 * @author KH2259
 *
 */
public class ObjectServiceHelperMethods implements Callable<CallableUtil> {
    /**
     * 
     * @param key
     * @param requestInstance
     * @return
     */
    FabricRequestManager requestManager;
    FabricResponseManager responseManager = null;
    String eventType;
    String eventSubType;
    String producer;
    String statusId;
    String account;
    String customerId;
    JsonObject customParams;
    JsonObject responsePayload;
    private static final Logger logger = LogManager.getLogger(ObjectServiceHelperMethods.class);

    public ObjectServiceHelperMethods(FabricRequestManager requestManager, FabricResponseManager responseManager,
            String eventType, String eventSubType, String producer, String statusId, String account, String customerId,
            JsonObject customParams) {

        this.requestManager = requestManager;
        this.responseManager = responseManager;
        this.eventType = eventType;
        this.eventSubType = eventSubType;
        this.producer = producer;
        this.statusId = statusId;
        this.account = account;
        this.customerId = customerId;
        this.customParams = customParams;
    }

    public ObjectServiceHelperMethods(FabricRequestManager requestManager, JsonObject responsePayload, String eventType,
            String eventSubType, String producer, String statusId, String account, String customerId,
            JsonObject customParams) {

        this.requestManager = requestManager;
        this.responsePayload = responsePayload;
        this.eventType = eventType;
        this.eventSubType = eventSubType;
        this.producer = producer;
        this.statusId = statusId;
        this.account = account;
        this.customerId = customerId;
        this.customParams = customParams;
    }

    public static String getConfigurableParameters(String key, FabricRequestManager requestInstance) {
        try {
            ServicesManager serviceManager = requestInstance.getServicesManager();
            ConfigurableParametersHelper configurableParametersHelper = serviceManager
                    .getConfigurableParametersHelper();
            return configurableParametersHelper.getServerProperty(key);
        } catch (Exception are) {
            logger.error("Error occured while fetching environment configuration", are);
        }

        return "";
    }

    // Code to check whether a json has given key or not
    public static boolean hasKey(JsonObject object, String key) {
        return object.has(key);
    }

    public static void execute(ObjectServiceHelperMethods call) throws InterruptedException, ExecutionException {
        try {
            ThreadExecutor.getExecutor(call.requestManager).execute(call);
        }
        catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public CallableUtil call() throws Exception {

        CallableUtil cUtil = new CallableUtil(this.responseManager, this.requestManager);
        if (responseManager != null) {
            EventsDispatcher.dispatch(requestManager, responseManager, eventType, eventSubType, producer, statusId,
                    null, customerId, customParams);
        } else
            EventsDispatcher.dispatch(requestManager, responsePayload, eventType, eventSubType, producer, statusId,
                    account, customerId, customParams);

        return cUtil;
    }

}