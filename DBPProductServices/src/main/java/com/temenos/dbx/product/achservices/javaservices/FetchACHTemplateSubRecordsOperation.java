package com.temenos.dbx.product.achservices.javaservices;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.achservices.resource.api.ACHTemplateSubRecordResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FetchACHTemplateSubRecordsOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(FetchACHTemplateSubRecordsOperation.class);

    @Override
    public Object invoke(String s, Object[] inputArray, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) throws Exception {
        Result result = new Result();

        try {
            //Initializing of ACHTemplateRecordResource through Abstract factory method
            ACHTemplateSubRecordResource achResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(ACHTemplateSubRecordResource.class);

            result  = achResource.fetchACHTemplateSubRecord(inputArray, dataControllerRequest);
        }
        catch(Exception e) {
            LOG.error("Error occurred while fetching ACHTemplateSubRecords: ", e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
        return result;

    }
}
