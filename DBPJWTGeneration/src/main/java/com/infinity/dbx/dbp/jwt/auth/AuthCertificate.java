/**
 * 
 */
package com.infinity.dbx.dbp.jwt.auth;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.dbp.jwt.auth.utils.GetCertifcateKeys;
import com.infinity.dbx.dbp.jwt.auth.utils.TemenosUtils;
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
	
	public PrivateKey getPrivateKey()
	{
		Map<String, String> certMap = getCertMap();
		if (certMap == null || certMap.get(AuthConstants.PARAM_CERT_PRIVATE_KEY) == null) {
			logger.error("auth private key is null");
			return null;
		}
		return certifcateKeys.getPrivateKey(certMap);
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
		String cachedData = (String)temenosUtils.getDataFromCache(request, AuthConstants.AUTH_CERT_CACHE_KEY+"_"+AuthConstants.CONSTANT_TEMPLATE_NAME);
		if (StringUtils.isNotBlank(cachedData) && StringUtils.contains(cachedData, AuthConstants.AUTH_CERT_CACHE_KEY+"_"+AuthConstants.CONSTANT_TEMPLATE_NAME)) {
			certMap = (Map<String, String>)temenosUtils.buildObjectFromJSONString(cachedData);
			logger.error("auth keys retrieved from cache");
		}
		else
		{
			request.addRequestParam_(AuthConstants.PARAM_BACKEND_NAME, AuthConstants.CONSTANT_TEMPLATE_NAME);
			request.addRequestParam_(AuthConstants.PARAM_CERT_NAME, AuthConstants.AUTH_CERT_NAME);
			certMap = certifcateKeys.getCertKeyPair(request);
			temenosUtils.insertDataIntoCache(request, certMap, AuthConstants.AUTH_CERT_CACHE_KEY+"_"+AuthConstants.CONSTANT_TEMPLATE_NAME, AuthConstants.AUTH_CERT_CACHE_TIME);
			logger.error("auth keys stored in cache");
		}
		return certMap;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> getCertMap()
	{
		TemenosUtils temenosUtils = TemenosUtils.getInstance();
		Map<String, String> certMap = null;
		String cachedData = (String)temenosUtils.getDataFromCache(AuthConstants.AUTH_CERT_CACHE_KEY+"_"+AuthConstants.CONSTANT_TEMPLATE_NAME);
		if (StringUtils.isNotBlank(cachedData)) {
			certMap = (Map<String, String>)temenosUtils.buildObjectFromJSONString(cachedData);
			logger.error("auth keys retrieved from cache");
		}
		else
		{
			Map<String, String> input = new HashMap<String, String>();
			input.put(AuthConstants.PARAM_BACKEND_NAME, AuthConstants.CONSTANT_TEMPLATE_NAME);
			input.put(AuthConstants.PARAM_CERT_NAME, AuthConstants.AUTH_CERT_NAME);
			certMap = certifcateKeys.getCertKeyPair(input);
			temenosUtils.insertDataIntoCache(certMap, AuthConstants.AUTH_CERT_CACHE_KEY+"_"+AuthConstants.CONSTANT_TEMPLATE_NAME, AuthConstants.AUTH_CERT_CACHE_TIME);
			logger.error("auth keys stored in cache");
		}
		return certMap;
	}
}
