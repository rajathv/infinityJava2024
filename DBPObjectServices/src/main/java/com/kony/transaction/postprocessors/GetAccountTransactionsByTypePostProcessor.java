package com.kony.transaction.postprocessors;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.kony.postprocessors.GetAccountTransactionByTypeObjectServicePostProcessor;
import com.kony.task.sessionmgmt.SaveTransactionsInSessionTask;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;
import com.temenos.dbx.object.businessdelegate.impl.ViewTransactionActionValidationBDImpl;

/**
 * used in : 
 */

public class GetAccountTransactionsByTypePostProcessor implements ObjectServicePostProcessor {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager) throws Exception {
		Class<? extends ObjectProcessorTask>[] tasks = new Class[] {ViewTransactionActionValidationBDImpl.class,
				SaveTransactionsInSessionTask.class, GetAccountTransactionByTypeObjectServicePostProcessor.class};
		ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks);
	}

}
