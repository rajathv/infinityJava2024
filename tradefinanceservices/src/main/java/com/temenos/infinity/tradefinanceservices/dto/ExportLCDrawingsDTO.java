/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.dto;

import com.dbp.core.api.DBPDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.kony.dbputilities.util.HelperMethods;
import com.temenos.dbx.product.constants.Constants;

import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils._formatDate;
import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils.formatAmount;

import java.io.Serializable;
import java.text.ParseException;

import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class ExportLCDrawingsDTO implements Serializable, DBPDTO {

	private static final long serialVersionUID = 1L;

	private String drawingAmount;
	private String lcReferenceNo;
	private String financeBill;
	private String currency;
	private String applicant;
	private String creditAccount;
	private String externalAccount;
	private String chargesDebitAccount;
	private String messageToBank;
	private String status;
	private String physicalDocuments;
	private String uploadedDocuments;
	private String forwardDocuments;
	private String returnedDocuments;
	private String exportLCId;
	private String advisingBankReference;
	private String lcType;
	private String lcAmount;
	private String expiryDate;
	private String totalDocuments;
	private String documentStatus;
	private String documentReference;
	private String discrepencies;
	private String discrepenciesAcceptance;
	private String paymentStatus;
	private String totalAmount;
	private String reasonForReturn;
	private String drawingReferenceNo;
	private String drawingSRMSRequestId;
	private String drawingCreatedDate;
	private String discrepanciesHistory;
	private String discrepanciesHistory1;
	private String discrepanciesHistory2;
	private String discrepanciesHistory3;
	private String discrepanciesHistory4;
	private String discrepanciesHistory5;
	private String lcCurrency;
	private String lcIssueDate;
	private String issuingBank;
	private String paymentDate;
	private String messageFromBank;
	private String errorMessage;
	private String errorCode;
	private String customerId;
	private String returnedDate;
	private String approvedDate;
	private String returnMessageToBank;
	private String product;
	private String serviceRequestSrmsId;
	private String tradeCurrency;
	@JsonIgnore
    private String amountFormatted;
	@JsonIgnore
    private String createdOnFormatted;

	public String getCreatedOnFormatted() {
        return _formatDate(drawingCreatedDate);
    }
	
	public String getAmountFormatted() {
        return StringUtils.isNotBlank(String.valueOf(drawingAmount)) ? formatAmount(String.valueOf(drawingAmount)) : String.valueOf(drawingAmount);
    }
	
	public String getTradeCurrency() {
		return currency;
	}

	public String getServiceRequestSrmsId() {
		return drawingSRMSRequestId;
	}
	public String getProduct() {
		return "Export Drawing";
	}
	public String getDrawingAmount() {
		return drawingAmount;
	}
	public void setDrawingAmount(String drawingAmount) {
		this.drawingAmount = drawingAmount;
	}
	public String getLcReferenceNo() {
		return lcReferenceNo;
	}
	public void setLcReferenceNo(String lcReferenceNo) {
		this.lcReferenceNo = lcReferenceNo;
	}
	public String getFinanceBill() {
		return financeBill;
	}
	public void setFinanceBill(String financeBill) {
		this.financeBill = financeBill;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getApplicant() {
		return applicant;
	}
	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}
	public String getCreditAccount() {
		return creditAccount;
	}
	public void setCreditAccount(String creditAccount) {
		this.creditAccount = creditAccount;
	}
	public String getExternalAccount() {
		return externalAccount;
	}
	public void setExternalAccount(String externalAccount) {
		this.externalAccount = externalAccount;
	}
	public String getChargesDebitAccount() {
		return chargesDebitAccount;
	}
	public void setChargesDebitAccount(String chargesDebitAccount) {
		this.chargesDebitAccount = chargesDebitAccount;
	}
	public String getMessageToBank() {
		return messageToBank;
	}
	public void setMessageToBank(String messageToBank) {
		this.messageToBank = messageToBank;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPhysicalDocuments() {
		return physicalDocuments;
	}
	public void setPhysicalDocuments(String physicalDocuments) {
		this.physicalDocuments = physicalDocuments;
	}
	public String getUploadedDocuments() {
		return uploadedDocuments;
	}
	public void setUploadedDocuments(String uploadedDocuments) {
		this.uploadedDocuments = uploadedDocuments;
	}
	public String getForwardDocuments() {
		return forwardDocuments;
	}
	public void setForwardDocuments(String forwardDocuments) {
		this.forwardDocuments = forwardDocuments;
	}
	public String getReturnedDocuments() {
		return returnedDocuments;
	}
	public void setReturnedDocuments(String returnedDocuments) {
		this.returnedDocuments = returnedDocuments;
	}
	public String getExportLCId() {
		return exportLCId;
	}
	public void setExportLCId(String exportLCId) {
		this.exportLCId = exportLCId;
	}
	public String getAdvisingBankReference() {
		return advisingBankReference;
	}
	public void setAdvisingBankReference(String advisingBankReference) {
		this.advisingBankReference = advisingBankReference;
	}
	public String getLcType() {
		return lcType;
	}
	public void setLcType(String lcType) {
		this.lcType = lcType;
	}
	public String getLcAmount() {
		return lcAmount;
	}
	public void setLcAmount(String lcAmount) {
		this.lcAmount = lcAmount;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getTotalDocuments() {
		return totalDocuments;
	}
	public void setTotalDocuments(String totalDocuments) {
		this.totalDocuments = totalDocuments;
	}
	public String getDocumentStatus() {
		return documentStatus;
	}
	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus;
	}
	public String getDocumentReference() {
		return documentReference;
	}
	public void setDocumentReference(String documentReference) {
		this.documentReference = documentReference;
	}
	public String getDiscrepencies() {
		return discrepencies;
	}
	public void setDiscrepencies(String discrepencies) {
		this.discrepencies = discrepencies;
	}
	public String getDiscrepenciesAcceptance() {
		return discrepenciesAcceptance;
	}
	public void setDiscrepenciesAcceptance(String discrepenciesAcceptance) {
		this.discrepenciesAcceptance = discrepenciesAcceptance;
	}
	public String getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public String getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getReasonForReturn() {
		return reasonForReturn;
	}
	public void setReasonForReturn(String reasonForReturn) {
		this.reasonForReturn = reasonForReturn;
	}
	public String getDrawingReferenceNo() {
		return drawingReferenceNo;
	}
	public void setDrawingReferenceNo(String drawingReferenceNo) {
		this.drawingReferenceNo = drawingReferenceNo;
	}
	public String getDrawingSRMSRequestId() {
		return drawingSRMSRequestId;
	}
	public void setDrawingSRMSRequestId(String drawingSRMSRequestId) {
		this.drawingSRMSRequestId = drawingSRMSRequestId;
	}
	public String getDrawingCreatedDate() {
		try {
			return HelperMethods.changeDateFormat(drawingCreatedDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}
	public void setDrawingCreatedDate(String drawingCreatedDate) {
		this.drawingCreatedDate = drawingCreatedDate;
	}
	public String getDiscrepanciesHistory() {
		return discrepanciesHistory;
	}
	public void setDiscrepanciesHistory(String discrepanciesHistory) {
		this.discrepanciesHistory = discrepanciesHistory;
	}
	public String getDiscrepanciesHistory1() {
		return discrepanciesHistory1;
	}
	public void setDiscrepanciesHistory1(String discrepanciesHistory1) {
		this.discrepanciesHistory1 = discrepanciesHistory1;
	}
	public String getDiscrepanciesHistory2() {
		return discrepanciesHistory2;
	}
	public void setDiscrepanciesHistory2(String discrepanciesHistory2) {
		this.discrepanciesHistory2 = discrepanciesHistory2;
	}
	public String getDiscrepanciesHistory3() {
		return discrepanciesHistory3;
	}
	public void setDiscrepanciesHistory3(String discrepanciesHistory3) {
		this.discrepanciesHistory3 = discrepanciesHistory3;
	}
	public String getDiscrepanciesHistory4() {
		return discrepanciesHistory4;
	}
	public void setDiscrepanciesHistory4(String discrepanciesHistory4) {
		this.discrepanciesHistory4 = discrepanciesHistory4;
	}
	public String getDiscrepanciesHistory5() {
		return discrepanciesHistory5;
	}
	public void setDiscrepanciesHistory5(String discrepanciesHistory5) {
		this.discrepanciesHistory5 = discrepanciesHistory5;
	}
	public String getLcCurrency() {
		return lcCurrency;
	}
	public void setLcCurrency(String lcCurrency) {
		this.lcCurrency = lcCurrency;
	}
	public String getLcIssueDate() {
		return lcIssueDate;
	}
	public void setLcIssueDate(String lcIssueDate) {
		this.lcIssueDate = lcIssueDate;
	}
	public String getIssuingBank() {
		return issuingBank;
	}
	public void setIssuingBank(String issuingBank) {
		this.issuingBank = issuingBank;
	}
	public String getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getMessageFromBank() {
		return messageFromBank;
	}
	public void setMessageFromBank(String messageFromBank) {
		this.messageFromBank = messageFromBank;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getReturnedDate() {
		return returnedDate;
	}
	public void setReturnedDate(String returnedDate) {
		this.returnedDate = returnedDate;
	}
	public String getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}
	public String getReturnMessageToBank() {
		return returnMessageToBank;
	}
	public void setReturnMessageToBank(String returnMessageToBank) {
		this.returnMessageToBank = returnMessageToBank;
	}
	
	public ExportLCDrawingsDTO() {
		super();
	}
	
	public ExportLCDrawingsDTO(String drawingAmount, String lcReferenceNo, String financeBill, String currency,
			String applicant, String creditAccount, String externalAccount, String chargesDebitAccount,
			String messageToBank, String status, String physicalDocuments, String uploadedDocuments,
			String forwardDocuments, String returnedDocuments, String exportLCId, String advisingBankReference,
			String lcType, String lcAmount, String expiryDate, String totalDocuments, String documentStatus,
			String documentReference, String discrepencies, String discrepenciesAcceptance, String paymentStatus,
			String totalAmount, String reasonForReturn, String drawingReferenceNo, String drawingSRMSRequestId,
			String drawingCreatedDate, String discrepanciesHistory, String discrepanciesHistory1,
			String discrepanciesHistory2, String discrepanciesHistory3, String discrepanciesHistory4,
			String discrepanciesHistory5, String lcCurrency, String lcIssueDate, String issuingBank, String paymentDate,
			String messageFromBank, String errorMessage, String errorCode, String customerId, String returnedDate,
			String approvedDate, String returnMessageToBank) {
		super();
		this.drawingAmount = drawingAmount;
		this.lcReferenceNo = lcReferenceNo;
		this.financeBill = financeBill;
		this.currency = currency;
		this.applicant = applicant;
		this.creditAccount = creditAccount;
		this.externalAccount = externalAccount;
		this.chargesDebitAccount = chargesDebitAccount;
		this.messageToBank = messageToBank;
		this.status = status;
		this.physicalDocuments = physicalDocuments;
		this.uploadedDocuments = uploadedDocuments;
		this.forwardDocuments = forwardDocuments;
		this.returnedDocuments = returnedDocuments;
		this.exportLCId = exportLCId;
		this.advisingBankReference = advisingBankReference;
		this.lcType = lcType;
		this.lcAmount = lcAmount;
		this.expiryDate = expiryDate;
		this.totalDocuments = totalDocuments;
		this.documentStatus = documentStatus;
		this.documentReference = documentReference;
		this.discrepencies = discrepencies;
		this.discrepenciesAcceptance = discrepenciesAcceptance;
		this.paymentStatus = paymentStatus;
		this.totalAmount = totalAmount;
		this.reasonForReturn = reasonForReturn;
		this.drawingReferenceNo = drawingReferenceNo;
		this.drawingSRMSRequestId = drawingSRMSRequestId;
		this.drawingCreatedDate = drawingCreatedDate;
		this.discrepanciesHistory = discrepanciesHistory;
		this.discrepanciesHistory1 = discrepanciesHistory1;
		this.discrepanciesHistory2 = discrepanciesHistory2;
		this.discrepanciesHistory3 = discrepanciesHistory3;
		this.discrepanciesHistory4 = discrepanciesHistory4;
		this.discrepanciesHistory5 = discrepanciesHistory5;
		this.lcCurrency = lcCurrency;
		this.lcIssueDate = lcIssueDate;
		this.issuingBank = issuingBank;
		this.paymentDate = paymentDate;
		this.messageFromBank = messageFromBank;
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
		this.customerId = customerId;
		this.returnedDate = returnedDate;
		this.approvedDate = approvedDate;
		this.returnMessageToBank = returnMessageToBank;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((advisingBankReference == null) ? 0 : advisingBankReference.hashCode());
		result = prime * result + ((applicant == null) ? 0 : applicant.hashCode());
		result = prime * result + ((approvedDate == null) ? 0 : approvedDate.hashCode());
		result = prime * result + ((chargesDebitAccount == null) ? 0 : chargesDebitAccount.hashCode());
		result = prime * result + ((creditAccount == null) ? 0 : creditAccount.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((discrepanciesHistory == null) ? 0 : discrepanciesHistory.hashCode());
		result = prime * result + ((discrepanciesHistory1 == null) ? 0 : discrepanciesHistory1.hashCode());
		result = prime * result + ((discrepanciesHistory2 == null) ? 0 : discrepanciesHistory2.hashCode());
		result = prime * result + ((discrepanciesHistory3 == null) ? 0 : discrepanciesHistory3.hashCode());
		result = prime * result + ((discrepanciesHistory4 == null) ? 0 : discrepanciesHistory4.hashCode());
		result = prime * result + ((discrepanciesHistory5 == null) ? 0 : discrepanciesHistory5.hashCode());
		result = prime * result + ((discrepencies == null) ? 0 : discrepencies.hashCode());
		result = prime * result + ((discrepenciesAcceptance == null) ? 0 : discrepenciesAcceptance.hashCode());
		result = prime * result + ((documentReference == null) ? 0 : documentReference.hashCode());
		result = prime * result + ((documentStatus == null) ? 0 : documentStatus.hashCode());
		result = prime * result + ((drawingAmount == null) ? 0 : drawingAmount.hashCode());
		result = prime * result + ((drawingCreatedDate == null) ? 0 : drawingCreatedDate.hashCode());
		result = prime * result + ((drawingReferenceNo == null) ? 0 : drawingReferenceNo.hashCode());
		result = prime * result + ((drawingSRMSRequestId == null) ? 0 : drawingSRMSRequestId.hashCode());
		result = prime * result + ((errorCode == null) ? 0 : errorCode.hashCode());
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + ((exportLCId == null) ? 0 : exportLCId.hashCode());
		result = prime * result + ((externalAccount == null) ? 0 : externalAccount.hashCode());
		result = prime * result + ((financeBill == null) ? 0 : financeBill.hashCode());
		result = prime * result + ((forwardDocuments == null) ? 0 : forwardDocuments.hashCode());
		result = prime * result + ((issuingBank == null) ? 0 : issuingBank.hashCode());
		result = prime * result + ((lcAmount == null) ? 0 : lcAmount.hashCode());
		result = prime * result + ((lcCurrency == null) ? 0 : lcCurrency.hashCode());
		result = prime * result + ((lcIssueDate == null) ? 0 : lcIssueDate.hashCode());
		result = prime * result + ((lcReferenceNo == null) ? 0 : lcReferenceNo.hashCode());
		result = prime * result + ((lcType == null) ? 0 : lcType.hashCode());
		result = prime * result + ((messageFromBank == null) ? 0 : messageFromBank.hashCode());
		result = prime * result + ((messageToBank == null) ? 0 : messageToBank.hashCode());
		result = prime * result + ((paymentDate == null) ? 0 : paymentDate.hashCode());
		result = prime * result + ((paymentStatus == null) ? 0 : paymentStatus.hashCode());
		result = prime * result + ((physicalDocuments == null) ? 0 : physicalDocuments.hashCode());
		result = prime * result + ((reasonForReturn == null) ? 0 : reasonForReturn.hashCode());
		result = prime * result + ((returnMessageToBank == null) ? 0 : returnMessageToBank.hashCode());
		result = prime * result + ((returnedDate == null) ? 0 : returnedDate.hashCode());
		result = prime * result + ((returnedDocuments == null) ? 0 : returnedDocuments.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((totalAmount == null) ? 0 : totalAmount.hashCode());
		result = prime * result + ((totalDocuments == null) ? 0 : totalDocuments.hashCode());
		result = prime * result + ((uploadedDocuments == null) ? 0 : uploadedDocuments.hashCode());
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
		ExportLCDrawingsDTO other = (ExportLCDrawingsDTO) obj;
		if (advisingBankReference == null) {
			if (other.advisingBankReference != null)
				return false;
		} else if (!advisingBankReference.equals(other.advisingBankReference))
			return false;
		if (applicant == null) {
			if (other.applicant != null)
				return false;
		} else if (!applicant.equals(other.applicant))
			return false;
		if (approvedDate == null) {
			if (other.approvedDate != null)
				return false;
		} else if (!approvedDate.equals(other.approvedDate))
			return false;
		if (chargesDebitAccount == null) {
			if (other.chargesDebitAccount != null)
				return false;
		} else if (!chargesDebitAccount.equals(other.chargesDebitAccount))
			return false;
		if (creditAccount == null) {
			if (other.creditAccount != null)
				return false;
		} else if (!creditAccount.equals(other.creditAccount))
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
		if (discrepanciesHistory == null) {
			if (other.discrepanciesHistory != null)
				return false;
		} else if (!discrepanciesHistory.equals(other.discrepanciesHistory))
			return false;
		if (discrepanciesHistory1 == null) {
			if (other.discrepanciesHistory1 != null)
				return false;
		} else if (!discrepanciesHistory1.equals(other.discrepanciesHistory1))
			return false;
		if (discrepanciesHistory2 == null) {
			if (other.discrepanciesHistory2 != null)
				return false;
		} else if (!discrepanciesHistory2.equals(other.discrepanciesHistory2))
			return false;
		if (discrepanciesHistory3 == null) {
			if (other.discrepanciesHistory3 != null)
				return false;
		} else if (!discrepanciesHistory3.equals(other.discrepanciesHistory3))
			return false;
		if (discrepanciesHistory4 == null) {
			if (other.discrepanciesHistory4 != null)
				return false;
		} else if (!discrepanciesHistory4.equals(other.discrepanciesHistory4))
			return false;
		if (discrepanciesHistory5 == null) {
			if (other.discrepanciesHistory5 != null)
				return false;
		} else if (!discrepanciesHistory5.equals(other.discrepanciesHistory5))
			return false;
		if (discrepencies == null) {
			if (other.discrepencies != null)
				return false;
		} else if (!discrepencies.equals(other.discrepencies))
			return false;
		if (discrepenciesAcceptance == null) {
			if (other.discrepenciesAcceptance != null)
				return false;
		} else if (!discrepenciesAcceptance.equals(other.discrepenciesAcceptance))
			return false;
		if (documentReference == null) {
			if (other.documentReference != null)
				return false;
		} else if (!documentReference.equals(other.documentReference))
			return false;
		if (documentStatus == null) {
			if (other.documentStatus != null)
				return false;
		} else if (!documentStatus.equals(other.documentStatus))
			return false;
		if (drawingAmount == null) {
			if (other.drawingAmount != null)
				return false;
		} else if (!drawingAmount.equals(other.drawingAmount))
			return false;
		if (drawingCreatedDate == null) {
			if (other.drawingCreatedDate != null)
				return false;
		} else if (!drawingCreatedDate.equals(other.drawingCreatedDate))
			return false;
		if (drawingReferenceNo == null) {
			if (other.drawingReferenceNo != null)
				return false;
		} else if (!drawingReferenceNo.equals(other.drawingReferenceNo))
			return false;
		if (drawingSRMSRequestId == null) {
			if (other.drawingSRMSRequestId != null)
				return false;
		} else if (!drawingSRMSRequestId.equals(other.drawingSRMSRequestId))
			return false;
		if (errorCode == null) {
			if (other.errorCode != null)
				return false;
		} else if (!errorCode.equals(other.errorCode))
			return false;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (exportLCId == null) {
			if (other.exportLCId != null)
				return false;
		} else if (!exportLCId.equals(other.exportLCId))
			return false;
		if (externalAccount == null) {
			if (other.externalAccount != null)
				return false;
		} else if (!externalAccount.equals(other.externalAccount))
			return false;
		if (financeBill == null) {
			if (other.financeBill != null)
				return false;
		} else if (!financeBill.equals(other.financeBill))
			return false;
		if (forwardDocuments == null) {
			if (other.forwardDocuments != null)
				return false;
		} else if (!forwardDocuments.equals(other.forwardDocuments))
			return false;
		if (issuingBank == null) {
			if (other.issuingBank != null)
				return false;
		} else if (!issuingBank.equals(other.issuingBank))
			return false;
		if (lcAmount == null) {
			if (other.lcAmount != null)
				return false;
		} else if (!lcAmount.equals(other.lcAmount))
			return false;
		if (lcCurrency == null) {
			if (other.lcCurrency != null)
				return false;
		} else if (!lcCurrency.equals(other.lcCurrency))
			return false;
		if (lcIssueDate == null) {
			if (other.lcIssueDate != null)
				return false;
		} else if (!lcIssueDate.equals(other.lcIssueDate))
			return false;
		if (lcReferenceNo == null) {
			if (other.lcReferenceNo != null)
				return false;
		} else if (!lcReferenceNo.equals(other.lcReferenceNo))
			return false;
		if (lcType == null) {
			if (other.lcType != null)
				return false;
		} else if (!lcType.equals(other.lcType))
			return false;
		if (messageFromBank == null) {
			if (other.messageFromBank != null)
				return false;
		} else if (!messageFromBank.equals(other.messageFromBank))
			return false;
		if (messageToBank == null) {
			if (other.messageToBank != null)
				return false;
		} else if (!messageToBank.equals(other.messageToBank))
			return false;
		if (paymentDate == null) {
			if (other.paymentDate != null)
				return false;
		} else if (!paymentDate.equals(other.paymentDate))
			return false;
		if (paymentStatus == null) {
			if (other.paymentStatus != null)
				return false;
		} else if (!paymentStatus.equals(other.paymentStatus))
			return false;
		if (physicalDocuments == null) {
			if (other.physicalDocuments != null)
				return false;
		} else if (!physicalDocuments.equals(other.physicalDocuments))
			return false;
		if (reasonForReturn == null) {
			if (other.reasonForReturn != null)
				return false;
		} else if (!reasonForReturn.equals(other.reasonForReturn))
			return false;
		if (returnMessageToBank == null) {
			if (other.returnMessageToBank != null)
				return false;
		} else if (!returnMessageToBank.equals(other.returnMessageToBank))
			return false;
		if (returnedDate == null) {
			if (other.returnedDate != null)
				return false;
		} else if (!returnedDate.equals(other.returnedDate))
			return false;
		if (returnedDocuments == null) {
			if (other.returnedDocuments != null)
				return false;
		} else if (!returnedDocuments.equals(other.returnedDocuments))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (totalAmount == null) {
			if (other.totalAmount != null)
				return false;
		} else if (!totalAmount.equals(other.totalAmount))
			return false;
		if (totalDocuments == null) {
			if (other.totalDocuments != null)
				return false;
		} else if (!totalDocuments.equals(other.totalDocuments))
			return false;
		if (uploadedDocuments == null) {
			if (other.uploadedDocuments != null)
				return false;
		} else if (!uploadedDocuments.equals(other.uploadedDocuments))
			return false;
		return true;
	}
}
