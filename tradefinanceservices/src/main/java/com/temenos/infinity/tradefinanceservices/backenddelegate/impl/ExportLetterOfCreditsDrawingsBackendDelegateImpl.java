/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.ExportLetterOfCreditsDrawingsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.ExportLCDrawingsDTO;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class ExportLetterOfCreditsDrawingsBackendDelegateImpl
		implements ExportLetterOfCreditsDrawingsBackendDelegate, TradeFinanceConstants {

	private static final Logger LOG = LogManager.getLogger(ExportLetterOfCreditsDrawingsBackendDelegateImpl.class);

	public ExportLCDrawingsDTO createExportDrawing(ExportLCDrawingsDTO inputDTO, DataControllerRequest request) {

		JSONObject reqBody = constructSRMSParams(inputDTO);

		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);

		String requestbody = reqBody.toString().replaceAll("\"", "'");
		// Input Parameters in InputMap
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceReqStatus", "Success");
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsDrawingType"));
		inputMap.put("subtype", props.getProperty("ExportLetterOfCreditsDrawingsSubType"));
		inputMap.put("requestBody", requestbody);

		// Set Header Map
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		// Making a call to order request API
		String drawingsResponse = null;
		try {
			drawingsResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to create Letter Of Credit request order " + e);
//		            throw ApplicationException(ErrorCodeEnum.ERRTF_29046);
		}
		JSONObject responseObject = null;
		if (StringUtils.isNotEmpty(drawingsResponse)) {
			responseObject = new JSONObject(drawingsResponse);
			if (!responseObject.has("dbpErrMsg")) {
				inputDTO.setDrawingSRMSRequestId(responseObject.get("orderId").toString());
				if (StringUtils.isBlank(inputDTO.getDrawingReferenceNo()))
					inputDTO.setDrawingReferenceNo(responseObject.get("orderId").toString());
			}
			if (responseObject.has("dbpErrMsg") && StringUtils.isNotEmpty(responseObject.getString("dbpErrMsg"))) {
				inputDTO.setErrorMessage(responseObject.getString("dbpErrMsg"));
				LOG.error("Unable to create export LC drawing request order Error Message"
						+ responseObject.getString("dbpErrMsg"));
			}

			if (responseObject.has("dbpErrCode")
					&& StringUtils.isNotEmpty(String.valueOf(responseObject.get("dbpErrCode")))) {
				inputDTO.setErrorCode(String.valueOf(responseObject.get("dbpErrCode")));
				LOG.error("Unable to create export LC drawing request order Error Code"
						+ String.valueOf(responseObject.get("dbpErrCode")));
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date  = new Date();
		inputDTO.setDrawingCreatedDate(sdf.format(date));
		return inputDTO;
	}

	public ExportLCDrawingsDTO updateExportLetterOfCreditDrawing(ExportLCDrawingsDTO exportPayloadDTO,
			DataControllerRequest request) {
		String requestBody = constructSRMSParams(exportPayloadDTO).toString().replaceAll("\"", "'");

		Properties props = TradeFinanceProperties.loadProps(PARAM_PROPERTY);
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsDrawingType"));
		inputMap.put("subType", props.getProperty("ExportLetterOfCreditsDrawingsSubType"));
		inputMap.put("serviceRequestId", exportPayloadDTO.getDrawingSRMSRequestId());
		inputMap.put("requestBody", requestBody);


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
				exportPayloadDTO.setDrawingSRMSRequestId(orderId);
				exportPayloadDTO.setDrawingReferenceNo(orderId);
			} else {
				LOG.error("Unable to update export LC drawing request order. Error Code: " + errorCode
						+ " Error Message: " + errorMessage);
				exportPayloadDTO.setErrorCode(errorCode);
				exportPayloadDTO.setErrorMessage(errorMessage);
			}
			return exportPayloadDTO;
		} catch (Exception e) {
			LOG.error("Unable to update Drawings requests " + e);
			return null;
		}
	}

	public boolean matchSRMSId(LetterOfCreditsDTO letterOfCredit, String customerId, DataControllerRequest request) {
		JSONObject Response = new JSONObject();
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceRequestIds", letterOfCredit.getSrmsReqOrderID());

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
			LOG.error("Unable to GET SRMS ID " + e);
		}
		if (StringUtils.isNotEmpty(response)) {
			Response = new JSONObject(response);
			LOG.info("OMS Response " + response);
		}
		JSONArray Orders = Response.getJSONArray("serviceReqs");
		JSONObject serviceResponse = Orders.getJSONObject(0);
		if (serviceResponse.has("serviceReqId") && serviceResponse.has("partyId")) {
			if (serviceResponse.get("serviceReqId").toString().equalsIgnoreCase(letterOfCredit.getSrmsReqOrderID())
					&& serviceResponse.get("partyId").toString().equalsIgnoreCase(customerId)) {
				return true;
			}
		}
		return false;
	}

	private JSONObject constructSRMSParams(ExportLCDrawingsDTO inputDTO) {
		JSONObject reqBody = new JSONObject();
		if (StringUtils.isNotEmpty(inputDTO.getDrawingAmount()))
			reqBody.put("drawingAmount", inputDTO.getDrawingAmount());
		if (StringUtils.isNotEmpty(inputDTO.getFinanceBill()))
			reqBody.put("financeBill", inputDTO.getFinanceBill());
		if (StringUtils.isNotEmpty(inputDTO.getCurrency()))
			reqBody.put("currency", inputDTO.getCurrency());
		if (StringUtils.isNotEmpty(inputDTO.getApplicant()))
			reqBody.put("applicant", inputDTO.getApplicant());
		if (StringUtils.isNotEmpty(inputDTO.getCreditAccount()))
			reqBody.put("creditAccount", inputDTO.getCreditAccount());
		if (StringUtils.isNotEmpty(inputDTO.getExternalAccount()))
			reqBody.put("externalAccount", inputDTO.getExternalAccount());
		if (StringUtils.isNotEmpty(inputDTO.getChargesDebitAccount()))
			reqBody.put("chargesDebitAccount", inputDTO.getChargesDebitAccount());
		if (StringUtils.isNotEmpty(inputDTO.getMessageToBank()))
			reqBody.put("messageToBank", inputDTO.getMessageToBank());
		if (StringUtils.isNotEmpty(inputDTO.getStatus()))
			reqBody.put("status", inputDTO.getStatus());
		if (StringUtils.isNotEmpty(inputDTO.getLcReferenceNo()))
			reqBody.put("lcReferenceNo", inputDTO.getLcReferenceNo());
		if (StringUtils.isNotEmpty(inputDTO.getPhysicalDocuments()))
			reqBody.put("physicalDocuments", inputDTO.getPhysicalDocuments());
		if (StringUtils.isNotEmpty(inputDTO.getLcType()))
			reqBody.put("lcType", inputDTO.getLcType());
		if (StringUtils.isNotEmpty(inputDTO.getUploadedDocuments()))
			reqBody.put("uploadedDocuments", inputDTO.getUploadedDocuments());
		if (StringUtils.isNotEmpty(inputDTO.getForwardDocuments()))
			reqBody.put("forwardDocuments", inputDTO.getForwardDocuments());
		if (StringUtils.isNotEmpty(inputDTO.getAdvisingBankReference()))
			reqBody.put("advisingBankReference", inputDTO.getAdvisingBankReference());
		if (StringUtils.isNotEmpty(inputDTO.getExportLCId()))
			reqBody.put("exportLCId", inputDTO.getExportLCId());
		if (StringUtils.isNotEmpty(inputDTO.getLcAmount()))
			reqBody.put("lcAmount", inputDTO.getLcAmount());
		if (StringUtils.isNotEmpty(inputDTO.getExpiryDate()))
			reqBody.put("expiryDate", inputDTO.getExpiryDate());
		if (StringUtils.isNotEmpty(inputDTO.getTotalDocuments()))
			reqBody.put("totalDocuments", inputDTO.getTotalDocuments());
		if (StringUtils.isNotEmpty(inputDTO.getDiscrepencies()))
			reqBody.put("discrepencies", inputDTO.getDiscrepencies());
		if (StringUtils.isNotEmpty(inputDTO.getDiscrepenciesAcceptance()))
			reqBody.put("discrepenciesAcceptance", inputDTO.getDiscrepenciesAcceptance());
		if (StringUtils.isNotEmpty(inputDTO.getPaymentStatus()))
			reqBody.put("paymentStatus", inputDTO.getPaymentStatus());
		if (StringUtils.isNotEmpty(inputDTO.getTotalAmount()))
			reqBody.put("totalAmount", inputDTO.getTotalAmount());
		if (StringUtils.isNotEmpty(inputDTO.getDocumentStatus()))
			reqBody.put("documentStatus", inputDTO.getDocumentStatus());
		if (StringUtils.isNotEmpty(inputDTO.getDiscrepanciesHistory1()))
			reqBody.put("discrepanciesHistory1",
					inputDTO.getDiscrepanciesHistory1().replace("'", "\'").replace("'", "\""));
		if (StringUtils.isNotEmpty(inputDTO.getDiscrepanciesHistory2()))
			reqBody.put("discrepanciesHistory2",
					inputDTO.getDiscrepanciesHistory2().replace("'", "\'").replace("'", "\""));
		if (StringUtils.isNotEmpty(inputDTO.getDiscrepanciesHistory3()))
			reqBody.put("discrepanciesHistory3",
					inputDTO.getDiscrepanciesHistory3().replace("'", "\'").replace("'", "\""));
		if (StringUtils.isNotEmpty(inputDTO.getDiscrepanciesHistory4()))
			reqBody.put("discrepanciesHistory4",
					inputDTO.getDiscrepanciesHistory4().replace("'", "\'").replace("'", "\""));
		if (StringUtils.isNotEmpty(inputDTO.getDiscrepanciesHistory5()))
			reqBody.put("discrepanciesHistory5",
					inputDTO.getDiscrepanciesHistory5().replace("'", "\'").replace("'", "\""));
		if (StringUtils.isNotEmpty(inputDTO.getLcCurrency()))
			reqBody.put("lcCurrency", inputDTO.getLcCurrency());
		if (StringUtils.isNotEmpty(inputDTO.getLcIssueDate()))
			reqBody.put("lcIssueDate", inputDTO.getLcIssueDate());
		if (StringUtils.isNotEmpty(inputDTO.getIssuingBank()))
			reqBody.put("issuingBank", inputDTO.getIssuingBank());
		if (StringUtils.isNotEmpty(inputDTO.getPaymentDate()))
			reqBody.put("paymentDate", inputDTO.getPaymentDate());
		if (StringUtils.isNotEmpty(inputDTO.getMessageFromBank()))
			reqBody.put("messageFromBank", inputDTO.getMessageFromBank());
		if (StringUtils.isNotEmpty(inputDTO.getReasonForReturn()))
			reqBody.put("reasonForReturn", inputDTO.getReasonForReturn());
		if (StringUtils.isNotEmpty(inputDTO.getReturnedDate()))
			reqBody.put("returnedDate", inputDTO.getReturnedDate());
		if (StringUtils.isNotEmpty(inputDTO.getReturnMessageToBank()))
			reqBody.put("returnMessageToBank", inputDTO.getReturnMessageToBank());
		if (StringUtils.isNotEmpty(inputDTO.getReturnedDocuments()))
			reqBody.put("returnedDocuments", inputDTO.getReturnedDocuments());
		if (StringUtils.isNotEmpty(inputDTO.getApprovedDate()))
			reqBody.put("approvedDate", inputDTO.getApprovedDate());
		if (StringUtils.isNotEmpty(inputDTO.getDocumentReference()))
			reqBody.put("documentReference", inputDTO.getDocumentReference());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date  = new Date();
		reqBody.put("drawingCreatedDate", sdf.format(date));

		return reqBody;
	}

	@Override
	public List<ExportLCDrawingsDTO> getExportLetterOfCreditDrawings(DataControllerRequest request) {
		Properties props = TradeFinanceProperties.loadProps(PARAM_PROPERTY);
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsDrawingType"));
		inputMap.put("subType", props.getProperty("ExportLetterOfCreditsDrawingsSubType"));

		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		String rawResponse = null;
		JSONObject drawingsResponse = new JSONObject();
		try {
			rawResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
		} catch (Exception e) {
			LOG.error("Unable to get Drawings requests " + e);
		}

		if (StringUtils.isNotBlank(rawResponse)) {
			drawingsResponse = new JSONObject(rawResponse);
			LOG.info("OMS Response " + rawResponse);
		}

		List<ExportLCDrawingsDTO> exportDrawings = new ArrayList<>();
		ExportLCDrawingsDTO exportDTO = new ExportLCDrawingsDTO();
		JSONArray Orders = drawingsResponse.getJSONArray(TradeFinanceConstants.PARAM_SERVICE_REQUESTS);
		for (int i = 0; i < Orders.length(); i++) {
			JSONObject singleOrder = Orders.getJSONObject(i);
			if (singleOrder.has(PARAM_INPUT_PAYLOAD)) {
				JSONObject inputPayload = singleOrder.getJSONObject(PARAM_INPUT_PAYLOAD);
				try {
					exportDTO = JSONUtils.parse(inputPayload.toString(), ExportLCDrawingsDTO.class);
					exportDTO.setDrawingSRMSRequestId(singleOrder.getString(PARAM_TRANS_ID));
					exportDTO.setDrawingReferenceNo(singleOrder.getString(PARAM_TRANS_ID));
					if (StringUtils.isNotBlank(exportDTO.getUploadedDocuments())) {
						JSONArray uploadedDocuments = new JSONArray(exportDTO.getUploadedDocuments());
						exportDTO.setUploadedDocuments(uploadedDocuments.toString());
					}
					if (StringUtils.isNotBlank(exportDTO.getReturnedDocuments())) {
						JSONArray returnedDocuments = new JSONArray(exportDTO.getReturnedDocuments());
						exportDTO.setReturnedDocuments(returnedDocuments.toString());
					}
					if (StringUtils.isNotBlank(exportDTO.getPhysicalDocuments())) {
						JSONArray physicalDocuments = new JSONArray(exportDTO.getPhysicalDocuments());
						exportDTO.setPhysicalDocuments(physicalDocuments.toString());
					}
					if (StringUtils.isNotBlank(exportDTO.getDiscrepencies())) {
						JSONArray discrepencies = new JSONArray(exportDTO.getDiscrepencies());
						exportDTO.setDiscrepencies(discrepencies.toString());
					}

					JSONArray discrepanciesHistory = new JSONArray();
					if (StringUtils.isNotBlank(exportDTO.getDiscrepanciesHistory1()))
						discrepanciesHistory.put(exportDTO.getDiscrepanciesHistory1());
					if (StringUtils.isNotBlank(exportDTO.getDiscrepanciesHistory2()))
						discrepanciesHistory.put(exportDTO.getDiscrepanciesHistory2());
					if (StringUtils.isNotBlank(exportDTO.getDiscrepanciesHistory3()))
						discrepanciesHistory.put(exportDTO.getDiscrepanciesHistory3());
					if (StringUtils.isNotBlank(exportDTO.getDiscrepanciesHistory4()))
						discrepanciesHistory.put(exportDTO.getDiscrepanciesHistory4());
					if (StringUtils.isNotBlank(exportDTO.getDiscrepanciesHistory5()))
						discrepanciesHistory.put(exportDTO.getDiscrepanciesHistory5());
					if (discrepanciesHistory.length() > 0)
						exportDTO.setDiscrepanciesHistory(discrepanciesHistory.toString());
				} catch (Exception e) {
					LOG.error("Exception occurred while fetching params: ", e);
				}
				exportDrawings.add(exportDTO);
			}
		}
		return exportDrawings;
	}

	@Override
	public ExportLCDrawingsDTO getExportLetterOfCreditDrawingById(DataControllerRequest request,
			String drawingSRMSRequestId) {
		Map<String, Object> inputMap = new HashMap<>();
		Properties props = TradeFinanceProperties.loadProps(PARAM_PROPERTY);
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsDrawingType"));
		inputMap.put("subtype", props.getProperty("ExportLetterOfCreditsDrawingsSubType"));
		inputMap.put("serviceRequestIds", drawingSRMSRequestId);


		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		String rawResponse = null;
		JSONObject jsonResponseObj = new JSONObject();

		try {
			rawResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getServiceName())
					.withOperationId(
							TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to GET SRMS ID " + e);
		}

		if (StringUtils.isNotBlank(rawResponse)) {
			jsonResponseObj = new JSONObject(rawResponse);
			LOG.info("OMS Response " + rawResponse);
		}

		ExportLCDrawingsDTO exportDrawingDetails = new ExportLCDrawingsDTO();
		JSONObject drawingDetailsResponse = jsonResponseObj.getJSONArray(PARAM_SERVICE_REQUESTS).getJSONObject(0);
		if (drawingDetailsResponse.has(PARAM_INPUT_PAYLOAD)) {
			JSONObject inputPayload = drawingDetailsResponse.getJSONObject(PARAM_INPUT_PAYLOAD);
			try {
				exportDrawingDetails = JSONUtils.parse(inputPayload.toString(), ExportLCDrawingsDTO.class);
				exportDrawingDetails.setDrawingSRMSRequestId(drawingDetailsResponse.getString(PARAM_TRANS_ID));
				exportDrawingDetails.setDrawingReferenceNo(drawingDetailsResponse.getString(PARAM_TRANS_ID));
				exportDrawingDetails.setCustomerId((String) drawingDetailsResponse.get(PARAM_PARTY_ID));

				if (StringUtils.isNotBlank(exportDrawingDetails.getUploadedDocuments())) {
					JSONArray uploadedDocuments = new JSONArray(exportDrawingDetails.getUploadedDocuments());
					exportDrawingDetails.setUploadedDocuments(uploadedDocuments.toString());
				}
				if (StringUtils.isNotBlank(exportDrawingDetails.getReturnedDocuments())) {
					JSONArray returnedDocuments = new JSONArray(exportDrawingDetails.getReturnedDocuments());
					exportDrawingDetails.setReturnedDocuments(returnedDocuments.toString());
				}
				if (StringUtils.isNotBlank(exportDrawingDetails.getPhysicalDocuments())) {
					JSONArray physicalDocuments = new JSONArray(exportDrawingDetails.getPhysicalDocuments());
					exportDrawingDetails.setPhysicalDocuments(physicalDocuments.toString());
				}
				if (StringUtils.isNotBlank(exportDrawingDetails.getDiscrepencies())) {
					JSONArray discrepencies = new JSONArray(exportDrawingDetails.getDiscrepencies());
					exportDrawingDetails.setDiscrepencies(discrepencies.toString());
				}

				JSONArray discrepanciesHistory = new JSONArray();
				if (StringUtils.isNotBlank(exportDrawingDetails.getDiscrepanciesHistory1()))
					discrepanciesHistory.put(exportDrawingDetails.getDiscrepanciesHistory1());
				if (StringUtils.isNotBlank(exportDrawingDetails.getDiscrepanciesHistory2()))
					discrepanciesHistory.put(exportDrawingDetails.getDiscrepanciesHistory2());
				if (StringUtils.isNotBlank(exportDrawingDetails.getDiscrepanciesHistory3()))
					discrepanciesHistory.put(exportDrawingDetails.getDiscrepanciesHistory3());
				if (StringUtils.isNotBlank(exportDrawingDetails.getDiscrepanciesHistory4()))
					discrepanciesHistory.put(exportDrawingDetails.getDiscrepanciesHistory4());
				if (StringUtils.isNotBlank(exportDrawingDetails.getDiscrepanciesHistory5()))
					discrepanciesHistory.put(exportDrawingDetails.getDiscrepanciesHistory5());
				if (discrepanciesHistory.length() > 0)
					exportDrawingDetails.setDiscrepanciesHistory(discrepanciesHistory.toString());
			} catch (Exception e) {
				LOG.error("Exception occurred while fetching params: ", e);
			}
		}

		String errorCode = drawingDetailsResponse.has("returnCode") ? drawingDetailsResponse.getString("returnCode")
				: "";
		String errorMessage = drawingDetailsResponse.has("additionalErrorInfo")
				? drawingDetailsResponse.getString("additionalErrorInfo")
				: "";

		if (StringUtils.isNotBlank(errorCode)) {
			exportDrawingDetails.setErrorCode(errorMessage);
		}
		if (StringUtils.isNotBlank(errorMessage)) {
			exportDrawingDetails.setErrorMessage(errorMessage);
		}
		if (jsonResponseObj.has("errmsg") && StringUtils.isNotBlank(jsonResponseObj.getString("errmsg"))) {
			exportDrawingDetails.setErrorMessage(jsonResponseObj.getString("errmsg"));
			LOG.error("Unable to get drawings request " + jsonResponseObj.getString("errmsg"));
		}

		return exportDrawingDetails;
	}

}
