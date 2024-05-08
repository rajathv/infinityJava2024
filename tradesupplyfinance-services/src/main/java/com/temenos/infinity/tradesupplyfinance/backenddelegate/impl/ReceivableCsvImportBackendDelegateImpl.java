/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.backenddelegate.impl;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradesupplyfinance.backenddelegate.api.ReceivableCsvImportBackendDelegate;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableCsvImportDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;
import static com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceSRMSUtils.invoke;

/**
 * @author k.meiyazhagan
 */
public class ReceivableCsvImportBackendDelegateImpl implements ReceivableCsvImportBackendDelegate {

    private static final Logger LOG = LogManager.getLogger(ReceivableCsvImportBackendDelegateImpl.class);

    @Override
    public ReceivableCsvImportDTO createCsvImport(ReceivableCsvImportDTO inputDto, DataControllerRequest request) {
        JSONObject responseObject = invoke().createOrder().addRequestBody(inputDto)
                .addDataControllerRequest(request).addTypeAndSubType("ReceivableCsvImportType", "ReceivableCsvImportSubType").sendRequest().fetchResponse();
        if (!responseObject.has(PARAM_DBP_ERR_MSG)) {
            inputDto.setFileReference(responseObject.get(PARAM_ORDER_ID).toString());
        } else {
            inputDto = new ReceivableCsvImportDTO();
            inputDto.setDbpErrMsg(responseObject.getString(PARAM_DBP_ERR_MSG));
            inputDto.setDbpErrCode(responseObject.getString(PARAM_DBP_ERR_CODE));
        }
        return inputDto;
    }

    @Override
    public ReceivableCsvImportDTO updateCsvImport(ReceivableCsvImportDTO inputDto, DataControllerRequest request) {
        JSONObject responseObject = invoke().updateOrder().addServiceRequestId(inputDto.getFileReference())
                .addRequestBody(inputDto).addDataControllerRequest(request).sendRequest().fetchResponse();
        if (!responseObject.has(PARAM_DBP_ERR_MSG)) {
            inputDto.setFileReference(responseObject.get(PARAM_ORDER_ID).toString());
        } else {
            inputDto = new ReceivableCsvImportDTO();
            inputDto.setDbpErrMsg(responseObject.getString(PARAM_DBP_ERR_MSG));
            inputDto.setDbpErrCode(responseObject.getString(PARAM_DBP_ERR_CODE));
        }
        return inputDto;
    }

    @Override
    public List<ReceivableCsvImportDTO> getCsvImports(DataControllerRequest request) {
        List csvImports = null;
        try {
            csvImports = invoke().addDTO(ReceivableCsvImportDTO.class).addDataControllerRequest(request)
                    .addTypeAndSubType("ReceivableCsvImportType", "ReceivableCsvImportSubType").getOrders().sendRequest().fetchOrdersResponseWithDTO();
        } catch (IOException e) {
            LOG.error("Error occurred while fetching csv import records", e);

        }
        return csvImports;
    }

    @Override
    public ReceivableCsvImportDTO getCsvImportById(String fileReference, DataControllerRequest request) {
        ReceivableCsvImportDTO collectionDto = null;
        try {
            collectionDto = (ReceivableCsvImportDTO) invoke().addDTO(ReceivableCsvImportDTO.class).
                    addServiceRequestId(fileReference).addDataControllerRequest(request).
                    getOrderById().sendRequest().fetchOrderByIdResponse();
        } catch (IOException e) {
            LOG.error("Error occurred while fetching csv import record", e);
        }

        return collectionDto;
    }
}
