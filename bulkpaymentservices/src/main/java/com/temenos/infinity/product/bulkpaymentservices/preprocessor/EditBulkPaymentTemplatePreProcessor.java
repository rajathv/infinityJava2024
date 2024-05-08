package com.temenos.infinity.product.bulkpaymentservices.preprocessor;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.dbp.core.object.task.ObjectProcessorTaskManager;

import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;
import com.temenos.infinity.product.accountValidation.CreateTransactionActionValidationBDImpl;

public class EditBulkPaymentTemplatePreProcessor implements ObjectServicePreProcessor {

	@SuppressWarnings("unchecked")
	public void execute(FabricRequestManager fabricReqManager, FabricResponseManager fabricResManager,
			FabricRequestChain fabricReqChain) throws Exception {

		Class<? extends ObjectProcessorTask>[] tasks = new Class[] { CreateTransactionActionValidationBDImpl.class };
		if (ObjectProcessorTaskManager.invokeAll(fabricReqManager, fabricResManager, tasks)) {
			fabricReqChain.execute();
		}
	}
}
