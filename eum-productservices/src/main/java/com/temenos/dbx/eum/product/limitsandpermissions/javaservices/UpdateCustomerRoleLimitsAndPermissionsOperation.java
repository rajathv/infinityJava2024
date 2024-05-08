package com.temenos.dbx.eum.product.limitsandpermissions.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.limitsandpermissions.resource.api.LimitsAndPermissionsResource;

public class UpdateCustomerRoleLimitsAndPermissionsOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(UpdateCustomerRoleLimitsAndPermissionsOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			LimitsAndPermissionsResource limitsAndPermissionsResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(LimitsAndPermissionsResource.class);
			result = limitsAndPermissionsResource.UpdateCustomerRoleLimitsAndPermissions(methodId, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of UpdateCustomerRoleLimitsAndPermissionsOperation: ", e);
			return ErrorCodeEnum.ERR_28033.setErrorCode(new Result());
		}
		return result;
	}

}