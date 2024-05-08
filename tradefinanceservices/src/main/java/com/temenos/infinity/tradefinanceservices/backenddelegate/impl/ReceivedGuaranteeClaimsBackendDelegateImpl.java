/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.ReceivedGuaranteeClaimsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.ReceivedGuaranteeClaimsDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum.ERRTF_29070;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.*;


public class ReceivedGuaranteeClaimsBackendDelegateImpl implements ReceivedGuaranteeClaimsBackendDelegate, TradeFinanceConstants {

    private static final Logger LOG = LogManager.getLogger(ReceivedGuaranteeClaimsBackendDelegateImpl.class);

    public ReceivedGuaranteeClaimsDTO createClaim(ReceivedGuaranteeClaimsDTO claimsDTO, HashMap<String, Object> inputParams,
                                                  DataControllerRequest request) {

        claimsDTO.setServiceRequestTime(getCurrentDateTimeUTF(TIMESTAMP_FORMAT));
        claimsDTO.setCreatedOn(getCurrentDateTimeUTF(STANDARDTIMESTAMP_FORMAT));

        // Input Parameters in InputMap
        Map<String, Object> inputMap = getTypeAndSubType("ReceivedGuaranteeClaimsType", "ReceivedGuaranteeClaimsSubType", true);
        inputMap.put("serviceReqStatus", "Success");
        inputMap.put("requestBody", getRequestBody(claimsDTO));
        LOG.info("OMS Guarantees Claim Request" + inputMap);

        // Making a call to order request API
        String guaranteeClaimsResponse = null;
        try {
            guaranteeClaimsResponse = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();
        } catch (Exception e) {
            request.addRequestParam_("isSrmsFailed", "true");
            LOG.error("Unable to create Guarantee claims request order " + e);
        }
        JSONObject responseObject;
        if (StringUtils.isNotEmpty(guaranteeClaimsResponse)) {
            responseObject = new JSONObject(guaranteeClaimsResponse);
            if (!responseObject.has("dbpErrMsg")) {
                claimsDTO.setClaimsSRMSId(responseObject.get("orderId").toString());
                claimsDTO.setMessage("Service Request Created successfully");
            }

            if (responseObject.has("dbpErrCode") && StringUtils.isNotEmpty(
                    String.valueOf(responseObject.get("dbpErrCode")))) {
                claimsDTO = new ReceivedGuaranteeClaimsDTO();
                claimsDTO.setErrorCode(String.valueOf(responseObject.get("dbpErrCode")));
                LOG.error("Unable to create Guarantee claims request order Error Code" + String.valueOf(
                        responseObject.get("dbpErrCode")));
            }
            if (responseObject.has("dbpErrMsg") && StringUtils.isNotEmpty(responseObject.getString("dbpErrMsg"))) {
                claimsDTO = new ReceivedGuaranteeClaimsDTO();
                claimsDTO.setErrorMsg(responseObject.getString("dbpErrMsg"));
                LOG.error("Unable to create Guarantee claims request order Error Message" + responseObject.getString(
                        "dbpErrMsg"));
            }
        }
        return claimsDTO;
    }

    public ReceivedGuaranteeClaimsDTO updateGuaranteeClaims(ReceivedGuaranteeClaimsDTO inputClaimsDTO,
                                                            boolean isMergeRequired,
                                                            ReceivedGuaranteeClaimsDTO initiatedClaimsDTO,
                                                            DataControllerRequest request) {
        String srmsId = inputClaimsDTO.getClaimsSRMSId();
        boolean isSrmsFailed = false;
        try {
            Map<String, Object> inputMap = new HashMap<>();
            inputMap.put("serviceRequestId", srmsId);
            JSONObject reqBody = isMergeRequired ? getRequestBody(inputClaimsDTO, initiatedClaimsDTO) : new JSONObject(inputClaimsDTO);
            inputMap.put("requestBody", getRequestBody(JSONUtils.parse(reqBody.toString(), ReceivedGuaranteeClaimsDTO.class)));
            String updateResponse = null;
            JSONObject Response;
            try {
                updateResponse = DBPServiceExecutorBuilder.builder()
                        .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getServiceName())
                        .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getOperationName())
                        .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request))
                        .withDataControllerRequest(request).build().getResponse();
            } catch (Exception e) {
                request.addRequestParam_("isSrmsFailed", "true");
                LOG.error("Unable to update guarantees request order " + e);
                isSrmsFailed = true;
            }
            if (StringUtils.isNotBlank(updateResponse)) {
                Response = new JSONObject(updateResponse);
                LOG.info("OMS Response " + Response);
                inputClaimsDTO.setClaimsSRMSId(srmsId);
                if (Response.has("dbpErrMsg") || Response.has("dbpErrCode")) {
                    LOG.error("Something went wrong while updating guarantees request order"
                            + Response.get("dbpErrMsg") + "  " + Response.get("dbpErrCode"));
                    isSrmsFailed = true;
                }
            }

        } catch (Exception e) {
            LOG.error("Unable to GET SRMS ID " + e);
            isSrmsFailed = true;
        }

        inputClaimsDTO.setMessage("Service Request Updated successfully");
        if (isSrmsFailed) {
            inputClaimsDTO = new ReceivedGuaranteeClaimsDTO();
            inputClaimsDTO.setDbpErrMsg("Unable to update record. Please try after sometime.");
        }
        return inputClaimsDTO;
    }

    private JSONObject getRequestBody(ReceivedGuaranteeClaimsDTO inputClaimsDTO, ReceivedGuaranteeClaimsDTO backendClaimsDTO) {
        JSONObject inputClaimsObject = new JSONObject(inputClaimsDTO);
        if (backendClaimsDTO.getDocumentInformation() != null)
            backendClaimsDTO.setDocumentInformation(new JSONArray(backendClaimsDTO.getDocumentInformation()).toString());
        if (backendClaimsDTO.getPhysicalDocuments() != null)
            backendClaimsDTO.setPhysicalDocuments(new JSONArray(backendClaimsDTO.getPhysicalDocuments()).toString());
        if (backendClaimsDTO.getReturnedHistory() != null)
            backendClaimsDTO.setReturnedHistory(backendClaimsDTO.getReturnedHistory().replaceAll("\'", "\""));

        return TradeFinanceCommonUtils.mergeJSONObjects(new JSONObject(backendClaimsDTO), inputClaimsObject);
    }

    public List<ReceivedGuaranteeClaimsDTO> getClaims(DataControllerRequest request) {
        List<ReceivedGuaranteeClaimsDTO> ReceivedGuaranteeClaimsDTOList = new ArrayList<>();
        String customerId = fetchCustomerFromSession(request);
        Map<String, Object> inputMap = getTypeAndSubType("ReceivedGuaranteeClaimsType", "ReceivedGuaranteeClaimsSubType", false);

        String guaranteeClaimsResponse = null;
        JSONObject Response = new JSONObject();
        try {
            guaranteeClaimsResponse = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();

        } catch (Exception e) {
            LOG.error("Unable to get export letter of credits requests " + e);
        }

        if (StringUtils.isNotBlank(guaranteeClaimsResponse)) {
            Response = new JSONObject(guaranteeClaimsResponse);
            LOG.info("OMS Response " + guaranteeClaimsResponse);
        }

        JSONArray Orders = Response.getJSONArray(PARAM_SERVICE_REQUESTS);
        ReceivedGuaranteeClaimsDTO claimsDTO = null;
        for (int i = 0; i < Orders.length(); i++) {
            JSONObject singleOrder = Orders.getJSONObject(i);
            if (StringUtils.equals(customerId, singleOrder.getString(PARAM_PARTY_ID)) && singleOrder.has(PARAM_INPUT_PAYLOAD)) {
                JSONObject inputPayload = singleOrder.getJSONObject(PARAM_INPUT_PAYLOAD);
                try {
                    claimsDTO = JSONUtils.parse(inputPayload.toString(), ReceivedGuaranteeClaimsDTO.class);
                    claimsDTO.setClaimsSRMSId(singleOrder.getString(PARAM_SERVICE_REQ_ID));
                } catch (IOException e) {
                    LOG.error("Exception occurred while fetching params: ", e);
                }
                ReceivedGuaranteeClaimsDTOList.add(claimsDTO);
            }
        }
        return ReceivedGuaranteeClaimsDTOList;
    }

    public ReceivedGuaranteeClaimsDTO getClaimsById(String claimsSRMSId, DataControllerRequest request) {

        JSONObject Response = new JSONObject();
        ReceivedGuaranteeClaimsDTO claimsDTO = new ReceivedGuaranteeClaimsDTO();

        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("serviceRequestIds", claimsSRMSId);

        String response;
        try {
            response = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();

        } catch (Exception e) {
            request.addRequestParam_("isSrmsFailed", "true");
            claimsDTO.setDbpErrMsg("Failed to fetch record");
            LOG.error("Unable to GET SRMS ID " + e);
            return claimsDTO;
        }
        if (StringUtils.isNotBlank(response)) {
            Response = new JSONObject(response);
            LOG.info("OMS Response " + response);
        }

        try {
            JSONArray Orders = Response.getJSONArray(PARAM_SERVICE_REQUESTS);
            JSONObject serviceResponse = Orders.getJSONObject(0);
            if (!StringUtils.equals(serviceResponse.getString(PARAM_PARTY_ID), fetchCustomerFromSession(request))) {
                throw new SecurityException("Unauthorized Access to record");
            }
            if (serviceResponse.has(PARAM_INPUT_PAYLOAD)) {
                JSONObject inputPayload = serviceResponse.getJSONObject(PARAM_INPUT_PAYLOAD);
                try {
                    claimsDTO = JSONUtils.parse(inputPayload.toString(), ReceivedGuaranteeClaimsDTO.class);
                    claimsDTO.setClaimsSRMSId(serviceResponse.getString(PARAM_SERVICE_REQ_ID));
                } catch (IOException e) {
                    LOG.error("Exception occurred while fetching params: ", e);
                    claimsDTO.setDbpErrMsg("Failed to fetch record");
                }
            }
        } catch (SecurityException e) {
            LOG.error("SECURITY EXCEPTION - UNAUTHORIZED ACCESS " + e);
            claimsDTO.setDbpErrCode(ERRTF_29070.getErrorCodeAsString());
            claimsDTO.setDbpErrMsg(ERRTF_29070.getErrorMessage());
            claimsDTO.setErrorMsg(e.getMessage());
        } catch (Exception e) {
            claimsDTO.setDbpErrMsg("Failed to fetch record");
            LOG.info("OMS Response " + response);
            LOG.error("Unable to GET SRMS ID " + e);
            return claimsDTO;
        }
        return claimsDTO;
    }

    private String getCurrentDateTimeUTF(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(new Date());
    }

    private String getRequestBody(ReceivedGuaranteeClaimsDTO inputDto) {
        String requestBody = null;
        try {
            requestBody = new ObjectMapper().writeValueAsString(inputDto).replaceAll("\"", "'");
        } catch (JsonProcessingException e) {
            LOG.error("Error in processing input payload", e);
        }
        return requestBody;
    }

}
