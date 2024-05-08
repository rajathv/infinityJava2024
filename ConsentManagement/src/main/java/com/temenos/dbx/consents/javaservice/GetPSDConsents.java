package com.temenos.dbx.consents.javaservice;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.dbp.jwt.auth.utils.CommonUtils;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.LegalEntityUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class GetPSDConsents implements JavaService2{

	private static final Logger logger = LogManager.getLogger(com.temenos.dbx.consents.javaservice.GetPSDConsents.class);
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
			String type="PSD";
			request.getHeaderMap().put("legalEntityId", LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request));
			LegalEntityUtil.addCompanyIDToHeaders(request);
			request.addRequestParam_("legalEntityId", LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request));
			params.put("legalEntityId", LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request));
			logger.debug("input params in psd2"+params);
			logger.debug("request in psd2"+request);
//			String serviceName = "T24ISConsents";
//			String operationName = "getPSDConsent";
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
			logger.error("Exception Occured while fetching PSD Consents:" + e);
			return errorResult;
		}
		return result;
	}
}
