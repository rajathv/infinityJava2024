package com.temenos.dbx.product.commons.businessdelegate.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.temenos.dbx.product.commons.businessdelegate.api.AccountBusinessDelegate;
import com.temenos.dbx.product.commons.businessdelegate.api.ContractBusinessDelegate;
import com.temenos.dbx.product.commons.dto.ContractCoreCustomersDTO;
import com.temenos.dbx.product.commons.dto.CustomerAccountsDTO;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

public class AccountBusinessDelegateImpl implements AccountBusinessDelegate {

	private static final Logger LOG = LogManager.getLogger(AccountBusinessDelegateImpl.class);
	ContractBusinessDelegate contractDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ContractBusinessDelegate.class);

	@Override
	public boolean isBusinessAccount(String accountID) {
		return true;
	}
	
	@Override
	public List<String> getUserBusinessAccounts(String customerID) {

		List<String> accounts = new ArrayList<String>();
	    Map<String, Object> inputParams = new HashMap<String, Object>();
		inputParams.put(DBPUtilitiesConstants.FILTER, "Customer_id" + DBPUtilitiesConstants.EQUAL + customerID +
				DBPUtilitiesConstants.AND + "IsOrganizationAccount" + DBPUtilitiesConstants.EQUAL + "1");
		
		try {
			String response = DBPServiceExecutorBuilder.builder().
					withServiceId(ServiceId.DBPRBLOCALSERVICEDB).
					withObjectId(null).
					withOperationId(OperationName.DB_CUSTOMERACCOUNTS_GET).
					withRequestParameters(inputParams).
					build().getResponse();
			
			JSONObject responseJSON = new JSONObject(response);
			JSONArray responsepArray = responseJSON.getJSONArray("customeraccounts");
			
			responsepArray.forEach(resObject -> accounts.add(((JSONObject) resObject).optString("Account_id")));
			
		}
		catch (Exception e) {
			LOG.error("Exception caught while fetching user role", e);
		}
		return accounts;
	}

	@Override
	public CustomerAccountsDTO getAccountDetails(String customerId, String accountId) {
		
		try {
			List<CustomerAccountsDTO> accounts = getAccountDetails(customerId, new HashSet<String>(Arrays.asList(accountId)));
			
			if (CollectionUtils.isNotEmpty(accounts)) {
				accounts.get(0).setOrganizationId(accounts.get(0).getContractId() + "_" + accounts.get(0).getCoreCustomerId());
				return accounts.get(0);
    		}
		}
		catch (Exception e) {
			LOG.error("Exception caught while fetching user role", e);
		}
		
		return null;
	}
	
	@Override
	public List<String> fetchCifAccounts(String cif, String contractId) {
		
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_CONTRACTACCOUNTS_GET;
		
		HashMap<String, Object> requestParameters = new HashMap<>();
		String filter = "coreCustomerId" + DBPUtilitiesConstants.EQUAL + cif +
				DBPUtilitiesConstants.AND + "contractId" + DBPUtilitiesConstants.EQUAL + contractId;
				
		requestParameters.put(DBPUtilitiesConstants.FILTER, filter);

		String response = null;
		JSONArray records = null;
		try {
			response = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();			
		}
		catch (Exception e) {
			LOG.error("Error while invoking DBService" , e);
			return null;
		}
		
		if (response.length()==0 || response==null)
		{
			LOG.error("DB Service returned no records");
			return null;
		}

		try {
			JSONObject approvalMatrixJSON = new JSONObject(response);
			records = CommonUtils.getFirstOccuringArray(approvalMatrixJSON);
		}
		catch (JSONException e) {
			LOG.error("Error while parsing string JSON" , e);
			return null;
		} 
		
		if (records.length()==0)
		{
			LOG.error("DB Service returned no records");
			return null;
		}

		try {
			List<String> accountIds = new ArrayList<String>();
			for(Object obj: records) {
				JSONObject record = (JSONObject) obj;
				if(record.has("accountId")) {
					accountIds.add(record.getString("accountId"));
				}
			}			
			return accountIds;
		} 
		catch (JSONException e) {
			LOG.error("Caught exception while parsing list: " , e);
			return null;
		}
	}
	
	@Override
	public List<CustomerAccountsDTO> getAccountDetails(String customerId, Set<String> accounts) {

		List<CustomerAccountsDTO> customerAccountsDTOs = new ArrayList<CustomerAccountsDTO>();
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		
		String filter = "Customer_id" + DBPUtilitiesConstants.EQUAL + customerId + DBPUtilitiesConstants.AND;
		
		if(CollectionUtils.isNotEmpty(accounts)) {
			filter = filter + DBPUtilitiesConstants.OPEN_BRACE + "Account_id" + DBPUtilitiesConstants.EQUAL + 
					String.join(DBPUtilitiesConstants.OR + "Account_id" + DBPUtilitiesConstants.EQUAL, accounts)
			+ DBPUtilitiesConstants.CLOSE_BRACE;
		}
		requestParameters.put(DBPUtilitiesConstants.FILTER, filter);
		
		try {
			String response = DBPServiceExecutorBuilder.builder().
					withServiceId(ServiceId.DBPRBLOCALSERVICEDB).
					withObjectId(null).
					withOperationId(OperationName.DB_CUSTOMERACCOUNTS_GET).
					withRequestParameters(requestParameters).
					build().getResponse();
			
			JSONObject res = new JSONObject(response);
			JSONArray actions = CommonUtils.getFirstOccuringArray(res);
			customerAccountsDTOs = JSONUtils.parseAsList(actions.toString(), CustomerAccountsDTO.class);
		}
		catch (Exception e) {
			LOG.error("Exception caught while fetching application properties", e);
		}
		
		List<ContractCoreCustomersDTO> contractCoreCustomers;
		contractCoreCustomers = contractDelegate.fetchContractCustomers();
		customerAccountsDTOs = (new FilterDTO()).merge(customerAccountsDTOs, contractCoreCustomers, "coreCustomerId=coreCustomerId", "");
		return customerAccountsDTOs;
	}

	@Override
	public CustomerAccountsDTO getCommonContractCustomer(String customerId, Set<String> accounts) {
		
		String contractId = null;
		String coreCustomerId = null;
		CustomerAccountsDTO accountDTO = new CustomerAccountsDTO();
		
		List<CustomerAccountsDTO> customerAccountsDTOs = getAccountDetails(customerId, accounts);
		
		for(CustomerAccountsDTO account: customerAccountsDTOs) {
			if(StringUtils.isEmpty(contractId)) {
				contractId = account.getContractId();
			}
			else if(! contractId.equalsIgnoreCase(account.getContractId())) {
				return null;
			}
			
			if(StringUtils.isEmpty(coreCustomerId)) {
				coreCustomerId = account.getCoreCustomerId();
			}
			else if(! coreCustomerId.equalsIgnoreCase(account.getCoreCustomerId())) {
				return null;
			}
		}
		
		accountDTO.setContractId(contractId);
		accountDTO.setCoreCustomerId(coreCustomerId);
		accountDTO.setOrganizationId(contractId + "_" + coreCustomerId);
		
		return accountDTO;
	}
}

