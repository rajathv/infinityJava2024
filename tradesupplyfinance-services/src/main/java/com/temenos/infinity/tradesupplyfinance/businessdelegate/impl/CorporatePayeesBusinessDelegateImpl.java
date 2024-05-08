/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.businessdelegate.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradesupplyfinance.backenddelegate.api.CorporatePayeesBackendDelegate;
import com.temenos.infinity.tradesupplyfinance.businessdelegate.api.CorporatePayeesBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.dto.CorporatePayeesDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author k.meiyazhagan
 */
public class CorporatePayeesBusinessDelegateImpl implements CorporatePayeesBusinessDelegate {

    CorporatePayeesBackendDelegate corporatePayeesBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(CorporatePayeesBackendDelegate.class);

    public List<CorporatePayeesDTO> getCorporatePayees(Map<String, List<String>> inputCifMap, HashMap inputParams, DataControllerRequest request)
            throws Exception {

        String select = "name, address1, address2, city, state, country, zipcode";
        return corporatePayeesBackendDelegate.getCorporatePayees(inputCifMap, inputParams, select, null, request);
    }

}
