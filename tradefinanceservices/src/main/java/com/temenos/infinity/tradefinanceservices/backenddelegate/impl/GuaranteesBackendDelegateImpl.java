/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GuaranteesBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.ClauseDTO;
import com.temenos.infinity.tradefinanceservices.dto.GuranteesDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceSRMSUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GuaranteesBackendDelegateImpl implements GuaranteesBackendDelegate, TradeFinanceConstants {
	private static final Logger LOG = LogManager.getLogger(GuaranteesBackendDelegateImpl.class);

	public GuranteesDTO createGuarantees(GuranteesDTO guaranteesDto, DataControllerRequest request) {

		JSONObject reqBody = constructSRMSBody(guaranteesDto, new JSONObject());
		String requestBody = reqBody.toString().replaceAll("\"", "'");
		JSONObject responseObject = TradeFinanceSRMSUtils.invoke().
				createOrder().
				addRequestBody(requestBody).
				addDataControllerRequest(request).
				addTypeAndSubType("GuaranteesType", "GuaranteesSubType").
				sendRequest().
				fetchResponse();

		if (!responseObject.has("dbpErrMsg")) {
			guaranteesDto.setGuaranteesSRMSId(responseObject.get("orderId").toString());
			guaranteesDto.setGuaranteesReferenceNo(responseObject.get("orderId").toString());
			guaranteesDto.setMessage(responseObject.get(MESSAGE).toString());
		} else {
			guaranteesDto = new GuranteesDTO();
			guaranteesDto.setDbpErrMsg(responseObject.getString("dbpErrMsg"));
			guaranteesDto.setDbpErrCode(responseObject.getString("dbpErrCode"));
		}
		return guaranteesDto;
	}

	public GuranteesDTO updateGuarantees(GuranteesDTO guaranteesDto, DataControllerRequest request) {

		JSONObject Response;
		TradeFinanceSRMSUtils<GuranteesDTO> srmsUtils = TradeFinanceSRMSUtils.invoke();
		Response = srmsUtils.
				addServiceRequestId(guaranteesDto.getGuaranteesSRMSId()).
				addDataControllerRequest(request).
				getOrderById().
				sendRequest().
				fetchResponse();
		if (!srmsUtils.getIsServiceRequestFailed()) {
			try {
				JSONArray Orders = Response.getJSONArray("serviceReqs");
				JSONObject serviceResponse = Orders.getJSONObject(0);
				if (serviceResponse.has("serviceReqId")) {
					if (serviceResponse.has("serviceReqRequestIn")) {
						JSONObject inputPayload = serviceResponse.getJSONObject("serviceReqRequestIn");
						if (inputPayload.has("status") && inputPayload.get("status").equals(PARAM_STATUS_DELETED)) {
							guaranteesDto = new GuranteesDTO();
							guaranteesDto.setDbpErrMsg("Requested record is already deleted");
							return guaranteesDto;
						}
						JSONObject reqBody = constructSRMSBody(guaranteesDto, inputPayload);
						String requestBody = reqBody.toString().replaceAll("\"", "'");
						Response = TradeFinanceSRMSUtils.invoke().
								updateOrder().
								addServiceRequestId(guaranteesDto.getGuaranteesSRMSId()).
								addRequestBody(requestBody).
								addDataControllerRequest(request).
								sendRequest().
								fetchResponse();
						LOG.info("OMS Response " + Response);
					}
				}
			} catch (Exception e) {
				srmsUtils.setIsServiceRequestFailed(true);
				LOG.info("OMS Response " + Response);
				LOG.error("Unable to GET SRMS ID " + e);
			}
		}
		if (srmsUtils.getIsServiceRequestFailed()) {
			guaranteesDto = new GuranteesDTO();
			guaranteesDto.setDbpErrMsg(Response.getString("dbpErrCode"));
			guaranteesDto.setDbpErrCode(Response.getString("dbpErrCode"));
		} else {
			guaranteesDto.setGuaranteesReferenceNo(Response.get("serviceReqId").toString());
			guaranteesDto.setMessage(Response.get(MESSAGE).toString());
		}
		return guaranteesDto;
	}

	public GuranteesDTO getGuaranteesById(String srmsId, DataControllerRequest request) {

		GuranteesDTO guaranteeDto = null;
		try {
			guaranteeDto = (GuranteesDTO) TradeFinanceSRMSUtils.invoke().
					addDTO(GuranteesDTO.class).
					addServiceRequestId(srmsId).
					addDataControllerRequest(request).
					getOrderById().
					sendRequest().
					fetchOrderByIdResponse();
		} catch (IOException e) {
			LOG.error("Error occurred while fetching guarantees");
		}
		return guaranteeDto;
	}

	@Override
	public List<GuranteesDTO> getGuranteesLC(GuranteesDTO guranteesDTO, DataControllerRequest request) {
		List guarantees = null;
		try {
			guarantees = TradeFinanceSRMSUtils.invoke().
					addDTO(GuranteesDTO.class).
					addDataControllerRequest(request).
					addTypeAndSubType("GuaranteesType", "GuaranteesSubType").
					getOrders().
					sendRequest().
					fetchOrdersResponseWithDTO();
		} catch (IOException e) {
			LOG.error("Error occurred while fetching guarantees");
		}
		return guarantees;
	}

	private JSONObject constructSRMSBody(GuranteesDTO dto, JSONObject reqBody) {
		if (StringUtils.isNotBlank(dto.getBeneficiaryName()))
			reqBody.put("beneficiaryName", dto.getBeneficiaryName());
		if(StringUtils.isNotBlank(dto.getGuaranteesReferenceNo()))
			reqBody.put("guaranteesReferenceNo",dto.getGuaranteesReferenceNo());
		if (StringUtils.isNotBlank(dto.getStatus()))
			reqBody.put("status", dto.getStatus());
		if (StringUtils.isNotBlank(dto.getIssueDate()))
			reqBody.put("issueDate", dto.getIssueDate());
		if (StringUtils.isNotBlank(dto.getExpiryDate()))
			reqBody.put("expiryDate", dto.getExpiryDate());
		if (StringUtils.isNotBlank(dto.getExpiryType()))
			reqBody.put("expiryType", dto.getExpiryType());
		if (StringUtils.isNotBlank(dto.getModeOfTransaction()))
			reqBody.put("modeOfTransaction", dto.getModeOfTransaction());
		if (StringUtils.isNotBlank(dto.getAdvisingBank()))
			reqBody.put("advisingBank", dto.getAdvisingBank());
		if (StringUtils.isNotBlank(dto.getCustomerId()))
			reqBody.put("customerId", dto.getCustomerId());
		if (StringUtils.isNotBlank(dto.getInstructingParty()))
			reqBody.put("instructingParty", dto.getInstructingParty().replaceAll("\'", "\""));
		if (StringUtils.isNotBlank(dto.getApplicantParty()))
			reqBody.put("applicantParty", dto.getApplicantParty().replaceAll("\'", "\""));
		if (StringUtils.isNotBlank(dto.getExpectedIssueDate()))
			reqBody.put("expectedIssueDate", dto.getExpectedIssueDate());
		if (StringUtils.isNotBlank(dto.getClaimExpiryDate()))
			reqBody.put("claimExpiryDate", dto.getClaimExpiryDate());
		if (StringUtils.isNotBlank(dto.getExpiryCondition()))
			reqBody.put("expiryCondition", dto.getExpiryCondition());
		if (StringUtils.isNotBlank(dto.getExtendExpiryDate()))
			reqBody.put("extendExpiryDate", dto.getExtendExpiryDate());
		if (StringUtils.isNotBlank(dto.getExtensionCapPeriod()))
			reqBody.put("extensionCapPeriod", dto.getExtensionCapPeriod());
		if (StringUtils.isNotBlank(dto.getExtensionPeriod()))
			reqBody.put("extensionPeriod", dto.getExtensionPeriod());
		if (StringUtils.isNotBlank(dto.getNotificationPeriod()))
			reqBody.put("notificationPeriod", dto.getNotificationPeriod());
		if (StringUtils.isNotBlank(dto.getExtensionDetails()))
			reqBody.put("extensionDetails", dto.getExtensionDetails());
		if (StringUtils.isNotBlank(dto.getGoverningLaw()))
			reqBody.put("governingLaw", dto.getGoverningLaw());
		if (StringUtils.isNotBlank(dto.getOtherInstructions()))
			reqBody.put("otherInstructions", dto.getOtherInstructions());
		if (StringUtils.isNotBlank(dto.getBeneficiaryType()))
			reqBody.put("beneficiaryType", dto.getBeneficiaryType());
		if (StringUtils.isNotBlank(dto.getDeliveryInstructions()))
			reqBody.put("deliveryInstructions", dto.getDeliveryInstructions());
		if (StringUtils.isNotBlank(dto.getBeneficiaryAddress1()))
			reqBody.put("beneficiaryAddress1", dto.getBeneficiaryAddress1());
		if (StringUtils.isNotBlank(dto.getBeneficiaryAddress2()))
			reqBody.put("beneficiaryAddress2", dto.getBeneficiaryAddress2());
		if (StringUtils.isNotBlank(dto.getCity()))
			reqBody.put("city", dto.getCity());
		if (StringUtils.isNotBlank(dto.getState()))
			reqBody.put("state", dto.getState());
		if (StringUtils.isNotBlank(dto.getCountry()))
			reqBody.put("country", dto.getCountry());
		if (StringUtils.isNotBlank(dto.getZipCode()))
			reqBody.put("zipCode", dto.getZipCode());
		if (StringUtils.isNotBlank(dto.getSaveBeneficiary()))
			reqBody.put("saveBeneficiary", dto.getSaveBeneficiary());
		if (StringUtils.isNotBlank(dto.getSwiftCode()))
			reqBody.put("swiftCode", dto.getSwiftCode());
		if (StringUtils.isNotBlank(dto.getBankName()))
			reqBody.put("bankName", dto.getBankName());
		if (StringUtils.isNotBlank(dto.getIban()))
			reqBody.put("iban", dto.getIban());
		if (StringUtils.isNotBlank(dto.getLocalCode()))
			reqBody.put("localCode", dto.getLocalCode());
		if (StringUtils.isNotBlank(dto.getBankAddress1()))
			reqBody.put("bankAddress1", dto.getBankAddress1());
		if (StringUtils.isNotBlank(dto.getBankAddress2()))
			reqBody.put("bankAddress2", dto.getBankAddress2());
		if (StringUtils.isNotBlank(dto.getBankCity()))
			reqBody.put("bankCity", dto.getBankCity());
		if (StringUtils.isNotBlank(dto.getBankState()))
			reqBody.put("bankState", dto.getBankState());
		if (StringUtils.isNotBlank(dto.getBankCountry()))
			reqBody.put("bankCountry", dto.getBankCountry());
		if (StringUtils.isNotBlank(dto.getBankZipCode()))
			reqBody.put("bankZipCode", dto.getBankZipCode());
		if (StringUtils.isNotBlank(dto.getInstructionCurrencies()))
			reqBody.put("instructionCurrencies", dto.getInstructionCurrencies().replaceAll("\'", "\""));
		else if(reqBody.has("instructionCurrencies"))
			reqBody.put("instructionCurrencies", reqBody.getString("instructionCurrencies").replaceAll("\'", "\""));
		if (StringUtils.isNotBlank(dto.getLimitInstructions()))
			reqBody.put("limitInstructions", dto.getLimitInstructions().replaceAll("\'", "\""));
		else if(reqBody.has("limitInstructions"))
			reqBody.put("limitInstructions", reqBody.getString("limitInstructions").replaceAll("\'", "\""));
		if (StringUtils.isNotBlank(dto.getOtherBankInstructions()))
			reqBody.put("otherBankInstructions", dto.getOtherBankInstructions());
		if (StringUtils.isNotBlank(dto.getMessageToBank()))
			reqBody.put("messageToBank", dto.getMessageToBank());
		if (StringUtils.isNotBlank(dto.getDocumentReferences()))
			reqBody.put("documentReferences", dto.getDocumentReferences().replaceAll("\'", "\""));
		else if(reqBody.has("documentReferences"))
			reqBody.put("documentReferences", reqBody.getString("documentReferences").replaceAll("\'", "\""));
		if (StringUtils.isNotBlank(dto.getDocumentName()))
			reqBody.put("documentName", dto.getDocumentName().replaceAll("\'", "\""));
		else if(reqBody.has("documentName"))
			reqBody.put("documentName", reqBody.getString("documentName").replaceAll("\'", "\""));
		if (StringUtils.isNotBlank(dto.getClauseConditions()))
			reqBody.put("clauseConditions", dto.getClauseConditions().replaceAll("\'", "\""));
		else if(reqBody.has("clauseConditions"))
			reqBody.put("clauseConditions", reqBody.getString("clauseConditions").replaceAll("\'", "\""));
		if (StringUtils.isNotBlank(dto.getBeneficiaryDetails()))
			reqBody.put("beneficiaryDetails", dto.getBeneficiaryDetails().replaceAll("\'", "\""));
		else if(reqBody.has("beneficiaryDetails"))
			reqBody.put("beneficiaryDetails", reqBody.getString("beneficiaryDetails").replaceAll("\'", "\""));
		if (StringUtils.isNotBlank(dto.getTotalAmount()))
			reqBody.put("totalAmount", dto.getTotalAmount());
		if (StringUtils.isNotBlank(dto.getCreatedOn()))
			reqBody.put("createdOn", dto.getCreatedOn());
		if (StringUtils.isNotBlank(dto.getApplicableRules()))
			reqBody.put("applicableRules", dto.getApplicableRules());
		if (StringUtils.isNotBlank(dto.getDemandAcceptance()))
			reqBody.put("demandAcceptance", dto.getDemandAcceptance());
		if (StringUtils.isNotBlank(dto.getPartialDemandPercentage()))
			reqBody.put("partialDemandPercentage", dto.getPartialDemandPercentage());
		if (StringUtils.isNotBlank(dto.getIsSingleSettlement()))
			reqBody.put("isSingleSettlement", dto.getIsSingleSettlement());
		if (StringUtils.isNotBlank(dto.getTransferable()))
			reqBody.put("transferable", dto.getTransferable());
		if (StringUtils.isNotBlank(dto.getAmount()))
			reqBody.put("amount", dto.getAmount());
		if (StringUtils.isNotBlank(dto.getCurrency()))
			reqBody.put("currency", dto.getCurrency());
		if (StringUtils.isNotBlank(dto.getReturnHistory()))
			reqBody.put("returnHistory", dto.getReturnHistory().replaceAll("'", "\""));
		else if (reqBody.has("returnHistory"))
			reqBody.put("returnHistory", reqBody.getString("returnHistory").replaceAll("'", "\""));
		if (StringUtils.isNotBlank(dto.getAmendmentNo()))
			reqBody.put("amendmentNo", dto.getAmendmentNo().replaceAll("\'", "\""));
		else if(reqBody.has("amendmentNo"))
			reqBody.put("amendmentNo", reqBody.getString("amendmentNo").replaceAll("\'", "\""));
		reqBody.put("productType", dto.getProductType() == null ? " " : dto.getProductType());
		reqBody.put("guaranteeAndSBLCType", dto.getGuaranteeAndSBLCType() == null ? " " : dto.getGuaranteeAndSBLCType());
		reqBody.put("serviceRequestTime", getCurrentDateTimeUTF());
		return reqBody;
	}
	public String getCurrentDateTimeUTF() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
		return dateFormat.format(new Date());
	}

	public List<ClauseDTO> createClauses(Map<String, Object> requestParameters, DataControllerRequest dataControllerRequest) {

		List<ClauseDTO> clauseDTOs = new ArrayList<>();

		String clauseResponse = null;
		try {
			clauseResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(TradeFinanceAPIServices.DBPRBLOCALSERVICE_PAYEMENT_CREATE_CLAUSE.getServiceName()).
					withObjectId(null).
					withOperationId(TradeFinanceAPIServices.DBPRBLOCALSERVICE_PAYEMENT_CREATE_CLAUSE.getOperationName()).
					withRequestParameters(requestParameters).
					withRequestHeaders(null).
					withDataControllerRequest(dataControllerRequest).
					build().getResponse();

			JSONObject clasueJSON = new JSONObject(clauseResponse);
			JSONArray records = clasueJSON.getJSONArray("LoopDataset");
			JSONArray clauses =  new JSONArray();
			for(Object clause : records){
				JSONObject cls = (new JSONObject(clause.toString())).getJSONArray("clauses").getJSONObject(0);
				clauses.put(cls);
			}
			clauseDTOs = JSONUtils.parseAsList(clauses.toString(), ClauseDTO.class);
		} catch (JSONException e) {
			LOG.error("Failed to create clause : ", e);
			return null;
		} catch (Exception e) {
			LOG.error("Caught exception while creating clause: ", e);
			return null;
		}

		return clauseDTOs;
	}

}
