/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.InwardCollectionsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.dto.InwardCollectionsDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum.*;
import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.*;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.*;

public class InwardCollectionsBackendDelegateImpl implements InwardCollectionsBackendDelegate {

    private static final Logger LOG = LogManager.getLogger(InwardCollectionsBackendDelegateImpl.class);

    @Override
    public InwardCollectionsDTO createInwardCollection(InwardCollectionsDTO inputDTO, DataControllerRequest request) {
        JSONObject reqBody = constructSRMSParams(inputDTO);
        String requestBody = reqBody.toString().replaceAll("\"", "'");

        Map<String, Object> inputMap = getTypeAndSubType("InwardCollectionsType", "InwardCollectionsSubType", true);
        inputMap.put("requestBody", requestBody);

        try {
            String createOrder = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();
            JSONObject responseObject = new JSONObject(createOrder);
            inputDTO.setCollectionSrmsId(responseObject.getString(PARAM_ORDER_ID));
            inputDTO.setTransactionReference(responseObject.getString(PARAM_ORDER_ID));
        } catch (Exception e) {
            LOG.error("Unable to create inward collection request order " + e);
            inputDTO = new InwardCollectionsDTO();
            inputDTO.setDbpErrCode(ERRTF_29091.getErrorCodeAsString());
            inputDTO.setDbpErrMsg(ERRTF_29091.getErrorMessage());
            inputDTO.setErrorMsg(e.getMessage());
        }

        return inputDTO;
    }

    @Override
    public List<InwardCollectionsDTO> getInwardCollections(DataControllerRequest request) {
        List<InwardCollectionsDTO> collectionsList = new ArrayList<>();
        String customerId = fetchCustomerFromSession(request);
        Map<String, Object> inputMap = getTypeAndSubType("InwardCollectionsType", "InwardCollectionsSubType", false);

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
                    InwardCollectionsDTO collectionDTO;
                    try {
                        collectionDTO = JSONUtils.parse(inputPayload.toString(), InwardCollectionsDTO.class);
                        collectionDTO.setCollectionSrmsId((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
                        collectionsList.add(collectionDTO);
                    } catch (Exception e) {
                        LOG.error("Exception occurred while fetching records: ", e);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to get inward collection records " + e);
            return null;
        }
        return collectionsList;
    }

    @Override
    public InwardCollectionsDTO getInwardCollectionById(String collectionSrmsId, DataControllerRequest request) {
        InwardCollectionsDTO collectionDTO = new InwardCollectionsDTO();
        Map<String, Object> inputMap = getTypeAndSubType("InwardCollectionsType", "InwardCollectionsSubType", false);
        inputMap.put("serviceRequestIds", collectionSrmsId);

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
            collectionDTO = JSONUtils.parse(inputPayload.toString(), InwardCollectionsDTO.class);
            collectionDTO.setCollectionSrmsId((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
        } catch (SecurityException e) {
            LOG.error("SECURITY EXCEPTION - UNAUTHORIZED ACCESS " + e);
            collectionDTO.setDbpErrCode(ERRTF_29070.getErrorCodeAsString());
            collectionDTO.setDbpErrMsg(ERRTF_29070.getErrorMessage());
            collectionDTO.setErrorMsg(e.getMessage());
        } catch (Exception e) {
            LOG.error("Unable to fetch the record " + e);
            collectionDTO.setDbpErrCode(ERRTF_29071.getErrorCodeAsString());
            collectionDTO.setDbpErrMsg(ERRTF_29071.getErrorMessage());
            collectionDTO.setErrorMsg(e.getMessage());
        }
        return collectionDTO;
    }

    @Override
    public InwardCollectionsDTO updateInwardCollection(InwardCollectionsDTO collectionDetails, DataControllerRequest request) {
        JSONObject reqBody = constructSRMSParams(collectionDetails);
        String requestBody = reqBody.toString().replaceAll("\"", "'");

        Map<String, Object> inputMap = getTypeAndSubType("InwardCollectionsType", "InwardCollectionsSubType", false);
        inputMap.put("serviceRequestId", collectionDetails.getCollectionSrmsId());
        inputMap.put("requestBody", requestBody);

        try {
            String updateOrder = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();
            JSONObject responseObject = new JSONObject(updateOrder);
            String orderId = responseObject.getString(PARAM_ORDER_ID);
            LOG.info("Update Inward Collections Response: ", responseObject);
        } catch (Exception e) {
            LOG.error("Unable to update inward collection request order " + e);
            collectionDetails = new InwardCollectionsDTO();
            collectionDetails.setDbpErrCode(ERRTF_29092.getErrorCodeAsString());
            collectionDetails.setDbpErrMsg(ERRTF_29092.getErrorMessage());
            collectionDetails.setErrorMsg(e.getMessage());
        }

        return collectionDetails;
    }

    private JSONObject constructSRMSParams(InwardCollectionsDTO inputDTO) {
        JSONObject reqBody = new JSONObject();
        if (StringUtils.isNotBlank(inputDTO.getAmendmentDetails()))
            reqBody.put("amendmentDetails", inputDTO.getAmendmentDetails().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getAmount()))
            reqBody.put("amount", inputDTO.getAmount());
        if (StringUtils.isNotBlank(inputDTO.getBillExchangeStatus()))
            reqBody.put("billExchangeStatus", inputDTO.getBillExchangeStatus());
        if (StringUtils.isNotBlank(inputDTO.getCharges()))
            reqBody.put("charges", inputDTO.getCharges());
        if (StringUtils.isNotBlank(inputDTO.getChargesDebitFrom()))
            reqBody.put("chargesDebitFrom", inputDTO.getChargesDebitFrom());
        if (StringUtils.isNotBlank(inputDTO.getCreatedDate()))
            reqBody.put("createdDate", inputDTO.getCreatedDate());
        if (StringUtils.isNotBlank(inputDTO.getCurrency()))
            reqBody.put("currency", inputDTO.getCurrency());
        if (StringUtils.isNotBlank(inputDTO.getDebitAmountFrom()))
            reqBody.put("debitAmountFrom", inputDTO.getDebitAmountFrom());
        if (StringUtils.isNotBlank(inputDTO.getDocumentNo()))
            reqBody.put("documentNo", inputDTO.getDocumentNo());
        if (StringUtils.isNotBlank(inputDTO.getDocumentsUploaded()))
            reqBody.put("documentsUploaded", inputDTO.getDocumentsUploaded().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getDraweeAcknowledgement()))
            reqBody.put("draweeAcknowledgement", inputDTO.getDraweeAcknowledgement());
        if (StringUtils.isNotBlank(inputDTO.getDraweeAcknowledgementDate()))
            reqBody.put("draweeAcknowledgementDate", inputDTO.getDraweeAcknowledgementDate());
        if (StringUtils.isNotBlank(inputDTO.getDrawerName()))
            reqBody.put("drawerName", inputDTO.getDrawerName());
        if (StringUtils.isNotBlank(inputDTO.getIncoTerms()))
            reqBody.put("incoTerms", inputDTO.getIncoTerms());
        if (StringUtils.isNotBlank(inputDTO.getLastUpdatedDate()))
            reqBody.put("lastUpdatedDate", inputDTO.getLastUpdatedDate());
        if (StringUtils.isNotBlank(inputDTO.getMaturityDate()))
            reqBody.put("maturityDate", inputDTO.getMaturityDate());
        if (StringUtils.isNotBlank(inputDTO.getMessageFromBank()))
            reqBody.put("messageFromBank", inputDTO.getMessageFromBank());
        if (StringUtils.isNotBlank(inputDTO.getMessageToBank()))
            reqBody.put("messageToBank", inputDTO.getMessageToBank());
        if (StringUtils.isNotBlank(inputDTO.getPaymentStatus()))
            reqBody.put("paymentStatus", inputDTO.getPaymentStatus());
        if (StringUtils.isNotBlank(inputDTO.getReasonForRejection()))
            reqBody.put("reasonForRejection", inputDTO.getReasonForRejection());
        if (StringUtils.isNotBlank(inputDTO.getReasonForReturn()))
            reqBody.put("reasonForReturn", inputDTO.getReasonForReturn());
        if (StringUtils.isNotBlank(inputDTO.getReceivedOn()))
            reqBody.put("receivedOn", inputDTO.getReceivedOn());
        if (StringUtils.isNotBlank(inputDTO.getRemittingBank()))
            reqBody.put("remittingBank", inputDTO.getRemittingBank());
        if (StringUtils.isNotBlank(inputDTO.getSettledDate()))
            reqBody.put("settledDate", inputDTO.getSettledDate());
        if (StringUtils.isNotBlank(inputDTO.getStatus()))
            reqBody.put("status", inputDTO.getStatus());
        if (StringUtils.isNotBlank(inputDTO.getTenorType()))
            reqBody.put("tenorType", inputDTO.getTenorType());
        if (StringUtils.isNotBlank(inputDTO.getTransactionReference()))
            reqBody.put("transactionReference", inputDTO.getTransactionReference());
        if (StringUtils.isNotBlank(inputDTO.getUsanceAcceptance()))
            reqBody.put("usanceAcceptance", inputDTO.getUsanceAcceptance());
        if (StringUtils.isNotBlank(inputDTO.getUsanceAcceptanceDate()))
            reqBody.put("usanceAcceptanceDate", inputDTO.getUsanceAcceptanceDate());
        if (StringUtils.isNotBlank(inputDTO.getUsanceAcceptanceEligibility()))
            reqBody.put("usanceAcceptanceEligibility", inputDTO.getUsanceAcceptanceEligibility());
        if (StringUtils.isNotBlank(inputDTO.getUsanceDetails()))
            reqBody.put("usanceDetails", inputDTO.getUsanceDetails());
        if (StringUtils.isNotBlank(inputDTO.getRejectedDate()))
            reqBody.put("rejectedDate", inputDTO.getRejectedDate());
        if (StringUtils.isNotBlank(inputDTO.getDocumentsAgainstAcceptance()))
            reqBody.put("documentsAgainstAcceptance", inputDTO.getDocumentsAgainstAcceptance());

        return reqBody;
    }
}
