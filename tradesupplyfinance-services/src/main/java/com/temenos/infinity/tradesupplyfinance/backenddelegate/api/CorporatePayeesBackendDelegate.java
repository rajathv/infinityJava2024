/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.backenddelegate.api;

import com.dbp.core.api.BackendDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradesupplyfinance.dto.CorporatePayeesDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author k.meiyazhagan
 */
public interface CorporatePayeesBackendDelegate extends BackendDelegate {
    List<CorporatePayeesDTO> getCorporatePayees(Map<String, List<String>> inputCifMap, HashMap inputParams, String select, String filter, DataControllerRequest request)
            throws Exception;
}
