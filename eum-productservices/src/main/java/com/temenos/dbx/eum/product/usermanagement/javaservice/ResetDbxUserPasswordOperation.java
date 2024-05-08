package com.temenos.dbx.eum.product.usermanagement.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.javaservice.ResetDbxUserPasswordOperation;
import com.temenos.dbx.eum.product.usermanagement.resource.api.UserManagementResource;

public class ResetDbxUserPasswordOperation implements JavaService2 {

	LoggerUtil logger = new LoggerUtil(ResetDbxUserPasswordOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {

		logger = new LoggerUtil(ResetDbxUserPasswordOperation.class);
		
		 Result result = new Result();
	        try {
	            UserManagementResource userManagementResource = DBPAPIAbstractFactoryImpl.getInstance()
	                    .getFactoryInstance(ResourceFactory.class).getResource(UserManagementResource.class);
	           result = userManagementResource.resetUserPassword(methodID, inputArray, dcRequest, dcResponse);
	        } catch (Exception e) {
	            logger.error("Caught exception while creating Customer: ",  e);
	        }

	        return result;
	}

}
