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

import java.util.HashMap;

/**
 * @author naveen.yerra
 */
public class EditAccountSweepsOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(EditAccountSweepsOperation.class);
    AccountSweepsResource accountSweepsResource =  DBPAPIAbstractFactoryImpl.getInstance()
            .getFactoryInstance(ResourceFactory.class).getResource(AccountSweepsResource.class);
    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) throws Exception {
        HashMap<String, Object> inputMap = (HashMap<String, Object>) objects[1];
        try {
            return accountSweepsResource.editSweep(inputMap, dataControllerRequest);
        } catch(Exception e) {
            LOG.error("Exception occurred while creating sweep", e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
    }
}
