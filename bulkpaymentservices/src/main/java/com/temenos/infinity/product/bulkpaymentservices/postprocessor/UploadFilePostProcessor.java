package com.temenos.infinity.product.bulkpaymentservices.postprocessor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.accounts.AccountsConstants;
import com.kony.dbx.BasePostProcessor;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class UploadFilePostProcessor extends BasePostProcessor implements AccountsConstants {

	private static final Logger logger = LogManager.getLogger(UploadFilePostProcessor.class);

	@Override
	public Result execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {		
       
		return null;
	}

}