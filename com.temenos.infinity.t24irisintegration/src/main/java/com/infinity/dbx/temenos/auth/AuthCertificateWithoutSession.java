package com.infinity.dbx.temenos.auth;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.infinity.dbx.temenos.utils.GetCertifcateKeys;
import com.konylabs.middleware.controller.DataControllerRequest;

public class AuthCertificateWithoutSession {
	
	private static Logger logger = LogManager.getLogger(AuthCertificateWithoutSession.class);

	GetCertifcateKeys certifcateKeys = GetCertifcateKeys.getInstance();

	private volatile static AuthCertificateWithoutSession instance = null;

	private AuthCertificateWithoutSession()
	{

	}
	
	public static AuthCertificateWithoutSession getInstance()
	{
		if (instance == null) {
			synchronized (AuthCertificate.class){
				if (instance == null) {
					instance = new AuthCertificateWithoutSession();
				}
			}
		}
		return instance;
	}
	
	public PrivateKey getPrivateKey(DataControllerRequest request)
	{
		Map<String, String> certMap = getCertMap(request);
		if (certMap == null || certMap.get(TemenosConstants.PARAM_CERT_PRIVATE_KEY) == null) {
			logger.error("auth private key is null");
			return null;
		}
		return certifcateKeys.getPrivateKey(certMap, request);
	}

	public PublicKey getPublicKey(DataControllerRequest request)
	{
		Map<String, String> certMap = getCertMap(request);
		if (certMap == null || certMap.get(TemenosConstants.PARAM_CERT_PRIVATE_KEY) == null) {
			logger.error("auth public key is null");
			return null;
		}
		return certifcateKeys.getPublicKey(certMap);
	}

	private Map<String, String> getCertMap(DataControllerRequest request)
	{
		Map<String, String> certMap = null;

			request.addRequestParam_(TemenosConstants.PARAM_BACKEND_NAME, TemenosConstants.CONSTANT_TEMPLATE_NAME);
			request.addRequestParam_(TemenosConstants.PARAM_CERT_NAME, AuthConstants.AUTH_CERT_NAME);
			certMap = certifcateKeys.getCertKeyPair(request);
		return certMap;
	}

}
