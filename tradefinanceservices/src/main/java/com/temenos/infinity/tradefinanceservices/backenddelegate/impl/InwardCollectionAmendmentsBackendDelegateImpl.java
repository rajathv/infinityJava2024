/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.InwardCollectionAmendmentsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.dto.InwardCollectionAmendmentsDTO;
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
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.PARAM_ORDER_ID;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.*;

public class InwardCollectionAmendmentsBackendDelegateImpl implements InwardCollectionAmendmentsBackendDelegate {

    private static final Logger LOG = LogManager.getLogger(InwardCollectionAmendmentsBackendDelegateImpl.class);

    @Override
    public InwardCollectionAmendmentsDTO createInwardCollectionAmendment(InwardCollectionAmendmentsDTO inputDTO, DataControllerRequest request) {
        JSONObject reqBody = constructSRMSParams(inputDTO);
        String requestBody = reqBody.toString().replaceAll("\"", "'");

        Map<String, Object> inputMap = getTypeAndSubType("InwardCollectionAmendmentsType", "InwardCollectionAmendmentsSubType", true);
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
            LOG.error("Unable to create amendment request order " + e);
            inputDTO = new InwardCollectionAmendmentsDTO();
            inputDTO.setDbpErrCode(ERRTF_29091.getErrorCodeAsString());
            inputDTO.setDbpErrMsg(ERRTF_29091.getErrorMessage());
            inputDTO.setErrorMsg(e.getMessage());
        }

        return inputDTO;
    }


    @Override
    public List<InwardCollectionAmendmentsDTO> getInwardCollectionAmendments(DataControllerRequest request) {
        List<InwardCollectionAmendmentsDTO> amendmentsList = new ArrayList<>();
        String customerId = fetchCustomerFromSession(request);
        Map<String, Object> inputMap = getTypeAndSubType("InwardCollectionAmendmentsType", "InwardCollectionAmendmentsSubType", false);

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
                    InwardCollectionAmendmentsDTO amendmentDTO;
                    try {
                        amendmentDTO = JSONUtils.parse(inputPayload.toString(), InwardCollectionAmendmentsDTO.class);
                        amendmentDTO.setAmendmentSrmsId((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
                        amendmentDTO.setLastUpdatedDate(singleOrder.getString(PARAM_ORDER_PROCESSED_TIME));
                        amendmentsList.add(amendmentDTO);
                    } catch (Exception e) {
                        LOG.error("Exception occurred while fetching records: ", e);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to get records requests " + e);
            return null;
        }
        return amendmentsList;
    }

    @Override
    public InwardCollectionAmendmentsDTO getInwardCollectionAmendmentById(String amendmentSrmsId, DataControllerRequest request) {
        InwardCollectionAmendmentsDTO amendmentDTO = new InwardCollectionAmendmentsDTO();
        Map<String, Object> inputMap = getTypeAndSubType("InwardCollectionAmendmentsType", "InwardCollectionAmendmentsSubType", false);
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
            amendmentDTO = JSONUtils.parse(inputPayload.toString(), InwardCollectionAmendmentsDTO.class);
            amendmentDTO.setLastUpdatedDate(singleOrder.getString(PARAM_ORDER_PROCESSED_TIME));
            amendmentDTO.setAmendmentSrmsId((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
        } catch (SecurityException e) {
            LOG.error("SECURITY EXCEPTION - UNAUTHORIZED ACCESS " + e);
            amendmentDTO.setDbpErrCode(ERRTF_29070.getErrorCodeAsString());
            amendmentDTO.setDbpErrMsg(ERRTF_29070.getErrorMessage());
            amendmentDTO.setErrorMsg(e.getMessage());
        } catch (Exception e) {
            LOG.error("Unable to fetch the record " + e);
            amendmentDTO.setDbpErrCode(ERRTF_29071.getErrorCodeAsString());
            amendmentDTO.setDbpErrMsg(ERRTF_29071.getErrorMessage());
            amendmentDTO.setErrorMsg(e.getMessage());
        }
        return amendmentDTO;
    }

    @Override
    public InwardCollectionAmendmentsDTO updateInwardCollectionAmendment(InwardCollectionAmendmentsDTO amendmentDetails, DataControllerRequest request) {
        JSONObject reqBody = constructSRMSParams(amendmentDetails);
        String requestBody = reqBody.toString().replaceAll("\"", "'");

        Map<String, Object> inputMap = getTypeAndSubType("InwardCollectionAmendmentsType", "InwardCollectionAmendmentsSubType", false);
        inputMap.put("serviceRequestId", amendmentDetails.getAmendmentSrmsId());
        inputMap.put("requestBody", requestBody);

        try {
            String updateOrder = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();
            JSONObject responseObject = new JSONObject(updateOrder);
            String orderId = responseObject.getString(PARAM_ORDER_ID);
            LOG.info("Update Inward Collection Amendment Response: ", responseObject);
        } catch (Exception e) {
            LOG.error("Unable to update guarantee request order " + e);
            amendmentDetails = new InwardCollectionAmendmentsDTO();
            amendmentDetails.setDbpErrCode(ERRTF_29092.getErrorCodeAsString());
            amendmentDetails.setDbpErrMsg(ERRTF_29092.getErrorMessage());
            amendmentDetails.setErrorMsg(e.getMessage());
        }

        return amendmentDetails;
    }

    private JSONObject constructSRMSParams(InwardCollectionAmendmentsDTO inputDTO) {
        JSONObject reqBody = new JSONObject();

        if (StringUtils.isNotBlank(inputDTO.getAmendAmount()))
            reqBody.put("amendAmount", inputDTO.getAmendAmount());
        if (StringUtils.isNotBlank(inputDTO.getAmendDocuments()))
            reqBody.put("amendDocuments", inputDTO.getAmendDocuments().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getAmendMaturityDate()))
            reqBody.put("amendMaturityDate", inputDTO.getAmendMaturityDate());
        if (StringUtils.isNotBlank(inputDTO.getAmendRemittingBank()))
            reqBody.put("amendRemittingBank", inputDTO.getAmendRemittingBank());
        if (StringUtils.isNotBlank(inputDTO.getAmendTenorType()))
            reqBody.put("amendTenorType", inputDTO.getAmendTenorType());
        if (StringUtils.isNotBlank(inputDTO.getAmendUsanceDetails()))
            reqBody.put("amendUsanceDetails", inputDTO.getAmendUsanceDetails());
        if (StringUtils.isNotBlank(inputDTO.getAmendmentNo()))
            reqBody.put("amendmentNo", inputDTO.getAmendmentNo());
        if (StringUtils.isNotBlank(inputDTO.getAmendmentSrmsId()))
            reqBody.put("amendmentSrmsId", inputDTO.getAmendmentSrmsId());
        if (StringUtils.isNotBlank(inputDTO.getAmount()))
            reqBody.put("amount", inputDTO.getAmount());
        if (StringUtils.isNotBlank(inputDTO.getCancellationStatus()))
            reqBody.put("cancellationStatus", inputDTO.getCancellationStatus());
        if (StringUtils.isNotBlank(inputDTO.getCollectionSrmsId()))
            reqBody.put("collectionSrmsId", inputDTO.getCollectionSrmsId());
        if (StringUtils.isNotBlank(inputDTO.getCreatedDate()))
            reqBody.put("createdDate", inputDTO.getCreatedDate());
        if (StringUtils.isNotBlank(inputDTO.getCurrency()))
            reqBody.put("currency", inputDTO.getCurrency());
        if (StringUtils.isNotBlank(inputDTO.getDraweeAcknowledgement()))
            reqBody.put("draweeAcknowledgement", inputDTO.getDraweeAcknowledgement());
        if (StringUtils.isNotBlank(inputDTO.getDraweeAcknowledgementDate()))
            reqBody.put("draweeAcknowledgementDate", inputDTO.getDraweeAcknowledgementDate());
        if (StringUtils.isNotBlank(inputDTO.getDrawer()))
            reqBody.put("drawer", inputDTO.getDrawer());
        if (StringUtils.isNotBlank(inputDTO.getMaturityDate()))
            reqBody.put("maturityDate", inputDTO.getMaturityDate());
        if (StringUtils.isNotBlank(inputDTO.getMessageFromBank()))
            reqBody.put("messageFromBank", inputDTO.getMessageFromBank());
        if (StringUtils.isNotBlank(inputDTO.getMessageToBank()))
            reqBody.put("messageToBank", inputDTO.getMessageToBank());
        if (StringUtils.isNotBlank(inputDTO.getReasonForCancellation()))
            reqBody.put("reasonForCancellation", inputDTO.getReasonForCancellation());
        if (StringUtils.isNotBlank(inputDTO.getReasonForRejection()))
            reqBody.put("reasonForRejection", inputDTO.getReasonForRejection());
        if (StringUtils.isNotBlank(inputDTO.getReceivedOn()))
            reqBody.put("receivedOn", inputDTO.getReceivedOn());
        if (StringUtils.isNotBlank(inputDTO.getRemittingBank()))
            reqBody.put("remittingBank", inputDTO.getRemittingBank());
        if (StringUtils.isNotBlank(inputDTO.getStatus()))
            reqBody.put("status", inputDTO.getStatus());
        if (StringUtils.isNotBlank(inputDTO.getTenorType()))
            reqBody.put("tenorType", inputDTO.getTenorType());
        if (StringUtils.isNotBlank(inputDTO.getTransactionReference()))
            reqBody.put("transactionReference", inputDTO.getTransactionReference());
        reqBody.put("reasonForReturn", inputDTO.getReasonForReturn());

        return reqBody;
    }

}
