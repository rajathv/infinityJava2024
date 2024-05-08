package com.temenos.dbx.eum.mfa.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.mfa.resource.api.MFAServiceResource;

public class MFAServiceCreateOperation implements JavaService2 {
    LoggerUtil logger = new LoggerUtil(MFAServiceCreateOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response)
            throws Exception {
        Result result = new Result();
        try {
            MFAServiceResource resource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(MFAServiceResource.class);
            result = resource.createMFAServiceFromCommunication(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            e.setError(result);
            logger.error("Caught exception while creating mfaservice record:", e);
        } catch (Exception e) {
            logger.error("Caught exception while creating mfaservice record:", e);
        }

        return result;
    }

}
