/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.resource.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.InitiateDownloadExportLCAmendmentsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.constants.Constants;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.resource.api.GenerateExportLCAmendmentsPdfResource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.PREFIX_EXPORT_AMENDMENT;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.generateTradeFinanceFileID;

public class GenerateExportLCAmendmentsPdfResourceImpl implements GenerateExportLCAmendmentsPdfResource {
    private static final Logger LOG = LogManager.getLogger(GenerateExportLCAmendmentsPdfResourceImpl.class);

    @Override
    public Result initiateExportLCAmendmetspdf(Object[] inputArray, DataControllerRequest request) {
        byte[] bytes = new byte[0];
        Result result = new Result();
        InitiateDownloadExportLCAmendmentsBusinessDelegate pdfBusinessDelegate = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(InitiateDownloadExportLCAmendmentsBusinessDelegate.class);

        @SuppressWarnings("unchecked")
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        String srmsRequestId = inputParams.get("amendmentReferenceNo").toString();
        if (StringUtils.isBlank(srmsRequestId)) {
            LOG.error("SRMS RequestId is missing");
            return ErrorCodeEnum.ERRTF_29055.setErrorCode(new Result(), Constants.PROVIDE_MANDATORY_FIELDS);
        }

        try {
            if (StringUtils.isNotBlank(srmsRequestId)) {
                //Calling BusinessDelegate Class
                bytes = pdfBusinessDelegate.generateExportLCAmendmentsPdf(srmsRequestId, request);
            }
            if (bytes == null) {
                LOG.error("Error while generating pdf");
                return ErrorCodeEnum.ERRTF_29056.setErrorCode(result);
            }
            String fileId = generateTradeFinanceFileID(PREFIX_EXPORT_AMENDMENT);
            MemoryManager.saveIntoCache(fileId, Base64.encodeBase64String(bytes), 120);
            result.addParam("fileId", fileId);
            return result;
        } catch (Exception e) {
            LOG.error("Error while generating the trade finance file", e);
        }
        return ErrorCodeEnum.ERRTF_29056.setErrorCode(new Result());
    }

}
