/**
 * 
 */
package com.temenos.infinity.api.wealthservices.tap.preandpostprocessors;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.util.PortfolioWealthUtils;

/**
 * @author himaja.sridhar
 *
 */
public class TokenGenerator implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(TokenGenerator.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		String backendToken = PortfolioWealthUtils.getTokenFromCache(request);
		if (StringUtils.isNotBlank(backendToken)) {
			request.addRequestParam_("Authorization", backendToken);
			request.addRequestParam_("x-channel", "PCK_TCIB_PM_DESKTOP");
			LOG.error("Token Generated From Cache");
		} else {
			TAPTokenGenPreProcessor obj = new TAPTokenGenPreProcessor();
			obj.execute(inputMap, request, response, result);
			LOG.error("New Token Generated");
		}
		return true;
	}

}
