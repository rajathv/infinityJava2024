package com.temenos.infinity.api.holdings.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.api.processor.IdentityHandler;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.ehcache.ResultCache;
import com.konylabs.middleware.ehcache.ResultCacheImpl;
import com.konylabs.middleware.exceptions.MiddlewareException;
import com.nimbusds.jose.JWSAlgorithm;
import com.temenos.infinity.api.commons.invocation.Executor;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.holdings.config.HoldingsAPIServices;
import com.temenos.infinity.api.holdings.config.ServerConfigurations;
import com.temenos.infinity.api.holdings.constants.TemenosConstants;
import com.temenos.infinity.api.holdings.dto.AccountsDTO;
import com.temenos.infinity.transact.tokenmanager.dto.BackendCertificate;

public class HoldingsUtils {

	 private HoldingsUtils() {

	    }

	    private static final Logger LOG = LogManager.getLogger(HoldingsUtils.class);
	    private static final String SUFFIX_CACHE_NAME = "_CURRENT_LEID";

    /**
     * Get Backend Certificate from DB
     * 
     * @param backendName
     */    
    public static BackendCertificate getCertFromDB(String backendName) {
        BackendCertificate backendCertificate = new BackendCertificate();
        Map<String, Object> inputMap = new HashMap<>();
        StringBuffer queryString = new StringBuffer();
        queryString.append("BackendName" + " ");
        queryString.append("eq" + " ");
        queryString.append(backendName);

        inputMap.put("$filter", queryString.toString());
        String backendCertResponse = null;
        try {
            backendCertResponse =
                    Executor.invokeService(HoldingsAPIServices.SERVICE_BACKEND_CERTIFICATE, inputMap, null);
            LOG.debug("Backend Certificate" + backendCertResponse);
        } catch (Exception e1) {
            LOG.error("Service call to dbxdb failed "+e1.toString());
        }
        JSONObject serviceResponseJSON = Utilities.convertStringToJSON(backendCertResponse);
        if (serviceResponseJSON == null) {
            LOG.error("EmptyResponse no backend certificate for MS");
        }
        if (serviceResponseJSON.has("backendcertificate")
                && serviceResponseJSON.getJSONArray("backendcertificate").length() > 0) {
            JSONObject certificateObj = serviceResponseJSON.getJSONArray("backendcertificate").getJSONObject(0);
            backendCertificate.setBackendName(certificateObj.getString("BackendName"));
            String certificateEncryptionKey = new String();
            try {
                certificateEncryptionKey = ServerConfigurations.HOLDINGS_PRIVATE_ENCRYPTION_KEY.getValue();
            } catch (Exception e) {
                LOG.error("Couldnt parse MS_PRIVATE_ENCRYPTION_KEY from environment "+ e.toString());

            }

            if (StringUtils.isNotBlank(certificateEncryptionKey))
                backendCertificate.setCertificateEncryptionKey(certificateEncryptionKey);
            backendCertificate.setCertificatePrivateKey(certificateObj.getString("CertPrivateKey"));
            backendCertificate.setCertificatePublicKey(certificateObj.getString("CertPublicKey"));
            backendCertificate.setGetPublicKeyServiceURL("/sample/service");
            backendCertificate.setId(certificateObj.getString("id"));
            backendCertificate.setJwsAlgorithm(JWSAlgorithm.RS256);

        }

        return backendCertificate;

    }
    
    /**
     * Get User Attributes from Identity Session
     * 
     * @param request
     * @param attributeName
     */ 
    public static String getUserAttributeFromIdentity(DataControllerRequest request, String attribute) {
        // TODO Auto-generated method stub
        try {
            if (request.getServicesManager() != null && request.getServicesManager().getIdentityHandler() != null) {
                Map<String, Object> userMap = request.getServicesManager().getIdentityHandler().getUserAttributes();
                if (userMap.get(attribute) != null) {
                    String attributeValue = userMap.get(attribute) + "";

                    return attributeValue;
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOG.error(e);
        }

        return "";
    }
    /**
     * retrieves data from cache
     * 
     * @param servicesManager
     * @param result
     * @param key
     */
    @SuppressWarnings("deprecation")
    private static Object retriveDataFromCache(ServicesManager servicesManager, String key, String sessionId) {
        Object result = null;
        try {
            String cacheKey = "";
            String userId =
                    servicesManager.getIdentityHandler() != null ? servicesManager.getIdentityHandler().getUserId()
                            : "";
            cacheKey = StringUtils.isNotBlank(userId) && !com.temenos.infinity.api.holdings.constants.TemenosConstants.USER_ID_ANONYMOUS.equalsIgnoreCase(userId)
                    ? userId
                    : sessionId;
            ResultCache resultCache = servicesManager.getResultCache();
            String valueInCache = "";
            try {
                valueInCache = resultCache.retrieveFromCache(cacheKey) != null
                        ? (String) resultCache.retrieveFromCache(cacheKey)
                        : null;
            } catch (Exception e) {
                try {
                    valueInCache = (String) ServicesManagerHelper.getServicesManager().getResultCache()
                            .retrieveFromCache(cacheKey);
                } catch (MiddlewareException e1) {
                    LOG.error(e1);
                    valueInCache = (String) ResultCacheImpl.getInstance().retrieveFromCache(cacheKey);
                }
            }
            if (StringUtils.isNotBlank(valueInCache)) {
                Gson gson = new Gson();
                Type type = new TypeToken<HashMap<String, Object>>() {
                }.getType();
                Map<String, Object> resultMap = gson.fromJson(valueInCache, type);
                if (resultMap != null) {
                    if (resultMap.get(key) != null) {
                        result = resultMap.get(key);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Exception occured while retrieving from cache" + e);
            return null;
        }
        return result;
    }
    
    /**
     * with DCR
     * 
     * @param key
     * @param dcRequest
     * @return
     * @throws Exception
     */
    public static Object retreiveFromSession(String key, DataControllerRequest dcRequest) {
        try {
            ServicesManager servicesManager = dcRequest.getServicesManager();
            String sessionId = dcRequest.getHeader(TemenosConstants.COOKIE);
            return retriveDataFromCache(servicesManager, key, sessionId);
        } catch (Exception e) {
            LOG.error("Exception occured:" + e);
            return null;
        }

    }
    
    public static HashMap<String, AccountsDTO> getAccountsMapFromCache(DataControllerRequest request) {
        String accountsInSessionGson = (String) retreiveFromSession("Accounts", request);
        if (StringUtils.isNotBlank(accountsInSessionGson)) {
            Gson gson = new Gson();
            Type AccountMapType = new TypeToken<HashMap<String, AccountsDTO>>() {
            }.getType();
            HashMap<String, AccountsDTO> accounts = gson.fromJson(accountsInSessionGson, AccountMapType);
            return accounts;
        }
        LOG.error("Unable to find accounts in session");
        return null;
    }
    
    public static String getAccountIdWithCompanyFromCache(String accountId, DataControllerRequest request) {
        HashMap<String, AccountsDTO> accounts = HoldingsUtils.getAccountsMapFromCache(request);
        AccountsDTO account = accounts.get(accountId);
        return account.getAccountIdWithCompanyId();
    }
    
    public static Object getDataFromCache(DataControllerRequest request, String key) {
        Object cachedData = null;
        try {
            ServicesManager servicesManager = request.getServicesManager();
            ResultCache resultCache = servicesManager.getResultCache();
            cachedData = resultCache.retrieveFromCache(key);
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
        return cachedData;
    }
    
    public static <T> void insertDataIntoCache(DataControllerRequest request, T value, String key, int time) {
        try {
            ServicesManager servicesManager = request.getServicesManager();
            ResultCache resultCache = servicesManager.getResultCache();
            Gson gson = new Gson();
            String jsonString = gson.toJson(value);
            resultCache.insertIntoCache(key, jsonString, time);
        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }
    
    public static void setCompanyIdToRequest(DataControllerRequest request) {
    	String companyId = null;
		try {
			String userId = request.getServicesManager().getIdentityHandler().getUserId();
			companyId = (String) MemoryManager.getFromCache(userId+
					SUFFIX_CACHE_NAME);
			LOG.debug("retrieved leid "+companyId);
			if(StringUtils.isEmpty(companyId)) {
				IdentityHandler identityHandler = request.getServicesManager().getIdentityHandler();
				Map<String, Object> userAttributes = identityHandler.getUserAttributes();
				if(userAttributes != null && userAttributes.size() >0) {
					companyId = (String)userAttributes.get("legalEntityId");
					if(StringUtils.isBlank(companyId)) {
						companyId = (String)userAttributes.get("companyId");
					}
				}
			}
			request.addRequestParam_("companyid", companyId);
			request.getHeaderMap().put("companyid", companyId);
		} catch (Exception e) {
			LOG.error(e);
		}

	}
}
