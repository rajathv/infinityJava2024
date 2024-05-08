package com.temenos.dbx.party.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.party.resource.api.CustomerResource;

public class GetCustomerDetailsOperation implements JavaService2 {

	private LoggerUtil logger = new LoggerUtil(GetCustomerDetailsOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			CustomerResource customerResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(CustomerResource.class);
			result = customerResource.get(methodID, inputArray, request, response);
		} catch (Exception e) {
			logger.error("Caught exception while creating Customer: ", e);
		}
		return result;
	}
}
