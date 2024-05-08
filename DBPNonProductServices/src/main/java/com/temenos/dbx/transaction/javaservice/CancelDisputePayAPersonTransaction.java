package com.temenos.dbx.transaction.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.transservices.DeleteTransaction;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CancelDisputePayAPersonTransaction implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(CancelDisputePayAPersonTransaction.class);


	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = new Result();
		try {
			result = (Result) new DeleteTransaction().invoke("DeleteTransaction", inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke : "+e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
	}


}
