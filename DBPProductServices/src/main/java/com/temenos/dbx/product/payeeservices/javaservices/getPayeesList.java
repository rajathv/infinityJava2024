package com.temenos.dbx.product.payeeservices.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.payeeservices.resource.api.GetExternalPayeesResource;
import org.apache.log4j.Logger;

public class getPayeesList implements JavaService2 {
    private static final Logger LOG = Logger.getLogger(getPayeesList.class);
    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {
        GetExternalPayeesResource payeesResource = DBPAPIAbstractFactoryImpl.getResource(GetExternalPayeesResource.class);
        try {
            return payeesResource.fetchPayees(inputArray, request);
        } catch (Exception e) {
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
    }
}
