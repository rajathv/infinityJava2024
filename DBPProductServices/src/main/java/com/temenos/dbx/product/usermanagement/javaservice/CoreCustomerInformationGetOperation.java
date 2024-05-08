package com.temenos.dbx.product.usermanagement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.resource.api.InfinityUserManagementResource;
import com.temenos.dbx.product.usermanagement.resource.api.UserManagementResource;

public class CoreCustomerInformationGetOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(CoreCustomerInformationGetOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response)
            throws Exception {
        Result result = new Result();
        try {
            InfinityUserManagementResource resource =
                    DBPAPIAbstractFactoryImpl.getResource(InfinityUserManagementResource.class);
            result = resource.getCoreCustomerInformation(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            e.setError(result);
            LOG.error("Exception occured while sending activation code and username" + e.getMessage());
        } catch (Exception e) {
            LOG.error("Exception occured while sending activation code and username" + e.getMessage());
        }
        return result;
    }

}
