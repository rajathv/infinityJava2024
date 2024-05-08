package com.temenos.infinity.api.accountaggregation.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.LegalEntityUtil;
import com.kony.dbputilities.util.TokenUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.accountaggregation.constant.ErrorCodeEnum;
import com.temenos.infinity.api.accountaggregation.resource.api.AccountAggregationResource;
import com.temenos.infinity.api.accountaggregation.resource.impl.AccountAggregationResourceImpl;
import com.temenos.infinity.transact.tokenmanager.exception.CertificateNotRegisteredException;

public class AccountAggregationCreateTermsAndConditionsOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(AccountAggregationResourceImpl.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {

        try {

            AccountAggregationResource customerResource =
                    DBPAPIAbstractFactoryImpl.getResource(AccountAggregationResource.class);
            String scopes = request.getParameter("scopes").replace("'", "\"");
            String fetch_scopes = request.getParameter("fetch_scopes").replace("'", "\"");
            String operation =request.getParameter("operation");
            String authToken = "";
            try {
            	authToken = TokenUtils.getAccAggMSAuthToken(request);
    		} catch (CertificateNotRegisteredException e) {
    			LOG.error("Certificate Not Registered" + e);
    		} catch (Exception e) {
    			LOG.error("Exception occured during generation of authToken " + e);
    		}
           // String CompanyId = AccountAggregationUtils.getUserAttributeFromIdentity(request, MSCertificateConstants.COMPANY_ID);
            String CompanyId = LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request);
            Result result = customerResource.createTermsAndConditions(request.getParameter("digitalProfileId"),
                    request.getParameter("javascript_callback_type"), request.getParameter("from_date"), scopes,
                    request.getParameter("providerCode"), request.getParameter("period_days"), fetch_scopes,operation, authToken, CompanyId);
            return result;
        } catch (Exception e) {
            LOG.error(e);
            return ErrorCodeEnum.ERR_20001.setErrorCode(new Result());
        }
    }

}
