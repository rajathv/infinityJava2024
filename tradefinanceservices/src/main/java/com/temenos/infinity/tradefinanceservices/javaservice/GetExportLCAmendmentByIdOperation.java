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
import com.temenos.infinity.tradefinanceservices.resource.api.ExportLCAmendmentResource;

public class GetExportLCAmendmentByIdOperation implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(GetExportLCAmendmentByIdOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		ExportLCAmendmentResource requestResource = DBPAPIAbstractFactoryImpl
				.getResource(ExportLCAmendmentResource.class);
		Result result = new Result();
		try {
			result = requestResource.getExportLCAmendmentById(request);
			return result;
		} catch (Exception e) {
			LOG.debug("Error occured while fetching export amendment" + e);
			return ErrorCodeEnum.ERRTF_29080.setErrorCode(result);
		}
	}
}
