package com.temenos.dbx.party.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.party.resource.api.UpdateCustomerApplicationResource;

public class UpdateCustomerApplicationOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(UpdateCustomerApplicationOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {

        Result result = new Result();

        try {
            UpdateCustomerApplicationResource update = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(UpdateCustomerApplicationResource.class);
            result = update.updateCustomerApplication(methodID, inputArray, dcRequest, dcResponse);
        } catch (Exception e) {
            LOG.error("Caught exception while creating Customer: ", e);
        }
        return result;
    }

}
