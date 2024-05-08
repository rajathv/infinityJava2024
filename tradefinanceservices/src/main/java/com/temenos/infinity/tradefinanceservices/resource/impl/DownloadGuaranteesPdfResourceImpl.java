/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.resource.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.infinity.dbx.temenos.transactions.TransactionConstants;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.DownloadGuaranteesPdfBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.DownloadGuaranteesSwiftPdfBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.GuaranteesBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.dto.GuranteesDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.DownloadGuaranteesPdfResource;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.PARAM_STATUS_APPROVED;
import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.PREFIX_GUARANTEE_LOC;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.generateTradeFinanceFileID;

public class DownloadGuaranteesPdfResourceImpl implements DownloadGuaranteesPdfResource {
    private static final Logger LOG = LogManager.getLogger(DownloadGuaranteesPdfResourceImpl.class);

    @Override
    public Result initiateDownloadGuaranteesPdf(Object[] inputArray, DataControllerRequest request) {
        byte[] bytes = new byte[0];
        Result result = new Result();
        DownloadGuaranteesPdfBusinessDelegate pdfBusinessDelegate = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(DownloadGuaranteesPdfBusinessDelegate.class);

        DownloadGuaranteesSwiftPdfBusinessDelegate swiftBusinessDelegate = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(DownloadGuaranteesSwiftPdfBusinessDelegate.class);

        GuaranteesBusinessDelegate guaranteesBusinessDelegate = DBPAPIAbstractFactoryImpl
                .getBusinessDelegate(GuaranteesBusinessDelegate.class);

        @SuppressWarnings("unchecked")
        //Check for guaranteesId
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        String guaranteesSRMSId = inputParams.get("guaranteesSRMSId").toString();
        if (StringUtils.isBlank(guaranteesSRMSId)) {
            LOG.error("guaranteesSRMSId RequestId is missing");
            return ErrorCodeEnum.ERRTF_29049.setErrorCode(result);
        }

        GuranteesDTO guaranteesDTO = guaranteesBusinessDelegate.getGuaranteesById(guaranteesSRMSId, request);
        if (StringUtils.isNotBlank(guaranteesDTO.getDbpErrMsg())) {
            JSONObject responseObj = new JSONObject(guaranteesDTO);
            result = JSONToResult.convert(responseObj.toString());
            return result;
        }

        try {
            if (StringUtils.isNotBlank(guaranteesSRMSId)) {

                //Retrieving Swift Enable tag from Bundle Configurations
                JSONObject bundleConfig = TemenosUtils.getBundleConfigurations(TransactionConstants.DBP_BUNDLE,
                        "SWIFT_TAG_ENABLE", request);
                JSONArray swift = bundleConfig.getJSONArray("configurations");
                JSONObject swiftEnable = new JSONObject(swift.getJSONObject(0).getString("config_value"));

                //Checking Swift Enable tag and calling business Delegate Class Accordingly
                if (swiftEnable.get("Swift Enable").equals("True") && guaranteesDTO.getStatus().equals(PARAM_STATUS_APPROVED)) {
                    bytes = swiftBusinessDelegate.generateGuaranteesSwiftPdf(guaranteesDTO, request);
                } else {
                    bytes = pdfBusinessDelegate.generateGuaranteesPdf(guaranteesDTO, request);
                }
            }
            if (bytes == null) {
                LOG.error("Error while generating pdf");
                return ErrorCodeEnum.ERRTF_29056.setErrorCode(result);
            }
            String fileId = generateTradeFinanceFileID(PREFIX_GUARANTEE_LOC);
            MemoryManager.saveIntoCache(fileId, Base64.encodeBase64String(bytes), 120);
            result.addParam("fileId", fileId);
            return result;
        } catch (Exception e) {
            LOG.error("Error while generating the trade finance file", e);
        }
        return ErrorCodeEnum.ERRTF_29056.setErrorCode(new Result());
    }
}
