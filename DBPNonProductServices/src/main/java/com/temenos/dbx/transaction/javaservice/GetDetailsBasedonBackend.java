package com.temenos.dbx.transaction.javaservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.dbp.jwt.auth.utils.CommonUtils;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.config.EnvironmentConfigurationsHandler;

public class GetDetailsBasedonBackend implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetSwiftCode.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		String serviceName;
		HashMap<String, Object> params = (HashMap<String, Object>) inputArray[1];
		HashMap<String, Object> serviceHeaders = new HashMap<String, Object>();
		Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
        String operationName = inputParams.get("operationName");
		
		try {
			String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getServerAppProperty("PAYMENT_BACKEND");
			serviceName = ("MOCK".equalsIgnoreCase(PAYMENT_BACKEND) || "SRMS_MOCK".equals(PAYMENT_BACKEND)) ? "TransfersMockData" : "T24ISPaymentsView";
			result = CommonUtils.callIntegrationService(request, params, serviceHeaders, serviceName, operationName,
					true);
			return result;
			
		} catch (Exception e) {
			LOG.error("Caught exception at invoke : " + e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

	}
}
