/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.ReceivedGuaranteeAmendmentsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.dto.ReceivedAmendmentsDTO;
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
import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.*;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.*;

public class ReceivedGuaranteeAmendmentsBackendDelegateImpl implements ReceivedGuaranteeAmendmentsBackendDelegate {

    private static final Logger LOG = LogManager.getLogger(ReceivedGuaranteesBackendDelegateImpl.class);

    @Override
    public ReceivedAmendmentsDTO createReceivedAmendment(ReceivedAmendmentsDTO inputDTO, DataControllerRequest request) {
        JSONObject reqBody = constructSRMSParams(inputDTO);
        String requestBody = reqBody.toString().replaceAll("\"", "'");

        Map<String, Object> inputMap = getTypeAndSubType("ReceivedGuaranteeAmendmentsType", "ReceivedGuaranteeAmendmentsSubType", true);
        inputMap.put("requestBody", requestBody);

        try {
            String createOrder = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();
            JSONObject responseObject = new JSONObject(createOrder);
            inputDTO.setAmendmentSrmsId(responseObject.getString(PARAM_ORDER_ID));
        } catch (Exception e) {
            LOG.error("Unable to create guarantee request order " + e);
            inputDTO = new ReceivedAmendmentsDTO();
            inputDTO.setDbpErrCode(ERRTF_29091.getErrorCodeAsString());
            inputDTO.setDbpErrMsg(ERRTF_29091.getErrorMessage());
            inputDTO.setErrorMsg(e.getMessage());
        }

        return inputDTO;
    }

    @Override
    public ReceivedAmendmentsDTO updateReceivedAmendment(ReceivedAmendmentsDTO amendmentDetails, DataControllerRequest request) {
        JSONObject reqBody = constructSRMSParams(amendmentDetails);
        String requestBody = reqBody.toString().replaceAll("\"", "'");

        Map<String, Object> inputMap = getTypeAndSubType("ReceivedGuaranteeAmendmentsType", "ReceivedGuaranteeAmendmentsSubType", false);
        inputMap.put("serviceRequestId", amendmentDetails.getAmendmentSrmsId());
        inputMap.put("requestBody", requestBody);

        try {
            String updateOrder = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();
            JSONObject responseObject = new JSONObject(updateOrder);
            responseObject.getString(PARAM_ORDER_ID);
            LOG.info("Update Received Guarantee Response: ", responseObject);
        } catch (Exception e) {
            LOG.error("Unable to update guarantee request order " + e);
            amendmentDetails = new ReceivedAmendmentsDTO();
            amendmentDetails.setDbpErrCode(ERRTF_29092.getErrorCodeAsString());
            amendmentDetails.setDbpErrMsg(ERRTF_29092.getErrorMessage());
            amendmentDetails.setErrorMsg(e.getMessage());
        }

        return amendmentDetails;
    }

    @Override
    public List<ReceivedAmendmentsDTO> getReceivedAmendments(DataControllerRequest request) {
        List<ReceivedAmendmentsDTO> amendmentsList = new ArrayList<>();
        String customerId = fetchCustomerFromSession(request);
        Map<String, Object> inputMap = getTypeAndSubType("ReceivedGuaranteeAmendmentsType", "ReceivedGuaranteeAmendmentsSubType", false);

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
                    ReceivedAmendmentsDTO amendmentsDTO;
                    try {
                        amendmentsDTO = JSONUtils.parse(inputPayload.toString(), ReceivedAmendmentsDTO.class);
                        amendmentsDTO.setAmendmentSrmsId((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
                        if (amendmentsDTO.getCurrency() != null && amendmentsDTO.getAmount() != null)
                            amendmentsDTO.setAmountWithCurrency(getAmountWithCurrency(amendmentsDTO.getCurrency(), amendmentsDTO.getAmount(), false));
                        else
                            amendmentsDTO.setAmountWithCurrency("N/A");
                        amendmentsList.add(amendmentsDTO);
                    } catch (IOException e) {
                        LOG.error("Exception occurred while fetching records: ", e);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to get export letter of credits requests " + e);
            return null;
        }

        return amendmentsList;
    }

    @Override
    public ReceivedAmendmentsDTO getReceivedAmendmentById(String amendmentSrmsId, DataControllerRequest request) {
        ReceivedAmendmentsDTO amendmentsDTO = new ReceivedAmendmentsDTO();
        Map<String, Object> inputMap = getTypeAndSubType("ReceivedGuaranteeAmendmentsType", "ReceivedGuaranteeAmendmentsSubType", false);
        inputMap.put("serviceRequestIds", amendmentSrmsId);

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
            amendmentsDTO = JSONUtils.parse(inputPayload.toString(), ReceivedAmendmentsDTO.class);
            amendmentsDTO.setAmendmentSrmsId((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
        } catch (SecurityException e) {
            LOG.error("SECURITY EXCEPTION - UNAUTHORIZED ACCESS " + e);
            amendmentsDTO.setDbpErrCode(ERRTF_29070.getErrorCodeAsString());
            amendmentsDTO.setDbpErrMsg(ERRTF_29070.getErrorMessage());
            amendmentsDTO.setErrorMsg(e.getMessage());
        } catch (Exception e) {
            LOG.error("Unable to fetch the record " + e);
            amendmentsDTO.setDbpErrCode(ERRTF_29071.getErrorCodeAsString());
            amendmentsDTO.setDbpErrMsg(ERRTF_29071.getErrorMessage());
            amendmentsDTO.setErrorMsg(e.getMessage());
        }
        return amendmentsDTO;
    }

    private JSONObject constructSRMSParams(ReceivedAmendmentsDTO inputDTO) {
        JSONObject reqBody = new JSONObject();
        if (StringUtils.isNotBlank(inputDTO.getAmendmentSrmsId()))
            reqBody.put("amendmentSrmsId", inputDTO.getAmendmentSrmsId());
        if (StringUtils.isNotBlank(inputDTO.getGuaranteeSrmsId()))
            reqBody.put("guaranteeSrmsId", inputDTO.getGuaranteeSrmsId());
        if (StringUtils.isNotBlank(inputDTO.getStatus()))
            reqBody.put("status", inputDTO.getStatus());
        if (StringUtils.isNotBlank(inputDTO.getAmendmentNo()))
            reqBody.put("amendmentNo", inputDTO.getAmendmentNo());
        if (StringUtils.isNotBlank(inputDTO.getReceivedOn()))
            reqBody.put("receivedOn", inputDTO.getReceivedOn());
        if (StringUtils.isNotBlank(inputDTO.getApplicant()))
            reqBody.put("applicant", inputDTO.getApplicant());
        if (StringUtils.isNotBlank(inputDTO.getProductType()))
            reqBody.put("productType", inputDTO.getProductType());
        if (StringUtils.isNotBlank(inputDTO.getLcType()))
            reqBody.put("lcType", inputDTO.getLcType());
        if (StringUtils.isNotBlank(inputDTO.getAmount()))
            reqBody.put("amount", inputDTO.getAmount());
        if (StringUtils.isNotBlank(inputDTO.getCurrency()))
            reqBody.put("currency", inputDTO.getCurrency());
        if (StringUtils.isNotBlank(inputDTO.getExpiryType()))
            reqBody.put("expiryType", inputDTO.getExpiryType());
        if (StringUtils.isNotBlank(inputDTO.getAmendmentCharges()))
            reqBody.put("amendmentCharges", inputDTO.getAmendmentCharges());
        if (StringUtils.isNotBlank(inputDTO.getDateOfAmountChange()))
            reqBody.put("dateOfAmountChange", inputDTO.getDateOfAmountChange());
        if (StringUtils.isNotBlank(inputDTO.getAmendAmount()))
            reqBody.put("amendAmount", inputDTO.getAmendAmount());
        if (StringUtils.isNotBlank(inputDTO.getAmendExpiryType()))
            reqBody.put("amendExpiryType", inputDTO.getAmendExpiryType());
        if (StringUtils.isNotBlank(inputDTO.getAmendExpiryDate()))
            reqBody.put("amendExpiryDate", inputDTO.getAmendExpiryDate());
        if (StringUtils.isNotBlank(inputDTO.getAmendExpiryConditions()))
            reqBody.put("amendExpiryConditions", inputDTO.getAmendExpiryConditions());
        if (StringUtils.isNotBlank(inputDTO.getBeneficiaryDetails()))
            reqBody.put("beneficiaryDetails", inputDTO.getBeneficiaryDetails().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getOtherAmendments()))
            reqBody.put("otherAmendments", inputDTO.getOtherAmendments().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getOtherInstructions()))
            reqBody.put("otherInstructions", inputDTO.getOtherInstructions());
        if (StringUtils.isNotBlank(inputDTO.getMessageFromBank()))
            reqBody.put("messageFromBank", inputDTO.getMessageFromBank());
        if (StringUtils.isNotBlank(inputDTO.getSupportingDocuments()))
            reqBody.put("supportingDocuments", inputDTO.getSupportingDocuments().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getSelfAcceptance()))
            reqBody.put("selfAcceptance", inputDTO.getSelfAcceptance());
        if (StringUtils.isNotBlank(inputDTO.getSelfAcceptanceDate()))
            reqBody.put("selfAcceptanceDate", inputDTO.getSelfAcceptanceDate());
        if (StringUtils.isNotBlank(inputDTO.getReasonForSelfRejection()))
            reqBody.put("reasonForSelfRejection", inputDTO.getReasonForSelfRejection());
        if (StringUtils.isNotBlank(inputDTO.getMessageToBank()))
            reqBody.put("messageToBank", inputDTO.getMessageToBank());
        if (StringUtils.isNotBlank(inputDTO.getLastUpdatedTimeStamp()))
            reqBody.put("lastUpdatedTimeStamp", inputDTO.getLastUpdatedTimeStamp());
        reqBody.put("reasonForReturn", inputDTO.getReasonForReturn());

        return reqBody;
    }
}
