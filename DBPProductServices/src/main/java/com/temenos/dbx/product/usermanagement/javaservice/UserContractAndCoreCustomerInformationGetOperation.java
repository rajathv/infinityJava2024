package com.temenos.dbx.product.usermanagement.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.resource.api.InfinityUserManagementResource;

/**
 * 
 * @author sowmya.vandanapu
 * @since 2021.01
 * @version 1.0 Fetches the customer accounts
 */
public class UserContractAndCoreCustomerInformationGetOperation implements JavaService2 {

    LoggerUtil logger = new LoggerUtil(UserContractAndCoreCustomerInformationGetOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {

        Result result = new Result();
        try {
            InfinityUserManagementResource resource =
                    DBPAPIAbstractFactoryImpl.getResource(InfinityUserManagementResource.class);
            result = resource.getInfinityUserContractCustomerDetails(methodID, inputArray, dcRequest, dcResponse);
        } catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            logger.error("Exception occured while fetching the logged in user contracts and core customers"
                    + e.getStackTrace());
        } catch (Exception e) {
            logger.error("Exception occured while fetching the logged in user contracts and core customers"
                    + e.getStackTrace());
            ErrorCodeEnum.ERR_10776.setErrorCode(result);
        }
        return result;

    }
}
