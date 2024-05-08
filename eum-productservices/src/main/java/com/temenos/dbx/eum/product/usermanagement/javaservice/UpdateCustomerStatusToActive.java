package com.temenos.dbx.eum.product.usermanagement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.javaservice.UpdateCustomerStatusToActive;
import com.temenos.dbx.eum.product.usermanagement.resource.api.UserManagementResource;

public class UpdateCustomerStatusToActive implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(UpdateCustomerStatusToActive.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response)
            throws Exception {
        Result result = new Result();
        try {
            UserManagementResource resource = DBPAPIAbstractFactoryImpl.getResource(UserManagementResource.class);
            result = resource.updateCustomerStatusToActive(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            e.setError(result);
            LOG.error("Exception occured while updating the customer status" + e.getMessage());
        } catch (Exception e) {
            LOG.error("Exception occured while updating the customer status" + e.getMessage());
        }
        return result;
    }
}