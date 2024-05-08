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

@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DrawingsDTO implements Serializable, DBPDTO {
	private static final long serialVersionUID = 158234743696496699L;

	private String lcReferenceNo;
	private String lcType;
	private String drawingReferenceNo;
	private String beneficiaryName;
	private String documentStatus;
	private String drawingCreationDate;
	private String drawingCurrency;
	private String drawingAmount;
	private String drawingStatus;
	private String lcAmount;
	private String lcCurrency;
	private String lcIssueDate;
	private String lcExpiryDate;
	private String paymentTerms;
	private String presentorReference;
	private String presentorName;
	private String documentsReceived;
	private String forwardContact;
	private String shippingGuaranteeReference;
	private String approvalDate;
	private String totalDocuments;
	private String documentName;
	private String discrepancyDescription;
	private String paymentStatus;
	private String rejectedDate;
	private String totalAmountToBePaid;
	private String accountToBeDebited;
	private String messageFromBank;
	private String messageToBank;
	private String totalPaidAmount;
	private String paymentDate;
	private String reasonForRejection;
	private String discrepancies;
	private String acceptance;
	private String messageType;
	private String deliveryDestination;
	private String messageDate;
	private String messageCategory;
	private String CustomerId;
	private String flowType;
	private String status;
	private String errorCode;
	private String errorMessage;
	private String lcSrmsReqOrderID;
	private String drawingsSrmsReqOrderID;
	private String product;
	private String serviceRequestSrmsId;
	private String tradeCurrency;
	@JsonIgnore
    private String createdOnFormatted;
	@JsonIgnore
    private String amountFormatted;
		
	public String getCreatedOnFormatted() {
        return _formatDate(drawingCreationDate);
    }
	
	public String getAmountFormatted() {
        return StringUtils.isNotBlank(String.valueOf(lcAmount)) ? formatAmount(String.valueOf(lcAmount)) : String.valueOf(lcAmount);
    }
	 
	public String getTradeCurrency() {
		return drawingCurrency;
	}

	public String getServiceRequestSrmsId() {
		return drawingsSrmsReqOrderID;
	}
	public String getProduct() {
		return "Import Drawing";
	}

	public String getLcReferenceNo() {
		return lcReferenceNo;
	}

	public void setLcReferenceNo(String lcReferenceNo) {
		this.lcReferenceNo = lcReferenceNo;
	}

	public String getLcType() {
		return lcType;
	}

	public void setLcType(String lcType) {
		this.lcType = lcType;
	}

	public String getDrawingReferenceNo() {
		return drawingReferenceNo;
	}

	public void setDrawingReferenceNo(String drawingReferenceNo) {
		this.drawingReferenceNo = drawingReferenceNo;
	}

	public String getBeneficiaryName() {
		return beneficiaryName;
	}

	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}

	public String getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus;
	}

	public String getDrawingCreationDate() {
		try {
			return HelperMethods.changeDateFormat(drawingCreationDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setDrawingCreationDate(String drawingCreationDate) {
		this.drawingCreationDate = drawingCreationDate;
	}

	public String getDrawingCurrency() {
		return drawingCurrency;
	}

	public void setDrawingCurrency(String drawingCurrency) {
		this.drawingCurrency = drawingCurrency;
	}

	public String getDrawingAmount() {
		return drawingAmount;
	}

	public void setDrawingAmount(String drawingAmount) {
		this.drawingAmount = drawingAmount;
	}

	public String getDrawingStatus() {
		return drawingStatus;
	}

	public void setDrawingStatus(String drawingStatus) {
		this.drawingStatus = drawingStatus;
	}

	public String getLcAmount() {
		return lcAmount;
	}

	public void setLcAmount(String lcAmount) {
		this.lcAmount = lcAmount;
	}

	public String getLcCurrency() {
		return lcCurrency;
	}

	public void setLcCurrency(String lcCurrency) {
		this.lcCurrency = lcCurrency;
	}

	public String getLcIssueDate() {
		try {
			return HelperMethods.changeDateFormat(lcIssueDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setLcIssueDate(String lcIssueDate) {
		this.lcIssueDate = lcIssueDate;
	}

	public String getLcExpiryDate() {
		try {
			return HelperMethods.changeDateFormat(lcExpiryDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setLcExpiryDate(String lcExpiryDate) {
		this.lcExpiryDate = lcExpiryDate;
	}

	public String getPaymentTerms() {
		return paymentTerms;
	}

	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	public String getPresentorReference() {
		return presentorReference;
	}

	public void setPresentorReference(String presentorReference) {
		this.presentorReference = presentorReference;
	}

	public String getPresentorName() {
		return presentorName;
	}

	public void setPresentorName(String presentorName) {
		this.presentorName = presentorName;
	}

	public String getDocumentsReceived() {
		return documentsReceived;
	}

	public void setDocumentsReceived(String documentsReceived) {
		this.documentsReceived = documentsReceived;
	}

	public String getForwardContact() {
		return forwardContact;
	}

	public void setForwardContact(String forwardContact) {
		this.forwardContact = forwardContact;
	}

	public String getShippingGuaranteeReference() {
		return shippingGuaranteeReference;
	}

	public void setShippingGuaranteeReference(String shippingGuaranteeReference) {
		this.shippingGuaranteeReference = shippingGuaranteeReference;
	}

	public String getApprovalDate() {
		try {
			return HelperMethods.changeDateFormat(approvalDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setApprovalDate(String approvalDate) {
		this.approvalDate = approvalDate;
	}

	public String getTotalDocuments() {
		return totalDocuments;
	}

	public void setTotalDocuments(String totalDocuments) {
		this.totalDocuments = totalDocuments;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public String getDiscrepancyDescription() {
		return discrepancyDescription;
	}

	public void setDiscrepancyDescription(String discrepancyDescription) {
		this.discrepancyDescription = discrepancyDescription;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getRejectedDate() {
		try {
			return HelperMethods.changeDateFormat(rejectedDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setRejectedDate(String rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	public String getTotalAmountToBePaid() {
		return totalAmountToBePaid;
	}

	public void setTotalAmountToBePaid(String totalAmountToBePaid) {
		this.totalAmountToBePaid = totalAmountToBePaid;
	}

	public String getAccountToBeDebited() {
		return accountToBeDebited;
	}

	public void setAccountToBeDebited(String accountToBeDebited) {
		this.accountToBeDebited = accountToBeDebited;
	}

	public String getMessageFromBank() {
		return messageFromBank;
	}

	public void setMessageFromBank(String messageFromBank) {
		this.messageFromBank = messageFromBank;
	}

	public String getMessageToBank() {
		return messageToBank;
	}

	public void setMessageToBank(String messageToBank) {
		this.messageToBank = messageToBank;
	}

	public String getTotalPaidAmount() {
		return totalPaidAmount;
	}

	public void setTotalPaidAmount(String totalPaidAmount) {
		this.totalPaidAmount = totalPaidAmount;
	}

	public String getPaymentDate() {
		try {
			return HelperMethods.changeDateFormat(paymentDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getReasonForRejection() {
		return reasonForRejection;
	}

	public void setReasonForRejection(String reasonForRejection) {
		this.reasonForRejection = reasonForRejection;
	}

	public String getDiscrepancies() {
		return discrepancies;
	}

	public void setDiscrepancies(String discrepancies) {
		this.discrepancies = discrepancies;
	}

	public String getAcceptance() {
		return acceptance;
	}

	public void setAcceptance(String acceptance) {
		this.acceptance = acceptance;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getDeliveryDestination() {
		return deliveryDestination;
	}

	public void setDeliveryDestination(String deliveryDestination) {
		this.deliveryDestination = deliveryDestination;
	}

	public String getMessageDate() {
		try {
			return HelperMethods.changeDateFormat(messageDate, Constants.TIMESTAMP_FORMAT);
		} catch (ParseException e) {
			return null;
		}
	}

	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}

	public String getMessageCategory() {
		return messageCategory;
	}

	public void setMessageCategory(String messageCategory) {
		this.messageCategory = messageCategory;
	}

	public String getFlowType() {
		return flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getLcSrmsReqOrderID() {
		return lcSrmsReqOrderID;
	}

	public void setLcSrmsReqOrderID(String lcSrmsReqOrderID) {
		this.lcSrmsReqOrderID = lcSrmsReqOrderID;
	}

	public String getDrawingsSrmsReqOrderID() {
		return drawingsSrmsReqOrderID;
	}

	public void setDrawingsSrmsReqOrderID(String drawingsSrmsReqOrderID) {
		this.drawingsSrmsReqOrderID = drawingsSrmsReqOrderID;
	}

	public DrawingsDTO() {
		super();
	}

	public DrawingsDTO(String lcReferenceNo, String lcType, String drawingReferenceNo, String beneficiaryName,
			String documentStatus, String drawingCreationDate, String drawingCurrency, String drawingAmount,
			String drawingStatus, String lcAmount, String lcCurrency, String lcIssueDate, String lcExpiryDate,
			String paymentTerms, String presentorReference, String presentorName, String documentsReceived,
			String forwardContact, String shippingGuaranteeReference, String approvalDate, String totalDocuments,
			String documentName, String discrepancyDescription, String paymentStatus, String rejectedDate,
			String totalAmountToBePaid, String accountToBeDebited, String messageFromBank, String messageToBank,
			String totalPaidAmount, String paymentDate, String reasonForRejection, String discrepancies,
			String acceptance, String messageType, String deliveryDestination, String messageDate,
			String messageCategory, String flowType, String status, String errorCode, String errorMessage,
			String lcSrmsReqOrderID, String drawingsSrmsReqOrderID) {
		super();
		this.lcReferenceNo = lcReferenceNo;
		this.lcType = lcType;
		this.drawingReferenceNo = drawingReferenceNo;
		this.beneficiaryName = beneficiaryName;
		this.documentStatus = documentStatus;
		this.drawingCreationDate = drawingCreationDate;
		this.drawingCurrency = drawingCurrency;
		this.drawingAmount = drawingAmount;
		this.drawingStatus = drawingStatus;
		this.lcAmount = lcAmount;
		this.lcCurrency = lcCurrency;
		this.lcIssueDate = lcIssueDate;
		this.lcExpiryDate = lcExpiryDate;
		this.paymentTerms = paymentTerms;
		this.presentorReference = presentorReference;
		this.presentorName = presentorName;
		this.documentsReceived = documentsReceived;
		this.forwardContact = forwardContact;
		this.shippingGuaranteeReference = shippingGuaranteeReference;
		this.approvalDate = approvalDate;
		this.totalDocuments = totalDocuments;
		this.documentName = documentName;
		this.discrepancyDescription = discrepancyDescription;
		this.paymentStatus = paymentStatus;
		this.rejectedDate = rejectedDate;
		this.totalAmountToBePaid = totalAmountToBePaid;
		this.accountToBeDebited = accountToBeDebited;
		this.messageFromBank = messageFromBank;
		this.messageToBank = messageToBank;
		this.totalPaidAmount = totalPaidAmount;
		this.paymentDate = paymentDate;
		this.reasonForRejection = reasonForRejection;
		this.discrepancies = discrepancies;
		this.acceptance = acceptance;
		this.messageType = messageType;
		this.deliveryDestination = deliveryDestination;
		this.messageDate = messageDate;
		this.messageCategory = messageCategory;
		this.flowType = flowType;
		this.status = status;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.lcSrmsReqOrderID = lcSrmsReqOrderID;
		this.drawingsSrmsReqOrderID = drawingsSrmsReqOrderID;
		this.CustomerId = CustomerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lcReferenceNo == null) ? 0 : lcReferenceNo.hashCode());
		result = prime * result + ((lcType == null) ? 0 : lcType.hashCode());
		result = prime * result + ((drawingReferenceNo == null) ? 0 : drawingReferenceNo.hashCode());
		result = prime * result + ((beneficiaryName == null) ? 0 : beneficiaryName.hashCode());
		result = prime * result + ((documentStatus == null) ? 0 : documentStatus.hashCode());
		result = prime * result + ((drawingCreationDate == null) ? 0 : drawingCreationDate.hashCode());
		result = prime * result + ((drawingCurrency == null) ? 0 : drawingCurrency.hashCode());
		result = prime * result + ((drawingAmount == null) ? 0 : drawingAmount.hashCode());
		result = prime * result + ((drawingStatus == null) ? 0 : drawingStatus.hashCode());
		result = prime * result + ((lcAmount == null) ? 0 : lcAmount.hashCode());
		result = prime * result + ((lcCurrency == null) ? 0 : lcCurrency.hashCode());
		result = prime * result + ((lcIssueDate == null) ? 0 : lcIssueDate.hashCode());
		result = prime * result + ((lcExpiryDate == null) ? 0 : lcExpiryDate.hashCode());
		result = prime * result + ((paymentTerms == null) ? 0 : paymentTerms.hashCode());
		result = prime * result + ((presentorReference == null) ? 0 : presentorReference.hashCode());
		result = prime * result + ((presentorName == null) ? 0 : presentorName.hashCode());
		result = prime * result + ((documentsReceived == null) ? 0 : documentsReceived.hashCode());
		result = prime * result + ((forwardContact == null) ? 0 : forwardContact.hashCode());
		result = prime * result + ((shippingGuaranteeReference == null) ? 0 : shippingGuaranteeReference.hashCode());
		result = prime * result + ((approvalDate == null) ? 0 : approvalDate.hashCode());
		result = prime * result + ((totalDocuments == null) ? 0 : totalDocuments.hashCode());
		result = prime * result + ((documentName == null) ? 0 : documentName.hashCode());
		result = prime * result + ((discrepancyDescription == null) ? 0 : discrepancyDescription.hashCode());
		result = prime * result + ((paymentStatus == null) ? 0 : paymentStatus.hashCode());
		result = prime * result + ((rejectedDate == null) ? 0 : rejectedDate.hashCode());
		result = prime * result + ((totalAmountToBePaid == null) ? 0 : totalAmountToBePaid.hashCode());
		result = prime * result + ((accountToBeDebited == null) ? 0 : accountToBeDebited.hashCode());
		result = prime * result + ((messageFromBank == null) ? 0 : messageFromBank.hashCode());
		result = prime * result + ((messageToBank == null) ? 0 : messageToBank.hashCode());
		result = prime * result + ((totalPaidAmount == null) ? 0 : totalPaidAmount.hashCode());
		result = prime * result + ((paymentDate == null) ? 0 : paymentDate.hashCode());
		result = prime * result + ((reasonForRejection == null) ? 0 : reasonForRejection.hashCode());
		result = prime * result + ((discrepancies == null) ? 0 : discrepancies.hashCode());
		result = prime * result + ((acceptance == null) ? 0 : acceptance.hashCode());
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + ((deliveryDestination == null) ? 0 : deliveryDestination.hashCode());
		result = prime * result + ((messageDate == null) ? 0 : messageDate.hashCode());
		result = prime * result + ((messageCategory == null) ? 0 : messageCategory.hashCode());
		result = prime * result + ((flowType == null) ? 0 : flowType.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((errorCode == null) ? 0 : errorCode.hashCode());
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + ((lcSrmsReqOrderID == null) ? 0 : lcSrmsReqOrderID.hashCode());
		result = prime * result + ((drawingsSrmsReqOrderID == null) ? 0 : drawingsSrmsReqOrderID.hashCode());
		result = prime * result + ((CustomerId == null) ? 0 : CustomerId.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DrawingsDTO drawingsObj = (DrawingsDTO) obj;
		if (lcReferenceNo == null) {
			if (drawingsObj.lcReferenceNo != null)
				return false;
		} else if (!lcReferenceNo.equals(drawingsObj.lcReferenceNo))
			return false;
		if (lcType == null) {
			if (drawingsObj.lcType != null)
				return false;
		} else if (!lcType.equals(drawingsObj.lcType))
			return false;
		if (drawingReferenceNo == null) {
			if (drawingsObj.drawingReferenceNo != null)
				return false;
		} else if (!drawingReferenceNo.equals(drawingsObj.drawingReferenceNo))
			return false;
		if (beneficiaryName == null) {
			if (drawingsObj.beneficiaryName != null)
				return false;
		} else if (!beneficiaryName.equals(drawingsObj.beneficiaryName))
			return false;
		if (documentStatus == null) {
			if (drawingsObj.documentStatus != null)
				return false;
		} else if (!documentStatus.equals(drawingsObj.documentStatus))
			return false;
		if (drawingCreationDate == null) {
			if (drawingsObj.drawingCreationDate != null)
				return false;
		} else if (!drawingCreationDate.equals(drawingsObj.drawingCreationDate))
			return false;
		if (drawingCurrency == null) {
			if (drawingsObj.drawingCurrency != null)
				return false;
		} else if (!drawingCurrency.equals(drawingsObj.drawingCurrency))
			return false;
		if (drawingAmount == null) {
			if (drawingsObj.drawingAmount != null)
				return false;
		} else if (!drawingAmount.equals(drawingsObj.drawingAmount))
			return false;
		if (drawingStatus == null) {
			if (drawingsObj.drawingStatus != null)
				return false;
		} else if (!drawingStatus.equals(drawingsObj.drawingStatus))
			return false;
		if (lcAmount == null) {
			if (drawingsObj.lcAmount != null)
				return false;
		} else if (!lcAmount.equals(drawingsObj.lcAmount))
			return false;
		if (lcCurrency == null) {
			if (drawingsObj.lcCurrency != null)
				return false;
		} else if (!lcCurrency.equals(drawingsObj.lcCurrency))
			return false;
		if (lcIssueDate == null) {
			if (drawingsObj.lcIssueDate != null)
				return false;
		} else if (!lcIssueDate.equals(drawingsObj.lcIssueDate))
			return false;
		if (lcExpiryDate == null) {
			if (drawingsObj.lcExpiryDate != null)
				return false;
		} else if (!lcExpiryDate.equals(drawingsObj.lcExpiryDate))
			return false;
		if (paymentTerms == null) {
			if (drawingsObj.paymentTerms != null)
				return false;
		} else if (!paymentTerms.equals(drawingsObj.paymentTerms))
			return false;
		if (presentorReference == null) {
			if (drawingsObj.presentorReference != null)
				return false;
		} else if (!presentorReference.equals(drawingsObj.presentorReference))
			return false;
		if (presentorName == null) {
			if (drawingsObj.presentorName != null)
				return false;
		} else if (!presentorName.equals(drawingsObj.presentorName))
			return false;
		if (documentsReceived == null) {
			if (drawingsObj.documentsReceived != null)
				return false;
		} else if (!documentsReceived.equals(drawingsObj.documentsReceived))
			return false;
		if (forwardContact == null) {
			if (drawingsObj.forwardContact != null)
				return false;
		} else if (!forwardContact.equals(drawingsObj.forwardContact))
			return false;
		if (shippingGuaranteeReference == null) {
			if (drawingsObj.shippingGuaranteeReference != null)
				return false;
		} else if (!shippingGuaranteeReference.equals(drawingsObj.shippingGuaranteeReference))
			return false;
		if (approvalDate == null) {
			if (drawingsObj.approvalDate != null)
				return false;
		} else if (!approvalDate.equals(drawingsObj.approvalDate))
			return false;
		if (totalDocuments == null) {
			if (drawingsObj.totalDocuments != null)
				return false;
		} else if (!totalDocuments.equals(drawingsObj.totalDocuments))
			return false;
		if (documentName == null) {
			if (drawingsObj.documentName != null)
				return false;
		} else if (!documentName.equals(drawingsObj.documentName))
			return false;
		if (discrepancyDescription == null) {
			if (drawingsObj.discrepancyDescription != null)
				return false;
		} else if (!discrepancyDescription.equals(drawingsObj.discrepancyDescription))
			return false;
		if (paymentStatus == null) {
			if (drawingsObj.paymentStatus != null)
				return false;
		} else if (!paymentStatus.equals(drawingsObj.paymentStatus))
			return false;
		if (rejectedDate == null) {
			if (drawingsObj.rejectedDate != null)
				return false;
		} else if (!rejectedDate.equals(drawingsObj.rejectedDate))
			return false;
		if (totalAmountToBePaid == null) {
			if (drawingsObj.totalAmountToBePaid != null)
				return false;
		} else if (!totalAmountToBePaid.equals(drawingsObj.totalAmountToBePaid))
			return false;
		if (accountToBeDebited == null) {
			if (drawingsObj.accountToBeDebited != null)
				return false;
		} else if (!accountToBeDebited.equals(drawingsObj.accountToBeDebited))
			return false;
		if (messageFromBank == null) {
			if (drawingsObj.messageFromBank != null)
				return false;
		} else if (!messageFromBank.equals(drawingsObj.messageFromBank))
			return false;
		if (messageToBank == null) {
			if (drawingsObj.messageToBank != null)
				return false;
		} else if (!messageToBank.equals(drawingsObj.messageToBank))
			return false;
		if (totalPaidAmount == null) {
			if (drawingsObj.totalPaidAmount != null)
				return false;
		} else if (!totalPaidAmount.equals(drawingsObj.totalPaidAmount))
			return false;
		if (paymentDate == null) {
			if (drawingsObj.paymentDate != null)
				return false;
		} else if (!paymentDate.equals(drawingsObj.paymentDate))
			return false;
		if (reasonForRejection == null) {
			if (drawingsObj.reasonForRejection != null)
				return false;
		} else if (!reasonForRejection.equals(drawingsObj.reasonForRejection))
			return false;
		if (discrepancies == null) {
			if (drawingsObj.discrepancies != null)
				return false;
		} else if (!discrepancies.equals(drawingsObj.discrepancies))
			return false;
		if (acceptance == null) {
			if (drawingsObj.acceptance != null)
				return false;
		} else if (!acceptance.equals(drawingsObj.acceptance))
			return false;
		if (messageType == null) {
			if (drawingsObj.messageType != null)
				return false;
		} else if (!messageType.equals(drawingsObj.messageType))
			return false;
		if (deliveryDestination == null) {
			if (drawingsObj.deliveryDestination != null)
				return false;
		} else if (!deliveryDestination.equals(drawingsObj.deliveryDestination))
			return false;
		if (messageDate == null) {
			if (drawingsObj.messageDate != null)
				return false;
		} else if (!messageDate.equals(drawingsObj.messageDate))
			return false;
		if (messageCategory == null) {
			if (drawingsObj.messageCategory != null)
				return false;
		} else if (!messageCategory.equals(drawingsObj.messageCategory))
			return false;
		if (flowType == null) {
			if (drawingsObj.flowType != null)
				return false;
		} else if (!flowType.equals(drawingsObj.flowType))
			return false;
		if (status == null) {
			if (drawingsObj.status != null)
				return false;
		} else if (!status.equals(drawingsObj.status))
			return false;
		if (errorCode == null) {
			if (drawingsObj.errorCode != null)
				return false;
		} else if (!errorCode.equals(drawingsObj.errorCode))
			return false;
		if (errorMessage == null) {
			if (drawingsObj.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(drawingsObj.errorMessage))
			return false;
		if (lcSrmsReqOrderID == null) {
			if (drawingsObj.lcSrmsReqOrderID != null)
				return false;
		} else if (!lcSrmsReqOrderID.equals(drawingsObj.lcSrmsReqOrderID))
			return false;
		if (drawingsSrmsReqOrderID == null) {
			if (drawingsObj.drawingsSrmsReqOrderID != null)
				return false;
		} else if (!drawingsSrmsReqOrderID.equals(drawingsObj.drawingsSrmsReqOrderID))
			return false;
		if (CustomerId == null) {
			if (drawingsObj.CustomerId != null)
				return false;
		} else if (!CustomerId.equals(drawingsObj.CustomerId))
			return false;
		return true;
	}

	public String getCustomerId() {
		return CustomerId;
	}

	public void setCustomerId(String customerId) {
		CustomerId = customerId;
	}
}
