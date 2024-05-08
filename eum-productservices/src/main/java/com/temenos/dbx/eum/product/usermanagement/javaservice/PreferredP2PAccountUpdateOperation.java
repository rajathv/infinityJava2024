package com.temenos.dbx.eum.product.usermanagement.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.javaservice.PreferredP2PAccountUpdateOperation;
import com.temenos.dbx.eum.product.usermanagement.resource.api.CustomerPreferenceResource;
import com.temenos.dbx.product.usermanagement.javaservice.ResetPasswordOperation;

public class PreferredP2PAccountUpdateOperation implements JavaService2 {

	LoggerUtil logger = new LoggerUtil(ResetPasswordOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		logger = new LoggerUtil(PreferredP2PAccountUpdateOperation.class);
		
		 Result result = new Result();
	        try {
	            CustomerPreferenceResource customerPreferenceResource = DBPAPIAbstractFactoryImpl.getInstance()
	                    .getFactoryInstance(ResourceFactory.class).getResource(CustomerPreferenceResource.class);
	            result = customerPreferenceResource.updatePreferredDbxP2PAccounts(methodID, inputArray, request, response);
	        } catch (Exception e) {
	            logger.error("Caught exception while updating Customer Preferences: ",  e);
	        }

	        return result;
	}

}
