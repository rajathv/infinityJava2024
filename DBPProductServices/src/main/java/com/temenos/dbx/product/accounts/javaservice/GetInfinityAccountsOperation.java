package com.temenos.dbx.product.accounts.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.dbx.product.accounts.resource.api.InfinityAccountsResource;

/**
 * 
 *
 */
public class GetInfinityAccountsOperation implements JavaService2{


    /**
     *
     */
    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        InfinityAccountsResource infinityUserManagementResource = DBPAPIAbstractFactoryImpl.getResource(InfinityAccountsResource.class);
        return infinityUserManagementResource.getInfinityAccounts(methodId, inputArray, request, response );
    }

}
