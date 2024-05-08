/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradesupplyfinance.backenddelegate.api.CorporatePayeesBackendDelegate;
import com.temenos.infinity.tradesupplyfinance.config.TradeSupplyFinanceAPIServices;
import com.temenos.infinity.tradesupplyfinance.dto.CorporatePayeesDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * @author k.meiyazhagan
 */
public class CorporatePayeesBackendDelegateImpl implements CorporatePayeesBackendDelegate {

    public List<CorporatePayeesDTO> getCorporatePayees(Map<String, List<String>> inputCifMap, HashMap inputParams, String select, String externalFilter, DataControllerRequest request)
            throws Exception {
        List<CorporatePayeesDTO> dtos;
        Set<String> associatedCifs = getContractCifs(inputCifMap);
        Map<String, Object> requestParams = new HashMap<>();
        StringBuilder filter = new StringBuilder();
        filter.append("(");
        for (String cif : associatedCifs) {
            if (filter.length() == 1) {
                filter.append("cif" + DBPUtilitiesConstants.EQUAL).append(cif);
            } else
                filter.append(DBPUtilitiesConstants.OR).append("cif").append(DBPUtilitiesConstants.EQUAL).append(cif);
        }
        filter.append(")");
        if (externalFilter != null)
            filter.append(externalFilter);

        requestParams.put(DBPUtilitiesConstants.FILTER, filter.toString());
        requestParams.put("$select", select);

        String fetchResponse = DBPServiceExecutorBuilder.builder().
                withServiceId(TradeSupplyFinanceAPIServices.DBPRBLOCALSERVICE_PAYEE_GET.getServiceName()).
                withObjectId(null).
                withOperationId(TradeSupplyFinanceAPIServices.DBPRBLOCALSERVICE_PAYEE_GET.getOperationName()).
                withRequestParameters(requestParams).
                build().getResponse();

        JSONObject jsonRsponse = new JSONObject(fetchResponse);
        JSONArray countJsonArray = jsonRsponse.getJSONArray("corporatepayees");
        dtos = JSONUtils.parseAsList(countJsonArray.toString(), CorporatePayeesDTO.class);

        return dtos;

    }

    private Set<String> getContractCifs(Map<String, List<String>> cifsMap) {
        Set<String> associatedCifs = new HashSet<>();
        for (String key : cifsMap.keySet()) {
            associatedCifs.addAll(cifsMap.get(key));
        }
        return associatedCifs;
    }
}
