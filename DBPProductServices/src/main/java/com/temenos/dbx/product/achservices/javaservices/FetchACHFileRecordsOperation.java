package com.temenos.dbx.product.achservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.achservices.resource.api.ACHFileRecordResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class FetchACHFileRecordsOperation implements JavaService2  {
	
	private static final Logger LOG = LogManager.getLogger(FetchACHFileRecordsOperation.class);

    @Override
    public Object invoke(String methodName, Object[] inputArray, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) {

        Result result = new Result();
        try {
            //Initializing of ACHFileRecordResource through Abstract factory method
            ACHFileRecordResource achFileRecordResource = DBPAPIAbstractFactoryImpl.getResource(ACHFileRecordResource.class);
            result  = achFileRecordResource.fetchACHFileRecords(methodName, inputArray, dataControllerRequest, dataControllerResponse);
        }
        catch(Exception e) {
            LOG.error("Error occurred while fetching ACHFileRecords: ", e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
        
        return result;
    }

}
