/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.OutwardCollectionsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.dto.OutwardCollectionsDTO;
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
public class OutwardCollectionsBackendDelegateImpl implements OutwardCollectionsBackendDelegate {

    private static final Logger LOG = LogManager.getLogger(OutwardCollectionsBackendDelegateImpl.class);

    @Override
    public OutwardCollectionsDTO createCollection(OutwardCollectionsDTO inputDto, DataControllerRequest request) {
        JSONObject responseObject = TradeFinanceSRMSUtils.invoke().createOrder().addRequestBody(inputDto).
                addDataControllerRequest(request).addTypeAndSubType("OutwardCollectionsType", "OutwardCollectionsSubType").
                sendRequest().fetchResponse();
        if (!responseObject.has(PARAM_DBP_ERR_MSG)) {
            inputDto.setCollectionReference(responseObject.get(PARAM_ORDER_ID).toString());
        } else {
            inputDto = new OutwardCollectionsDTO();
            inputDto.setDbpErrMsg(responseObject.getString(PARAM_DBP_ERR_MSG));
            inputDto.setDbpErrCode(responseObject.getString(PARAM_DBP_ERR_CODE));
        }
        return inputDto;
    }

    @Override
    public List<OutwardCollectionsDTO> getCollections(DataControllerRequest request) {
        List collections = null;
        try {
        	collections = TradeFinanceSRMSUtils.invoke().addDTO(OutwardCollectionsDTO.class).
                    addDataControllerRequest(request).addTypeAndSubType("OutwardCollectionsType", "OutwardCollectionsSubType").
                    getOrders().sendRequest().fetchOrdersResponseWithDTO();
        } catch (IOException e) {
            LOG.error("Error occurred while fetching outward collections", e);
        }
        return collections;
    }

    @Override
    public OutwardCollectionsDTO getCollectionById(String collectionReference, DataControllerRequest request) {
        OutwardCollectionsDTO collectionDto = null;
        try {
        	collectionDto = (OutwardCollectionsDTO) TradeFinanceSRMSUtils.invoke().addDTO(OutwardCollectionsDTO.class).
                    addServiceRequestId(collectionReference).addDataControllerRequest(request).
                    getOrderById().sendRequest().fetchOrderByIdResponse();
        } catch (IOException e) {
            LOG.error("Error occurred while fetching outward collection", e);
        }
        return collectionDto;
    }

    @Override
    public OutwardCollectionsDTO updateCollection(OutwardCollectionsDTO inputDto, DataControllerRequest request) {
        JSONObject responseObject = TradeFinanceSRMSUtils.invoke().updateOrder().
                addServiceRequestId(inputDto.getCollectionReference()).addRequestBody(inputDto).
                addDataControllerRequest(request).sendRequest().fetchResponse();
        if (!responseObject.has(PARAM_DBP_ERR_MSG)) {
            inputDto.setCollectionReference(responseObject.get(PARAM_ORDER_ID).toString());
        } else {
            inputDto = new OutwardCollectionsDTO();
            inputDto.setDbpErrMsg(responseObject.getString(PARAM_DBP_ERR_MSG));
            inputDto.setDbpErrCode(responseObject.getString(PARAM_DBP_ERR_CODE));
        }
        return inputDto;
    }
}
