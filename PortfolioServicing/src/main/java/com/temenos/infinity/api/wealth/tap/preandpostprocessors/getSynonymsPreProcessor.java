/**
 * 
 */
package com.temenos.infinity.api.wealth.tap.preandpostprocessors;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.TokenUtils;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * (INFO) Prepares the input for the TAP service in the desired format.
 * 
 * @author himaja.sridhar
 *
 */
public class getSynonymsPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(getSynonymsPreProcessor.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
	
		inputMap.put("id", request.getParameter("id"));
		try {
			Map<String, String> inputparamMap = new HashMap<>();
			inputparamMap.put("userName","dbpolbuser");
			inputparamMap.put("customerId","1026540");
			String backendToken = TokenUtils.getPortfolioWealthMSAuthToken(inputparamMap);
			if (StringUtils.isBlank(backendToken)) {
				return false;
			}
			LOG.error("JWT TOKEN" +backendToken);
			backendToken = "Bearer ".concat(backendToken);
			request.addRequestParam_("Authorization", backendToken);
			request.addRequestParam_("x-channel", "PCK_TCIB_PM_DESKTOP");
		return true;
	}catch(Exception e) {}
		return false;
	}
	

}
