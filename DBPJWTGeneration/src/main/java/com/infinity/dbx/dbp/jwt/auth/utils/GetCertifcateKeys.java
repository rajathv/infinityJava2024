package com.infinity.dbx.dbp.jwt.auth.utils;

import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import com.infinity.dbx.dbp.jwt.auth.AuthConstants;
import com.infinity.dbx.dbp.jwt.auth.Authentication;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.nimbusds.jose.util.IOUtils;


public class GetCertifcateKeys {
	
	private static final Logger logger = LogManager.getLogger(GetCertifcateKeys.class);
	
	private static class Holder
	{
		private static GetCertifcateKeys instance = new GetCertifcateKeys();
	}
	private GetCertifcateKeys()
	{
		
	}
	public static GetCertifcateKeys getInstance()
	{
		return Holder.instance;
	}
	
	public PublicKey getPublicKeyFromFile(String fileName) 
	{
		PublicKey publicKey = null;
		if (StringUtils.isNotBlank(fileName)) {
			byte[] keyBytes = readkeyFile(fileName);
			if (keyBytes != null && keyBytes.length > 0) {
				Security.addProvider(new BouncyCastleProvider());
				X509EncodedKeySpec pkcs8EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
				try {
					KeyFactory keyFactory = KeyFactory.getInstance("RSA");
					publicKey = keyFactory.generatePublic(pkcs8EncodedKeySpec);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		return publicKey;
	}

	public PrivateKey getPrivateKeyFromFile(String fileName)
	{
		PrivateKey privateKey = null;
		if (StringUtils.isNotBlank(fileName)) {
			byte[] keyBytes = readkeyFile(fileName);
			if (keyBytes != null && keyBytes.length > 0) {
				Security.addProvider(new BouncyCastleProvider());
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
				try {
					KeyFactory keyFactory = KeyFactory.getInstance("RSA");
					privateKey = keyFactory.generatePrivate(keySpec);
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
		return privateKey;
	}

	private byte[] readkeyFile(String fileName)
	{
		byte[] keyBytes = null;
		InputStream inputStream = Authentication.class.getClassLoader().getResourceAsStream(fileName);
		try {
			String inputStreamString = IOUtils.readInputStreamToString(inputStream, StandardCharsets.UTF_8);
			@SuppressWarnings("resource")
			PemObject pem = new PemReader(new StringReader(inputStreamString)).readPemObject();
			keyBytes = pem.getContent();
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("error in reading the key -",e);
		}finally {
        	if (inputStream!=null) {
        		try {
        			inputStream.close();			
        		}
        		catch(Exception e)
        		{
        			logger.error(e);
        		}
        	}
        }
		return keyBytes;
	}
	
	public Map<String, String> getCertKeyPair(DataControllerRequest request)
	{
		HashMap<String,String> certFromDB = getCertFromDB(request);
		return certFromDB;
	}
	
	public Map<String, String> getCertKeyPair(Map<String, String> input)
	{
		HashMap<String,String> certFromDB = getCertFromDB(input);
		return certFromDB;
	}
	
	@SuppressWarnings({"resource"})
	public PrivateKey getPrivateKey(Map<String,String> certFromDB,DataControllerRequest request)
	{
		PrivateKey privateKey = null;
		if (certFromDB != null && certFromDB.get(AuthConstants.PARAM_CERT_PRIVATE_KEY) != null) {
			String ENCRYPTION_KEY = TemenosUtils.getServerEnvironmentProperty(AuthConstants.PRIVATE_ENCRYPTION_KEY, request);
			try {
				String decryptedPrivateKey = EncryptionUtils.decrypt(certFromDB.get(AuthConstants.PARAM_CERT_PRIVATE_KEY), ENCRYPTION_KEY);
				PemObject pem = new PemReader(new StringReader(decryptedPrivateKey)).readPemObject();
				Security.addProvider(new BouncyCastleProvider());
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pem.getContent());
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				privateKey = keyFactory.generatePrivate(keySpec);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(),e);
			}
		}
		return privateKey;
	}
	
	@SuppressWarnings({"resource"})
	public PrivateKey getPrivateKey(Map<String,String> certFromDB)
	{
		PrivateKey privateKey = null;
		if (certFromDB != null && certFromDB.get(AuthConstants.PARAM_CERT_PRIVATE_KEY) != null) {
			String ENCRYPTION_KEY = TemenosUtils.getServerEnvironmentProperty(AuthConstants.PRIVATE_ENCRYPTION_KEY);
			try {
				String decryptedPrivateKey = EncryptionUtils.decrypt(certFromDB.get(AuthConstants.PARAM_CERT_PRIVATE_KEY), ENCRYPTION_KEY);
				PemObject pem = new PemReader(new StringReader(decryptedPrivateKey)).readPemObject();
				Security.addProvider(new BouncyCastleProvider());
				PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pem.getContent());
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				privateKey = keyFactory.generatePrivate(keySpec);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(),e);
			}
		}
		return privateKey;
	}
	
	@SuppressWarnings({"resource"})
	public PublicKey getPublicKey(Map<String,String> certFromDB)
	{
		PublicKey publicKey = null;
		try {
			if (certFromDB != null && certFromDB.get(AuthConstants.PARAM_CERT_PUBLIC_KEY) != null) {
				PemObject pem = new PemReader(new StringReader(certFromDB.get(AuthConstants.PARAM_CERT_PUBLIC_KEY))).readPemObject();
				Security.addProvider(new BouncyCastleProvider());
				X509EncodedKeySpec pkcs8EncodedKeySpec = new X509EncodedKeySpec(pem.getContent());
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				publicKey = keyFactory.generatePublic(pkcs8EncodedKeySpec);
			}
		} catch (Exception e) {
			logger.error("error reading public key -"+e.getLocalizedMessage(),e);
		}
		return publicKey;
	}
	
	private  HashMap<String, String> getCertFromDB(DataControllerRequest request)
	{
		HashMap<String, String> certMap = new HashMap<String, String>();
		StringBuffer queryString = new StringBuffer();
		queryString.append(AuthConstants.PARAM_BACKEND_NAME+" ");
		queryString.append(AuthConstants.EQ+" ");
		queryString.append(request.getParameter(AuthConstants.PARAM_BACKEND_NAME)+" ");
		queryString.append(AuthConstants.AND+" ");
		queryString.append(AuthConstants.PARAM_CERT_NAME+" ");
		queryString.append(request.getParameter(AuthConstants.PARAM_CERT_NAME));
		request.addRequestParam_(AuthConstants.$FILTER, queryString.toString());
		Result backendCertResult = (Result)CommonUtils.callInternalService(AuthConstants.SERVICE_BACKEND_CERTIFICATE, AuthConstants.OPERATION_BACKEND_CERTIFICATE_GET, null, null, request, 1, true);
		if (backendCertResult != null && backendCertResult.getDatasetById(AuthConstants.PARAM_BACKEND_CERTIFICATE) != null) {
			Dataset certDataset = backendCertResult.getDatasetById(AuthConstants.PARAM_BACKEND_CERTIFICATE);
			if (certDataset.getAllRecords().size() != 0) {
				Record certRecord = certDataset.getRecord(0);
				if (certRecord != null) {
					certMap.put(AuthConstants.PARAM_CERT_PRIVATE_KEY, certRecord.getParamValueByName(AuthConstants.PARAM_CERT_PRIVATE_KEY));
					certMap.put(AuthConstants.PARAM_CERT_PUBLIC_KEY, certRecord.getParamValueByName(AuthConstants.PARAM_CERT_PUBLIC_KEY));
				}
			}
		}
		return certMap;
	}
	
	private  HashMap<String, String> getCertFromDB(Map<String, String> input)
	{
		HashMap<String, String> certMap = new HashMap<String, String>();
		StringBuffer queryString = new StringBuffer();
		queryString.append(AuthConstants.PARAM_BACKEND_NAME+" ");
		queryString.append(AuthConstants.EQ+" ");
		queryString.append(input.get(AuthConstants.PARAM_BACKEND_NAME)+" ");
		queryString.append(AuthConstants.AND+" ");
		queryString.append(AuthConstants.PARAM_CERT_NAME+" ");
		queryString.append(input.get(AuthConstants.PARAM_CERT_NAME));
		input.put(AuthConstants.$FILTER, queryString.toString());
		Result backendCertResult = (Result)CommonUtils.callInternalService(AuthConstants.SERVICE_BACKEND_CERTIFICATE, AuthConstants.OPERATION_BACKEND_CERTIFICATE_GET, null, null, input, 1);
		if (backendCertResult != null && backendCertResult.getDatasetById(AuthConstants.PARAM_BACKEND_CERTIFICATE) != null) {
			Dataset certDataset = backendCertResult.getDatasetById(AuthConstants.PARAM_BACKEND_CERTIFICATE);
			if (certDataset.getAllRecords().size() != 0) {
				Record certRecord = certDataset.getRecord(0);
				if (certRecord != null) {
					certMap.put(AuthConstants.PARAM_CERT_PRIVATE_KEY, certRecord.getParamValueByName(AuthConstants.PARAM_CERT_PRIVATE_KEY));
					certMap.put(AuthConstants.PARAM_CERT_PUBLIC_KEY, certRecord.getParamValueByName(AuthConstants.PARAM_CERT_PUBLIC_KEY));
				}
			}
		}
		return certMap;
	}
}
