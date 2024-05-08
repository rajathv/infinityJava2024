package com.kony.AdminConsole.BLProcessor;

import org.apache.log4j.Logger;

import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.utils.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetPasswordPolicies implements JavaService2 {
	

	private static final Logger LOG = Logger.getLogger(GetPasswordPolicies.class);



    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
            DataControllerResponse responseInstance) throws Exception {
    	try {
            return AdminUtil.invokeAPI(HelperMethods.getInputParamMap(inputArray), URLConstants.GET_PASSWORD_POLICIES, requestInstance);
        } catch (Exception e) {
           LOG.error(e);
           return new Result();
        }
    }

}
