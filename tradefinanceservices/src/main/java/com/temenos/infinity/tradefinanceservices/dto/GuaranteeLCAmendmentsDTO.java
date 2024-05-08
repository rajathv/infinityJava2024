/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.dto;

import com.dbp.core.api.DBPDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import static com.temenos.infinity.tradefinanceservices.utils.TradeFinanceCommonUtils._formatDate;

import org.apache.commons.lang3.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GuaranteeLCAmendmentsDTO implements DBPDTO {

    private static final long serialVersionUID = 164174856490598625L;

    private String benificiaryName;
    private String beneficiaryDetails;
    private String guaranteesReference;
    private String currency;
    private String amount;
    private String expiryDate;
    private String productType;
    private String issueDate;
    private String instructingParty;
    private String billType;
    private String expiryType;
    private String applicantParty;
    private String amendmentNo;
    private String amendmentEffectiveDate;
    private String amendAmount;
    private String amendCharges;
    private String amendExpiryType;
    private String amendExpiryDate;
    private String amendExpiryCondition;
    private String amendDetails;
    private String messageToBank;
    private String amendStatus;
    private String amendRequestedDate;
    private String amendmentReference;
    private String amendmentSRMSRequestId;
    private String cancellationStatus;
    private String approvedDate;
    private String reasonForReturned;
    private String returnMessage;
    private String corporateUserName;
    private String supportingDocument;
    private String rejectedReason;
    private String rejectedDate;
    private String guaranteesSRMSId;
    private String dbpErrMsg;
    private String dbpErrCode;
    private String historyCount;
    private String amendmentHistory1;
    private String amendmentHistory2;
    private String amendmentHistory3;
    private String amendmentHistory4;
    private String amendmentHistory5;
    private String amountWithCurrency;
    private String amendRequestedDateFormatted;
    private String product;
    private String serviceRequestSrmsId;
    private String tradeCurrency;
    @JsonIgnore
    private String createdOnFormatted;
    @JsonIgnore
    private String amendEffectiveDateFormatted;
    
    public String getAmendEffectiveDateFormatted() {
        return _formatDate(amendmentEffectiveDate);
    }
    
    public String getCreatedOnFormatted() {
        return _formatDate(amendRequestedDate);
    }
    
    public String getTradeCurrency() {
        return currency;
    }
    
    public String getServiceRequestSrmsId() {
        return amendmentSRMSRequestId;
    }
    
    public String getProduct() {
        return "Issued GT & SBLC Amendment";
    }
    
    public String getBenificiaryName() {
        return benificiaryName;
    }

    public void setBenificiaryName(String benificiaryName) {
        this.benificiaryName = benificiaryName;
    }

    public String getBeneficiaryDetails() {
        return beneficiaryDetails;
    }

    public void setBeneficiaryDetails(String beneficiaryDetails) {
        this.beneficiaryDetails = beneficiaryDetails;
    }

    public String getGuaranteesReference() {
        return guaranteesReference;
    }

    public void setGuaranteesReference(String guaranteesReference) {
        this.guaranteesReference = guaranteesReference;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getInstructingParty() {
        return instructingParty;
    }

    public void setInstructingParty(String instructingParty) {
        this.instructingParty = instructingParty;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getExpiryType() {
        return expiryType;
    }

    public void setExpiryType(String expiryType) {
        this.expiryType = expiryType;
    }

    public String getApplicantParty() {
        return applicantParty;
    }

    public void setApplicantParty(String applicantParty) {
        this.applicantParty = applicantParty;
    }

    public String getAmendmentNo() {
        return amendmentNo;
    }

    public void setAmendmentNo(String amendmentNo) {
        this.amendmentNo = amendmentNo;
    }

    public String getAmendmentEffectiveDate() {
        return amendmentEffectiveDate;
    }

    public void setAmendmentEffectiveDate(String amendmentEffectiveDate) {
        this.amendmentEffectiveDate = amendmentEffectiveDate;
    }

    public String getAmendAmount() {
        return amendAmount;
    }

    public void setAmendAmount(String amendAmount) {
        this.amendAmount = amendAmount;
    }

    public String getAmendCharges() {
        return amendCharges;
    }

    public void setAmendCharges(String amendCharges) {
        this.amendCharges = amendCharges;
    }

    public String getAmendExpiryType() {
        return amendExpiryType;
    }

    public void setAmendExpiryType(String amendExpiryType) {
        this.amendExpiryType = amendExpiryType;
    }

    public String getAmendExpiryDate() {
        return amendExpiryDate;
    }

    public void setAmendExpiryDate(String amendExpiryDate) {
        this.amendExpiryDate = amendExpiryDate;
    }

    public String getAmendExpiryCondition() {
        return amendExpiryCondition;
    }

    public void setAmendExpiryCondition(String amendExpiryCondition) {
        this.amendExpiryCondition = amendExpiryCondition;
    }

    public String getAmendDetails() {
        return amendDetails;
    }

    public void setAmendDetails(String amendDetails) {
        this.amendDetails = amendDetails;
    }

    public String getMessageToBank() {
        return messageToBank;
    }

    public void setMessageToBank(String messageToBank) {
        this.messageToBank = messageToBank;
    }

    public String getAmendStatus() {
        return amendStatus;
    }

    public void setAmendStatus(String amendStatus) {
        this.amendStatus = amendStatus;
    }

    public String getAmendRequestedDate() {
        return amendRequestedDate;
    }

    public void setAmendRequestedDate(String amendRequestedDate) {
        this.amendRequestedDate = amendRequestedDate;
    }

    public String getAmendmentReference() {
        return amendmentReference;
    }

    public void setAmendmentReference(String amendmentReference) {
        this.amendmentReference = amendmentReference;
    }

    public String getAmendmentSRMSRequestId() {
        return amendmentSRMSRequestId;
    }

    public void setAmendmentSRMSRequestId(String amendmentSRMSRequestId) {
        this.amendmentSRMSRequestId = amendmentSRMSRequestId;
    }

    public String getCancellationStatus() {
        return cancellationStatus;
    }

    public void setCancellationStatus(String cancellationStatus) {
        this.cancellationStatus = cancellationStatus;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getReasonForReturned() {
        return reasonForReturned;
    }

    public void setReasonForReturned(String reasonForReturned) {
        this.reasonForReturned = reasonForReturned;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public String getCorporateUserName() {
        return corporateUserName;
    }

    public void setCorporateUserName(String corporateUserName) {
        this.corporateUserName = corporateUserName;
    }

    public String getSupportingDocument() {
        return supportingDocument;
    }

    public void setSupportingDocument(String supportingDocument) {
        this.supportingDocument = supportingDocument;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public String getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(String rejectedDate) {
        this.rejectedDate = rejectedDate;
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

    public String getHistoryCount() {
        return historyCount;
    }

    public void setHistoryCount(String historyCount) {
        this.historyCount = historyCount;
    }

    public String getAmendmentHistory1() {
        return amendmentHistory1;
    }

    public void setAmendmentHistory1(String amendmentHistory1) {
        this.amendmentHistory1 = amendmentHistory1;
    }

    public String getAmendmentHistory2() {
        return amendmentHistory2;
    }

    public void setAmendmentHistory2(String amendmentHistory2) {
        this.amendmentHistory2 = amendmentHistory2;
    }

    public String getAmendmentHistory3() {
        return amendmentHistory3;
    }

    public void setAmendmentHistory3(String amendmentHistory3) {
        this.amendmentHistory3 = amendmentHistory3;
    }

    public String getAmendmentHistory4() {
        return amendmentHistory4;
    }

    public void setAmendmentHistory4(String amendmentHistory4) {
        this.amendmentHistory4 = amendmentHistory4;
    }

    public String getAmendmentHistory5() {
        return amendmentHistory5;
    }

    public void setAmendmentHistory5(String amendmentHistory5) {
        this.amendmentHistory5 = amendmentHistory5;
    }

    public String getAmountWithCurrency() {
        return amountWithCurrency;
    }

    public void setAmountWithCurrency(String amountWithCurrency) {
        this.amountWithCurrency = amountWithCurrency;
    }

    public String getAmendRequestedDateFormatted() {
        return amendRequestedDateFormatted;
    }

    public void setAmendRequestedDateFormatted(String amendRequestedDateFormatted) {
        this.amendRequestedDateFormatted = amendRequestedDateFormatted;
    }

    public GuaranteeLCAmendmentsDTO() {
    }

    public GuaranteeLCAmendmentsDTO(String benificiaryName, String beneficiaryDetails, String guaranteesReference,
                                    String currency, String amount, String expiryDate, String productType, String issueDate,
                                    String instructingParty, String billType, String expiryType, String applicantParty, String amendmentNo,
                                    String amendmentEffectiveDate, String amendAmount, String amendCharges, String amendExpiryType,
                                    String amendExpiryDate, String amendExpiryCondition, String amendDetails, String messageToBank,
                                    String amendStatus, String amendRequestedDate, String amendmentReference, String amendmentSRMSRequestId,
                                    String cancellationStatus, String approvedDate, String reasonForReturned, String returnMessage,
                                    String corporateUserName, String supportingDocument, String rejectedReason, String rejectedDate,
                                    String guaranteesSRMSId, String dbpErrMsg, String dbpErrCode, String historyCount, String amendmentHistory1,
                                    String amendmentHistory2, String amendmentHistory3, String amendmentHistory4, String amendmentHistory5,
                                    String amountWithCurrency, String amendRequestedDateFormatted) {
        super();
        this.benificiaryName = benificiaryName;
        this.beneficiaryDetails = beneficiaryDetails;
        this.guaranteesReference = guaranteesReference;
        this.currency = currency;
        this.amount = amount;
        this.expiryDate = expiryDate;
        this.productType = productType;
        this.issueDate = issueDate;
        this.instructingParty = instructingParty;
        this.billType = billType;
        this.expiryType = expiryType;
        this.applicantParty = applicantParty;
        this.amendmentNo = amendmentNo;
        this.amendmentEffectiveDate = amendmentEffectiveDate;
        this.amendAmount = amendAmount;
        this.amendCharges = amendCharges;
        this.amendExpiryType = amendExpiryType;
        this.amendExpiryDate = amendExpiryDate;
        this.amendExpiryCondition = amendExpiryCondition;
        this.amendDetails = amendDetails;
        this.messageToBank = messageToBank;
        this.amendStatus = amendStatus;
        this.amendRequestedDate = amendRequestedDate;
        this.amendmentReference = amendmentReference;
        this.amendmentSRMSRequestId = amendmentSRMSRequestId;
        this.cancellationStatus = cancellationStatus;
        this.approvedDate = approvedDate;
        this.reasonForReturned = reasonForReturned;
        this.returnMessage = returnMessage;
        this.corporateUserName = corporateUserName;
        this.supportingDocument = supportingDocument;
        this.rejectedReason = rejectedReason;
        this.rejectedDate = rejectedDate;
        this.guaranteesSRMSId = guaranteesSRMSId;
        this.dbpErrMsg = dbpErrMsg;
        this.dbpErrCode = dbpErrCode;
        this.historyCount = historyCount;
        this.amendmentHistory1 = amendmentHistory1;
        this.amendmentHistory2 = amendmentHistory2;
        this.amendmentHistory3 = amendmentHistory3;
        this.amendmentHistory4 = amendmentHistory4;
        this.amendmentHistory5 = amendmentHistory5;
        this.amountWithCurrency = amountWithCurrency;
        this.amendRequestedDateFormatted = amendRequestedDateFormatted;
    }

    public GuaranteeLCAmendmentsDTO swap(GuaranteeLCAmendmentsDTO inputDTO) {
        if (StringUtils.isNotEmpty(inputDTO.getBenificiaryName()))
            this.benificiaryName = inputDTO.getBenificiaryName();
        if (StringUtils.isNotEmpty(inputDTO.getBeneficiaryDetails()))
            this.beneficiaryDetails = inputDTO.getBeneficiaryDetails();
        if (StringUtils.isNotEmpty(inputDTO.getGuaranteesReference()))
            this.guaranteesReference = inputDTO.getGuaranteesReference();
        if (StringUtils.isNotEmpty(inputDTO.getCurrency()))
            this.currency = inputDTO.getCurrency();
        if (StringUtils.isNotEmpty(inputDTO.getAmount()))
            this.amount = inputDTO.getAmount();
        if (StringUtils.isNotEmpty(inputDTO.getExpiryDate()))
            this.expiryDate = inputDTO.getExpiryDate();
        if (StringUtils.isNotEmpty(inputDTO.getProductType()))
            this.productType = inputDTO.getProductType();
        if (StringUtils.isNotEmpty(inputDTO.getIssueDate()))
            this.issueDate = inputDTO.getIssueDate();
        if (StringUtils.isNotEmpty(inputDTO.getInstructingParty()))
            this.instructingParty = inputDTO.getInstructingParty();
        if (StringUtils.isNotEmpty(inputDTO.getBillType()))
            this.billType = inputDTO.getBillType();
        if (StringUtils.isNotEmpty(inputDTO.getExpiryType()))
            this.expiryType = inputDTO.getExpiryType();
        if (StringUtils.isNotEmpty(inputDTO.getApplicantParty()))
            this.applicantParty = inputDTO.getApplicantParty();
        if (StringUtils.isNotEmpty(inputDTO.getAmendmentNo()))
            this.amendmentNo = inputDTO.getAmendmentNo();
        if (StringUtils.isNotEmpty(inputDTO.getAmendmentEffectiveDate()))
            this.amendmentEffectiveDate = inputDTO.getAmendmentEffectiveDate();
        if (StringUtils.isNotEmpty(inputDTO.getAmendAmount()))
            this.amendAmount = inputDTO.getAmendAmount();
        if (StringUtils.isNotEmpty(inputDTO.getAmendCharges()))
            this.amendCharges = inputDTO.getAmendCharges();
        if (StringUtils.isNotEmpty(inputDTO.getAmendExpiryType()))
            this.amendExpiryType = inputDTO.getAmendExpiryType();
        if (StringUtils.isNotEmpty(inputDTO.getAmendExpiryDate()))
            this.amendExpiryDate = inputDTO.getAmendExpiryDate();
        if (StringUtils.isNotEmpty(inputDTO.getAmendExpiryCondition()))
            this.amendExpiryCondition = inputDTO.getAmendExpiryCondition();
        if (StringUtils.isNotEmpty(inputDTO.getAmendDetails()))
            this.amendDetails = inputDTO.getAmendDetails();
        if (StringUtils.isNotEmpty(inputDTO.getMessageToBank()))
            this.messageToBank = inputDTO.getMessageToBank();
        if (StringUtils.isNotEmpty(inputDTO.getAmendStatus()))
            this.amendStatus = inputDTO.getAmendStatus();
        if (StringUtils.isNotEmpty(inputDTO.getAmendmentReference()))
            this.amendmentReference = inputDTO.getAmendmentReference();
        if (StringUtils.isNotEmpty(inputDTO.getAmendmentSRMSRequestId()))
            this.amendmentSRMSRequestId = inputDTO.getAmendmentSRMSRequestId();
        if (StringUtils.isNotEmpty(inputDTO.getCancellationStatus()))
            this.cancellationStatus = inputDTO.getCancellationStatus();
        if (StringUtils.isNotEmpty(inputDTO.getApprovedDate()))
            this.approvedDate = inputDTO.getApprovedDate();
        if (StringUtils.isNotEmpty(inputDTO.getReasonForReturned()))
            this.reasonForReturned = inputDTO.getReasonForReturned();
        if (StringUtils.isNotEmpty(inputDTO.getReturnMessage()))
            this.returnMessage = inputDTO.getReturnMessage();
        if (StringUtils.isNotEmpty(inputDTO.getCorporateUserName()))
            this.corporateUserName = inputDTO.getCorporateUserName();
        if (StringUtils.isNotEmpty(inputDTO.getSupportingDocument()))
            this.supportingDocument = inputDTO.getSupportingDocument();
        if (StringUtils.isNotEmpty(inputDTO.getRejectedReason()))
            this.rejectedReason = inputDTO.getRejectedReason();
        if (StringUtils.isNotEmpty(inputDTO.getRejectedDate()))
            this.rejectedDate = inputDTO.getRejectedDate();
        if (StringUtils.isNotEmpty(inputDTO.getGuaranteesSRMSId()))
            this.guaranteesSRMSId = inputDTO.getGuaranteesSRMSId();
        if (StringUtils.isNotEmpty(inputDTO.getAmendRequestedDate()))
            this.amendRequestedDate = inputDTO.getAmendRequestedDate();
        if (StringUtils.isNotEmpty(inputDTO.getHistoryCount()))
            this.historyCount = inputDTO.getHistoryCount();
        return this;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amendAmount == null) ? 0 : amendAmount.hashCode());
        result = prime * result + ((amendCharges == null) ? 0 : amendCharges.hashCode());
        result = prime * result + ((amendDetails == null) ? 0 : amendDetails.hashCode());
        result = prime * result + ((amendExpiryCondition == null) ? 0 : amendExpiryCondition.hashCode());
        result = prime * result + ((amendExpiryDate == null) ? 0 : amendExpiryDate.hashCode());
        result = prime * result + ((amendExpiryType == null) ? 0 : amendExpiryType.hashCode());
        result = prime * result + ((amendRequestedDate == null) ? 0 : amendRequestedDate.hashCode());
        result = prime * result + ((amendStatus == null) ? 0 : amendStatus.hashCode());
        result = prime * result + ((amendmentEffectiveDate == null) ? 0 : amendmentEffectiveDate.hashCode());
        result = prime * result + ((amendmentHistory1 == null) ? 0 : amendmentHistory1.hashCode());
        result = prime * result + ((amendmentHistory2 == null) ? 0 : amendmentHistory2.hashCode());
        result = prime * result + ((amendmentHistory3 == null) ? 0 : amendmentHistory3.hashCode());
        result = prime * result + ((amendmentHistory4 == null) ? 0 : amendmentHistory4.hashCode());
        result = prime * result + ((amendmentHistory5 == null) ? 0 : amendmentHistory5.hashCode());
        result = prime * result + ((amendmentNo == null) ? 0 : amendmentNo.hashCode());
        result = prime * result + ((amendmentReference == null) ? 0 : amendmentReference.hashCode());
        result = prime * result + ((amendmentSRMSRequestId == null) ? 0 : amendmentSRMSRequestId.hashCode());
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((applicantParty == null) ? 0 : applicantParty.hashCode());
        result = prime * result + ((approvedDate == null) ? 0 : approvedDate.hashCode());
        result = prime * result + ((beneficiaryDetails == null) ? 0 : beneficiaryDetails.hashCode());
        result = prime * result + ((benificiaryName == null) ? 0 : benificiaryName.hashCode());
        result = prime * result + ((billType == null) ? 0 : billType.hashCode());
        result = prime * result + ((cancellationStatus == null) ? 0 : cancellationStatus.hashCode());
        result = prime * result + ((corporateUserName == null) ? 0 : corporateUserName.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((dbpErrCode == null) ? 0 : dbpErrCode.hashCode());
        result = prime * result + ((dbpErrMsg == null) ? 0 : dbpErrMsg.hashCode());
        result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
        result = prime * result + ((expiryType == null) ? 0 : expiryType.hashCode());
        result = prime * result + ((guaranteesReference == null) ? 0 : guaranteesReference.hashCode());
        result = prime * result + ((guaranteesSRMSId == null) ? 0 : guaranteesSRMSId.hashCode());
        result = prime * result + ((historyCount == null) ? 0 : historyCount.hashCode());
        result = prime * result + ((instructingParty == null) ? 0 : instructingParty.hashCode());
        result = prime * result + ((issueDate == null) ? 0 : issueDate.hashCode());
        result = prime * result + ((messageToBank == null) ? 0 : messageToBank.hashCode());
        result = prime * result + ((productType == null) ? 0 : productType.hashCode());
        result = prime * result + ((reasonForReturned == null) ? 0 : reasonForReturned.hashCode());
        result = prime * result + ((rejectedDate == null) ? 0 : rejectedDate.hashCode());
        result = prime * result + ((rejectedReason == null) ? 0 : rejectedReason.hashCode());
        result = prime * result + ((returnMessage == null) ? 0 : returnMessage.hashCode());
        result = prime * result + ((supportingDocument == null) ? 0 : supportingDocument.hashCode());
        result = prime * result + ((amountWithCurrency == null) ? 0 : amountWithCurrency.hashCode());
        result = prime * result + ((amendRequestedDateFormatted == null) ? 0 : amendRequestedDateFormatted.hashCode());
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
        GuaranteeLCAmendmentsDTO other = (GuaranteeLCAmendmentsDTO) obj;
        if (amendAmount == null) {
            if (other.amendAmount != null)
                return false;
        } else if (!amendAmount.equals(other.amendAmount))
            return false;
        if (amendCharges == null) {
            if (other.amendCharges != null)
                return false;
        } else if (!amendCharges.equals(other.amendCharges))
            return false;
        if (amendDetails == null) {
            if (other.amendDetails != null)
                return false;
        } else if (!amendDetails.equals(other.amendDetails))
            return false;
        if (amendExpiryCondition == null) {
            if (other.amendExpiryCondition != null)
                return false;
        } else if (!amendExpiryCondition.equals(other.amendExpiryCondition))
            return false;
        if (amendExpiryDate == null) {
            if (other.amendExpiryDate != null)
                return false;
        } else if (!amendExpiryDate.equals(other.amendExpiryDate))
            return false;
        if (amendExpiryType == null) {
            if (other.amendExpiryType != null)
                return false;
        } else if (!amendExpiryType.equals(other.amendExpiryType))
            return false;
        if (amendRequestedDate == null) {
            if (other.amendRequestedDate != null)
                return false;
        } else if (!amendRequestedDate.equals(other.amendRequestedDate))
            return false;
        if (amendStatus == null) {
            if (other.amendStatus != null)
                return false;
        } else if (!amendStatus.equals(other.amendStatus))
            return false;
        if (amendmentEffectiveDate == null) {
            if (other.amendmentEffectiveDate != null)
                return false;
        } else if (!amendmentEffectiveDate.equals(other.amendmentEffectiveDate))
            return false;
        if (amendmentHistory1 == null) {
            if (other.amendmentHistory1 != null)
                return false;
        } else if (!amendmentHistory1.equals(other.amendmentHistory1))
            return false;
        if (amendmentHistory2 == null) {
            if (other.amendmentHistory2 != null)
                return false;
        } else if (!amendmentHistory2.equals(other.amendmentHistory2))
            return false;
        if (amendmentHistory3 == null) {
            if (other.amendmentHistory3 != null)
                return false;
        } else if (!amendmentHistory3.equals(other.amendmentHistory3))
            return false;
        if (amendmentHistory4 == null) {
            if (other.amendmentHistory4 != null)
                return false;
        } else if (!amendmentHistory4.equals(other.amendmentHistory4))
            return false;
        if (amendmentHistory5 == null) {
            if (other.amendmentHistory5 != null)
                return false;
        } else if (!amendmentHistory5.equals(other.amendmentHistory5))
            return false;
        if (amendmentNo == null) {
            if (other.amendmentNo != null)
                return false;
        } else if (!amendmentNo.equals(other.amendmentNo))
            return false;
        if (amendmentReference == null) {
            if (other.amendmentReference != null)
                return false;
        } else if (!amendmentReference.equals(other.amendmentReference))
            return false;
        if (amendmentSRMSRequestId == null) {
            if (other.amendmentSRMSRequestId != null)
                return false;
        } else if (!amendmentSRMSRequestId.equals(other.amendmentSRMSRequestId))
            return false;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (applicantParty == null) {
            if (other.applicantParty != null)
                return false;
        } else if (!applicantParty.equals(other.applicantParty))
            return false;
        if (approvedDate == null) {
            if (other.approvedDate != null)
                return false;
        } else if (!approvedDate.equals(other.approvedDate))
            return false;
        if (beneficiaryDetails == null) {
            if (other.beneficiaryDetails != null)
                return false;
        } else if (!beneficiaryDetails.equals(other.beneficiaryDetails))
            return false;
        if (benificiaryName == null) {
            if (other.benificiaryName != null)
                return false;
        } else if (!benificiaryName.equals(other.benificiaryName))
            return false;
        if (billType == null) {
            if (other.billType != null)
                return false;
        } else if (!billType.equals(other.billType))
            return false;
        if (cancellationStatus == null) {
            if (other.cancellationStatus != null)
                return false;
        } else if (!cancellationStatus.equals(other.cancellationStatus))
            return false;
        if (corporateUserName == null) {
            if (other.corporateUserName != null)
                return false;
        } else if (!corporateUserName.equals(other.corporateUserName))
            return false;
        if (currency == null) {
            if (other.currency != null)
                return false;
        } else if (!currency.equals(other.currency))
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
        if (guaranteesReference == null) {
            if (other.guaranteesReference != null)
                return false;
        } else if (!guaranteesReference.equals(other.guaranteesReference))
            return false;
        if (guaranteesSRMSId == null) {
            if (other.guaranteesSRMSId != null)
                return false;
        } else if (!guaranteesSRMSId.equals(other.guaranteesSRMSId))
            return false;
        if (historyCount == null) {
            if (other.historyCount != null)
                return false;
        } else if (!historyCount.equals(other.historyCount))
            return false;
        if (instructingParty == null) {
            if (other.instructingParty != null)
                return false;
        } else if (!instructingParty.equals(other.instructingParty))
            return false;
        if (issueDate == null) {
            if (other.issueDate != null)
                return false;
        } else if (!issueDate.equals(other.issueDate))
            return false;
        if (messageToBank == null) {
            if (other.messageToBank != null)
                return false;
        } else if (!messageToBank.equals(other.messageToBank))
            return false;
        if (productType == null) {
            if (other.productType != null)
                return false;
        } else if (!productType.equals(other.productType))
            return false;
        if (reasonForReturned == null) {
            if (other.reasonForReturned != null)
                return false;
        } else if (!reasonForReturned.equals(other.reasonForReturned))
            return false;
        if (rejectedDate == null) {
            if (other.rejectedDate != null)
                return false;
        } else if (!rejectedDate.equals(other.rejectedDate))
            return false;
        if (rejectedReason == null) {
            if (other.rejectedReason != null)
                return false;
        } else if (!rejectedReason.equals(other.rejectedReason))
            return false;
        if (returnMessage == null) {
            if (other.returnMessage != null)
                return false;
        } else if (!returnMessage.equals(other.returnMessage))
            return false;
        if (supportingDocument == null) {
            if (other.supportingDocument != null)
                return false;
        } else if (!supportingDocument.equals(other.supportingDocument))
            return false;
        if (amountWithCurrency == null) {
            if (other.amountWithCurrency != null)
                return false;
        } else if (!amountWithCurrency.equals(other.amountWithCurrency))
            return false;
        if (amendRequestedDateFormatted == null) {
            if (other.amendRequestedDateFormatted != null)
                return false;
        } else if (!amendRequestedDateFormatted.equals(other.amendRequestedDateFormatted))
            return false;
        return true;
    }

}
