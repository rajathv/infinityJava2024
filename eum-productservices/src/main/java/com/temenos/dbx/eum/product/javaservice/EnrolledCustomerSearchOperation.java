package com.temenos.dbx.eum.product.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.resource.api.ProfileManagementResource;
import com.temenos.dbx.eum.product.usermanagement.resource.api.UserManagementResource;

public class EnrolledCustomerSearchOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(EnrolledCustomerSearchOperation.class);
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		Result result = new Result();
        try {
            UserManagementResource resource = DBPAPIAbstractFactoryImpl.getResource(UserManagementResource.class);
            result = resource.searchEnrolledCustomer(methodId, inputArray, request, response);
        } catch (Exception e) {
            LOG.error("Caught exception while searching Customer: " + e);
        }
        return result;
	}

}
