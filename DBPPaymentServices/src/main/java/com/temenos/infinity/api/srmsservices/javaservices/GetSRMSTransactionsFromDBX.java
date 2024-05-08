package com.temenos.infinity.api.srmsservices.javaservices;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class GetSRMSTransactionsFromDBX implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetSRMSTransactionsFromDBX.class);
    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) throws Exception {
        //InfinityTransactionServices.getUserTransaction
        try {
            HashMap<String, Object> inputMap = (HashMap<String, Object>) objects[1];
            inputMap.put("isScheduled", 1);
            String createOrder = DBPServiceExecutorBuilder.builder()
                    .withServiceId("InfinityTransactionServices")
                    .withOperationId("getUserTransaction")
                    .withRequestParameters(inputMap)
                    .withDataControllerRequest(dataControllerRequest)
                    .build().getResponse();
            JSONObject json = new JSONObject(createOrder);
            ArrayList<LinkedTreeMap<String, Object>> arrayList = new Gson().fromJson(json.getJSONArray("Transactions").toString(), ArrayList.class);
            arrayList.forEach(map -> {
                map.put("statusDesc", "Active");
                map.put("frequencyStartDate", map.get("scheduledDate"));
            });
            //arrayList = (ArrayList<LinkedTreeMap>) arrayList.stream().map(obj -> obj.put("statusDescription", "Active")).collect(Collectors.toList());
            json.put("Transactions", arrayList);
            return JSONToResult.convert(json.toString());
        } catch (Exception e) {
            LOG.error("Error occurred while sending request.");
        }
        return new Result();
    }
}
