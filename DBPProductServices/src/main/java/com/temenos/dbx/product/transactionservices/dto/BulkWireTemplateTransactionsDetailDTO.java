package com.temenos.dbx.product.transactionservices.dto;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.DBPDTO;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.transactionservices.resource.impl.BulkWireTransactionsResourceImpl;
import com.kony.dbputilities.util.HelperMethods;

public class BulkWireTemplateTransactionsDetailDTO implements DBPDTO{

	private static final long serialVersionUID = 3952427625754296362L;

	private static final Logger LOG = LogManager.getLogger(BulkWireTransactionsResourceImpl.class);

	private int bulkWireTransactionID;
	private String bulkWireTemplateID;
	private String initiatedBy;
	private String createdts;
	private String lastmodifiedts; 
	private String transactionDate;
	private int totalCountOfTransactions;
	private int totalCountOfDomesticTransactions;
	private int totalCountOfInternationalTransactions;
	private String createdby;
	private String modifiedby;
	private String synctimestamp;
	private String firstname;
	private String lastname;
	
	public BulkWireTemplateTransactionsDetailDTO() {
		super();
	}
	
	public BulkWireTemplateTransactionsDetailDTO(int bulkWireTransactionID, String bulkWireTemplateID,
			String initiatedBy, String createdts, String lastmodifiedts, String transactionDate,
			int totalCountOfTransactions, int totalCountOfDomesticTransactions,
			int totalCountOfInternationalTransactions, String createdby, String modifiedby, String synctimestamp,
			String firstname, String lastname) {
		super();
		this.bulkWireTransactionID = bulkWireTransactionID;
		this.bulkWireTemplateID = bulkWireTemplateID;
		this.initiatedBy = initiatedBy;
		this.createdts = createdts;
		this.lastmodifiedts = lastmodifiedts;
		this.transactionDate = transactionDate;
		this.totalCountOfTransactions = totalCountOfTransactions;
		this.totalCountOfDomesticTransactions = totalCountOfDomesticTransactions;
		this.totalCountOfInternationalTransactions = totalCountOfInternationalTransactions;
		this.createdby = createdby;
		this.modifiedby = modifiedby;
		this.synctimestamp = synctimestamp;
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	public int getBulkWireTransactionID() {
		return bulkWireTransactionID;
	}

	public void setBulkWireTransactionID(int bulkWireTransactionID) {
		this.bulkWireTransactionID = bulkWireTransactionID;
	}

	public String getBulkWireTemplateID() {
		return bulkWireTemplateID;
	}

	public void setBulkWireTemplateID(String bulkWireTemplateID) {
		this.bulkWireTemplateID = bulkWireTemplateID;
	}

	public String getInitiatedBy() {
		return initiatedBy;
	}

	public void setInitiatedBy(String initiatedBy) {
		this.initiatedBy = initiatedBy;
	}

	public String getCreatedts() {
		return createdts;
	}

	public void setCreatedts(String createdts) {
		try {
			if (StringUtils.isNotBlank(createdts))
				this.createdts = HelperMethods.convertDateFormat(createdts, Constants.TIMESTAMP_FORMAT);
			else {
				LOG.error("createdts : Is Empty");
				this.createdts = createdts;
			}
		} catch (Exception e) {
			LOG.error("Error while converting timeStamp to required format : " + e);
			this.createdts = createdts;
		}
	}

	public String getLastmodifiedts() {
		return lastmodifiedts;
	}

	public void setLastmodifiedts(String lastmodifiedts) {
		try {
			if (StringUtils.isNotBlank(lastmodifiedts))
				this.lastmodifiedts = HelperMethods.convertDateFormat(lastmodifiedts, Constants.TIMESTAMP_FORMAT);
			else {
				LOG.error("lastmodifiedts : Is Empty");
				this.lastmodifiedts = lastmodifiedts;
			}
		} catch (Exception e) {
			LOG.error("Error while converting timeStamp to required format : " + e);
			this.lastmodifiedts = lastmodifiedts;
		}
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		try {
			if (StringUtils.isNotBlank(transactionDate))
				this.transactionDate = HelperMethods.convertDateFormat(transactionDate, Constants.TIMESTAMP_FORMAT);
			else {
				LOG.error("createdts : Is Empty");
				this.transactionDate = transactionDate;
			}
		} catch (Exception e) {
			LOG.error("Error while converting timeStamp to required format : " + e);
			this.transactionDate = transactionDate;
		}
	}

	public int getTotalCountOfTransactions() {
		return totalCountOfTransactions;
	}

	public void setTotalCountOfTransactions(int totalCountOfTransactions) {
		this.totalCountOfTransactions = totalCountOfTransactions;
	}

	public int getTotalCountOfDomesticTransactions() {
		return totalCountOfDomesticTransactions;
	}

	public void setTotalCountOfDomesticTransactions(int totalCountOfDomesticTransactions) {
		this.totalCountOfDomesticTransactions = totalCountOfDomesticTransactions;
	}

	public int getTotalCountOfInternationalTransactions() {
		return totalCountOfInternationalTransactions;
	}

	public void setTotalCountOfInternationalTransactions(int totalCountOfInternationalTransactions) {
		this.totalCountOfInternationalTransactions = totalCountOfInternationalTransactions;
	}

	public String getCreatedby() {
		return createdby;
	}

	public void setCreatedby(String createdby) {
		this.createdby = createdby;
	}

	public String getModifiedby() {
		return modifiedby;
	}

	public void setModifiedby(String modifiedby) {
		this.modifiedby = modifiedby;
	}

	public String getSynctimestamp() {
		return synctimestamp;
	}

	public void setSynctimestamp(String synctimestamp) {
		this.synctimestamp = synctimestamp;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bulkWireTemplateID == null) ? 0 : bulkWireTemplateID.hashCode());
		result = prime * result + bulkWireTransactionID;
		result = prime * result + ((createdby == null) ? 0 : createdby.hashCode());
		result = prime * result + ((createdts == null) ? 0 : createdts.hashCode());
		result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
		result = prime * result + ((initiatedBy == null) ? 0 : initiatedBy.hashCode());
		result = prime * result + ((lastmodifiedts == null) ? 0 : lastmodifiedts.hashCode());
		result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
		result = prime * result + ((modifiedby == null) ? 0 : modifiedby.hashCode());
		result = prime * result + ((synctimestamp == null) ? 0 : synctimestamp.hashCode());
		result = prime * result + totalCountOfDomesticTransactions;
		result = prime * result + totalCountOfInternationalTransactions;
		result = prime * result + totalCountOfTransactions;
		result = prime * result + ((transactionDate == null) ? 0 : transactionDate.hashCode());
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
		BulkWireTemplateTransactionsDetailDTO other = (BulkWireTemplateTransactionsDetailDTO) obj;
		if (bulkWireTemplateID == null) {
			if (other.bulkWireTemplateID != null)
				return false;
		} else if (!bulkWireTemplateID.equals(other.bulkWireTemplateID))
			return false;
		if (bulkWireTransactionID != other.bulkWireTransactionID)
			return false;
		if (createdby == null) {
			if (other.createdby != null)
				return false;
		} else if (!createdby.equals(other.createdby))
			return false;
		if (createdts == null) {
			if (other.createdts != null)
				return false;
		} else if (!createdts.equals(other.createdts))
			return false;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (initiatedBy == null) {
			if (other.initiatedBy != null)
				return false;
		} else if (!initiatedBy.equals(other.initiatedBy))
			return false;
		if (lastmodifiedts == null) {
			if (other.lastmodifiedts != null)
				return false;
		} else if (!lastmodifiedts.equals(other.lastmodifiedts))
			return false;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		if (modifiedby == null) {
			if (other.modifiedby != null)
				return false;
		} else if (!modifiedby.equals(other.modifiedby))
			return false;
		if (synctimestamp == null) {
			if (other.synctimestamp != null)
				return false;
		} else if (!synctimestamp.equals(other.synctimestamp))
			return false;
		if (totalCountOfDomesticTransactions != other.totalCountOfDomesticTransactions)
			return false;
		if (totalCountOfInternationalTransactions != other.totalCountOfInternationalTransactions)
			return false;
		if (totalCountOfTransactions != other.totalCountOfTransactions)
			return false;
		if (transactionDate == null) {
			if (other.transactionDate != null)
				return false;
		} else if (!transactionDate.equals(other.transactionDate))
			return false;
		return true;
	}

}
