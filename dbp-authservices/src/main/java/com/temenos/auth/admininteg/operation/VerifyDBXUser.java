package com.temenos.auth.admininteg.operation;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.auth.security.resource.api.CaptchaResource;
import com.temenos.dbx.product.usermanagement.resource.api.UserManagementResource;

public class VerifyDBXUser implements JavaService2 {

	boolean isOnlySsnPresent = false;
	boolean isSSnLastNameDOBPresent = false;

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		CaptchaResource userManagementResource = DBPAPIAbstractFactoryImpl.getResource(CaptchaResource.class);
		return userManagementResource.verifyCustomer(methodID, inputArray, dcRequest, dcResponse);
	}
}