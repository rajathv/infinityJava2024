package com.temenos.dbx.product.usermanagement.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.resource.api.CustomerCommunicationResource;

public class CustomerCommunicationGetOperation implements JavaService2 {
    LoggerUtil logger = new LoggerUtil(CustomerCommunicationGetOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            CustomerCommunicationResource customerCommunicationResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(CustomerCommunicationResource.class);
            result = customerCommunicationResource.getCustomerCommunication(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            e.setError(result);
            logger.error("Caught exception while getting customer communication info: ", e);
        } catch (Exception e) {
            logger.error("Caught exception while getting customer communication info: ", e);
        }

        return result;
    }

}
