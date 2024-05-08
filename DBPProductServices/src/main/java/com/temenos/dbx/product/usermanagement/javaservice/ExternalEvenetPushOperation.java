package com.temenos.dbx.product.usermanagement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.usermanagement.resource.api.PushExternalEventResource;

public class ExternalEvenetPushOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(ExternalEvenetPushOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            PushExternalEventResource resource = DBPAPIAbstractFactoryImpl.getResource(PushExternalEventResource.class);
            result = resource.pushUserIdAndActivationCode(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            e.setError(result);
            LOG.error("Error occured while pushing the event" + e.getMessage());
        } catch (Exception e) {
            LOG.error("Error occured while pushing the event" + e.getMessage());
        }
        return result;
    }
}
