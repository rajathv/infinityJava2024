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
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GuaranteeLCAmendmentsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.GuaranteeLCAmendmentsDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceProperties;

import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.getAmountWithCurrency;

public class GuaranteeLCAmendmentsBackendDelegateImpl implements GuaranteeLCAmendmentsBackendDelegate, TradeFinanceConstants {
	private static final Logger LOG = LogManager.getLogger(GuaranteeLCAmendmentsBackendDelegateImpl.class);

	@Override
	public List<GuaranteeLCAmendmentsDTO> getGuaranteeLCAmendments(DataControllerRequest request) {
		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);

		// Set Input Map
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("type", props.getProperty("GuaranteeLetterOfCreditAmendmentsType"));
		inputMap.put("subType", props.getProperty("GuaranteeLetterOfCreditAmendmentsSubType"));

		// Set Header Map
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));
		LOG.info("OMS Request" + headerMap.toString());

		String guaranteesResponse = null;
		JSONObject Response = new JSONObject();
		try {
			guaranteesResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();

		} catch (Exception e) {
			LOG.error("Unable to get guarantees requests " + e);
		}

		if (StringUtils.isNotBlank(guaranteesResponse)) {
			Response = new JSONObject(guaranteesResponse);
			LOG.info("OMS Response " + guaranteesResponse);
		}

		List<GuaranteeLCAmendmentsDTO> amendmentsList = new ArrayList<>();
		JSONArray Orders = Response.getJSONArray("serviceReqs");
		for (int i = 0; i < Orders.length(); i++) {
			JSONObject singleOrder = Orders.getJSONObject(i);
			if (singleOrder.has("serviceReqRequestIn")) {
				JSONObject inputPayload = singleOrder.getJSONObject("serviceReqRequestIn");
				GuaranteeLCAmendmentsDTO amendmentsDTO = new GuaranteeLCAmendmentsDTO();
				try {
                    amendmentsDTO = JSONUtils.parse(inputPayload.toString(), GuaranteeLCAmendmentsDTO.class);
                    amendmentsDTO.setAmendmentSRMSRequestId(singleOrder.getString("serviceReqId"));
                    amendmentsDTO.setAmendmentReference(singleOrder.getString("serviceReqId"));
                    amendmentsDTO.setAmountWithCurrency(getAmountWithCurrency(amendmentsDTO.getCurrency(), amendmentsDTO.getAmount(), false));
                    amendmentsDTO.setAmendRequestedDateFormatted(amendmentsDTO.getAmendRequestedDate().substring(0, 10));
				} catch (IOException e) {
					LOG.error("Exception occurred while processing guarantees: ", e);
				}
                amendmentsList.add(amendmentsDTO);
			}
		}
		return amendmentsList;
	}

	@Override
	public GuaranteeLCAmendmentsDTO createGuaranteeLCAmendment(GuaranteeLCAmendmentsDTO guaranteeReqPayloadDTO,
			DataControllerRequest request) {

		JSONObject amendResJSON = new JSONObject();
		String requestbody = constructSRMSReqBody(guaranteeReqPayloadDTO, amendResJSON).toString().replaceAll("\"",
				"'");

		// Set Input Map
		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("type", props.getProperty("GuaranteeLetterOfCreditAmendmentsType"));
		inputMap.put("subtype", props.getProperty("GuaranteeLetterOfCreditAmendmentsSubType"));
		inputMap.put("requestBody", requestbody);
		LOG.info("OMS Guarantee Amendment Request: " + inputMap.toString());

		// Set Header Map
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		// Making a call to order request API
		String amendRes = null;
		try {
			amendRes = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
			LOG.info("OMS Response " + amendRes);
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to create Guarantee request order. Error: " + e);
		}

		amendResJSON = new JSONObject(amendRes);
		request.addRequestParam_("isSrmsFailed",
				amendResJSON.has(TradeFinanceConstants.PARAM_ORDER_ID) ? "true" : "false");

		if (amendResJSON.has(TradeFinanceConstants.PARAM_ORDER_ID)
				&& StringUtils.isNotBlank(amendResJSON.getString(TradeFinanceConstants.PARAM_ORDER_ID))) {
			guaranteeReqPayloadDTO
					.setAmendmentSRMSRequestId(amendResJSON.getString(TradeFinanceConstants.PARAM_ORDER_ID));
			guaranteeReqPayloadDTO.setAmendmentReference(amendResJSON.getString(TradeFinanceConstants.PARAM_ORDER_ID));
		}
		if (amendResJSON.has("dbpErrMsg") && StringUtils.isNotBlank(amendResJSON.getString("dbpErrMsg"))) {
			guaranteeReqPayloadDTO.setDbpErrMsg(amendResJSON.getString("dbpErrMsg"));
			LOG.error("Failed to create guarantee amendment request order. Error Message"
					+ amendResJSON.getString("dbpErrMsg"));
		}
		if (amendResJSON.has("dbpErrCode") && StringUtils.isNotBlank(amendResJSON.getString("dbpErrCode"))) {
			guaranteeReqPayloadDTO.setDbpErrCode(String.valueOf(amendResJSON.getInt("dbpErrCode")));
			LOG.error("Failed to create guarantee amendment request order. Error Code"
					+ amendResJSON.getString("dbpErrCode"));
		}

		return guaranteeReqPayloadDTO;
	}

	@Override
	public GuaranteeLCAmendmentsDTO getGuaranteeLCAmendmentById(String amendmentSRMSId, DataControllerRequest request) {
		JSONObject Response = new JSONObject();
		GuaranteeLCAmendmentsDTO amendmentResDTO = new GuaranteeLCAmendmentsDTO();
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);
		String customerId = CustomerSession.getCustomerId(customer);

		// Set Input Map
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceRequestIds", amendmentSRMSId);

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
			amendmentResDTO.setDbpErrMsg("Failed to fetch record");
			LOG.error("Unable to GET SRMS ID " + e);
			return amendmentResDTO;
		}
		if (StringUtils.isNotBlank(response)) {
			Response = new JSONObject(response);
			LOG.info("OMS Response " + response);
		}

		try {
			JSONArray Orders = Response.getJSONArray("serviceReqs");
			JSONObject serviceResponse = Orders.getJSONObject(0);
			if (serviceResponse.has("serviceReqId") && serviceResponse.has("partyId")) {
				if (serviceResponse.get("serviceReqId").toString().equalsIgnoreCase(amendmentSRMSId)
						&& serviceResponse.get("partyId").toString().equalsIgnoreCase(customerId)) {
					if (serviceResponse.has("serviceReqRequestIn")) {
						JSONObject inputPayload = serviceResponse.getJSONObject("serviceReqRequestIn");
						try {
							amendmentResDTO = JSONUtils.parse(inputPayload.toString(), GuaranteeLCAmendmentsDTO.class);
							amendmentResDTO.setAmendmentSRMSRequestId((String) serviceResponse.get("serviceReqId"));
							amendmentResDTO.setAmendmentReference((String) serviceResponse.get("serviceReqId"));
						} catch (IOException e) {
							LOG.error("Exception occurred while fetching params: ", e);
							amendmentResDTO.setDbpErrMsg("Failed to fetch record");
						}
					}
				}
			} else {
				amendmentResDTO.setDbpErrMsg("Security Exception - Unauthorized Access ");
				LOG.info("OMS Response " + response);
				return amendmentResDTO;
			}
		} catch (Exception e) {
			amendmentResDTO.setDbpErrMsg("Failed to fetch record");
			LOG.info("OMS Response " + response);
			LOG.error("Unable to GET SRMS ID " + e);
			return amendmentResDTO;
		}
		return amendmentResDTO;
	}
	public GuaranteeLCAmendmentsDTO updateGuaranteeAmendment(GuaranteeLCAmendmentsDTO guaranteeReqPayloadDTO,JSONObject inputObj,
																   DataControllerRequest request){
		String requestBody = constructSRMSReqBody(guaranteeReqPayloadDTO, inputObj).toString().replaceAll("\"",
				"'");
		Properties props = TradeFinanceProperties.loadProps(PARAM_PROPERTY);
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("type", props.getProperty("GuaranteeLetterOfCreditAmendmentsType"));
		inputMap.put("subtype", props.getProperty("GuaranteeLetterOfCreditAmendmentsSubType"));
		inputMap.put("serviceRequestId", guaranteeReqPayloadDTO.getAmendmentSRMSRequestId());
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
				return guaranteeReqPayloadDTO;
			} else {
				LOG.error("Unable to update export LC drawing request order. Error Code: " + errorCode
						+ " Error Message: " + errorMessage);
				guaranteeReqPayloadDTO.setDbpErrCode(errorCode);
				guaranteeReqPayloadDTO.setDbpErrMsg(errorMessage);
			}
			return guaranteeReqPayloadDTO;
		} catch (Exception e) {
			LOG.error("Unable to update Drawings requests " + e);
			return null;
		}

	}

	private JSONObject constructSRMSReqBody(GuaranteeLCAmendmentsDTO inputDTO, JSONObject reqBody) {
		if (StringUtils.isNotEmpty(inputDTO.getBenificiaryName()))
			reqBody.put("benificiaryName", inputDTO.getBenificiaryName());
		if (StringUtils.isNotBlank(inputDTO.getBeneficiaryDetails()))
			reqBody.put("beneficiaryDetails", inputDTO.getBeneficiaryDetails().replaceAll("\'", "\""));
		if (StringUtils.isNotEmpty(inputDTO.getGuaranteesReference()))
			reqBody.put("guaranteesReference", inputDTO.getGuaranteesReference());
		if (StringUtils.isNotEmpty(inputDTO.getCurrency()))
			reqBody.put("currency", inputDTO.getCurrency());
		if (StringUtils.isNotEmpty(inputDTO.getAmount()))
			reqBody.put("amount", inputDTO.getAmount());
		if (StringUtils.isNotEmpty(inputDTO.getExpiryDate()))
			reqBody.put("expiryDate", inputDTO.getExpiryDate());
		if (StringUtils.isNotEmpty(inputDTO.getProductType()))
			reqBody.put("productType", inputDTO.getProductType());
		if (StringUtils.isNotEmpty(inputDTO.getIssueDate()))
			reqBody.put("issueDate", inputDTO.getIssueDate());
		if (StringUtils.isNotEmpty(inputDTO.getInstructingParty()))
			reqBody.put("instructingParty", inputDTO.getInstructingParty().replaceAll("\'", "\""));
		if (StringUtils.isNotEmpty(inputDTO.getBillType()))
			reqBody.put("billType", inputDTO.getBillType());
		if (StringUtils.isNotEmpty(inputDTO.getExpiryType()))
			reqBody.put("expiryType", inputDTO.getExpiryType());
		if (StringUtils.isNotEmpty(inputDTO.getApplicantParty()))
			reqBody.put("applicantParty", inputDTO.getApplicantParty().replaceAll("\'", "\""));
		if (StringUtils.isNotEmpty(inputDTO.getAmendmentNo()))
			reqBody.put("amendmentNo", inputDTO.getAmendmentNo().replaceAll("\'", "\""));
		if (StringUtils.isNotEmpty(inputDTO.getAmendmentEffectiveDate()))
			reqBody.put("amendmentEffectiveDate", inputDTO.getAmendmentEffectiveDate());
		if (StringUtils.isNotEmpty(inputDTO.getAmendAmount()))
			reqBody.put("amendAmount", inputDTO.getAmendAmount());
		if (StringUtils.isNotEmpty(inputDTO.getAmendCharges()))
			reqBody.put("amendCharges", inputDTO.getAmendCharges());
		if (StringUtils.isNotEmpty(inputDTO.getAmendExpiryType()))
			reqBody.put("amendExpiryType", inputDTO.getAmendExpiryType());
		if (StringUtils.isNotEmpty(inputDTO.getAmendExpiryDate()))
			reqBody.put("amendExpiryDate", inputDTO.getAmendExpiryDate());
		if (StringUtils.isNotEmpty(inputDTO.getAmendExpiryCondition()))
			reqBody.put("amendExpiryCondition", inputDTO.getAmendExpiryCondition());
		if (StringUtils.isNotEmpty(inputDTO.getAmendDetails()))
			reqBody.put("amendDetails", inputDTO.getAmendDetails());
		if (StringUtils.isNotEmpty(inputDTO.getMessageToBank()))
			reqBody.put("messageToBank", inputDTO.getMessageToBank());
		if (StringUtils.isNotEmpty(inputDTO.getAmendStatus()))
			reqBody.put("amendStatus", inputDTO.getAmendStatus());
		if (StringUtils.isNotEmpty(inputDTO.getAmendmentReference()))
			reqBody.put("amendmentReference", inputDTO.getAmendmentReference());
		if (StringUtils.isNotEmpty(inputDTO.getAmendmentSRMSRequestId()))
			reqBody.put("amendmentSRMSRequestId", inputDTO.getAmendmentSRMSRequestId());
		if (StringUtils.isNotEmpty(inputDTO.getCancellationStatus()))
			reqBody.put("cancellationStatus", inputDTO.getCancellationStatus());
		if (StringUtils.isNotEmpty(inputDTO.getApprovedDate()))
			reqBody.put("approvedDate", inputDTO.getApprovedDate());
		if (StringUtils.isNotEmpty(inputDTO.getReasonForReturned()))
			reqBody.put("reasonForReturned", inputDTO.getReasonForReturned());
		if (StringUtils.isNotEmpty(inputDTO.getReturnMessage()))
			reqBody.put("returnMessage", inputDTO.getReturnMessage());
		if (StringUtils.isNotEmpty(inputDTO.getCorporateUserName()))
			reqBody.put("corporateUserName", inputDTO.getCorporateUserName());
		if (StringUtils.isNotEmpty(inputDTO.getSupportingDocument()))
			reqBody.put("supportingDocument", inputDTO.getSupportingDocument().replaceAll("\'", "\""));
		if (StringUtils.isNotEmpty(inputDTO.getRejectedReason()))
			reqBody.put("rejectedReason", inputDTO.getRejectedReason());
		if (StringUtils.isNotEmpty(inputDTO.getRejectedDate()))
			reqBody.put("rejectedDate", inputDTO.getRejectedDate());
		if (StringUtils.isNotEmpty(inputDTO.getGuaranteesSRMSId()))
			reqBody.put("guaranteesSRMSId", inputDTO.getGuaranteesSRMSId());
		if (StringUtils.isNotEmpty(inputDTO.getAmendRequestedDate()))
			reqBody.put("amendRequestedDate", inputDTO.getAmendRequestedDate());
		if (StringUtils.isNotEmpty(inputDTO.getHistoryCount()))
			reqBody.put("historyCount", inputDTO.getHistoryCount());
		if (StringUtils.isNotEmpty(inputDTO.getAmendmentHistory1()))
			reqBody.put("amendmentHistory1", new JSONObject(inputDTO.getAmendmentHistory1()).toString());
		if (StringUtils.isNotEmpty(inputDTO.getAmendmentHistory2()))
			reqBody.put("amendmentHistory2", new JSONObject(inputDTO.getAmendmentHistory2()).toString());
		if (StringUtils.isNotEmpty(inputDTO.getAmendmentHistory3()))
			reqBody.put("amendmentHistory3", new JSONObject(inputDTO.getAmendmentHistory3()).toString());
		if (StringUtils.isNotEmpty(inputDTO.getAmendmentHistory4()))
			reqBody.put("amendmentHistory4", new JSONObject(inputDTO.getAmendmentHistory4()).toString());
		if (StringUtils.isNotEmpty(inputDTO.getAmendmentHistory5()))
			reqBody.put("amendmentHistory5", new JSONObject(inputDTO.getAmendmentHistory5()).toString());
		return reqBody;
	}

}
