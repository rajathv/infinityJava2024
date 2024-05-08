/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GetExportLetterOfCreditsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.ExportLOCDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceProperties;

public class GetExportLetterOfCreditsBackendDelegateImpl
		implements GetExportLetterOfCreditsBackendDelegate, TradeFinanceConstants {
	private static final Logger LOG = LogManager.getLogger(GetExportLetterOfCreditsBackendDelegateImpl.class);

	@Override
	public List<ExportLOCDTO> getExportLetterOfCredits(DataControllerRequest request) {
		// TODO Auto-generated method stub

		List<ExportLOCDTO> letterOfCredits = new ArrayList<>();

		Properties props = TradeFinanceProperties.loadProps(PARAM_PROPERTY);

		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsType"));
		inputMap.put("subType", props.getProperty("ExportLetterOfCreditsSubType"));

		// Set Header Map
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		String letterOfCreditsResponse = null;
		JSONObject Response = new JSONObject();
		try {
			letterOfCreditsResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();

		} catch (Exception e) {
			LOG.error("Unable to get export letter of credits requests " + e);
		}

		if (StringUtils.isNotBlank(letterOfCreditsResponse)) {
			Response = new JSONObject(letterOfCreditsResponse);
			LOG.info("OMS Response " + letterOfCreditsResponse);
		}

		JSONArray Orders = Response.getJSONArray("serviceReqs");
		for (int i = 0; i < Orders.length(); i++) {
			JSONObject singleOrder = Orders.getJSONObject(i);
			if (singleOrder.has("serviceReqRequestIn")) {
				JSONObject inputPayload = singleOrder.getJSONObject("serviceReqRequestIn");
				ExportLOCDTO exportDTO = null;
				try {
					exportDTO = JSONUtils.parse(inputPayload.toString(), ExportLOCDTO.class);
					exportDTO.setExportLCId((String) singleOrder.get("serviceReqId"));
					exportDTO.setLcCreatedOn(singleOrder.getString("requestCreatedTime"));
				} catch (IOException e) {
					LOG.error("Exception occurred while fetching params: ", e);
				}
				letterOfCredits.add(exportDTO);
			}

		}
		return letterOfCredits;
	}
}
