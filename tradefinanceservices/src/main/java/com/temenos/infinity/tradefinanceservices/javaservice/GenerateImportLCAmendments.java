/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.resource.api.InitiateDownloadImportLCAmendmentsResource;

public class GenerateImportLCAmendments implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(GenerateImportLCAmendments.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			InitiateDownloadImportLCAmendmentsResource letterOfCreditsResource = DBPAPIAbstractFactoryImpl
					.getResource(InitiateDownloadImportLCAmendmentsResource.class);

			result = letterOfCreditsResource.initiateDownloadLetterOfCreditsAck(inputArray, request);
		} catch (Exception e) {
			LOG.error("Error occured while invoking download Amendments LetterOfCredits details pdf: ", e);
			return ErrorCodeEnum.ERR_21252.setErrorCode(new Result());
		}
		return result;
	}

}
