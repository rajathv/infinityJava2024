/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.postprocessor;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;
import com.temenos.infinity.tradefinanceservices.postprocessor.alerts.TradeFinanceAlertsProcessor;

/**
 * @author k.meiyazhagan
 */
public class TradeFinanceAlertsPostProcessor implements ObjectServicePostProcessor {
    @Override
    public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager) throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[]{TradeFinanceAlertsProcessor.class};
        ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks);
    }
}
