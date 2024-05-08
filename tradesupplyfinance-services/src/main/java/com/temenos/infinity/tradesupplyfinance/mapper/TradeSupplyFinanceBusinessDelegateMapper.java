/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.mapper;

import com.dbp.core.api.BusinessDelegate;
import com.dbp.core.api.DBPAPIMapper;
import com.temenos.infinity.tradesupplyfinance.businessdelegate.api.*;
import com.temenos.infinity.tradesupplyfinance.businessdelegate.impl.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author k.meiyazhagan
 */
public class TradeSupplyFinanceBusinessDelegateMapper implements DBPAPIMapper<BusinessDelegate> {

    @Override
    public Map<Class<? extends BusinessDelegate>, Class<? extends BusinessDelegate>> getAPIMappings() {
        Map<Class<? extends BusinessDelegate>, Class<? extends BusinessDelegate>> businessDelegateMap = new HashMap<>();
        businessDelegateMap.put(ReceivableSingleBillBusinessDelegate.class, ReceivableSingleBillBusinessDelegateImpl.class);
        businessDelegateMap.put(ReceivableCsvImportBusinessDelegate.class, ReceivableCsvImportBusinessDelegateImpl.class);
        businessDelegateMap.put(DocumentsBusinessDelegate.class, DocumentsBusinessDelegateImpl.class);
        businessDelegateMap.put(CorporatePayeesBusinessDelegate.class, CorporatePayeesBusinessDelegateImpl.class);

        return businessDelegateMap;
    }

    @Override
    public Class<BusinessDelegate> getParameterizedAPITypeClass() {
        return DBPAPIMapper.super.getParameterizedAPITypeClass();
    }
}
