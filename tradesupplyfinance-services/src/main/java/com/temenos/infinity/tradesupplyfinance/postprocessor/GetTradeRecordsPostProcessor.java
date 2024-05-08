/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.postprocessor;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;
import com.temenos.infinity.tradesupplyfinance.sessionmanagement.SaveMessageEligibleRecordsInSession;

/**
 * @author k.meiyazhagan
 */
public class GetTradeRecordsPostProcessor implements ObjectServicePostProcessor {
    @Override
    public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[]{SaveMessageEligibleRecordsInSession.class};
        ObjectProcessorTaskManager.invokeAll(fabricRequestManager, fabricResponseManager, tasks);
    }
}
