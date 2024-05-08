package com.kony.consent.preprocessors;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.kony.dbputilities.mfa.preprocessors.TransactionsMFAPreProcessor;
import com.kony.task.datavalidation.AddOrderManageMentRequestBody;
import com.kony.task.datavalidation.UpdatePSDConsentOrderManagementTask;
import com.kony.task.datavalidation.UpdatePSDConsentServiceValidation;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;



public class UpdatePSDConsentPreProcessor implements ObjectServicePreProcessor {

    @SuppressWarnings("unchecked")
	@Override
    public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager,
            FabricRequestChain fabricReqChain) throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[] {
        		UpdatePSDConsentServiceValidation.class,
        		TransactionsMFAPreProcessor.class,
        		AddOrderManageMentRequestBody.class,
        		UpdatePSDConsentOrderManagementTask.class
        		};
        if (ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks)) {
            fabricReqChain.execute();
        }
    }

}