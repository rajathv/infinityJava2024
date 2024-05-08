package com.temenos.dbx.eum.product.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.resource.api.ApproverResource;

/**
 * 
 * @author KH2627
 * @version 1.0 Java Service end point to get approvers for specific account and action
 */

public class AccountActionApprovalListGetServiceOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(AccountActionApprovalListGetServiceOperation.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {

        Result result = new Result();
        try {
            ApproverResource approverResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(ApproverResource.class);
            result = approverResource.getAccountActionApprovers(methodID, inputArray, dcRequest, dcResponse);
        } catch (Exception e) {
            LOG.error("Caught exception while creating Customer: " + e);
        }

        return result;
    }
}
