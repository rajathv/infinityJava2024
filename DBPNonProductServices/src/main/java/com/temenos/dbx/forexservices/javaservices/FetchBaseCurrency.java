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

public class FetchBaseCurrency implements JavaService2 {

	 private static final Logger LOG = LogManager.getLogger(FetchBaseCurrency.class);

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		Result result;
			try {
				ForexCurrencyDBServices forexCurrencyDBServices = new ForexCurrencyDBServices();
				Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
				String CountryCode = inputParams.get("CountryCode") != null ? inputParams.get("CountryCode").toString() : null;

				result = forexCurrencyDBServices.fetchBaseCurrency(CountryCode,request);
			}
			catch(Exception e) {
				LOG.error("Error occured while invoking fetchBaseCurrency", e);
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			}
		return result;
	}

}
