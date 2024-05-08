/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.javaservices;

import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.MWConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum;
import com.temenos.infinity.tradesupplyfinance.constants.GeneratedFileDetailsEnum;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;

/**
 * @author k.meiyazhagan
 */
public class DownloadGeneratedFileOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(DownloadGeneratedFileOperation.class);

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        Result result = new Result();

        @SuppressWarnings("unchecked")
        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
        String fileId = (String) inputParams.get(PARAM_FILE_ID);
        if (StringUtils.isBlank(fileId)) {
            LOG.error("Mandatory inputs missing in payload");
            return ErrorCodeEnum.ERR_30004.setErrorCode(result);
        }

        String fileDetails = (String) MemoryManager.getFromCache(fileId);
        if (StringUtils.isBlank(fileDetails)) {
            LOG.error("File not found in cache");
            return ErrorCodeEnum.ERR_30010.setErrorCode(result);
        }
        MemoryManager.removeFromCache(fileId);
        byte[] bytes = Base64.decodeBase64(fileDetails);

        try {
            response.getHeaders().putAll(getCustomHeaders(fileId.substring(0, 4)));
            response.setAttribute(MWConstants.CHUNKED_RESULTS_IN_JSON, new BufferedHttpEntity(new ByteArrayEntity(bytes)));
            response.setStatusCode(HttpStatus.SC_OK);
            return result;
        } catch (Exception e) {
            LOG.error("Error while downloading the generated file", e);
            return ErrorCodeEnum.ERR_30009.setErrorCode(new Result());
        }
    }

    private Map<String, String> getCustomHeaders(String prefix) {
        GeneratedFileDetailsEnum file = GeneratedFileDetailsEnum.valueOf(prefix);
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put(HttpHeaders.CONTENT_TYPE, file.getContentType());
        customHeaders.put(HTTP_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS, HTTP_HEADER_CONTENT_DISPOSITION);
        customHeaders.put(HTTP_HEADER_CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"");
        return customHeaders;
    }
}
