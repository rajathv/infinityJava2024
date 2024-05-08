/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.CreateExportLCBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.ExportLOCDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceProperties;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.getCurrentDateTimeUTF;

public class CreateExportLCBackendDelegateImpl implements CreateExportLCBackendDelegate, TradeFinanceConstants {
	private static final Logger LOG = LogManager.getLogger(CreateExportLCBackendDelegateImpl.class);

	@Override
	public ExportLOCDTO createExportLetterOfCredit(ExportLOCDTO createPayloadDTO, DataControllerRequest request) {
		
		JSONObject reqBody = constructSRMSParams(createPayloadDTO);
		String requestbody = reqBody.toString().replaceAll("\"", "'");

		Properties props = TradeFinanceProperties.loadProps(PARAM_PROPERTY);
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsType"));
		inputMap.put("subtype", props.getProperty("ExportLetterOfCreditsSubType"));
		inputMap.put("requestBody", requestbody);

		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		String createOrder = null;
		try {
			createOrder = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
		} catch (Exception e) {
			LOG.error("Unable to create Letter Of Credit request order " + e);
		}
		if (StringUtils.isNotEmpty(createOrder)) {
			JSONObject responseObject = new JSONObject();
			responseObject = new JSONObject(createOrder);
			if (!responseObject.has("dbpErrMsg")) {
				createPayloadDTO.setExportLCId(responseObject.get("orderId").toString());
				createPayloadDTO.setLcReferenceNo(responseObject.get("orderId").toString());
			}
			if (responseObject.has("dbpErrMsg") && StringUtils.isNotEmpty(responseObject.getString("dbpErrMsg"))) {
				createPayloadDTO.setErrorMsg(responseObject.getString("dbpErrMsg"));
				LOG.error("Unable to create export LC request order Error Message"
						+ responseObject.getString("dbpErrMsg"));
			}

			if (responseObject.has("dbpErrCode")
					&& StringUtils.isNotEmpty(String.valueOf(responseObject.get("dbpErrCode")))) {
				createPayloadDTO.setErrorCode(String.valueOf(responseObject.get("dbpErrCode")));
				LOG.error("Unable to create export LC request order Error Code"
						+ String.valueOf(responseObject.get("dbpErrCode")));
			}
		}
		return createPayloadDTO;
	}

	public ExportLOCDTO updateExportLetterOfCredit(ExportLOCDTO createPayloadDTO, DataControllerRequest request) {		
		
		JSONObject reqBody = constructSRMSParams(createPayloadDTO);
		String requestbody = reqBody.toString().replaceAll("\"", "'");

		Properties props = TradeFinanceProperties.loadProps(PARAM_PROPERTY);
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("type", props.getProperty("ExportLetterOfCreditsType"));
		inputMap.put("subtype", props.getProperty("ExportLetterOfCreditsSubType"));
		inputMap.put("serviceRequestId", createPayloadDTO.getExportLCId());
		inputMap.put("requestBody", requestbody);

		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		String createOrder = null;
		try {
			createOrder = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
		} catch (Exception e) {
			LOG.error("Unable to create Letter Of Credit request order " + e);
		}
		if (StringUtils.isNotEmpty(createOrder)) {
			JSONObject responseObject = new JSONObject();
			responseObject = new JSONObject(createOrder);
			if (!responseObject.has("dbpErrMsg")) {
				createPayloadDTO.setExportLCId(responseObject.get("orderId").toString());
				createPayloadDTO.setLcReferenceNo(responseObject.get("orderId").toString());
			}
			if (responseObject.has("dbpErrMsg") && StringUtils.isNotEmpty(responseObject.getString("dbpErrMsg"))) {
				createPayloadDTO.setErrorMsg(responseObject.getString("dbpErrMsg"));
				LOG.error("Unable to update export LC request order Error Message"
						+ responseObject.getString("dbpErrMsg"));
			}

			if (responseObject.has("dbpErrCode")
					&& StringUtils.isNotEmpty(String.valueOf(responseObject.get("dbpErrCode")))) {
				createPayloadDTO.setErrorCode(String.valueOf(responseObject.get("dbpErrCode")));
				LOG.error("Unable to update export LC request order Error Code"
						+ String.valueOf(responseObject.get("dbpErrCode")));
			}
		}
		return createPayloadDTO;
	}

	private JSONObject constructSRMSParams(ExportLOCDTO createPayloadDTO) {
		JSONObject requestBody = new JSONObject();
		if (StringUtils.isNotEmpty(createPayloadDTO.getDrawingAmount()))
			requestBody.put("drawingAmount", createPayloadDTO.getDrawingAmount());
		if (StringUtils.isNotEmpty(createPayloadDTO.getLcType()))
			requestBody.put("lcType", createPayloadDTO.getLcType());
		if (StringUtils.isNotEmpty(createPayloadDTO.getLcReferenceNo()))
			requestBody.put("lcReferenceNo", createPayloadDTO.getLcReferenceNo());
		if (StringUtils.isNotEmpty(createPayloadDTO.getIssuingBankReference()))
			requestBody.put("issuingBankReference", createPayloadDTO.getIssuingBankReference());
		if (StringUtils.isNotEmpty(createPayloadDTO.getAdvisingBankReference()))
			requestBody.put("advisingBankReference", createPayloadDTO.getAdvisingBankReference());
		if (StringUtils.isNotEmpty(createPayloadDTO.getApplicant()))
			requestBody.put("applicant", createPayloadDTO.getApplicant());
		if (StringUtils.isNotEmpty(createPayloadDTO.getUtilizedLCAmount()))
			requestBody.put("utilizedLCAmount", createPayloadDTO.getUtilizedLCAmount());
		if (StringUtils.isNotEmpty(createPayloadDTO.getIssueDate()))
			requestBody.put("issueDate", createPayloadDTO.getIssueDate());
		if (StringUtils.isNotEmpty(createPayloadDTO.getAmount()))
			requestBody.put("amount", createPayloadDTO.getAmount());
		if (StringUtils.isNotEmpty(createPayloadDTO.getExpiryDate()))
			requestBody.put("expiryDate", createPayloadDTO.getExpiryDate());
		if (StringUtils.isNotEmpty(createPayloadDTO.getCurrency()))
			requestBody.put("currency", createPayloadDTO.getCurrency());
		if (StringUtils.isNotEmpty(createPayloadDTO.getIssuingBank()))
			requestBody.put("issuingBank", createPayloadDTO.getIssuingBank());
		if (StringUtils.isNotEmpty(createPayloadDTO.getApplicantaddress()))
			requestBody.put("applicantaddress", createPayloadDTO.getApplicantaddress());
		if (StringUtils.isNotEmpty(createPayloadDTO.getIssuingbankaddress()))
			requestBody.put("issuingbankaddress", createPayloadDTO.getIssuingbankaddress());
		if (StringUtils.isNotEmpty(createPayloadDTO.getPaymentTerms()))
			requestBody.put("paymentTerms", createPayloadDTO.getPaymentTerms());
		if (StringUtils.isNotEmpty(createPayloadDTO.getDocumentName()))
			requestBody.put("documentName", createPayloadDTO.getDocumentName());
		if (StringUtils.isNotEmpty(createPayloadDTO.getUploadedFiles()))
			requestBody.put("uploadedFiles", createPayloadDTO.getUploadedFiles());
		if (StringUtils.isNotEmpty(createPayloadDTO.getForwardContract()))
			requestBody.put("forwardContract", createPayloadDTO.getForwardContract());
		if (StringUtils.isNotEmpty(createPayloadDTO.getBeneficiaryName()))
			requestBody.put("beneficiaryName", createPayloadDTO.getBeneficiaryName());
		if (StringUtils.isNotEmpty(createPayloadDTO.getBeneficiaryAddress()))
			requestBody.put("beneficiaryAddress", createPayloadDTO.getBeneficiaryAddress());
		if (StringUtils.isNotEmpty(createPayloadDTO.getGoodsDescription()))
			requestBody.put("goodsDescription", createPayloadDTO.getGoodsDescription());
		if (StringUtils.isNotEmpty(createPayloadDTO.getAdditionalConditions()))
			requestBody.put("additionalConditions", createPayloadDTO.getAdditionalConditions());
		if (StringUtils.isNotEmpty(createPayloadDTO.getConfirmInstructions()))
			requestBody.put("confirmInstructions", createPayloadDTO.getConfirmInstructions());
		if (StringUtils.isNotEmpty(createPayloadDTO.getLatestShipmentDate()))
			requestBody.put("latestShipmentDate", createPayloadDTO.getLatestShipmentDate());
		if (StringUtils.isNotEmpty(createPayloadDTO.getStatus()))
			requestBody.put("status", createPayloadDTO.getStatus());
		if(StringUtils.isNotEmpty(createPayloadDTO.getAmendmentNo()))
			requestBody.put("amendmentNo", createPayloadDTO.getAmendmentNo());
		if(StringUtils.isNotEmpty(createPayloadDTO.getBeneficiaryConsent()))
			requestBody.put("beneficiaryConsent", createPayloadDTO.getBeneficiaryConsent());
		if(StringUtils.isNotEmpty(createPayloadDTO.getMessageToBank()))
			requestBody.put("messageToBank", createPayloadDTO.getMessageToBank());
		if(StringUtils.isNotEmpty(createPayloadDTO.getReasonForRejection()))
			requestBody.put("reasonForRejection", createPayloadDTO.getReasonForRejection());
		if(StringUtils.isNotEmpty(createPayloadDTO.getLcUpdatedOn()))
			requestBody.put("lcUpdatedOn", createPayloadDTO.getLcUpdatedOn());
		return requestBody;
	}
}
