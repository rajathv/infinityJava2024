package com.temenos.dbx.core.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.resource.api.CoreCustomerResource;

public class UpdateCoreProspectOperation implements JavaService2 {
	private static LoggerUtil logger;

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {

		logger = new LoggerUtil(UpdateCoreProspectOperation.class);
		
		 Result result = new Result();
	        try {
	            CoreCustomerResource customerResource = DBPAPIAbstractFactoryImpl.getInstance()
	                    .getFactoryInstance(ResourceFactory.class).getResource(CoreCustomerResource.class);
	           result = customerResource.updateFromDBX(methodID, inputArray, dcRequest, dcResponse);
	        } catch (Exception e) {
	            logger.error("Caught exception while creating Customer: ",  e);
	        }

	        return result;
	}
	
}