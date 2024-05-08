package com.temenos.infinity.api.arrangements.resource.impl;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.MWConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.resource.impl.DocumentStorageResourceImpl;
import com.temenos.infinity.api.arrangements.businessdelegate.api.DocumentStorageBusinessDelegate;
import com.temenos.infinity.api.arrangements.dto.DocumentStorage;
import com.temenos.infinity.api.arrangements.dto.DocumentStorageEvidence;
import com.temenos.infinity.api.arrangements.exception.DMSException;
import com.temenos.infinity.api.arrangements.resource.api.DocumentStorageResource;

public class DocumentStorageResourceImpl implements DocumentStorageResource {


		private static final Logger logger = LogManager.getLogger(DocumentStorageResourceImpl.class);
		@Override
		public Result downloadDocument(String methodID, Object[] inputArray, DataControllerRequest request,
				DataControllerResponse response) throws Exception {
			Result result = null;
			DocumentStorageBusinessDelegate documentStorageBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(DocumentStorageBusinessDelegate.class);

			try {
				
				DocumentStorage docInfo = createDocumentStorageDto(request);
				DocumentStorage downloadResponse = documentStorageBusinessDelegate.downloadDocument(docInfo, request);
				if (downloadResponse != null) {
					
					result = JSONToResult.convert(JSONUtils.stringify(downloadResponse));
					
					
					
					Map<String, String> customHeaders = new HashMap<>();
		            customHeaders.put(HttpHeaders.CONTENT_TYPE, "application/pdf");
		            customHeaders.put("Content-Disposition", "attachment; filename=\"" + downloadResponse.getDocumentName() + "\"");
		            response.getHeaders().putAll(customHeaders);
					result.addOpstatusParam(0);
					result.addHttpStatusCodeParam(200);
					
					result.addStringParam("message", "Document downloaded successfully");
				} else {
					result = new Result();
					result.addOpstatusParam(-1);
					result.addHttpStatusCodeParam(500);
					result.addErrMsgParam("Internal Error: Document download failed");
				}
			} catch (IOException ie) {
				logger.error("Exception occured while downloading document", ie);
				result = new Result();
				result.addOpstatusParam(-1);
				result.addHttpStatusCodeParam(500);
				result.addErrMsgParam("Internal Error: "+ie.getMessage());
			} catch (DMSException de) {
				result = de.constructResultObject();
			}
			return result;
		}
		
		public Result searchDocument(String methodID, Object[] inputArray, DataControllerRequest request,
				DataControllerResponse response) throws Exception {
			Result result = new Result();
			DocumentStorageBusinessDelegate documentStorageBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(DocumentStorageBusinessDelegate.class);

			try {
				DocumentStorage docInfo = createDocumentStorageDto(request);
				List<DocumentStorage> searchResponse = documentStorageBusinessDelegate.searchDocument(docInfo, request);

				if (searchResponse != null) {
					Map<String, List<DocumentStorage>> documentsList = new HashMap<String, List<DocumentStorage>>();
					documentsList.put("documentsList", searchResponse);
					result = JSONToResult.convert(JSONUtils.stringify(documentsList));
					result.addOpstatusParam(0);
					result.addHttpStatusCodeParam(200);
				} else {
					result = new Result();
					result.addOpstatusParam(-1);
					result.addHttpStatusCodeParam(500);
					result.addErrMsgParam("Internal Error: Document search failed");
				}
			} catch (IOException ie) {
				logger.error("Exception occured while searching document", ie);
				result = new Result();
				result.addOpstatusParam(-1);
				result.addHttpStatusCodeParam(500);
				result.addErrMsgParam("Internal Error: "+ie.getMessage());
			}catch (DMSException de) {
				result = de.constructResultObject();
			}
			return result;
		}
		
		private DocumentStorage createDocumentStorageDto(DataControllerRequest request) {
			DocumentStorage documentStorageDto = new DocumentStorage();
			documentStorageDto.setUserId(request.getParameter("userId"));
			documentStorageDto.setDocumentId(request.getParameter("documentId"));
			documentStorageDto.setReferenceId(request.getParameter("referenceId"));
			documentStorageDto.setCategory(request.getParameter("category"));
			documentStorageDto.setDocumentName(request.getParameter("documentName"));
			documentStorageDto.setContent(request.getParameter("content"));
			documentStorageDto.setVersion(request.getParameter("version"));
			documentStorageDto.setAuthorizationKey(request.getParameter("authorization"));
			documentStorageDto.setOwnerSystemId(request.getParameter("ownerSystemId"));
			documentStorageDto.setApplicationId(request.getParameter("applicationId"));
			documentStorageDto.setDocumentType(request.getParameter("documentType"));
			documentStorageDto.setDocumentGroup(request.getParameter("documentGroup"));
			documentStorageDto.setMetaDocumentName(request.getParameter("metaDocumentName"));
			documentStorageDto.setDocumentStatus(request.getParameter("documentStatus"));
			documentStorageDto.setLastChangeUserId(request.getParameter("lastChangeUserId"));
			documentStorageDto.setAction(request.getParameter("action"));
			documentStorageDto.setNewDocumentGroup(request.getParameter("newDocumentGroup"));
			documentStorageDto.setNewOwnerSystemId(request.getParameter("newOwnerSystemId"));
			documentStorageDto.setIsSystemGenerated(request.getParameter("isSystemGenerated"));
			documentStorageDto.setCollateralId(request.getParameter("collateralId"));
			documentStorageDto.setArrangementId(request.getParameter("arrangementId"));
			String content = request.getParameter("content");
			String documentName = request.getParameter("documentName");
			
			if(StringUtils.isNotBlank(content) && StringUtils.isNotBlank(documentName) && documentStorageDto.getCollateralId() == null) {
				int fileSize = (int)(content.length() * 0.75);
				String[] extensionArray = documentName.split("[.]", 0);
				String fileType = null;
				if (extensionArray.length == 2)
					fileType = extensionArray[1];
				String fileInfo = "{'size':'" + fileSize + "','type':'" + fileType + "'}";
				documentStorageDto.setFileInfo(fileInfo);
			}
			return documentStorageDto;
		}
		private DocumentStorageEvidence createDocumentStorageEvidenceDto(DataControllerRequest request) {
			DocumentStorageEvidence documentStorageEvidenceDto = new DocumentStorageEvidence();
			documentStorageEvidenceDto.setContent(request.getParameter("fileContent"));
			documentStorageEvidenceDto.setEvidenceType(request.getParameter("evidenceType"));
			documentStorageEvidenceDto.setAuthorizationKey(request.getParameter("authorization"));
			documentStorageEvidenceDto.setOwnerSystemId(request.getParameter("ownerSystemId"));
			documentStorageEvidenceDto.setFulfilmentId(request.getParameter("fulfilmentId"));
			documentStorageEvidenceDto.setForRequirements(request.getParameter("forRequirements"));
			documentStorageEvidenceDto.setFileName(request.getParameter("fileName"));
			documentStorageEvidenceDto.setDocumentGroup(request.getParameter("documentGroup"));
	        documentStorageEvidenceDto.setAppEvidenceId(request.getParameter("appEvidenceId"));
			
			return documentStorageEvidenceDto;
		}
		private Map<String, Object> prepareFulfilmentPayload(DataControllerRequest request) {
			Map<String, Object> payloadMap = new HashMap<String, Object>();
			if (request.containsKeyInRequest("applicationId"))
				payloadMap.put("applicationId", request.getParameter("applicationId").toString());
			if (request.containsKeyInRequest("ownerId"))
				payloadMap.put("ownerId", request.getParameter("ownerId").toString());
			if (request.containsKeyInRequest("ownerType"))
				payloadMap.put("ownerType", request.getParameter("ownerType").toString());
			if (request.containsKeyInRequest("description"))
				payloadMap.put("description", request.getParameter("description").toString());
			if (request.containsKeyInRequest("requirements"))
				payloadMap.put("requirements", request.getParameter("requirements"));
			if (request.containsKeyInRequest("ownerSystemId"))
				payloadMap.put("ownerSystemId", request.getParameter("ownerSystemId").toString());
			if (request.containsKeyInRequest("documentGroup"))
				payloadMap.put("documentGroup", request.getParameter("documentGroup").toString());
			if (request.containsKeyInRequest("Authorization"))
				payloadMap.put("Authorization", request.getParameter("Authorization").toString());
			return payloadMap;
		}
	}
