/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetGuranteesLimitInstruction implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetGuranteesLimitInstruction.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {

        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        String customerId = CustomerSession.getCustomerId(customer);

        String[] limits = {".0008100.01", ".0008110.01", ".00009900.01"};
        List<HashMap> limitsArray = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            HashMap<String, String> limit = new HashMap<>();
            limit.put("Limit iD", customerId + limits[i]);
            limit.put("Limit Currency", "USD");
            limit.put("Os Amount", "-515,000.00");
            limitsArray.add(limit);
        }
        return JSONToResult.convert((new JSONObject()).put("Limits", limitsArray).toString());
    }
}