package com.temenos.infinity.api.arrangements.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.config.ServerConfigurations;

public class ValidateAccountClosure implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(ValidateAccountClosure.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        
    	Result result = new Result();
    	
    	String accountId = request.getParameter("accountID") != null ? request.getParameter("accountID") : "";
    	
        String ACCOUNT_CLOSURE_OVERRIDE = ServerConfigurations.ACCOUNT_CLOSURE_OVERRIDE.getValueIfExists();
        
        if (ACCOUNT_CLOSURE_OVERRIDE!= null && ACCOUNT_CLOSURE_OVERRIDE.equals(accountId)) {
        	result.addStringParam("status", "Override");
        	result.addStringParam("description", "This account is the final open account");
			result.addOpstatusParam(0);
			result.addHttpStatusCodeParam(200);
			return result;
        }

        else {
        	result.addStringParam("status", "Valid");
			result.addOpstatusParam(0);
			result.addHttpStatusCodeParam(200);
			return result;
        }      
    }
}
