package com.temenos.dbx.eum.product.usermanagement.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.resource.api.InfinityUserManagementResource;

/**
 * 
 *
 */
public class EditInfinityUserOperation implements JavaService2{

	LoggerUtil logger = new LoggerUtil(EditInfinityUserOperation.class);
    /**
     *
     */
    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
    	Result result = new Result();
        InfinityUserManagementResource infinityUserManagementResource = DBPAPIAbstractFactoryImpl.getResource(InfinityUserManagementResource.class);
        try {
        return infinityUserManagementResource.editInfinityUser(methodId, inputArray, request, response);
        }
        catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
        } catch (Exception e) {
            ErrorCodeEnum.ERR_10819.setErrorCode(result);
            logger.error("Error",e);
        }
        return result;
    }

}
