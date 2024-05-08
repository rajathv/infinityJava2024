package com.kony.dbputilities.customersecurityservices.preprocessor;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.kony.dbputilities.mfa.preprocessors.PostLoginMFAPreProcessor;
import com.kony.task.datavalidation.UpdateFeatureActionIdTask;
import com.kony.task.datavalidation.ValidateUpdateUserDetailsTask;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;
import com.temenos.dbx.object.businessdelegate.impl.ActionValidationBDImpl;

public class ProfileUpdatePreProcessor implements ObjectServicePreProcessor {

    @SuppressWarnings("unchecked")
    @Override
    public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager,
            FabricRequestChain fabricReqChain) throws Exception {
        Class<? extends ObjectProcessorTask>[] tasks = new Class[] {
                UpdateFeatureActionIdTask.class,
                ActionValidationBDImpl.class,
                ValidateUpdateUserDetailsTask.class,
                PostLoginMFAPreProcessor.class };
        if (ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks)) {
            fabricReqChain.execute();
        }
    }
}