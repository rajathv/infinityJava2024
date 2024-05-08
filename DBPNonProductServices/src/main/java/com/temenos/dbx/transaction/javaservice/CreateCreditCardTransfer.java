package com.temenos.dbx.transaction.javaservice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class CreateCreditCardTransfer implements JavaService2 {
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		// dummy Mock service until integrate with CMS.

		Result result = new Result();
        result.addParam(new Param("referenceId", String.format("%06d", new java.util.Date().getTime()%1000000)));
        result.addParam(new Param("status", "success"));
        result.addParam(new Param("message", "Success! Your transaction has been completed"));

        return result;
	}
}
