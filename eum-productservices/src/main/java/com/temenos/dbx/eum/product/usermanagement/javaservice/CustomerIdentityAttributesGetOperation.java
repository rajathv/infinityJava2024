package com.temenos.dbx.eum.product.usermanagement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.javaservice.CustomerIdentityAttributesGetOperation;
import com.temenos.dbx.eum.product.usermanagement.resource.api.CustomerIdentityAttributesResource;

public class CustomerIdentityAttributesGetOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(CustomerIdentityAttributesGetOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {

            CustomerIdentityAttributesResource resource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(CustomerIdentityAttributesResource.class);

            result = resource.getCustomerIdentityAttributes(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            LOG.error("Caught exception while getting identity attributes: ", e);
        } catch (Exception e) {
            LOG.error("Caught exception while getting identity attributes: ", e);
        }

        return result;

    }

}
