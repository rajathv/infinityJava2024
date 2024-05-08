package com.temenos.infinity.api.chequemanagement.businessdelegate.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.chequemanagement.backenddelegate.api.CreateStopPaymentBackendDelegate;
import com.temenos.infinity.api.chequemanagement.businessdelegate.api.CreateStopPaymentBusinessDelegate;
import com.temenos.infinity.api.chequemanagement.dto.StopPayment;
import com.temenos.infinity.api.commons.exception.ApplicationException;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class CreateStopPaymentBusinessDelegateImpl implements CreateStopPaymentBusinessDelegate {
    private static final Logger LOG = LogManager.getLogger(CreateStopPaymentBusinessDelegateImpl.class);

    @Override
    public StopPayment createStopPayment(StopPayment stopPaymentInput, DataControllerRequest request)
            throws ApplicationException {
        
        CreateStopPaymentBackendDelegate orderBackendDelegate = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(CreateStopPaymentBackendDelegate.class);
        StopPayment stopPaymentDTO = orderBackendDelegate.createStopPaymentOrder(stopPaymentInput, request); 
        return stopPaymentDTO; 
    }
    
    @Override
    public StopPayment validateStopPayment(StopPayment stopPaymentInput, DataControllerRequest request)
            throws ApplicationException {
        
        CreateStopPaymentBackendDelegate orderBackendDelegate = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(CreateStopPaymentBackendDelegate.class);
        StopPayment stopPaymentDTO = orderBackendDelegate.validateStopPaymentOrder(stopPaymentInput, request); 
        return stopPaymentDTO;  
    }

}
