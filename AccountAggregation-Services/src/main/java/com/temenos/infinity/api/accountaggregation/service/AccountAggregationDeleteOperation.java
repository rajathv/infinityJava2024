package com.temenos.infinity.api.accountaggregation.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.TokenUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.accountaggregation.constant.MSCertificateConstants;
import com.temenos.infinity.api.accountaggregation.resource.api.AccountAggregationResource;
import com.temenos.infinity.api.accountaggregation.resource.impl.AccountAggregationResourceImpl;
import com.temenos.infinity.api.accountaggregation.utils.AccountAggregationUtils;
import com.temenos.infinity.transact.tokenmanager.exception.CertificateNotRegisteredException;

public class AccountAggregationDeleteOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(AccountAggregationResourceImpl.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {

        AccountAggregationResource providerResource =
                DBPAPIAbstractFactoryImpl.getResource(AccountAggregationResource.class);
        String authToken = "";
        try {
        	authToken = TokenUtils.getAccAggMSAuthToken(request);
        } catch (CertificateNotRegisteredException e) {
			LOG.error("Certificate Not Registered" + e);
		} catch (Exception e) {
			LOG.error("Exception occured during generation of authToken " + e);
		}
        String CompanyId = AccountAggregationUtils.getUserAttributeFromIdentity(request, MSCertificateConstants.COMPANY_ID);
        Result result = providerResource.deleteOperation(request.getParameter("digitalProfileId"),
                request.getParameter("providerCode"), authToken, CompanyId);
        return result;
    }

}
