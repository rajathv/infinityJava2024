package com.temenos.infinity.tradefinanceservices.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.resource.api.InitiateDownloadLetterOfCreditsAckResource;

public class InitiateDownloadLetterOfCreditsAckOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(InitiateDownloadLetterOfCreditsAckOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			InitiateDownloadLetterOfCreditsAckResource letterOfCreditsResource = DBPAPIAbstractFactoryImpl
					.getResource(InitiateDownloadLetterOfCreditsAckResource.class);

			result = letterOfCreditsResource.initiateDownloadLetterOfCreditsAck(inputArray, request);
		} catch (Exception e) {
			LOG.error("Error occured while invoking initiate download for trade finance details pdf: ", e);
			return ErrorCodeEnum.ERRTF_29054.setErrorCode(new Result());
		}
		return result;
	}

}
