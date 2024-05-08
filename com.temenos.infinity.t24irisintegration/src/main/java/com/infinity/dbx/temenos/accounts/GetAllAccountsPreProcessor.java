package com.infinity.dbx.temenos.accounts;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.TemenosPreLoginBasePreProcessor;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetAllAccountsPreProcessor extends TemenosPreLoginBasePreProcessor{

	private static final Logger logger = LogManager
			.getLogger(GetAllAccountsPreProcessor.class);
	@Override
	public boolean execute(HashMap params, DataControllerRequest request, DataControllerResponse response, Result result)
			throws Exception {
		
		super.execute(params, request, response, result);
		logger.error("GetAllAccountsPreProcessor params " + params.toString());
		String membership_id = params.get(AccountsConstants.MEMBERSHIP_ID) != null ? params.get(AccountsConstants.MEMBERSHIP_ID).toString() : "";
		if(!"".equalsIgnoreCase(membership_id)) {
			params.put(USER_ID, membership_id);
		}
		
		String customer_id = request.getParameter(AccountsConstants.CUSTOMER_ID) != null ? request.getParameter(AccountsConstants.CUSTOMER_ID).toString() : "";
		if(!"".equalsIgnoreCase(customer_id)) {
			params.put(USER_ID, customer_id);
		}
		
		String account_id = params.get(AccountsConstants.ACCOUNT_ID) != null ? params.get(AccountsConstants.ACCOUNT_ID).toString() : "";
		if(!"".equalsIgnoreCase(account_id)) {
			result.addOpstatusParam(0);
			result.addHttpStatusCodeParam(200);
			return Boolean.FALSE;
		}
		logger.error("Params true " + params.toString());
		return Boolean.TRUE;
	}
}
