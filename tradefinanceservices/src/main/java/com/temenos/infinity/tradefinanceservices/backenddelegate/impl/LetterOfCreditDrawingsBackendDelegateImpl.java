/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.LetterOfCreditDrawingsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.DrawingsDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class LetterOfCreditDrawingsBackendDelegateImpl
		implements LetterOfCreditDrawingsBackendDelegate, TradeFinanceConstants {
	private static final Logger LOG = LogManager.getLogger(LetterOfCreditDrawingsBackendDelegateImpl.class);

	@Override
	public DrawingsDTO getImportDrawingDetailsById(DataControllerRequest request, String srmsRequestOrderId)
			throws ApplicationException {
		DrawingsDTO drawingDetails = new DrawingsDTO();

		Map<String, Object> inputMap = new HashMap<>();
		Properties props = TradeFinanceProperties.loadProps(PARAM_PROPERTY);
		inputMap.put("type", props.getProperty("LetterOfCreditsDrawingsType"));
		inputMap.put("subtype", props.getProperty("LetterOfCreditsDrawingsSubType"));
		inputMap.put("serviceRequestIds", srmsRequestOrderId);

		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		String rawResponse = null;
		try {
			rawResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getServiceName())
					.withOperationId(
							TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to GET SRMS ID " + e);
		}

		JSONObject jsonResponseObj = new JSONObject();
		if (StringUtils.isNotBlank(rawResponse)) {
			jsonResponseObj = new JSONObject(rawResponse);
			LOG.info("OMS Response " + rawResponse);
		}
		JSONObject drawingDetailsResponse = jsonResponseObj.getJSONArray(PARAM_SERVICE_REQUESTS).getJSONObject(0);
		if (drawingDetailsResponse.has(PARAM_INPUT_PAYLOAD)) {
			drawingDetails = setDrawingsData(drawingDetailsResponse);
		}

		String drawingsSrmsReqOrderID = drawingDetailsResponse.has("serviceReqId")
				? drawingDetailsResponse.getString("serviceReqId")
				: "";
		String errorCode = drawingDetailsResponse.has("returnCode") ? drawingDetailsResponse.getString("returnCode")
				: "";
		String errorMessage = drawingDetailsResponse.has("additionalErrorInfo")
				? drawingDetailsResponse.getString("additionalErrorInfo")
				: "";

		if (StringUtils.isNotBlank(drawingsSrmsReqOrderID)) {
			drawingDetails.setDrawingsSrmsReqOrderID(drawingsSrmsReqOrderID);
		}
		if (StringUtils.isNotBlank(errorCode)) {
			drawingDetails.setErrorCode(errorMessage);
		}
		if (StringUtils.isNotBlank(errorMessage)) {
			drawingDetails.setErrorMessage(errorMessage);
		}
		if (jsonResponseObj.has("errmsg") && StringUtils.isNotBlank(jsonResponseObj.getString("errmsg"))) {
			drawingDetails.setErrorMessage(jsonResponseObj.getString("errmsg"));
			LOG.error("Unable to get drawings request " + jsonResponseObj.getString("errmsg"));
		}

		return drawingDetails;
	}
	public List<DrawingsDTO> getImportDrawingsFromSRMS(DrawingsDTO drawingsDTO, DataControllerRequest request) throws ApplicationException {

		List<DrawingsDTO> drawings = new ArrayList<>();

		Properties props = TradeFinanceProperties.loadProps(PARAM_PROPERTY);

		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("type", props.getProperty("LetterOfCreditsDrawingsType"));
		inputMap.put("subType", props.getProperty("LetterOfCreditsDrawingsSubType"));

		// Set Header Map
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));
		String drawingsResponse = null;
		JSONObject Response = new JSONObject();
		try {
			drawingsResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETORDERDETAILS.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();

		} catch (Exception e) {
			LOG.error("Unable to get Drawings requests " + e);
			throw new ApplicationException(ErrorCodeEnum.ERRTF_29061);
		}

		if (StringUtils.isNotBlank(drawingsResponse)) {
			Response = new JSONObject(drawingsResponse);
			LOG.info("OMS Response " + drawingsResponse);
		}
		JSONArray Orders = Response.getJSONArray(TradeFinanceConstants.PARAM_SERVICE_REQUESTS);
		for (int i = 0; i < Orders.length(); i++) {
			// do logic to insert data here
			DrawingsDTO order = new DrawingsDTO();
			JSONObject singleOrder = Orders.getJSONObject(i);
			if (singleOrder.has(PARAM_INPUT_PAYLOAD)) {
				order = setDrawingsData(singleOrder);
			}
			drawings.add(order);
		}

		return drawings;
	}

	public DrawingsDTO setDrawingsData(JSONObject drawingDetailsResponse) {
		DrawingsDTO drawingDetails = new DrawingsDTO();
		JSONObject payload = drawingDetailsResponse.getJSONObject(PARAM_INPUT_PAYLOAD);
		String lcReferenceNo = payload.has(PARAM_LC_REFERENCE_NO)
				? payload.getString(TradeFinanceConstants.PARAM_LC_REFERENCE_NO)
				: "";
		String lcType = payload.has(PARAM_LCTYPE) ? payload.getString(TradeFinanceConstants.PARAM_LCTYPE) : "";
		String drawingReferenceNo = payload.has(PARAM_DRAWING_REFERENCENO)
				? payload.getString(TradeFinanceConstants.PARAM_DRAWING_REFERENCENO)
				: "";
		String beneficiaryName = payload.has(PARAM_BENEFICIARYNAME)
				? payload.getString(TradeFinanceConstants.PARAM_BENEFICIARYNAME)
				: "";
		String documentStatus = payload.has(PARAM_DOCUMENTSTATUS)
				? payload.getString(TradeFinanceConstants.PARAM_DOCUMENTSTATUS)
				: "";
		String drawingCreationDate = payload.has(PARAM_DRAWING_CREATIONDATE)
				? payload.getString(TradeFinanceConstants.PARAM_DRAWING_CREATIONDATE)
				: "";
		String drawingCurrency = payload.has(PARAM_DRAWING_CURRENCY)
				? payload.getString(TradeFinanceConstants.PARAM_DRAWING_CURRENCY)
				: "";
		String drawingAmount = payload.has(PARAM_DRAWING_AMOUNT)
				? payload.getString(TradeFinanceConstants.PARAM_DRAWING_AMOUNT)
				: "";
		String drawingStatus = payload.has(PARAM_DRAWING_STATUS)
				? payload.getString(TradeFinanceConstants.PARAM_DRAWING_STATUS)
				: "";
		String lcAmount = payload.has(PARAM_LC_AMOUNT) ? payload.getString(TradeFinanceConstants.PARAM_LC_AMOUNT) : "";
		String lcCurrency = payload.has(PARAM_LC_CURRENCY) ? payload.getString(TradeFinanceConstants.PARAM_LC_CURRENCY)
				: "";
		String lcIssueDate = payload.has(PARAM_LC_ISSUEDATE)
				? payload.getString(TradeFinanceConstants.PARAM_LC_ISSUEDATE)
				: "";
		String lcExpiryDate = payload.has(PARAM_LC_EXPIRYDATE)
				? payload.getString(TradeFinanceConstants.PARAM_LC_EXPIRYDATE)
				: "";
		String paymentTerms = payload.has(PARAM_PAYMENTTERMS)
				? payload.getString(TradeFinanceConstants.PARAM_PAYMENTTERMS)
				: "";
		String presentorReference = payload.has(PARAM_PRESENTORREFERENCE)
				? payload.getString(TradeFinanceConstants.PARAM_PRESENTORREFERENCE)
				: "";
		String presentorName = payload.has(PARAM_PRESENTORNAME)
				? payload.getString(TradeFinanceConstants.PARAM_PRESENTORNAME)
				: "";
		String documentsReceived = payload.has(PARAM_DOCUMENTS_RECEIVED)
				? payload.getString(TradeFinanceConstants.PARAM_DOCUMENTS_RECEIVED)
				: "";
		String forwardContact = payload.has(PARAM_FORWARDCONTACT)
				? payload.getString(TradeFinanceConstants.PARAM_FORWARDCONTACT)
				: "";
		String shippingGuaranteeReference = payload.has(PARAM_SHIPPING_GUARANTEE_REFERENCE)
				? payload.getString(TradeFinanceConstants.PARAM_SHIPPING_GUARANTEE_REFERENCE)
				: "";
		String approvalDate = payload.has(PARAM_APPROVALDATE)
				? payload.getString(TradeFinanceConstants.PARAM_APPROVALDATE)
				: "";
		String totalDocuments = payload.has(PARAM_TOTALDOCUMENTS)
				? payload.getString(TradeFinanceConstants.PARAM_TOTALDOCUMENTS)
				: "";
		String documentName = payload.has(PARAM_DOCUMENTNAME)
				? payload.getString(TradeFinanceConstants.PARAM_DOCUMENTNAME)
				: "";
		String discrepancyDescription = payload.has(PARAM_DISCREPANCY_DESCRIPTION)
				? payload.getString(TradeFinanceConstants.PARAM_DISCREPANCY_DESCRIPTION)
				: "";
		String paymentStatus = payload.has(PARAM_PAYMENTSTATUS)
				? payload.getString(TradeFinanceConstants.PARAM_PAYMENTSTATUS)
				: "";
		String rejectedDate = payload.has(PARAM_REJECTEDDATE)
				? payload.getString(TradeFinanceConstants.PARAM_REJECTEDDATE)
				: "";
		String totalAmountToBePaid = payload.has(PARAM_TOTALAMOUNT_TO_BE_PAID)
				? payload.getString(TradeFinanceConstants.PARAM_TOTALAMOUNT_TO_BE_PAID)
				: "";
		String accountToBeDebited = payload.has(PARAM_ACCOUNT_TO_BE_DEBITED)
				? payload.getString(TradeFinanceConstants.PARAM_ACCOUNT_TO_BE_DEBITED)
				: "";
		String messageFromBank = payload.has(PARAM_MESSAGEFROMBANK)
				? payload.getString(TradeFinanceConstants.PARAM_MESSAGEFROMBANK)
				: "";
		String messageToBank = payload.has(PARAM_MESSAGETOBANK)
				? payload.getString(TradeFinanceConstants.PARAM_MESSAGETOBANK)
				: "";
		String totalPaidAmount = payload.has(PARAM_TOTAL_PAID_AMOUNT)
				? payload.getString(TradeFinanceConstants.PARAM_TOTAL_PAID_AMOUNT)
				: "";
		String paymentDate = payload.has(PARAM_PAYMENTDATE) ? payload.getString(TradeFinanceConstants.PARAM_PAYMENTDATE)
				: "";
		String reasonForRejection = payload.has(PARAM_REASON_FOR_REJECTION)
				? payload.getString(TradeFinanceConstants.PARAM_REASON_FOR_REJECTION)
				: "";
		String discrepancies = payload.has(PARAM_DISCREPANCIES)
				? payload.getString(TradeFinanceConstants.PARAM_DISCREPANCIES)
				: "";
		String acceptance = payload.has(PARAM_ACCEPTANCE) ? payload.getString(TradeFinanceConstants.PARAM_ACCEPTANCE)
				: "";
		String messageType = payload.has(PARAM_MESSAGETYPE) ? payload.getString(TradeFinanceConstants.PARAM_MESSAGETYPE)
				: "";
		String deliveryDestination = payload.has(PARAM_DELIVERYDESTINATION)
				? payload.getString(TradeFinanceConstants.PARAM_DELIVERYDESTINATION)
				: "";
		String messageDate = payload.has(PARAM_MESSAGEDATE) ? payload.getString(TradeFinanceConstants.PARAM_MESSAGEDATE)
				: "";
		String messageCategory = payload.has(PARAM_MESSAGECATEGORY)
				? payload.getString(TradeFinanceConstants.PARAM_MESSAGECATEGORY)
				: "";
		String lcSrmsReqOrderID = payload.has(PARAM_LCSRMSREQORDERID)
				? payload.getString(TradeFinanceConstants.PARAM_LCSRMSREQORDERID)
				: "";
		String status = payload.has(PARAM_ORDER_STATUS) ? payload.getString(TradeFinanceConstants.PARAM_ORDER_STATUS)
				: "";
		if (StringUtils.isNotBlank(lcReferenceNo)) {
			drawingDetails.setLcReferenceNo(lcReferenceNo);
		}
		if (StringUtils.isNotBlank(lcType)) {
			drawingDetails.setLcType(lcType);
		}
		if (StringUtils.isNotBlank(drawingReferenceNo)) {
			drawingDetails.setDrawingReferenceNo(drawingReferenceNo);
		}
		if (StringUtils.isNotBlank(beneficiaryName)) {
			drawingDetails.setBeneficiaryName(beneficiaryName);
		}
		if (StringUtils.isNotBlank(documentStatus)) {
			drawingDetails.setDocumentStatus(documentStatus);
		}
		if (StringUtils.isNotBlank(drawingCreationDate)) {
			drawingDetails.setDrawingCreationDate(drawingCreationDate);
		}
		if (StringUtils.isNotBlank(drawingCurrency)) {
			drawingDetails.setDrawingCurrency(drawingCurrency);
		}
		if (StringUtils.isNotBlank(drawingAmount)) {
			drawingDetails.setDrawingAmount(drawingAmount);
		}
		if (StringUtils.isNotBlank(drawingStatus)) {
			drawingDetails.setDrawingStatus(drawingStatus);
		}
		if (StringUtils.isNotBlank(lcAmount)) {
			drawingDetails.setLcAmount(lcAmount);
		}
		if (StringUtils.isNotBlank(lcCurrency)) {
			drawingDetails.setLcCurrency(lcCurrency);
		}
		if (StringUtils.isNotBlank(lcIssueDate)) {
			drawingDetails.setLcIssueDate(lcIssueDate);
		}
		if (StringUtils.isNotBlank(lcExpiryDate)) {
			drawingDetails.setLcExpiryDate(lcExpiryDate);
		}
		if (StringUtils.isNotBlank(paymentTerms)) {
			drawingDetails.setPaymentTerms(paymentTerms);
		}
		if (StringUtils.isNotBlank(presentorReference)) {
			drawingDetails.setPresentorReference(presentorReference);
		}
		if (StringUtils.isNotBlank(presentorName)) {
			drawingDetails.setPresentorName(presentorName);
		}
		if (StringUtils.isNotBlank(documentsReceived)) {
			drawingDetails.setDocumentsReceived(documentsReceived);
		}
		if (StringUtils.isNotBlank(forwardContact)) {
			drawingDetails.setForwardContact(forwardContact);
		}
		if (StringUtils.isNotBlank(shippingGuaranteeReference)) {
			drawingDetails.setShippingGuaranteeReference(shippingGuaranteeReference);
		}
		if (StringUtils.isNotBlank(approvalDate)) {
			drawingDetails.setApprovalDate(approvalDate);
		}
		if (StringUtils.isNotBlank(totalDocuments)) {
			drawingDetails.setTotalDocuments(totalDocuments);
		}
		if (StringUtils.isNotBlank(documentName)) {
			drawingDetails.setDocumentName(documentName);
		}
		if (StringUtils.isNotBlank(discrepancyDescription)) {
			drawingDetails.setDiscrepancyDescription(discrepancyDescription);
		}
		if (StringUtils.isNotBlank(paymentStatus)) {
			drawingDetails.setPaymentStatus(paymentStatus);
		}
		if (StringUtils.isNotBlank(rejectedDate)) {
			drawingDetails.setRejectedDate(rejectedDate);
		}
		if (StringUtils.isNotBlank(totalAmountToBePaid)) {
			drawingDetails.setTotalAmountToBePaid(totalAmountToBePaid);
		}
		if (StringUtils.isNotBlank(accountToBeDebited)) {
			drawingDetails.setAccountToBeDebited(accountToBeDebited);
		}
		if (StringUtils.isNotBlank(messageFromBank)) {
			drawingDetails.setMessageFromBank(messageFromBank);
		}
		if (StringUtils.isNotBlank(messageToBank)) {
			if(messageToBank.contains("||")) messageToBank = messageToBank.replaceAll("\\|\\|", "\n");
			drawingDetails.setMessageToBank(messageToBank);
		}
		if (StringUtils.isNotBlank(totalPaidAmount)) {
			drawingDetails.setTotalPaidAmount(totalPaidAmount);
		}
		if (StringUtils.isNotBlank(paymentDate)) {
			drawingDetails.setPaymentDate(paymentDate);
		}
		if (StringUtils.isNotBlank(reasonForRejection)) {
			drawingDetails.setReasonForRejection(reasonForRejection);
		}
		if (StringUtils.isNotBlank(discrepancies)) {
			drawingDetails.setDiscrepancies(discrepancies);
		}
		if (StringUtils.isNotBlank(acceptance)) {
			drawingDetails.setAcceptance(acceptance);
		}
		if (StringUtils.isNotBlank(messageType)) {
			drawingDetails.setMessageType(messageType);
		}
		if (StringUtils.isNotBlank(deliveryDestination)) {
			drawingDetails.setDeliveryDestination(deliveryDestination);
		}
		if (StringUtils.isNotBlank(messageDate)) {
			drawingDetails.setMessageDate(messageDate);
		}
		if (StringUtils.isNotBlank(messageCategory)) {
			drawingDetails.setMessageCategory(messageCategory);
		}
		if (StringUtils.isNotBlank(lcSrmsReqOrderID)) {
			drawingDetails.setLcSrmsReqOrderID(lcSrmsReqOrderID);
		}
		if (StringUtils.isNotBlank(status)) {
			drawingDetails.setStatus(status);
		} else {
			drawingDetails.setStatus("");
		}
		if (drawingDetailsResponse.has(PARAM_REQUEST_DATE_TIME)
				&& StringUtils.isNotBlank(drawingDetailsResponse.getString(PARAM_REQUEST_DATE_TIME)))
			drawingDetails.setDrawingCreationDate(drawingDetailsResponse.getString(PARAM_REQUEST_DATE_TIME));
		if (drawingDetailsResponse.has(PARAM_SERVICE_REQ_ID)
				&& StringUtils.isNotBlank(drawingDetailsResponse.getString(PARAM_SERVICE_REQ_ID)))
			drawingDetails.setDrawingsSrmsReqOrderID(drawingDetailsResponse.getString(PARAM_SERVICE_REQ_ID));
		if(drawingDetailsResponse.has("partyId") && StringUtils.isNotBlank(drawingDetailsResponse.getString("partyId"))) {
			drawingDetails.setCustomerId(drawingDetailsResponse.getString("partyId"));
		}
		return drawingDetails;
	}
	
	@Override
	public DrawingsDTO createDrawingsOrder(DrawingsDTO drawings, DataControllerRequest request)
			throws ApplicationException {
		// TODO Auto-generated method stub
		DrawingsDTO drawingsResponse = new DrawingsDTO();
		String requestbody = _responseBody(drawings);
		
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceReqStatus", "Success");
		
		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);
		inputMap.put("type", props.getProperty("LetterOfCreditsDrawingsType"));
		inputMap.put("subtype", props.getProperty("LetterOfCreditsDrawingsSubType"));
		inputMap.put("requestBody", requestbody);

		// Set Header Map
		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		// Making a call to order request API
		String DrawingsResponse = null;
		JSONObject Response = new JSONObject();
		try {
			DrawingsResponse = DBPServiceExecutorBuilder.builder()
								.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
								.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
								.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
								.build().getResponse();

		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to create Drawings request order " + e);
		}
		
		if (StringUtils.isNotBlank(DrawingsResponse)) {
			Response = new JSONObject(DrawingsResponse);
			LOG.info(" OMS Drawings Response for LC ID" + drawings.getLcSrmsReqOrderID() + Response);
		}
		
		
		if (Response.has(TradeFinanceConstants.PARAM_ORDER_ID)
				&& StringUtils.isNotBlank(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID))) {
			drawingsResponse.setDrawingsSrmsReqOrderID(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID));
			drawingsResponse.setStatus(drawings.getStatus());
			request.addRequestParam_("isSrmsFailed", "false");
		}
		else {
			request.addRequestParam_("isSrmsFailed", "true");
		}

		if (Response.has("dbpErrMsg") && StringUtils.isNotBlank(Response.getString("dbpErrMsg"))) {
			drawingsResponse.setErrorMessage(Response.getString("dbpErrMsg"));
		}

		if (Response.has("dbpErrCode") && StringUtils.isNotBlank(String.valueOf(Response.getInt("dbpErrCode")))) {
			drawingsResponse.setErrorCode(String.valueOf(Response.getInt("dbpErrCode")));
		}

		
		return drawingsResponse;
	}

	@Override
	public DrawingsDTO updateDrawingsOrder(DrawingsDTO drawings, DataControllerRequest request)
			throws ApplicationException {
		DrawingsDTO drawingsResponse = new DrawingsDTO();
		String requestbody = _responseBody(drawings);
		
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceRequestId", drawings.getDrawingsSrmsReqOrderID());
		inputMap.put("requestBody", requestbody);

		
		// Set Header Map
        HashMap<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
        headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));
        
		String drawingResponse = null;
		JSONObject Response = new JSONObject();
		try {
			drawingResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getServiceName()).
					withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getOperationName()).
					withRequestParameters(inputMap).
					withRequestHeaders(headerMap).
					withDataControllerRequest(request).
					build().getResponse();
			
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to update Drawings request order " + e);
		}
		
		if (StringUtils.isNotBlank(drawingResponse)) {
			Response = new JSONObject(drawingResponse);
			LOG.info("OMS Response " + Response);
		}
		
		if (Response.has(TradeFinanceConstants.PARAM_ORDER_ID)
				&& StringUtils.isNotBlank(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID))) {
			drawingsResponse.setDrawingsSrmsReqOrderID((Response.getString(TradeFinanceConstants.PARAM_ORDER_ID)));
			drawingsResponse.setStatus(drawings.getStatus());
			request.addRequestParam_("isSrmsFailed", "false");
		}

		else {
			request.addRequestParam_("isSrmsFailed", "true");
		}
		if (Response.has("dbpErrMsg") && StringUtils.isNotBlank(Response.getString("dbpErrMsg"))) {
			drawingsResponse.setErrorMessage(Response.getString("dbpErrMsg"));
		}

		if (Response.has("dbpErrCode") && StringUtils.isNotBlank(Response.getString("dbpErrCode"))) {
			drawingsResponse.setErrorCode(String.valueOf(Response.getInt("dbpErrCode")));
		}
		return drawingsResponse;

	}
	
	public String _responseBody(DrawingsDTO drawings) {
		JSONObject requestBody = new JSONObject();
		requestBody.put("lcReferenceNo", drawings.getLcReferenceNo());
		requestBody.put("lcType", drawings.getLcType());
		requestBody.put("drawingReferenceNo", drawings.getDrawingReferenceNo());
		requestBody.put("beneficiaryName", drawings.getBeneficiaryName());
		if(StringUtils.isNotBlank(drawings.getDocumentStatus()) && drawings.getDocumentStatus().equalsIgnoreCase(PARAM_DISCREPANT)) 
			drawings.setDocumentStatus(PARAM_DISCREPANT);
		else if(StringUtils.isNotBlank(drawings.getDocumentStatus()) && drawings.getDocumentStatus().equalsIgnoreCase(PARAM_CLEAN)) 
			drawings.setDocumentStatus(PARAM_CLEAN);
		requestBody.put("documentStatus", drawings.getDocumentStatus());
		requestBody.put("drawingCreationDate", drawings.getDrawingCreationDate());
		requestBody.put("drawingCurrency", drawings.getDrawingCurrency());
		requestBody.put("drawingAmount", drawings.getDrawingAmount());
		requestBody.put("lcAmount", drawings.getLcAmount());
		requestBody.put("lcCurrency", drawings.getLcCurrency());
		requestBody.put("lcIssueDate", drawings.getLcIssueDate());
		requestBody.put("lcExpiryDate", drawings.getLcExpiryDate());	
		requestBody.put("presentorReference", drawings.getPresentorReference());
		requestBody.put("presentorName", drawings.getPresentorName());
		if(StringUtils.isNotBlank(drawings.getDocumentsReceived()) && drawings.getDocumentsReceived().equalsIgnoreCase(PARAM_YES))
			drawings.setDocumentsReceived(PARAM_YES);
		else if(StringUtils.isNotBlank(drawings.getDocumentsReceived()) && drawings.getDocumentsReceived().equalsIgnoreCase(PARAM_NO))
			drawings.setDocumentsReceived(PARAM_NO);
		requestBody.put("documentsReceived", drawings.getDocumentsReceived());
		requestBody.put("forwardContact", drawings.getForwardContact());
		requestBody.put("shippingGuaranteeReference", drawings.getShippingGuaranteeReference());
		requestBody.put("approvalDate", drawings.getApprovalDate());
		requestBody.put("totalDocuments", drawings.getTotalDocuments());
		requestBody.put("documentName", drawings.getDocumentName());
		requestBody.put("discrepancies", drawings.getDiscrepancies());
		if(StringUtils.isNotBlank(drawings.getAcceptance()) && drawings.getAcceptance().equalsIgnoreCase(PARAM_APPROVED))
			drawings.setAcceptance(PARAM_APPROVED);
		else if(StringUtils.isNotBlank(drawings.getAcceptance()) && drawings.getAcceptance().equalsIgnoreCase(PARAM_REJECTED))
			drawings.setAcceptance(PARAM_REJECTED);
		requestBody.put("acceptance", drawings.getAcceptance());
		requestBody.put("totalAmountToBePaid", drawings.getTotalAmountToBePaid());
		requestBody.put("accountToBeDebited", drawings.getAccountToBeDebited());
		requestBody.put("messageFromBank", drawings.getMessageFromBank());
		requestBody.put("messageToBank", drawings.getMessageToBank());
		requestBody.put("totalPaidAmount", drawings.getTotalPaidAmount());
		requestBody.put("paymentDate", drawings.getPaymentDate());
		requestBody.put("reasonForRejection", drawings.getReasonForRejection());
		requestBody.put("paymentStatus", drawings.getPaymentStatus());
		requestBody.put("rejectedDate", drawings.getRejectedDate());
		requestBody.put("lcSrmsReqOrderID", drawings.getLcSrmsReqOrderID());
		requestBody.put("paymentStatus",drawings.getPaymentStatus());
		requestBody.put("status", drawings.getStatus());
		return requestBody.toString().replaceAll("\"", "'");
	}
	
}
