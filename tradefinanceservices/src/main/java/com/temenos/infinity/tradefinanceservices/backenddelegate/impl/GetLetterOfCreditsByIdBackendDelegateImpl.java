package com.temenos.infinity.tradefinanceservices.backenddelegate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.tradefinanceservices.backenddelegate.api.GetLetterOfCreditsByIdBackendDelegate;
import com.temenos.infinity.tradefinanceservices.config.TradeFinanceAPIServices;
import com.temenos.infinity.tradefinanceservices.constants.TradeFinanceConstants;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;

public class GetLetterOfCreditsByIdBackendDelegateImpl implements GetLetterOfCreditsByIdBackendDelegate,TradeFinanceConstants{
	private static final Logger LOG = LogManager.getLogger(GetLetterOfCreditsByIdBackendDelegateImpl.class);

	public LetterOfCreditsDTO getSRMSID(LetterOfCreditsDTO letterOfCredit,DataControllerRequest request) 
			throws ApplicationException {
		LetterOfCreditsDTO order = new LetterOfCreditsDTO();
		String lcReferenceNo_old = letterOfCredit.getLcReferenceNo();
		String response = null;
		String return_response = "";
		List<String> LOC=new ArrayList<String>();
		JSONObject Response = new JSONObject();
		Map<String, Object> inputMap = new HashMap<>();
		inputMap.put("serviceRequestIds", letterOfCredit.getSrmsReqOrderID());
		
		
		// Set Header Map
        HashMap<String, Object> headerMap = new HashMap<String, Object>();
        headerMap.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
        headerMap.put("X-Kony-ReportingParams", request.getHeader("X-Kony-ReportingParams"));
		
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.
					getServiceName()).withOperationId(TradeFinanceAPIServices.SERVICEREQUESTJAVA_GETSERVICEREQUESTBYID.getOperationName()).withRequestParameters(inputMap).
					withRequestHeaders(headerMap).
					withDataControllerRequest(request).
					build().getResponse();
			
		} catch (Exception e) {
			request.addRequestParam_("isSrmsFailed", "true");
			LOG.error("Unable to GET SRMS ID " + e);
		}
		if (StringUtils.isNotBlank(response)) {
			Response = new JSONObject(response);
			LOG.info("OMS Response " + response);
		}
		//for validating requestId with input Id
		if (Response.has("lcReferenceNo") && StringUtils.isNotBlank(Response.getString("lcReferenceNo"))) {
			if(letterOfCredit.getSrmsReqOrderID().equals(Response.getString(TradeFinanceConstants.PARAM_REFERENCE_ID))) {
				letterOfCredit.setReferenceNomatch(true);
			}
			else {
				LOG.info("ReferenceNo doesnot match");
				letterOfCredit.setReferenceNomatch(false);
			}
		}
		
		//
        JSONArray Orders = Response.getJSONArray(PARAM_SERVICE_REQUESTS);
        for (int i = 0; i < Orders.length(); i++) {
        	
        	//do logic to insert data here
			JSONObject singleOrder = Orders.getJSONObject(i);
			if (singleOrder.has(PARAM_INPUT_PAYLOAD)) {
				JSONObject payload = singleOrder.getJSONObject(PARAM_INPUT_PAYLOAD);

				String lcReferenceNo = payload.has(PARAM_LC_REFERENCE_NO) ? payload.getString(TradeFinanceConstants.PARAM_LC_REFERENCE_NO): "";
				String lcAmount = payload.has(PARAM_LC_AMOUNT) ? payload.getString(PARAM_LC_AMOUNT) : "";
				String lcCurrency = payload.has(PARAM_LC_CURRENCY) ? payload.getString(PARAM_LC_CURRENCY) : "";
				String tolerancePercentage = payload.has(PARAM_TOLERENCEPERCENTAGE)
						? payload.getString(PARAM_TOLERENCEPERCENTAGE)
						: "";
				String maximumCreditAmount = payload.has(PARAM_MAXIMUMCREDITAMOUNT)
						? payload.getString(PARAM_MAXIMUMCREDITAMOUNT)
						: "";
				String additionalAmountPayable = payload.has(PARAM_ADDITIONALAMOUNTPAYABLE)
						? payload.getString(PARAM_ADDITIONALAMOUNTPAYABLE)
						: "";
				String paymentTerms = payload.has(PARAM_PAYMENTTERMS) ? payload.getString(PARAM_PAYMENTTERMS) : "";
				String availableWith1 = payload.has(PARAM_AVAILABLEWITH1) ? payload.getString(PARAM_AVAILABLEWITH1)
						: "";
				String availableWith2 = payload.has(PARAM_AVAILABLEWITH2) ? payload.getString(PARAM_AVAILABLEWITH2)
						: "";
				String availableWith3 = payload.has(PARAM_AVAILABLEWITH3) ? payload.getString(PARAM_AVAILABLEWITH3)
						: "";
				String availableWith4 = payload.has(PARAM_AVAILABLEWITH4) ? payload.getString(PARAM_AVAILABLEWITH4)
						: "";
				String issueDate = payload.has(PARAM_ISSUEDATE) ? payload.getString(PARAM_ISSUEDATE) : "";
				String expiryDate = payload.has(PARAM_EXPIRYDATE) ? payload.getString(PARAM_EXPIRYDATE) : "";
				String expiryPlace = payload.has(PARAM_EXPIRYPLACE) ? payload.getString(PARAM_EXPIRYPLACE) : "";
				String chargesAccount = payload.has(PARAM_CHARGEAMOUNT) ? payload.getString(PARAM_CHARGEAMOUNT)
						: "";
				String commisionAccount = payload.has(PARAM_COMMISSIONACCOUNT)
						? payload.getString(PARAM_COMMISSIONACCOUNT)
						: "";
				String marginAccount = payload.has(PARAM_MARGINAMOUNT) ? payload.getString(PARAM_MARGINAMOUNT) : "";
				String messageToBank = payload.has(PARAM_MESSAGETOBANK) ? payload.getString(PARAM_MESSAGETOBANK)
						: "";
				String beneficiaryName = payload.has(PARAM_BENEFICIARYNAME)
						? payload.getString(PARAM_BENEFICIARYNAME)
						: "";
				String beneficiaryAddressLine1 = payload.has(PARAM_BENEFICIARYADDRESSLINE1)
						? payload.getString(PARAM_BENEFICIARYADDRESSLINE1)
						: "";
				String beneficiaryAddressLine2 = payload.has(PARAM_BENEFICIARYADDRESSLINE2)
						? payload.getString(PARAM_BENEFICIARYADDRESSLINE2)
						: "";
				String beneficiaryPostCode = payload.has(PARAM_BENEFICIARYPOSTCODE)
						? payload.getString(PARAM_BENEFICIARYPOSTCODE)
						: "";
				String beneficiaryCountry = payload.has(PARAM_BENEFICIARYCOUNTRY)
						? payload.getString(PARAM_BENEFICIARYCOUNTRY)
						: "";
				String beneficiaryCity = payload.has(PARAM_BENEFICIARYCITY)
						? payload.getString(PARAM_BENEFICIARYCITY)
						: "";
				String beneficiaryState = payload.has(PARAM_BENEFICIARYSTATE)
						? payload.getString(PARAM_BENEFICIARYSTATE)
						: "";
				String beneficiaryBank = payload.has(PARAM_BENEFICIARYBANK)
						? payload.getString(PARAM_BENEFICIARYBANK)
						: "";
				String beneficiaryBankAdressLine1 = payload.has(PARAM_BENEFICIARY_BANK_ADDRESS_LINE1)
						? payload.getString(PARAM_BENEFICIARY_BANK_ADDRESS_LINE1)
						: "";
				String beneficiaryBankAdressLine2 = payload.has(PARAM_BENEFICIARY_BANK_ADDRESS_LINE2)
						? payload.getString(PARAM_BENEFICIARY_BANK_ADDRESS_LINE2)
						: "";
				String beneficiaryBankPostCode = payload.has(PARAM_BENEFICIARY_BANK_POST_CODE)
						? payload.getString(PARAM_BENEFICIARY_BANK_POST_CODE)
						: "";
				String beneficiaryBankCountry = payload.has(PARAM_BENEFICIARY_BANK_COUNTRY)
						? payload.getString(PARAM_BENEFICIARY_BANK_COUNTRY)
						: "";
				String beneficiaryBankCity = payload.has(PARAM_BENEFICIARY_BANK_CITY)
						? payload.getString(PARAM_BENEFICIARY_BANK_CITY)
						: "";
				String beneficiaryBankState = payload.has(PARAM_BENEFICIARY_BANK_STATE)
						? payload.getString(PARAM_BENEFICIARY_BANK_STATE)
						: "";
				String placeOfTakingIncharge = payload.has(PARAM_PLACE_OF_TAKING_INCHARGE)
						? payload.getString(PARAM_PLACE_OF_TAKING_INCHARGE)
						: "";
				String portOfLoading = payload.has(PARAM_PORT_OF_LOADING) ? payload.getString(PARAM_PORT_OF_LOADING)
						: "";
				String portOfDischarge = payload.has(PARAM_PORT_OF_DISCHARGE)
						? payload.getString(PARAM_PORT_OF_DISCHARGE)
						: "";
				String placeOfFinalDelivery = payload.has(PARAM_PLACE_OF_FINAL_DELIVERY)
						? payload.getString(PARAM_PLACE_OF_FINAL_DELIVERY)
						: "";
				String latestShippingDate = payload.has(PARAM_LATEST_SHIPPING_DATE)
						? payload.getString(PARAM_LATEST_SHIPPING_DATE)
						: "";
				String presentationPeriod = payload.has(PARAM_PRESENTATION_PERIOD)
						? payload.getString(PARAM_PRESENTATION_PERIOD)
						: "";
				String transshipment = payload.has(PARAM_TRANS_SHIPMENT) ? payload.getString(PARAM_TRANS_SHIPMENT)
						: "";
				String partialShipments = payload.has(PARAM_PARTIAL_SHIPMENT)
						? payload.getString(PARAM_PARTIAL_SHIPMENT)
						: "";
				String incoTerms = payload.has(PARAM_INCO_TERMS) ? payload.getString(PARAM_INCO_TERMS) : "";
				String modeOfShipment = payload.has(PARAM_MODE_OF_SHIPMENT)
						? payload.getString(PARAM_MODE_OF_SHIPMENT)
						: "";
				String descriptionOfGoods = payload.has(PARAM_DESCRIPTION_OF_GOODS)
						? payload.getString(PARAM_DESCRIPTION_OF_GOODS)
						: "";
				String documentsRequired = payload.has(PARAM_DOCUMENTS_REQUIRED)
						? payload.getString(PARAM_DOCUMENTS_REQUIRED)
						: "";
				String additionalConditionsCode = payload.has(PARAM_ADDITIONAL_CONDITIONS_CODE)
						? payload.getString(PARAM_ADDITIONAL_CONDITIONS_CODE)
						: "";
				String otherAdditionalConditions = payload.has(PARAM_OTHER_ADDITIONAL_CONDITIONS)
						? payload.getString(PARAM_OTHER_ADDITIONAL_CONDITIONS)
						: "";
				String documentCharges = payload.has(PARAM_DOCUMENT_CHARGES)
						? payload.getString(PARAM_DOCUMENT_CHARGES)
						: "";
				String supportDocuments = payload.has(PARAM_SUPPORT_DOCUMENTS)
						? payload.getString(PARAM_SUPPORT_DOCUMENTS)
						: "";
				String fileToUpload = payload.has(PARAM_FILE_TO_UPLOAD) ? payload.getString(PARAM_FILE_TO_UPLOAD)
						: "";
				String confirmationInstruction = payload.has(PARAM_CONFIRMATION_INSTRUCTION)
						? payload.getString(PARAM_CONFIRMATION_INSTRUCTION)
						: "";
				String isDraft = payload.has(PARAM_IS_DRAFT) ? payload.getString(PARAM_IS_DRAFT) : "";
				String additionalPayableCurrency = payload.has(PARAM_ADDITIONAL_PAYABLE_CURRENCY)
						? payload.getString(PARAM_ADDITIONAL_PAYABLE_CURRENCY)
						: "";
				String transferable = payload.has(PARAM_TRANSFERABLE) ? payload.getString(PARAM_TRANSFERABLE)
						: "";
				String standByLC = payload.has(PARAM_STAND_BY_LC) ? payload.getString(PARAM_STAND_BY_LC) : "";
				String status = payload.has(PARAM_ORDER_STATUS) ? payload.getString(PARAM_ORDER_STATUS) : "";
				
				if (StringUtils.isNotBlank(status)) {
					order.setStatus(status);
				}
				if (StringUtils.isNotBlank(lcReferenceNo)) {
					order.setLcReferenceNo(lcReferenceNo);
				}
				if (StringUtils.isNotBlank(lcAmount)) {
					order.setLcAmount(Double.parseDouble(lcAmount));
				}
				if (StringUtils.isNotBlank(lcCurrency)) {
					order.setLcCurrency(lcCurrency);
				}

				if (StringUtils.isNotBlank(tolerancePercentage)) {
					order.setTolerancePercentage(tolerancePercentage);
				}
				if (StringUtils.isNotBlank(maximumCreditAmount)) {
					order.setMaximumCreditAmount(Double.parseDouble(maximumCreditAmount));
				}
				if (StringUtils.isNotBlank(additionalAmountPayable)) {
					order.setAdditionalAmountPayable(additionalAmountPayable);
				}
				if (StringUtils.isNotBlank(paymentTerms)) {
					order.setPaymentTerms(paymentTerms);
				}
				if (StringUtils.isNotBlank(availableWith1)) {
					order.setAvailableWith1(availableWith1);
				}
				if (StringUtils.isNotBlank(availableWith2)) {
					order.setAvailableWith2(availableWith2);
				}
				if (StringUtils.isNotBlank(availableWith3)) {
					order.setAvailableWith3(availableWith3);
				}
				if (StringUtils.isNotBlank(availableWith4)) {
					order.setAvailableWith4(availableWith4);
				}
				if (StringUtils.isNotBlank(issueDate)) {
					order.setIssueDate(issueDate);
				}
				if (StringUtils.isNotBlank(expiryDate)) {
					order.setExpiryDate(expiryDate);
				}
				if (StringUtils.isNotBlank(expiryPlace)) {
					order.setExpiryPlace(expiryPlace);
				}
				if (StringUtils.isNotBlank(chargesAccount)) {
					order.setChargesAccount(chargesAccount);
				}
				if (StringUtils.isNotBlank(commisionAccount)) {
					order.setCommisionAccount(commisionAccount);
				}
				if (StringUtils.isNotBlank(marginAccount)) {
					order.setMarginAccount(marginAccount);
				}
				if (StringUtils.isNotBlank(messageToBank)) {
					order.setMessageToBank(messageToBank);
				}
				if (StringUtils.isNotBlank(beneficiaryName)) {
					order.setBeneficiaryName(beneficiaryName);
				}
				if (StringUtils.isNotBlank(beneficiaryAddressLine1)) {
					order.setBeneficiaryAddressLine1(beneficiaryAddressLine1);
				}
				if (StringUtils.isNotBlank(beneficiaryAddressLine2)) {
					order.setBeneficiaryAddressLine2(beneficiaryAddressLine2);
				}
				if (StringUtils.isNotBlank(beneficiaryPostCode)) {
					order.setBeneficiaryPostCode(beneficiaryPostCode);
				}
				if (StringUtils.isNotBlank(beneficiaryCountry)) {
					order.setBeneficiaryCountry(beneficiaryCountry);
				}
				if (StringUtils.isNotBlank(beneficiaryCity)) {
					order.setBeneficiaryCity(beneficiaryCity);
				}
				if (StringUtils.isNotBlank(beneficiaryState)) {
					order.setBeneficiaryState(beneficiaryState);
				}
				if (StringUtils.isNotBlank(beneficiaryBank)) {
					order.setBeneficiaryBank(beneficiaryBank);
				}
				if (StringUtils.isNotBlank(beneficiaryBankAdressLine1)) {
					order.setBeneficiaryBankAdressLine1(beneficiaryBankAdressLine1);
				}
				if (StringUtils.isNotBlank(beneficiaryBankAdressLine2)) {
					order.setBeneficiaryBankAdressLine2(beneficiaryBankAdressLine2);
				}
				if (StringUtils.isNotBlank(beneficiaryBankPostCode)) {
					order.setBeneficiaryBankPostCode(beneficiaryBankPostCode);
				}
				if (StringUtils.isNotBlank(beneficiaryBankCountry)) {
					order.setBeneficiaryBankCountry(beneficiaryBankCountry);
				}
				if (StringUtils.isNotBlank(beneficiaryBankCity)) {
					order.setBeneficiaryBankCity(beneficiaryBankCity);
				}
				if (StringUtils.isNotBlank(beneficiaryBankState)) {
					order.setBeneficiaryBankState(beneficiaryBankState);
				}
				if (StringUtils.isNotBlank(placeOfTakingIncharge)) {
					order.setPlaceOfTakingIncharge(placeOfTakingIncharge);
				}
				if (StringUtils.isNotBlank(portOfLoading)) {
					order.setPortOfLoading(portOfLoading);
				}
				if (StringUtils.isNotBlank(portOfDischarge)) {
					order.setPortOfDischarge(portOfDischarge);
				}
				if (StringUtils.isNotBlank(placeOfFinalDelivery)) {
					order.setPlaceOfFinalDelivery(placeOfFinalDelivery);
				}
				if (StringUtils.isNotBlank(latestShippingDate)) {
					order.setLatestShippingDate(latestShippingDate);
				}
				if (StringUtils.isNotBlank(presentationPeriod)) {
					order.setPresentationPeriod(presentationPeriod);
				}
				if (StringUtils.isNotBlank(transshipment)) {
					order.setTransshipment(transshipment);
				}
				if (StringUtils.isNotBlank(partialShipments)) {
					order.setPartialShipments(partialShipments);
				}
				if (StringUtils.isNotBlank(incoTerms)) {
					order.setIncoTerms(incoTerms);
				}
				if (StringUtils.isNotBlank(modeOfShipment)) {
					order.setModeOfShipment(modeOfShipment);
				}
				if (StringUtils.isNotBlank(descriptionOfGoods)) {
					order.setDescriptionOfGoods(descriptionOfGoods);
				}
				if (StringUtils.isNotBlank(documentsRequired)) {
					order.setDocumentsRequired(documentsRequired);
				}
				if (StringUtils.isNotBlank(additionalConditionsCode)) {
					order.setAdditionalConditionsCode(additionalConditionsCode);
				}
				if (StringUtils.isNotBlank(otherAdditionalConditions)) {
					order.setOtherAdditionalConditions(otherAdditionalConditions);
				}
				if (StringUtils.isNotBlank(documentCharges)) {
					order.setDocumentCharges(documentCharges);
				}
				if (StringUtils.isNotBlank(supportDocuments)) {
					order.setSupportDocuments(supportDocuments);
				}
				if (StringUtils.isNotBlank(fileToUpload)) {
					order.setFileToUpload(fileToUpload);
				}
				if (StringUtils.isNotBlank(confirmationInstruction)) {
					order.setConfirmationInstruction(confirmationInstruction);
				}
				if (StringUtils.isNotBlank(transferable)) {
					order.setTransferable(transferable);
				}
				if (StringUtils.isNotBlank(standByLC)) {
					order.setStandByLC(standByLC);
				}
				if (StringUtils.isNotBlank(isDraft)) {
					order.setIsDraft(isDraft);
				}
				if (StringUtils.isNotBlank(additionalPayableCurrency)) {
					order.setAdditionalPayableCurrency(additionalPayableCurrency);
				}
			}
			
			if (singleOrder.has(PARAM_SERVICE_REQ_ID)) {
			String srmsId = singleOrder.has(PARAM_SERVICE_REQ_ID) ? singleOrder.getString(PARAM_SERVICE_REQ_ID) : "";
			String error_code = singleOrder.has("returnCode") ? singleOrder.getString("returnCode") : "";
			String additionalErrorInfo = singleOrder.has("additionalErrorInfo") ? singleOrder.getString("additionalErrorInfo") : "";
			
			if(singleOrder.has("partyId") && StringUtils.isNotBlank(singleOrder.getString("partyId"))) order.setCustomerId(singleOrder.getString("partyId")); 
			
			if (StringUtils.isNotBlank(srmsId)) {
				order.setSrmsReqOrderID(srmsId);
			}
			
			if(StringUtils.isNotBlank(error_code)) {
				order.setErrorCode(error_code);
			}
			if(StringUtils.isNotBlank(additionalErrorInfo)) {
				order.setErrorMessage(additionalErrorInfo);
			}
			
        }
//			return_response = order.JSON_object();
//			LOC.add(return_response);
			
		}
		if (Response.has("errmsg") && StringUtils.isNotBlank(Response.getString("errmsg"))) {
			order.setErrorMsgSRMSmatch(Response.getString("errmsg"));
			LOG.error("Unable to create LOC request order Error Message" + Response.getString("errmsg"));
		}
		order.setReferenceNomatch(true);

		return order;
	}
}
