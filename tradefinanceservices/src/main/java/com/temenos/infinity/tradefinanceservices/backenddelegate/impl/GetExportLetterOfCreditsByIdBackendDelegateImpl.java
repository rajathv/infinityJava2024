/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GetExportLetterOfCreditsByIdBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.dto.ExportLOCDTO;

public class GetExportLetterOfCreditsByIdBackendDelegateImpl implements GetExportLetterOfCreditsByIdBackendDelegate {

	private static final Logger LOG = LogManager.getLogger(GetExportLetterOfCreditsByIdBackendDelegateImpl.class);

	public ExportLOCDTO getExportLetterOfCreditById(String exportLcId, DataControllerRequest request) {

		JSONObject Response = new JSONObject();
		ExportLOCDTO exportLCDto = new ExportLOCDTO();
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);

		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceRequestIds", exportLcId);

		// Set Header Map
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));
		String response = null;
		try {
			response = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getServiceName())
					.withOperationId(
							TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();

		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			exportLCDto.setErrorMsg("Failed to fetch record");
			LOG.error("Unable to GET SRMS ID " + e);
			return exportLCDto;
		}
		if (StringUtils.isNotBlank(response)) {
			Response = new JSONObject(response);
			LOG.info("OMS Response " + response);
		}

		try {
			JSONArray Orders = Response.getJSONArray("serviceReqs");
			JSONObject serviceResponse = Orders.getJSONObject(0);
			if (serviceResponse.has("serviceReqId") && serviceResponse.has("partyId")) {
				if (serviceResponse.get("serviceReqId").toString().equalsIgnoreCase(exportLcId)
						&& serviceResponse.get("partyId").toString().equalsIgnoreCase(customerId)) {
					if (serviceResponse.has("serviceReqRequestIn")) {
						JSONObject inputPayload = serviceResponse.getJSONObject("serviceReqRequestIn");
						try {
							exportLCDto = JSONUtils.parse(inputPayload.toString(), ExportLOCDTO.class);
							exportLCDto.setExportLCId((String) serviceResponse.get("serviceReqId"));
							exportLCDto.setCustomerId((String) serviceResponse.get("partyId"));
							exportLCDto.setLcCreatedOn(serviceResponse.getString("requestCreatedTime"));
						} catch (IOException e) {
							LOG.error("Exception occurred while fetching params: ", e);
							exportLCDto.setErrorMsg("Failed to fetch record");
						}

					}
				}
			} else {
				exportLCDto.setErrorMsg("Security Exception - Unauthorized Access ");
				LOG.info("OMS Response " + response);
				return exportLCDto;
			}
		} catch (Exception e) {
			exportLCDto.setErrorMsg("Failed to fetch record");
			LOG.info("OMS Response " + response);
			LOG.error("Unable to GET SRMS ID " + e);
			return exportLCDto;
		}

		return exportLCDto;
	}

}
