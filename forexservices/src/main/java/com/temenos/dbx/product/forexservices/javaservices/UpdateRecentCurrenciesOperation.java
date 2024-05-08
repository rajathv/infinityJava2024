package com.temenos.dbx.product.forexservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.forexservices.resource.api.ForexResource;

public class UpdateRecentCurrenciesOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(UpdateRecentCurrenciesOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
	
		Result result = null;
		
		try {
			//Initializing of ForexResource through Abstract factory method
			ForexResource forexResource = DBPAPIAbstractFactoryImpl.getResource(ForexResource.class);
			result = forexResource.updateRecentCurrencies(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while updating RecentCurrencies: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
		
	}

}
