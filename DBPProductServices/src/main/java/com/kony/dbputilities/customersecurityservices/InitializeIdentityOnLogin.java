package com.kony.dbputilities.customersecurityservices;

import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.processor.IdentityHandler;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class InitializeIdentityOnLogin implements JavaService2 {

    private static LoggerUtil logger = new LoggerUtil(CustomerLogin.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {

        Result result = new Result();
        logger = new LoggerUtil(InitializeIdentityOnLogin.class);

        ServicesManager servicesManager = request.getServicesManager();
        IdentityHandler identityHandler = servicesManager.getIdentityHandler();
        if (identityHandler == null) {
            logger.error("Identity Handler is Null");
            result.addHttpStatusCodeParam(201);
            result.addOpstatusParam(0);
        } else {
            logger.error("Identity Handler worked.");
            identityHandler.getUserAttributes();
            identityHandler.getSecurityAttributes();
            result.addHttpStatusCodeParam(200);
            result.addOpstatusParam(0);
        }

        return result;
    }

}
