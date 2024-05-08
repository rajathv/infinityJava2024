package com.temenos.dbx.product.achservices.javaservices;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.achservices.resource.api.ACHTransactionSubRecordResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FetchACHTransactionSubRecordsOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(FetchACHTransactionSubRecordsOperation.class);

    @Override
    public Object invoke(String methodName, Object[] inputArray, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) {
        Result result ;

        try {
            //Initializing of ACHTransactionRecordResource through Abstract factory method
            ACHTransactionSubRecordResource achResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(ACHTransactionSubRecordResource.class);

            result  = achResource.fetchACHTransactionSubRecords( methodName, inputArray, dataControllerRequest, dataControllerResponse);
        }
        catch(Exception e) {
            LOG.error("Error occurred while fetching ACHTransactionSubRecords: ", e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
        return result;

    }
}