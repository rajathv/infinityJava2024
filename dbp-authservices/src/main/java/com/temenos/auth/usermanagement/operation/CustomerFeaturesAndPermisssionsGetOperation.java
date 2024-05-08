package com.temenos.auth.usermanagement.operation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.auth.usermanagement.resource.api.AuthUserManagementResource;

public class CustomerFeaturesAndPermisssionsGetOperation implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(
			CustomerFeaturesAndPermisssionsGetOperation.class);
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse)
			throws Exception {
		try {
			AuthUserManagementResource resource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class)
                    .getResource(AuthUserManagementResource.class);
			return resource.getCustomerFeatureAndPermissions(methodId, inputArray, dcRequest,
					dcResponse);
			
		} catch (ApplicationException e) {
			LOG.error("Exception while getting customer legal entities" , e.getMessage());
			return e.getErrorCodeEnum().setErrorCode(new Result());
		}
		 catch (Exception e) {
			 LOG.error("Exception while getting customer legal entities" , e);
			 return ErrorCodeEnum.ERR_10220.setErrorCode(new Result());
		}
	}

}
