/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GuaranteesAmendmentsSwiftAndAdvicesBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.SwiftsAndAdvisesDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.*;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.getHeadersMap;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.getTypeAndSubType;

public class GuaranteesAmendmentsSwiftAndAdvicesBackendDelegateImpl implements GuaranteesAmendmentsSwiftAndAdvicesBackendDelegate {
    private static final Logger LOG = LogManager.getLogger(GuaranteesAmendmentsSwiftAndAdvicesBackendDelegateImpl.class);

    @Override
    public SwiftsAndAdvisesDTO createSwiftsAndAdvises(SwiftsAndAdvisesDTO swiftsAndAdvises, DataControllerRequest request) {
        SwiftsAndAdvisesDTO swiftAndMessagesResponse = new SwiftsAndAdvisesDTO();
        JSONObject requestBody = constructSRMSParams(swiftsAndAdvises);
        String requestbody = requestBody.toString().replaceAll("\"", "\'");

        Map<String, Object> inputMap = swiftsAndAdvises.getModule().equalsIgnoreCase("GUAM") ?
                getTypeAndSubType("GuaranteeLetterOfCreditAmendmentsSwiftAndAdvicesType", "GuaranteeLetterOfCreditAmendmentsSwiftAndAdvicesSubType", true)
                : getTypeAndSubType("ReceivedGuaranteeSwiftMessagesType", "ReceivedGuaranteeSwiftMessagesSubType", true);
        inputMap.put(PARAM_SERVICE_REQ_STATUS, "Success");
        inputMap.put("requestBody", requestbody);

        // Making a call to order request API
        String SwiftAdvicesResponse = null;
        JSONObject Response = new JSONObject();
        try {
            SwiftAdvicesResponse = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();

        } catch (Exception e) {
            request.addRequestParam_("isSrmsFailed", "true");
            LOG.error("Unable to create Swift & Advices request order " + e);
        }

        if (StringUtils.isNotBlank(SwiftAdvicesResponse)) {
            Response = new JSONObject(SwiftAdvicesResponse);
            LOG.info("OMS Response : " + Response);
        }

        if (Response.has(TradeFinanceConstants.PARAM_ORDER_ID) && StringUtils.isNotBlank(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID))) {
            swiftAndMessagesResponse.setSwiftsAndAdvicesSrmsRequestOrderID(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID));
            swiftAndMessagesResponse.setStatus("Success");
            request.addRequestParam_("isSrmsFailed", "false");
        } else {
            request.addRequestParam_("isSrmsFailed", "true");
        }

        if (Response.has("dbpErrMsg") && StringUtils.isNotBlank(Response.getString("dbpErrMsg")))
            swiftAndMessagesResponse.setErrorMessage(Response.getString("dbpErrMsg"));

        if (Response.has("dbpErrCode") && StringUtils.isNotBlank(String.valueOf(Response.get("dbpErrCode"))))
            swiftAndMessagesResponse.setErrorCode(String.valueOf(Response.getInt("dbpErrCode")));

        return swiftAndMessagesResponse;
    }

    public List<SwiftsAndAdvisesDTO> getGuaranteeSwiftAdvices(DataControllerRequest request) {

        String orderId = request.getParameter("orderId");

        // Set Input Map
        Map<String, Object> inputMap = request.getParameter("module").equalsIgnoreCase("GUAM") ?
                getTypeAndSubType("GuaranteeLetterOfCreditAmendmentsSwiftAndAdvicesType", "GuaranteeLetterOfCreditAmendmentsSwiftAndAdvicesSubType", true)
                : getTypeAndSubType("ReceivedGuaranteeSwiftMessagesType", "ReceivedGuaranteeSwiftMessagesSubType", false);

        String guaranteeSwiftAdvicesRes = null;
        JSONObject responseJSON = new JSONObject();
        try {
            guaranteeSwiftAdvicesRes = DBPServiceExecutorBuilder.builder()
                    .withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getServiceName())
                    .withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getOperationName())
                    .withRequestParameters(inputMap).withRequestHeaders(getHeadersMap(request)).withDataControllerRequest(request)
                    .build().getResponse();
        } catch (Exception e) {
            LOG.error("Unable to get guarantees requests " + e);
        }

        if (StringUtils.isNotBlank(guaranteeSwiftAdvicesRes)) {
            responseJSON = new JSONObject(guaranteeSwiftAdvicesRes);
            LOG.info("MS Response " + guaranteeSwiftAdvicesRes);
        }

        List<SwiftsAndAdvisesDTO> guarantees = new ArrayList<>();
        JSONArray Orders = responseJSON.getJSONArray(PARAM_SERVICE_REQUESTS);
        for (int i = 0; i < Orders.length(); i++) {
            JSONObject singleOrder = Orders.getJSONObject(i);
            if (singleOrder.has(PARAM_INPUT_PAYLOAD)) {
                JSONObject inputPayload = singleOrder.getJSONObject(PARAM_INPUT_PAYLOAD);
                SwiftsAndAdvisesDTO swiftsDTO;
                try {
                    swiftsDTO = JSONUtils.parse(inputPayload.toString(), SwiftsAndAdvisesDTO.class);
                    swiftsDTO.setSwiftsAndAdvicesSrmsRequestOrderID((String) singleOrder.get(PARAM_SERVICE_REQ_ID));
                    if (StringUtils.isNotBlank(orderId)) {
                        if (StringUtils.equals(orderId, swiftsDTO.getOrderId()))
                            guarantees.add(swiftsDTO);
                    } else {
                        if (StringUtils.isNotBlank(swiftsDTO.getOrderId()))
                            guarantees.add(swiftsDTO);
                    }
                } catch (IOException e) {
                    LOG.error("Exception occurred while processing guarantees: ", e);
                }
            }
        }
        return guarantees;
    }

    private JSONObject constructSRMSParams(SwiftsAndAdvisesDTO inputDTO) {
        JSONObject reqBody = new JSONObject(inputDTO);

        reqBody.remove("module");
        if (StringUtils.isNotBlank(inputDTO.getNewSequence()))
            reqBody.put("newSequence", inputDTO.getNewSequence().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getRequestedDateOfIssue()))
            reqBody.put("requestedDateOfIssue", inputDTO.getRequestedDateOfIssue().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getFormOfUndertaking()))
            reqBody.put("formOfUndertaking", inputDTO.getFormOfUndertaking().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getApplicableRules()))
            reqBody.put("applicableRules", inputDTO.getApplicableRules().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getTypeOfUndertaking()))
            reqBody.put("typeOfUndertaking", inputDTO.getTypeOfUndertaking().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getExpiryType()))
            reqBody.put("expiryType", inputDTO.getExpiryType().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getDateOfExpiry()))
            reqBody.put("dateOfExpiry", inputDTO.getDateOfExpiry().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getExpiryConditionOrEvent()))
            reqBody.put("expiryConditionOrEvent", inputDTO.getExpiryConditionOrEvent().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getApplicant()))
            reqBody.put("applicant", inputDTO.getApplicant().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getObligorOrInstructingParty()))
            reqBody.put("obligorOrInstructingParty", inputDTO.getObligorOrInstructingParty().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getIssuer()))
            reqBody.put("issuer", inputDTO.getIssuer().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getBeneficiary()))
            reqBody.put("beneficiary", inputDTO.getBeneficiary().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getUndertakingAmount()))
            reqBody.put("undertakingAmount", inputDTO.getUndertakingAmount().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getAdditionalAmountInformation()))
            reqBody.put("additionalAmountInformation", inputDTO.getAdditionalAmountInformation().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getAvailableWith()))
            reqBody.put("availableWith", inputDTO.getAvailableWith().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getCharges()))
            reqBody.put("charges", inputDTO.getCharges().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getDocumentAndPresentationInstructions()))
            reqBody.put("documentAndPresentationInstructions", inputDTO.getDocumentAndPresentationInstructions().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getRequestedLocalUndertakingTermsAndConditions()))
            reqBody.put("requestedLocalUndertakingTermsAndConditions", inputDTO.getRequestedLocalUndertakingTermsAndConditions().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getStandardWordingRequired()))
            reqBody.put("standardWordingRequired", inputDTO.getStandardWordingRequired().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getStandardWordingRequestedLanguage()))
            reqBody.put("standardWordingRequestedLanguage", inputDTO.getStandardWordingRequestedLanguage().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getGoverningLawAndOrPlaceOfJurisdiction()))
            reqBody.put("governingLawAndOrPlaceOfJurisdiction", inputDTO.getGoverningLawAndOrPlaceOfJurisdiction().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getAutomaticExtensionPeriod()))
            reqBody.put("automaticExtensionPeriod", inputDTO.getAutomaticExtensionPeriod().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getAutomaticExtensionNonExtensionPeriod()))
            reqBody.put("automaticExtensionNonExtensionPeriod", inputDTO.getAutomaticExtensionNonExtensionPeriod().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getAutomaticExtensionNotificationPeriod()))
            reqBody.put("automaticExtensionNotificationPeriod", inputDTO.getAutomaticExtensionNotificationPeriod().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getAutomaticExtensionFinalExpiryDate()))
            reqBody.put("automaticExtensionFinalExpiryDate", inputDTO.getAutomaticExtensionFinalExpiryDate().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getDemandIndicator()))
            reqBody.put("demandIndicator", inputDTO.getDemandIndicator().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getTransferIndicator()))
            reqBody.put("transferIndicator", inputDTO.getTransferIndicator().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getTransferConditions()))
            reqBody.put("transferConditions", inputDTO.getTransferConditions().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getUnderlyingTransactionDetails()))
            reqBody.put("underlyingTransactionDetails", inputDTO.getUnderlyingTransactionDetails().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getDeliveryOfLocalUndertaking()))
            reqBody.put("deliveryOfLocalUndertaking", inputDTO.getDeliveryOfLocalUndertaking().replaceAll("\'", "\""));
        if (StringUtils.isNotBlank(inputDTO.getDeliveryToOrCollectionBy()))
            reqBody.put("deliveryToOrCollectionBy", inputDTO.getDeliveryToOrCollectionBy().replaceAll("\'", "\""));

        return reqBody;
    }

}
