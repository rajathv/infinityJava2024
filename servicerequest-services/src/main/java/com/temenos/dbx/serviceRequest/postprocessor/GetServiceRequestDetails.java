package com.temenos.dbx.serviceRequest.postprocessor;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import org.json.JSONObject;

public class GetServiceRequestDetails implements DataPostProcessor2{

	 private static final Logger LOG = LogManager.getLogger(GetServiceRequestDetails.class);
	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		//to mask the cardpin number 
		ArrayList<Record> record =result.getDatasetById("serviceReqs").getRecords();
		for (int i = 0; i < record.size(); i++) {
			String requestConfigId=record.get(i).getParamValueByName("requestConfigId");
			if(requestConfigId.equalsIgnoreCase("ApplyDebitCard")) {
				Record cardDetails=record.get(i).getRecordById("serviceReqRequestIn");
				if(StringUtils.isNotBlank(cardDetails.toString())) {
					if(cardDetails.hasParamByName("pinNumber")) {
						cardDetails.addParam("pinNumber", cardDetails.getParamValueByName("pinNumber").replaceAll("[0-9]", "*"));
					}
				}
			}
			
		}
		return result;
	}

}
