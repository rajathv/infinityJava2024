package com.temenos.infinity.api.wealth.preandpostprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealth.config.PortfolioWealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

/**
 * (INFO) Builds the Result in the desired format for the Transact service.
 * balaji.kk
 */

public class GetCashAccountsPostProcessor implements DataPostProcessor2 {

	private static final Logger LOG = LogManager.getLogger(GetCashAccountsPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		try {

			String[] portfolioIdArr = null;
			String portfolioId = "";

			portfolioIdArr = request.getParameterValues(TemenosConstants.PORTFOLIOID);
			if (portfolioIdArr != null && portfolioIdArr.length > 0) {
				portfolioId = portfolioIdArr[0].trim();
			}
			String totalCashBalance = "0.00", totalCashBalanceCurrency = "", currency = "", accountName = "",
					accountNumber = "", accountNameWithCurrency = "", accountNameHyphenWithCurrency = "";

			double cashBalance = 0.00;
			Record headerRec = result.getRecordById("header");

			JSONObject responseJSON = new JSONObject();
			String statusVal = headerRec.getParamValueByName("status");
			if (statusVal != null && statusVal.trim().equalsIgnoreCase("success")) {

				Dataset ds = result.getDatasetById("body");
				if (ds != null) {

					JSONObject resultObj = new JSONObject();
					resultObj.put("Field",
							Utilities.convertStringToJSONArray(ResultToJSON.convertDataset(ds).toString()));
					JSONArray cashAccountsArray = resultObj.getJSONArray("Field");

					JSONArray finalArr = new JSONArray();
					if (cashAccountsArray != null && cashAccountsArray.length() > 0) {
						String[] colArray = new String[] { "balance", "accountName", "referenceCurrencyValue",
								"currency", "accountNumber", "referenceCurrency" };

						for (int i = 0; i < cashAccountsArray.length(); i++) {
							JSONObject cashAccountsObj = cashAccountsArray.getJSONObject(i);
							JSONObject responseObj = new JSONObject();
							if (cashAccountsObj != null && cashAccountsObj.length() > 0) {
								responseObj = cashAccountsArray.getJSONObject(i);
								for (String key : colArray) {

									if (!responseObj.has(key)) {

										responseObj.put(key, "");
									}
									if (key.trim().equalsIgnoreCase("referenceCurrency")) {
										if (responseObj.get(key).toString().length() > 0) {
											totalCashBalanceCurrency = responseObj.get("referenceCurrency").toString();

										}
									}

								}

								responseObj.remove("referenceCurrency");

								if ((responseObj.get("accountName").toString().length() > 0)
										&& (responseObj.get("currency").toString().length() > 0)
										&& (responseObj.get("accountNumber").toString().length() > 0)) {

									String amountInRefCurrency = responseObj.get("referenceCurrencyValue").toString()
											.trim().length() > 0 ? responseObj.get("referenceCurrencyValue").toString()
													: "0";

									cashBalance = Double.parseDouble(totalCashBalance)
											+ Double.parseDouble(amountInRefCurrency);
									totalCashBalance = String.format("%.2f", cashBalance);

									currency = responseObj.get("currency").toString();
									accountName = responseObj.get("accountName").toString();
									accountNumber = responseObj.get("accountNumber").toString();
									if (accountName.length() > 26) {
										if (accountNumber.length() >= 4) {
											accountNameWithCurrency = currency + " " + accountName.substring(0, 23)
													+ "..." + accountNumber.substring((accountNumber.length() - 4),
															accountNumber.length());
											accountNameHyphenWithCurrency = currency + " "
													+ accountName.substring(0, 23) + "-" + accountNumber.substring(
															(accountNumber.length() - 4), accountNumber.length());
										} else {

											accountNameWithCurrency = currency + " " + accountName.substring(0, 23)
													+ "..." + accountNumber;
											accountNameHyphenWithCurrency = currency + " "
													+ accountName.substring(0, 23) + "-" + accountNumber;

										}

									} else {
										if (accountNumber.length() >= 4) {
											accountNameWithCurrency = currency + " " + accountName + "..."
													+ accountNumber.substring((accountNumber.length() - 4),
															accountNumber.length());

											accountNameHyphenWithCurrency = currency + " " + accountName + "-"
													+ accountNumber.substring((accountNumber.length() - 4),
															accountNumber.length());
										} else {
											accountNameWithCurrency = currency + " " + accountName + "..."
													+ accountNumber;

											accountNameHyphenWithCurrency = currency + " " + accountName + "-"
													+ accountNumber;
										}
									}

									responseObj.put("accountNameWithCurrency", accountNameWithCurrency);
									responseObj.put("accountNameHyphenWithCurrency", accountNameHyphenWithCurrency);

									finalArr.put(responseObj);
								}
							}

						}
						responseJSON.put("opstatus", "0");
						responseJSON.put("totalCashBalance", totalCashBalance);
						responseJSON.put("totalCashBalanceCurrency", totalCashBalanceCurrency);
						responseJSON.put("portfolioID", portfolioId);
						responseJSON.put("httpStatusCode", "200");
						responseJSON.put("cashAccounts", finalArr);
						responseJSON.put("status", statusVal);

					}

				}
			} else {

				Record errorRec = result.getRecordById("error");
				if (errorRec != null) {
					Record error = errorRec.getAllRecords().get(0);
					responseJSON.put("errormessage", error.getParamValueByName("message"));
				} else {
					responseJSON.put("errormessage", "");
				}
				responseJSON.put("status", statusVal);
			}
			return Utilities.constructResultFromJSONObject(responseJSON);
		} catch (Exception e) {

			LOG.error("Error while invoking GetOrderDetailsPostProcessor - "
					+ PortfolioWealthAPIServices.WEALTHSERVICESORCHESTRATION.getOperationName() + "  : " + e);
		}

		return result;
	}

}
