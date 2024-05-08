/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.mapper;

import com.dbp.core.api.BackendDelegate;
import com.dbp.core.api.DBPAPIMapper;
import com.temenos.infinity.tradesupplyfinance.backenddelegate.api.*;
import com.temenos.infinity.tradesupplyfinance.backenddelegate.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author k.meiyazhagan
 */
public class TradeSupplyFinanceBackendDelegateMapper implements DBPAPIMapper<BackendDelegate> {

    @Override
    public Map<Class<? extends BackendDelegate>, Class<? extends BackendDelegate>> getAPIMappings() {
        Map<Class<? extends BackendDelegate>, Class<? extends BackendDelegate>> backendDelegateMap = new HashMap<>();
        backendDelegateMap.put(ReceivableSingleBillBackendDelegate.class, ReceivableSingleBillBackendDelegateImpl.class);
        backendDelegateMap.put(ReceivableCsvImportBackendDelegate.class, ReceivableCsvImportBackendDelegateImpl.class);
        backendDelegateMap.put(CorporatePayeesBackendDelegate.class, CorporatePayeesBackendDelegateImpl.class);
        backendDelegateMap.put(DocumentsBackendDelegate.class, DocumentsBackendImpl.class);

        return backendDelegateMap;
    }

    @Override
    public Class<BackendDelegate> getParameterizedAPITypeClass() {
        return DBPAPIMapper.super.getParameterizedAPITypeClass();
    }
}
