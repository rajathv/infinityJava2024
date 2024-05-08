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
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GetAmendmentsLetterOfCreditsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceProperties;

public class GetAmendmentsLetterOfCreditsBackendDelegateImpl implements GetAmendmentsLetterOfCreditsBackendDelegate {
	private static final Logger LOG = LogManager.getLogger(GetLetterOfCreditsBackendDelegateImpl.class);

	@Override
	public List<LetterOfCreditsDTO> getamendLetterOfCreditsFromSRMS(LetterOfCreditsDTO letterOfCreditsDTO,
			DataControllerRequest request) throws ApplicationException {
		// TODO Auto-generated method stub

		List<LetterOfCreditsDTO> letterOfCredits = new ArrayList<>();

		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);

		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("type", props.getProperty("LetterOfCreditsAmendmentType"));
		inputMap.put("subType", props.getProperty("LetterOfCreditsAmendmentSubType"));

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
			throw new ApplicationException(ErrorCodeEnum.ERRTF_29046);
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
				LetterOfCreditsDTO importamendDTO = null;
				try {
					importamendDTO = JSONUtils.parse(inputPayload.toString(), LetterOfCreditsDTO.class);
					importamendDTO.setAmendmentReference(singleOrder.getString("serviceReqId"));
					importamendDTO.setAmendmentDate(singleOrder.getString("requestCreatedTime"));
					importamendDTO.setAmendmentApprovedDate(singleOrder.getString("requestCreatedTime"));

				} catch (IOException e) {
					LOG.error("Exception occurred while fetching params: ", e);
				}
				letterOfCredits.add(importamendDTO);
			}

		}
		return letterOfCredits;
	}

	public LetterOfCreditsDTO getAmendmentsById(String amendmentReference, DataControllerRequest request) {

		JSONObject Response = new JSONObject();
		LetterOfCreditsDTO amendmentDto = new LetterOfCreditsDTO();
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);

		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceRequestIds", amendmentReference);

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
			amendmentDto.setMsg("Failed to fetch record");
			LOG.error("Unable to GET SRMS ID " + e);
			return amendmentDto;
		}
		if (StringUtils.isNotBlank(response)) {
			Response = new JSONObject(response);
			LOG.info("OMS Response " + response);
		}

		try {
			JSONArray Orders = Response.getJSONArray("serviceReqs");
			JSONObject serviceResponse = Orders.getJSONObject(0);
			if (serviceResponse.has("serviceReqId") && serviceResponse.has("partyId")) {
				if (serviceResponse.get("serviceReqId").toString().equalsIgnoreCase(amendmentReference)
						&& serviceResponse.get("partyId").toString().equalsIgnoreCase(customerId)) {
					if (serviceResponse.has("serviceReqRequestIn")) {
						JSONObject inputPayload = serviceResponse.getJSONObject("serviceReqRequestIn");
						try {
							amendmentDto = JSONUtils.parse(inputPayload.toString(), LetterOfCreditsDTO.class);
							amendmentDto.setAmendmentReference(((String) serviceResponse.get("serviceReqId")));
							amendmentDto.setAmendmentDate(serviceResponse.getString("requestCreatedTime"));
							amendmentDto.setAmendmentApprovedDate(serviceResponse.getString("requestCreatedTime"));
						} catch (IOException e) {
							LOG.error("Exception occurred while fetching params: ", e);
							amendmentDto.setMsg("Failed to fetch record");
						}
					}
				}
			} else {
				amendmentDto.setMsg("Security Exception - Unauthorized Access ");
				LOG.info("OMS Response " + response);
				return amendmentDto;
			}
		} catch (Exception e) {
			amendmentDto.setMsg("Failed to fetch record");
			LOG.info("OMS Response " + response);
			LOG.error("Unable to GET SRMS ID " + e);
			return amendmentDto;
		}

		return amendmentDto;
	}

}
