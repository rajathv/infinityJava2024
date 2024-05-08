package com.temenos.infinity.api.chequemanagement.businessdelegate.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.chequemanagement.backenddelegate.api.RevokeStopPaymentBackendDelegate;
import com.temenos.infinity.api.chequemanagement.businessdelegate.api.RevokeStopPaymentBusinessDelegate;
import com.temenos.infinity.api.chequemanagement.dto.StopPayment;
import com.temenos.infinity.api.commons.exception.ApplicationException;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class RevokeStopPaymentBusinessDelegateImpl implements RevokeStopPaymentBusinessDelegate {
    private static final Logger LOG = LogManager.getLogger(RevokeStopPaymentBusinessDelegateImpl.class);

    @Override
    public StopPayment revokeStopPayment(StopPayment stopPaymentInput, DataControllerRequest request)
            throws ApplicationException {
        
    	RevokeStopPaymentBackendDelegate orderBackendDelegate = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(RevokeStopPaymentBackendDelegate.class);
        StopPayment stopPaymentDTO = orderBackendDelegate.revokeStopPaymentOrder(stopPaymentInput, request); 
        return stopPaymentDTO; 
    }
    
    @Override
    public StopPayment validateRevokeStopPayment(StopPayment stopPaymentInput, DataControllerRequest request)
            throws ApplicationException {
        
    	RevokeStopPaymentBackendDelegate orderBackendDelegate = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(RevokeStopPaymentBackendDelegate.class);
        StopPayment stopPaymentDTO = orderBackendDelegate.validateRevokeStopPaymentOrder(stopPaymentInput, request); 
        return stopPaymentDTO;  
    }

}
