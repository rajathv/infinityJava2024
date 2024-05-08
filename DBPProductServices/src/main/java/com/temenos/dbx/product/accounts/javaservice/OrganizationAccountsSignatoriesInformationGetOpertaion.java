package com.temenos.dbx.product.accounts.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.accounts.resource.api.AccountsResource;

public class OrganizationAccountsSignatoriesInformationGetOpertaion implements JavaService2 {
	LoggerUtil logger = new LoggerUtil(OrganizationAccountsSignatoriesInformationGetOpertaion.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			AccountsResource accountsResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(AccountsResource.class);
			result = accountsResource.getOrganizationAccountsSignatoriesInformation(methodID, inputArray, request,
					response);
		} catch (ApplicationException e) {
			e.getErrorCodeEnum().setErrorCode(result);
			logger.error("Exception occured while fetching the account signatories information", e);
		} catch (Exception e) {
			logger.error("Exception occured while fetching the account signatories information", e);
			throw new ApplicationException(ErrorCodeEnum.ERR_10260);
		}

		return result;
	}
}
