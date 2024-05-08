/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.businessdelegate.api;

import com.dbp.core.api.BusinessDelegate;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradesupplyfinance.dto.CorporatePayeesDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author k.meiyazhagan
 */
public interface CorporatePayeesBusinessDelegate extends BusinessDelegate {
    List<CorporatePayeesDTO> getCorporatePayees(Map<String, List<String>> inputCifMap, HashMap inputParams, DataControllerRequest request) throws Exception;
}
