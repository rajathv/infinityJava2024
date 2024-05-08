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

public class CustomersAssociatedToAccountGetOperation implements JavaService2 {
    LoggerUtil logger = new LoggerUtil(AccountInformationGetOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            AccountsResource accountsResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(AccountsResource.class);
            result = accountsResource.getCustomerAssociatedToAccounts(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            logger.error(
                    "CustomersAssociatedToAccountGetOperation : Exception occured while fetching customers associated to contract"
                            + e.getMessage());
        } catch (Exception e) {
            logger.error(
                    "CustomersAssociatedToAccountGetOperation : Exception occured while fetching customers associated to contract"
                            + e.getMessage());
        }

        return result;
    }

}
