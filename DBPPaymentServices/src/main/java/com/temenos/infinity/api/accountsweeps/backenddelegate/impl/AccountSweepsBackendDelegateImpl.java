package com.temenos.infinity.api.accountsweeps.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.accountsweeps.backenddelegate.api.AccountSweepsBackendDelegate;
import com.temenos.infinity.api.accountsweeps.constants.Constants;
import com.temenos.infinity.api.accountsweeps.constants.ErrorCodeEnum;
import com.temenos.infinity.api.accountsweeps.dto.AccountSweepsDTO;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.temenos.infinity.api.accountsweeps.utils.AccountSweepsAPIServices.DB_GETACCOUNTSWEEP;
import static com.temenos.infinity.api.srmstransactions.config.SrmsTransactionsAPIServices.SERVICEREQUESTJAVA_CREATEORDER;

/**
 * @author naveen.yerra
 */
public class AccountSweepsBackendDelegateImpl implements AccountSweepsBackendDelegate, Constants {
    private static final LoggerUtil LOG = new LoggerUtil(AccountSweepsBackendDelegate.class);

    public AccountSweepsDTO getSweepByAccountId(String accountId) {

        String filter = "primaryAccountNumber" +  DBPUtilitiesConstants.EQUAL + accountId +
                DBPUtilitiesConstants.OR + "secondaryAccountNumber" +  DBPUtilitiesConstants.EQUAL + accountId;
        HashMap<String, Object> inputMap = new HashMap<>();
        inputMap.put(DBPUtilitiesConstants.FILTER, filter);
        String response;
        try {
            response = DBPServiceExecutorBuilder.builder()
                    .withServiceId(DB_GETACCOUNTSWEEP.getServiceName())
                    .withOperationId(DB_GETACCOUNTSWEEP.getOperationName())
                    .withRequestParameters(inputMap)
                    .build().getResponse();
        } catch (Exception e) {
            LOG.error("Unable to get Account Sweep from Backend" + e);
            return ErrorCodeEnum.ERR_3001.setErrorMessageToDto(null);
        }


        AccountSweepsDTO sweepsDTO;
        try {
            JSONArray responseArray = new JSONObject(response).getJSONArray("accountsweeps");
            sweepsDTO = responseArray.length() !=0 ?
                    JSONUtils.parse(responseArray.getJSONObject(0).toString(), AccountSweepsDTO.class) : new AccountSweepsDTO();
        } catch (Exception e) {
            LOG.error("Unable to get Account Sweep from Backend" + e);
            return ErrorCodeEnum.ERR_3001.setErrorMessageToDto(null);
        }
        return sweepsDTO;
    }

    public AccountSweepsDTO createSweepAtBackEnd (AccountSweepsDTO sweepsDTO, DataControllerRequest request) {
        // Convert JSONObject requestbody to String
        String requestBody = constructRequestPayload(sweepsDTO).replaceAll("\"", "'");

        // convert into input map
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put(REQUEST_BODY, requestBody);
        inputMap.put(TYPE, "AccountSweeps");
        inputMap.put(SUBTYPE, "SetUpSweep");
        inputMap.put(ACCOUNT_ID, sweepsDTO.getPrimaryAccountNumber());

        return createOrder(inputMap, request);
    }

    @Override
    public List<AccountSweepsDTO> getAllSweepsFromBackend(Set<String> accounts) {
        StringBuilder filter = new StringBuilder();
        AtomicInteger i = new AtomicInteger(accounts.size());
        accounts.forEach(accountId -> filter.append("primaryAccountNumber" + DBPUtilitiesConstants.EQUAL)
                        .append(accountId).append(i.decrementAndGet() != 0 ? DBPUtilitiesConstants.OR : ""));

        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put(DBPUtilitiesConstants.FILTER, filter.toString());

        String response;
        try {
            response = DBPServiceExecutorBuilder.builder()
                    .withServiceId(DB_GETACCOUNTSWEEP.getServiceName())
                    .withOperationId(DB_GETACCOUNTSWEEP.getOperationName())
                    .withRequestParameters(inputMap)
                    .build().getResponse();
        } catch (Exception e) {
            LOG.error("Unable to get Account Sweep from Backend" + e);
            return null;
        }

        List<AccountSweepsDTO> sweepsList;
        try {
            JSONArray responseArray = new JSONObject(response).getJSONArray("accountsweeps");
            sweepsList = responseArray.length() !=0 ?
                    JSONUtils.parseAsList(responseArray.toString(), AccountSweepsDTO.class) : new ArrayList<>();
        } catch (IOException e) {
            LOG.error("Unable to get Account Sweep from Backend" + e);
            return null;
        }
        return sweepsList;
    }

    public AccountSweepsDTO editSweep(AccountSweepsDTO sweepsDTO, DataControllerRequest request) {
        // Convert JSONObject requestbody to String
        String requestBody = constructRequestPayload(sweepsDTO).replaceAll("\"", "'");

        // convert into input map
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put(REQUEST_BODY, requestBody);
        inputMap.put(TYPE, "AccountSweeps");
        inputMap.put(SUBTYPE, "ModifySweep");
        inputMap.put(ACCOUNT_ID, sweepsDTO.getPrimaryAccountNumber());

        return createOrder(inputMap, request);
    }

    @Override
    public AccountSweepsDTO deleteSweepAtBackEnd(AccountSweepsDTO accountSweepsDTO, DataControllerRequest request) {
        // Convert JSONObject requestbody to String
        String requestBody = constructRequestPayload(accountSweepsDTO).replaceAll("\"", "'");

        // convert into input map
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put(REQUEST_BODY, requestBody);
        inputMap.put(TYPE, "AccountSweeps");
        inputMap.put(SUBTYPE, "StopSweep");
        inputMap.put(ACCOUNT_ID, accountSweepsDTO.getPrimaryAccountNumber());
        return createOrder(inputMap, request);
    }

    private String constructRequestPayload(AccountSweepsDTO inputDTO) {
        String requestBody = null;
        try {
            requestBody = new ObjectMapper().writeValueAsString(inputDTO).replaceAll("\"", "'");
        } catch (JsonProcessingException e) {
            LOG.error("Error occurred while constructing input payload");
        }
        return requestBody;
    }

    private AccountSweepsDTO createOrder(Map<String, Object> inputMap, DataControllerRequest request) {
        AccountSweepsDTO responseDto = new AccountSweepsDTO();
        HashMap<String, Object> headerMap = new HashMap<>();
        headerMap.put(X_KONY_AUTHORIZATION, request.getHeader(X_KONY_AUTHORIZATION));
        headerMap.put(X_KONY_REPORTING_PARAMS, request.getHeader(X_KONY_REPORTING_PARAMS));

        // Making a call to order request API
        String accountsSweepResponse;
        JSONObject Response = new JSONObject();
        try {
            accountsSweepResponse = DBPServiceExecutorBuilder.builder()
                    .withServiceId(SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
                    .withOperationId(SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
                    .build().getResponse();
        } catch (Exception e) {
            LOG.error("Unable to create request " + e);
            return ErrorCodeEnum.ERR_3007.setErrorMessageToDto(responseDto);
        }
        if (StringUtils.isNotBlank(accountsSweepResponse)) {
            Response = new JSONObject(accountsSweepResponse);
            LOG.debug("Response " + accountsSweepResponse);
        }
        if (Response.has(ORDER_ID)){
            responseDto.setServiceRequestId(Response.getString(ORDER_ID));
            responseDto.setMessage(Response.getString("message"));
        }
        if (Response.has(DBP_ERR_MSG) && StringUtils.isNotBlank(Response.getString(DBP_ERR_MSG))) {
            LOG.error("Unable to create request at backend" + Response.getString(DBP_ERR_MSG));
            return ErrorCodeEnum.ERR_3007.setErrorMessageToDto(responseDto);
        }
        return responseDto;
    }
}
