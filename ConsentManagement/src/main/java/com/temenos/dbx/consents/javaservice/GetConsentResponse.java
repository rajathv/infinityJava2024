package com.temenos.dbx.consents.javaservice;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.infinity.dbx.dbp.jwt.auth.utils.CommonUtils;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.LegalEntityUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetConsentResponse implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(GetConsentResponse.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		HashMap<String, Object> params = (HashMap<String, Object>) inputArray[1];
		HashMap<String, Object> serviceHeaders = new HashMap<String, Object>();
		String serviceName = new String();
		String operationName = new String();
		request.getHeaderMap().put("legalEntityId", LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request));
		LegalEntityUtil.addCompanyIDToHeaders(request);
		request.addRequestParam_("legalEntityId", LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request));
		params.put("legalEntityId", LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request));
		LOG.debug("input params in consent"+params);
		LOG.debug("request in consent"+request);
		try {
			String command;
			command=GetUrl.getURL(request.getParameter("type"));
	   		  String[] api = null;
				api = command.split("#");
					if (api != null && api.length > 1) {
						serviceName = api[0];
						operationName = api[1];
					}
			Result result =CommonUtils.callIntegrationService(request, params, serviceHeaders, serviceName, operationName,
					true); 	
			return result;
		}
		catch (Exception e) { 
			LOG.error(e);
			return ErrorCodeEnum.ERR_26021.setErrorCode(new Result()); 
		}
	}
}
