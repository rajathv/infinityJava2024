package com.temenos.infinity.api.wealthOrder.mockdata;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealthservices.constants.ErrorCodeEnum;
import com.temenos.infinity.api.wealthOrder.util.WealthMockUtil;

/**
 * (INFO) Prepares Result object from the mock data returned by the Util.
 *
 */
public class CreateForexOrdersMock implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetMarketRatesMock.class);

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		WealthMockUtil mockUtil = new WealthMockUtil();
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];

		try {
			JSONObject resultJSON = mockUtil.mockForexOrders(inputParams);
			return Utilities.constructResultFromJSONObject(resultJSON);
		} catch (Exception e) {
			LOG.error("Caught exception at invoke of getMarketRates: ", e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
	}
}
