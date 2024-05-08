package com.infinity.dbx.temenos.transfers;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.TemenosBasePreProcessor;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CreateOneTimeTransferPreProcessor extends TemenosBasePreProcessor {
	private static final Logger logger = LogManager.getLogger(com.infinity.dbx.temenos.transfers.CreateOneTimeTransferPreProcessor.class);
	@SuppressWarnings("rawtypes")
	public boolean execute(HashMap params, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		try {
			super.execute(params, request);
	} catch (Exception e) {
		logger.error("Exception occurred in Create OneTimeTransfer PreProcessor:" + e);
		return false;
	}
	return Boolean.TRUE;
	}
}
