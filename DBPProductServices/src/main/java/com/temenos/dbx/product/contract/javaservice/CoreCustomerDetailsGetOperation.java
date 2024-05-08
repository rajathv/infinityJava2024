package com.temenos.dbx.product.contract.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.contract.resource.api.CoreCustomerResource;

/**
 * Fetches the Relative core customers
 * 
 * @author sowmya.vandanapu
 * @since 2021.01
 * @version 1.0 Fetches the relative customers
 */
public class CoreCustomerDetailsGetOperation implements JavaService2 {

    LoggerUtil logger = new LoggerUtil(CoreCustomerDetailsGetOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {

        Result result = new Result();
        try {
            CoreCustomerResource resource = DBPAPIAbstractFactoryImpl.getResource(CoreCustomerResource.class);
            result = resource.getCoreRelativeCustomers(methodID, inputArray, dcRequest, dcResponse);
        } catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            logger.error("Exception occured while fetching core customer details " + e.getStackTrace());
        } catch (Exception e) {
            logger.error("Exception occured while fetching core customer details " + e.getStackTrace());
            ErrorCodeEnum.ERR_10760.setErrorCode(result);
        }
        return result;

    }
}
