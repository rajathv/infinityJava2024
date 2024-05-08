package com.temenos.dbx.eum.product.contract.businessdelegate.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.temenos.dbx.eum.product.contract.backenddelegate.api.ContractFeatureActionsBackendDelegate;
import com.temenos.dbx.eum.product.contract.businessdelegate.api.ContractFeatureActionsBusinessDelegate;

public class ContractFeatureActionsBusinessDelegateImpl implements ContractFeatureActionsBusinessDelegate {
	
	private static final Logger LOG = LogManager.getLogger(
			ContractFeatureActionsBusinessDelegateImpl.class);

    @Override
    public Set<String> createContractFeatures(Set<String> featuresList, String contractId, String customerId,
            String serviceTypeID, String isDefaultActionsEnabled, String legalEntityId, Map<String, Object> headersMap)
            throws ApplicationException {
        ContractFeatureActionsBackendDelegate featureActionsBD = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(ContractFeatureActionsBackendDelegate.class);
        return featureActionsBD.createContractFeatures(featuresList, contractId, customerId, serviceTypeID,
                isDefaultActionsEnabled, legalEntityId, headersMap);
    }

    @Override
    public void createContractActionLimits(StringBuilder queryString, Map<String, Object> headersMap)
            throws ApplicationException {
        ContractFeatureActionsBackendDelegate featureActionsBD = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(ContractFeatureActionsBackendDelegate.class);
        featureActionsBD.createContractActionLimits(queryString, headersMap);

    }

    @Override
    public String getActionsWithApproveFeatureAction(String actionsCSV, String legalEntityId, Map<String, Object> headersMap)
            throws ApplicationException {
        ContractFeatureActionsBackendDelegate featureActionsBD = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(ContractFeatureActionsBackendDelegate.class);
        return featureActionsBD.getActionsWithApproveFeatureAction(actionsCSV, legalEntityId, headersMap);
    }

    @Override
    public void decreaseUserLimits(StringBuilder queryString, Map<String, Object> headersMap)
            throws ApplicationException {
        ContractFeatureActionsBackendDelegate featureActionsBD = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(ContractFeatureActionsBackendDelegate.class);
        featureActionsBD.decreaseUserLimits(queryString, headersMap);
    }
    
    @Override
    public void createDefaultContractActionLimits(String contractId, String legalEntityId, Map<String, Object> headersMap)
            throws ApplicationException {
        Map<String, Object> inputParams = new HashMap<>();
        try {
            inputParams.put("_contractId", contractId);
            inputParams.put("_legalEntityId", legalEntityId);
            ServiceCallHelper.invokeServiceAndGetJson(inputParams, headersMap,
                    URLConstants.DEAFULT_CONTRACTACTIONS_CREATE_PROC);
        } catch (Exception e) {
        	LOG.error("default contractactions create failed", e);
            throw new ApplicationException(ErrorCodeEnum.ERR_10359);
        }
    }

}
