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
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetCustomerTermsAndConditions implements JavaService2 {
	
	private static final Logger LOG = LogManager.getLogger(GetCustomerTermsAndConditions.class);
    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
        String termsAndConditionsCode = null;
        String language = DBPUtilitiesConstants.TNC_DEFAULT_LANGUAGE;
        String versionIdfromAdmin = null;
        String versionId = null;
        String filter = null;
        String customerId = HelperMethods.getCustomerIdFromSession(dcRequest);
        Result result = new Result();
        Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        
        String leid = dcRequest.getParameter("legalEntityId");
        if(StringUtils.isBlank(leid)) {
        	leid = LegalEntityUtil.getLegalEntityIdFromSessionOrCache(dcRequest);
        }
        LOG.debug(" legal entity id for the service excution is : "+leid);

        if (StringUtils.isNotBlank(inputParams.get("languageCode"))) {
            language = inputParams.get("languageCode");
        }
        termsAndConditionsCode = inputParams.get("termsAndConditionsCode");

        if (StringUtils.isBlank(termsAndConditionsCode)) {
            ErrorCodeEnum.ERR_10184.setErrorCode(result);
            return result;
        }
        
        if (StringUtils.isBlank(leid)) {
            ErrorCodeEnum.ERR_29040.setErrorCode(result);
            return result;
        }
        
        inputParams.put("appId", HelperMethods.getAppId(dcRequest));
        inputParams.put("legalEntityId", leid);
        HelperMethods.removeNullValues(inputParams);
        Result termsAndConditionsFromAdmin =
                AdminUtil.invokeAPI(inputParams, URLConstants.GET_TERMS_AND_CONDITIONS, dcRequest);
        if (HelperMethods.hasDBPErrorMSG(termsAndConditionsFromAdmin)) {
            ErrorCodeEnum.ERR_10188.setErrorCode(result);
            return result;
        }
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
        return termsAndConditionsFromAdmin;
    }
}
