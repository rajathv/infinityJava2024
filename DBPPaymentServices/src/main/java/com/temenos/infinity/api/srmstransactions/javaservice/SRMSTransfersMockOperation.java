package com.temenos.infinity.api.srmstransactions.javaservice;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import org.json.JSONObject;

public class SRMSTransfersMockOperation implements JavaService2 {
    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) throws Exception {
        JSONObject responseObj = new JSONObject();
        responseObj.put("status", "SUCCESS");
        return JSONToResult.convert(responseObj.toString());
    }
}
