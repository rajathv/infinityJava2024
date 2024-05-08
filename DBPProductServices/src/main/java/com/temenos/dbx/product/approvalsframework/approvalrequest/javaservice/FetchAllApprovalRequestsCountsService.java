package com.temenos.dbx.product.approvalsframework.approvalrequest.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalsframework.approvalrequest.resource.api.ApprovalRequestResource;
import org.apache.log4j.Logger;

import java.util.Map;

public class FetchAllApprovalRequestsCountsService implements JavaService2 {
    private static final Logger LOG = Logger.getLogger(FetchAllApprovalRequestsCountsService.class);

    @Override
    public Object invoke(String method, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) throws Exception {
        Map<String, Object> inputMap = (Map<String, Object>) inputArray[1];
        Result result = new Result();
        ApprovalRequestResource approvalRequestResource = DBPAPIAbstractFactoryImpl.getResource(ApprovalRequestResource.class);
        try {
            result = approvalRequestResource.fetchApprovalRequestsCounts(inputMap, dcRequest);
        } catch (ApplicationException ae) {
            return ae.getErrorCodeEnum().setErrorCode(result);
        } catch (Exception e) {
            LOG.error("Internal error occurred at ApproveRequestService exec: " + e);
            // DBB-14829
            // e.printStackTrace();
            return ErrorCodeEnum.ERR_87420.setErrorCode(result);
        }
        return result;
    }
}
