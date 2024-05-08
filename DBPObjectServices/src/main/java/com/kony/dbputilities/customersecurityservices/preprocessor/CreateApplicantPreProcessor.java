package com.kony.dbputilities.customersecurityservices.preprocessor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CreateApplicantPreProcessor implements DataPreProcessor2 {
	private static final Logger LOG = LogManager.getLogger(CreateApplicantPreProcessor.class);
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest dcRequest, DataControllerResponse dcResponse,
			Result result) {
		String enable = URLFinder.getPathUrl("BACKGROUNDVERIFICATION_ENABLE", dcRequest);
		if ("true".equalsIgnoreCase(enable)) {
			Result response = null;
			try {
				response = HelperMethods.callApi(dcRequest, getInput(dcRequest), HelperMethods.getHeaders(dcRequest),
						URLConstants.BACKGROUNDVERIFICATION);
			} catch (HttpCallException e) {

				LOG.error(e.getMessage());
			}
			if (null != response
					&& "PASS".equalsIgnoreCase(HelperMethods.getParamValue(response.getParamByName("success")))) {
				return true;
			}
			if (null != response) {
				result.addAllParams(response.getAllParams());
			}

			return false;
		}

		return true;
	}

	private Map<String, String> getInput(DataControllerRequest dcRequest) {
		Map<String, String> inputMap = new HashMap<>();
		inputMap.put("invoice", "12358");
		String contactInformation = dcRequest.getParameter("contactInformation");
		if (StringUtils.isNotBlank(contactInformation)) {
			HashMap<String, String> hashMap = HelperMethods.getRecordMap(contactInformation);
			inputMap.put("email", hashMap.get("emailAddress"));
		}
		String personalInformation = dcRequest.getParameter("personalInformation");
		if (StringUtils.isNotBlank(personalInformation)) {
			HashMap<String, String> hashMap = HelperMethods.getRecordMap(personalInformation);
			inputMap.put("firstName", hashMap.get("firstName"));
			inputMap.put("lastName", hashMap.get("lastName"));
		}
		String addressInformation = dcRequest.getParameter("addressInformation");
		if (StringUtils.isNotBlank(addressInformation)) {
			HashMap<String, String> hashMap = HelperMethods.getRecordMap(addressInformation);
			inputMap.put("address", hashMap.get("addressLine1"));
			inputMap.put("city", hashMap.get("city"));
			inputMap.put("state", hashMap.get("state"));
			inputMap.put("zipcode", hashMap.get("zipcode"));
		}
		return inputMap;
	}
}
