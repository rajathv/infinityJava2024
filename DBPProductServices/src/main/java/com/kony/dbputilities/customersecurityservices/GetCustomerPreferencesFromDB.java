package com.kony.dbputilities.customersecurityservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.dbx.product.usermanagement.resource.api.CustomerPreferenceResource;

public class GetCustomerPreferencesFromDB implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetCustomerPreferencesFromDB.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		CustomerPreferenceResource customerPreferenceResource = DBPAPIAbstractFactoryImpl.getResource(CustomerPreferenceResource.class);
		return customerPreferenceResource.getPreferencesForLogin(methodID, inputArray, dcRequest, dcResponse);
	}

}