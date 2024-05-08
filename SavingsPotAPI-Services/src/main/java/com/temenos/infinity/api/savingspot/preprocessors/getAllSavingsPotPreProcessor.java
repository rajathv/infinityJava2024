package com.temenos.infinity.api.savingspot.preprocessors;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;
import com.temenos.infinity.api.savingspot.task.datavalidation.SavingsPotFundingAccountValidationTask;

public class getAllSavingsPotPreProcessor implements ObjectServicePreProcessor{

	@SuppressWarnings("unchecked")
	@Override
	public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager,
			FabricRequestChain fabricRequestChain) throws Exception {
		
		Class<? extends ObjectProcessorTask>[] tasks = new Class[] {SavingsPotFundingAccountValidationTask.class };
        if (ObjectProcessorTaskManager.invokeAll(fabricRequestManager, fabricResponseManager,
                tasks)){
        	fabricRequestChain.execute();
        }  	    
  }
}
