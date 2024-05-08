package com.temenos.dbx.product.approvalsframework.approvalrequest.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalsframework.approvalrequest.resource.api.ApprovalRequestResource;

import java.util.Map;

public class FetchAllPendingRequestsService implements JavaService2 {
    @Override
    public Object invoke(String method, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) inputArray[1];
        Result result = new Result();
        ApprovalRequestResource approvalRequestResource = DBPAPIAbstractFactoryImpl.getResource(ApprovalRequestResource.class);
        try {
            result = approvalRequestResource.fetchAllPendingRequests(inputMap, dcRequest);
        } catch (ApplicationException ae) {
            return ae.getErrorCodeEnum().setErrorCode(result);
        }
        return result;
    }
}
