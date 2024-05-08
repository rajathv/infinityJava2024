/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.businessdelegate.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.tradesupplyfinance.backenddelegate.api.ReceivableSingleBillBackendDelegate;
import com.temenos.infinity.tradesupplyfinance.businessdelegate.api.ReceivableSingleBillBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;
import com.temenos.infinity.tradesupplyfinance.utils.ExportListBusinessDelegate;

import java.util.List;

/**
 * @author k.meiyazhagan
 */
public class ReceivableSingleBillBusinessDelegateImpl implements ReceivableSingleBillBusinessDelegate, ExportListBusinessDelegate {

    private final ReceivableSingleBillBackendDelegate requestBackend = DBPAPIAbstractFactoryImpl.getBackendDelegate(ReceivableSingleBillBackendDelegate.class);

    @Override
    public ReceivableSingleBillDTO createSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        return requestBackend.createSingleBill(inputDto, request);
    }

    @Override
    public ReceivableSingleBillDTO updateSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        return requestBackend.updateSingleBill(inputDto, request);
    }

    @Override
    public List<ReceivableSingleBillDTO> getSingleBills(DataControllerRequest request) {
        return requestBackend.getSingleBills(request);
    }

    @Override
    public ReceivableSingleBillDTO getSingleBillById(String billReference, DataControllerRequest request) {
        return requestBackend.getSingleBillById(billReference, request);
    }

    @Override
    public List<ReceivableSingleBillDTO> getRecordsList(DataControllerRequest request) throws ApplicationException {
        return requestBackend.getSingleBills(request);
    }
}
