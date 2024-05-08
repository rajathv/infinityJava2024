package com.temenos.infinity.api.srmstransactions.javaservice;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

import java.util.Map;

public class CreatePaymentWithoutApprover implements JavaService2 {
    @Override
    public Object invoke(String s, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse dataControllerResponse) throws Exception {
        String serviceName = ServiceId.SRMSBACKEND_PAYMENTORDER_INTEGRATIONSERVICE;
        String operationName = OperationName.SRMS_CREATEPAYMENT_WITHOUTAPPROVER;

        Map<String, Object> requestParameters = (Map<String, Object>) inputArray[1];
        String response =  DBPServiceExecutorBuilder.builder().
                withServiceId(serviceName).
                withObjectId(null).
                withOperationId(operationName).
                withRequestParameters(requestParameters).
                withRequestHeaders(request.getHeaderMap()).
                withDataControllerRequest(request).
                build().getResponse();
        return JSONToResult.convert(response);
    }
}
