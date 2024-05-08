package com.temenos.dbx.product.approvalservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalservices.resource.api.ApprovalQueueResource;

public class WithdrawOperation implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(WithdrawOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
		
		Result result;
		try {
			//Initializing of ApprovalQueueResource through Abstract factory method
			ApprovalQueueResource approvalQueueResource = DBPAPIAbstractFactoryImpl.getResource(ApprovalQueueResource.class);
			result = approvalQueueResource.withdraw(methodId, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking withdraw: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}

}
