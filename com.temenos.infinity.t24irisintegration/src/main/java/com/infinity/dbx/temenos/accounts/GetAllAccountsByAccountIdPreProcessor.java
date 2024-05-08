package com.infinity.dbx.temenos.accounts;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.TemenosPreLoginBasePreProcessor;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetAllAccountsByAccountIdPreProcessor extends TemenosPreLoginBasePreProcessor{

	private static final Logger logger = LogManager.getLogger(GetAllAccountsByAccountIdPreProcessor.class);
	@Override
	public boolean execute(HashMap params, DataControllerRequest request, DataControllerResponse response, Result result)
			throws Exception {
		
		super.execute(params, request, response, result);
		logger.error("GetAllAccountsByAccountIdPreProcessor");
		String account_id = params.get("Account_id") != null ? params.get("Account_id").toString() : "";
		if(!"".equalsIgnoreCase(account_id)) {
			logger.error("GetAllAccountsByAccountIdPreProcessor return true");
			return Boolean.TRUE;
		}
		
		result.addOpstatusParam(0);
		result.addHttpStatusCodeParam(200);
		return Boolean.FALSE;
	}

}
