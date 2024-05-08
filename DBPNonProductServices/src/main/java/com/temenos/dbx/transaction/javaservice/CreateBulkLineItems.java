package com.temenos.dbx.transaction.javaservice;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.dbp.jwt.auth.utils.CommonUtils;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class CreateBulkLineItems implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(CreateBulkLineItems.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            //For T24 Service Call
            HashMap<String, Object> params = (HashMap<String, Object>) inputArray[1];
            HashMap<String, Object> serviceHeaders = new HashMap<String, Object>();
            String serviceName = "dbpRbLocalServicesdb";
            String operationName = "dbxdb_bulkwirefilelineitems_create";
            result = CommonUtils.callIntegrationService(request, params, serviceHeaders, serviceName, operationName,
                        true);
        } catch (Exception e) {
            LOG.error("Caught exception in invoke method : " + e);
            return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
        }

        return result;
    }

}
