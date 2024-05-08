package com.temenos.dbx.product.payeeservices.javaservices;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.payeeservices.resource.api.P2PPayeeResource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EditP2PPayeeOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(EditP2PPayeeOperation.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {

        Result result = new Result();

        try {
        	P2PPayeeResource payeeResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(P2PPayeeResource.class);

            result  = payeeResource.editPayee(methodID, inputArray, request, response);
        }
        catch(Exception e) {
            LOG.error("Error occurred while invoking fetchAllMyPayees: ", e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }

        return result;
    }
}