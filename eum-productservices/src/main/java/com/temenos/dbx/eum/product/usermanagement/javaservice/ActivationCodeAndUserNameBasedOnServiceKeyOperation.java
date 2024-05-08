package com.temenos.dbx.eum.product.usermanagement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.javaservice.ActivationCodeAndUserNameBasedOnServiceKeyOperation;
import com.temenos.dbx.eum.product.usermanagement.resource.api.UserManagementResource;

public class ActivationCodeAndUserNameBasedOnServiceKeyOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(ActivationCodeAndUserNameBasedOnServiceKeyOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response)
            throws Exception {
        Result result = new Result();
        try {
            UserManagementResource resource = DBPAPIAbstractFactoryImpl.getResource(UserManagementResource.class);
            result = resource.sendActivationCodeAndUsernameBasedOnServiceKey(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            e.setError(result);
            LOG.error("Exception occured while sending activation code and username" + e.getMessage());
        } catch (Exception e) {
            LOG.error("Exception occured while sending activation code and username" + e.getMessage());
        }
        return result;
    }

}
