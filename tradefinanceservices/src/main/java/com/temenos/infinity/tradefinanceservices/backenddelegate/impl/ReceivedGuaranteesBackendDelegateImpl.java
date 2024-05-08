/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.ReceivedGuaranteesBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.dto.ReceivedGuaranteesDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum.*;
import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.PARAM_ORDER_ID;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.*;

public class ReceivedGuaranteesBackendDelegateImpl implements ReceivedGuaranteesBackendDelegate {

    private static final Logger LOG = LogManager.getLogger(ReceivedGuaranteesBackendDelegateImpl.class);

    @Override
    public ReceivedGuaranteesDTO createReceivedGuarantee(ReceivedGuaranteesDTO inputDTO, DataControllerRequest request) {
        JSONObject reqBody = constructSRMSParams(inputDTO);
        String requestBody = reqBody.toString().replaceAll("\"", "'");

        Map<String, Object> inputMap = getTypeAndSubType("ReceivedGuaranteesType", "ReceivedGuaranteesSubType", true);
        inputMap.put("requestBody", requestBody);

        try {
            String createOrder = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();
            JSONObject responseObject = new JSONObject(createOrder);
            inputDTO.setGuaranteeSrmsId(responseObject.getString(PARAM_ORDER_ID));
            inputDTO.setTransactionReference(responseObject.getString(PARAM_ORDER_ID));
        } catch (Exception e) {
            LOG.error("Unable to create guarantee request order " + e);
            inputDTO = new ReceivedGuaranteesDTO();
            inputDTO.setDbpErrCode(ERRTF_29091.getErrorCodeAsString());
            inputDTO.setDbpErrMsg(ERRTF_29091.getErrorMessage());
            inputDTO.setErrorMsg(e.getMessage());
        }

        return inputDTO;
    }

    @Override
    public List<ReceivedGuaranteesDTO> getReceivedGuarantees(DataControllerRequest request) {
        List<ReceivedGuaranteesDTO> guaranteesList = new ArrayList<>();
        String customerId = fetchCustomerFromSession(request);
        Map<String, Object> inputMap = getTypeAndSubType("ReceivedGuaranteesType", "ReceivedGuaranteesSubType", false);

        try {
            String response = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();

            JSONObject responseObj = new JSONObject(response);
            JSONArray Orders = responseObj.getJSONArray(PARAM_SERVICE_REQUESTS);
            for (int i = 0; i < Orders.length(); i++) {
                JSONObject singleOrder = Orders.getJSONObject(i);
                if (StringUtils.equals(customerId, singleOrder.getString(PARAM_PARTY_ID)) && singleOrder.has(PARAM_INPUT_PAYLOAD)) {
                    JSONObject inputPayload = singleOrder.getJSONObject(PARAM_INPUT_PAYLOAD);
                    ReceivedGuaranteesDTO guaranteeDTO;
                    try {
                        guaranteeDTO = JSONUtils.parse(inputPayload.toString(), ReceivedGuaranteesDTO.class);
                        guaranteeDTO.setGuaranteeSrmsId((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
                        guaranteeDTO.setTransactionReference((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
                        guaranteeDTO.setAmountWithCurrency(getAmountWithCurrency(guaranteeDTO.getCurrency(), guaranteeDTO.getAmount(), false));
                        guaranteesList.add(guaranteeDTO);
                    } catch (IOException e) {
                        LOG.error("Exception occurred while fetching records: ", e);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to get export letter of credits requests " + e);
            return null;
        }
        return guaranteesList;
    }

    @Override
    public ReceivedGuaranteesDTO getReceivedGuaranteeById(String guaranteeSrmsId, DataControllerRequest request) {
        ReceivedGuaranteesDTO guaranteeDTO = new ReceivedGuaranteesDTO();
        Map<String, Object> inputMap = getTypeAndSubType("ReceivedGuaranteesType", "ReceivedGuaranteesSubType", false);
        inputMap.put("serviceRequestIds", guaranteeSrmsId);

        try {
            String response = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();

            JSONObject responseObj = new JSONObject(response);
            JSONObject singleOrder = responseObj.getJSONArray(PARAM_SERVICE_REQUESTS).getJSONObject(0);
            if (!StringUtils.equals(singleOrder.getString(PARAM_PARTY_ID), fetchCustomerFromSession(request))) {
                throw new SecurityException("Unauthorized Access to record");
            }
            JSONObject inputPayload = singleOrder.getJSONObject(PARAM_INPUT_PAYLOAD);
            guaranteeDTO = JSONUtils.parse(inputPayload.toString(), ReceivedGuaranteesDTO.class);
            guaranteeDTO.setGuaranteeSrmsId((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
            guaranteeDTO.setTransactionReference((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
        } catch (SecurityException e) {
            LOG.error("SECURITY EXCEPTION - UNAUTHORIZED ACCESS " + e);
            guaranteeDTO.setDbpErrCode(ERRTF_29070.getErrorCodeAsString());
            guaranteeDTO.setDbpErrMsg(ERRTF_29070.getErrorMessage());
            guaranteeDTO.setErrorMsg(e.getMessage());
        } catch (Exception e) {
            LOG.error("Unable to fetch the record " + e);
            guaranteeDTO.setDbpErrCode(ERRTF_29071.getErrorCodeAsString());
            guaranteeDTO.setDbpErrMsg(ERRTF_29071.getErrorMessage());
            guaranteeDTO.setErrorMsg(e.getMessage());
        }
        return guaranteeDTO;
    }

    @Override
    public ReceivedGuaranteesDTO updateReceivedGuarantee(ReceivedGuaranteesDTO guaranteeDetails, DataControllerRequest request) {
        JSONObject reqBody = constructSRMSParams(guaranteeDetails);
        String requestBody = reqBody.toString().replaceAll("\"", "'");

        Map<String, Object> inputMap = getTypeAndSubType("ReceivedGuaranteesType", "ReceivedGuaranteesSubType", false);
        inputMap.put("serviceRequestId", guaranteeDetails.getGuaranteeSrmsId());
        inputMap.put("requestBody", requestBody);

        try {
            String updateOrder = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();
            JSONObject responseObject = new JSONObject(updateOrder);
            String orderId = responseObject.getString(PARAM_ORDER_ID);
            LOG.info("Update Received Guarantee Response: ", responseObject);
        } catch (Exception e) {
            LOG.error("Unable to update guarantee request order " + e);
            guaranteeDetails = new ReceivedGuaranteesDTO();
            guaranteeDetails.setDbpErrCode(ERRTF_29092.getErrorCodeAsString());
            guaranteeDetails.setDbpErrMsg(ERRTF_29092.getErrorMessage());
            guaranteeDetails.setErrorMsg(e.getMessage());
        }

        return guaranteeDetails;
    }

    private JSONObject constructSRMSParams(ReceivedGuaranteesDTO inputDTO) {
        JSONObject reqBody = new JSONObject();
        if (StringUtils.isNotBlank(inputDTO.getGuaranteeSrmsId()))
            reqBody.put("guaranteeSrmsId", inputDTO.getGuaranteeSrmsId());
        if (StringUtils.isNotBlank(inputDTO.getTransactionReference()))
            reqBody.put("transactionReference", inputDTO.getTransactionReference());
        if (StringUtils.isNotBlank(inputDTO.getReceivedOn()))
            reqBody.put("receivedOn", inputDTO.getReceivedOn());
        if (StringUtils.isNotBlank(inputDTO.getStatus()))
            reqBody.put("status", inputDTO.getStatus());
        if (StringUtils.isNotBlank(inputDTO.getProductType()))
            reqBody.put("productType", inputDTO.getProductType());
        if (StringUtils.isNotBlank(inputDTO.getLcType()))
            reqBody.put("lcType", inputDTO.getLcType());
        if (StringUtils.isNotBlank(inputDTO.getRelatedTransactionReference()))
            reqBody.put("relatedTransactionReference", inputDTO.getRelatedTransactionReference());
        if (StringUtils.isNotBlank(inputDTO.getModeOfTransaction()))
            reqBody.put("modeOfTransaction", inputDTO.getModeOfTransaction());
        if (StringUtils.isNotBlank(inputDTO.getApplicantParty()))
            reqBody.put("applicantParty", inputDTO.getApplicantParty());
        if (StringUtils.isNotBlank(inputDTO.getBeneficiaryDetails()))
            reqBody.put("beneficiaryDetails", inputDTO.getBeneficiaryDetails().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getAmount()))
            reqBody.put("amount", inputDTO.getAmount());
        if (StringUtils.isNotBlank(inputDTO.getCurrency()))
            reqBody.put("currency", inputDTO.getCurrency());
        if (StringUtils.isNotBlank(inputDTO.getExpiryType()))
            reqBody.put("expiryType", inputDTO.getExpiryType());
        if (StringUtils.isNotBlank(inputDTO.getExpiryDate()))
            reqBody.put("expiryDate", inputDTO.getExpiryDate());
        if (StringUtils.isNotBlank(inputDTO.getExpiryConditions()))
            reqBody.put("expiryConditions", inputDTO.getExpiryConditions());
        if (StringUtils.isNotBlank(inputDTO.getExpectedIssueDate()))
            reqBody.put("expectedIssueDate", inputDTO.getExpectedIssueDate());
        if (StringUtils.isNotBlank(inputDTO.getAutoExtensionExpiry()))
            reqBody.put("autoExtensionExpiry", inputDTO.getAutoExtensionExpiry());
        if (StringUtils.isNotBlank(inputDTO.getExtensionPeriod()))
            reqBody.put("extensionPeriod", inputDTO.getExtensionPeriod());
        if (StringUtils.isNotBlank(inputDTO.getExtensionCapPeriod()))
            reqBody.put("extensionCapPeriod", inputDTO.getExtensionCapPeriod());
        if (StringUtils.isNotBlank(inputDTO.getNotificationPeriod()))
            reqBody.put("notificationPeriod", inputDTO.getNotificationPeriod());
        if (StringUtils.isNotBlank(inputDTO.getExtensionDetails()))
            reqBody.put("extensionDetails", inputDTO.getExtensionDetails());
        if (StringUtils.isNotBlank(inputDTO.getApplicableRules()))
            reqBody.put("applicableRules", inputDTO.getApplicableRules());
        if (StringUtils.isNotBlank(inputDTO.getGoverningLaw()))
            reqBody.put("governingLaw", inputDTO.getGoverningLaw());
        if (StringUtils.isNotBlank(inputDTO.getApplicableRules()))
            reqBody.put("applicableRules", inputDTO.getApplicableRules());
        if (StringUtils.isNotBlank(inputDTO.getDeliveryInstructions()))
            reqBody.put("deliveryInstructions", inputDTO.getDeliveryInstructions());
        if (StringUtils.isNotBlank(inputDTO.getOtherInstructions()))
            reqBody.put("otherInstructions", inputDTO.getOtherInstructions());
        if (StringUtils.isNotBlank(inputDTO.getApplicantName()))
            reqBody.put("applicantName", inputDTO.getApplicantName());
        if (StringUtils.isNotBlank(inputDTO.getApplicantAddress()))
            reqBody.put("applicantAddress", inputDTO.getApplicantAddress().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getIssuingBankName()))
            reqBody.put("issuingBankName", inputDTO.getIssuingBankName());
        if (StringUtils.isNotBlank(inputDTO.getIssuingBankSwiftBicCode()))
            reqBody.put("issuingBankSwiftBicCode", inputDTO.getIssuingBankSwiftBicCode());
        if (StringUtils.isNotBlank(inputDTO.getIssuingBankIban()))
            reqBody.put("issuingBankIban", inputDTO.getIssuingBankIban());
        if (StringUtils.isNotBlank(inputDTO.getIssuingBankLocalCode()))
            reqBody.put("issuingBankLocalCode", inputDTO.getIssuingBankLocalCode());
        if (StringUtils.isNotBlank(inputDTO.getIssuingBankAddress()))
            reqBody.put("issuingBankAddress", inputDTO.getIssuingBankAddress().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getMessageFromBank()))
            reqBody.put("messageFromBank", inputDTO.getMessageFromBank());
        if (StringUtils.isNotBlank(inputDTO.getUploadedDocuments()))
            reqBody.put("uploadedDocuments", inputDTO.getUploadedDocuments().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getSelfAcceptance()))
            reqBody.put("selfAcceptance", inputDTO.getSelfAcceptance());
        if (StringUtils.isNotBlank(inputDTO.getSelfAcceptanceDate()))
            reqBody.put("selfAcceptanceDate", inputDTO.getSelfAcceptanceDate());
        if (StringUtils.isNotBlank(inputDTO.getSelfRejectionHistory()))
            reqBody.put("selfRejectionHistory", inputDTO.getSelfRejectionHistory().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getClausesAndConditions()))
            reqBody.put("clausesAndConditions", inputDTO.getClausesAndConditions().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getLastAmendmentDetails()))
            reqBody.put("lastAmendmentDetails", inputDTO.getLastAmendmentDetails().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getClaimInformation()))
            reqBody.put("claimInformation", inputDTO.getClaimInformation().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getUtilizedAmount()))
            reqBody.put("utilizedAmount", inputDTO.getUtilizedAmount());
        if (StringUtils.isNotBlank(inputDTO.getReleasedAmount()))
            reqBody.put("releasedAmount", inputDTO.getReleasedAmount());
        if (StringUtils.isNotBlank(inputDTO.getLiabilityDetails()))
            reqBody.put("liabilityDetails", inputDTO.getLiabilityDetails().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getDemandAcceptance()))
            reqBody.put("demandAcceptance", inputDTO.getDemandAcceptance());
        reqBody.put("reasonForSelfRejection", inputDTO.getReasonForSelfRejection());
        reqBody.put("messageToBank", inputDTO.getMessageToBank());
        return reqBody;
    }
}