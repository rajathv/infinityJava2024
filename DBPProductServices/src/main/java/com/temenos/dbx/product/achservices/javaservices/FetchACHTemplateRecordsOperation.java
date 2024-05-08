package com.temenos.dbx.product.achservices.javaservices;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.achservices.resource.api.ACHTemplateRecordResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FetchACHTemplateRecordsOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(FetchACHTemplateRecordsOperation.class);

    @Override
    public Object invoke(String s, Object[] inputArray, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) throws Exception {


            Result result = new Result();

            try {
                //Initializing of ACHTemplateResource through Abstract factory method
                ACHTemplateRecordResource achResource = DBPAPIAbstractFactoryImpl.getInstance()
                        .getFactoryInstance(ResourceFactory.class).getResource(ACHTemplateRecordResource.class);

                result  = achResource.fetchACHTemplateRecord(inputArray, dataControllerRequest);
            }
            catch(Exception e) {
                LOG.error("Error occurred while fetching ACHTemplateRecords: ", e);
                return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
            }
            return result;

        }
    }
