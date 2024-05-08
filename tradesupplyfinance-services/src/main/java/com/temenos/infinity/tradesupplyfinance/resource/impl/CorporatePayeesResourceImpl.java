/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.resource.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.businessdelegate.api.AuthorizationChecksBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.businessdelegate.api.CorporatePayeesBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants;
import com.temenos.infinity.tradesupplyfinance.dto.CorporatePayeesDTO;
import com.temenos.infinity.tradesupplyfinance.resource.api.CorporatePayeesResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * @author k.meiyazhagan
 */
public class CorporatePayeesResourceImpl implements CorporatePayeesResource {
    private static final Logger LOG = LogManager.getLogger(CorporatePayeesResourceImpl.class);
    private AuthorizationChecksBusinessDelegate authBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(AuthorizationChecksBusinessDelegate.class);
    private final CorporatePayeesBusinessDelegate corporatePayeesBusiness = DBPAPIAbstractFactoryImpl.getBusinessDelegate(CorporatePayeesBusinessDelegate.class);

    public Result getCorporatePayees(HashMap inputParams, DataControllerRequest request) {
        Result result = new Result();
        String inputCif = (String) inputParams.get("cif");

        List<String> featureActions = new ArrayList<>();
        featureActions.add(TradeSupplyFinanceConstants.TRADESUPPLYFINANCE_PAYEE_VIEW);
        Map<String, List<String>> inputCifMap;

        if (StringUtils.isBlank(inputCif)) {
            inputCifMap = authBusinessDelegate.getAuthorizedCifs(featureActions, request.getHeaderMap(), request);
            if (inputCifMap == null || inputCifMap.isEmpty()) {
                LOG.error("The logged in user doesn't have permission to perform this action");
                return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
            }
        } else {
            inputCifMap = getContractCifMap(inputCif);
            if (!authBusinessDelegate.isUserAuthorizedForFeatureAction(featureActions, inputCifMap, request.getHeaderMap(), request)) {
                LOG.error("The logged in user doesn't have permission to perform this action");
                return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
            }
        }

        List<CorporatePayeesDTO> payeesDtoList;
        try {
            payeesDtoList = corporatePayeesBusiness.getCorporatePayees(inputCifMap, inputParams, request);
        } catch (Exception e) {
            result.addParam("dbpErrMsg", "Unable to fetch payees, Please try later.");
            return result;
        }
        Set<CorporatePayeesDTO> payeesSet = new HashSet<>(payeesDtoList);

        JSONObject responseObj1 = new JSONObject();
        responseObj1.put("Payees", payeesSet);
        result = JSONToResult.convert(responseObj1.toString());
        return result;
    }

    private Map<String, List<String>> getContractCifMap(String cifs) {
        Map<String, List<String>> contractCifMap = new HashMap<String, List<String>>();
        JSONArray cifArr = new JSONArray(cifs);
        for (int i = 0; i < cifArr.length(); i++) {
            JSONObject contractObject = cifArr.getJSONObject(i);
            if (StringUtils.isNotBlank(contractObject.getString("coreCustomerId"))) {
                List<String> coreCustomerIds = Arrays.asList(contractObject.getString("coreCustomerId").split(","));
                contractCifMap.put(contractObject.getString("contractId"), coreCustomerIds);
            }
        }
        return contractCifMap;
    }
}
