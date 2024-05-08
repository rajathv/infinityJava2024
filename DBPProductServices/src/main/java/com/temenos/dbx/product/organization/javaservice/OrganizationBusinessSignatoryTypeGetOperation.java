package com.temenos.dbx.product.organization.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.organization.resource.api.OrganizationResource;

public class OrganizationBusinessSignatoryTypeGetOperation implements JavaService2 {
	LoggerUtil logger = new LoggerUtil(OrganizationBusinessSignatoryTypeGetOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {

			OrganizationResource resource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(OrganizationResource.class);

			result = resource.getOrganizationBusinessSignatoryTypes(methodID, inputArray, request, response);
		} catch (ApplicationException e) {
			e.getErrorCodeEnum().setErrorCode(result);
			logger.error("Exception occured while fetching the organization signatory types", e);
		} catch (Exception e) {
			logger.error("Exception occured while fetching the organization signatory types", e);
		}

		return result;
	}

}
