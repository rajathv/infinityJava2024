package com.infinity.dbx.temenos.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.kony.dbx.BasePostProcessor;
import com.kony.dbx.util.CommonUtils;
import com.kony.dbx.util.Constants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class UpdateUserDetailsPostProcessor extends BasePostProcessor implements TemenosConstants, UserConstants, Constants {

	private static final Logger logger = LogManager.getLogger(UpdateUserDetailsPostProcessor.class);

	@Override
	public Result execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		if(!"".equalsIgnoreCase(CommonUtils.getParamValue(result, "errmsg"))) {
			result.addStringParam("Status", "Operation failed");
			result.addStringParam("success", "failed");
			result.addOpstatusParam(8009);
			result.addHttpStatusCodeParam(400);
		}
		return result;
	}
}
