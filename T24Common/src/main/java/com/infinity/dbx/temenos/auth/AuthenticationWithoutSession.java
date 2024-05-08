package com.infinity.dbx.temenos.auth;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.kony.dbx.util.CommonUtils;
import com.kony.dbx.util.Constants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class AuthenticationWithoutSession {

	private static final Logger logger = LogManager.getLogger(AuthenticationWithoutSession.class);

	private static class Inner {

		private static AuthenticationWithoutSession instance = new AuthenticationWithoutSession();
	}

	public static AuthenticationWithoutSession getInstance() {
		return Inner.instance;
	}

	private AuthenticationWithoutSession() {

	}
	
	public String getAuthToken(DataControllerRequest request) {
		AuthCertificateWithoutSession authCertificate = AuthCertificateWithoutSession.getInstance();
		String token = "";
		HashMap<String, Object> jwtParamsMap = new HashMap<>();
		try {
			PrivateKey privateKey = authCertificate.getPrivateKey(request);
			if (privateKey == null) {
				throw new Exception("error in getting private key");
			}
			jwtParamsMap.put(TemenosConstants.PARAM_CERT_PRIVATE_KEY, privateKey);
			String hostURL = CommonUtils.getServerEnvironmentProperty(Constants.DBP_HOST_URL, request);
			if (StringUtils.isBlank(hostURL)) {
				logger.error("DBP_HOST_URL is not specified");
				return token;
			}
			jwtParamsMap.put("issuer", hostURL);
			token = generateToken(jwtParamsMap);
			logger.error("token generated and stored in session");
		} catch (Exception e) {
			logger.error("error occured while generating token ", e);
		}
		return token;
	}
	
	
	private String generateToken(Map<String, Object> params) throws Exception {
		String jwt = "";
		JWTClaimsSet.Builder JWTbulder = new JWTClaimsSet.Builder().issuer(AuthConstants.TOKEN_ISSUER)
				.audience(AuthConstants.TOKEN_AUDIENCE)
				.subject(String.valueOf(params.get(TemenosConstants.PARAM_USERNAME)))
				.jwtID(UUID.randomUUID().toString()).issueTime(Date.from(Instant.now()))
				.expirationTime(Date.from(Instant.ofEpochSecond(Instant.now().getEpochSecond() + 1800)));

		addCustomAttributes(JWTbulder, params);

		JWTClaimsSet claimsSet = JWTbulder.build();
		JOSEObjectType joseObjectType = new JOSEObjectType("JWT");
		SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
				.keyID(AuthConstants.TOKEN_HEADER_KEY).type(joseObjectType).build(), claimsSet);

		PrivateKey privateKey = (PrivateKey) params.get(TemenosConstants.PARAM_CERT_PRIVATE_KEY);
		if (privateKey != null) {
			JWSSigner signer = new RSASSASigner(privateKey);

			signedJWT.sign(signer);

			/*
			 * JWEObject jweObject = new JWEObject( new
			 * JWEHeader.Builder(JWEAlgorithm.RSA_OAEP_256, EncryptionMethod.A256CBC_HS512)
			 * .contentType("JWT") // required to indicate nested JWT .build(), new
			 * Payload(signedJWT));
			 * 
			 * PublicKey encPublicKey = getPublicKey(encryptionCert); jweObject.encrypt(new
			 * RSAEncrypter((RSAPublicKey) encPublicKey));
			 */

			jwt = signedJWT.serialize();
		}
		return jwt;

	}
	
	private void addCustomAttributes(JWTClaimsSet.Builder JWTbulder, Map<String, Object> params) {
		JWTbulder.claim(TemenosConstants.PARAM_USER_ID, String.valueOf(params.get(TemenosConstants.PARAM_USERNAME)));
		JWTbulder.claim(TemenosConstants.PARAM_ROLE_ID, AuthConstants.TOKEN_ROLE_ID);
		JWTbulder.claim(TemenosConstants.PARAM_DBX_USER_ID, String.valueOf(params.get(TemenosConstants.PARAM_DBX_USER_ID)));
		JWTbulder.claim("_issmeta", String.valueOf(params.get("issuer")) + AuthConstants.AUTH_PUBLIC_KEY_METADATA);
	}

}
