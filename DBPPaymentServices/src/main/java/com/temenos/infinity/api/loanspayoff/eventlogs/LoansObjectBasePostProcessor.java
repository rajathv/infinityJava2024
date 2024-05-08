package com.temenos.infinity.api.loanspayoff.eventlogs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class LoansObjectBasePostProcessor implements ObjectServicePostProcessor {
    private static final Logger LOG = LogManager.getLogger(LoansObjectBasePostProcessor.class);

    @SuppressWarnings("unchecked")
    @Override
    public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager)
            throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[] { EventLogPostProcessorTask.class };
        ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks);
    }
}
