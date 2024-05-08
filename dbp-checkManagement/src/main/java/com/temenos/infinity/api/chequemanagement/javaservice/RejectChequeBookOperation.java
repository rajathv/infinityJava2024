package com.temenos.infinity.api.chequemanagement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.chequemanagement.resource.api.ChequeManagementResource;

public class RejectChequeBookOperation implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(RejectChequeBookOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			ChequeManagementResource chequeManagementResource = DBPAPIAbstractFactoryImpl
					.getResource(ChequeManagementResource.class);
			result = chequeManagementResource.rejectChequeBook(request);
			return result;
		} catch (Exception e) { 
			LOG.error("Unable to Reject ChequeBook : "+e);
			return ErrorCodeEnum.ERR_26021.setErrorCode(new Result()); 
		}

	}

}
