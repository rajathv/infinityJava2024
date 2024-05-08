package com.temenos.dbx.bulkpaymentservices.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class BulkPaymentsPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(BulkPaymentsPostProcessor.class);
	
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response) {
		try {
			result.addParam(new Param("recordId", request.getParameter("recordId")));
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking post processor for fetchBulkPaymentRecordbyId: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		return result;
	}
}