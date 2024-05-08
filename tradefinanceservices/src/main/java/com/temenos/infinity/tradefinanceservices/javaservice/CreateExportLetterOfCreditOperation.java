/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.dto.ExportLOCDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.CreateExportLCResource;

public class CreateExportLetterOfCreditOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(CreateExportLetterOfCreditOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		CreateExportLCResource createResource = DBPAPIAbstractFactoryImpl.getResource(CreateExportLCResource.class);
		try {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> requestParams = (HashMap<String, Object>) inputArray[1];
			ExportLOCDTO createPayloadDTO = JSONUtils.parse(new JSONObject(requestParams).toString(),
					ExportLOCDTO.class);
			result = createResource.createExportLetterOfCredit(createPayloadDTO, request);
			return result;
		} catch (Exception e) {
			LOG.error("Failed to Create Letter of Credit" + e);
			return ErrorCodeEnum.ERRTF_29045.setErrorCode(result, "Failed to Create Letter of Credit");

		}
	}
}
