package com.temenos.dbx.product.contract.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.contract.resource.api.ContractResource;

public class CoreCustomerBasedContractDetailsGetOperation implements JavaService2 {
    LoggerUtil logger = new LoggerUtil(CoreCustomerBasedContractDetailsGetOperation.class);

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            ContractResource resource = DBPAPIAbstractFactoryImpl.getResource(ContractResource.class);
            result = resource.getCoreCustomerBasedContractDetails(methodId, inputArray, request, response);
        } catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            logger.error("Exception occured while fetching the core customer based contract details" + e.getMessage());
        } catch (Exception e) {
            logger.error("Exception occured while fetching the core customer based contract details" + e.getMessage());
            ErrorCodeEnum.ERR_10781.setErrorCode(result);
        }
        return result;
    }

}
