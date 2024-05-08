package com.temenos.dbx.party.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.party.resource.api.DueDiligenceResource;

public class UpdateDueDiligenceOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(UpdateDueDiligenceOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			DueDiligenceResource dueDiligenceResource = DBPAPIAbstractFactoryImpl
					.getResource(DueDiligenceResource.class);
			result = dueDiligenceResource.createDueDiligenceOperation(methodID, inputArray, dcRequest, dcResponse, true);
		} catch (Exception e) {
			LOG.error("Caught exception while creating due diligence: ", e);
		}
		return result;
	}
}