package com.infinity.dbx.temenos.user;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.TemenosPreLoginBasePreProcessor;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetUserIdBySSNPreProcessor extends TemenosPreLoginBasePreProcessor{

	private static final Logger logger = LogManager.getLogger(GetUserIdBySSNPreProcessor.class);
	@Override
	public boolean execute(HashMap params, DataControllerRequest request, DataControllerResponse response, Result result)
			throws Exception {
		
		super.execute(params, request, response, result);
		logger.error("GetUserIdBySSNPreProcessor");
		String taxId = params.get("Taxid") != null ? params.get("Taxid").toString() : "";
		logger.error("Taxid " + taxId);
		if("".equalsIgnoreCase(taxId)) {
			logger.error("GetUserIdBySSNPreProcessor false");
			result.addOpstatusParam(0);
			result.addHttpStatusCodeParam(200);
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}

}
