package com.temenos.dbx.product.usermanagement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.resource.api.CustomRoleResource;

public class OrganisationCustomRolesGetOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(OrganisationCustomRolesGetOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = null;
		try {
			CustomRoleResource customRoleResource = DBPAPIAbstractFactoryImpl.getResource(CustomRoleResource.class);
			result = customRoleResource.getAllCustomRoles(methodID, inputArray, request, response);
		}
		catch (Exception exp) {
			LOG.error("Exception occured while invoking resource in OrganisationCustomRolesGetOperation", exp);
		}

		return result;
	}

}
