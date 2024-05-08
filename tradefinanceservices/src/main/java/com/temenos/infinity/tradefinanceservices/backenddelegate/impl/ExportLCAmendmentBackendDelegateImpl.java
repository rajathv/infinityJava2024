/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.ExportLCAmendmentBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.ExportLCAmendmentsDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceProperties;

public class ExportLCAmendmentBackendDelegateImpl implements ExportLCAmendmentBackendDelegate, TradeFinanceConstants{
	private static final Logger LOG = LogManager.getLogger(ExportLCAmendmentBackendDelegateImpl.class);
	@Override
	public List<ExportLCAmendmentsDTO> getExportAmendments(DataControllerRequest request) {
		
		Map<String, Object> inputMap = new HashMap<>();
		Properties props = TradeFinanceProperties.loadProps(PARAM_PROPERTY);
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsAmendmentType"));
		inputMap.put("subType", props.getProperty("ExportLetterOfCreditsAmendmentSubType"));
		
		LOG.info("OMS Export LC Amendment Request" + inputMap.toString());
		// Set Header Map
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		// Making a call to order request API
		String amendmentsResponse = null;
		try {
			amendmentsResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to fetch Export Letter Of Credit Amendments " + e);
		}
		JSONObject Response = new JSONObject();
		if (StringUtils.isNotBlank(amendmentsResponse)) {
			Response = new JSONObject(amendmentsResponse);
			LOG.info("OMS Response " + amendmentsResponse);
		}
		List<ExportLCAmendmentsDTO> amendmentsList = new ArrayList<>();
		JSONArray Orders = Response.getJSONArray("serviceReqs");
		for (int i = 0; i < Orders.length(); i++) {
			JSONObject singleOrder = Orders.getJSONObject(i);
			if (singleOrder.has("serviceReqRequestIn")) {
				JSONObject inputPayload = singleOrder.getJSONObject("serviceReqRequestIn");
				ExportLCAmendmentsDTO amendDTO = null;
				try {
					amendDTO = JSONUtils.parse(inputPayload.toString(), ExportLCAmendmentsDTO.class);
					amendDTO.setAmendmentSRMSRequestId((String) singleOrder.get("serviceReqId"));
					amendDTO.setAmendmentReferenceNo((String) singleOrder.get("serviceReqId"));
				} catch (IOException e) {
					LOG.error("Exception occurred while fetching params: ", e);
				}
				amendmentsList.add(amendDTO);
			}
		}
		
		return amendmentsList;
	}
	public ExportLCAmendmentsDTO amendExportLCcreate(ExportLCAmendmentsDTO letterOfCredits, DataControllerRequest request) {
		ExportLCAmendmentsDTO LCResponse = letterOfCredits;
		JSONObject requestBody = constructRequestPayload(letterOfCredits);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date = new Date();
		requestBody.put("serviceRequestTime", sdf.format(date));

		// Retrieve properties
		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);

		// Convert JSONObject requestbody to String
		String requestbody = requestBody.toString().replaceAll("\"", "'");

		// convert into input map
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceReqStatus", "Success");
		inputMap.put("requestBody", requestbody);
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsAmendmentType"));
		inputMap.put("subtype", props.getProperty("ExportLetterOfCreditsAmendmentSubType"));

		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		// Making a call to order request API
		String letterOfCreditsResponse = null;
		JSONObject Response = new JSONObject();
		try {
			letterOfCreditsResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to ammend Export Letter Of Credit " + e);
		}

		// Converting response into JSONObject
		if (StringUtils.isNotBlank(letterOfCreditsResponse)) {
			Response = new JSONObject(letterOfCreditsResponse);
			LOG.info("OMS Response " + letterOfCreditsResponse);
		}
		if (Response.has(TradeFinanceConstants.PARAM_ORDER_ID)
				&& StringUtils.isNotBlank(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID))) {
			LCResponse.setAmendmentSRMSRequestId(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID));
			LCResponse.setAmendmentReferenceNo(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID));
			LCResponse.setSelfAcceptance("Pending");
			request.addRequestParam_("isSrmsFailed", "false");

		} else {
			request.addRequestParam_("isSrmsFailed", "true");
		}

		if (Response.has("dbpErrMsg") && StringUtils.isNotBlank(Response.getString("dbpErrMsg"))) {
			LCResponse.setErrorMessage(Response.getString("dbpErrMsg"));
			LOG.error("Unable to ammend LOC request order Error Message" + Response.getString("dbpErrMsg"));
		}

		if (Response.has("dbpErrCode") && StringUtils.isNotBlank(Response.getString("dbpErrCode"))) {
			LCResponse.setErrorMessage(String.valueOf(Response.getInt("dbpErrCode")));
			LOG.error("Unable to ammend LOC request order Error Code" + Response.getString("dbpErrMsg"));
		}

		return LCResponse;
	}
	public JSONObject constructRequestPayload(ExportLCAmendmentsDTO letterOfCredit) {
		JSONObject requestBody = new JSONObject();
		if (StringUtils.isNotEmpty(letterOfCredit.getApplicantName())) {
			requestBody.put("applicantName", letterOfCredit.getApplicantName());
		}
		if (letterOfCredit.getOldLcAmount() != null) {
			requestBody.put("oldLcAmount",
					String.valueOf(letterOfCredit.getOldLcAmount()));
		}

		if (letterOfCredit.getNewLcAmount() != null) {
			requestBody.put("newLcAmount",
					String.valueOf(letterOfCredit.getNewLcAmount()));
		}

		if (StringUtils.isNotEmpty(letterOfCredit.getExportlcReferenceNo())) {
			requestBody.put("exportlcReferenceNo", letterOfCredit.getExportlcReferenceNo());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getExportlcSRMSRequestId())) {
			requestBody.put("exportlcSRMSRequestId", letterOfCredit.getExportlcSRMSRequestId());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getAmendmentStatus())) {
			requestBody.put("amendmentStatus", letterOfCredit.getAmendmentStatus());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getAmendmentNo())) {
			requestBody.put("amendmentNo", letterOfCredit.getAmendmentNo());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getLcType())) {
			requestBody.put("lcType", letterOfCredit.getLcType());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getLcIssueDate())) {
			requestBody.put("lcIssueDate", letterOfCredit.getLcIssueDate());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getLcExpiryDate())) {
			requestBody.put("lcExpiryDate", letterOfCredit.getLcExpiryDate());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getLcCurrency())) {
			requestBody.put("lcCurrency", letterOfCredit.getLcCurrency());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getLcAmountStatus())) {
			requestBody.put("lcAmountStatus", letterOfCredit.getLcAmountStatus());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getLatestShipmentDate())) {
			requestBody.put("latestShipmentDate", letterOfCredit.getLatestShipmentDate());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getOtherAmendments())) {
			requestBody.put("otherAmendments", letterOfCredit.getOtherAmendments());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getPeriodOfPresentation())) {
			requestBody.put("periodOfPresentation", letterOfCredit.getPeriodOfPresentation());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getAmendmentChargesPayer())) {
			requestBody.put("amendmentChargesPayer", letterOfCredit.getAmendmentChargesPayer());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getChargesDebitAccount())) {
			requestBody.put("chargesDebitAccount", letterOfCredit.getChargesDebitAccount());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getAmendmentReferenceNo())) {
			requestBody.put("amendmentReferenceNo", letterOfCredit.getAmendmentReferenceNo());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getAmendmentSRMSRequestId())) {
			requestBody.put("amendmentSRMSRequestId", letterOfCredit.getAmendmentSRMSRequestId());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getAmendmentReceivedDate())) {
			requestBody.put("amendmentReceivedDate", letterOfCredit.getAmendmentReceivedDate());
		}
		/*if (StringUtils.isNotEmpty(letterOfCredit.getAmendmentReceivedDate())) {
			requestBody.put("amendmentReceivedDate", letterOfCredit.getAmendmentReceivedDate());
		}*/
		if (StringUtils.isNotEmpty(letterOfCredit.getReasonForSelfRejection())) {
			requestBody.put("reasonForSelfRejection", letterOfCredit.getReasonForSelfRejection());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getSelfRejectedDate())) {
			requestBody.put("selfRejectedDate", letterOfCredit.getSelfRejectedDate());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getSelfAcceptance())) {
			requestBody.put("selfAcceptance", letterOfCredit.getSelfAcceptance());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getSelfAcceptanceDate())) {
			requestBody.put("selfAcceptanceDate", letterOfCredit.getSelfAcceptanceDate());
		}


		// requestBody.put("lcAmount", String.valueOf(letterOfCredit.getLcAmount()));
		return requestBody;
	}
	
	public ExportLCAmendmentsDTO updateExportLCAmendment(ExportLCAmendmentsDTO amendmentData,
			DataControllerRequest request) {
		String requestBody = constructRequestPayload(amendmentData).toString().replaceAll("\"", "'");

		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceReqStatus", "Success");
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsAmendmentType"));
		inputMap.put("subtype", props.getProperty("ExportLetterOfCreditsAmendmentSubType"));
		inputMap.put("serviceRequestId", amendmentData.getAmendmentSRMSRequestId());
		inputMap.put("requestBody", requestBody);
		LOG.info("OMS Export LC Amendment Update Request" + inputMap.toString());

		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		try {
			String rawResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
			JSONObject responseObject = new JSONObject(rawResponse);
			String errorCode = responseObject.has("dbpErrCode") ? String.valueOf(responseObject.get("dbpErrCode"))
					: null;
			String errorMessage = responseObject.has("dbpErrMsg") ? responseObject.getString("dbpErrMsg") : null;
			if (StringUtils.isBlank(errorCode) || StringUtils.isBlank(errorMessage)) {
				String orderId = responseObject.get(PARAM_ORDER_ID).toString();
				amendmentData.setAmendmentSRMSRequestId(orderId);
				amendmentData.setAmendmentReferenceNo(orderId);
			} else {
				LOG.error("Unable to update export LC drawing request order. Error Code: " + errorCode
						+ " Error Message: " + errorMessage);
				amendmentData.setErrorCode(errorCode);
				amendmentData.setErrorMessage(errorMessage);
			}
			return amendmentData;
		} catch (Exception e) {
			LOG.error("Error occured while updating export lc amendment " + e);
			return null;
		}
	}

	public ExportLCAmendmentsDTO getExportLCAmendmentById(String amendmentSRMSRequestId,
			DataControllerRequest request) {
		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceReqStatus", "Success");
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsAmendmentType"));
		inputMap.put("subtype", props.getProperty("ExportLetterOfCreditsAmendmentSubType"));
		inputMap.put("serviceRequestIds", amendmentSRMSRequestId);
		LOG.info("OMS Export LC Amendment Get Request By Id" + inputMap.toString());

		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		ExportLCAmendmentsDTO amendmentData = new ExportLCAmendmentsDTO();
		try {
			String rawResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getServiceName())
					.withOperationId(
							TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();

			JSONObject jsonResponseObj = new JSONObject(rawResponse);
			JSONObject amendmentResponse = jsonResponseObj.getJSONArray(PARAM_SERVICE_REQUESTS).getJSONObject(0);

			String errorCode = amendmentResponse.has("dbpErrCode") ? String.valueOf(amendmentResponse.get("dbpErrCode"))
					: null;
			String errorMessage = amendmentResponse.has("dbpErrMsg") ? amendmentResponse.getString("dbpErrMsg") : null;

			if (StringUtils.isBlank(errorCode) || StringUtils.isBlank(errorMessage)) {
				JSONObject inputPayload = amendmentResponse.getJSONObject(PARAM_INPUT_PAYLOAD);
				amendmentData = JSONUtils.parse(inputPayload.toString(), ExportLCAmendmentsDTO.class);

				String orderId = amendmentResponse.get(PARAM_SERVICE_REQ_ID).toString();
				amendmentData.setAmendmentSRMSRequestId(orderId);
				amendmentData.setAmendmentReferenceNo(orderId);
			} else {
				LOG.error("Unable to update export LC drawing request order. Error Code: " + errorCode
						+ " Error Message: " + errorMessage);
				amendmentData.setErrorCode(errorCode);
				amendmentData.setErrorMessage(errorMessage);
			}
			return amendmentData;
		} catch (Exception e) {
			LOG.error("Error occured while fetching amendment record by id" + e);
			return null;
		}
	}
}
