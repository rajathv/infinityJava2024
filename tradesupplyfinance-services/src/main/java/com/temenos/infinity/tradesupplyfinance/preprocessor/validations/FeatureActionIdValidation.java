/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.preprocessor.validations;

import com.dbp.core.object.task.ObjectProcessorTask;
import com.google.gson.JsonObject;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author k.meiyazhagan
 */
public class FeatureActionIdValidation implements ObjectProcessorTask {
    private static final Logger LOG = LogManager.getLogger(FeatureActionIdValidation.class);

    @Override
    public boolean process(FabricRequestManager fabricRequestManager, FabricResponseManager fabricResponseManager) throws Exception {
        Result result;
        Map<String, String> dataMap = new HashMap<>();

        if (StringUtils.isBlank(HelperMethods.getCustomerIdFromSession(fabricRequestManager))) {
            updateErrorCode(fabricResponseManager);
            return false;
        }

        try {
            Map<String, Object> customerSecurityAttributes = fabricRequestManager.getServicesManager().getIdentityHandler().getSecurityAttributes();

            dataMap.put(DBPUtilitiesConstants.FILTER, "operation " + DBPUtilitiesConstants.EQUAL + fabricRequestManager.getServicesManager().getOperationData().getOperationId());
            result = HelperMethods.callApi(fabricRequestManager, dataMap, HelperMethods.getHeaders(fabricRequestManager), URLConstants.SERVICE_PERMISSION_MAPPER_READ);

            String permission = result.getAllDatasets().get(0).getAllRecords().get(0).getParamValueByName("permissions");
            if (getPermittedActionIds(customerSecurityAttributes, Collections.singletonList(permission)) == null) {
                updateErrorCode(fabricResponseManager);
                return false;
            }
            return true;
        } catch (Exception e) {
            updateErrorCode(fabricResponseManager);
            return false;
        }
    }

    private void updateErrorCode(FabricResponseManager fabricResponseManager) {
        JsonObject resPayload = null;
        if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
            resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        }
        resPayload = ErrorCodeEnum.ERR_12007.setErrorCode(resPayload);
        fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
    }

    public static String getPermittedActionIds(Map<String, Object> customerSecurityAttributes, List<String> requiredActionIds) {

        try {
            Object permissionsObj = customerSecurityAttributes.get("permissions");

            if (permissionsObj != null) {
                String activeActions = "";
                String permissions = permissionsObj.toString();
                permissions = permissions.replaceAll("\"", "");
                permissions = permissions.substring(1, permissions.length() - 1);

                List<String> permissionList = Arrays.asList(permissions.split(","));
                Set<String> result = permissionList.stream().distinct().filter(requiredActionIds::contains).collect(Collectors.toSet());

                if (!result.isEmpty()) {
                    activeActions = result.toString();
                    activeActions = activeActions.replaceAll("\\s", "");
                    activeActions = activeActions.substring(1, activeActions.length() - 1);
                    return activeActions;
                }
            }

        } catch (NullPointerException e) {
            LOG.error("Error occurred while fetching feature ations. " + e);
        }
        return null;
    }
}
