package com.temenos.dbx.eum.product.usermanagement.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.javaservice.GetUserNameOperation;
import com.temenos.dbx.eum.product.usermanagement.resource.api.UserSearchResource;
import com.temenos.dbx.product.usermanagement.javaservice.ResetPasswordOperation;

public class GetUserNameOperation implements JavaService2 {

	LoggerUtil logger = new LoggerUtil(ResetPasswordOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		logger = new LoggerUtil(GetUserNameOperation.class);
		
		 Result result = new Result();
	        try {
	            UserSearchResource managementResource = DBPAPIAbstractFactoryImpl.getResource(UserSearchResource.class);
	            result = managementResource.verifyDbxUserName(methodID, inputArray, request, response);
	        } catch (Exception e) {
	            logger.error("Caught exception while updating Customer Preferences: ",  e);
	        }

	        return result;
	}

}