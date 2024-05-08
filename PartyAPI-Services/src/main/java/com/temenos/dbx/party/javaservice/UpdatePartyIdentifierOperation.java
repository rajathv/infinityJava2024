package com.temenos.dbx.party.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.party.resource.api.CustomerResource;

public class UpdatePartyIdentifierOperation implements JavaService2 {

	private LoggerUtil logger = new LoggerUtil(UpdatePartyIdentifierOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse)
			throws Exception {
		Result result = new Result();
		try {
			CustomerResource customerResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(CustomerResource.class);
			result = customerResource.updateIdentifier(methodID, inputArray, dcRequest, dcResponse);
		} catch (Exception e) {
			logger.error("Caught exception while creating party identifier: ", e);
		}

		return result;
	}

}