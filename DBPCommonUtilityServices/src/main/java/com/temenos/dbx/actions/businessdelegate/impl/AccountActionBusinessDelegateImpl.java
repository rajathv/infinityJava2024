package com.temenos.dbx.actions.businessdelegate.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.LegalEntityUtil;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.temenos.dbx.actions.businessdelegate.api.AccountActionBusinessDelegate;
import com.temenos.dbx.actions.dto.AccountActionDTO;
import com.temenos.dbx.constants.ActionConstant;

public class AccountActionBusinessDelegateImpl implements AccountActionBusinessDelegate {

	public static final String ACCOUNT_ACTION_KEY = "Accounts";
	public static final String ACCOUNT_NUMBER = "accountID";
	public static final String IS_BUSINESS_ACCOUNT = "isBusinessAccount";
	public static final String ACTIONS = "actions";
	public static final String PERMISSIONS_KEY = "permissions";

	protected Map<String, AccountActionDTO> accountFeatureActions = new HashMap<>();
	protected Set<String> permissions = new HashSet<>();
	protected boolean isPermissionCheckEnabled = true;
	protected boolean isInitialized = false;
	private static final Logger LOG = LogManager.getLogger(AccountActionBusinessDelegateImpl.class);
	
	public void initFeatureAction(FabricRequestManager fabricRequestManager) {
		isPermissionCheckEnabled = getPermissionCheckEnableFlag();
		if(isPermissionCheckEnabled) {
			JSONObject featureAndPermissions = 
					LegalEntityUtil.getUserCurrentLegalEntityFeaturePermissions(fabricRequestManager);
			if (featureAndPermissions != null) {
				try {
					populatePermissions(getPermissions(featureAndPermissions));
					LOG.debug("permissions :"+permissions);
				} catch (Exception e) {
					LOG.debug("exception while getting permissions",e);
				}
			}
		}
	}
	
	public JSONArray getPermissions(JSONObject featureAndPermissions) {
		if (featureAndPermissions.has(PERMISSIONS_KEY)) {
			return new JSONArray(featureAndPermissions.getString(PERMISSIONS_KEY));
		}
		return new JSONArray();
	}
	
	public void populatePermissions(JSONArray permission) {
		for (int i = 0; i < permission.length(); i++) {
			permissions.add(permission.getString(i));
		}
	}

	public Set<String> getActionsSet(String action) {
		Set<String> actionSet = new HashSet<>();
		if (StringUtils.isNotEmpty(action)) {
			JSONArray actions = new JSONArray(action);
			for (int j = 0; j < actions.length(); j++) {
				actionSet.add(actions.getString(j));
			}
		}
		return actionSet;
	}

	@Override
	public boolean hasUserAction(ActionConstant action) {
		return permissions.contains(action.name()) || !isPermissionCheckEnabled;
	}

	@Override
	public Set<String> getAccountsHavingAction(ActionConstant action) {
		Set<String> actionSet = new HashSet<>();
		for(Entry<String, AccountActionDTO> accountAction : accountFeatureActions.entrySet()) {
			if(accountFeatureActions.get(accountAction.getKey()).getActions().contains(action.name())) {
				actionSet.add(accountAction.getKey());
			}
		}
		return actionSet;
	}
	
	@Override
	public Set<String> getAccountActions(String accountId) {
		if(StringUtils.isNotBlank(accountId)) {
			return accountFeatureActions.get(accountId).getActions();
		}
		return new HashSet<>();
	}
	
	public boolean getPermissionCheckEnableFlag() {
		try {
			String isPermissionEnabled = EnvironmentConfigurationsHandler.getValue("PERMISSION_CHECK_ENABLE");
			LOG.debug("Runtime Parameter PERMISSION_CHECK_ENABLE is set to :"+isPermissionEnabled);
			if(StringUtils.isNotBlank(isPermissionEnabled)) {
				return BooleanUtils.toBoolean(isPermissionEnabled);
			}
		}catch(Exception e) {
			LOG.error("PERMISSION_CHECK_ENABLE flag is not configured.");
		}
		return true;
	}

	@Override
	public boolean hasUserAccountFeatureAction(String userId, String accountId, ActionConstant action) {
		
		if(!isPermissionCheckEnabled) {
			return true;
		}
		
		if(!isInitialized) {
			initAccountFeatureAction(userId);
		}
		
		return (accountFeatureActions.containsKey(accountId) && 
				accountFeatureActions.get(accountId).getActions().contains(action.name()));
	}

	public void initAccountFeatureAction(String userId) {
		isPermissionCheckEnabled = getPermissionCheckEnableFlag();
		if(!isPermissionCheckEnabled) {
			return;
		}
		
		try {
			String featureActionsInCache = (String)MemoryManager.getFromCache(
					DBPUtilitiesConstants.ACCOUNTS_POSTLOGIN_CACHE_KEY + userId);
			LOG.debug("accountActions retrieved from cache is :"+featureActionsInCache);
			if(StringUtils.isNotBlank(featureActionsInCache)) {
				JSONObject accountFeatureActionsObj = new JSONObject(featureActionsInCache);
				if(accountFeatureActionsObj.has(ACCOUNT_ACTION_KEY) && 
						accountFeatureActionsObj.get(ACCOUNT_ACTION_KEY) != null) {
					JSONArray accountFeatureActionsList = accountFeatureActionsObj
							.getJSONArray(ACCOUNT_ACTION_KEY);
					for (int i = 0; i < accountFeatureActionsList.length(); i++) {
						JSONObject accountActions = accountFeatureActionsList.getJSONObject(i);
						String accountNumber = accountActions.optString(ACCOUNT_NUMBER);
						boolean isBusinessAccount = accountActions.optBoolean(IS_BUSINESS_ACCOUNT);
						Set<String> actionSet = getActionsSet(accountActions.optString(ACTIONS));
						AccountActionDTO accountAction = new AccountActionDTO();
						accountAction.setAccountNumber(accountNumber);
						accountAction.setActions(actionSet);
						accountAction.setBusinessAccount(isBusinessAccount);
						accountFeatureActions.put(accountNumber, accountAction);
					}
				}
			}
		} catch (Exception e) {
			LOG.error("Exception occured while initializing feature actions from cache :",e);
		}
	}

}