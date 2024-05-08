package com.temenos.auth.usermanagement.businessdelegate.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.JSONUtil;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.auth.usermanagement.backenddelegate.api.AuthUserManagementBackendDelegate;
import com.temenos.auth.usermanagement.businessdelegate.api.AuthUserManagementBusinessDelegate;
import com.temenos.dbx.product.usermanagement.businessdelegate.api.CustomerActionsBusinessDelegate;
import com.temenos.dbx.product.utils.InfinityConstants;

public class AuthUserManagementBusinessDelegateImpl 
				implements AuthUserManagementBusinessDelegate {
	
	private static final Logger LOG = LogManager
			.getLogger(AuthUserManagementBusinessDelegateImpl.class);

	/**
	 * Fetches active legal entities.
	 * Throws exception if there is no active legal entity
	 */
	@Override
	public Result getCustomerActiveLegalEntities(String customerId) 
			throws ApplicationException {
		AuthUserManagementBackendDelegate usermgmntBackendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(AuthUserManagementBackendDelegate.class);
		LOG.debug("is customer id null : "+ StringUtils.isBlank(customerId));
		Result response = usermgmntBackendDelegate.getCustomerActiveLegalEntities(customerId);
		if(!HelperMethods.hasRecords(response)) {
			throw new ApplicationException(ErrorCodeEnum.ERR_29055); 
		}
		return response;
	}

	@Override
	public Result getCustomerFeatureAndPermissions(String legalEntityId, String cacheKey,
			Map<String, String> userInfo) throws ApplicationException {
		
		String isC360Admin = userInfo.get("isC360Admin");
		/**
		 * returns for API indentity login
		 */
		if("true".equalsIgnoreCase(isC360Admin)) {
			Result featureAndPermissions = new Result();
			LOG.debug("... loggedin user is API user...");
			JSONArray permissionsArray = new JSONArray();
			permissionsArray.put("API_ACCESS");
			featureAndPermissions.addStringParam("permissions",
					permissionsArray.toString());
			return featureAndPermissions;
		}
		
		/**
		 * returns from cache
		 */
		
		String featuresAndPermissionsStr = (String) MemoryManager.getFromCache(cacheKey);
		LOG.debug("retieved current leid features and permissions::" + featuresAndPermissionsStr);
		if(StringUtils.isNoneBlank(featuresAndPermissionsStr)) {
			return JSONToResult.convert(featuresAndPermissionsStr);
		}
		
		/**
		 * Seems currentLegalEntity is not set
		 */
		if(StringUtils.isBlank(legalEntityId)) {
			Result featureAndPermissions = new Result();
			LOG.debug("... loggedin user has not set current legal entity...");
			JSONArray permissionsArray = new JSONArray();
			permissionsArray.put("ALLOW");
			featureAndPermissions.addStringParam("permissions",
					permissionsArray.toString());
			return featureAndPermissions;
		}
		
		/**
		 * populates cache
		 */
		
		CustomerActionsBusinessDelegate businessDelegate =
		        DBPAPIAbstractFactoryImpl.getBusinessDelegate(CustomerActionsBusinessDelegate.class);
		
		boolean isProspect = HelperMethods.isProspectUserType(userInfo.get("CustomerType_id"));
		LOG.debug("current legal entity id {}, isProspect {} ", legalEntityId, isProspect);
		Map<String, Object> map = new HashMap<>();
		map.put(InfinityConstants.isProspectFlow, isProspect);
		
		Map<String, Set<String>> securityAttributes =
		        businessDelegate.getSecurityAttributes(userInfo.get("customer_id"),map,legalEntityId);
		
		Set<String> actions = securityAttributes.get("actions");
		Set<String> features = securityAttributes.get("features");

		if (null == actions || null == features) {
		    actions = new HashSet<>();
		    features = new HashSet<>();
		}
		Result featureAndPermissions = new Result();
		featureAndPermissions.addStringParam("permissions", JSONUtil.getJSONString(actions));
		featureAndPermissions.addStringParam("features", JSONUtil.getJSONString(features));
		featuresAndPermissionsStr = ResultToJSON.convert(featureAndPermissions);
		int EXPIRY_TIME = HelperMethods.getExpiryTime("FEATURE_PERMISSIONS_EXPIRY_TIME");
		MemoryManager.saveIntoCache(cacheKey, featuresAndPermissionsStr, EXPIRY_TIME);
		LOG.debug("saving current leid permissions::" + featuresAndPermissionsStr);
		return featureAndPermissions;
	}

}
