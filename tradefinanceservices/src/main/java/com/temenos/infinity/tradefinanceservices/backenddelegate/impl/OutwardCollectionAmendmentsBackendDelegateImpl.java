/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.OutwardCollectionAmendmentsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.dto.OutwardCollectionAmendmentsDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceSRMSUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.*;

/**
 * @author k.meiyazhagan
 */
public class OutwardCollectionAmendmentsBackendDelegateImpl implements OutwardCollectionAmendmentsBackendDelegate {

    private static final Logger LOG = LogManager.getLogger(OutwardCollectionAmendmentsBackendDelegateImpl.class);

    @Override
    public OutwardCollectionAmendmentsDTO createAmendment(OutwardCollectionAmendmentsDTO inputDto, DataControllerRequest request) {
        JSONObject responseObject = TradeFinanceSRMSUtils.invoke().createOrder().addRequestBody(inputDto).
                addDataControllerRequest(request).addTypeAndSubType("OutwardCollectionAmendmentsType", "OutwardCollectionAmendmentsSubType").
                sendRequest().fetchResponse();
        if (!responseObject.has(PARAM_DBP_ERR_MSG)) {
            inputDto.setAmendmentReference(responseObject.get(PARAM_ORDER_ID).toString());
        } else {
            inputDto = new OutwardCollectionAmendmentsDTO();
            inputDto.setDbpErrMsg(responseObject.getString(PARAM_DBP_ERR_MSG));
            inputDto.setDbpErrCode(responseObject.getString(PARAM_DBP_ERR_CODE));
        }
        return inputDto;
    }

    @Override
    public List<OutwardCollectionAmendmentsDTO> getAmendments(DataControllerRequest request) {
        List amendments = null;
        try {
            amendments = TradeFinanceSRMSUtils.invoke().addDTO(OutwardCollectionAmendmentsDTO.class).
                    addDataControllerRequest(request).addTypeAndSubType("OutwardCollectionAmendmentsType", "OutwardCollectionAmendmentsSubType").
                    getOrders().sendRequest().fetchOrdersResponseWithDTO();
        } catch (IOException e) {
            LOG.error("Error occurred while fetching outward collection amendments", e);
        }
        return amendments;
    }

    @Override
    public OutwardCollectionAmendmentsDTO getAmendmentById(String amendmentReference, DataControllerRequest request) {
        OutwardCollectionAmendmentsDTO amendmentDto = null;
        try {
            amendmentDto = (OutwardCollectionAmendmentsDTO) TradeFinanceSRMSUtils.invoke().addDTO(OutwardCollectionAmendmentsDTO.class).
                    addServiceRequestId(amendmentReference).addDataControllerRequest(request).
                    getOrderById().sendRequest().fetchOrderByIdResponse();
        } catch (IOException e) {
            LOG.error("Error occurred while fetching outward collection amendment", e);
        }
        return amendmentDto;
    }

    @Override
    public OutwardCollectionAmendmentsDTO updateAmendment(OutwardCollectionAmendmentsDTO inputDto, DataControllerRequest request) {
        JSONObject responseObject = TradeFinanceSRMSUtils.invoke().updateOrder().
                addServiceRequestId(inputDto.getAmendmentReference()).addRequestBody(inputDto).
                addDataControllerRequest(request).sendRequest().fetchResponse();
        if (!responseObject.has(PARAM_DBP_ERR_MSG)) {
            inputDto.setAmendmentReference(responseObject.get(PARAM_ORDER_ID).toString());
        } else {
            inputDto = new OutwardCollectionAmendmentsDTO();
            inputDto.setDbpErrMsg(responseObject.getString(PARAM_DBP_ERR_MSG));
            inputDto.setDbpErrCode(responseObject.getString(PARAM_DBP_ERR_CODE));
        }
        return inputDto;
    }
}
