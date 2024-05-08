package com.temenos.dbx.eum.product.limitsandpermissions.businessdelegate.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.BackendDelegate;
import com.dbp.core.api.factory.BackendDelegateFactory;
import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.google.gson.JsonObject;
import com.temenos.dbx.eum.product.constants.OperationName;
import com.temenos.dbx.eum.product.constants.ServiceId;
import com.temenos.dbx.eum.product.limitsandpermissions.backenddelegate.api.LimitsAndPermissionsBackendDelegate;
import com.temenos.dbx.eum.product.limitsandpermissions.businessdelegate.api.LimitsAndPermissionsBusinessDelegate;
import com.temenos.dbx.eum.product.limitsandpermissions.dto.ActionLimitsDTO;
import com.temenos.dbx.eum.product.limitsandpermissions.dto.ContractDTO;
import com.temenos.dbx.eum.product.limitsandpermissions.dto.CustomerGroupDTO;
import com.temenos.dbx.product.utils.ThreadExecutor;

public class LimitsAndPermissionsBusinessDelegateImpl implements LimitsAndPermissionsBusinessDelegate{

	private static final Logger LOG = LogManager.getLogger(LimitsAndPermissionsBusinessDelegateImpl.class);
	
	@Override
	public List<ContractDTO> fetchContracts(String serviceDefinitionId) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_CONTRACT_GET;
       
        List<ContractDTO> contractDTOs = null;
		String contractResponse = null;
		
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		String filter = "servicedefinitionId eq "+ serviceDefinitionId;
		requestParameters.put("$filter", filter);
		
		try {
			contractResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			JSONObject responseObj = new JSONObject(contractResponse);
			JSONArray jsonArray = responseObj.optJSONArray("contract");
			contractDTOs = JSONUtils.parseAsList(jsonArray.toString(), ContractDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch contracts from table: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at fetchContracts: " + e);
			return null;
		}
		
		return contractDTOs;
	}

	@Override
	public boolean editServiceDefinitionLimits(ActionLimitsDTO actionsDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_SERVICEDEFINITION_ACTIONLIMITS_UPDATE;

		Map<String, Object> requestParameters =  new HashMap<String, Object>();
		
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(actionsDTO).toString(), String.class, Object.class);
			requestParameters.put("_action",actionsDTO.getActionId());
			requestParameters.put("_maxTxLimit",actionsDTO.getMaxTransactionLimitValue());
			requestParameters.put("_dailyLimit",actionsDTO.getDailyLimitValue());
			requestParameters.put("_weeklyLimit",actionsDTO.getWeeklyLimitValue());
			requestParameters.put("_contractIdValues",actionsDTO.getContractId());
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: " + e);
			return false;
		}
		
		try {
			return callAsync(serviceName, operationName, requestParameters);
		} catch (JSONException e) {
			LOG.error("Unable to edit action limits " + e);
			return false;
		} catch (Exception e) {
			LOG.error("Caught exception at editServiceDefinitionLimits method: " + e);
			return false;
		}

	}

	@Override
	public boolean deleteServiceDefinitionActions(String actionId, String contractId) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_SERVICEDEFINITION_REMOVE_ACTIONS;

		Map<String, Object> requestParameters =  new HashMap<String, Object>();
		requestParameters.put("_action",actionId);
	    requestParameters.put("_contractIdValues",contractId);
		
		try {
			return callAsync(serviceName, operationName, requestParameters);
		} catch (JSONException e) {
			LOG.error("Unable to remove actions" + e);
			return false;
		} catch (Exception e) {
			LOG.error("Caught exception at deleteServiceDefinitionActions method: " + e);
			return false;
		}
	}

	@Override
	public List<CustomerGroupDTO> fetchCustomerIds(String customerRoleId) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.DB_CUSTOMERGROUP_GET;
       
        List<CustomerGroupDTO> customerGroupDTOs = null;
		String groupResponse = null;
		
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		String filter = "Group_id eq "+ customerRoleId;
		requestParameters.put("$filter", filter);
		
		try {
			groupResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					build().getResponse();
			JSONObject responseObj = new JSONObject(groupResponse);
			JSONArray jsonArray = responseObj.optJSONArray("customergroup");
			customerGroupDTOs = JSONUtils.parseAsList(jsonArray.toString(), CustomerGroupDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch contracts from table: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at fetchContracts: " + e);
			return null;
		}
		
		return customerGroupDTOs;
	}

	@Override
	public boolean editCustomerRoleLimits(ActionLimitsDTO actionsDTO) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_CUSTOMERROLE_ACTIONLIMITS_UPDATE;

		Map<String, Object> requestParameters =  new HashMap<String, Object>();
		
		try {
			requestParameters = JSONUtils.parseAsMap(new JSONObject(actionsDTO).toString(), String.class, Object.class);
			requestParameters.put("_action",actionsDTO.getActionId());
			requestParameters.put("_maxTxLimit",actionsDTO.getMaxTransactionLimitValue());
			requestParameters.put("_dailyLimit",actionsDTO.getDailyLimitValue());
			requestParameters.put("_weeklyLimit",actionsDTO.getWeeklyLimitValue());
			requestParameters.put("_customerIdValues",actionsDTO.getCustomerId());
		} catch (IOException e) {
			LOG.error("Error occured while fetching the input params: " + e);
			return false;
		}
		
		try {
			return callAsync(serviceName, operationName, requestParameters);
		} catch (JSONException e) {
			LOG.error("Unable to edit action limits " + e);
			return false;
		} catch (Exception e) {
			LOG.error("Caught exception at editCustomerRoleLimits method: " + e);
			return false;
		}

	}

	@Override
	public boolean deleteCustomerRoleActions(String actionId, String customerId) {
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_CUSTOMERROLE_REMOVE_ACTIONS;

		Map<String, Object> requestParameters =  new HashMap<String, Object>();
		requestParameters.put("_action",actionId);
	    requestParameters.put("_customerIdValues",customerId);
		
		try {
			return callAsync(serviceName, operationName, requestParameters);
		} catch (JSONException e) {
			LOG.error("Unable to remove actions" + e);
			return false;
		} catch (Exception e) {
			LOG.error("Caught exception at deleteCustomerRoleActions method: " + e);
			return false;
		}
	}

	private boolean callAsync(String serviceName, String operationName, Map<String,Object> requestParameters) {
		Callable<Boolean> updateLimits = new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
				String response = DBPServiceExecutorBuilder.builder().
						withServiceId(serviceName).
						withObjectId(null)
						.withOperationId(operationName).
						withRequestParameters(requestParameters).build()
						.getResponse();
				return true;
			}
		};
		try {
			ThreadExecutor.getExecutor().execute(updateLimits);
		} catch (InterruptedException e) {
			LOG.error("actionLimitUpdates throw error ", e);
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			LOG.error("Exception occured", e);
		}
		return true;
	}

	@Override
	public boolean addFeaturesToContract(ActionLimitsDTO actionLimit, Map<String, Object> headerMap, JsonObject log) {
		LimitsAndPermissionsBackendDelegate limitsAndPermissionsBackendDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BackendDelegateFactory.class)
				.getBackendDelegate(LimitsAndPermissionsBackendDelegate.class);
		
		return limitsAndPermissionsBackendDelegate.addFeaturesToContract(actionLimit, headerMap, log);
	}

	@Override
	public boolean addActionsToContract(ActionLimitsDTO actionLimit, Map<String, Object> headerMap, JsonObject log,Set<String> contractCustomerAccounts) {
		LimitsAndPermissionsBackendDelegate limitsAndPermissionsBackendDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BackendDelegateFactory.class)
				.getBackendDelegate(LimitsAndPermissionsBackendDelegate.class);
		
		return limitsAndPermissionsBackendDelegate.addActionsToContract(actionLimit, headerMap, log,contractCustomerAccounts);
	}

	@Override
	public boolean addActionsToCustomer(ActionLimitsDTO actionLimit, Map<String, Object> headerMap) {
		LimitsAndPermissionsBackendDelegate limitsAndPermissionsBackendDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BackendDelegateFactory.class)
				.getBackendDelegate(LimitsAndPermissionsBackendDelegate.class);
		
		return limitsAndPermissionsBackendDelegate.addActionsToCustomer(actionLimit, headerMap);
	}

	@Override
	public boolean addActionsToCustomRole(ActionLimitsDTO actionLimit, Map<String, Object> headerMap) {
		LimitsAndPermissionsBackendDelegate limitsAndPermissionsBackendDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BackendDelegateFactory.class)
				.getBackendDelegate(LimitsAndPermissionsBackendDelegate.class);
		
		return limitsAndPermissionsBackendDelegate.addActionsToCustomRole(actionLimit, headerMap);
	}
}
