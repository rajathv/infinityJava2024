package com.kony.achobjects.preprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;

public class ACHTemplateExecutionPreProcessor implements ObjectServicePreProcessor {

    private static final Logger LOG = LogManager.getLogger(ACHTemplateExecutionPreProcessor.class);

    @SuppressWarnings("unchecked")
    @Override
    public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager,
            FabricRequestChain fabricRequestChain) throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[] { ACHTransactionsCompanyValidationTask.class,
                ACHTransactionsAccountValidationTask.class, ACHTransactionsMFAPreProcessorTask.class };

        LOG.debug("Entered into ACHTemplateExecutionPreProcessor");
        if (ObjectProcessorTaskManager.invokeAll(fabricRequestManager, fabricResponseManager, tasks)) {
            LOG.debug("All preprocessors are passed");
            fabricRequestChain.execute();
        }
    }

}
