/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.utils;

import com.google.gson.JsonObject;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.api.processor.manager.FabricResponseManager;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.infinity.tradesupplyfinance.config.TradeSupplyFinanceAlerts;
import com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.temenos.infinity.tradesupplyfinance.constants.TradeSupplyFinanceConstants.*;

/**
 * @author k.meiyazhagan
 */
public class TradeSupplyFinanceCommonUtils {

    private static final Logger LOG = Logger.getLogger(TradeSupplyFinanceCommonUtils.class);

    public static JSONObject mergeJSONObjects(JSONObject toJsonObj, JSONObject fromDataObject) {
        JSONObject mergedJSON = new JSONObject();
        try {
            mergedJSON = new JSONObject(toJsonObj, JSONObject.getNames(toJsonObj));
            for (String key : JSONObject.getNames(fromDataObject)) {
                mergedJSON.put(key, fromDataObject.get(key));
            }
        } catch (JSONException e) {
            LOG.error("Error occurred while merging JSON objects");
        }
        return mergedJSON;
    }

    public static String formatAmount(String amount) {
        double amountInDouble = Double.parseDouble(amount.replaceAll(",", ""));
        NumberFormat amountFormat = NumberFormat.getInstance();
        amountFormat.setMinimumFractionDigits(2);
        return amountFormat.format(amountInDouble);
    }

    public static String getAmountWithCurrency(String currency, String amount) {
        return StringUtils.isNotBlank(currency) && StringUtils.isNotBlank(amount)
                ? StringUtils.join(currency, " ", formatAmount(amount)) : "NA";
    }

    public static JSONObject getAccountsFromSession(String customerId) {
        String userAccountDetails = (String) MemoryManager.getFromCache("INTERNAL_BANK_ACCOUNTS" + customerId);
        return userAccountDetails != null ? new JSONObject(userAccountDetails) : new JSONObject();
    }

    public static String getAccountDetails(String customerId, String accountNumber, String key) {
        if (accountNumber != null) {
            JSONObject accountObject = getAccountsFromSession(customerId);
            if (accountObject.has(accountNumber))
                return accountObject.getJSONObject(accountNumber).optString(key);
        }
        return null;
    }

    public static String getMaskedAccountDetails(String customerId, String accountNumber) {
        String maskedAccount = "";
        try {
            String accountName = getAccountDetails(customerId, accountNumber, "accountName");
            accountName = accountName != null ? accountName : "";
            maskedAccount = StringUtils.join(accountName, "....", accountNumber.substring(accountNumber.length() - 4));
        } catch (Exception e) {
            LOG.error("Error in masking account details. ", e);
        }
        return maskedAccount;
    }

    public static String fetchCustomerIdFromSession(DataControllerRequest request) {
        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        return CustomerSession.getCustomerId(customer);
    }

    public static String fetchCustomerNameFromSession(DataControllerRequest request) {
        Map<String, Object> customer = CustomerSession.getCustomerMap(request);
        return CustomerSession.getCustomerName(customer);
    }

    public static String getCurrentDateTimeUTF() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIMESTAMP_FORMAT);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String formatDate(String dateInString, String fromDateFormat, String toDateFormat) {
        String result = "";
        try {
            Date date = new SimpleDateFormat(fromDateFormat).parse(dateInString);
            result = new SimpleDateFormat(toDateFormat).format(date);
        } catch (Exception e) {
            LOG.error("Error in formatting date. ", e);
        }
        return result;
    }

    public static String formatDocumentsList(String uploadedDocuments, String delimiter) {
        String result = "";
        try {
            JSONArray documentArr = new JSONArray(uploadedDocuments);
            String[] resultArr = new String[documentArr.length()];
            for (int i = 0; i < documentArr.length(); i++) {
                resultArr[i] = documentArr.getJSONObject(i).getString(PARAM_DOCUMENT_NAME);
            }
            result = String.join(delimiter, resultArr);
        } catch (Exception e) {
            LOG.error("Error in formatting documents. ", e);
        }
        return result;
    }

    public static void setAlertDataInResult(Result result, TradeSupplyFinanceAlerts alert, String orderId) {
        if (alert != null && StringUtils.isNotBlank(orderId)) {
            JSONObject alertsConfig = new JSONObject();
            alertsConfig.put(PARAM_ORDER_ID, orderId);
            alertsConfig.put(PARAM_ALERT_NAME, alert.name());
            result.addParam(PARAM_ALERT_DATA, String.valueOf(alertsConfig));
        }
    }

    public static boolean updateErrorResponse(FabricResponseManager fabricResponseManager, ErrorCodeEnum errEnum) {
        JsonObject resPayload = null;
        if (!HelperMethods.isJsonEleNull(fabricResponseManager.getPayloadHandler().getPayloadAsJson())) {
            resPayload = fabricResponseManager.getPayloadHandler().getPayloadAsJson().getAsJsonObject();
        }
        resPayload = errEnum.setErrorCode(resPayload);
        fabricResponseManager.getPayloadHandler().updatePayloadAsJson(resPayload);
        return false;
    }
}
