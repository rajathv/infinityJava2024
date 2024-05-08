package com.temenos.infinity.api.srmstransactions.preprocessor;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;
import com.temenos.infinity.api.srmstransactions.utils.GetValidIBANMockOperation;

public class GetValidIBANPreProcessor implements ObjectServicePreProcessor{
	  public void execute(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager, FabricRequestChain fabricRequestChain) throws Exception {

	        Class<? extends ObjectProcessorTask>[] tasks = new Class[]{GetValidIBANMockOperation.class};

	        if (ObjectProcessorTaskManager.invokeAll(fabricRequestManager, fabricResponseManager, tasks)) {
	            fabricRequestChain.execute();
	        }
	    }
}