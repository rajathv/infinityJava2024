package com.temenos.dbx.product.approvalmatrixservices.backenddelegate.impl;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.temenos.dbx.product.approvalmatrixservices.backenddelegate.api.ApprovalMatrixBackendDelegate;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ApprovalMatrixBackendDelegateImpl implements ApprovalMatrixBackendDelegate {

    private static final Logger LOG = LogManager.getLogger(ApprovalMatrixBackendDelegateImpl.class);
    @Override
    public Map<String, JSONObject> fetchFeatureActionsEligibleForApproval(String legalEntityId) {
        String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_FEATUREACTION_GET;
        Map<String, Object> requestParameters = new HashMap<String, Object>();
        Map<String, JSONObject> featureActionObjectMap = new HashMap<>();

        requestParameters.put(DBPUtilitiesConstants.FILTER, "companyLegalUnit" + DBPUtilitiesConstants.EQUAL + legalEntityId +
                DBPUtilitiesConstants.AND + "approveFeatureAction" + DBPUtilitiesConstants.NOT_EQ + "null");

        try {

            String response = DBPServiceExecutorBuilder.builder()
                    .withServiceId(serviceName)
                    .withObjectId(null)
                    .withOperationId(operationName)
                    .withRequestParameters(requestParameters)
                    .build().getResponse();

            JSONObject resObj = new JSONObject(response);
            if (resObj.has("opstatus") && resObj.getInt("opstatus") == 0) {
                JSONArray resArr = resObj.getJSONArray("featureaction");
                if (resArr.length() > 0) {
                    for(Object obj : resArr){
                        JSONObject featureActionObj = (JSONObject) obj;
                        featureActionObjectMap.put(featureActionObj.getString("id"), featureActionObj);
                    }
                    return featureActionObjectMap;
                } else {
                    LOG.error("FEATUREACTION table entry not found for the COMPANYLEGALUNIT: " + legalEntityId);
                    return null;
                }
            } else {
                LOG.error("Failed to read FEATUREACTION table.");
                return null;
            }
        } catch (JSONException je) {
            LOG.error("Exception occured while parsing FEATUREACTION_GET operation JSON response: ", je);
            return null;
        } catch (DBPApplicationException dbpE) {
            LOG.error("Exception occured while reading FEATUREACTION table: " + dbpE);
            return null;
        }
    }

    /**
     *
     * @param coreCustomerId
     * @param contractId
     * @return 1 - if group-based approval matrix, 0 - if user-based approval matrix
     */
    @Override
    public Integer fetchApprovalMode(String coreCustomerId, String contractId, String legalEntityId) {
        Map<String, Object> reqParams = new HashMap<>();
        reqParams.put(DBPUtilitiesConstants.FILTER, "coreCustomerId" + DBPUtilitiesConstants.EQUAL + coreCustomerId +
                DBPUtilitiesConstants.AND + "contractId" + DBPUtilitiesConstants.EQUAL + contractId +
                DBPUtilitiesConstants.AND + "legalEntityId" + DBPUtilitiesConstants.EQUAL + legalEntityId);

        try {
            String response = DBPServiceExecutorBuilder.builder()
                    .withServiceId(ServiceId.DBPRBLOCALSERVICEDB)
                    .withObjectId(null)
                    .withOperationId(OperationName.DB_APPROVALMODE_GET)
                    .withRequestParameters(reqParams)
                    .withRequestHeaders(null)
                    .build().getResponse();

            JSONObject resObj = new JSONObject(response);
            if (resObj.has("opstatus") && resObj.getInt("opstatus") == 0) {
                JSONArray resArr = resObj.getJSONArray("approvalmode");
                if (resArr.length() > 0) {
                    JSONObject approvalModeObj = (JSONObject) resArr.get(0);
                    return approvalModeObj.getInt("isGroupLevel");
                } else {
                    LOG.error("APPROVALMODE table entry not found for the CIF: " + coreCustomerId + " and CONTRACT: " + contractId);
                    return null;
                }
            } else {
                LOG.error("Failed to read APPROVALMODE table.");
                return null;
            }
        } catch (JSONException jsonE) {
            LOG.error("Exception occured while parsing APPROVALMODE_GET operation JSON response: " + jsonE);
            return null;
        } catch (DBPApplicationException dbpE) {
            LOG.error("Exception occured while reading APPROVALMODE table: " + dbpE);
            return null;
        }
    }
}
