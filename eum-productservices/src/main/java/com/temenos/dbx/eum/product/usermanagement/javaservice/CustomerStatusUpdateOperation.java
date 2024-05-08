package com.temenos.dbx.eum.product.usermanagement.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.javaservice.CustomerStatusUpdateOperation;
import com.temenos.dbx.eum.product.usermanagement.resource.api.UserManagementResource;

public class CustomerStatusUpdateOperation implements JavaService2 {

	LoggerUtil logger = new LoggerUtil(CustomerStatusUpdateOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		logger = new LoggerUtil(CustomerStatusUpdateOperation.class);

		Result result = new Result();
		try {
			UserManagementResource userManagementResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(UserManagementResource.class);
			result = userManagementResource.updateDBXUserStatus(methodID, inputArray, request, response);
		} catch (ApplicationException e) {
			logger.error("Exception occured while updating customer user status", e);
			e.setError(result);
		} catch (Exception e) {
			logger.error("Exception occured while updating customer user status", e);
		}

		return result;
	}

}
