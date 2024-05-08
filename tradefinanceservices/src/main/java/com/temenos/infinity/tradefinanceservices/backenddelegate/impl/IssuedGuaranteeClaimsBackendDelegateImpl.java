/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.IssuedGuaranteeClaimsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.dto.IssuedGuaranteeClaimsDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum.ERRTF_29070;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.*;

public class IssuedGuaranteeClaimsBackendDelegateImpl implements IssuedGuaranteeClaimsBackendDelegate {
    private static final Logger LOG = LogManager.getLogger(IssuedGuaranteeClaimsBackendDelegateImpl.class);

    @Override
    public IssuedGuaranteeClaimsDTO createClaim(IssuedGuaranteeClaimsDTO guaranteeClaimsDTO, DataControllerRequest request) {

        // Input Parameters in InputMap
        Map<String, Object> inputMap = getTypeAndSubType("IssuedGuaranteeClaimsType", "IssuedGuaranteeClaimsSubType", true);
        inputMap.put("serviceReqStatus", "Success");
        inputMap.put("requestBody", getRequestBody(guaranteeClaimsDTO));
        LOG.info("OMS Guarantees Claim Request" + inputMap);

        // Making a call to order request API
        String guaranteeClaimsResponse = null;
        try {
            guaranteeClaimsResponse = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request))
                    .withDataControllerRequest(request)
                    .build().getResponse();
        } catch (Exception e) {
            request.addRequestParam_("isSrmsFailed", "true");
            LOG.error("Unable to create Guarantee claims request order " + e);
        }
        JSONObject responseObject;
        if (StringUtils.isNotEmpty(guaranteeClaimsResponse)) {
            responseObject = new JSONObject(guaranteeClaimsResponse);
            if (!responseObject.has("dbpErrMsg")) {
                guaranteeClaimsDTO.setClaimsSRMSId(responseObject.get("orderId").toString());
                guaranteeClaimsDTO.setMessage("Service Request Created successfully");
            }

            if (responseObject.has("dbpErrCode") && StringUtils.isNotEmpty(
                    String.valueOf(responseObject.get("dbpErrCode")))) {
                guaranteeClaimsDTO = new IssuedGuaranteeClaimsDTO();
                guaranteeClaimsDTO.setErrorCode(String.valueOf(responseObject.get("dbpErrCode")));
                LOG.error("Unable to create Guarantee claims request order Error Code" + responseObject.get(
                        "dbpErrCode"));
            }
            if (responseObject.has("dbpErrMsg") && StringUtils.isNotEmpty(responseObject.getString("dbpErrMsg"))) {
                guaranteeClaimsDTO = new IssuedGuaranteeClaimsDTO();
                guaranteeClaimsDTO.setErrorMsg(responseObject.getString("dbpErrMsg"));
                LOG.error("Unable to create Guarantee claims request order Error Message" + responseObject.getString(
                        "dbpErrMsg"));
            }
        }
        return guaranteeClaimsDTO;
    }

    public IssuedGuaranteeClaimsDTO updateClaim(IssuedGuaranteeClaimsDTO guaranteeClaimsDTO,
                                                IssuedGuaranteeClaimsDTO previousGuaranteeClaimsDTO,
                                                boolean isMergeRequired, DataControllerRequest request) {
        String srmsId = guaranteeClaimsDTO.getClaimsSRMSId();
        boolean isSrmsFailed = false;
        try {
            Map<String, Object> inputMap = new HashMap<>();
            inputMap.put("serviceRequestId", srmsId);
            JSONObject reqBody = isMergeRequired ?
                    TradeFinanceCommonUtils.mergeJSONObjects(new JSONObject(previousGuaranteeClaimsDTO),
                            new JSONObject(guaranteeClaimsDTO)) : new JSONObject(guaranteeClaimsDTO);
            inputMap.put("requestBody", getRequestBody(JSONUtils.parse(reqBody.toString(), IssuedGuaranteeClaimsDTO.class)));
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
                LOG.error("Unable to update issued guarantee claim request order " + e);
                isSrmsFailed = true;
            }
            if (StringUtils.isNotBlank(updateResponse)) {
                Response = new JSONObject(updateResponse);
                LOG.info("OMS Response " + Response);
                guaranteeClaimsDTO.setClaimsSRMSId(srmsId);
                if (Response.has("dbpErrMsg") || Response.has("dbpErrCode")) {
                    LOG.error("Something went wrong while updating issued guarantee claim request order"
                            + Response.get("dbpErrMsg") + "  " + Response.get("dbpErrCode"));
                    isSrmsFailed = true;
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to GET SRMS ID " + e);
            isSrmsFailed = true;
        }
        guaranteeClaimsDTO.setMessage("Service Request Updated successfully");
        if (isSrmsFailed) {
            guaranteeClaimsDTO = new IssuedGuaranteeClaimsDTO();
            guaranteeClaimsDTO.setDbpErrMsg("Unable to update record. Please try after sometime.");
        }
        return guaranteeClaimsDTO;
    }

    public IssuedGuaranteeClaimsDTO getClaimById(String claimsSRMSId, DataControllerRequest request) {
        JSONObject Response = new JSONObject();
        IssuedGuaranteeClaimsDTO claimsDTO = new IssuedGuaranteeClaimsDTO();

        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("serviceRequestIds", claimsSRMSId);

        String response;
        try {
            response = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getServiceName())
                    .withOperationId(
                            TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request))
                    .withDataControllerRequest(request)
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
                    claimsDTO = JSONUtils.parse(inputPayload.toString(), IssuedGuaranteeClaimsDTO.class);
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

    public List<IssuedGuaranteeClaimsDTO> getClaims(DataControllerRequest request) {
        List<IssuedGuaranteeClaimsDTO> issuedGuaranteeClaimsDTOS = new ArrayList<>();
        Map<String, Object> inputMap = getTypeAndSubType("IssuedGuaranteeClaimsType", "IssuedGuaranteeClaimsSubType",
                false);

        String guaranteeClaimsResponse = null;
        JSONObject Response = new JSONObject();
        try {
            guaranteeClaimsResponse = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request))
                    .withDataControllerRequest(request)
                    .build().getResponse();

        } catch (Exception e) {
            LOG.error("Unable to get issued guarantee claim requests " + e);
        }

        if (StringUtils.isNotBlank(guaranteeClaimsResponse)) {
            Response = new JSONObject(guaranteeClaimsResponse);
            LOG.info("OMS Response " + guaranteeClaimsResponse);
        }

        JSONArray Orders = Response.getJSONArray(PARAM_SERVICE_REQUESTS);
        IssuedGuaranteeClaimsDTO claimsDTO = null;
        for (int i = 0; i < Orders.length(); i++) {
            JSONObject singleOrder = Orders.getJSONObject(i);
            if (singleOrder.has(PARAM_INPUT_PAYLOAD)) {
                JSONObject inputPayload = singleOrder.getJSONObject(PARAM_INPUT_PAYLOAD);
                try {
                    claimsDTO = JSONUtils.parse(inputPayload.toString(), IssuedGuaranteeClaimsDTO.class);
                    claimsDTO.setClaimsSRMSId(singleOrder.getString(PARAM_SERVICE_REQ_ID));
                } catch (IOException e) {
                    LOG.error("Exception occurred while fetching params: ", e);
                }
                issuedGuaranteeClaimsDTOS.add(claimsDTO);
            }

        }
        return issuedGuaranteeClaimsDTOS;
    }

    private String getRequestBody(IssuedGuaranteeClaimsDTO inputDto) {
        String requestBody = null;
        try {
            requestBody = new ObjectMapper().writeValueAsString(inputDto).replaceAll("\"", "'");
        } catch (JsonProcessingException e) {
            LOG.error("Error in processing input payload", e);
        }
        return requestBody;
    }
}
