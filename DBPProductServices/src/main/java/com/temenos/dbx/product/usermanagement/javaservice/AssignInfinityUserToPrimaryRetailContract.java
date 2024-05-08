package com.temenos.dbx.product.usermanagement.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.resource.api.InfinityUserManagementResource;

public class AssignInfinityUserToPrimaryRetailContract implements JavaService2 {

    LoggerUtil logger = new LoggerUtil(ResetPasswordOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {

        logger = new LoggerUtil(this.getClass());
        
         Result result = new Result();
            try {
                InfinityUserManagementResource resource = DBPAPIAbstractFactoryImpl.getResource(InfinityUserManagementResource.class);
                result = resource.assignInfinityUserToPrimaryRetailContract(methodID, inputArray, request, response);
            } catch (Exception e) {
                logger.error("Caught exception while updating Customer Preferences: ",  e);
            }

            return result;
    }

}