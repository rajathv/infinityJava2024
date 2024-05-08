package com.temenos.dbx.consents.javaservice;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.dbp.jwt.auth.utils.CommonUtils;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.kony.dbputilities.util.LegalEntityUtil;


public class GetCDPConsents implements JavaService2{

	private static final Logger logger = LogManager.getLogger(com.temenos.dbx.consents.javaservice.GetCDPConsents.class);
	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			HashMap<String, Object> params = (HashMap<String, Object>) inputArray[1];
			HashMap<String, Object> serviceHeaders = new HashMap<String, Object>();
			String serviceName = new String();
			String operationName = new String();
			String type="CDP";
			request.getHeaderMap().put("legalEntityId", LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request));
			LegalEntityUtil.addCompanyIDToHeaders(request);
			request.addRequestParam_("legalEntityId", LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request));
			params.put("legalEntityId", LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request));
			logger.debug("input params in cdp"+params);
			logger.debug("request in cdp"+request);
			//String serviceName = "T24ISConsents";
			//String operationName = "getCDPConsents";
			String command;
			command=GetUrl.getURL(type);
	   		  String[] api = null;
				api = command.split("#");
					if (api != null && api.length > 1) {
						serviceName = api[0];
						operationName = api[1];
					}
			result = CommonUtils.callIntegrationService(request, params, serviceHeaders, serviceName, operationName,
						true);
		} catch (Exception e) {
			Result errorResult = new Result();
			logger.error("Exception Occured while fetching CDP Consents:" + e);
			return errorResult;
		}
		return result;
	}
}
