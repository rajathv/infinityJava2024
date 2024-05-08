package com.temenos.dbx.product.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.resource.api.CombinedUserResource;

public class CombinedUserPermissionsGetOperation implements JavaService2 {

	LoggerUtil logger = new LoggerUtil(CombinedUserPermissionsGetOperation.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			CombinedUserResource combinedUserResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(CombinedUserResource.class);
			result = combinedUserResource.getCombinedUserPermissions(methodID, inputArray, dcRequest, dcResponse);
		} catch (ApplicationException e) {
			e.getErrorCodeEnum().setErrorCode(result);
			logger.error("Exception occured while fetching combined user permissions", e);
		} catch (Exception e) {
			logger.error("Exception occured while fetching combined user permissions", e);
		}
		return result;
	}
}
