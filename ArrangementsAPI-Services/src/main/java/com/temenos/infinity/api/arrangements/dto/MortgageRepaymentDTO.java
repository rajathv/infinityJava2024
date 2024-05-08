package com.temenos.infinity.api.arrangements.dto;

import java.io.Serializable;

import com.dbp.core.api.DBPDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class MortgageRepaymentDTO implements Serializable, DBPDTO {

	private static final long serialVersionUID = 653792502420487768L;

	private String facilityName;
	private String arrangementId;
	private String customerName;
	private String numOfLoans;
	private String currentOutstandingBalanceCurrency;
	private String currentOutstandingBalanceAmount;
	private String currentMaturityDate;
	private String supportingDocumentIds;
	private String loanName;
	private String loanAccountNumber;
	private String code;
	private String message;
	private String status;
	private String Id;
	private String errorMessage;
	private String loansDetails;
	private String accountId;
	private String requestDetails;
	private String customerId;
	

	/**
	 * Constructor using super class
	 */
	public MortgageRepaymentDTO() {
		super();
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getArrangementId() {
		return arrangementId;
	}

	public void setArrangementId(String arrangementId) {
		this.arrangementId = arrangementId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getNumOfLoans() {
		return numOfLoans;
	}

	public void setNumOfLoans(String numOfLoans) {
		this.numOfLoans = numOfLoans;
	}

	public String getCurrentOutstandingBalanceCurrency() {
		return currentOutstandingBalanceCurrency;
	}

	public void setCurrentOutstandingBalanceCurrency(String currentOutstandingBalanceCurrency) {
		this.currentOutstandingBalanceCurrency = currentOutstandingBalanceCurrency;
	}

	public String getCurrentOutstandingBalanceAmount() {
		return currentOutstandingBalanceAmount;
	}

	public void setCurrentOutstandingBalanceAmount(String currentOutstandingBalanceAmount) {
		this.currentOutstandingBalanceAmount = currentOutstandingBalanceAmount;
	}

	public String getCurrentMaturityDate() {
		return currentMaturityDate;
	}

	public void setCurrentMaturityDate(String currentMaturityDate) {
		this.currentMaturityDate = currentMaturityDate;
	}

	public String getSupportingDocumentIds() {
		return supportingDocumentIds;
	}

	public void setSupportingDocumentIds(String supportingDocumentIds) {
		this.supportingDocumentIds = supportingDocumentIds;
	}

	public String getLoanName() {
		return loanName;
	}

	public void setLoanName(String loanName) {
		this.loanName = loanName;
	}

	public String getLoanAccountNumber() {
		return loanAccountNumber;
	}

	public void setLoanAccountNumber(String loanAccountNumber) {
		this.loanAccountNumber = loanAccountNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLoansDetails() {
		return loansDetails;
	}

	public void setLoansDetails(String loansDetails) {
		this.loansDetails = loansDetails;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getRequestDetails() {
		return requestDetails;
	}

	public void setRequestDetails(String requestDetails) {
		this.requestDetails = requestDetails;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((facilityName == null) ? 0 : facilityName.hashCode());
		result = prime * result + ((arrangementId == null) ? 0 : arrangementId.hashCode());
		result = prime * result + ((customerName == null) ? 0 : customerName.hashCode());
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((numOfLoans == null) ? 0 : numOfLoans.hashCode());
		result = prime * result + ((currentOutstandingBalanceCurrency == null) ? 0 : currentOutstandingBalanceCurrency.hashCode());
		result = prime * result + ((currentOutstandingBalanceAmount == null) ? 0 : currentOutstandingBalanceAmount.hashCode());
		result = prime * result + ((currentMaturityDate == null) ? 0 : currentMaturityDate.hashCode());
		result = prime * result + ((supportingDocumentIds == null) ? 0 : supportingDocumentIds.hashCode());
		result = prime * result + ((loanName == null) ? 0 : loanName.hashCode());
		result = prime * result + ((loanAccountNumber == null) ? 0 : loanAccountNumber.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
		result = prime * result + ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((loansDetails == null) ? 0 : loansDetails.hashCode());
		result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
		result = prime * result + ((requestDetails == null) ? 0 : requestDetails.hashCode());
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
		MortgageRepaymentDTO other = (MortgageRepaymentDTO) obj;
		if (facilityName == null) {
			if (other.facilityName != null)
				return false;
		} else if (!facilityName.equals(other.facilityName))
			return false;
		if (arrangementId == null) {
			if (other.arrangementId != null)
				return false;
		} else if (!arrangementId.equals(other.arrangementId))
			return false;
		if (customerName == null) {
			if (other.customerName != null)
				return false;
		} else if (!customerName.equals(other.customerName))
			return false;
		if (numOfLoans == null) {
			if (other.numOfLoans != null)
				return false;
		} else if (!numOfLoans.equals(other.numOfLoans))
			return false;
		if (currentOutstandingBalanceCurrency == null) {
			if (other.currentOutstandingBalanceCurrency != null)
				return false;
		} else if (!currentOutstandingBalanceCurrency.equals(other.currentOutstandingBalanceCurrency))
			return false;
		if (currentOutstandingBalanceAmount == null) {
			if (other.currentOutstandingBalanceAmount != null)
				return false;
		} else if (!currentOutstandingBalanceAmount.equals(other.currentOutstandingBalanceAmount))
			return false;
		if (currentMaturityDate == null) {
			if (other.currentMaturityDate != null)
				return false;
		} else if (!currentMaturityDate.equals(other.currentMaturityDate))
			return false;
		if (supportingDocumentIds == null) {
			if (other.supportingDocumentIds != null)
				return false;
		} else if (!supportingDocumentIds.equals(other.supportingDocumentIds))
			return false;
		if (loanName == null) {
			if (other.loanName != null)
				return false;
		} else if (!loanName.equals(other.loanName))
			return false;
		if (loanAccountNumber == null) {
			if (other.loanAccountNumber != null)
				return false;
		} else if (!loanAccountNumber.equals(other.loanAccountNumber))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (loansDetails == null) {
			if (other.loansDetails != null)
				return false;
		} else if (!loansDetails.equals(other.loansDetails))
			return false;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		if (requestDetails == null) {
			if (other.requestDetails != null)
				return false;
		} else if (!requestDetails.equals(other.requestDetails))
			return false;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		
		return true;
	}

	
	public MortgageRepaymentDTO(String facilityName, String arrangementId, String customerName, String numOfLoans,
			String currentOutstandingBalanceCurrency, String currentOutstandingBalanceAmount, String currentMaturityDate, String supportingDocumentIds, String loanName, String loanAccountNumber
			,String message, String status, String id, String errorMessage, String code, String loansDetails, String accountId, String requestDetails, String customerId) {
		super();
		this.facilityName = facilityName;
		this.arrangementId = arrangementId;
		this.customerName = customerName;
		this.numOfLoans = numOfLoans;
		this.currentOutstandingBalanceCurrency = currentOutstandingBalanceCurrency;
		this.currentOutstandingBalanceAmount = currentOutstandingBalanceAmount;
		this.currentMaturityDate = currentMaturityDate;
		this.supportingDocumentIds = supportingDocumentIds;
		this.loanName = loanName;
		this.loanAccountNumber=loanAccountNumber;
		this.message = message;
		this.status = status;
	    Id = id;
		this.errorMessage = errorMessage;
		this.code = code;
		this.loansDetails = loansDetails;
		this.accountId =  accountId;
		this.requestDetails = requestDetails;
		this.customerId = customerId;
	}
}