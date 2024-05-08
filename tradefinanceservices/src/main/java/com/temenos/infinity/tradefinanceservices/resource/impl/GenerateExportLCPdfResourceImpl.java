/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.resource.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.GetExportLetterOfCreditsByIdBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.InitiateDownloadExportLCBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.constants.Constants;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.dto.ExportLOCDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.GenerateExportLCPdfResource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.PREFIX_EXPORT_LOC;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.generateTradeFinanceFileID;

public class GenerateExportLCPdfResourceImpl implements GenerateExportLCPdfResource {
    private static final Logger LOG = LogManager.getLogger(LetterOfCreditDrawingsResourceImpl.class);

    public Result initiateExportLCpdf(Object[] inputArray, DataControllerRequest request) {
        byte[] bytes = new byte[0];
        Result result = new Result();
        GetExportLetterOfCreditsByIdBusinessDelegate exportBusinessDelegate = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(GetExportLetterOfCreditsByIdBusinessDelegate.class);
        InitiateDownloadExportLCBusinessDelegate pdfBusinessDelegate = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(InitiateDownloadExportLCBusinessDelegate.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        String srmsRequestId = inputParams.get("exportLCId").toString();
        if (StringUtils.isBlank(srmsRequestId)) {
            LOG.error("SRMS RequestId is missing");
            return ErrorCodeEnum.ERRTF_29055.setErrorCode(new Result(), Constants.PROVIDE_MANDATORY_FIELDS);
        }

        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        String customerId = CustomerSession.getCustomerId(customer);
        try {
            if (StringUtils.isNotBlank(srmsRequestId)) {
                ExportLOCDTO exportdto = exportBusinessDelegate.getExportLetterOfCreditById(srmsRequestId, request);
                if (!StringUtils.isNotBlank(customerId) && customerId.equalsIgnoreCase(exportdto.getCustomerId())) {
                    LOG.info("Failed validating customerId.");
                    return ErrorCodeEnum.ERRTF_29070.setErrorCode(new Result());
                }
                if (StringUtils.isBlank(exportdto.getApplicant())) {
                    LOG.error("Requested Record Not Found");
                    return ErrorCodeEnum.ERRTF_29057.setErrorCode(result);
                }
                bytes = pdfBusinessDelegate.getRecordPDFAsBytes(exportdto, request);
            }
            String fileId = generateTradeFinanceFileID(PREFIX_EXPORT_LOC);
            MemoryManager.saveIntoCache(fileId, Base64.encodeBase64String(bytes), 120);
            result.addParam("fileId", fileId);
            return result;
        } catch (Exception e) {
            LOG.error("Error while generating the trade finance file", e);
        }
        return ErrorCodeEnum.ERRTF_29056.setErrorCode(new Result());
    }

}
