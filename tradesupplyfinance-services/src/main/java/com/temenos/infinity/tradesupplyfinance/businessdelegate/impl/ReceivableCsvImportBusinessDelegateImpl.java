/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.businessdelegate.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradesupplyfinance.backenddelegate.api.ReceivableCsvImportBackendDelegate;
import com.temenos.infinity.tradesupplyfinance.businessdelegate.api.ReceivableCsvImportBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableCsvImportDTO;
import com.temenos.infinity.tradesupplyfinance.utils.ExportListBusinessDelegate;

import java.util.List;

/**
 * @author k.meiyazhagan
 */
public class ReceivableCsvImportBusinessDelegateImpl implements ReceivableCsvImportBusinessDelegate, ExportListBusinessDelegate {

    private final ReceivableCsvImportBackendDelegate requestBackend = DBPAPIAbstractFactoryImpl.getBackendDelegate(ReceivableCsvImportBackendDelegate.class);

    @Override
    public ReceivableCsvImportDTO createCsvImport(ReceivableCsvImportDTO inputDto, DataControllerRequest request) {
        return requestBackend.createCsvImport(inputDto, request);
    }

    @Override
    public ReceivableCsvImportDTO updateCsvImport(ReceivableCsvImportDTO inputDto, DataControllerRequest request) {
        return requestBackend.updateCsvImport(inputDto, request);
    }

    @Override
    public List<ReceivableCsvImportDTO> getCsvImports(DataControllerRequest request) {
        return requestBackend.getCsvImports(request);
    }

    @Override
    public ReceivableCsvImportDTO getCsvImportById(String fileReference, DataControllerRequest request) {
        return requestBackend.getCsvImportById(fileReference, request);
    }

    @Override
    public List<ReceivableCsvImportDTO> getRecordsList(DataControllerRequest request) {
        return requestBackend.getCsvImports(request);
    }

}
