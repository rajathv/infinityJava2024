/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.PaymentAdviceBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.PaymentAdviceDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.getHeadersMap;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.getTypeAndSubType;

public class PaymentAdviceBackendDelegateImpl implements PaymentAdviceBackendDelegate, TradeFinanceConstants {
    private static final Logger LOG = LogManager.getLogger(PaymentAdviceBackendDelegate.class);

    public PaymentAdviceDTO createPaymentAdvice(PaymentAdviceDTO paymentAdviceDTO, DataControllerRequest request) {

        JSONObject requestObj = new JSONObject(paymentAdviceDTO);
        String requestBody = requestObj.toString().replaceAll("\"", "'");

        Map<String, Object> inputMap = getTypeAndSubType("PaymentAdviceType", "PaymentAdviceSubType", true);
        inputMap.put("serviceReqStatus", "Success");
        inputMap.put("requestBody", requestBody);

        // Making a call to order request API
        String response = null;
        JSONObject responseObj = new JSONObject();
        try {
            response = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();

        } catch (Exception e) {
            request.addRequestParam_("isSrmsFailed", "true");
            LOG.error("Unable to create Swift & Advices request order " + e);
        }

        if (StringUtils.isNotBlank(response)) {
            responseObj = new JSONObject(response);
            LOG.info("OMS Response : " + responseObj);
        }

        if (responseObj.has(PARAM_ORDER_ID)
                && StringUtils.isNotBlank(responseObj.getString(PARAM_ORDER_ID))) {
            paymentAdviceDTO.setPaymentAdviceReference(responseObj.getString(PARAM_ORDER_ID));
            paymentAdviceDTO.setStatus("Success");

            request.addRequestParam_("isSrmsFailed", "false");
        } else {
            request.addRequestParam_("isSrmsFailed", "true");
        }

        if (responseObj.has("dbpErrMsg") && StringUtils.isNotBlank(responseObj.getString("dbpErrMsg"))) {
            paymentAdviceDTO.setErrorMessage(responseObj.getString("dbpErrMsg"));
        }

        if (responseObj.has("dbpErrCode") && StringUtils.isNotBlank(responseObj.getString("dbpErrCode"))) {
            paymentAdviceDTO.setErrorCode(String.valueOf(responseObj.getInt("dbpErrCode")));
        }

        return paymentAdviceDTO;
    }

    @Override
    public List<PaymentAdviceDTO> getPaymentAdvice(DataControllerRequest request) {
        List<PaymentAdviceDTO> paymentAdvice = new ArrayList<>();
        String orderId = request.getParameter("orderId");

        Map<String, Object> inputMap = getTypeAndSubType("PaymentAdviceType", "PaymentAdviceSubType", false);

        String paymentAdviceResponse = null;
        JSONObject Response = new JSONObject();
        try {
            paymentAdviceResponse = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();

        } catch (Exception e) {
            LOG.error("Unable to get letter of credits requests " + e);
        }
        if (StringUtils.isNotBlank(paymentAdviceResponse)) {
            Response = new JSONObject(paymentAdviceResponse);
            LOG.info("OMS Response " + paymentAdviceResponse);
        }

        JSONArray Orders = Response.getJSONArray(PARAM_SERVICE_REQUESTS);
        for (int i = 0; i < Orders.length(); i++) {
            PaymentAdviceDTO order;
            JSONObject singleOrder = Orders.getJSONObject(i);
            if (singleOrder.has(PARAM_INPUT_PAYLOAD)) {
                JSONObject responseObj = singleOrder.getJSONObject(PARAM_INPUT_PAYLOAD);
                try {
                    order = JSONUtils.parse(responseObj.toString(), PaymentAdviceDTO.class);
                    order.setPaymentAdviceReference(singleOrder.getString(PARAM_TRANS_ID));
                    order.setCreatedDate(singleOrder.getString(PARAM_REQUEST_DATE_TIME));
                    if (StringUtils.isNotBlank(orderId)) {
                        if (StringUtils.equals(orderId, order.getOrderId()))
                            paymentAdvice.add(order);
                    } else {
                        if (StringUtils.isNotBlank(order.getOrderId()))
                            paymentAdvice.add(order);
                    }
                } catch (IOException e) {
                    LOG.error("Exception occurred while fetching params: ", e);
                }
            }
        }
        return paymentAdvice;
    }

}
