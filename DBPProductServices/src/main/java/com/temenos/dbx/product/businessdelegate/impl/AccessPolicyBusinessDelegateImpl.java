package com.temenos.dbx.product.businessdelegate.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.product.businessdelegate.api.AccessPolicyBusinessDelegate;
import com.temenos.dbx.product.dto.DBXResult;

/**
 * 
 * @author sowmya.vandanapu
 *
 */
public class AccessPolicyBusinessDelegateImpl implements AccessPolicyBusinessDelegate {
    LoggerUtil logger = new LoggerUtil(AccessPolicyBusinessDelegateImpl.class);

    @Override
    public DBXResult getAccessPolicies(Map<String, Object> headersMap) throws ApplicationException {
        DBXResult result = new DBXResult();
        try {
            Map<String, Object> inputParams = new HashMap<>();
            JsonObject response = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headersMap,
                    URLConstants.ACCESSPOLICY_GET);
            if (response != null && JSONUtil.hasKey(response, DBPDatasetConstants.DATASET_ACCESSPOLICY)
                    && response.get(DBPDatasetConstants.DATASET_ACCESSPOLICY) != null
                    && response.get(DBPDatasetConstants.DATASET_ACCESSPOLICY).getAsJsonArray().size() > 0) {
                result.setResponse(response.get(DBPDatasetConstants.DATASET_ACCESSPOLICY).getAsJsonArray());
            }
        } catch (Exception e) {
            logger.error("Exception occured while fetching access policies" + e.getMessage());
            throw new ApplicationException(ErrorCodeEnum.ERR_10787);
        }
        return result;
    }

}
