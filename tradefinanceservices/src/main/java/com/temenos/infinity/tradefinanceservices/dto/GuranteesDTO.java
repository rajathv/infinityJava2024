/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.dto;

import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils._formatDate;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.DBPDTO;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GuranteesDTO implements DBPDTO {
	private static final long serialVersionUID = -2845836757438220988L;

	private String beneficiaryName;
	private String productType;
	private String guaranteeAndSBLCType;
	private String guaranteesReferenceNo;
	private String createdOn;
	private String status;
	private String amount;
	private String issueDate;
	private String expiryDate;
	private String expiryType;
	private String totalBeneficiaries;
	private String modeOfTransaction;
	private String advisingBank;
	private String customerId;
	private String instructingParty;
	private String instructingPartyName;
	private String instructingPartyId;
	private String applicantParty;
	private String applicantPartyName;
	private String applicantPartyId;
	private String currency;
	private String expectedIssueDate;
	private String claimExpiryDate;
	private String expiryCondition;
	private String extendExpiryDate; // Yes/No
	private String extensionCapPeriod;
	private String notificationPeriod;
	private String extensionDetails;
	private String governingLaw;
	private String otherInstructions;
	private String beneficiaryType;
	private String deliveryInstructions;
	private String beneficiaryAddress1;
	private String beneficiaryAddress2;
	private String city;
	private String state;
	private String country;
	private String zipCode;
	private String saveBeneficiary;
	private String swiftCode;
	private String bankName;
	private String iban;
	private String localCode;
	private String bankAddress1;
	private String bankAddress2;
	private String bankCity;

	private String bankState;
	private String bankCountry;
	private String bankZipCode;
	private String instructionCurrencies;
	private String limitInstructions;
	private String otherBankInstructions;
	private String messageToBank;
	private String documentReferences;
	private String documentName;
	private String clauseConditions;
	private String errorMsg;
	private String errorCode;
	private String dbpErrMsg;
	private String dbpErrCode;
	@JsonAlias({"guaranteesSRMSId","srmsId"})
	private String guaranteesSRMSId;
	private String message;
	private String beneficiaryDetails;
	private String totalAmount;
	private String serviceRequestTime;
	private String isSingleSettlement;
	private String extensionPeriod;
	private String linkedPayees;
	private String applicableRules;
	private String demandAcceptance;
	private String partialDemandPercentage;
	private String returnHistory;
	private String reasonForReturn;
	private String corporateUserName;
	private String amendmentNo;
	private String lastUpdatedTimeStamp;
	private String amountWithCurrency;
	private String transferable;
	private String product;
	private String serviceRequestSrmsId;
	private String tradeCurrency;
	@JsonIgnore
    private String createdOnFormatted;
	@JsonIgnore
	private String beneficiaryCount;
	
	public String getBeneficiaryCount() {
		JSONArray beneficiaries= new JSONArray(beneficiaryDetails);
		return Integer.toString(beneficiaries.length());		
	}

	public String getCreatedOnFormatted() {
        return _formatDate(createdOn);
    }
	
	public String getTradeCurrency() {
		return currency;
	}
	
	public String getServiceRequestSrmsId() {
		return guaranteesSRMSId;
	}
	
	public String getProduct() {
		return "Issued GT & SBLC";
	}
	
	public String getInstructingPartyName() {
		return instructingPartyName;
	}


	public void setInstructingPartyName(String instructingPartyName) {
		this.instructingPartyName = instructingPartyName;
	}

	public String getInstructingPartyId() {
		return instructingPartyId;
	}

	public void setInstructingPartyId(String instructingPartyId) {
		this.instructingPartyId = instructingPartyId;
	}

	public String getApplicantPartyName() {
		return applicantPartyName;
	}

	public void setApplicantPartyName(String applicantPartyName) {
		this.applicantPartyName = applicantPartyName;
	}

	public String getApplicantPartyId() {
		return applicantPartyId;
	}

	public void setApplicantPartyId(String applicantPartyId) {
		this.applicantPartyId = applicantPartyId;
	}

	public String getTransferable() {
		return transferable;
	}

	public void setTransferable(String transferable) {
		this.transferable = transferable;
	}

	public String getAmendmentNo() {
		return amendmentNo;
	}

	public void setAmendmentNo(String amendmentNo) {
		this.amendmentNo = amendmentNo;
	}

	public String getGuaranteeAndSBLCType() {
		return guaranteeAndSBLCType;
	}

	public void setGuaranteeAndSBLCType(String guaranteeAndSBLCType) {
		this.guaranteeAndSBLCType = guaranteeAndSBLCType;
	}

	public String getDeliveryInstructions() {
		return deliveryInstructions;
	}

	public void setDeliveryInstructions(String deliveryInstructions) {
		this.deliveryInstructions = deliveryInstructions;
	}

	public String getApplicableRules() {
		return applicableRules;
	}

	public void setApplicableRules(String applicableRules) {
		this.applicableRules = applicableRules;
	}

	public String getDemandAcceptance() {
		return demandAcceptance;
	}

	public void setDemandAcceptance(String demandAcceptance) {
		this.demandAcceptance = demandAcceptance;
	}

	public String getPartialDemandPercentage() {
		return partialDemandPercentage;
	}

	public void setPartialDemandPercentage(String partialDemandPercentage) {
		this.partialDemandPercentage = partialDemandPercentage;
	}

	public String getLinkedPayees() {
		return linkedPayees;
	}

	public void setLinkedPayees(String linkedPayees) {
		this.linkedPayees = linkedPayees;
	}

	public String getExtensionPeriod() {
		return extensionPeriod;
	}

	public void setExtensionPeriod(String extensionPeriod) {
		this.extensionPeriod = extensionPeriod;
	}

	public String getIsSingleSettlement() {
		return isSingleSettlement;
	}

	public void setIsSingleSettlement(String isSingleSettlement) {
		this.isSingleSettlement = isSingleSettlement;
	}

	public String getServiceRequestTime() {
		return serviceRequestTime;
	}

	public void setServiceRequestTime(String serviceRequestTime) {
		this.serviceRequestTime = serviceRequestTime;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getBeneficiaryDetails() {
		return beneficiaryDetails;
	}

	public void setBeneficiaryDetails(String beneficiaryDetails) {
		this.beneficiaryDetails = beneficiaryDetails;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getGuaranteesSRMSId() {
		return guaranteesSRMSId;
	}

	public void setGuaranteesSRMSId(String guaranteesSRMSId) {
		this.guaranteesSRMSId = guaranteesSRMSId;
	}

	public String getDbpErrMsg() {
		return dbpErrMsg;
	}

	public void setDbpErrMsg(String dbpErrMsg) {
		this.dbpErrMsg = dbpErrMsg;
	}

	public String getDbpErrCode() {
		return dbpErrCode;
	}

	public void setDbpErrCode(String dbpErrCode) {
		this.dbpErrCode = dbpErrCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errormsg) {
		this.errorMsg = errormsg;
	}

	// Getter And Setter
	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getGuaranteesReferenceNo() {
		return guaranteesReferenceNo;
	}

	public void setGuaranteesReferenceNo(String guaranteesReferenceNo) {
		this.guaranteesReferenceNo = guaranteesReferenceNo;
	}

	public String getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getExpiryType() {
		return expiryType;
	}

	public void setExpiryType(String expiryType) {
		this.expiryType = expiryType;
	}

	public String getTotalBeneficiaries() {
		return totalBeneficiaries;
	}

	public void setTotalBeneficiaries(String totalBeneficiaries) {
		this.totalBeneficiaries = totalBeneficiaries;
	}

	public String getModeOfTransaction() {
		return modeOfTransaction;
	}

	public void setModeOfTransaction(String modeOfTransaction) {
		this.modeOfTransaction = modeOfTransaction;
	}

	public String getAdvisingBank() {
		return advisingBank;
	}

	public void setAdvisingBank(String advisingBank) {
		this.advisingBank = advisingBank;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getInstructingParty() {
		return instructingParty;
	}

	public void setInstructingParty(String instructingParty) {
		this.instructingParty = instructingParty;
	}

	public String getApplicantParty() {
		return applicantParty;
	}

	public void setApplicantParty(String applicantParty) {
		this.applicantParty = applicantParty;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getExpectedIssueDate() {
		return expectedIssueDate;
	}

	public void setExpectedIssueDate(String expectedIssueDate) {
		this.expectedIssueDate = expectedIssueDate;
	}

	public String getClaimExpiryDate() {
		return claimExpiryDate;
	}

	public void setClaimExpiryDate(String claimExpiryDate) {
		this.claimExpiryDate = claimExpiryDate;
	}

	public String getExpiryCondition() {
		return expiryCondition;
	}

	public void setExpiryCondition(String expiryCondition) {
		this.expiryCondition = expiryCondition;
	}

	public String getExtendExpiryDate() {
		return extendExpiryDate;
	}

	public void setExtendExpiryDate(String extendExpiryDate) {
		this.extendExpiryDate = extendExpiryDate;
	}

	public String getExtensionCapPeriod() {
		return extensionCapPeriod;
	}

	public void setExtensionCapPeriod(String extensionCapPeriod) {
		this.extensionCapPeriod = extensionCapPeriod;
	}

	public String getNotificationPeriod() {
		return notificationPeriod;
	}

	public void setNotificationPeriod(String notificationPeriod) {
		this.notificationPeriod = notificationPeriod;
	}

	public String getExtensionDetails() {
		return extensionDetails;
	}

	public void setExtensionDetails(String extensionDetails) {
		this.extensionDetails = extensionDetails;
	}

	public String getGoverningLaw() {
		return governingLaw;
	}

	public void setGoverningLaw(String governingLaw) {
		this.governingLaw = governingLaw;
	}

	public String getOtherInstructions() {
		return otherInstructions;
	}

	public void setOtherInstructions(String otherInstructions) {
		this.otherInstructions = otherInstructions;
	}

	public String getBeneficiaryType() {
		return beneficiaryType;
	}

	public void setBeneficiaryType(String beneficiaryType) {
		this.beneficiaryType = beneficiaryType;
	}

	public String getBeneficiaryAddress1() {
		return beneficiaryAddress1;
	}

	public void setBeneficiaryAddress1(String beneficiaryAddress1) {
		this.beneficiaryAddress1 = beneficiaryAddress1;
	}

	public String getBeneficiaryAddress2() {
		return beneficiaryAddress2;
	}

	public void setBeneficiaryAddress2(String beneficiaryAddress2) {
		this.beneficiaryAddress2 = beneficiaryAddress2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getSaveBeneficiary() {
		return saveBeneficiary;
	}

	public void setSaveBeneficiary(String saveBeneficiary) {
		this.saveBeneficiary = saveBeneficiary;
	}

	public String getSwiftCode() {
		return swiftCode;
	}

	public void setSwiftCode(String swiftCode) {
		this.swiftCode = swiftCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getLocalCode() {
		return localCode;
	}

	public void setLocalCode(String localCode) {
		this.localCode = localCode;
	}

	public String getBankAddress1() {
		return bankAddress1;
	}

	public void setBankAddress1(String bankAddress1) {
		this.bankAddress1 = bankAddress1;
	}

	public String getBankAddress2() {
		return bankAddress2;
	}

	public void setBankAddress2(String bankAddress2) {
		this.bankAddress2 = bankAddress2;
	}

	public String getBankCity() {
		return bankCity;
	}

	public void setBankCity(String bankCity) {
		this.bankCity = bankCity;
	}

	public String getBankState() {
		return bankState;
	}

	public void setBankState(String bankState) {
		this.bankState = bankState;
	}

	public String getBankCountry() {
		return bankCountry;
	}

	public void setBankCountry(String bankCountry) {
		this.bankCountry = bankCountry;
	}

	public String getBankZipCode() {
		return bankZipCode;
	}

	public void setBankZipCode(String bankZipCode) {
		this.bankZipCode = bankZipCode;
	}

	public String getInstructionCurrencies() {
		return instructionCurrencies;
	}

	public void setInstructionCurrencies(String instructionCurrencies) {
		this.instructionCurrencies = instructionCurrencies;
	}

	public String getLimitInstructions() {
		return limitInstructions;
	}

	public void setLimitInstructions(String limitInstructions) {
		this.limitInstructions = limitInstructions;
	}

	public String getOtherBankInstructions() {
		return otherBankInstructions;
	}

	public void setOtherBankInstructions(String otherBankInstructions) {
		this.otherBankInstructions = otherBankInstructions;
	}

	public String getMessageToBank() {
		return messageToBank;
	}

	public void setMessageToBank(String messageToBank) {
		this.messageToBank = messageToBank;
	}

	public String getDocumentReferences() {
		return documentReferences;
	}

	public void setDocumentReferences(String documentReferences) {
		this.documentReferences = documentReferences;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getClauseConditions() {
		return clauseConditions;
	}

	public void setClauseConditions(String clauseConditions) {
		this.clauseConditions = clauseConditions;
	}

	public String getReturnHistory() {
		return returnHistory;
	}

	public void setReturnHistory(String returnHistory) {
		this.returnHistory = returnHistory;
	}

	public String getReasonForReturn() {
		return reasonForReturn;
	}

	public void setReasonForReturn(String reasonForReturn) {
		this.reasonForReturn = reasonForReturn;
	}

	public String getCorporateUserName() {
		return corporateUserName;
	}

	public void setCorporateUserName(String corporateUserName) {
		this.corporateUserName = corporateUserName;
	}

	public String getLastUpdatedTimeStamp() {
		return lastUpdatedTimeStamp;
	}

	public void setLastUpdatedTimeStamp(String lastUpdatedTimeStamp) {
		this.lastUpdatedTimeStamp = lastUpdatedTimeStamp;
	}

	public String getAmountWithCurrency() {
		return TradeFinanceCommonUtils.getAmountWithCurrency(currency, amount, false);
	}

	public void setAmountWithCurrency(String amountWithCurrency) {
		this.amountWithCurrency = amountWithCurrency;
	}

	public GuranteesDTO() {
		super();
	}

	public GuranteesDTO(String beneficiaryName, String productType, String guaranteeAndSBLCType,
			String guaranteesReferenceNo, String createdOn, String status, String amount, String issueDate,
			String expiryDate, String expiryType, String totalBeneficiaries, String modeOfTransaction,
			String advisingBank, String customerId, String instructingParty, String applicantParty, String currency,
			String expectedIssueDate, String claimExpiryDate, String expiryCondition, String extendExpiryDate,
			String extensionCapPeriod, String notificationPeriod, String extensionDetails, String governingLaw,
			String otherInstructions, String beneficiaryType, String deliveryInstructions, String beneficiaryAddress1,
			String beneficiaryAddress2, String city, String state, String country, String zipCode,
			String saveBeneficiary, String swiftCode, String bankName, String iban, String localCode,
			String bankAddress1, String bankAddress2, String bankCity, String bankState, String bankCountry,
			String bankZipCode, String instructionCurrencies, String limitInstructions, String otherBankInstructions,
			String messageToBank, String documentReferences, String documentName, String clauseConditions,
			String errorMsg, String errorCode, String dbpErrMsg, String dbpErrCode, String guaranteesSRMSId,
			String message, String beneficiaryDetails, String totalAmount, String serviceRequestTime,
			String isSingleSettlement, String extensionPeriod, String linkedPayees, String applicableRules,
			String demandAcceptance, String partialDemandPercentage, String returnHistory, String reasonForReturn,
			String corporateUserName, String amendmentNo, String lastUpdatedTimeStamp, String amountWithCurrency) {
		super();
		this.beneficiaryName = beneficiaryName;
		this.productType = productType;
		this.guaranteeAndSBLCType = guaranteeAndSBLCType;
		this.guaranteesReferenceNo = guaranteesReferenceNo;
		this.createdOn = createdOn;
		this.status = status;
		this.amount = amount;
		this.issueDate = issueDate;
		this.expiryDate = expiryDate;
		this.expiryType = expiryType;
		this.totalBeneficiaries = totalBeneficiaries;
		this.modeOfTransaction = modeOfTransaction;
		this.advisingBank = advisingBank;
		this.customerId = customerId;
		this.instructingParty = instructingParty;
		this.applicantParty = applicantParty;
		this.currency = currency;
		this.expectedIssueDate = expectedIssueDate;
		this.claimExpiryDate = claimExpiryDate;
		this.expiryCondition = expiryCondition;
		this.extendExpiryDate = extendExpiryDate;
		this.extensionCapPeriod = extensionCapPeriod;
		this.notificationPeriod = notificationPeriod;
		this.extensionDetails = extensionDetails;
		this.governingLaw = governingLaw;
		this.otherInstructions = otherInstructions;
		this.beneficiaryType = beneficiaryType;
		this.deliveryInstructions = deliveryInstructions;
		this.beneficiaryAddress1 = beneficiaryAddress1;
		this.beneficiaryAddress2 = beneficiaryAddress2;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipCode = zipCode;
		this.saveBeneficiary = saveBeneficiary;
		this.swiftCode = swiftCode;
		this.bankName = bankName;
		this.iban = iban;
		this.localCode = localCode;
		this.bankAddress1 = bankAddress1;
		this.bankAddress2 = bankAddress2;
		this.bankCity = bankCity;
		this.bankState = bankState;
		this.bankCountry = bankCountry;
		this.bankZipCode = bankZipCode;
		this.instructionCurrencies = instructionCurrencies;
		this.limitInstructions = limitInstructions;
		this.otherBankInstructions = otherBankInstructions;
		this.messageToBank = messageToBank;
		this.documentReferences = documentReferences;
		this.documentName = documentName;
		this.clauseConditions = clauseConditions;
		this.errorMsg = errorMsg;
		this.errorCode = errorCode;
		this.dbpErrMsg = dbpErrMsg;
		this.dbpErrCode = dbpErrCode;
		this.guaranteesSRMSId = guaranteesSRMSId;
		this.message = message;
		this.beneficiaryDetails = beneficiaryDetails;
		this.totalAmount = totalAmount;
		this.serviceRequestTime = serviceRequestTime;
		this.isSingleSettlement = isSingleSettlement;
		this.extensionPeriod = extensionPeriod;
		this.linkedPayees = linkedPayees;
		this.applicableRules = applicableRules;
		this.demandAcceptance = demandAcceptance;
		this.partialDemandPercentage = partialDemandPercentage;
		this.returnHistory = returnHistory;
		this.reasonForReturn = reasonForReturn;
		this.corporateUserName = corporateUserName;
		this.amendmentNo=amendmentNo;
		this.lastUpdatedTimeStamp=lastUpdatedTimeStamp;
		this.amountWithCurrency = amountWithCurrency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((advisingBank == null) ? 0 : advisingBank.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((applicableRules == null) ? 0 : applicableRules.hashCode());
		result = prime * result + ((applicantParty == null) ? 0 : applicantParty.hashCode());
		result = prime * result + ((bankAddress1 == null) ? 0 : bankAddress1.hashCode());
		result = prime * result + ((bankAddress2 == null) ? 0 : bankAddress2.hashCode());
		result = prime * result + ((bankCity == null) ? 0 : bankCity.hashCode());
		result = prime * result + ((bankCountry == null) ? 0 : bankCountry.hashCode());
		result = prime * result + ((bankName == null) ? 0 : bankName.hashCode());
		result = prime * result + ((bankState == null) ? 0 : bankState.hashCode());
		result = prime * result + ((bankZipCode == null) ? 0 : bankZipCode.hashCode());
		result = prime * result + ((beneficiaryAddress1 == null) ? 0 : beneficiaryAddress1.hashCode());
		result = prime * result + ((beneficiaryAddress2 == null) ? 0 : beneficiaryAddress2.hashCode());
		result = prime * result + ((beneficiaryDetails == null) ? 0 : beneficiaryDetails.hashCode());
		result = prime * result + ((beneficiaryName == null) ? 0 : beneficiaryName.hashCode());
		result = prime * result + ((beneficiaryType == null) ? 0 : beneficiaryType.hashCode());
		result = prime * result + ((city == null) ? 0 : city.hashCode());
		result = prime * result + ((claimExpiryDate == null) ? 0 : claimExpiryDate.hashCode());
		result = prime * result + ((clauseConditions == null) ? 0 : clauseConditions.hashCode());
		result = prime * result + ((corporateUserName == null) ? 0 : corporateUserName.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((dbpErrCode == null) ? 0 : dbpErrCode.hashCode());
		result = prime * result + ((dbpErrMsg == null) ? 0 : dbpErrMsg.hashCode());
		result = prime * result + ((deliveryInstructions == null) ? 0 : deliveryInstructions.hashCode());
		result = prime * result + ((demandAcceptance == null) ? 0 : demandAcceptance.hashCode());
		result = prime * result + ((documentName == null) ? 0 : documentName.hashCode());
		result = prime * result + ((documentReferences == null) ? 0 : documentReferences.hashCode());
		result = prime * result + ((errorCode == null) ? 0 : errorCode.hashCode());
		result = prime * result + ((errorMsg == null) ? 0 : errorMsg.hashCode());
		result = prime * result + ((expectedIssueDate == null) ? 0 : expectedIssueDate.hashCode());
		result = prime * result + ((expiryCondition == null) ? 0 : expiryCondition.hashCode());
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + ((expiryType == null) ? 0 : expiryType.hashCode());
		result = prime * result + ((extendExpiryDate == null) ? 0 : extendExpiryDate.hashCode());
		result = prime * result + ((extensionCapPeriod == null) ? 0 : extensionCapPeriod.hashCode());
		result = prime * result + ((extensionDetails == null) ? 0 : extensionDetails.hashCode());
		result = prime * result + ((extensionPeriod == null) ? 0 : extensionPeriod.hashCode());
		result = prime * result + ((returnHistory == null) ? 0 : returnHistory.hashCode());
		result = prime * result + ((governingLaw == null) ? 0 : governingLaw.hashCode());
		result = prime * result + ((guaranteeAndSBLCType == null) ? 0 : guaranteeAndSBLCType.hashCode());
		result = prime * result + ((guaranteesReferenceNo == null) ? 0 : guaranteesReferenceNo.hashCode());
		result = prime * result + ((guaranteesSRMSId == null) ? 0 : guaranteesSRMSId.hashCode());
		result = prime * result + ((iban == null) ? 0 : iban.hashCode());
		result = prime * result + ((instructingParty == null) ? 0 : instructingParty.hashCode());
		result = prime * result + ((instructionCurrencies == null) ? 0 : instructionCurrencies.hashCode());
		result = prime * result + ((isSingleSettlement == null) ? 0 : isSingleSettlement.hashCode());
		result = prime * result + ((issueDate == null) ? 0 : issueDate.hashCode());
		result = prime * result + ((limitInstructions == null) ? 0 : limitInstructions.hashCode());
		result = prime * result + ((linkedPayees == null) ? 0 : linkedPayees.hashCode());
		result = prime * result + ((localCode == null) ? 0 : localCode.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((messageToBank == null) ? 0 : messageToBank.hashCode());
		result = prime * result + ((modeOfTransaction == null) ? 0 : modeOfTransaction.hashCode());
		result = prime * result + ((notificationPeriod == null) ? 0 : notificationPeriod.hashCode());
		result = prime * result + ((otherBankInstructions == null) ? 0 : otherBankInstructions.hashCode());
		result = prime * result + ((otherInstructions == null) ? 0 : otherInstructions.hashCode());
		result = prime * result + ((partialDemandPercentage == null) ? 0 : partialDemandPercentage.hashCode());
		result = prime * result + ((productType == null) ? 0 : productType.hashCode());
		result = prime * result + ((reasonForReturn == null) ? 0 : reasonForReturn.hashCode());
		result = prime * result + ((saveBeneficiary == null) ? 0 : saveBeneficiary.hashCode());
		result = prime * result + ((serviceRequestTime == null) ? 0 : serviceRequestTime.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((swiftCode == null) ? 0 : swiftCode.hashCode());
		result = prime * result + ((totalAmount == null) ? 0 : totalAmount.hashCode());
		result = prime * result + ((totalBeneficiaries == null) ? 0 : totalBeneficiaries.hashCode());
		result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
		result = prime * result + ((amendmentNo == null) ? 0 : amendmentNo.hashCode());
		result = prime * result + ((lastUpdatedTimeStamp==null) ? 0 : lastUpdatedTimeStamp.hashCode());
		result = prime * result + ((amountWithCurrency==null) ? 0 : amountWithCurrency.hashCode());
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
		GuranteesDTO other = (GuranteesDTO) obj;
		if (advisingBank == null) {
			if (other.advisingBank != null)
				return false;
		} else if (!advisingBank.equals(other.advisingBank))
			return false;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (applicableRules == null) {
			if (other.applicableRules != null)
				return false;
		} else if (!applicableRules.equals(other.applicableRules))
			return false;
		if (applicantParty == null) {
			if (other.applicantParty != null)
				return false;
		} else if (!applicantParty.equals(other.applicantParty))
			return false;
		if (bankAddress1 == null) {
			if (other.bankAddress1 != null)
				return false;
		} else if (!bankAddress1.equals(other.bankAddress1))
			return false;
		if (bankAddress2 == null) {
			if (other.bankAddress2 != null)
				return false;
		} else if (!bankAddress2.equals(other.bankAddress2))
			return false;
		if (bankCity == null) {
			if (other.bankCity != null)
				return false;
		} else if (!bankCity.equals(other.bankCity))
			return false;
		if (bankCountry == null) {
			if (other.bankCountry != null)
				return false;
		} else if (!bankCountry.equals(other.bankCountry))
			return false;
		if (bankName == null) {
			if (other.bankName != null)
				return false;
		} else if (!bankName.equals(other.bankName))
			return false;
		if (bankState == null) {
			if (other.bankState != null)
				return false;
		} else if (!bankState.equals(other.bankState))
			return false;
		if (bankZipCode == null) {
			if (other.bankZipCode != null)
				return false;
		} else if (!bankZipCode.equals(other.bankZipCode))
			return false;
		if (beneficiaryAddress1 == null) {
			if (other.beneficiaryAddress1 != null)
				return false;
		} else if (!beneficiaryAddress1.equals(other.beneficiaryAddress1))
			return false;
		if (beneficiaryAddress2 == null) {
			if (other.beneficiaryAddress2 != null)
				return false;
		} else if (!beneficiaryAddress2.equals(other.beneficiaryAddress2))
			return false;
		if (beneficiaryDetails == null) {
			if (other.beneficiaryDetails != null)
				return false;
		} else if (!beneficiaryDetails.equals(other.beneficiaryDetails))
			return false;
		if (beneficiaryName == null) {
			if (other.beneficiaryName != null)
				return false;
		} else if (!beneficiaryName.equals(other.beneficiaryName))
			return false;
		if (beneficiaryType == null) {
			if (other.beneficiaryType != null)
				return false;
		} else if (!beneficiaryType.equals(other.beneficiaryType))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		if (claimExpiryDate == null) {
			if (other.claimExpiryDate != null)
				return false;
		} else if (!claimExpiryDate.equals(other.claimExpiryDate))
			return false;
		if (clauseConditions == null) {
			if (other.clauseConditions != null)
				return false;
		} else if (!clauseConditions.equals(other.clauseConditions))
			return false;
		if (corporateUserName == null) {
			if (other.corporateUserName != null)
				return false;
		} else if (!corporateUserName.equals(other.corporateUserName))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (dbpErrCode == null) {
			if (other.dbpErrCode != null)
				return false;
		} else if (!dbpErrCode.equals(other.dbpErrCode))
			return false;
		if (dbpErrMsg == null) {
			if (other.dbpErrMsg != null)
				return false;
		} else if (!dbpErrMsg.equals(other.dbpErrMsg))
			return false;
		if (deliveryInstructions == null) {
			if (other.deliveryInstructions != null)
				return false;
		} else if (!deliveryInstructions.equals(other.deliveryInstructions))
			return false;
		if (demandAcceptance == null) {
			if (other.demandAcceptance != null)
				return false;
		} else if (!demandAcceptance.equals(other.demandAcceptance))
			return false;
		if (documentName == null) {
			if (other.documentName != null)
				return false;
		} else if (!documentName.equals(other.documentName))
			return false;
		if (documentReferences == null) {
			if (other.documentReferences != null)
				return false;
		} else if (!documentReferences.equals(other.documentReferences))
			return false;
		if (errorCode == null) {
			if (other.errorCode != null)
				return false;
		} else if (!errorCode.equals(other.errorCode))
			return false;
		if (errorMsg == null) {
			if (other.errorMsg != null)
				return false;
		} else if (!errorMsg.equals(other.errorMsg))
			return false;
		if (expectedIssueDate == null) {
			if (other.expectedIssueDate != null)
				return false;
		} else if (!expectedIssueDate.equals(other.expectedIssueDate))
			return false;
		if (expiryCondition == null) {
			if (other.expiryCondition != null)
				return false;
		} else if (!expiryCondition.equals(other.expiryCondition))
			return false;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (expiryType == null) {
			if (other.expiryType != null)
				return false;
		} else if (!expiryType.equals(other.expiryType))
			return false;
		if (extendExpiryDate == null) {
			if (other.extendExpiryDate != null)
				return false;
		} else if (!extendExpiryDate.equals(other.extendExpiryDate))
			return false;
		if (extensionCapPeriod == null) {
			if (other.extensionCapPeriod != null)
				return false;
		} else if (!extensionCapPeriod.equals(other.extensionCapPeriod))
			return false;
		if (extensionDetails == null) {
			if (other.extensionDetails != null)
				return false;
		} else if (!extensionDetails.equals(other.extensionDetails))
			return false;
		if (extensionPeriod == null) {
			if (other.extensionPeriod != null)
				return false;
		} else if (!extensionPeriod.equals(other.extensionPeriod))
			return false;
		if (returnHistory == null) {
			if (other.returnHistory != null)
				return false;
		} else if (!returnHistory.equals(other.returnHistory))
			return false;
		if (governingLaw == null) {
			if (other.governingLaw != null)
				return false;
		} else if (!governingLaw.equals(other.governingLaw))
			return false;
		if (guaranteeAndSBLCType == null) {
			if (other.guaranteeAndSBLCType != null)
				return false;
		} else if (!guaranteeAndSBLCType.equals(other.guaranteeAndSBLCType))
			return false;
		if (guaranteesReferenceNo == null) {
			if (other.guaranteesReferenceNo != null)
				return false;
		} else if (!guaranteesReferenceNo.equals(other.guaranteesReferenceNo))
			return false;
		if (guaranteesSRMSId == null) {
			if (other.guaranteesSRMSId != null)
				return false;
		} else if (!guaranteesSRMSId.equals(other.guaranteesSRMSId))
			return false;
		if (iban == null) {
			if (other.iban != null)
				return false;
		} else if (!iban.equals(other.iban))
			return false;
		if (instructingParty == null) {
			if (other.instructingParty != null)
				return false;
		} else if (!instructingParty.equals(other.instructingParty))
			return false;
		if (instructionCurrencies == null) {
			if (other.instructionCurrencies != null)
				return false;
		} else if (!instructionCurrencies.equals(other.instructionCurrencies))
			return false;
		if (isSingleSettlement == null) {
			if (other.isSingleSettlement != null)
				return false;
		} else if (!isSingleSettlement.equals(other.isSingleSettlement))
			return false;
		if (issueDate == null) {
			if (other.issueDate != null)
				return false;
		} else if (!issueDate.equals(other.issueDate))
			return false;
		if (limitInstructions == null) {
			if (other.limitInstructions != null)
				return false;
		} else if (!limitInstructions.equals(other.limitInstructions))
			return false;
		if (linkedPayees == null) {
			if (other.linkedPayees != null)
				return false;
		} else if (!linkedPayees.equals(other.linkedPayees))
			return false;
		if (localCode == null) {
			if (other.localCode != null)
				return false;
		} else if (!localCode.equals(other.localCode))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (messageToBank == null) {
			if (other.messageToBank != null)
				return false;
		} else if (!messageToBank.equals(other.messageToBank))
			return false;
		if (modeOfTransaction == null) {
			if (other.modeOfTransaction != null)
				return false;
		} else if (!modeOfTransaction.equals(other.modeOfTransaction))
			return false;
		if (notificationPeriod == null) {
			if (other.notificationPeriod != null)
				return false;
		} else if (!notificationPeriod.equals(other.notificationPeriod))
			return false;
		if (otherBankInstructions == null) {
			if (other.otherBankInstructions != null)
				return false;
		} else if (!otherBankInstructions.equals(other.otherBankInstructions))
			return false;
		if (otherInstructions == null) {
			if (other.otherInstructions != null)
				return false;
		} else if (!otherInstructions.equals(other.otherInstructions))
			return false;
		if (partialDemandPercentage == null) {
			if (other.partialDemandPercentage != null)
				return false;
		} else if (!partialDemandPercentage.equals(other.partialDemandPercentage))
			return false;
		if (productType == null) {
			if (other.productType != null)
				return false;
		} else if (!productType.equals(other.productType))
			return false;
		if (reasonForReturn == null) {
			if (other.reasonForReturn != null)
				return false;
		} else if (!reasonForReturn.equals(other.reasonForReturn))
			return false;
		if (saveBeneficiary == null) {
			if (other.saveBeneficiary != null)
				return false;
		} else if (!saveBeneficiary.equals(other.saveBeneficiary))
			return false;
		if (serviceRequestTime == null) {
			if (other.serviceRequestTime != null)
				return false;
		} else if (!serviceRequestTime.equals(other.serviceRequestTime))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (swiftCode == null) {
			if (other.swiftCode != null)
				return false;
		} else if (!swiftCode.equals(other.swiftCode))
			return false;
		if (totalAmount == null) {
			if (other.totalAmount != null)
				return false;
		} else if (!totalAmount.equals(other.totalAmount))
			return false;
		if (totalBeneficiaries == null) {
			if (other.totalBeneficiaries != null)
				return false;
		} else if (!totalBeneficiaries.equals(other.totalBeneficiaries))
			return false;
		if (zipCode == null) {
			if (other.zipCode != null)
				return false;
		} else if (!zipCode.equals(other.zipCode))
			return false;
		if (amendmentNo == null) {
			if (other.amendmentNo != null)
				return false;
		} else if (!amendmentNo.equals(other.amendmentNo))
			return false;
		if (lastUpdatedTimeStamp == null) {
			if (other.lastUpdatedTimeStamp != null)
				return false;
		} else if (!lastUpdatedTimeStamp.equals(other.lastUpdatedTimeStamp))
			return false;
		if (amountWithCurrency == null) {
			if (other.amountWithCurrency != null)
				return false;
		} else if (!amountWithCurrency.equals(other.amountWithCurrency))
			return false;
		return true;
	}

}
