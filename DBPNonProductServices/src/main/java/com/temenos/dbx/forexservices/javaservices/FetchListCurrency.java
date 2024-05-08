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

public class FetchListCurrency implements JavaService2 {

	 private static final Logger LOG = LogManager.getLogger(FetchBaseCurrency.class);

	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		Result result;
			try {
				ForexCurrencyDBServices forexCurrencyDBServices = new ForexCurrencyDBServices();

				result = forexCurrencyDBServices.fetchCurrencyList(request);
			}
			catch(Exception e) {
				LOG.error("Error occured while invoking FetchListCurrency", e);
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			}
		return result;
	}

}

