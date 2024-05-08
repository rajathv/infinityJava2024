package com.kony.transaction.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;
import com.kony.postprocessors.SearchTransactionTypeObjectServicePostProcessor;
import com.kony.task.sessionmgmt.SaveTransactionsInSessionTask;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePostProcessor;
import com.temenos.dbx.object.businessdelegate.impl.ViewTransactionActionValidationBDImpl;

/**
 * 
 * Used in : get
 *
 */
public class SearchTransactionsPostProcessor implements ObjectServicePostProcessor {
	private static final Logger LOG = LogManager.getLogger(SearchTransactionsPostProcessor.class);

	@SuppressWarnings("unchecked")
	@Override
	public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager)
			throws Exception {
		Class<? extends ObjectProcessorTask>[] tasks = new Class[] { ViewTransactionActionValidationBDImpl.class,
				SaveTransactionsInSessionTask.class, SearchTransactionTypeObjectServicePostProcessor.class };
		ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks);
	}

}
