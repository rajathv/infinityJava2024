package com.temenos.dbx.product.payeeservices.dto;

import com.dbp.core.api.DBPDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WireTransfersPayeeDTO implements DBPDTO{
	
	private static final long serialVersionUID = 2112698981835901245L;

	private String id;
	private String typeId;
	private String payeeId;
	private String createdBy;
	private String contractId;
	private String cif;
	@JsonIgnore
	private String isBusinessPayee;
	private String noOfCustomersLinked;
	
	public WireTransfersPayeeDTO() {
		super();
	}

	public WireTransfersPayeeDTO(String id, String typeId, String payeeId, String createdBy, String contractId, String cif,
			String isBusinessPayee, String noOfCustomersLinked) {
		super();
		this.id = id;
		this.typeId = typeId;
		this.payeeId = payeeId;
		this.createdBy = createdBy;
		this.contractId = contractId;
		this.cif = cif;
		this.isBusinessPayee = isBusinessPayee;
		this.noOfCustomersLinked = noOfCustomersLinked;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getPayeeId() {
		return payeeId;
	}

	public void setPayeeId(String payeeId) {
		this.payeeId = payeeId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getCif() {
		return cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	public String getIsBusinessPayee() {
		return isBusinessPayee;
	}

	public void setIsBusinessPayee(String isBusinessPayee) {
		this.isBusinessPayee = isBusinessPayee;
	}

	public String getNoOfCustomersLinked() {
		return noOfCustomersLinked;
	}

	public void setNoOfCustomersLinked(String noOfCustomersLinked) {
		this.noOfCustomersLinked = noOfCustomersLinked;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cif == null) ? 0 : cif.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((contractId == null) ? 0 : contractId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((isBusinessPayee == null) ? 0 : isBusinessPayee.hashCode());
		result = prime * result + ((payeeId == null) ? 0 : payeeId.hashCode());
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
		result = prime * result + ((noOfCustomersLinked == null) ? 0 : noOfCustomersLinked.hashCode());
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
		WireTransfersPayeeDTO other = (WireTransfersPayeeDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
