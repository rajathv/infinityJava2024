package com.kony.savingspot.preprocessors;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.kony.task.datavalidation.SavingsPotClosedValidationTask;
import com.kony.task.datavalidation.SavingsPotValidationTask;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;

public class updateSavingsPotPreProcessor implements ObjectServicePreProcessor{

	@SuppressWarnings("unchecked")
	@Override
	public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager,
			FabricRequestChain fabricRequestChain) throws Exception {
		
		Class<? extends ObjectProcessorTask>[] tasks = new Class[] { SavingsPotValidationTask.class,SavingsPotClosedValidationTask.class };
        if (ObjectProcessorTaskManager.invokeAll(fabricRequestManager, fabricResponseManager,
                tasks)){
        	fabricRequestChain.execute();
        }      
  }
}

