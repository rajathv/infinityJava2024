package com.kony.contentproductservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.LegalEntityUtil;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.UserAgentUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class CreateCustomerTNCForLogin implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(CreateCustomerTNCForLogin.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
        String termsAndConditionsCode = null;
        String language = DBPUtilitiesConstants.TNC_DEFAULT_LANGUAGE;
        String customerId = HelperMethods.getCustomerIdFromSession(dcRequest);
        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        termsAndConditionsCode = DBPUtilitiesConstants.TNC_LOGIN;
        
        String leid = dcRequest.getParameter("legalEntityId");
        if(StringUtils.isBlank(leid)) {
        	leid = LegalEntityUtil.getLegalEntityIdFromSessionOrCache(dcRequest);
        }

        if (StringUtils.isBlank(leid)) {
            ErrorCodeEnum.ERR_29040.setErrorCode(result);
            return result;
        }

        if (StringUtils.isNotBlank(inputParams.get("languageCode"))) {
            language = inputParams.get("languageCode");
        }

        inputParams.put("termsAndConditionsCode", termsAndConditionsCode);
        inputParams.put("languageCode", language);
        inputParams.put("appId", HelperMethods.getAppId(dcRequest));
        inputParams.put("legalEntityId", leid);
        
        Result termsAndConditionsFromAdmin =
                AdminUtil.invokeAPI(inputParams, URLConstants.GET_TERMS_AND_CONDITIONS, dcRequest);
        if (HelperMethods.hasDBPErrorMSG(termsAndConditionsFromAdmin)) {
            ErrorCodeEnum.ERR_10185.setErrorCode(result);
            return result;
        }

        UserAgentUtil ua = new UserAgentUtil(dcRequest);
        Map<String, Object> params = new HashMap<>();

        params.clear();
        params.put("partyId", customerId);
        Map<String, String> header = HelperMethods.getHeaders(dcRequest);
        header.put("companyid", leid);
        
        String authToken = AdminUtil.getForceLoginAccessToken();
        Result partyConsentDetails = ServiceCallHelper.invokeServiceAndGetResult(params,
        		header, URLConstants.GET_PARTY_CONSENT_DETAILS, authToken);

        if (partyConsentDetails != null) {
            Dataset ds = partyConsentDetails.getDatasetById("partyConsentDetails");
            if (ds != null && ds.getAllRecords().size() > 0) {
                String globalContentId = termsAndConditionsFromAdmin.getParamValueByName("contentId");
                for(Record record : ds.getAllRecords()) {
                    String contentId = record.getParamValueByName("termsNConditionContentId");
                    String consentLeId = HelperMethods.getFieldValue(record, "legalEntityId");
                    LOG.debug(" consent legal entity id for the service excution is : "+consentLeId);
                    if (contentId != null && globalContentId != null && contentId.equalsIgnoreCase(globalContentId)
                    		&& StringUtils.isNotBlank(consentLeId) && consentLeId.equalsIgnoreCase(leid)) {
                        result.addParam(new Param("alreadySigned", "true", DBPUtilitiesConstants.STRING_TYPE));
                        return result;
                    }
                }
            }
        }

        params.clear();
        params.put("masterConsentId", DBPUtilitiesConstants.CONSENT_MASTER_ID);
        params.put("bankName", "Infinity Bank");
        params.put("externalUserId", customerId);
        params.put("retentionPeriod", "30");
        params.put("status", "ACTIVE");
        params.put("backendIdentifier", customerId);
        params.put("channelId", "Online");
        params.put("termsNConditionContentId",
                termsAndConditionsFromAdmin.getParamValueByName("contentId"));
        params.put("consentGiven", "true");
        params.put("partyId", customerId);

        LOG.debug("create party consent details params " + params);
        HelperMethods.removeNullValues(params);
        result = ServiceCallHelper.invokeServiceAndGetResult(dcRequest, params,
        		header, URLConstants.CREATE_PARTY_CONSENT_DETAILS);

        if (result.hasParamByName("partyConsentId")) {
            result.removeParamByName("dbpErrCode");
            result.removeParamByName("dbpErrMsg");
        }
        return result;
    }

}
