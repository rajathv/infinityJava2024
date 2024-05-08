package com.temenos.dbx.product.commons.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dbp.core.api.DBPDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.temenos.dbx.product.approvalmatrixservices.dto.SignatoryGroupMatrixDTO;
import com.temenos.dbx.product.constants.TransactionStatusEnum;
import org.json.JSONArray;
import org.json.JSONObject;

@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionStatusDTO implements DBPDTO{
	
	private static final long serialVersionUID = 2351059360219657311L;
	
	private List<String> approvalMatrixIds;
	private Map<String, SignatoryGroupMatrixDTO> signatoryGroupMatrices;
	private TransactionStatusEnum status;
	private String requestId;
	private String confirmationNumber;
	private String customerId;
	private String companyId;
	private String accountId;
	private String featureActionID;
	private Double amount;
	private String serviceCharge;
	private String convertedAmount;
	private boolean isBusinessUser;
	private TransactionStatusEnum transactionStatus;
	private String date;
	private String transactionCurrency;
	private Map<String, Double> offsetDetails;
	private String dbpErrCode;
	private String dbpErrMsg;
	private String transactionAmount;

	private LimitsDTO userExhaustedLimits;
	private Map<String, Double> userAccountLevelLimits;
	private JSONArray contractCifMap;
	private JSONObject additionalMetaInfo;

	public Map<String, Double> getUserAccountLevelLimits() {
		return userAccountLevelLimits;
	}

	public void setUserAccountLevelLimits(Map<String, Double> userAccountLevelLimits) {
		this.userAccountLevelLimits = new HashMap<>();
		for(Map.Entry<String, Double> limitEntry: userAccountLevelLimits.entrySet()){
			this.userAccountLevelLimits.put(limitEntry.getKey(), limitEntry.getValue());
		}
	}

	public LimitsDTO getUserExhaustedLimits() {
		return userExhaustedLimits;
	}

	public void setUserExhaustedLimits(LimitsDTO userExhaustedLimits) {
		this.userExhaustedLimits = new LimitsDTO();
		this.userExhaustedLimits.setMaxTransactionLimit(userExhaustedLimits.getMaxTransactionLimit());
		this.userExhaustedLimits.setDailyLimit(userExhaustedLimits.getDailyLimit());
		this.userExhaustedLimits.setWeeklyLimit(userExhaustedLimits.getWeeklyLimit());
	}

	/**
     * Start: Added as part of ADP-2810
     */
	private boolean isSelfApproved; 

	public boolean isSelfApproved() {
		return isSelfApproved;
	}
	public void setSelfApproved(boolean isSelfApproved) {
		this.isSelfApproved = isSelfApproved;
	}
	/**
     * End: Added as part of ADP-2810
     */
	public List<String> getApprovalMatrixIds() {
		return approvalMatrixIds;
	}
	public String getTransactionAmount() {
		return transactionAmount;
	}
	public void setTransactionAmount(String transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
	public void setApprovalMatrixIds(List<String> approvalMatrixIds) {
		this.approvalMatrixIds = approvalMatrixIds;
	}
	public TransactionStatusEnum getStatus() {
		return status;
	}
	public void setStatus(TransactionStatusEnum status) {
		this.status = status;
	}
	public Map<String, SignatoryGroupMatrixDTO> getSignatoryGroupMatrices() {
		return signatoryGroupMatrices;
	}
	public void setSignatoryGroupMatrices(Map<String, SignatoryGroupMatrixDTO> signatoryGroupMatrices) {
		this.signatoryGroupMatrices = signatoryGroupMatrices;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getFeatureActionID() {
		return featureActionID;
	}
	public void setFeatureActionID(String featureActionID) {
		this.featureActionID = featureActionID;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getServiceCharge() {
		return serviceCharge;
	}
	public void setServiceCharge(String serviceCharge) {
		this.serviceCharge = serviceCharge;
	}
	public String getConvertedAmount() {
		return convertedAmount;
	}
	public void setConvertedAmount(String convertedAmount) {
		this.convertedAmount = convertedAmount;
	}
	public boolean isBusinessUser() {
		return isBusinessUser;
	}
	public void setBusinessUser(boolean isBusinessUser) {
		this.isBusinessUser = isBusinessUser;
	}
	public TransactionStatusEnum getTransactionStatus() {
		return transactionStatus;
	}
	public void setTransactionStatus(TransactionStatusEnum transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Map<String, Double> getOffsetDetails() {
		return offsetDetails;
	}
	public void setOffsetDetails(Map<String, Double> offsetDetails) {
		this.offsetDetails = offsetDetails;
	}
	public String getDbpErrCode() {
		return dbpErrCode;
	}
	public void setDbpErrCode(String dbpErrCode) {
		this.dbpErrCode = dbpErrCode;
	}
	public String getDbpErrMsg() {
		return dbpErrMsg;
	}
	public void setDbpErrMsg(String dbpErrMsg) {
		this.dbpErrMsg = dbpErrMsg;
	}
	public String getTransactionCurrency() {
		return transactionCurrency;
	}
	public void setTransactionCurrency(String transactionCurrency) {
		this.transactionCurrency = transactionCurrency;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getConfirmationNumber() {
		return confirmationNumber;
	}
	public void setConfirmationNumber(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((approvalMatrixIds == null) ? 0 : approvalMatrixIds.hashCode());
		result = prime * result + ((companyId == null) ? 0 : companyId.hashCode());
		result = prime * result + ((convertedAmount == null) ? 0 : convertedAmount.hashCode());
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((dbpErrCode == null) ? 0 : dbpErrCode.hashCode());
		result = prime * result + ((dbpErrMsg == null) ? 0 : dbpErrMsg.hashCode());
		result = prime * result + ((featureActionID == null) ? 0 : featureActionID.hashCode());
		result = prime * result + (isBusinessUser ? 1231 : 1237);
		result = prime * result + ((offsetDetails == null) ? 0 : offsetDetails.hashCode());
		result = prime * result + ((serviceCharge == null) ? 0 : serviceCharge.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((transactionStatus == null) ? 0 : transactionStatus.hashCode());
		result = prime * result + ((transactionCurrency == null) ? 0 : transactionCurrency.hashCode());
		result = prime * result + (isSelfApproved ? 1231 : 1237); ////Added as part of  ADP-2810
		result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
		result = prime * result + ((confirmationNumber == null) ? 0 : confirmationNumber.hashCode());
		result = prime * result + ((transactionAmount == null) ? 0 : transactionAmount.hashCode());
		result = prime * result + ((signatoryGroupMatrices == null) ? 0 : signatoryGroupMatrices.hashCode());
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
		TransactionStatusDTO other = (TransactionStatusDTO) obj;
		if (accountId == null) {
			if (other.accountId != null)
				return false;
		} else if (!accountId.equals(other.accountId))
			return false;
		if (amount == null) {
			if (other.amount != null)
				return false;
		} else if (!amount.equals(other.amount))
			return false;
		if (approvalMatrixIds == null) {
			if (other.approvalMatrixIds != null)
				return false;
		} else if (!approvalMatrixIds.equals(other.approvalMatrixIds))
			return false;
		if (companyId == null) {
			if (other.companyId != null)
				return false;
		} else if (!companyId.equals(other.companyId))
			return false;
		if (convertedAmount == null) {
			if (other.convertedAmount != null)
				return false;
		} else if (!convertedAmount.equals(other.convertedAmount))
			return false;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
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
		if (featureActionID == null) {
			if (other.featureActionID != null)
				return false;
		} else if (!featureActionID.equals(other.featureActionID))
			return false;
		if (isBusinessUser != other.isBusinessUser)
			return false;
		if (offsetDetails == null) {
			if (other.offsetDetails != null)
				return false;
		} else if (!offsetDetails.equals(other.offsetDetails))
			return false;
		if (serviceCharge == null) {
			if (other.serviceCharge != null)
				return false;
		} else if (!serviceCharge.equals(other.serviceCharge))
			return false;
		if (status != other.status)
			return false;
		if (transactionStatus != other.transactionStatus)
			return false;
		if (isSelfApproved != other.isSelfApproved) //Added as part of  ADP-2810
			return false;
		if (requestId == null) {
			if (other.requestId != null)
				return false;
		} else if (!requestId.equals(other.requestId))
			return false;
		if (confirmationNumber == null) {
			if (other.confirmationNumber != null)
				return false;
		} else if (!confirmationNumber.equals(other.confirmationNumber))
			return false;
		if (transactionAmount == null) {
			if (other.transactionAmount != null)
				return false;
		} else if (!transactionAmount.equals(other.transactionAmount))
			return false;
		if (signatoryGroupMatrices == null) {
			if (other.signatoryGroupMatrices != null)
				return false;
		} else if (!signatoryGroupMatrices.equals(other.signatoryGroupMatrices))
			return false;
		return true;
	}
	
	public TransactionStatusDTO() {
		approvalMatrixIds = new ArrayList<>();
		signatoryGroupMatrices = new HashMap<>();
	}
	
	public TransactionStatusDTO(List<String> approvalMatrixIds, TransactionStatusEnum status, String customerId,
			String companyId, String accountId, String featureActionID, Double amount, String serviceCharge, String convertedAmount, boolean isBusinessUser,
			TransactionStatusEnum transactionStatus, String date, Map<String, Double> offsetDetails, String dbpErrCode,
			String dbpErrMsg, boolean isSelfApproved, String transactionCurrency, String confirmationNumber, String requestId, String transactionAmount, Map<String, SignatoryGroupMatrixDTO> signatoryGroupMatrices) {
		super();
		this.approvalMatrixIds = approvalMatrixIds;
		this.status = status;
		this.customerId = customerId;
		this.companyId = companyId;
		this.accountId = accountId;
		this.featureActionID = featureActionID;
		this.amount = amount;
		this.isBusinessUser = isBusinessUser;
		this.transactionStatus = transactionStatus;
		this.date = date;
		this.offsetDetails = offsetDetails;
		this.dbpErrCode = dbpErrCode;
		this.dbpErrMsg = dbpErrMsg;
		this.isSelfApproved = isSelfApproved;
		this.serviceCharge = serviceCharge;
		this.convertedAmount = convertedAmount;
		this.transactionCurrency = transactionCurrency;
		this.requestId=requestId;
		this.confirmationNumber=confirmationNumber;
		this.transactionAmount = transactionAmount;
		this.signatoryGroupMatrices=signatoryGroupMatrices;
	}

	
	public void setContractCifMap( JSONArray contractCifMap ) {
		this.contractCifMap = contractCifMap;
	}

	public JSONArray getContractCifMap( ) {
		return contractCifMap;
	}
	
	public void setAdditionalMetaData( JSONObject additionalMetaDataForBeneficiary ) {
		this.additionalMetaInfo = additionalMetaDataForBeneficiary;
	}

	public JSONObject getAdditionalMetaInfo( ) {
		return additionalMetaInfo;
	}
}