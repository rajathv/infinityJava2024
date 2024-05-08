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

/**
 * Searches the contract
 * 
 * @author sowmya.vandanapu
 * @version 1.0
 * @since 2021.01
 */
public class ContractSearchOperation implements JavaService2 {
    LoggerUtil logger = new LoggerUtil(ContractSearchOperation.class);

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            ContractResource resource = DBPAPIAbstractFactoryImpl.getResource(ContractResource.class);
            result = resource.searchContracts(methodId, inputArray, request, response);
        } catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            logger.error("Exception occured while searching contracts" + e.getStackTrace());
        } catch (Exception e) {
            logger.error("Exception occured while searching contracts" + e.getStackTrace());
            ErrorCodeEnum.ERR_10786.setErrorCode(result);
        }
        return result;
    }
}
