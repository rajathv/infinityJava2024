package com.kony.dbputilities.customersecurityservices.preprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.mfa.preprocessors.BulkWireTransactionsMFAPreProcessor;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;

public class CreateBulkWireTransferObjectServicePreProcessor extends CreateTransferObjectServicePreProcessor{
	private static final Logger logger = LogManager.getLogger(CreateBulkWireTransferObjectServicePreProcessor.class);

	@Override
	public boolean process(FabricRequestManager requestManager, FabricResponseManager responseManager)
			throws Exception {
		boolean status = true;
		try {
			// Triggered Process Block
			status = new BulkWireTransactionsMFAPreProcessor().process(requestManager, responseManager);
			logger.error(status);
			execute(requestManager, responseManager, null);
		} catch (Exception e) {
			logger.error("exception occured in MFA Preprocessor", e);

		}
		return status;
	}
}
