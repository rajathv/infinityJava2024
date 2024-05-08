package com.temenos.dbx.product.contract.backenddelegate.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dbp.core.api.BackendDelegate;
import com.google.gson.JsonArray;
import com.kony.dbp.exception.ApplicationException;
import com.temenos.dbx.product.dto.ContractAccountsDTO;
import com.temenos.dbx.product.dto.DBXResult;

public interface ContractAccountBackendDelegate extends BackendDelegate {

    public ContractAccountsDTO createContractAccount(ContractAccountsDTO inputAccountDTO,
            Map<String, Object> headersMap) throws ApplicationException;

    public List<ContractAccountsDTO> getContractCustomerAccounts(String contractId, String customerId,
            Map<String, Object> headersMap) throws ApplicationException;

    /**
     * 
     * @param jsonarray
     * @param headersMap
     * @return isAssociated flag is mapped to input jsonarray if account is part of any of the contract
     * @throws ApplicationException
     */
    public DBXResult validateAccountsContractAssociation(JsonArray jsonarray, Map<String, Object> headersMap)
            throws ApplicationException;

    public boolean checkIfGivenAccountsAreValid(Set<String> accountsList, Map<String, Object> headersMap)
            throws ApplicationException;

}
