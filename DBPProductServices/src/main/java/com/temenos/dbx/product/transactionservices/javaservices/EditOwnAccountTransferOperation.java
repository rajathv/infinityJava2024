package com.temenos.dbx.product.transactionservices.javaservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.transactionservices.resource.api.OwnAccountFundTransferResource;


/**
 * 
 * @author KH1755
 * @version 1.0
 * implements {@link JavaService2}
 */
public class EditOwnAccountTransferOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(EditOwnAccountTransferOperation.class);
	public static final int FILENAME_INDEX = 0;
	public static final int FILETYPE_INDEX = 1;
	public static final int FILECONTENTS_INDEX = 2;
	public static final int UNIQUE_ID_LENGTH = 32;

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) throws Exception {

		Result result = new Result();
		String referenceId = null;
		
		try {
			//Initializing of TransactionResource through Abstract factory method
			OwnAccountFundTransferResource ownaccountTranscationResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(OwnAccountFundTransferResource.class);
			
			result  = ownaccountTranscationResource.editTransaction(methodID, inputArray, request, response);
			String dbpErrMsg = result.getParamValueByName("dbpErrMsg");
			String dbpErrorCode = result.getParamValueByName("dbpErrCode");
			if (StringUtils.isNotBlank(dbpErrMsg) && !dbpErrorCode.equals("0")) {
				return result;
			}
			result.removeParamByName("dbpErrMsg");
			result.removeParamByName("dbpErrCode");
			referenceId = result.getParamValueByName("referenceId");
			Map<String, String> inputParams = HelperMethods.getInputParamMap(inputArray);
			if(inputParams.containsKey("uploadedattachments") && inputParams.containsKey("validate")) {
				String isValidate = inputParams.get("validate");
				if ((StringUtils.isBlank(isValidate)
						|| (StringUtils.isNotBlank(isValidate) && isValidate.equalsIgnoreCase("false")))
						&& StringUtils.isNotBlank(referenceId)) {
					String uploadedAttachments = inputParams.get("uploadedattachments");
					String deletedAttachments = inputParams.get("deletedDocuments");
					Map<String, Object> customer = CustomerSession.getCustomerMap(request);
					String userId = CustomerSession.getCustomerId(customer);
					if (StringUtils.isNotBlank(uploadedAttachments)) {
						String uploadedAttachmentsArray[] = uploadedAttachments.split(",");
						parseUploadingAttachments(request, result, referenceId, userId,
								uploadedAttachmentsArray);
					}
					if (StringUtils.isNotBlank(deletedAttachments)) {
						String deletedAttachmentsArray[] = deletedAttachments.split(",");
						deleteAttachments(request, result, referenceId, userId,
								deletedAttachmentsArray);
					}
				}
			}
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking EditOwnAccountTransferOperation: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
	}
	
	private void deleteAttachments(DataControllerRequest request, Result result, String referenceId, String userId, String[] deletedAttachmentsArray) {
		Result deleteResult = new Result();
		Map<String, String> dataMap = new HashMap<>();
		for(int i=0;i<deletedAttachmentsArray.length;i++) {
			try {
				if (CommonUtils.isDMSIntegrationEnabled()) {
					dataMap.put("documentId", deletedAttachmentsArray[i]);
					deleteResult = HelperMethods.callApi(request, dataMap, HelperMethods.getHeaders(request),
							URLConstants.DOCUMENT_STORAGE_DELETE);
					if (deleteResult.getParamByName("message") != null) {
						deleteResult.addParam("deleteMessage", result.getParamValueByName("message").toString());
					}
				} else {
					dataMap.put("documentId", deletedAttachmentsArray[i]);
					dataMap.put(DBPUtilitiesConstants.FILTER, "userId" + DBPUtilitiesConstants.EQUAL + userId
							+ DBPUtilitiesConstants.AND + "transactionId" + DBPUtilitiesConstants.EQUAL + referenceId
							+ DBPUtilitiesConstants.AND + "paymentFileID" + DBPUtilitiesConstants.EQUAL + deletedAttachmentsArray[i]);
					deleteResult = HelperMethods.callApi(request, dataMap, HelperMethods.getHeaders(request),
							URLConstants.PAYMENT_FILES_DELETE);
					result.addParam("deleteMessage", "Document deleted successfully");
				}
			} catch (Exception e) {
				result.addParam("deleteMessage", "Error occured while deleting document(s)");
			}
		}
	}
	
	private void parseUploadingAttachments(DataControllerRequest request, Result transactionResult, String referenceId,
			String userId, String[] uploadedAttachmentsArray) {
		String uploadedFileName = null;
		String fileExtension = null;
		String fileContents = null;
		ArrayList<String> failedUploads = new ArrayList<>();
		ArrayList<String> successfulUploads = new ArrayList<>();
		try {
			for (int i = 0; i < uploadedAttachmentsArray.length; i++) {
				String attachmentDetails[] = uploadedAttachmentsArray[i].split("-");
				if (attachmentDetails.length == 3) {
					uploadedFileName = attachmentDetails[FILENAME_INDEX];
					fileExtension = attachmentDetails[FILETYPE_INDEX];
					fileContents = attachmentDetails[FILECONTENTS_INDEX];
					boolean status = uploadAttachments(request, referenceId, userId, uploadedFileName, fileExtension,
							fileContents);
					if(status) {
						successfulUploads.add(uploadedFileName);
					} else {
						failedUploads.add(uploadedFileName);
					}
				} else {
					LOG.error("File input is incorrect for performing upload operation");
					failedUploads.add(attachmentDetails[FILENAME_INDEX]);
				}
			}
		} catch (Exception e) {
			LOG.error("Error occured while uploading attachments ", e);
		} finally {
			transactionResult.addParam("failedUploads", StringUtils.join(failedUploads, ","));
			transactionResult.addParam("successfulUploads", StringUtils.join(successfulUploads, ","));
		}
	}

	private boolean uploadAttachments(DataControllerRequest request, String referenceId,
			String userId, String uploadedFileName, String fileExtension, String fileContents) {
		Map<String, String> dataMap = new HashMap<>();
		Result result = new Result();
		try {
			if (CommonUtils.isDMSIntegrationEnabled()) {
				dataMap.put("documentName", uploadedFileName);
				dataMap.put("category", "payment");
				dataMap.put("content", fileContents);
				dataMap.put("version", "1.0");
				dataMap.put("referenceId", referenceId);
				dataMap.put("userId", userId);
				result = HelperMethods.callApi(request, dataMap, HelperMethods.getHeaders(request),
						URLConstants.DOCUMENT_STORAGE_UPLOAD);
			} else {
				String id = CommonUtils.generateUniqueID(UNIQUE_ID_LENGTH);
				dataMap.put("paymentFileID", id);
				dataMap.put("paymentFileName", uploadedFileName);
				dataMap.put("paymentFileType", fileExtension);
				dataMap.put("paymentFileContents", fileContents);
				dataMap.put("transactionId", referenceId);
				dataMap.put("userId", userId);
				result = HelperMethods.callApi(request, dataMap, HelperMethods.getHeaders(request),
						URLConstants.PAYMENT_FILES_CREATE);
			}
			if(result.getOpstatusParamValue().equals("0")) {
				return true;
			}else {
				return false;
			}
		} catch (Exception e) {
			LOG.error("Error occured while uploading attachments ", e);
			return false;
		}
	}
}
