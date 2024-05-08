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

public class WithdrawLetterOfCreditsOperation implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(RejectLetterOfCreditsOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			LetterOfCreditsResource letterOfCreditsResource = DBPAPIAbstractFactoryImpl
					.getResource(LetterOfCreditsResource.class);
			result = letterOfCreditsResource.withdrawLetterOfCredit(request);
			return result;
		} catch (Exception e) { //Change error code
			LOG.error("Unable to withdraw letter of credit : "+e);
			return ErrorCodeEnum.ERR_26021.setErrorCode(new Result()); 
		}

	}

}



