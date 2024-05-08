/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.resource.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradesupplyfinance.dto.TsfFilterDTO;
import com.temenos.infinity.tradesupplyfinance.businessdelegate.api.ReceivableCsvImportBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.businessdelegate.api.ReceivableSingleBillBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.config.TradeSupplyFinanceAlerts;
import com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableCsvImportDTO;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;
import com.temenos.infinity.tradesupplyfinance.resource.api.ReceivableCsvImportResource;
import com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceCommonUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static com.temenos.infinity.tradesupplyfinance.config.TradeSupplyFinanceAPIServices.RECEIVABLECSVIMPORT_CREATESINGLEBILLS;
import static com.temenos.infinity.tradesupplyfinance.config.TradeSupplyFinanceAPIServices.RECEIVABLECSVIMPORT_DELETESINGLEBILLS;
import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;
import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceStatus.*;
import static com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceCommonUtils.getCurrentDateTimeUTF;

/**
 * @author k.meiyazhagan
 */
public class ReceivableCsvImportResourceImpl implements ReceivableCsvImportResource {

    private static final Logger LOG = Logger.getLogger(ReceivableCsvImportResourceImpl.class);
    private static final ReceivableCsvImportBusinessDelegate csvBusiness = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ReceivableCsvImportBusinessDelegate.class);
    private static final ReceivableSingleBillBusinessDelegate billResource = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ReceivableSingleBillBusinessDelegate.class);

    @Override
    public Result createBillsCsvImport(ReceivableCsvImportDTO inputDto, DataControllerRequest request) {
        TradeSupplyFinanceAlerts alertToPush;
        inputDto.setStatus(PARAM_STATUS_DRAFT);
        inputDto.setFileType("Bill");
        inputDto.setCounts("1");
        inputDto.setCreatedOn(getCurrentDateTimeUTF());
        inputDto.setUpdatedOn(inputDto.getCreatedOn());

        try {
            inputDto = csvBusiness.createCsvImport(inputDto, request);
            if (StringUtils.isBlank(inputDto.getFileReference())) {
                return ErrorCodeEnum.ERR_30014.setErrorCode(new Result());
            }

            HashMap inputParams = new ObjectMapper().readValue(request.getParameter(PARAM_BILLS), HashMap.class);
            int totalRecords = StringUtils.countMatches(inputParams.get("currency").toString(), SEPARATOR_BILLS_ORCHESTRATION);
            inputParams.put(PARAM_FILE_REFERENCE, StringUtils.repeat(inputDto.getFileReference() + SEPARATOR_BILLS_ORCHESTRATION, totalRecords));
            inputParams.put(PARAM_LOOP_COUNT, totalRecords);

            String createResponse = DBPServiceExecutorBuilder.builder()
                    .withServiceId(RECEIVABLECSVIMPORT_CREATESINGLEBILLS.getServiceName())
                    .withObjectId(null)
                    .withOperationId(RECEIVABLECSVIMPORT_CREATESINGLEBILLS.getOperationName())
                    .withRequestParameters(inputParams)
                    .withRequestHeaders(request.getHeaderMap())
                    .withDataControllerRequest(request)
                    .build().getResponse();
            JSONArray createdBills = new JSONObject(createResponse).getJSONArray("LoopDataset");
            inputDto.setBills(createdBills);
            JSONObject billsToCsv = new JSONObject();
            int createdBillsCount = 0;
            for (int i = 0; i < createdBills.length(); i++) {
                JSONObject bill = createdBills.getJSONObject(i);
                if (bill.has(PARAM_BILL_REFERENCE)) {
                    createdBillsCount++;
                    billsToCsv.put(bill.getString(PARAM_BILL_REFERENCE), bill.getString("status"));
                }
            }
            inputDto.setBillReferences(String.valueOf(billsToCsv));
            inputDto.setStatus(PARAM_STATUS_IN_REVIEW);
            alertToPush = TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_IMPORT_DONE;
        } catch (Exception e) {
            LOG.error(e);
            inputDto.setStatus(PARAM_STATUS_DELETED);
            alertToPush = TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_IMPORT_FAILED;
        }

        inputDto.setUpdatedOn(getCurrentDateTimeUTF());
        ReceivableCsvImportDTO updateResponseDTO = csvBusiness.updateCsvImport(inputDto, request);
        Result result = JSONToResult.convert(String.valueOf(new JSONObject(updateResponseDTO)));
        TradeSupplyFinanceCommonUtils.setAlertDataInResult(result, alertToPush, inputDto.getFileReference());
        return result;
    }

    @Override
    public Result deleteImportedBills(DataControllerRequest request) {
        TradeSupplyFinanceAlerts alertToPush;
        String fileReference = request.getParameter(PARAM_FILE_REFERENCE);
        if (StringUtils.isBlank(fileReference)) {
            return ErrorCodeEnum.ERR_30004.setErrorCode(new Result());
        }

        ReceivableCsvImportDTO csvImportDTO;
        try {
            csvImportDTO = csvBusiness.getCsvImportById(fileReference, request);
            JSONObject csvBills = new JSONObject(csvImportDTO.getBillReferences());
            Iterator<String> importedBills = csvBills.keys();

            StringBuilder orchInput = new StringBuilder();
            while (importedBills.hasNext()) {
                String billReference = importedBills.next();
                if (!Objects.equals(csvBills.getString(billReference), PARAM_STATUS_IN_REVIEW)) {
                    return ErrorCodeEnum.ERR_30023.setErrorCode(new Result());
                }
                orchInput.append(billReference).append(SEPARATOR_BILLS_ORCHESTRATION);
            }

            HashMap<String, Object> inputParams = new HashMap<>();
            inputParams.put(PARAM_BILL_REFERENCE, orchInput);
            inputParams.put(PARAM_LOOP_COUNT, StringUtils.countMatches(orchInput.toString(), SEPARATOR_BILLS_ORCHESTRATION));

            String deleteResponse = DBPServiceExecutorBuilder.builder()
                    .withServiceId(RECEIVABLECSVIMPORT_DELETESINGLEBILLS.getServiceName())
                    .withObjectId(null)
                    .withOperationId(RECEIVABLECSVIMPORT_DELETESINGLEBILLS.getOperationName())
                    .withRequestParameters(inputParams)
                    .withRequestHeaders(request.getHeaderMap())
                    .withDataControllerRequest(request)
                    .build().getResponse();

            JSONArray deletedBills = new JSONObject(deleteResponse).getJSONArray("LoopDataset");
            for (int i = 0; i < deletedBills.length(); i++) {
                JSONObject bill = deletedBills.getJSONObject(i);
                if (bill.has(PARAM_BILL_REFERENCE)) {
                    csvBills.put(bill.getString(PARAM_BILL_REFERENCE), bill.getString("status"));
                }
            }

            csvImportDTO.setBillReferences(String.valueOf(csvBills));
            csvImportDTO.setStatus(PARAM_STATUS_DELETED);
            csvImportDTO.setUpdatedOn(getCurrentDateTimeUTF());
            csvImportDTO = csvBusiness.updateCsvImport(csvImportDTO, request);
            alertToPush = TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_IMPORT_DELETED;
        } catch (Exception e) {
            return ErrorCodeEnum.ERR_30024.setErrorCode(new Result());
        }

        Result result = JSONToResult.convert(String.valueOf(new JSONObject(csvImportDTO)));
        TradeSupplyFinanceCommonUtils.setAlertDataInResult(result, alertToPush, csvImportDTO.getFileReference());
        return result;
    }

    @Override
    public Result updateCsvImportBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        if (StringUtils.isBlank(inputDto.getBillReference())
                || StringUtils.isBlank(inputDto.getUploadedDocuments())) {
            return ErrorCodeEnum.ERR_30004.setErrorCode(new Result());
        }

        ReceivableSingleBillDTO singleBillDto;
        try {
            singleBillDto = billResource.getSingleBillById(inputDto.getBillReference(), request);
            if (StringUtils.isBlank(singleBillDto.getStatus())
                    || StringUtils.isBlank(singleBillDto.getFileReference())
                    || !StringUtils.equals(singleBillDto.getStatus(), PARAM_STATUS_IN_REVIEW)) {
                return ErrorCodeEnum.ERR_30007.setErrorCode(new Result());
            }

            ReceivableCsvImportDTO csvImportDTO = csvBusiness.getCsvImportById(singleBillDto.getFileReference(), request);
            JSONObject csvBills = new JSONObject(csvImportDTO.getBillReferences());
            if (StringUtils.isBlank(csvImportDTO.getStatus())
                    || !csvBills.has(singleBillDto.getBillReference())) {
                return ErrorCodeEnum.ERR_30007.setErrorCode(new Result());
            }

            singleBillDto.setStatus(PARAM_STATUS_SUBMITTED_TO_BANK);
            singleBillDto.setUploadedDocuments(inputDto.getUploadedDocuments());
            singleBillDto.setUpdatedOn(getCurrentDateTimeUTF());
            singleBillDto = billResource.updateSingleBill(singleBillDto, request);

            csvBills.put(singleBillDto.getBillReference(), singleBillDto.getStatus());
            csvImportDTO.setBillReferences(String.valueOf(csvBills));
            csvBusiness.updateCsvImport(csvImportDTO, request);
        } catch (Exception e) {
            return ErrorCodeEnum.ERR_30022.setErrorCode(new Result());
        }
        return JSONToResult.convert(String.valueOf(new JSONObject(singleBillDto)));
    }

    @Override
    public Result updateCsvImport(DataControllerRequest request) {
        String fileReference = request.getParameter(PARAM_FILE_REFERENCE);
        if (StringUtils.isBlank(fileReference)) {
            return ErrorCodeEnum.ERR_30004.setErrorCode(new Result());
        }

        ReceivableCsvImportDTO csvImportDTO;
        try {
            csvImportDTO = csvBusiness.getCsvImportById(fileReference, request);
            if (StringUtils.isBlank(csvImportDTO.getStatus())
                    || !StringUtils.equals(csvImportDTO.getStatus(), PARAM_STATUS_IN_REVIEW)) {
                return ErrorCodeEnum.ERR_30021.setErrorCode(new Result());
            }
            JSONObject csvBills = new JSONObject(csvImportDTO.getBillReferences());
            Iterator<String> importedBills = csvBills.keys();
            while (importedBills.hasNext()) {
                if (!Objects.equals(csvBills.getString(String.valueOf(importedBills.next())), PARAM_STATUS_SUBMITTED_TO_BANK)) {
                    return ErrorCodeEnum.ERR_30020.setErrorCode(new Result());
                }
            }
            csvImportDTO.setStatus(PARAM_STATUS_SUBMITTED);
            csvImportDTO.setUpdatedOn(getCurrentDateTimeUTF());
            csvImportDTO = csvBusiness.updateCsvImport(csvImportDTO, request);
        } catch (Exception e) {
            return ErrorCodeEnum.ERR_30022.setErrorCode(new Result());
        }

        Result result = JSONToResult.convert(String.valueOf(new JSONObject(csvImportDTO)));
        TradeSupplyFinanceCommonUtils.setAlertDataInResult(result, TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_IMPORTED_BILLS_SUBMITTED, csvImportDTO.getFileReference());
        return result;
    }

    @Override
    public Result getCsvImportById(DataControllerRequest request) {
        String fileReference = request.getParameter(PARAM_FILE_REFERENCE);
        if (StringUtils.isBlank(fileReference)) {
            return ErrorCodeEnum.ERR_30004.setErrorCode(new Result());
        }
        ReceivableCsvImportDTO responseDto = csvBusiness.getCsvImportById(fileReference, request);
        return JSONToResult.convert(new JSONObject(responseDto).toString());
    }

    @Override
    public Result getCsvImports(TsfFilterDTO filterDto, DataControllerRequest request) {
        List<ReceivableCsvImportDTO> singleBills = csvBusiness.getCsvImports(request);
        filterDto.set_removeByParam("status");
        Set<String> set = new HashSet<>();
        set.add(PARAM_STATUS_DELETED);
        filterDto.set_removeByValue(set);
        singleBills = filterDto.filter(singleBills);
        return JSONToResult.convert((new JSONObject()).put("CsvImports", singleBills).toString());
    }

}