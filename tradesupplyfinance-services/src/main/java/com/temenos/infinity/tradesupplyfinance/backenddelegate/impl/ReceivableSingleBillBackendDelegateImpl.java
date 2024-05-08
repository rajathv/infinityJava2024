/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.backenddelegate.impl;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradesupplyfinance.backenddelegate.api.ReceivableSingleBillBackendDelegate;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;
import static com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceSRMSUtils.*;

/**
 * @author k.meiyazhagan
 */
public class ReceivableSingleBillBackendDelegateImpl implements ReceivableSingleBillBackendDelegate {

    private static final Logger LOG = LogManager.getLogger(ReceivableSingleBillBackendDelegateImpl.class);

    @Override
    public ReceivableSingleBillDTO createSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        JSONObject responseObject = invoke().createOrder().addRequestBody(inputDto)
                .addDataControllerRequest(request).addTypeAndSubType("ReceivableSingleBillType", "ReceivableSingleBillSubType").sendRequest().fetchResponse();
        if (!responseObject.has(PARAM_DBP_ERR_MSG)) {
            inputDto.setBillReference(responseObject.get(PARAM_ORDER_ID).toString());
        } else {
            inputDto = new ReceivableSingleBillDTO();
            inputDto.setDbpErrMsg(responseObject.getString(PARAM_DBP_ERR_MSG));
            inputDto.setDbpErrCode(responseObject.getString(PARAM_DBP_ERR_CODE));
        }
        return inputDto;
    }

    @Override
    public ReceivableSingleBillDTO updateSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        JSONObject responseObject = invoke().updateOrder().addServiceRequestId(inputDto.getBillReference())
                .addRequestBody(inputDto).addDataControllerRequest(request).sendRequest().fetchResponse();
        if (!responseObject.has(PARAM_DBP_ERR_MSG)) {
            inputDto.setBillReference(responseObject.get(PARAM_ORDER_ID).toString());
        } else {
            inputDto = new ReceivableSingleBillDTO();
            inputDto.setDbpErrMsg(responseObject.getString(PARAM_DBP_ERR_MSG));
            inputDto.setDbpErrCode(responseObject.getString(PARAM_DBP_ERR_CODE));
        }
        return inputDto;
    }

    @Override
    public List<ReceivableSingleBillDTO> getSingleBills(DataControllerRequest request) {
        List bills = null;
        try {
            bills = invoke().addDTO(ReceivableSingleBillDTO.class).addDataControllerRequest(request)
                    .addTypeAndSubType("ReceivableSingleBillType", "ReceivableSingleBillSubType").getOrders().sendRequest().fetchOrdersResponseWithDTO();
        } catch (IOException e) {
            LOG.error("Error occurred while fetching Trade single bills", e);

        }
        return bills;
    }

    @Override
    public ReceivableSingleBillDTO getSingleBillById(String billReference, DataControllerRequest request) {
        ReceivableSingleBillDTO collectionDto = null;
        try {
            collectionDto = (ReceivableSingleBillDTO) invoke().addDTO(ReceivableSingleBillDTO.class).
                    addServiceRequestId(billReference).addDataControllerRequest(request).
                    getOrderById().sendRequest().fetchOrderByIdResponse();
        } catch (IOException e) {
            LOG.error("Error occurred while fetching single bill", e);
        }
        return collectionDto;
    }
}
