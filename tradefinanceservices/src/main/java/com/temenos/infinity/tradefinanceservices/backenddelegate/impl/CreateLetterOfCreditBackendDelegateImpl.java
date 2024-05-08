package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.CreateLetterOfCreditBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The Class CreateLetterOfCreditBackendDelegateImpl.
 */
public class CreateLetterOfCreditBackendDelegateImpl implements CreateLetterOfCreditBackendDelegate {
	private static final Logger LOG = LogManager.getLogger(CreateLetterOfCreditBackendDelegateImpl.class);

	/**
	 * Creates the letter of credits order.
	 *
	 * @param letterOfCredit the letter of credit
	 * @param request        the request
	 * @return the letter of credits DTO
	 */
	@Override
	public LetterOfCreditsDTO createLetterOfCreditsOrder(LetterOfCreditsDTO letterOfCredit,
			DataControllerRequest request) {

		LetterOfCreditsDTO LCResponse = new LetterOfCreditsDTO();

		String status = "";
		JSONObject requestBody = new JSONObject();
		requestBody.put("lcReferenceNo", letterOfCredit.getLcReferenceNo());
		requestBody.put("lcAmount", String.valueOf(letterOfCredit.getLcAmount()));
		requestBody.put("lcCurrency", letterOfCredit.getLcCurrency());
		requestBody.put("tolerancePercentage", letterOfCredit.getTolerancePercentage());
		requestBody.put("maximumCreditAmount", String.valueOf(letterOfCredit.getMaximumCreditAmount()));
		requestBody.put("additionalAmountPayable", letterOfCredit.getAdditionalAmountPayable());
		requestBody.put("paymentTerms", letterOfCredit.getPaymentTerms());
		requestBody.put("availableWith1", letterOfCredit.getAvailableWith1());
		requestBody.put("availableWith2", letterOfCredit.getAvailableWith2());
		requestBody.put("availableWith3", letterOfCredit.getAvailableWith3());
		requestBody.put("availableWith4", letterOfCredit.getAvailableWith4());
		requestBody.put("issueDate", letterOfCredit.getIssueDate());
		requestBody.put("expiryDate", letterOfCredit.getExpiryDate());
		requestBody.put("expiryPlace", letterOfCredit.getExpiryPlace());
		requestBody.put("chargesAccount", letterOfCredit.getChargesAccount());
		requestBody.put("commisionAccount", letterOfCredit.getCommisionAccount());
		requestBody.put("marginAccount", letterOfCredit.getMarginAccount());
		requestBody.put("messageToBank", letterOfCredit.getMessageToBank());
		requestBody.put("beneficiaryName", letterOfCredit.getBeneficiaryName());
		requestBody.put("beneficiaryAddressLine1", letterOfCredit.getBeneficiaryAddressLine1());
		requestBody.put("beneficiaryAddressLine2", letterOfCredit.getBeneficiaryAddressLine2());
		requestBody.put("beneficiaryPostCode", letterOfCredit.getBeneficiaryPostCode());
		requestBody.put("beneficiaryCountry", letterOfCredit.getBeneficiaryCountry());
		requestBody.put("beneficiaryCity", letterOfCredit.getBeneficiaryCity());
		requestBody.put("beneficiaryState", letterOfCredit.getBeneficiaryState());
		requestBody.put("beneficiaryBank", letterOfCredit.getBeneficiaryBank());
		requestBody.put("beneficiaryBankAdressLine1", letterOfCredit.getBeneficiaryBankAdressLine1());
		requestBody.put("beneficiaryBankAdressLine2", letterOfCredit.getBeneficiaryBankAdressLine2());
		requestBody.put("beneficiaryBankPostCode", letterOfCredit.getBeneficiaryBankPostCode());
		requestBody.put("beneficiaryBankCountry", letterOfCredit.getBeneficiaryBankCountry());
		requestBody.put("beneficiaryBankCity", letterOfCredit.getBeneficiaryBankCity());
		requestBody.put("beneficiaryBankState", letterOfCredit.getBeneficiaryBankState());
		requestBody.put("placeOfTakingIncharge", letterOfCredit.getPlaceOfTakingIncharge());
		requestBody.put("portOfLoading", letterOfCredit.getPortOfLoading());
		requestBody.put("portOfDischarge", letterOfCredit.getPortOfDischarge());
		requestBody.put("placeOfFinalDelivery", letterOfCredit.getPlaceOfFinalDelivery());
		requestBody.put("latestShippingDate", letterOfCredit.getLatestShippingDate());
		requestBody.put("presentationPeriod", letterOfCredit.getPresentationPeriod());
		requestBody.put("transshipment", letterOfCredit.getTransshipment());
		requestBody.put("partialShipments", letterOfCredit.getPartialShipments());
		requestBody.put("incoTerms", letterOfCredit.getIncoTerms());
		requestBody.put("modeOfShipment", letterOfCredit.getModeOfShipment());
		requestBody.put("descriptionOfGoods", letterOfCredit.getDescriptionOfGoods());
		requestBody.put("documentsRequired", letterOfCredit.getDocumentsRequired());
		requestBody.put("additionalConditionsCode", letterOfCredit.getAdditionalConditionsCode());
		requestBody.put("otherAdditionalConditions", letterOfCredit.getOtherAdditionalConditions());
		requestBody.put("documentCharges", letterOfCredit.getDocumentCharges());
		requestBody.put("supportDocuments", letterOfCredit.getSupportDocuments());
		requestBody.put("fileToUpload", letterOfCredit.getFileToUpload());
		requestBody.put("confirmationInstruction", letterOfCredit.getConfirmationInstruction());

		requestBody.put("standByLC", letterOfCredit.getStandByLC());
		requestBody.put("transferable", letterOfCredit.getTransferable());
		requestBody.put("screenNumber", letterOfCredit.getScreenNumber());
		if (letterOfCredit.getFlowType().equalsIgnoreCase("finalSubmit")) {
			requestBody.put("isDraft", "false");
			status = letterOfCredit.getSignatoryApprovalRequired() == "true" ? "Pending"
					: TradeFinanceConstants.PARAM_STATUS_SUBMITTED_TO_BANK;
		} else {
			requestBody.put("isDraft", "true");
			status = TradeFinanceConstants.PARAM_STATUS_DRAFT;
		}

		requestBody.put("status", status);
		requestBody.put("additionalPayableCurrency", letterOfCredit.getAdditionalPayableCurrency());

		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);
		// Input Parameters in InputMap
		String requestbody = requestBody.toString().replaceAll("\"", "'");
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceReqStatus", "Success");
		inputMap.put("type", props.getProperty("letterOfCreditsType"));
		inputMap.put("subtype", props.getProperty("letterOfCreditsSubType"));
		inputMap.put("requestBody", requestbody);

		// Set Header Map
		HashMap<String, Object> headerMap = new HashMap<>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		// Making a call to order request API
		String letterOfCreditsResponse = null;
		JSONObject Response = new JSONObject();
		try {
			letterOfCreditsResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to create Letter Of Credit request order " + e);
		}

		if (StringUtils.isNotBlank(letterOfCreditsResponse)) {
			Response = new JSONObject(letterOfCreditsResponse);
			LOG.info("OMS Response " + letterOfCreditsResponse);
		}

		if (Response.has(TradeFinanceConstants.PARAM_ORDER_ID)
				&& StringUtils.isNotBlank(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID))) {
			LCResponse.setSrmsReqOrderID(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID));
			if (letterOfCredit.getFlowType().equalsIgnoreCase(TradeFinanceConstants.PARAM_STATUS_DRAFT)
					&& Response.getString(TradeFinanceConstants.PARAM_ORDER_STATUS).equalsIgnoreCase(TradeFinanceConstants.PARAM_STATUS_SUBMITTED_TO_BANK)) {
				LCResponse.setStatus(TradeFinanceConstants.PARAM_STATUS_DRAFT);
			} else if (letterOfCredit.getFlowType().equalsIgnoreCase("FinalSubmit")
					&& Response.getString(TradeFinanceConstants.PARAM_ORDER_STATUS).equalsIgnoreCase(TradeFinanceConstants.PARAM_STATUS_SUBMITTED_TO_BANK)) {
				LCResponse.setStatus(status);
			}
			request.addRequestParam_("backendEndId", LCResponse.getSrmsReqOrderID());
			request.addRequestParam_("isSrmsFailed", "false");
		}

		else {
			request.addRequestParam_("isSrmsFailed", "true");
		}

		if (Response.has("dbpErrMsg") && StringUtils.isNotBlank(Response.getString("dbpErrMsg"))) {
			LCResponse.setMsg(Response.getString("dbpErrMsg"));
			LOG.error("Unable to create LOC request order Error Message" + Response.getString("dbpErrMsg"));
		}

		if (Response.has("dbpErrCode") && StringUtils.isNotBlank(Response.getString("dbpErrCode"))) {
			LCResponse.setMsg(String.valueOf(Response.getInt("dbpErrCode")));
			LOG.error("Unable to LOC request order Error Code" + Response.getString("dbpErrMsg"));
		}

		return LCResponse;
	}

	public LetterOfCreditsDTO amendLetterOfCredit(LetterOfCreditsDTO letterOfCredit, DataControllerRequest request) {
		LetterOfCreditsDTO LCResponse;
		LCResponse = letterOfCredit;

		JSONObject requestBody = constructRequestPayload(letterOfCredit);
		// Retrieve properties
		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);

		// Covert JSONObject requestbody to String
		String requestbody = requestBody.toString().replaceAll("\"", "'");
		// covert into input map
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceReqStatus", "Success");
		inputMap.put("requestBody", requestbody);
		inputMap.put("type", props.getProperty("LetterOfCreditsAmendmentType"));
		inputMap.put("subtype", props.getProperty("LetterOfCreditsAmendmentSubType"));

		HashMap<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		// Making a call to order request API
		String letterOfCreditsResponse = null;
		JSONObject Response = new JSONObject();
		try {
			letterOfCreditsResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_CREATEORDER.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to amend Letter Of Credit " + e);
		}

		// Converting response into JSONObject
		if (StringUtils.isNotBlank(letterOfCreditsResponse)) {
			Response = new JSONObject(letterOfCreditsResponse);
			LOG.info("OMS Response " + letterOfCreditsResponse);
		}

		if (Response.has(TradeFinanceConstants.PARAM_ORDER_ID)
				&& StringUtils.isNotBlank(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID))) {
			LCResponse.setSrmsReqOrderID(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID));
			LCResponse.setAmendmentReference(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID));
			LCResponse.setAmendmentDate(java.time.LocalDate.now().toString());
			request.addRequestParam_("backendEndId", LCResponse.getSrmsReqOrderID());
			request.addRequestParam_("isSrmsFailed", "false");

		} else {
			request.addRequestParam_("isSrmsFailed", "true");
		}

		if (Response.has("dbpErrMsg") && StringUtils.isNotBlank(Response.getString("dbpErrMsg"))) {
			LCResponse.setMsg(Response.getString("dbpErrMsg"));
			LOG.error("Unable to amend LOC request order Error Message" + Response.getString("dbpErrMsg"));
		}

		if (Response.has("dbpErrCode") && StringUtils.isNotBlank(Response.getString("dbpErrCode"))) {
			LCResponse.setMsg(String.valueOf(Response.getInt("dbpErrCode")));
			LOG.error("Unable to amend LOC request order Error Code" + Response.getString("dbpErrMsg"));
		}

		return LCResponse;
	}

	public JSONObject constructRequestPayload(LetterOfCreditsDTO letterOfCredit) {
		JSONObject requestBody = new JSONObject();
		if (StringUtils.isNotEmpty(letterOfCredit.getLcReferenceNo())) {
			requestBody.put("lcReferenceNo", letterOfCredit.getLcReferenceNo());
		}

		if (StringUtils.isNotEmpty(letterOfCredit.getLcCurrency())) {
			requestBody.put("lcCurrency", letterOfCredit.getLcCurrency());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getPaymentTerms())) {
			requestBody.put("paymentTerms", letterOfCredit.getPaymentTerms());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getExpiryDate())) {
			requestBody.put("expiryDate", letterOfCredit.getExpiryDate());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getLatestShippingDate())) {
			requestBody.put("latestShippingDate", letterOfCredit.getLatestShippingDate());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getPresentationPeriod())) {
			requestBody.put("presentationPeriod", letterOfCredit.getPresentationPeriod());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getChargesAccount())) {
			requestBody.put("chargesAccount", letterOfCredit.getChargesAccount());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getChargesPaid())) {
			requestBody.put("chargesPaid", letterOfCredit.getChargesPaid());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getBeneficiaryName())) {
			requestBody.put("beneficiaryName", letterOfCredit.getBeneficiaryName());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getIssueDate())) {
			requestBody.put("issueDate", letterOfCredit.getIssueDate());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getAmountType())) {
			requestBody.put("amountType", letterOfCredit.getAmountType());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getOtherAmendments())) {
			requestBody.put("otherAmendments", letterOfCredit.getOtherAmendments());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getAmendCharges())) {
			requestBody.put("amendCharges", letterOfCredit.getAmendCharges());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getCreditAmount())) {
			requestBody.put("creditAmount", letterOfCredit.getCreditAmount());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getAmendmentExpiryDate())) {
			requestBody.put("amendmentExpiryDate", letterOfCredit.getAmendmentExpiryDate());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getAmendStatus())) {
			requestBody.put("amendStatus", letterOfCredit.getAmendStatus());
		}
		if (StringUtils.isNotEmpty(letterOfCredit.getLcSRMSId())) {
			requestBody.put("lcSRMSId", letterOfCredit.getLcSRMSId());
		}
		requestBody.put("lcAmount", String.valueOf(letterOfCredit.getLcAmount()));
		return requestBody;
	}

	@Override
	public LetterOfCreditsDTO updateAmendLC(LetterOfCreditsDTO updateLCDTObyid, DataControllerRequest request) {
		LetterOfCreditsDTO LCResponse;
		LCResponse = updateLCDTObyid;
		JSONObject requestBody = constructRequestPayload(updateLCDTObyid);

		// Covert JSONObject requestbody to String
		String requestbodystring = requestBody.toString().replaceAll("\"", "'");

		// loading property
		Properties props = TradeFinanceProperties.loadProps(TradeFinanceConstants.PARAM_PROPERTY);

		// covert into input map
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceRequestId",updateLCDTObyid.getAmendmentReference());
		inputMap.put("requestBody", requestbodystring);

		HashMap<String, Object> headerMap = new HashMap<>();
		headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
		headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		// Making a call to order request API
		String letterOfCreditsResponse = null;
		JSONObject Response = new JSONObject();
		try {
			letterOfCreditsResponse = DBPServiceExecutorBuilder.builder()
					.withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getServiceName())
					.withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getOperationName())
					.withRequestParameters(inputMap).withRequestHeaders(headerMap).withDataControllerRequest(request)
					.build().getResponse();

		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to update Letter Of Credits request order in SRMS" + e);
		}
		if (StringUtils.isNotBlank(letterOfCreditsResponse)) {
			Response = new JSONObject(letterOfCreditsResponse);
			LOG.info("OMS Response " + letterOfCreditsResponse);
		}
		if (Response.has(TradeFinanceConstants.PARAM_ORDER_ID)
				&& StringUtils.isNotBlank(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID))) {
			LCResponse.setSrmsReqOrderID(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID));
			request.addRequestParam_("backendEndId", LCResponse.getSrmsReqOrderID());
			request.addRequestParam_("isSrmsFailed", "false");

		} else {
			request.addRequestParam_("isSrmsFailed", "true");
		}

		if (Response.has("dbpErrMsg") && StringUtils.isNotBlank(Response.getString("dbpErrMsg"))) {
			LCResponse.setMsg(Response.getString("dbpErrMsg"));
			LOG.error("Unable to update amend LOC request order Error Message" + Response.getString("dbpErrMsg"));
		}

		if (Response.has("dbpErrCode") && StringUtils.isNotBlank(Response.getString("dbpErrCode"))) {
			LCResponse.setMsg(String.valueOf(Response.getInt("dbpErrCode")));
			LOG.error("Unable to update amend LOC request order Error Code" + Response.getString("dbpErrMsg"));
		}
		return LCResponse;
     }
}
