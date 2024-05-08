package com.temenos.dbx.eum.product.contract.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.contract.resource.api.CoreCustomerResource;

/**
 * Fetches the core customer accounts
 * 
 * @author sowmya.vandanapu
 * @since 2021.01
 * @version 1.0 Fetches the customer accounts
 */
public class CoreCustomerAccountsGetOperation implements JavaService2 {

    LoggerUtil logger = new LoggerUtil(CoreCustomerAccountsGetOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {

        Result result = new Result();
        try {
            CoreCustomerResource resource = DBPAPIAbstractFactoryImpl.getResource(CoreCustomerResource.class);
            result = resource.getCoreCustomerAccounts(methodID, inputArray, dcRequest, dcResponse);
        } catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            logger.error("Exception occured while fetching core customer accounts" , e);
        } catch (Exception e) {
            logger.error("Exception occured while fetching core customer accounts" , e);
            ErrorCodeEnum.ERR_10785.setErrorCode(result);
        }
        return result;
    }
}
