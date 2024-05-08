/**
 * 
 */
package com.temenos.infinity.api.wealth.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.ErrorCodeEnum;
import com.temenos.infinity.api.wealth.resource.api.DailyMarketResource;

/**
 * @author himaja.sridhar
 *
 */
public class GetDailyMarket implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetDailyMarket.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			DailyMarketResource dailyMarketResource = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(ResourceFactory.class).getResource(DailyMarketResource.class);
		result  = dailyMarketResource.getDailyMarket(methodId, inputArray, request, response);
	}
	catch(Exception e) {

		LOG.error("Caught exception at invoke of GetTopMarketNews: ", e);
		return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		
	}
		return result;
	}

}
