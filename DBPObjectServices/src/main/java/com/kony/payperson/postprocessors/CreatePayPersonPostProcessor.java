package com.kony.payperson.postprocessors;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.kony.postprocessors.AddP2PRecipientObjectServicePostProcessor;
import com.kony.task.datavalidation.PayPersonReloadTask;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

/**
 * 
 * Used in: create
 *
 */
public class CreatePayPersonPostProcessor implements ObjectServicePostProcessor {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager)
            throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[] { PayPersonReloadTask.class,
                AddP2PRecipientObjectServicePostProcessor.class };
        ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks);
    }

}
