/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.postprocessor.alerts;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.objectserviceutils.EventsDispatcher;
import com.kony.utilities.ObjectServiceHelperMethods;
import com.konylabs.middleware.api.OperationData;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.temenos.infinity.tradesupplyfinance.config.TradeSupplyFinanceAlerts;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.kony.postprocessors.ObjectServicesConstants.*;
import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.PARAM_ALERT_DATA;
import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.PARAM_ALERT_NAME;

/**
 * @author k.meiyazhagan
 */
public class TradeSupplyFinanceAlertsProcessor implements ObjectProcessorTask {

    private static final Logger LOG = LogManager.getLogger(TradeSupplyFinanceAlertsProcessor.class);

    @Override
    public boolean process(FabricRequestManager requestManager, FabricResponseManager responseManager) {
        JsonObject responsePayload = new JsonObject();
        try {
            String enableEvents = ObjectServiceHelperMethods.getConfigurableParameters(PARAM_ENABLE_EVENTS, requestManager);
            responsePayload = responseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
            LOG.info("Response payload: " + responsePayload);
            if (StringUtils.isBlank(enableEvents)
                    || !StringUtils.equalsIgnoreCase(enableEvents, PARAM_TRUE)
                    || !responsePayload.has(PARAM_ALERT_DATA)) {
                LOG.info("Request failed or Alerts are Disabled");
                return true;
            }

            JsonObject customParams = JsonParser.parseString(responsePayload.get(PARAM_ALERT_DATA).getAsString()).getAsJsonObject();
            String alertName = customParams.get(PARAM_ALERT_NAME).getAsString();
            OperationData operation = requestManager.getServicesManager().getOperationData();

            EventsDispatcher.dispatch(requestManager, responseManager,
                    TradeSupplyFinanceAlerts.valueOf(alertName).getType(), TradeSupplyFinanceAlerts.valueOf(alertName).getSubType(),
                    StringUtils.join(operation.getServiceId(), "/operations/", operation.getObjectId(), "/", operation.getOperationId()),
                    PARAM_SID_EVENT_SUCCESS, null, HelperMethods.getCustomerIdFromSession(requestManager), customParams);

        } catch (Exception e) {
            LOG.error("Error occurred while processing TradeFinance alerts. ", e);
        } finally {
            if (responsePayload.has(PARAM_ALERT_DATA)) {
                responsePayload.remove(PARAM_ALERT_DATA);
                responseManager.getPayloadHandler().updatePayloadAsJson(responsePayload);
            }
        }

        return true;
    }
}
