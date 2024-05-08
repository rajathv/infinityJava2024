package com.temenos.dbx.product.businessdelegate.impl;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPDatasetConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.JSONUtil;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.product.businessdelegate.api.BusinessConfigurationBusinessDelegate;

public class BusinessConfigurationBusinessDelegateImpl implements BusinessConfigurationBusinessDelegate {

    LoggerUtil logger = new LoggerUtil(BusinessConfigurationBusinessDelegateImpl.class);

    @Override
    public String getAutoApprovalStatus(Map<String, Object> headersMap) throws ApplicationException {

        String approvalStatus = DBPUtilitiesConstants.CONTRACT_STATUS_PENDING;
        try {

            Map<String, Object> inputParams = new HashMap<>();
            String filterQuery = "key" + DBPUtilitiesConstants.EQUAL + "BUSINESS_ENROLLMENT_AUTO_APPROVAL";
            inputParams.put(DBPUtilitiesConstants.FILTER, filterQuery);
            JsonObject businessConfigurationJson = ServiceCallHelper.invokeServiceAndGetJson(inputParams, headersMap,
                    URLConstants.BUSINESSCONFIGURATION_GET);

            if (JSONUtil.isJsonNotNull(businessConfigurationJson)
                    && JSONUtil.hasKey(businessConfigurationJson, DBPDatasetConstants.DATASET_BUSINESSCONFIGURATION)
                    && businessConfigurationJson.get(DBPDatasetConstants.DATASET_BUSINESSCONFIGURATION)
                            .isJsonArray()) {
                JsonArray configurationArray =
                        businessConfigurationJson.get(DBPDatasetConstants.DATASET_BUSINESSCONFIGURATION)
                                .getAsJsonArray();
                JsonObject object =
                        configurationArray.size() > 0 ? configurationArray.get(0).getAsJsonObject() : new JsonObject();
                String value = object.has("value") ? object.get("value").getAsString() : "";
                approvalStatus =
                        "1".equals(value) || "true".equals(value) ? DBPUtilitiesConstants.CONTRACT_STATUS_ACTIVE
                                : approvalStatus;

            } else {
            	logger.debug("businessconfiguration get unsuccessful");
                throw new ApplicationException(ErrorCodeEnum.ERR_10283);
            }
        } catch (Exception e) {
            logger.error("Exception while getting AutoApprovalStatus", e);
            throw new ApplicationException(ErrorCodeEnum.ERR_10283);
        }

        return approvalStatus;
    }

}
