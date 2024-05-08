package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GetLetterOfCreditsByIdBackendDelegate;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.UpdateLetterOfCreditsBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class UpdateLetterOfCreditsBackendDelegateImpl implements UpdateLetterOfCreditsBackendDelegate{
	private static final Logger LOG = LogManager.getLogger(UpdateLetterOfCreditsBackendDelegateImpl.class);

	public LetterOfCreditsDTO updateLetterOfCreditsOrder(LetterOfCreditsDTO letterOfCredit,
			DataControllerRequest request) throws ApplicationException {
		
		String status = "";
		GetLetterOfCreditsByIdBackendDelegate getLetterOfCreditsByIdBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(GetLetterOfCreditsByIdBackendDelegate.class);
		
		LetterOfCreditsDTO LCResponse = new LetterOfCreditsDTO();
		JSONObject requestParameters = new JSONObject();
		if(letterOfCredit.getStatus().equalsIgnoreCase("Delete")) {
			letterOfCredit=getLetterOfCreditsByIdBackendDelegate.getSRMSID(letterOfCredit, request); 
			letterOfCredit.setStatus("Delete");
			letterOfCredit.setFlowType("");
			letterOfCredit.setIsDraft("");
		}
		requestParameters.put("lcReferenceNo", letterOfCredit.getLcReferenceNo());
		requestParameters.put("srmsReqOrderID", letterOfCredit.getSrmsReqOrderID());
		requestParameters.put("lcAmount", String.valueOf(letterOfCredit.getLcAmount()));
		requestParameters.put("lcCurrency", letterOfCredit.getLcCurrency());
		requestParameters.put("tolerancePercentage", letterOfCredit.getTolerancePercentage());
		requestParameters.put("maximumCreditAmount", String.valueOf(letterOfCredit.getMaximumCreditAmount()));
		requestParameters.put("additionalAmountPayable", letterOfCredit.getAdditionalAmountPayable());
		requestParameters.put("paymentTerms", letterOfCredit.getPaymentTerms());
		requestParameters.put("availableWith1", letterOfCredit.getAvailableWith1());
		requestParameters.put("availableWith2", letterOfCredit.getAvailableWith2());
		requestParameters.put("availableWith3", letterOfCredit.getAvailableWith3());
		requestParameters.put("availableWith4", letterOfCredit.getAvailableWith4());
		requestParameters.put("issueDate", letterOfCredit.getIssueDate());
		requestParameters.put("expiryDate", letterOfCredit.getExpiryDate());
		requestParameters.put("expiryPlace", letterOfCredit.getExpiryPlace());
		requestParameters.put("chargesAccount", letterOfCredit.getChargesAccount());
		requestParameters.put("commisionAccount", letterOfCredit.getCommisionAccount());
		requestParameters.put("marginAccount", letterOfCredit.getMarginAccount());
		requestParameters.put("messageToBank", letterOfCredit.getMessageToBank());
		requestParameters.put("beneficiaryName", letterOfCredit.getBeneficiaryName());
		requestParameters.put("beneficiaryAddressLine1", letterOfCredit.getBeneficiaryAddressLine1());
		requestParameters.put("beneficiaryAddressLine2", letterOfCredit.getBeneficiaryAddressLine2());
		requestParameters.put("beneficiaryPostCode", letterOfCredit.getBeneficiaryPostCode());
		requestParameters.put("beneficiaryCountry", letterOfCredit.getBeneficiaryCountry());
		requestParameters.put("beneficiaryCity", letterOfCredit.getBeneficiaryCity());
		requestParameters.put("beneficiaryState", letterOfCredit.getBeneficiaryState());
		requestParameters.put("beneficiaryBank", letterOfCredit.getBeneficiaryBank());
		requestParameters.put("beneficiaryBankAdressLine1", letterOfCredit.getBeneficiaryBankAdressLine1());
		requestParameters.put("beneficiaryBankAdressLine2", letterOfCredit.getBeneficiaryBankAdressLine2());
		requestParameters.put("beneficiaryBankPostCode", letterOfCredit.getBeneficiaryBankPostCode());
		requestParameters.put("beneficiaryBankCountry", letterOfCredit.getBeneficiaryBankCountry());
		requestParameters.put("beneficiaryBankCity", letterOfCredit.getBeneficiaryBankCity());
		requestParameters.put("beneficiaryBankState", letterOfCredit.getBeneficiaryBankState());
		requestParameters.put("placeOfTakingIncharge", letterOfCredit.getPlaceOfTakingIncharge());
		requestParameters.put("portOfLoading", letterOfCredit.getPortOfLoading());
		requestParameters.put("portOfDischarge", letterOfCredit.getPortOfDischarge());
		requestParameters.put("placeOfFinalDelivery", letterOfCredit.getPlaceOfFinalDelivery());
		requestParameters.put("latestShippingDate", letterOfCredit.getLatestShippingDate());
		requestParameters.put("presentationPeriod", letterOfCredit.getPresentationPeriod());
		requestParameters.put("transshipment", letterOfCredit.getTransshipment());
		requestParameters.put("partialShipments", letterOfCredit.getPartialShipments());
		requestParameters.put("incoTerms", letterOfCredit.getIncoTerms());
		requestParameters.put("modeOfShipment", letterOfCredit.getModeOfShipment());
		requestParameters.put("descriptionOfGoods", letterOfCredit.getDescriptionOfGoods());
		requestParameters.put("documentsRequired", letterOfCredit.getDocumentsRequired());
		requestParameters.put("additionalConditionsCode", letterOfCredit.getAdditionalConditionsCode());
		requestParameters.put("otherAdditionalConditions", letterOfCredit.getOtherAdditionalConditions());
		requestParameters.put("documentCharges", letterOfCredit.getDocumentCharges());
		requestParameters.put("supportDocuments", letterOfCredit.getSupportDocuments());
		requestParameters.put("fileToUpload", letterOfCredit.getFileToUpload());
		requestParameters.put("confirmationInstruction", letterOfCredit.getConfirmationInstruction());
		requestParameters.put("transferable", letterOfCredit.getTransferable());
		requestParameters.put("standByLC", letterOfCredit.getStandByLC());
		requestParameters.put("screenNumber",letterOfCredit.getScreenNumber());
		
		if (letterOfCredit.getFlowType().equalsIgnoreCase("finalSubmit")) {
			requestParameters.put("isDraft", "false");
			status = letterOfCredit.getSignatoryApprovalRequired() == "true" ? "Pending" : TradeFinanceConstants.PARAM_STATUS_SUBMITTED_TO_BANK;
			}
		else if(letterOfCredit.getFlowType().equalsIgnoreCase("BankUpdate")) {
			requestParameters.put("isDraft", "false");
			status = letterOfCredit.getStatus();
		}
		else {
			requestParameters.put("isDraft", "true");
			status = TradeFinanceConstants.PARAM_STATUS_DRAFT;
		}
		if(letterOfCredit.getStatus().equalsIgnoreCase("Delete")) {
			status = TradeFinanceConstants.PARAM_STATUS_DELETE;
		}
		requestParameters.put("status", status);
		
		requestParameters.put("additionalPayableCurrency", letterOfCredit.getAdditionalPayableCurrency());
		String requestbody = requestParameters.toString().replaceAll("\"", "'");
		
		
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceRequestId", requestParameters.get("srmsReqOrderID"));
		inputMap.put("requestBody", requestbody);

		
		// Set Header Map
        HashMap<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
        headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));

		String letterOfCreditsResponse = null;
		JSONObject Response = new JSONObject();
		try {
			letterOfCreditsResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getServiceName()).
					withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_UPDATEORDER.getOperationName()).
					withRequestParameters(inputMap).
					withRequestHeaders(headerMap).
					withDataControllerRequest(request).
					build().getResponse();
			
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to update Letter Of Credits request order " + e);
		}
		if (StringUtils.isNotBlank(letterOfCreditsResponse)) {
			Response = new JSONObject(letterOfCreditsResponse);
			LOG.info("OMS Response " + letterOfCreditsResponse);
		}
		
		if (Response.has(TradeFinanceConstants.PARAM_ORDER_ID)
				&& StringUtils.isNotBlank(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID))) {
			LCResponse.setSrmsReqOrderID(Response.getString(TradeFinanceConstants.PARAM_ORDER_ID));
			if(letterOfCredit.getStatus().equalsIgnoreCase("Delete") && Response.getString(TradeFinanceConstants.PARAM_ORDER_STATUS).equalsIgnoreCase("Self Approved")) {
				LCResponse.setStatus(TradeFinanceConstants.PARAM_STATUS_DELETE);
			}
			else if(letterOfCredit.getFlowType().equalsIgnoreCase("Draft") && Response.getString(TradeFinanceConstants.PARAM_ORDER_STATUS).equalsIgnoreCase("Self Approved")){
				LCResponse.setStatus(TradeFinanceConstants.PARAM_STATUS_DRAFT);
			}
			else if(letterOfCredit.getFlowType().equalsIgnoreCase("FinalSubmit") && Response.getString(TradeFinanceConstants.PARAM_ORDER_STATUS).equalsIgnoreCase("Self Approved")) {
				LCResponse.setStatus(status);
			}
			request.addRequestParam_("isSrmsFailed", "false");
			request.addRequestParam_("backendEndId",LCResponse.getSrmsReqOrderID());
		}

		else {
			request.addRequestParam_("isSrmsFailed", "true");
		}

		if (Response.has("dbpErrMsg") && StringUtils.isNotBlank(Response.getString("dbpErrMsg"))) {
			LCResponse.setMsg(Response.getString("dbpErrMsg"));
			LOG.error("Unable to create cheque book request order Error Message" + Response.getString("dbpErrMsg"));
		}

		if (Response.has("dbpErrCode")) {
			LCResponse.setMsg(String.valueOf(Response.get("dbpErrCode")));
			LOG.error("Unable to create cheque book request order Error Code" + Response.getString("dbpErrMsg"));
		}
		
		return LCResponse;
	}
	
	

	
	
}
