package com.temenos.dbx.party.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.dbx.party.resource.api.DueDiligenceResource;

public class UpdateSourceOfFundsOperation implements JavaService2 {

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		DueDiligenceResource dueDiligenceResource = DBPAPIAbstractFactoryImpl.getResource(DueDiligenceResource.class);
		return dueDiligenceResource.updateFundsSource(methodId, inputArray, request, response);
	}

}
