package com.temenos.dbx.product.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.resource.api.BusinessTypeResource;

public class BusinessTypeRolesGetOperation implements JavaService2 {

	LoggerUtil logger = new LoggerUtil(BusinessTypeRolesGetOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			BusinessTypeResource businessTypeResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(BusinessTypeResource.class);
			result = businessTypeResource.getBusinessTypeRoles(methodID, inputArray, dcRequest, dcResponse);
		} catch (ApplicationException e) {
			e.getErrorCodeEnum().setErrorCode(result);
			logger.error("Exception occured while fetching the business types" + e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Exception occured while fetching the business types :" + e.getMessage());
		}

		return result;
	}
}
