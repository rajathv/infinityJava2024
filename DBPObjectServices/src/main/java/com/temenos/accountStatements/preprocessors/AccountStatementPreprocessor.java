package com.temenos.accountStatements.preprocessors;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.kony.task.datavalidation.DocumentValidationTask;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;

public class AccountStatementPreprocessor implements ObjectServicePreProcessor {

	@Override
	public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager, FabricRequestChain fabricReqChain)
			throws Exception {
		 Class<? extends ObjectProcessorTask>[] tasks = new Class[] { 
				 DocumentValidationTask.class};
	        if (ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks)) {
	            fabricReqChain.execute();
	        }
	}

}
