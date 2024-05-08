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
import com.temenos.infinity.tradesupplyfinance.preprocessor.validations.ReceivableSingleBillValidation;

/**
 * @author k.meiyazhagan
 */
public class ReceivableSingleBillPreProcessor implements ObjectServicePreProcessor {
    @Override
    public void execute(FabricRequestManager reqManager, FabricResponseManager resManager, FabricRequestChain reqChain) throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[]{ReceivableSingleBillValidation.class};
        if (ObjectProcessorTaskManager.invokeAll(reqManager, resManager, tasks)) {
            reqChain.execute();
        }
    }
}
