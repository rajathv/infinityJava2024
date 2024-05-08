/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.preprocessor;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;
import com.temenos.infinity.tradesupplyfinance.preprocessor.validations.ProcessReceivableSingleBillsTask;

/**
 * @author k.meiyazhagan
 */
public class CreateReceivableSingleBillsPreProcessor implements ObjectServicePreProcessor {
    @Override
    public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager, FabricRequestChain requestChain) throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[]{ProcessReceivableSingleBillsTask.class, };
        if (ObjectProcessorTaskManager.invokeAll(requestManager, responseManager, tasks)) {
            requestChain.execute();
        }
    }
}
