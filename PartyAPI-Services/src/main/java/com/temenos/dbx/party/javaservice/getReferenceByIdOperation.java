package com.temenos.dbx.party.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.dbx.party.resource.api.CustomerResource;

public class getReferenceByIdOperation implements JavaService2 {

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		CustomerResource customerResource = DBPAPIAbstractFactoryImpl.getResource(CustomerResource.class);
		
		return customerResource.getReferenceByID(methodID, inputArray, dcRequest, dcResponse);
		
	}

}
