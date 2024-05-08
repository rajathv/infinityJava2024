package com.temenos.dbx.eum.product.usermanagement.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.resource.api.CustomerAddressResource;
import com.temenos.dbx.eum.product.usermanagement.resource.api.CustomerPreferenceResource;

public class GetCustomerAddressForUserResponseOperation implements JavaService2 {

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		
		CustomerAddressResource impl = DBPAPIAbstractFactoryImpl.getResource(CustomerAddressResource.class);
		
		return impl.getCustomerAddressForUserResponse(methodID, inputArray, dcRequest, dcResponse);
		
	}

}
