/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.resource.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.tradefinanceservices.businessdelegate.api.TradeFinanceDocumentsBusinessDelegate;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.resource.api.TradeFinanceDocumentsResource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Pattern;

import static com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants.PREFIX_DOCUMENT;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.fetchCustomerFromSession;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.generateTradeFinanceFileID;

public class TradeFinanceDocumentsResourceImpl implements TradeFinanceDocumentsResource {

    private static final int INDEX_FILENAME = 0;
    private static final int INDEX_FILETYPE = 1;
    private static final int INDEX_PRODUCT = 1;
    private static final int INDEX_FILECONTENTS = 2;
    private static final int MAX_ALLOWED_FILE_SIZE = 25;
    private static final Pattern validFileContent = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");

    public static final ArrayList<String> validFileExtensions = new ArrayList<>(Arrays.asList("application/pdf", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/x-zip-compressed", "image/bmp", "image/jpeg"));
    private static final TradeFinanceDocumentsBusinessDelegate orderBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(TradeFinanceDocumentsBusinessDelegate.class);
    private static final Logger LOG = LogManager.getLogger(TradeFinanceDocumentsResourceImpl.class);

    @Override
    public Result uploadDocument(DataControllerRequest request) {
        Result result = new Result();
        boolean dmsIntegrationEnabled = CommonUtils.isDMSIntegrationEnabled();
        String referenceId = generateTradeFinanceFileID(PREFIX_DOCUMENT);
        ArrayList<String> failedUploads = new ArrayList<>();
        ArrayList<String> successfulUploads = new ArrayList<>();

        String customerId = fetchCustomerFromSession(request);
        if (StringUtils.isBlank(customerId))
            return ErrorCodeEnum.ERR_26014.setErrorCode(result);

        String inputDocumentsField = request.getParameter("uploadedattachments");
        if (StringUtils.isBlank(inputDocumentsField)) {
            LOG.debug("Mandatory fields are missing");
            return ErrorCodeEnum.ERRTF_29049.setErrorCode(result);
        }

        String[] documentsToUpload = inputDocumentsField.split(",");
        if (ArrayUtils.isEmpty(documentsToUpload)) {
            LOG.error("Invalid Input Payload");
            return ErrorCodeEnum.ERR_12002.setErrorCode(result);
        }

        try {
            for (String document : documentsToUpload) {
                String[] documentDetails = document.split("~");
                if (documentDetails.length != 3) {
                    LOG.error("File input is incorrect for performing upload operation");
                    failedUploads.add(documentDetails[INDEX_FILENAME]);
                    continue;
                }

                String uploadedFileName = documentDetails[INDEX_FILENAME];
                String fileExtension = documentDetails[INDEX_FILETYPE];
                String fileContents = documentDetails[INDEX_FILECONTENTS];
                if (!isFileSizeValid(fileContents) ||  !validateFileExtension(fileExtension, uploadedFileName)) {
                    failedUploads.add(uploadedFileName);
                    continue;
                }
                result = dmsIntegrationEnabled ? orderBusinessDelegate.uploadDocumentDMS(referenceId, customerId, uploadedFileName,
                        fileContents, request) : orderBusinessDelegate.uploadDocumentDBXDB(referenceId, customerId, uploadedFileName,
                        fileExtension, fileContents, request);
                if (result.hasParamByName("status") && Boolean.parseBoolean(result.getParamValueByName("status"))) {
                    successfulUploads.add(uploadedFileName);
                } else {
                    failedUploads.add(uploadedFileName);
                }
            }

        } catch (Exception e) {
            LOG.debug("Failed to upload document. Error: " + e);
            return ErrorCodeEnum.ERRTF_29083.setErrorCode(result);
        } finally {
            result.addParam("failedUploads", StringUtils.join(failedUploads, ","));
            result.addParam("successfulUploads", StringUtils.join(successfulUploads, ","));
            if (successfulUploads.size() != 0)
                result.addParam("documentReference", referenceId);
        }
        return result;
    }

    @Override
    public Result fetchDocument(DataControllerRequest request) {
        Result result = new Result();
        String customerId = fetchCustomerFromSession(request);
        if (StringUtils.isBlank(customerId)) {
            LOG.debug("Failed to fetch Customer ID");
            return ErrorCodeEnum.ERR_26014.setErrorCode(result);
        }

        String referenceId = request.getParameter("documentReference");
        if (StringUtils.isBlank(referenceId)) {
            LOG.debug("Mandatory fields are missing");
            return ErrorCodeEnum.ERRTF_29049.setErrorCode(result);
        }

        try {
            if (CommonUtils.isDMSIntegrationEnabled()) {
                result = orderBusinessDelegate.fetchDocumentDMS(referenceId, request);
            } else {
                result = orderBusinessDelegate.fetchDocumentDBXDB(customerId, referenceId, request);
            }
            return result;
        } catch (Exception e) {
            LOG.debug("Failed to fetch document. Error: " + e);
            return ErrorCodeEnum.ERRTF_29082.setErrorCode(result);
        }
    }

    @Override
    public Result deleteDocument(DataControllerRequest request) {
        Result result = new Result();
        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        String customerId = CustomerSession.getCustomerId(customer);
        if (StringUtils.isBlank(customerId)) {
            LOG.debug("Failed to fetch Customer ID");
            return ErrorCodeEnum.ERR_26014.setErrorCode(result);
        }

        String referenceId = request.getParameter("documentReference");
        if (StringUtils.isBlank(referenceId)) {
            LOG.debug("Mandatory fields are missing");
            return ErrorCodeEnum.ERRTF_29049.setErrorCode(result);
        }

        try {
            if (CommonUtils.isDMSIntegrationEnabled()) {
                result = orderBusinessDelegate.deleteDocumentDMS(referenceId, request);
            } else {
                result = orderBusinessDelegate.deleteDocumentDBXDB(customerId, referenceId, request);
            }
            return result;
        } catch (Exception e) {
            LOG.debug("Failed to delete document. Error: " + e);
            return ErrorCodeEnum.ERRTF_29076.setErrorCode(result);
        }
    }

    @Override
    public Result uploadSwiftsAdvices(DataControllerRequest request) {
        Result result = new Result();
        String customerId = fetchCustomerFromSession(request);
        if (StringUtils.isBlank(customerId))
            return ErrorCodeEnum.ERR_26014.setErrorCode(result);

        String[] swiftsAdvices = new String[]{request.getParameter("swiftMessages"), request.getParameter("paymentAdvices")};
        if (ArrayUtils.isEmpty(swiftsAdvices)) {
            LOG.debug("Mandatory fields are missing");
            return ErrorCodeEnum.ERRTF_29049.setErrorCode(result);
        }

        ArrayList<String> failedUploads = new ArrayList<>();
        ArrayList<String> successfulUploads = new ArrayList<>();
        try {
            for (int i = 0; i < swiftsAdvices.length; i++) {
                if (StringUtils.isBlank(swiftsAdvices[i])) {
                    continue;
                }
                String[] documentsToUpload = swiftsAdvices[i].split(",");
                for (String document : documentsToUpload) {
                    String[] documentDetails = document.split("~");
                    if (documentDetails.length != 3) {
                        LOG.error("File input is incorrect for performing upload operation");
                        failedUploads.add(documentDetails[INDEX_FILENAME]);
                        continue;
                    }

                    String uploadedFileName = documentDetails[INDEX_FILENAME];
                    String productName = documentDetails[INDEX_PRODUCT].toUpperCase();
                    String category = i == 0 ? "SM" : "PA";
                    String fileContents = documentDetails[INDEX_FILECONTENTS];
                    if (!Arrays.asList("LETTEROFCREDIT", "GUARANTEE", "COLLECTION").contains(productName)
                            || !validFileContent.matcher(fileContents).matches()) {
                        failedUploads.add(uploadedFileName);
                        continue;
                    }

                    Result uploadRes = orderBusinessDelegate.uploadDocumentDBXDB(productName + "_" + category, customerId, uploadedFileName, "text/plain", fileContents, request);
                    if (uploadRes.hasParamByName("status") && Boolean.parseBoolean(uploadRes.getParamValueByName("status"))) {
                        successfulUploads.add(productName + ": " + uploadedFileName);
                    } else {
                        failedUploads.add(productName + ": " + uploadedFileName);
                    }
                }
            }
        } catch (Exception e) {
            LOG.debug("Failed to upload document. Error: " + e);
            return ErrorCodeEnum.ERRTF_29083.setErrorCode(result);
        } finally {
            result.addParam("failedUploads", StringUtils.join(failedUploads, ","));
            result.addParam("successfulUploads", StringUtils.join(successfulUploads, ","));
        }
        return result;
    }

    @Override
    public Result fetchSwiftsAdvices(DataControllerRequest request) {
        Result result = new Result();
        String customerId = fetchCustomerFromSession(request);
        if (StringUtils.isBlank(customerId)) {
            LOG.debug("Failed to fetch Customer ID");
            return ErrorCodeEnum.ERR_26014.setErrorCode(result);
        }

        String orderId = request.getParameter("orderId").toUpperCase();
        String productName = request.getParameter("product").toUpperCase();
        if (!Arrays.asList("LETTEROFCREDIT", "GUARANTEE", "COLLECTION").contains(productName)) {
            LOG.debug("Mandatory fields are missing");
            return ErrorCodeEnum.ERRTF_29049.setErrorCode(result);
        }

        try {
            JSONObject response = new JSONObject();
            JSONArray swiftsAdvices = new JSONArray();
            for (Record record : orderBusinessDelegate.fetchDocumentDBXDB(customerId, productName + "_SM", request).getDatasetById("fileNames").getAllRecords()) {
                swiftsAdvices.put(new JSONObject()
                        .put("category", "SWIFT")
                        .put("product", productName)
                        .put("orderId", orderId)
                        .put("fileName", record.getParamValueByName("fileName"))
                        .put("fileId", record.getParamValueByName("fileID"))
                        .put("uploadedTimeStamp", record.getParamValueByName("uploadedTimeStamp")));
            }
            response.put("SwiftMessages", swiftsAdvices);

            swiftsAdvices = new JSONArray();
            for (Record record : orderBusinessDelegate.fetchDocumentDBXDB(customerId, productName + "_PA", request).getDatasetById("fileNames").getAllRecords()) {
                swiftsAdvices.put(new JSONObject()
                        .put("category", "ADVICE")
                        .put("product", productName)
                        .put("orderId", orderId)
                        .put("fileName", record.getParamValueByName("fileName"))
                        .put("fileId", record.getParamValueByName("fileID"))
                        .put("uploadedTimeStamp", record.getParamValueByName("uploadedTimeStamp")));
            }
            response.put("PaymentAdvices", swiftsAdvices);
            return JSONToResult.convert(String.valueOf(response));
        } catch (Exception e) {
            LOG.debug("Failed to fetch document. Error: " + e);
            return ErrorCodeEnum.ERRTF_29082.setErrorCode(result);
        }
    }

    private Boolean validateFileExtension(String fileExtension, String uploadedFileName) {
        Pattern regexFileName = Pattern.compile("^[-_() A-Za-z0-9]+\\.(pdf|xlsx|csv|jpeg|doc|docx|bmp|zip){0,250}$");
        if (!validFileExtensions.contains(fileExtension)
                || !regexFileName.matcher(uploadedFileName).matches()) {
            return false;
        }

        if (fileExtension.equalsIgnoreCase("application/pdf")) {
            String nameExtension = uploadedFileName.substring(uploadedFileName.length() - 3);
            if (!nameExtension.equalsIgnoreCase("pdf")) {
                return false;
            }
        }

        if (fileExtension.equalsIgnoreCase("image/jpeg")) {
            String nameExtension = uploadedFileName.substring(uploadedFileName.length() - 4);
            return nameExtension.equalsIgnoreCase("jpeg");
        }

        return true;
    }

    private static boolean isFileSizeValid(String base64string) {
        long fileSizeInMB = 0;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64string);
            fileSizeInMB = decodedBytes.length / (1024 * 1024);
        } catch (Exception e) {
            LOG.error("Error validating file:" + e);
        }
        return fileSizeInMB <= MAX_ALLOWED_FILE_SIZE;
    }
}
