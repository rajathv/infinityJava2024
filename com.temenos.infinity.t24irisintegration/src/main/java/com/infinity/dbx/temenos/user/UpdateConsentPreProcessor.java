package com.infinity.dbx.temenos.user;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.TemenosBasePreProcessor;
import com.infinity.dbx.temenos.transactions.TransactionConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

import org.json.JSONObject;

public class UpdateConsentPreProcessor extends TemenosBasePreProcessor {

    private static final String CONSENTS = "consent";
    private static final Logger LOG = LogManager.getLogger(UpdateConsentPreProcessor.class);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean execute(  HashMap params, DataControllerRequest request,
            DataControllerResponse response,
            Result result) throws Exception {

        try {
            LOG.debug("In " + UpdateConsentPreProcessor.class.getName());
            JSONObject consent = null;
            super.execute(params, request, response, result);
            
            params.put(CONSENTS,((String) params.get(CONSENTS)).replace("\'","\""));
	}catch (Exception e) {
        LOG.error("Exception in " + UpdateConsentPreProcessor.class.getName(), e);
    }
        return Boolean.TRUE;   
	}

}
