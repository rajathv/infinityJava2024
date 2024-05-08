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

import java.text.ParseException;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class ExportLOCDTO implements DBPDTO{
	private static final long serialVersionUID = 1L;

	private String drawingAmount;
	private String lcType;
	private String lcReferenceNo;
	private String issuingBankReference;
	private String advisingBankReference;
	private String applicant;
	private String utilizedLCAmount;
	private String issueDate;
	private String amount;
	private String expiryDate;
	private String currency;
	private String issuingBank;
	private String applicantaddress;
	private String issuingbankaddress;
	private String paymentTerms;
	private String documentName;
	private String uploadedFiles;
	private String forwardContract;
	private String beneficiaryName;
	private String beneficiaryAddress;
	private String goodsDescription;
	private String additionalConditions;
	private String confirmInstructions;
	private String latestShipmentDate;
	private String status;
	private String exportLCId;
	private String errorMsg;
	private String errorCode;
	private String lcCreatedOn;
	private String lcUpdatedOn;
	private String customerId;
	private String amendmentNo;
	private String product;
	private String serviceRequestSrmsId;
	private String tradeCurrency;
	private String beneficiaryConsent;
	private String reasonForRejection;
	private String messageToBank;
	@JsonIgnore
    private String expiredOnFormatted;
	@JsonIgnore
    private String issueDateFormatted;
	@JsonIgnore
    private String latestShipmentDateFormatted;
	@JsonIgnore
    private String amountFormatted;
		
	public String getAmountFormatted() {
        return StringUtils.isNotBlank(String.valueOf(amount)) ? formatAmount(String.valueOf(amount)) : String.valueOf(amount);
    }
	public String getLatestShipmentDateFormatted() {
        return _formatDate(latestShipmentDate);
    }
	
	public String getIssueDateFormatted() {
        return _formatDate(issueDate);
    }
	
	public String getExpiredOnFormatted() {
        return _formatDate(expiryDate);
    }
	public String getTradeCurrency() {
		return currency;
	}

	public String getServiceRequestSrmsId() {
		return exportLCId;
	}
	public String getProduct() {
		return "Export LC";
	}
	public String getAmendmentNo() {
		return amendmentNo;
	}

	public void setAmendmentNo(String amendmentNo) {
		this.amendmentNo = amendmentNo;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
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

	public ExportLOCDTO(String drawingAmount, String lcType, String lcReferenceNo, String issuingBankReference, String customerId,
			String advisingBankReference, String applicant, String utilizedLCAmount, String issueDate, String amount,
			String expiryDate, String currency, String issuingBank, String applicantaddress, String issuingbankaddress,
			String paymentTerms, String forwardContract, String beneficiaryName, String beneficiaryAddress,
			String goodsDescription, String additionalConditions, String confirmInstructions, String latestShipmentDate,
			String status, String exportLCId, String documentName, String uploadedFiles) {
		super();
		this.drawingAmount = drawingAmount;
		this.lcType = lcType;
		this.lcReferenceNo = lcReferenceNo;
		this.issuingBankReference = issuingBankReference;
		this.advisingBankReference = advisingBankReference;
		this.applicant = applicant;
		this.utilizedLCAmount = utilizedLCAmount;
		this.issueDate = issueDate;
		this.amount = amount;
		this.expiryDate = expiryDate;
		this.currency = currency;
		this.issuingBank = issuingBank;
		this.applicantaddress = applicantaddress;
		this.issuingbankaddress = issuingbankaddress;
		this.paymentTerms = paymentTerms;
		this.forwardContract = forwardContract;
		this.beneficiaryName = beneficiaryName;
		this.beneficiaryAddress = beneficiaryAddress;
		this.goodsDescription = goodsDescription;
		this.additionalConditions = additionalConditions;
		this.confirmInstructions = confirmInstructions;
		this.latestShipmentDate = latestShipmentDate;
		this.status = status;
		this.exportLCId = exportLCId;
		this.documentName = documentName;
		this.uploadedFiles = uploadedFiles;
		this.customerId  = customerId;
		
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((drawingAmount == null) ? 0 : drawingAmount.hashCode());
		result = prime * result + ((lcType == null) ? 0 : lcType.hashCode());
		result = prime * result + ((lcReferenceNo == null) ? 0 : lcReferenceNo.hashCode());
		result = prime * result + ((issuingBankReference == null) ? 0 : issuingBankReference.hashCode());
		result = prime * result + ((advisingBankReference == null) ? 0 : advisingBankReference.hashCode());
		result = prime * result + ((applicant == null) ? 0 : applicant.hashCode());
		result = prime * result + ((utilizedLCAmount == null) ? 0 : utilizedLCAmount.hashCode());
		result = prime * result + ((issueDate == null) ? 0 : issueDate.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + ((drawingAmount == null) ? 0 : drawingAmount.hashCode());
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((issuingBank == null) ? 0 : issuingBank.hashCode());
		result = prime * result + ((applicantaddress == null) ? 0 : applicantaddress.hashCode());
		result = prime * result + ((issuingbankaddress == null) ? 0 : issuingbankaddress.hashCode());
		result = prime * result + ((paymentTerms == null) ? 0 : paymentTerms.hashCode());
		result = prime * result + ((forwardContract == null) ? 0 : forwardContract.hashCode());
		result = prime * result + ((beneficiaryName == null) ? 0 : beneficiaryName.hashCode());
		result = prime * result + ((beneficiaryAddress == null) ? 0 : beneficiaryAddress.hashCode());
		result = prime * result + ((goodsDescription == null) ? 0 : goodsDescription.hashCode());
		result = prime * result + ((additionalConditions == null) ? 0 : additionalConditions.hashCode());
		result = prime * result + ((confirmInstructions == null) ? 0 : confirmInstructions.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((latestShipmentDate == null) ? 0 : latestShipmentDate.hashCode());
		result = prime * result + ((exportLCId == null) ? 0 : exportLCId.hashCode());
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		
		return result;
	}
	
	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getUploadedFiles() {
		return uploadedFiles;
	}

	public void setUploadedFiles(String uploadedFiles) {
		this.uploadedFiles = uploadedFiles;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public ExportLOCDTO() {
		super();
	}
	public String getDrawingAmount() {
		return drawingAmount;
	}
	public void setDrawingAmount(String drawingAmount) {
		this.drawingAmount = drawingAmount;
	}
	public String getLcType() {
		return lcType;
	}
	public void setLcType(String lcType) {
		this.lcType = lcType;
	}
	public String getLcReferenceNo() {
		return lcReferenceNo;
	}
	public void setLcReferenceNo(String lcReferenceNo) {
		this.lcReferenceNo = lcReferenceNo;
	}
	public String getIssuingBankReference() {
		return issuingBankReference;
	}
	public void setIssuingBankReference(String issuingBankReference) {
		this.issuingBankReference = issuingBankReference;
	}
	public String getAdvisingBankReference() {
		return advisingBankReference;
	}
	public void setAdvisingBankReference(String advisingBankReference) {
		this.advisingBankReference = advisingBankReference;
	}
	public String getApplicant() {
		return applicant;
	}
	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}
	public String getUtilizedLCAmount() {
		return utilizedLCAmount;
	}
	public void setUtilizedLCAmount(String utilizedLCAmount) {
		this.utilizedLCAmount = utilizedLCAmount;
	}
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getIssuingBank() {
		return issuingBank;
	}
	public void setIssuingBank(String issuingBank) {
		this.issuingBank = issuingBank;
	}
	public String getApplicantaddress() {
		return applicantaddress;
	}
	public void setApplicantaddress(String applicantaddress) {
		this.applicantaddress = applicantaddress;
	}
	public String getIssuingbankaddress() {
		return issuingbankaddress;
	}
	public void setIssuingbankaddress(String issuingbankaddress) {
		this.issuingbankaddress = issuingbankaddress;
	}
	public String getPaymentTerms() {
		return paymentTerms;
	}
	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}
	public String getForwardContract() {
		return forwardContract;
	}
	public void setForwardContract(String forwardContract) {
		this.forwardContract = forwardContract;
	}
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	public String getBeneficiaryAddress() {
		return beneficiaryAddress;
	}
	public void setBeneficiaryAddress(String beneficiaryAddress) {
		this.beneficiaryAddress = beneficiaryAddress;
	}
	public String getGoodsDescription() {
		return goodsDescription;
	}
	public void setGoodsDescription(String goodsDescription) {
		this.goodsDescription = goodsDescription;
	}
	public String getAdditionalConditions() {
		return additionalConditions;
	}
	public void setAdditionalConditions(String additionalConditions) {
		this.additionalConditions = additionalConditions;
	}
	public String getConfirmInstructions() {
		return confirmInstructions;
	}
	public void setConfirmInstructions(String confirmInstructions) {
		this.confirmInstructions = confirmInstructions;
	}
	public String getLatestShipmentDate() {
		return latestShipmentDate;
	}
	public void setLatestShipmentDate(String latestShipmentDate) {
		this.latestShipmentDate = latestShipmentDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getExportLCId() {
		return exportLCId;
	}
	public void setExportLCId(String exportLCId) {
		this.exportLCId = exportLCId;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExportLOCDTO other = (ExportLOCDTO) obj;
		return Objects.equals(additionalConditions, other.additionalConditions)
				&& Objects.equals(advisingBankReference, other.advisingBankReference)
				&& Objects.equals(amount, other.amount) && Objects.equals(applicant, other.applicant)
				&& Objects.equals(applicantaddress, other.applicantaddress)
				&& Objects.equals(beneficiaryAddress, other.beneficiaryAddress)
				&& Objects.equals(beneficiaryName, other.beneficiaryName)
				&& Objects.equals(confirmInstructions, other.confirmInstructions)
				&& Objects.equals(currency, other.currency) && Objects.equals(drawingAmount, other.drawingAmount)
				&& Objects.equals(expiryDate, other.expiryDate) && Objects.equals(exportLCId, other.exportLCId)
				&& Objects.equals(forwardContract, other.forwardContract)
				&& Objects.equals(goodsDescription, other.goodsDescription)
				&& Objects.equals(issueDate, other.issueDate) && Objects.equals(issuingBank, other.issuingBank)
				&& Objects.equals(issuingBankReference, other.issuingBankReference)
				&& Objects.equals(issuingbankaddress, other.issuingbankaddress)
				&& Objects.equals(latestShipmentDate, other.latestShipmentDate)
				&& Objects.equals(lcReferenceNo, other.lcReferenceNo) && Objects.equals(lcType, other.lcType)
				&& Objects.equals(paymentTerms, other.paymentTerms) && Objects.equals(status, other.status)
				&& Objects.equals(utilizedLCAmount, other.utilizedLCAmount);
	}

	public String getBeneficiaryConsent() {
		return beneficiaryConsent;
	}

	public void setBeneficiaryConsent(String beneficiaryConsent) {
		this.beneficiaryConsent = beneficiaryConsent;
	}

	public String getReasonForRejection() {
		return reasonForRejection;
	}

	public void setReasonForRejection(String reasonForRejection) {
		this.reasonForRejection = reasonForRejection;
	}

	public String getMessageToBank() {
		return messageToBank;
	}

	public void setMessageToBank(String messageToBank) {
		this.messageToBank = messageToBank;
	}

	public String getLcUpdatedOn() {
		return lcUpdatedOn;
	}

	public void setLcUpdatedOn(String lcUpdatedOn) {
		this.lcUpdatedOn = lcUpdatedOn;
	}

}
