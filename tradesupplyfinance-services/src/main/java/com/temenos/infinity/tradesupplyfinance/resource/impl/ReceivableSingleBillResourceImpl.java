/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.resource.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradesupplyfinance.dto.TsfFilterDTO;
import com.temenos.infinity.tradesupplyfinance.businessdelegate.api.ReceivableSingleBillBusinessDelegate;
import com.temenos.infinity.tradesupplyfinance.config.TradeSupplyFinanceAlerts;
import com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum;
import com.temenos.infinity.tradesupplyfinance.dto.ReceivableSingleBillDTO;
import com.temenos.infinity.tradesupplyfinance.resource.api.ReceivableSingleBillResource;
import com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceCommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;
import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceStatus.*;
import static com.temenos.infinity.tradesupplyfinance.utils.TradeSupplyFinanceCommonUtils.*;

/**
 * @author k.meiyazhagan
 */
public class ReceivableSingleBillResourceImpl implements ReceivableSingleBillResource {

    private static final Logger LOG = Logger.getLogger(ReceivableSingleBillResourceImpl.class);
    private final ReceivableSingleBillBusinessDelegate requestBusiness = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ReceivableSingleBillBusinessDelegate.class);

    @Override
    public Result saveSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        inputDto.setUpdatedOn(getCurrentDateTimeUTF());
        inputDto.setStatus(PARAM_STATUS_DRAFT);
        inputDto.setOrigin(PARAM_ORIGIN_SINGLE);

        ReceivableSingleBillDTO responseDTO;
        if (StringUtils.isBlank(inputDto.getBillReference())) {
            inputDto.setCreatedOn(inputDto.getUpdatedOn());
            responseDTO = requestBusiness.createSingleBill(inputDto, request);
        } else {
            responseDTO = requestBusiness.getSingleBillById(inputDto.getBillReference(), request);
            if (StringUtils.isBlank(responseDTO.getBillReference())
                    || !StringUtils.equals(responseDTO.getStatus(), PARAM_STATUS_DRAFT)) {
                return ErrorCodeEnum.ERR_30008.setErrorCode(new Result());
            }
            inputDto.setCreatedOn(responseDTO.getCreatedOn());
            responseDTO = requestBusiness.updateSingleBill(inputDto, request);
        }

        return JSONToResult.convert(String.valueOf(new JSONObject(responseDTO)));
    }

    @Override
    public Result deleteSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        if (StringUtils.isBlank(inputDto.getBillReference())) {
            return ErrorCodeEnum.ERR_30004.setErrorCode(new Result());
        }

        ReceivableSingleBillDTO singleBillDto = requestBusiness.getSingleBillById(inputDto.getBillReference(), request);
        if (StringUtils.isBlank(singleBillDto.getStatus())
                || !Arrays.asList(PARAM_STATUS_DRAFT, PARAM_STATUS_IN_REVIEW).contains(singleBillDto.getStatus())) {
            return ErrorCodeEnum.ERR_30008.setErrorCode(new Result());
        }
        singleBillDto.setUpdatedOn(getCurrentDateTimeUTF());
        singleBillDto.setStatus(PARAM_STATUS_DELETED);
        singleBillDto = requestBusiness.updateSingleBill(singleBillDto, request);
        Result result = JSONToResult.convert(String.valueOf(new JSONObject(singleBillDto)));
        TradeSupplyFinanceCommonUtils.setAlertDataInResult(result, TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_DELETED, singleBillDto.getBillReference());
        return result;
    }

    @Override
    public Result createSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        TradeSupplyFinanceAlerts alertToPush = null;
        // Mandatory fields check
        if (StringUtils.isBlank(inputDto.getBillNumber())
                || StringUtils.isBlank(inputDto.getBillDate())
                || StringUtils.isBlank(inputDto.getBillType())
                || StringUtils.isBlank(inputDto.getDueDate())
                || StringUtils.isBlank(inputDto.getCurrency())
                || StringUtils.isBlank(inputDto.getAmount())
                || StringUtils.isBlank(inputDto.getPaymentTerms())
                || StringUtils.isBlank(inputDto.getBuyerName())
                || StringUtils.isBlank(inputDto.getBuyerAddress())
                || StringUtils.isBlank(inputDto.getGoodsDescription())
                || StringUtils.isBlank(inputDto.getShipmentDate())
                || StringUtils.isBlank(inputDto.getShipmentTrackingDetails())
                || StringUtils.isBlank(inputDto.getCountryOfOrigin())
                || StringUtils.isBlank(inputDto.getCountryOfDestination())
                || (StringUtils.isBlank(inputDto.getBuyerId()) && (StringUtils.isBlank(inputDto.getBuyerName()) || StringUtils.isBlank(inputDto.getBuyerAddress())))) {
            return ErrorCodeEnum.ERR_30004.setErrorCode(new Result());
        }

        inputDto.setCreatedOn(getCurrentDateTimeUTF());
        inputDto.setUpdatedOn(inputDto.getCreatedOn());
        inputDto.setStatus(StringUtils.isBlank(inputDto.getFileReference()) ? PARAM_STATUS_SUBMITTED_TO_BANK : PARAM_STATUS_IN_REVIEW);
        inputDto.setOrigin(PARAM_ORIGIN_SINGLE);
        if (StringUtils.isBlank(inputDto.getFileReference())) {
            alertToPush = TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_SUBMITTED;
        }

        ReceivableSingleBillDTO responseDTO;
        if (StringUtils.isBlank(inputDto.getBillReference())) {
            responseDTO = requestBusiness.createSingleBill(inputDto, request);
        } else {
            responseDTO = requestBusiness.getSingleBillById(inputDto.getBillReference(), request);
            if (StringUtils.isBlank(responseDTO.getBillReference())
                    || !StringUtils.equals(responseDTO.getStatus(), PARAM_STATUS_DRAFT)) {
                return ErrorCodeEnum.ERR_30008.setErrorCode(new Result());
            }
            responseDTO = requestBusiness.updateSingleBill(inputDto, request);
        }

        Result result = JSONToResult.convert(String.valueOf(new JSONObject(responseDTO)));
        TradeSupplyFinanceCommonUtils.setAlertDataInResult(result, alertToPush, responseDTO.getBillReference());
        return result;
    }

    @Override
    public Result reviseSingleBill(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        ReceivableSingleBillDTO singleBillDto = requestBusiness.getSingleBillById(inputDto.getBillReference(), request);

        try {
            if (StringUtils.equals(singleBillDto.getStatus(), inputDto.getStatus()) ||
                    !StringUtils.equals(PARAM_STATUS_RETURNED_BY_BANK, singleBillDto.getStatus())) {
                return ErrorCodeEnum.ERR_30007.setErrorCode(new Result());
            }

            JSONArray returnedHistory = new JSONArray(singleBillDto.getReturnedHistory());
            int returnCount = returnedHistory.length();
            if (returnCount >= PARAM_MAX_RETURNS) {
                return ErrorCodeEnum.ERR_30005.setErrorCode(new Result());
            }
            returnedHistory.put(returnCount - 1, returnedHistory.getJSONObject(returnCount - 1).put("messageToBank", inputDto.getMessageToBank()));
            inputDto.setReturnedHistory(String.valueOf(returnedHistory));
            singleBillDto = JSONUtils.parse(mergeJSONObjects(new JSONObject(singleBillDto), new JSONObject(inputDto)).toString(), ReceivableSingleBillDTO.class);
        } catch (IOException e) {
            LOG.error("Failed to merge status");
        }
        singleBillDto.setUpdatedOn(getCurrentDateTimeUTF());
        singleBillDto.setStatus(PARAM_STATUS_SUBMITTED_TO_BANK);
        singleBillDto = requestBusiness.updateSingleBill(singleBillDto, request);
        Result result = JSONToResult.convert(String.valueOf(new JSONObject(singleBillDto)));
        TradeSupplyFinanceCommonUtils.setAlertDataInResult(result, TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_REVISION_SUBMITTED, singleBillDto.getBillReference());
        return result;
    }

    @Override
    public Result updateSingleBillByBank(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        TradeSupplyFinanceAlerts alertToPush = null;
        ReceivableSingleBillDTO singleBillDto = requestBusiness.getSingleBillById(inputDto.getBillReference(), request);
        singleBillDto.setUpdatedOn(getCurrentDateTimeUTF());

        if (StringUtils.equals(singleBillDto.getStatus(), inputDto.getStatus()) ||
                Arrays.asList(PARAM_STATUS_REJECTED, PARAM_STATUS_CANCELLED, PARAM_STATUS_IN_REVIEW).contains(singleBillDto.getStatus())) {
            return ErrorCodeEnum.ERR_30007.setErrorCode(new Result());
        }

        switch (inputDto.getStatus()) {
            case PARAM_STATUS_PROCESSING_BY_BANK:
                break;
            case PARAM_STATUS_APPROVED:
                alertToPush = TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_APPROVED;
                break;
            case PARAM_STATUS_SETTLED:
                alertToPush = TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_SETTLED;
                break;
            case PARAM_STATUS_FINANCED:
                alertToPush = TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_FINANCED;
                break;
            case PARAM_STATUS_NEED_FINANCE:
                alertToPush = TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_FINANCE_REQUESTED;
                break;
            case PARAM_STATUS_RETURNED_BY_BANK:
                singleBillDto.setReasonForReturn(inputDto.getReasonForReturn());
                JSONArray returnedHistory = StringUtils.isNotBlank(singleBillDto.getReturnedHistory()) ? new JSONArray(singleBillDto.getReturnedHistory()) : new JSONArray();
                if (returnedHistory.length() >= PARAM_MAX_RETURNS) {
                    return ErrorCodeEnum.ERR_30005.setErrorCode(new Result());
                }
                returnedHistory.put(new JSONObject()
                        .put("returnNo", returnedHistory.length() + 1)
                        .put("reasonForReturn", inputDto.getReasonForReturn())
                        .put("returnedTimeStamp", singleBillDto.getUpdatedOn())
                        .put("corporateUserName", fetchCustomerNameFromSession(request)));
                singleBillDto.setReturnedHistory(String.valueOf(returnedHistory));
                alertToPush = TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_RETURNED_BY_BANK;
                break;
            case PARAM_STATUS_REJECTED:
                singleBillDto.setReasonForRejection(inputDto.getReasonForRejection());
                alertToPush = StringUtils.equals(singleBillDto.getCancellationStatus(), PARAM_STATUS_REQUESTED)
                        ? TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_REJECTED : TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_CANCELLATION_REJECTED;
                break;
            case PARAM_STATUS_CANCELLED:
                singleBillDto.setCancellationStatus(PARAM_STATUS_APPROVED);
                alertToPush = TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_CANCELLATION_APPROVED;
                break;
            default:
                return ErrorCodeEnum.ERR_30006.setErrorCode(new Result());
        }

        singleBillDto.setStatus(inputDto.getStatus());
        singleBillDto = requestBusiness.updateSingleBill(singleBillDto, request);
        Result result = JSONToResult.convert(String.valueOf(new JSONObject(singleBillDto)));
        TradeSupplyFinanceCommonUtils.setAlertDataInResult(result, alertToPush, singleBillDto.getBillReference());
        return result;
    }

    @Override
    public Result requestSingleBillCancellation(ReceivableSingleBillDTO inputDto, DataControllerRequest request) {
        if (StringUtils.isBlank(inputDto.getBillReference())
                || StringUtils.isBlank(inputDto.getReasonForCancellation())) {
            return ErrorCodeEnum.ERR_30004.setErrorCode(new Result());
        }

        ReceivableSingleBillDTO singleBillDto = requestBusiness.getSingleBillById(inputDto.getBillReference(), request);
        if (StringUtils.isBlank(singleBillDto.getStatus())
                || !StringUtils.equals(singleBillDto.getStatus(), PARAM_STATUS_APPROVED)) {
            return ErrorCodeEnum.ERR_30007.setErrorCode(new Result());
        }

        singleBillDto.setCancellationDocuments(inputDto.getCancellationDocuments());
        singleBillDto.setStatus(PARAM_STATUS_SUBMITTED_TO_BANK);
        singleBillDto.setCancellationStatus(PARAM_STATUS_REQUESTED);
        singleBillDto.setReasonForCancellation(inputDto.getReasonForCancellation());
        singleBillDto.setUpdatedOn(getCurrentDateTimeUTF());
        singleBillDto = requestBusiness.updateSingleBill(singleBillDto, request);
        Result result = JSONToResult.convert(String.valueOf(new JSONObject(singleBillDto)));
        TradeSupplyFinanceCommonUtils.setAlertDataInResult(result, TradeSupplyFinanceAlerts.TSF_RECEIVABLE_SINGLE_BILL_CANCELLATION_SUBMITTED, singleBillDto.getBillReference());
        return result;
    }

    @Override
    public Result getSingleBillById(DataControllerRequest request) {
        String billReference = request.getParameter("billReference");
        if (StringUtils.isBlank(billReference)) {
            return ErrorCodeEnum.ERR_30004.setErrorCode(new Result());
        }
        ReceivableSingleBillDTO responseDto = requestBusiness.getSingleBillById(billReference, request);
        return JSONToResult.convert(new JSONObject(responseDto).toString());
    }

    @Override
    public Result getSingleBills(TsfFilterDTO filterDto, DataControllerRequest request) {
        List<ReceivableSingleBillDTO> singleBills = requestBusiness.getSingleBills(request);
        filterDto.set_removeByParam("status");
        Set<String> set = new HashSet<>();
        set.add(PARAM_STATUS_DELETED);
        if (!filterDto.get_filterByParam().contains(PARAM_FILE_REFERENCE))
            set.add(PARAM_STATUS_IN_REVIEW);
        filterDto.set_removeByValue(set);
        singleBills = filterDto.filter(singleBills);
        return JSONToResult.convert((new JSONObject()).put("ReceivableBills", singleBills).toString());
    }
}
