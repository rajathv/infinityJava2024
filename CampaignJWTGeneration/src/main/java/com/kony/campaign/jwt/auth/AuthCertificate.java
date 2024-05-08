/**
 * 
 */
package com.kony.campaign.jwt.auth;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.campaign.jwt.auth.utils.GetCertifcateKeys;
import com.kony.campaign.jwt.auth.utils.TemenosUtils;
import com.konylabs.middleware.controller.DataControllerRequest;

public class AuthCertificate {
	private static Logger logger = LogManager.getLogger(AuthCertificate.class);

	GetCertifcateKeys certifcateKeys = GetCertifcateKeys.getInstance();

	private volatile static AuthCertificate instance = null;

	private AuthCertificate()
	{

	}
	
	public static AuthCertificate getInstance()
	{
		if (instance == null) {
			synchronized (AuthCertificate.class){
				if (instance == null) {
					instance = new AuthCertificate();
				}
			}
		}
		return instance;
	}
	
	public PrivateKey getPrivateKey(DataControllerRequest request)
	{
		Map<String, String> certMap = getCertMap(request);
		if (certMap == null || certMap.get(AuthConstants.PARAM_CERT_PRIVATE_KEY) == null) {
			logger.error("auth private key is null");
			return null;
		}
		return certifcateKeys.getPrivateKey(certMap, request);
	}

	public PublicKey getPublicKey(DataControllerRequest request)
	{
		Map<String, String> certMap = getCertMap(request);
		if (certMap == null || certMap.get(AuthConstants.PARAM_CERT_PRIVATE_KEY) == null) {
			logger.error("auth public key is null");
			return null;
		}
		return certifcateKeys.getPublicKey(certMap);
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getCertMap(DataControllerRequest request)
	{
		TemenosUtils temenosUtils = TemenosUtils.getInstance();
		Map<String, String> certMap = null;
		String cachedData = (String)temenosUtils.getDataFromCache(request, AuthConstants.AUTH_CERT_CACHE_KEY);
		if (StringUtils.isNotBlank(cachedData)) {
			certMap = (Map<String, String>)temenosUtils.buildObjectFromJSONString(cachedData);
			logger.error("auth keys retrieved from cache");
		}
		else
		{
			request.addRequestParam_(AuthConstants.PARAM_BACKEND_NAME, AuthConstants.CONSTANT_TEMPLATE_NAME);
			request.addRequestParam_(AuthConstants.PARAM_CERT_NAME, AuthConstants.AUTH_CERT_NAME);
			certMap = certifcateKeys.getCertKeyPair(request);
			temenosUtils.insertDataIntoCache(request, certMap, AuthConstants.AUTH_CERT_CACHE_KEY, AuthConstants.AUTH_CERT_CACHE_TIME);
			logger.error("auth keys stored in cache");
		}
		return certMap;
	}
}
