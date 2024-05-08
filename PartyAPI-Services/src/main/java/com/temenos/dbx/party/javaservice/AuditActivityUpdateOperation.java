package com.temenos.dbx.party.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.party.resource.api.AuditActivityUpdateResource;

/**
 * 
 * @author KH2627
 * @version 1.0 Java Service to update audit logs with customerId and
 *          coreCustomerId for the provided partyId
 * 
 */

public class AuditActivityUpdateOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(AuditActivityUpdateOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {

		Result result = new Result();
		try {
			AuditActivityUpdateResource auditActivityUpdateResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(AuditActivityUpdateResource.class);
			result = auditActivityUpdateResource.updatePartyAuditLogsWithCustomerInformation(methodID, inputArray,
					dcRequest, dcResponse);
		} catch (Exception e) {
			LOG.error("Exception occured while update party customer information" + e);
		}

		return result;
	}

}