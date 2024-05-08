package com.temenos.infinity.api.chequemanagement.businessdelegate.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.chequemanagement.backenddelegate.api.GetStopPaymentBackendDelegate;
import com.temenos.infinity.api.chequemanagement.businessdelegate.api.GetStopPaymentBusinessDelegate;
import com.temenos.infinity.api.chequemanagement.dto.StopPayment;
import com.temenos.infinity.api.commons.exception.ApplicationException;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class GetStopPaymentBusinessDelegateImpl implements GetStopPaymentBusinessDelegate {
    private static final Logger LOG = LogManager.getLogger(GetStopPaymentBusinessDelegateImpl.class);

    @Override
    public List<StopPayment> getStopPaymentRequests(StopPayment stopPaymentInstance, DataControllerRequest request)
            throws ApplicationException {
        
        GetStopPaymentBackendDelegate orderBackendDelegate = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(GetStopPaymentBackendDelegate.class);
        List<StopPayment> stopPaymentList = orderBackendDelegate.getStopPaymentOrdersFromOMS(stopPaymentInstance, request);
        return stopPaymentList;   
    }

}

