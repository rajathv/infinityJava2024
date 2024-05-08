package com.temenos.infinity.api.docmanagement.acctstatement.javaservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.Base64;
import com.kony.dbputilities.fileutil.FileGenerator;
import com.kony.dbputilities.fileutil.FileGeneratorFactory;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.konylabs.middleware.api.events.EventData;
import com.konylabs.middleware.api.events.EventSubscriber;
import com.konylabs.middleware.api.events.IntegrationEventSubscriber;
import com.temenos.dbx.product.constants.Constants;

@IntegrationEventSubscriber(topics = { "/events/combinedstatement" })
public class CombinedStatementEvent implements EventSubscriber {

	private static final Logger LOG = LogManager.getLogger(CombinedStatementEvent.class);

	@Override
	public void onEvent(EventData eventData) {
		LOG.info("Combined statement event Triggered");
		String data = eventData.getData().toString();
		JSONObject dataObject;
		String fileId = "";
		String authToken = null;
		try {
			dataObject = new JSONObject(data);
			JSONObject eventObject = dataObject.getJSONObject("events");
			JSONObject eventDataObject = eventObject.getJSONObject("eventData");
			String accounts = eventDataObject.has("accountIds") ? eventDataObject.get("accountIds").toString() : null;

			if (StringUtils.isBlank(accounts)) {
				return;
			}
			String fromDate = eventDataObject.has("fromDate") ? eventDataObject.get("fromDate").toString() : null;
			String toDate = eventDataObject.has("toDate") ? eventDataObject.get("toDate").toString() : null;
			String userId = eventDataObject.has("userId") ? eventDataObject.get("userId").toString() : null;
			String fileType = eventDataObject.has("fileType") ? eventDataObject.get("fileType").toString() : null;
			String currencyCode = eventDataObject.has("currencyCode") ? eventDataObject.get("currencyCode").toString()
					: null;
			String paymentDateFormat = eventDataObject.has("dateFormat") ? eventDataObject.get("dateFormat").toString()
					: null;

			String generatedBy = eventDataObject.has("generatedBy") ? eventDataObject.get("generatedBy").toString()
					: null;
			String customerType = eventDataObject.has("customerType") ? eventDataObject.get("customerType").toString() : null;
			String companyId = eventDataObject.has("companyId") ? eventDataObject.get("companyId").toString() : "";
			String bankName = eventDataObject.has("bankName") ? eventDataObject.get("bankName").toString() : null;
			fileId = eventDataObject.has("id") ? eventDataObject.get("id").toString() : null;

			// Get Auth Token
			HashMap<String, Object> identityParam = new HashMap<String, Object>();
			Map<String, Object> identitytHeaders = new HashMap<>();
			String dbpAppKey = EnvironmentConfigurationsHandler.getValue("DBP_CORE_APPKEY");
			String dbpAppSecret = EnvironmentConfigurationsHandler.getValue("DBP_CORE_SECRET");
			String sharedSecret = EnvironmentConfigurationsHandler.getValue("DBP_CORE_SHARED_SECRET");
			identitytHeaders.put("X-Kony-App-Key", dbpAppKey);
			identitytHeaders.put("X-Kony-App-Secret", dbpAppSecret);
			identitytHeaders.put("x-kony-dbp-api-access-token", sharedSecret);
			if (StringUtils.isBlank(dbpAppKey) || StringUtils.isBlank(dbpAppSecret)
					|| StringUtils.isBlank(sharedSecret)) {
				LOG.error("Error while file fetching DBP_CORE_APPKEY or DBP_CORE_SECRET or DBP_CORE_SHARED_SECRET");
			}
			JsonObject identityResult = ServiceCallHelper.invokeServiceAndGetJson(identityParam, identitytHeaders,
					"dm.api.login");
			if (identityResult.has("claimsToken")) {
				authToken = identityResult.get("claimsToken").getAsString();
			} else {
				LOG.error("Error while file fetching auth Token in combined statements");
				return;
			}

			JSONArray accountList = new JSONArray(accounts);

			JsonArray transactionsArray = new JsonArray();

			HashMap<String, Object> failureUpdateParam = new HashMap<String, Object>();
			Map<String, Object> failureUpdateHeader = new HashMap<>();
			failureUpdateHeader.put(DBPUtilitiesConstants.X_KONY_AUTHORIZATION, authToken);
			failureUpdateParam.put("id", fileId);
			failureUpdateParam.put("status", Constants.STATUS_FAIL);

			for (Object individualAccountIdObj : accountList) {
				String individualAccountId = individualAccountIdObj.toString();
				// Fetch Account Details
				HashMap<String, Object> accountsParam = new HashMap<String, Object>();
				accountsParam.put("accountID", individualAccountId);
				accountsParam.put("customerID", userId);
				accountsParam.put("customerType",customerType);
				accountsParam.put("companyId",companyId);
				Map<String, Object> accountHeaders = new HashMap<>();
				accountHeaders.put(DBPUtilitiesConstants.X_KONY_AUTHORIZATION, authToken);
				JsonObject accountJsonObject = ServiceCallHelper.invokeServiceAndGetJson(accountsParam, accountHeaders,
						URLConstants.GET_COMBINED_STATEMENT_ACCOUNT_DETAILS);
				String accountName = "";
				String accountCurrencyCode = "";
				String nickName = "";

				if (accountJsonObject.has("accountName") && accountJsonObject.get("accountName") != null) {
					accountName = accountJsonObject.get("accountName").getAsString();
				}
				if (accountJsonObject.has("currencyCode") && accountJsonObject.get("currencyCode") != null) {
					accountCurrencyCode = accountJsonObject.get("currencyCode").getAsString();
				}
				if (accountJsonObject.has("nickName") && accountJsonObject.get("nickName") != null) {
					nickName = accountJsonObject.get("nickName").getAsString();
				}

				// fetch Transactions
				JsonObject individualAccountTransaction = new JsonObject();
				HashMap<String, Object> transactionsParam = new HashMap<String, Object>();
				transactionsParam.put("accountNumber", individualAccountId);
				transactionsParam.put("searchTransactionType", "Both");
				transactionsParam.put("searchStartDate", fromDate);
				transactionsParam.put("searchEndDate", toDate);
				transactionsParam.put("isScheduled", "0");
				transactionsParam.put("searchType", "Search");
				transactionsParam.put("User_id", userId);
				transactionsParam.put("companyId",companyId);
				Map<String, Object> headers = new HashMap<>();
				headers.put(DBPUtilitiesConstants.X_KONY_AUTHORIZATION, authToken);
				JsonObject transactionObject = ServiceCallHelper.invokeServiceAndGetJson(transactionsParam, headers,
						URLConstants.TRANSACTIONS_POST);

				if (transactionObject.has(Constants.OPSTATUS) && transactionObject.get(Constants.OPSTATUS) != null
						&& !transactionObject.get(Constants.OPSTATUS).toString().equalsIgnoreCase("0")) {
					failureUpdateParam.put("failureMessage",
							"Error while fetching transaction for accounId" + individualAccountId);
					updateAccountStatement(failureUpdateParam, failureUpdateHeader);
					return;
				}
				if (transactionObject.has(ErrorCodeEnum.ERROR_CODE_KEY)
						&& transactionObject.get(ErrorCodeEnum.ERROR_CODE_KEY) != null
						&& StringUtils.isNotBlank(transactionObject.get(ErrorCodeEnum.ERROR_CODE_KEY).toString())) {
					failureUpdateParam.put("failureMessage",
							"Error while fetching transaction for accounId" + individualAccountId);
					updateAccountStatement(failureUpdateParam, failureUpdateHeader);
					return;
				}

				// generate object
				if (transactionObject.has("Transactions")) {
					individualAccountTransaction.add("transactionsList",
							transactionObject.get("Transactions").getAsJsonArray());
				}
				individualAccountTransaction.addProperty("accountName", accountName);
				individualAccountTransaction.addProperty("accountNumber", individualAccountId);
				individualAccountTransaction.addProperty("nickName", nickName);
				individualAccountTransaction.addProperty("currencySymbol", getCurrencySymbol(accountCurrencyCode));
				// add into final Array
				transactionsArray.add(individualAccountTransaction);
			}
			String base64 = GenerateFile(transactionsArray, fileType, fromDate, toDate, currencyCode, paymentDateFormat,
					generatedBy, bankName);

			if (StringUtils.isBlank(base64)) {
				failureUpdateParam.put("failureMessage", "Error while generating combinedstatement file");
				updateAccountStatement(failureUpdateParam, failureUpdateHeader);
				return;

			}
			HashMap<String, Object> updateParam = new HashMap<String, Object>();
			Map<String, Object> updateHeader = new HashMap<>();
			updateHeader.put(DBPUtilitiesConstants.X_KONY_AUTHORIZATION, authToken);
			updateParam.put("id", fileId);
			updateParam.put("fileContent", base64);
			updateParam.put("status", Constants.STATUS_SUCCESS);
			updateAccountStatement(updateParam, updateHeader);

		} catch (Exception e) {
			LOG.error("Error getting generating combined statements", e);
			HashMap<String, Object> failureUpdateParam = new HashMap<String, Object>();
			Map<String, Object> failureUpdateHeader = new HashMap<>();
			failureUpdateHeader.put(DBPUtilitiesConstants.X_KONY_AUTHORIZATION, authToken);
			failureUpdateParam.put("id", fileId);
			failureUpdateParam.put("status", Constants.STATUS_FAIL);
			failureUpdateParam.put("failureMessage", e.getMessage());
			try {
				updateAccountStatement(failureUpdateParam, failureUpdateHeader);
			} catch (Exception exception) {
				LOG.error("Error while updating the database status ", exception);
			}
		}
	}

	public static void updateAccountStatement(HashMap<String, Object> updateParam, Map<String, Object> updateHeader) {
		ServiceCallHelper.invokeServiceAndGetJson(updateParam, updateHeader,
				URLConstants.ACCOUNTS_STATEMENT_FILES_UPDATE);

	}

	private String GenerateFile(JsonArray transactionsArray, String fileType, String startDate, String endDate,
			String currencyCode, String paymentDateFormat, String generatedBy, String bankName) {
		FileGenerator generator = FileGeneratorFactory.getFileGenerator(fileType);
		String title = "Combined Statement";
		String currencySymbol = getCurrencySymbol(currencyCode);
		byte[] bytes = null;
		try {
			bytes = generator.generateCombinedStatementsFile(transactionsArray, title, generatedBy, startDate, endDate,
					getFieldList(), bankName, currencySymbol, paymentDateFormat);
		} catch (Exception e) {
			LOG.error("Error while generating combined statement file");
			return null;
		}
		//String encodedText = Base64.encodeBytes(bytes);
		String encodedText = Base64.getEncoder().encodeToString(bytes);
		return encodedText;
	}

	public String getCurrencySymbol(String currencyCode) {

		switch (currencyCode) {

		case "USD":
			return "$";
		case "EUR":
			return "€"; // Euro
		case "CRC":
			return "₡"; // Costa Rican Colón
		case "GBP":
			return "£"; // British Pound Sterling
		case "ILS":
			return "₪";// Israeli New Sheqel
		case "INR":
			return "₹"; // Indian Rupee
		case "JPY":
			return "¥"; // Japanese Yen
		case "KRW":
			return "₩"; // South Korean Won
		case "NGN":
			return "₦"; // Nigerian Naira
		case "PHP":
			return "₱"; // Philippine Peso
		case "PLN":
			return "zł"; // Polish Zloty
		case "PYG":
			return "₲"; // Paraguayan Guarani
		case "THB":
			return "฿"; // Thai Baht
		case "UAH":
			return "₴"; // Ukrainian Hryvnia
		case "VND":
			return "₫"; // Vietnamese Dong
		case "AUD":
			return "$"; // Australian Dollar
		case "CAD":
			return "$"; // Canadian Dollar
		case "CHF":
			return "Fr."; // Swiss Franc
		}
		return "";
	}

	private Map<String, String> getFieldList() {
		String resourceName = "CombinedStatementsHeader.properties";
		Map<String, String> fieldList = new LinkedHashMap<>();
		try (InputStream is = URLFinder.class.getClassLoader().getResourceAsStream(resourceName);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));) {
			String line = null;
			String[] words = null;
			while ((line = reader.readLine()) != null) {
				if (!line.startsWith("#")) {
					words = line.split("=");
					fieldList.put(words[0], words[1]);
				}
			}
		} catch (IOException e) {
			LOG.error("Error on reading CombinedStatementsHeader.properties..." + e);
		}
		return fieldList;
	}
}
