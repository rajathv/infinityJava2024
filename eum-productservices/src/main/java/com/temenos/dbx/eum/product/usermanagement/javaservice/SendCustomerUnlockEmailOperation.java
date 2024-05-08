package com.temenos.dbx.eum.product.usermanagement.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.dbx.eum.product.usermanagement.resource.api.OrganizationUserManagementResource;
import com.temenos.dbx.eum.product.usermanagement.resource.api.ProfileManagementResource;

public class SendCustomerUnlockEmailOperation implements JavaService2{

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		ProfileManagementResource managementResource = DBPAPIAbstractFactoryImpl.getResource(ProfileManagementResource.class);
		return managementResource.sendCustomerUnlockEmail(methodID, inputArray, request, response);
		
	}

	
}