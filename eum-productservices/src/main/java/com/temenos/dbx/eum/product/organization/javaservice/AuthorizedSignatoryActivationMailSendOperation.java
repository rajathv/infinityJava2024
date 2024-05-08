package com.temenos.dbx.eum.product.organization.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.organization.resource.api.AuthorizedSignatoriesResource;

public class AuthorizedSignatoryActivationMailSendOperation implements JavaService2 {

	LoggerUtil logger = new LoggerUtil(AuthorizedSignatoryActivationMailSendOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {

			AuthorizedSignatoriesResource authorizedSignatoriesResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(AuthorizedSignatoriesResource.class);

			result = authorizedSignatoriesResource.sendActivationMailToAuthorizedSignatory(methodID, inputArray,
					request, response);
		} catch (ApplicationException e) {
			logger.error("Exception occured while sending activation mail notification to authorized signatories", e);
		} catch (Exception e) {
			logger.error("Exception occured while sending activation mail notification to authorized signatories", e);
		}

		return result;
	}
}
