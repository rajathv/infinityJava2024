package com.temenos.dbx.product.accounts.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.accounts.resource.api.AccountsResource;

/**
 * 
 * @author sowmya.vandanapu
 *
 */
public class AccountInformationGetForAdminOperation implements JavaService2 {
	LoggerUtil logger = new LoggerUtil(AccountInformationGetForAdminOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			AccountsResource accountsResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(AccountsResource.class);
			result = accountsResource.getAccountInformationForAdmin(methodID, inputArray, request, response);
		} catch (ApplicationException e) {
			e.getErrorCodeEnum().setErrorCode(result);
			logger.error("Exception occured while fetching account details :" + e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Exception occured while fetching the account details :" + e.getMessage(), e);
		}

		return result;
	}
}
