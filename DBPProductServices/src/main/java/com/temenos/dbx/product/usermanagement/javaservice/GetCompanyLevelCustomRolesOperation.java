package com.temenos.dbx.product.usermanagement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.resource.api.CustomRoleResource;
import com.temenos.dbx.product.usermanagement.resource.api.InfinityUserManagementResource;

public class GetCompanyLevelCustomRolesOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(GetCompanyLevelCustomRolesOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = null;
		try {
		    InfinityUserManagementResource customRoleResource = DBPAPIAbstractFactoryImpl.getResource(InfinityUserManagementResource.class);
			result = customRoleResource.getCompanyLevelCustomRoles(methodID, inputArray, request, response);
		}
		catch (Exception exp) {
			LOG.error("Exception occured while invoking resource in CustomRoleDetailsGetOperation", exp);
		}

		return result;
	}
}
