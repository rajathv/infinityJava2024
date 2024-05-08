package com.infinity.dbx.temenos.transfers;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbx.BasePostProcessor;
import com.kony.dbx.util.CommonUtils;
import com.kony.dbx.util.Constants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class StandingOrderPostProcessor extends BasePostProcessor {
	private static final Logger logger = LogManager
			.getLogger(com.infinity.dbx.temenos.transfers.StandingOrderPostProcessor.class);

	@Override
	public Result execute(Result result, DataControllerRequest request, DataControllerResponse response) {
		try {
			result = super.execute(result, request, response);
			String status = result.getParamValueByName(TransferConstants.PARAM_STATUS);
			String errmsg = result.getParamValueByName(Constants.PARAM_ERR_MSG);
			if((StringUtils.isNotEmpty(status) && TransferConstants.STATUS_FAILED.equalsIgnoreCase(status)) || StringUtils.isNotBlank(errmsg) ) {
				Result res = TransferUtils.setErrorDetails(result);
				return res;
			}

		} catch (Exception e) {
			logger.error("Exception occured in Standing Order Post Processor:" + e);
			Result errorResult = new Result();
			CommonUtils.setOpStatusError(errorResult);
			CommonUtils.setErrMsg(errorResult, e.getMessage());
			return errorResult;
		}
		return result;
	}
}
