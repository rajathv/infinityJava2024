package com.temenos.dbx.consents.javaservice;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.infinity.dbx.dbp.jwt.auth.utils.CommonUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

public class CreateOrderForConsent implements JavaService2{

	private static final Logger logger = LogManager.getLogger(com.temenos.dbx.consents.javaservice.CreateOrderForConsent.class);
	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			HashMap<String, Object> params = (HashMap<String, Object>) inputArray[1];
			HashMap<String, Object> serviceHeaders = new HashMap<String, Object>();
			String serviceName = "ServiceRequestJavaService";
			String operationName = "createOrder";
			result = CommonUtils.callIntegrationService(request, params, serviceHeaders, serviceName, operationName,
						true);
		} catch (Exception e) {
			Result errorResult = new Result();
			logger.error("Exception while invoking ServiceRequestJavaService:" + e);
			return errorResult;
		}
		return result;
	}
}