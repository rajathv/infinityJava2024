package com.temenos.infinity.api.docmanagement.acctstatement.postprocessors;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.kony.task.datavalidation.SaveStatementsInSessionTask;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;

public class GetStatementsPostProcessor implements ObjectServicePostProcessor{

	@Override
	public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager) throws Exception {
		Class<? extends ObjectProcessorTask>[] tasks = new Class[] { SaveStatementsInSessionTask.class};
		ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks);
	}

}
