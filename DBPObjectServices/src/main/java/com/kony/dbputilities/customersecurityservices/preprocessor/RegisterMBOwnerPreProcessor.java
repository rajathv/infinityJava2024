package com.kony.dbputilities.customersecurityservices.preprocessor;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonObject;
import com.kony.dbputilities.mfa.MFAConstants;
import com.kony.dbputilities.util.AdminUtil;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.konylabs.middleware.api.processor.FabricRequestChain;
import com.konylabs.middleware.api.processor.PayloadHandler;
import com.konylabs.middleware.api.processor.manager.FabricRequestManager;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.common.objectservice.ObjectServicePreProcessor;
import com.konylabs.middleware.dataobject.Result;

public class RegisterMBOwnerPreProcessor implements ObjectServicePreProcessor {
	private static final Logger LOG = LogManager.getLogger(RegisterMBOwnerPreProcessor.class);

	@Override
	public void execute(FabricRequestManager requestManager, FabricResponseManager responseManager,
			FabricRequestChain requestChain) throws Exception {

		Map<String, String> headerMap = new HashMap<>();
		Map<String, String> inputMap = new HashMap<>();
		responseManager.getHeadersHandler().addHeader(HttpHeaders.CONTENT_TYPE,
				ContentType.APPLICATION_JSON.getMimeType());
		String userName = "";
		String password = "";
		PayloadHandler requestPayloadHandler = requestManager.getPayloadHandler();
		PayloadHandler responsePayloadHandler = responseManager.getPayloadHandler();

		JsonObject resultJson = new JsonObject();
		JsonObject requestpayload = requestPayloadHandler.getPayloadAsJson().getAsJsonObject();
		if (requestpayload.get("UserName") != null) {
			userName = requestpayload.get("UserName").getAsString();
		}
		if (requestpayload.get("Password") != null) {
			password = requestpayload.get("Password").getAsString();
		}

		JsonObject rules = AdminUtil.invokeAPIAndGetJson(inputMap, headerMap,
				URLConstants.ADMIN_USERNAME_PASSWORD_RULES, requestManager);
		Map<String, String> usernameRules = HelperMethods.getRecordMap(rules.get("usernamerules").toString());
		Map<String, String> passwordRules = HelperMethods.getRecordMap(rules.get("passwordrules").toString());

		if (StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(password)) {
			if (validateUsername(userName, usernameRules, resultJson)
					&& ValidatePassword(password, passwordRules, resultJson)) {
			} else {
				responsePayloadHandler.updatePayloadAsJson(resultJson);
			}
		} else if (StringUtils.isNotBlank(userName)) {
			if (validateUsername(userName, usernameRules, resultJson)) {
			} else {
				responsePayloadHandler.updatePayloadAsJson(resultJson);
			}
		} else if (StringUtils.isNotBlank(password)) {
			if (ValidatePassword(password, passwordRules, resultJson)) {
			} else {
				responsePayloadHandler.updatePayloadAsJson(resultJson);
			}
		} else {
			ErrorCodeEnum.ERR_10120.setErrorCode(resultJson);
			resultJson.addProperty(DBPConstants.FABRIC_OPSTATUS_KEY, 0);
			resultJson.addProperty(DBPConstants.FABRIC_HTTP_STATUS_CODE_KEY, 0);
			responsePayloadHandler.updatePayloadAsJson(resultJson);
			return;
		}
		if (resultJson.has(DBPConstants.DBP_ERROR_CODE_KEY) || resultJson.has(DBPConstants.DBP_ERROR_MESSAGE_KEY)) {
			return;
		}

		JsonObject jsonObject = requestManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
		resultJson = new JsonObject();
		String serviceKey = jsonObject.get("serviceKey").getAsString();
		String serviceName = MFAConstants.SERVICE_ID_PRELOGIN;

		if (StringUtils.isNotBlank(serviceKey)
				&& isStateVerified(requestManager, jsonObject, serviceKey, serviceName, resultJson)) {
			requestChain.execute();
			return;
		}
		ErrorCodeEnum.ERR_10584.setErrorCode(resultJson);
		responseManager.getPayloadHandler().updatePayloadAsJson(resultJson);
	}

	private boolean validateUsername(String userName, Map<String, String> usernameRules, JsonObject resultJson) {

		int userMinLength = (usernameRules.get("minLength") != null ? Integer.parseInt((usernameRules.get("minLength")))
				: 8);
		Boolean symbolsAllowed = (usernameRules.get("symbolsAllowed") != null
				? Boolean.parseBoolean((usernameRules.get("symbolsAllowed")))
				: true);
		int userMaxLength = (usernameRules.get("maxLength") != null ? Integer.parseInt((usernameRules.get("maxLength")))
				: 64);
		String supportedSymbols = (usernameRules.get("supportedSymbols") != null
				? (usernameRules.get("supportedSymbols"))
				: ".,-,_,@,!,#,$");
		String givenSupportedSymbols = supportedSymbols;

		LOG.error("supported symbols before: " + supportedSymbols);
		supportedSymbols = supportedSymbols.replaceAll(",", "");
		if (supportedSymbols.contains("-")) {
			supportedSymbols = supportedSymbols.replace("-", "\\-");
		}
		LOG.error("supported symbols after: " + supportedSymbols);

		if (StringUtils.isBlank(userName)) {
			ErrorCodeEnum.ERR_10121.setErrorCode(resultJson);
			return false;
		}
		if (!(userName.length() >= userMinLength && userName.length() <= userMaxLength)) {
			ErrorCodeEnum.ERR_10122.setErrorCode(resultJson,
					" Username length should be in between the limits " + userMinLength + " and " + userMaxLength);
			return false;
		}
		if (symbolsAllowed) {
			Pattern newpattern = Pattern.compile("^(?=.*[^\\s\\w\\d" + supportedSymbols + "])");
			Matcher newm = newpattern.matcher(userName);
			if (newm.find()) {
				ErrorCodeEnum.ERR_10124.setErrorCode(resultJson, "special characters other than these "
						+ givenSupportedSymbols + " are not allowed for username");
				return false;
			}
		} else {
			Pattern pattern = Pattern.compile("[$&+,:;=?@#|'<>.^*()%!-]");
			Matcher m = pattern.matcher(userName);
			if (m.find()) {
				ErrorCodeEnum.ERR_10153.setErrorCode(resultJson,
						"Special Characters should not be included in your Username.");
				return false;
			}

		}
		return true;

	}

	private boolean ValidatePassword(String password, Map<String, String> passwordRules, JsonObject resultJson) {
		int passMinLength = (passwordRules.get("minLength") != null ? Integer.parseInt((passwordRules.get("minLength")))
				: 8);
		int passMaxLength = (passwordRules.get("maxLength") != null ? Integer.parseInt((passwordRules.get("maxLength")))
				: 64);
		Boolean lowerCase = (passwordRules.get("atleastOneLowerCase") != null
				? Boolean.parseBoolean((passwordRules.get("maxLength")))
				: true);
		Boolean upperCase = (passwordRules.get("atleastOneUpperCase") != null
				? Boolean.parseBoolean((passwordRules.get("atleastOneUpperCase")))
				: true);
		Boolean number = (passwordRules.get("atleastOneNumber") != null
				? Boolean.parseBoolean((passwordRules.get("atleastOneNumber")))
				: true);
		Boolean symbol = (passwordRules.get("atleastOneSymbol") != null
				? Boolean.parseBoolean((passwordRules.get("atleastOneSymbol")))
				: true);
		int charRepeatCount = (passwordRules.get("charRepeatCount") != null
				? Integer.parseInt((passwordRules.get("charRepeatCount")))
				: 4);
		String supportedSymbols = (passwordRules.get("supportedSymbols") != null
				? (passwordRules.get("supportedSymbols"))
				: ".,-,_,@,!,#,$");
		String givenSupportedSymbols = supportedSymbols;

		supportedSymbols = supportedSymbols.replaceAll(",", "");
		if (supportedSymbols.contains("-")) {
			supportedSymbols = supportedSymbols.replace("-", "\\-");
		}

		if (StringUtils.isBlank(password)) {
			ErrorCodeEnum.ERR_10125.setErrorCode(resultJson);
			return false;
		}
		if (!(password.length() >= passMinLength && password.length() <= passMaxLength)) {
			ErrorCodeEnum.ERR_10126.setErrorCode(resultJson,
					" password length should be in between the limits " + passMinLength + " and " + passMaxLength);
			return false;
		}
		if (lowerCase) {
			Pattern pattern = Pattern.compile("^(?=[^a-z]*[a-z])");
			Matcher m = pattern.matcher(password);
			if (!m.lookingAt()) {
				ErrorCodeEnum.ERR_10127.setErrorCode(resultJson);
				return false;
			}
		}
		if (upperCase) {
			Pattern pattern = Pattern.compile("^(?=[^A-Z]*[A-Z])");
			Matcher m = pattern.matcher(password);
			if (!m.lookingAt()) {
				ErrorCodeEnum.ERR_10128.setErrorCode(resultJson);
				return false;
			}
		}
		if (number) {
			Pattern pattern = Pattern.compile("^(?=\\D*\\d)");
			Matcher m = pattern.matcher(password);
			if (!m.lookingAt()) {
				ErrorCodeEnum.ERR_10129.setErrorCode(resultJson);
				return false;
			}
		}
		if (symbol) {
			Pattern pattern = Pattern.compile("^(?=.*[" + supportedSymbols + "])");
			Matcher m = pattern.matcher(password);
			if (!m.lookingAt()) {
				ErrorCodeEnum.ERR_10130.setErrorCode(resultJson);
				return false;
			}
			pattern = Pattern.compile("^(?=.*[^\\s\\w\\d" + supportedSymbols + "])");
			m = pattern.matcher(password);
			if (m.find()) {
				ErrorCodeEnum.ERR_10131.setErrorCode(resultJson, "special characters other than these "
						+ givenSupportedSymbols + " are not allowed for password");
				return false;
			}

		}
		if (charRepeatCount > 0) {
			Pattern pattern = Pattern.compile("(.)\\1{" + charRepeatCount + ",}");
			Matcher m = pattern.matcher(password);
			if (m.find()) {
				ErrorCodeEnum.ERR_10132.setErrorCode(resultJson,
						"Maximum number of times a character can be repeated consecutively in the password is "
								+ charRepeatCount);
				return false;
			}
		}

		return true;

	}

	public boolean isStateVerified(FabricRequestManager requestManager, JsonObject inputJson, String serviceKey,
			String serviceName, JsonObject resultJson) {
		LOG.error("Verifying serviceKey");
		Result result = new Result();
		Result OTP = new Result();
		String filter = MFAConstants.SERVICE_KEY + DBPUtilitiesConstants.EQUAL + serviceKey;
		try {
			result = HelperMethods.callGetApi(requestManager, filter, HelperMethods.getHeaders(requestManager),
					URLConstants.MFA_SERVICE_GET);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		try {
			OTP = HelperMethods.callGetApi(requestManager, filter, HelperMethods.getHeaders(requestManager),
					URLConstants.OTP_GET);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		if (HelperMethods.hasRecords(result) && StringUtils.isNotBlank(HelperMethods.getFieldValue(result, "User_id"))
				&& !HelperMethods.hasRecords(OTP)) {
			if (!isServiceKeyExpired(requestManager, result)) {
				return true;
			}
		}
		return false;
	}

	private boolean isServiceKeyExpired(FabricRequestManager requestManager, Result result) {

		String string = HelperMethods.getFieldValue(result, "Createddts");

		if (StringUtils.isNotBlank(string)) {
			Date createdts = HelperMethods.getFormattedTimeStamp(string);
			Calendar generatedCal = Calendar.getInstance();
			generatedCal.setTime(createdts);

			Date verifyDate = new Date();
			Calendar verifyingCal = Calendar.getInstance();
			verifyingCal.setTime(verifyDate);
			int otpValidityTime = getServiceKeyExpiretime(requestManager);
			generatedCal.add(Calendar.MINUTE, otpValidityTime);

			long GeneratedMilliSeconds = generatedCal.getTimeInMillis();
			long verifyingMilliSeconds = verifyingCal.getTimeInMillis();

			if (GeneratedMilliSeconds > verifyingMilliSeconds) {
				return false;
			}
		}
		return true;
	}

	private int getServiceKeyExpiretime(FabricRequestManager requestManager) {
		try {
			return Integer.parseInt(URLFinder.getPathUrl(URLConstants.SERVICEKEY_EXPIRE_TIME, requestManager));
		} catch (Exception e) {
		}
		return 10;
	}

}