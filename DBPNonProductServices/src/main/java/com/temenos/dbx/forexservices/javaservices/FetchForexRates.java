package com.temenos.dbx.forexservices.javaservices;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.forexservices.dbservices.ForexCurrencyDBServices;

public class FetchForexRates implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(FetchForexRates.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		Result result;
			try {
				ForexCurrencyDBServices forexCurrencyDBServices = new ForexCurrencyDBServices();
				Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
				String baseCurrencyCode = inputParams.get("baseCurrencyCode") != null ? inputParams.get("baseCurrencyCode").toString() : null;
				String quoteCurrencyCode = inputParams.get("quoteCurrencyCode") != null ? inputParams.get("quoteCurrencyCode").toString() : null;
				String market = inputParams.get("market") != null ? inputParams.get("market").toString() : null;
				result = forexCurrencyDBServices.fetchRates(baseCurrencyCode,quoteCurrencyCode,market,request);
			}
			catch(Exception e) {
				LOG.error("Error occured while invoking FetchForexRates", e);
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			}
		return result;
	}

}
