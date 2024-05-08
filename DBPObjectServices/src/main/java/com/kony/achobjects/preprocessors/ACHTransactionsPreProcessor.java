package com.kony.achobjects.preprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;

public class ACHTransactionsPreProcessor implements ObjectServicePreProcessor {

    private static final Logger LOG = LogManager.getLogger(ACHTransactionsPreProcessor.class);

    @SuppressWarnings("unchecked")
    @Override
    public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager,
            FabricRequestChain fabricRequestChain) throws Exception {

        LOG.debug("Entered into ACHTransactionsPreProcessor");

        Class<? extends ObjectProcessorTask>[] tasks = new Class[] { ACHTransactionsCompanyValidationTask.class,
                ACHTransactionsAccountValidationTask.class, ACHTransactionsMFAPreProcessorTask.class };

        if (ObjectProcessorTaskManager.invokeAll(fabricRequestManager, fabricResponseManager, tasks)) {
            fabricRequestChain.execute();
        }

    }

}
