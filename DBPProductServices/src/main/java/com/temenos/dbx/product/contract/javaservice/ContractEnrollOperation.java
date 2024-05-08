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
import com.temenos.dbx.product.usermanagement.resource.api.InfinityUserManagementResource;

public class ContractEnrollOperation implements JavaService2 {
    LoggerUtil logger = new LoggerUtil(ContractEnrollOperation.class);

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            // added isDefaultActionsEnabled param in request to create default actions for features while creating the
            // contract
            request.addRequestParam_("isDefaultActionsEnabled", "true");
            ContractResource resource = DBPAPIAbstractFactoryImpl.getResource(ContractResource.class);
            InfinityUserManagementResource userManagementResource =
                    DBPAPIAbstractFactoryImpl.getResource(InfinityUserManagementResource.class);
            boolean status = resource.validateEnrollContractPayload(methodId, inputArray, request, response);
            if (status) {
                result = resource.createContract(methodId, inputArray, request, response);

                userManagementResource.createAUserAndAssignTOGivenContract(methodId, inputArray, request, response);
            } else {
                ErrorCodeEnum.ERR_10328.setErrorCode(result);
            }
        } catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            logger.error("Exception occured while creating a contract " + e.getStackTrace());
        } catch (Exception e) {
            logger.error("Exception occured while creating a contract " + e.getStackTrace());
            ErrorCodeEnum.ERR_10390.setErrorCode(result);
        }

        return result;
    }

}
