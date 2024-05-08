package com.temenos.infinity.tradefinanceservices.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.resource.api.LetterOfCreditsResource;


public class FetchLetterOfCreditsDetailsOperation implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(FetchLetterOfCreditsDetailsOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		try {
			LetterOfCreditsResource letterOfCreditsResource = DBPAPIAbstractFactoryImpl.getResource(LetterOfCreditsResource.class);
			Result result =  letterOfCreditsResource.fetchLetterOfCreditDetails(request);
			return result;
		}
		catch (Exception e) { 
			LOG.error(e);
			return ErrorCodeEnum.ERR_26021.setErrorCode(new Result()); 
		}
	}
	
}
