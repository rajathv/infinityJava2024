/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.javaservices;

import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.MWConstants;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;

/**
 * @author k.meiyazhagan
 */
public class DownloadTradeDocumentOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(DownloadTradeDocumentOperation.class);

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        Result result;
        try {
            String fileId = request.getParameter(PARAM_FILE_ID);
            String fileName = null;
            String fileType = null;
            String fileContents = null;
            HashMap<String, String> dataMapFilter = new HashMap<>();

            if (CommonUtils.isDMSIntegrationEnabled()) {
                dataMapFilter.put("documentId", fileId);
                result = HelperMethods.callApi(request, dataMapFilter, HelperMethods.getHeaders(request), URLConstants.DOCUMENT_STORAGE_DOWNLOAD);
                fileContents = result.getParamValueByName("documentContent");
            } else {
                dataMapFilter.put(DBPUtilitiesConstants.FILTER, "paymentFileID" + DBPUtilitiesConstants.EQUAL + fileId);
                result = HelperMethods.callApi(request, dataMapFilter, HelperMethods.getHeaders(request), URLConstants.PAYMENT_FILES_GET);
                if (!HelperMethods.hasRecords(result)) {
                    return ErrorCodeEnum.ERR_30012.setErrorCode(new Result());
                }
                if (!result.getAllDatasets().isEmpty() && result.getAllDatasets().get(0).getRecord(0) != null) {
                    Record recordData = result.getAllDatasets().get(0).getRecord(0);
                    fileName = recordData.getParamValueByName("paymentFileName");
                    fileType = recordData.getParamValueByName("paymentFileType");
                    fileContents = recordData.getParamValueByName("paymentFileContents");
                }
            }

            Map<String, String> customHeaders = new HashMap<>();
            customHeaders.put(HttpHeaders.CONTENT_TYPE, fileType);
            customHeaders.put(HTTP_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS, HTTP_HEADER_CONTENT_DISPOSITION);
            customHeaders.put(HTTP_HEADER_CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            response.getHeaders().putAll(customHeaders);

            byte[] bytes = Base64.getMimeDecoder().decode(fileContents);
            response.setAttribute(MWConstants.CHUNKED_RESULTS_IN_JSON, new BufferedHttpEntity(new ByteArrayEntity(bytes)));
            response.setStatusCode(HttpStatus.SC_OK);

            return result;
        } catch (Exception e) {
            LOG.error(e);
            return ErrorCodeEnum.ERR_30012.setErrorCode(new Result());
        }
    }
}
