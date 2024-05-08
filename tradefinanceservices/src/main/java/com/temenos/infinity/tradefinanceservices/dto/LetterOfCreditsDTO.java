package com.temenos.infinity.tradefinanceservices.dto;

import com.dbp.core.api.DBPDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.kony.dbputilities.util.HelperMethods;
import com.temenos.dbx.product.constants.Constants;
import org.apache.commons.lang3.StringUtils;

import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils._formatDate;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.formatAmount;

import java.io.Serializable;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;

@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LetterOfCreditsDTO implements Serializable, DBPDTO {

	private static final long serialVersionUID = 653792502420487768L;

	private String lcReferenceNo;
	private double lcAmount;
	private String lcCurrency;
	private String tolerancePercentage;
	private double maximumCreditAmount;
	private String additionalAmountPayable;
	private String paymentTerms;
	private String availableWith1;
	private String availableWith2;
	private String availableWith3;
	private String availableWith4;
	private String issueDate;
	private String expiryDate;
	private String expiryPlace;
	private String chargesAccount;
	private String commisionAccount;
	private String marginAccount;
	private String messageToBank;
	private String beneficiaryName;
	private String beneficiaryAddressLine1;
	private String beneficiaryAddressLine2;
	private String beneficiaryPostCode;
	private String beneficiaryCountry;
	private String beneficiaryCity;
	private String beneficiaryState;
	private String beneficiaryBank;
	private String beneficiaryBankAdressLine1;
	private String beneficiaryBankAdressLine2;
	private String beneficiaryBankPostCode;
	private String beneficiaryBankCountry;
	private String beneficiaryBankCity;
	private String beneficiaryBankState;
	private String placeOfTakingIncharge;
	private String portOfLoading;
	private String portOfDischarge;
	private String placeOfFinalDelivery;
	private String latestShippingDate;
	private String presentationPeriod;
	private String transshipment;
	private String partialShipments;
	private String incoTerms;
	private String modeOfShipment;
	private String descriptionOfGoods;
	private String documentsRequired;
	private String additionalConditionsCode;
	private String otherAdditionalConditions;
	private String documentCharges;
	private String supportDocuments;
	private String fileToUpload;
	private String confirmationInstruction;
	private String transferable;
	private String standByLC;
	private String isDraft;
	private String additionalPayableCurrency;
	private String code;
	private String msg;
	private String flowType;
	private String id;
	private String status;
	private String ErrorCode;
	private String ErrorMessage;
	private String srmsReqOrderID;
	private String lcCreatedOn;
	private boolean referenceNomatch;
	private String ErrorCodeSRMSmatch;
	private String ErrorMsgSRMSmatch;
	private String CustomerId;
	@JsonIgnore
    private String createdOnFormatted;
	@JsonIgnore
    private String issuedOnFormatted;
	@JsonIgnore
    private String expiredOnFormatted;
	@JsonIgnore
    private String amountFormatted;
	@JsonIgnore
    private String amendDateFormatted;
	@JsonIgnore
    private String amendApprovedDateFormatted;
	private String screenNumber;
	private String draftCount;
	private String draftAmount;
	private String deletedCount;
	private String deletedAmount;
	private String selfApprovedCount;
	private String selfApprovedAmount;
	private String totalCount;
	private String totalAmount;
	private String signatoryApprovalRequired;

	// for amendmentspurpose
	private String amountType;
	private String otherAmendments;
	private String amendCharges;
	private String chargesPaid;
	private String amendmentReference;
	private String amendmentDate;
	private String amendmentApprovedDate;
	private String creditAmount;
	private String amendmentExpiryDate;
	private String importLCId;
	private String amendStatus;
	private String lcSRMSId;
	private String utilizedAmount;
	private String product;
	private String serviceRequestSrmsId;
	private String tradeCurrency;
	
	public String getAmendApprovedDateFormatted() {
        return _formatDate(amendmentApprovedDate);
    }
	
	public String getAmendDateFormatted() {
        return _formatDate(amendmentDate);
    }
	
	public String getTradeCurrency() {
		return lcCurrency;
	}
	public String getServiceRequestSrmsId() {
		return amendmentReference == null ? srmsReqOrderID : amendmentReference;
	}
	public String getProduct() {
		return amendmentReference == null ? "Import LC" : "Import Amendment";
	}

	public String getLcReferenceNo() {
		return lcReferenceNo;
	}

	public String getUtilizedAmount() {
		return utilizedAmount;
	}

	public void setUtilizedAmount(String utilizedAmount) {
		this.utilizedAmount = utilizedAmount;
	}

	public void setLcReferenceNo(String lcReferenceNo) {
		this.lcReferenceNo = lcReferenceNo;
	}

	public double getLcAmount() {
		return lcAmount;
	}

	public void setLcAmount(double lcAmount) {
		this.lcAmount = lcAmount;
	}
	public String getAmountFormatted() {
        return StringUtils.isNotBlank(String.valueOf(lcAmount)) ? formatAmount(String.valueOf(lcAmount)) : String.valueOf(lcAmount);
    }

	public String getLcCurrency() {
		return lcCurrency;
	}

	public void setLcCurrency(String lcCurrency) {
		this.lcCurrency = lcCurrency;
	}

	public String getTolerancePercentage() {
		return tolerancePercentage;
	}

	public void setTolerancePercentage(String tolerancePercentage) {
		this.tolerancePercentage = tolerancePercentage;
	}

	public double getMaximumCreditAmount() {
		return maximumCreditAmount;
	}

	public void setMaximumCreditAmount(double maximumCreditAmount) {
		this.maximumCreditAmount = maximumCreditAmount;
	}

	public String getAdditionalAmountPayable() {
		return additionalAmountPayable;
	}

	public void setAdditionalAmountPayable(String additionalAmountPayable) {
		this.additionalAmountPayable = additionalAmountPayable;
	}

	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public String getAvailableWith1() {
		return availableWith1;
	}

	public void setAvailableWith1(String availableWith1) {
		this.availableWith1 = availableWith1;
	}

	public String getAvailableWith2() {
		return availableWith2;
	}

	public void setAvailableWith2(String availableWith2) {
		this.availableWith2 = availableWith2;
	}

	public String getAvailableWith3() {
		return availableWith3;
	}

	public void setAvailableWith3(String availableWith3) {
		this.availableWith3 = availableWith3;
	}

	public String getAvailableWith4() {
		return availableWith4;
	}

	public void setAvailableWith4(String availableWith4) {
		this.availableWith4 = availableWith4;
	}

	public String getIssueDate() {
		try {
			return HelperMethods.changeDateFormat(issueDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	
	public String getIssuedOnFormatted() {
        return _formatDate(issueDate);
    }

	public String getExpiryDate() {
		try {
			return HelperMethods.changeDateFormat(expiryDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getExpiredOnFormatted() {
       return _formatDate(expiryDate);
	}
	 
	public String getExpiryPlace() {
		return expiryPlace;
	}

	public void setExpiryPlace(String expiryPlace) {
		this.expiryPlace = expiryPlace;
	}

	public String getChargesAccount() {
		return chargesAccount;
	}

	public void setChargesAccount(String chargesAccount) {
		this.chargesAccount = chargesAccount;
	}

	public String getCommisionAccount() {
		return commisionAccount;
	}

	public void setCommisionAccount(String commisionAccount) {
		this.commisionAccount = commisionAccount;
	}

	public String getMarginAccount() {
		return marginAccount;
	}

	public void setMarginAccount(String marginAccount) {
		this.marginAccount = marginAccount;
	}

	public String getMessageToBank() {
		return messageToBank;
	}

	public void setMessageToBank(String messageToBank) {
		this.messageToBank = messageToBank;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getBeneficiaryAddressLine1() {
		return beneficiaryAddressLine1;
	}

	public void setBeneficiaryAddressLine1(String beneficiaryAddressLine1) {
		this.beneficiaryAddressLine1 = beneficiaryAddressLine1;
	}

	public String getBeneficiaryAddressLine2() {
		return beneficiaryAddressLine2;
	}

	public void setBeneficiaryAddressLine2(String beneficiaryAddressLine2) {
		this.beneficiaryAddressLine2 = beneficiaryAddressLine2;
	}

	public String getBeneficiaryPostCode() {
		return beneficiaryPostCode;
	}

	public void setBeneficiaryPostCode(String beneficiaryPostCode) {
		this.beneficiaryPostCode = beneficiaryPostCode;
	}

	public String getBeneficiaryCountry() {
		return beneficiaryCountry;
	}

	public void setBeneficiaryCountry(String beneficiaryCountry) {
		this.beneficiaryCountry = beneficiaryCountry;
	}

	public String getBeneficiaryCity() {
		return beneficiaryCity;
	}

	public void setBeneficiaryCity(String beneficiaryCity) {
		this.beneficiaryCity = beneficiaryCity;
	}

	public String getBeneficiaryState() {
		return beneficiaryState;
	}

	public void setBeneficiaryState(String beneficiaryState) {
		this.beneficiaryState = beneficiaryState;
	}

	public String getBeneficiaryBank() {
		return beneficiaryBank;
	}

	public void setBeneficiaryBank(String beneficiaryBank) {
		this.beneficiaryBank = beneficiaryBank;
	}

	public String getBeneficiaryBankAdressLine1() {
		return beneficiaryBankAdressLine1;
	}

	public void setBeneficiaryBankAdressLine1(String beneficiaryBankAdressLine1) {
		this.beneficiaryBankAdressLine1 = beneficiaryBankAdressLine1;
	}

	public String getBeneficiaryBankAdressLine2() {
		return beneficiaryBankAdressLine2;
	}

	public void setBeneficiaryBankAdressLine2(String beneficiaryBankAdressLine2) {
		this.beneficiaryBankAdressLine2 = beneficiaryBankAdressLine2;
	}

	public String getBeneficiaryBankPostCode() {
		return beneficiaryBankPostCode;
	}

	public void setBeneficiaryBankPostCode(String beneficiaryBankPostCode) {
		this.beneficiaryBankPostCode = beneficiaryBankPostCode;
	}

	public String getBeneficiaryBankCountry() {
		return beneficiaryBankCountry;
	}

	public void setBeneficiaryBankCountry(String beneficiaryBankCountry) {
		this.beneficiaryBankCountry = beneficiaryBankCountry;
	}

	public String getBeneficiaryBankCity() {
		return beneficiaryBankCity;
	}

	public void setBeneficiaryBankCity(String beneficiaryBankCity) {
		this.beneficiaryBankCity = beneficiaryBankCity;
	}

	public String getBeneficiaryBankState() {
		return beneficiaryBankState;
	}

	public void setBeneficiaryBankState(String beneficiaryBankState) {
		this.beneficiaryBankState = beneficiaryBankState;
	}

	public String getPlaceOfTakingIncharge() {
		return placeOfTakingIncharge;
	}

	public void setPlaceOfTakingIncharge(String placeOfTakingIncharge) {
		this.placeOfTakingIncharge = placeOfTakingIncharge;
	}

	public String getPortOfLoading() {
		return portOfLoading;
	}

	public void setPortOfLoading(String portOfLoading) {
		this.portOfLoading = portOfLoading;
	}

	public String getPortOfDischarge() {
		return portOfDischarge;
	}

	public void setPortOfDischarge(String portOfDischarge) {
		this.portOfDischarge = portOfDischarge;
	}

	public String getPlaceOfFinalDelivery() {
		return placeOfFinalDelivery;
	}

	public void setPlaceOfFinalDelivery(String placeOfFinalDelivery) {
		this.placeOfFinalDelivery = placeOfFinalDelivery;
	}

	public String getLatestShippingDate() {
		try {
			return HelperMethods.changeDateFormat(latestShippingDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setLatestShippingDate(String latestShippingDate) {
		this.latestShippingDate = latestShippingDate;
	}

	public String getPresentationPeriod() {
		return presentationPeriod;
	}

	public void setPresentationPeriod(String presentationPeriod) {
		this.presentationPeriod = presentationPeriod;
	}

	public String getTransshipment() {
		return transshipment;
	}

	public void setTransshipment(String transshipment) {
		this.transshipment = transshipment;
	}

	public String getPartialShipments() {
		return partialShipments;
	}

	public void setPartialShipments(String partialShipments) {
		this.partialShipments = partialShipments;
	}

	public String getIncoTerms() {
		return incoTerms;
	}

	public void setIncoTerms(String incoTerms) {
		this.incoTerms = incoTerms;
	}

	public String getModeOfShipment() {
		return modeOfShipment;
	}

	public void setModeOfShipment(String modeOfShipment) {
		this.modeOfShipment = modeOfShipment;
	}

	public String getDescriptionOfGoods() {
		return descriptionOfGoods;
	}

	public void setDescriptionOfGoods(String descriptionOfGoods) {
		this.descriptionOfGoods = descriptionOfGoods;
	}

	public String getDocumentsRequired() {
		return documentsRequired;
	}

	public void setDocumentsRequired(String documentsRequired) {
		this.documentsRequired = documentsRequired;
	}

	public String getAdditionalConditionsCode() {
		return additionalConditionsCode;
	}

	public void setAdditionalConditionsCode(String additionalConditionsCode) {
		this.additionalConditionsCode = additionalConditionsCode;
	}

	public String getOtherAdditionalConditions() {
		return otherAdditionalConditions;
	}

	public void setOtherAdditionalConditions(String otherAdditionalConditions) {
		this.otherAdditionalConditions = otherAdditionalConditions;
	}

	public String getDocumentCharges() {
		return documentCharges;
	}

	public void setDocumentCharges(String documentCharges) {
		this.documentCharges = documentCharges;
	}

	public String getSupportDocuments() {
		return supportDocuments;
	}

	public void setSupportDocuments(String supportDocuments) {
		this.supportDocuments = supportDocuments;
	}

	public String getFileToUpload() {
		return fileToUpload;
	}

	public void setFileToUpload(String fileToUpload) {
		this.fileToUpload = fileToUpload;
	}

	public String getConfirmationInstruction() {
		return confirmationInstruction;
	}

	public void setConfirmationInstruction(String confirmationInstruction) {
		this.confirmationInstruction = confirmationInstruction;
	}

	public String getTransferable() {
		return transferable;
	}

	public void setTransferable(String transferable) {
		this.transferable = transferable;
	}

	public String getStandByLC() {
		return standByLC;
	}

	public void setStandByLC(String standByLC) {
		this.standByLC = standByLC;
	}

	public String getIsDraft() {
		return isDraft;
	}

	public void setIsDraft(String isDraft) {
		this.isDraft = isDraft;
	}

	public String getAdditionalPayableCurrency() {
		return additionalPayableCurrency;
	}

	public void setAdditionalPayableCurrency(String additionalPayableCurrency) {
		this.additionalPayableCurrency = additionalPayableCurrency;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getFlowType() {
		return flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorCode() {
		return ErrorCode;
	}

	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}

	public String getSrmsReqOrderID() {
		return srmsReqOrderID;
	}

	public void setSrmsReqOrderID(String srmsReqOrderID) {
		this.srmsReqOrderID = srmsReqOrderID;
	}

	public String getLcCreatedOn() {
		try {
			return HelperMethods.changeDateFormat(lcCreatedOn, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setLcCreatedOn(String lcCreatedOn) {
		this.lcCreatedOn = lcCreatedOn;
	}
	
	 public String getCreatedOnFormatted() {
	        return _formatDate(lcCreatedOn);
	    }

	public boolean isReferenceNomatch() {
		return referenceNomatch;
	}

	public void setReferenceNomatch(boolean referenceNomatch) {
		this.referenceNomatch = referenceNomatch;
	}

	public String getErrorCodeSRMSmatch() {
		return ErrorCodeSRMSmatch;
	}

	public void setErrorCodeSRMSmatch(String errorCodeSRMSmatch) {
		ErrorCodeSRMSmatch = errorCodeSRMSmatch;
	}

	public String getErrorMsgSRMSmatch() {
		return ErrorMsgSRMSmatch;
	}

	public void setErrorMsgSRMSmatch(String errorMsgSRMSmatch) {
		ErrorMsgSRMSmatch = errorMsgSRMSmatch;
	}

	public String getDraftCount() {
		return draftCount;
	}

	public void setDraftCount(String draftCount) {
		this.draftCount = draftCount;
	}

	public String getDraftAmount() {
		return draftAmount;
	}

	public void setDraftAmount(String draftAmount) {
		this.draftAmount = draftAmount;
	}

	public String getDeletedCount() {
		return deletedCount;
	}

	public void setDeletedCount(String deletedCount) {
		this.deletedCount = deletedCount;
	}

	public String getDeletedAmount() {
		return deletedAmount;
	}

	public void setDeletedAmount(String deletedAmount) {
		this.deletedAmount = deletedAmount;
	}

	public String getSelfApprovedCount() {
		return selfApprovedCount;
	}

	public void setSelfApprovedCount(String selfApprovedCount) {
		this.selfApprovedCount = selfApprovedCount;
	}

	public String getSelfApprovedAmount() {
		return selfApprovedAmount;
	}

	public void setSelfApprovedAmount(String selfApprovedAmount) {
		this.selfApprovedAmount = selfApprovedAmount;
	}

	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getSignatoryApprovalRequired() {
		return signatoryApprovalRequired;
	}

	public void setSignatoryApprovalRequired(String signatoryApprovalRequired) {
		this.signatoryApprovalRequired = signatoryApprovalRequired;
	}

	// new getter setter

	public String getAmountType() {
		return amountType;
	}

	public void setAmountType(String amountType) {
		this.amountType = amountType;
	}

	public String getOtherAmendments() {
		return otherAmendments;
	}

	public void setOtherAmendments(String otherAmendments) {
		this.otherAmendments = otherAmendments;
	}

	public String getAmendCharges() {
		return amendCharges;
	}

	public void setAmendCharges(String amendCharges) {
		this.amendCharges = amendCharges;
	}

	public String getChargesPaid() {
		return chargesPaid;
	}

	public void setChargesPaid(String chargesPaid) {
		this.chargesPaid = chargesPaid;
	}

	public String getAmendmentReference() {
		return amendmentReference;
	}

	public void setAmendmentReference(String amendmentReference) {
		this.amendmentReference = amendmentReference;
	}

	public String getAmendmentDate() {
		try {
			return HelperMethods.changeDateFormat(amendmentDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setAmendmentDate(String amendmentDate) {
		this.amendmentDate = amendmentDate;
	}

	public String getAmendmentApprovedDate() {
		try {
			return HelperMethods.changeDateFormat(amendmentApprovedDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setAmendmentApprovedDate(String amendmentApprovedDate) {
		this.amendmentApprovedDate = amendmentApprovedDate;
	}

	public String getAmendmentExpiryDate() {
		try {
			return HelperMethods.changeDateFormat(amendmentExpiryDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setAmendmentExpiryDate(String amendmentExpiryDate) {
		this.amendmentExpiryDate = amendmentExpiryDate;
	}

	public String getCreditAmount() {
		return creditAmount;
	}

	public void setCreditAmount(String creditAmount) {
		this.creditAmount = creditAmount;
	}

	public String getImportLCId() {
		return importLCId;
	}

	public void setImportLCId(String importLCId) {
		this.importLCId = importLCId;
	}

	public String getAmendStatus() {
		return amendStatus;
	}

	public void setAmendStatus(String amendStatus) {
		this.amendStatus = amendStatus;
	}

	public String getLcSRMSId() {
		return lcSRMSId;
	}

	public void setLcSRMSId(String lcSRMSId) {
		this.lcSRMSId = lcSRMSId;
	}

	public LetterOfCreditsDTO() {
		super();
	}

	public LetterOfCreditsDTO(String lcReferenceNo, double lcAmount, String lcCurrency, String tolerancePercentage,
			double maximumCreditAmount, String additionalAmountPayable, String paymentTerms, String availableWith1,
			String availableWith2, String availableWith3, String availableWith4, String issueDate, String expiryDate,
			String expiryPlace, String chargesAccount, String commisionAccount, String marginAccount,
			String messageToBank, String beneficiaryName, String beneficiaryAddressLine1,
			String beneficiaryAddressLine2, String beneficiaryPostCode, String beneficiaryCountry,
			String beneficiaryCity, String beneficiaryState, String beneficiaryBank, String beneficiaryBankAdressLine1,
			String beneficiaryBankAdressLine2, String beneficiaryBankPostCode, String beneficiaryBankCountry,
			String beneficiaryBankCity, String beneficiaryBankState, String placeOfTakingIncharge, String portOfLoading,
			String portOfDischarge, String placeOfFinalDelivery, String placeOfFinalDeliver, String latestShippingDate,
			String presentationPeriod, String transshipment, String partialShipments, String incoTerms,
			String modeOfShipment, String descriptionOfGoods, String documentsRequired, String additionalConditionsCode,
			String otherAdditionalConditions, String documentCharges, String supportDocuments, String fileToUpload,
			String confirmationInstruction, String transferable, String standByLC, String isDraft,
			String additionalPayableCurrency, String code, String msg, String flowType, String id, String status,
			String ErrorCode, String ErrorMessage, String srmsReqOrderID, String lcCreatedOn, Boolean referenceNomatch,
			String ErrorCodeSRMSmatch, String ErrorMsgSRMSmatch, String draftCount, String draftAmount,
			String deletedCount, String deletedAmount, String selfApprovedCount, String selfApprovedAmount,
			String totalCount, String totalAmount, String signatoryApprovalRequired, String amountType,
			String otherAmendments, String amendCharges, String chargesPaid, String amendmentReference,
			String amendmentDate, String amendmentApprovedDate, String creditAmount, String amendmentExpiryDate,
			String importLCId, String amendStatus, String lcSRMSId) {
		super();

		this.lcReferenceNo = lcReferenceNo;
		this.lcAmount = lcAmount;
		this.lcCurrency = lcCurrency;
		this.tolerancePercentage = tolerancePercentage;
		this.maximumCreditAmount = maximumCreditAmount;
		this.additionalAmountPayable = additionalAmountPayable;
		this.paymentTerms = paymentTerms;
		this.availableWith1 = availableWith1;
		this.availableWith2 = availableWith2;
		this.availableWith3 = availableWith3;
		this.availableWith4 = availableWith4;
		this.issueDate = issueDate;
		this.expiryDate = expiryDate;
		this.expiryPlace = expiryPlace;
		this.chargesAccount = chargesAccount;
		this.commisionAccount = commisionAccount;
		this.marginAccount = marginAccount;
		this.messageToBank = messageToBank;
		this.beneficiaryName = beneficiaryName;
		this.beneficiaryAddressLine1 = beneficiaryAddressLine1;
		this.beneficiaryAddressLine2 = beneficiaryAddressLine2;
		this.beneficiaryBankAdressLine1 = beneficiaryBankAdressLine2;
		this.beneficiaryBankAdressLine2 = beneficiaryBankAdressLine2;
		this.beneficiaryBankPostCode = beneficiaryBankPostCode;
		this.beneficiaryBankCountry = beneficiaryBankCountry;
		this.beneficiaryBankCity = beneficiaryBankCity;
		this.beneficiaryBankState = beneficiaryBankState;
		this.placeOfTakingIncharge = placeOfTakingIncharge;
		this.portOfLoading = portOfLoading;
		this.portOfDischarge = portOfDischarge;
		this.placeOfFinalDelivery = placeOfFinalDelivery;
		this.latestShippingDate = latestShippingDate;
		this.presentationPeriod = presentationPeriod;
		this.transshipment = transshipment;
		this.partialShipments = partialShipments;
		this.incoTerms = incoTerms;
		this.modeOfShipment = modeOfShipment;
		this.descriptionOfGoods = descriptionOfGoods;
		this.presentationPeriod = presentationPeriod;
		this.documentsRequired = documentsRequired;
		this.additionalConditionsCode = additionalConditionsCode;
		this.otherAdditionalConditions = otherAdditionalConditions;
		this.documentCharges = documentCharges;
		this.supportDocuments = supportDocuments;
		this.fileToUpload = fileToUpload;
		this.confirmationInstruction = confirmationInstruction;
		this.standByLC = standByLC;
		this.isDraft = isDraft;
		this.code = code;
		this.msg = msg;
		this.flowType = flowType;
		this.additionalPayableCurrency = additionalPayableCurrency;
		this.id = id;
		this.status = status;
		this.ErrorCode = ErrorCode;
		this.ErrorMessage = ErrorMessage;
		this.srmsReqOrderID = srmsReqOrderID;
		this.lcCreatedOn = lcCreatedOn;
		this.beneficiaryPostCode = beneficiaryPostCode;
		this.beneficiaryCountry = beneficiaryCountry;
		this.beneficiaryCity = beneficiaryCity;
		this.beneficiaryState = beneficiaryState;
		this.beneficiaryBank = beneficiaryBank;
		this.transferable = transferable;
		this.referenceNomatch = referenceNomatch;
		this.ErrorCodeSRMSmatch = ErrorCodeSRMSmatch;
		this.ErrorMsgSRMSmatch = ErrorMsgSRMSmatch;
		this.screenNumber = screenNumber;
		this.draftCount = draftCount;
		this.draftAmount = draftAmount;
		this.deletedCount = deletedCount;
		this.deletedAmount = deletedAmount;
		this.selfApprovedCount = selfApprovedCount;
		this.selfApprovedAmount = selfApprovedAmount;
		this.totalCount = totalCount;
		this.totalAmount = totalAmount;
		this.signatoryApprovalRequired = signatoryApprovalRequired;
		this.CustomerId = CustomerId;
		// new
		this.amountType = amountType;
		this.otherAmendments = otherAmendments;
		this.amendCharges = amendCharges;
		this.chargesPaid = chargesPaid;
		this.amendmentReference = amendmentReference;
		this.amendmentDate = amendmentDate;
		this.amendmentApprovedDate = amendmentApprovedDate;
		this.creditAmount = creditAmount;
		this.amendmentExpiryDate = amendmentExpiryDate;
		this.importLCId = importLCId;
		this.amendStatus = amendStatus;
		this.lcSRMSId = lcSRMSId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((additionalAmountPayable == null) ? 0 : additionalAmountPayable.hashCode());
		result = prime * result + ((additionalConditionsCode == null) ? 0 : additionalConditionsCode.hashCode());
		result = prime * result + ((additionalPayableCurrency == null) ? 0 : additionalPayableCurrency.hashCode());
		result = prime * result + ((availableWith1 == null) ? 0 : availableWith1.hashCode());
		result = prime * result + ((availableWith2 == null) ? 0 : availableWith2.hashCode());
		result = prime * result + ((availableWith3 == null) ? 0 : availableWith3.hashCode());
		result = prime * result + ((availableWith4 == null) ? 0 : availableWith4.hashCode());
		result = prime * result + ((beneficiaryAddressLine1 == null) ? 0 : beneficiaryAddressLine1.hashCode());
		result = prime * result + ((beneficiaryAddressLine2 == null) ? 0 : beneficiaryAddressLine2.hashCode());
		result = prime * result + ((beneficiaryBank == null) ? 0 : beneficiaryBank.hashCode());
		result = prime * result + ((beneficiaryBankAdressLine1 == null) ? 0 : beneficiaryBankAdressLine1.hashCode());
		result = prime * result + ((beneficiaryBankAdressLine2 == null) ? 0 : beneficiaryBankAdressLine2.hashCode());
		result = prime * result + ((beneficiaryBankCity == null) ? 0 : beneficiaryBankCity.hashCode());
		result = prime * result + ((beneficiaryBankCountry == null) ? 0 : beneficiaryBankCountry.hashCode());
		result = prime * result + ((beneficiaryBankPostCode == null) ? 0 : beneficiaryBankPostCode.hashCode());
		result = prime * result + ((beneficiaryBankState == null) ? 0 : beneficiaryBankState.hashCode());
		result = prime * result + ((beneficiaryCity == null) ? 0 : beneficiaryCity.hashCode());
		result = prime * result + ((beneficiaryCountry == null) ? 0 : beneficiaryCountry.hashCode());
		result = prime * result + ((beneficiaryName == null) ? 0 : beneficiaryName.hashCode());
		result = prime * result + ((beneficiaryPostCode == null) ? 0 : beneficiaryPostCode.hashCode());
		result = prime * result + ((beneficiaryState == null) ? 0 : beneficiaryState.hashCode());
		result = prime * result + ((chargesAccount == null) ? 0 : chargesAccount.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((commisionAccount == null) ? 0 : commisionAccount.hashCode());
		result = prime * result + ((confirmationInstruction == null) ? 0 : confirmationInstruction.hashCode());
		result = prime * result + ((descriptionOfGoods == null) ? 0 : descriptionOfGoods.hashCode());
		result = prime * result + ((documentCharges == null) ? 0 : documentCharges.hashCode());
		result = prime * result + ((documentsRequired == null) ? 0 : documentsRequired.hashCode());
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + ((expiryPlace == null) ? 0 : expiryPlace.hashCode());
		result = prime * result + ((fileToUpload == null) ? 0 : fileToUpload.hashCode());
		result = prime * result + ((flowType == null) ? 0 : flowType.hashCode());
		result = prime * result + ((incoTerms == null) ? 0 : incoTerms.hashCode());
		result = prime * result + ((isDraft == null) ? 0 : isDraft.hashCode());
		result = prime * result + ((issueDate == null) ? 0 : issueDate.hashCode());
		result = prime * result + ((latestShippingDate == null) ? 0 : latestShippingDate.hashCode());
		// result = prime * result + ((lcAmount == null) ? 0 : lcAmount.hashCode());
		long temp;
		temp = Double.doubleToLongBits(lcAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((lcCurrency == null) ? 0 : lcCurrency.hashCode());
		result = prime * result + ((lcReferenceNo == null) ? 0 : lcReferenceNo.hashCode());
		result = prime * result + ((marginAccount == null) ? 0 : marginAccount.hashCode());
		temp = Double.doubleToLongBits(maximumCreditAmount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((messageToBank == null) ? 0 : messageToBank.hashCode());
		result = prime * result + ((modeOfShipment == null) ? 0 : modeOfShipment.hashCode());
		result = prime * result + ((msg == null) ? 0 : msg.hashCode());
		result = prime * result + ((otherAdditionalConditions == null) ? 0 : otherAdditionalConditions.hashCode());
		result = prime * result + ((partialShipments == null) ? 0 : partialShipments.hashCode());
		result = prime * result + ((paymentTerms == null) ? 0 : paymentTerms.hashCode());
		result = prime * result + ((placeOfFinalDelivery == null) ? 0 : placeOfFinalDelivery.hashCode());
		result = prime * result + ((placeOfTakingIncharge == null) ? 0 : placeOfTakingIncharge.hashCode());
		result = prime * result + ((portOfDischarge == null) ? 0 : portOfDischarge.hashCode());
		result = prime * result + ((portOfLoading == null) ? 0 : portOfLoading.hashCode());
		result = prime * result + ((presentationPeriod == null) ? 0 : presentationPeriod.hashCode());
		result = prime * result + ((standByLC == null) ? 0 : standByLC.hashCode());
		result = prime * result + ((transferable == null) ? 0 : transferable.hashCode());
		result = prime * result + ((supportDocuments == null) ? 0 : supportDocuments.hashCode());
		result = prime * result + ((tolerancePercentage == null) ? 0 : tolerancePercentage.hashCode());
		result = prime * result + ((transshipment == null) ? 0 : transshipment.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((ErrorCode == null) ? 0 : ErrorCode.hashCode());
		result = prime * result + ((ErrorMessage == null) ? 0 : ErrorMessage.hashCode());
		result = prime * result + ((srmsReqOrderID == null) ? 0 : srmsReqOrderID.hashCode());
		result = prime * result + ((lcCreatedOn == null) ? 0 : lcCreatedOn.hashCode());
		result = prime * result + ((screenNumber == null) ? 0 : screenNumber.hashCode());
		result = prime * result + ((deletedAmount == null) ? 0 : deletedAmount.hashCode());
		result = prime * result + ((deletedCount == null) ? 0 : deletedCount.hashCode());
		result = prime * result + ((draftAmount == null) ? 0 : draftAmount.hashCode());
		result = prime * result + ((draftCount == null) ? 0 : draftCount.hashCode());
		result = prime * result + ((selfApprovedAmount == null) ? 0 : selfApprovedAmount.hashCode());
		result = prime * result + ((selfApprovedCount == null) ? 0 : selfApprovedCount.hashCode());
		result = prime * result + ((totalAmount == null) ? 0 : totalAmount.hashCode());
		result = prime * result + ((totalCount == null) ? 0 : totalCount.hashCode());
		result = prime * result + ((CustomerId == null) ? 0 : CustomerId.hashCode());
		result = prime * result + ((signatoryApprovalRequired == null) ? 0 : signatoryApprovalRequired.hashCode());
		// new
		result = prime * result + ((amountType == null) ? 0 : amountType.hashCode());
		result = prime * result + ((otherAmendments == null) ? 0 : otherAmendments.hashCode());
		result = prime * result + ((amendCharges == null) ? 0 : amendCharges.hashCode());
		result = prime * result + ((chargesPaid == null) ? 0 : chargesPaid.hashCode());
		result = prime * result + ((amendmentReference == null) ? 0 : amendmentReference.hashCode());
		result = prime * result + ((amendmentDate == null) ? 0 : amendmentDate.hashCode());
		result = prime * result + ((amendmentApprovedDate == null) ? 0 : amendmentApprovedDate.hashCode());
		result = prime * result + ((creditAmount == null) ? 0 : creditAmount.hashCode());
		result = prime * result + ((amendmentExpiryDate == null) ? 0 : amendmentExpiryDate.hashCode());
		result = prime * result + ((importLCId == null) ? 0 : importLCId.hashCode());
		result = prime * result + ((amendStatus == null) ? 0 : amendStatus.hashCode());
		result = prime * result + ((lcSRMSId == null) ? 0 : lcSRMSId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LetterOfCreditsDTO other = (LetterOfCreditsDTO) obj;
		if (additionalAmountPayable == null) {
			if (other.additionalAmountPayable != null)
				return false;
		} else if (!additionalAmountPayable.equals(other.additionalAmountPayable))
			return false;
		if (additionalConditionsCode == null) {
			if (other.additionalConditionsCode != null)
				return false;
		} else if (!additionalConditionsCode.equals(other.additionalConditionsCode))
			return false;
		if (additionalPayableCurrency == null) {
			if (other.additionalPayableCurrency != null)
				return false;
		} else if (!additionalPayableCurrency.equals(other.additionalPayableCurrency))
			return false;
		if (availableWith1 == null) {
			if (other.availableWith1 != null)
				return false;
		} else if (!availableWith1.equals(other.availableWith1))
			return false;
		if (availableWith2 == null) {
			if (other.availableWith2 != null)
				return false;
		} else if (!availableWith2.equals(other.availableWith2))
			return false;
		if (availableWith3 == null) {
			if (other.availableWith3 != null)
				return false;
		} else if (!availableWith3.equals(other.availableWith3))
			return false;
		if (availableWith4 == null) {
			if (other.availableWith4 != null)
				return false;
		} else if (!availableWith4.equals(other.availableWith4))
			return false;
		if (beneficiaryAddressLine1 == null) {
			if (other.beneficiaryAddressLine1 != null)
				return false;
		} else if (!beneficiaryAddressLine1.equals(other.beneficiaryAddressLine1))
			return false;
		if (beneficiaryAddressLine2 == null) {
			if (other.beneficiaryAddressLine2 != null)
				return false;
		} else if (!beneficiaryAddressLine2.equals(other.beneficiaryAddressLine2))
			return false;
		if (beneficiaryBank == null) {
			if (other.beneficiaryBank != null)
				return false;
		} else if (!beneficiaryBank.equals(other.beneficiaryBank))
			return false;
		if (beneficiaryBankAdressLine1 == null) {
			if (other.beneficiaryBankAdressLine1 != null)
				return false;
		} else if (!beneficiaryBankAdressLine1.equals(other.beneficiaryBankAdressLine1))
			return false;
		if (beneficiaryBankAdressLine2 == null) {
			if (other.beneficiaryBankAdressLine2 != null)
				return false;
		} else if (!beneficiaryBankAdressLine2.equals(other.beneficiaryBankAdressLine2))
			return false;
		if (beneficiaryBankCity == null) {
			if (other.beneficiaryBankCity != null)
				return false;
		} else if (!beneficiaryBankCity.equals(other.beneficiaryBankCity))
			return false;
		if (beneficiaryBankCountry == null) {
			if (other.beneficiaryBankCountry != null)
				return false;
		} else if (!beneficiaryBankCountry.equals(other.beneficiaryBankCountry))
			return false;
		if (beneficiaryBankPostCode == null) {
			if (other.beneficiaryBankPostCode != null)
				return false;
		} else if (!beneficiaryBankPostCode.equals(other.beneficiaryBankPostCode))
			return false;
		if (beneficiaryBankState == null) {
			if (other.beneficiaryBankState != null)
				return false;
		} else if (!beneficiaryBankState.equals(other.beneficiaryBankState))
			return false;
		if (beneficiaryCity == null) {
			if (other.beneficiaryCity != null)
				return false;
		} else if (!beneficiaryCity.equals(other.beneficiaryCity))
			return false;
		if (beneficiaryCountry == null) {
			if (other.beneficiaryCountry != null)
				return false;
		} else if (!beneficiaryCountry.equals(other.beneficiaryCountry))
			return false;
		if (beneficiaryName == null) {
			if (other.beneficiaryName != null)
				return false;
		} else if (!beneficiaryName.equals(other.beneficiaryName))
			return false;
		if (beneficiaryPostCode == null) {
			if (other.beneficiaryPostCode != null)
				return false;
		} else if (!beneficiaryPostCode.equals(other.beneficiaryPostCode))
			return false;
		if (beneficiaryState == null) {
			if (other.beneficiaryState != null)
				return false;
		} else if (!beneficiaryState.equals(other.beneficiaryState))
			return false;
		if (chargesAccount == null) {
			if (other.chargesAccount != null)
				return false;
		} else if (!chargesAccount.equals(other.chargesAccount))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (commisionAccount == null) {
			if (other.commisionAccount != null)
				return false;
		} else if (!commisionAccount.equals(other.commisionAccount))
			return false;
		if (confirmationInstruction == null) {
			if (other.confirmationInstruction != null)
				return false;
		} else if (!confirmationInstruction.equals(other.confirmationInstruction))
			return false;
		if (descriptionOfGoods == null) {
			if (other.descriptionOfGoods != null)
				return false;
		} else if (!descriptionOfGoods.equals(other.descriptionOfGoods))
			return false;
		if (documentCharges == null) {
			if (other.documentCharges != null)
				return false;
		} else if (!documentCharges.equals(other.documentCharges))
			return false;
		if (documentsRequired == null) {
			if (other.documentsRequired != null)
				return false;
		} else if (!documentsRequired.equals(other.documentsRequired))
			return false;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (expiryPlace == null) {
			if (other.expiryPlace != null)
				return false;
		} else if (!expiryPlace.equals(other.expiryPlace))
			return false;
		if (fileToUpload == null) {
			if (other.fileToUpload != null)
				return false;
		} else if (!fileToUpload.equals(other.fileToUpload))
			return false;
		if (flowType == null) {
			if (other.flowType != null)
				return false;
		} else if (!flowType.equals(other.flowType))
			return false;
		if (incoTerms == null) {
			if (other.incoTerms != null)
				return false;
		} else if (!incoTerms.equals(other.incoTerms))
			return false;
		if (isDraft == null) {
			if (other.isDraft != null)
				return false;
		} else if (!isDraft.equals(other.isDraft))
			return false;
		if (issueDate == null) {
			if (other.issueDate != null)
				return false;
		} else if (!issueDate.equals(other.issueDate))
			return false;
		if (latestShippingDate == null) {
			if (other.latestShippingDate != null)
				return false;
		} else if (!latestShippingDate.equals(other.latestShippingDate))
			return false;
		if (lcCurrency == null) {
			if (other.lcCurrency != null)
				return false;
		} else if (!lcCurrency.equals(other.lcCurrency))
			return false;
		if (lcReferenceNo == null) {
			if (other.lcReferenceNo != null)
				return false;
		} else if (!lcReferenceNo.equals(other.lcReferenceNo))
			return false;
		if (marginAccount == null) {
			if (other.marginAccount != null)
				return false;
		} else if (!marginAccount.equals(other.marginAccount))
			return false;
		if (messageToBank == null) {
			if (other.messageToBank != null)
				return false;
		} else if (!messageToBank.equals(other.messageToBank))
			return false;
		if (modeOfShipment == null) {
			if (other.modeOfShipment != null)
				return false;
		} else if (!modeOfShipment.equals(other.modeOfShipment))
			return false;
		if (msg == null) {
			if (other.msg != null)
				return false;
		} else if (!msg.equals(other.msg))
			return false;
		if (otherAdditionalConditions == null) {
			if (other.otherAdditionalConditions != null)
				return false;
		} else if (!otherAdditionalConditions.equals(other.otherAdditionalConditions))
			return false;
		if (partialShipments == null) {
			if (other.partialShipments != null)
				return false;
		} else if (!partialShipments.equals(other.partialShipments))
			return false;
		if (paymentTerms == null) {
			if (other.paymentTerms != null)
				return false;
		} else if (!paymentTerms.equals(other.paymentTerms))
			return false;
		if (placeOfFinalDelivery == null) {
			if (other.placeOfFinalDelivery != null)
				return false;
		} else if (!placeOfFinalDelivery.equals(other.placeOfFinalDelivery))
			return false;
		if (placeOfTakingIncharge == null) {
			if (other.placeOfTakingIncharge != null)
				return false;
		} else if (!placeOfTakingIncharge.equals(other.placeOfTakingIncharge))
			return false;
		if (portOfDischarge == null) {
			if (other.portOfDischarge != null)
				return false;
		} else if (!portOfDischarge.equals(other.portOfDischarge))
			return false;
		if (portOfLoading == null) {
			if (other.portOfLoading != null)
				return false;
		} else if (!portOfLoading.equals(other.portOfLoading))
			return false;
		if (presentationPeriod == null) {
			if (other.presentationPeriod != null)
				return false;
		} else if (!presentationPeriod.equals(other.presentationPeriod))
			return false;
		if (standByLC != other.standByLC)
			return false;
		if (supportDocuments == null) {
			if (other.supportDocuments != null)
				return false;
		} else if (!supportDocuments.equals(other.supportDocuments))
			return false;
		if (tolerancePercentage == null) {
			if (other.tolerancePercentage != null)
				return false;
		} else if (!tolerancePercentage.equals(other.tolerancePercentage))
			return false;
		if (transferable != other.transferable)
			return false;
		if (transshipment == null) {
			if (other.transshipment != null)
				return false;
		} else if (!transshipment.equals(other.transshipment))
			return false;

		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;

		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;

		if (ErrorCode == null) {
			if (other.ErrorCode != null)
				return false;
		} else if (!ErrorCode.equals(other.ErrorCode))
			return false;

		if (ErrorMessage == null) {
			if (other.ErrorMessage != null)
				return false;
		} else if (!ErrorMessage.equals(other.ErrorMessage))
			return false;

		if (srmsReqOrderID == null) {
			if (other.srmsReqOrderID != null)
				return false;
		} else if (!srmsReqOrderID.equals(other.srmsReqOrderID))
			return false;

		if (lcCreatedOn == null) {
			if (other.lcCreatedOn != null)
				return false;
		} else if (!lcCreatedOn.equals(other.lcCreatedOn))
			return false;

		if (screenNumber == null) {
			if (other.screenNumber != null)
				return false;
		} else if (!screenNumber.equals(other.screenNumber))
			return false;

		if (deletedAmount == null) {
			if (other.deletedAmount != null)
				return false;
		} else if (!deletedAmount.equals(other.deletedAmount))
			return false;
		if (deletedCount == null) {
			if (other.deletedCount != null)
				return false;
		} else if (!deletedCount.equals(other.deletedCount))
			return false;
		if (draftAmount == null) {
			if (other.draftAmount != null)
				return false;
		} else if (!draftAmount.equals(other.draftAmount))
			return false;
		if (draftCount == null) {
			if (other.draftCount != null)
				return false;
		} else if (!draftCount.equals(other.draftCount))
			return false;
		if (selfApprovedAmount == null) {
			if (other.selfApprovedAmount != null)
				return false;
		} else if (!selfApprovedAmount.equals(other.selfApprovedAmount))
			return false;
		if (selfApprovedCount == null) {
			if (other.selfApprovedCount != null)
				return false;
		} else if (!selfApprovedCount.equals(other.selfApprovedCount))
			return false;
		if (totalAmount == null) {
			if (other.totalAmount != null)
				return false;
		} else if (!totalAmount.equals(other.totalAmount))
			return false;
		if (totalCount == null) {
			if (other.totalCount != null)
				return false;
		} else if (!totalCount.equals(other.totalCount))
			return false;
		if (signatoryApprovalRequired == null) {
			if (other.signatoryApprovalRequired != null)
				return false;
		} else if (!signatoryApprovalRequired.equals(other.signatoryApprovalRequired))
			return false;

		if (CustomerId == null) {
			if (other.CustomerId != null)
				return false;
		} else if (!CustomerId.equals(other.CustomerId))
			return false;
		// new
		if (amountType == null) {
			if (other.amountType != null)
				return false;
		} else if (!amountType.equals(other.amountType))
			return false;
		if (otherAmendments == null) {
			if (other.otherAmendments != null)
				return false;
		} else if (!otherAmendments.equals(other.otherAmendments))
			return false;
		if (amendCharges == null) {
			if (other.amendCharges != null)
				return false;
		} else if (!amendCharges.equals(other.amendCharges))
			return false;
		if (chargesPaid == null) {
			if (other.chargesPaid != null)
				return false;
		} else if (!chargesPaid.equals(other.chargesPaid))
			return false;
		if (amendmentReference == null) {
			if (other.amendmentReference != null)
				return false;
		} else if (!amendmentReference.equals(other.amendmentReference))
			return false;
		if (amendmentDate == null) {
			if (other.amendmentDate != null)
				return false;
		} else if (!amendmentDate.equals(other.amendmentDate))
			return false;
		if (amendmentApprovedDate == null) {
			if (other.amendmentApprovedDate != null)
				return false;
		} else if (!amendmentApprovedDate.equals(other.amendmentApprovedDate))
			return false;
		if (creditAmount == null) {
			if (other.creditAmount != null)
				return false;
		} else if (!creditAmount.equals(other.creditAmount))
			return false;
		if (amendmentExpiryDate == null) {
			if (other.amendmentExpiryDate != null)
				return false;
		} else if (!amendmentExpiryDate.equals(other.amendmentExpiryDate))
			return false;
		if (importLCId == null) {
			if (other.importLCId != null)
				return false;
		} else if (!importLCId.equals(other.importLCId))
			return false;
		if (amendStatus == null) {
			if (other.amendStatus != null)
				return false;
		} else if (!amendStatus.equals(other.amendStatus))
			return false;
		if (lcSRMSId == null) {
			if (other.lcSRMSId != null)
				return false;
		} else if (!lcSRMSId.equals(other.lcSRMSId))
			return false;

		return true;
	}

	public String getScreenNumber() {
		return screenNumber;
	}

	public void setScreenNumber(String screenNumber) {
		this.screenNumber = screenNumber;
	}

	public String getCustomerId() {
		return CustomerId;
	}

	public void setCustomerId(String customerId) {
		CustomerId = customerId;
	}

}
