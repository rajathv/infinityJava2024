/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.resource.api.GuaranteeLCAmendmentsResource;

public class GetGuaranteeLCAmendmentByIdOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetGuaranteeLCAmendmentByIdOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		GuaranteeLCAmendmentsResource letterOfCreditsResource = DBPAPIAbstractFactoryImpl
				.getResource(GuaranteeLCAmendmentsResource.class);
		Result result = new Result();
		try {
			result = letterOfCreditsResource.getGuaranteeLCAmendmentById(request);
			return result;
		} catch (Exception e) {
			LOG.error("Error occured while invoking GetGuaranteeLCAmendmentByIdOperation. Error: ", e);
			return ErrorCodeEnum.ERRTF_29054.setErrorCode(result);
		}
	}

}
