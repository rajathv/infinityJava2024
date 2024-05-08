package com.temenos.infinity.api.accountsweeps.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.accountsweeps.resource.api.AccountSweepsResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CreateAccountSweepsOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(CreateAccountSweepsOperation.class);
    AccountSweepsResource accountSweepsResource =  DBPAPIAbstractFactoryImpl.getInstance()
            .getFactoryInstance(ResourceFactory.class).getResource(AccountSweepsResource.class);
    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {

        Result result;
        try {
            result=accountSweepsResource.createSweep(methodID, inputArray, request, response);
        }
        catch (Exception e){
            LOG.error("Exception occured while creating sweep"+e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }

        return result;
    }
}
