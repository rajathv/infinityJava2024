package com.temenos.dbx.eum.product.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.resource.api.ProfileManagementResource;

public class UserIdSearchOperationDetailedData implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(UserIdSearchOperationDetailedData.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {

		Result result = new Result();
		try {
			ProfileManagementResource resource = DBPAPIAbstractFactoryImpl.getResource(ProfileManagementResource.class);
			result = resource.userIdSearchDetailed(methodID, inputArray, dcRequest, dcResponse);
		} catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            LOG.error("Exception occured while getting a contract details" , e);
        } catch (Exception e) {
        	LOG.error("Exception occured while getting a contract details" , e);
            ErrorCodeEnum.ERR_10363.setErrorCode(result);
        }
		return result;
	}
	
}