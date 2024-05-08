/**
 * 
 */
package com.temenos.infinity.api.wealthservices.util;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.infinity.dbx.temenos.auth.AuthCertificate;
import com.infinity.dbx.temenos.auth.AuthConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.temenos.infinity.api.wealthservices.constants.T24CertificateConstants;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealthservices.vo.TokenVO;

/**
 * @author himaja.sridhar
 *
 */
public class JWTTokenUtil {
	private static final Logger LOG = Logger.getLogger(JWTTokenUtil.class);

	public static String computeJWTToken(TokenVO tokenVO, DataControllerRequest request) throws Exception {
		try {
			String backendToken = "";
			String userName = tokenVO.getUserName();
			String userId = tokenVO.getUserId();
			String hostURL = tokenVO.getHostURL();
			String roleId = tokenVO.getRoleId();
			Long validity = tokenVO.getValidityInMinutes();

			HashMap<String, Object> jwtParamsMap = new HashMap<>();

			jwtParamsMap.put(TemenosConstants.PARAM_USERNAME, userName);
			jwtParamsMap.put(TemenosConstants.PARAM_DBX_USER_ID, userId);
			jwtParamsMap.put(TemenosConstants.PARAM_ROLE_ID, roleId);
			jwtParamsMap.put(TemenosConstants.VALIDITY, validity);

			AuthCertificate authCertificate = AuthCertificate.getInstance();

			PrivateKey privateKey = authCertificate.getPrivateKey(request);
			if (privateKey == null) {
				return backendToken;
			}

			jwtParamsMap.put(TemenosConstants.PARAM_CERT_PRIVATE_KEY, privateKey);

			if (StringUtils.isBlank(hostURL)) {
				LOG.error("DBP_HOST_URL is not specified");
				return backendToken;
			}
			jwtParamsMap.put("issuer", hostURL);
			backendToken = generateToken(jwtParamsMap);

			if (StringUtils.isBlank(backendToken)) {
			}
			//LOG.error("JWT TOKEN" + backendToken);
			//backendToken = "Bearer ".concat(backendToken);
			return backendToken;
		} catch (Exception ex) {
			LOG.error("==========> Exception occurred while generating token: " + ex.getMessage());
		}
		return null;
	}

	public static TokenVO setParamsForToken(DataControllerRequest request) {
		TokenVO tokenVO = new TokenVO();

		String userId = PortfolioWealthUtils.getUserAttributeFromIdentity(request, "UserName");
		tokenVO.setUserId(userId); // session

		String userName = PortfolioWealthUtils.getUserAttributeFromIdentity(request, "customer_id");
		tokenVO.setUserName(userName); // session

		tokenVO.setAppName(T24CertificateConstants.BACKEND); // constant
		tokenVO.setRoleId(T24CertificateConstants.ROLEID); // constant

		String tap_DBP_HOST_URL = EnvironmentConfigurationsHandler.getValue(TemenosConstants.DBX_HOST_URL,
				request) != null
						? EnvironmentConfigurationsHandler.getValue(TemenosConstants.DBX_HOST_URL, request).toString()
								.trim()
						: "";
		tokenVO.setHostURL(tap_DBP_HOST_URL); // env variable

		long Validity = request.getParameter("validityInMinutes") == null ? T24CertificateConstants.TOKEN_VALIDITY
				: Long.parseLong(request.getParameter("validityInMinutes").toString())*60;
		// if passed take it, else 30 minutes
		tokenVO.setValidityInMinutes(Validity);
		return tokenVO;
	}

	private static String generateToken(Map<String, Object> params) throws Exception {
		String jwt = "";
		JWTClaimsSet.Builder JWTbulder = new JWTClaimsSet.Builder().issuer(AuthConstants.TOKEN_ISSUER)
				.audience(AuthConstants.TOKEN_AUDIENCE)
				.subject(String.valueOf(params.get(TemenosConstants.PARAM_USERNAME)))
				.jwtID(UUID.randomUUID().toString()).issueTime(Date.from(Instant.now()))
				.expirationTime(Date.from(Instant.ofEpochSecond(Instant.now().getEpochSecond() + 1800)));

		addCustomAttributes(JWTbulder, params);

		JWTClaimsSet claimsSet = JWTbulder.build();
		JOSEObjectType joseObjectType = new JOSEObjectType("JWT");
		SignedJWT signedJWT = new SignedJWT(
				new JWSHeader.Builder(JWSAlgorithm.RS256).keyID("KONY").type(joseObjectType).build(), claimsSet);

		PrivateKey privateKey = (PrivateKey) params.get(TemenosConstants.PARAM_CERT_PRIVATE_KEY);
		if (privateKey != null) {
			JWSSigner signer = new RSASSASigner(privateKey);

			signedJWT.sign(signer);
			jwt = signedJWT.serialize();
		}
		return jwt;

	}

	private static void addCustomAttributes(JWTClaimsSet.Builder JWTbulder, Map<String, Object> params) {
		JWTbulder.claim(TemenosConstants.PARAM_USER_ID, String.valueOf(params.get(TemenosConstants.PARAM_USERNAME)));
		JWTbulder.claim(TemenosConstants.PARAM_ROLE_ID, params.get("roleId").toString());
		JWTbulder.claim(TemenosConstants.PARAM_DBX_USER_ID,
				String.valueOf(params.get(TemenosConstants.PARAM_DBX_USER_ID)));
		JWTbulder.claim("_issmeta",
				String.valueOf(params.get("issuer")) + "/services/WealthOrderServices/getPublicKey");
	}
}
