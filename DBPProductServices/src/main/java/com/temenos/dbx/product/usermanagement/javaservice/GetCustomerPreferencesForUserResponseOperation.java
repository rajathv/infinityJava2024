package com.temenos.dbx.product.usermanagement.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.resource.api.CustomerPreferenceResource;

public class GetCustomerPreferencesForUserResponseOperation implements JavaService2 {

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		
		CustomerPreferenceResource customerPreferenceResourceImpl = DBPAPIAbstractFactoryImpl.getResource(CustomerPreferenceResource.class);
		
		return customerPreferenceResourceImpl.getPreferencesForUserResponse(methodID, inputArray, dcRequest, dcResponse);
		
	}

}
