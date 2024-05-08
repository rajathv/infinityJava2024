package com.temenos.infinity.api.accountaggregation.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.TokenUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.accountaggregation.resource.api.AccountAggregationResource;
import com.temenos.infinity.api.accountaggregation.resource.impl.AccountAggregationResourceImpl;
import com.temenos.infinity.transact.tokenmanager.exception.CertificateNotRegisteredException;

public class AccountAggregationUpdateStatusOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(AccountAggregationResourceImpl.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {

        AccountAggregationResource providerResource =
                DBPAPIAbstractFactoryImpl.getResource(AccountAggregationResource.class);

        String data = request.getParameter("data");
        JSONObject dataObj = new JSONObject(data);

        String stage = dataObj.getString("stage");
        JSONObject custom_fields = dataObj.getJSONObject("custom_fields");

        String digitalProfileId = custom_fields.getString("digitalprofileid");
        String providerCode = custom_fields.getString("providercode");
        String authToken = "";
        try {
        	authToken = TokenUtils.getAccAggMSAuthToken(request);
		} catch (CertificateNotRegisteredException e) {
			LOG.error("Certificate Not Registered" + e);
		} catch (Exception e) {
			LOG.error("Exception occured during generation of authToken " + e);
		}
        Result result = providerResource.updateStatus(stage, digitalProfileId, providerCode, authToken);
        return result;
    }
}
