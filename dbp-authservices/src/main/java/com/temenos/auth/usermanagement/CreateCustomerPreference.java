package com.temenos.auth.usermanagement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.controller.DataControllerRequest;

public class CreateCustomerPreference {
	private static final Logger LOG = LogManager.getLogger(CreateCustomerPreference.class);

	public static void invoke(Map<String, String> inputParams, DataControllerRequest dcRequest) {

		Map<String, String> input = new HashMap<>();
		String id = inputParams.get("id");
		SimpleDateFormat idformatter = new SimpleDateFormat("yyMMddHHmmssSSS");
		input.put("id", idformatter.format(new Date()));
		input.put("Customer_id", id);
		try {
			HelperMethods.callApi(dcRequest, input, HelperMethods.getHeaders(dcRequest),
					URLConstants.CUSTOMERPREFERENCE_CREATE);
		} catch (HttpCallException e) {

			LOG.error(e.getMessage());
		}
	}
}
