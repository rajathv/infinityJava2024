package com.temenos.dbx.eum.product.accounts.javaservice;


import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.accounts.resource.api.AccountsResource;

/**
 * 
 * @author kaushik.mondal(KH2691)
 *
 */

public class AccountPermissionCheckOperation implements JavaService2 {

	private LoggerUtil logger = new LoggerUtil(AccountPermissionCheckOperation.class);
	
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = null;
		
		try {
			
            AccountsResource accountsResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(AccountsResource.class);
            result = accountsResource.checkIfAccountPermissionEnabled(methodId, inputArray, request, response);
        
		}catch (Exception e) {
        	
            logger.error("Exception occured while checking permission of the given account "+ e.getMessage());
            result =new Result();
            ErrorCodeEnum.ERR_29009.setErrorCode(result);
        }
		
		
		return result;
	}

}
