package com.temenos.infinity.api.transactionadviceapi.javaservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.cookie.Cookie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class AutoFormLoginPostProcessor implements DataPostProcessor2 {
	
	private static final Logger logger = LogManager.getLogger(AutoFormLoginPostProcessor.class);

	@Override
	public Result execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		Result finalResult = new Result();
		List<Cookie> cookies = response.getCookies();
		Map<String, String> cookieList = new HashMap<String, String>();
		if(cookies.isEmpty())
		{
			logger.error("No Cookies Found in Response");
			
		}
		for (Cookie cookie : cookies) {
			cookieList.put(cookie.getName(), cookie.getValue());
			finalResult.addParam(new Param(cookie.getName(), cookie.getValue(), "string"));
		}

		return finalResult;

	}

}
