package com.temenos.dbx.party.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.party.resource.api.DueDiligenceResource;

/**
 * Update Citizenship and Tax Operation Java Service is the logic block for
 * updating the combined list of Citizenship and Tax of a Customer.
 * 
 * @author KH2117-ManojDasari
 */
public class UpdateCitizenshipOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(UpdateCitizenshipOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			DueDiligenceResource dueDiligenceResource = DBPAPIAbstractFactoryImpl
					.getResource(DueDiligenceResource.class);
			result = dueDiligenceResource.updateCitizenship(methodID, inputArray, dcRequest, dcResponse);
		} catch (Exception e) {
			LOG.error("Caught exception while updating Citizenship: ", e);
		}
		return result;
	}
}