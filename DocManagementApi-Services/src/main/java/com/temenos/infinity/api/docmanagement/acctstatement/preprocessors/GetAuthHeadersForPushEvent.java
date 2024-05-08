package com.temenos.infinity.api.docmanagement.acctstatement.preprocessors;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetAuthHeadersForPushEvent implements DataPreProcessor2 {

	private static final Logger LOG = LogManager.getLogger(GetAuthHeadersForPushEvent.class);

	@Override
	public boolean execute(HashMap requestParams, DataControllerRequest request, DataControllerResponse response,
			Result resultObj) {
		LOG.info("PUsh event preprocessor");
		boolean result = true;
		String claimsToken = null;
		Map<String, Object> headerMap = request.getHeaderMap();
		String eventMangerAppkey = EnvironmentConfigurationsHandler.getValue("EVENT_MANAGER_APP_KEY", request);
		String eventMangerAppSecret = EnvironmentConfigurationsHandler.getValue("EVENT_MANAGER_APP_SECRET", request);

		if (StringUtils.isBlank(eventMangerAppkey) || StringUtils.isBlank(eventMangerAppSecret)) {
			LOG.error("Error while fetching EVENT_MANAGER_APP_KEY or EVENT_MANAGER_APP_SECRET");
		}
		Map<String, Object> data = request.getHeaderMap();
		data.put("X-Kony-App-Key", eventMangerAppkey);
		data.put("X-Kony-App-Secret", eventMangerAppSecret);
		try {
			Result loginResult = HelperMethods.callApi(request, data, HelperMethods.getHeaders(request),
					URLConstants.PUSH_EVENT_LOGIN);
			claimsToken = loginResult.getParamValueByName("claimsToken");
			if (StringUtils.isBlank(claimsToken)) {
				LOG.error("Error in generating claims token.Push event Identity service failed");
			}
		} catch (HttpCallException e) {
			LOG.error("Error while generating anonymous claims token");
		}
		headerMap.put("X-Kony-Authorization", claimsToken);
		headerMap.remove("x-kony-authorization");
		return true;
	}
}
