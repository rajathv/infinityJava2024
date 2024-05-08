package com.temenos.dbx.eum.product.usermanagement.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.javaservice.CreateRetailContractOperation;
import com.temenos.dbx.eum.product.usermanagement.resource.api.InfinityUserManagementResource;

public class CreateRetailContractOperation implements JavaService2 {
	LoggerUtil logger = new LoggerUtil(CreateRetailContractOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		try {
			InfinityUserManagementResource profileManagementResource = DBPAPIAbstractFactoryImpl
					.getResource(InfinityUserManagementResource.class);
			return profileManagementResource.createRetailContract(methodID, inputArray, request, response);
		} catch (ApplicationException e) {
			Result result = new Result();
			e.setError(result);
			logger.error("Exception occured while creating a contract " + e.getStackTrace());
			return result;
		}
	}
}
