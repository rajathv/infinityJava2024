package com.temenos.infinity.tradefinanceservices.javaservice;

import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.MWConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;

import java.util.HashMap;
import java.util.Map;

import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.HTTP_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS;
import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.HTTP_HEADER_CONTENT_DISPOSITION;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.getAttachmentName;

public class DownloadGeneratedAcknowledgementOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(DownloadGeneratedAcknowledgementOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) {
        Result result = new Result();
        try {
            Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
            String fileId = (String) inputParams.get("fileId");
            if (StringUtils.isBlank(fileId)) {
                LOG.error("FileId is missing in the payload which is mandatory to fetch the file details");
                return ErrorCodeEnum.ERRTF_29058.setErrorCode(result);
            }

            String fileDetails = (String) MemoryManager.getFromCache(fileId);
            if (fileDetails == null) {
                LOG.error("SECURITY EXCEPTION - UNAUTHORIZED ACCESS");
                response.setAttribute("dbpErrMsg", "SECURITY EXCEPTION - UNAUTHORIZED ACCESS");
                response.setAttribute("dbpErrCode", "12403");
                return new Result();
            }
            MemoryManager.removeFromCache(fileId);
            byte[] bytes = Base64.decodeBase64(fileDetails);
            String attachmentName = getAttachmentName(fileId.substring(0, 4)) + " Acknowledgement.pdf";

            // custom header map
            Map<String, String> customHeaders = new HashMap<>();
            customHeaders.put(HttpHeaders.CONTENT_TYPE, "application/pdf");
            customHeaders.put(HTTP_HEADER_ACCESS_CONTROL_EXPOSE_HEADERS, HTTP_HEADER_CONTENT_DISPOSITION);
            customHeaders.put(HTTP_HEADER_CONTENT_DISPOSITION, "attachment; filename=\"" + attachmentName + "\"");

            try {
                response.getHeaders().putAll(customHeaders);
                response.setAttribute(MWConstants.CHUNKED_RESULTS_IN_JSON, new BufferedHttpEntity(new ByteArrayEntity(bytes)));
                response.setStatusCode(HttpStatus.SC_OK);
                return result;
            } catch (Exception e) {
                LOG.error("Error while downloading the " + attachmentName + " List", e);
            }
        } catch (Exception e) {
            LOG.error("Error occurred while invoking download Trade Finance pdf: ", e);
        }
        return ErrorCodeEnum.ERRTF_29054.setErrorCode(result);
    }

}