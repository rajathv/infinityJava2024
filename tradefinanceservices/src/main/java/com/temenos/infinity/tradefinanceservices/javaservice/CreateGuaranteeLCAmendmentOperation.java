/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import java.util.HashMap;

import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.dto.GuaranteeLCAmendmentsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.GuaranteeLCAmendmentsResource;

public class CreateGuaranteeLCAmendmentOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(CreateGuaranteeLCAmendmentOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		GuaranteeLCAmendmentsResource orderResource = DBPAPIAbstractFactoryImpl
				.getResource(GuaranteeLCAmendmentsResource.class);
		try {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> requestParams = (HashMap<String, Object>) inputArray[1];
			GuaranteeLCAmendmentsDTO guaranteeReqPayloadDTO = JSONUtils.parse(new JSONObject(requestParams).toString(),
					GuaranteeLCAmendmentsDTO.class);
			result = orderResource.createGuaranteeLCAmendment(guaranteeReqPayloadDTO, request);
			return result;
		} catch (Exception e) {
			LOG.error("Error occured while creating Guarantee Amendment. Error: " + e);
			return ErrorCodeEnum.ERRTF_29045.setErrorCode(result, "Error occured while creating Guarantee Amendment");
		}
	}

}
