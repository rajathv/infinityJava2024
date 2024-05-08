package com.temenos.infinity.api.wealthOrder.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.temenos.infinity.api.wealthOrder.backenddelegate.impl.CurrencyBackendDelegateImpl;
import com.temenos.infinity.api.wealthOrder.backenddelegate.impl.HistoricalDataBackendDelegateImpl;
import com.temenos.infinity.api.wealthOrder.backenddelegate.impl.HoldingsListBackendDelegateImpl;
import com.temenos.infinity.api.wealthOrder.backenddelegate.impl.PortfolioDetailsBackendDelegateImpl;
import com.temenos.infinity.api.wealthOrder.backenddelegate.impl.TransactionsListBackendDelegateImpl;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;

public class WealthMockUtil {

	public JSONObject cancelOrder(Map<String, Object> inputMap) throws ParseException {
		String portfolioId = (String) inputMap.get(TemenosConstants.PORTFOLIOID);
		String orderId = (String) inputMap.get(TemenosConstants.ORDER_ID);
		JSONObject response = new JSONObject();

		response.put("portfolioId", portfolioId);
		response.put("orderId", orderId);
		response.put("opstatus", "0");
		response.put("httpStatusCode", "200");
		return response;
	}

	public JSONObject mockForexOrders(Map<String, Object> inputMap) {

		try {

			JSONObject response = new JSONObject();
			if (inputMap.get(TemenosConstants.VALIDATEONLY) != null) {
				boolean validate_only = Boolean.parseBoolean(inputMap.get(TemenosConstants.VALIDATEONLY).toString());
				if (validate_only) {
					JSONObject feesObj = new JSONObject();
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put("safekeepChargeInTradeCurrency", "19.69565");
					hm.put("safekeepChargeInChargeCurrency", "19.69565");
					hm.put("InducementFeesInChargeCurrency", "16.3085");
					hm.put("InducementFeesInTradeCurrency", "16.3085");
					hm.put("advisoryFeesInChargeCurrency", "18.8175");
					hm.put("advisoryFeesInTradeCurrency", "18.8175");
					hm.put("tradeCurrency", "USD");
					hm.put("chargeCurrency", "USD");
					hm.forEach((key, value) -> feesObj.put(key, value));
					response.put("feeDetails", feesObj);

					//JSONObject messageObj = new JSONObject();
					//JSONArray messageArray = new JSONArray();
					//messageObj.put("id", "SC.ORD.DATE.GT.TODAY");
					//messageObj.put("message", "Message:ORDER DATE GREATER THAN TODAY");
					//messageArray.put(messageObj);
					//response.put("messageDetails", messageArray.toString());
				}
			}

			response.put("id", "FX" + CurrencyBackendDelegateImpl.getUniqueNumber());
			response.put("fees", "31.36");
			response.put("status", "success");
			response.put("uniqueIdentifier", "SEAT" + CurrencyBackendDelegateImpl.getUniqueNumber() + ".00");
			response.put("opstatus", "0");
			response.put("httpStatusCode", "200");

			return response;
		} catch (Exception e) {
			return null;
		}

	}

	public JSONArray getAddCurrency() {

		String currencyCode[] = { "AED", "BHD", "BRL", "CAD", "CHF", "CNY", "HKD", "KWD", "SGD", "YEN" };
		String currencyVal[] = { "United Arab Emirates Dirham", "Bahrain Dinar", "Brazil Real", "Canada Dollar",
				"Switzerland Franc", "Chinese Yuan", "Hong Kong Dollar", "Kuwait Dinar", "Singapore Dollar",
				"Japanese Yen" };
		JSONArray addCurrency = new JSONArray();

		for (int i = 0; i < currencyCode.length; i++) {
			JSONObject responseObj = new JSONObject();
			responseObj.put("CurrencyCode", currencyCode[i]);
			responseObj.put("CurrencyValue", currencyVal[i]);
			addCurrency.put(responseObj);
		}

		return addCurrency;
	}

	/**
	 * (INFO) Fetches the mock cash balance for the chosen portfolio. To alter the
	 * value, change the "currency","balance" etc cash Obj. Can add further Cash
	 * Accounts.
	 * 
	 * @param inputMap
	 * @return {@link JSONObject}
	 * @author 23042
	 */

	public JSONObject mockCashBalance(Map<String, Object> inputMap) {

		JSONObject response = new JSONObject();
		JSONArray cashArray = new JSONArray();
		String portfolioId = (String) inputMap.get(TemenosConstants.PORTFOLIOID);
		String[] currency = null, balance = null, accountName = null, currencyName = null,
				referenceCurrencyValue = null, accountNumber;
		String totalCashBalance = "", totalCashBalanceCurrency = "";
		if (portfolioId.equalsIgnoreCase("100777-1")) {
			currency = new String[] { "USD", "EUR" };
			balance = new String[] { "10312.11", "4142.79" };
			accountName = new String[] { "John Bailey A/c 1", "John Bailey A/c 2" };
			accountNumber = new String[] { "11098", "13465" };
			currencyName = new String[] { "United States Dollar", "Euro" };
			referenceCurrencyValue = new String[] { "10312.11", "5016.50" };
			totalCashBalance = "15328.61";
			totalCashBalanceCurrency = "USD";
		} else if (portfolioId.equalsIgnoreCase("100777-2")) {
			currency = new String[] { "USD", "EUR" };
			balance = new String[] { "172.40", "112.79" };
			accountName = new String[] { "John Bailey A/c 1", "John Bailey A/c 2" };
			accountNumber = new String[] { "11156", "16325" };
			currencyName = new String[] { "United States Dollar", "Euro" };
			referenceCurrencyValue = new String[] { "172.40", "136.58" };
			totalCashBalance = "308.98";
			totalCashBalanceCurrency = "USD";
		} else if (portfolioId.equalsIgnoreCase("100777-3")) {
			currency = new String[] { "USD" };
			balance = new String[] { "9560.00" };
			accountName = new String[] { "John Bailey A/c 3" };
			accountNumber = new String[] { "12573" };
			currencyName = new String[] { "United States Dollar" };
			referenceCurrencyValue = new String[] { "9560.00" };
			totalCashBalance = "9560.00";
			totalCashBalanceCurrency = "USD";
		} else {
			currency = new String[] { "USD", "EUR" };
			balance = new String[] { "10312.11", "4142.79" };
			accountName = new String[] { "John Bailey A/c 1", "John Bailey A/c 2" };
			accountNumber = new String[] { "11098", "13465" };
			currencyName = new String[] { "United States Dollar", "Euro" };
			referenceCurrencyValue = new String[] { "10312.11", "5016.50" };
			totalCashBalance = "15328.61";
			totalCashBalanceCurrency = "USD";
		}

		for (int i = 0; i < currency.length; i++) {
			JSONObject cashObj = new JSONObject();
			cashObj.put(TemenosConstants.CURRENCY, currency[i]);
			cashObj.put(TemenosConstants.BALANCE, balance[i]);
			cashObj.put(TemenosConstants.ACCOUNTNAME, accountName[i]);
			cashObj.put(TemenosConstants.ACCOUNTID, accountNumber[i]);
			cashObj.put(TemenosConstants.CURRENCYNAME, currencyName[i]);
			cashObj.put(TemenosConstants.REFERENCECURRENCYVALUE, referenceCurrencyValue[i]);
			cashArray.put(cashObj);
		}
		response.put("opstatus", "0");
		response.put("totalCashBalance", totalCashBalance);
		response.put("totalCashBalanceCurrency", totalCashBalanceCurrency);
		response.put("portfolioID", portfolioId);
		response.put("httpStatusCode", "200");
		response.put("cashAccounts", cashArray);

		return response;
	}

	/**
	 * (INFO) Fetches the array of objects containing the x-axis labels(timestamp)
	 * and the corresponding data point to be plotted for the currency Pair Code.
	 * The series of timestamp is created based on the input filter values such as
	 * "1Year", "1Day", "1Month" etc. Alter the list value for the corresponding
	 * symbol.
	 * 
	 * @param inputMap
	 * @return {@link JSONArray}
	 * @author 22950
	 */
	@SuppressWarnings("static-access")
	public JSONArray getCurrencyGraphData(Map<String, Object> inputMap) {
		HistoricalDataBackendDelegateImpl historicalData = new HistoricalDataBackendDelegateImpl();

		String graphDuration = (String) inputMap.get(TemenosConstants.DATEORPERIOD);
		List<String> XaxisArray = new ArrayList<String>();
		List<Double> XaxisArrayValues = getMockDataPoints(inputMap);
		// DecimalFormat format = new DecimalFormat("##.00");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		JSONArray assetArray = new JSONArray();
		if (graphDuration == null || graphDuration.equalsIgnoreCase("1Y")) {
			String[] months = historicalData.nextMonths();

			XaxisArray = historicalData.getMonths(months);

			for (int i = 0; i < XaxisArray.size() - 1; i++) {
				JSONObject responseObj = new JSONObject();
				responseObj.put("CLOSE", XaxisArrayValues.get(i));
				responseObj.put("TIMESTAMP", XaxisArray.get(i).concat("+05:30"));
				assetArray.put(responseObj);
			}
			JSONObject responseObj = new JSONObject();
			responseObj.put("CLOSE", XaxisArrayValues.get(XaxisArrayValues.size() - 1));
			responseObj.put("TIMESTAMP", XaxisArray.get(XaxisArray.size() - 1).concat("+05:30"));
			assetArray.put(responseObj);
		} else if (graphDuration.equalsIgnoreCase("1M")) {
			Calendar c = Calendar.getInstance();
			Calendar backDate = Calendar.getInstance();
			backDate.add(Calendar.DAY_OF_MONTH, -30); // to get the date exactly
														// one month before
			// backDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // set the
			// Monday of the week

			while (backDate.before(Calendar.getInstance())) {
				if (backDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
						|| Calendar.DAY_OF_WEEK == Calendar.SUNDAY) {
					backDate.add(Calendar.DATE, 1);
					continue;
				}
				XaxisArray.add(df.format(backDate.getTime()));
				backDate.add(Calendar.DATE, 1);
			}
			XaxisArray.add(df.format(c.getTime()));

			for (int i = 0; i < XaxisArray.size() - 1; i++) {
				JSONObject responseObj = new JSONObject();
				responseObj.put("CLOSE", XaxisArrayValues.get(i));
				responseObj.put("TIMESTAMP", XaxisArray.get(i).concat("+05:30"));
				assetArray.put(responseObj);
			}
			JSONObject responseObj = new JSONObject();
			responseObj.put("CLOSE", XaxisArrayValues.get(XaxisArrayValues.size() - 1));
			responseObj.put("TIMESTAMP", XaxisArray.get(XaxisArray.size() - 1).concat("+05:30"));
			assetArray.put(responseObj);
		}

		else if (graphDuration.equalsIgnoreCase("5Y")) {
			Calendar c = Calendar.getInstance();
			Calendar backDate = Calendar.getInstance();
			int firstYear = c.getInstance().get(Calendar.YEAR) - 4;
			backDate.set(firstYear, Calendar.JANUARY, 01, 00, 00, 00);
			if (backDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				backDate.add(Calendar.DATE, 2);
			} else if (backDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				backDate.add(Calendar.DATE, 1);
			}

			while (backDate.before(Calendar.getInstance())) {
				XaxisArray.add(df.format(backDate.getTime()));
				backDate.add(Calendar.MONTH, 3);
			}

			XaxisArray.add(df.format(c.getTime()));
			for (int i = 0; i < XaxisArray.size() - 1; i++) {
				JSONObject responseObj = new JSONObject();
				responseObj.put("CLOSE", XaxisArrayValues.get(i));
				responseObj.put("TIMESTAMP", XaxisArray.get(i).concat("+05:30"));
				assetArray.put(responseObj);
			}
			JSONObject responseObj = new JSONObject();
			responseObj.put("CLOSE", XaxisArrayValues.get(XaxisArrayValues.size() - 1));
			responseObj.put("TIMESTAMP", XaxisArray.get(XaxisArray.size() - 1).concat("+05:30"));
			assetArray.put(responseObj);
		} else if (graphDuration.equalsIgnoreCase("YTD")) {

			// int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
			Map<Integer, Integer> daysPerMonth = new HashMap<>();

			Calendar backDate = Calendar.getInstance();
			int year = backDate.getInstance().get(Calendar.YEAR);
			backDate.set(year, Calendar.JANUARY, 01, 00, 00, 00);
			if (backDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				backDate.add(Calendar.DATE, -1);
			} else if (backDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				backDate.add(Calendar.DATE, -2);
			}

			while (backDate.before(Calendar.getInstance())) {
				if (daysPerMonth.containsKey(backDate.get(Calendar.MONTH))) {
					daysPerMonth.put(backDate.get(Calendar.MONTH), daysPerMonth.get(backDate.get(Calendar.MONTH)) + 1);
				} else {
					daysPerMonth.put(backDate.get(Calendar.MONTH), 1);
				}
				if (daysPerMonth.get(backDate.get(Calendar.MONTH)) <= 4)
					XaxisArray.add(df.format(backDate.getTime()));
				backDate.add(Calendar.DATE, 7);

			}

			for (int i = 0; i < XaxisArray.size() - 1; i++) {
				JSONObject responseObj = new JSONObject();
				responseObj.put("CLOSE", XaxisArrayValues.get(i));
				responseObj.put("TIMESTAMP", XaxisArray.get(i).concat("+05:30"));
				assetArray.put(responseObj);
			}
			JSONObject responseObj = new JSONObject();
			responseObj.put("CLOSE", XaxisArrayValues.get(XaxisArrayValues.size() - 1));
			responseObj.put("TIMESTAMP", XaxisArray.get(XaxisArray.size() - 1).concat("+05:30"));
			assetArray.put(responseObj);
		}
		// assetArray.put(response);
		return assetArray;
	}

	/**
	 * (INFO) Fetches the array of objects containing the x-axis labels(timestamp)
	 * and the corresponding data point to be plotted for the RIC Instrument Code.
	 * The series of timestamp is created based on the input filter values such as
	 * "1Year", "1Day", "1Month" etc. Alter the list value for the corresponding
	 * symbol.
	 * 
	 * @param inputMap
	 * @return {@link JSONArray}
	 * @author 22950
	 */

	@SuppressWarnings("static-access")
	public JSONArray getInstrumentGraphData(Map<String, Object> inputMap) {
		HistoricalDataBackendDelegateImpl historicalData = new HistoricalDataBackendDelegateImpl();

		String graphDuration = (String) inputMap.get(TemenosConstants.DATEORPERIOD);
		List<String> XaxisArray = new ArrayList<String>();
		List<Double> XaxisArrayValues = getMockDataPoints(inputMap);
		// DecimalFormat format = new DecimalFormat("##.00");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

		JSONArray assetArray = new JSONArray();
		if (graphDuration.equalsIgnoreCase("1D")) {
			ZonedDateTime zonedTime = ZonedDateTime.now(ZoneId.of("America/New_York"));

			if (zonedTime.getDayOfWeek() == DayOfWeek.SATURDAY) {
				zonedTime = zonedTime.minusDays(1);
			} else if (zonedTime.getDayOfWeek() == DayOfWeek.SATURDAY) {
				zonedTime = zonedTime.minusDays(2);
			} else {
				if (zonedTime.getHour() < 9 || (zonedTime.getHour() == 9 && zonedTime.getMinute() < 30)) {

					zonedTime = zonedTime.minusDays(1);

					if (zonedTime.getDayOfWeek() == DayOfWeek.SATURDAY) {
						zonedTime = zonedTime.minusDays(1);
					} else if (zonedTime.getDayOfWeek() == DayOfWeek.SATURDAY) {
						zonedTime = zonedTime.minusDays(2);
					}
				}
			}

			zonedTime = zonedTime.withHour(9);
			zonedTime = zonedTime.withMinute(30);

			Calendar backDate = GregorianCalendar.from(zonedTime);

			Calendar endTime = GregorianCalendar.from(zonedTime);
			endTime.set(Calendar.HOUR_OF_DAY, 16);
			endTime.set(Calendar.MINUTE, 00);

			XaxisArray.add(df.format(backDate.getTime()));
			while (backDate.before(endTime)) {
				backDate.add(Calendar.MINUTE, 30);
				XaxisArray.add(df.format(backDate.getTime()));
			}

			for (int i = 0; i < XaxisArray.size() - 1; i++) {
				JSONObject responseObj = new JSONObject();
				if (XaxisArrayValues != null) {
					responseObj.put("CLOSE", XaxisArrayValues.get(i));
					responseObj.put("TIMESTAMP", XaxisArray.get(i).concat("+05:30"));
					assetArray.put(responseObj);
				}
			}
			JSONObject responseObj = new JSONObject();
			if (XaxisArrayValues != null) {
				responseObj.put("CLOSE", XaxisArrayValues.get(XaxisArrayValues.size() - 1));
				responseObj.put("TIMESTAMP", XaxisArray.get(XaxisArray.size() - 1).concat("+05:30"));
				assetArray.put(responseObj);
			}
		} else if (graphDuration == null || graphDuration.equalsIgnoreCase("1Y")) {
			String[] months = historicalData.nextMonths();

			XaxisArray = historicalData.getMonths(months);

			for (int i = 0; i < XaxisArray.size() - 1; i++) {
				JSONObject responseObj = new JSONObject();
				if (XaxisArrayValues != null) {
					responseObj.put("CLOSE", XaxisArrayValues.get(i));
					responseObj.put("TIMESTAMP", XaxisArray.get(i).concat("+05:30"));
					assetArray.put(responseObj);
				}
			}
			JSONObject responseObj = new JSONObject();
			if (XaxisArrayValues != null) {
				responseObj.put("CLOSE", XaxisArrayValues.get(XaxisArrayValues.size() - 1));
				responseObj.put("TIMESTAMP", XaxisArray.get(XaxisArray.size() - 1).concat("+05:30"));
				assetArray.put(responseObj);
			}
		} else if (graphDuration.equalsIgnoreCase("1M")) {
			Calendar c = Calendar.getInstance();
			Calendar backDate = Calendar.getInstance();
			backDate.add(Calendar.DAY_OF_MONTH, -30); // to get the date exactly
														// one month before
			// backDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // set the
			// Monday of the week

			while (backDate.before(Calendar.getInstance())) {
				if (backDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
						|| Calendar.DAY_OF_WEEK == Calendar.SUNDAY) {
					backDate.add(Calendar.DATE, 1);
					continue;
				}
				XaxisArray.add(df.format(backDate.getTime()));
				backDate.add(Calendar.DATE, 1);
			}
			XaxisArray.add(df.format(c.getTime()));

			for (int i = 0; i < XaxisArray.size() - 1; i++) {
				JSONObject responseObj = new JSONObject();
				if (XaxisArrayValues != null) {
					responseObj.put("CLOSE", XaxisArrayValues.get(i));
					responseObj.put("TIMESTAMP", XaxisArray.get(i).concat("+05:30"));
					assetArray.put(responseObj);
				}
			}
			JSONObject responseObj = new JSONObject();
			if (XaxisArrayValues != null) {
				responseObj.put("CLOSE", XaxisArrayValues.get(XaxisArrayValues.size() - 1));
				responseObj.put("TIMESTAMP", XaxisArray.get(XaxisArray.size() - 1).concat("+05:30"));
				assetArray.put(responseObj);
			}
		}

		else if (graphDuration.equalsIgnoreCase("5Y")) {
			Calendar c = Calendar.getInstance();
			Calendar backDate = Calendar.getInstance();
			int firstYear = c.getInstance().get(Calendar.YEAR) - 4;
			backDate.set(firstYear, Calendar.JANUARY, 01, 00, 00, 00);
			if (backDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				backDate.add(Calendar.DATE, 2);
			} else if (backDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				backDate.add(Calendar.DATE, 1);
			}

			while (backDate.before(Calendar.getInstance())) {
				XaxisArray.add(df.format(backDate.getTime()));
				backDate.add(Calendar.MONTH, 3);
			}

			XaxisArray.add(df.format(c.getTime()));
			for (int i = 0; i < XaxisArray.size() - 1; i++) {
				JSONObject responseObj = new JSONObject();
				if (XaxisArrayValues != null) {
					responseObj.put("CLOSE", XaxisArrayValues.get(i));
					responseObj.put("TIMESTAMP", XaxisArray.get(i).concat("+05:30"));
					assetArray.put(responseObj);
				}
			}
			JSONObject responseObj = new JSONObject();
			if (XaxisArrayValues != null) {
				responseObj.put("CLOSE", XaxisArrayValues.get(XaxisArrayValues.size() - 1));
				responseObj.put("TIMESTAMP", XaxisArray.get(XaxisArray.size() - 1).concat("+05:30"));
				assetArray.put(responseObj);
			}
		} else if (graphDuration.equalsIgnoreCase("YTD")) {

			// int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
			Map<Integer, Integer> daysPerMonth = new HashMap<>();

			Calendar backDate = Calendar.getInstance();
			int year = backDate.getInstance().get(Calendar.YEAR);
			backDate.set(year, Calendar.JANUARY, 01, 00, 00, 00);
			if (backDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				backDate.add(Calendar.DATE, -1);
			} else if (backDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				backDate.add(Calendar.DATE, -2);
			}

			while (backDate.before(Calendar.getInstance())) {
				if (daysPerMonth.containsKey(backDate.get(Calendar.MONTH))) {
					daysPerMonth.put(backDate.get(Calendar.MONTH), daysPerMonth.get(backDate.get(Calendar.MONTH)) + 1);
				} else {
					daysPerMonth.put(backDate.get(Calendar.MONTH), 1);
				}
				if (daysPerMonth.get(backDate.get(Calendar.MONTH)) <= 4)
					XaxisArray.add(df.format(backDate.getTime()));
				backDate.add(Calendar.DATE, 7);

			}

			for (int i = 0; i < XaxisArray.size() - 1; i++) {
				JSONObject responseObj = new JSONObject();
				if (XaxisArrayValues != null) {
					responseObj.put("CLOSE", XaxisArrayValues.get(i));
					responseObj.put("TIMESTAMP", XaxisArray.get(i).concat("+05:30"));
					assetArray.put(responseObj);
				}
			}
			JSONObject responseObj = new JSONObject();
			if (XaxisArrayValues != null) {
				responseObj.put("CLOSE", XaxisArrayValues.get(XaxisArrayValues.size() - 1));
				responseObj.put("TIMESTAMP", XaxisArray.get(XaxisArray.size() - 1).concat("+05:30"));
				assetArray.put(responseObj);
			}
		}
		// assetArray.put(response);
		return assetArray;
	}

	/**
	 * (INFO) Fetches the data points to be plotted for the currency Pair/ RIC
	 * Instrument Code Alter the list value for the corresponding symbol.
	 * 
	 * @param inputMap
	 * @return {@link List}
	 * @author 22950
	 */
	public List<Double> getMockDataPoints(Map<String, Object> inputMap) {
		String currencyPair = (String) inputMap.get(TemenosConstants.CURRENCYPAIRS);
		String ricCode = (String) inputMap.get(TemenosConstants.RICCODE);
		if (currencyPair != null) {
			if (currencyPair.equalsIgnoreCase("EURUSD")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(1.1016, 1.105, 1.1022, 1.1015, 1.1057,
						1.1119, 1.1078, 1.1175, 1.1158, 1.112, 1.1088, 1.1023, 1.1093, 1.0943, 1.083, 1.0843, 1.1025,
						1.1285, 1.1105, 1.0694, 1.114, 1.0808, 1.0935, 1.0876, 1.082, 1.0983, 1.084, 1.0815, 1.09,
						1.1098, 1.1284, 1.1254, 1.1175, 1.1217, 1.1248, 1.1298, 1.1426, 1.1654, 1.1774, 1.1786, 1.1841,
						1.1795, 1.1903, 1.1838, 1.1845, 1.1837, 1.163, 1.1713, 1.1824, 1.1718, 1.1859, 1.1647, 1.1872,
						1.1832, 1.1873));
				return dataPoints;
			} else if (currencyPair.equalsIgnoreCase("USDEUR")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(0.9073, 0.9046, 0.9069, 0.9075, 0.9041,
						0.899, 0.9025, 0.8946, 0.8958, 0.899, 0.9016, 0.9069, 0.9013, 0.9135, 0.923, 0.9219, 0.9068,
						0.886, 0.9003, 0.9349, 0.8973, 0.9251, 0.9142, 0.9195, 0.9239, 0.9105, 0.9224, 0.9245, 0.9172,
						0.9009, 0.8859, 0.8883, 0.8945, 0.8912, 0.8889, 0.8848, 0.8749, 0.8578, 0.8488, 0.8482, 0.8443,
						0.8475, 0.8398, 0.8445, 0.844, 0.8445, 0.8595, 0.8534, 0.8455, 0.8532, 0.843, 0.8583, 0.842,
						0.8449, 0.842));
				return dataPoints;
			} else if (currencyPair.equalsIgnoreCase("USDGBP")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(0.7828, 0.775, 0.7792, 0.7731, 0.7611,
						0.7503, 0.7689, 0.7646, 0.764, 0.7655, 0.7685, 0.7646, 0.7574, 0.7756, 0.7663, 0.7708, 0.7799,
						0.7662, 0.8143, 0.8588, 0.8026, 0.8154, 0.8028, 0.7998, 0.8083, 0.7996, 0.8057, 0.8259, 0.8213,
						0.8098, 0.7895, 0.7973, 0.8091, 0.8106, 0.8008, 0.7921, 0.7955, 0.7816, 0.7641, 0.766, 0.7641,
						0.7638, 0.7488, 0.7528, 0.7814, 0.7741, 0.7843, 0.7727, 0.7663, 0.7741, 0.7666, 0.7725, 0.7599,
						0.7582, 0.7539));
				return dataPoints;
			} else if (currencyPair.equalsIgnoreCase("GBPUSD")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(1.277, 1.2899, 1.283, 1.2933, 1.3135,
						1.3325, 1.3002, 1.3076, 1.3086, 1.3059, 1.3008, 1.3076, 1.3199, 1.2891, 1.3046, 1.2969, 1.282,
						1.3047, 1.2276, 1.1641, 1.2456, 1.226, 1.2452, 1.2499, 1.2367, 1.2502, 1.2407, 1.2104, 1.2164,
						1.2344, 1.2663, 1.254, 1.2356, 1.2333, 1.2483, 1.262, 1.2565, 1.2789, 1.3088, 1.3051, 1.3084,
						1.3087, 1.3349, 1.3282, 1.2793, 1.2915, 1.2745, 1.2931, 1.3046, 1.2913, 1.304, 1.2941, 1.3156,
						1.3186, 1.3261));
				return dataPoints;
			} else {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(1.1586, 1.1669, 1.1636, 1.1737, 1.1875,
						1.198, 1.1735, 1.1698, 1.1723, 1.1739, 1.1727, 1.1858, 1.1896, 1.1776, 1.2042, 1.1956, 1.1625,
						1.1559, 1.1052, 1.0884, 1.1177, 1.1341, 1.1384, 1.1492, 1.1426, 1.1384, 1.1445, 1.119, 1.1163,
						1.1121, 1.1218, 1.1139, 1.1053, 1.0991, 1.1096, 1.1166, 1.0993, 1.0972, 1.1105, 1.107, 1.1047,
						1.1092, 1.1211, 1.1217, 1.0798, 1.0907, 1.0955, 1.1039, 1.103, 1.1018, 1.0992, 1.1107, 1.1078,
						1.1141, 1.1165));
				return dataPoints;
			}
		} else if (ricCode != null) {
			if (ricCode.equalsIgnoreCase("AMZN.O")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(1785.00, 1736.91, 1745.90, 1801.52,
						1751.99, 1762.15, 1784.75, 1869.28, 1874.08, 1883.03, 1862.94, 1861.77, 2008.26, 2077.99,
						2133.71, 2094.18, 1884.27, 1902.09, 1778.32, 1846.73, 1902.12, 1906.20, 2044.25, 2367.06,
						2409.79, 2288.24, 2377.14, 2409.04, 2433.06, 2437.34, 2480.73, 2549.27, 2672.94, 2693.91,
						2886.27, 3205.00, 2961.14, 3008.5, 3165.885, 3167.15, 3146.75, 3284.38, 3400.445, 3297.89,
						3117.73, 2954.72, 3095.97, 3126.585, 3287.70, 3216.10, 3204.98, 3037.32, 3311.86, 3128.79,
						3113.84));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("GOOGL.O")) {
				List<Double> dataPoints = new ArrayList<Double>(
						Arrays.asList(1307.43, 1331.68, 1294.3, 1305.77, 1339.95, 1347.31, 1351.09, 1354.52, 1360.00,
								1429.36, 1479.43, 1466.73, 1434.45, 1478.63, 1518.34, 1482.39, 1339.96, 1295.65, 1213.7,
								1066.15, 1109.16, 1089.63, 1206.17, 1274.34, 1273.81, 1317.08, 1381.755, 1374.21,
								1414.65, 1434.52, 1437.29, 1414.97, 1427.34, 1362.73, 1471.79, 1540.58, 1514.23,
								1509.07, 1486.45, 1498.19, 1501.84, 1574.82, 1638.35, 1582.28, 1517.01, 1451.2, 1440.16,
								1457.07, 1510.45, 1566.99, 1632.88, 1615.39, 1759.2, 1772.17, 1758.85));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("AAPL.O")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(65.035, 66.44, 65.445, 66.8125, 67.6775,
						68.7875, 69.86, 72.45, 74.3575, 77.5825, 79.6825, 79.5775, 77.3775, 80.0075, 81.2375, 78.2625,
						68.34, 72.2575, 69.4925, 57.31, 61.935, 60.3525, 66.9975, 70.7, 70.7425, 72.2675, 77.5325,
						76.9275, 79.7225, 79.485, 82.875, 84.7, 87.43, 88.4075, 91.0275, 95.92, 96.3275, 92.615, 106.26,
						111.1125, 114.9075, 124.37, 124.8075, 120.96, 112.00, 106.84, 112.28, 113.02, 116.97, 119.02,
						115.04, 108.86, 118.69, 119.26, 118.64));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("LVMH.PA")) {
				List<Double> dataPoints = new ArrayList<Double>(
						Arrays.asList(403.0, 405.5, 396.25, 407.3, 401.35, 403.8, 410.5, 417.6, 419.1, 423.65, 439.05,
								416.3, 395.3, 413.95, 414.95, 404.5, 370.85, 360.35, 314.9, 311.0, 341.65, 325.05,
								347.4, 358.0, 340.85, 352.45, 352.25, 330.25, 355.3, 375.0, 404.25, 379.15, 380.05,
								387.7, 396.7, 400.7, 410.25, 399.1, 366.75, 374.0, 386.05, 385.45, 396.35, 402.85,
								416.6, 413.85, 400.2, 407.65, 411.9, 432.6, 428.0, 402.3, 435.5, 469.75, 488.65));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("AMAG.OQ")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(17.45, 18.03, 18.0, 18.12, 18.65, 18.75,
						18.94, 18.59, 18.18, 18.04, 17.95, 17.22, 16.24, 17.48, 17.14, 17.2, 17.01, 14.64, 10.77, 9.08,
						9.22, 8.84, 9.7, 9.31, 10.27, 10.85, 10.61, 9.54, 10.57, 11.23, 13.22, 10.97, 11.25, 12.57,
						11.62, 11.05, 11.24, 11.17, 11.55, 12.05, 12.37, 11.97, 12.25, 11.7, 12.46, 11.83, 10.53, 10.6,
						10.97, 11.86, 11.88, 11.1, 12.05, 12.5, 13.24));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("AMAL.OQ")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(18.45, 19.03, 19.0, 19.12, 19.65, 19.75,
						19.94, 19.59, 19.18, 19.04, 18.95, 18.22, 17.24, 18.48, 18.14, 18.2, 16.01, 15.64, 11.77, 9.08,
						9.22, 8.84, 9.7, 9.31, 10.27, 10.85, 10.61, 9.54, 10.57, 11.23, 13.22, 10.97, 11.25, 12.57,
						11.62, 11.05, 11.24, 11.17, 11.55, 12.05, 12.37, 11.97, 12.25, 11.7, 12.46, 11.83, 10.53, 11.6,
						11.97, 11.86, 11.88, 11.1, 11.05, 12.5, 13.24));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("AMRN.A")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(17.49, 24.06, 20.92, 21.25, 22.45, 24.12,
						21.04, 21.55, 20.97, 18.96, 19.91, 20.23, 18.56, 17.66, 17.64, 17.93, 14.65, 14.92, 12.43,
						10.63, 13.22, 4.78, 6.04, 6.5, 7.52, 7.35, 7.71, 7.71, 7.23, 6.87, 6.67, 6.59, 6.93, 6.72, 6.93,
						6.7, 6.72, 6.52, 6.49, 6.99, 6.83, 6.92, 7.36, 4.315, 4.06, 4.17, 3.81, 4.45, 5.25, 5.14, 4.99,
						4.85, 4.28, 4.27, 4.54));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("AMBA.OQ")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(56.1, 58.39, 54.72, 54.74, 53.72, 54.97,
						57.97, 59.91, 62.82, 62.42, 62.69, 60.49, 59.14, 62.31, 71.52, 67.45, 59.45, 50.64, 45.19,
						40.31, 47.88, 45.43, 47.72, 49.25, 52.05, 49.0, 54.75, 52.2, 57.29, 56.74, 53.68, 50.55, 48.9,
						44.86, 45.21, 48.21, 47.37, 44.58, 45.28, 46.34, 45.83, 45.97, 52.01, 52.18, 50.37, 53.03,
						49.61, 51.92, 58.15, 57.06, 57.15, 54.67, 60.15, 58.71, 65.345));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("AMCX.O")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(41.41, 39.65, 39.82, 38.43, 38.23, 37.39,
						39.47, 39.54, 39.36, 39.69, 42.25, 38.25, 36.59, 37.69, 37.79, 35.05, 31.0, 27.33, 27.94, 24.73,
						24.83, 20.58, 24.5, 24.37, 21.19, 24.04, 26.03, 27.58, 29.16, 28.27, 33.31, 27.89, 26.62, 22.64,
						23.16, 24.35, 24.92, 24.87, 23.1, 23.595, 25.2, 24.51, 25.39, 23.21, 20.86, 24.98, 24.19, 24.05,
						23.75, 23.275, 23.36, 21.25, 23.89, 26.36, 29.0));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("MSFT.N")) {
				List<Double> dataPoints = new ArrayList<Double>(
						Arrays.asList(151.69, 154.5, 157.335, 158.97, 158.67, 161.21, 167.03, 165.03, 170.19, 183.9,
								185.35, 178.46, 161.96, 161.36, 158.93, 136.77, 149.58, 153.92, 164.95, 178.67, 174.48,
								174.63, 184.63, 183.31, 183.46, 183.13, 187.24, 187.8, 195.14, 196.24, 206.25, 213.75,
								202.9, 201.3, 204.99, 212.52, 208.8, 213.3, 228.95, 214.17, 204.08, 200.45, 207.79,
								206.12, 215.78, 219.725, 216.13, 202.61, 223.44, 216.5, 210.39, 215.23, 214.19));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("TSLA.OQ")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(67.178, 71.678, 81.118, 86.076, 88.602,
						95.63, 102.1, 112.964, 130.114, 149.614, 160.006, 180.2, 133.598, 140.696, 109.324, 85.506,
						102.872, 96.002, 114.6, 150.778, 145.03, 140.264, 163.884, 159.834, 163.376, 167.00, 177.132,
						187.056, 200.18, 191.948, 241.732, 308.93, 300.168, 283.4, 286.152, 290.542, 330.142, 409.996,
						442.68, 418.32, 372.72, 442.15, 407.34, 415.09, 434.00, 439.67, 420.63, 388.04, 429.95, 408.5,
						489.61, 585.76, 593.38));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("WMT.N")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(119.78, 120.29, 120.29, 119.59, 117.89,
						116.38, 114.96, 114.37, 114.49, 116.45, 117.89, 118.58, 107.68, 117.23, 114.1, 113.97, 109.58,
						119.48, 121.8, 132.12, 129.44, 122.92, 122.94, 125.94, 124.33, 124.06, 121.56, 117.74, 119.85,
						118.32, 119.21, 130.68, 131.74, 131.24, 129.4, 129.97, 132.6, 131.63, 140.3, 142.83, 136.7,
						135.29, 137.27, 140.5, 142.78, 144.71, 143.85, 138.75, 145.77, 150.54, 150.24, 151.6, 149.3));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("IXM0461.DE")) {
				List<Double> dataPoints = new ArrayList<Double>(Arrays.asList(286.67, 287.7138, 287.4623, 287.6988,
						285.9225, 286.4889, 285.2566, 287.0173, 286.0793, 289.1433, 290.9961, 291.8677, 294.5241,
						295.6687, 295.5725, 294.9065, 295.8449, 301.7169, 293.4718, 289.2953, 291.4473, 286.9141,
						290.6052, 286.8046, 282.9884, 287.637, 289.7462, 289.7178, 284.3093, 281.3128, 277.9103,
						282.1898, 283.8088, 281.279, 282.7912, 289.2102, 285.9447, 290.1855, 289.7221, 288.9341,
						289.8574, 291.4802, 291.075, 294.6975, 295.7867, 301.371, 301.8388, 298.8635, 298.2935,
						300.3985, 296.4671, 294.3168, 291.2942, 292.8192, 292.6368, 288.0287, 289.7738, 282.0492,
						287.1513, 280.9408, 285.5923, 291.2942, 292.8192, 292.6368, 301.23, 306.65));
				return dataPoints;
			} else if (ricCode.equalsIgnoreCase("2YTD") || ricCode.equalsIgnoreCase("5YTD")
					|| ricCode.equalsIgnoreCase("10YTD") || ricCode.equalsIgnoreCase("FEURUSD")
					|| ricCode.equalsIgnoreCase("FGBPUSD") || ricCode.equalsIgnoreCase("FCHFUSD")) {
				return null;
			} else {
				List<Double> dataPoints = new ArrayList<Double>(
						Arrays.asList(15.55, 15.415, 15.44, 14.98, 15.06, 14.915, 15.13, 15.245, 15.03, 15.015, 14.75,
								15.29, 15.305, 15.525, 15.855, 16.13, 15.645, 15.605, 13.03, 13.715, 14.245, 15.09,
								14.22, 13.995, 13.32, 13.495, 13.295, 12.76, 13.205, 13.655, 14.865, 14.04, 14.105,
								13.945, 13.56, 13.47, 13.81, 13.55, 13.5, 13.39, 13.75, 13.225, 13.445, 14.315, 14.605,
								13.43, 13.695, 13.49, 13.875, 14.055, 13.85, 13.36, 13.41, 14.34, 14.135));
				return dataPoints;
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	public JSONObject mockGetHoldingsList(Map<String, Object> inputMap) {
		String portfolioId = (String) inputMap.get(TemenosConstants.PORTFOLIOID);
		String sortBy = (String) inputMap.get(TemenosConstants.SORTBY);
		String searchVal = (String) inputMap.get(TemenosConstants.SEARCHBYINSTRUMENTNAME);
		String instrumentidVal = (String) inputMap.get(TemenosConstants.INSTRUMENTID);
		String[] holdingsId = null, description = null, ISIN = null, exchange = null, secCCy = null, quantity = null,
				marketPrice = null, costPrice = null, marketValue = null, unrealPLMkt = null, RICCode = null,
				weightPercentage = null, assestClass = null, region = null, sector = null, exchangeRate = null,
				marketValPOS = null, costValue = null, costExchangeRate = null, unRealizedPLPercentage = null,
				dailyPL = null, dailyPLPercentage = null, instrumentId = null, unrealPLMktSec = null,
				unRealizedPLPercentageSec = null, costValueSec = null, amountBought = null, accruedInterest = null,
				balance = null, amountSold = null, quote = null, costQuote = null, counterpartAmount = null,
				subAssetClass = null, application = null;
		;
		int averageCostIndex = 0, nominalIndex = 0, accruedInterestIndex = 0, amountBoughtIndex = 0, balanceIndex = 0,
				amountSoldIndex = 0, quoteIndex = 0, costQuoteIndex = 0, counterpartAmountIndex = 0;

		boolean[] isSecurityAsset = null, isAdvisory = null;
		String sortType = (String) inputMap.get(TemenosConstants.SORTORDER);
		int totalCount = 0;
		String limitVal = (String) inputMap.get(TemenosConstants.PAGESIZE);
		String offsetVal = (String) inputMap.get(TemenosConstants.PAGEOFFSET);
		int limit = (limitVal != null && limitVal.trim().length() > 0) ? Integer.parseInt(limitVal) : 0;
		int offset = (offsetVal != null && offsetVal.trim().length() > 0) ? Integer.parseInt(offsetVal) : 0;
		String search = (searchVal != null && searchVal.trim().length() > 0) ? searchVal : "";
		String instrumentid = (instrumentidVal != null && instrumentidVal.trim().length() > 0) ? instrumentidVal : "";
		JSONObject response = new JSONObject();
		JSONArray holdingsArr = new JSONArray();

		if (portfolioId.equalsIgnoreCase("100777-1")) {
			holdingsId = new String[] { "100051-000", "100093-000", "100016-000", "100020-000", "100086-000",
					"100017-000", "100018-000", "100019-000", "100022-000", "100021-000", "100077-000", "100050-000",
					"100056-000", "100156-000", "100091-000", "100092-000" };
			instrumentId = new String[] { "100051-000", "100093-000", "100016-000", "100020-000", "100086-000",
					"100017-000", "100018-000", "100019-000", "100022-000", "100021-000", "100077-000", "100050-000",
					"100056-000", "100156-000", "100091-000", "100092-000" };
			description = new String[] { "LVMH", "iShares Core S&P 500 UCITS ETF", "Coca-Cola Co",
					"American Express Company", "Boeing Co", "Bank of America Corp", "Citigroup Inc",
					"General Motors Company", "Pfizer Inc", "Amazon.com Inc", "Alphabet", "Apple" };
			ISIN = new String[] { "FR0000121014", "IE00B5BMR087", "US1912161007", "US0258161092", "US0970231058",
					"US0605051046", "US1729671016", "US37045V1008", "INE182A01018", "US0231351067", "US02079K1079",
					"US0378331005" };
			exchange = new String[] { "Euronext Paris", "GER", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE",
					"NYSE", "NYSE", "NYSE" };
			secCCy = new String[] { "EUR", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD" };
			quantity = new String[] { "6", "16", "10", "15", "10", "12", "15", "8", "10", "4", "2", "23" };
			marketPrice = new String[] { "497.95", "308.75", "49.29", "116.15", "202.06", "30.94", "60.91", "52.04",
					"37.31", "3203.53", " 1824.97", "123.08" };
			costPrice = new String[] { "372.29", "300.00", "45.00", "105.00", "190.00", "33.00", "55.00", "49.00",
					"34.00", "2652.13", "1622.11", "98.08" };
			marketValue = new String[] { "2987.70", "5631.12", "492.90", "1742.25", "2020.60", "371.28", "913.65",
					"416.32", "373.10", "12814.12", "3649.94", "2830.84" };
			unrealPLMkt = new String[] { "+753.96", "+631.12", "+42.90", "+167.25", "+120.60", "-24.72", "+88.65",
					"+24.32", "+33.10", "+2205.60", "+405.72", "575.00" };
			unrealPLMktSec = new String[] { "+753.96", "+631.12", "+42.90", "+167.25", "+120.60", "-24.72", "+88.65",
					"+24.32", "+33.10", "+2205.60", "+405.72", "575.00" };
			RICCode = new String[] { "LVMH.PA", "BLK", "KO.N", "AXP.N", "BA.NQ", "BAC.N", "C.N", "GM.N", "PFE.N",
					"AMZN.OQ", "GOOGL.OQ", "AAPL.OQ" };

			weightPercentage = new String[] { "6.03", "11.36", "0.99", "3.51", "4.08", "0.75", "1.84", "0.84", "0.75",
					"25.85", "7.36", "5.71" };
			assestClass = new String[] { "Share", "Fund", "Share", "Share", "Share", "Share", "Share", "Share", "Share",
					"Share", "Share", "Share" };
			subAssetClass = new String[] { "Ordinary Shares", "Exchange Traded Funds", "Ordinary Shares",
					"Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares",
					"Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares" };

			region = new String[] { "EU", "US", "US", "US", "US", "US", "US", "US", "US", "North America",
					"North America", "North America" };
			sector = new String[] { "Consumer Cyclical", "Fund -Open Ended Investment Company", "Consumer Defensive",
					"Financial Services", "Industrials", "Financial Services", "Financial Services",
					"Consumer Cyclical", "Healthcare", "Consumer Cyclical", "Communication Services", "Technology" };
			exchangeRate = new String[] { "0.82", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1" };
			marketValPOS = new String[] { "2987.70", "5631.12", "492.90", "1742.25", "2020.60", "371.28", "913.65",
					"416.32", "373.10", "12814.12", "3649.94", "2830.84" };
			costValue = new String[] { "2233.74", "5000.00", "450.00", "1575.00", "1900.00", "396.00", "825.00",
					"392.00", "340.00", "10608.52", "3244.22", "2255.84" };
			costValueSec = new String[] { "2233.74", "5000.00", "450.00", "1575.00", "1900.00", "396.00", "825.00",
					"392.00", "340.00", "10608.52", "3244.22", "2255.84" };
			costExchangeRate = new String[] { "0.15", "0.5", "0.6", "0.7", "0.8", "0.9", "0.10", "0.11", "0.12", "0.13",
					"0.14", "0.15", "0.16" };
			unRealizedPLPercentage = new String[] { "34", "13", "10", "11", "6", "-6", "11", "6", "10", "21", "13",
					"25" };
			unRealizedPLPercentageSec = new String[] { "34", "13", "10", "11", "6", "-6", "11", "6", "10", "21", "13",
					"25" };
			dailyPL = new String[] { "85.9", "0.13", "44.5", "-30.2", "-20.4", "-10.45", "23.6", "-10.65", "90.5",
					"145.5", "29.61", "0.36" };
			dailyPLPercentage = new String[] { "0.11", "1.3", "0.14", "-0.92", "0.60", "-1.20", "0.56", "0.45", "0.83",
					"0.03", "1.65", "0.29" };
			isSecurityAsset = new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true };
			isAdvisory = new boolean[] { false, false, false, false, false, false, false, false, false, false, false,
					false };
			application = new String[] { "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC" };
			accruedInterest = new String[] { "121.56", "55.23", "356.76" };
		} else if (portfolioId.equalsIgnoreCase("100777-2")) {
			holdingsId = new String[] { "100077-000", "100014-000", "100016-000", "100020-000", "100086-000",
					"100017-000", "100018-000", "100019-000", "100022-000", "100021-000", "100050-000" };
			instrumentId = new String[] { "100077-000", "100014-000", "100016-000", "100020-000", "100086-000",
					"100017-000", "100018-000", "100019-000", "100022-000", "100021-000", "100050-000" };
			description = new String[] { "Alphabet", "Walmart Inc", "Coca-Cola Co", "American Express Company",
					"Boeing Co", "Bank of America Corp", "Citigroup Inc", "General Motors Company", "Pfizer Inc",
					"Amazon.com Inc", "Apple" };
			ISIN = new String[] { "US02079K1079", "US9311421039", "US1912161007", "US0258161092", "US0970231058",
					"US0605051046", "US1729671016", "US37045V1008", "INE182A01018", "US0231351067", "US0378331005" };
			exchange = new String[] { "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE",
					"NYSE" };
			secCCy = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD" };
			quantity = new String[] { "2", "6", "6", "10", "8", "9", "12", "6", "9", "2", "7" };
			marketPrice = new String[] { "1824.97", "148.91", "49.29", "116.15", "202.06", "30.94", "60.91", "52.04",
					"37.31", "3203.53", "123.08" };
			costPrice = new String[] { "1622.11", "132.51", "42.00", "106.00", "184.00", "27.00", "61.00", "50.00",
					"32.00", "2652.13", "98.08" };
			marketValue = new String[] { "3649.94", "893.46", "295.74", "1161.50", "1616.48", "278.46", "730.92",
					"312.24", "335.79", "6407.06", "861.56" };
			unrealPLMkt = new String[] { "+405.72", "+98.40", "+43.74", "+105.50", "+144.48", "+35.46", "-1.08",
					"+12.24", "+52.29", "+1102.80", "175.00" };
			unrealPLMktSec = new String[] { "+405.72", "+98.40", "+43.74", "+105.50", "+144.48", "+35.46", "-1.08",
					"+12.24", "+52.29", "+1102.80", "175.00" };
			RICCode = new String[] { "GOOGL.OQ", "WMT.N", "KO.N", "AXP.N", "BA.NQ", "BAC.N", "C.N", "GM.N", "PFE.N",
					"AMZN.OQ", "AAPL.OQ" };
			weightPercentage = new String[] { "21.66", "5.30", "1.75", "6.89", "9.59", "1.65", "4.34", "1.85", "1.99",
					"38.02", "5.11" };
			assestClass = new String[] { "Share", "Share", "Share", "Share", "Share", "Share", "Share", "Share",
					"Share", "Share", "Share" };
			subAssetClass = new String[] { "Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares",
					"Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares",
					"Ordinary Shares", "Ordinary Shares" };

			region = new String[] { "US", "US", "US", "US", "US", "US", "US", "US", "US", "US", "US" };
			sector = new String[] { "Communication Services", "Consumer Defensive", "Consumer Defensive",
					"Financial Services", "Industrials", "Financial Services", "Financial Services",
					"Consumer Cyclical", "Healthcare", "Consumer Cyclical", "Technology" };
			exchangeRate = new String[] { "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1" };
			marketValPOS = new String[] { "3649.94", "893.46", "295.74", "1161.50", "1616.48", "278.46", "730.92",
					"312.94", "335.79", "6407.06", "861.56" };
			costValue = new String[] { "3244.22", "795.06", "252.00", "1056.00", "1472.00", "243.00", "732.00",
					"300.00", "283.50", "5304.26", "686.56" };
			costValueSec = new String[] { "3244.22", "795.06", "252.00", "1056.00", "1472.00", "243.00", "732.00",
					"300.00", "283.50", "5304.26", "686.56" };
			costExchangeRate = new String[] { "0.1", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "0.10", "0.11", "0.14",
					"0.16" };
			unRealizedPLPercentage = new String[] { "13", "12", "17", "10", "10", "15", "0", "4", "18", "21", "25" };
			unRealizedPLPercentageSec = new String[] { "13", "12", "17", "10", "10", "15", "0", "4", "18", "21", "25" };
			dailyPL = new String[] { "5.34", "2.46", "4.42", "-3.56", "4.44", "-4.89", "6.9", "2.24", "1.56", "145.5",
					"0.36" };
			dailyPLPercentage = new String[] { "0.10", "0.13", "0.10", "0.23", "0.10", "-0.34", "0.32", "0.12", "0.14",
					"0.11", "0.03", "0.29" };
			isSecurityAsset = new boolean[] { true, true, true, true, true, true, true, true, true, true, true };
			isAdvisory = new boolean[] { false, false, false, false, false, false, false, false, false, false, false };
			application = new String[] { "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC" };

			accruedInterest = new String[] { "132.74", "185.63", "127.52" };
			balance = new String[] { "25132.74", "12785.6325", "35127.518" };

		} else if (portfolioId.equalsIgnoreCase("100777-3")) {
			holdingsId = new String[] { "100027-000", "100051-000", "100016-000", "100020-000", "100086-000",
					"100098-000", "100099-000", "100028-000", "100029-000", "100030-000", "100130-000", "100230-000" };
			instrumentId = new String[] { "100027-000", "100051-000", "100016-000", "100020-000", "100086-000",
					"100098-000", "100099-000", "100028-000", "100029-000", "100030-000", "100130-000", "100230-000" };
			description = new String[] { "Google LLC", "LVMH", "Coca-Cola Co", "American Express Company", "Boeing Co",
					"Cisco Systems, Inc.", "APPLE-CALL-115-16JUL", "GOOGLE-PUT-2300-16JUL", "CITI-CALL-70-16JUL",
					"Forward EURUSD 2901 2022", "Forward GBPUSD 2901 2022", "Forward CHFUSD 2901 2022" };
			ISIN = new String[] { "US02079K1079", "FR0000121014", "US1912161007", "US0258161092", "US0970231058",
					"US17275R1023", "US0378331005", "US02079K1079", "US1729671016", "", "", "" };
			exchange = new String[] { "NYSE", "Euronext Paris", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE",
					"", "", "" };
			secCCy = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD" };
			quantity = new String[] { "1", "5", "7", "8", "9", "14", "13", "24", "22", "", "", "" };
			marketPrice = new String[] { "1824.97", "497.95", "49.29", "116.15", "202.06", "3203.53", "123.08",
					"497.95", "49.29", "116.15", "202.06", "3503.24" };
			costPrice = new String[] { "1622.11", "372.29", "45.00", "105.00", "190.00", "2.15", "12.24", "13.5",
					"2.14", "", "", "" };
			marketValue = new String[] { "1824.97", "2489.75", "345.03", "929.20", "1818.54", "74424.00", "21645.00",
					"31680.00", "5148.00", "106400.00", "140000.00", "89600.00" };
			unrealPLMkt = new String[] { "+202.86", "+628.30", "+30.03", "+89.20", "+108.54", "+71414.00", "+5733.00",
					"-720.00", "+440.00", "+1191.68", "+1568.00", "+1003.52" };
			unrealPLMktSec = new String[] { "+212.86", "+638.30", "+40.03", "+99.20", "+118.54", "+71414.00",
					"+5733.00", "-720.00", "+440.00", "+1191.68", "+1568.00", "+1003.52" };
			RICCode = new String[] { "GOOGL.O", "LVMH.PA", "KO.N", "AXP.N", "BA.NQ", "CSCO.OQ", "AAPL.N", "GOOGL.N",
					"TXG.OQ", "FEURUSD", "FGBPUSD", "FCHFUSD" };
			weightPercentage = new String[] { "5.33", "7.28", "1.01", "2.72", "5.31", "5.82", "1.69", "2.48", "0.40",
					"8.33", "10.96", "7.01" };
			assestClass = new String[] { "Share", "Share", "Share", "Share", "Share", "Future", "Option", "Option",
					"Option", "Forward", "Forward", "Forward" };
			subAssetClass = new String[] { "Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares",
					"Ordinary Shares", "Equity Futures", "Share Options", "Share Options", "Share Options", "Forward",
					"Forward", "Forward" };
			region = new String[] { "US", "US", "EU", "US", "US", "US", "US", "US", "US", "US", "US", "US", "US" };
			sector = new String[] { "Computer Services", "Retail", "Beverages (Nonalcoholic)",
					"Consumer Financial Services", "Aerospace & Defense", "Retail", "Communications Equipment",
					"Retail", "Beverages (Nonalcoholic)", "Consumer Financial Services", "Aerospace & Defense",
					"Retail" };
			exchangeRate = new String[] { "1", "0.82", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1" };
			marketValPOS = new String[] { "1824.97", "2041.60", "345.03", "929.20", "1818.54", "74424.00", "21645.00",
					"31680.00", "5148.00", "106400.00", "140000.00", "89600.00" };
			costValue = new String[] { "1622.11", "1861.45", "315.00", "840.00", "1710.00", "13260.65", "980.80",
					"1861.45", "315.00", "840.00", "1710.00", "355.00" };
			costValueSec = new String[] { "1632.11", "1871.45", "325.00", "850.00", "1720.00", "13360.65", "990.80",
					"1871.45", "325.00", "860.00", "1730.00", "365.00" };
			costExchangeRate = new String[] { "0.1", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0", "1.1", "1.2",
					"1.3", "1.4" };
			unRealizedPLPercentage = new String[] { "13", "34", "10", "11", "6", "14", "31", "15", "51", "12", "22",
					"17" };
			unRealizedPLPercentageSec = new String[] { "14", "35", "11", "12", "7", "6", "14", "31", "15", "51", "12",
					"22" };
			dailyPL = new String[] { "145.5", "85.9", "44.5", "-30.2", "-20.4", "60.5", "75.6", "95.9", "54.5", "-20.2",
					"-40.4", "-54.5" };
			dailyPLPercentage = new String[] { "145.05", "85.9", "44.5", "-30.02", "-20.4", "70.5", "85.6", "75.9",
					"64.5", "-10.2", "-50.4", "-64.5" };
			isSecurityAsset = new boolean[] { true, true, true, true, true, false, false, false, false, false, false,
					false };
			isAdvisory = new boolean[] { false, false, false, false, false, false, false, false, false, false, false,
					false };
			application = new String[] { "SC", "SC", "SC", "SC", "SC", "DX", "DX", "DX", "DX", "DX", "DX", "DX" };

			amountBought = new String[] { "95000.00", "125000.00", "80000.00" };
			amountSold = new String[] { "35000.00", "24000.00", "25000.00" };
			quote = new String[] { "0.75", "0.73", "0.65" };
			costQuote = new String[] { "0.86", "0.83", "0.71" };
			counterpartAmount = new String[] { "35000.00", "24000.00", "25000.00" };

		} else if (portfolioId.equalsIgnoreCase("100777-4")) {
			holdingsId = new String[] { "100077-000", "100021-000", "100050-000", "100051-000", "100016-000",
					"100020-000", "100086-000", "100017-000", "100018-000", "100019-000", "100022-000", "100093-000" };
			instrumentId = new String[] { "100077-000", "100021-000", "100050-000", "100051-000", "100016-000",
					"100020-000", "100086-000", "100017-000", "100018-000", "100019-000", "100022-000", "100093-000" };
			description = new String[] { "Alphabet", "Amazon.com Inc", "Apple", "LVMH", "Coca-Cola Co",
					"American Express Company", "Boeing Co", "Bank of America Corp", "Citigroup Inc",
					"General Motors Company", "Pfizer Inc", "iShares Core S&P 500 UCITS ETF" };
			ISIN = new String[] { "US02079K1079", "US0231351067", "US0378331005", "FR0000121014", "US1912161007",
					"US0258161092", "US0970231058", "US0605051046", "US1729671016", "US37045V1008", "INE182A01018",
					"IE00B5BMR087" };
			exchange = new String[] { "NYSE", "NYSE", "NYSE", "Euronext Paris", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE",
					"NYSE", "NYSE", "NYSE", };
			secCCy = new String[] { "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", };
			quantity = new String[] { "2", "4", "23", "6", "10", "15", "10", "12", "15", "8", "10", "16" };
			marketPrice = new String[] { "1824.97", "3203.53", "123.08", "497.95", "49.29", "116.15", "202.06", "30.94",
					"60.91", "52.04", "37.31", "308.75" };
			costPrice = new String[] { "1622.11", "2652.13", "98.08", "372.29", "45.00", "105.00", "190.00", "33.00",
					"55.00", "49.00", "34.00", "300.00" };
			marketValue = new String[] { "3649.94", "12814.12", "2830.84", "2987.70", "492.90", "1742.25", "2020.60",
					"371.28", "913.65", "416.32", "373.10", "4940.00" };
			unrealPLMkt = new String[] { "+405.72", "+2205.60", "+575.00", "+753.96", "+42.90", "+167.25", "+120.60",
					"-24.72", "+88.65", "+24.32", "+33.10", "+140.00" };
			unrealPLMktSec = new String[] { "+405.72", "+2205.60", "+575.00", "+753.96", "+42.90", "+167.25", "+120.60",
					"-24.72", "+88.65", "+24.32", "+33.10", "+140.00" };
			RICCode = new String[] { "GOOGL.OQ", "AMZN.OQ", "AAPL.OQ", "LVMH.PA", "KO.N", "AXP.N", "BA.NQ", "BAC.N",
					"C.N", "GM.N", "PFE.N", "IXMO461.DE" };
			weightPercentage = new String[] { "7.47", "26.21", "5.79", "6.11", "1.01", "3.56", "4.13", "0.76", "1.87",
					"0.85", "0.76", "10.11" };
			assestClass = new String[] { "Share", "Share", "Share", "Share", "Share", "Share", "Share", "Share",
					"Share", "Share", "Share", "Fund" };
			subAssetClass = new String[] { "Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares",
					"Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares",
					"Ordinary Shares", "Exchange Traded Funds", "Exchange Traded Funds" };

			region = new String[] { "US", "US", "US", "EU", "US", "US", "US", "US", "US", "US", "US", "US" };
			sector = new String[] { "Computer Services", "Retail", "Communications Equipment", "Retail",
					"Beverages (Nonalcoholic)", "Consumer Financial Services", "Aerospace & Defense", "Regional Banks",
					"Regional Banks", "Auto & Truck Manufacturers", "Biotechnology & Drugs", "Exchange Traded Funds" };
			exchangeRate = new String[] { "1", "1", "1", "0.82", "1", "1", "1", "1", "1", "1", "1", "1" };
			marketValPOS = new String[] { "3649.94", "12814.12", "2830.84", "2449.91", "492.90", "1742.25", "2020.60",
					"371.28", "913.65", "416.32", "373.10", "4940.00" };
			costValue = new String[] { "3244.22", "10608.52", "2255.84", "2233.74", "450.00", "1575.00", "1900.00",
					"396.00", "825.00", "392.00", "340.00", "4800.00" };
			costValueSec = new String[] { "3244.22", "10608.52", "2255.84", "2233.74", "450.00", "1575.00", "1900.00",
					"396.00", "825.00", "392.00", "340.00", "4800.00" };
			costExchangeRate = new String[] { "1", "1", "1", "0.82", "1", "1", "1", "1", "1", "1", "1", "1" };
			unRealizedPLPercentage = new String[] { "13", "21", "25", "34", "10", "11", "6", "-6", "11", "6", "10",
					"3" };
			unRealizedPLPercentageSec = new String[] { "13", "21", "25", "34", "10", "11", "6", "-6", "11", "6", "10",
					"3" };
			dailyPL = new String[] { "145.5", "50.5", "65.6", "85.9", "44.5", "-30.2", "-20.4", "-10.45", "23.6",
					"-10.65", "90.5", "90.5" };
			dailyPLPercentage = new String[] { "0.03", "0.15", "0.12", "0.11", "0.14", "-0.92", "0.60", "-1.20", "0.56",
					"0.45", "0.83", "0.83" };
			isSecurityAsset = new boolean[] { false, false, false, false, false, false, false, false, false, false,
					false, false };
			isAdvisory = new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true };
			application = new String[] { "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "DX" };

		} else if (portfolioId.equalsIgnoreCase("100777-5")) {
			holdingsId = new String[] { "100077-000", "100021-000", "100050-000", "100014-000", "100016-000",
					"100020-000", "100086-000", "100158-000", "100095-000", "100019-000", "100022-000" };
			instrumentId = new String[] { "100077-000", "100021-000", "100050-000", "100014-000", "100016-000",
					"100020-000", "100086-000", "100158-000", "100095-000", "100019-000", "100022-000" };
			description = new String[] { "Alphabet", "Amazon.com Inc", "Apple", "Walmart Inc", "Coca-Cola Co",
					"American Express Company", "Boeing Co", "Nestle", "Novartis", "General Motors Company",
					"Pfizer Inc" };
			ISIN = new String[] { "US02079K1079", "US0231351067", "US0378331005", "US9311421039", "US1912161007",
					"US0258161092", "US0970231058", "CH0038863350", "CH0012005267", "US37045V1008", "INE182A01018" };
			exchange = new String[] { "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "SWX", "SWX", "NYSE",
					"NYSE" };
			secCCy = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "EUR", "EUR", "USD", "USD" };
			quantity = new String[] { "2", "2", "7", "6", "6", "10", "8", "9", "12", "6", "9" };
			marketPrice = new String[] { "1824.97", "3203.53", "123.08", "148.91", "49.29", "116.15", "202.06",
					"145.00", "86.96", "52.04", "37.31" };
			costPrice = new String[] { "1622.11", "2652.13", "98.08", "132.51", "42.00", "106.00", "184.00", "131.00",
					"82.00", "50.00", "32.00" };
			marketValue = new String[] { "3649.94", "6407.06", "861.56", "893.46", "295.74", "1161.50", "1616.48",
					"1305.00", "1043.52", "312.24", "335.79" };
			unrealPLMkt = new String[] { "405.72", "1102.80", "175.00", "98.40", "43.74", "105.50", "144.48", "126.00",
					"65.52", "12.24", "52.29" };
			unrealPLMktSec = new String[] { "405.72", "1102.80", "175.00", "98.40", "43.74", "105.50", "144.48",
					"126.00", "65.52", "12.24", "52.29" };
			RICCode = new String[] { "GOOGL.OQ", "AMZN.OQ", "AAPL.OQ", "WMT.N", "KO.N", "AXP.N", "BA.NQ", "NESN.S",
					"NOVN.S", "GM.N", "PFE.N" };
			weightPercentage = new String[] { "20.06", "35.22", "4.74", "4.91", "1.63", "6.38", "8.89", "7.17", "5.74",
					"1.72", "1.85" };
			assestClass = new String[] { "Share", "Share", "Share", "Share", "Share", "Share", "Share", "Share",
					"Share", "Share", "Share" };
			subAssetClass = new String[] { "Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares",
					"Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares", "Ordinary Shares",
					"Ordinary Shares", "Exchange Traded Funds" };

			region = new String[] { "US", "US", "US", "US", "US", "US", "US", "EU", "EU", "US", "US" };
			sector = new String[] { "Computer Services", "Retail", "Communications Equipment", "Retail",
					"Beverages (Nonalcoholic)", "Consumer Financial Services", "Aerospace & Defense", "Food processing",
					"Pharmaceuticals", "Auto & Truck Manufacturers", "Biotechnology & Drugs" };
			exchangeRate = new String[] { "1", "1", "1", "1", "1", "1", "1", "0.93", "0.93", "1", "1" };
			marketValPOS = new String[] { "3649.94", "6407.06", "861.56", "893.46", "295.74", "1161.50", "1616.48",
					"1213.65", "970.47", "312.24", "335.79" };
			costValue = new String[] { "3244.22", "5304.26", "686.56", "795.06", "252.00", "1056.00", "1472.00",
					"1179.00", "978.00", "300.00", "283.50" };
			costValueSec = new String[] { "3244.22", "5304.26", "686.56", "795.06", "252.00", "1056.00", "1472.00",
					"1179.00", "978.00", "300.00", "283.50" };
			costExchangeRate = new String[] { "1", "1", "1", "1", "1", "1", "1", "0.93", "0.93", "1", "1" };
			unRealizedPLPercentage = new String[] { "13", "21", "25", "12", "17", "10", "10", "11", "7", "4", "18" };
			unRealizedPLPercentageSec = new String[] { "13", "21", "25", "12", "17", "10", "10", "11", "7", "4", "18" };
			dailyPL = new String[] { "5.34", "4.56", "-1.45", "2.46", "4.42", "-3.56", "4.44", "-4.89", "6.9", "2.24",
					"1.56" };
			dailyPLPercentage = new String[] { "0.10", "0.11", "-0.34", "0.13", "0.10", "-0.23", "0.10", "-0.34",
					"0.32", "0.12", "0.14" };
			isSecurityAsset = new boolean[] { false, false, false, false, false, false, false, false, false, false,
					false };
			isAdvisory = new boolean[] { true, true, true, true, true, true, true, true, true, true, true };
			application = new String[] { "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC" };
		}

		for (int i = 0; i < description.length; i++) {
			JSONObject holdingsObj = new JSONObject();
			holdingsObj.put(TemenosConstants.HOLDINGSID, holdingsId[i]);
			holdingsObj.put(TemenosConstants.INSTRUMENTID, instrumentId[i]);
			holdingsObj.put(TemenosConstants.DESCRIPTION, description[i]);
			holdingsObj.put("ISIN", ISIN[i]);
			holdingsObj.put("holdingsType", exchange[i]);
			holdingsObj.put(TemenosConstants.QUANTITY, quantity[i]);
			holdingsObj.put(TemenosConstants.MARKETPRICE, marketPrice[i]);
			holdingsObj.put(TemenosConstants.COSTPRICE, costPrice[i]);
			holdingsObj.put(TemenosConstants.MARKETVALUE, marketValue[i]);
			holdingsObj.put(TemenosConstants.UNREALPLMKT, unrealPLMkt[i]);
			holdingsObj.put("secCCy", secCCy[i]);
			holdingsObj.put(TemenosConstants.RICCODE, RICCode[i]);
			holdingsObj.put(TemenosConstants.UNREALPLMKTSECCCY, unrealPLMktSec[i]);
			holdingsObj.put(TemenosConstants.UNREALIZEDPLPERCENTAGESECCCY, unRealizedPLPercentageSec[i]);
			holdingsObj.put(TemenosConstants.COSTVALUESECCCY, costValueSec[i]);
			holdingsObj.put(TemenosConstants.WEIGHTPERCENTAGE, weightPercentage[i]);
			holdingsObj.put(TemenosConstants.ASSESTCLASS, assestClass[i]);
			holdingsObj.put(TemenosConstants.SUBASSETCLASS, subAssetClass[i]);
			holdingsObj.put(TemenosConstants.REGION, region[i]);
			holdingsObj.put(TemenosConstants.SECTOR, sector[i]);
			holdingsObj.put(TemenosConstants.EXCHANGERATE, exchangeRate[i]);
			holdingsObj.put(TemenosConstants.MARKETVALPOS, marketValPOS[i]);
			holdingsObj.put(TemenosConstants.COSTVALUE, costValue[i]);
			holdingsObj.put(TemenosConstants.COSTEXCHANGERATE, costExchangeRate[i]);
			holdingsObj.put(TemenosConstants.UNREALIZEDPLPERCENTAGE, unRealizedPLPercentage[i]);
			holdingsObj.put(TemenosConstants.DAILYPL, dailyPL[i]);
			holdingsObj.put(TemenosConstants.DAILYPLPERCENTAGE, dailyPLPercentage[i]);
			holdingsObj.put(TemenosConstants.ISSECURITYASSET, isSecurityAsset[i]);
			holdingsObj.put(TemenosConstants.APPLICATION, application[i]);
			holdingsObj.put(TemenosConstants.ISADVISORY, isAdvisory[i]);

			if (portfolioId.equalsIgnoreCase("100777-1")) {
				/*
				 * if (i >= 12) { holdingsObj.put("averageCost", averageCost[averageCostIndex]);
				 * averageCostIndex++; } if (i >= 15 && i <= 17) { holdingsObj.put("nominal",
				 * nominal[nominalIndex]); nominalIndex++; }
				 */
				if (i >= 13 && i <= 15) {
					holdingsObj.put("accruedInterest", accruedInterest[accruedInterestIndex]);
					accruedInterestIndex++;
				}
			} else if (portfolioId.equalsIgnoreCase("100777-2")) {
				/*
				 * if (i >= 11) { holdingsObj.put("averageCost", averageCost[averageCostIndex]);
				 * averageCostIndex++; } if (i >= 13) { holdingsObj.put("nominal",
				 * nominal[nominalIndex]); nominalIndex++; }
				 */
				if (i >= 11 && i <= 13) {
					holdingsObj.put("accruedInterest", accruedInterest[accruedInterestIndex]);
					accruedInterestIndex++;
				}
				if (i >= 11 && i <= 13) {
					holdingsObj.put("balance", balance[balanceIndex]);
					balanceIndex++;
				}
				/*
				 * if(i>=13 && i<=15) { holdingsObj.remove(TemenosConstants.MARKETVALPOS);
				 * holdingsObj.remove(TemenosConstants.UNREALPLMKTSECCCY);
				 * holdingsObj.remove(TemenosConstants.UNREALIZEDPLPERCENTAGESECCCY);
				 * holdingsObj.remove(TemenosConstants.UNREALIZEDPLPERCENTAGE);
				 * holdingsObj.remove(TemenosConstants.UNREALPLMKT);
				 * holdingsObj.remove(TemenosConstants.COSTPRICE); }
				 */
			} else if (portfolioId.equalsIgnoreCase("100777-3")) {
				/*
				 * if (i >= 7 && i <= 10) { holdingsObj.put("averageCost",
				 * averageCost[averageCostIndex]); averageCostIndex++;
				 * holdingsObj.put("nominal", nominal[nominalIndex]); nominalIndex++; }
				 */
				if (i >= 9) {
					holdingsObj.put("amountBought", amountBought[accruedInterestIndex]);
					accruedInterestIndex++;
					holdingsObj.put("amountSold", amountSold[amountSoldIndex]);
					amountSoldIndex++;
					holdingsObj.put("quote", quote[quoteIndex]);
					quoteIndex++;
					holdingsObj.put("costQuote", costQuote[costQuoteIndex]);
					costQuoteIndex++;
					holdingsObj.put("counterpartAmount", counterpartAmount[counterpartAmountIndex]);
					counterpartAmountIndex++;
					// holdingsObj.remove(TemenosConstants.QUANTITY);
					// holdingsObj.remove(TemenosConstants.COSTPRICE);
				}

			}

			holdingsArr.put(holdingsObj);
		}
		// holdingsArr.put(holdingsObj);
		JSONArray sortedJSON = new JSONArray();
		if (sortBy.equals("") || sortBy.equalsIgnoreCase(TemenosConstants.DESCRIPTION)) {
			List<JSONObject> jsonValues = new ArrayList<JSONObject>();
			for (int i = 0; i < holdingsArr.length(); i++) {
				jsonValues.add(holdingsArr.getJSONObject(i));
			}
			Collections.sort(jsonValues, new Comparator<JSONObject>() {

				private final String KEY_NAME = TemenosConstants.DESCRIPTION;

				@Override
				public int compare(JSONObject a, JSONObject b) {
					String str1 = new String();
					String str2 = new String();
					str1 = a.has(KEY_NAME) ? (String) a.get(KEY_NAME) : "";
					str2 = b.has(KEY_NAME) ? (String) b.get(KEY_NAME) : "";
					return str1.compareToIgnoreCase(str2);
				}

			});

			if (sortType != null && sortType.equalsIgnoreCase(TemenosConstants.DESCENDING)) {
				for (int i = holdingsArr.length() - 1; i >= 0; i--) {
					sortedJSON.put(jsonValues.get(i));
				}
			} else {
				for (int i = 0; i < holdingsArr.length(); i++) {
					sortedJSON.put(jsonValues.get(i));
				}
			}

		} else if (sortBy.equalsIgnoreCase(TemenosConstants.ASSESTCLASS)
				|| sortBy.equalsIgnoreCase(TemenosConstants.REGION) || sortBy.equalsIgnoreCase(TemenosConstants.SECTOR)
				|| sortBy.equalsIgnoreCase(TemenosConstants.SECCCY)) {
			List<JSONObject> jsonValues = new ArrayList<JSONObject>();
			for (int i = 0; i < holdingsArr.length(); i++) {
				jsonValues.add(holdingsArr.getJSONObject(i));
			}
			Collections.sort(jsonValues, new Comparator<JSONObject>() {

				private final String KEY_NAME = sortBy;

				@Override
				public int compare(JSONObject a, JSONObject b) {
					String str1 = new String();
					String str2 = new String();
					str1 = a.has(KEY_NAME) ? (String) a.get(KEY_NAME) : "";
					str2 = b.has(KEY_NAME) ? (String) b.get(KEY_NAME) : "";
					return str1.compareToIgnoreCase(str2);
				}

			});

			if (sortType != null && sortType.equalsIgnoreCase(TemenosConstants.DESCENDING)) {
				for (int i = holdingsArr.length() - 1; i >= 0; i--) {
					sortedJSON.put(jsonValues.get(i));
				}
			} else {
				for (int i = 0; i < holdingsArr.length(); i++) {
					sortedJSON.put(jsonValues.get(i));
				}
			}

		} else {
			List<JSONObject> jsonValues = new ArrayList<JSONObject>();
			for (int i = 0; i < holdingsArr.length(); i++) {
				jsonValues.add(holdingsArr.getJSONObject(i));
			}
			Collections.sort(jsonValues, new Comparator<JSONObject>() {
				private final String KEY_NAME = sortBy;

				@Override
				public int compare(JSONObject a, JSONObject b) {
					Double dbl1 = null;
					Double dbl2 = null;
					dbl1 = (a.has(KEY_NAME) && a.get(KEY_NAME).toString().length() > 0)
							? Double.parseDouble((a.get(KEY_NAME)).toString())
							: 0;
					dbl2 = (b.has(KEY_NAME) && b.get(KEY_NAME).toString().length() > 0)
							? Double.parseDouble((b.get(KEY_NAME)).toString())
							: 0;
					// str1 = (Double) a.getDouble(KEY_NAME);
					// str2 = (Double) b.getDouble(KEY_NAME);
					return dbl1.compareTo(dbl2);
				}
			});
			if (sortType != null && sortType.equalsIgnoreCase(TemenosConstants.DESCENDING)) {
				for (int i = holdingsArr.length() - 1; i >= 0; i--) {
					sortedJSON.put(jsonValues.get(i));
				}

			} else {
				for (int i = 0; i < holdingsArr.length(); i++) {
					sortedJSON.put(jsonValues.get(i));
				}
			}
		}

		if (search.equals("")) {
		} else {
			HoldingsListBackendDelegateImpl holdingsListBackendDelegate = new HoldingsListBackendDelegateImpl();
			sortedJSON = holdingsListBackendDelegate.returnSearch(sortedJSON, search, "");
		}

		if (instrumentid.equals("")) {
		} else {
			HoldingsListBackendDelegateImpl holdingsListBackendDelegate = new HoldingsListBackendDelegateImpl();
			sortedJSON = holdingsListBackendDelegate.returnInstrumentID(sortedJSON, instrumentid);
		}

		totalCount = sortedJSON.length();
		if (limit > 0 && offset >= 0) {
			sortedJSON = pagination(sortedJSON, limit, offset);
		}
		if (portfolioId.equalsIgnoreCase("100777-1")) {
			response.put("portfolioID", portfolioId);
			response.put("referenceCurrency", "USD");
			response.put("marketValue", "34243.82");
			response.put("unRealizedPL", "P");
			response.put("unRealizedPLAmount", "5023.50");
			response.put("unRealizedPLPercentage", "21");
			response.put("todayPL", "P");
			response.put("todayPLAmount", "498.23");
			response.put("accountName", "John Bailey Portfolio 1.");
			response.put("accountNumber", "100777-1");
			response.put("todayPLPercentage", "2.12");
		} else if (portfolioId.equalsIgnoreCase("100777-2")) {
			response.put("referenceCurrency", "USD");
			response.put("marketValue", "16543.15");
			response.put("unRealizedPL", "P");
			response.put("unRealizedPLAmount", "2174.55");
			response.put("unRealizedPLPercentage", "15");
			response.put("todayPL", "P");
			response.put("todayPLAmount", "1593.21");
			response.put("todayPLPercentage", "11.09");
			response.put("accountName", "John Bailey Portfolio 2.");
			response.put("accountNumber", "100777-2");
			response.put("portfolioID", portfolioId);
		} else if (portfolioId.equalsIgnoreCase("100777-3")) {
			response.put("referenceCurrency", "USD");
			response.put("marketValue", "24655.94");
			response.put("unRealizedPL", "P");
			response.put("unRealizedPLAmount", "4065.93");
			response.put("unRealizedPLPercentage", "20");
			response.put("todayPL", "P");
			response.put("todayPLAmount", "46.00");
			response.put("todayPLPercentage", "1.15");
			response.put("accountName", "John Bailey Portfolio 3.");
			response.put("accountNumber", "100777-3");
			response.put("portfolioID", portfolioId);
		} else if (portfolioId.equalsIgnoreCase("100777-4")) {
			response.put("referenceCurrency", "USD");
			response.put("marketValue", "48881.31");
			response.put("unRealizedPL", "P");
			response.put("unRealizedPLAmount", "4532.38");
			response.put("unRealizedPLPercentage", "9.22");
			response.put("todayPL", "P");
			response.put("todayPLAmount", "510.12");
			response.put("todayPLPercentage", "1.04");
			response.put("accountName", "John Bailey Portfolio 4.");
			response.put("accountNumber", "100777-4");
			response.put("portfolioID", portfolioId);
		} else if (portfolioId.equalsIgnoreCase("100777-5")) {
			response.put("referenceCurrency", "USD");
			response.put("marketValue", "18191.27");
			response.put("unRealizedPL", "P");
			response.put("unRealizedPLAmount", "2331.69");
			response.put("unRealizedPLPercentage", "14.99");
			response.put("todayPL", "P");
			response.put("todayPLAmount", "22.11");
			response.put("todayPLPercentage", "0.14");
			response.put("accountName", "John Bailey Portfolio 5.");
			response.put("accountNumber", "100777-5");
			response.put("portfolioID", portfolioId);
		} else {
			response.put("portfolioID", portfolioId);
			response.put("referenceCurrency", "USD");
			response.put("marketValue", "34243.82");
			response.put("unRealizedPL", "P");
			response.put("unRealizedPLAmount", "5023.50");
			response.put("unRealizedPLPercentage", "21");
			response.put("todayPL", "P");
			response.put("todayPLAmount", "498.23");
			response.put("accountName", "John Bailey Portfolio 1.");
			response.put("accountNumber", "100777-1");
			response.put("todayPLPercentage", "2.12");
		}
		String[] colArray = new String[] { TemenosConstants.HOLDINGSID, "holdingsType", TemenosConstants.MARKETPRICE,
				"ISIN", TemenosConstants.MARKETVALPOS, TemenosConstants.WEIGHTPERCENTAGE, TemenosConstants.UNREALPLMKT,
				TemenosConstants.REGION, TemenosConstants.ASSESTCLASS, TemenosConstants.SECTOR, TemenosConstants.SECCCY,
				TemenosConstants.MARKETVALUE, TemenosConstants.COSTVALUE, TemenosConstants.UNREALIZEDPLPERCENTAGE,
				TemenosConstants.QUANTITY, TemenosConstants.COSTPRICE, TemenosConstants.DESCRIPTION,
				TemenosConstants.RICCODE, TemenosConstants.EXCHANGERATE, TemenosConstants.COSTEXCHANGERATE,
				TemenosConstants.DAILYPL, TemenosConstants.DAILYPLPERCENTAGE,
				TemenosConstants.UNREALIZEDPLPERCENTAGESECCCY, TemenosConstants.UNREALPLMKTSECCCY,
				TemenosConstants.COSTVALUESECCCY };

		String fieldValue = Arrays.toString(colArray).replace("[", "").replace("]", "");
		response.put("portfolioID", portfolioId);
		response.put("portfolioHoldings", sortedJSON);
		response.put("fieldstoDisplay", fieldValue);
		response.put(TemenosConstants.SORTBY, sortBy);
		response.put("totalCount", totalCount);
		response.put("opstatus", "0");
		response.put("httpStatusCode", "200");
		return response;
	}

	public JSONArray pagination(JSONArray sortedJSON, int limit, int offset) {

		JSONArray paginationJSON = new JSONArray();

		int j = 0;
		for (int i = offset; i < sortedJSON.length(); i++) {
			if (j == limit) {
				break;
			} else {
				paginationJSON.put(sortedJSON.get(i));
			}
			j++;
		}

		return paginationJSON;

	}

	/**
	 * (INFO) Fetches the mock Instrument Details for a given RIC Code. The input
	 * String is the RIC Code. The values can be changed in the response Object.
	 * Alternately, additional instruments can be added for mocking
	 * 
	 * @param isinCode
	 * @return {@link JSONObject}
	 * @author 22950
	 */

	public JSONObject getInstrumentDetails(String isinCode) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY");
		Date date = new Date();
		JSONObject response = new JSONObject();
		// JSONObject assetObj = new JSONObject();

		if (isinCode.equalsIgnoreCase("AMZN.OQ")) {

			response.put("instrumentName", "Amazon.com Inc");
			response.put("ISINCode", "US0231351067");
			response.put("stockExchange", "NYSE");
			response.put(TemenosConstants.INSTRUMENTID, "100021-000");
			/*
			 * assetObj.put("sector", "Retail"); assetObj.put("region", "North America");
			 * assetObj.put("country", "USA"); assetObj.put("assetType", "Stock");
			 */
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "3203.53");
			response.put("closeRate", "3203.53");
			response.put("netchange", "16.55");
			response.put("percentageChange", "0.51");
			response.put("timeReceived", "12:20:00");
			response.put("dateReceived", sdf.format(date));
			/*
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */

		} else if (isinCode.equalsIgnoreCase("GOOGL.OQ")) {

			response.put("instrumentName", "Alphabet");
			response.put("ISINCode", "US02079K1079");
			response.put("stockExchange", "NYSE");
			response.put(TemenosConstants.INSTRUMENTID, "100077-000");
			/*
			 * assetObj.put("sector", "Computer Services"); assetObj.put("region",
			 * "North America"); assetObj.put("country", "USA"); assetObj.put("assetType",
			 * "Stock");
			 */

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "1824.97");
			response.put("closeRate", "1824.97");
			response.put("netchange", "29.61");
			response.put("percentageChange", "1.65");
			response.put("timeReceived", "12:20:00");
			response.put("dateReceived", sdf.format(date));
			/*
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */

		} else if (isinCode.equalsIgnoreCase("AAPL.OQ")) {

			response.put("instrumentName", "Apple");
			response.put("ISINCode", "US0378331005");
			response.put("stockExchange", "NYSE");
			/*
			 * assetObj.put("sector", "Communication Equipment"); assetObj.put("region",
			 * "North America"); assetObj.put("country", "USA"); assetObj.put("assetType",
			 * "Stock");
			 */
			response.put(TemenosConstants.INSTRUMENTID, "100050-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "123.08");
			response.put("closeRate", "123.08");
			response.put("netchange", "0.36");
			response.put("percentageChange", "0.29");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
			/*
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */

		} else if (isinCode.equalsIgnoreCase("TSLA.OQ")) {

			response.put("instrumentName", "TSLA 5.300% 15Aug2025 Corp (USD)");
			response.put("ISINCode", "USU8810LAA18");
			response.put("stockExchange", "XNYS");
			/*
			 * assetObj.put("assetType", "Bond"); assetObj.put("interestRate", "8.13");
			 * assetObj.put("nxtCoupnDate", "07/15/2021"); assetObj.put("maturityDate",
			 * "07/15/2039"); assetObj.put("duration", "20 Yrs");
			 * assetObj.put("modifiedDuration", "20 Yrs"); assetObj.put("rating", "BB");
			 */
			response.put(TemenosConstants.INSTRUMENTID, "100056-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "593.38");
			response.put("closeRate", "593.38");
			response.put("netchange", "24.56");
			response.put("percentageChange", "4.31");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			/*
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */

		} else if (isinCode.equalsIgnoreCase("GM.N")) {

			response.put("instrumentName", "General Motors, 4% 1apr2025, USD");
			response.put("ISINCode", "US37045VAG59");
			response.put("stockExchange", "XFRA");
			/*
			 * assetObj.put("assetType", "Bond"); assetObj.put("interestRate", "0.72");
			 * assetObj.put("nxtCoupnDate", "04/27/2022"); assetObj.put("maturityDate",
			 * "04/27/2022"); assetObj.put("duration", "3 Yrs");
			 * assetObj.put("modifiedDuration", "3 Yrs"); assetObj.put("rating", "BB");
			 */
			response.put(TemenosConstants.INSTRUMENTID, "100156-000");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "52.04");
			response.put("closeRate", "52.04");
			response.put("netchange", "-1.35");
			response.put("percentageChange", "-2.53");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);
			/*
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */
		} else if (isinCode.equalsIgnoreCase("F.N")) {

			response.put("instrumentName", "Ford, 8.875% 15jan2022, USD");
			response.put("ISINCode", "US345370BJ82");
			response.put("stockExchange", "XNYS");
			/*
			 * assetObj.put("assetType", "Bond"); assetObj.put("interestRate", "8.88");
			 * assetObj.put("nxtCoupnDate", "01/15/2022"); assetObj.put("maturityDate",
			 * "01/15/2022"); assetObj.put("duration", "6 Yrs");
			 * assetObj.put("modifiedDuration", "6 Yrs"); assetObj.put("rating", "AA");
			 */
			response.put(TemenosConstants.INSTRUMENTID, "100091-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "12.86");
			response.put("closeRate", "12.86");
			response.put("netchange", "1.66");
			response.put("percentageChange", "0.55");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			/*
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */
		} else if (isinCode.equalsIgnoreCase("JPM.N")) {

			response.put("instrumentName", "JPMorgan Funds - US Growth Fund A (acc)");
			response.put("ISINCode", "LU0210536198");
			response.put("stockExchange", "MFAU");
			/*
			 * assetObj.put("assetType", "Fund"); assetObj.put("sector", "Financials");
			 * assetObj.put("region", "Europe"); assetObj.put("country", "Luxumbourg");
			 * assetObj.put("issuer", "JPMorgan Asset Management"); assetObj.put("type",
			 * "Mutual Fund");
			 */
			response.put(TemenosConstants.INSTRUMENTID, "100092-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "3203.53");
			response.put("closeRate", "3203.53");
			response.put("netchange", "16.55");
			response.put("percentageChange", "0.51");
			response.put("timeReceived", "12:20:00");
			response.put("dateReceived", sdf.format(date));
//			response.put("assetTypes",assetObj);
//			response.put("isSecurityAsset",true);

		} else if (isinCode.equalsIgnoreCase("BLK")) {

			response.put("instrumentName", "iShares Core S&P 500 UCITS ETF");
			response.put("ISINCode", "IE00B5BMR087");
			response.put("stockExchange", "GER");
//			assetObj.put("assetType", "Fund");
//			assetObj.put("sector", "Large Cap");
//			assetObj.put("region", "North America");
//			assetObj.put("country", "USA");
//			assetObj.put("issuer", "Vanguard");
//			assetObj.put("type", "ETF");
			response.put(TemenosConstants.INSTRUMENTID, "100093-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "308.75");
			response.put("closeRate", "308.75");
			response.put("netchange", "0.13");
			response.put("percentageChange", "1.30");
			response.put("timeReceived", "12:20:00");
			response.put("dateReceived", sdf.format(date));
//			response.put("assetTypes",assetObj);
//			response.put("isSecurityAsset",true);

		} else if (isinCode.equalsIgnoreCase("FIDELIT.LG")) {

			response.put("instrumentName", "Fidelity S&P 500 Index USD P Acc");
			response.put("ISINCode", "IE00BYX5MS15");
			response.put("stockExchange", "NYSE");
//			assetObj.put("assetType", "Fund");
//			assetObj.put("sector", "Large Cap");
//			assetObj.put("region", "North America");
//			assetObj.put("country", "USA");
//			assetObj.put("issuer", "Fidelity");
//			assetObj.put("type", "Mutual Fund");
			response.put(TemenosConstants.INSTRUMENTID, "100094-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "3203.53");
			response.put("closeRate", "3203.53");
			response.put("netchange", "16.55");
			response.put("percentageChange", "0.51");
			response.put("timeReceived", "12:20:00");
			response.put("dateReceived", sdf.format(date));
//			response.put("assetTypes",assetObj);
//			response.put("isSecurityAsset",true);

		} else if (isinCode.equalsIgnoreCase("2YTD")) {

			/*
			 * response.put("instrumentName", "2 Year Term Deposit 1.5%");
			 * assetObj.put("assetType", "Money Market"); assetObj.put("interestRate",
			 * "1.5%"); assetObj.put("contractType", "Term Deposit");
			 * assetObj.put("maturityDate", "07/31/2021");
			 * response.put(TemenosConstants.INSTRUMENTID, "100095-000");
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */

		} else if (isinCode.equalsIgnoreCase("5YTD")) {

			/*
			 * response.put("instrumentName", "5 Year Term Deposit 3.25%");
			 * assetObj.put("assetType", "Money Market"); assetObj.put("interestRate",
			 * "3.25%"); assetObj.put("contractType", "Term Deposit");
			 * assetObj.put("maturityDate", "07/31/2021");
			 * response.put(TemenosConstants.INSTRUMENTID, "100195-000");
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */

		} else if (isinCode.equalsIgnoreCase("10YTD")) {

			/*
			 * response.put("instrumentName", "10 Year Term Deposit 4.75%");
			 * assetObj.put("assetType", "Money Market"); assetObj.put("interestRate",
			 * "4.75%"); assetObj.put("contractType", "Term Deposit");
			 * assetObj.put("maturityDate", "07/31/2021");
			 * response.put(TemenosConstants.INSTRUMENTID, "100295-000");
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */

		} else if (isinCode.equalsIgnoreCase("BA.N")) {

			response.put("instrumentName", "The Boeing Company (BA)");
			response.put("ISINCode", "US0378331005");
			response.put("stockExchange", "NYSE");
//			assetObj.put("assetType", "Future");
//			assetObj.put("underlying", "The Boeing Company (BA)");
//			assetObj.put("contractSize", "100");
//			assetObj.put("expiryDate", "07/16/2021");
			response.put(TemenosConstants.INSTRUMENTID, "100096-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "120.3");
			response.put("closeRate", "120.3");
			response.put("netchange", "-1.04");
			response.put("percentageChange", "-0.87");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
//			response.put("assetTypes",assetObj);
//			response.put("isSecurityAsset",false);

		} else if (isinCode.equalsIgnoreCase("BA.NQ")) {

			response.put("instrumentName", "Boeing Co");
			response.put("ISINCode", "US0970231058");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "202.06");
			response.put("closeRate", "202.06");
			response.put("netchange", "-1.3");
			response.put("percentageChange", "-0.64");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("AXP.N")) {

			response.put("instrumentName", "American Express Company");
			response.put("ISINCode", "US0258161092");
			response.put("stockExchange", "NYSE");
//			assetObj.put("assetType", "Future");
//			assetObj.put("underlying", "AXP American Express Co.");
//			assetObj.put("contractSize", "100");
//			assetObj.put("expiryDate", "07/16/2021");
			response.put(TemenosConstants.INSTRUMENTID, "100020-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "306.67");
			response.put("closeRate", "116.15");
			response.put("netchange", "1.66");
			response.put("percentageChange", "0.55");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
//			response.put("assetTypes",assetObj);
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("PNC.N")) {

			response.put("instrumentName", "AXP American Express Co.");
			response.put("ISINCode", "US0258161092");
			response.put("stockExchange", "NYSE");
//			assetObj.put("assetType", "Future");
//			assetObj.put("underlying", "AXP American Express Co.");
//			assetObj.put("contractSize", "100");
//			assetObj.put("expiryDate", "07/16/2021");
			response.put(TemenosConstants.INSTRUMENTID, "100097-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "116.15");
			response.put("closeRate", "116.15");
			response.put("netchange", "-5.00");
			response.put("percentageChange", "-4.13");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
//			response.put("assetTypes",assetObj);
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("CSCO.OQ")) {

			response.put("instrumentName", "Cisco Systems, Inc.");
			response.put("ISINCode", "US17275R1023");
			response.put("stockExchange", "NYSE");
//			assetObj.put("assetType", "Future");
//			assetObj.put("underlying", "Cisco Systems, Inc.");
//			assetObj.put("contractSize", "100");
//			assetObj.put("expiryDate", "07/16/2021");
			response.put(TemenosConstants.INSTRUMENTID, "100098-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "120.3");
			response.put("closeRate", "120.3");
			response.put("netchange", "-1.04");
			response.put("percentageChange", "-0.87");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
//			response.put("assetTypes",assetObj);
//			response.put("isSecurityAsset",false);

		} else if (isinCode.equalsIgnoreCase("AAPL.N")) {

			response.put("instrumentName", "APPLE-CALL-115-16JUL");
			response.put("ISINCode", "US0378331005");
			response.put("stockExchange", "NYSE");
//			assetObj.put("assetType", "Option");
//			assetObj.put("underlying", "APPLE-CALL-115-16JUL");
//			assetObj.put("contractSize", "100");
//			assetObj.put("strikePrice", "115");
//			assetObj.put("optionClass", "Call");
//			assetObj.put("optionStyle", "American");
//			assetObj.put("expiryDate", "07/16/2021");
			response.put(TemenosConstants.INSTRUMENTID, "100099-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "120.3");
			response.put("closeRate", "120.3");
			response.put("netchange", "-1.04");
			response.put("percentageChange", "-0.87");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
//			response.put("assetTypes",assetObj);
//			response.put("isSecurityAsset",false);

		} else if (isinCode.equalsIgnoreCase("GOOGL.N")) {

			response.put("instrumentName", "GOOGLE-PUT-2300-16JUL");
			response.put("ISINCode", "US02079K1079");
			response.put("stockExchange", "NYSE");
//			assetObj.put("assetType", "Option");
//			assetObj.put("underlying", "GOOGLE-PUT-2300-16JUL");
//			assetObj.put("contractSize", "100");
//			assetObj.put("strikePrice", "2300");
//			assetObj.put("optionClass", "Put");
//			assetObj.put("optionStyle", "American");
//			assetObj.put("expiryDate", "07/16/2021");
			response.put(TemenosConstants.INSTRUMENTID, "100028-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "120.3");
			response.put("closeRate", "120.3");
			response.put("netchange", "-1.04");
			response.put("percentageChange", "-0.87");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
//			response.put("assetTypes",assetObj);
//			response.put("isSecurityAsset",false);

		} else if (isinCode.equalsIgnoreCase("TXG.OQ")) {

			response.put("instrumentName", "CITI-CALL-70-16JUL");
			response.put("ISINCode", "US1729671016");
			response.put("stockExchange", "NYSE");
//			assetObj.put("assetType", "Option");
//			assetObj.put("underlying", "CITI-CALL-70-16JUL");
//			assetObj.put("contractSize", "100");
//			assetObj.put("strikePrice", "70");
//			assetObj.put("optionClass", "Call");
//			assetObj.put("optionStyle", "American");
//			assetObj.put("expiryDate", "07/16/2021");
			response.put(TemenosConstants.INSTRUMENTID, "100029-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "60.91");
			response.put("closeRate", "60.91");
			response.put("netchange", "0.35");
			response.put("percentageChange", "0.58");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
//			response.put("assetTypes",assetObj);
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("FEURUSD")) {

			/*
			 * response.put("instrumentName", "Forward EURUSD 2901 2022");
			 * assetObj.put("assetType", "Forward"); assetObj.put("maturityDate",
			 * "01/21/2022"); response.put(TemenosConstants.INSTRUMENTID, "100030-000");
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */

		} else if (isinCode.equalsIgnoreCase("FGBPUSD")) {

			/*
			 * response.put("instrumentName", "Forward GBPUSD 2901 2022");
			 * assetObj.put("assetType", "Forward"); assetObj.put("maturityDate",
			 * "01/21/2022"); response.put(TemenosConstants.INSTRUMENTID, "100130-000");
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */

		} else if (isinCode.equalsIgnoreCase("FCHFUSD")) {

			/*
			 * response.put("instrumentName", "Forward CHFUSD 2901 2022");
			 * assetObj.put("assetType", "Forward"); assetObj.put("maturityDate",
			 * "01/21/2022"); response.put(TemenosConstants.INSTRUMENTID, "100230-000");
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",true);
			 */

		} else if (isinCode.equalsIgnoreCase("AMZN.O")) {

			response.put("ISINCode", "US0231351067");
			response.put("instrumentName", "Amazon.com Inc");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "3203.53");
			response.put("closeRate", "3203.53");
			response.put("netchange", "16.55");
			response.put("percentageChange", "0.51");
			response.put("timeReceived", "12:20:00");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("GOOGL.O")) {

			response.put("ISINCode", "US02079K1079");
			response.put("instrumentName", "Google LLC");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "1776.84");
			response.put("closeRate", "1776.84");
			response.put("netchange", "4.67");
			response.put("percentageChange", "0.26");
			response.put("timeReceived", "20:59:00");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("AAPL.O")) {

			response.put("ISINCode", "US0378331005");
			response.put("instrumentName", "Apple Inc");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "123.08");
			response.put("closeRate", "123.08");
			response.put("netchange", "0.36");
			response.put("percentageChange", "0.29");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("LVMH.PA")) {

			response.put("ISINCode", "FR0000121014");
			response.put("instrumentName", "LVMH");
			response.put("stockExchange", "Euronext Paris");
			response.put("referenceCurrency", "EUR");
			response.put("marketPrice", "497.95");
			response.put("closeRate", "497.95");
			response.put("netchange", "0.15");
			response.put("percentageChange", "0.03");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("C.N")) {

			response.put("instrumentName", "Citigroup Inc");
			response.put("ISINCode", "US1729671016");
			response.put(TemenosConstants.INSTRUMENTID, "100018-000");

			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "8.86");
			response.put("closeRate", "60.91");
			response.put("netchange", "0.35");
			response.put("percentageChange", "0.58");
			response.put("timeReceived", "21:00:00");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("AMAG.OQ")) {

			response.put("ISINCode", "US02266311111");
			response.put("instrumentName", "AMAG PHARMACEUTICALS INC");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "13.75");
			response.put("closeRate", "13.75");
			response.put("netchange", "0.02");
			response.put("percentageChange", "0.15");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("AMAL.OQ")) {

			response.put("ISINCode", "US0226631085");
			response.put("instrumentName", "AMALGAMTD BANK A");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "13.15");
			response.put("closeRate", "13.15");
			response.put("netchange", "0.14");
			response.put("percentageChange", "-1.05");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("AMRN.OQ")) {

			response.put("ISINCode", "US0231112063");
			response.put("instrumentName", "AMARIN CORP");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "4.93");
			response.put("closeRate", "4.93");
			response.put("netchange", "0.09");
			response.put("percentageChange", "1.85");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("AMBA.OQ")) {

			response.put("ISINCode", "US00001210000");
			response.put("instrumentName", "AMBARELLA INC");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "60.72");
			response.put("closeRate", "60.72");
			response.put("netchange", "-0.0050");
			response.put("percentageChange", "-0.09");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("AMCX.O")) {

			response.put("ISINCode", "US00164V1035");
			response.put("instrumentName", "AMC NTWK CL A");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "28.745");
			response.put("closeRate", "28.745");
			response.put("netchange", "-0.355");
			response.put("percentageChange", "-1.25");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("MSFT.N")) {

			response.put("ISINCode", "US5949181045");
			response.put("instrumentName", "MICROSOFT CP");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "214.19");
			response.put("closeRate", "214.19");
			response.put("netchange", "-1.13");
			response.put("percentageChange", "-0.52");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("WMT.N")) {

			response.put("ISINCode", "US9311421039");
			response.put("instrumentName", "Walmart Inc");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "148.91");
			response.put("closeRate", "148.91");
			response.put("netchange", "0.15");
			response.put("percentageChange", "0.03");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("IXM0461.DE")) {

			response.put("ISINCode", "IE00B5BMR087");
			response.put("instrumentName", "iShares Core S&P 500");
			response.put("stockExchange", "GER");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "308.75");
			response.put("closeRate", "308.75");
			response.put("netchange", "0.13");
			response.put("percentageChange", "1.30");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("BAC.N")) {

			response.put("ISINCode", "US0605051046");
			response.put("instrumentName", "Bank of America Corp");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "30.94");
			response.put("closeRate", "30.94");
			response.put("netchange", "-0.22");
			response.put("percentageChange", "-0.71");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("KO.N")) {

			response.put("ISINCode", "US1912161007");
			response.put("instrumentName", "Coca-Cola Co");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "49.29");
			response.put("closeRate", "49.29");
			response.put("netchange", "0.51");
			response.put("percentageChange", "1.05");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("PFE.N")) {

			response.put("ISINCode", "INE182A01018");
			response.put("instrumentName", "Pfizer Inc");
			response.put("stockExchange", "NYSE");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "37.31");
			response.put("closeRate", "37.31");
			response.put("netchange", "0.03");
			response.put("percentageChange", "0.08");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("NESN.S")) {

			response.put("ISINCode", "CH0012005267");
			response.put("instrumentName", "Nestle");
			response.put("stockExchange", "SWX");
			response.put("referenceCurrency", "EUR");
			response.put("marketPrice", "145.00");
			response.put("closeRate", "145.00");
			response.put("netchange", "-0.22");
			response.put("percentageChange", "-0.71");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else if (isinCode.equalsIgnoreCase("NOVN.S")) {

			response.put("ISINCode", "CH0012005267");
			response.put("instrumentName", "Nestle");
			response.put("stockExchange", "SWX");
			response.put("referenceCurrency", "EUR");
			response.put("marketPrice", "86.96");
			response.put("closeRate", "86.96");
			response.put("netchange", "0.35");
			response.put("percentageChange", "0.58");
			response.put("timeReceived", "06:22:50");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);

		} else {
			response.put("ISINCode", "FRACARRE2354");
			response.put("instrumentName", "CARREFOUR SA");
			response.put("stockExchange", "Euronext Paris");
			response.put("referenceCurrency", "USD");
			response.put("marketPrice", "14.14");
			response.put("closeRate", "14.14");
			response.put("netchange", "36.61");
			response.put("percentageChange", "1.55");
			response.put("timeReceived", "22:00:00");
			response.put("dateReceived", sdf.format(date));
			response.put("isSecurityAsset", true);
		}

		/*
		 * response.put("opstatus", "0"); response.put("httpStatusCode", "200");
		 */
		return response;
	}

	public JSONArray getInstrumentMinimal(String instrumentId) {

		JSONArray responseArr = new JSONArray();
		JSONObject response = new JSONObject();
		String todayDate = (new SimpleDateFormat("dd MMM yyyy")).format(new Date()).toString().toUpperCase();
		if (instrumentId.equalsIgnoreCase("100010-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Carrizo Oil Gas");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100010-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "");
			response.put("ISINCode", "US1445771033");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100022-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Pfizer Inc");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100022-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "PFE.N");
			response.put("ISINCode", "INE182A01018");
			response.put("quantity", "6");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100158-000")) {
			response.put("stockExchange", "SWX");
			response.put("instrumentName", "Nestle");
			response.put("referenceCurrency", "EUR");
			response.put("instrumentId", "100158-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "NESN.S");
			response.put("ISINCode", "CH0038863350");
			response.put("quantity", "6");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100027-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Google LLC");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100027-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "GOOGL.O");
			response.put("ISINCode", "US02079K1079");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100077-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Alphabet");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100077-000");
			response.put("marketPrice", "9.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "GOOGL.OQ");
			response.put("ISINCode", "US02079K1079");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100044-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Amazon.com Inc");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100044-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "AMZN.O");
			response.put("ISINCode", "US0231351067");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100050-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Apple");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100050-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "AAPL.OQ");
			response.put("ISINCode", "US0378331005");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100070-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Apple Inc");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100070-000");
			response.put("marketPrice", "9.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "AAPL.O");
			response.put("ISINCode", "US0378331005");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100051-000")) {
			response.put("stockExchange", "Euronext Paris");
			response.put("instrumentName", "LVMH");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100051-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "LVMH.PA");
			response.put("ISINCode", "FR0000121014");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100014-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Walmart Inc");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100014-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "WMT.N");
			response.put("ISINCode", "US9311421039");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100015-000")) {
			response.put("stockExchange", "GER");
			response.put("instrumentName", "iShares Core S&P 500");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100015-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "IXM0461.DE");
			response.put("ISINCode", "IE00B5BMR087");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100016-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Coca-Cola Co");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100016-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "KO.N");
			response.put("ISINCode", "US1912161007");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100017-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Bank of America Corp");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100017-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "BAC.N");
			response.put("ISINCode", "US0605051046");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100018-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Citigroup Inc");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100018-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "C.N");
			response.put("ISINCode", "US1729671016");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100019-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "General Motors Company");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100019-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "GM.N");
			response.put("ISINCode", "US37045V1008");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100020-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "American Express Company");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100020-000");
			response.put("marketPrice", "116.15");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "AXP.N");
			response.put("ISINCode", "US0258161092");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100021-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Amazon.com Inc");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100021-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "AMZN.OQ");
			response.put("ISINCode", "US0231351067");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100056-000")) {
			response.put("stockExchange", "XNYS");
			response.put("instrumentName", "TSLA 5.300% 15Aug2025 Corp (USD)");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100056-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "TSLA.OQ");
			response.put("ISINCode", "USU8810LAA18");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100156-000")) {
			response.put("stockExchange", "XFRA");
			response.put("instrumentName", "General Motors, 4% 1apr2025, USD");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100156-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "GM.N");
			response.put("ISINCode", "US37045VAG59");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100091-000")) {
			response.put("stockExchange", "XNYS");
			response.put("instrumentName", "Ford, 8.875% 15jan2022, USD");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100091-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "F.N");
			response.put("ISINCode", "US345370BJ82");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100092-000")) {
			response.put("stockExchange", "MFAU");
			response.put("instrumentName", "JPMorgan Funds - US Growth Fund A (acc)");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100092-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "JPM.N");
			response.put("ISINCode", "LU0210536198");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100093-000")) {
			response.put("stockExchange", "GER");
			response.put("instrumentName", "iShares Core S&P 500 UCITS ETF");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100093-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "BLK");
			response.put("ISINCode", "IE00B5BMR087");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100094-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Fidelity S&P 500 Index USD P Acc");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100094-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "FIDELIT.LG");
			response.put("ISINCode", "IE00BYX5MS15");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100095-000")) {
			response.put("stockExchange", "SWX");
			response.put("instrumentName", "Novartis");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100094-000");
			response.put("RICCode", "NOVN.S");
			response.put("ISINCode", "CH0012005267");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
			response.put("dateReceived", todayDate);
			/*
			 * assetObj.put("assetType", "Money Market"); assetObj.put("interestRate",
			 * "1.5"); assetObj.put("contractType", "Term Deposit");
			 * assetObj.put("maturityDate", "07/31/2021");
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",false);
			 */

		} else if (instrumentId.equalsIgnoreCase("100195-000")) {
			response.put("stockExchange", "");
			response.put("instrumentName", "5 Year Term Deposit 3.25%");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100195-000");
			response.put("RICCode", "5YTD");
			response.put("ISINCode", "");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
			/*
			 * assetObj.put("assetType", "Money Market"); assetObj.put("interestRate",
			 * "3.25"); assetObj.put("contractType", "Term Deposit");
			 * assetObj.put("maturityDate", "07/31/2021");
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",false);
			 */
		} else if (instrumentId.equalsIgnoreCase("100295-000")) {
			response.put("stockExchange", "");
			response.put("instrumentName", "10 Year Term Deposit 4.75%");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100295-000");
			response.put("RICCode", "10YTD");
			response.put("ISINCode", "");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
			/*
			 * assetObj.put("assetType", "Money Market"); assetObj.put("interestRate",
			 * "4.75"); assetObj.put("contractType", "Term Deposit");
			 * assetObj.put("maturityDate", "07/31/2021");
			 * response.put("assetTypes",assetObj); response.put("isSecurityAsset",false);
			 */
		} else if (instrumentId.equalsIgnoreCase("100096-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "The Boeing Company (BA)");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100096-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "BA.N");
			response.put("ISINCode", "US0378331005");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100086-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Boeing Co");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100086-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "BA.NQ");
			response.put("ISINCode", "US0970231058");
			response.put("quantity", "10");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100097-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "AXP American Express Co.");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100097-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "PNC.N");
			response.put("ISINCode", "US0258161092");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100098-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "Cisco Systems, Inc.");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100098-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "CSCO.OQ");
			response.put("ISINCode", "US17275R1023");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100099-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "APPLE-CALL-115-16JUL");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100099-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "AAPL.N");
			response.put("ISINCode", "US0378331005");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100028-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "GOOGLE-PUT-2300-16JUL");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100028-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "GOOGL.N");
			response.put("ISINCode", "US02079K1079");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100029-000")) {
			response.put("stockExchange", "NYSE");
			response.put("instrumentName", "CITI-CALL-70-16JUL");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100029-000");
			response.put("marketPrice", "8.86");
			response.put("dateReceived", todayDate);
			response.put("RICCode", "TXG.OQ");
			response.put("ISINCode", "US1729671016");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
		} else if (instrumentId.equalsIgnoreCase("100030-000")) {
			response.put("stockExchange", "");
			response.put("instrumentName", "Forward EURUSD 2901 2022");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100030-000");
			response.put("RICCode", "FEURUSD");
			response.put("ISINCode", "");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
			/*
			 * assetObj.put("assetType", "Forward"); assetObj.put("maturityDate",
			 * "01/21/2022"); response.put("assetTypes",assetObj);
			 * response.put("isSecurityAsset",false);
			 */
		} else if (instrumentId.equalsIgnoreCase("100130-000")) {
			response.put("stockExchange", "");
			response.put("instrumentName", "Forward GBPUSD 2901 2022");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100130-000");
			response.put("RICCode", "FGBPUSD");
			response.put("ISINCode", "");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
			/*
			 * assetObj.put("assetType", "Forward"); assetObj.put("maturityDate",
			 * "01/21/2022"); response.put("assetTypes",assetObj);
			 * response.put("isSecurityAsset",false);
			 */
		} else if (instrumentId.equalsIgnoreCase("100230-000")) {
			response.put("stockExchange", "");
			response.put("instrumentName", "Forward CHFUSD 2901 2022");
			response.put("referenceCurrency", "USD");
			response.put("instrumentId", "100230-000");
			response.put("RICCode", "FCHFUSD");
			response.put("ISINCode", "");
			response.put("quantity", "1");
			response.put("instrumentCurrencyId", "USD");
			/*
			 * assetObj.put("assetType", "Forward"); assetObj.put("maturityDate",
			 * "01/21/2022"); response.put("assetTypes",assetObj);
			 * response.put("isSecurityAsset",false);
			 */
		}
		response.put("opstatus", "0");
		response.put("httpStatusCode", "200");
		responseArr.put(response);
		return responseArr;
	}

	public JSONObject getInstrumentAssets(String instrumentId) {
		JSONObject response = new JSONObject();
		JSONObject assetObj = new JSONObject();
		if (instrumentId.equalsIgnoreCase("100010-000")) {
			response.put("isSecurityAsset", false);
		} else if (instrumentId.equalsIgnoreCase("100027-000")) {
			assetObj.put("sector", "Computer Services");
			assetObj.put("region", "North America");
			assetObj.put("country", "USA");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100077-000")) {
			assetObj.put("sector", "Computer Services");
			assetObj.put("region", "North America");
			assetObj.put("country", "USA");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100044-000")) {
			assetObj.put("sector", "Retail");
			assetObj.put("region", "North America");
			assetObj.put("country", "USA");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100050-000")) {
			assetObj.put("sector", "Communications Equipment");
			assetObj.put("region", "North America");
			assetObj.put("country", "USA");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100070-000")) {
			assetObj.put("sector", "Communications Equipment");
			assetObj.put("region", "North America");
			assetObj.put("country", "USA");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100051-000")) {
			assetObj.put("sector", "Retail");
			assetObj.put("region", "EUROPE");
			assetObj.put("country", "FR");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100014-000")) {
			assetObj.put("sector", "Retail");
			assetObj.put("region", "AMERICA");
			assetObj.put("country", "US");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100016-000")) {
			assetObj.put("sector", "Beverages (Nonalcoholic)");
			assetObj.put("region", "AMERICA");
			assetObj.put("country", "US");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
			/*
			 * assetObj.put("interestRate", "5"); assetObj.put("duration", "30.0 Yrs");
			 * assetObj.put("maturityDate", "2044-01-21"); assetObj.put("rating", "BB");
			 * assetObj.put("nxtCoupnDate", "2023-01-21"); assetObj.put("modifiedDuration",
			 * "30.0 Yrs"); assetObj.put("assetType", "Bond"); response.put("assetTypes",
			 * assetObj); response.put("isSecurityAsset", true);
			 */
		} else if (instrumentId.equalsIgnoreCase("100017-000")) {
			assetObj.put("country", "US");
			assetObj.put("region", "AMERICA");
			assetObj.put("sector", "Regional Banks");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100086-000")) {
			assetObj.put("country", "US");
			assetObj.put("region", "AMERICA");
			assetObj.put("sector", "AEROSPACE & DEFENCE");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100022-000")) {
			assetObj.put("country", "US");
			assetObj.put("region", "AMERICA");
			assetObj.put("sector", "Biotechnology & Drugs");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100015-000")) {
			assetObj.put("assetType", "Fund");
			assetObj.put("sector", "Large Cap");
			assetObj.put("region", "North America");
			assetObj.put("country", "USA");
			assetObj.put("issuer", "Vanguard");
			assetObj.put("type", "ETF");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100018-000")) {

			/*
			 * assetObj.put("assetType", "Option"); assetObj.put("underlying",
			 * "CITI-CALL-70-16JUL"); assetObj.put("contractSize", "100");
			 * assetObj.put("strikePrice", "70"); assetObj.put("optionClass", "Call");
			 * assetObj.put("optionStyle", "American"); assetObj.put("expiryDate",
			 * "07/16/2021"); response.put("assetTypes", assetObj);
			 * response.put("isSecurityAsset", false);
			 */
			assetObj.put("country", "US");
			assetObj.put("region", "AMERICA");
			assetObj.put("sector", "Regional Banks");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);

		} else if (instrumentId.equalsIgnoreCase("100019-000")) {

			assetObj.put("country", "US");
			assetObj.put("region", "AMERICA");
			assetObj.put("sector", "Auto & Truck Manufacturers");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100020-000")) {

			assetObj.put("country", "US");
			assetObj.put("region", "AMERICA");
			assetObj.put("sector", "Consumer Financial Services");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);

		} else if (instrumentId.equalsIgnoreCase("100021-000")) {

			assetObj.put("sector", "Retail");
			assetObj.put("region", "North America");
			assetObj.put("country", "USA");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100056-000")) {
			assetObj.put("assetType", "Bond");
			assetObj.put("interestRate", "8.13");
			assetObj.put("nxtCoupnDate", "07/15/2021");
			assetObj.put("maturityDate", "07/15/2039");
			assetObj.put("duration", "20 Yrs");
			assetObj.put("modifiedDuration", "20 Yrs");
			assetObj.put("rating", "BB");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100156-000")) {
			assetObj.put("assetType", "Bond");
			assetObj.put("interestRate", "0.72");
			assetObj.put("nxtCoupnDate", "04/27/2022");
			assetObj.put("maturityDate", "04/27/2022");
			assetObj.put("duration", "3 Yrs");
			assetObj.put("modifiedDuration", "3 Yrs");
			assetObj.put("rating", "BB");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);

		} else if (instrumentId.equalsIgnoreCase("100091-000")) {

			assetObj.put("assetType", "Bond");
			assetObj.put("interestRate", "8.88");
			assetObj.put("nxtCoupnDate", "01/15/2022");
			assetObj.put("maturityDate", "01/15/2022");
			assetObj.put("duration", "6 Yrs");
			assetObj.put("modifiedDuration", "6 Yrs");
			assetObj.put("rating", "AA");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100092-000")) {
			assetObj.put("assetType", "Fund");
			assetObj.put("sector", "Financials");
			assetObj.put("region", "Europe");
			assetObj.put("country", "Luxumbourg");
			assetObj.put("issuer", "JPMorgan Asset Management");
			assetObj.put("type", "Mutual Fund");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100093-000")) {
			assetObj.put("assetType", "Fund");
			assetObj.put("sector", "Exchange Traded Funds");
			assetObj.put("region", "North America");
			assetObj.put("country", "USA");
			assetObj.put("issuer", "Vanguard");
			assetObj.put("type", "ETF");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);

		} else if (instrumentId.equalsIgnoreCase("100094-000")) {

			assetObj.put("assetType", "Fund");
			assetObj.put("sector", "Large Cap");
			assetObj.put("region", "North America");
			assetObj.put("country", "USA");
			assetObj.put("issuer", "Fidelity");
			assetObj.put("type", "Mutual Fund");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);

		} else if (instrumentId.equalsIgnoreCase("100095-000")) {

			assetObj.put("sector", "Pharmaceuticals");
			assetObj.put("region", "Europe");
			assetObj.put("country", "Switzerland");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100195-000")) {

			assetObj.put("assetType", "Money Market");
			assetObj.put("interestRate", "3.25");
			assetObj.put("contractType", "Term Deposit");
			assetObj.put("maturityDate", "07/31/2021");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", false);

		} else if (instrumentId.equalsIgnoreCase("100295-000")) {

			assetObj.put("assetType", "Money Market");
			assetObj.put("interestRate", "4.75");
			assetObj.put("contractType", "Term Deposit");
			assetObj.put("maturityDate", "07/31/2021");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", false);

		} else if (instrumentId.equalsIgnoreCase("100096-000")) {

			assetObj.put("assetType", "Future");
			assetObj.put("underlying", "The Boeing Company (BA)");
			assetObj.put("contractSize", "100");
			assetObj.put("expiryDate", "07/16/2021");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", false);
		} else if (instrumentId.equalsIgnoreCase("100097-000")) {

			assetObj.put("assetType", "Future");
			assetObj.put("underlying", "AXP American Express Co.");
			assetObj.put("contractSize", "100");
			assetObj.put("expiryDate", "07/16/2021");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", false);
		} else if (instrumentId.equalsIgnoreCase("100098-000")) {

			assetObj.put("assetType", "Future");
			assetObj.put("underlying", "Cisco Systems, Inc.");
			assetObj.put("contractSize", "100");
			assetObj.put("expiryDate", "07/16/2021");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", false);
		} else if (instrumentId.equalsIgnoreCase("100099-000")) {
			assetObj.put("assetType", "Option");
			assetObj.put("underlying", "Apple Inc");
			assetObj.put("contractSize", "100");
			assetObj.put("strikePrice", "115");
			assetObj.put("optionClass", "Call");
			assetObj.put("optionStyle", "American");
			assetObj.put("expiryDate", "07/16/2021");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", false);

		} else if (instrumentId.equalsIgnoreCase("100028-000")) {

			assetObj.put("assetType", "Option");
			assetObj.put("underlying", "Google Inc");
			assetObj.put("contractSize", "100");
			assetObj.put("strikePrice", "2300");
			assetObj.put("optionClass", "Put");
			assetObj.put("optionStyle", "American");
			assetObj.put("expiryDate", "07/16/2021");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", false);
		} else if (instrumentId.equalsIgnoreCase("100029-000")) {

			assetObj.put("country", "US");
			assetObj.put("region", "AMERICA");
			assetObj.put("sector", "Regional Banks");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		} else if (instrumentId.equalsIgnoreCase("100030-000")) {

			assetObj.put("assetType", "Forward");
			assetObj.put("maturityDate", "01/21/2022");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", false);

		} else if (instrumentId.equalsIgnoreCase("100130-000")) {

			assetObj.put("assetType", "Forward");
			assetObj.put("maturityDate", "01/21/2022");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", false);
		} else if (instrumentId.equalsIgnoreCase("100230-000")) {

			assetObj.put("assetType", "Forward");
			assetObj.put("maturityDate", "01/21/2022");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", false);
		} else if (instrumentId.equalsIgnoreCase("100158-000")) {
			assetObj.put("sector", "Food processing");
			assetObj.put("region", "Europe");
			assetObj.put("country", "Switzerland");
			assetObj.put("assetType", "Stock");
			response.put("assetTypes", assetObj);
			response.put("isSecurityAsset", true);
		}
		return response;
	}

	/**
	 * (INFO) Fetches the mock currency conversion rate for the input currency Pair.
	 * To alter the value, change the "marketRate" data in the Response Obj. Can add
	 * further else if condition for specific currency pair rates.
	 * 
	 * @param inputMap
	 * @return {@link JSONObject}
	 * @author 22950
	 */
	public JSONObject mockMarketRates(Map<String, Object> inputMap) {
		JSONObject response = new JSONObject();
		String currencyPair = (String) inputMap.get(TemenosConstants.CURRENCYPAIRS);
		if (currencyPair.equalsIgnoreCase("EURUSD")) {
			response.put("opstatus", "0");
			response.put("marketRate", "1.16");
			response.put("httpStatusCode", "200");
		} else if (currencyPair.equalsIgnoreCase("USDEUR")) {
			response.put("opstatus", "0");
			response.put("marketRate", "0.85");
			response.put("httpStatusCode", "200");
		} else if (currencyPair.equalsIgnoreCase("USDGBP")) {
			response.put("opstatus", "0");
			response.put("marketRate", "0.78");
			response.put("httpStatusCode", "200");
		} else if (currencyPair.equalsIgnoreCase("GBPUSD")) {
			response.put("opstatus", "0");
			response.put("marketRate", "1.30");
			response.put("httpStatusCode", "200");
		} else {
			response.put("opstatus", "0");
			response.put("marketRate", "1.12");
			response.put("httpStatusCode", "200");
		}

		response.put("status", "success");

		return response;
	}

	public JSONObject mockNoDataReturned() {
		JSONObject response = new JSONObject();
		response.put("opstatus", "0");
		response.put("httpStatusCode", "200");
		response.put("status", "success");

		return response;
	}

	public JSONObject mockportfolioDetails(Map<String, Object> inputMap) {
		JSONObject responseJSON = new JSONObject();

		JSONObject instrumenttotResponse = new JSONObject();
		JSONArray instotalobjArray = new JSONArray();

		String portfolioId = (String) inputMap.get(TemenosConstants.PORTFOLIOID);
		String graphDuration = (String) inputMap.get(TemenosConstants.GRAPHDURATION);
		String navPage = (String) inputMap.get("navPage");
		if (portfolioId.equalsIgnoreCase("100777-1")) {
			instrumenttotResponse.put("portfolioID", portfolioId);
			instrumenttotResponse.put("referenceCurrency", "USD");
			instrumenttotResponse.put("marketValue", "49572.43");
			instrumenttotResponse.put("unRealizedPL", "P");
			instrumenttotResponse.put("unRealizedPLAmount", "5023.50");
			instrumenttotResponse.put("unRealizedPLPercentage", "11.33");
			instrumenttotResponse.put("todayPL", "P");
			instrumenttotResponse.put("todayPLAmount", "510.12");
			instrumenttotResponse.put("accountName", "John Bailey Portfolio 1.");
			instrumenttotResponse.put("accountNumber", "100777-1");
			instrumenttotResponse.put("todayPLPercentage", "1.33");
		} else if (portfolioId.equalsIgnoreCase("100777-2")) {
			instrumenttotResponse.put("referenceCurrency", "USD");
			instrumenttotResponse.put("marketValue", "16852.13");
			instrumenttotResponse.put("unRealizedPL", "P");
			instrumenttotResponse.put("unRealizedPLAmount", "2174.55");
			instrumenttotResponse.put("unRealizedPLPercentage", "20.49");
			instrumenttotResponse.put("todayPL", "P");
			instrumenttotResponse.put("todayPLAmount", "22.11");
			instrumenttotResponse.put("todayPLPercentage", "0.21");
			instrumenttotResponse.put("accountName", "John Bailey Portfolio 2.");
			instrumenttotResponse.put("accountNumber", "100777-2");
			instrumenttotResponse.put("portfolioID", portfolioId);
		} else if (portfolioId.equalsIgnoreCase("100777-3")) {
			instrumenttotResponse.put("referenceCurrency", "USD");
			instrumenttotResponse.put("marketValue", "34215.94");
			instrumenttotResponse.put("unRealizedPL", "P");
			instrumenttotResponse.put("unRealizedPLAmount", "4065.93");
			instrumenttotResponse.put("unRealizedPLPercentage", "20.00");
			instrumenttotResponse.put("todayPL", "P");
			instrumenttotResponse.put("todayPLAmount", "46.00");
			instrumenttotResponse.put("todayPLPercentage", "1.15");
			instrumenttotResponse.put("accountName", "John Bailey Portfolio 3.");
			instrumenttotResponse.put("accountNumber", "100777-3");
			instrumenttotResponse.put("portfolioID", portfolioId);
		} else {
			instrumenttotResponse.put("portfolioID", portfolioId);
			instrumenttotResponse.put("referenceCurrency", "USD");
			instrumenttotResponse.put("marketValue", "49572.43");
			instrumenttotResponse.put("unRealizedPL", "P");
			instrumenttotResponse.put("unRealizedPLAmount", "5023.50");
			instrumenttotResponse.put("unRealizedPLPercentage", "11.33");
			instrumenttotResponse.put("todayPL", "P");
			instrumenttotResponse.put("todayPLAmount", "510.12");
			instrumenttotResponse.put("accountName", "John Bailey Portfolio 1.");
			instrumenttotResponse.put("accountNumber", "100777-1");
			instrumenttotResponse.put("todayPLPercentage", "1.33");

		}
		instrumenttotResponse.put("httpStatusCode", "200");
		instrumenttotResponse.put("opstatus", "0");
		if (navPage != null && navPage.equalsIgnoreCase("Portfolio")) {
			JSONArray objGraph = getGraphData(inputMap);
			instrumenttotResponse.put(graphDuration, objGraph);
			instrumenttotResponse.put("graphDuration", graphDuration);
		}
		instotalobjArray.put(instrumenttotResponse);

		responseJSON.put("instrumentTotal", instotalobjArray);

		JSONArray assetArray = new JSONArray();
		String[] assetGroup = null, marketValue = null;
		String totalMarketValue = "", referenceCurrency = "";
		if (portfolioId.equalsIgnoreCase("100777-1")) {
			assetGroup = new String[] { "Stocks", "Cash", "Funds" };
			marketValue = new String[] { "28612.70", "15328.61", "5631.12" };
			totalMarketValue = "49572.43";
			referenceCurrency = "USD";
		} else if (portfolioId.equalsIgnoreCase("100777-2")) {
			assetGroup = new String[] { "Stocks", "Cash" };
			marketValue = new String[] { "16543.15", "308.98" };
			totalMarketValue = "16852.13";
			referenceCurrency = "USD";
		} else if (portfolioId.equalsIgnoreCase("100777-3")) {
			assetGroup = new String[] { "Stocks", "Cash" };
			marketValue = new String[] { "24655.94", "9560.00" };
			totalMarketValue = "34215.94";
			referenceCurrency = "USD";
		} else {
			assetGroup = new String[] { "Stocks", "Cash", "Funds" };
			marketValue = new String[] { "28612.70", "15328.61", "5631.12" };
			totalMarketValue = "49572.43";
			referenceCurrency = "USD";
		}
		for (int i = 0; i < assetGroup.length; i++) {
			JSONObject assetObj = new JSONObject();
			assetObj.put(TemenosConstants.ASSETGROUP, assetGroup[i]);
			assetObj.put(TemenosConstants.MARKETVALUE, marketValue[i]);
			assetArray.put(assetObj);
		}
		responseJSON.put("portfolioID", portfolioId);
		responseJSON.put(TemenosConstants.REFERENCECURRENCY, referenceCurrency);
		responseJSON.put(TemenosConstants.TOTALMARKETVALUE, totalMarketValue);
		responseJSON.put(TemenosConstants.ASSETS, assetArray);
		responseJSON.put("opstatus", "0");
		responseJSON.put("httpStatusCode", "200");

		JSONArray cashArray = new JSONArray();
		String[] currency = null, balance = null, accountName = null, currencyName = null,
				referenceCurrencyValue = null, accountNumber;
		String totalCashBalance = "", totalCashBalanceCurrency = "";
		if (portfolioId.equalsIgnoreCase("100777-1")) {
			currency = new String[] { "USD", "EUR" };
			balance = new String[] { "10312.11", "4142.79" };
			accountName = new String[] { "John Bailey A/c 1", "John Bailey A/c 2" };
			accountNumber = new String[] { "11098", "13465" };
			currencyName = new String[] { "United States Dollar", "Euro" };
			referenceCurrencyValue = new String[] { "10312.11", "5016.50" };
			totalCashBalance = "15328.61";
			totalCashBalanceCurrency = "USD";
		} else if (portfolioId.equalsIgnoreCase("100777-2")) {
			currency = new String[] { "USD", "EUR" };
			balance = new String[] { "172.40", "112.79" };
			accountName = new String[] { "John Bailey A/c 1", "John Bailey A/c 2" };
			accountNumber = new String[] { "11156", "16325" };
			currencyName = new String[] { "United States Dollar", "Euro" };
			referenceCurrencyValue = new String[] { "172.40", "136.58" };
			totalCashBalance = "308.98";
			totalCashBalanceCurrency = "USD";
		} else if (portfolioId.equalsIgnoreCase("100777-3")) {
			currency = new String[] { "USD" };
			balance = new String[] { "9560.00" };
			accountName = new String[] { "John Bailey A/c 3" };
			accountNumber = new String[] { "12573" };
			currencyName = new String[] { "United States Dollar" };
			referenceCurrencyValue = new String[] { "9560.00" };
			totalCashBalance = "9560.00";
			totalCashBalanceCurrency = "USD";
		} else {
			currency = new String[] { "USD", "EUR" };
			balance = new String[] { "10312.11", "4142.79" };
			accountName = new String[] { "John Bailey A/c 1", "John Bailey A/c 2" };
			accountNumber = new String[] { "11098", "13465" };
			currencyName = new String[] { "United States Dollar", "Euro" };
			referenceCurrencyValue = new String[] { "10312.11", "5016.50" };
			totalCashBalance = "15328.61";
			totalCashBalanceCurrency = "USD";
		}

		for (int i = 0; i < currency.length; i++) {
			JSONObject cashObj = new JSONObject();
			cashObj.put(TemenosConstants.CURRENCY, currency[i]);
			cashObj.put(TemenosConstants.BALANCE, balance[i]);
			cashObj.put(TemenosConstants.ACCOUNTNAME, accountName[i]);
			cashObj.put(TemenosConstants.ACCOUNTID, accountNumber[i]);
			cashObj.put(TemenosConstants.CURRENCYNAME, currencyName[i]);
			cashObj.put(TemenosConstants.REFERENCECURRENCYVALUE, referenceCurrencyValue[i]);
			cashArray.put(cashObj);
		}
		responseJSON.put("opstatus", "0");
		responseJSON.put("totalCashBalance", totalCashBalance);
		responseJSON.put("totalCashBalanceCurrency", totalCashBalanceCurrency);
		responseJSON.put("portfolioID", portfolioId);
		responseJSON.put("httpStatusCode", "200");
		responseJSON.put("cashAccounts", cashArray);

		return responseJSON;
	}

	/**
	 * (INFO) Fetches the mock Pricing Data Details for a given RIC Code. The input
	 * String is the RIC Code. The values can be changed in the response Object.
	 * Alternately, additional instruments can be added for mocking
	 * 
	 * @param isinCode
	 * @return {@link JSONObject}
	 * @author 22950
	 */
	public JSONObject getPricingDataDetails(String isinCode) {
		JSONObject response = new JSONObject();
		if (isinCode.equalsIgnoreCase("AMZN.O")) {
			response.put("ISINCode", "US0231351067");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "3201.12");
			response.put("bidVolume", "9160");
			response.put("askRate", "3204.32");
			response.put("askVolume", "12560");
			response.put("openRate", "3221.65");
			response.put("closeRate", "3220.08");
			response.put("volume", "132120");
			response.put("high52W", "3552.25");
			response.put("low52W", "1626.03");
			response.put("latestRate", "3203.53");
		} else if (isinCode.equalsIgnoreCase("GOOGL.O")) {
			response.put("ISINCode", "US02079K1079");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "1776.84");
		} else if (isinCode.equalsIgnoreCase("AAPL.O")) {
			response.put("ISINCode", "US0378331005");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "122.08");
			response.put("bidVolume", "6");
			response.put("askRate", "124.08");
			response.put("askVolume", "1");
			response.put("openRate", "118.88");
			response.put("closeRate", "120.3");
			response.put("volume", "20769880");
			response.put("high52W", "137.98");
			response.put("low52W", "53.1575");
			response.put("latestRate", "120.3");
		} else if (isinCode.equalsIgnoreCase("LVMH.PA")) {
			response.put("ISINCode", "FR0000121014");
			response.put("referenceCurrency", "EUR");
			response.put("bidRate", "478.45");
			response.put("bidVolume", "713");
			response.put("askRate", "476.65");
			response.put("askVolume", "288");
			response.put("openRate", "479.00");
			response.put("closeRate", "477.3");
			response.put("volume", "752");
			response.put("high52W", "489.5");
			response.put("low52W", "278.7");
			response.put("latestRate", "477.3");
		}
		///////////////////////////////////////////////////////////////////////////////////////
		else if (isinCode.equalsIgnoreCase("AMAG.OQ")) {

			response.put("ISINCode", "US02266311111");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "13.74");
			response.put("bidVolume", "5");
			response.put("askRate", "13.70");
			response.put("askVolume", "3");
			response.put("openRate", "13.75");
			response.put("closeRate", "13.73");
			response.put("volume", "742572");
			response.put("high52W", "13.80");
			response.put("low52W", "4.41");
			response.put("latestRate", "120.3");
		} else if (isinCode.equalsIgnoreCase("AMAL.O")) {

			response.put("ISINCode", "US0226631085");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "13.3");
			response.put("bidVolume", "1");
			response.put("askRate", "13.43");
			response.put("askVolume", "1");
			response.put("openRate", "13.11");
			response.put("closeRate", "13.29");
			response.put("volume", "2108");
			response.put("high52W", "19.98");
			response.put("low52W", "7.96");
			response.put("latestRate", "13.31");
		} else if (isinCode.equalsIgnoreCase("AMRN.O")) {

			response.put("ISINCode", "US0231112063");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "4.89");
			response.put("bidVolume", "36");
			response.put("askRate", "5.04");
			response.put("askVolume", "4");
			response.put("openRate", "4.84");
			response.put("closeRate", "4.93");
			response.put("volume", "1175064");
			response.put("high52W", "26.11");
			response.put("low52W", "3.36");
			response.put("latestRate", "4.93");
		} else if (isinCode.equalsIgnoreCase("AMBA.O")) {

			response.put("ISINCode", "US00001210000");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "60.87");
			response.put("bidVolume", "1");
			response.put("askRate", "60.98");
			response.put("askVolume", "1");
			response.put("openRate", "60.68");
			response.put("closeRate", "60.725");
			response.put("volume", "15445");
			response.put("high52W", "73.59");
			response.put("low52W", "36.02");
			response.put("latestRate", "60.92");
		} else if (isinCode.equalsIgnoreCase("AMCX.O")) {

			response.put("ISINCode", "US00164V1035");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "28.83");
			response.put("bidVolume", "1");
			response.put("askRate", "28.85");
			response.put("askVolume", "1");
			response.put("openRate", "28.07");
			response.put("closeRate", "28.39");
			response.put("volume", "245551");
			response.put("high52W", "42.63");
			response.put("low52W", "19.62");
			response.put("latestRate", "28.83");
		} else if (isinCode.equalsIgnoreCase("MSFT.O")) {

			response.put("ISINCode", "US5949181045");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "214.70");
			response.put("bidVolume", "23");
			response.put("askRate", "214.85");
			response.put("askVolume", "7");
			response.put("openRate", "214.75");
			response.put("closeRate", "215.32");
			response.put("volume", "804970");
			response.put("high52W", "232.86");
			response.put("low52W", "132.52");
			response.put("latestRate", "214.19");
		} else if (isinCode.equalsIgnoreCase("TSLA.O")) {

			response.put("ISINCode", "US88160R1014");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "593.0");
			response.put("bidVolume", "5");
			response.put("askRate", "593.48");
			response.put("askVolume", "1");
			response.put("openRate", "590.88");
			response.put("closeRate", "593.38");
			response.put("volume", "8435006");
			response.put("high52W", "607.77");
			response.put("low52W", "65.45");
			response.put("latestRate", "593.38");
		} else if (isinCode.equalsIgnoreCase("WMT.N")) {

			response.put("ISINCode", "US9311421039");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "149.25");
			response.put("bidVolume", "19");
			response.put("askRate", "149.80");
			response.put("askVolume", "3");
			response.put("openRate", "150.0");
			response.put("closeRate", "149.3");
			response.put("volume", "2087843");
			response.put("high52W", "153.4");
			response.put("low52W", "102.0");
			response.put("latestRate", "149.3");
		} else if (isinCode.equalsIgnoreCase("IXM0461.DE")) {

			response.put("ISINCode", "IE00B5BMR087");
			response.put("referenceCurrency", "EUR");
			response.put("bidRate", "306.65");
			response.put("bidVolume", "19");
			response.put("askRate", "307.5");
			response.put("askVolume", "3");
			response.put("openRate", "304.90");
			response.put("closeRate", "305.01");
			response.put("volume", "2087843");
			response.put("high52W", "312.90");
			response.put("low52W", "203.30");
			response.put("latestRate", "306.67");
		} else if (isinCode.equalsIgnoreCase("AMZN.OQ")) {
			response.put("ISINCode", "US0231351067");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "3203.53");
		} else if (isinCode.equalsIgnoreCase("GOOGL.OQ")) {
			response.put("ISINCode", "US02079K1079");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "1824.97");
		} else if (isinCode.equalsIgnoreCase("AAPL.OQ")) {
			response.put("ISINCode", "US0378331005");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "123.08");
		} else if (isinCode.equalsIgnoreCase("TSLA.OQ")) {
			response.put("ISINCode", "USU8810LAA18");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "593.38");
		} else if (isinCode.equalsIgnoreCase("GM.N")) {
			response.put("ISINCode", "US37045VAG59");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate",  "52.04");
		} else if (isinCode.equalsIgnoreCase("F.N")) {
			response.put("ISINCode", "US345370BJ82");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "12.86");
		} else if (isinCode.equalsIgnoreCase("JPM.N")) {
			response.put("ISINCode", "LU0210536198");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "3203.53");
		} else if (isinCode.equalsIgnoreCase("BLK")) {
			response.put("ISINCode", "IE0031442068");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "308.75");
		} else if (isinCode.equalsIgnoreCase("FIDELIT.LG")) {
			response.put("ISINCode", "IE00BYX5MS15");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "3203.53");
		} else if (isinCode.equalsIgnoreCase("BA.N")) {
			response.put("ISINCode", "US0378331005");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "120.3");
		} else if (isinCode.equalsIgnoreCase("BA.NQ")) {
			response.put("ISINCode", "US0970231058");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "202.06");
		} else if (isinCode.equalsIgnoreCase("AXP.N")) {
			response.put("ISINCode", "US0258161092");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "306.67");
		} else if (isinCode.equalsIgnoreCase("PNC.N")) {
			response.put("ISINCode", "US0258161092");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "116.15");
		} else if (isinCode.equalsIgnoreCase("CSCO.OQ")) {
			response.put("ISINCode", "US17275R1023");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "120.3");
		} else if (isinCode.equalsIgnoreCase("AAPL.N")) {
			response.put("ISINCode", "US0378331005");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "120.3");
		} else if (isinCode.equalsIgnoreCase("GOOGL.N")) {
			response.put("ISINCode", "US02079K1079");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "120.3");
		} else if (isinCode.equalsIgnoreCase("C.N")) {
			response.put("ISINCode", "US1729671016");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1769.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1787.6");
			response.put("askVolume", "12");
			response.put("openRate", "1767.875");
			response.put("closeRate", "1772.17");
			response.put("volume", "14419");
			response.put("high52W", "1816.09");
			response.put("low52W", "1009.0");
			response.put("latestRate", "8.86");
		} else if (isinCode.equalsIgnoreCase("BAC.N")) {
			response.put("ISINCode", "US0605051046");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1719.77");
			response.put("bidVolume", "10");
			response.put("askRate", "1717.6");
			response.put("askVolume", "13");
			response.put("openRate", "1717.875");
			response.put("closeRate", "1742.17");
			response.put("volume", "14409");
			response.put("high52W", "1810.09");
			response.put("low52W", "1090.0");
			response.put("latestRate", "30.94");
		} else if (isinCode.equalsIgnoreCase("KO.N")) {
			response.put("ISINCode", "US1912161007");
			response.put("referenceCurrency", "USD");
			response.put("bidRate", "1739.77");
			response.put("bidVolume", "11");
			response.put("askRate", "1785.6");
			response.put("askVolume", "10");
			response.put("openRate", "1967.875");
			response.put("closeRate", "1272.17");
			response.put("volume", "13019");
			response.put("high52W", "1716.09");
			response.put("low52W", "1089.0");
			response.put("latestRate", "49.29");
		}
		return response;
	}

	/**
	 * (INFO) Fetches the array of objects containing the x-axis labels(timestamp)
	 * and the corresponding data point to be plotted for the Portfolio details tab.
	 * The series of timestamp is created based on the input filter values such as
	 * "1Year", "1Day", "1Month" etc. Alter the list value for the corresponding
	 * symbol.
	 * 
	 * @param inputMap
	 * @return {@link JSONArray}
	 * @author 22952
	 */
	@SuppressWarnings("static-access")
	public JSONArray getGraphData(Map<String, Object> inputMap) {
		PortfolioDetailsBackendDelegateImpl portfolioDetailsBackendDelegateImpl = new PortfolioDetailsBackendDelegateImpl();
		String portfolioId = (String) inputMap.get(TemenosConstants.PORTFOLIOID);
		String graphDuration = (String) inputMap.get(TemenosConstants.GRAPHDURATION);
		String[] XaxisArray = new String[30];
		Double[] XaxisArrayVal;

		if (portfolioId.equalsIgnoreCase("100777-1")) {
			XaxisArrayVal = new Double[] { 47600.12, 48400.22, 47500.82, 48800.33, 49200.22, 49000.65, 48700.22,
					47800.12, 48700.33, 49000.01, 49000.88, 47600.68, 47800.72, 48200.33, 47600.12, 48200.43, 48700.77,
					48000.2, 49900.33, 47700.00, 47200.00, 47200.5, 47700.3, 47800.0, 48500.0, 47200.0, 48200.11,
					47700.12, 49500.23, 48700.3, 49572.43 };
		} else if (portfolioId.equalsIgnoreCase("100777-2")) {
			XaxisArrayVal = new Double[] { 14600.12, 15100.22, 15400.82, 15300.33, 16000.22, 15800.65, 15600.22,
					15300.12, 15600.33, 15800.01, 15800.88, 14600.68, 14800.72, 15000.33, 14600.12, 15000.43, 16600.77,
					15800.2, 16900.33, 15200.00, 15000.00, 15000.5, 15200.3, 15300.0, 15400.0, 15000.0, 15000.11,
					14800.12, 15400.23, 15200.3, 16852.13 };
		} else if (portfolioId.equalsIgnoreCase("100777-3")) {
			XaxisArrayVal = new Double[] { 32600.12, 33100.22, 33400.82, 33300.33, 34000.22, 33800.65, 33600.22,
					33300.12, 33600.33, 33800.01, 33800.88, 32600.68, 32800.72, 33000.33, 32600.12, 33000.43, 34600.77,
					33800.2, 34900.33, 33200.00, 33000.00, 33000.5, 33200.3, 33300.0, 33400.0, 32800.0, 33000.11,
					32800.12, 33400.23, 33200.3, 34215.94 };
		} else {
			XaxisArrayVal = new Double[] { 47600.12, 48400.22, 47500.82, 48800.33, 49200.22, 49000.65, 48700.22,
					47800.12, 48700.33, 49000.01, 49000.88, 47600.68, 47800.72, 48200.33, 47600.12, 48200.43, 48700.77,
					48000.2, 49900.33, 47700.00, 47200.00, 47200.5, 47700.3, 47800.0, 48500.0, 47200.0, 48200.11,
					47700.12, 49500.23, 48700.3, 49572.43 };
		}

		JSONArray assetArray = new JSONArray();
		if (graphDuration.equalsIgnoreCase("OneM")) {
			Date today = new Date();
			Calendar dates = Calendar.getInstance();
			dates.add(Calendar.DAY_OF_MONTH, -30);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			for (int i = 0; i < 29; i++) {
				dates.add(Calendar.DATE, 1);
				XaxisArray[i] = df.format(dates.getTime());

			}
			XaxisArray[29] = df.format(today.getTime());
			// XaxisArrayPerc = new Double[] {};
			for (int i = 0; i < XaxisArray.length; i++) {
				JSONObject response = new JSONObject();
				response.put("TIMESTAMP", XaxisArray[i].concat("+05:30"));
				response.put("AMOUNT", XaxisArrayVal[i]);
				response.put("PERCENTAGE", i + 0.25);
				assetArray.put(response);
			}
		}
		if (graphDuration.equalsIgnoreCase("OneY")) {
			if (portfolioId.equalsIgnoreCase("100777-1")) {
				XaxisArrayVal = new Double[] { 46600.12, 46500.12, 47400.22, 48500.82, 47800.33, 49200.22, 49000.65,
						48700.22, 47800.12, 48100.33, 48200.33, 48300.01, 48400.01, 48300.88, 48400.88, 40600.68,
						46700.72, 47100.33, 47200.33, 40600.12, 40700.12, 47200.43, 48700.77, 49000.2, 49600.2,
						49700.33, 47500.00, 40600.00, 47700.0, 47200.0, 47200.5, 47400.5, 47700.3, 47800.0, 48000.0,
						47200.0, 47100.0, 47200.11, 40700.12, 40700.12, 47400.23, 48000.23, 47700.3, 47300.3, 47200.21,
						49572.43 };
			} else if (portfolioId.equalsIgnoreCase("100777-2")) {
				XaxisArrayVal = new Double[] { 14600.12, 14500.12, 15100.22, 15400.82, 15300.33, 16000.22, 15800.65,
						15600.22, 15300.12, 15500.33, 15600.33, 15700.01, 15800.01, 15700.88, 15800.88, 14600.68,
						16800.72, 14900.33, 15000.33, 14600.12, 14800.12, 15000.43, 15600.77, 15800.2, 16100.2,
						16900.33, 15200.00, 15000.00, 15200.0, 15000.0, 15000.5, 15100.5, 15200.3, 15300.0, 15400.0,
						16000.0, 14900.0, 15000.11, 14800.12, 14700.12, 15100.23, 15400.23, 15200.3, 15100.3, 15000.21,
						16852.13 };
			} else if (portfolioId.equalsIgnoreCase("100777-3")) {
				XaxisArrayVal = new Double[] { 32600.12, 32500.12, 32100.22, 33400.82, 33300.33, 34000.22, 33800.65,
						33600.22, 33300.12, 33500.33, 33600.33, 33700.01, 33800.01, 33700.88, 33800.88, 32600.68,
						34800.72, 32900.33, 33000.33, 32600.12, 32800.12, 33000.43, 33600.77, 33800.2, 34100.2,
						34900.33, 33200.00, 33000.00, 33200.0, 33000.0, 33000.5, 33100.5, 33200.3, 33300.0, 33400.0,
						34000.0, 32900.0, 33000.11, 32800.12, 32700.12, 33100.23, 33400.23, 33200.3, 33100.3, 33000.21,
						34215.94 };
			} else {
				XaxisArrayVal = new Double[] { 46600.12, 46500.12, 47400.22, 48500.82, 47800.33, 49200.22, 49000.65,
						48700.22, 47800.12, 48100.33, 48200.33, 48300.01, 48400.01, 48300.88, 48400.88, 40600.68,
						46700.72, 47100.33, 47200.33, 40600.12, 40700.12, 47200.43, 48700.77, 49000.2, 49600.2,
						49700.33, 47500.00, 40600.00, 47700.0, 47200.0, 47200.5, 47400.5, 47700.3, 47800.0, 48000.0,
						47200.0, 47100.0, 47200.11, 40700.12, 40700.12, 47400.23, 48000.23, 47700.3, 47300.3, 47200.21,
						49572.43 };
			}
			String[] months = portfolioDetailsBackendDelegateImpl.nextMonths();
			XaxisArray = portfolioDetailsBackendDelegateImpl.getMonths(months);
			for (int i = 0; i < XaxisArray.length; i++) {
				JSONObject response = new JSONObject();
				response.put("TIMESTAMP", XaxisArray[i].concat("+05:30"));
				response.put("AMOUNT", XaxisArrayVal[i]);
				response.put("PERCENTAGE", i + 0.25);
				assetArray.put(response);
			}
		}
		if (graphDuration.equalsIgnoreCase("FiveY")) {
			Calendar c = Calendar.getInstance();
			Calendar backDate = Calendar.getInstance();
			int firstYear = c.getInstance().get(Calendar.YEAR) - 4;
			backDate.set(firstYear, Calendar.JANUARY, 06, 00, 00, 00);

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			XaxisArray[0] = df.format(backDate.getTime());
			int index_count = 1;
			while (backDate.before(Calendar.getInstance())) {
				backDate.add(Calendar.MONTH, 3);
				XaxisArray[index_count] = df.format(backDate.getTime());
				index_count++;
			}

			XaxisArray[index_count - 1] = df.format(c.getTime());
			for (int i = 0; i < index_count; i++) {
				JSONObject response = new JSONObject();
				response.put("TIMESTAMP", XaxisArray[i].concat("+05:30"));
				response.put("AMOUNT", XaxisArrayVal[i]);
				response.put("PERCENTAGE", i + 0.25);
				assetArray.put(response);
			}
		}
		if (graphDuration.equalsIgnoreCase("YTD")) {
			if (portfolioId.equalsIgnoreCase("100777-1")) {
				XaxisArrayVal = new Double[] { 46600.12, 46500.12, 48300.22, 48000.82, 48900.33, 49200.22, 49000.65,
						47800.22, 48500.12, 48500.33, 48700.33, 49100.01, 49300.01, 48800.88, 47700.88, 46600.68,
						47800.72, 46700.33, 48500.33, 46600.12, 46800.12, 48200.43, 47400.77, 48500.2, 49700.2,
						50000.33, 49800.12, 48200.43, 48700.00, 48200.00, 48700.0, 48500.0, 48500.5, 48600.5, 48950.3,
						48100.0, 48500.0, 48600.0, 48500.0, 48650.11, 48700.12, 46600.12, 48600.23, 48000.50, 48700.3,
						47600.3, 48200.21, 49572.43 };
			} else if (portfolioId.equalsIgnoreCase("100777-2")) {
				XaxisArrayVal = new Double[] { 14600.12, 14500.12, 15100.22, 15400.82, 15300.33, 16000.22, 16500.65,
						14600.22, 15300.12, 15500.33, 15600.33, 15700.01, 16000.01, 15700.88, 15800.88, 14600.68,
						14000.72, 14900.33, 15000.33, 14600.12, 14800.12, 15000.43, 15600.77, 15800.2, 16700.2,
						16900.33, 16700.12, 15000.43, 15200.00, 15000.00, 15200.0, 15000.0, 15000.5, 15100.5, 15200.3,
						15300.0, 15400.0, 15000.0, 14900.0, 15000.11, 14800.12, 14700.12, 15100.23, 15400.23, 15200.3,
						15100.3, 15000.21, 16852.13 };
			} else if (portfolioId.equalsIgnoreCase("100777-3")) {
				XaxisArrayVal = new Double[] { 32600.12, 32500.12, 32100.22, 33400.82, 33300.33, 34000.22, 33800.65,
						33600.22, 33300.12, 33500.33, 33600.33, 33700.01, 33800.01, 33700.88, 33800.88, 32600.68,
						34800.72, 32900.33, 33000.33, 32600.12, 32800.12, 33000.43, 33600.77, 33800.2, 34100.2,
						34900.33, 33200.00, 33000.00, 33200.0, 33000.0, 33000.5, 33100.5, 33200.3, 33300.0, 33400.0,
						34000.0, 32900.0, 33000.11, 32800.12, 32700.12, 33100.23, 33400.23, 33200.3, 33100.3, 33000.21,
						33100.3, 33000.21, 34215.94 };
			} else {
				XaxisArrayVal = new Double[] { 46600.12, 46500.12, 48300.22, 48000.82, 48900.33, 49200.22, 49000.65,
						47800.22, 48500.12, 48500.33, 48700.33, 49100.01, 49300.01, 48800.88, 47700.88, 46600.68,
						47800.72, 46700.33, 48500.33, 46600.12, 46800.12, 48200.43, 47400.77, 48500.2, 49700.2,
						50000.33, 49800.12, 48200.43, 48700.00, 48200.00, 48700.0, 48500.0, 48500.5, 48600.5, 48950.3,
						48100.0, 48500.0, 48600.0, 48500.0, 48650.11, 48700.12, 46600.12, 48600.23, 48000.50, 48700.3,
						47600.3, 48200.21, 49572.43 };
			}

			String[] months = portfolioDetailsBackendDelegateImpl.prevMonths();
			XaxisArray = portfolioDetailsBackendDelegateImpl.getPrevMonths(months);
			for (int i = 0; i < XaxisArray.length; i++) {
				JSONObject response = new JSONObject();
				response.put("TIMESTAMP", XaxisArray[i].concat("+05:30"));
				response.put("AMOUNT", XaxisArrayVal[i]);
				response.put("PERCENTAGE", i + 0.25);
				assetArray.put(response);
			}
		}
		return assetArray;

	}

	/**
	 * (INFO) Searches the array of objects containing the holdings (of the chosen
	 * portfolio) and instruments containing the keyword. The holdings Array is
	 * obtained by calling the method "mockGetHoldingsList". Each JSON object is
	 * formed by performing the for loop on all the string arrays. A new JSON Object
	 * can be created by adding values to all the string arrays.
	 * 
	 * 
	 * @param inputMap
	 * @return {@link JSONArray}
	 * @author 22952
	 */
	public JSONObject mockGetSearchList(Map<String, Object> inputMap) {
		String search = (String) inputMap.get(TemenosConstants.SEARCHBYINSTRUMENTNAME);
		String portfolioId = (String) inputMap.get(TemenosConstants.PORTFOLIOID);
		String[] description = null, instrumentId = null, holdingsId = null, ISIN = null, exchange = null,
				RICCode = null, application = null, secCCy= null, marketPrice = null, percentageChange = null;
		boolean[] isSecurityAsset = null;
		JSONObject response = new JSONObject();
		JSONObject holdingsObj = new JSONObject();
		JSONArray instrumentArr = new JSONArray();
		if(portfolioId != null) {
			
		holdingsObj = mockGetHoldingsList(inputMap);
		
		instrumentArr = holdingsObj.getJSONArray("portfolioHoldings");
		}
		
		JSONArray sortedJSON = new JSONArray();
		
		if(portfolioId != null) {
		
		if (portfolioId.equalsIgnoreCase("100777-1")) {
			instrumentId = new String[] { "100014-000","100044-000", "100052-000", "100053-000", "100054-000", "100055-000", "100059-000", "100012-000", "100013-000", "100015-000",
					"100070-000", "100056-000", "100091-000", "100092-000", "100094-000", "100101-000", "100195-000", "100295-000", "100096-000",
					"100097-000", "100158-000", "100095-000", "100027-000",
					"100098-000", "100099-000", "100028-000", "100029-000", "100030-000", "100130-000", "100230-000"};
			holdingsId = new String[] { "100014-000","100044-000", "100052-000", "100053-000", "100054-000", "100055-000", "100059-000", "100012-000", "100013-000", "100015-000",
					"100070-000", "100056-000", "100091-000", "100092-000", "100094-000", "100101-000", "100195-000", "100295-000", "100096-000",
					"100097-000", "100158-000", "100095-000", "100027-000",
					"100098-000", "100099-000", "100028-000", "100029-000", "100030-000", "100130-000", "100230-000"};
			description = new String[] { "Walmart Inc",  "Honeywell International Inc", "AMAG PHARMACEUTICALS INC", "AMALGAMTD BANK A", "AMARIN CORP", "AMBARELLA INC", "AMC NTWK CL A", "MICROSOFT CP",
					"TESLA INC", "iShares Core S&P 500", "Apple Inc", "TSLA 5.300% 15Aug2025 Corp (USD)", "Ford, 8.875% 15jan2022, USD", "JPMorgan Funds - US Growth Fund A (acc)",
					"Fidelity S&P 500 Index USD P Acc", "2 Year Term Deposit 1.5%, ", "5 Year Term Deposit 3.25%", "10 Year Term Deposit 4.75%", "The Boeing Company (BA)","AXP American Express Co.", "Nestle", "Novartis", 
					"Google LLC", "Cisco Systems, Inc.", "APPLE-CALL-115-16JUL", "GOOGLE-PUT-2300-16JUL", "CITI-CALL-70-16JUL", "Forward EURUSD 2901 2022", "Forward GBPUSD 2901 2022", "Forward CHFUSD 2901 2022",};
			ISIN = new String[] { "US9311421039", "US4385161066", "US02266311111", "US0226631085", "US0231112063", "US00001210000", "US00164V1035", "US5949181045", "US88160R1014",
					"IE00B5BMR087", "US0378331005", "USU8810LAA18", "US345370BJ82", "LU0210536198", "IE00BYX5MS15", "", "", "", "US0378331005", "US0258161092", "CH0038863350", "CH0012005267", "US02079K1079","US17275R1023", "US0378331005", "US02079K1079", "US1729671016", "", "", "",};
			exchange = new String[] { "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "GER", "NYSE", "XNYS", "XNYS",
					"MFAU", "NYSE", "", "", "", "NYSE", "NYSE", "SWX", "SWX", "NYSE","NYSE", "NYSE", "NYSE", "NYSE","", "", ""};
			RICCode = new String[] { "WMT.N", "HON.N", "AMAG.OQ", "AMAL.OQ", "AMRN.OQ", "AMBA.OQ", "AMCX.O", "MSFT.N",
					"TSLA.OQ", "IXM0461.DE", "AAPL.O", "TSLA.OQ", "F.N", "JPM.N", "FIDELIT.LG", "2YTD", "5YTD", "10YTD", "BA.N", "PNC.N", "NESN.S", "NOVN.S", "GOOGL.O", "CSCO.OQ",
					"AAPL.N", "GOOGL.N", "TXG.OQ", "FEURUSD", "FGBPUSD", "FCHFUSD" };
			isSecurityAsset = new boolean[] { true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, true, true, false, false,false, false, false, false, false, false,
					false, false,};
			application = new String[] { "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "DX", "DX", "DX", "SC", "SC", "SC", "SC", "SC","DX", "DX", "DX", "DX", "DX", "DX", "DX" };
			secCCy = new String[] {"USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD", "USD", "USD", "EUR", "EUR", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD"};
			marketPrice = new String[] {"148.91", "3203.53", "13.75", "13.15", "4.93", "60.72", "28.745", "214.19", "593.38", "306.67", "123.08", "306.67", "306.67", "306.67",
	    		     "306.67", "306.67", "306.67", "306.67", "306.67", "306.67", "", "", "1776.84","306.67","306.67","306.67","","306.67","306.67","306.67"};
			percentageChange = new String[] {"0.03","0.51", "0.15", "-1.05", "1.85", "-0.09", "-1.25", "-0.52", "4.31", "0.55", "0.29", "0.55", "0.55", "0.55", "0.55", "0.55", "0.55",
	    		      "0.55", "0.55", "0.55", "", "", "0.26" ,"0.55","0.55","0.55","","0.55","0.55","0.55"};
		} else if (portfolioId.equalsIgnoreCase("100777-2")) {
			instrumentId = new String[] { "100051-000", "100044-000", "100052-000", "100053-000", "100054-000", "100055-000", "100059-000", "100012-000", "100013-000", "100015-000",
					"100070-000", "100056-000", "100091-000", "100092-000", "100094-000", "100101-000", "100195-000", "100295-000", "100096-000",
					"100097-000", "100158-000", "100095-000", "100027-000", "100098-000", "100099-000", "100028-000", "100029-000", "100030-000", "100130-000", "100230-000", "100093-000" };
			holdingsId = new String[] { "100051-000", "100044-000", "100052-000", "100053-000", "100054-000", "100055-000", "100059-000", "100012-000", "100013-000", "100015-000",
					"100070-000", "100056-000", "100091-000", "100092-000", "100094-000", "100101-000", "100195-000", "100295-000", "100096-000",
					"100097-000", "100158-000", "100095-000", "100027-000", "100098-000", "100099-000", "100028-000", "100029-000", "100030-000", "100130-000", "100230-000", "100093-000"};
			description = new String[] { "LVMH", "Honeywell International Inc", "AMAG PHARMACEUTICALS INC", "AMALGAMTD BANK A", "AMARIN CORP", "AMBARELLA INC", "AMC NTWK CL A", "MICROSOFT CP",
					"TESLA INC", "iShares Core S&P 500", "Apple Inc", "TSLA 5.300% 15Aug2025 Corp (USD)", "Ford, 8.875% 15jan2022, USD", "JPMorgan Funds - US Growth Fund A (acc)",
					"Fidelity S&P 500 Index USD P Acc", "2 Year Term Deposit 1.5%, ", "5 Year Term Deposit 3.25%", "10 Year Term Deposit 4.75%", "The Boeing Company (BA)","AXP American Express Co.", "Nestle", "Novartis", "Google LLC", "Cisco Systems, Inc.",
					"APPLE-CALL-115-16JUL", "GOOGLE-PUT-2300-16JUL", "CITI-CALL-70-16JUL", "Forward EURUSD 2901 2022", "Forward GBPUSD 2901 2022", "Forward CHFUSD 2901 2022","iShares Core S&P 500 UCITS ETF"};
			ISIN = new String[] { "FR0000121014", "US4385161066", "US02266311111", "US0226631085", "US0231112063", "US00001210000", "US00164V1035", "US5949181045", "US88160R1014",
					"IE00B5BMR087", "US0378331005", "USU8810LAA18", "US345370BJ82", "LU0210536198", "IE00BYX5MS15", "", "", "", "US0378331005", "US0258161092", "CH0038863350", "CH0012005267", "US02079K1079","US17275R1023", "US0378331005", "US02079K1079", "US1729671016", "", "", "", "IE00B5BMR087" };
			exchange = new String[] { "Euronext Paris", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "GER", "NYSE", "XNYS", "XNYS",
					"MFAU", "NYSE", "", "", "", "NYSE", "NYSE", "SWX", "SWX", "NYSE","NYSE", "NYSE", "NYSE", "NYSE","", "", "", "GER"};
			RICCode = new String[] { "LVMH.PA", "HON.N", "AMAG.OQ", "AMAL.OQ", "AMRN.OQ", "AMBA.OQ", "AMCX.O", "MSFT.N",
					"TSLA.OQ", "IXM0461.DE", "AAPL.O", "TSLA.OQ", "F.N", "JPM.N", "FIDELIT.LG", "2YTD", "5YTD", "10YTD", "BA.N", "PNC.N", "NESN.S", "NOVN.S", "GOOGL.O", "CSCO.OQ",
					"AAPL.N", "GOOGL.N", "TXG.OQ", "FEURUSD", "FGBPUSD", "FCHFUSD", "BLK"};
			isSecurityAsset = new boolean[] { true,  true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, true, true, false, false,
					false, false, false, false, false, false, false, false, true};
			application = new String[] { "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "DX", "DX", "DX", "SC", "SC", "SC", "SC", "SC","DX", "DX", "DX", "DX", "DX", "DX", "DX", "DX" };
			secCCy = new String[] {"EUR", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD", "USD", "USD", "EUR", "EUR", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD","USD"};
			marketPrice = new String[] {"497.95","3203.53", "13.75", "13.15", "4.93", "60.72", "28.745", "214.19", "593.38", "306.67", "123.08", "306.67", "306.67", "306.67",
	    		     "306.67", "306.67", "306.67", "306.67", "306.67", "306.67", "", "", "1776.84","306.67","306.67","306.67","","306.67","306.67","306.67", "308.75"};
			percentageChange = new String[] {"0.03","0.51", "0.15", "-1.05", "1.85", "-0.09", "-1.25", "-0.52", "4.31", "0.55", "0.29", "0.55", "0.55", "0.55", "0.55", "0.55", "0.55",
	    		      "0.55", "0.55", "0.55", "", "", "0.26" ,"0.55","0.55","0.55","","0.55","0.55","0.55", "0.08"};
		} else if (portfolioId.equalsIgnoreCase("100777-3")) {
			instrumentId = new String[] { "100017-000", "100018-000", "100019-000", "100093-000", "100022-000",
					"100014-000", "100044-000", "100052-000", "100053-000", "100054-000", "100055-000", "100059-000", "100012-000", "100013-000", "100015-000",
					"100070-000", "100056-000", "100091-000", "100092-000", "100094-000", "100101-000", "100195-000", "100295-000", "100096-000",
					"100097-000", "100158-000", "100095-000","100021-000", "100077-000", "100050-000"};
			holdingsId = new String[] { "100017-000", "100018-000", "100019-000", "100093-000", "100022-000",
					"100014-000", "100044-000", "100052-000", "100053-000", "100054-000", "100055-000", "100059-000", "100012-000", "100013-000", "100015-000",
					"100070-000", "100056-000", "100091-000", "100092-000", "100094-000", "100101-000", "100195-000", "100295-000", "100096-000",
					"100097-000", "100158-000", "100095-000", "100021-000", "100077-000", "100050-000"};
			description = new String[] { "Bank of America Corp", "Citigroup Inc", "General Motors Company",
					"iShares Core S&P 500 UCITS ETF", "Pfizer Inc", "Walmart Inc", "Honeywell International Inc", "AMAG PHARMACEUTICALS INC", "AMALGAMTD BANK A", "AMARIN CORP", "AMBARELLA INC", "AMC NTWK CL A", "MICROSOFT CP",
					"TESLA INC", "iShares Core S&P 500", "Apple Inc", "TSLA 5.300% 15Aug2025 Corp (USD)", "Ford, 8.875% 15jan2022, USD", "JPMorgan Funds - US Growth Fund A (acc)",
					"Fidelity S&P 500 Index USD P Acc", "2 Year Term Deposit 1.5%, ", "5 Year Term Deposit 3.25%", "10 Year Term Deposit 4.75%", "The Boeing Company (BA)","AXP American Express Co.", "Nestle", "Novartis", "Amazon.com Inc", "Alphabet", "Apple"};
			ISIN = new String[] { "US0605051046", "US1729671016", "US37045V1008", "IE00B5BMR087", "INE182A01018",
					"US9311421039", "US4385161066", "US02266311111", "US0226631085", "US0231112063", "US00001210000", "US00164V1035", "US5949181045", "US88160R1014",
					"IE00B5BMR087", "US0378331005", "USU8810LAA18", "US345370BJ82", "LU0210536198", "IE00BYX5MS15", "", "", "", "US0378331005", "US0258161092", "CH0038863350", "CH0012005267", "US0231351067", "US02079K1079", "US0378331005" };
			exchange = new String[] { "NYSE", "NYSE", "NYSE", "GER", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "GER", "NYSE", "XNYS", "XNYS",
					"MFAU", "NYSE", "", "", "", "NYSE", "NYSE", "SWX", "SWX",  "NYSE", "NYSE", "NYSE"};
			RICCode = new String[] { "BAC.N", "C.N", "GM.N", "BLK", "PFE.N", "WMT.N", "HON.N", "AMAG.OQ", "AMAL.OQ", "AMRN.OQ", "AMBA.OQ", "AMCX.O", "MSFT.N",
					"TSLA.OQ", "IXM0461.DE", "AAPL.O", "TSLA.OQ", "F.N", "JPM.N", "FIDELIT.LG", "2YTD", "5YTD", "10YTD", "BA.N", "PNC.N", "NESN.S", "NOVN.S", "AMZN.OQ", "GOOGL.OQ", "AAPL.OQ"};
			isSecurityAsset = new boolean[] { true, true, true, true, true, true,  true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, true, true, false, false, true, true, true};
			application = new String[] { "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "DX", "DX", "DX", "SC", "SC", "SC", "SC", "SC", "SC", "SC"};
			secCCy = new String[] {"USD","USD","USD","USD","USD","USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD", "USD", "USD", "EUR", "EUR", "USD", "USD", "USD"};
			marketPrice = new String[] {"30.94","60.91","52.04", "308.75","37.31","148.91","3203.53", "13.75", "13.15", "4.93", "60.72", "28.745", "214.19", "593.38", "306.67", "123.08", "306.67", "306.67", "306.67",
	    		     "306.67", "306.67", "306.67", "306.67", "306.67", "306.67", "", "", "3203.53", "1824.97", "123.08"};
			percentageChange = new String[] {"-0.71","0.58","-2.53","0.08","0.08","0.03","0.51", "0.15", "-1.05", "1.85", "-0.09", "-1.25", "-0.52", "4.31", "0.55", "0.29", "0.55", "0.55", "0.55", "0.55", "0.55", "0.55",
	    		      "0.55", "0.55", "0.55", "", "", "0.51", "1.65", "0.29"};
		} 
		}else {
			instrumentId = new String[] {"100051-000", "100093-000", "100016-000", "100020-000", "100086-000",
					"100017-000", "100018-000", "100019-000", "100022-000", "100021-000", "100077-000", "100050-000", "100014-000", "100027-000",
					"100098-000", "100099-000", "100028-000", "100029-000", "100030-000", "100130-000", "100230-000", "100158-000", "100095-000", 
					"100044-000", "100052-000", "100053-000", "100054-000", "100055-000", "100059-000", "100012-000", "100013-000", "100015-000",
					"100070-000", "100056-000", "100091-000", "100092-000", "100094-000", "100101-000", "100195-000", "100295-000", "100096-000",
					"100097-000"};
			holdingsId = new String[] { "100051-000", "100093-000", "100016-000", "100020-000", "100086-000",
					"100017-000", "100018-000", "100019-000", "100022-000", "100021-000", "100077-000", "100050-000","100014-000", "100027-000",
					"100098-000", "100099-000", "100028-000", "100029-000", "100030-000", "100130-000", "100230-000", "100158-000", "100095-000",
					"100044-000", "100052-000", "100053-000", "100054-000", "100055-000", "100059-000", "100012-000", "100013-000", "100015-000",
					"100070-000", "100056-000", "100091-000", "100092-000", "100094-000", "100101-000", "100195-000", "100295-000", "100096-000",
					"100097-000"};
			description = new String[] { "LVMH", "iShares Core S&P 500 UCITS ETF", "Coca-Cola Co","American Express Company", "Boeing Co", "Bank of America Corp",
					"Citigroup Inc", "General Motors Company", "Pfizer Inc", "Amazon.com Inc", "Alphabet", "Apple", "Walmart Inc", "Google LLC", "Cisco Systems, Inc.",
					"APPLE-CALL-115-16JUL", "GOOGLE-PUT-2300-16JUL", "CITI-CALL-70-16JUL", "Forward EURUSD 2901 2022", "Forward GBPUSD 2901 2022", "Forward CHFUSD 2901 2022",
					"Nestle", "Novartis", "Honeywell International Inc", "AMAG PHARMACEUTICALS INC", "AMALGAMTD BANK A", "AMARIN CORP", "AMBARELLA INC", "AMC NTWK CL A", "MICROSOFT CP",
					"TESLA INC", "iShares Core S&P 500", "Apple Inc", "TSLA 5.300% 15Aug2025 Corp (USD)", "Ford, 8.875% 15jan2022, USD", "JPMorgan Funds - US Growth Fund A (acc)",
					"Fidelity S&P 500 Index USD P Acc", "2 Year Term Deposit 1.5%, ", "5 Year Term Deposit 3.25%", "10 Year Term Deposit 4.75%", "The Boeing Company (BA)","AXP American Express Co."};
			ISIN = new String[] {"FR0000121014", "IE00B5BMR087", "US1912161007", "US0258161092", "US0970231058", "US0605051046", "US1729671016", "US37045V1008", "INE182A01018",
					"US0231351067", "US02079K1079","US0378331005","US9311421039", "US02079K1079","US17275R1023", "US0378331005", "US02079K1079", "US1729671016", "", "", "",
					"CH0038863350", "CH0012005267", "US4385161066", "US02266311111", "US0226631085", "US0231112063", "US00001210000", "US00164V1035", "US5949181045", "US88160R1014",
					"IE00B5BMR087", "US0378331005", "USU8810LAA18", "US345370BJ82", "LU0210536198", "IE00BYX5MS15", "", "", "", "US0378331005", "US0258161092"};
			exchange = new String[] {"Euronext Paris", "GER", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE",
					"NYSE", "NYSE", "NYSE", "NYSE", "NYSE","NYSE", "NYSE", "NYSE", "NYSE","", "", "", "SWX", "SWX",
					"NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "GER", "NYSE", "XNYS", "XNYS",
					"MFAU", "NYSE", "", "", "", "NYSE", "NYSE"};
			RICCode = new String[] { "LVMH.PA", "BLK", "KO.N", "AXP.N", "BA.NQ", "BAC.N", "C.N", "GM.N", "PFE.N","AMZN.OQ", "GOOGL.OQ", "AAPL.OQ", "WMT.N", "GOOGL.O", "CSCO.OQ",
					"AAPL.N", "GOOGL.N", "TXG.OQ", "FEURUSD", "FGBPUSD", "FCHFUSD","NESN.S", "NOVN.S","HON.N", "AMAG.OQ", "AMAL.OQ", "AMRN.OQ", "AMBA.OQ", "AMCX.O", "MSFT.N",
					"TSLA.OQ", "IXM0461.DE", "AAPL.O", "TSLA.OQ", "F.N", "JPM.N", "FIDELIT.LG", "2YTD", "5YTD", "10YTD", "BA.N", "PNC.N"};
			isSecurityAsset = new boolean[] {true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, false, false, false,
					false, false, false,true, true, true, true, true, true, true, true, true, true, true, true, true, true, false, false, false, true, true};
			application = new String[] { "SC", "DX", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC" , "SC", "SC","DX", "DX", "DX", "DX", "DX", "DX", "DX", "SC", "SC",
					"SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "SC", "DX", "DX", "DX", "SC", "SC"};
			secCCy = new String[] {"EUR", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD","USD","USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "EUR", "EUR",
					"USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD", "USD", "USD"};
		    marketPrice = new String[] {"497.95", "308.75","49.29","306.67","202.06","30.94","60.91","52.04", "37.31","3203.53","1824.97","123.08",
		    		     "148.91","1776.84","306.67","306.67","306.67","","306.67","306.67","306.67","","",
		    		     "3203.53", "13.75", "13.15", "4.93", "60.72", "28.745", "214.19", "593.38", "306.67", "123.08", "306.67", "306.67", "306.67",
		    		     "306.67", "306.67", "306.67", "306.67", "306.67", "306.67"};
		    percentageChange = new String[] {"0.03", "0.08","1.05", "0.55","-0.64","-0.71","0.58", "-2.53", "0.08", "0.51","1.65","0.29","0.03","0.26"
		    		      ,"0.55","0.55","0.55","","0.55","0.55","0.55","","",
		    		      "0.51", "0.15", "-1.05", "1.85", "-0.09", "-1.25", "-0.52", "4.31", "0.55", "0.29", "0.55", "0.55", "0.55", "0.55", "0.55", "0.55",
		    		      "0.55", "0.55", "0.55"};
		    
		}
		for (int i = 0; i < description.length; i++) {
			JSONObject instrumentObj = new JSONObject();
			instrumentObj.put(TemenosConstants.INSTRUMENTID, instrumentId[i]);
			instrumentObj.put(TemenosConstants.HOLDINGSID, holdingsId[i]);
			instrumentObj.put(TemenosConstants.DESCRIPTION, description[i]);
			instrumentObj.put("ISIN", ISIN[i]);
			instrumentObj.put("holdingsType", exchange[i]);
			instrumentObj.put(TemenosConstants.RICCODE, RICCode[i]);
			instrumentObj.put(TemenosConstants.ISSECURITYASSET, isSecurityAsset[i]);
			instrumentObj.put(TemenosConstants.APPLICATION, application[i]);
			instrumentObj.put(TemenosConstants.SECCCY, secCCy[i]);
			instrumentObj.put(TemenosConstants.MARKETPRICE, marketPrice[i]);
			instrumentObj.put(TemenosConstants.PERCENTAGECHANGE, percentageChange[i]);
			
			instrumentArr.put(instrumentObj);
		}
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < instrumentArr.length(); i++) {
			jsonValues.add(instrumentArr.getJSONObject(i));
		}
		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			private final String KEY_NAME = TemenosConstants.DESCRIPTION;

			@Override
			public int compare(JSONObject a, JSONObject b) {
				String str1 = new String();
				String str2 = new String();
				str1 = (String) a.get(KEY_NAME);
				str2 = (String) b.get(KEY_NAME);
				return str1.compareToIgnoreCase(str2);
			}

		});
		for (int i = 0; i < instrumentArr.length(); i++) {
			sortedJSON.put(jsonValues.get(i));
		}
		if (search.equals("")) {
		} else {

			HoldingsListBackendDelegateImpl holdingsListBackendDelegate = new HoldingsListBackendDelegateImpl();
			sortedJSON = holdingsListBackendDelegate.returnSearch(sortedJSON, search, "");

		}
		response.put("instrumentList", sortedJSON);
		response.put("opstatus", "0");
		response.put("httpStatusCode", "200");

		return response;
	}

	@SuppressWarnings("rawtypes")
	public JSONArray getStockNews(String isinCode) {
		LinkedHashMap<String, String> hm1 = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> hm2 = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> hm3 = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> hm4 = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> hm5 = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> hm6 = new LinkedHashMap<String, String>();
		String[] dateValues = new String[30];

		LocalDateTime currDate = LocalDateTime.now();
		for (int j = 0; j < 20; j++) {
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

			dateValues[j] = currDate.minusHours(j).format(myFormatObj).concat("-00:00");
		}

		if (isinCode.trim().equalsIgnoreCase("AMZN.O") || isinCode.trim().equalsIgnoreCase("AMZN.OQ")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201117:nBw8fgMlna");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Amazon Named “Low Price Leader” in New Study");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201117:nL1N2I308K");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "NXP, Amazon partner to connect cars to cloud computing services");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:onlinereport.com:20201116:nRTROPT20201116231350KBN27W315");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Does vaccine promise put U.S. consumers in a shopping mood? Retailers may have clues");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:onlinereport.com:20201116:nRTROPT20201116231350KBN27W315-OCABS");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Does vaccine promise put U.S. consumers in a shopping mood? Retailers may have clues");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			hm5.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ1P2");
			hm5.put("RT", dateValues[5]);
			hm5.put("PR", "reuters.com");
			hm5.put("HT", "UPDATE 1-Loeb&apos;s Third Point funds gain in November, up double-digits YTD");
			hm5.put("LT", dateValues[5]);
			hm5.put("CT", dateValues[5]);

			hm6.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ1TX");
			hm6.put("RT", dateValues[6]);
			hm6.put("PR", "reuters.com");
			hm6.put("HT",
					"UPDATE 1-More than 400 lawmakers from 34 countries back &apos;Make Amazon Pay&apos; campaign");
			hm6.put("LT", dateValues[6]);
			hm6.put("CT", dateValues[6]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);
			al.add(hm5);
			al.add(hm6);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("GOOGL.O") || isinCode.trim().equalsIgnoreCase("GOOGL.OQ")
				|| isinCode.trim().equalsIgnoreCase("GOOGL.N")) {

			hm1.put("ID", "urn:newsml:onlinereport.com:20201117:nOLINTECH");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Reuters India Online Report Technology 	");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201116:nL1N2I221H");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT",
					"WRAPUP 2-Brazil launches &apos;Pix&apos; instant payments system, Whatsapp to enter &apos;soon&apos;");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201116:nPremltmRa");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Hedge Funds Are Betting Big On This $130 Trillion Trend");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:onlinereport.com:20201116:nRTROPT20201116173458KBN27W2DC");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT",
					"Brazil launches &apos;Pix&apos; instant payments system, Whatsapp to enter &apos;soon&apos;");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AAPL.O") || isinCode.trim().equalsIgnoreCase("AAPL.OQ")
				|| isinCode.trim().equalsIgnoreCase("AAPL.N")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201116:nPn8n5GmYa");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"WPI-MANA Team Creates Thermoelectric Device Combined with Wavelength-selective Thermal Emitter That Generates Continuous Power, Day and Night");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201109:nCNWs6qtka");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "Montage Gold Inc. Issues Stock Options");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201030:nGNX7nSvLd");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Montage Gold Corp. Announces Full Exercise of Underwriters’ Over-Allotment Option");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201029:nL4N2HK569");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "UPDATE 5-Facebook anticipates tougher 2021 even as pandemic boosts ad revenue");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("LVMH.PA")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201116:nPRrGB7ADa");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "REG-BlackRock Grtr Eur: Portfolio Update");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201109:nCNWs6qtka");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "Hennessy Announces Second Phase of Unfinished Business Funding");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201113:nBw88YMtTa");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT",
					"$422.9 Billion Worldwide Winter Wear Industry to 2027 - Impact of COVID-19 on the Market - ResearchAndMarkets.com");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201112:nPnbc3Fs4a");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT",
					"Narvar Simplifies Returns for Consumers and Retailers, Launching Digital, Boxless Returns with Cole Haan");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AMAG.OQ")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201116:nPNA4qXWga");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Covis Group Completes Acquisition of AMAG Pharmaceuticals");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201116:nPre6CRv7a");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "Covis Group Completes Acquisition of AMAG Pharmaceuticals");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201116:nPn2rN98Ta");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Covis Group Completes Acquisition of AMAG Pharmaceuticals");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201116:nCNWxYhf4a");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Covis Group Completes Acquisition of AMAG Pharmaceuticals");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AMAL.O") || isinCode.trim().equalsIgnoreCase("AMAL.OQ")) {

			hm1.put("ID", "urn:newsml:onlinereport.com:20201117:nRTROPT20201117123049KBN27X1IB-OCABS");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "TSX futures down as weaker oil, rising virus cases weigh");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:onlinereport.com:20201117:nRTROPT20201117123049KBN27X1IB-OCATP");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "TSX futures down as weaker oil, rising virus cases weigh");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201117:nL4N2I32EG");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "CANADA STOCKS-TSX futures down as weaker oil, rising virus cases weigh");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201116:nZaw3q4PRR");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "SNG: 24 women appointed to Saudi Shoura committees");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AMRN.O")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201116:nCNWyxhx6a");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"VASCEPA® (Icosapent Ethyl) Found to Significantly Reduce Ischemic Events in Patients with Prior Coronary Artery Bypass Grafting Procedures");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201113:nGNX1CLGsL");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT",
					"VASCEPA® (Icosapent Ethyl) Found to Significantly Reduce Ischemic Events in Patients with Prior Coronary Artery Bypass Grafting (CABG) Procedures in Post Hoc Subgroup Analyses of Landmark REDUCE-IT® Study Presented at American Heart Association’s Virtual Scientific Sessions 2020");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201111:nGNX9nq9Wk");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT",
					"Amarin to Present at the Stifel 2020 Virtual Healthcare and  Jefferies Virtual London Healthcare Conferences");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201109:nFWN2HV13A");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "BRIEF-ADF Foods Ltd Sept-Quarter Consol PAT Rises");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AMBA.O")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201117:nGNE6CFqqv");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "COVID-19 is a ‘wake-up command’ to address Africa’s challenges - Tony Blair");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201117:nGNX366Dj2");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "COVID-19 is a ‘wake-up command’ to address Africa’s challenges - Tony Blair");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201114:nGNE669KWF");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Moody’s Investor Service affirms African Development Bank’s AAA credit rating");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201114:nGNXc4gF4N");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Moody’s Investor Service affirms African Development Bank’s AAA credit rating");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;

		} else if (isinCode.trim().equalsIgnoreCase("AMCX.O")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201102:nASA01BFN");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-AMC Networks Inc Reports Third Quarter EPS $1.17");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201102:nGNX2kTy77");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "AMC Networks Inc. Reports Third Quarter 2020 Results");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201021:nGNX2Vqnkk");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "AMC Networks to Report Third Quarter 2020 Results");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201021:nFWN2HB15K");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "BRIEF-Amc Networks Announces Final Results Of Modified Dutch Auction Tender Offer");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("MSFT.O")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ26V");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 3-Justice Department accuses Facebook of discriminating against U.S. workers");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201203:nB8N2FR00N");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "BRIEF-Microsoft Unveils A New Data Governance Solution, Azure Purview");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ1I8");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Microsoft aims to help businesses get handle on data with new tool");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201203:nL2N2GK17L");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "FOCUS-North American farmers profit as consumers pressure food business to go green");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("TSLA.O") || isinCode.trim().equalsIgnoreCase("TSLA.OQ")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201204:nL1N2IK01K");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 1-LG Chem, SK Innovation spar over EV recalls in trade dispute");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ323");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "GLOBAL MARKETS-Asia stocks set for small gains as U.S. advances fiscal stimulus");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ2BL");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "LG Chem, SK Innovation spar over EV recalls in trade dispute");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ2OS");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "US STOCKS-Nasdaq hits record high, S&amp;P 500 ends lower");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AMRN.O")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201201:nGNX7J4hN");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Amarin Files Patent Infringement Lawsuit Against Hikma");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201124:nGNX6QdSS9");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "Amarin to Present at Piper Sandler’s 32nd Annual Healthcare Conference (Virtual)");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201120:nFWN2I519C");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "BRIEF-Amarin Shares Topline Data From Partner’s Phase 3 Study Of Vascepa In Mainland China");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201119:nGNX9pW2Dl");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT",
					"Amarin Shares Topline Data from Partner’s Pivotal Phase 3 Study of VASCEPA® (Icosapent Ethyl) in Mainland China");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("WMT.N")) {

			hm1.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ26V");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 3-Justice Department accuses Facebook of discriminating against U.S. workers");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ0BY");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "UPDATE 4-UK food retailers hand back $2.4 bln in property tax relief");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20201203:nL4N2IJ3GN");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Walmart to spend more than $700 mln on new round of employee bonuses");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20201203:nFWN2IJ0UZ");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT",
					"BRIEF-Walmart Announces More Than $700 Mln In Additional Associate Bonuses, Tops $2.8 Bln In Total Cash Bonuses To Associates In 2020");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("F.N") || isinCode.trim().equalsIgnoreCase("2YTD")
				|| isinCode.trim().equalsIgnoreCase("5YTD")) {

			hm1.put("ID", "urn:newsml:reuters.com:20210517:nL2N2N41RT");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "US STOCKS-Wall St weighed down by falling tech stocks");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20210517:nPn2m2kqsa");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "First National Bank Expands in Charleston with Branch at Freshfields Village");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20210517:nL3N2N41OT");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Chinese automaker Changan aims to list EV unit on STAR Market -sources");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20210516:nL1N2N10NH");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "European climate group says EU needs far tougher van CO2 targets");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			hm5.put("ID", "urn:newsml:reuters.com:20210514:nFWN2N10RL");
			hm5.put("RT", dateValues[5]);
			hm5.put("PR", "reuters.com");
			hm5.put("HT", "BRIEF-F.N.B. Corp Files For Potential Mixed Shelf Offering Size Not Disclosed");
			hm5.put("LT", dateValues[5]);
			hm5.put("CT", dateValues[5]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);
			al.add(hm5);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("JPM.N")) {

			hm1.put("ID", "urn:newsml:reuters.com:20210518:nD5N2K201R");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Emirates NBD hires banks for AT1 dollar bonds - document");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20210517:nFWN2N41H0");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "BRIEF-JPMorgan Chase Declares Common Stock Dividend");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20210517:nFWN2N40SA");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "BRIEF-Jpmorgan Chase Says Credit Card Charge-Off Rate 1.97% In April Versus 2.03% In March");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("BLK")) {

			hm1.put("ID", "urn:newsml:reuters.com:20210518:nRSR9606Ya");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "REG - iShares III BLK E £  - Net Asset Value(s)");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20210518:nRSR9607Ya");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "REG - iShares III BLK MAM£  - Net Asset Value(s)");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20210518:nRSR9608Ya");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "REG - iShares III BLK MAG£  - Net Asset Value(s)");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20210518:nL5N2N43A7");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Italy - Factors to watch on May 18");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("FIDELIT.LG")) {

			hm1.put("ID", "urn:newsml:reuters.com:20210315:nFWN2LD0VX");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"BRIEF-Fidelity Bank Lists 41.21 Bln Naira 8.5% Fixed Rate Unsecured Subordinated Bonds Due 2031");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("BA.N") || isinCode.trim().equalsIgnoreCase("BA.NQ")) {

			hm1.put("ID", "urn:newsml:reuters.com:20210517:nL2N2N41GJ");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Biden administration approved $735 million arms sale to Israel - sources");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20210517:nL2N2N40YK");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "UPDATE 2-Emirates could swap Boeing 777X jets for smaller Dreamliners, chairman says");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20210517:nD5N2MD001");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Emirates could swap Boeing 777x for smaller Dreamliners, chairman says");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20210517:nL2N2N40A4");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "UPDATE 1-Ryanair &apos;upset&apos; with Boeing, fears no MAX deliveries before summer");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AXP.N") || isinCode.trim().equalsIgnoreCase("FEURUSD")
				|| isinCode.trim().equalsIgnoreCase("FGBPUSD")) {

			hm1.put("ID", "urn:newsml:reuters.com:20210517:nFWN2N40WN");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-American Express Reports Card Member Loan Stats for April");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20210423:nL1N2MG2F8");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "US STOCKS-Wall Street rallies on strong economic data; tech in focus");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20210423:nL1N2MG22Q");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "US STOCKS-Wall Street rallies on strong economic data; tech in focus");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20210423:nL4N2MG3KY");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "US STOCKS-Wall Street rallies on strong economic data; tech in focus");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("CSCO.O")) {

			hm1.put("ID", "urn:newsml:reuters.com:20210514:nFWN2N10X4");
			hm1.put("RT", dateValues[1]);
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-Cisco Says On May 13 Entered Second Amended And Restated Credit Agreement");
			hm1.put("LT", dateValues[1]);
			hm1.put("CT", dateValues[1]);

			hm2.put("ID", "urn:newsml:reuters.com:20210514:nASA026SG");
			hm2.put("RT", dateValues[2]);
			hm2.put("PR", "reuters.com");
			hm2.put("HT",
					"BRIEF-Cisco Announces Intent To Acquire Kenna Security To Deliver Industry Leading Vulnerability Management");
			hm2.put("LT", dateValues[2]);
			hm2.put("CT", dateValues[2]);

			hm3.put("ID", "urn:newsml:reuters.com:20210512:nASA0262A");
			hm3.put("RT", dateValues[3]);
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "BRIEF-Cisco Announces Intent To Acquire Socio Labs To Power The Future Of Hybrid Events");
			hm3.put("LT", dateValues[3]);
			hm3.put("CT", dateValues[3]);

			hm4.put("ID", "urn:newsml:reuters.com:20210511:nL1N2MX2MK");
			hm4.put("RT", dateValues[4]);
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Tech giants join call for funding U.S. chip production");
			hm4.put("LT", dateValues[4]);
			hm4.put("CT", dateValues[4]);

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		}

		return null;

	}

	/**
	 * (INFO) Fetches the mock complete news Story for a given input STory ID. The
	 * inputMap contains a StoryID.
	 * 
	 * @param inputMap
	 * @return {@link JSONObject}
	 * @author 22950
	 */
	public JSONObject mockGetStockNewsStory(Map<String, Object> inputMap) {
		JSONObject responseJSON = new JSONObject();
		String storyId = (String) inputMap.get("StoryId");
		Map<String, String> hm1 = new HashMap<>();
		if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201117:nBw8fgMlna")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-17T08:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Amazon Named Price Leaderin New Study");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201117:nBw8fgMlna</Origin>Amazon Named Price Leaderin New StudyAmazon has best prices across 21 product categories important to customersAmazon.com, Inc. (NASDAQ: AMZN) was named the low price leader inProfiteroannual Warsstudy. The comprehensive analysis, nowin its fourth year, compares prices on over 18,400 products at Amazon,Walmart, Target, Staples, Home Depot, Wayfair, MacyWalgreenCVS,Kroger, Nordstrom, Best Buy, Lowes, and Chewy, and found Amazon offeredcustomers the lowest prices most often and most consistently.The study compared products in 21 categories important to customers, includinghousehold supplies, health and personal care, office supplies, home furniture,electronics, appliances and more. Amazon proved to be the lowest priced optionin all categories, with the largest potential customer savings, on average, inHome Furniture (35% more affordable than other options in the category),Vitamins and Supplements (34%), Health and Personal Care (26%) and Beauty(22%).The full study is available here:<Origin Href=\\\"Link\\\">https://insights.profitero.com/rs/476-BCC-343/images/Profitero%20-%20Pandemic%20Price%20Wars%202020.pdf</Origin>(<Origin Href=\\\"Link\\\">https://cts.businesswire.com/ct/CT?id=smartlink&url=https%3A%2F%2Finsights.profitero.com%2Frs%2F476-BCC-343%2Fimages%2FProfitero%2520-%2520Pandemic%2520Price%2520Wars%25202020.pdf&esheet=52329421&newsitemid=20201117005503&lan=en-US&anchor=https%3A%2F%2Finsights.profitero.com%2Frs%2F476-BCC-343%2Fimages%2FProfitero%2520-%2520Pandemic%2520Price%2520Wars%25202020.pdf&index=1&md5=14d4f03b831c7ad6e69c8f964e7724cd</Origin>)Customers expect to find low prices in our store, and we work hard to providethe best available price across the hundreds of millions of products in ourstore for all customers, every day.About AmazonAmazon is guided by four principles: customer obsession rather than competitorfocus, passion for invention, commitment to operational excellence, andlong-term thinking. Customer reviews, 1-Click shopping, personalizedrecommendations, Prime, Fulfillment by Amazon, AWS, Kindle Direct Publishing,Kindle, Fire tablets, Fire TV, Amazon Echo, and Alexa are some of the productsand services pioneered by Amazon. For more information, visit amazon.com/about(<Origin Href=\\\"Link\\\">https://cts.businesswire.com/ct/CT?id=smartlink&url=https%3A%2F%2Fwww.aboutamazon.com%2F&esheet=52329421&newsitemid=20201117005503&lan=en-US&anchor=amazon.com%2Fabout&index=2&md5=228eb8fee563727e2447cc484501d240</Origin>)and follow @AmazonNews(<Origin Href=\\\"Link\\\">https://cts.businesswire.com/ct/CT?id=smartlink&url=https%3A%2F%2Ftwitter.com%2Famazonnews&esheet=52329421&newsitemid=20201117005503&lan=en-US&anchor=%40AmazonNews&index=3&md5=04dcd585fbda468e573213f0a45fbdab</Origin>).View source version on businesswire.com:<Origin Href=\\\"Link\\\">https://www.businesswire.com/news/home/20201117005503/en/</Origin>(<Origin Href=\\\"Link\\\">https://www.businesswire.com/news/home/20201117005503/en/</Origin>)Amazon.com, Inc.Media HotlineAmazon-pr@amazon.com (mailto:Amazon-pr@amazon.com) <Origin Href=\\\"Link\\\">www.amazon.com/pr</Origin>(<Origin Href=\\\"Link\\\">https://cts.businesswire.com/ct/CT?id=smartlink&url=http%3A%2F%2Fwww.amazon.com%2Fpr&esheet=52329421&newsitemid=20201117005503&lan=en-US&anchor=www.amazon.com%2Fpr&index=4&md5=79af7887c4891c08ded61ac2040bf63f</Origin>)Copyright Business Wire 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201117:nL1N2I308K")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-17T07:00:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "NXP, Amazon partner to connect cars to cloud computing services");
			hm1.put("TE",
					"<p>    Nov 17 (Reuters) - Dutch chipmaker NXP Semiconductors <Origin Href=\\\"QuoteRef\\\">NXPI.O</Origin>  on Tuesday said it had entered a strategic relationshipwith Amazon.com's  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin>  cloud computing unit to help carscommunicate with data centers.    NXP is one of the biggest suppliers of computing chips toautomakers and Amazon Web Services is the largest cloudcomputing service by revenue. The companies said the partnershipwould involve making a new NXP chip designed to aggregate datafrom a car's systems and send it over the internet to workbetter with Amazon's data centers.     see the opportunity to help (automakers) make impactfulimprovements throughout vehicle life cycles with new vehicledata insights and the ability to make continuous improvementsusing machine learning and over-the-air updates,\" Henri Ardevol,executive vice president and general manager of automotiveprocessing at NXP Semiconductors, said in a statement.    The two companies said they would give more details aboutthe partnership in a web presentation on Nov. 19. (Reporting by Stephen Nellis in San Francisco; Editing byChristian Schmollinger) ((Stephen.Nellis@thomsonreuters.com; (415) 344-4934;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:onlinereport.com:20201116:nRTROPT20201116231350KBN27W315")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T23:13:50-00:00");
			hm1.put("PR", "onlinereport.com");
			hm1.put("HT", "Does vaccine promise put U.S. consumers in a shopping mood? Retailers may have clues");
			hm1.put("TE",
					"<p>By Caroline Valetkevitch</p><p>NEW YORK (Reuters) - As positive coronavirus vaccine news makes strategists more optimistic about the future for retailers, investors will look to earnings results and forecasts from these companies in the coming days for clues about consumer spending.</p><p>The thinking is that effective vaccines will \"translate into positive performances for discretionary,\" said Sam Stovall, chief investment strategist for CFRA, which recently raised its recommendation on the S&P 500 consumer discretionary sector <.SPLRCD> to overweight from market weight.    </p><p>The pandemic and restrictions on businesses have added to revenue struggles for some traditional retailers like department stores while boosting sales for Amazon <AMZN.O> and other sellers that have capitalized on the work-from-home phenomenon. </p><p>Results from Home Depot Inc. <HD.N>, Walmart Inc. <WMT.N> and Kohl's Corp. <KSS.N> are due on Tuesday, while reports from Macy's Inc. <M.N>, L Brands <LB.N>, Target Corp. <TGT.N> and TJX Cos <TJX.N> are expected later this week.</p><p>The U.S. retail sales report for October also is due on Tuesday.</p><p>Investors will be especially eager to hear guidance and commentary from retail executives on consumer spending.</p><p>\"We've gotten used to now buying online increasingly and delivery has become smoother,\" said Quincy Krosby, chief market strategist at Prudential Financial in Newark, New Jersey. \"The question is, does it continue?\"</p><p>Shares in a number of retailers rallied on Monday as a result of optimism that a COVID-19 vaccine will help the economic recovery in the months ahead.</p><p>Moderna Inc <MRNA.O> said its experimental vaccine was 94.5% effective in preventing COVID-19 based on interim data from a late-stage trial. Pfizer Inc <PFE.N> said last week its experimental COVID-19 vaccine was more than 90% effective based on initial trial results.</p><p>Among the biggest percentage gainers in the S&P 500 consumer discretionary index on Monday were Gap Inc. <GPS.N>, up 9.5%; Under Armour <UAA.N>, up 5.8%; and Ulta Beauty <ULTA.O>, up 4.9%. </p><p>Investors also are watching whether upcoming holiday sales data can support hopes for retailer shares despite a surge in COVID cases and increased restrictions across the United States and in Europe.</p><p>\"As long as the jobs market holds up and we see (unemployment benefit claims) continuing to go down, that helps consumers' optimism,\" said Krosby.    </p><p>Some retailers' shares remain down sharply this year so far, including Macy's, which is off more than 50% since Dec. 31 despite a 9% gain on Monday as the pandemic added to their recent struggles.</p><p>On the earnings front, there continues to be a big divide within the consumer discretionary sector. </p><p>Analysts expect the sector to post an earnings decline of 5.1% for the third quarter from a year ago, compared with a 7.4% decline for the entire S&P 500 <.SPX>, according to IBES data from Refinitiv. </p><p>But earnings for internet and direct marketing retail companies are show about a 43% rise in the quarter, while  apparel retail earnings are forecast to fall 40.1%. </p><p>Nordstrom Inc <JWN.N>, Gap Inc <GPS.N> and American Eagle Outfitters Inc <AEO.N> report earnings next week.</p><p></p><p> (Reporting by Caroline Valetkevitch; Editing by Alden Bentley and Cynthia Osterman)</p><Origin Href=\\\"ThumbnailRef\\\">2020-11-16T231350Z_1_LYNXMPEGAF1PI_RTROPTP_1_USA-STOCKS-RETAILERS.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201116:nRTROPT20201116231350LYNXMPEGAF1PI</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: A shopper is seen wearing a mask while shopping at a Walmart store in Bradford, Pennsylvania</Origin> <Origin Href=\\\"ChannelCode\\\">OLUSBUS</Origin> <Origin Href=\\\"LinkTitle\\\">Reuters US Online Report Business News</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201116T231350+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201116T231350+0000</Origin>");
		} else if (storyId
				.equalsIgnoreCase("urn:newsml:onlinereport.com:20201116:nRTROPT20201116231350KBN27W315-OCABS")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T23:13:50-00:00");
			hm1.put("PR", "onlinereport.com");
			hm1.put("HT", "Does vaccine promise put U.S. consumers in a shopping mood? Retailers may have clues");
			hm1.put("TE",
					"<p>By Caroline Valetkevitch</p><p>NEW YORK (Reuters) - As positive coronavirus vaccine news makes strategists more optimistic about the future for retailers, investors will look to earnings results and forecasts from these companies in the coming days for clues about consumer spending.</p><p>The thinking is that effective vaccines will \"translate into positive performances for discretionary,\" said Sam Stovall, chief investment strategist for CFRA, which recently raised its recommendation on the S&P 500 consumer discretionary sector <.SPLRCD> to overweight from market weight.    </p><p>The pandemic and restrictions on businesses have added to revenue struggles for some traditional retailers like department stores while boosting sales for Amazon <AMZN.O> and other sellers that have capitalized on the work-from-home phenomenon. </p><p>Results from Home Depot Inc. <HD.N>, Walmart Inc. <WMT.N> and Kohl's Corp. <KSS.N> are due on Tuesday, while reports from Macy's Inc. <M.N>, L Brands <LB.N>, Target Corp. <TGT.N> and TJX Cos <TJX.N> are expected later this week.</p><p>The U.S. retail sales report for October also is due on Tuesday.</p><p>Investors will be especially eager to hear guidance and commentary from retail executives on consumer spending.</p><p>\"We've gotten used to now buying online increasingly and delivery has become smoother,\" said Quincy Krosby, chief market strategist at Prudential Financial in Newark, New Jersey. \"The question is, does it continue?\"</p><p>Shares in a number of retailers rallied on Monday as a result of optimism that a COVID-19 vaccine will help the economic recovery in the months ahead.</p><p>Moderna Inc <MRNA.O> said its experimental vaccine was 94.5% effective in preventing COVID-19 based on interim data from a late-stage trial. Pfizer Inc <PFE.N> said last week its experimental COVID-19 vaccine was more than 90% effective based on initial trial results.</p><p>Among the biggest percentage gainers in the S&P 500 consumer discretionary index on Monday were Gap Inc. <GPS.N>, up 9.5%; Under Armour <UAA.N>, up 5.8%; and Ulta Beauty <ULTA.O>, up 4.9%. </p><p>Investors also are watching whether upcoming holiday sales data can support hopes for retailer shares despite a surge in COVID cases and increased restrictions across the United States and in Europe.</p><p>\"As long as the jobs market holds up and we see (unemployment benefit claims) continuing to go down, that helps consumers' optimism,\" said Krosby.    </p><p>Some retailers' shares remain down sharply this year so far, including Macy's, which is off more than 50% since Dec. 31 despite a 9% gain on Monday as the pandemic added to their recent struggles.</p><p>On the earnings front, there continues to be a big divide within the consumer discretionary sector. </p><p>Analysts expect the sector to post an earnings decline of 5.1% for the third quarter from a year ago, compared with a 7.4% decline for the entire S&P 500 <.SPX>, according to IBES data from Refinitiv. </p><p>But earnings for internet and direct marketing retail companies are show about a 43% rise in the quarter, while  apparel retail earnings are forecast to fall 40.1%. </p><p>Nordstrom Inc <JWN.N>, Gap Inc <GPS.N> and American Eagle Outfitters Inc <AEO.N> report earnings next week.</p><p></p><p> (Reporting by Caroline Valetkevitch; Editing by Alden Bentley and Cynthia Osterman)</p><Origin Href=\\\"ThumbnailRef\\\">2020-11-16T231350Z_1_LYNXMPEGAF1PI-OCABS_RTROPTP_1_CBUSINESS-US-USA-STOCKS-RETAILERS.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201116:nRTROPT20201116231350LYNXMPEGAF1PI-OCABS</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: A shopper is seen wearing a mask while shopping at a Walmart store in Bradford, Pennsylvania</Origin> <Origin Href=\\\"ChannelCode\\\">OLCABUS</Origin> <Origin Href=\\\"LinkTitle\\\">Reuters Canada Online Report Business News</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201116T231350+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201116T231350+0000</Origin>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:onlinereport.com:20201117:nOLINTECH")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-17T13:36:35-00:00");
			hm1.put("PR", "onlinereport.com");
			hm1.put("HT", "Reuters India Online Report Technology");
			hm1.put("TE",
					"<p><Origin Href=\\\"StoryRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117114222KBN27X1B6</Origin> Amazon launches online pharmacy in new contest with drug retail <p>(Reuters) - Amazon.com Inc on Tuesday launched an online pharmacy for delivering prescription medications in the United States, increasing competition with drug retailers such as Walgreens, CVS and Walmart. </p> <Origin Href=\\\"ThumbnailRef\\\">2020-11-17T114222Z_1_LYNXMPEGAG0QV_RTROPTP_1_AMAZON-COM-NVIDIA.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117114222LYNXMPEGAG0QV</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: Amazon logo is pictured in Mexico City</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201117T114222+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201117T114222+0000</Origin> </p><p><Origin Href=\\\"StoryRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117110932KBN27X186</Origin> Facebook, Twitter CEOs to testify Tuesday to U.S. Senate panel over content moderation decisions <p>WASHINGTON (Reuters) - The chief executives of Facebook and Twitter are set to testify before a congressional hearing on Tuesday that will explore their content moderation practices as Republicans accuse social media companies of censoring conservative speech.    </p> <Origin Href=\\\"ThumbnailRef\\\">2020-11-17T110932Z_1_LYNXMPEGAG0O7_RTROPTP_1_USA-TECH-SENATE.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117110932LYNXMPEGAG0O7</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: Tech CEOs testify at U.S. Senate hearing about internet regulation</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201117T110932+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201117T110932+0000</Origin> </p><p><Origin Href=\\\"StoryRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117090900KBN27X0VA</Origin> 'SpaceX, this is Resilience': Four astronauts begin six-month stay on space station <p>WASHINGTON (Reuters) - Four astronauts riding a newly-designed spacecraft from Elon Musk's rocket company SpaceX greeted their new crewmates aboard the International Space Station on Tuesday after successfully docking in a landmark achievement for private space travel.</p> <Origin Href=\\\"ThumbnailRef\\\">2020-11-17T090900Z_1_LYNXMPEGAG0HW_RTROPTP_1_SPACE-EXPLORATION-SPACEX.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117090900LYNXMPEGAG0HW</Origin> <Origin Href=\\\"ImageTitle\\\">The crew of a SpaceX Falcon 9 rocket departs for the launch pad for the first operational NASA commercial crew mission at Kennedy Space Center in Cape Canaveral</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201117T090900+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201117T090900+0000</Origin> </p><p><Origin Href=\\\"StoryRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117124912KBN27X1JO</Origin> GameStop investor Ryan Cohen urges strategic review <p>(Reuters) - One of GameStop Corp's largest shareholders, Ryan Cohen, has urged the videogame retailer to conduct a strategic review of its business and to focus on digital sales by moving away from its traditional brick-and-mortar model.</p> <Origin Href=\\\"ThumbnailRef\\\">2020-11-17T124912Z_1_LYNXMPEGAG0V9_RTROPTP_1_GAMESTOP-RESULTS.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117124912LYNXMPEGAG0V9</Origin> <Origin Href=\\\"ImageTitle\\\">A GameStop store is photographed in Austin, Texas</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201117T124912+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201117T124912+0000</Origin> </p><p><Origin Href=\\\"StoryRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117022726KBN27X08F</Origin> Solomon Islands prepares to ban Facebook after government criticism on platform: media <p>SYDNEY (Reuters) - The Solomon Islands is planning to ban the use of Facebook for an indeterminate period after inflammatory critique of the government was aired on the social media platform, the Solomon Times reported.</p> <Origin Href=\\\"ThumbnailRef\\\">2020-11-17T022726Z_1_LYNXMPEGAG04B_RTROPTP_1_PACIFIC-SOLOMONISLANDS-FACEBOOK.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117022726LYNXMPEGAG04B</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: FILE PHOTO: Facebook logos</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201117T022726+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201117T022726+0000</Origin> </p><p><Origin Href=\\\"StoryRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117111357KBN27X17W</Origin> Thieves steal $6.6 million of Apple products in UK truck heist <p>LONDON (Reuters) - British police said on Tuesday they were hunting for thieves who made off with 5 million pounds ($6.6 million) worth of Apple products after tying up a driver and security guard during a truck heist in central England.</p> <Origin Href=\\\"ThumbnailRef\\\">2020-11-17T111357Z_1_LYNXMPEGAG0OH_RTROPTP_1_APPLE-PRIVACY-EU.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117111357LYNXMPEGAG0OH</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: The Apple logo is seen through a security fence erected around the Apple Fifth Avenue store</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201117T111357+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201117T111357+0000</Origin> </p><p><Origin Href=\\\"StoryRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117025033KBN27X098</Origin> Huawei selling Honor brand to agent-dealer consortium to keep smartphone unit alive <p>SHENZHEN, China (Reuters) - Huawei Technologies Co Ltd [HWT.UL] is selling its budget brand smartphone unit Honor to a consortium of over 30 agents and dealers in a bid to keep it alive, the company and the consortium said on Tuesday.</p> <Origin Href=\\\"ThumbnailRef\\\">2020-11-17T025033Z_1_LYNXMPEGAG04M_RTROPTP_1_HUAWEI-M-A-DIGITAL-CHINA.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117025033LYNXMPEGAG04M</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: A visitor to the launch of Huawei's Honor 20 range of smartphones photographs the product at an event in London</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201117T025033+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201117T025033+0000</Origin> </p><p><Origin Href=\\\"StoryRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117133551KBN27X1LF</Origin> Apple, ChargePoint team up on electric vehicle charging info <p>(Reuters) - Electric vehicle charging network ChargePoint said on Tuesday that it will team up with Apple Inc to integrate a wide range of EV charging information in Apple's CarPlay infotainment system.</p> <Origin Href=\\\"StoryCreationDate\\\">20201117T133551+0000</Origin> </p><p><Origin Href=\\\"StoryRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117115309KBN27X1DG</Origin> Robot enforces mask-wearing, distancing at store in Japan <p>TOKYO (Reuters) - A robot has signed on as the newest staff member at a store in Japan, taking on the job of ensuring customers wear masks and practice social distancing to prevent the spread of the coronavirus.</p> <Origin Href=\\\"StoryCreationDate\\\">20201117T115309+0000</Origin> </p><p><Origin Href=\\\"StoryRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117042814KBN27X0CJ</Origin> Baidu revenue beats estimates; to buy JOYY's China live-streaming unit <p>(Reuters) - Chinese search engine leader Baidu Inc <BIDU.O> booked a 1% rise in quarterly revenue, beating market estimates, and said it will buy streaming platform YY Live from social media firm JOYY Inc <YY.O> for about $3.6 billion to help diversify revenue sources.</p> <Origin Href=\\\"ThumbnailRef\\\">2020-11-17T042814Z_1_LYNXMPEGAG07D_RTROPTP_1_BAIDU-RESULTS.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117042814LYNXMPEGAG07D</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: Logo of Baidu is seen at the company's headquarters in Beijing</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201117T042814+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201117T042814+0000</Origin> </p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201116:nL1N2I221H")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T19:57:18-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "WRAPUP 2-Brazil launches 'Pix' instant payments system, Whatsapp to enter 'soon'");
			hm1.put("TE",
					"<p>    By Jamie McGeever, Marcela Ayres and Carolina Mandl    BRASILIA, Nov 16 (Reuters) - Brazil's central bank on Mondaylaunched an instant payments platform that will speed up andsimplify transactions, as well as foster financial sectorcompetition and lure in new players such as big techs FacebookInc  <Origin Href=\\\"QuoteRef\\\">FB.O</Origin>  and Google  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin> .     Dubbed \"Pix,\" the state-owned instant payments system allowsconsumers and companies to make money transfers 24 hours a day,seven days a week, without requiring debit or credit cards. Itis also free of charge for individuals.    \"Huge changes are underway in payments. Society demandssomething that is fast, cheap, safe, transparent and open,\"central bank president Roberto Campos Neto said in a virtualpress conference to mark the launch.    The move by Brazil's central bank aims to increasecompetition in a highly concentrated banking system, with itstop-five lenders, such as Itau Unibanco Holding SA  <Origin Href=\\\"QuoteRef\\\">ITUB4.SA</Origin>  and Banco Santander Brasil SA  <Origin Href=\\\"QuoteRef\\\">SANB11.SA</Origin> , holding roughly 80%of total assets and deposits.    As the central bank sets low prices for money transfers andpayments via Pix, the regulator believes competition willincrease. Itau's card processor, Rede, said on Monday it willnot charge merchants using Pix for the first six months.    By 2030, Pix is likely to account for 22% of electronicpayments in Brazil, consultancy firm Oliver Wyman said in arecent study. Last year, debit and card payments in Braziltotaled 1.8 trillion reais ($382 billion).    Pix will cause banks to lose some fees as individuals useit.    The platform went live at 0930 local time on Monday, and canbe used to buy anything from ice cream to a car, Campos Netosaid.    According to the central bank, 72 million registrations havebeen opened for the service, by 30 million individuals and 1.8million businesses.    Campos Neto also said the central bank is in talks with bigtech players such as Google and Facebook about entering theBrazilian payments services market.     \"WhatsApp will start doing P2P soon. I have talked a lotwith their CEO, we are making good progress. He has told me thatthe process (with us) was faster than in other countries,\"Campos Neto said.    \"Our only concern is that we must go through all theapproval criteria and that we have a system that fosterscompetition,\" Campos Neto said.    Some 750 companies have signed up to Pix to accept and offerinstant payments. Uber Technologies Inc  <Origin Href=\\\"QuoteRef\\\">UBER.N</Origin>  said it startedto accept Pix payments, hoping to add unbanked clients. <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2HX1Y4</Origin>    In the future, Pix will add new functionalities, such ascash-back and preprogrammed payments, which are currentlyoffered mainly through credit cards. ($1 = 5.40 reais) (Reporting by Jamie McGeever, Marcela Ayres and Carolina MandlEditing by Chris Reese and Steve Orlofsky) ((jamie.mcgeever@thomsonreuters.com; +55 (0)11 97189 3169;Reuters Messaging: jamie.mcgeever.reuters.com@reuters.net))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201116:nPremltmRa")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T18:15:28-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Hedge Funds Are Betting Big On This $130 Trillion Trend");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201116:nPremltmRa</Origin>Hedge Funds Are Betting Big On This $130 Trillion TrendPR NewswireLONDON, Nov. 16, 2020FN Media Group Presents Oilprice.com Market CommentaryLONDON, Nov. 16, 2020 /PRNewswire/ -- This isn't just a megatrend. It's amovement in Big Money, and it's the most profound redistribution of investmentthat the world's biggest asset and wealth managers have recently seen. Bigmoney is fleeing anything that's not sustainable.   Mentioned in today'scommentary includes:  Microsoft Corporation (NASDAQ: MSFT), Apple Inc.(NASDAQ: AAPL), Facebook, Inc. (NASDAQ: FB), Alphabet Inc. (NASDAQ: GOOGL),Tesla, Inc. (NASDAQ: TSLA).By 2022, PwC says that 77% of institutional investors will stop buying non-ESGproducts entirely. ESG fund assets will account for over 50% of all Europeanfund assets by 2025. That's nearly $8 trillion. And it's only thebeginning. Europe may be trouncing ESG assets right now, but across the Atlantic, there'snearly $120 trillion up for grabs. Over 3,000 investors with over $110trillion in assets under management support ESG investing. Anotherindustry-led group of 70 members with $9 trillion in assets under managementdoes, too. Why? Because sustainability isn't just outperforming the market BIgMoney's downside protection against ESG-related risks. That's partly whyBlackRock, with some $6.5 trillion in assets under management, is now the Kingof Wall Street. And it's why Facedrive (FD (<Origin Href=\\\"Link\\\">https://finance.yahoo.com/quote/FD.V</Origin>).V, FDVRF(<Origin Href=\\\"Link\\\">https://finance.yahoo.com/quote/FDVRF</Origin>))--a tech-driven, multi-vertical,next-gen company with an multiple ESG-focused portfolio is grabbing headlinesacross North America's biggest industriesthe $5-trillion(<Origin Href=\\\"Link\\\">https://www.statista.com/statistics/574151/global-automotive-industry-revenue/</Origin>) globaltransportation industry and the $9 trillion(<Origin Href=\\\"Link\\\">https://policyadvice.net/health-insurance/insights/health-care-industry/</Origin>) healthcareindustry, which is now explicitly tied to the fate of the $850-billion airlineindustry, to the $600-billion major league sports industry and the $26-billionfood delivery segment And the future of Facedrive verticals are multiplewith an uncompromising \"people and planet first\" viewpoint for everythingit does the world's first carbon-offset ride-hailing company and environmentallyfriendlier food delivery to Tier 1 technology on the frontline of COVID-19, ananswer to major league sports' big revenue problem and most recently, amajor push into the U.S. with an EV subscription car service that plans tohelp change the way North Americans think about owning vehicles--forever. Multi-Trillions of Dollars Looking for Somewhere To InvestThis is a new generation of investors, and they're looking for a new kind ofinvestment: One that harnesses the profit of ESG.  It's about avoidingpotential financial losses and enduring scandals that can impact returns andproduct value. That's why PwC says that \"public awareness of ESG-related risks has catapultedclimate change and sustainability to the top of the global agenda\" and that\"COVID has accelerated this shift, bringing the real-life impacts ofoverlooking ESG factors into the spotlight\".  And that's why BlackRock CEOLarry Fink(<Origin Href=\\\"Link\\\">https://www.blackrock.com/us/financial-professionals/larry-fink-ceo-letter?cid=ppc:CEOLetter:PMS:US:NA</Origin>) saysthat \"awareness is rapidly changing, and I believe we are on the edge of afundamental reshaping of finance\".That's a multi-million-dollar reshaping of finance It's an expensivelesson for a company like Uber, which disrupted a hundred-year-old dynasty,bringing the taxi industry to its knees within 7 years. But now finds itselfon the wrong side of ESG history (FD (<Origin Href=\\\"Link\\\">https://finance.yahoo.com/quote/FD.V</Origin>).V, FDVRF(<Origin Href=\\\"Link\\\">https://finance.yahoo.com/quote/FDVRF</Origin>)) is on the right side and it's workingto disrupt the transportation industry from two flanking positions: #1 Next-Gen Ride-Hailing: The ESG ElementFacedrive's flagship ride-hailing platform was the first to offer riders achoice of EVs and hybrids. And the first to plant trees to offset its carbonfootprint. That's because it was the first to foresee the problem with Uber and Lyft:They completely ignored sustainability, with ride-hailing resulting in nearly70% more pollution than whatever transportation it displaced. It's the firstto bring cities and communities on as stakeholders, and it's the first totreat its drivers as people who deserve living wages. #2 The Transportation Revolution When you combine the $5 trillion global transportation industry with an energyindustry whose renewables sector is growing dramatically, you get one of themost lucrative marriages of industry yet ... Steer (<Origin Href=\\\"Link\\\">https://www.steerev.com/</Origin>), backed by an Exelon (NASDAQ: EXC)subsidiary, is planning the biggest disruption the global transportationindustry has seen in decades. And it was just acquired by Facedrive(<Origin Href=\\\"Link\\\">https://www.businesswire.com/news/home/20200908005359/en/Facedrive-Acquires-Steer-and-Accepts-Strategic-Investment-From-Exelon-Subsidiary-Exelorate</Origin>). Washington, D.C.-based Steer is a high-tech vehicle subscription service thatisn't planning to simply disrupt the auto industry and change the way we \"own\"cars This seamless, hassle-free technology is grabbing onto the ESGmegatrend by giving subscribers access to their own virtual garage oflow-emissions vehicles and EVs. Multiple Verticals, Limitless ESGFrom the best in high-tech contact-tracing tech that could help airlines, to asolution for revenue-starved major league sports, the verticals here aredizzying--but they're all ESG, and they're all high-tech. Facedrive (FD (<Origin Href=\\\"Link\\\">https://finance.yahoo.com/quote/FD.V</Origin>).V, FDVRF(<Origin Href=\\\"Link\\\">https://finance.yahoo.com/quote/FDVRF</Origin>)) engineered a major coup at the heightof the COVID pandemic, launching TraceSCAN, a homegrown Canadian COVID-19tracing solution and the only viable application that features Bluetoothwearable tech integration. It's also got one of the biggest labor unions inthe world on board, and more recently--official endorsement from theGovernment of Ontario, which is supporting its deployment--far and wide.That's because it's the only tech that can effectively help trace coronavirusinfections without use of a smart phone, and it could become crucial to openoperations on everything from Parliament Hill's major renovation project inOttawa, to corporate offices, sporting events, healthcare facilities,long-term care facilities and outdoor venues. But the biggest TraceScan couptook place just two weeks ago, when giant Air Canada signed a deal to launch aTraceScan pilot project for its employees.  The news flow for ESG has a climate change, pandemic and \"people and planet\"first momentum that is fast and furious, and Facedrive (FD(<Origin Href=\\\"Link\\\">https://finance.yahoo.com/quote/FD.V</Origin>).V, FDVRF(<Origin Href=\\\"Link\\\">https://finance.yahoo.com/quote/FDVRF</Origin>)) deal flow is just as fast.The ESG Trend Is Heating UpTech giants across the board are diving head-first into the sustainabilitypush. Facebook (NASDAQ: FB), for its part, has taken an innovative approach inits efforts to reduce its carbon footprint. Its data centers are some of themost resource-efficient on the planet, and it's become an example for theentire industry.  And by the end of the year, it will have 100% of its datacenters running on green energy. A massive and ambitious undertaking. But ifanyone can do it, it's Facebook. Facebook has even gone a step further with its focus on building moresustainable workplaces. It's building designs incorporate a number ofrenewable energy sources and water recycling methods, in addition to promotingthe recycling and sustainability of all products consumed on site.Facebook is by no means the only tech company pushing this trend, either.Microsoft, Google and Apple are on making major moves to clean up their acts,as well. And not only is it a big draw for investors, it's forcing otherindustries to make changes, as well.Microsoft (MSFT) is a prime example of a company pushing sustainability intothe center stage of its operations. In fact, Microsoft is going above andbeyond in its carbon emissions pledge. It is aiming to be carbon neutral inthe next decade. Not only is the tech giant taking a leadership role inreducing its carbon emissions, but it is also at the forefront of atechnological wave that is actively helping other companies curb theiremissions, as well.Microsoft has created numerous resources to help monitor and evaluate theimpact of different businesses on the environment, helping gather data tobetter understand where and how the world can improve. Additionally, Microsoftis creating tools to better regulate the use of water and curb the world'sgrowing waste problems.Not only has it helped other companies reduce emissions, it's taking a seriousstance on the climate crisis itself. In fact, it's pushing so hard that it isaiming to be carbon NEGATIVE by 2030. That's a huge pledge. And if anyone cando it, it's Microsoft. It's no secret that Apple (NASDAQ: AAPL) has always thought outside of thebox. And when it brought back Steve Jobs in 1997, the company really took off.Jobs also paved the way to a greener future for the company.  From theproducts themselves, to the packages they came in, and even the data centerspowering them, Steve Jobs went above and beyond to cut the environmentalimpact of his company. Apple has made significant moves towards renewables. All of Apple'soperations run on 100% renewable energy. \"We proved that 100 percent renewableis 100 percent doable. All our facilities worldwideApple offices,retail stores, and data centersnow powered entirely by clean energy.But this is just the beginning of how we're reducing greenhouse gas emissionsthat contribute to climate change. We're continuing to go further than mostcompanies in measuring our carbon footprint, including manufacturing andproduct use. And we're making great progress in those areas too,\" CEO Tim Cookexplained.And it's already having an impact. Not only have they decreased their averageproduct's energy use by 70 percent. They've reduced their total carbonfootprint by more than 35 percent in just a few short years. All whilesecuring the title as the World's First Trillion Dollar Company. Not to be outdone, Google (NASDAQ: GOOGL) is jumping on the green bandwagon,as well. It's focus is on raising the bar for smarter and more efficient useof the world's limited resources. It is building sustainable, energy-efficientdata centers and workplaces. It is also harnessing artificial intelligence toutilize energy more efficiently.  Despite being one of the largest companies on the planet, in many ways it haslived up to its original \"Don't Be Evil\" slogan. Not only is Google poweringits data centers with renewable energy, it is also on the cutting edge ofinnovation in the industry, investing in new technology and green solutions tobuild a more sustainable tomorrow. It's bid to reduce its carbon footprint hasbeen well received by both younger and older investors. And as the need toslow down climate change becomes increasingly dire, it's easy to see why.   There's a reason TSLA (NASDAQ: TSLA) has performed so well this year.Investors love its message. As one of the world's most innovative carmanufacturers, it has made electric vehicles cool again. Its slick design isbeloved across the world. In fact, it's likely impossible to NOT see a Teslain cities like Hong Kong or San Francisco.  Musk is likely to emerge withthree crowns on the ground: EVs, solar, and clean energy. Each revolutionary.It may seem easy to overlook Tesla's solar business considering that the solarpanel and battery segment brought in just six percent of the company's revenuein 2019. But with the meteoric rise of ESG investing over the past couple ofyears, many companies, including traditional fossil fuel companies, have beeninvesting in clean energy projects including solar and wind energy at anunprecedented rate. By. Sasha Kay**IMPORTANT! BY READING OUR CONTENT YOU EXPLICITLY AGREE TO THE FOLLOWING.PLEASE READ CAREFULLY**Forward-Looking StatementsThis publication contains forward-looking information which is subject to avariety of risks and uncertainties and other factors that could cause actualevents or results to differ from those projected in the forward-lookingstatements.  Forward looking statements in this publication include that thedemand for ride sharing services will grow; that Steer can help completelychange the way people view car ownership, that Steer can disrupt industrysegments; that the Tally app will become popular and start generatingsubstantial revenues; that the Tally sports predictive app will lead to onlinesports revenue; that Tracescan  could help the tourism industry deal withCOVID and will sign new agreements for use of its alert wearables; that newtech deals will be signed by Facedrive; that Facedrive will be able to fundits capital requirements in the near term and long term; and that Facedrivewill be able to carry out its business plans. These forward-looking statementsare subject to a variety of risks and uncertainties and other factors thatcould cause actual events or results to differ materially from those projectedin the forward-looking information.  Risks that could change or prevent thesestatements from coming to fruition include that riders are not as attracted toEV rides as expected; that the Tally app may not become popular, may not leadto revenues from the app; that competitors may offer better or cheaperalternatives to the Facedrive businesses; TraceScan may not work as expectedin commercial settings; changing governmental laws and policies; the company'sability to obtain and retain necessary licensing in each geographical area inwhich it operates; the success of the company's expansion activities andwhether markets justify additional expansion; the ability of the company toattract drivers who have electric vehicles and hybrid cars; the ability ofFacedrive to attract providers of good and services for merchandisepartnerships on terms acceptable to both parties, and on profitable terms forFacedrive; and that the products co-branded by Facedrive may not be asmerchantable as expected. The forward-looking information contained herein isgiven as of the date hereof and we assume no responsibility to update orrevise such information to reflect new events or circumstances, except asrequired by law.DISCLAIMERSThis communication is not a recommendation to buy or sell securities.Oilprice.com, Advanced Media Solutions Ltd, and their owners, managers,employees, and assigns (collectively \"the Company\") owns a considerable numberof shares of FaceDrive (FD.V) for investment, however the views reflectedherein do not represent Facedrive nor has Facedrive authored or sponsored thisarticle. This share position in FD.V is a major conflict with our ability tobe unbiased, more specifically:This communication is for entertainment purposes only. Never invest purelybased on our communication. Therefore, this communication should be viewed asa commercial advertisement only. We have not investigated the background ofthe featured company. Frequently companies profiled in our alerts experience alarge increase in volume and share price during the course of investorawareness marketing, which often end as soon as the investor awarenessmarketing ceases. The information in our communications and on our website hasnot been independently verified and is not guaranteed to be correct.SHARE OWNERSHIP. The owner of Oilprice.com owns a substantial number of sharesof this featured company and therefore has a substantial incentive to see thefeatured company's stock perform well. The owner of Oilprice.com will notnotify the market when it decides to buy more or sell shares of this issuer inthe market. The owner of Oilprice.com will be buying and selling shares ofthis issuer for its own profit. This is why we stress that you conductextensive due diligence as well as seek the advice of your financial advisoror a registered broker-dealer before investing in any securities.NOT AN INVESTMENT ADVISOR. The Company is not registered or licensed by anygoverning body in any jurisdiction to give investing advice or provideinvestment recommendation. ALWAYS DO YOUR OWN RESEARCH and consult with alicensed investment professional before making an investment. Thiscommunication should not be used as a basis for making any investment.RISK OF INVESTING. Investing is inherently risky. Don't trade with money youcan't afford to lose. This is neither a solicitation nor an offer to Buy/Sellsecurities. No representation is being made that any stock acquisition will oris likely to achieve profits.DISCLAIMER:  OilPrice.com is Source of all content listed above.  FN MediaGroup, LLC (FNM), is a third party publisher and news dissemination serviceprovider, which disseminates electronic information through multiple onlinemedia channels. FNM is NOT affiliated in any manner with OilPrice.com or anycompany mentioned herein.  The commentary, views and opinions expressed inthis release by OilPrice.com are solely those of OilPrice.com and are notshared by and do not reflect in any manner the views or opinions of FNM.  FNMis not liable for any investment decisions by its readers or subscribers. FNM and its affiliated companies are a news dissemination and financialmarketing solutions provider and are NOT a registeredbroker/dealer/analyst/adviser, holds no investment licenses and may NOT sell,offer to sell or offer to buy any security.  FNM was not compensated by anypublic company mentioned herein to disseminate this press release.FNM HOLDS NO SHARES OF ANY COMPANY NAMED IN THIS RELEASE.This release contains \"forward-looking statements\" within the meaning ofSection 27A of the Securities Act of 1933, as amended, and Section 21E theSecurities Exchange Act of 1934, as amended and such forward-lookingstatements are made pursuant to the safe harbor provisions of the PrivateSecurities Litigation Reform Act of 1995. \"Forward-looking statements\"describe future expectations, plans, results, or strategies and are generallypreceded by words such as \"may\", \"future\", \"plan\" or \"planned\", \"will\" or\"should\", \"expected,\" \"anticipates\", \"draft\", \"eventually\" or \"projected\". Youare cautioned that such statements are subject to a multitude of risks anduncertainties that could cause future circumstances, events, or results todiffer materially from those projected in the forward-looking statements,including the risks that actual results may differ materially from thoseprojected in the forward-looking statements as a result of various factors,and other risks identified in a company's annual report on Form 10-K or 10-KSBand other filings made by such company with the Securities and ExchangeCommission. You should consider these factors in evaluating theforward-looking statements included herein, and not place undue reliance onsuch statements. The forward-looking statements in this release are made as ofthe date hereof and FNM undertakes no obligation to update such statements.Contact Information:Media Contact e-mail:  editor@financialnewsmedia.com  U.S. Phone:+1(954)345-0611 Copyright (c) 2020 PR Newswire Association,LLC. All Rights Reserved.</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:onlinereport.com:20201116:nRTROPT20201116173458KBN27W2DC")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T17:34:58-00:00");
			hm1.put("PR", "onlinereport.com");
			hm1.put("HT", "Brazil launches 'Pix' instant payments system, Whatsapp to enter 'soon'");
			hm1.put("TE",
					"<p>By Jamie McGeever, Marcela Ayres and Carolina Mandl</p><p>BRASILIA (Reuters) - Brazil's central bank on Monday launched an instant payments platform that will speed up and simplify transactions, as well as foster financial sector competition and lure in new players such as big techs Facebook Inc <FB.O> and Google <GOOGL.O>.     </p><p>Dubbed \"Pix,\" the state-owned instant payments system allows consumers and companies to make money transfers 24 hours a day, seven days a week, without requiring debit or credit cards. It is also free of charge for individuals.</p><p>\"Huge changes are underway in payments. Society demands something that is fast, cheap, safe, transparent and open,\" central bank president Roberto Campos Neto said in a virtual press conference to mark the launch.</p><p>The move by Brazil's central bank aims to increase competition in a highly concentrated banking system, with its top-five lenders, such as Itau Unibanco Holding SA <ITUB4.SA>  and Banco Santander Brasil SA <SANB11.SA>, holding roughly 80% of total assets and deposits.    </p><p>As the central bank sets low prices for money transfers and payments via Pix, the regulator believes competition will increase. Itau's card processor, Rede, said on Monday it will not charge merchants using Pix for the first six months.    </p><p>By 2030, Pix is likely to account for 22% of electronic payments in Brazil, consultancy firm Oliver Wyman said in a recent study. Last year, debit and card payments in Brazil totaled 1.8 trillion reais ($382 billion).    </p><p>Pix will cause banks to lose some fees as individuals use it.    </p><p>The platform went live at 0930 local time on Monday, and can be used to buy anything from ice cream to a car, Campos Neto said.</p><p>According to the central bank, 72 million registrations have been opened for the service, by 30 million individuals and 1.8 million businesses.</p><p>Campos Neto also said the central bank is in talks with big tech players such as Google and Facebook about entering the Brazilian payments services market. </p><p>\"WhatsApp will start doing P2P soon. I have talked a lot with their CEO, we are making good progress. He has told me that the process (with us) was faster than in other countries,\" Campos Neto said.</p><p>\"Our only concern is that we must go through all the approval criteria and that we have a system that fosters competition,\" Campos Neto said.</p><p>Some 750 companies have signed up to Pix to accept and offer instant payments. Uber Technologies Inc <UBER.N> said it started to accept Pix payments, hoping to add unbanked clients.    </p><p>In the future, Pix will add new functionalities, such as cash-back and preprogrammed payments, which are currently offered mainly through credit cards.</p><p></p><p> (Reporting by Jamie McGeever, Marcela Ayres and Carolina Mandl; Editing by Chris Reese and Steve Orlofsky)</p><Origin Href=\\\"ThumbnailRef\\\">2020-11-16T173458Z_1_LYNXMPEGAF1BD_RTROPTP_1_INDIA-WHATSAPP-ANTITRUST.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201116:nRTROPT20201116173458LYNXMPEGAF1BD</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: A logo of WhatsApp is pictured on a T-shirt worn by a WhatsApp-Reliance Jio representative during a drive by the two companies to educate users, on the outskirts of Kolkata</Origin> <Origin Href=\\\"ChannelCode\\\">OLINTECH</Origin> <Origin Href=\\\"LinkTitle\\\">Reuters India Online Report Technology</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201116T173458+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201116T173458+0000</Origin>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201116:nPn8n5GmYa")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T06:00:02-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"WPI-MANA Team Creates Thermoelectric Device Combined with Wavelength-selective Thermal Emitter That Generates Continuous Power, Day and Night");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201116:nPn8n5GmYa</Origin>\n\nWPI-MANA Team Creates Thermoelectric Device Combined with Wavelength-selective Thermal Emitter That Generates Continuous Power, Day and Night\n\nPR Newswire\n\nTSUKUBA, Japan, Nov. 16, 2020\n\nTSUKUBA, Japan, Nov. 16, 2020 /PRNewswire/ -- A team at WPI-MANA has created a\nthermoelectric device that can generate power continuously, 24 hours a day,\nwithout the problem of voltage dropping to zero when night falls and\ntemperatures drop.\n\n(Image:\n<Origin Href=\\\"Link\\\">https://kyodonewsprwire.jp/prwfile/release/M105739/202011096797/_prw_PI1fl_q8Y4RCD3.jpg</Origin>\n(<Origin Href=\\\"Link\\\">https://kyodonewsprwire.jp/prwfile/release/M105739/202011096797/_prw_PI1fl_q8Y4RCD3.jpg</Origin>)\n)\n\nThermoelectric devices have been attracting attention for energy harvesting,\nespecially those that require independent power supply, such as outdoor\nsensors and monitors. Such devices, which are more ubiquitous than\nphotovoltaics, only require temperature difference between its top and bottom\nto generate power.\n\nHowever, thermoelectric devices placed outside experience reversal of voltage\nwhen temperatures change -- they flip the sign of their voltage, and the\nelectrical current changes its direction of flow, so the voltage drops to zero\nand power generation ceases.\n\nTo address this problem, the MANA team built their device with a\nwavelength-selective emitter that continually radiates heat, so that its\nsurface temperature is always cooler than the bottom side of the\nthermoelectric module, which is placed below the selective emitter.\n\nThe device consists of a 100-nanometer-thick aluminum film on the bottom of a\nglass substrate. Because the top of the device is cooler than the bottom, the\ntemperature difference creates constant voltage all day and night.\n\nThe team found that using a selective emitter eliminates the problem of\nvoltage dropping to zero during environmental changes in temperature.\n\nAs team leader Satoshi Ishii noted, \"Cooling can be used to create a\ntemperature difference compared to the ambient temperature, and because\nradiative cooling takes place day and night, thermoelectric generation is\nalways possible.\"\n\nThe larger the temperature difference, the larger the voltage. Using the heat\non the underside of the device increases the temperature difference between\nthe bottom and top, so heat from the mounting surface helps boost power output\nas well.\n\nThis research was carried out by Ishii (Principal Researcher, Photonics\nNano-Engineering Group, WPI-MANA, NIMS) and his collaborators.\n\n\"Radiative cooling for continuous thermoelectric power generation in day and\nnight\"\nSatoshi Ishii et al., Appl. Phys. Lett. (July 7, 2020);\nDOI: 10.1063/5.0010190\n\nMANA E-BULLETIN\n<Origin Href=\\\"Link\\\">https://www.nims.go.jp/mana/ebulletin/</Origin>\n(<Origin Href=\\\"Link\\\">https://www.nims.go.jp/mana/ebulletin/</Origin>)\n\nMANA International Symposium 2021\n<Origin Href=\\\"Link\\\">https://www.nims.go.jp/mana/2021/</Origin> (<Origin Href=\\\"Link\\\">https://www.nims.go.jp/mana/2021/</Origin>)\n\nView original\ncontent:<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/wpi-mana-team-creates-thermoelectric-device-combined-with-wavelength-selective-thermal-emitter-that-generates-continuous-power-day-and-night-301173325.html</Origin>\n(<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/wpi-mana-team-creates-thermoelectric-device-combined-with-wavelength-selective-thermal-emitter-that-generates-continuous-power-day-and-night-301173325.html</Origin>)\n\nSOURCE International Center for Materials Nanoarchitectonics (WPI-MANA),\nNational Institute for Materials Science (NIMS)\n\n\n\nRisa Sawada, MANA Planning and Outreach Team, International Center for Materials Nanoarchitectonics (WPI-MANA), National Institute for Materials Science (NIMS), Tel: +81-29-860-4710, Email: mana-pr@nims.go.jp\n\nCopyright (c) 2020 PR Newswire Association,LLC. All Rights Reserved.</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201109:nCNWs6qtka")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-09T22:10:40-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Montage Gold Inc. Issues Stock Options");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201109:nCNWs6qtka</Origin>Montage Gold Inc. Issues Stock OptionsCanada NewsWireVANCOUVER, BC, Nov. 9, 2020VANCOUVER, BC, Nov. 9, 2020 /CNW/ - Montage Gold Inc. (TSXV: MAU) (\"Montage\"or the \"Company\") announces that it has granted an aggregate 3,800,000incentive stock options to certain officers, directors and other eligiblepersons of the Company. The options are exercisable, subject to vestingprovisions, over a period of three years at a price of $1.30 per share.(<Origin Href=\\\"Link\\\">https://mma.prnewswire.com/media/1330601/Montage_Gold_Corp_Montage_Gold_Inc__Issues_Stock_Options.html</Origin>)About Montage Gold Corp.Montage Gold Corp. is a Canadian-based precious metals exploration anddevelopment company focused on opportunities in Cd'Ivoire. The Company'sflagship property is the Morondo Gold Project (\"MGP\"), located in northwestCd'Ivoire, which hosts an inferred mineral resource of 52.5Mt grading0.91 g/t for 1,536koz of gold, based on a 0.5 g/t cutoff grade. Montage has amanagement team and board with significant experience in discovering anddeveloping gold deposits in Africa. The Inferred Mineral Resource wasestimated using an optimal pit shell generated for constraining InferredMineral Resources with  dimensions of approximately 1,100 m by 620 m, with amaximum depth of around 260 m, and used a gold price of US$1,500/oz. See theCompany's amended and restated technical report entitled \"Amended and RestatedNI 43-101 Technical Report for the Morondo Gold Project, Cd'Ivoire\" withan effective date of September 17, 2020, available on SEDAR at <Origin Href=\\\"Link\\\">www.sedar.com</Origin>,which was prepared for the Company by Jonathon Robert Abbott, BASc Appl. Geol,MAIG, of MPR Geological Consultants Pty Ltd. and Remi Bosc, Eurgeol, ofArethuse Geology SARL, who are \"qualified persons\" and \"independent\" ofMontage within the meanings of NI 43-101.Forward Looking StatementsThis press release contains certain forward-looking information andforward-looking statements within the meaning of Canadian securitieslegislation (collectively, \"Forward-looking Statements\"). All statements,other than statements of historical fact, constitute Forward-lookingStatements. Words such as \"will\", \"intends\", \"proposed\" and \"expects\" orsimilar expressions are intended to identify Forward-looking Statements.Forward looking Statements in this press release include statements related tothe completion of the Offering, the listing of the Company's common shares onthe TSXV, the proceeds to be raised pursuant to the Offering, the exercise ofthe Over-Allotment Option, the use of proceeds from the Offering, theCompany's resource properties, and the Company's plans, focus and objectives.Forward-looking Statements involve various risks and uncertainties and arebased on certain factors and assumptions. There can be no assurance that suchstatements will prove to be accurate, and actual results and future eventscould differ materially from those anticipated in such statements. Importantfactors that could cause actual results to differ materially from theCompany's expectations include uncertainties related to fluctuations in goldand other commodity prices, uncertainties inherent in the exploration ofmineral properties, the Company's ability to complete the Offering, the impactand progression of the COVID-19 pandemic and other risk factors set forth inthe Company's final prospectus under the heading \"Risk Factors\". The Companyundertakes no obligation to update or revise any Forward-looking Statements,whether as a result of new information, future events or otherwise, except asmay be required by law. New factors emerge from time to time, and it is notpossible for Montage to predict all of them, or assess the impact of each suchfactor or the extent to which any factor, or combination of factors, may causeresults to differ materially from those contained in any Forward-lookingStatement. Any Forward-looking Statements contained in this press release areexpressly qualified in their entirety by this cautionary statement.SOURCE Montage Gold CorpView original content to download multimedia:<Origin Href=\\\"Link\\\">http://www.newswire.ca/en/releases/archive/November2020/09/c1910.html</Origin></p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201030:nGNX7nSvLd")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-10-30T12:32:41-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Montage Gold Corp. Announces Full Exercise of UnderwritersOver-Allotment Option");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201030:nGNX7nSvLd</Origin>NOT FOR DISTRIBUTION TO U.S. NEWSWIRE SERVICES OR FOR RELEASE, PUBLICATION,DISTRIBUTION OR DISSEMINATION DIRECTLY OR INDIRECTLY, IN WHOLE OR IN PART, INOR INTO THE UNITED STATESVANCOUVER, British Columbia, Oct. 30, 2020 (GLOBE NEWSWIRE) -- Montage GoldCorp. (or the today announced that in connectionwith its previously announced upsized initial public offering (theof an aggregate of 27,272,728 common shares (the a price of $1.10 per Share (the Pricefor total grossproceeds of $30,000,001, which closed on October 23, 2020, the underwritershave fully-exercised their over-allotment option to acquire an additional4,090,909 Shares at the Offering Price resulting in additional aggregate grossproceeds of approximately $4,500,000.Raymond James Ltd. and Stifel GMP acted as co-lead underwriters for theOffering with respect to a syndicate that included BMO Nesbitt Burns Inc.,Canaccord Genuity Corp., Beacon Securities Limited, Cormark Securities Inc.,and Sprott Capital Partners LP.No securities regulatory authority has either approved or disapproved of thecontents of this news release. The common shares have not been and will not beregistered under the United States Securities Act of 1933, as amended (theSecurities Actor any state securities laws. Accordingly, thecommon shares may not be offered or sold within the United States unlessregistered under the U.S. Securities Act and applicable state securities lawsor pursuant to exemptions from the registration requirements of the U.S.Securities Act and applicable state securities laws. This news release doesnot constitute an offer to sell or a solicitation of an offer to buy anysecurities of the Company in any jurisdiction in which such offer,solicitation or sale would be unlawful.About Montage Gold Corp.Montage Gold Corp. is a Canadian-based precious metals exploration anddevelopment company focused on opportunities in CdTheCompanyflagship property is the Morondo Gold Project (locatedin northwest Cdwhich hosts an Inferred Mineral Resource of52.5Mt grading 0.91 g/t for 1,536koz of gold, based on a 0.5 g/t cut-offgrade. Montage has a management team and board with significant experience indiscovering and developing gold deposits in Africa. The Inferred MineralResource was estimated using an optimal pit shell generated for constrainingInferred Mineral Resources with dimensions of approximately 1,100 m by 620 m,with a maximum depth of around 260 m, and used a gold price of US$1,500/oz.See the Companyamended and restated technical report entitled Restated NI 43-101 Technical Report for the Morondo Gold Project, Cwith an effective date of September 17, 2020, available on SEDARat <Origin Href=\\\"Link\\\">www.sedar.com</Origin>, which was prepared for the Company by Jonathon RobertAbbott, BASc Appl. Geol, MAIG, of MPR Geological Consultants Pty Ltd. and RemiBosc, Eurgeol, of Arethuse Geology SARL, who are personsandof Montage within the meanings of NI 43-101.For Further Information, Contact:Adam SpencerExecutive Vice President, Corporate Developmentaspencer@montagegoldcorp.comForward Looking StatementsThis press release contains certain forward-looking information andforward-looking statements within the meaning of Canadian securitieslegislation (collectively, StatementsAll statements,other than statements of historical fact, constitute Forward-lookingStatements. Words such as andor similar expressions are intended to identify Forward-lookingStatements. Forward looking Statements in this press release includestatements related to the Companyresource properties, and the Companyfocus and objectives. Forward-looking Statements involve various risksand uncertainties and are based on certain factors and assumptions. There canbe no assurance that such statements will prove to be accurate, and actualresults and future events could differ materially from those anticipated insuch statements. Important factors that could cause actual results to differmaterially from the Company's expectations include uncertainties related tofluctuations in gold and other commodity prices, uncertainties inherent in theexploration of mineral properties, the impact and progression of the COVID-19pandemic and other risk factors set forth in the Companyfinal prospectusunder the heading FactorsThe Company undertakes no obligation toupdate or revise any Forward-looking Statements, whether as a result of newinformation, future events or otherwise, except as may be required by law. Newfactors emerge from time to time, and it is not possible for Montage topredict all of them, or assess the impact of each such factor or the extent towhich any factor, or combination of factors, may cause results to differmaterially from those contained in any Forward-looking Statement. AnyForward-looking Statements contained in this press release are expresslyqualified in their entirety by this cautionary statement.(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/72d06585-8bff-4a56-a487-2a093d2bb531</Origin>)GlobeNewswire, Inc. 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201029:nL4N2HK569")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-10-29T23:51:22-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 5-Facebook anticipates tougher 2021 even as pandemic boosts ad revenue");
			hm1.put("TE",
					"<p> (Adds CFO comment)    By Katie Paul and Noor Zainab Hussain    Oct 29 (Reuters) - Facebook Inc  <Origin Href=\\\"QuoteRef\\\">FB.O</Origin>  on Thursday warned ofa tougher 2021  despite beating analysts' estimates forquarterly revenue as businesses adjusting to the globalcoronavirus pandemic continued to rely on the company's digitalad tools.    The world's biggest social media company said in its outlookthat it faced \"a significant amount of uncertainty,\" citingimpending privacy changes by Apple  <Origin Href=\\\"QuoteRef\\\">APPL.O</Origin>  and a possiblereversal in the pandemic-prompted shift to online commerce.    \"Considering that online commerce is our largest advertical, a change in this trend could serve as a headwind toour 2021 ad revenue growth,\" it said.    Shares of the company were flat in extended trading.    Facebook's financial results and those of Google  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin> and Amazon  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin>  demonstrate how resilient tech giants havebeen even as the pandemic devastated other parts of the economy.    The success has earned them extra scrutiny in Washington,where the companies face multiple antitrust investigations.    Facebook's total revenue, which primarily consists of adsales, rose 22% to $21.47 billion from $17.65 billion in thethird quarter ended Sept. 30, beating analysts' estimates of a12% rise, according to IBES data from Refinitiv.    A July ad boycott over Facebook's handling of hate speech,which saw some of the social media giant's biggest individualspenders press pause, barely made a dent in its sales, whichmostly come from small businesses.    Revenue growth at Facebook, the world's second-biggestseller of online ads after Google, has been cooling steadily asits business matures, although it came in at more than 20%throughout 2019.    Still, compared to expectations, the company has had abumper year due to surging use of its platforms by users stuckat home amid virus-related lockdowns, which cushioned online adsales even as broader economic activity suffered.        USER BASE GROWTH    Facebook continued to expand its user base, with monthlyactive users rising to 2.74 billion, compared with estimates of2.70 billion according to the IBES data, although user numbersdeclined in North America compared to the second quarter.    The company projected that trend would continue for the restof the year, with user numbers either flat or slightly down inthe fourth quarter compared to the third quarter.    \"It appears that investors are disappointed that despiteuser growth jumping across most regions during the quarter, thesocial media platform reported a decrease in users in NorthAmerica, which covers the U.S. and Canada - its most lucrativead market,\" said Jesse Cohen, senior analyst at Investing.com.    Total expenses increased 28% to $13.43 billion, with costscontinuing to grow as Facebook tries to build out its non-adbusinesses and quell criticism that its handling of user privacyand abusive content is lax.    Facebook CFO Dave Wehner said on an earnings conference callthat expenses would rise due to the costs of returningwork-from-home staff to offices as well as increased headcount,product investments and higher legal expenses.     He said the company was expecting a margin decline as aresult, although he did not give specific revenue guidance.    The company has been under especially strong pressure aheadof next week's U.S. presidential election and is aiming to avoida repeat of 2016, when Russia used its platforms to spreadelection-related misinformation.    EMarketer principal analyst Debra Aho Williamson saidFacebook remains \"a go-to for advertisers\" seeking to reach abroad set of consumers, despite its content moderation issues,but said that may change in 2021.    \"We expect that more advertisers will take a hard look attheir reliance on Facebook and will ask themselves whether theenvironment is safe for their brands,\" she said.    Net income came in at $7.85 billion, or $2.71 per share,compared with $6.09 billion, or $2.12 per share, a year earlier.Analysts had expected a profit of $1.90 per share, according toIBES data from Refinitiv.    <^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^Facebook's quarterly revenue growth    <Origin Href=\\\"Link\\\">https://tmsnrt.rs/3kJv7yd</Origin>    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^> (Reporting by Noor Zainab Hussain in Bengaluru and Katie Paulin San Francisco; Editing by Cynthia Osterman) ((noor.hussain@thomsonreuters.com; Within U.S. +1 646 223 8780;Outside U.S. +91 80 6182 2663 or +91 80 3796 2663))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201116:nPRrGB7ADa")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T14:04:27-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "REG-BlackRock Grtr Eur: Portfolio Update");
			hm1.put("TE",
					"<p>The information contained in this release was correct as at 31 October 2020.Information on the Companyup to date net asset values can be found on theLondon Stock Exchange website at:<Origin Href=\\\"Link\\\">https://www.londonstockexchange.com/exchange/news/market-news/market-news-home.html.</Origin> BLACKROCK GREATER EUROPE INVESTMENT TRUST PLC (LEI - 5493003R8FJ6I76ZUW55)All information is at 31 October 2020 and unaudited.Performance at month end with net income reinvested                                One Month  Three Months  One Year  Three Years  Launch (20 Sep 04)  Net asset value (undiluted)       -4.3%          2.9%     15.8%        35.2%              503.1%  Net asset value* (diluted)        -4.3%          3.1%     15.7%        35.2%              503.3%  Share price                       -4.1%         -0.7%     14.8%        33.7%              482.6%  FTSE World Europe ex UK           -6.1%         -3.3%     -4.2%         0.8%              251.0% * Diluted for treasury shares and subscription shares.Sources: BlackRock and Datastream. At month end Net asset value (capital only):              453.37p  Net asset value (including income):          453.90p  Net asset value (capital only) (1):          453.37p  Net asset value (including income) (1):      453.90p  Share price:                                 434.00p  Discount to NAV (including income):             4.4%  Discount to NAV (including income) (1):         4.4%  Net gearing:                                   10.1%  Net yield (2):                                  1.4%  Total assets (including income):              382.7m  Ordinary shares in issue (3):             84,323,101  Ongoing charges (4):                            1.0%                                                       1  Diluted for treasury shares.2  Based on an interim dividend of 1.75p per share and a final dividend of4.40p per share for the year ended 31 August 2020.3  Excluding 26,005,837 shares held in treasury.4  Calculated as a percentage of average net assets and using expenses,excluding interest costs, after relief for taxation, for the year ended 31August 2020. Sector Analysis      Total Assets (%)   Country Analysis     Total Assets (%)  Technology                       24.1   Denmark                          19.7  Industrials                      21.9   Switzerland                      15.0  Health Care                      18.4   Netherlands                      13.3  Consumer Goods                   11.2   France                           13.3  Consumer Services                 8.5   Germany                           7.1  Financials                        7.9   Italy                             6.0  Basic Materials                   3.9   Sweden                            5.4  Oil & Gas                         3.4   Poland                            3.4  Telecommunications                1.7   United Kingdom                    3.4  Net Current Assets               -1.0   Israel                            2.7                                  -----   Russia                            2.6                                  100.0   Finland                           2.3                                  =====   Spain                             2.3                                          Ireland                           2.3                                          Belgium                           2.0                                          Greece                            0.2                                          Net Current Assets               -1.0                                                                          -----                                                                          100.0                                                                          =====     Top 10 Holdings  Country          Fund %  Sika             Switzerland         6.0  ASML             Netherlands         5.9  Kering           France              5.1  Lonza Group      Switzerland         5.1  Novo Nordisk     Denmark             5.0  Royal Unibrew    Denmark             4.7  DSV              Denmark             4.5  RELX             United Kingdom      3.3  Hexagon          Sweden              3.3  Safran           France              3.1 Commenting on the markets, Stefan Gries, representing the Investment Manager,noted:During the month, the CompanyNAV fell by 4.3% and the share pricedeclined by 4.1%. For reference, the FTSE World Europe ex UK Index returned-6.1% during the period.Europe ex UK markets sold off during the month as surging coronavirusinfection rates, as well as the reintroduction of national lockdowns in manyEuropean countries weighed on sentiment. While hospitalisation rates rise, wewould note that increased hospital capacity and care and a betterunderstanding of the virus have so far led to lower mortality rates thanduring the first wave. We believe the economic impact of a second lockdown islikely to be more muted this time around.Many companies are now facing earnings downgrades in Q4 and Q1, but theoverall market level is supported by the idea of a vaccine being announced inthe near-term, as well as continued recovery in selective end markets goinginto 2021.In October, defensive sectors such as telecommunications and utilities held upslightly better, while technology and oil & gas saw the sharpest market falls.The Company outperformed the reference index, driven by strong stock selectionwhile sector allocation was negative.In sector terms, the Company benefited from being overweight consumer servicesand industrials. Our higher exposure to technology detracted from returns,although this was more than offset by strong stock selection. While theCompanyhigher allocation to healthcare a sector that oftenexperiences volatility ahead of US elections detracted, this was also morethan offset by accurate stock selection.Stock selection was strongest within technology, where a new position inAllegro was the top performer over the month. This Polish e-commerce platformselling both international and local brands enjoyed a strong share priceperformance after only going public in early October. We believe the groupprospects look strong over the medium to long term, as onlinepenetration in the Polish retail market remains relatively low. Within thesame sector, Netcompany and ASML were also amongst the strongest contributors.The industrials sector contributed to relative returns with a holding in Sikabeing amongst the best performers, beating analyst expectations and raisingguidance. The firm has rebounded quickly from considerably negative organicgrowth in Q2 to being flat in Q3, while also showing strong progression inmargins.French aerospace company Safran also reported strong Q3 numbers towards theend of the month and commentary on improving business trends was taken well bythe market. Further, the companyextensive cost cutting programme isrunning ahead of expectations. Stock selection within healthcare aided returns. Italian DiaSorin performedwell given the launch of their COVID antigen test. Additionally, not owningBayer was beneficial. The company issued a profit warning with the pandemichitting profits harder than expected, forcing Bayer to write down the value ofassets in its agricultural business.Strong contribution also came from luxury brand Hermes which continued tobenefit from the outstanding resilience of the high-end consumer. Within thiscontext, not owning LVMH was negative as the company delivered strong Q3results.The Companyinvestment in software group SAP was the largest detractorduring October. The company published weaker than expected results and cut itsrevenue and profit forecasts for this year due to depressed business spendingas coronavirus cases continue to rise. Importantly, SAP have updated mediumterm targets, essentially pushing them out by circa two years in part toreflect disruption caused by COVID. We had been expecting the management teamto update the strategic targets in light of COVID, however were left surprisedby the extent of the revision to forecasts.A negative contribution also came from RELX which continues to see a COVIDimpact on their exhibitions business, as well as concerns around lowerjournals and subscription numbers from universities.At the end of the period the Company had a higher allocation than thereference index towards technology, consumer services, industrials and healthcare. The Company had a neutral weighting towards oil & gas and underweightallocation to consumer goods, financials, utilities, basic materials andtelecoms.OutlookOver recent years, many investors have avoided exposure to European equitiesowing to concerns around political risk, rising populism and a challengedfinancial system. We have long been of the view that one needs to take anactive approach to investing in European equities. With this in mind, we feltthat for active stock pickers Europe as a region can offer shareholders accessto some highly attractive companies listed in the region. The response to thefallout from COVID-19 has the potential to change the more negative perceptionon the asset class as a whole.The proposed billion EU Recovery Fund is a significant step ofsolidarity for the bloc and one that can potentially bring greater fiscalcoordination. In this context, both the economy and local stock markets appearwell positioned to make up lost ground, potentially transforming Europeanequities into a standout opportunity in the developed world, while notablyproviding further subsidies for growth in Emerging Europe.16 November 2020ENDSLatest information is available by typing <Origin Href=\\\"Link\\\">www.brgeplc.co.uk</Origin> on the internet,\"BLRKINDEX\" on Reuters, \"BLRK\" on Bloomberg or \"8800\" on Topic 3 (ICVterminal).  Neither the contents of the Managerwebsite nor the contentsof any website accessible from hyperlinks on the Managerwebsite (or anyother website) is incorporated into, or forms part of, this announcement.Copyright (c) 2020 PR Newswire Association,LLC. All Rights Reserved.</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201109:nCNWs6qtka")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-09T22:10:40-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Montage Gold Inc. Issues Stock Options");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201109:nCNWs6qtka</Origin>Montage Gold Inc. Issues Stock OptionsCanada NewsWireVANCOUVER, BC, Nov. 9, 2020VANCOUVER, BC, Nov. 9, 2020 /CNW/ - Montage Gold Inc. (TSXV: MAU) (\"Montage\"or the \"Company\") announces that it has granted an aggregate 3,800,000incentive stock options to certain officers, directors and other eligiblepersons of the Company. The options are exercisable, subject to vestingprovisions, over a period of three years at a price of $1.30 per share.(<Origin Href=\\\"Link\\\">https://mma.prnewswire.com/media/1330601/Montage_Gold_Corp_Montage_Gold_Inc__Issues_Stock_Options.html</Origin>)About Montage Gold Corp.Montage Gold Corp. is a Canadian-based precious metals exploration anddevelopment company focused on opportunities in Cd'Ivoire. The Company'sflagship property is the Morondo Gold Project (\"MGP\"), located in northwestCd'Ivoire, which hosts an inferred mineral resource of 52.5Mt grading0.91 g/t for 1,536koz of gold, based on a 0.5 g/t cutoff grade. Montage has amanagement team and board with significant experience in discovering anddeveloping gold deposits in Africa. The Inferred Mineral Resource wasestimated using an optimal pit shell generated for constraining InferredMineral Resources with  dimensions of approximately 1,100 m by 620 m, with amaximum depth of around 260 m, and used a gold price of US$1,500/oz. See theCompany's amended and restated technical report entitled \"Amended and RestatedNI 43-101 Technical Report for the Morondo Gold Project, Cd'Ivoire\" withan effective date of September 17, 2020, available on SEDAR at <Origin Href=\\\"Link\\\">www.sedar.com</Origin>,which was prepared for the Company by Jonathon Robert Abbott, BASc Appl. Geol,MAIG, of MPR Geological Consultants Pty Ltd. and Remi Bosc, Eurgeol, ofArethuse Geology SARL, who are \"qualified persons\" and \"independent\" ofMontage within the meanings of NI 43-101.Forward Looking StatementsThis press release contains certain forward-looking information andforward-looking statements within the meaning of Canadian securitieslegislation (collectively, \"Forward-looking Statements\"). All statements,other than statements of historical fact, constitute Forward-lookingStatements. Words such as \"will\", \"intends\", \"proposed\" and \"expects\" orsimilar expressions are intended to identify Forward-looking Statements.Forward looking Statements in this press release include statements related tothe completion of the Offering, the listing of the Company's common shares onthe TSXV, the proceeds to be raised pursuant to the Offering, the exercise ofthe Over-Allotment Option, the use of proceeds from the Offering, theCompany's resource properties, and the Company's plans, focus and objectives.Forward-looking Statements involve various risks and uncertainties and arebased on certain factors and assumptions. There can be no assurance that suchstatements will prove to be accurate, and actual results and future eventscould differ materially from those anticipated in such statements. Importantfactors that could cause actual results to differ materially from theCompany's expectations include uncertainties related to fluctuations in goldand other commodity prices, uncertainties inherent in the exploration ofmineral properties, the Company's ability to complete the Offering, the impactand progression of the COVID-19 pandemic and other risk factors set forth inthe Company's final prospectus under the heading \"Risk Factors\". The Companyundertakes no obligation to update or revise any Forward-looking Statements,whether as a result of new information, future events or otherwise, except asmay be required by law. New factors emerge from time to time, and it is notpossible for Montage to predict all of them, or assess the impact of each suchfactor or the extent to which any factor, or combination of factors, may causeresults to differ materially from those contained in any Forward-lookingStatement. Any Forward-looking Statements contained in this press release areexpressly qualified in their entirety by this cautionary statement.SOURCE Montage Gold CorpView original content to download multimedia:<Origin Href=\\\"Link\\\">http://www.newswire.ca/en/releases/archive/November2020/09/c1910.html</Origin></p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201113:nBw88YMtTa")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-13T18:07:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"$422.9 Billion Worldwide Winter Wear Industry to 2027 - Impact of COVID-19 on the Market - ResearchAndMarkets.com");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201113:nBw88YMtTa</Origin>$422.9 Billion Worldwide Winter Wear Industry to 2027 - Impact of COVID-19 onthe Market - ResearchAndMarkets.comThe \"Winter Wear - Global Market Trajectory & Analytics\"(<Origin Href=\\\"Link\\\">https://cts.businesswire.com/ct/CT?id=smartlink&url=https%3A%2F%2Fwww.researchandmarkets.com%2Freports%2F5140439%2Fwinter-wear-global-market-trajectory-and%3Futm_source%3DBW%26utm_medium%3DPressRelease%26utm_code%3Dqcdzmx%26utm_campaign%3D1463607%2B-%2B%2524422.9%2BBillion%2BWorldwide%2BWinter%2BWear%2BIndustry%2Bto%2B2027%2B-%2BImpact%2Bof%2BCOVID-19%2Bon%2Bthe%2BMarket%26utm_exec%3Djamu273prd&esheet=52327234&newsitemid=20201113005616&lan=en-US&anchor=%26quot%3BWinter+Wear+-+Global+Market+Trajectory+%26amp%3B+Analytics%26quot%3B&index=1&md5=23861d88eb6d4f7413e774590ea2e240</Origin>)report has been added to ResearchAndMarkets.com's offering.The publisher brings years of research experience to the 9th edition of thisreport. The 182-page report presents concise insights into how the pandemichas impacted production and the buy side for 2020 and 2021. A short-termphased recovery by key geography is also addressed.Global Winter Wear Market to Reach $582.4 Billion by 2027Amid the COVID-19 crisis, the global market for Winter Wear estimated atUS$422.9 Billion in the year 2020, is projected to reach a revised size ofUS$582.4 Billion by 2027, growing at a CAGR of 4.7% over the period 2020-2027.Winter Apparel, one of the segments analyzed in the report, is projected torecord 4.8% CAGR and reach US$528.5 Billion by the end of the analysis period.After an early analysis of the business implications of the pandemic and itsinduced economic crisis, growth in the Winter Footwear segment is readjustedto a revised 3.8% CAGR for the next 7-year period.The U.S. Market is Estimated at $114.6 Billion, While China is Forecast toGrow at 7.2% CAGRThe Winter Wear market in the U.S. is estimated at US$114.6 Billion in theyear 2020. China, the world's second largest economy, is forecast to reach aprojected market size of US$118.4 Billion by the year 2027 trailing a CAGR of7.2% over the analysis period 2020 to 2027. Among the other noteworthygeographic markets are Japan and Canada, each forecast to grow at 2.6% and4.2% respectively over the 2020-2027 period. Within Europe, Germany isforecast to grow at approximately 2.9% CAGR.Competitors identified in this market include, among others: * Amazon. com, Inc. * Backcountry. com, LLC * CustomInk LLC * Gap, Inc. * J. C. Penney Co., Inc. * Kohl's Corporation * LVMH Moet Hennessy - Louis Vuitton * Macy's, Inc. * Nordstrom, Inc. * VF Corporation * Wal-Mart, Inc.Key Topics Covered:I. INTRODUCTION, METHODOLOGY & REPORT SCOPEII. EXECUTIVE SUMMARY1. MARKET OVERVIEW * Global Competitor Market Shares * Winter Wear Competitor Market Share Scenario Worldwide (in %): 2019 & 2025 * Impact of Covid-19 and a Looming Global Recession2. FOCUS ON SELECT PLAYERS3. MARKET TRENDS & DRIVERS4. GLOBAL MARKET PERSPECTIVEIII. MARKET ANALYSISIV. COMPETITION * Total Companies Profiled: 44For more information about this report visit<Origin Href=\\\"Link\\\">https://www.researchandmarkets.com/r/hy8zm3</Origin>(<Origin Href=\\\"Link\\\">https://cts.businesswire.com/ct/CT?id=smartlink&url=https%3A%2F%2Fwww.researchandmarkets.com%2Freports%2F5140439%2Fwinter-wear-global-market-trajectory-and%3Futm_source%3DBW%26utm_medium%3DPressRelease%26utm_code%3Dqcdzmx%26utm_campaign%3D1463607%2B-%2B%2524422.9%2BBillion%2BWorldwide%2BWinter%2BWear%2BIndustry%2Bto%2B2027%2B-%2BImpact%2Bof%2BCOVID-19%2Bon%2Bthe%2BMarket%26utm_exec%3Djamu273prd&esheet=52327234&newsitemid=20201113005616&lan=en-US&anchor=https%3A%2F%2Fwww.researchandmarkets.com%2Fr%2Fhy8zm3&index=2&md5=4af012fffac833f2d7a478db0dc54ca2</Origin>).View source version on businesswire.com:<Origin Href=\\\"Link\\\">https://www.businesswire.com/news/home/20201113005616/en/</Origin>(<Origin Href=\\\"Link\\\">https://www.businesswire.com/news/home/20201113005616/en/</Origin>)ResearchAndMarkets.comLaura Wood, Senior Press Managerpress@researchandmarkets.com (mailto:press@researchandmarkets.com) For E.S.T. Office Hours Call 1-917-300-0470For U.S./CAN Toll Free Call 1-800-526-8630For GMT Office Hours Call +353-1-416-8900Copyright Business Wire 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201112:nPnbc3Fs4a")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-12T14:00:11-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"Narvar Simplifies Returns for Consumers and Retailers, Launching Digital, Boxless Returns with Cole Haan");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201112:nPnbc3Fs4a</Origin>Narvar Simplifies Returns for Consumers and Retailers, Launching Digital, Boxless Returns with Cole HaanCole Haan leads in retail innovation by offering convenient online returns without packaging or label in time for peak holiday season.PR NewswireSAN FRANCISCO, Nov. 12, 2020SAN FRANCISCO, Nov. 12, 2020 /PRNewswire/ -- It's well known that consumersare more than twice as likely to return a purchase made online, and theexperience impacts sentiment(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&l=en&o=2979021-1&h=101690345&u=https%3A%2F%2Fgo.forrester.com%2Fblogs%2Fretails-secret-growth-weapon-generous-flexible-online-returns%2F&a=the+experience+impacts+sentiment</Origin>), so smart retailers are making the returns process as easy as possible. Enterthe ability to send back merchandise without having to repackage the shippingbox or even print out a label.(<Origin Href=\\\"Link\\\">https://mma.prnewswire.com/media/335452/Narvar_Logo.html</Origin>)Cole Haan customers can drop off returns powered by Narvar with no box or label at nearly 5,000 The UPS Store locations.Narvar(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&l=en&o=2979021-1&h=2300086854&u=https%3A%2F%2Fcorp.narvar.com%2F%3Futm_source%3Dpress%26%26utm_medium%3Dreferral%26%26utm_content%3Dpressrelease%26%26utm_campaign%3DUPSBoxless&a=Narvar</Origin>), an intelligent customer engagement platform, today announced an integrationwith UPS Digital Returns to enable no-label and boxless returns of ecommercepurchases made from global performance lifestyle brand Cole Haan. Thisinnovative service, which allows consumers to quickly drop off returns to bepackaged and labeled at The UPS Store, is expected to expand to many retailbrands.Starting this week, consumers making an eligible return will see the option toreturn their item with no additional packaging or label required via ColeHaan's online returns portal powered by Narvar. Clear instructions with amobile QR code and closest locations are provided for easy drop-off; 85% ofthe U.S. population lives within 10 miles of one of nearly 5,000 The UPS Storelocations. The returns service will require a nominal fee.As ecommerce has increased, remote work has accelerated the need forconvenience and safety in the returns experience. Demand for more accessiblelocations for drop-offs and unlabeled boxless returns has increased. RecentNarvar consumer research(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&l=en&o=2979021-1&h=3459266175&u=https%3A%2F%2Fcorp.narvar.com%2Fstate-of-returns-new-expectations%3Futm_source%3Dpress%26utm_medium%3Dreferral%26utm_content%3Dpressrelease%26utm_campaign%3DUPSBoxless&a=Narvar+consumer+research</Origin>)has found that both desire for and usage of unlabeled returns has doubledsince last year, with 27% of consumers using a mobile QR code instead ofprinted label for their last return. In addition, 21% indicated they would bewilling to pay up to $5 to have packaging and label provided for them. Thisservice answers that need.\"Although this has been a challenging year for retail, ecommerce has beensupercharged. It's critical that we prioritize customer experience, includingtimes when a customer wishes to make a return, which have long been a painpoint for both retailers and consumers. It's clear that this part of thejourney can drive customer loyalty, and we're happy to have been able to getthis convenient option up and running quickly with Narvar and UPS,\" commentedDavid Maddocks, Brand President of Cole Haan.Both Narvar and UPS operate at a scale to be able to support enterprise-levelretailers as well as direct-to-consumer brands. More than 300 Narvar customersglobally use the platform for online returns, including Gap, Ann Taylor, andFinish Line. This integration helps retailers get onboarded to the boxlessreturns option within about a week without any engineering or technical liftby the merchant.\"UPS continues to innovate with customer first experiences such as no-box,no-label returns to deliver what matters to e-commerce merchants andconsumers,\" said UPS Vice President of Global eCommerce Strategy Nick Basford.\"Embedding UPS Digital Returns functionality into Narvar's post-sale, consumerfacing solution and combining it with the physical reach of The UPS Storenetwork, creates a differentiated e-commerce solution to drive sales growth.\"\"Narvar's mission is to simplify the everyday lives of consumers, and removingthe friction of repackaging returns and printing labels at scale is animportant element to help retailers make the returns experience a competitiveadvantage,\" said Amit Sharma, founder and CEO of Narvar. \"We're excited todeepen our partnership with UPS to enable this convenient returns option atthousands of locations within minutes of most Americans, while requiringminimal effort from retailers like Cole Haan to offer this benefit to theircustomers.\"About NarvarNarvar is an intelligent customer experience platform that helps commercecompanies simplify the everyday lives of consumers. Serving over 800 retailersglobally including Sephora, Patagonia, Levi's, Bose, Warby Parker, Home Depot,LVMH, and L'OrNarvar ensures every touchpoint along the consumerpurchase journey engages consumers and enables emotional connectionsto in-store experiences and beyond. With customizable customermessaging and tailored interfaces driven by unparalleled data intelligence,Narvar empowers commerce brands to turn every touchpoint into an opportunity.For more information, visit narvar.com(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&l=en&o=2979021-1&h=1454693577&u=https%3A%2F%2Fcorp.narvar.com%2F%3Futm_source%3Dpress%26utm_medium%3Dreferral%26utm_content%3Dpressrelease%26utm_campaign%3DUPSBoxless&a=narvar.com</Origin>). View original content to downloadmultimedia:<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/narvar-simplifies-returns-for-consumers-and-retailers-launching-digital-boxless-returns-with-cole-haan-301171353.html</Origin>(<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/narvar-simplifies-returns-for-consumers-and-retailers-launching-digital-boxless-returns-with-cole-haan-301171353.html</Origin>)SOURCE NarvarAndria Tay, Director of Marketing, Narvar, press@narvar.comPhoto: <Origin Href=\\\"Link\\\">https://mma.prnewswire.com/media/335452/Narvar_Logo.jpg</Origin>Website: <Origin Href=\\\"Link\\\">https://www.narvar.com</Origin>Copyright (c) 2020 PR Newswire Association,LLC. All Rights Reserved.</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201116:nPNA4qXWga")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T14:44:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Covis Group Completes Acquisition of AMAG Pharmaceuticals");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201116:nPNA4qXWga</Origin>Covis Group Completes Acquisition of AMAG PharmaceuticalsPR NewswireLUXEMBOURG and ZUG, Switzerland, Nov. 16, 2020LUXEMBOURG and ZUG, Switzerland, Nov. 16, 2020 /PRNewswire/ -- Covis GroupS.r.l. (\"Covis\") today announced the completion of its acquisition of AMAGPharmaceuticals, Inc. (NASDAQ: AMAG) through the successful tender offer forall of the outstanding shares of common stock of AMAG at $13.75 per share incash and subsequent merger. The combined organization will operate as part ofthe Covis Pharma Group and will be led by Covis CEO Michael Porter.The addition of AMAG's category leading treatments and development-stageassets in women's health and hematology/oncology supports the execution ofCovis' strategic vision to enhance the company's ability to impact the livesof patients by expanding its portfolio of 'best in class' products inattractive new therapeutic areas. In addition, Covis will be positioned tofurther support patients, building on its track record of efficient andeffective management of therapeutic solutions.\"The acquisition of AMAG represents a key milestone in Covis' efforts tofulfill our strategic vision to become a leading global specialty pharmacompany for life threatening and chronic illnesses for both commercial anddevelopment stage assets,\" said Michael Porter, CEO of Covis. \"AMAG'sexpertise and key productsMakenaand Ciraparantagvalue to Covis as we execute our mandate of expanding patientaccess to much needed therapies. We will continue to put patients' interestsfirst and look forward to collaborating with the FDA to ensure that Makenacontinues to be available as an option for appropriate patients. More thanever, we are positioned to continue growth of our therapeutic portfolioglobally and look forward to further enhancing our capabilities and offerings.We're excited to work with our designated transition teams to execute on ourintegration plans and move forward as a stronger, unified organization.\"The tender offer expired at 12:00 a.m., Eastern Time, on November 12, 2020(one minute after 11:59 p.m., Eastern Time, on November 12, 2020). Thecondition to the tender offer that a majority of the outstanding shares ofAMAG's common stock be validly tendered and not withdrawn was satisfied and,accordingly, all such validly tendered shares were accepted for payment.Following the consummation of the tender offer, Covis Mergerco Inc. mergedwith and into AMAG pursuant to Section 251(h) of the General Corporation Lawof the State of Delaware. As a result of the merger, each share of AMAG thatwas not validly tendered in the tender offer (other than shares held by anystockholder of AMAG who properly demanded appraisal of such shares under theapplicable provisions of Delaware law) was cancelled and converted into theright to receive $13.75 per share in cash, and AMAG became an indirect whollyowned subsidiary of Covis. Shares of AMAG will cease to be traded on NASDAQ.Goldman Sachs & Co. LLC acted as exclusive financial advisor, and GoodwinProcter LLP acted as legal advisor to AMAG. Paul, Weiss, Rifkind, Wharton andGarrison LLP acted as legal advisor to Covis. Financing for this transactionhas been provided by Capital One, N.A. and investment funds and accountsmanaged by HPS Investment Partners, LLC.About CovisCovis is headquartered in Luxembourg with operations in Zug, Switzerland andis a global specialty pharmaceutical company that markets therapeuticsolutions for patients with life-threatening conditions and chronic illnesses.Additional information is available at <Origin Href=\\\"Link\\\">www.covispharma.com</Origin>.Cautionary Statement Regarding Forward-Looking StatementsThis communication contains forward-looking statements. Forward-lookingstatements relate to future events or Covis' future financial performance.Covis generally identifies forward-looking statements by terminology such as\"may,\" \"will,\" \"should,\" \"expects,\" \"plans,\" \"anticipates,\" \"could,\"\"intends,\" \"target,\" \"projects,\" \"contemplates,\" \"believes,\" \"estimates,\"\"predicts,\" \"potential\" or \"continue\" or the negative of these terms or othersimilar words. These statements are only predictions. Covis has based theseforward-looking statements largely on its then-current expectations andprojections about future events and financial trends as well as the beliefsand assumptions of management. Forward-looking statements are subject to anumber of risks and uncertainties, many of which involve factors orcircumstances that are beyond Covis' control. Covis' actual results coulddiffer materially from those stated or implied in forward-looking statementsdue to a number of factors, including but not limited to: risks associatedwith the combined organization following completion of the merger transactionincluding difficulties in executing Covis' strategic vision, continuing togrow Covis' therapeutic portfolio and enhancing Covis' capabilities andofferings; the outcome of any legal proceedings that may be instituted againstthe parties and others related to the merger transaction; the inability ofCovis' to impact the lives of patients by expanding its portfolio of 'best inclass' products in attractive new therapeutic areas; Covis not beingpositioned to further support patients, and failing to efficiently andeffectively manage therapeutic solutions; Covis not being able to fulfill itsstrategic vision of becoming a leading specialty pharma company globally forlife threatening and chronic illnesses for both commercial and developmentstage assets, which could have a material adverse impact on Covis' business,financial results and operations; Covis failing to collaborate with the FDA toensure that Makena continues to be available as an option for appropriatepatients; risks associated with Makena's withdrawal, including the impacts onCovis' financial results; Covis failing to grow its therapeutic portfolioglobally and enhancing its capabilities and offerings; Covis not being able toexecute on its integration plans, resulting in a failure to achieve synergiesor a more unified, stronger organization; and unanticipated difficulties orexpenditures relating to the merger transaction, the response of businesspartners and competitors to the completion of the merger transaction, and/orpotential difficulties in employee retention as a result of the completion ofthe merger transaction. Accordingly, you should not rely upon forward-lookingstatements as predictions of future events. Covis cannot assure you that theevents and circumstances reflected in the forward-looking statements will beachieved or occur, and actual results could differ materially from thoseprojected in the forward-looking statements. The forward-looking statementsmade in this communication relate only to events as of the date on which thestatements are made. Covis undertakes no obligation to update anyforward-looking statement to reflect events or circumstances after the date onwhich the statement is made or to reflect the occurrence of unanticipatedevents.ContactArik Ben-ZviBreakwater Strategy(202) 270-1848arik@breakwaterstrategy.comSOURCE Covis PharmaWebsite: <Origin Href=\\\"Link\\\">https://www.covispharma.com</Origin>Copyright (c) 2020 PR Newswire Association,LLC. All Rights Reserved.</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201116:nPre6CRv7a")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T14:26:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Covis Group Completes Acquisition of AMAG Pharmaceuticals");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201116:nPre6CRv7a</Origin>Covis Group Completes Acquisition of AMAG PharmaceuticalsPR NewswireLUXEMBOURG and ZUG, Switzerland, Nov. 16, 2020LUXEMBOURG and ZUG, Switzerland, Nov. 16, 2020 /PRNewswire/ -- Covis GroupS.r.l. (\"Covis\") today announced the completion of its acquisition of AMAGPharmaceuticals, Inc. (NASDAQ: AMAG) through the successful tender offer forall of the outstanding shares of common stock of AMAG at $13.75 per share incash and subsequent merger. The combined organization will operate as part ofthe Covis Pharma Group and will be led by Covis CEO Michael Porter.The addition of AMAG's category leading treatments and development-stageassets in women's health and hematology/oncology supports the execution ofCovis' strategic vision to enhance the company's ability to impact the livesof patients by expanding its portfolio of 'best in class' products inattractive new therapeutic areas. In addition, Covis will be positioned tofurther support patients, building on its track record of efficient andeffective management of therapeutic solutions.\"The acquisition of AMAG represents a key milestone in Covis' efforts tofulfill our strategic vision to become a leading global specialty pharmacompany for life threatening and chronic illnesses for both commercial anddevelopment stage assets,\" said Michael Porter, CEO of Covis. \"AMAG'sexpertise and key productsMakenaand Ciraparantagvalue to Covis as we execute our mandate of expanding patientaccess to much needed therapies. We will continue to put patients' interestsfirst and look forward to collaborating with the FDA to ensure that Makenacontinues to be available as an option for appropriate patients. More thanever, we are positioned to continue growth of our therapeutic portfolioglobally and look forward to further enhancing our capabilities andofferings.  We're excited to work with our designated transition teams toexecute on our integration plans and move forward as a stronger, unifiedorganization.\"The tender offer expired at 12:00 a.m., Eastern Time, on November 12, 2020(one minute after 11:59 p.m., Eastern Time, on November 12, 2020).  Thecondition to the tender offer that a majority of the outstanding shares ofAMAG's common stock be validly tendered and not withdrawn was satisfied and,accordingly, all such validly tendered shares were accepted for payment. Following the consummation of the tender offer, Covis Mergerco Inc. mergedwith and into AMAG pursuant to Section 251(h) of the General Corporation Lawof the State of Delaware. As a result of the merger, each share of AMAG thatwas not validly tendered in the tender offer (other than shares held by anystockholder of AMAG who properly demanded appraisal of such shares under theapplicable provisions of Delaware law) was cancelled and converted into theright to receive $13.75 per share in cash, and AMAG became an indirect whollyowned subsidiary of Covis. Shares of AMAG will cease to be traded on NASDAQ.Goldman Sachs & Co. LLC acted as exclusive financial advisor, and GoodwinProcter LLP acted as legal advisor to AMAG.  Paul, Weiss, Rifkind, Whartonand Garrison LLP acted as legal advisor to Covis. Financing for thistransaction has been provided by Capital One, N.A. and investment funds andaccounts managed by HPS Investment Partners, LLC.About CovisCovis is headquartered in Luxembourg with operations in Zug, Switzerland andis a global specialty pharmaceutical company that markets therapeuticsolutions for patients with life-threatening conditions and chronic illnesses. Additional information is available at <Origin Href=\\\"Link\\\">www.covispharma.com</Origin>.Cautionary Statement Regarding Forward-Looking StatementsThis communication contains forward-looking statements. Forward-lookingstatements relate to future events or Covis' future financial performance.Covis generally identifies forward-looking statements by terminology such as\"may,\" \"will,\" \"should,\" \"expects,\" \"plans,\" \"anticipates,\" \"could,\"\"intends,\" \"target,\" \"projects,\" \"contemplates,\" \"believes,\" \"estimates,\"\"predicts,\" \"potential\" or \"continue\" or the negative of these terms or othersimilar words. These statements are only predictions. Covis has based theseforward-looking statements largely on its then-current expectations andprojections about future events and financial trends as well as the beliefsand assumptions of management. Forward-looking statements are subject to anumber of risks and uncertainties, many of which involve factors orcircumstances that are beyond Covis' control. Covis' actual results coulddiffer materially from those stated or implied in forward-looking statementsdue to a number of factors, including but not limited to: risks associatedwith the combined organization following completion of the merger transactionincluding difficulties in executing Covis' strategic vision, continuing togrow Covis' therapeutic portfolio and enhancing Covis' capabilities andofferings; the outcome of any legal proceedings that may be instituted againstthe parties and others related to the merger transaction; the inability ofCovis' to impact the lives of patients by expanding its portfolio of 'best inclass' products in attractive new therapeutic areas; Covis not beingpositioned to further support patients, and failing to efficiently andeffectively manage therapeutic solutions; Covis not being able to fulfill itsstrategic vision of becoming a leading specialty pharma company globally forlife threatening and chronic illnesses for both commercial and developmentstage assets, which could have a material adverse impact on Covis' business,financial results and operations; Covis failing to collaborate with the FDA toensure that Makena continues to be available as an option for appropriatepatients; risks associated with Makena's withdrawal, including the impacts onCovis' financial results; Covis failing to grow its therapeutic portfolioglobally and enhancing its capabilities and offerings; Covis not being able toexecute on its integration plans, resulting in a failure to achieve synergiesor a more unified, stronger organization; and unanticipated difficulties orexpenditures relating to the merger transaction, the response of businesspartners and competitors to the completion of the merger transaction, and/orpotential difficulties in employee retention as a result of the completion ofthe merger transaction. Accordingly, you should not rely upon forward-lookingstatements as predictions of future events. Covis cannot assure you that theevents and circumstances reflected in the forward-looking statements will beachieved or occur, and actual results could differ materially from thoseprojected in the forward-looking statements. The forward-looking statementsmade in this communication relate only to events as of the date on which thestatements are made. Covis undertakes no obligation to update anyforward-looking statement to reflect events or circumstances after the date onwhich the statement is made or to reflect the occurrence of unanticipatedevents.ContactArik Ben-ZviBreakwater Strategy(202) 270-1848arik@breakwaterstrategy.comWebsite: <Origin Href=\\\"Link\\\">https://www.covispharma.com</Origin>Copyright (c) 2020 PR Newswire Association,LLC. All Rights Reserved.</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201116:nPn2rN98Ta")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T14:20:39-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Covis Group Completes Acquisition of AMAG Pharmaceuticals");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201116:nPn2rN98Ta</Origin>Covis Group Completes Acquisition of AMAG PharmaceuticalsPR NewswireLUXEMBOURG and ZUG, Switzerland, Nov. 16, 2020LUXEMBOURG and ZUG, Switzerland, Nov. 16, 2020 /PRNewswire/ -- Covis GroupS.r.l. (\"Covis\") today announced the completion of its acquisition of AMAGPharmaceuticals, Inc. (NASDAQ: AMAG) through the successful tender offer forall of the outstanding shares of common stock of AMAG at $13.75 per share incash and subsequent merger. The combined organization will operate as part ofthe Covis Pharma Group and will be led by Covis CEO Michael Porter.The addition of AMAG's category leading treatments and development-stageassets in women's health and hematology/oncology supports the execution ofCovis' strategic vision to enhance the company's ability to impact the livesof patients by expanding its portfolio of 'best in class' products inattractive new therapeutic areas. In addition, Covis will be positioned tofurther support patients, building on its track record of efficient andeffective management of therapeutic solutions.\"The acquisition of AMAG represents a key milestone in Covis' efforts tofulfill our strategic vision to become a leading global specialty pharmacompany for life threatening and chronic illnesses for both commercial anddevelopment stage assets,\" said Michael Porter, CEO of Covis. \"AMAG'sexpertise and key productsMakenaand Ciraparantagvalue to Covis as we execute our mandate of expanding patientaccess to much needed therapies. We will continue to put patients' interestsfirst and look forward to collaborating with the FDA to ensure that Makenacontinues to be available as an option for appropriate patients. More thanever, we are positioned to continue growth of our therapeutic portfolioglobally and look forward to further enhancing our capabilities andofferings.  We're excited to work with our designated transition teams toexecute on our integration plans and move forward as a stronger, unifiedorganization.\"The tender offer expired at 12:00 a.m., Eastern Time, on November 12, 2020(one minute after 11:59 p.m., Eastern Time, on November 12, 2020).  Thecondition to the tender offer that a majority of the outstanding shares ofAMAG's common stock be validly tendered and not withdrawn was satisfied and,accordingly, all such validly tendered shares were accepted for payment. Following the consummation of the tender offer, Covis Mergerco Inc. mergedwith and into AMAG pursuant to Section 251(h) of the General Corporation Lawof the State of Delaware. As a result of the merger, each share of AMAG thatwas not validly tendered in the tender offer (other than shares held by anystockholder of AMAG who properly demanded appraisal of such shares under theapplicable provisions of Delaware law) was cancelled and converted into theright to receive $13.75 per share in cash, and AMAG became an indirect whollyowned subsidiary of Covis. Shares of AMAG will cease to be traded on NASDAQ.Goldman Sachs & Co. LLC acted as exclusive financial advisor, and GoodwinProcter LLP acted as legal advisor to AMAG.  Paul, Weiss, Rifkind, Whartonand Garrison LLP acted as legal advisor to Covis. Financing for thistransaction has been provided by Capital One, N.A. and investment funds andaccounts managed by HPS Investment Partners, LLC.About CovisCovis is headquartered in Luxembourg with operations in Zug, Switzerland andis a global specialty pharmaceutical company that markets therapeuticsolutions for patients with life-threatening conditions and chronic illnesses. Additional information is available at <Origin Href=\\\"Link\\\">www.covispharma.com</Origin>(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&l=en&o=2982257-1&h=1473285475&u=https%3A%2F%2Fc212.net%2Fc%2Flink%2F%3Ft%3D0%26l%3Den%26o%3D2804947-1%26h%3D2975190818%26u%3Dhttp%253A%252F%252Fcts.businesswire.com%252Fct%252FCT%253Fid%253Dsmartlink%2526url%253Dhttp%25253A%25252F%25252Fcovispharma.com%25252F%2526esheet%253D51588368%2526newsitemid%253D20170713005470%2526lan%253Den-US%2526anchor%253Dcovispharma.com%2526index%253D2%2526md5%253Da5c5b8aaa851291e6e6f6123e2f6d4e2%26a%3Dwww.covispharma.com&a=www.covispharma.com</Origin>).Cautionary Statement Regarding Forward-Looking StatementsThis communication contains forward-looking statements. Forward-lookingstatements relate to future events or Covis' future financial performance.Covis generally identifies forward-looking statements by terminology such as\"may,\" \"will,\" \"should,\" \"expects,\" \"plans,\" \"anticipates,\" \"could,\"\"intends,\" \"target,\" \"projects,\" \"contemplates,\" \"believes,\" \"estimates,\"\"predicts,\" \"potential\" or \"continue\" or the negative of these terms or othersimilar words. These statements are only predictions. Covis has based theseforward-looking statements largely on its then-current expectations andprojections about future events and financial trends as well as the beliefsand assumptions of management. Forward-looking statements are subject to anumber of risks and uncertainties, many of which involve factors orcircumstances that are beyond Covis' control. Covis' actual results coulddiffer materially from those stated or implied in forward-looking statementsdue to a number of factors, including but not limited to: risks associatedwith the combined organization following completion of the merger transactionincluding difficulties in executing Covis' strategic vision, continuing togrow Covis' therapeutic portfolio and enhancing Covis' capabilities andofferings; the outcome of any legal proceedings that may be instituted againstthe parties and others related to the merger transaction; the inability ofCovis' to impact the lives of patients by expanding its portfolio of 'best inclass' products in attractive new therapeutic areas; Covis not beingpositioned to further support patients, and failing to efficiently andeffectively manage therapeutic solutions; Covis not being able to fulfill itsstrategic vision of becoming a leading specialty pharma company globally forlife threatening and chronic illnesses for both commercial and developmentstage assets, which could have a material adverse impact on Covis' business,financial results and operations; Covis failing to collaborate with the FDA toensure that Makena continues to be available as an option for appropriatepatients; risks associated with Makena's withdrawal, including the impacts onCovis' financial results; Covis failing to grow its therapeutic portfolioglobally and enhancing its capabilities and offerings; Covis not being able toexecute on its integration plans, resulting in a failure to achieve synergiesor a more unified, stronger organization; and unanticipated difficulties orexpenditures relating to the merger transaction, the response of businesspartners and competitors to the completion of the merger transaction, and/orpotential difficulties in employee retention as a result of the completion ofthe merger transaction. Accordingly, you should not rely upon forward-lookingstatements as predictions of future events. Covis cannot assure you that theevents and circumstances reflected in the forward-looking statements will beachieved or occur, and actual results could differ materially from thoseprojected in the forward-looking statements. The forward-looking statementsmade in this communication relate only to events as of the date on which thestatements are made. Covis undertakes no obligation to update anyforward-looking statement to reflect events or circumstances after the date onwhich the statement is made or to reflect the occurrence of unanticipatedevents.ContactArik Ben-ZviBreakwater Strategy(202) 270-1848arik@breakwaterstrategy.com (mailto:arik@breakwaterstrategy.com)View originalcontent:<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/covis-group-completes-acquisition-of-amag-pharmaceuticals-301173667.html</Origin>(<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/covis-group-completes-acquisition-of-amag-pharmaceuticals-301173667.html</Origin>)SOURCE Covis PharmaWebsite: <Origin Href=\\\"Link\\\">https://www.covispharma.com</Origin>Copyright (c) 2020 PR Newswire Association,LLC. All Rights Reserved.</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201116:nCNWxYhf4a")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T14:20:38-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Covis Group Completes Acquisition of AMAG Pharmaceuticals");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201116:nCNWxYhf4a</Origin>Covis Group Completes Acquisition of AMAG PharmaceuticalsCanada NewsWireLUXEMBOURG and ZUG, Switzerland, Nov. 16, 2020LUXEMBOURG and ZUG, Switzerland, Nov. 16, 2020 /CNW/ -- Covis Group S.r.l.(\"Covis\") today announced the completion of its acquisition of AMAGPharmaceuticals, Inc. (NASDAQ: AMAG) through the successful tender offer forall of the outstanding shares of common stock of AMAG at $13.75 per share incash and subsequent merger. The combined organization will operate as part ofthe Covis Pharma Group and will be led by Covis CEO Michael Porter.The addition of AMAG's category leading treatments and development-stageassets in women's health and hematology/oncology supports the execution ofCovis' strategic vision to enhance the company's ability to impact the livesof patients by expanding its portfolio of 'best in class' products inattractive new therapeutic areas. In addition, Covis will be positioned tofurther support patients, building on its track record of efficient andeffective management of therapeutic solutions.\"The acquisition of AMAG represents a key milestone in Covis' efforts tofulfill our strategic vision to become a leading global specialty pharmacompany for life threatening and chronic illnesses for both commercial anddevelopment stage assets,\" said Michael Porter, CEO of Covis. \"AMAG'sexpertise and key productsMakenaand Ciraparantagvalue to Covis as we execute our mandate of expanding patientaccess to much needed therapies. We will continue to put patients' interestsfirst and look forward to collaborating with the FDA to ensure that Makenacontinues to be available as an option for appropriate patients. More thanever, we are positioned to continue growth of our therapeutic portfolioglobally and look forward to further enhancing our capabilities andofferings.  We're excited to work with our designated transition teams toexecute on our integration plans and move forward as a stronger, unifiedorganization.\"The tender offer expired at 12:00 a.m., Eastern Time, on November 12, 2020(one minute after 11:59 p.m., Eastern Time, on November 12, 2020).  Thecondition to the tender offer that a majority of the outstanding shares ofAMAG's common stock be validly tendered and not withdrawn was satisfied and,accordingly, all such validly tendered shares were accepted for payment. Following the consummation of the tender offer, Covis Mergerco Inc. mergedwith and into AMAG pursuant to Section 251(h) of the General Corporation Lawof the State of Delaware. As a result of the merger, each share of AMAG thatwas not validly tendered in the tender offer (other than shares held by anystockholder of AMAG who properly demanded appraisal of such shares under theapplicable provisions of Delaware law) was cancelled and converted into theright to receive $13.75 per share in cash, and AMAG became an indirect whollyowned subsidiary of Covis. Shares of AMAG will cease to be traded on NASDAQ.Goldman Sachs & Co. LLC acted as exclusive financial advisor, and GoodwinProcter LLP acted as legal advisor to AMAG.  Paul, Weiss, Rifkind, Whartonand Garrison LLP acted as legal advisor to Covis. Financing for thistransaction has been provided by Capital One, N.A. and investment funds andaccounts managed by HPS Investment Partners, LLC.About CovisCovis is headquartered in Luxembourg with operations in Zug, Switzerland andis a global specialty pharmaceutical company that markets therapeuticsolutions for patients with life-threatening conditions and chronic illnesses. Additional information is available at <Origin Href=\\\"Link\\\">www.covispharma.com</Origin>.Cautionary Statement Regarding Forward-Looking StatementsThis communication contains forward-looking statements. Forward-lookingstatements relate to future events or Covis' future financial performance.Covis generally identifies forward-looking statements by terminology such as\"may,\" \"will,\" \"should,\" \"expects,\" \"plans,\" \"anticipates,\" \"could,\"\"intends,\" \"target,\" \"projects,\" \"contemplates,\" \"believes,\" \"estimates,\"\"predicts,\" \"potential\" or \"continue\" or the negative of these terms or othersimilar words. These statements are only predictions. Covis has based theseforward-looking statements largely on its then-current expectations andprojections about future events and financial trends as well as the beliefsand assumptions of management. Forward-looking statements are subject to anumber of risks and uncertainties, many of which involve factors orcircumstances that are beyond Covis' control. Covis' actual results coulddiffer materially from those stated or implied in forward-looking statementsdue to a number of factors, including but not limited to: risks associatedwith the combined organization following completion of the merger transactionincluding difficulties in executing Covis' strategic vision, continuing togrow Covis' therapeutic portfolio and enhancing Covis' capabilities andofferings; the outcome of any legal proceedings that may be instituted againstthe parties and others related to the merger transaction; the inability ofCovis' to impact the lives of patients by expanding its portfolio of 'best inclass' products in attractive new therapeutic areas; Covis not beingpositioned to further support patients, and failing to efficiently andeffectively manage therapeutic solutions; Covis not being able to fulfill itsstrategic vision of becoming a leading specialty pharma company globally forlife threatening and chronic illnesses for both commercial and developmentstage assets, which could have a material adverse impact on Covis' business,financial results and operations; Covis failing to collaborate with the FDA toensure that Makena continues to be available as an option for appropriatepatients; risks associated with Makena's withdrawal, including the impacts onCovis' financial results; Covis failing to grow its therapeutic portfolioglobally and enhancing its capabilities and offerings; Covis not being able toexecute on its integration plans, resulting in a failure to achieve synergiesor a more unified, stronger organization; and unanticipated difficulties orexpenditures relating to the merger transaction, the response of businesspartners and competitors to the completion of the merger transaction, and/orpotential difficulties in employee retention as a result of the completion ofthe merger transaction. Accordingly, you should not rely upon forward-lookingstatements as predictions of future events. Covis cannot assure you that theevents and circumstances reflected in the forward-looking statements will beachieved or occur, and actual results could differ materially from thoseprojected in the forward-looking statements. The forward-looking statementsmade in this communication relate only to events as of the date on which thestatements are made. Covis undertakes no obligation to update anyforward-looking statement to reflect events or circumstances after the date onwhich the statement is made or to reflect the occurrence of unanticipatedevents.ContactArik Ben-ZviBreakwater Strategy(202) 270-1848arik@breakwaterstrategy.comView originalcontent:<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/covis-group-completes-acquisition-of-amag-pharmaceuticals-301173667.html</Origin>SOURCE Covis PharmaView original content:<Origin Href=\\\"Link\\\">http://www.newswire.ca/en/releases/archive/November2020/16/c9694.html</Origin></p>");
		} else if (storyId
				.equalsIgnoreCase("urn:newsml:onlinereport.com:20201117:nRTROPT20201117123049KBN27X1IB-OCABS")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-17T12:30:49-00:00");
			hm1.put("PR", "onlinereport.com");
			hm1.put("HT", "TSX opens lower as energy stocks drag");
			hm1.put("TE",
					"<p>(Reuters) - Canada's main stock index opened lower on Tuesday, weighed down by energy stocks as oil prices slumped on tightening coronavirus-driven restrictions across the world.</p><p>* At 9:30 a.m. ET (14:30 GMT), the Toronto Stock Exchange's S&P/TSX composite index was down 103.39 points, or 0.61%, at 16,786.42.</p><p></p><p></p><p> (Reporting by Amal S in Bengaluru; Editing by Sriraj Kalluvila)</p><Origin Href=\\\"ThumbnailRef\\\">2020-11-17T123049Z_1_LYNXMPEGAG0UD-OCABS_RTROPTP_1_CBUSINESS-US-CANADA-STOCKS.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117123049LYNXMPEGAG0UD-OCABS</Origin> <Origin Href=\\\"ImageTitle\\\">The facade of the original Toronto Stock Exchange building is seen in Toronto</Origin> <Origin Href=\\\"ChannelCode\\\">OLCABUS</Origin> <Origin Href=\\\"LinkTitle\\\">Reuters Canada Online Report Business News</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201117T123049+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201117T123049+0000</Origin>");
		} else if (storyId
				.equalsIgnoreCase("urn:newsml:onlinereport.com:20201117:nRTROPT20201117123049KBN27X1IB-OCATP")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-17T12:30:49-00:00");
			hm1.put("PR", "onlinereport.com");
			hm1.put("HT", "TSX opens lower as energy stocks drag");
			hm1.put("TE",
					"<p>(Reuters) - Canada's main stock index opened lower on Tuesday, weighed down by energy stocks as oil prices slumped on tightening coronavirus-driven restrictions across the world.</p><p>* At 9:30 a.m. ET (14:30 GMT), the Toronto Stock Exchange's S&P/TSX composite index was down 103.39 points, or 0.61%, at 16,786.42.</p><p></p><p></p><p> (Reporting by Amal S in Bengaluru; Editing by Sriraj Kalluvila)</p><Origin Href=\\\"ThumbnailRef\\\">2020-11-17T123049Z_1_LYNXMPEGAG0UD-OCATP_RTROPTP_1_CNEWS-US-CANADA-STOCKS.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201117:nRTROPT20201117123049LYNXMPEGAG0UD-OCATP</Origin> <Origin Href=\\\"ImageTitle\\\">The facade of the original Toronto Stock Exchange building is seen in Toronto</Origin> <Origin Href=\\\"ChannelCode\\\">OLCANAT</Origin> <Origin Href=\\\"LinkTitle\\\">Reuters Canada Online Report Top News</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201117T123049+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201117T123049+0000</Origin>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201117:nL4N2I32EG")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-17T12:27:47-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "CANADA STOCKS-TSX futures down as weaker oil, rising virus cases weigh");
			hm1.put("TE",
					"<p>    Nov 17 (Reuters) - Canadian stock futures fell on Tuesday,dragged lower by weaker crude prices and concerns around risingglobal cases of the novel coronavirus, with investors nowawaiting domestic wholesale trade data for September.    Brent crude  <Origin Href=\\\"QuoteRef\\\">LCOc1</Origin>  futures fell 0.37%, while U.S. WestTexas Intermediate (WTI) crude futures  <Origin Href=\\\"QuoteRef\\\">CLc1</Origin>  shed 0.27%, as oilprices remained under pressure after many Western governmentsimposed new lockdowns to curb the spread of COVID-19.    December futures on the S&P/TSX index  <Origin Href=\\\"QuoteRef\\\">SXFc1</Origin>  were down0.46% at 7:00 a.m. ET.    Canadian wholesale trade data for September is due at 8:30a.m. ET (1230 GMT).    The Toronto Stock Exchange's S&P/TSX composite index <Origin Href=\\\"QuoteRef\\\">.GSPTSE</Origin>  ended 1.28% higher at 16,889.8 on Monday.     Dow Jones Industrial Average e-mini futures  <Origin Href=\\\"QuoteRef\\\">1YMc1</Origin>  weredown 0.53% at 7:00 a.m. ET, while S&P 500 e-mini futures  <Origin Href=\\\"QuoteRef\\\">ESc1</Origin> were down 0.45% and Nasdaq 100 e-mini futures NQc1 were up 0.3%.       ANALYST RESEARCH HIGHLIGHTS  <Origin Href=\\\"NewsSearch\\\">RCH/CA</Origin>     Aurora Cannabis  <Origin Href=\\\"QuoteRef\\\">ACB.TO</Origin> : ATB Capital Markets raises to\"sector perform\" from \"underperform\"    Champion Iron Ltd  <Origin Href=\\\"QuoteRef\\\">CIA.TO</Origin> : Laurentian Bank Securitiesraises PT to C$5.35 from C$5    Exchange Income Corp  <Origin Href=\\\"QuoteRef\\\">EIF.TO</Origin> : Scotiabank raises targetprice to C$38 from C$32        COMMODITIES AT 7:00 a.m. ET     Gold futures  <Origin Href=\\\"QuoteRef\\\">GCc2</Origin> : $1886.9; -0.05%  <Origin Href=\\\"NewsSearch\\\">GOL/</Origin>     US crude  <Origin Href=\\\"QuoteRef\\\">CLc1</Origin> : $41.23; -0.27%  <Origin Href=\\\"NewsSearch\\\">O/R</Origin>     Brent crude  <Origin Href=\\\"QuoteRef\\\">LCOc1</Origin> : $43.66; -0.37%  <Origin Href=\\\"NewsSearch\\\">O/R</Origin>         U.S. ECONOMIC DATA DUE ON TUESDAY    0830 Import prices mm for Oct: Expected 0.2%; Prior 0.3%    0830 Export prices mm for Oct: Expected 0.3%; Prior 0.6%    0830 Import prices yy for Oct: Prior -1.1%    0830 Retail sales ex-autos mm for Oct: Expected 0.6%; Prior1.5%    0830 Retail sales mm for Oct: Expected 0.5%; Prior 1.9%    0830 Retail ex gas/autos for Oct: Prior 1.5%    0830 Retail control for Oct: Expected 0.5%; Prior 1.4%    0830 Retail sales YoY for Oct: Prior 5.36%    0915 Industrial production mm for Oct: Expected 1.0%; Prior-0.6%    0915 Capacity utilization SA for Oct: Expected 72.3%; Prior71.5%    0915 Manufacturing output mm for Oct: Expected 1.0%; Prior-0.3%    0915 Industrial production YoY for Oct: Prior -7.28%    1000 Business inventories mm for Sep: Expected 0.6%; Prior0.3%    1000 Retail inventories ex-auto rev for Sep: Prior 0.9%    1000 NAHB Housing Market Index for Nov: Expected 85; Prior85        FOR CANADIAN MARKETS NEWS, CLICK ON CODES:    TSX market report  <Origin Href=\\\"NewsSearch\\\">.TO</Origin>     Canadian dollar and bonds report  <Origin Href=\\\"NewsSearch\\\">CAD/</Origin>   <Origin Href=\\\"NewsSearch\\\">CA/</Origin>     Reuters global stocks poll for Canada  <Origin Href=\\\"QuoteRef\\\">EQUITYPOLL1</Origin>  <Origin Href=\\\"QuoteRef\\\">EPOLL/CA</Origin>     Canadian markets directory  <Origin Href=\\\"QuoteRef\\\">CANADA</Origin>  ($1= C$1.31) (Reporting by Amal S in Bengaluru; editing by Uttaresh.V) ((Amal.S@thomsonreuters.com; within U.S.+1 646 223 8780;outside U.S. +91 80 6749 3677;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201116:nZaw3q4PRR")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T21:01:36-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "SNG: 24 women appointed to Saudi Shoura committees");
			hm1.put("TE",
					"<p>RIYADH For the first time in the history of the Saudi Shoura Council, alarge number of women members have been picked to serve in its variouscommittees. There are 24 women in 14 Shoura committees, which were hithertomonopolized by their male counterparts.The security affairs committee includes a woman doctor and seven formersecurity officials, most of them are major generals.Each Shoura committee consists of nine members besides the chairman and deputychairman and each member participates in one specialized committee based onhis/her experience, and its duration is one year. It is permissible tore-nominate chairman and deputy chairman for one time only and they areelected by secret ballot.The committees shall study whatever subjects are referred to them and submittheir reports and recommendations for deliberations in the council.Dr. Zainab Bint Muthanna Abu Talib was chosen as the chairperson of the healthcommittee and Dr. Saleh Al-Shuhayeb is deputy chairman. Women occupy half ofthe committeemembership, and they are Dr. Ameera Al-Balawi, Dr. AliaAl-Dahlawi, Dr. Mona Al-Mushayt, and Dr. Najwa Al-Ghamdi. This is inrecognition of the vast experiences of the women members have in the healthand administrative fields.Dr. Maha Al-Sinan has been chosen deputy chairperson of the culture, media,and tourism and antiquities committee. There are three women members in thecommittee Dr. Iman Al-Jabreen, Dr. Haifa Al-Shammari, and Mona AbidKhazandar. Dr. Amal Al-Shaman was elected deputy chairperson of the educationand scientific research committee while Dr. Aisha Zakri will serve as amember.Kawthar Al-Arbash, a leading figure, has been chosen as the deputy chairpersonof the human rights committee and Dr. Latifa Al-Shaalan, who has extensiveparliamentary experience, is a member. It is expected that the files of womenand children will get extra care and keenness under Al-Arbash and Al-Shaalan.Likewise, Dr. Samia Bukhari is the new deputy chairperson of the humanresources and administration committee. Dr. Ameera Al-Jaafari, Dr. SultanAl-Badawi, and Dr. Mona Al-Fadhli are the members of the committee, which istasked with handling employment files and address the pending issues of womenand their employment concerns.Princess Al-Jawhara Bint Fahd Al-Saud has been chosen as the deputychairperson of the committee of social affairs, family, and youth, and thewomen members of the committee are Dr. Amal Al-Sheikh, Somayya Jabarti, andDr. Reema Saleh Al-Yahya. The committee is tasked with addressing urgent filesof Saudi families and youth.Dr. Latifa Al-Abdul Karim, Dr. Asma Al-Muwaisheer, Huda Al-Holaisi, and AyishaOraishi are the women members picked to represent committees for transport andcommunications, Hajj and services, foreign affairs, water, and agriculture.These are committees in which men still command the majority.Dr. Mastoura Al-Shammari is the woman member of the security affairscommittee, which is headed by Maj. Gen. Ali Al-Asiri. The committee membersinclude Maj. Gen. Mansour Al-Turki, former spokesman of the Ministry ofInterior, and five other brigadiers and major generals.Dr. Iman Al-Zahrani and Hanan Al-Samari are women members of the economics andenergy committee while Raeda Abu Nayan was will be a member of the financecommittee. Copyright 2020 The Saudi Gazette. All Rights Reserved. Provided bySyndiGate Media Inc. (Syndigate.info (<Origin Href=\\\"Link\\\">http://syndigate.info</Origin>)).Disclaimer: The content of this article is syndicated or provided to thiswebsite from an external third party provider. We are not responsible for, anddo not control, such external websites, entities, applications or mediapublishers. The body of the text is provided on an isand basis and has not been edited in any way. Neither we nor ouraffiliates guarantee the accuracy of or endorse the views or opinionsexpressed in this article. Read our full disclaimer policy here(<Origin Href=\\\"Link\\\">https://d3a0oa8cittb3t.cloudfront.net/researchreports/Disclaimers/TR%20Disclaimer.pdf</Origin>).Saudi Gazette</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201116:nCNWyxhx6a")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-16T11:30:02-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"VASCEPA(Icosapent Ethyl) Found to Significantly Reduce Ischemic Events in Patients with Prior Coronary Artery Bypass Grafting Procedures");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201116:nCNWyxhx6a</Origin>VASCEPA(Icosapent Ethyl) Found to Significantly Reduce Ischemic Events inPatients with Prior Coronary Artery Bypass Grafting ProceduresCanada NewsWireTORONTO, Nov. 16, 2020* Results from Post Hoc Subgroup Analyses of Landmark REDUCE-ITStudyPresented at American Heart Association's Virtual Scientific Sessions 2020 * VASCEPAcompared with placebo, significantly reduced primary compositefirst and total MACE (major adverse cardiovascular events) in post hocexploratory analyses of patients with a history of Coronary Artery BypassGrafting (commonly referred to as Bypass Surgery) by 24% and 36%,respectively, and key secondary composite first hard MACE, comprised of heartattacks, stroke and cardiovascular death, by 31% * The use of VASCEPA resulted in absolute risk reductions of MACE equal to, orgreater than, 6.0% in these subgroup analyses, avoiding on average one cardiacevent for every 17 patients treatedTORONTO, Nov. 16, 2020 /CNW/ - HLS Therapeutics Inc. (\"HLS\" or the \"Company\")(TSX: HLS), a specialty pharmaceutical company focusing on central nervoussystem and cardiovascular markets, reported today on the presentation ofREDUCE-ITCoronary Artery Bypass Grafting (\"CABG\") analysis at the AmericanHeart Association's (\"AHA\") Virtual Scientific Sessions 2020, being heldvirtually from November 13-17, 2020, adding to the growing body of knowledgeon the clinical impact of VASCEPA(icosapent ethyl). These new analyses werepresented by Subodh Verma, M.D., Ph.D., a cardiac surgeon and Professor at theUniversity of Toronto, Toronto, Ontario, Canada.Dr. Subodh Verma, who was the lead author of this analysis, commented: \"Theanalyses from REDUCE-IT indicate that VASCEPA (icosapent ethyl) reduces theneed for CABG (commonly referred to as bypass surgery) by about 40%. Furthermore, in patients who have undergone CABG surgery this therapyprofoundly reduces subsequent major cardiovascular events of cardiovasculardeath, heart attack or stroke by 31%. These data have important implicationsfor our patients and the health care system.\" \"The REDUCE-IT CABG analysis results are another piece of the puzzle whenlooking at the potential use of icosapent ethyl in the procedural setting,\"commented Dr. Deepak L. Bhatt, M.D., M.P.H., Executive Director ofInterventional Cardiovascular Programs at Brigham and Women's Hospital andProfessor of Medicine at Harvard Medical School, principal investigator ofREDUCE-IT. \"The findings of benefit in at-risk patients with prior CABG areconsistent with previously presented data on overall reductions in first andtotal coronary revascularization events, as well as in patients with priorpercutaneous coronary interventions, and further strengthen the case forconsideration of icosapent ethyl as an additional intervention for use byphysicians to care for this patient population.\"The REDUCE-IT CABG analysis looked at 1,837 (22.5%) of the patients enrolledin REDUCE-IT(1), representing all patients who had undergone a prior CABGprocedure, a common form of surgical intervention to help treat coronary heartdisease. Baseline characteristics were similar among patients randomized toVASCEPA versus placebo. Post hoc exploratory analyses of this subgroup showedthat, for the composite endpoint of 5-point MACE, which was the prespecifiedprimary endpoint for the full REDUCE-IT study cohort, time to first event forVascepa was significantly better than placebo by 24% (p=0.004) and total(first and subsequent) events were also better by 36% (p=0.0002). For theREDUCE-IT study's key secondary composite endpoint of 3-point MACE, time tofirst event was better than placebo by 31% (p=0.001) in the subgroup ofpatients with a prior CABG.Coronary revascularization procedures, such as CABG, are invasive, carrymultiple risks, and can have significant direct and indirect costs. Patientswith elevated triglycerides despite statin therapy have increased risk forischemic events, including coronary revascularizations. These procedures,whether pre-scheduled or performed in an emergency, inevitably result inadditional time spent in a healthcare setting.REDUCE-IT was not specifically powered to examine individual cardiovascularendpoints or patient subgroups, therefore these revascularization analyses arenominal and exploratory with no adjustment for multiple comparisons. Inaddition, coronary revascularization as an endpoint can sometimes beconsidered subjective; however, these endpoints were adjudicated by anindependent, blinded clinical endpoint committee. Results from the totalcoronary revascularization events analyses are consistent across the variousrecurrent event statistical models and are also consistent with the firstcoronary revascularization events results. Together, the REDUCE-IT first andtotal coronary revascularization events results support the robustness andconsistency of the clinical benefit of VASCEPA therapy in reducing coronaryrevascularization.These REDUCE-IT CABG results follow multiple scientific presentations ofanalysis results from other important patient subgroups in the REDUCE-ITstudy, including REDUCE-IT REVASC(2) and REDUCE-IT PCI.Additional information on AHA Virtual Scientific Sessions 2020 can be foundhere(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&l=en&o=2982340-1&h=2498562933&u=https%3A%2F%2Fprofessional.heart.org%2Fen%2Fmeetings%2Fscientific-sessions%3Fgclid%3DCj0KCQjw59n8BRD2ARIsAAmgPmKBl9pcsDUFzTCTbXfEaHinmdSDakjEzoH2Oam2b-UvdDp0cywarEMaAnxdEALw_wcB&a=here</Origin>). ABOUT CARDIOVASCULAR DISEASEWorldwide, cardiovascular disease (CVD) remains the #1 cause of mortality ofmen and women.Multiple primary and secondary prevention trials have shown a significantreduction of 25% to 35% in the risk of cardiovascular events with statintherapy, leaving significant persistent residual risk despite the achievementof target LDL-C levels.(3)Beyond the cardiovascular risk associated with LDL-C, genetic, epidemiologic,clinical and real-world data suggest that patients with elevated triglycerides(TG) (fats in the blood), and TG-rich lipoproteins, are at increased risk forcardiovascular disease.(4, 5, 6, 7  )ABOUT VASCEPA (ICOSAPENT ETHYL) CAPSULESVASCEPA (icosapent ethyl) capsules are the first-and-only prescriptiontreatment comprised solely of the active ingredient, icosapent ethyl (IPE), aunique form of eicosapentaenoic acid. Vascepa was approved by Health Canada,was added to Health Canada's Register of Innovative Drugs and benefits fromdata protection for a term of eight years, as well as being the subject ofmultiple issued and pending patents based on its unique clinical profile. HLSin-licensed the exclusive rights to Vascepa for the Canadian market fromAmarin Corporation (NASDAQ:AMRN).ABOUT HLS THERAPEUTICS INC.Formed in 2015, HLS is a specialty pharmaceutical company focused on theacquisition and commercialization of late stage development, commercial stagepromoted and established branded pharmaceutical products in the North Americanmarkets. HLS's focus is on products targeting the central nervous system andcardiovascular therapeutic areas. HLS's management team is composed ofseasoned pharmaceutical executives with a strong track record of success inthese therapeutic areas and at managing products in each of these lifecyclestages. For more information visit: <Origin Href=\\\"Link\\\">www.hlstherapeutics.com</Origin>REFERENCES 1:  Bhatt DL, Steg PG, Miller M, et al. Cardiovascular Risk Reduction with Icosapent Ethyl for Hypertriglyceridemia. N Engl J Med. 2019;380(1):11doi:10.1056/NEJMoa1812792.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              2:  Peterson BE, Bhatt DL, Steg PG, Miller M, Brinton EA, Jacobson TA, Ketchum SB, Juliano RA, Jiao L, Doyle RT Jr, Granowitz C, Gibson CM, Pinto D, Giugliano RP, Budoff MJ, Tardif JC, Verma S, Ballantyne CM; REDUCE-IT Investigators. Reduction in Revascularization with Icosapent Ethyl: Insights from REDUCE-IT REVASC. Circulation. 2020 Nov 5. doi: 10.1161/CIRCULATIONAHA.120.050276. Epub ahead of print. PMID: 33148016.                                                                                                                                                                                                                                                                                                                                                                                                                                          3:  Ganda OP, Bhatt DL, Mason RP, et al. Unmet need for adjunctive dyslipidemia therapy in hypertriglyceridemia management. J Am Coll Cardiol. 2018;72(3):330-343.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            4:  Budoff M. Triglycerides and triglyceride-rich lipoproteins in the causal pathway of cardiovascular disease. Am J Cardiol. 2016;118:138-145.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               5:  Toth PP, Granowitz C, Hull M, et al. High triglycerides are associated with increased cardiovascular events, medical costs, and resource use: A real-world administrative claims analysis of statin-treated patients with high residual cardiovascular risk. J Am Heart Assoc. 2018;7(15):e008740.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        6:  Nordestgaard BG. Triglyceride-rich lipoproteins and atherosclerotic cardiovascular disease - New insights from epidemiology, genetics, and biology. Circ Res. 2016;118:547-563.                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           7:  Nordestgaard BG, Varbo A. Triglycerides and cardiovascular disease. Lancet. 2014;384:626                                                                                                                                                                                                                                                                                                                                    FORWARD LOOKING INFORMATIONThis release includes forward-looking statements regarding HLS and itsbusiness. Such statements are based on the current expectations and views offuture events of HLS's management. In some cases the forward-lookingstatements can be identified by words or phrases such as \"may\", \"will\",\"expect\", \"plan\", \"anticipate\", \"intend\", \"potential\", \"estimate\", \"believe\"or the negative of these terms, or other similar expressions intended toidentify forward-looking statements, including, among others, statements withrespect to HLS's pursuit of additional product and pipeline opportunities incertain therapeutic markets, statements regarding growth opportunities andexpectations regarding financial performance. The forward-looking events andcircumstances discussed in this release may not occur and could differmaterially as a result of known and unknown risk factors and uncertaintiesaffecting HLS, including risks relating to the specialty pharmaceuticalindustry, risks related to the regulatory approval process, economic factorsand many other factors beyond the control of HLS. Forward-looking statementsand information by their nature are based on assumptions and involve known andunknown risks, uncertainties and other factors which may cause HLS's actualresults, performance or achievements, or industry results, to be materiallydifferent from any future results, performance or achievements expressed orimplied by such forward-looking statement or information. Accordingly, readersshould not place undue reliance on any forward-looking statements orinformation. A discussion of the material risks and assumptions associatedwith this release can be found in the Company's Annual Information Form datedMarch 18, 2020 and Management's Discussion and Analysis dated November 4,2020, both of which have been filed on SEDAR and can be accessed at<Origin Href=\\\"Link\\\">www.sedar.com</Origin>. Accordingly, readers should not place undue reliance on anyforward-looking statements or information. Except as required by applicablesecurities laws, forward-looking statements speak only as of the date on whichthey are made and HLS undertakes no obligation to publicly update or reviseany forward-looking statement, whether as a result of new information, futureevents, or otherwise.SOURCE HLS Therapeutics Inc.View original content:<Origin Href=\\\"Link\\\">http://www.newswire.ca/en/releases/archive/November2020/16/c4115.html</Origin></p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201113:nGNX1CLGsL")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-13T15:00:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"VASCEPA(Icosapent Ethyl) Found to Significantly Reduce Ischemic Events in Patients with Prior Coronary Artery Bypass Grafting (CABG) Procedures in Post Hoc Subgroup Analyses of Landmark REDUCE-ITStudy Presented at American Heart AssociationVirtual Scientific Sessions 2020");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201113:nGNX1CLGsL</Origin>VASCEPA(compared with placebo, significantly reduced primary compositefirst and total major adverse cardiovascular events (MACE) in post hocexploratory analyses of patients with a history of CABG by 24% and 36%,respectively, and key secondary composite first hard MACE, comprised of heartattacks, stroke and cardiovascular death, by 31%Administration of VASCEPA resulted in robust absolute risk reductions of 6.2%and 6.0% and numbers needed to treat (NNT) of 16 and 17, respectively, forboth primary and key secondary (hard MACE) composite endpoints in thesesubgroup analysesAmarin to Webcast Discussion of Presented Data on November 18, 2020 at 4:30p.m., Eastern Standard TimeDUBLIN, Ireland and BRIDGEWATER, N.J., Nov. 13, 2020 (GLOBE NEWSWIRE) --Amarin Corporation plc (NASDAQ:AMRN) today announced the presentation ofREDUCE-IT(CABG at American Heart Association(AHA) Virtual ScientificSessions 2020, being held virtually from November 13 November 17, 2020,adding to the growing body of knowledge on the clinical impact of VASCEPA(ethyl). These new analyses supported by Amarin were presented bySubodh Verma, M.D., Ph.D., FRCSC, Professor and Cardiac Surgeon at Universityof Toronto.REDUCE-IT CABG analysis results are another piece of the puzzle whenlooking at the potential use of icosapent ethyl in the procedural setting,Dr. Deepak L. Bhatt, M.D., M.P.H., Executive Director ofInterventional Cardiovascular Programs at Brigham and WomenHospital andProfessor of Medicine at Harvard Medical School, principal investigator ofREDUCE-IT. findings of benefit in at-risk patients with prior CABG areconsistent with previously presented data on overall reductions in first andtotal coronary revascularization events, as well as in patients with priorpercutaneous coronary interventions, and further strengthen the case forconsideration of icosapent ethyl as an additional intervention for use byphysicians to care for this patient population.REDUCE-IT CABG analysis examined 1,837 (22.5%) of the patients enrolled inREDUCE-IT, representing all patients who had undergone a prior coronary arterybypass grafting (CABG) procedure, a common form of surgical intervention tohelp treat coronary heart disease. Baseline characteristics were similar amongpatients randomized to VASCEPA versus placebo. Post hoc exploratory analysesof this subgroup showed that, for the composite endpoint of 5-point MACE,which was the prespecified primary endpoint for the full REDUCE-IT studycohort, time to first event was significantly reduced with VASCEPA versusplacebo by 24% (p=0.004) and total (first and subsequent) events were alsoreduced by 36% (p=0.0002). For the REDUCE-IT studykey secondary compositeendpoint of 3-point MACE, time to first event was reduced by 31% (p=0.001) inthe subgroup of patients with a prior CABG.Coronary revascularization procedures, such as CABG, are invasive, carrymultiple risks, and can have significant direct and indirect costs. Patientswith elevated triglycerides despite statin therapy have increased risk forischemic events, including coronary revascularizations. These procedures,whether pre-scheduled or performed in an emergency, inevitably result inadditional time spent in a healthcare setting. The latest statistical updatefrom the American Heart Association (AHA) shows that, in 2014, an estimated371,000 inpatient CABG procedures were performed in the United States with amean inpatient hospital charge for CABG of $168,541.(1)procedures overall significantly impact the healthcaresystem, with CABG procedures adding to the burden and driving substantialcosts,said Steven Ketchum, Ph.D., senior vice president and president,research & development and chief scientific officer, Amarin. subgroupdata presented at AHA Virtual Scientific Sessions 2020 suggests another way inwhich VASCEPA can possibly alleviate the significant burden that at-riskpatients and the healthcare system face with CABG procedures and how VASCEPAtherapy can potentially reduce the risk of dangerous subsequent events. REDUCE-IT was not specifically powered to examine individual cardiovascularendpoints or patient subgroups, therefore p-values presented for theserevascularization analyses are nominal and exploratory with no adjustment formultiple comparisons. In addition, coronary revascularization as an endpointcan sometimes be considered subjective; however, these endpoints wereadjudicated by an independent, blinded clinical endpoint committee. Resultsfrom the total coronary revascularization events analyses are consistentacross the various recurrent event statistical models and are also consistentwith the first coronary revascularization events results. Together, theREDUCE-IT first and total coronary revascularization events results supportthe robustness and consistency of the clinical benefit of VASCEPA therapy inreducing coronary revascularization.These REDUCE-IT CABG results follow multiple scientific presentations ofanalysis results from other important patient subgroups in the REDUCE-ITstudy, including REDUCE-IT REVASC(2) and REDUCE-IT PCI. References to theseother subgroup analyses are available on Amarinwebsite at<Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>.Additional information on AHA Virtual Scientific Sessions 2020 can be foundhere.(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=-xAJ8_nZQm1A2oKXr8XGRIMyj6WMdrg8X0YxCPM7EnOImcPex1PD9Gv7wi4_k9gQd6E8Ocg7xGMUEk6TftGWLCJbyiNuSWSRbk-9ZuklgJgfMmjA16dbg7fHNOAn4Pc1tT0MrAo3hphQ199DVQNQMwivhDSbKFC5T0dRXXhzmBNcLUPtQbJjhPiCZvg2XtTunLL_Kx1eHmlvWam-1lW8jh6jE8JZlhihqkYrd4TPfhZKAU2Q9mQfoouHeCOK6Wqh</Origin>)Audio Webcast InformationAmarin will host an audio webcast on November 18, 2020, at 4:30 p.m. EST todiscuss this and other VASCEPA-related information presented during the AHAVirtual Scientific Sessions 2020. To listen please register here(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=qx7mGF5KxzfNP09j3-z-CHyhh9QYuzFItSKq3MHA0HVZ8pk_h3EfVBTC0FDJsaa5FH1Ur5MwzsMNjWALhueJ2Q==</Origin>),listen live on the investor relations section of the company's website at<Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>, or via telephone by dialing 877-407-8033 within the UnitedStates, 201-689-8033 from outside the United States.About AmarinAmarin Corporation plc is a rapidly growing, innovative pharmaceutical companyfocused on developing and commercializing therapeutics to cost-effectivelyimprove cardiovascular health. Amarinlead product, VASCEPA((icosapentethyl), is available by prescription in the United States, Canada, Lebanon andthe United Arab Emirates. VASCEPA is not yet approved and available in anyother countries. Amarin, on its own or together with its commercial partnersin select geographies, is pursuing additional regulatory approvals for VASCEPAin China, Europe and the Middle East. For more information about Amarin, visit<Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>.About Cardiovascular RiskThe number of deaths in the United States attributed to cardiovascular diseasecontinues to rise. There are 605,000 new and 200,000 recurrent heart attacksper year (approximately 1 every 40 seconds), in the United States. Strokerates are 795,000 per year (approximately 1 every 40 seconds), accounting for1 of every 19 U.S. deaths. Cardiovascular disease results in 859,000 deathsper year in the United States.(1) In aggregate, there are more than 2.4million major adverse cardiovascular events per year from cardiovasculardisease or, on average, one every 13 seconds in the United States alone.Controlling bad cholesterol, also known as LDL-C, is one way to reduce apatientrisk for cardiovascular events, such as heart attack, stroke ordeath. However, even with the achievement of target LDL-C levels, millions ofpatients still have significant and persistent risk of cardiovascular events,especially those patients with elevated triglycerides. Statin therapy has beenshown to control LDL-C, thereby reducing the risk of cardiovascular events by25-35%.(3) Significant cardiovascular risk remains after statin therapy.People with elevated triglycerides have 35% more cardiovascular eventscompared to people with normal (in range) triglycerides takingstatins.(4)(,)(5)(,)(6)About REDUCE-IT(was a global cardiovascular outcomes study designed to evaluate theeffect of VASCEPA in adult patients with LDL-C controlled to between 41-100mg/dL (median baseline 75 mg/dL) by statin therapy and various cardiovascularrisk factors including persistent elevated triglycerides between 135-499 mg/dL(median baseline 216 mg/dL) and either established cardiovascular disease(secondary prevention cohort) or diabetes mellitus and at least one othercardiovascular risk factor (primary prevention cohort).REDUCE-IT, conducted over seven years and completed in 2018, followed 8,179patients at over 400 clinical sites in 11 countries with the largest number ofsites located within the United States. REDUCE-IT was conducted based on aspecial protocol assessment agreement with FDA. The design of the REDUCE-ITstudy was published in March 2017 in Clinical Cardiology.(7) The primaryresults of REDUCE-IT were published in The New England Journal of Medicine inNovember 2018.(8) The total events results of REDUCE-IT were published in theJournal of the American College of Cardiology in March 2019.(9) These andother publications can be found in the R&D section on the companywebsiteat <Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>.About VASCEPA((icosapent ethyl) CapsulesVASCEPA (icosapent ethyl) capsules are the first-and-only prescriptiontreatment approved by the FDA comprised solely of the active ingredient,icosapent ethyl (IPE), a unique form of eicosapentaenoic acid. VASCEPA wasinitially launched in the United States in 2013 based on the druginitialFDA approved indication for use as an adjunct therapy to diet to reducetriglyceride levels in adult patients with severe (mg/dL)hypertriglyceridemia. Since launch, VASCEPA has been prescribed over eightmillion times. VASCEPA is covered by most major medical insurance plans. Thenew, cardiovascular risk indication for VASCEPA was approved by the FDA inDecember 2019.Indications and Limitation of UseVASCEPA is indicated:* As an adjunct to maximally tolerated statin therapy to reduce the risk ofmyocardial infarction, stroke, coronary revascularization and unstable anginarequiring hospitalization in adult patients with elevated triglyceride (TG)levels (150 mg/dL) and * established cardiovascular disease or* diabetes mellitus and two or more additional risk factors for cardiovasculardisease. * As an adjunct to diet to reduce TG levels in adult patients with severe(500 mg/dL) hypertriglyceridemia.The effect of VASCEPA on the risk for pancreatitis in patients with severehypertriglyceridemia has not been determined.Important Safety Information* VASCEPA is contraindicated in patients with known hypersensitivity (e.g.,anaphylactic reaction) to VASCEPA or any of its components.* VASCEPA was associated with an increased risk (3% vs 2%) of atrialfibrillation or atrial flutter requiring hospitalization in a double-blind,placebo-controlled trial. The incidence of atrial fibrillation was greater inpatients with a previous history of atrial fibrillation or atrial flutter.* It is not known whether patients with allergies to fish and/or shellfish areat an increased risk of an allergic reaction to VASCEPA. Patients with suchallergies should discontinue VASCEPA if any reactions occur.* VASCEPA was associated with an increased risk (12% vs 10%) of bleeding in adouble-blind, placebo-controlled trial. The incidence of bleeding was greaterin patients receiving concomitant antithrombotic medications, such as aspirin,clopidogrel or warfarin.* Common adverse reactions in the cardiovascular outcomes trial (incidenceand more frequent than placebo): musculoskeletal pain (4% vs 3%),peripheral edema (7% vs 5%), constipation (5% vs 4%), gout (4% vs 3%), andatrial fibrillation (5% vs 4%).* Common adverse reactions in the hypertriglyceridemia trials (incidence >1%more frequent than placebo): arthralgia (2% vs 1%) and oropharyngeal pain (1%vs 0.3%).* Adverse events may be reported by calling 1-855-VASCEPA or the FDA at1-800-FDA-1088.* Patients receiving VASCEPA and concomitant anticoagulants and/oranti-platelet agents should be monitored for bleeding.Key clinical effects of VASCEPA on major adverse cardiovascular events areincluded in the Clinical Studies section of the prescribing information forVASCEPA as set forth below:Effect of VASCEPA on Time to First Occurrence of Cardiovascular Events inPatients with Elevated Triglyceride levels and Other Risk Factors for Cardiovascular Diseasein REDUCE-IT                                                                                                                                      VASCEPA                                                 Placebo                                                 VASCEPA  vs Placebo                                                                                                                                          N = 4089 n (%)  Incidence Rate (per 100 patient years)  N = 4090 n (%)  Incidence Rate (per 100 patient years)  Hazard Ratio (95% CI)   Primary composite endpoint                                                                                                                                                                                                                                                   Cardiovascular death, myocardial infarction, stroke, coronary revascularization, hospitalization for unstable angina (5-point MACE)  705 (17.2)      4.3                                     901 (22.0)      5.7                                     0.75 (0.68, 0.83)       Key secondary composite endpoint                                                                                                                                                                                                                                             Cardiovascular death, myocardial infarction, stroke (3-point MACE)                                                                   459 (11.2)      2.7                                     606 (14.8)      3.7                                     0.74 (0.65, 0.83)       Other secondary endpoints                                                                                                                                                                                                                                                    Fatal or non-fatal myocardial infarction                                                                                             250 (6.1)       1.5                                     355 (8.7)       2.1                                     0.69 (0.58, 0.81)       Emergent or urgent coronary revascularization                                                                                        216 (5.3)       1.3                                     321 (7.8)       1.9                                     0.65 (0.55, 0.78)       Cardiovascular death ( <Origin Href=\\\"NewsSearch\\\">1</Origin> )                                                                                                           174 (4.3)       1.0                                     213 (5.2)       1.2                                     0.80 (0.66, 0.98)       Hospitalization for unstable angina ( <Origin Href=\\\"NewsSearch\\\">2</Origin> )                                                                                            108 (2.6)       0.6                                     157 (3.8)       0.9                                     0.68 (0.53, 0.87)       Fatal or non-fatal stroke                                                                                                            98 (2.4)        0.6                                     134 (3.3)       0.8                                     0.72 (0.55, 0.93)        <Origin Href=\\\"NewsSearch\\\">1</Origin>  Includes adjudicated cardiovascular deaths and deaths of undetermined causality.  <Origin Href=\\\"NewsSearch\\\">2</Origin>  Determined to be caused by myocardial ischemia by invasive/non-invasive testing and requiring emergent hospitalization.                                                            FULL VASCEPA PRESCRIBING INFORMATION(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=GZ80u8mEJx83DXpKdifGj45jbkgQj4xrpV2uDaGqVZwQa6_1up3UpOy9WXca4vcFiZgkKUNS4ACQABqwy2tVZAk--BOLT2Nol38LkRxMa3vMyyFv6LjvkkAU6i78TNKu</Origin>)CAN BE FOUND AT WWW.(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=ipgGZKoYzBpS3Te5Oqr6kdbTUXr_W-G3Rm4_oBffXzLsADtw5O9FW8FS784AUCPN-MRwo0ciG3ZOWSey4sgxcw==</Origin>)VASCEPA(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=ca7YX-dvXyPHV48tIvruvMI4Wud3Ntx2fADV88n9srsVEctcGaWfcmvdv4kMNgzJHF9eQ7Mm1qn8n3aNSLDKBw==</Origin>).COM(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=vQJSNHyzsOq0sfupaFv6co0cc5e07nRmI8RHLruWX0MfaAMfbJ0NSfMF65-U77rn3BadJ8uJLbqu0Y9sKA7oyg==</Origin>).Forward-Looking Statements This press release contains forward-looking statements, including statementsregarding the potential impact of VASCEPA in various clinical uses. Theseforward-looking statements are not promises or guarantees and involvesubstantial risks and uncertainties. Among the factors that could cause actualresults to differ materially from those described or projected herein includethe following: uncertainties associated generally with research anddevelopment and clinical trials such as further clinical evaluations failingto confirm earlier findings. A further list and description of these risks,uncertainties and other risks associated with an investment in Amarin can befound in Amarin's filings with the U.S. Securities and Exchange Commission,including its most recent Quarterly Report on Form 10-Q. Existing andprospective investors are cautioned not to place undue reliance on theseforward-looking statements, which speak only as of the date hereof. Amarinundertakes no obligation to update or revise the information contained in thispress release, whether as a result of new information, future events orcircumstances or otherwise. Amarinforward-looking statements do notreflect the potential impact of significant transactions the company may enterinto, such as mergers, acquisitions, dispositions, joint ventures or anymaterial agreements that Amarin may enter into, amend or terminate.Availability of Other Information About AmarinInvestors and others should note that Amarin communicates with its investorsand the public using the company website (<Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>), the investorrelations website (investor.amarincorp.com(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=DX4KwbtJiUJh-AIvQ11aXF2Eyuiw9fV3WtZeYeYsdJiNwJbJH4FK7nTu1SqmcO5iFcMQLeSv0coDLqeSHy7I5P7yyujPa-EdXKbPuL541Qo=</Origin>)),including but not limited to investor presentations and investor FAQs,Securities and Exchange Commission filings, press releases, public conferencecalls and webcasts. The information that Amarin posts on these channels andwebsites could be deemed to be material information. As a result, Amarinencourages investors, the media, and others interested in Amarin to review theinformation that is posted on these channels, including the investor relationswebsite, on a regular basis. This list of channels may be updated from time totime on Amarininvestor relations website and may include social mediachannels. The contents of Amarinwebsite or these channels, or any otherwebsite that may be accessed from its website or these channels, shall not bedeemed incorporated by reference in any filing under the Securities Act of1933.Amarin Contact InformationInvestor Inquiries:Investor RelationsIn U.S.: +1 (908) 719-1315Amarin Corporation plcIR@amarincorp.com (investor inquiries)Solebury Troutlstern@soleburytrout.comMedia Inquiries:Alina KolomeyerCommunicationsAmarin Corporation plcIn U.S.: +1 (908) 892-2028 PR@amarincorp.com (media inquiries)_____________________________(1)  American Heart Association. Heart Disease and Stroke StatisticsA Report From the American Heart Association. Circulation.2020;141:e139 Peterson BE, Bhatt DL, Steg PG, Miller M, Brinton EA, Jacobson TA,Ketchum SB, Juliano RA, Jiao L, Doyle RT Jr, Granowitz C, Gibson CM, Pinto D,Giugliano RP, Budoff MJ, Tardif JC, Verma S, Ballantyne CM; REDUCE-ITInvestigators. Reduction in Revascularization with Icosapent Ethyl: Insightsfrom REDUCE-IT REVASC. Circulation. 2020 Nov 5. doi:10.1161/CIRCULATIONAHA.120.050276. Epub ahead of print. PMID: 33148016.(3)  Ganda OP, Bhatt DL, Mason RP, et al. Unmet need for adjunctivedyslipidemia therapy in hypertriglyceridemia management. J Am Coll Cardiol.2018;72(3):330-343.(4)  Budoff M. Triglycerides and triglyceride-rich lipoproteins in the causalpathway of cardiovascular disease. Am J Cardiol. 2016;118:138-145.(5)  Toth PP, Granowitz C, Hull M, et al. High triglycerides are associatedwith increased cardiovascular events, medical costs, and resource use: Areal-world administrative claims analysis of statin-treated patients with highresidual cardiovascular risk. J Am Heart Assoc. 2018;7(15):e008740.(6  )Nordestgaard BG. Triglyceride-rich lipoproteins and atheroscleroticcardiovascular disease - New insights from epidemiology, genetics, andbiology. Circ Res. 2016;118:547-563.(7)  Bhatt DL, Steg PG, Brinton E, et al., on behalf of the REDUCE-ITInvestigators. Rationale and Design of REDUCEReduction ofCardiovascular Events with Icosapent EthylTrial. Clin Cardiol.2017;40:138-148.(8)  Bhatt DL, Steg PG, Miller M, et al., on behalf of the REDUCE-ITInvestigators. Cardiovascular Risk Reduction with Icosapent Ethyl forHypertriglyceridemia. N Engl J Med. 2019;380:11-22.(9)  Bhatt DL, Steg PG, Miller M, et al., on behalf of the REDUCE-ITInvestigators. Reduction in first and total ischemic events with icosapentethyl across baseline triglyceride tertiles. J Am Coll Cardiol.2019;74:1159-1161.(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/00eeab93-e634-4279-86b3-7973c2a21623</Origin>)GlobeNewswire, Inc. 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201111:nGNX9nq9Wk")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-11T21:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"Amarin to Present at the Stifel 2020 Virtual Healthcare and  Jefferies Virtual London Healthcare Conferences");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201111:nGNX9nq9Wk</Origin>DUBLIN, Ireland and BRIDGEWATER, N.J., Nov. 11, 2020 (GLOBE NEWSWIRE) --Amarin Corporation plc (NASDAQ:AMRN) today announced that John F. Thero,Amarin's president and chief executive officer, is scheduled to presentgeneral company updates at the following investor conferences scheduled inNovember:* Stifel 2020 Virtual Healthcare conference on Monday, November 16, 2020 at8:40 a.m. Eastern Time (ET)* Jefferies Virtual London Healthcare conference on Thursday, November 19,2020 at 9:40 a.m. Eastern Time (ET)Live audio webcasts of the presentations will be available at:<Origin Href=\\\"Link\\\">http://www.amarincorp.com</Origin>, and will be accessible at the same link for 30days.About AmarinAmarin Corporation plc is a rapidly growing, innovative pharmaceutical companyfocused on developing and commercializing therapeutics to cost-effectivelyimprove cardiovascular health. Amarinlead product, VASCEPA(icosapentethyl), is available by prescription in the United States, Canada, Lebanon andthe United Arab Emirates. VASCEPA is not yet approved and available in anyother countries. Amarin, on its own or together with its commercial partnersin select geographies, is pursuing additional regulatory approvals for VASCEPAin China, Europe and the Middle East. For more information about Amarin, visit<Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>.Availability of Other Information About AmarinInvestors and others should note that Amarin communicates with its investorsand the public using the company website <Origin Href=\\\"Link\\\">http://www.amarincorp.com/</Origin>), theinvestor relations website (<Origin Href=\\\"Link\\\">http://investor.amarincorp.com/</Origin>), including butnot limited to investor presentations and investor FAQs, Securities andExchange Commission filings, press releases, public conference calls andwebcasts. The information that Amarin posts on these channels and websitescould be deemed to be material information. As a result, Amarin encouragesinvestors, the media, and others interested in Amarin to review theinformation that is posted on these channels, including the investor relationswebsite, on a regular basis. This list of channels may be updated from time totime on Amarininvestor relations website and may include social mediachannels. The contents of Amarinwebsite or these channels, or any otherwebsite that may be accessed from its website or these channels, shall not bedeemed incorporated by reference in any filing under the Securities Act of1933.Amarin Contact InformationInvestor Inquiries:Investor RelationsAmarin Corporation plcIn U.S.: +1 (908) 719-1315investor.relations@amarincorp.comSolebury Troutlstern@soleburytrout.com(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=xOn8lylJQkhXVYTuPnEI64QpuVwUIvjc8cTGEuyRhu2x4TFWfrQAM1JF5VOwd42XktVOueqQ373kSJICnt_W-Q==</Origin>)MediaInquiries:CommunicationsAmarin Corporation plcIn U.S.: +1 (908) 892-2028pr@amarincorp.com(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/4e9ba297-8a1c-40bf-a8e8-4e7147b9d5de</Origin>)GlobeNewswire, Inc. 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201109:nFWN2HV13A")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-09T14:00:09-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-ADF Foods Ltd Sept-Quarter Consol PAT Rises");
			hm1.put("TE",
					"<p>    Nov 9 (Reuters) - ADF Foods Ltd  <Origin Href=\\\"QuoteRef\\\">AMRN.NS</Origin> :    * ADF FOODS LTD SEPT-QUARTER CONSOL PAT 125.8 MILLION RUPEESVERSUS 92.9 MILLION RUPEES YEAR AGO    * ADF FOODS LTD SEPT-QUARTER CONSOL INCOME FROM OPERATIONS942.9MILLION RUPEES VERSUS 601.3 MILLION RUPEES YEAR AGO    * ADF FOODS LTD -  APPROVED PROPOSAL OF TAKING ON LEASE AMANUFACTURING FACILITY IN GUJARATSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:</Origin> Further company coverage:  <Origin Href=\\\"NewsSearch\\\">AMRN.NS</Origin>  ((Reuters.Briefs@thomsonreuters.com;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201117:nGNE6CFqqv")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-17T08:11:01-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "COVID-19 is a commandto address Africachallenges - Tony Blair");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201117:nGNE6CFqqv</Origin>* We have the same problems but what we also have is vastly increased urgency- Blair* Agriculture offers Africa its best opportunity for industrializationis how does Africa raise productivity, develop the integrated technologyin rural areas? - AdesinaABIDJAN, CdNov. 17, 2020 (GLOBE NEWSWIRE) -- The COVID-19pandemic has exposed the challenges and opportunities of Africalandscape, former British prime minister Tony Blair said on Mondayin a lecture organized by the African Development Institute in Abidjan.have the same problems but what we also have is vastly increasedurgencyso much a wake-up call but a wake-up command,Blair said.The former UK prime minister addressed a virtual audience on the topicBuilding Back Better in Post COVID-19 Africa: The Role of Technology andGovernance, as part of the Kofi A. Annan Lecture Series. The series, launchedby the African Development BankAfrican Development Institute in 2006, hascovered a range of African and global development topics, including economics,finance, regional integration, human development and the environment. Thelectures have been a forum for eminent persons to share policy insights ondevelopment challenges in Africa.Over 4,500 delegates from across the globe including Government Officials,Governors and Executive Directors of the Bank Group, the BankSeniorManagement, and leading experts and heads of institutions tuned in to thelecture.In opening remarks, Rabah Arezki, Chief Economist and Vice President forEconomic Governance and Knowledge Management of the Bank described the taskahead as and challenging.in his first ever virtual lecture, outlined three aspects which in hiswords would make a big difference to Africa: investing in industrialization,accelerating technological innovations, and building capacity for institutionsto get things done. are components to the BankHigh 5 priorities.All of those things which define the challenges that Africa has all ofthose are now given added urgency by Covid and its impact,Blair said.To build back better, West Africa, for instance, could capitalize on its richsource of cotton for garment production and the textile industry. Elsewhere onthe continent, Africa was already leading in the digital technology spacewhich can be scaled up.the world you are seeing governments use technology effectivelythis is a great ambition of the African Development Bank. This iscritical,Blair said.Blaire highlighted the four Ps of government delivery prioritization,policy, personnel and performance management. On prioritization, Mr. Blaircalled on African governments to identify and focus on their comparativeadvantages, and focus on deliveryon key transformative projectsand manage expectations.the end only Africa can do itwe are partners in AfricaAfricaprogress,Blair said.Blairspeech was followed by a conversation with Bank Group PresidentAkinwumi Adesina, who said the lecture series brought global and nationalperspectives to the development issues discussed.need to constantly push the frontiers of dialogue in the publicsphere,Adesina said. is more topical today than the challengesposed by COVID-19. The pandemic has upended economic growth.Mr. Adesinanoted.Agreeing with Mr. Blair about the importance of the culture of delivery,Adesina said agriculture offered Africa its best opportunity forindustrialization. key is: how does Africa raise productivity inagriculturedoes it develop the integrated infrastructure in those ruralareaswill allow the creation of new economic sources of prosperity outof what it has?Adesina asked.Although the BankTechnologies for African Agricultural Transformation(TAAT) initiative had allowed it to reach millions of farmers withagricultural technology and is boosting yields in wheat, there is still theneed to scale up. have a lot of pilots...The name of the game isscale,Adesina said.Adesina cited other key interventions by the Bank, including a $10 billionCOVID-19 Response Facility to provide budget support to African countries andits innovative $3 billion COVID-19 social bond, to save livelihoods.After retiring from office, Blair launched the Tony Blair Institute for GlobalChange, which works on some of the most difficult challenges in the worldtoday, primarily in three areas: supporting governments to deliver effectivelyfor their people, working for peace in the Middle East, and counteringextremism.Speaking after the seminar, Prof. Kevin Urama, Senior Director of the ADI saidthe priorities are well mapped out for Africa to build back better. TheAfrican Development Institute (ADI) has been at the forefront of acceleratingcapacity development, technical assistance and policy dialogue on thecontinent.Contact: Amba Mpoke-Bigg, African Development Bank, Communication and ExternalRelations Department, African Development Bank, email: a.mpoke-bigg@afdb.orgAfrican Development Institute Global Community of Practice, email:adigcop@afdb.orgA photo accompanying this announcement is available at<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/4617b492-0771-41cb-af72-a02ea32ca2fb</Origin></p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201117:nGNX366Dj2")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-17T08:11:01-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "COVID-19 is a commandto address Africachallenges - Tony Blair");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201117:nGNX366Dj2</Origin>* We have the same problems but what we also have is vastly increased urgency- Blair* Agriculture offers Africa its best opportunity for industrializationis how does Africa raise productivity, develop the integrated technologyin rural areas? - AdesinaABIDJAN, CdNov. 17, 2020 (GLOBE NEWSWIRE) -- The COVID-19pandemic has exposed the challenges and opportunities of Africalandscape, former British prime minister Tony Blair said on Mondayin a lecture organized by the African Development Institute in Abidjan.have the same problems but what we also have is vastly increasedurgencyso much a wake-up call but a wake-up command,Blair said.The former UK prime minister addressed a virtual audience on the topicBuilding Back Better in Post COVID-19 Africa: The Role of Technology andGovernance, as part of the Kofi A. Annan Lecture Series. The series, launchedby the African Development BankAfrican Development Institute in 2006, hascovered a range of African and global development topics, including economics,finance, regional integration, human development and the environment. Thelectures have been a forum for eminent persons to share policy insights ondevelopment challenges in Africa.Over 4,500 delegates from across the globe including Government Officials,Governors and Executive Directors of the Bank Group, the BankSeniorManagement, and leading experts and heads of institutions tuned in to thelecture.In opening remarks, Rabah Arezki, Chief Economist and Vice President forEconomic Governance and Knowledge Management of the Bank described the taskahead as and challenging.in his first ever virtual lecture, outlined three aspects which in hiswords would make a big difference to Africa: investing in industrialization,accelerating technological innovations, and building capacity for institutionsto get things done. are components to the BankHigh 5 priorities.All of those things which define the challenges that Africa has all ofthose are now given added urgency by Covid and its impact,Blair said.To build back better, West Africa, for instance, could capitalize on its richsource of cotton for garment production and the textile industry. Elsewhere onthe continent, Africa was already leading in the digital technology spacewhich can be scaled up.the world you are seeing governments use technology effectivelythis is a great ambition of the African Development Bank. This iscritical,Blair said.Blaire highlighted the four Ps of government delivery prioritization,policy, personnel and performance management. On prioritization, Mr. Blaircalled on African governments to identify and focus on their comparativeadvantages, and focus on deliveryon key transformative projectsand manage expectations.the end only Africa can do itwe are partners in AfricaAfricaprogress,Blair said.Blairspeech was followed by a conversation with Bank Group PresidentAkinwumi Adesina, who said the lecture series brought global and nationalperspectives to the development issues discussed.need to constantly push the frontiers of dialogue in the publicsphere,Adesina said. is more topical today than the challengesposed by COVID-19. The pandemic has upended economic growth.Mr. Adesinanoted.Agreeing with Mr. Blair about the importance of the culture of delivery,Adesina said agriculture offered Africa its best opportunity forindustrialization. key is: how does Africa raise productivity inagriculturedoes it develop the integrated infrastructure in those ruralareaswill allow the creation of new economic sources of prosperity outof what it has?Adesina asked.Although the BankTechnologies for African Agricultural Transformation(TAAT) initiative had allowed it to reach millions of farmers withagricultural technology and is boosting yields in wheat, there is still theneed to scale up. have a lot of pilots...The name of the game isscale,Adesina said.Adesina cited other key interventions by the Bank, including a $10 billionCOVID-19 Response Facility to provide budget support to African countries andits innovative $3 billion COVID-19 social bond, to save livelihoods.After retiring from office, Blair launched the Tony Blair Institute for GlobalChange, which works on some of the most difficult challenges in the worldtoday, primarily in three areas: supporting governments to deliver effectivelyfor their people, working for peace in the Middle East, and counteringextremism.Speaking after the seminar, Prof. Kevin Urama, Senior Director of the ADI saidthe priorities are well mapped out for Africa to build back better. TheAfrican Development Institute (ADI) has been at the forefront of acceleratingcapacity development, technical assistance and policy dialogue on thecontinent.Contact: Amba Mpoke-Bigg, African Development Bank, Communication and ExternalRelations Department, African Development Bank, email: a.mpoke-bigg@afdb.orgAfrican Development Institute Global Community of Practice, email:adigcop@afdb.orgA photo accompanying this announcement is available at<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/4617b492-0771-41cb-af72-a02ea32ca2fb</Origin>(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/262eaf06-229c-4c1e-93c7-32ff259ae6dd</Origin>)Tony Blair and Dr Akinwumi Adesina (<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/4617b492-0771-41cb-af72-a02ea32ca2fb/en</Origin>)His Excellency the Rt Hon Tony Blair, Executive Chairman of the Tony BlairInstitute for Global Change and former Prime Minister of Great Britain andNorthern Ireland, delivering the 2020 Kofi Annan Eminent SpeakersLectureat a virtual event organized by the African Development Institute.GlobeNewswire, Inc. 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201114:nGNE669KWF")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-14T10:08:25-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "MoodyInvestor Service affirms African Development BankAAA credit rating");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201114:nGNE669KWF</Origin>ABIDJAN, CdNov. 14, 2020 (GLOBE NEWSWIRE) -- MoodyService has affirmed the African Development BankAAA creditrating, with a stable outlook.credit profile of African Development Bank (AfDB) is supported by thebankrobust capital buffers and superior risk management, which mitigaterisks,MoodyInvestor Service said in an annual credit analysis dated27 October 2020. Moodyadded: ample liquidity buffer and unfettered access tointernational capital markets also support its ability to meet itsdebt-service obligations. Moreover, the bank has a long track record of beingthe premier development institution in Africa and benefits fromshareholdersability and willingness to support its development objectives,exemplified by the significant contributions of highly rated non-regionalmember countries.Akinwumi Adesina, President of the African Development Bank, said: rating by Moodyvalidates the strength of the Bankprudentfinancial and risk management and strong governance systems even in the faceof tough challenges imposed by the Covid-19 pandemic. The extraordinarysupport of the Bankshareholders boosts our capacity to finance Africancountries. We will continue to manage risks and capital requirementsadequately to help African countries to build their economies back better andfaster, while assuring economic, health and climate resilience.Tshabalala, Acting Senior Vice President, Vice President for Finance andChief Finance Officer at the African Development Bank, said: to thesolid backing of its shareholders and strong financial profile, the AfricanDevelopment Bank is rated triple-A with stable outlook by all the majorinternational rating agencies.rating from Moodyfollows earlier affirmations of therating of the Bank, with stable outlook, by the other leading ratingagencies, namely Fitch Ratings, Standard and PoorGlobal Ratings and JapanCredit Rating Agency.About the African Development Bank GroupThe African Development Bank Group is Africapremier development financeinstitution. It comprises three distinct entities: the African DevelopmentBank (AfDB), the African Development Fund (ADF) and the Nigeria Trust Fund(NTF). On the ground in 41 African countries with an external office in Japan,the Bank contributes to the economic development and the social progress ofits 54 regional member states. For more information: <Origin Href=\\\"Link\\\">www.afdb.org</Origin>Contact: Amba Mpoke-Bigg, Communication and External Relations Department,African Development Bank, email: a.mpoke-bigg@afdb.org</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201114:nGNXc4gF4N")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-14T10:08:25-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "MoodyInvestor Service affirms African Development BankAAA credit rating");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201114:nGNXc4gF4N</Origin>ABIDJAN, CdNov. 14, 2020 (GLOBE NEWSWIRE) -- MoodyService has affirmed the African Development BankAAA creditrating, with a stable outlook.credit profile of African Development Bank (AfDB) is supported by thebankrobust capital buffers and superior risk management, which mitigaterisks,MoodyInvestor Service said in an annual credit analysis dated27 October 2020. Moodyadded: ample liquidity buffer and unfettered access tointernational capital markets also support its ability to meet itsdebt-service obligations. Moreover, the bank has a long track record of beingthe premier development institution in Africa and benefits fromshareholdersability and willingness to support its development objectives,exemplified by the significant contributions of highly rated non-regionalmember countries.Akinwumi Adesina, President of the African Development Bank, said: rating by Moodyvalidates the strength of the Bankprudentfinancial and risk management and strong governance systems even in the faceof tough challenges imposed by the Covid-19 pandemic. The extraordinarysupport of the Bankshareholders boosts our capacity to finance Africancountries. We will continue to manage risks and capital requirementsadequately to help African countries to build their economies back better andfaster, while assuring economic, health and climate resilience.Tshabalala, Acting Senior Vice President, Vice President for Finance andChief Finance Officer at the African Development Bank, said: to thesolid backing of its shareholders and strong financial profile, the AfricanDevelopment Bank is rated triple-A with stable outlook by all the majorinternational rating agencies.rating from Moodyfollows earlier affirmations of therating of the Bank, with stable outlook, by the other leading ratingagencies, namely Fitch Ratings, Standard and PoorGlobal Ratings and JapanCredit Rating Agency.About the African Development Bank GroupThe African Development Bank Group is Africapremier development financeinstitution. It comprises three distinct entities: the African DevelopmentBank (AfDB), the African Development Fund (ADF) and the Nigeria Trust Fund(NTF). On the ground in 41 African countries with an external office in Japan,the Bank contributes to the economic development and the social progress ofits 54 regional member states. For more information: <Origin Href=\\\"Link\\\">www.afdb.org</Origin>Contact: Amba Mpoke-Bigg, Communication and External Relations Department,African Development Bank, email: a.mpoke-bigg@afdb.org(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/262eaf06-229c-4c1e-93c7-32ff259ae6dd</Origin>)GlobeNewswire, Inc. 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201102:nASA01BFN")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-02T12:18:58-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-AMC Networks Inc Reports Third Quarter EPS $1.17");
			hm1.put("TE",
					"<p>    Nov 2 (Reuters) - AMC Networks Inc  <Origin Href=\\\"QuoteRef\\\">AMCX.O</Origin> :    * . REPORTS THIRD QUARTER 2020 RESULTS    * Q3 ADJUSTED EARNINGS PER SHARE $1.32    * Q3 EARNINGS PER SHARE $1.17    * Q3 REVENUE $654 MILLION VERSUS REFINITIV IBES ESTIMATE OF$606MILLION    * Q3 EARNINGS PER SHARE ESTIMATE $1.28 -- REFINITIV IBESDATA    * AMC NETWORKS - NOW EXPECTS IN EXCESS OF 4.0 MILLION PAIDSUBSCRIBERS IN AGGREGATE FOR ITS FOUR SVOD SERVICES: ACORN TV,SHUDDER, SUNDANCE NOW & UMC BY YEAR-END 2020.    * EXPECTS 5.0 MILLION TO 5.5 MILLION PAID SUBSCRIBERS INAGGREGATEBY YEAR-END 2020 FOR ITS PORTFOLIO OF STREAMING SERVICESSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:nGNX2kTy77</Origin> Further company coverage:  <Origin Href=\\\"QuoteRef\\\">AMCX.O</Origin>  ((Reuters.Briefs@thomsonreuters.com;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201102:nGNX2kTy77")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-02T12:00:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "AMC Networks Inc. Reports Third Quarter 2020 Results");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201102:nGNX2kTy77</Origin>NEW YORK, Nov. 02, 2020 (GLOBE NEWSWIRE) -- AMC Networks Inc. (or the (NASDAQ: AMCX) today reported financialresults for the third quarter ended September 30, 2020.President and Chief Executive Officer Josh Sapan said: companydelivered solid results in the 3(rd) quarter and we continue to maintain astrong financial profile, with a solid balance sheet, very good liquidity andhealthy levels of free cash flow. AMC Networks is fast becoming the globalleader in SVOD services for targeted audiences, with our Acorn TV, Shudder,Sundance Now and UMC services set to exceed 4 million subscribers by year-end,outperforming our expectations. With the addition of our new AMC+ premium SVODoffering, we expect to have 5.0 to 5.5 million total SVOD subscribers, inaggregate, by the end of the year. Our strong content also continues toresonate with viewers, with AMC home to 4 of the top 6 cable dramas in 2020among adults 25-54, including our newest series in The Walking DeadUniverse, The Walking Dead: World Beyond ranking as the #1 freshman cabledrama of the year.Highlights:* Net revenues of $654 million* Operating income of $139 million; Adjusted Operating Income(1) of $185million* Diluted EPS of $1.17; Adjusted EPS(1) of $1.32* Cash Provided by Operating Activities of $219 million and Free Cash Flow(1)of $203 million for the three months ended September 30, 2020* 10.8 million shares repurchased for $251 million in October 2020 inconnection with the Companymodified Dutch auction tender offerOperational Highlights:* The Company continued to make significant progress on its digitalinitiatives-- The Company expects 5.0 million to 5.5 million paid subscribers inaggregate by year-end 2020 for its portfolio of streaming services: includingAMC+, Acorn TV, Shudder, Sundance Now, UMC and IFC Films Unlimited.-- The Company now expects in excess of 4.0 million paid subscribers inaggregate for its four SVOD services: Acorn TV, Shudder, Sundance Now and UMCby year-end 2020.-- The Company significantly expanded the distribution of AMC+, itssubscription video on demand bundle, with launches on Amazon Prime and AppleTV platforms-- Shudder surpasses 1 million subscribers* The Company announced significant updates on The Walking Dead universe-- The Walking Dead will conclude with an expanded two-year eleventh season-- The greenlighting of a new original series focused on the popular DarylDixon and Carol Peletier characters, which is scheduled to premiere in 2023-- The development of a new episodic anthology series called Tales of theWalking DeadConsolidated ResultsThird quarter net revenues decreased 9.0%, or $65 million, to $654 millionover the third quarter of 2019. The decrease in net revenues reflected adecrease of 17.3% at National Networks and an increase of 9.0% atInternational and Other. Operating income was $139 million, a decrease of17.2%, or $29 million, versus the prior year period. The operating incomedecrease reflected a decrease of 28.8% at National Networks and an increase of$23 million in operating income at International and Other. Adjusted OperatingIncome(1) was $185 million, a decrease of 15.4%, or $34 million, versus theprior year period. The decrease in adjusted operating income reflected adecrease of 23.6% at National Networks offset by an increase of $14 million atInternational and Other versus the prior year period.For the nine months ended September 30, 2020, net revenues decreased 10.6%, or$240 million, to $2.035 billion, operating income decreased 38.1%, or $222million, to $361 million, and adjusted operating income decreased 14.9%, or$111 million, to $633 million.Third quarter net income was $62 million ($1.17 per diluted share), comparedwith $117 million ($2.07 per diluted share) in the prior year period. EPSprimarily reflected the decrease in operating income and an increase in incometax expense partially offset by an increase in miscellaneous, net. Thirdquarter Adjusted EPS(1) was $70 million ($1.32 per diluted share), comparedwith $132 million ($2.33 per diluted share) in the prior year period. Thedecrease in adjusted EPS primarily related to the decrease in adjustedoperating income and an increase in income tax expense partially offset by anincrease in miscellaneous, net.Net income for the nine months ended September 30, 2020 was $145 million($2.69 per diluted share), compared with $389 million ($6.80 per dilutedshare) in the prior year period. Adjusted EPS for the nine months endedSeptember 30, 2020 was $279 million ($5.17 per diluted share), compared with$433 million ($7.57 per diluted share) in the prior year period.For the nine months ended September 30, 2020, net cash provided by operatingactivities was $644 million, an increase of $244 million versus the prior yearperiod. The increase was primarily the result of a decrease in working capitaland tax payments partially offset by a decrease in adjusted operating income.Free Cash Flow(1) for the nine months ended September 30, 2020 was $595million, an increase of $277 million versus the prior year period. Theincrease primarily reflects the increase in net cash provided by operatingactivities as well as a decrease in capital expenditures.                                                                                                                                                                                                                               1.  See page 5 of this earnings release for a discussion of non-GAAP financial measures used in this release. This discussion includes the definition of Adjusted Operating Income (Loss), Adjusted EPS and Free Cash Flow.                                                                                                                                                                                                                                Segment Results (dollars in thousands)                       Three Months Ended September 30,                                    Nine Months Ended September 30,                                                                            2020                          2019                       Change     2020                         2019                   Change                                                                                                                                                                                         Net Revenues:                                                                                                                                                                                         National Networks           $        462,149              $     558,992              (17.3%)    $     1,524,938              $     1,779,850        (14.3%)                    International and Other     199,292                       182,839                    9.0%             530,765                533,454                (0.5%)                     Inter-segment eliminations           (7,426   )                 (23,234  )           n/m              (21,022    )                 (38,187    )     n/m       Total Net Revenues                           $        654,015              $     718,597              (9.0%)     $     2,034,681              $     2,275,117        (10.6%)                                                                                                                                                                                              Operating Income (Loss):                                                                                                                                                                              National Networks           $        129,842              $     182,479              (28.8%)    $     512,598                $     648,180          (20.9%)                          International and Other     11,198                              (11,501  )           n/m              (147,226   )                 (52,532    )     n/m                              Inter-segment eliminations           (1,563   )                 (2,540   )           n/m              (4,123     )                 (12,090    )     n/m             Total Operating Income (Loss)                $        139,477              $     168,438              (17.2%)    $     361,249                $     583,558          (38.1%)         Adjusted Operating Income:                                                                                                                                       National Networks           $       159,177            $  208,410       (23.6%)    $  586,699       $  721,569     (18.7%)                     International and Other             27,877                13,465        107.0%        50,647           35,237      43.7%                       Inter-segment eliminations          (1,563   )            (2,540   )    n/m           (4,123   )       (12,786  )  n/m       Total Adjusted Operating Income               $       185,491            $  219,335       (15.4%)    $  633,223       $  744,020     (14.9%)                                                                                                                                                 National Networks  National Networks principally consists of the Companyfive nationallydistributed programming networks, AMC, WE tv, BBC AMERICA, IFC and SundanceTV;and AMC Studios, the Companyproduction business.National Networks revenues for the third quarter 2020 decreased 17.3% to $462million, operating income decreased 28.8% to $130 million, and adjustedoperating income decreased 23.6% to $159 million, all compared to the prioryear period.National Networks revenues for the nine months ended September 30, 2020decreased 14.3% to $1.525 billion, operating income decreased 20.9% to $513million, and adjusted operating income decreased 18.7% to $587 million, allcompared to the prior year period.Third quarter revenues reflected an 18.3% decrease in distribution revenues to$298 million. The decrease in distribution revenues was attributable to both adecrease in content licensing and subscription revenues. Advertising revenuesdecreased 15.5% to $164 million. The decrease in advertising revenues wasprimarily related to the timing of the airing of original programming as wellas the impact of the COVID-19 pandemic, which resulted in lower demand.Third quarter operating income and adjusted operating income reflected adecrease in revenues partially offset by a decrease in operating expenses. Thedecrease in operating expenses was primarily attributable to lower programmingexpenses. Programming expenses included charges of $20 million in the currentyear period related to the write-off of programming assets, as compared tocharges of $1 million in the prior year period. Advertising and marketingexpenses also decreased as compared to the prior year period.International and Other  International and Other principally consists of AMC Networks International,the Company's international programming business; AMC Networks SVOD, theCompanysubscription streaming business; Levity, the Companyservices and comedy venues business; and IFC Films, the Companyfilm distribution business.International and Other revenues for the third quarter of 2020 increased 9.0%to $199 million, operating income increased $23 million to $11 million, andadjusted operating income increased $14 million to $28 million, all comparedto the prior year period.International and Other revenues for the nine months ended September 30, 2020decreased 0.5% to $531 million, operating loss increased $95 million to a lossof $147 million, and adjusted operating income increased $15 million to $51million, all compared to the prior year period.Third quarter revenues primarily reflected an increase at AMC Networks SVODpartially offset by a decrease at Levity.Third quarter operating loss and adjusted operating income reflected theincrease in revenues partially offset by an increase in operating expenses.Operating income also reflected a decrease in restructuring and other relatedcharges as well as depreciation and amortization.Other MattersCOVID-19As previously disclosed, the impact of COVID-19 and measures to prevent itsspread are affecting the Companybusinesses in a number of ways. Beginningin mid-March, the Company experienced adverse advertising sales impacts;suspended content production, which has led to delays in the creation andavailability of some of its television programming; and temporarily closed itscomedy venues. Operationally, nearly all Company employees are workingremotely, and the Company has restricted business travel. The Company hasevaluated and continues to evaluate the potential impact of the COVID-19pandemic on its consolidated financial statements. The Company cannotreasonably predict the ultimate impact of the COVID-19 pandemic, including theextent of any adverse impact on its business, results of operations andfinancial condition, which will depend on, among other things, the durationand spread of the pandemic, the impact of governmental regulations that havebeen, and may continue to be, imposed in response to the pandemic, theeffectiveness of actions taken to contain or mitigate the outbreak, theavailability, safety and efficacy of a vaccine, and global economicconditions. The Company believes that the COVID-19 pandemic has had a materialimpact on its operations. However, the Company does not expect the COVID-19pandemic and its related economic impact to affect its liquidity position orits ongoing ability to meet the covenants in its debt instruments.Stock Repurchase ProgramAs previously disclosed, the CompanyBoard of Directors authorized aprogram to repurchase up to $1.5 billion of its outstanding shares of commonstock. The Company will determine the timing and the amount of any repurchasesbased on its evaluation of market conditions, share price, and other factors.The stock repurchase program has no pre-established closing date and may besuspended or discontinued at any time. On September 15, 2020, the Companyannounced that it planned to commence a Dutch auctiontenderoffer to purchase up to $250 million of common stock. On October 21, 2020, theCompany announced the final results of the tender offer and repurchasedapproximately 10.8 million shares for $251 million. As of October 30, 2020,the Company had $135 million available under its stock repurchaseauthorization.Please see the CompanyForm 10-Q for the period ended September 30, 2020for further details regarding the above matters.Description of Non-GAAP MeasuresThe Company defines Adjusted Operating Income (Loss), which is a non-GAAPfinancial measure, as operating income (loss) before depreciation andamortization, cloud computing amortization, share-based compensation expenseor benefit, impairment charges (including gains or losses on sales ordispositions of businesses), restructuring and other related charges, andincluding the Companyproportionate share of adjusted operating income(loss) from majority owned equity method investees. Because it is based uponoperating income (loss), Adjusted Operating Income (Loss) also excludesinterest expense (including cash interest expense) and other non-operatingincome and expense items. The Company believes that the exclusion ofshare-based compensation expense or benefit allows investors to better trackthe performance of the various operating units of the business without regardto the effect of the settlement of an obligation that is not expected to bemade in cash.The Company believes that Adjusted Operating Income (Loss) is an appropriatemeasure for evaluating the operating performance of the business segments andthe Company on a consolidated basis. Adjusted Operating Income (Loss) andsimilar measures with similar titles are common performance measures used byinvestors, analysts and peers to compare performance in the industry.Internally, the Company uses net revenues and Adjusted Operating Income (Loss)measures as the most important indicators of its business performance, andevaluates managementeffectiveness with specific reference to theseindicators. Adjusted Operating Income (Loss) should be viewed as a supplementto and not a substitute for operating income (loss), net income (loss), andother measures of performance presented in accordance with U.S. generallyaccepted accounting principles (\"GAAP\"). Since Adjusted Operating Income(Loss) is not a measure of performance calculated in accordance with GAAP,this measure may not be comparable to similar measures with similar titlesused by other companies. For a reconciliation of operating income (loss) toAdjusted Operating Income (Loss), please see page 8 of this release.The Company defines Free Cash Flow, which is a non-GAAP financial measure, asnet cash provided by operating activities less capital expenditures and cashdistributions to noncontrolling interests, all of which are reported in ourConsolidated Statement of Cash Flows. The Company believes the most comparableGAAP financial measure of its liquidity is net cash provided by operatingactivities. The Company believes that Free Cash Flow is useful as an indicatorof its overall liquidity, as the amount of Free Cash Flow generated in anyperiod is representative of cash that is available for debt repayment,investment, and other discretionary and non-discretionary cash uses. TheCompany also believes that Free Cash Flow is one of several benchmarks used byanalysts and investors who follow the industry for comparison of its liquiditywith other companies in the industry, although the Companymeasure of FreeCash Flow may not be directly comparable to similar measures reported by othercompanies. For a reconciliation of net cash provided by operating activitiesto Free Cash Flow, please see page 9 of this release.The Company defines Adjusted Earnings per Diluted Share (EPSis a non-GAAP financial measure, as earnings per diluted share excludingthe following items: amortization of acquisition-related intangible assets;impairment charges (including gains or losses on sales or dispositions ofbusinesses); non-cash impairments of goodwill, intangible and fixed assets;restructuring and other related charges; and gains and losses related to theextinguishment of debt; as well as the impact of taxes on the aforementioneditems. The Company believes the most comparable GAAP financial measure isearnings per diluted share. The Company believes that Adjusted EPS is one ofseveral benchmarks used by analysts and investors who follow the industry forcomparison of its performance with other companies in the industry, althoughthe Companymeasure of Adjusted EPS may not be directly comparable tosimilar measures reported by other companies. For a reconciliation of earningsper diluted share to Adjusted EPS, please see pages 10-11 of this release.Forward-Looking StatementsThis earnings release may contain statements that constitute forward-lookingstatements within the meaning of the Private Securities Litigation Reform Actof 1995. These statements are based on management's current expectations andare subject to uncertainty and changes in circumstances. Investors arecautioned that any such forward-looking statements are not guarantees offuture performance or results and involve risks and uncertainties, and thatactual results or developments may differ materially from those in theforward-looking statements as a result of various factors, including financialcommunity and rating agency perceptions of the Company and its business,operations, financial condition and the industries in which it operates andthe factors described in the Companyfilings with the Securities andExchange Commission, including the sections entitled \"Risk Factors\" and\"ManagementDiscussion and Analysis of Financial Condition and Results ofOperations\" contained therein. The Company disclaims any obligation to updateany forward-looking statements contained herein.Conference Call InformationAMC Networks will host a conference call today at 8:30 a.m. ET to discuss itsthird quarter 2020 results. To listen to the call, visit<Origin Href=\\\"Link\\\">http://www.amcnetworks.com</Origin> or dial 877-347-9170, using the following passcode:1572997.About AMC Networks Inc.AMC Networks is a global entertainment company known for deliveringhigh-quality content to audiences and a valuable platform to distributors andadvertisers. The Company, which operates several of the most recognizablebrands in entertainment, manages its business through two operating segments:(i) National Networks, which principally includes AMC, BBC AMERICA, IFC,SundanceTV and WE tv; and AMC Studios, the Companyproduction business;and (ii) International and Other, which principally includes AMC NetworksInternational, the Companyinternational programming business; AMCNetworks SVOD, the Company's subscription streaming services business, whichprincipally includes AMC+, Acorn TV, Shudder, Sundance Now and UMC; Levity,the Companyproduction services and comedy venues business; and IFC Films,the Company's independent film distribution business. For more information onAMC Networks, please visit the Companywebsite at <Origin Href=\\\"Link\\\">www.amcnetworks.com</Origin>. Contacts                                                       Investor Relations           Corporate Communications          Seth Zaslow (646) 273-3766   Georgia Juvelis (917) 542-6390    seth.zaslow@amcnetworks.com  georgia.juvelis@amcnetworks.com                                                                 AMC NETWORKS INC.  CONSOLIDATED STATEMENTS OF INCOME  (In thousands, except per share amounts)  (unaudited)                                                                     Three Months Ended September 30,                       Nine Months Ended September 30,                                                                                                      2020                          2019                     2020                            2019                                                                                                                                                                                                    Revenues, net                                                       $      654,015                $      718,597           $      2,034,681                $      2,275,117                                                                                                                                                                                               Operating expenses:                                                                                                                                                                  Technical and operating (excluding depreciation and amortization)          333,816                       354,992                  960,379                         1,080,763          Selling, general and administrative                                        148,769                       159,357                  488,581                         505,233            Depreciation and amortization                                              27,547                        25,619                   80,182                          75,568             Impairment charges                                                         -                             -                        130,411                         -                  Restructuring and other related charges                                    4,406                         10,191                   13,879                          29,995                                                                                        514,538                       550,159                  1,673,432                       1,691,559                                                                                                                                                                                                                                                                                                                                                                                    Operating income                                                           139,477                       168,438                  361,249                         583,558                                                                                                                                                                                                 Other income (expense):                                                                                                                                                              Interest expense                                                           (33,418  )                    (39,621  )               (105,283   )                    (118,982   )       Interest income                                                            2,994                         4,626                    11,276                          13,571             Loss on extinguishment of debt                                             -                             -                        (2,908     )                    -                  Miscellaneous, net                                                         11,138                        (1,490   )               (10,088    )                    (16,972    )                                                                                  (19,286  )                    (36,485  )               (107,003   )                    (122,383   )                                                                                                                                                                                            Income from operations before income taxes                                 120,191                       131,953                  254,246                         461,175            Income tax expense                                                         (52,195  )                    (8,727   )               (95,490    )                    (53,807    )       Net income including noncontrolling interests                              67,996                        123,226                  158,756                         407,368            Net income attributable to noncontrolling interests                        (6,356   )                    (6,303   )               (13,488    )                    (18,305    )       Net income attributable to AMC Networksstockholders               $      61,640                 $      116,923           $      145,268                  $      389,063                                                                                                                                                                                                 Net income per share attributable to AMC Networksstockholders:                                                                                                                     Basic                                                               $      1.18                   $      2.09              $      2.72                     $      6.91               Diluted                                                             $      1.17                   $      2.07              $      2.69                     $      6.80                                                                                                                                                                                                    Weighted average common shares:                                                                                                                                                      Basic                                                                      52,346                        55,847                   53,374                          56,339             Diluted                                                                    52,904                        56,605                   53,917                          57,218                                                                                                                                                                                                 AMC NETWORKS INC.  SUPPLEMENTAL FINANCIAL DATA  (Dollars in thousands)  (Unaudited)                                          Three Months Ended September 30, 2020                                                                                                                           National Networks          International and Other            Inter-segment eliminations            Consolidated                                                                                                                                                                       Operating income (loss)                  $          129,842         $         11,198                   $          (1,563     )               $        139,477   Share-based compensation expense                    9,922                     2,472                               -                                   12,394    Depreciation and amortization                       13,422                    14,125                              -                                   27,547    Impairment charges                                  -                         -                                   -                                   -         Restructuring and other related charges             5,991                     (1,585    )                         -                                   4,406     Majority owned equity investees AOI                 -                         1,667                               -                                   1,667     Adjusted operating income (loss)         $          159,177         $         27,877                   $          (1,563     )               $        185,491                                                                                                                                                                                                            Three Months Ended September 30, 2019                                                                                                                           National Networks          International and Other            Inter-segment eliminations            Consolidated                                                                                                                                                                       Operating income (loss)                  $          182,479         $         (11,501   )              $          (2,540     )               $        168,438   Share-based compensation expense                    11,684                    2,157                               -                                   13,841    Depreciation and amortization                       8,048                     17,571                              -                                   25,619    Impairment charges                                  -                         -                                   -                                   -         Restructuring and other related charges             6,199                     3,992                               -                                   10,191    Majority owned equity investees AOI                 -                         1,246                               -                                   1,246     Adjusted operating income (loss)         $          208,410         $         13,465                   $          (2,540     )               $        219,335                                                                                                                                                                                                            Nine Months Ended September 30, 2020                                                                                                                            National Networks          International and Other            Inter-segment eliminations            Consolidated                                                                                                                                                                       Operating income (loss)                  $          512,598         $         (147,226  )              $          (4,123     )               $        361,249   Share-based compensation expense                    34,754                    8,387                               -                                   43,141    Depreciation and amortization                       30,633                    49,549                              -                                   80,182    Impairment charges                                  -                         130,411                             -                                   130,411   Restructuring and other related charges             8,714                     5,165                               -                                   13,879    Majority owned equity investees AOI                 -                         4,361                               -                                   4,361     Adjusted operating income (loss)         $          586,699         $         50,647                   $          (4,123     )               $        633,223                                                                                                                                                                                                            Nine Months Ended September 30, 2019                                                                                                                            National Networks          International and Other            Inter-segment eliminations            Consolidated                                                                                                                                                                       Operating income (loss)                  $          648,180         $         (52,532   )              $          (12,090    )               $        583,558   Share-based compensation expense                    41,774                    8,691                               -                                   50,465    Depreciation and amortization                       24,839                    50,729                              -                                   75,568    Impairment charges                                  -                         -                                   -                                   -         Restructuring and other related charges             6,776                     23,915                              (696       )                        29,995    Majority owned equity investees AOI                 -                         4,434                               -                                   4,434     Adjusted operating income (loss)         $          721,569         $         35,237                   $          (12,786    )               $        744,020                                                                                                                                                                  AMC NETWORKS INC.  SUPPLEMENTAL FINANCIAL DATA  (In thousands)  (Unaudited) Capitalization                           September 30, 2020                                                                                                                 Cash and cash equivalents            $                     1,071,860              Credit facility debt ((a))               $                     693,750                Senior notes ((a))                                             2,200,000              Other debt                                                     1,000                  Total debt                               $                     2,894,750                                                                                                    Net debt                                 $                     1,822,890                                                                                                    Finance leases                                                 32,025                 Net debt and finance leases              $                     1,854,915                                                                                                                                             Twelve Months Ended September 30, 2020       Operating Income (GAAP)                  $                     402,968                Share-based compensation expense                               56,809                 Depreciation and amortization                                  105,712                Impairment charges                                             237,014                Restructuring and other related charges                        24,798                 Majority owned equity investees AOI                            5,892                  Adjusted Operating Income (Non-GAAP)     $                     833,193                                                                                                      Leverage ratio ((b))                     2.2 x                                        (a)  Represents the aggregate principal amount of the debt.                                                                                                                                                                                                                                             (b)  Represents net debt and finance leases divided by Adjusted Operating Income for the twelve months ended September 30, 2020. This ratio differs from the calculation contained in the Company's credit facility. No adjustments have been made for consolidated entities that are not 100% owned.                                                                                                                                                                                                                                                                                                            Free Cash Flow                                   Nine Months Ended September 30,                                                                               2020                          2019             Net cash provided by operating activities        $      644,087                $      400,397          Less: capital expenditures                              (34,990  )                    (69,096  )       Less: distributions to noncontrolling interests         (13,955  )                    (13,545  )       Free cash flow                                   $      595,142                $      317,756                                                                                                                Adjusted Earnings Per Diluted Share                                                        Three Months Ended September 30, 2020                                                                                                                                                                                                                                                                                                        Income from operations before income taxes           Income tax expense              Net income attributable to noncontrolling interests              Net income attributable to AMC Networksstockholders            Diluted EPS attributable to AMC Networksstockholders                                                                                                                                                                                                                                                                                                                                                      Reported Results (GAAP)                                $                       120,191                      $        (52,195  )             $                   (6,356              )                        $                             61,640                             $                              1.17                            Adjustments:                                                                                                                                                                                                                                                                                                                                 Amortization of acquisition-related intangible assets                          9,548                                 (1,464   )                                 (3,027              )                                                      5,057                                                             0.09                            Impairment charges                                                             -                                     -                                          -                                                                          -                                                                 -                               Restructuring and other related charges                                        4,406                                 (1,051   )                                 -                                                                          3,355                                                             0.06                            Loss on extinguishment of debt                                                 -                                     -                                          -                                                                          -                                                                 -                               Adjusted Results (Non-GAAP)                            $                       134,145                      $        (54,710  )             $                   (9,383              )                        $                             70,052                             $                              1.32                                                                                   Three Months Ended September 30, 2019                                                                                                                                                                                                                                                                                                        Income from operations before income taxes           Income tax expense              Net income attributable to noncontrolling interests              Net income attributable to AMC Networksstockholders            Diluted EPS attributable to AMC Networksstockholders                                                                                                                                                                                                                                                                                                                                                      Reported Results (GAAP)                                $                       131,953                      $        (8,727   )             $                   (6,303              )                        $                             116,923                            $                              2.07                            Adjustments:                                                                                                                                                                                                                                                                                                                                 Amortization of acquisition-related intangible assets                          11,943                                (1,877   )                                 (3,027              )                                                      7,039                                                             0.12                            Impairment charges                                                             -                                     -                                          -                                                                          -                                                                 -                               Restructuring and other related charges                                        10,191                                (2,318   )                                 (18                 )                                                      7,855                                                             0.14                            Loss on extinguishment of debt                                                 -                                     -                                          -                                                                          -                                                                 -                               Adjusted Results (Non-GAAP)                            $                       154,087                      $        (12,922  )             $                   (9,348              )                        $                             131,817                            $                              2.33                                                                                   Nine Months Ended September 30, 2020                                                                                                                                                                                                                                                                                                          Income from operations before income taxes           Income tax expense               Net income attributable to noncontrolling interests              Net income attributable to AMC Networksstockholders            Diluted EPS attributable to AMC Networksstockholders                                                                                                                                                                                                                                                                                                                                                       Reported Results (GAAP)                                $                       254,246                      $        (95,490   )             $                   (13,488             )                        $                             145,268                            $                              2.69                            Adjustments:                                                                                                                                                                                                                                                                                                                                  Amortization of acquisition-related intangible assets                          32,642                                (5,413    )                                 (9,081              )                                                      18,148                                                            0.34                            Impairment charges                                                             130,411                               (27,984   )                                 -                                                                          102,427                                                           1.90                            Restructuring and other related charges                                        13,879                                (3,349    )                                 13                                                                         10,543                                                            0.20                            Loss on extinguishment of debt                                                 2,908                                 (733      )                                 -                                                                          2,175                                                             0.04                            Adjusted Results (Non-GAAP)                            $                       434,086                      $        (132,969  )             $                   (22,556             )                        $                             278,561                            $                              5.17                                                                                   Nine Months Ended September 30, 2019                                                                                                                                                                                                                                                                                                         Income from operations before income taxes           Income tax expense              Net income attributable to noncontrolling interests              Net income attributable to AMC Networksstockholders            Diluted EPS attributable to AMC Networksstockholders                                                                                                                                                                                                                                                                                                                                                      Reported Results (GAAP)                                $                       461,175                      $        (53,807  )             $                   (18,305             )                        $                             389,063                            $                              6.80                            Adjustments:                                                                                                                                                                                                                                                                                                                                 Amortization of acquisition-related intangible assets                          34,235                                (5,721   )                                 (7,561              )                                                      20,953                                                            0.37                            Impairment charges                                                             -                                     -                                          -                                                                          -                                                                 -                               Restructuring and other related charges                                        29,995                                (6,619   )                                 (114                )                                                      23,262                                                            0.41                            Loss on extinguishment of debt                                                 -                                     -                                          -                                                                          -                                                                 -                               Adjusted Results (Non-GAAP)                            $                       525,405                      $        (66,147  )             $                   (25,980             )                        $                             433,278                            $                              7.57                           (<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/f40c6e66-3876-4756-a2bd-10c4c69f9dcf</Origin>)GlobeNewswire, Inc. 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201021:nGNX2Vqnkk")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-10-21T12:30:20-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "AMC Networks to Report Third Quarter 2020 Results");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201021:nGNX2Vqnkk</Origin>NEW YORK, Oct. 21, 2020 (GLOBE NEWSWIRE) -- AMC Networks Inc. (NASDAQ: AMCX)will host a conference call to discuss results for the third quarter 2020 onMonday, November 2, 2020 at 8:30 a.m. Eastern Time. AMC Networks will issue apress release reporting its results prior to the market opening.The conference call will be webcast live via the companywebsite at<Origin Href=\\\"Link\\\">www.amcnetworks.com</Origin> under the heading Those partiesinterested in participating via telephone please dial 877-347-9170 with theconference ID number 1572997 approximately 10 minutes prior to the call.For those who are unable to participate on the conference call, you may accessa recording of the call by dialing 855-859-2056 (conference ID number1572997). The call replay will be available from 11:30 a.m. on Monday,November 2, 2020 until 11:59 p.m. on Monday, November 9, 2020.Internet replays will also be available on the AMC Networks website beginningapproximately two hours after the call ends.About AMC Networks Inc.  Dedicated to creating and distributing bold and inventive stories fueled bythe artistic vision of dynamic storytellers, AMC Networks owns and operatesseveral of the most popular and award-winning brands in television and film.Cable television networks AMC(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=nMRtjor8pDuKKrNj_xqGw4UWp1IolOnvBiV9TXULEZFDt3nNziGVFvPHGzlgYu3V</Origin>), BBCAMERICA(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=KMSLHnSF-O9iSacIkZDmmmbuo5AeZqg9f62ED4boUfIe5iZ3u26odnHZUumeSKz6Kh5ccbOEjzn50CPCxSbY3w==</Origin>)(through a joint venture with BBC Studios), IFC(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=lM84Mok6WgZ_saa9iUmnt7_B1TSPcTUNyWMkQjuAdobQBej2fAR8jAltHkE-tXEg</Origin>), SundanceTV(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=w1UdciQUKrtq4yemG8zxK9aCTLa_un1EuJGTysuesGjaMHHWU8UDGHB1dPdqaNX2hzrYkM44v9CAtLgHK4woGw==</Origin>),and WE tv(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=hqtRuzo7roHtd3GjJ6Q3VDmpf9_gRI6TObmPBwXUktauO0kbqRojqRhlWaiEgHNa</Origin>);independent film production and distribution division IFC Films(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=xLVhFgeykKbunXoYjXzX5UOmxV1VPQOTCQ_MYy40IGmM69IEpJjc2An83vjdAkHTNrGZjEVH3mjxSuBxsDGVtA==</Origin>);and premium streaming video services Acorn TV, Shudder, Sundance Now,(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=w1UdciQUKrtq4yemG8zxK2KJk3tLxzptVeh9ylfJUcZgmYLVRIhrEyAz_fm0kU9n_lO3UdJ-dL0w5bkK_QWmYg==</Origin>) UMC(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=Cf3CqHbO1XQh283dFEziY8H5HRRm9DC4bVS_JzepF6zeoaqgEc1UZmYQtw_gyNr7</Origin>) and AMCPremiere(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=Zy2gw480QIVGGxWl_xQWWpdcAZpF4kTiql6MMV4QwsyN4Rz5PGJgvLPgQ9I0wrUXoJ6bak7ZB8zM_jfa447Wvwn8Y69tdGY4g7o9XZuSM_E5AkDRkC7idDMPrKwagn6CbynPgV9RaEIyQz1tdRZgUpGF6Kx9fjD5s8nSvGfIAlK8DzdcbDOZSIMlLo_GfrID</Origin>),the offering that gives subscribers commercial-free access to AMC shows,produce and deliver distinctive, compelling and culturally relevant contentthat engages audiences across multiple platforms. The Company alsooperates AMC Studios(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=YCZccZ7L_BTApvV_-p7ycnT8UqhzSPTsQEjdEg6J5qugFPS-hs4t8ToED-TSXJo6FwGR5nVrp1lZaWO4uhiZvCCWHmadCmKW5hLVgKWpdeI=</Origin>),its television production and worldwide content distribution division;and AMC Networks International(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=Q0TDfFyVIsiAoHPfjDG42UFSQu5I0NJ6cS9D_g3qxkRtSnNBqffa5hh06WylyTQCBC_jtXB_jGyg_Jp70Vj3-1lrJ6Y9ckauuuvWvQ3EvUBAd5nOUAk9NOrM1TBsMrUzKw0pNVSZ1B9BYaB19gDeMg==</Origin>),its international programming business. For more information, please visit<Origin Href=\\\"Link\\\">www.amcnetworks.com</Origin>.Contacts:  Seth Zaslow  Investor Relations  646-273-3766  seth.zaslow@amcnetworks.comGeorgia Juvelis  Corporate Communications  917-542-6390  georgia.juvelis@amcnetworks.com(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/f40c6e66-3876-4756-a2bd-10c4c69f9dcf</Origin>)GlobeNewswire, Inc. 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201021:nFWN2HB15K")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-10-21T11:08:24-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-Amc Networks Announces Final Results Of Modified Dutch Auction Tender Offer");
			hm1.put("TE",
					"<p>    Oct 21 (Reuters) - AMC Networks Inc  <Origin Href=\\\"QuoteRef\\\">AMCX.O</Origin> :    * AMC NETWORKS ANNOUNCES FINAL RESULTS OF MODIFIED DUTCHAUCTIONTENDER OFFERSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:nGNXbh6YnB</Origin> Further company coverage:  <Origin Href=\\\"NewsSearch\\\">AMCX.O</Origin>  ((Reuters.Briefs@thomsonreuters.com;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL1N2IJ26V")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-12-03T12:00:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "FOCUS-North American farmers profit as consumers pressure food business to go green");
			hm1.put("TE",
					"<p>    By Karl Plume and Rod Nickel\n    CHICAGO/WINNIPEG, Manitoba, Dec 3 (Reuters) - Beer made from\nrice grown with less water, rye planted in the off-season and\nthe sale of carbon credits to tech firms are just a few of the\nchanges North American farmers are making as the food industry\nstrives to go green.\n    The changes are enabling some farmers to earn extra money\nfrom industry giants like Cargill, Nutrien and Anheuser-Busch.\nConsumers are pressuring food producers to support farms that\nuse less water and fertilizer, reduce greenhouse gas emissions\nand use more natural techniques to maintain soil quality. \n    Investments in sustainability remain a tiny part of overall\nspending by the agriculture sector, which enjoyed healthy\nprofits in 2020. They may help to head off more costly\nregulations down the road now that Democratic climate advocate\nJoe Biden was elected U.S. president.\n    Some companies, like farm retailer and fertilizer producer\nNutrien  <Origin Href=\\\"QuoteRef\\\">NTR.TO</Origin> , are also opening new revenue potential for\nfarmers by monetizing the carbon their fields soak up. The\ncompanies say technology is improving measurement and tracking\nof carbon capture, although some environmental activists\nquestion the benefit of such programs and how sequestered\ngreenhouse gas volumes can be verified. \n    Sustainable techniques farmers are adopting include\nrefraining from tilling soil at times to preserve carbon. Some\nare adding an off-season cover crop of rye or grass to restore\nsoil nutrients instead of applying heavy fertilizer loads over\nthe winter that can contaminate local water supplies. \n    A study conducted by agriculture technology company Indigo\nAg estimated that if U.S. corn, soy and wheat farmers employed\nno-till and cover crops on 15% of fields, they would generate an\nadditional $600 million by reducing costs, bolstering soil\nproductivity or selling carbon credits.\n    Indigo has a partnership with brewer Anheuser-Busch Inbev NV\n <Origin Href=\\\"QuoteRef\\\">ABI.BR</Origin> , which plans to buy 2.6 million bushels of rice this\nyear grown with less water and nitrogen fertilizer than\nconventional rice. Anheuser-Busch said that is up from 2.2\nmillion bushels last year and accounts for 10% of its U.S. rice\nsupplies. \n    Bill Jones, the brewer's manager of raw materials, said\nfarmers voluntarily growing rice with a lower environmental\nimpact along the sensitive Mississippi River would be less\ndisruptive to supplies than having local authorities require\nsuch practices by legislating changes to water and nitrogen use.\n    \"We look at supply chain security. I see this gaining\ntraction,\" he said, noting that Minnesota and other U.S. states\nand conservation districts worried about polluting the\nMississippi are already introducing limits on how much manure\nfarmers can spread on fields.\n    Arkansas farmer Carson Stewart used the program for the\nfirst time this year, earmarking his entire 340-acre rice crop\nto Anheuser-Busch. Depending on milling quality, his rice may\nearn up to $1.50 a bushel more than conventional rice, a premium\nof about 27%, he said. \n    \n    10 MILLION ACRE SHIFT \n    While companies expect Washington and Ottawa to grow more\ncommitted to funding and regulating sustainable farming,\nindustry sources and activists said widespread adoption remains\nfar off. \n    \"They come with high up-front costs,\" said Giana Amador,\nmanaging director at climate-focused NGO Carbon180. \"We're\nseeing a huge differentiation in quality among all these\ncorporate commitments.\"\n    In September, privately held Cargill Inc  <Origin Href=\\\"NewsSearch\\\">CARG.UL</Origin>  said it\nwould help North American farmers shift 10 million acres to\nregenerative practices during the next 10 years by offering them\nfinancial support and training.\n    Pushed by demand for greener foods from food companies that\nbuy its products, Cargill has already signed up 750 farmers to\ngreen programs, representing 300,000 acres, said Ryan Sirolli,\nCargill's director of row crop sustainability. \n    With projects like one that pays Iowa farmers to leave soils\nuntilled or to create field buffers to prevent fertilizer\nrunoff, Cargill hopes to cut 30% of its supply chain greenhouse\ngas emissions over the next decade.\n    \"We've done a lot to stop soil erosion. And we've had a\nreduction of 538 tons of CO2, which is the equivalent of taking\n104 passenger cars off the road,\" said Iowa farmer Lance\nLillibridge, who estimates he will earn about $37 an acre in a\nCargill pilot project this year.\n    Environmental groups and consumer activists are sceptical\nabout such corporate sustainability pledges, noting that Cargill\nhas not made good on its promise to eliminate deforestation from\nsupply chains by 2020.\n    As more premium-paying buyers emerge, more farmers will be\nenticed into sustainable growing, said Devin Lammers, CEO of\nGradable. The unit of input dealer Farmers Business Network\nmatches farmers using sustainable practices with buyers such as\nUnilever  <Origin Href=\\\"QuoteRef\\\">ULVR.L</Origin> , Tyson Foods  <Origin Href=\\\"QuoteRef\\\">TSN.N</Origin>  and ethanol producer POET\n <Origin Href=\\\"QuoteRef\\\">POET.SJ</Origin> . \n   \n    CARBON CREDITS\n    Some farmers are making money by verifying the amount of\nclimate-warming emissions their fields soak up and selling\ncarbon credits to polluting companies seeking to reduce their\nnet emissions. Agribusiness companies call that a double win for\nfarmers as their fields become healthier and they earn extra\ncash.\n    This week, Saskatchewan-based Nutrien said it was launching\na sustainable agriculture program on 100,000 acres in the United\nStates and Canada, with expansion planned later in South America\nand Australia.\n    Nutrien Chief Executive Chuck Magro estimated that farmers\nwill earn an additional $50 per acre in profits under the\nprogram - $20 per acre for carbon credits and $30 per acre worth\nof higher crop yields.\n    The announcement followed Nutrien's 2018 purchase of digital\nfarming company Agrible, which helps farmers log reduced\nemissions and water use. Magro said in an interview that the aim\nis to enable farmers to use that data to sell carbon credits. He\nnoted that previous efforts produced meagre returns that were\nnot worth the effort for farmers who had to wade through\nhundreds of pages of documents.\n    Agriculture accounts for 3% of the global carbon credit\nmarket, but that looks to grow to 30% by 2050, Magro said.    \n    \"We see carbon being the next big agricultural revolution,\"\nhe said.\n    Matt Coutts, chief investment officer of 100,000-acre Coutts\nAgro in Saskatchewan, plans to sell carbon credits through\nNutrien for up to 10,000 acres per year of canola, lentils and\nspring wheat. He expects they could eventually generate at least\nC$75,000 in annual additional revenue.\n    Ohio-based start-up Locus Agricultural Solutions helped Iowa\nfarmer Kelly Garrett create 22,400 tonnes in carbon credits by\nverifying his fields locked in about 1.4 tonnes per acre from\n2015 to 2019. Garrett received a check for 5,000 of those\ncredits in November, after e-commerce platform Shopify  <Origin Href=\\\"QuoteRef\\\">SHOP.TO</Origin> \nbought them on the carbon trading marketplace Nori for $75,000.\n    \"The ability to sell our carbon credits through the Nori\nsystem and help the rest of the world be more green is a\nwonderful benefit to our economy and our finances,\" Garrett\nsaid.\n    Still, Nori noted that Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin>  passed on a\ndeal to buy most of Garrett's remaining credits because they\nwere not verified by on-farm soil tests. Nori deems individual\nsoil tests too costly, and instead verifies its credits based on\nsoil type, crops planted and other data, said Alexsandra Guerra,\nthe company's director of corporate development.\n    Microsoft declined to comment.\n    Few North American farmers have gone through the vetting\nprocess Garrett underwent, which also limits supplies of the\nhigh-quality carbon credits that some buyers seek.\n    Some critics say carbon saved from no-till farming can\neasily escape if the soil is tilled again. \n    \"Statements that soils can sequester all of our emissions\nand more are overstated ... There's no way we could make that\nshift fast enough to address the climate crisis,\" said Tara\nRitter, senior program associate with the Institute for\nAgriculture and Trade Policy.\n    \n    PAYING UP FRONT\n    Despite those doubts, food companies are banking more on\ncarbon capture and regenerative agriculture. General Mills\n <Origin Href=\\\"QuoteRef\\\">GIS.N</Origin>  offers farmers technical advice while other companies\npay growers up front to adopt greener practices.\n    PepsiCo  <Origin Href=\\\"QuoteRef\\\">PEP.O</Origin> , maker of Quaker Oats and Frito-Lay chips,\npays farmers $10 an acre to plant cover crops over winter, which\ncan reduce erosion and control weeds and insects.\n    This helps PepsiCo meet its sustainability targets and\nsecure its food supply, said director of sustainable agriculture\nMargaret Henry. PepsiCo subsidized cover crops such as rye and\nradish last year across 50,000 Midwest acres and plans to grow\nthe program further.\n    Henry pointed to an added benefit: Cover crops soak up\nexcess moisture, making many fields ready for spring planting\ntwo weeks earlier than fields that lay fallow.\n    \"We want this to be a win win for the long term,\" she said. \n\n (Reporting by Karl Plume in Chicago and Rod Nickel in Winnipeg,\nManitoba; Editing by Caroline Stauffer)\n ((rod.nickel@tr.com; Twitter: @RodNickel_Rtrs;\n1-204-230-6043;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nB8N2FR00N")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-12-03T16:03:29-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-Microsoft Unveils A New Data Governance Solution, Azure Purview");
			hm1.put("TE",
					"<p>    Dec 3 (Reuters) - Microsoft:\n    * MICROSOFT SAYS THE LATEST VERSION OF AZURE SYNAPSE IS GENERALLY AVAILABLE,\nAND\nUNVEILS A NEW DATA GOVERNANCE SOLUTION, AZURE PURVIEW\n    * MICROSOFT SAYS IN THE COMING MONTHS, CO PLANS TO DEPLOY FEDEX SURROUND TO\nSUPPORT THE DISTRIBUTION OF COVID-19 VACCINES\n\nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">MSFT.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL1N2IJ1I8")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-12-03T16:00:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Microsoft aims to help businesses get handle on data with new tool");
			hm1.put("TE",
					"<p>    By Stephen Nellis\n    Dec 3 (Reuters) - Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin>  on Thursday\nannounced a new cloud-based tool designed to help corporate\ncustomers understand where data is scattered throughout their\noperations and whether they are in compliance with data privacy\nregulations.\n    Once known for its Windows operating system and applications\nsuch as Office, Microsoft has built a large business in cloud\ncomputing, helping store and process huge amounts of data for\ncorporate customers. \n    Last year, it introduced a tool called Azure Synapse that is\nbeing used by companies such as FedEx Corp  <Origin Href=\\\"QuoteRef\\\">FDX.N</Origin>  to analyze\nthe flow of its 16 million daily packages.\n    But for large companies, stores of data have become so\nlarge, and distributed across so many countries, that Microsoft\nis rolling out a tool called Azure Purview to help companies\nbetter understand precisely what information they have and where\nit resides. \n    In particular, the tool is designed to help data privacy and\nrisk management officials ensure their companies are in\ncompliance with rules such as the European Union's General Data\nProtection Regulation, John \"JG\" Chirapurath, vice president of\nAzure data, artificial intelligence and edge, told Reuters in an\ninterview.    \n    The new tool uses artificial intelligence to detect\nsensitive or regulated data and can automatically mask it out,\nfor example by redacting data on European customers from a sales\nreport to U.S. employees who are not authorized to access it.\n    \"It's one thing to generate insights from data, but it's\nanother thing to ask questions about the data itself. Can we use\nthis data? Are we being responsible with the fair use of this\ndata?\" Chirapurath said. \"These might seem like esoteric terms,\nbut they are vital to how we run modern businesses. You have to\nbe able to trust your data.\"\n    Microsoft said on Thursday the service was being used by a\nhandful of customers, and Chirapurath said it was expected to\nbecome generally available \"shortly.\"\n\n (Reporting by Stephen Nellis in San Francisco; Editing by Mark\nPotter)\n ((Stephen.Nellis@thomsonreuters.com; (415) 344-4934;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL2N2GK17L")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-12-03T12:00:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "FOCUS-North American farmers profit as consumers pressure food business to go green");
			hm1.put("TE",
					"<p>    By Karl Plume and Rod Nickel\n    CHICAGO/WINNIPEG, Manitoba, Dec 3 (Reuters) - Beer made from\nrice grown with less water, rye planted in the off-season and\nthe sale of carbon credits to tech firms are just a few of the\nchanges North American farmers are making as the food industry\nstrives to go green.\n    The changes are enabling some farmers to earn extra money\nfrom industry giants like Cargill, Nutrien and Anheuser-Busch.\nConsumers are pressuring food producers to support farms that\nuse less water and fertilizer, reduce greenhouse gas emissions\nand use more natural techniques to maintain soil quality. \n    Investments in sustainability remain a tiny part of overall\nspending by the agriculture sector, which enjoyed healthy\nprofits in 2020. They may help to head off more costly\nregulations down the road now that Democratic climate advocate\nJoe Biden was elected U.S. president.\n    Some companies, like farm retailer and fertilizer producer\nNutrien  <Origin Href=\\\"QuoteRef\\\">NTR.TO</Origin> , are also opening new revenue potential for\nfarmers by monetizing the carbon their fields soak up. The\ncompanies say technology is improving measurement and tracking\nof carbon capture, although some environmental activists\nquestion the benefit of such programs and how sequestered\ngreenhouse gas volumes can be verified. \n    Sustainable techniques farmers are adopting include\nrefraining from tilling soil at times to preserve carbon. Some\nare adding an off-season cover crop of rye or grass to restore\nsoil nutrients instead of applying heavy fertilizer loads over\nthe winter that can contaminate local water supplies. \n    A study conducted by agriculture technology company Indigo\nAg estimated that if U.S. corn, soy and wheat farmers employed\nno-till and cover crops on 15% of fields, they would generate an\nadditional $600 million by reducing costs, bolstering soil\nproductivity or selling carbon credits.\n    Indigo has a partnership with brewer Anheuser-Busch Inbev NV\n <Origin Href=\\\"QuoteRef\\\">ABI.BR</Origin> , which plans to buy 2.6 million bushels of rice this\nyear grown with less water and nitrogen fertilizer than\nconventional rice. Anheuser-Busch said that is up from 2.2\nmillion bushels last year and accounts for 10% of its U.S. rice\nsupplies. \n    Bill Jones, the brewer's manager of raw materials, said\nfarmers voluntarily growing rice with a lower environmental\nimpact along the sensitive Mississippi River would be less\ndisruptive to supplies than having local authorities require\nsuch practices by legislating changes to water and nitrogen use.\n    \"We look at supply chain security. I see this gaining\ntraction,\" he said, noting that Minnesota and other U.S. states\nand conservation districts worried about polluting the\nMississippi are already introducing limits on how much manure\nfarmers can spread on fields.\n    Arkansas farmer Carson Stewart used the program for the\nfirst time this year, earmarking his entire 340-acre rice crop\nto Anheuser-Busch. Depending on milling quality, his rice may\nearn up to $1.50 a bushel more than conventional rice, a premium\nof about 27%, he said. \n    \n    10 MILLION ACRE SHIFT \n    While companies expect Washington and Ottawa to grow more\ncommitted to funding and regulating sustainable farming,\nindustry sources and activists said widespread adoption remains\nfar off. \n    \"They come with high up-front costs,\" said Giana Amador,\nmanaging director at climate-focused NGO Carbon180. \"We're\nseeing a huge differentiation in quality among all these\ncorporate commitments.\"\n    In September, privately held Cargill Inc  <Origin Href=\\\"NewsSearch\\\">CARG.UL</Origin>  said it\nwould help North American farmers shift 10 million acres to\nregenerative practices during the next 10 years by offering them\nfinancial support and training.\n    Pushed by demand for greener foods from food companies that\nbuy its products, Cargill has already signed up 750 farmers to\ngreen programs, representing 300,000 acres, said Ryan Sirolli,\nCargill's director of row crop sustainability. \n    With projects like one that pays Iowa farmers to leave soils\nuntilled or to create field buffers to prevent fertilizer\nrunoff, Cargill hopes to cut 30% of its supply chain greenhouse\ngas emissions over the next decade.\n    \"We've done a lot to stop soil erosion. And we've had a\nreduction of 538 tons of CO2, which is the equivalent of taking\n104 passenger cars off the road,\" said Iowa farmer Lance\nLillibridge, who estimates he will earn about $37 an acre in a\nCargill pilot project this year.\n    Environmental groups and consumer activists are sceptical\nabout such corporate sustainability pledges, noting that Cargill\nhas not made good on its promise to eliminate deforestation from\nsupply chains by 2020.\n    As more premium-paying buyers emerge, more farmers will be\nenticed into sustainable growing, said Devin Lammers, CEO of\nGradable. The unit of input dealer Farmers Business Network\nmatches farmers using sustainable practices with buyers such as\nUnilever  <Origin Href=\\\"QuoteRef\\\">ULVR.L</Origin> , Tyson Foods  <Origin Href=\\\"QuoteRef\\\">TSN.N</Origin>  and ethanol producer POET\n <Origin Href=\\\"QuoteRef\\\">POET.SJ</Origin> . \n   \n    CARBON CREDITS\n    Some farmers are making money by verifying the amount of\nclimate-warming emissions their fields soak up and selling\ncarbon credits to polluting companies seeking to reduce their\nnet emissions. Agribusiness companies call that a double win for\nfarmers as their fields become healthier and they earn extra\ncash.\n    This week, Saskatchewan-based Nutrien said it was launching\na sustainable agriculture program on 100,000 acres in the United\nStates and Canada, with expansion planned later in South America\nand Australia.\n    Nutrien Chief Executive Chuck Magro estimated that farmers\nwill earn an additional $50 per acre in profits under the\nprogram - $20 per acre for carbon credits and $30 per acre worth\nof higher crop yields.\n    The announcement followed Nutrien's 2018 purchase of digital\nfarming company Agrible, which helps farmers log reduced\nemissions and water use. Magro said in an interview that the aim\nis to enable farmers to use that data to sell carbon credits. He\nnoted that previous efforts produced meagre returns that were\nnot worth the effort for farmers who had to wade through\nhundreds of pages of documents.\n    Agriculture accounts for 3% of the global carbon credit\nmarket, but that looks to grow to 30% by 2050, Magro said.    \n    \"We see carbon being the next big agricultural revolution,\"\nhe said.\n    Matt Coutts, chief investment officer of 100,000-acre Coutts\nAgro in Saskatchewan, plans to sell carbon credits through\nNutrien for up to 10,000 acres per year of canola, lentils and\nspring wheat. He expects they could eventually generate at least\nC$75,000 in annual additional revenue.\n    Ohio-based start-up Locus Agricultural Solutions helped Iowa\nfarmer Kelly Garrett create 22,400 tonnes in carbon credits by\nverifying his fields locked in about 1.4 tonnes per acre from\n2015 to 2019. Garrett received a check for 5,000 of those\ncredits in November, after e-commerce platform Shopify  <Origin Href=\\\"QuoteRef\\\">SHOP.TO</Origin> \nbought them on the carbon trading marketplace Nori for $75,000.\n    \"The ability to sell our carbon credits through the Nori\nsystem and help the rest of the world be more green is a\nwonderful benefit to our economy and our finances,\" Garrett\nsaid.\n    Still, Nori noted that Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin>  passed on a\ndeal to buy most of Garrett's remaining credits because they\nwere not verified by on-farm soil tests. Nori deems individual\nsoil tests too costly, and instead verifies its credits based on\nsoil type, crops planted and other data, said Alexsandra Guerra,\nthe company's director of corporate development.\n    Microsoft declined to comment.\n    Few North American farmers have gone through the vetting\nprocess Garrett underwent, which also limits supplies of the\nhigh-quality carbon credits that some buyers seek.\n    Some critics say carbon saved from no-till farming can\neasily escape if the soil is tilled again. \n    \"Statements that soils can sequester all of our emissions\nand more are overstated ... There's no way we could make that\nshift fast enough to address the climate crisis,\" said Tara\nRitter, senior program associate with the Institute for\nAgriculture and Trade Policy.\n    \n    PAYING UP FRONT\n    Despite those doubts, food companies are banking more on\ncarbon capture and regenerative agriculture. General Mills\n <Origin Href=\\\"QuoteRef\\\">GIS.N</Origin>  offers farmers technical advice while other companies\npay growers up front to adopt greener practices.\n    PepsiCo  <Origin Href=\\\"QuoteRef\\\">PEP.O</Origin> , maker of Quaker Oats and Frito-Lay chips,\npays farmers $10 an acre to plant cover crops over winter, which\ncan reduce erosion and control weeds and insects.\n    This helps PepsiCo meet its sustainability targets and\nsecure its food supply, said director of sustainable agriculture\nMargaret Henry. PepsiCo subsidized cover crops such as rye and\nradish last year across 50,000 Midwest acres and plans to grow\nthe program further.\n    Henry pointed to an added benefit: Cover crops soak up\nexcess moisture, making many fields ready for spring planting\ntwo weeks earlier than fields that lay fallow.\n    \"We want this to be a win win for the long term,\" she said. \n\n (Reporting by Karl Plume in Chicago and Rod Nickel in Winnipeg,\nManitoba; Editing by Caroline Stauffer)\n ((rod.nickel@tr.com; Twitter: @RodNickel_Rtrs;\n1-204-230-6043;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201204:nL1N2IK01K")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-12-04T01:35:36-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "");
			hm1.put("TE", ">");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL1N2IJ323")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-10-21T11:08:24-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 1-LG Chem, SK Innovation spar over EV recalls in trade dispute");
			hm1.put("TE",
					"<p> (Adds LG's voluntary recall of its residential energy storage\nbatteries in the U.S.)\n    By David Shepardson and Hyunjoo Jin\n    WASHINGON/SEOUL, Dec 4 (Reuters) - South Korean battery\nmakers LG Chem  <Origin Href=\\\"QuoteRef\\\">051910.KS</Origin>  and SK Innovation  <Origin Href=\\\"QuoteRef\\\">096770.KS</Origin>  are\nsparring over whether the U.S. International Trade Commission\n(ITC) should consider recent electric vehicle recalls in a trade\nsecrets case. \n    LG Chem, an EV battery supplier for Tesla  <Origin Href=\\\"QuoteRef\\\">TSLA.O</Origin>  and\nGeneral Motors, filed its trade complaints against SK Innovation\nlast year in the United States. Alleging trade secret theft, LG\nseeks to block SK from producing battery cells in the United\nStates and from importing the components necessary to make them.\n    SK has denied wrongdoing.\n    Last month, SK asked the ITC to consider that \"LG Chem\nbatteries have been involved in a series of fires and\nexplosions, raising substantial public interest concerns.\"\n    In the request, SK said General Motors Co  <Origin Href=\\\"QuoteRef\\\">GM.N</Origin>  and Hyundai\nMotor Co  <Origin Href=\\\"QuoteRef\\\">005380.KS</Origin>  had each issued recent recalls for EVs with\nLG Chem batteries. The recalls \"raise questions of public safety\nand underscore the critical need for SK batteries in the United\nStates,\" the company said.\n    It added the fires are \"compelling grounds\" not to impede\nSK's ability to manufacture batteries at its U.S. plant.\n    LG Chem in a response filed Wednesday said the filing was\n\"untimely and irrelevant\" and should not factor in the ITC\nreview \"because the electric vehicle recalls cited by SKI do not\nimpact future-year battery models and do not impact EVs with\nbatteries manufactured in LG Chem’s Holland, Michigan plant.\"\n    GM said last month it was recalling 68,677 EVs worldwide for\nhigh-voltage batteries after five reported fires and two minor\ninjuries. In October, Hyundai recalled nearly 77,000 Kona EVs\nworldwide, saying possible defects in battery cells increased \nfire risks.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2GZ1QD</Origin> <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2HZ23N</Origin>\n    LG Chem, which spilt off its battery business renamed as LG\nEnergy Solution this week, also said on Friday that it is\nvoluntarily recalling its residential energy storage batteries\nin the United States over concerns of thermal issues after\nreceiving five related reports. \n    \"This is a free replacement of some of the batteries in\nresidential energy storage system products that preemptively\nprioritize customer safety. We are currently working with\nrelated companies to determine the cause,\" LG Energy Solution\nsaid in a statement to Reuters on Friday. \n    The ITC is expected to have a decision on the dispute\nbetween the two South Korean battery makers on Dec. 10. An\nadverse ruling could potentially cause setbacks for Volkswagen\n <Origin Href=\\\"QuoteRef\\\">VOWG_p.DE</Origin>  and Ford Motor  <Origin Href=\\\"QuoteRef\\\">F.N</Origin>  as they move to build new\nelectric vehicles using SK Innovation's batteries and\ncomponents.\n\n (Reporting by David Shepardson and Hyunjoo Jin; Additional\nreporting by Heekyong Yang; Editing by Tom Brown and Grant\nMcCool)\n ((David.Shepardson@thomsonreuters.com; 2028988324;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL1N2IJ2BL")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-12-03T21:46:21-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "LG Chem, SK Innovation spar over EV recalls in trade dispute");
			hm1.put("TE",
					"<p>    By David Shepardson and Hyunjoo Jin\n    WASHINGON/SEOUL, Dec 3 (Reuters) - South Korean battery\nmakers LG Chem  <Origin Href=\\\"QuoteRef\\\">051910.KS</Origin>  and SK Innovation  <Origin Href=\\\"QuoteRef\\\">096770.KS</Origin>  are\nsparring over whether the U.S. International Trade Commission\n(ITC) should consider recent electric vehicle recalls in a trade\nsecrets case. \n    LG Chem, an EV battery supplier for Tesla  <Origin Href=\\\"QuoteRef\\\">TSLA.O</Origin>  and\nGeneral Motors, filed its trade complaints against SK Innovation\nlast year in the United States. Alleging trade secret theft, LG\nseeks to block SK from producing battery cells in the United\nStates and from importing the components necessary to make them.\n    SK has denied wrongdoing.\n    Last month, SK asked the ITC to consider that \"LG Chem\nbatteries have been involved in a series of fires and\nexplosions, raising substantial public interest concerns.\"\n    In the request, SK said General Motors Co  <Origin Href=\\\"QuoteRef\\\">GM.N</Origin>  and Hyundai\nMotor Co  <Origin Href=\\\"QuoteRef\\\">005380.KS</Origin>  had each issued recent recalls for EVs with\nLG Chem batteries. The recalls \"raise questions of public safety\nand underscore the critical need for SK batteries in the United\nStates,\" the company said.\n    It added the fires are \"compelling grounds\" not to impede\nSK's ability to manufacture batteries at its U.S. plant.\n   LG Chem in a response filed Wednesday said the filing was\n\"untimely and irrelevant\" and should not factor in the ITC\nreview \"because the electric vehicle recalls cited by SKI do not\nimpact future-year battery models and do not impact EVs with\nbatteries manufactured in LG Chem’s Holland, Michigan plant.\"\n    GM said last month it was recalling 68,677 EVs worldwide for\nhigh-voltage batteries after five reported fires and two minor\ninjuries. In October, Hyundai recalled nearly 77,000 Kona EVs\nworldwide, saying possible defects in battery cells increased \nfire risks.\n    The ITC is expected to have a decision on the matter on\nMonday. An adverse ruling could potentially cause setbacks for\nVolkswagen  <Origin Href=\\\"QuoteRef\\\">VOWG_p.DE</Origin>  and Ford Motor  <Origin Href=\\\"QuoteRef\\\">F.N</Origin>  as they move to\nbuild new electric vehicles using SK Innovation's batteries and\ncomponents.\n\n (Reporting by David Shepardson and Hyunjoo Jin; Editing by Tom\nBrown)\n ((David.Shepardson@thomsonreuters.com; 2028988324;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL1N2IJ2OS")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-12-03T21:32:25-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "US STOCKS-Nasdaq hits record high, S&P 500 ends lower");
			hm1.put("TE",
					"<p> (For a Reuters live blog on U.S., UK and European stock\nmarkets, click LIVE/ or type LIVE/ in a news window)\n    * Boeing jumps on Ryanair's 737 MAX jet orders\n    * Jobless claims fall after rising for two straight weeks\n    * Goldman Sachs raises Tesla to \"buy\"  \n\n (Updates with close)\n    By Noel Randewich\n    Dec 3 (Reuters) - The Nasdaq Composite Index  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  closed\nat a record high on Thursday, lifted by Tesla Inc  <Origin Href=\\\"QuoteRef\\\">TSLA.O</Origin> ,\nwhile the S&P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  fell after a report that Pfizer Inc\n <Origin Href=\\\"QuoteRef\\\">PFE.N</Origin>  had slashed the target for the rollout of its COVID-19\nvaccine.\n    Tesla surged 5% after Goldman Sachs upgraded the stock to\n\"buy\" in the run-up to the electric car maker's addition to the\nS&P 500 index.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2IJ2KY</Origin>\n    Tesla was Wall Street's most traded stock by value, with\nabout $25 billion worth of shares exchanged, according to\nRefinitiv data, more than double Boeing, in second place.\n    The widely followed S&P 500 fell from all-time highs late in\nthe session after the Wall Street Journal reported that Pfizer\nfaced supply chain obstacles related to the vaccine, sending its\nstock down 1.7%.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nFWN2IJ0W1</Origin>\n    Progress in developing a working COVID-19 vaccine before the\nend of the year has driven Wall Street's main indexes to record\nlevels in recent days. Despite the economic destruction caused\nby the pandemic, the S&P 500 has gained about 13% in 2020. \n    \"It was the Pfizer news that cut into earlier gains,\" said\nPeter Cardillo, chief market economist at Spartan Capital\nSecurities in New York.\n    \"We're in a year-end Santa rally, but it's going to be a\nmuted one. Stimulus hopes are back in the market and it looks\nlike we might get something before year-end, which the economy\ndesperately needs.\"\n    First-time U.S. claims for jobless benefits fell last week,\nbut remained extraordinarily high at 712,000, while a separate\nsurvey showed U.S. services industry activity slowed to a\nsix-month low in November.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2II2LR</Origin> <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nN9N2HQ03B</Origin>\n    \"I don't think we will see those numbers start to get back\nto normal until we start to see a rollout of a vaccine,\" said\nSal Bruno, chief investment officer at IndexIQ. \"The market is\ndiscounting that and saying we are looking forward to the first\nor second quarter and a rollout of the vaccine to the general\npopulation.\"\n    U.S. Senate Majority Leader Mitch McConnell cited some\npositive movement in congressional efforts to reach a compromise\non a new coronavirus aid bill but gave no hints on when such a\ndeal could be struck.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nW1N2HV00V</Origin>\n    Boeing Co  <Origin Href=\\\"QuoteRef\\\">BA.N</Origin>  jumped after budget airline Ryanair  <Origin Href=\\\"QuoteRef\\\">RYA.I</Origin> \nordered 75 additional 737 MAX jets with a list price of $9\nbillion, throwing a commercial lifeline to the embattled U.S.\nplanemaker.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2IJ19U</Origin>\n    Six of 11 S&P 500 sector indexes rose, led by a 1% gain in\nenergy  <Origin Href=\\\"QuoteRef\\\">.SPNY</Origin> . \n    The Dow Jones Industrial Average  <Origin Href=\\\"QuoteRef\\\">.DJI</Origin>  rose 0.29% to end at\n29,969.52 points, while the S&P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  lost 0.06% to\n3,666.72. \n    The Nasdaq Composite  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  climbed 0.23% to 12,377.18.\n    Broad vaccine optimism helped lift the S&P 1500 airlines\nindex  <Origin Href=\\\"QuoteRef\\\">.SPCOMAIR</Origin>  4%. Cruise operators Carnival Corp  <Origin Href=\\\"QuoteRef\\\">CCL.N</Origin>  and\nNorwegian Cruise Line Holdings Ltd  <Origin Href=\\\"QuoteRef\\\">NCLH.N</Origin>  both surged more\nthan 8%.\n    Cloud-security provider Zscaler Inc  <Origin Href=\\\"QuoteRef\\\">ZS.O</Origin>  rallied over 26%\nafter it reported better-than-expected first-quarter revenue and\nadjusted profit.\n    Volume on U.S. exchanges was 11.5 billion shares, compared\nwith the 11.7 billion average over the last 20 trading days.\n    Advancing issues outnumbered decliners on the NYSE by a\n2.02-to-1 ratio; on Nasdaq, a 1.51-to-1 ratio favored advancers.\n    The S&P 500 posted 34 new 52-week highs and no new lows; the\nNasdaq Composite recorded 174 new highs and seven new lows.\n\n (Additional reporting by Shriya Ramakrishnan and Medha Singh in\nBengaluru; Additional reporting by Caroline Valetkevitch in New\nYork; Editing by Richard Chang)\n ((noel.randewich@tr.com; (415) 677 2542, Twitter: @randewich))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201201:nGNX7J4hN")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-19T23:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Amarin Files Patent Infringement Lawsuit Against Hikma");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201201:nGNX7J4hN&default-theme=true</Origin>\n\n\nDUBLIN, Ireland and BRIDGEWATER, N.J., Nov. 30, 2020 (GLOBE NEWSWIRE) --\nAmarin Corporation plc (NASDAQ:AMRN), announced today the filing of a patent\ninfringement lawsuit by Amarin affiliates and a licensor against Hikma\nPharmaceuticals PLC and Hikma’s U.S. affiliate. The lawsuit was filed in the\nUnited States District Court in Delaware. A copy of the complaint is available\nin the FAQ section of Amarin’s investor relations website.\n\nThe Amarin complaint alleges that Hikma has induced the infringement of U.S.\nPatent Nos. 9,700,537 (Composition for preventing the occurrence of\ncardiovascular event in multiple risk patient), 8,642,077 (Stable\npharmaceutical composition and methods of using same), and 10,568,861 (Methods\nof reducing the risk of a cardiovascular event in a subject at risk for\ncardiovascular disease) by making, selling, offering to sell and importing\ngeneric icosapent ethyl capsules in or into the United States. Amarin is\nseeking remedies including a permanent injunction against Hikma’s unlawful\ninducement of infringing uses of its generic product to reduce cardiovascular\nrisk and monetary damages in an amount sufficient to compensate Amarin for\nsuch infringement. Amarin is considering its legal options against similarly\nsituated parties acting in concert with Hikma by making or selling any drug\nproduct or component thereof covered by the subject patents, or inducing\nothers to do the same.\n\n“Amarin has the benefit of multiple issued patents in the United States and\nworldwide designed to protect its substantial investment in drug development\nbased on the use of icosapent ethyl in the treatment of serious diseases\nincluding cardiovascular disease and based on Amarin’s expertise in lipid\nscience and the therapeutic benefits of polyunsaturated fatty acids,” stated\nJoseph T. Kennedy, Amarin executive vice president and general counsel. “We\nintend to pursue this litigation vigorously and to protect our intellectual\nproperty rights and our VASCEPA(®) (icosapent ethyl) franchise in the United\nStates and globally.”\n\nIn December 2019, following over a decade of development and clinical study,\nVASCEPA was approved by the U.S. Food and Drug Administration as the first and\nonly drug for reduction in cardiovascular risk in the patient population\ndetailed below. This approval was heralded as one of the most significant\nadvances in preventative cardiovascular care in decades. Amarin has the\nbenefit of multiple patents that cover the use of VASCEPA for this important\nindication. Separately, Hikma has the benefit of court decisions that have led\nto its introduction of a generic version of VASCEPA for the drug’s original\nindication, use as an adjunct to diet to lower triglyceride levels in adult\npatients with very high (>500 mg/dL) triglyceride levels. In November 2020,\nHikma began to market and sell a generic version of VASCEPA. The Amarin\ncomplaint details its position that Hikma has knowingly and willfully\nintroduced its generic product in a manner that infringes Amarin’s patent\nrights and has exceeded the scope of permitted Hikma activity.\n\nAbout Amarin \nAmarin Corporation plc is a rapidly growing, innovative pharmaceutical company\nfocused on developing and commercializing therapeutics to cost-effectively\nimprove cardiovascular health. Amarin’s lead product, VASCEPA® (icosapent\nethyl), is available by prescription in the United States, Canada, Lebanon and\nthe United Arab Emirates. VASCEPA is not yet approved and available in any\nother countries. Amarin, on its own or together with its commercial partners\nin select geographies, is pursuing additional regulatory approvals for VASCEPA\nin China, Europe and the Middle East. For more information about Amarin, visit\n<Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>.\n\nAbout Cardiovascular Risk\nThe number of deaths in the United States attributed to cardiovascular disease\ncontinues to rise. There are 605,000 new and 200,000 recurrent heart attacks\nper year (approximately 1 every 40 seconds), in the United States. Stroke\nrates are 795,000 per year (approximately 1 every 40 seconds), accounting for\n1 of every 19 U.S. deaths. Cardiovascular disease results in 859,000 deaths\nper year in the United States.(1) In aggregate, there are more than 2.4\nmillion major adverse cardiovascular events per year from cardiovascular\ndisease or, on average, one every 13 seconds in the United States alone.\n\nControlling bad cholesterol, also known as LDL-C, is one way to reduce a\npatient’s risk for cardiovascular events, such as heart attack, stroke or\ndeath. However, even with the achievement of target LDL-C levels, millions of\npatients still have significant and persistent risk of cardiovascular events,\nespecially those patients with elevated triglycerides. Statin therapy has been\nshown to control LDL-C, thereby reducing the risk of cardiovascular events by\n25-35%.(2) Significant cardiovascular risk remains after statin therapy.\nPeople with elevated triglycerides have 35% more cardiovascular events\ncompared to people with normal (in range) triglycerides taking\nstatins.(3)(,)(4)(,)(5)\n\nAbout REDUCE-IT(®)\nREDUCE-IT was a global cardiovascular outcomes study designed to evaluate the\neffect of VASCEPA in adult patients with LDL-C controlled to between 41-100\nmg/dL (median baseline 75 mg/dL) by statin therapy and various cardiovascular\nrisk factors including persistent elevated triglycerides between 135-499 mg/dL\n(median baseline 216 mg/dL) and either established cardiovascular disease\n(secondary prevention cohort) or diabetes mellitus and at least one other\ncardiovascular risk factor (primary prevention cohort).\n\nREDUCE-IT, conducted over seven years and completed in 2018, followed 8,179\npatients at over 400 clinical sites in 11 countries with the largest number of\nsites located within the United States. REDUCE-IT was conducted based on a\nspecial protocol assessment agreement with FDA. The design of the REDUCE-IT\nstudy was published in March 2017 in Clinical Cardiology.(6) The primary\nresults of REDUCE-IT were published in The New England Journal of Medicine in\nNovember 2018.(7) The total events results of REDUCE-IT were published in the\nJournal of the American College of Cardiology in March 2019.(8) These and\nother publications can be found in the R&D section on the company’s website\nat <Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>.\n\nAbout VASCEPA(®) (icosapent ethyl) Capsules\nVASCEPA (icosapent ethyl) capsules are the first-and-only prescription\ntreatment approved by the FDA comprised solely of the active ingredient,\nicosapent ethyl (IPE), a unique form of eicosapentaenoic acid. VASCEPA was\ninitially launched in the United States in 2013 based on the drug’s initial\nFDA approved indication for use as an adjunct therapy to diet to reduce\ntriglyceride levels in adult patients with severe (≥500 mg/dL)\nhypertriglyceridemia. Since launch, VASCEPA has been prescribed over eight\nmillion times. VASCEPA is covered by most major medical insurance plans. The\nnew, cardiovascular risk indication for VASCEPA was approved by the FDA in\nDecember 2019.\n\nIndications and Limitation of Use\nVASCEPA is indicated:\n* As an adjunct to maximally tolerated statin therapy to reduce the risk of\nmyocardial infarction, stroke, coronary revascularization and unstable angina\nrequiring hospitalization in adult patients with elevated triglyceride (TG)\nlevels (≥ 150 mg/dL) and\n○  established cardiovascular disease or\n○  diabetes mellitus and two or more additional risk factors for\ncardiovascular disease.\n* As an adjunct to diet to reduce TG levels in adult patients with severe\n(≥ 500 mg/dL) hypertriglyceridemia.\nThe effect of VASCEPA on the risk for pancreatitis in patients with severe\nhypertriglyceridemia has not been determined.\n\nImportant Safety Information\n* VASCEPA is contraindicated in patients with known hypersensitivity (e.g.,\nanaphylactic reaction) to VASCEPA or any of its components.\n* VASCEPA was associated with an increased risk (3% vs 2%) of atrial\nfibrillation or atrial flutter requiring hospitalization in a double-blind,\nplacebo-controlled trial. The incidence of atrial fibrillation was greater in\npatients with a previous history of atrial fibrillation or atrial flutter.\n* It is not known whether patients with allergies to fish and/or shellfish are\nat an increased risk of an allergic reaction to VASCEPA. Patients with such\nallergies should discontinue VASCEPA if any reactions occur.\n* VASCEPA was associated with an increased risk (12% vs 10%) of bleeding in a\ndouble-blind, placebo-controlled trial. The incidence of bleeding was greater\nin patients receiving concomitant antithrombotic medications, such as aspirin,\nclopidogrel or warfarin.\n* Common adverse reactions in the cardiovascular outcomes trial (incidence\n≥3% and ≥1% more frequent than placebo): musculoskeletal pain (4% vs 3%),\nperipheral edema (7% vs 5%), constipation (5% vs 4%), gout (4% vs 3%), and\natrial fibrillation (5% vs 4%).\n* Common adverse reactions in the hypertriglyceridemia trials (incidence >1%\nmore frequent than placebo): arthralgia (2% vs 1%) and oropharyngeal pain (1%\nvs 0.3%).\n* Adverse events may be reported by calling 1-855-VASCEPA or the FDA at\n1-800-FDA-1088.\n* Patients receiving VASCEPA and concomitant anticoagulants and/or\nanti-platelet agents should be monitored for bleeding.\nKey clinical effects of VASCEPA on major adverse cardiovascular events are\nincluded in the Clinical Studies section of the prescribing information for\nVASCEPA as set forth below:\n\nEffect of VASCEPA on Time to First Occurrence of Cardiovascular Events in\nPatients with \nElevated Triglyceride levels and Other Risk Factors for Cardiovascular Disease\nin REDUCE-IT\n\n                                                                                                                                      VASCEPA                                                 Placebo                                                 VASCEPA  vs Placebo    \n                                                                                                                                      N = 4089 n (%)  Incidence Rate (per 100 patient years)  N = 4090 n (%)  Incidence Rate (per 100 patient years)  Hazard Ratio (95% CI)  \n Primary composite endpoint                                                                                                                                                                                                                                                  \n Cardiovascular death, myocardial infarction, stroke, coronary revascularization, hospitalization for unstable angina (5-point MACE)  705 (17.2)      4.3                                     901 (22.0)      5.7                                     0.75 (0.68, 0.83)      \n Key secondary composite endpoint                                                                                                                                                                                                                                            \n Cardiovascular death, myocardial infarction, stroke (3-point MACE)                                                                   459 (11.2)      2.7                                     606 (14.8)      3.7                                     0.74 (0.65, 0.83)      \n Other secondary endpoints                                                                                                                                                                                                                                                   \n Fatal or non-fatal myocardial infarction                                                                                             250 (6.1)       1.5                                     355 (8.7)       2.1                                     0.69 (0.58, 0.81)      \n Emergent or urgent coronary revascularization                                                                                        216 (5.3)       1.3                                     321 (7.8)       1.9                                     0.65 (0.55, 0.78)      \n Cardiovascular death ( <Origin Href=\\\"NewsSearch\\\">1</Origin> )                                                                                                           174 (4.3)       1.0                                     213 (5.2)       1.2                                     0.80 (0.66, 0.98)      \n Hospitalization for unstable angina ( <Origin Href=\\\"NewsSearch\\\">2</Origin> )                                                                                            108 (2.6)       0.6                                     157 (3.8)       0.9                                     0.68 (0.53, 0.87)      \n Fatal or non-fatal stroke                                                                                                            98 (2.4)        0.6                                     134 (3.3)       0.8                                     0.72 (0.55, 0.93)      \n  <Origin Href=\\\"NewsSearch\\\">1</Origin>  Includes adjudicated cardiovascular deaths and deaths of undetermined causality.  <Origin Href=\\\"NewsSearch\\\">2</Origin>  Determined to be caused by myocardial ischemia by invasive/non-invasive testing and requiring emergent hospitalization.                                                            \n\nFULL VASCEPA PRESCRIBING INFORMATION\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=X_EcF9KHkHw7GnVmsWQlntbdEA6AXfZ7bwPgJK8Rt7EoeUMWjMwu871IRqmMyQ6IUx6TmdlLNNVoWm1qBKnHJak930jZweWvqeJHXTY99o02RM7Y5rvLSfqq7PhRu7bg</Origin>)\nCAN BE FOUND AT WWW.\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=X_8olyBEd43PPCDbXW1rFR0RZi8hflxyCvO3njrtjdZWtV998C6mqvBiybz1fKrjP3N3SQ_IoGMAjQStna7DAw==</Origin>)VASCEPA\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=G0eH3-eMX0MSyNgHshK54GRv_EbTu6rxHIMsbEGJmmNEzDScmE8KzXsZqghwsUW_H0JoNwwqow5n-dlVPZJBdg==</Origin>).COM\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=v0D7qmoED4DVP9Cn7o4pftKcO-TBUebANGuCYwCew6HFGqALYV5HPwNe0Jkm4q7S-9T21aLLnyCLH6-lD77ENg==</Origin>).\n\nForward-Looking Statements \nThis press release contains forward-looking statements, including statements\nabout the subject patent litigation, Amarin’s plan to pursue the litigation\nvigorously and the validity or enforceability of the subject patents. These\nforward-looking statements are not promises or guarantees and involve\nsubstantial risks and uncertainties that may individually or mutually impact\nthe matters herein, and cause actual results, events and performance to differ\nmaterially from such forward looking statements. Among the factors that could\ncause actual results to differ materially from those described or projected\nherein include the following: events that could interfere with the continued\nvalidity or enforceability of a patent; uncertainties associated with\nlitigation generally and patent litigation specifically; Amarin's ability\ngenerally to maintain adequate patent protection and successfully enforce\npatent claims against third parties; commercializing Vascepa without violating\nthe intellectual property rights of others; and uncertainties associated\ngenerally with research and development and regulatory submissions, action\ndates and approvals. A further list and description of these risks,\nuncertainties and other risks associated with an investment in Amarin can be\nfound in Amarin's filings with the U.S. Securities and Exchange Commission,\nincluding its most recent quarterly report on Form 10-Q. Existing and\nprospective investors are cautioned not to place undue reliance on these\nforward-looking statements, which speak only as of the date hereof. Amarin\nundertakes no obligation to update or revise the information contained in this\npress release, whether as a result of new information, future events or\ncircumstances or otherwise.\n\nAvailability of Other Information About Amarin\nInvestors and others should note that Amarin communicates with its investors\nand the public using the company website (<Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>), the investor\nrelations website (investor.amarincorp.com\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=zpYT61U4yq96M18Zx6wv-A174mWKcRp5AglH2t4q88VrFmlJn_rCMo7hBADdXcdXFVOhJ-IOLbDC-mZ8O0ZWaMn97EmonbFk8-lNA7v_QkQ=</Origin>)),\nincluding but not limited to investor presentations and investor FAQs,\nSecurities and Exchange Commission filings, press releases, public conference\ncalls and webcasts. The information that Amarin posts on these channels and\nwebsites could be deemed to be material information. As a result, Amarin\nencourages investors, the media, and others interested in Amarin to review the\ninformation that is posted on these channels, including the investor relations\nwebsite, on a regular basis. This list of channels may be updated from time to\ntime on Amarin’s investor relations website and may include social media\nchannels. The contents of Amarin’s website or these channels, or any other\nwebsite that may be accessed from its website or these channels, shall not be\ndeemed incorporated by reference in any filing under the Securities Act of\n1933.\n\nAmarin Contact Information\nInvestor Inquiries:\nInvestor Relations\nAmarin Corporation plc\nIn U.S.: +1 (908) 719-1315\nIR@amarincorp.com (investor inquiries)\n\nSolebury Trout\namarinir@troutgroup.com\n\nMedia Inquiries:\nAlina Kolomeyer\nCommunications\nAmarin Corporation plc\nIn U.S.: +1 (908) 892-2028 \nPR@amarincorp.com (media inquiries)\n\n__________________________\n\n(1) American Heart Association. Heart Disease and Stroke Statistics—2020\nUpdate: A Report From the American Heart Association. Circulation.\n2020;141:e139–e596.\n(2 )Ganda OP, Bhatt DL, Mason RP, et al. Unmet need for adjunctive\ndyslipidemia therapy in hypertriglyceridemia management. J Am Coll Cardiol.\n2018;72(3):330-343.\n(3 )Budoff M. Triglycerides and triglyceride-rich lipoproteins in the causal\npathway of cardiovascular disease. Am J Cardiol. 2016;118:138-145.\n(4 )Toth PP, Granowitz C, Hull M, et al. High triglycerides are associated\nwith increased cardiovascular events, medical costs, and resource use: A\nreal-world administrative claims analysis of statin-treated patients with high\nresidual cardiovascular risk. J Am Heart Assoc. 2018;7(15):e008740.\n(5 )Nordestgaard BG. Triglyceride-rich lipoproteins and atherosclerotic\ncardiovascular disease - New insights from epidemiology, genetics, and\nbiology. Circ Res. 2016;118:547-563.\n(6 )Bhatt DL, Steg PG, Brinton E, et al., on behalf of the REDUCE-IT\nInvestigators. Rationale and Design of REDUCE‐IT: Reduction of\nCardiovascular Events with Icosapent Ethyl–Intervention Trial. Clin Cardiol.\n2017;40:138-148.\n(7 )Bhatt DL, Steg PG, Miller M, et al., on behalf of the REDUCE-IT\nInvestigators. Cardiovascular Risk Reduction with Icosapent Ethyl for\nHypertriglyceridemia. N Engl J Med. 2019;380:11-22.\n(8 )Bhatt DL, Steg PG, Miller M, et al., on behalf of the REDUCE-IT\nInvestigators. Reduction in first and total ischemic events with icosapent\nethyl across baseline triglyceride tertiles. J Am Coll Cardiol.\n2019;74:1159-1161.\n\n\n\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/00eeab93-e634-4279-86b3-7973c2a21623</Origin>)\n\n\n\nGlobeNewswire, Inc. 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201124:nGNX6QdSS9")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-19T23:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Amarin to Present at Piper Sandler’s 32nd Annual Healthcare Conference (Virtual)");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201124:nGNX6QdSS9&default-theme=true</Origin>\n\n\nDUBLIN, Ireland and BRIDGEWATER, N.J., Nov. 24, 2020 (GLOBE NEWSWIRE) --\nAmarin Corporation plc (NASDAQ:AMRN) today announced that John F. Thero,\nAmarin's president and chief executive officer, is scheduled to participate in\na Fireside Chat at the Piper Sandler’s 32(nd) Annual Healthcare Conference\n(Virtual) taking place from December 1 - December 3, 2020. Mr. Thero’s\npresentation is scheduled to take place on Wednesday, December 2, 2020 at\n10:00 am Eastern time.\n\nA live audio webcast of the presentations will be available at:\n<Origin Href=\\\"Link\\\">http://www.amarincorp.com</Origin>, and will be accessible at the same link for 30\ndays.\n\nAbout Amarin\n\nAmarin Corporation plc is a rapidly growing, innovative pharmaceutical company\nfocused on developing and commercializing therapeutics to cost-effectively\nimprove cardiovascular health. Amarin’s lead product, VASCEPA® (icosapent\nethyl), is available by prescription in the United States, Canada, Lebanon and\nthe United Arab Emirates. VASCEPA is not yet approved and available in any\nother countries. Amarin, on its own or together with its commercial partners\nin select geographies, is pursuing additional regulatory approvals for VASCEPA\nin China, Europe and the Middle East. For more information about Amarin, visit\n<Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>.\n\nAvailability of Other Information About Amarin\n\nInvestors and others should note that Amarin communicates with its investors\nand the public using the company website (<Origin Href=\\\"Link\\\">http://www.amarincorp.com/</Origin>), the\ninvestor relations website (<Origin Href=\\\"Link\\\">http://investor.amarincorp.com/</Origin>), including but\nnot limited to investor presentations and investor FAQs, Securities and\nExchange Commission filings, press releases, public conference calls and\nwebcasts. The information that Amarin posts on these channels and websites\ncould be deemed to be material information. As a result, Amarin encourages\ninvestors, the media, and others interested in Amarin to review the\ninformation that is posted on these channels, including the investor relations\nwebsite, on a regular basis. This list of channels may be updated from time to\ntime on Amarin’s investor relations website and may include social media\nchannels. The contents of Amarin’s website or these channels, or any other\nwebsite that may be accessed from its website or these channels, shall not be\ndeemed incorporated by reference in any filing under the Securities Act of\n1933.\n\nAmarin Contact Information\n\nInvestor Inquiries:\nInvestor Relations\nAmarin Corporation plc\nIn U.S.: +1 (908) 719-1315\nIR@amarincorp.com (investor inquiries)\n\nSolebury Trout\namarinir@troutgroup.com\n\nMedia Inquiries:\nCommunications\nAmarin Corporation plc\nIn U.S.: +1 (908) 892-2028 \nPR@amarincorp.com (media inquiries)\n\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/00eeab93-e634-4279-86b3-7973c2a21623</Origin>)\n\n\n\nGlobeNewswire, Inc. 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201120:nFWN2I519C")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-19T23:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-Amarin Shares Topline Data From Partner’s Phase 3 Study Of Vascepa In Mainland China");
			hm1.put("TE",
					"<p>    Nov 19 (Reuters) - Amarin Corporation PLC  <Origin Href=\\\"QuoteRef\\\">AMRN.O</Origin> :\n    * AMARIN SHARES TOPLINE DATA FROM PARTNER’S PIVOTAL PHASE 3\nSTUDY\nOF VASCEPA® (ICOSAPENT ETHYL) IN MAINLAND CHINA\n    * AMARIN CORPORATION PLC - SIGNIFICANT REDUCTION IN\nTRIGLYCERIDE\nLEVELS WITHOUT LOW-DENSITY LIPOPROTEIN CHOLESTEROL (LDL-C)\nINCREASE COMPARED TO PLACEBO\n    * AMARIN CORPORATION PLC - NO TREATMENT-RELATED SERIOUS\nADVERSE\nEVENTS IN EDPC003R01 STUDY\n    * AMARIN CORPORATION PLC - VASCEPA 4 GRAM PER DAY DOSE IN\nEDPC003R01 APPEARED TO BE WELL-TOLERATED WITH A SAFETY PROFILE\nSIMILAR TO PLACEBO\n\nSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:nGNX9pW2Dl</Origin> \nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">AMRN.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201119:nGNX9pW2Dl")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-19T23:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"Amarin Shares Topline Data from Partner’s Pivotal Phase 3 Study of VASCEPA® (Icosapent Ethyl) in Mainland China");
			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201119:nGNX9pW2Dl</Origin>\n\n\nSignificant Reduction in Triglyceride Levels Without Low-Density Lipoprotein\nCholesterol (LDL-C) Increase Compared to Placebo and Safety Profile Similar to\nPlacebo Achieved with 4 Grams Per Day Dose of Icosapent Ethyl in Chinese\nPatients with Very High Triglycerides (>500 mg/dL)\n\nResults Support Upcoming Submission by Partner, Edding, Seeking Regulatory\nApproval in China\n\nDUBLIN, Ireland and BRIDGEWATER, N.J., Nov. 19, 2020 (GLOBE NEWSWIRE) --\nAmarin Corporation plc (NASDAQ:AMRN) today shared positive, statistically\nsignificant top-line results from Protocol Number EDPC003R01, a Phase 3\nclinical trial of VASCEPA(®) (icosapent ethyl) conducted in China by Amarin\npartner, Edding. The study, which investigated VASCEPA as a treatment for\npatients with very high triglycerides (≥500 mg/dL), met its primary efficacy\nendpoint as defined in the clinical trial protocol and demonstrated a safety\nprofile similar to placebo. The findings are being prepared to support\nEdding’s dossier for seeking regulatory approval of VASCEPA in Mainland\nChina.\n\nThe EDPC003R01 trial was a multi-center, randomized, double-blind,\nplacebo-controlled, 12-week pivotal study in adult patients in China with\nqualifying fasting triglyceride (TG) levels greater than or equal to 500 mg/dL\nand less than or equal to 2000 mg/dL. The median baseline TG levels in the\nstudy were 812 mg/dL and 837 mg/dL for the patients assigned to placebo\n(n=123) and 4 grams per day of VASCEPA (n=122), respectively. Prior to\nrandomization into the 12-week double-blind treatment period, all patients\nunderwent a six- to eight-week washout period of lipid altering drugs, as well\nas diet and lifestyle stabilization.\n\nThe study’s primary endpoint, the percent change in TG levels from baseline\nto week 12, was met for the 4 gram per day VASCEPA dose group. The patient\ngroup assigned to 4 grams per day of VASCEPA showed a statistically\nsignificant median TG decrease of 19.9% (p<0.001) compared to placebo at the\nend of the 12-week treatment period.\n\nConsistent with Amarin’s MARINE study in a similar patient population, the 4\ngram per day dose of VASCEPA in the EDPC003R01 trial did not result in a\nsignificant median increase from baseline in low-density lipoprotein\ncholesterol (LDL-C) compared to placebo at the end of the 12-week treatment\nperiod. The primary results of MARINE were published in the American Journal\nof Cardiology(1) in June 2011. Results from the MARINE study were the basis\nfor VASCEPA’s initial approval in the United States for triglyceride\nlowering before the successful results of the REDUCE-IT(®) cardiovascular\noutcomes study.\n\nImportantly, the VASCEPA 4 gram per day dose in EDPC003R01 appeared to be\nwell-tolerated with a safety profile similar to placebo. There were no\ntreatment-related serious adverse events in the EDPC003R01 study.\n\n“We are proud to share news of these positive data from our partner’s\npivotal Phase 3 clinical study of VASCEPA in China. Elevated triglycerides are\na known marker of risk for pancreatitis and for cardiovascular disease. The\nstatistically significant reduction in TG levels seen with the VASCEPA 4 gram\nper day dose in the study highlights its potential to address an unmet medical\nneed in China, where hypertriglyceridemia is on the rise,” said Steven\nKetchum, Ph.D., senior vice president and president, research & development\nand chief scientific officer at Amarin. “This pivotal study in China\nmirrored Amarin’s MARINE study in patients from the United States and other\ncountries, and we are pleased that the data show consistency across the\nChinese and non-Chinese study populations. With these favorable data, we now\nlook forward to supporting our partner, Edding, in compiling and submitting\nits dossier at the earliest possible opportunity for regulatory review in\nChina.”\n\n“We are very pleased that the Phase III clinical study of VASCEPA in\nMainland China has achieved positive results. Cardiovascular (CV) disease is\nthe largest cause of death in China with significant unmet needs to address.\nPrevention and treatment of CV diseases is one of the major initiatives\npromoted by Health China 2030. VASCEPA is anticipated to be launched to\nfurther address these pressing needs. We will try our best to promote VASCEPA\nto market as soon as possible in order to benefit more Chinese patients,\" said\nMr. Xin Ni, founder, chairman and CEO of Edding. \"VASCEPA has huge commercial\npotential in the fast-growing Chinese market. We will work with Amarin to\nsubmit the new drug application as soon as possible to bring this cross-era\ninnovative drug to China.”\n\nAmarin intends to support Edding in its pursuit of an appropriate label for\nVASCEPA in China reflecting the results of EDPC003R01 and all other available\ndata supporting the safety and efficacy of VASCEPA.\n\nAbout Edding\nEdding is a leading integrated pharmaceutical company in China. Edding’s\nvision is to become a leading ‘Global for China' pharmaceutical company\nfocusing on three core therapeutic areas, namely anti-infectives,\ncardiovascular disease and respiratory system, by leveraging Edding’s\nmarket-tested full value chain capabilities. For more information about\nEdding, visit <Origin Href=\\\"Link\\\">www.eddingpharm.com</Origin>.\n\nAbout Hypertriglyceridemia in China\nThere were approximately 180.4 million hypertriglyceridemia (HTG) patients in\nChina in 2019, representing approximately 20.2% of the adult population. Among\nall HTG patients in China, there were approximately 9 million adults who had\nvery high TG levels (≥500 mg/dL). In 2019, there were approximately 36.1\nmillion statin-treated adult patients in China with elevated TG levels (≥150\nmg/dL) and either established CVD or diabetes mellitus and two or more\nadditional risk factors for CVD, the addressable patients of the FDA-approved\nindication for reducing CV events of VASCEPA in China.\n\nAbout Amarin \nAmarin Corporation plc is a rapidly growing, innovative pharmaceutical company\nfocused on developing and commercializing therapeutics to cost-effectively\nimprove cardiovascular health. Amarin’s lead product, VASCEPA(®) (icosapent\nethyl), is available by prescription in the United States, Canada, Lebanon and\nthe United Arab Emirates. VASCEPA is not yet approved and available in any\nother countries. Amarin, on its own or together with its commercial partners\nin select geographies, is pursuing additional regulatory approvals for VASCEPA\nin China, Europe and the Middle East. For more information about Amarin, visit\n<Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>.\n\nAbout VASCEPA(®) (icosapent ethyl) Capsules\nVASCEPA (icosapent ethyl) capsules are the first-and-only prescription\ntreatment approved by the FDA comprised solely of the active ingredient,\nicosapent ethyl (IPE), a unique form of eicosapentaenoic acid. VASCEPA was\ninitially launched in the United States in 2013 based on the drug’s initial\nFDA approved indication for use as an adjunct therapy to diet to reduce\ntriglyceride levels in adult patients with severe (≥500 mg/dL)\nhypertriglyceridemia. Since launch, VASCEPA has been prescribed over eight\nmillion times. VASCEPA is covered by most major medical insurance plans. The\nnew, cardiovascular risk indication for VASCEPA was approved by the FDA in\nDecember 2019.\n\nIndications and Limitation of Use\nVASCEPA is indicated:\n* As an adjunct to maximally tolerated statin therapy to reduce the risk of\nmyocardial infarction, stroke, coronary revascularization and unstable angina\nrequiring hospitalization in adult patients with elevated triglyceride (TG)\nlevels (≥ 150 mg/dL) and  * established cardiovascular disease or\n* diabetes mellitus and two or more additional risk factors for cardiovascular\ndisease.\n  \n* As an adjunct to diet to reduce TG levels in adult patients with severe\n(≥ 500 mg/dL) hypertriglyceridemia.\nThe effect of VASCEPA on the risk for pancreatitis in patients with severe\nhypertriglyceridemia has not been determined.\n\nImportant Safety Information\n* VASCEPA is contraindicated in patients with known hypersensitivity (e.g.,\nanaphylactic reaction) to VASCEPA or any of its components.\n* VASCEPA was associated with an increased risk (3% vs 2%) of atrial\nfibrillation or atrial flutter requiring hospitalization in a double-blind,\nplacebo-controlled trial. The incidence of atrial fibrillation was greater in\npatients with a previous history of atrial fibrillation or atrial flutter.\n* It is not known whether patients with allergies to fish and/or shellfish are\nat an increased risk of an allergic reaction to VASCEPA. Patients with such\nallergies should discontinue VASCEPA if any reactions occur.\n* VASCEPA was associated with an increased risk (12% vs 10%) of bleeding in a\ndouble-blind, placebo-controlled trial. The incidence of bleeding was greater\nin patients receiving concomitant antithrombotic medications, such as aspirin,\nclopidogrel or warfarin.\n* Common adverse reactions in the cardiovascular outcomes trial (incidence\n≥3% and ≥1% more frequent than placebo): musculoskeletal pain (4% vs 3%),\nperipheral edema (7% vs 5%), constipation (5% vs 4%), gout (4% vs 3%), and\natrial fibrillation (5% vs 4%).\n* Common adverse reactions in the hypertriglyceridemia trials (incidence >1%\nmore frequent than placebo): arthralgia (2% vs 1%) and oropharyngeal pain (1%\nvs 0.3%).\n* Adverse events may be reported by calling 1-855-VASCEPA or the FDA at\n1-800-FDA-1088.\n* Patients receiving VASCEPA and concomitant anticoagulants and/or\nanti-platelet agents should be monitored for bleeding.\nKey clinical effects of VASCEPA on major adverse cardiovascular events are\nincluded in the Clinical Studies section of the prescribing information for\nVASCEPA as set forth below:\n\nEffect of VASCEPA on Time to First Occurrence of Cardiovascular Events in\nPatients with \nElevated Triglyceride levels and Other Risk Factors for Cardiovascular Disease\nin REDUCE-IT\n\n                                                                                                                                      VASCEPA                                   Placebo                                   VASCEPA  vs Placebo  \n                                                                                                                                      N = 4089 n (%)  Incidence Rate (per 100   N = 4090 n (%)  Incidence Rate (per 100   Hazard Ratio         \n                                                                                                                                                      patient years)                            patient years)            (95% CI)             \n Primary composite endpoint                                                                                                                                                                                                                    \n Cardiovascular death, myocardial infarction, stroke, coronary revascularization, hospitalization for unstable angina (5-point MACE)  705 (17.2)      4.3                       901 (22.0)      5.7                       0.75 (0.68, 0.83)    \n Key secondary composite endpoint                                                                                                                                                                                                              \n Cardiovascular death, myocardial infarction, stroke (3-point MACE)                                                                   459 (11.2)      2.7                       606 (14.8)      3.7                       0.74 (0.65, 0.83)    \n Other secondary endpoints                                                                                                                                                                                                                     \n Fatal or non-fatal myocardial infarction                                                                                             250 (6.1)       1.5                       355 (8.7)       2.1                       0.69 (0.58, 0.81)    \n Emergent or urgent coronary revascularization                                                                                        216 (5.3)       1.3                       321 (7.8)       1.9                       0.65 (0.55, 0.78)    \n Cardiovascular death ( <Origin Href=\\\"NewsSearch\\\">1</Origin> )                                                                                                           174 (4.3)       1.0                       213 (5.2)       1.2                       0.80 (0.66, 0.98)    \n Hospitalization for unstable angina ( <Origin Href=\\\"NewsSearch\\\">2</Origin> )                                                                                            108 (2.6)       0.6                       157 (3.8)       0.9                       0.68 (0.53, 0.87)    \n Fatal or non-fatal stroke                                                                                                            98 (2.4)        0.6                       134 (3.3)       0.8                       0.72 (0.55, 0.93)    \n  <Origin Href=\\\"NewsSearch\\\">1</Origin>  Includes adjudicated cardiovascular deaths and deaths of undetermined causality.  <Origin Href=\\\"NewsSearch\\\">2</Origin>  Determined to be caused by myocardial ischemia by invasive/non-invasive testing and requiring emergent hospitalization.                              \n\nFULL VASCEPA PRESCRIBING INFORMATION\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=C9U6dYo6IXkZx0HsQwGaa5aVkwHWA_Wps-jx0CPI1MFc5YfCqzrJWZT3EvZndEPFcdeKRSGijB6ghc1Fi7i21sCCsxSIlrwH0mfzY4LLyoM89pzLgStStoUIKNJG35x1</Origin>)\nCAN BE FOUND AT WWW.\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=XcHXAaPLf6U9CArePkeivzjFlGM40VH6r896Fr0D8KuUZ3SzL0VZihuwNZs5ZCCtnEFrsGg2fF9QU-ttD1WTXw==</Origin>)VASCEPA\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=FMX20FSJBfJL73HYm7agzF7Xap8orDdO8cixGN2Oc0k-SZcviLyGa5iHhkJcoRra0JwP-abhRDtkr6n9ZOhSKA==</Origin>).COM\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=Cuul8_G6Uov2QwQN186-8eS5AdGTY36J5t2hhDuNmURu4BjmNfntEuMWUErlNSPXVl3pWBrM-t5Dc0Ynygh5XA==</Origin>).\n\nForward-Looking Statements \nThis press release contains forward-looking statements, including statements\nregarding the potential impact of VASCEPA in clinical use and expectations for\nregulatory filings and approval submissions. These forward-looking statements\nare not promises or guarantees and involve substantial risks and\nuncertainties. Among the factors that could cause actual results to differ\nmaterially from those described or projected herein include the following:\nuncertainties associated generally with research and development, clinical\ntrials and regulatory reviews. A further list and description of these risks,\nuncertainties and other risks associated with an investment in Amarin can be\nfound in Amarin's filings with the U.S. Securities and Exchange Commission,\nincluding its most recent Quarterly Report on Form 10-Q. Existing and\nprospective investors are cautioned not to place undue reliance on these\nforward-looking statements, which speak only as of the date hereof. Amarin\nundertakes no obligation to update or revise the information contained in this\npress release, whether as a result of new information, future events or\ncircumstances or otherwise. Amarin’s forward-looking statements do not\nreflect the potential impact of significant transactions the company may enter\ninto, such as mergers, acquisitions, dispositions, joint ventures or any\nmaterial agreements that Amarin may enter into, amend or terminate.\n\nAvailability of Other Information About Amarin\nInvestors and others should note that Amarin communicates with its investors\nand the public using the company website (<Origin Href=\\\"Link\\\">www.amarincorp.com</Origin>), the investor\nrelations website (investor.amarincorp.com\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/Tracker?data=PVz5_ldbFCQ86l1OJw6lM5t2tNJoQSGLYXXTdjp9iCRpOn1nSh-UPxjbLoiZ-JXvfVTGVWTMiSzZmb5OLZ_fXH_s7DpaCslhs95N5UuTFOs=</Origin>)),\nincluding but not limited to investor presentations and investor FAQs,\nSecurities and Exchange Commission filings, press releases, public conference\ncalls and webcasts. The information that Amarin posts on these channels and\nwebsites could be deemed to be material information. As a result, Amarin\nencourages investors, the media, and others interested in Amarin to review the\ninformation that is posted on these channels, including the investor relations\nwebsite, on a regular basis. This list of channels may be updated from time to\ntime on Amarin’s investor relations website and may include social media\nchannels. The contents of Amarin’s website or these channels, or any other\nwebsite that may be accessed from its website or these channels, shall not be\ndeemed incorporated by reference in any filing under the Securities Act of\n1933.\n\nAmarin Contact Information\nInvestor Inquiries:\nInvestor Relations\nAmarin Corporation plc\nIn U.S.: +1 (908) 719-1315 \nIR@amarincorp.com (investor inquiries)\n\nSolebury Trout\nlstern@soleburytrout.com\n\nMedia Inquiries:\nAlina Kolomeyer\nCommunications\nAmarin Corporation plc\nIn U.S.: +1 (908) 892-2028 \nPR@amarincorp.com (media inquiries)\n\n________________________\n(1) Bays HE, Ballantyne CM, Kastelein JJ et al. Eicosapentaenoic Acid Ethyl\nEster (AMR101) Therapy in Patients with Very High Triglyceride Levels (from\nthe Multi-center, plAcebo-controlled, Randomized, double-blINd, 12-week study\nwith an open-label Extension  <Origin Href=\\\"NewsSearch\\\">MARINE</Origin>  Trial). Am J Cardiol. 2011;108:682-690.\n\n\n\n(<Origin Href=\\\"Link\\\">https://www.globenewswire.com/NewsRoom/AttachmentNg/00eeab93-e634-4279-86b3-7973c2a21623</Origin>)\n\n\n\nGlobeNewswire, Inc. 2020</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL1N2IJ26V")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-19T23:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 3-Justice Department accuses Facebook of discriminating against U.S. workers");
			hm1.put("TE",
					"<p> (Adds details and background)\n    By Sarah N. Lynch, Nandita Bose and Katie Paul\n    Dec 3 (Reuters) - The U.S. Justice Department accused\nFacebook Inc  <Origin Href=\\\"QuoteRef\\\">FB.O</Origin>  on Thursday of discriminating against U.S.\nworkers, saying in a new lawsuit the social media giant has\ngiven hiring preferences to temporary workers, including those\nwho hold H-1B visas.\n    The Justice Department said that Facebook had \"refused\" to\nrecruit, consider or hire qualified U.S. workers for more than\n2,600 jobs that in many cases paid an average salary of $156,000\na year.\n    Instead, it opted to fill the positions using temporary visa\nholders, such as those with H-1B visas, the department added.\n    \"Facebook intentionally created a hiring system in which it\ndenied qualified U.S. workers a fair opportunity to learn about\nand apply for jobs,\" the Justice Department said. The social\nmedia company instead sought to channel such jobs to temporary\nvisa holders it wanted to sponsor for green cards or permanent\nresidency, it added.\n    Company spokesman Daniel Roberts said: \"Facebook has been\ncooperating with the DOJ in its review of this issue and while\nwe dispute the allegations in the complaint, we cannot comment\nfurther on pending litigation.\"  \n    H-1B visas are often used by the technology sector to bring\nhighly skilled foreign guest workers to the United States. But\ncritics say the laws governing these visas are lax, and make it\ntoo easy to replace U.S. workers with cheaper, foreign labor.\n    The Facebook lawsuit is the latest example of the Trump\nadministration clashing with Silicon Valley over attempts to\nrestrict immigration for foreign workers. Trump and Republican\nlawmakers have also clashed with the company in other areas,\nsuch as accusing the platform of stifling conservative voices.\n    The Justice and Labor departments have both investigated big\ntech companies in the past over allegations similar to those\nagainst Facebook, but they have rarely brought charges due to\nloopholes in the law.\n    Tech companies and industry groups have pushed back against\nmoves to limit immigration of foreign workers by saying there\nare not enough American students graduating with science and\nengineering degrees to meet the demand for filling jobs in areas\nsuch as artificial intelligence.\n    In June, Trump issued a presidential proclamation that\ntemporarily blocked foreign workers entering on H-1B visas - an\nattempt the administration then said would open up 525,000 jobs\nfor U.S. workers.\n    Among the top 30 H-1B employers are major U.S. firms\nincluding Amazon  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin> , Microsoft  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin> , Walmart  <Origin Href=\\\"QuoteRef\\\">WMT.N</Origin> ,\nAlphabet's Google  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin> , Apple  <Origin Href=\\\"QuoteRef\\\">APPL.O</Origin> , and Facebook,\naccording to a report by the Economic Policy Institute (EPI) in\nMay. \n    The EPI report said most companies using H1B visas take\nadvantage of program rules in order to legally pay such workers\nbelow the local median wage for the jobs they fill.\n\n (Reporting by Sarah N. Lynch and Nandita Bose in Washington and\nKatie Paul in San Francisco\nEditing by Edward Tobin and Rosalba O'Brien)\n ((sarah.n.lynch@thomsonreuters.com; 202-354-5831;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL1N2IJ0BY")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-19T23:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 4-UK food retailers hand back $2.4 bln in property tax relief");
			hm1.put("TE",
					"<p>    * Sainsbury's to pay 440 mln stg in business rates\n    * Asda to pay 340 mln stg\n    * Aldi to pay 100 mln stg\n    * Market leader Tesco was first to say will pay tax\n    * Tesco CEO says move not calculated to damage competition\n\n (Adds B&M comment)\n    By James Davey\n    LONDON, Dec 3 (Reuters) - Sainsbury's, Asda, Aldi and B&M\nwill forgo UK property tax relief during the pandemic, following\nrivals Tesco  <Origin Href=\\\"QuoteRef\\\">TSCO.L</Origin>  and Morrisons  <Origin Href=\\\"QuoteRef\\\">MRW.L</Origin>  and taking the total\nrecouped by the government from retailers to 1.8 billion pounds\n($2.4 billion).\n    Britain's supermarket groups have seen sales soar during the\npandemic, but have been criticised by lawmakers and media for\npaying shareholder dividends while receiving tax relief.\n    Sainsbury's  <Origin Href=\\\"QuoteRef\\\">SBRY.L</Origin>  said on Thursday it would now pay 440\nmillion pounds of so-called business rates, while Walmart\n <Origin Href=\\\"QuoteRef\\\">WMT.N</Origin>  owned Asda will pay 340 million pounds and German-owned\nAldi will pay 100 million pounds.\n    Discounter B&M  <Origin Href=\\\"QuoteRef\\\">BMEB.L</Origin>  later said it would forego business\nrates relief worth around 80 million pounds in its current \nfinancial year.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL8N2IJ566</Origin>\n    The British government and devolved administrations in March\nexempted all retailers from paying the tax on their stores for\nthe 2020/21 financial year to help them through the crisis.\n    However, on Wednesday, market leader Tesco said it would\nrepay the 585 million pounds it had claimed because some of the\nrisks of the crisis were now behind it, and returning the money\nwas \"the right thing to do\".  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2II0F4</Origin>\n    That stance put pressure on rivals to do the same.\n    Morrisons had followed, saying it would pay 274 million\npounds.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2II2G5</Origin>\n    Sainsbury's said it had performed ahead of expectations,\nparticularly since the start of the second national lockdown in\nEngland last month.\n    \"With regional restrictions likely to remain in place for\nsome time, we believe it is now fair and right to forgo the\nbusiness rates relief,\" CEO Simon Roberts said.\n    Asda CEO Roger Burnley said the group recognised there were\nother industries for whom the effects of COVID-19 would be much\nmore long lasting.\n    \n    PEER PRESSURE\n    Tesco CEO Ken Murphy denied its decision to pay was a\ncalculated one to damage competitors who do not share its\nfinancial strength.\n    \"When we made the decision, we didn't really think about the\ncompetition at all,\" he told Sky News.\n    Murphy also said the move was unconnected to Tesco's plan to\npay shareholders a 5 billion pound special dividend when the\nsale of its Asian business was completed.\n    The Co-operative Group  <Origin Href=\\\"QuoteRef\\\">42TE.L</Origin>  said it plans to review its\nposition at year-end, while discounter Lidl declined to comment.\n    M&S  <Origin Href=\\\"QuoteRef\\\">MKS.L</Origin>  and Waitrose owner, the John Lewis Partnership\n <Origin Href=\\\"NewsSearch\\\">JLPLC.UL</Origin> , have said they will not forgo it. Both groups sell a\nbroader range of products including clothing and homewares.\n <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL8N2II455</Origin>\n    A spokesman for British Prime Minister Boris Johnson told\nreporters the government welcomed any decision to repay support\n\"where it is no longer needed\".     \n    Taking account of the business rates it will now pay,\nSainsbury's forecast underlying pretax profit of at least 270\nmillion pounds ($363.99 million) in its 2020-21 year, and over\n586 million pounds in 2021-22.\n    It will prioritise dividend payments to shareholders over\ncutting debt in 2020-21, which will push back its timetable for\ndebt reduction.\n  ($1 = 0.7409 pounds)\n\n\n($1 = 0.7418 pounds)\n\n (Reporting by James Davey; additional reporting by Liz Piper;\nEditing by John Stonestreet/Mark Potter/Susan Fenton/Jane\nMerriman)\n ((james.davey@thomsonreuters.com))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL4N2IJ3GN")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-19T23:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Walmart to spend more than $700 mln on new round of employee bonuses");
			hm1.put("TE",
					"<p>    Dec 3 (Reuters) - Walmart Inc  <Origin Href=\\\"QuoteRef\\\">WMT.N</Origin>  said on Thursday it\nwould spend more than $700 million on bonuses as the retailer\ncompensates its U.S. employees for helping meet a surge in\ndemand for essential goods during the COVID-19 crisis.\n    The company, like many supermarket chains, was pushed to\nhire hundreds of thousands of new hourly workers during the\npandemic, incurring billions of dollars in costs to keep stores\noperating safely.\n    Walmart, which is also the largest employer in the United\nStates, said the additional spending brings the total 2020\nquarterly and special bonuses to more than $2.8 billion.\n    The latest bonus announcement includes $319 million in\nquarterly bonuses paid in November and about $388 million in\nspecial cash bonuses to be paid to full-time and part-time\nassociates later this month.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nBw5KdvCma</Origin>\n      \n\n (Reporting by Aishwarya Venugopal in Bengaluru; Editing by Anil\nD'Silva)\n ((Aishwarya.Venugopal@thomsonreuters.com; within U.S.\n+1-646-223-8780; outside U.S. +91 80 6749 2830;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nFWN2IJ0UZ")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-19T23:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"BRIEF-Walmart Announces More Than $700 Mln In Additional Associate Bonuses, Tops $2.8 Bln In Total Cash Bonuses To Associates In 2020");
			hm1.put("TE",
					"<p>    Dec 3 (Reuters) - Walmart Inc  <Origin Href=\\\"QuoteRef\\\">WMT.N</Origin> :\n    * WALMART ANNOUNCES MORE THAN $700 MILLION IN ADDITIONAL\nASSOCIATE\nBONUSES, TOPS $2.8 BILLION IN TOTAL CASH BONUSES TO ASSOCIATES\nIN 2020\n    * WALMART - COMPANY ALSO EXTENDS ASSOCIATE COVID EMERGENCY\nLEAVE\nPOLICY TO JULY 5, 2021\n    * WALMART - LATEST BONUS ANNOUNCEMENT INCLUDES $319 MILLION\nIN\nQUARTERLY BONUSES PAID IN ASSOCIATES' NOV. 25 PAYCHECKS\n    * WALMART - FULL AND PART-TIME ASSOCIATES ARE ELIGIBLE FOR\nBOTH\nBONUSES.\n\nSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:nBw5KdvCma</Origin> \nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">WMT.N</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL1N2IJ1P2")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-12-03T17:58:47-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 1-Loeb&apos;s Third Point funds gain in November, up double-digits YTD");
			hm1.put("TE",
					"<p> (Adds details on firm, positions, file photo available)\n    By Svea Herbst-Bayliss\n    BOSTON, Dec 3 (Reuters) - Daniel Loeb&apos;s Third Point LLC\nfunds posted strong gains in November to extend year-to-date\nreturns into the double digits after the billionaire investor\noverhauled his portfolio and the firm a few months ago.\n    The Third Point Offshore Fund gained 9.1% in November and is\nnow up 12.3% for the year, while the Third Point Ultra fund\ngained 12.1% last month and is now up 14.2%, according to a\nperformance update seen by an investor. \n    The gains were fueled mainly by investments in credit as\nwell as fundamental equity bets, particularly in the media and\ninternet and enterprise technology sectors.\n    Loeb added stocks including Amazon  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin> , Disney  <Origin Href=\\\"QuoteRef\\\">DIS.N</Origin> \nand Alibaba  <Origin Href=\\\"QuoteRef\\\">9988.HK</Origin>  <Origin Href=\\\"QuoteRef\\\">BABA.N</Origin>  during the year. During the third\nquarter he trimmed the stakes in Amazon and Disney slightly,\nregulatory filings show. \n    The gains represent a dramatic swing coming only months\nafter Third Point was caught off-guard by the coronavirus and\nmassive market sell-off to end the first quarter with\ndouble-digit losses. The Offshore fund lost 16% while the Ultra\nfund was down 21% at the end of the first quarter.\n    Third Point, which now oversees $14.8 billion in assets, has\nalways had a flexible investment mandate and while the firm has\nbeen known for some of its recent large activist bets, Loeb\nshifted course some this year.\n    In May, he took back the reins from Munib Islam who had been\npromoted to co-chief investment officer last year and had\nlargely been the face of Third Point&apos;s activism positions,\nincluding a bet this year on Prudential Plc  <Origin Href=\\\"QuoteRef\\\">PRU.L</Origin> .\n    Activism, which Third Point also calls constructivism,\nweighed on returns this year, the source said.\n    Third Point is handily outperforming the average hedge fund,\nwhich gained only 1.2% in the first ten months of 2020,\naccording to Hedge Fund Research data. More recent data is not\nyet available. \n\n (Reporting by Svea Herbst-Bayliss\nEditing by Nick Zieminski)\n ((svea.herbst@thomsonreuters.com; +617 856 4331; Reuters\nMessaging: svea.herbst.thomsonreuters.com@reuters.net))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20201203:nL1N2IJ1TX")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2020-11-19T23:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"UPDATE 1-More than 400 lawmakers from 34 countries back &apos;Make Amazon Pay&apos; campaign");
			hm1.put("TE",
					"<p> (Adds details from Amazon statement)\\\n    By Paul Sandle\\\n    LONDON, Dec 3 (Reuters) - More than 400 lawmakers from 34\\\ncountries have signed a letter to Amazon.com Inc  <Origin Href=\\\\\\\"QuoteRef\\\\\\\">AMZN.O</Origin>  boss\\\nJeff Bezos backing a campaign that claims the tech giant has\\\n&quot;dodged and dismissed … debts to workers, societies, and the\\\nplanet,&quot; organisers said.\\\n    The &quot;Make Amazon Pay&quot; campaign was launched on Nov. 27 - the\\\nannual Black Friday shopping bonanza - by a coalition of over 50\\\norganisations, with demands including improvements to working\\\nconditions and full tax transparency.\\\n    The letter&apos;s signatories include U.S. Congresswomen Ilhan\\\nOmar and Rashida Tlaib, former UK Labour Party leader Jeremy\\\nCorbyn and Vice President of the European Parliament Heidi\\\nHautala, co-convenors Progressive International and UNI Global\\\nUnion said.\\\n    &quot;We urge you to act decisively to change your policies and\\\npriorities to do right by your workers, their communities, and\\\nour planet,&quot; the letter said. \\\n    &quot;We stand ready to act in our respective legislatures to\\\nsupport the movement that is growing around the world to Make\\\nAmazon Pay.&quot;\\\n    Amazon, the world&apos;s biggest retailer, has faced criticism\\\nfor its tax practices before, including in the UK and the EU. It\\\nsays its profits remain low given retail is a highly\\\ncompetitive, low margin business and it invests heavily. \\\n    It said on Thursday that while it accepted scrutiny from\\\npolicymakers, many of the matters raised in the letter stemmed\\\nfrom misleading assertions.\\\n    &quot;Amazon has a strong track record of supporting our\\\nemployees, our customers, and our communities, including\\\nproviding safe working conditions, competitive wages and great\\\nbenefits,&quot; it said, adding it was &quot;paying billions of dollars in\\\ntaxes globally.&quot; The company has also pledged to be net carbon\\\nneutral by 2040.\\\n    Amazon grew rapidly during the pandemic, with sales soaring\\\nas restrictions to prevent the spread of the coronavirus closed\\\nbricks-and-mortar shops and sent consumers online.\\\n    Governments worldwide are considering tougher rules for big\\\ntech to assuage worries about competition.\\\n    The European Union, for example, last month charged Amazon\\\nwith damaging retail competition, alleging it used its size,\\\npower and data to gain an unfair advantage over smaller\\\nmerchants that sell on its online platform.  <Origin Href=\\\\\\\"StoryRef\\\\\\\">urn:newsml:reuters.com:*:nL1N2HW0TQ</Origin>\\\n    Amazon disagreed with the EU assertions, saying it\\\nrepresented less than 1% of the global retail market and there\\\nwere larger retailers in every country in which it operated.\\\n    \\\n\\\n (Reporting by Paul Sandle\\\nEditing by Nick Zieminski)\\\n ((paul.sandle@thomsonreuters.com; +44 20 7542 6843; Reuters\\\nMessaging: paul.sandle.thomsonreuters.com@reuters.net))</p>");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210517:nL2N2N41RT")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-17T19:06:22-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "US STOCKS-Wall St weighed down by falling tech stocks");
			hm1.put("TE",
					" (For a Reuters live blog on U.S., UK and European stock\nmarkets, click LIVE/ or type LIVE/ in a news window.)\n    * Discovery down after deal to merge with AT&amp;T&apos;s media unit\n    * ViacomCBS rises after report Soros scooped up stock\n    * Indexes down: Dow 0.23%, S&amp;P 0.42%, Nasdaq 0.83%\n\n (Adds details, comments; updates prices)\n    By Echo Wang\n    May 17 (Reuters) - Technology stocks pulled Wall Street&apos;s\nmain indexes lower on Monday, with the Nasdaq Composite index\n <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  falling about 1% as signs of growing inflationary\npressures raised concern about monetary policy tightening.\n    Six of the 11 major S&amp;P sectors declined, with technology\n <Origin Href=\\\"QuoteRef\\\">.SPLRCT</Origin>  shedding about 1.3%. Apple Inc  <Origin Href=\\\"QuoteRef\\\">AAPL.O</Origin>  and Microsoft\nCorp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin>  each fell more than 1%, weighing the most on the\nbenchmark S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  and the Nasdaq.\n    The S&amp;P 500 scored its biggest one-day jump in more than a\nmonth on Friday as investors picked up beaten-down stocks\nfollowing a pullback earlier in the week on worries about\ninflation and a sooner-than-expected tightening by the U.S.\nFederal Reserve.\n    &quot;What is causing the decline, no surprise to anybody, is the\nworry about inflation and interest rates,&quot; said Sam Stovall,\nchief investment strategist at CFRA Research in New York.\n    &quot;As a result that&apos;s causing the growth group, in particular\ntechnology and consumer discretionary stocks, to experience\nweakness, while some of the more value-oriented groups are\nholding up a bit better.&quot;\n    At 2:53 p.m. ET, the Dow Jones Industrial Average  <Origin Href=\\\"QuoteRef\\\">.DJI</Origin> \nfell 79.86 points, or 0.23%, to 34,302.27; the S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin> \nlost 17.38 points, or 0.42%, at 4,156.47; and the Nasdaq\nComposite  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  dropped 112.05 points, or 0.83%, to 13,317.93.\n    Earnings this week will be scrutinized for clues on whether\nrising prices had any impact on consumer demand and if retailers\ncan sustain their strong earnings momentum.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2N11NS</Origin>\n    Walmart Inc  <Origin Href=\\\"QuoteRef\\\">WMT.N</Origin> , home improvement chain Home Depot Inc\n <Origin Href=\\\"QuoteRef\\\">HD.N</Origin>  and department store operator Macy&apos;s Inc  <Origin Href=\\\"QuoteRef\\\">M.N</Origin>  are set to\nreport earnings on Tuesday, with Target Corp  <Origin Href=\\\"QuoteRef\\\">TGT.N</Origin>  Ralph\nLauren  <Origin Href=\\\"QuoteRef\\\">RL.N</Origin>  and TJX Cos  <Origin Href=\\\"QuoteRef\\\">TJX.N</Origin>  due later in the week.\n    With the earnings season at its tail end, overall earnings\nfor S&amp;P 500 companies are expected to have climbed 50.6% from a\nyear ago, according to Refinitiv IBES, the strongest pace in 11\nyears.\n    AT&amp;T Inc  <Origin Href=\\\"QuoteRef\\\">T.N</Origin> , owner of HBO and Warner Bros studios, and\nDiscovery Inc  <Origin Href=\\\"QuoteRef\\\">DISCA.O</Origin> , home to lifestyle TV networks such as\nHGTV and TLC, said on Monday they will combine their content\nassets to create a standalone global entertainment and media\nbusiness. AT&amp;T shares declined 0.4%, while Discovery fell about\n4.7%.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL2N2N308Q</Origin>\n    Cryptocurrency-related stocks like Marathon Digital\n <Origin Href=\\\"QuoteRef\\\">MARA.O</Origin> , Riot Blockchain  <Origin Href=\\\"QuoteRef\\\">RIOT.O</Origin>  and Coinbase  <Origin Href=\\\"QuoteRef\\\">COIN.O</Origin>  fell\nbetween 6% and 10% as bitcoin swung in volatile trading after\nTesla Inc  <Origin Href=\\\"QuoteRef\\\">TSLA.O</Origin>  boss Elon Musk tweeted about the carmaker&apos;s\nbitcoin holdings.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL2N2N408C</Origin>\n    Among the most active stocks on the NYSE were AT&amp;T  <Origin Href=\\\"QuoteRef\\\">T.N</Origin> ,\ndown 0.9% at $31.94; AMC Entertainment Holdings Inc  <Origin Href=\\\"QuoteRef\\\">AMC.N</Origin> , up\n7.2% at $13.92; and Ford Motor Co  <Origin Href=\\\"QuoteRef\\\">F.N</Origin> , up 1.9% at $12.07.\n    On the Nasdaq, the most active issues included Aerpio\nPharmaceuticals Inc  <Origin Href=\\\"QuoteRef\\\">ARPO.O</Origin> , up 37.1% at $1.59; Sundial Growers\n <Origin Href=\\\"QuoteRef\\\">SNDL.O</Origin> , up 4.5% at 74 cents; and Castor Maritime  <Origin Href=\\\"QuoteRef\\\">CTRM.O</Origin> , up\n10.9% at 44 cents.\n    Advancing issues outnumbered decliners on the NYSE by a\n1.02-to-1 ratio; on Nasdaq, a 1.20-to-1 ratio favored decliners.\n    The S&amp;P 500 posted 34 new 52-week highs and no new lows; the\nNasdaq Composite recorded 95 new highs and 49 new lows.  \n\n\n (Reporting by Echo Wang in New York; Additional reporting by\nMedha Singh and Sruthi Shankar in Bengaluru; Editing by\nSaumyadeb Chakrabarty, Maju Samuel and Richard Chang)\n ((e.wang@thomsonreuters.com))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210517:nPn2m2kqsa")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-17T14:00:11-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "First National Bank Expands in Charleston with Branch at Freshfields Village");
			hm1.put("TE",
					"For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210517:nPn2m2kqsa&amp;default-theme=true</Origin>\n\nFirst National Bank Expands in Charleston with Branch at Freshfields Village\nBuilds on Regional Presence with New Full-Service Branch and Adds ATMs in Charleston International Airport\n\nPR Newswire\n\nCHARLESTON, S.C., May 17, 2021\n\nCHARLESTON, S.C., May 17, 2021 /PRNewswire/ -- First National Bank, the\nlargest subsidiary of F.N.B. Corporation (NYSE: FNB), announced its continued\nexpansion in Charleston, SC, with an innovative branch at Freshfields Village,\nan open-air shopping and dining experience at the crossroads of Kiawah Island,\nSeabrook Island and Johns Island. FNB also deployed four ATMs in Charleston\nInternational Airport.\n\nThe new Freshfields Village location is FNB&apos;s third retail branch in the\nCharleston area. Along with the airport ATMs, the location builds on the\nCompany&apos;s successful expansion strategy, which leverages a strong commercial\nbanking presence and investments in technology to deliver a premium,\nfull-service customer experience. By the end of 2021, FNB has plans to have\nfive retail branch locations in the market in addition to its downtown\nregional hub.\n\n&quot;The exceptional team we have brought together in Charleston, coupled with\nprime retail locations, will enable us to continue to successfully execute on\nour expansion strategy in this dynamic market,&quot; said Vincent J. Delie Jr.,\nChairman, President and Chief Executive Officer of F.N.B. Corporation and\nFirst National Bank. &quot;We have already experienced considerable growth in the\nLowcountry over the last several years and are proud to further our commitment\nto the local community.&quot;\n\nSince entering the region in 2017, FNB has established significant commercial\nbanking and wealth management operations along with a retail network serving\nCharleston and upstate South Carolina. The Company was recently named a South\nCarolina Top Workplace\n(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&amp;l=en&amp;o=3165830-1&amp;h=4280450983&amp;u=https%3A%2F%2Fwww.fnb-online.com%2Fabout-us%2Fnewsroom%2Fpress-releases%2F2021%2Ffnb-corporations-banking-subsidiary-recognized-as-a-top-workplace--in-south-carolina-050321&amp;a=South+Carolina+Top+Workplace</Origin>)\nbased on employee feedback.\n\nLeonard &quot;Len&quot; L. Hutchison, III, Regional Market Executive and President of\nFNB&apos;s Charleston and South Carolina markets, added, &quot;FNB has established\nitself as a fixture in the Charleston community. The continued expansion of\nour physical network supplements our high-touch approach and even better\nequips our product specialists and bankers to deliver the full breadth of our\ncomprehensive consumer banking, commercial banking and wealth management\nsolutions.&quot;\n\nLocated at 398 Freshfields Drive, Johns Island, SC, FNB&apos;s new office utilizes\na modern concept branch design. Branch features include an ATM with\nTellerChat, which allows clients to use video chat technology to conduct\ntransactions with a representative during extended hours, as well as a remote\ndrive up ATM for added convenience. In addition, customers are able to shop\nand learn more about products and services using touchscreens and FNB&apos;s\ninteractive Solutions Center kiosk, which can also be explored online using\nthe Solutions Center e-store\n(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&amp;l=en&amp;o=3165830-1&amp;h=1682359195&amp;u=https%3A%2F%2Fwww.fnb-online.com%2Fsolutions-center&amp;a=Solutions+Center+e-store</Origin>)\n.\n\nCustomers and community members are invited to visit the Freshfields Village\nbranch to experience FNB&apos;s comprehensive products and solutions, including\npersonal and business banking, wealth management, private banking and\ninsurance services. Office hours are Monday to Thursday from 9:00 AM – 5:00\nPM and Friday from 9:00 AM – 6:00 PM. Plans for a ribbon-cutting and grand\nopening celebration will be communicated at a later date.\n\nAbout F.N.B. Corporation\n\nF.N.B. Corporation (NYSE: FNB), headquartered in Pittsburgh, Pennsylvania, is\na diversified financial services company operating in seven states and the\nDistrict of Columbia. FNB&apos;s market coverage spans several major metropolitan\nareas including: Pittsburgh, Pennsylvania; Baltimore, Maryland; Cleveland,\nOhio; Washington, D.C.; and Charlotte, Raleigh, Durham and the Piedmont Triad\n(Winston-Salem, Greensboro and High Point) in North Carolina. The Company has\ntotal assets of more than $38 billion and nearly 340 banking offices\nthroughout Pennsylvania, Ohio, Maryland, West Virginia, North Carolina, South\nCarolina, Washington, D.C. and Virginia.\n\nFNB provides a full range of commercial banking, consumer banking and wealth\nmanagement solutions through its subsidiary network which is led by its\nlargest affiliate, First National Bank of Pennsylvania, founded in 1864.\nCommercial banking solutions include corporate banking, small business\nbanking, investment real estate financing, government banking, business\ncredit, capital markets and lease financing. The consumer banking segment\nprovides a full line of consumer banking products and services, including\ndeposit products, mortgage lending, consumer lending and a complete suite of\nmobile and online banking services. FNB&apos;s wealth management services include\nasset management, private banking and insurance.\n\nThe common stock of F.N.B. Corporation trades on the New York Stock Exchange\nunder the symbol &quot;FNB&quot; and is included in Standard &amp; Poor&apos;s MidCap 400\nIndex with the Global Industry Classification Standard (GICS) Regional Banks\nSub-Industry Index. Customers, shareholders and investors can learn more about\nthis regional financial institution by visiting the F.N.B. Corporation website\nat <Origin Href=\\\"Link\\\">www.fnbcorporation.com</Origin>\n(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&amp;l=en&amp;o=3165830-1&amp;h=3724422096&amp;u=http%3A%2F%2Fwww.fnbcorporation.com%2F&amp;a=www.fnbcorporation.com</Origin>)\n.\n\nView original\ncontent:<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/first-national-bank-expands-in-charleston-with-branch-at-freshfields-village-301292573.html</Origin>\n(<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/first-national-bank-expands-in-charleston-with-branch-at-freshfields-village-301292573.html</Origin>)\n\nSOURCE F.N.B. Corporation\n\n\n\nMedia Contact:  Jennifer Reel, 724-983-4856, 724-699-6389 (cell), reel@fnb-corp.com or Analyst/Institutional Investor Contact: Matthew Lazzaro, 724-983-4254, 412-216-2510 (cell), lazzaro@fnb-corp.com\n\nWebsite: \n<Origin Href=\\\"Link\\\">http://www.fnbcorporation.com</Origin>\n\nCopyright (c) 2021 PR Newswire Association,LLC. All Rights Reserved.");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210517:nL3N2N41OT")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-17T08:09:55-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Chinese automaker Changan aims to list EV unit on STAR Market -sources");
			hm1.put("TE",
					"By Yilei Sun and Tony Munroe\n    BEIJING, May 17 (Reuters) - Chinese state-run automaker\nChongqing Changan Automobile  <Origin Href=\\\"QuoteRef\\\">000625.SZ</Origin>  plans to list its\nelectric vehicle (EV) unit on Shanghai&apos;s Nasdaq-style STAR\nMarket, three sources briefed on the matter said, to fund a\nrapid expansion of its business. \n    Changan, based in the southwestern city of Chongqing, holds\n48.95% of the unit, which makes entry-level and mass-market\nelectric vehicles. The unit aims to sell over 500,000 EVs a year\nin 2025 and one million in 2030, Changan said during a recent\nbriefing with investors. \n    This year Changan units plans to sell more than 70,000\nelectric cars. It plans to make its EV business profitable by\n2024 but has not set a time frame for the STAR listing. \n    Two of the sources attended the briefing and the third had\ndirect knowledge of the presentation. They all declined to be\nnamed as they are not allowed to speak to media. \n    Changan did not respond to a request for comment. \n    To reach its sales target, Changan will develop two or three\nmodel platforms for EVs including small cars to win market share\nin smaller Chinese cities. \n    General Motors Co&apos;s  <Origin Href=\\\"QuoteRef\\\">GM.N</Origin>  China venture with SAIC Motor\n <Origin Href=\\\"QuoteRef\\\">600104.SS</Origin>  dominates the small-car segment with its micro\ntwo-door Wuling Hong Guang MINI EV.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2M61I4</Origin>\n    Changan also plans to develop electric light commercial and\nhydrogen fuel cell vehicles, the three sources said. \n    Parent Changan group makes gasoline and electric cars\nthrough partnerships with Ford Motor Co  <Origin Href=\\\"QuoteRef\\\">F.N</Origin>  and Mazda\n <Origin Href=\\\"QuoteRef\\\">7261.T</Origin> . It also has a partnership with telecoms gear giant\nHuawei Technologies  <Origin Href=\\\"NewsSearch\\\">HWT.UL</Origin>  and battery maker CATL  <Origin Href=\\\"QuoteRef\\\">300750.SZ</Origin> ,\nand makes self-branded cars as well. \n    China, where more than 25 million vehicles were sold last\nyear, is the world&apos;s biggest auto market. It is also a big\npromoter of the EV sector. \n    Sales of electric, plug-in hybrid and hydrogen-powered\nvehicles in China are forecast to rise to 20% of all new car\nsales by 2025 from 5% last year.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2HO0KA</Origin> \n    Nio Inc  <Origin Href=\\\"QuoteRef\\\">NIO.N</Origin> , Xpeng Inc  <Origin Href=\\\"QuoteRef\\\">XPEV.N</Origin>  and Tesla Inc  <Origin Href=\\\"QuoteRef\\\">TSLA.O</Origin> \nare all expanding sales in China, encouraged by a state policy\nof promoting greener vehicles to cut pollution. \n\n (Reporting by Yilei Sun and Tony Munroe; Editing by Tom Hogue)\n ((Y.Sun@thomsonreuters.com; +86 10 66271262; Reuters Messaging:\ny.sun.thomsonreuters.com@reuters.net))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210516:nL1N2N10NH")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-16T22:01:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "European climate group says EU needs far tougher van CO2 targets");
			hm1.put("TE",
					"By Nick Carey\n    May 17 (Reuters) - The European Union needs to dramatically\ntoughen weak CO2 targets for commercial vans to spur a shift to\nelectric models and phase out fossil-fuel sales entirely by\n2035, European campaign group Transport and Environment (T&amp;E)\nsaid on Monday.\n    T&amp;E said an analysis of van sales in 2020 showed no change\nin CO2 emissions from 2017 and found the EU&apos;s CO2 targets are so\nweak that most manufacturers can meet them without selling a\nsingle zero-emission van.\n    &quot;Standards which entered into force at the beginning of 2020\nwere supposed to make vans cleaner, but vanmakers have had to do\nalmost nothing to reach them,&quot; T&amp;E freight manager James Nix\nsaid in a statement. &quot;With pathetic CO2 targets, the boom in\ne-commerce is becoming a nightmare for our planet.&quot;\n    EU sales of electric and plug-in hybrid passenger cars\nalmost trebled to over 1 million vehicles last year, accounting\nfor more than 10% of overall sales, thanks to stringent CO2\ntargets and government subsidies.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2KA0T4</Origin>\n    But electric van sales have languished at about 2% of the\nmarket.\n    T&amp;E said the EU needs to bring forward its current CO2\nreduction target of 31% forward to 2027 from 2030 and aim for a\nfar more ambitious target of at least a 60% reduction by 2030.\n    The group said the EU should set a 100% CO2 reduction target\nby 2035, effectively banning combustion engine vans.\n    T&amp;E said the EU should prevent van makers from building\nplug-in hybrid (PHEV) vans. Groups like T&amp;E are pushing for PHEV\npassenger car models to be phased out in the next few years,\narguing that owners do not charge them properly and rely too\nmuch on the fossil-fuel engine.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2LK2OM</Origin>\n    Few automakers have developed PHEV commercial vans, but Ford\nMotor Co  <Origin Href=\\\"QuoteRef\\\">F.N</Origin>  said in March that the next iteration of its\nTransit van will include a plug-in hybrid version.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2LE0PU</Origin>\n    \n\n (Reporting By Nick Carey; editing by David Evans)\n ((nick.carey@thomsonreuters.com; +44 7385 414 954;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210518:nD5N2K201R")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-18T06:16:30-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Emirates NBD hires banks for AT1 dollar bonds - document");
			hm1.put("TE",
					"DUBAI, May 18 (Reuters) - Dubai&apos;s biggest lender Emirates\nNBD  <Origin Href=\\\"QuoteRef\\\">ENBD.DU</Origin>  has hired banks to arrange the sale of U.S.\ndollar-denominated Additional Tier 1 bonds that will be\nnon-callable for six years, a document showed on Tuesday.\n    Emirates NBD Capital, First Abu Dhabi Bank  <Origin Href=\\\"QuoteRef\\\">FAB.AD</Origin> ,\nJPMorgan  <Origin Href=\\\"QuoteRef\\\">JPM.N</Origin> , HSBC  <Origin Href=\\\"QuoteRef\\\">HSBA.L</Origin> , NCB Capital  <Origin Href=\\\"QuoteRef\\\">1180.SE</Origin>  and\nStandard Chartered  <Origin Href=\\\"QuoteRef\\\">STAN.L</Origin>  will arrange fixed-income investor\ncalls starting on Tuesday, the document from one of the banks\nshowed. A benchmark unrated issuance will follow, subject to\nmarket conditions.\n\n (Reporting by Yousef Saba; Editing by Andrew Heavens)\n ((Yousef.Saba@thomsonreuters.com; +971562166204; <Origin Href=\\\"Link\\\">https://twitter.com/YousefSaba</Origin>))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210517:nFWN2N41H0")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-17T21:18:11-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-JPMorgan Chase Declares Common Stock Dividend");
			hm1.put("TE",
					"May 17 (Reuters) - JPMorgan Chase &amp; Co  <Origin Href=\\\"QuoteRef\\\">JPM.N</Origin> :\n    * JPMORGAN CHASE DECLARES COMMON STOCK DIVIDEND\n    * JPMORGAN - DECLARED A QUARTERLY DIVIDEND OF 90 CENTS PER\nSHARE\nON THE OUTSTANDING SHARES OF THE COMMON STOCK OF JPMORGAN CHASE\n\nSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:nBw9Pdd5Na</Origin> \nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">JPM.N</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210517:nFWN2N40SA")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-17T10:44:52-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-Jpmorgan Chase Says Credit Card Charge-Off Rate 1.97% In April Versus 2.03% In March");
			hm1.put("TE",
					"May 17 (Reuters) - JPMorgan Chase &amp; Co  <Origin Href=\\\"QuoteRef\\\">JPM.N</Origin> :\n    * JPMORGAN CHASE &amp; CO - CREDIT CARD DELINQUENCY RATE 0.78% \nAT\nAPRIL END VERSUS 0.89%  AT MARCH END\n    * JPMORGAN CHASE &amp; CO - CREDIT CARD CHARGE-OFF RATE 1.97% IN\nAPRIL\nVERSUS 2.03% IN MARCH - SEC FILING\n\nSource text fo(<Origin Href=\\\"Link\\\">https://bit.ly/3eSEVVR</Origin>)\nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">JPM.N</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210518:nRSR9607Ya")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-18T06:10:06-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "REG - iShares III BLK MAM£  - Net Asset Value(s)");
			hm1.put("TE",
					"For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210518:nRSR9607Ya&amp;default-theme=true</Origin>\n\nRNS Number : 9607Y  iShares III BLK MAM£  18 May 2021\n FUND:                       BlackRock ESG Multi-Asset Moderate Portfolio UCITS ETF GBP Hedged (Acc)\n DEALING DATE:               17-May-21\n NAV PER SHARE:              Official NAV GBP 5.47538\n NUMBER OF SHARES IN ISSUE:  319,978\n CODE:                       MAMG LN\n ISIN:                       IE00BLLZQ797\n DISCLAIMER:                 All information provided by BlackRock is purely of an indicative nature and\n                             subject to change without notice at any time. The information is for guidance\n                             only and does not represent an offer, investment advice or any kind of\n                             financial service. Although BlackRock has obtained the information provided\n                             from sources that should be considered reliable, BlackRock cannot guarantee\n                             its accuracy, completeness or that it is fit for any particular purpose. The\n                             information provided does not confer any rights.\n                             Past performance is not a guide to future performance.  The value of\n                             investments and the income from them can fall as well as rise and is not\n                             guaranteed.  You may not get back the amount originally invested.  Changes\n                             in the rates of exchange between currencies may cause the value of investments\n                             to diminish or increase.  Fluctuation may be particularly marked in the case\n                             of a higher volatility fund or segregated account and the value of an\n                             investment may fall suddenly and substantially.  Levels and basis of taxation\n                             may change from time to time. iShares® and BlackRock® are registered\n                             trademarks of BlackRock, Inc., or its subsidiaries in the United States and\n                             elsewhere.\n                             © 2021 BlackRock Advisors (UK) Limited, authorised and regulated by the\n                             Financial Conduct Authority.  Registered office: 12 Throgmorton Avenue,\n                             London, EC2N 2DL.  Tel: 020 7743 3000.  Registered in England No. 00796793.\n\nThis information is provided by RNS, the news service of the London Stock Exchange. RNS is approved by the Financial Conduct Authority to act as a Primary Information Provider in the United Kingdom. Terms and conditions relating to the use and distribution of this information may apply. For further information, please contact\nrns@lseg.com (mailto:rns@lseg.com)\n or visit\n<Origin Href=\\\"Link\\\">www.rns.com</Origin> (<Origin Href=\\\"Link\\\">http://www.rns.com/</Origin>)\n.\n\nRNS may use your IP address to confirm compliance with the terms and conditions, to analyse how you engage with the information contained in this communication, and to share such analysis on an anonymised basis with others as part of our commercial services. For further information about how RNS and the London Stock Exchange use the personal data you provide us, please see our\nPrivacy Policy (<Origin Href=\\\"Link\\\">https://www.lseg.com/privacy-and-cookie-policy</Origin>)\n.   END  NAVGPUWCAUPGUPR\n\n");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210518:nRSR9608Ya")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-18T06:10:06-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "REG - iShares III BLK MAG£  - Net Asset Value(s)");
			hm1.put("TE",
					"For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210518:nRSR9608Ya&amp;default-theme=true</Origin>\n\nRNS Number : 9608Y  iShares III BLK E MAG £HGD £  18 May 2021    FUND:                       BlackRock ESG Multi-Asset Growth Portfolio UCITS ETF GBP Hedged (Acc)\n DEALING DATE:               17-May-21\n NAV PER SHARE:              Official NAV GBP 5.699912\n NUMBER OF SHARES IN ISSUE:  870,415\n CODE:                       MAGG LN\n ISIN:                       IE00BLLZQ912\n DISCLAIMER:                 All information provided by BlackRock is purely of an indicative nature and\n                             subject to change without notice at any time. The information is for guidance\n                             only and does not represent an offer, investment advice or any kind of\n                             financial service. Although BlackRock has obtained the information provided\n                             from sources that should be considered reliable, BlackRock cannot guarantee\n                             its accuracy, completeness or that it is fit for any particular purpose. The\n                             information provided does not confer any rights.\n                             Past performance is not a guide to future performance.  The value of\n                             investments and the income from them can fall as well as rise and is not\n                             guaranteed.  You may not get back the amount originally invested.  Changes\n                             in the rates of exchange between currencies may cause the value of investments\n                             to diminish or increase.  Fluctuation may be particularly marked in the case\n                             of a higher volatility fund or segregated account and the value of an\n                             investment may fall suddenly and substantially.  Levels and basis of taxation\n                             may change from time to time. iShares® and BlackRock® are registered\n                             trademarks of BlackRock, Inc., or its subsidiaries in the United States and\n                             elsewhere.\n                             © 2021 BlackRock Advisors (UK) Limited, authorised and regulated by the\n                             Financial Conduct Authority.  Registered office: 12 Throgmorton Avenue,\n                             London, EC2N 2DL.  Tel: 020 7743 3000.  Registered in England No. 00796793.\n\nThis information is provided by RNS, the news service of the London Stock Exchange. RNS is approved by the Financial Conduct Authority to act as a Primary Information Provider in the United Kingdom. Terms and conditions relating to the use and distribution of this information may apply. For further information, please contact\nrns@lseg.com (mailto:rns@lseg.com)\n or visit\n<Origin Href=\\\"Link\\\">www.rns.com</Origin> (<Origin Href=\\\"Link\\\">http://www.rns.com/</Origin>)\n.\n\nRNS may use your IP address to confirm compliance with the terms and conditions, to analyse how you engage with the information contained in this communication, and to share such analysis on an anonymised basis with others as part of our commercial services. For further information about how RNS and the London Stock Exchange use the personal data you provide us, please see our\nPrivacy Policy (<Origin Href=\\\"Link\\\">https://www.lseg.com/privacy-and-cookie-policy</Origin>)\n.   END  NAVGPUWCAUPGUPR\n\n");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210518:nRSR9606Ya")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-18T06:10:06-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "REG - iShares III BLK E £  - Net Asset Value(s)");
			hm1.put("TE",
					"For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210518:nRSR9606Ya&amp;default-theme=true</Origin>\n\nRNS Number : 9606Y  iShares III BLK E MAC £HGD £  18 May 2021    FUND:                       BlackRock ESG Multi-Asset Conservative Portfolio UCITS ETF GBP Hedged (Acc)\n DEALING DATE:               17-May-21\n NAV PER SHARE:              Official NAV GBP 5.156557\n NUMBER OF SHARES IN ISSUE:  133,506\n CODE:                       MACG LN\n ISIN:                       IE00BLP53N06\n DISCLAIMER:                 All information provided by BlackRock is purely of an indicative nature and\n                             subject to change without notice at any time. The information is for guidance\n                             only and does not represent an offer, investment advice or any kind of\n                             financial service. Although BlackRock has obtained the information provided\n                             from sources that should be considered reliable, BlackRock cannot guarantee\n                             its accuracy, completeness or that it is fit for any particular purpose. The\n                             information provided does not confer any rights.\n                             Past performance is not a guide to future performance.  The value of\n                             investments and the income from them can fall as well as rise and is not\n                             guaranteed.  You may not get back the amount originally invested.  Changes\n                             in the rates of exchange between currencies may cause the value of investments\n                             to diminish or increase.  Fluctuation may be particularly marked in the case\n                             of a higher volatility fund or segregated account and the value of an\n                             investment may fall suddenly and substantially.  Levels and basis of taxation\n                             may change from time to time. iShares® and BlackRock® are registered\n                             trademarks of BlackRock, Inc., or its subsidiaries in the United States and\n                             elsewhere.\n                             © 2021 BlackRock Advisors (UK) Limited, authorised and regulated by the\n                             Financial Conduct Authority.  Registered office: 12 Throgmorton Avenue,\n                             London, EC2N 2DL.  Tel: 020 7743 3000.  Registered in England No. 00796793.\n\nThis information is provided by RNS, the news service of the London Stock Exchange. RNS is approved by the Financial Conduct Authority to act as a Primary Information Provider in the United Kingdom. Terms and conditions relating to the use and distribution of this information may apply. For further information, please contact\nrns@lseg.com (mailto:rns@lseg.com)\n or visit\n<Origin Href=\\\"Link\\\">www.rns.com</Origin> (<Origin Href=\\\"Link\\\">http://www.rns.com/</Origin>)\n.\n\nRNS may use your IP address to confirm compliance with the terms and conditions, to analyse how you engage with the information contained in this communication, and to share such analysis on an anonymised basis with others as part of our commercial services. For further information about how RNS and the London Stock Exchange use the personal data you provide us, please see our\nPrivacy Policy (<Origin Href=\\\"Link\\\">https://www.lseg.com/privacy-and-cookie-policy</Origin>)\n.   END  NAVDZGMKZRFGMZG\n\n");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210518:nL5N2N43A7")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-18T06:17:46-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Italy - Factors to watch on May 18");
			hm1.put("TE",
					"The following factors could affect Italian markets on Tuesday.\n    Reuters has not verified the newspaper reports, and cannot\nvouch for their accuracy. New items are marked with (*).\n    \n    For a complete list of diary events in Italy please click on\n <Origin Href=\\\"QuoteRef\\\">IT/DIA</Origin> .\n    \n    GENERAL\n    Italy&apos;s government on Monday approved a decree pushing back\nwith immediate effect a nightly coronavirus curfew to 11 p.m.\nfrom 10 p.m. and easing other curbs in the regions where\ninfections are low.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL5N2N45AF</Origin>\n    Italy reported 140 coronavirus-related deaths on Monday\nagainst 93 the day before, the health ministry said, while the\ndaily tally of new infections fell to 3,455 from 5,753.\n <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nS8N2MD06X</Origin>\n    \n    DEBT\n    Italy&apos;s Treasury said on Monday it will kick off the planned\nrepurchase agreements (Repo) project on May 24 in a move meant\nto help it manage its liquidity and enrich cash management\ninstruments already in use.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nS8N2MD071</Origin>\n    \n    ECONOMY\n    ISTAT releases March foreign trade data (0800 GMT).\n    \n    COMPANIES\n    MEDIOBANCA  <Origin Href=\\\"QuoteRef\\\">MDBI.MI</Origin> \n    Former Italian Prime Minister Silvio Berlusconi&apos;s family has\nsold its 2% stake in Mediobanca, in the latest change affecting\nthe shareholder base of Italy&apos;s top investment bank.\n <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL5N2N45LX</Origin>\n    (*) The stake was bought by Leonardo Del Vecchio&apos;s holding\ncompany Delfin, various Italian dailies reported.\n    \n    ASSICURAZIONI GENERALI  <Origin Href=\\\"QuoteRef\\\">GASI.MI</Origin> \n    First-quarter profits at Italy&apos;s top insurer Generali beat\nanalysts&apos; expectations due to the positive contribution from the\nnon-life and asset management businesses and as the life segment\nproved resilient despite low interest rates.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL5N2N50UE</Origin>\n    (*) Generali could introduce the role of general manager\nfollowing requests to change its governance structure put\nforward by its second-largest investor Francesco Gaetano\nCaltagirone and divergences among shareholders over the renewal\nof the board, Il Sole 24 Ore reported. The paper said a chairman\nwith strong international ties could also be considered to\nfoster Generali&apos;s overseas growth.\n    The decision over whether Generali&apos;s outgoing board could\nsubmit a slate of nominees for its renewal could be taken at a\nboard meeting next month, Corriere della Sera reported.\n    Analyst call on first quarter earnings (1000 GMT)\n        \n    STELLANTIS  <Origin Href=\\\"QuoteRef\\\">STLA.MI</Origin> \n    The world&apos;s fourth largest carmaker and Foxconn said they\nplan to announce a strategic partnership on Tuesday, adding to a\nstring of such deals by the Taiwanese iPhone assembler.\n    The two companies will hold a call on Tuesday to present the\npartnership, with Stellantis CEO Carlos Tavares and Foxconn\nChairman Young Liu (0945 GMT). <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL5N2N41D9</Origin>\n    \n    MEDIASET  <Origin Href=\\\"QuoteRef\\\">MS.MI</Origin> \n    Leading French broadcaster TF1  <Origin Href=\\\"QuoteRef\\\">TFFP.PA</Origin>  will combine with\nsecond-ranked rival M6 under a deal announced on Monday by their\nrespective owners Bouygues  <Origin Href=\\\"QuoteRef\\\">BOUY.PA</Origin>  and RTL Group, a division\nof Germany&apos;s Bertelsmann  <Origin Href=\\\"QuoteRef\\\">BTGGg.F</Origin> .  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL2N2N42DK</Origin>\n        \n    SAIPEM  <Origin Href=\\\"QuoteRef\\\">SPMI.MI</Origin> \n    The Italian oil services group said on Monday it had entered\nexclusive talks with France&apos;s Naval Group for the acquisition of\nNaval Energies&apos; floating wind business.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nS8N2MD06Z</Origin>\n    \n    (*) TELECOM ITALIA  <Origin Href=\\\"QuoteRef\\\">TLIT.MI</Origin> \n    Telecoms group Iliad  <Origin Href=\\\"QuoteRef\\\">ILD.PA</Origin>  said on Tuesday it expected to\nmake a profit from its Italian activities sooner than expected,\nthanks to mobile subscriber gains and a quick network\nrollout. <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL5N2N50KU</Origin>\n    Iliad&apos;s subscribers in Italy totalled 7.54 million at the\nend of March up from 7.24 million three months earlier, lifting\nrevenues to 188 million euros from 150 million euros a year ago.\n <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nFWN2N41FD</Origin> \n    \n    CNH INDUSTRIAL  <Origin Href=\\\"QuoteRef\\\">CNHI.MI</Origin> \n    The group on Monday priced a $600 million 2026 bond at\n99.208.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nFWN2N41GZ</Origin>\n    \n    (*) INTESA SANPAOLO  <Origin Href=\\\"QuoteRef\\\">ISP.MI</Origin>  \n    The Italian banking group&apos;s asset management unit Eurizon\nreported a 60% increase in net income for the first quarter to\n161 million euros. CEO Saverio Perissinotto told Il Sole 24 Ore\ndaily that Eurizon is fully focused on completing the\nintegration of Pramerica - a joint-venture UBI, now part of\nIntesa, had with Prudential Financial  <Origin Href=\\\"QuoteRef\\\">PRU.N</Origin>  - in the second\nhalf of the year.\n    \n    CREDEM  <Origin Href=\\\"QuoteRef\\\">EMBI.MI</Origin> \n    The lender said on Monday it had set an exchange ratio for\nthe acquisition of smaller rival CR Cento at 0.64 of its shares\nfor each CR Cento share.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nFWN2N40QG</Origin>\n    \n    GUALA CLOSURES  <Origin Href=\\\"QuoteRef\\\">GCL.MI</Origin> \n    Investindustrial starts takeover bid on Guala Closures\nshares; ends on June 7.\n    The board of the Italian bottle cap maker on Monday said the\n8.20 euro per share price offered in Investindustrial&apos;s takeover\nbid was fair.\n    \n    DIARY\n    Paris, Italy&apos;s PM Draghi expected to attend Africa summit.\n    Brescia, Italian President Sergio Mattarella attends\n&apos;Brescia University&apos; new academic year inauguration ceremony\n(0900 GMT).\n    Rome, Industry Minister Giancarlo Giorgetti attends news\nconference to present the annual catering report (0900 GMT).\n    Online presentation &quot;The future vision of Lamborghini\nAutomobiles&quot; with Chairman and CEO Stephan Winkelmann (0900\nGMT).\n    Online event on &quot;Protagonists. Governance and women&apos;s\nempowerment&quot; with Leonardo  <Origin Href=\\\"QuoteRef\\\">LDOF.MI</Origin>  CEO Alessandro Profumo\n(1000 GMT).    \n    Rome, welfare institute INPS President Pasquale Tridico\nspeaks via videoconference before Senate Education Committee on\nlaw decree &apos;Status of arts and culture workers&apos; (1100 GMT).\n    Rome, Italian Medicines Agency (AIFA) Director General\nNicola Magrini and head of Italy&apos;s Higher Health Council (CTS)\nFranco Locatelli informally speak via videoconference before\nSenate Health Committee on method of administration of COVID\nm-RNA vaccines (1100 GMT).\n    Rome, Italian Medicines Agency (AIFA) President Giorgio Palu\ninformally speaks via videoconference before Senate Health\nCommittee on method of administration of COVID m-RNA vaccines\n(1300 GMT). \n    Industry Minister Giancarlo Giorgetti attends via\nvideoconference Healthcare policy presentation (1600 GMT).\n    \n    \n    ((Milan newsroom, +39 02 6612 9507, fax +39 02 801149, \nmilan.newsroom@news.reuters.com))\n    \n    For Italian market data and news, click on codes in \nbrackets:\n\n    20 biggest gainers (in percentage)............ <Origin Href=\\\"QuoteRef\\\">.PG.MI</Origin> \n    20 biggest losers (in percentage)............. <Origin Href=\\\"QuoteRef\\\">.PL.MI</Origin> \n    FTSE IT allshare index  <Origin Href=\\\"QuoteRef\\\">.FTITLMS</Origin> \n    FTSE Mib index........  <Origin Href=\\\"QuoteRef\\\">.FTMIB</Origin> \n    FTSE Allstars index...  <Origin Href=\\\"QuoteRef\\\">.FTSTAR</Origin> \n    FTSE Mid Cap index....  <Origin Href=\\\"QuoteRef\\\">.FTITMC</Origin> \n    Block trades..........  <Origin Href=\\\"QuoteRef\\\">.BLK.MI</Origin> \n    Stories on Italy......  <Origin Href=\\\"QuoteRef\\\">IT-LEN</Origin> \n    \n    For pan-European market data and news, click on codes in \n brackets:\n  European Equities speed guide................... <Origin Href=\\\"QuoteRef\\\">EUR/EQUITY</Origin> \n  FTSEurofirst 300 index.............................. <Origin Href=\\\"QuoteRef\\\">.FTEU3</Origin> \n  DJ STOXX index...................................... <Origin Href=\\\"QuoteRef\\\">.STOXX</Origin> \n  Top 10 STOXX sectors........................... <Origin Href=\\\"QuoteRef\\\">.PGL.STOXXS</Origin> \n  Top 10 EUROSTOXX sectors...................... <Origin Href=\\\"QuoteRef\\\">.PGL.STOXXES</Origin> \n  Top 10 Eurofirst 300 sectors................... <Origin Href=\\\"QuoteRef\\\">.PGL.FTEU3S</Origin> \n  Top 25 European pct gainers....................... <Origin Href=\\\"QuoteRef\\\">.PG.PEUR</Origin> \n  Top 25 European pct losers........................ <Origin Href=\\\"QuoteRef\\\">.PL.PEUR</Origin> \n \n  Main stock markets:\n  Dow Jones............... <Origin Href=\\\"QuoteRef\\\">.DJI</Origin>   Wall Street report ..... <Origin Href=\\\"NewsSearch\\\">.N</Origin> \n  Nikkei 225............. <Origin Href=\\\"QuoteRef\\\">.N225</Origin>   Tokyo report............ <Origin Href=\\\"NewsSearch\\\">.T</Origin> \n  FTSE 100............... <Origin Href=\\\"QuoteRef\\\">.FTSE</Origin>   London report........... <Origin Href=\\\"NewsSearch\\\">.L</Origin> \n  Xetra DAX............. <Origin Href=\\\"QuoteRef\\\">.GDAXI</Origin>   Frankfurt market stories <Origin Href=\\\"NewsSearch\\\">.F</Origin> \n  CAC-40................. <Origin Href=\\\"QuoteRef\\\">.FCHI</Origin>   Paris market stories... <Origin Href=\\\"NewsSearch\\\">.PA</Origin> \n  World Indices..................................... <Origin Href=\\\"QuoteRef\\\">0#.INDEX</Origin> \n  Reuters survey of world bourse outlook......... <Origin Href=\\\"QuoteRef\\\">EQUITYPOLL1</Origin> \n  Western European IPO diary.......................... <Origin Href=\\\"QuoteRef\\\">WEUIPO</Origin> \n  European Asset Allocation........................ <Origin Href=\\\"NewsSearch\\\">EUR/ASSET</Origin> \n  Reuters News at a Glance: Equities............... <Origin Href=\\\"NewsSearch\\\">TOP/EQE</Origin> \n  Main currency report:............................... <Origin Href=\\\"NewsSearch\\\">FRX/</Origin> \n");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210315:nFWN2LD0VX")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-03-15T15:17:41-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"BRIEF-Fidelity Bank Lists 41.21 Bln Naira 8.5% Fixed Rate Unsecured Subordinated Bonds Due 2031");
			hm1.put("TE",
					"March 15 (Reuters) - Fidelity Bank PLC  <Origin Href=\\\"QuoteRef\\\">FIDELIT.LG</Origin> :\n    * NOTIFICATION OF LISTING OF 41.21 BILLION NAIRA 8.5% FIXED\nRATE\nUNSECURED SUBORDINATED BONDS DUE 2031 ON FMDQ SECURITIES\nEXCHANGE\n\nSource text for Eikon: [ID: <Origin Href=\\\"Link\\\">https://bit.ly/3rUjaZW]</Origin>\nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">FIDELIT.LG</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210517:nL2N2N41GJ")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-17T16:26:24-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Biden administration approved $735 million arms sale to Israel - sources");
			hm1.put("TE",
					"By Patricia Zengerle\n    WASHINGTON, May 17 (Reuters) - President Joe Biden&apos;s\nadministration approved the potential sale of $735 million in\nprecision-guided weapons to Israel, and congressional sources\nsaid on Monday that U.S. lawmakers were not expected to object\nto the deal despite violence between Israel and Palestinian\nmilitants.\n    Three congressional aides said Congress was officially\nnotified of the intended commercial sale on May 5, as part of\nthe regular review process before major foreign weapons sales\nagreements can go ahead. \n    The sale was first reported by the Washington Post.\n    Congress was informed of the planned sale in April, as part\nof the normal informal review process before of the formal\nnotification on May 5. Under U.S. law, the formal notification\nopens up a 15-day window for Congress to object to the sale,\nwhich is not expected despite the ongoing violence.\n    The sale of Joint Direct Attack Munitions, or JDAMs, made by\nBoeing Co  <Origin Href=\\\"QuoteRef\\\">BA.N</Origin> , was considered routine at the time, before the\nstart last week of the fiercest hostilities in the region in\nyears.\n    There were no objections at the time by the Democratic and\nRepublican leaders of the congressional foreign affairs\ncommittees that review such sales, aides said.\n    Asked for comment, a State Department spokesperson noted\nthat the department is restricted under federal law and\nregulations from publicly commenting on or confirming details of\nlicensing activity related to direct commercial sales like the\nJDAMs agreement.\n    &quot;We remain deeply concerned about the current violence and\nare working towards achieving a sustainable calm,&quot; the\nspokesperson said.\n    Strong support for Israel is a core value for both\nDemocratic and Republican members of the U.S. Congress, despite\ncalls from a few of the most progressive Democrats to take a\ntougher stance against the government of Israeli President\nBenjamin Netanyahu.\n    U.S. law allows Congress to object to weapons sales, but it\nis unlikely to do so in this case. Because Israel is among a\nhandful of countries whose military deals are approved under an\nexpedited process, the typical window for objecting will close\nbefore lawmakers can pass a resolution of disapproval, even if\nthey were inclined to.\n\n (Reporting by Patricia Zengerle; editing by Grant McCool)\n ((patricia.zengerle@thomsonreuters.com,\n<Origin Href=\\\"Link\\\">www.twitter.com/ReutersZengerle;</Origin> 001-202-898-8390;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210517:nL2N2N40YK")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-17T13:20:17-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 2-Emirates could swap Boeing 777X jets for smaller Dreamliners, chairman says");
			hm1.put("TE",
					"(Adds details, quotes)\n    DUBAI, May 17 (Reuters) - Emirates could swap some of its\norder for 126 Boeing  <Origin Href=\\\"QuoteRef\\\">BA.N</Origin>  777X jets for smaller 787\nDreamliners as part of a sweeping review of its future fleet\nrequirements, its chairman said on Monday.\n    The airline is currently in talks with the U.S. planemaker\nover its fleet planes, a review brought on by the coronavirus\npandemic, which has devastated the travel industry.\n    Asked if the airline could swap its orders to take fewer\n777X jets and more Dreamliners, Sheikh Ahmed bin Saeed Al\nMaktoum told reporters: &quot;It is always a possibility.&quot;\n    &quot;We are assessing our fleet requirements as we speak.&quot;\n    Emirates, the world&apos;s biggest long-haul airline before the\npandemic, has recently expressed frustration with Boeing over\nthe 777X programme, which is three years behind schedule, urging\nthe planemaker to share more details on the in-production jet.\n    Sheikh Ahmed said the delays had been &quot;tough&quot; for Boeing,\nwhich is emerging from its worst crisis after fatal crashes of\n737 MAX jets and a 20-month safety ban that has since been\nlifted.\n    Emirates reduced its order for 150 777X to 126 jets as part\nof a deal that saw the airline order 30 Dreamliners in 2019.\n    Sheikh Ahmed, who has headed Emirates since it was founded\nin 1985, did not say when the airline would make a decision on\nit future fleet.\n    The airline is due to report its annual results for the\nfinancial year ended March 31. \n    Sheikh Ahmed said it had been a very tough year, with the\nairline carrying around 30% of the 56.2 million passengers it\ncarried in the previous year, without providing further details.\n    He said he was optimistic for the upcoming summer travel\nseason, even as the airline was reviewing its cash reserves on a\nmonthly basis due to the deterioration in demand caused by the\npandemic.\n    &quot;A lot of people (who have) stopped travelling for the last\nyear and a half ... want to travel.&quot;\n    However, Sheikh Ahmed suggested the airline was taking a\nconservative approach to restoring capacity, telling reporters\nEmirates would only operate flights that make commercial sense.\n    &quot;We don&apos;t just open a route for the sake of opening or just\nfor a publicity reason.&quot;\n\n (Reporting by Alexander Cornwell; editing by Jason Neely and\nSteve Orlofsky)\n ((Alexander.Cornwell@thomsonreuters.com;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210517:nD5N2MD001")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-17T12:22:27-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Emirates could swap Boeing 777x for smaller Dreamliners, chairman says");
			hm1.put("TE",
					"DUBAI, May 17 (Reuters) - Emirates could swap some of its\norder for 126 Boeing  <Origin Href=\\\"QuoteRef\\\">BA.N</Origin>  777X jets for smaller 787\nDreamliners as part of a sweeping review of its future fleet\nrequirements, its chairman said on Monday.\n    &quot;We are assessing our fleet requirement as we speak,&quot; Sheikh\nAhmed bin Saeed al-Maktoum told reporters in Dubai.\n\n (Reporting by Alexander Cornwell; editing by Jason Neely)\n ((Alexander.Cornwell@thomsonreuters.com;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210517:nL2N2N40A4")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-17T06:32:58-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 1-Ryanair &apos;upset&apos; with Boeing, fears no MAX deliveries before summer");
			hm1.put("TE",
					"(Adds background, CEO quotes; details on possible MAX 10 order)\n    By Conor Humphries\n    DUBLIN, May 17 (Reuters) - Ryanair  <Origin Href=\\\"QuoteRef\\\">RYA.I</Origin>  fears it may not\ntake delivery of its first 737 MAX aircraft until after its peak\nsummer period and the Irish airline is &quot;quite upset with Boeing&quot;\n <Origin Href=\\\"QuoteRef\\\">BA.N</Origin> , Group Chief Executive Michael O&apos;Leary said on Monday. \n    But he said he believed the production issues would resolve\nin the medium term and said the Irish airline, Europe&apos;s largest\nlow-cost carrier, was in talks with Boeing about a significant\norder of the larger, 230-seat, MAX 10 aircraft. \n    The largest European customer of the MAX with 210 firm\norders of the 197-seat MAX200 model, Ryanair in late March said\nit expected that before summer it would take delivery of 16 of\nthe aircraft, down from an earlier forecast of 40. \n    But Boeing is now promising the first delivery of the jet,\nwhich has been delayed in part due to a recent electrical\ngrounding issue, in late May.\n    &quot;We are now being told the first delivery will be in late\nMay. I am not sure we necessarily believe that,&quot; O&apos;Leary said in\na pre-recorded presentation following the release of the\ncompany&apos;s full-year results. \n    &quot;As the management team in Seattle continues to mismanage\nthat process I think there is a real risk we might not see any\nof these aircraft in advance of summer 2021,&quot; O&apos;Leary said. \n    Ryanair was initially due to take delivery of its first MAX\ntwo years ago before the jet was grounded for 20 months after\ntwo fatal crashes. \n    The airline has agreed &quot;reasonable and fair&quot; compensation\nfor that delay, it said in its results statement on Monday. \n    Ryanair is confident it will have 60 of the aircraft in\nplace for the summer of 2022, he said. \n    Ryanair remains in talks with Boeing for a significant order\nof the larger MAX 10 aircraft, but &quot;we are not quite there on\nprice yet,&quot; Chief Financial Officer Neil Sorahan said in an\ninterview.\n    Asked how big such a deal would be, Sorahan said it would\ncover both fleet renewal and growth in the 2026 to 2030 period. \n    &quot;We don&apos;t tend to do small deals,&quot; he said.  \n\n (Reporting by Conor Humphries; editing by Christopher Cushing\nand Jason Neely)\n ((conor.humphries@thomsonreuters.com; +353 1 236 1915;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210517:nFWN2N40WN")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-17T16:05:51-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-American Express Reports Card Member Loan Stats for April");
			hm1.put("TE",
					"May 17 (Reuters) - American Express Co  <Origin Href=\\\"QuoteRef\\\">AXP.N</Origin> :\n    * AMERICAN EXPRESS- USCS CARD MEMBER LOANS 30 DAYS PAST DUE\nLOANS\nAS A % OF TOTAL 0.8% AT APRIL END VERSUS 0.9% AT MARCH END\n    * AMERICAN EXPRESS- USCS CARD MEMBER LOANS NET WRITE-OFF\nRATE-PRINCIPAL ONLY 1.0% AT APRIL END VERSUS 1.1% AT MARCH END\n    * AMERICAN EXPRESS-U.S. SMALL BUSINESS CARD MEMBER LOANS 30\nDAYS\nPAST DUE LOANS AS A% OF TOTAL 0.5% AT APRIL END VERSUS 0.6% AT\nMARCH END\n    * AMERICAN EXPRESS- U.S. SMALL BUSINESS CARD MEMBER LOANS\nNET\nWRITE-OFF RATE - PRINCIPAL ONLY 0.7% AT APRIL END VERSUS 0.7% AT\nMARCH END\n\nSource text : (<Origin Href=\\\"Link\\\">https://bit.ly/3tTvzxA</Origin>)\nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">AXP.N</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210423:nL1N2MG2F8")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-04-23T20:27:40-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "US STOCKS-Wall Street rallies on strong economic data; tech in focus");
			hm1.put("TE",
					"Stocks surge on strong outlook for earnings, economy\n    * Honeywell down on aerospace unit revenue miss \n    * Pinterest up as Credit Suisse raises price target\n    * Indexes close up: Dow 0.7%, S&amp;P 500 1.1%, Nasdaq 1.4%\n\n (Adds closing prices after 4 p.m. market close)\n    By Herbert Lash\n    April 23 (Reuters) - U.S. stocks rallied on Friday, driving\nthe S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  to a near-record closing high, after factory\ndata and new home sales underscored a booming economy while\nmegacap stocks rose in anticipation of strong earnings reports\nnext week.\n    The bounceback follows a sell-off on Thursday when reports\nthat U.S. President Joe Biden plans to almost double the capital\ngains tax spooked investors. Analysts dismissed the slide as a\nknee-jerk reaction and pointed to the strong outlook.\n    As the three major Wall Street indexes surged, the CBOE\nmarket volatility or &quot;fear&quot; index  <Origin Href=\\\"QuoteRef\\\">.VIX</Origin>  plunged almost 10% in a\nsign of tumbling investor anxiety about the risks ahead.\n    Companies are providing guidance after staying quiet during\nthe pandemic, while lower bond yields and results that beat\nestimates are driving the rally, said Tim Ghriskey, chief\ninvestment strategist at Inverness Counsel in New York.\n    &quot;There is a lot of anticipation of what&apos;s to come,&quot; he said.\n&quot;We&apos;ve seen actual reports beating these very high expectations.\nYields have come back down, which is very positive for tech.&quot;\n    Earnings take center stage next week when 40% of the S&amp;P\n500&apos;s market cap report on Tuesday through Thursday, including\nthe tech and related heavyweights of Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin> ,\nGoogle parent Alphabet Inc  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin> , Apple Inc  <Origin Href=\\\"QuoteRef\\\">AAPL.O</Origin>  and\nFacebook Inc  <Origin Href=\\\"QuoteRef\\\">FB.O</Origin> .\n    Those names, including Amazon.com Inc  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin> , supplied the\nbiggest upside to a broad-based rally in which advancing shares\neasily outpaced decliners.\n    Expectations for company results have steadily gained in\nrecent weeks as opposed to a typical decline as earnings season\napproaches. First-quarter earnings are expected to jump 33.9%\nfrom a year ago, the highest quarterly rate since the fourth\nquarter of 2010, according to IBES Refinitiv data.\n    U.S. factory activity powered ahead in early April. IHS\nMarkit&apos;s flash U.S. manufacturing PMI increased to 60.6 in the\nfirst half of this month, the highest reading since the series\nstarted in May 2007.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG17D</Origin>\n    In another sign of strong consumer demand, sales of new U.S.\nsingle-family homes rebounded more than expected in March,\nlikely boosted by an acute shortage of previously owned houses\non the market.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG0ZD</Origin>\n    All the 11 major S&amp;P 500 sectors were higher, with\ntechnology  <Origin Href=\\\"QuoteRef\\\">.SPLRCT</Origin>  and financials  <Origin Href=\\\"QuoteRef\\\">.SPSY</Origin>  leading gains.\n    Ron Temple, head of U.S. equity at Lazard Asset Management,\nsaid the U.S. economy is about to post the strongest growth in\n50 years, with more than 6% gains both this year and next.\n    The Federal Reserve will allow the economy to run hotter\nthan in the past, adding to the high-growth outlook.\n    &quot;Investors are gradually coming around to the sheer\nmagnitude of excess savings, pent-up demand and the implications\nof such a massive wave of fiscal stimulus,&quot; Temple said.\n    Stocks surged just before the bell, with the benchmark S&amp;P\n500 falling a bit to miss setting a record close.\n    The Dow Jones Industrial Average  <Origin Href=\\\"QuoteRef\\\">.DJI</Origin>  rose 0.67% to\n34,043.49 and the S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  gained 1.09% at 4,180.17, just\nbelow its previous closing high of 4,185.47 on April 16. The\nNasdaq Composite  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  added 1.44% at 14,016.81.\n    For the week, the S&amp;P 500 unofficially fell 0.13%, the Dow\nabout 0.46% and the Nasdaq 0.25%.\n    Some earnings reports on Friday were lackluster, with\nAmerican Express Co  <Origin Href=\\\"QuoteRef\\\">AXP.N</Origin>  sliding 1.9% after reporting a slump\nin credit spending and lower quarterly revenue.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG30U</Origin>\n    Honeywell International  <Origin Href=\\\"QuoteRef\\\">HON.N</Origin>  fell 2.1% after missing\nrevenue expectations in aerospace, its biggest business segment.\n    Naked Brand Group  <Origin Href=\\\"QuoteRef\\\">NAKD.O</Origin>  jumped 4.8% after shareholders\napproved the proposed divestiture of the company&apos;s Bendon\nbrick-and-mortar operations.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG36T</Origin>\n    Image sharing company Pinterest Inc  <Origin Href=\\\"QuoteRef\\\">PINS.N</Origin>  gained 4.2% as\nCredit Suisse raised its price target, saying newer product\nofferings and expanding footprint in markets abroad will yield\nhigher revenue and user growth.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG32O</Origin>\n    Advancing issues outnumbered declining ones on the NYSE by a\n3.62-to-1 ratio; on Nasdaq, a 2.82-to-1 ratio favored advancers.\n    The S&amp;P 500 posted 81 new 52-week highs and no new lows; the\nNasdaq Composite recorded 111 new highs and 20 new lows.  \n\n (Reporting by Herbert Lash; Additional reporting by Shivani\nKumaresan and Shreyashi Sanyal in Bengaluru; Editing by Sriraj\nKalluvila, Arun Koyyur and Richard Chang)\n ((herb.lash@thomsonreuters.com; 1-646-223-6019; Reuters\nMessaging: herb.lash.reuters.com@reuters.net))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210423:nL1N2MG22Q")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-04-23T20:00:45-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "US STOCKS-Wall Street rallies on strong economic data; tech in focus");
			hm1.put("TE",
					"Stocks surge on strong outlook for earnings, economy\n    * Honeywell down on aerospace unit revenue miss \n    * Pinterest up as Credit Suisse raises price target\n\n (Adds market close at 4 p.m.)\n    By Herbert Lash\n    April 23 (Reuters) - U.S. stocks rallied on Friday, driving\nthe S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  to a near-record closing high, after factory\ndata and new home sales underscored a booming economy while big\ntech stocks rose in anticipation of strong earnings reports next\nweek.\n    The bounceback follows a sell-off on Thursday when reports\nthat U.S. President Joe Biden plans to almost double the capital\ngains tax spooked investors. Analysts dismissed the slide as a\nknee-jerk reaction and pointed to the strong outlook.\n    As the three major Wall Street indexes surged, the CBOE\nmarket volatility or &quot;fear&quot; index  <Origin Href=\\\"QuoteRef\\\">.VIX</Origin>  plunged about 10% in a\nsign of tumbling investor anxiety about the risks ahead.\n    Companies are providing guidance after staying quiet during\nthe pandemic, while lower bond yields and results that beat\nestimates are driving the rally, said Tim Ghriskey, chief\ninvestment strategist at Inverness Counsel in New York.\n    &quot;There is a lot of anticipation of what&apos;s to come,&quot; he said.\n&quot;We&apos;ve seen actual reports beating these very high expectations.\nYields have come back down, which is very positive for tech.&quot;\n    Earnings take center stage next week when 40% of the S&amp;P\n500&apos;s market cap report on Tuesday through Thursday, including\nthe tech heavyweights of Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin> , Google parent\nAlphabet Inc  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin> , Apple Inc  <Origin Href=\\\"QuoteRef\\\">AAPL.O</Origin>  and Facebook Inc\n <Origin Href=\\\"QuoteRef\\\">FB.O</Origin> .\n    Those names, including Amazon.com Inc  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin> , supplied the\nbiggest upside to a rally in which advancing shares easily\noutpaced decliners.\n    First-quarter earnings are expected to jump 33.9% from a\nyear ago, the highest quarterly rate since the fourth quarter of\n2010, according to IBES Refinitiv data.\n    U.S. factory activity powered ahead in early April. IHS\nMarkit&apos;s flash U.S. manufacturing PMI increased to 60.6 in the\nfirst half of this month, the highest reading since the series\nstarted in May 2007.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG17D</Origin>\n    In another sign of strong consumer demand, sales of new U.S.\nsingle-family homes rebounded more than expected in March,\nlikely boosted by an acute shortage of previously owned houses\non the market.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG0ZD</Origin>\n    All the 11 major S&amp;P 500 sectors were higher, with\ntechnology  <Origin Href=\\\"QuoteRef\\\">.SPLRCT</Origin>  and financials  <Origin Href=\\\"QuoteRef\\\">.SPSY</Origin>  leading gains.\n    Ron Temple, head of U.S. equity at Lazard Asset Management,\nsaid the U.S. economy is about to post the strongest growth in\n50 years, with more than 6% gains both this year and next.\n    The Federal Reserve will allow the economy to run hotter\nthan in the past, adding to the high-growth outlook.\n    &quot;Investors are gradually coming around to the sheer\nmagnitude of excess savings, pent-up demand and the implications\nof such a massive wave of fiscal stimulus,&quot; Temple said.\n    Unofficially, the Dow Jones Industrial Average  <Origin Href=\\\"QuoteRef\\\">.DJI</Origin>  rose\n0.66% to 34,040.28 and the S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  gained 1.08% to\n4,179.73. The Nasdaq Composite  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  added 1.43% to 14,016.06.\n    Some earnings reports on Friday were lackluster, with\nAmerican Express Co  <Origin Href=\\\"QuoteRef\\\">AXP.N</Origin>  sliding after reporting a slump in\ncredit spending and lower quarterly revenue.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG30U</Origin>\n    Honeywell International  <Origin Href=\\\"QuoteRef\\\">HON.N</Origin>  fell after missing revenue\nexpectations in aerospace, its biggest business segment.\n    Naked Brand Group  <Origin Href=\\\"QuoteRef\\\">NAKD.O</Origin>  jumped after shareholders\napproved the proposed divestiture of the company&apos;s Bendon\nbrick-and-mortar operations.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG36T</Origin>\n    Image sharing company Pinterest Inc  <Origin Href=\\\"QuoteRef\\\">PINS.N</Origin>  gained as\nCredit Suisse raised its price target, saying newer product\nofferings and expanding footprint in markets abroad will yield\nhigher revenue and user growth.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG32O</Origin>\n\n (Reporting by Herbert Lash; Additional reporting by Shivani\nKumaresan and Shreyashi Sanyal in Bengaluru; Editing by Sriraj\nKalluvila, Arun Koyyur and Richard Chang)\n ((herb.lash@thomsonreuters.com; 1-646-223-6019; Reuters\nMessaging: herb.lash.reuters.com@reuters.net))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210423:nL4N2MG3KY")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-04-23T19:06:44-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "US STOCKS-Wall Street rallies on strong economic data; tech in focus");
			hm1.put("TE",
					"Stocks gain on strong outlook for earnings, economy\n    * Honeywell down on aerospace unit revenue miss \n    * Pinterest up as Credit Suisse raises price target\n    * Indexes up: Dow 0.7%, S&amp;P 500 1.2%, Nasdaq 1.6%\n\n (Adds mid-afternoon prices)\n    By Herbert Lash\n    April 23 (Reuters) - U.S. stocks rose in a broad rally on\nFriday as increased factory output and housing data supported\nexpectations of a swift economic recovery，while big tech stocks\nrose in anticipation of strong earnings reports next week.\n    The bounceback follows a sell-off on Thursday when reports\nthat U.S. President Joe Biden plans to almost double the capital\ngains tax spooked some investors. Analysts dismissed the\nsell-off as a knee-jerk reaction, saying equities are poised for\nnew highs.\n    The broad-based S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  rose more than 1%, trading\njust below what would be a new record close, while the\ntech-heavy Nasdaq  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  scored a bigger percentage gain.\n    Earnings take center stage next week when 40% of the S&amp;P\n500&apos;s market cap report on Tuesday through Thursday, including\nthe tech heavyweights of Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin> , Google parent\nAlphabet Inc  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin> , Apple Inc  <Origin Href=\\\"QuoteRef\\\">AAPL.O</Origin>  and Facebook Inc\n <Origin Href=\\\"QuoteRef\\\">FB.O</Origin> .\n    Those names, including Amazon.com Inc  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin> , supplied the\nbiggest upside to a rally where advancing shares easily outpaced\ndecliners.\n    Companies are providing guidance after staying quiet during\nthe pandemic, while lower bond yields and results that beat\nestimates are driving the rally, said Tim Ghriskey, chief\ninvestment strategist at Inverness Counsel in New York.\n    &quot;There is a lot of anticipation of what&apos;s to come,&quot; he said.\n&quot;We&apos;ve seen actual reports beating these very high expectations.\nYields have come back down, which is very positive for tech.&quot;\n    First-quarter earnings are expected to increase 33.9% from a\nyear ago, the highest quarterly rate since the fourth quarter of\n2010, according to IBES Refinitiv data.\n    U.S. factory activity powered ahead in early April. IHS\nMarkit&apos;s flash U.S. manufacturing PMI increased to 60.6 in the\nfirst half of this month, the highest reading since the series\nstarted in May 2007.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG17D</Origin>\n    In another sign of strong consumer demand, sales of new U.S.\nsingle-family homes rebounded more than expected in March,\nlikely boosted by an acute shortage of previously owned houses\non the market.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG0ZD</Origin>\n    All the 11 major S&amp;P 500 sectors were higher, with\ntechnology  <Origin Href=\\\"QuoteRef\\\">.SPLRCT</Origin>  and financials  <Origin Href=\\\"QuoteRef\\\">.SPSY</Origin>  leading gains.\n    Ron Temple, head of U.S. equity at Lazard Asset Management,\nsaid the U.S. economy is about to post the strongest growth in\nthe past 50 years, with more than 6% gains both this year and\nnext. The Federal Reserve, meanwhile, will allow the economy to\nrun hotter than in the past, adding to the high-growth outlook.\n    &quot;Investors are gradually coming around to the sheer\nmagnitude of excess savings, pent-up demand and the implications\nof such a massive wave of fiscal stimulus,&quot; Temple said.\n    The S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  gained 1.16% at 4,182.88, and the Dow\nJones Industrial Average  <Origin Href=\\\"QuoteRef\\\">.DJI</Origin>  rose 0.69% to 34,047.78. The\nNasdaq Composite  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  added 1.56% at 14,034.01. \n    Earnings reports in the day were lackluster, with American\nExpress Co  <Origin Href=\\\"QuoteRef\\\">AXP.N</Origin>  sliding 2.1% after reporting a slump in\ncredit spending and lower quarterly revenue.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG30U</Origin>\n    Honeywell International  <Origin Href=\\\"QuoteRef\\\">HON.N</Origin>  fell 2.0% after it missed\nrevenue expectations for its aerospace division, its biggest\nbusiness segment.\n    Naked Brand Group  <Origin Href=\\\"QuoteRef\\\">NAKD.O</Origin> , jumped 5.9% after shareholders\napproved the proposed divestiture of the company&apos;s Bendon\nbrick-and-mortar operations.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG36T</Origin>\n    Image sharing company Pinterest Inc  <Origin Href=\\\"QuoteRef\\\">PINS.N</Origin>  gained 2.6% as\nCredit Suisse raised its price target, saying its newer product\nofferings and expanding footprint in markets abroad will yield\nhigher revenue and user growth.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG32O</Origin>\n    Advancing issues outnumbered declining ones on the NYSE by a\n3.65-to-1 ratio; on Nasdaq, a 2.65-to-1 ratio favored advancers.\n    The S&amp;P 500 posted 71 new 52-week highs and no new lows; the\nNasdaq Composite recorded 94 new highs and 20 new lows.  \n\n (Reporting by Herbert Lash; Additional reporting by Shivani\nKumaresan and Shreyashi Sanyal in Bengaluru; Editing by Sriraj\nKalluvila, Arun Koyyur and Richard Chang)\n ((herb.lash@thomsonreuters.com; 1-646-223-6019; Reuters\nMessaging: herb.lash.reuters.com@reuters.net))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210514:nFWN2N10X4")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-14T21:47:16-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-Cisco Says On May 13 Entered Second Amended And Restated Credit Agreement");
			hm1.put("TE",
					"May 14 (Reuters) - Cisco Systems Inc  <Origin Href=\\\"QuoteRef\\\">CSCO.O</Origin> :\n    * CISCO - ON MAY 13, 2021, ENTERED SECOND AMENDED AND\nRESTATED\nCREDIT AGREEMENT\n    * CISCO - CREDIT AGREEMENT PROVIDES FOR A FIVE YEAR $3.0\nBILLION\nUNSECURED REVOLVING CREDIT FACILITY\n    * CISCO - CREDIT AGREEMENT IS AN AMENDMENT AND RESTATEMENT\nOF THAT\nCERTAIN AMENDED AND RESTATED CREDIT AGREEMENT, DATED AS OF MAY\n15, 2020\n\nSource: (<Origin Href=\\\"Link\\\">https://bit.ly/3y7xlhX</Origin>)\nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">CSCO.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210514:nASA026SG")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-14T12:15:37-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"BRIEF-Cisco Announces Intent To Acquire Kenna Security To Deliver Industry Leading Vulnerability Management");
			hm1.put("TE",
					"May 14 (Reuters) - Cisco Systems Inc  <Origin Href=\\\"QuoteRef\\\">CSCO.O</Origin> :\n    * CISCO ANNOUNCES INTENT TO ACQUIRE KENNA SECURITY TO\nDELIVER\nINDUSTRY LEADING VULNERABILITY MANAGEMENT\n    * CISCO - ACQUISITION IS EXPECTED TO CLOSE IN CISCO&apos;S Q4 OF\nFISCAL\n2021\n\nSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:nPn7kMh3sa</Origin> \nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">CSCO.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210512:nASA0262A")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-12T12:03:47-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-Cisco Announces Intent To Acquire Socio Labs To Power The Future Of Hybrid Events");
			hm1.put("TE",
					"May 12 (Reuters) - Cisco Systems Inc  <Origin Href=\\\"QuoteRef\\\">CSCO.O</Origin> :\n    * CISCO ANNOUNCES INTENT TO ACQUIRE SOCIO LABS TO POWER THE\nFUTURE\nOF HYBRID EVENTS\n    * CISCO - ACQUISITION IS EXPECTED TO CLOSE IN CISCO&apos;S Q4\nFISCAL\nYEAR 2021\n\nSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:nPn9vdWtSa</Origin> \nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">CSCO.O</Origin> \n\n ((reuters.briefs@thomsonreuters.com;))");
		} else if (storyId.equalsIgnoreCase("urn:newsml:reuters.com:20210511:nL1N2MX2MK")) {
			hm1.put("ID", storyId);
			hm1.put("RT", "2021-05-11T09:00:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Tech giants join call for funding U.S. chip production");
			hm1.put("TE",
					"By Stephen Nellis\n    May 11 (Reuters) - Some of the world&apos;s biggest chip buyers,\nincluding Apple Inc  <Origin Href=\\\"QuoteRef\\\">AAPL.O</Origin> , Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin>  and\nAlphabet Inc&apos;s  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin>  Google, are joining top chip-makers\nsuch as Intel Corp  <Origin Href=\\\"QuoteRef\\\">INTC.O</Origin>  to create a new lobbying group to\npress for government chip manufacturing subsidies.\n    The newly formed Semiconductors in America Coalition, which\nalso includes Amazon.com&apos;s  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin>  Amazon Web Services, said\nTuesday it has asked U.S. lawmakers to provide funding for the\nCHIPS for America Act, for which President Joe Biden has asked\nCongress to provide $50 billion. \n    &quot;Robust funding of the CHIPS Act would help America build\nthe additional capacity necessary to have more resilient supply\nchains to ensure critical technologies will be there when we\nneed them,&quot; the group said in a letter to Democratic and\nRepublican leaders in both houses of the U.S. Congress.\n    A global chip shortage has hit automakers hard, with Ford\nMotor Co  <Origin Href=\\\"QuoteRef\\\">F.N</Origin>  saying it could halve second-quarter production.\n <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2ML3FW</Origin>\n    Automotive industry groups have pressed the Biden\nadministration to secure chip supply for car factories. But\nReuters last week reported administration officials were\nreluctant to use a national security law to redirect computer\nchips to automakers because doing so could hurt other\nindustries.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG2OJ</Origin>\n    The new coalition includes some of those other\nchip-consuming industries, with members such as AT&amp;T  <Origin Href=\\\"QuoteRef\\\">T.N</Origin> ,\nCisco Systems  <Origin Href=\\\"QuoteRef\\\">CSCO.O</Origin> , General Electric  <Origin Href=\\\"QuoteRef\\\">GE.N</Origin> , Hewlett Packard\nEnterprise  <Origin Href=\\\"QuoteRef\\\">HPE.N</Origin>  and Verizon Communications Inc  <Origin Href=\\\"QuoteRef\\\">VZ.N</Origin> . It\ncautioned against government actions to favor a single industry\nsuch as automakers.\n    &quot;Government should refrain from intervening as industry\nworks to correct the current supply-demand imbalance causing the\nshortage,&quot; the group said. \n    Tech companies such as Apple are also being hit by the chip\nshortage, but far less severely than automakers. \n    The iPhone maker said last month it will lose $3 billion to\n$4 billion in sales in the current quarter ending in June\nbecause of the chip shortage, but that equates to just a few\npercent of the $72.9 billion in sales analyst expect for Apple&apos;s\nfiscal third quarter, according to Refinitiv revenue estimates.\n\n (Reporting by Stephen Nellis in San Francisco; editing by\nRichard Pullin)\n ((Stephen.Nellis@thomsonreuters.com; (415) 344-4934;))");
		}

		responseJSON.put("stockNewsStory", hm1);
		return responseJSON;
	}

	@SuppressWarnings("rawtypes")
	public JSONArray getStockNewsWeb(String isinCode) {
		LinkedHashMap<String, String> hm1 = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> hm2 = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> hm3 = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> hm4 = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> hm5 = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> hm6 = new LinkedHashMap<String, String>();

		if (isinCode.trim().equalsIgnoreCase("AMZN.O") || isinCode.trim().equalsIgnoreCase("AMZN.OQ")) {

			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20201117:nBw8fgMlna</Origin>Amazon Named Price Leaderin New StudyAmazon has best prices across 21 product categories important to customersAmazon.com, Inc. (NASDAQ: AMZN) was named the low price leader inProfiteroannual Warsstudy. The comprehensive analysis, nowin its fourth year, compares prices on over 18,400 products at Amazon,Walmart, Target, Staples, Home Depot, Wayfair, MacyWalgreenCVS,Kroger, Nordstrom, Best Buy, Lowes, and Chewy, and found Amazon offeredcustomers the lowest prices most often and most consistently.The study compared products in 21 categories important to customers, includinghousehold supplies, health and personal care, office supplies, home furniture,electronics, appliances and more. Amazon proved to be the lowest priced optionin all categories, with the largest potential customer savings, on average, inHome Furniture (35% more affordable than other options in the category),Vitamins and Supplements (34%), Health and Personal Care (26%) and Beauty(22%).The full study is available here:<Origin Href=\\\"Link\\\">https://insights.profitero.com/rs/476-BCC-343/images/Profitero%20-%20Pandemic%20Price%20Wars%202020.pdf</Origin>(<Origin Href=\\\"Link\\\">https://cts.businesswire.com/ct/CT?id=smartlink&url=https%3A%2F%2Finsights.profitero.com%2Frs%2F476-BCC-343%2Fimages%2FProfitero%2520-%2520Pandemic%2520Price%2520Wars%25202020.pdf&esheet=52329421&newsitemid=20201117005503&lan=en-US&anchor=https%3A%2F%2Finsights.profitero.com%2Frs%2F476-BCC-343%2Fimages%2FProfitero%2520-%2520Pandemic%2520Price%2520Wars%25202020.pdf&index=1&md5=14d4f03b831c7ad6e69c8f964e7724cd</Origin>)Customers expect to find low prices in our store, and we work hard to providethe best available price across the hundreds of millions of products in ourstore for all customers, every day.About AmazonAmazon is guided by four principles: customer obsession rather than competitorfocus, passion for invention, commitment to operational excellence, andlong-term thinking. Customer reviews, 1-Click shopping, personalizedrecommendations, Prime, Fulfillment by Amazon, AWS, Kindle Direct Publishing,Kindle, Fire tablets, Fire TV, Amazon Echo, and Alexa are some of the productsand services pioneered by Amazon. For more information, visit amazon.com/about(<Origin Href=\\\"Link\\\">https://cts.businesswire.com/ct/CT?id=smartlink&url=https%3A%2F%2Fwww.aboutamazon.com%2F&esheet=52329421&newsitemid=20201117005503&lan=en-US&anchor=amazon.com%2Fabout&index=2&md5=228eb8fee563727e2447cc484501d240</Origin>)and follow @AmazonNews(<Origin Href=\\\"Link\\\">https://cts.businesswire.com/ct/CT?id=smartlink&url=https%3A%2F%2Ftwitter.com%2Famazonnews&esheet=52329421&newsitemid=20201117005503&lan=en-US&anchor=%40AmazonNews&index=3&md5=04dcd585fbda468e573213f0a45fbdab</Origin>).View source version on businesswire.com:<Origin Href=\\\"Link\\\">https://www.businesswire.com/news/home/20201117005503/en/</Origin>(<Origin Href=\\\"Link\\\">https://www.businesswire.com/news/home/20201117005503/en/</Origin>)Amazon.com, Inc.Media HotlineAmazon-pr@amazon.com (mailto:Amazon-pr@amazon.com) <Origin Href=\\\"Link\\\">www.amazon.com/pr</Origin>(<Origin Href=\\\"Link\\\">https://cts.businesswire.com/ct/CT?id=smartlink&url=http%3A%2F%2Fwww.amazon.com%2Fpr&esheet=52329421&newsitemid=20201117005503&lan=en-US&anchor=www.amazon.com%2Fpr&index=4&md5=79af7887c4891c08ded61ac2040bf63f</Origin>)Copyright Business Wire 2020</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20201117:nBw8fgMlna");
			hm1.put("RT", "2020-11-17T07:00:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Amazon Named “Low Price Leader” in New Study");
			hm1.put("CT", "2020-11-17T07:00:00-00:00");
			hm1.put("LT", "2020-11-17T07:00:00-00:00");

			hm2.put("TE",
					"<p>    Nov 17 (Reuters) - Dutch chipmaker NXP Semiconductors <Origin Href=\\\"QuoteRef\\\">NXPI.O</Origin>  on Tuesday said it had entered a strategic relationshipwith Amazon.com's  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin>  cloud computing unit to help carscommunicate with data centers.    NXP is one of the biggest suppliers of computing chips toautomakers and Amazon Web Services is the largest cloudcomputing service by revenue. The companies said the partnershipwould involve making a new NXP chip designed to aggregate datafrom a car's systems and send it over the internet to workbetter with Amazon's data centers.     see the opportunity to help (automakers) make impactfulimprovements throughout vehicle life cycles with new vehicledata insights and the ability to make continuous improvementsusing machine learning and over-the-air updates,\" Henri Ardevol,executive vice president and general manager of automotiveprocessing at NXP Semiconductors, said in a statement.    The two companies said they would give more details aboutthe partnership in a web presentation on Nov. 19. (Reporting by Stephen Nellis in San Francisco; Editing byChristian Schmollinger) ((Stephen.Nellis@thomsonreuters.com; (415) 344-4934;))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20201117:nL1N2I308K");
			hm2.put("RT", "2020-11-17T07:00:00-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "NXP, Amazon partner to connect cars to cloud computing services");
			hm2.put("CT", "2020-11-17T07:00:00-00:00");
			hm2.put("LT", "2020-11-17T07:00:00-00:00");

			hm3.put("TE",
					"<p>By Caroline Valetkevitch</p><p>NEW YORK (Reuters) - As positive coronavirus vaccine news makes strategists more optimistic about the future for retailers, investors will look to earnings results and forecasts from these companies in the coming days for clues about consumer spending.</p><p>The thinking is that effective vaccines will \"translate into positive performances for discretionary,\" said Sam Stovall, chief investment strategist for CFRA, which recently raised its recommendation on the S&P 500 consumer discretionary sector <.SPLRCD> to overweight from market weight.    </p><p>The pandemic and restrictions on businesses have added to revenue struggles for some traditional retailers like department stores while boosting sales for Amazon <AMZN.O> and other sellers that have capitalized on the work-from-home phenomenon. </p><p>Results from Home Depot Inc. <HD.N>, Walmart Inc. <WMT.N> and Kohl's Corp. <KSS.N> are due on Tuesday, while reports from Macy's Inc. <M.N>, L Brands <LB.N>, Target Corp. <TGT.N> and TJX Cos <TJX.N> are expected later this week.</p><p>The U.S. retail sales report for October also is due on Tuesday.</p><p>Investors will be especially eager to hear guidance and commentary from retail executives on consumer spending.</p><p>\"We've gotten used to now buying online increasingly and delivery has become smoother,\" said Quincy Krosby, chief market strategist at Prudential Financial in Newark, New Jersey. \"The question is, does it continue?\"</p><p>Shares in a number of retailers rallied on Monday as a result of optimism that a COVID-19 vaccine will help the economic recovery in the months ahead.</p><p>Moderna Inc <MRNA.O> said its experimental vaccine was 94.5% effective in preventing COVID-19 based on interim data from a late-stage trial. Pfizer Inc <PFE.N> said last week its experimental COVID-19 vaccine was more than 90% effective based on initial trial results.</p><p>Among the biggest percentage gainers in the S&P 500 consumer discretionary index on Monday were Gap Inc. <GPS.N>, up 9.5%; Under Armour <UAA.N>, up 5.8%; and Ulta Beauty <ULTA.O>, up 4.9%. </p><p>Investors also are watching whether upcoming holiday sales data can support hopes for retailer shares despite a surge in COVID cases and increased restrictions across the United States and in Europe.</p><p>\"As long as the jobs market holds up and we see (unemployment benefit claims) continuing to go down, that helps consumers' optimism,\" said Krosby.    </p><p>Some retailers' shares remain down sharply this year so far, including Macy's, which is off more than 50% since Dec. 31 despite a 9% gain on Monday as the pandemic added to their recent struggles.</p><p>On the earnings front, there continues to be a big divide within the consumer discretionary sector. </p><p>Analysts expect the sector to post an earnings decline of 5.1% for the third quarter from a year ago, compared with a 7.4% decline for the entire S&P 500 <.SPX>, according to IBES data from Refinitiv. </p><p>But earnings for internet and direct marketing retail companies are show about a 43% rise in the quarter, while  apparel retail earnings are forecast to fall 40.1%. </p><p>Nordstrom Inc <JWN.N>, Gap Inc <GPS.N> and American Eagle Outfitters Inc <AEO.N> report earnings next week.</p><p></p><p> (Reporting by Caroline Valetkevitch; Editing by Alden Bentley and Cynthia Osterman)</p><Origin Href=\\\"ThumbnailRef\\\">2020-11-16T231350Z_1_LYNXMPEGAF1PI_RTROPTP_1_USA-STOCKS-RETAILERS.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201116:nRTROPT20201116231350LYNXMPEGAF1PI</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: A shopper is seen wearing a mask while shopping at a Walmart store in Bradford, Pennsylvania</Origin> <Origin Href=\\\"ChannelCode\\\">OLUSBUS</Origin> <Origin Href=\\\"LinkTitle\\\">Reuters US Online Report Business News</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201116T231350+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201116T231350+0000</Origin>");
			hm3.put("ID", "urn:newsml:onlinereport.com:20201116:nRTROPT20201116231350KBN27W315");
			hm3.put("RT", "2020-11-16T23:13:50-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Does vaccine promise put U.S. consumers in a shopping mood? Retailers may have clues");
			hm3.put("CT", "2020-11-16T23:13:50-00:00");
			hm3.put("LT", "2020-11-16T23:13:50-00:00");

			hm4.put("TE",
					"<p>By Caroline Valetkevitch</p><p>NEW YORK (Reuters) - As positive coronavirus vaccine news makes strategists more optimistic about the future for retailers, investors will look to earnings results and forecasts from these companies in the coming days for clues about consumer spending.</p><p>The thinking is that effective vaccines will \"translate into positive performances for discretionary,\" said Sam Stovall, chief investment strategist for CFRA, which recently raised its recommendation on the S&P 500 consumer discretionary sector <.SPLRCD> to overweight from market weight.    </p><p>The pandemic and restrictions on businesses have added to revenue struggles for some traditional retailers like department stores while boosting sales for Amazon <AMZN.O> and other sellers that have capitalized on the work-from-home phenomenon. </p><p>Results from Home Depot Inc. <HD.N>, Walmart Inc. <WMT.N> and Kohl's Corp. <KSS.N> are due on Tuesday, while reports from Macy's Inc. <M.N>, L Brands <LB.N>, Target Corp. <TGT.N> and TJX Cos <TJX.N> are expected later this week.</p><p>The U.S. retail sales report for October also is due on Tuesday.</p><p>Investors will be especially eager to hear guidance and commentary from retail executives on consumer spending.</p><p>\"We've gotten used to now buying online increasingly and delivery has become smoother,\" said Quincy Krosby, chief market strategist at Prudential Financial in Newark, New Jersey. \"The question is, does it continue?\"</p><p>Shares in a number of retailers rallied on Monday as a result of optimism that a COVID-19 vaccine will help the economic recovery in the months ahead.</p><p>Moderna Inc <MRNA.O> said its experimental vaccine was 94.5% effective in preventing COVID-19 based on interim data from a late-stage trial. Pfizer Inc <PFE.N> said last week its experimental COVID-19 vaccine was more than 90% effective based on initial trial results.</p><p>Among the biggest percentage gainers in the S&P 500 consumer discretionary index on Monday were Gap Inc. <GPS.N>, up 9.5%; Under Armour <UAA.N>, up 5.8%; and Ulta Beauty <ULTA.O>, up 4.9%. </p><p>Investors also are watching whether upcoming holiday sales data can support hopes for retailer shares despite a surge in COVID cases and increased restrictions across the United States and in Europe.</p><p>\"As long as the jobs market holds up and we see (unemployment benefit claims) continuing to go down, that helps consumers' optimism,\" said Krosby.    </p><p>Some retailers' shares remain down sharply this year so far, including Macy's, which is off more than 50% since Dec. 31 despite a 9% gain on Monday as the pandemic added to their recent struggles.</p><p>On the earnings front, there continues to be a big divide within the consumer discretionary sector. </p><p>Analysts expect the sector to post an earnings decline of 5.1% for the third quarter from a year ago, compared with a 7.4% decline for the entire S&P 500 <.SPX>, according to IBES data from Refinitiv. </p><p>But earnings for internet and direct marketing retail companies are show about a 43% rise in the quarter, while  apparel retail earnings are forecast to fall 40.1%. </p><p>Nordstrom Inc <JWN.N>, Gap Inc <GPS.N> and American Eagle Outfitters Inc <AEO.N> report earnings next week.</p><p></p><p> (Reporting by Caroline Valetkevitch; Editing by Alden Bentley and Cynthia Osterman)</p><Origin Href=\\\"ThumbnailRef\\\">2020-11-16T231350Z_1_LYNXMPEGAF1PI-OCABS_RTROPTP_1_CBUSINESS-US-USA-STOCKS-RETAILERS.JPG</Origin> <Origin Href=\\\"ImageRef\\\">urn:newsml:onlinereport.com:20201116:nRTROPT20201116231350LYNXMPEGAF1PI-OCABS</Origin> <Origin Href=\\\"ImageTitle\\\">FILE PHOTO: A shopper is seen wearing a mask while shopping at a Walmart store in Bradford, Pennsylvania</Origin> <Origin Href=\\\"ChannelCode\\\">OLCABUS</Origin> <Origin Href=\\\"LinkTitle\\\">Reuters Canada Online Report Business News</Origin> <Origin Href=\\\"StoryCreationDate\\\">20201116T231350+0000</Origin> <Origin Href=\\\"ImageCreationDate\\\">20201116T231350+0000</Origin>");
			hm4.put("ID", "urn:newsml:onlinereport.com:20201116:nRTROPT20201116231350KBN27W315-OCABS");
			hm4.put("RT", "2020-11-16T23:13:50-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Does vaccine promise put U.S. consumers in a shopping mood? Retailers may have clues");
			hm4.put("CT", "2020-11-16T23:13:50-00:00");
			hm4.put("LT", "2020-11-16T23:13:50-00:00");

			hm5.put("TE",
					"<p> (Adds details on firm, positions, file photo available)\n    By Svea Herbst-Bayliss\n    BOSTON, Dec 3 (Reuters) - Daniel Loeb&apos;s Third Point LLC\nfunds posted strong gains in November to extend year-to-date\nreturns into the double digits after the billionaire investor\noverhauled his portfolio and the firm a few months ago.\n    The Third Point Offshore Fund gained 9.1% in November and is\nnow up 12.3% for the year, while the Third Point Ultra fund\ngained 12.1% last month and is now up 14.2%, according to a\nperformance update seen by an investor. \n    The gains were fueled mainly by investments in credit as\nwell as fundamental equity bets, particularly in the media and\ninternet and enterprise technology sectors.\n    Loeb added stocks including Amazon  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin> , Disney  <Origin Href=\\\"QuoteRef\\\">DIS.N</Origin> \nand Alibaba  <Origin Href=\\\"QuoteRef\\\">9988.HK</Origin>  <Origin Href=\\\"QuoteRef\\\">BABA.N</Origin>  during the year. During the third\nquarter he trimmed the stakes in Amazon and Disney slightly,\nregulatory filings show. \n    The gains represent a dramatic swing coming only months\nafter Third Point was caught off-guard by the coronavirus and\nmassive market sell-off to end the first quarter with\ndouble-digit losses. The Offshore fund lost 16% while the Ultra\nfund was down 21% at the end of the first quarter.\n    Third Point, which now oversees $14.8 billion in assets, has\nalways had a flexible investment mandate and while the firm has\nbeen known for some of its recent large activist bets, Loeb\nshifted course some this year.\n    In May, he took back the reins from Munib Islam who had been\npromoted to co-chief investment officer last year and had\nlargely been the face of Third Point&apos;s activism positions,\nincluding a bet this year on Prudential Plc  <Origin Href=\\\"QuoteRef\\\">PRU.L</Origin> .\n    Activism, which Third Point also calls constructivism,\nweighed on returns this year, the source said.\n    Third Point is handily outperforming the average hedge fund,\nwhich gained only 1.2% in the first ten months of 2020,\naccording to Hedge Fund Research data. More recent data is not\nyet available. \n\n (Reporting by Svea Herbst-Bayliss\nEditing by Nick Zieminski)\n ((svea.herbst@thomsonreuters.com; +617 856 4331; Reuters\nMessaging: svea.herbst.thomsonreuters.com@reuters.net))</p>");
			hm5.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ1P2");
			hm5.put("RT", "2020-12-03T17:58:47-00:00");
			hm5.put("PR", "reuters.com");
			hm5.put("HT", "UPDATE 1-Loeb&apos;s Third Point funds gain in November, up double-digits YTD");
			hm5.put("CT", "2020-12-03T17:58:47-00:00");
			hm5.put("LT", "2020-12-03T17:58:47-00:00");

			hm6.put("TE",
					"<p> (Adds details from Amazon statement)\n    By Paul Sandle\n    LONDON, Dec 3 (Reuters) - More than 400 lawmakers from 34\ncountries have signed a letter to Amazon.com Inc  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin>  boss\nJeff Bezos backing a campaign that claims the tech giant has\n&quot;dodged and dismissed … debts to workers, societies, and the\nplanet,&quot; organisers said.\n    The &quot;Make Amazon Pay&quot; campaign was launched on Nov. 27 - the\nannual Black Friday shopping bonanza - by a coalition of over 50\norganisations, with demands including improvements to working\nconditions and full tax transparency.\n    The letter&apos;s signatories include U.S. Congresswomen Ilhan\nOmar and Rashida Tlaib, former UK Labour Party leader Jeremy\nCorbyn and Vice President of the European Parliament Heidi\nHautala, co-convenors Progressive International and UNI Global\nUnion said.\n    &quot;We urge you to act decisively to change your policies and\npriorities to do right by your workers, their communities, and\nour planet,&quot; the letter said. \n    &quot;We stand ready to act in our respective legislatures to\nsupport the movement that is growing around the world to Make\nAmazon Pay.&quot;\n    Amazon, the world&apos;s biggest retailer, has faced criticism\nfor its tax practices before, including in the UK and the EU. It\nsays its profits remain low given retail is a highly\ncompetitive, low margin business and it invests heavily. \n    It said on Thursday that while it accepted scrutiny from\npolicymakers, many of the matters raised in the letter stemmed\nfrom misleading assertions.\n    &quot;Amazon has a strong track record of supporting our\nemployees, our customers, and our communities, including\nproviding safe working conditions, competitive wages and great\nbenefits,&quot; it said, adding it was &quot;paying billions of dollars in\ntaxes globally.&quot; The company has also pledged to be net carbon\nneutral by 2040.\n    Amazon grew rapidly during the pandemic, with sales soaring\nas restrictions to prevent the spread of the coronavirus closed\nbricks-and-mortar shops and sent consumers online.\n    Governments worldwide are considering tougher rules for big\ntech to assuage worries about competition.\n    The European Union, for example, last month charged Amazon\nwith damaging retail competition, alleging it used its size,\npower and data to gain an unfair advantage over smaller\nmerchants that sell on its online platform.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2HW0TQ</Origin>\n    Amazon disagreed with the EU assertions, saying it\nrepresented less than 1% of the global retail market and there\nwere larger retailers in every country in which it operated.\n    \n\n (Reporting by Paul Sandle\nEditing by Nick Zieminski)\n ((paul.sandle@thomsonreuters.com; +44 20 7542 6843; Reuters\nMessaging: paul.sandle.thomsonreuters.com@reuters.net))</p>");
			hm6.put("ID", "urn:newsml:reuters.com:20201203:nL1N2IJ1TX");
			hm6.put("RT", "2020-12-03T17:37:46-00:00");
			hm6.put("PR", "reuters.com");
			hm6.put("HT",
					"UPDATE 1-More than 400 lawmakers from 34 countries back &apos;Make Amazon Pay&apos; campaign");
			hm6.put("CT", "2020-12-03T17:37:46-00:00");
			hm6.put("LT", "2020-12-03T17:37:46-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);
			al.add(hm5);
			al.add(hm6);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("GOOGL.O") || isinCode.trim().equalsIgnoreCase("GOOGL.OQ")
				|| isinCode.trim().equalsIgnoreCase("GOOGL.N")) {

			hm1.put("TE",
					"<p> (Repeats to additional subscribers)\n    By Byron Kaye\n    SYDNEY, Feb 17 (Reuters) - Australia claimed an early win in\na protracted licencing battle with Google on Wednesday as media\ncompanies lined up to announce content deals with the internet\ngiant that were reportedly far more lucrative than their global\nrivals.\n    A month after the Alphabet Inc-owned company  <Origin Href=\"QuoteRef\">GOOGL.O</Origin> \nthreatened to shut down its search engine in Australia to avoid\nwhat it called &quot;unworkable&quot; content laws, the country&apos;s two\nlargest free-to-air television broadcasters have struck deals\ncollectively worth A$60 million ($47 million) a year, according\nto media reports. <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KM2PZ</Origin>\n    That dwarfs the $76 million Google will split between 121\npublishers in France over three years, which averages $209,000 a\nyear per publisher, as reported by Reuters. <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KI1PD</Origin>\n    The Australian deals come days before the government plans\nto pass laws that would allow it to appoint an arbitrator to set\nGoogle&apos;s content fees if it can&apos;t strike a deal privately, a\nfactor that government and media figures held up as a turning\npoint for negotiations which stalled a year earlier. \n    &quot;I don&apos;t think that they would have been able to get that\nsort of money if they had to follow the normal sort of\nnegotiations with a company that&apos;s so powerful,&quot; said Paul\nBudde, an independent internet analyst, referring to the\nAustralian media companies.\n    Google and Nine declined to comment on unsourced reports in\nNine&apos;s newspapers on Wednesday that said the companies had\nreached an agreement. Seven and Google said two days earlier\nthey had struck a deal, without giving financials.\n    Though the individual deals mean Google avoids a\ngovernment-appointed arbitrator with those companies, Australian\nTreasurer Josh Frydenberg said he would still press ahead with\nthe law.\n    The local arm of Rupert Murdoch&apos;s News Corp  <Origin Href=\"QuoteRef\">NSWA.O</Origin> , which\nhas led a years-long campaign to make internet giants pay for\ncontent that drives traffic to their platforms, is yet to sign a\nGoogle deal. News Corp, owner of two-thirds of Australia&apos;s major\ncity newspapers, did not respond to requests for comment.\n    &quot;None of these deals would be happening if we didn&apos;t have\nthe legislation before the Parliament,&quot; Frydenberg told\nreporters.\n    Australian antitrust commissioner Rod Sims, who drafted the \nmedia laws, declined to comment but a spokesman directed Reuters\nto an earlier statement in which Sims called the law a &quot;back-up&quot;\nthat prevented internet platforms forcing &quot;terms on a\ntake-it-or-leave-it basis&quot;.\n\n    COLLECTIVE BARGAINING\n    Though specifics of the Australian deals have not been\ndisclosed, smaller outlets that inked Google deals last year\nahead of their larger rivals said they were approached\nindividually by the U.S. company and asked to present their own\nvaluation methods for content that would appear on Google&apos;s\n&quot;Showcase&quot; news platform.\n    That contrasts with the French negotiations, which were\nconducted on behalf of publishers by the Alliance de la presse\nd&apos;information generale (APIG), a lobby group representing most\nmajor French publishers.\n    Unlike the Australian law, through which the government\ncould intervene if the parties cannot reach a deal, the French\nrules, enacted under a recent European Union law, require only\nthat Big Tech platforms open talks with publishers seeking\npayment. \n    &quot;The context of the (Australian) bargaining was very much\none in which the government legislation was putting pressure on\nthe digital platforms to come to the table, and that has\nstrengthened the hand of publishers and contributed to these\noutcomes,&quot; said Misha Ketchell, editor of The Conversation, an\nacademic-focused website that signed a Google deal last year.\n    Separately, the Reuters news agency, a division of Thomson\nReuters Corp, struck a deal with Google in January, becoming the\nfirst global news provider to Google News Showcase. <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nFWN2JW0U0</Origin>\n    ($1 = 1.2903 Australian dollars)\n\n (Reporting by Byron Kaye; Editing by Sam Holmes)\n ((byron.kaye@thomsonreuters.com; +612 9321 8164; @byronkaye;))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210217:nL1N2KN08O");
			hm1.put("RT", "2021-02-17T09:34:29-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "RPT-Australian media firms squeeze more from Google as new law looms");
			hm1.put("CT", "2021-02-17T06:16:52-00:00");
			hm1.put("LT", "2021-02-17T09:34:29-00:00");

			hm2.put("TE",
					"<p> (Adds background)\n    By Andrea Shalal\n    WASHINGTON, Feb 16 (Reuters) - Treasury Secretary Janet\nYellen stressed the importance of cooperation with the European\nUnion in a call with the European Commission&apos;s vice president\nfor the economy, Valdis Dombrovskis, on Tuesday, the U.S.\nTreasury Department said.\n    Earlier on Tuesday, Yellen in a conversation with European\nCentral Bank President Christine Lagarde had also underscored\nways to deepen transatlantic cooperation on economic and\nfinancial issues.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KM2MB</Origin>\n    In her discussion with the EU executive, Yellen emphasized\nthe importance of the transatlantic partnership and said she\naimed to work more closely with Brussels on key challenges,\nincluding &quot;ending the pandemic, supporting a strong global\neconomic recovery, fighting income inequality, and forcefully\naddressing the threat of climate change,&quot; the Treasury said.\n    Yellen committed to re-engaging in discussions on\ninternational taxation to forge a timely international accord,\nand to seek solutions to key bilateral trade issues, it said.\n    The Biden administration is seeking to rebuild ties with\nEuropean allies on all fronts after the deep rifts that\ncharacterized Donald Trump&apos;s presidency, and want to work\ntogether to counter what Yellen has called China&apos;s &quot;abusive,\nunfair and illegal practices.&quot;\n    While Trump, a Republican, repeatedly took aim at European\nallies, threatening and imposing tariffs on a range of goods,\nthe administration of Democratic President Joe Biden has sent\nfar more positive signals on issues ranging from trade to taxes.\n    EU officials last week welcomed Washington&apos;s decision to\nrefrain from imposing additional tariffs on EU goods in a\nlong-running dispute over aircraft tariffs, and said they were\nready to work to resolve trade disputes.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KI02F</Origin>\n    The United States and European countries have also said they\nwill redouble efforts to reenergize and conclude talks on global\ntaxation being led by the Organization for Economic Cooperation\nand Development.\n    Nearly 140 countries are negotiating the first update in a\ngeneration to the rules for taxing cross-border commerce, to\naccount for the emergence of big tech companies like Alphabet\nInc&apos;s Google  <Origin Href=\"QuoteRef\">GOOGL.O</Origin> , Apple Inc  <Origin Href=\"QuoteRef\">AAPL.O</Origin>  and Facebook Inc\n <Origin Href=\"QuoteRef\">FB.O</Origin> .\n    The talks stalled under the Trump administration after\nYellen&apos;s predecessor suspended U.S. involvement, but OECD\nministers say they now hope to reach a deal by mid-2021.\n\n (Reporting by Eric Beech and Andrea Shalal, writing by Mohammad\nZargham; Editing by Himani Sarkar and Leslie Adler)\n ((mohammad.zargham@thomsonreuters.com;))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20210217:nL1N2KN03J");
			hm2.put("RT", "2021-02-17T02:54:38-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "UPDATE 1-U.S. Treasury&apos;s Yellen vows to boost transatlantic cooperation ");
			hm2.put("CT", "2021-02-17T02:54:38-00:00");
			hm2.put("LT", "2021-02-17T02:54:38-00:00");

			hm3.put("TE",
					"<p> (Recasts with Australian treasurer comment)\n    By Byron Kaye\n    SYDNEY, Feb 17 (Reuters) - Australia on Wednesday said\npromised laws forcing tech giants to pay media outlets for\ncontent had already succeeded after reports that publisher and\nbroadcaster Nine Entertainment Co Holdings Ltd  <Origin Href=\"QuoteRef\">NEC.AX</Origin>  agreed\non a licensing deal with Google.\n    The Alphabet Inc  <Origin Href=\"QuoteRef\">GOOGL.O</Origin>  owned company agreed to pay Nine\nmore than A$30 million ($23.25 million) a year for its content,\ntwo of Nine&apos;s newspapers reported, citing unidentified industry\nsources. The deal would be formally signed in the next two\nweeks, the newspapers said.\n    A Nine spokeswoman declined to comment to Reuters. A Google\nspokesman also declined to comment.\n    Nine would be the second major Australian media company to\nreach an agreement with Google just as the country&apos;s parliament\nprepares to pass laws giving the government power to set\nGoogle&apos;s content fees.\n    On Monday, Nine rival Seven West Media Ltd  <Origin Href=\"QuoteRef\">SWM.AX</Origin>  said it\nhad reached a deal that local media reported would also involve\nthe U.S. company paying it A$30 million a year.\n    &quot;None of these deals would be happening if we didn&apos;t have\nthe legislation before the Parliament,&quot; Australian treasurer\nJosh Frydenberg told reporters.\n    &quot;This legislation, this world-leading mandatory code, is\nbringing the parties to the table. We have held the line and\nheld it strongly.&quot;\n    The Australian federal government has said it still plans to\nput the laws - which effectively force Google and social media\ngiant Facebook Inc  <Origin Href=\"QuoteRef\">FB.O</Origin>  to strike deals with media companies\nor have fees set for them - to a vote in the coming weeks.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KK0PF</Origin>\n    Last year, seven smaller media companies, specialist\nwebsites and a regional newspaper, signed deals to have their\ncontent appear on Google&apos;s News Showcase platform, but the\ncountry&apos;s main metro outlets failed to reach agreements.\n    Several large domestic media players, including the local\narm of Rupert Murdoch&apos;s News Corp  <Origin Href=\"QuoteRef\">NWSA.O</Origin>  - which owns\ntwo-thirds of Australian newspapers - have yet to announce\nGoogle deals. A News Corp spokesman was not immediately\navailable for comment on Wednesday.\n    Media outlets around the world are trying to find a way to\ncompensate for a slump in advertising revenue, traditionally\ntheir main source of income, which has resulted in widespread\nclosures.\n    In January, the Reuters news agency, a division of Thomson\nReuters Corp, struck a deal with Google to be the first global\nnews provider for Google&apos;s News Showcase.\n\n\n($1 = 1.2903 Australian dollars)\n\n (Reporting by Byron Kaye; Editing by Christopher Cushing)\n ((byron.kaye@thomsonreuters.com; +612 9321 8164; @byronkaye;))</p>");
			hm3.put("ID", "urn:newsml:reuters.com:20210217:nL1N2KN040");
			hm3.put("RT", "2021-02-17T02:33:24-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "UPDATE 1-Australia says content laws already working after Nine-Google deal reports");
			hm3.put("CT", "2021-02-17T02:33:24-00:00");
			hm3.put("LT", "2021-02-17T02:33:24-00:00");

			hm4.put("TE",
					"<p>    SYDNEY, Feb 17 (Reuters) - Publisher and broadcaster Nine\nEntertainment Co Holdings Ltd  <Origin Href=\"QuoteRef\">NEC.AX</Origin>  has agreed on a content\nlicencing deal with Google, one of its newspapers reported on\nWednesday, the second large Australian media outlet to strike a\ndeal with the internet giant.\n    The Alphabet Inc  <Origin Href=\"QuoteRef\">GOOGL.O</Origin>  owned company agreed to pay Nine\nmore than A$30 million a year for its content, the Sydney\nMorning Herald reported, citing &quot;industry sources&quot;. The deal\nwould be formally signed in the next two weeks, the newspaper\nsaid. A Nine spokeswoman declined to comment to Reuters.\n    A Google spokesman also declined to comment.\n    Nine would be the second major Australian media company to\nreach agreement with Google just as the country&apos;s parliament\nprepares to pass laws giving the government power to set\nGoogle&apos;s content fees.\n    On Monday, Nine rival Seven West Media Ltd  <Origin Href=\"QuoteRef\">SWM.AX</Origin>  said it\nhad reached a deal that local media reported would also involve\nthe U.S. company paying it A$30 million a year.\n    The Australian federal government has said it still plans to\nput the laws, which effectively force Google and social media\ngiant Facebook Inc  <Origin Href=\"QuoteRef\">FB.O</Origin>  to strike deals with media companies\nor have fees set for them, to a vote in the coming weeks.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KK0PF</Origin>\n    Seven smaller media companies, specialist websites and a\nregional paper, signed deals to have their content appear on\nGoogle&apos;s &quot;Showcase&quot; news platform last year, but the country&apos;s\nmain metro outlets failed to reach agreements.\n    Several large domestic media players, including the local\narm of Rupert Murdoch&apos;s News Corp  <Origin Href=\"QuoteRef\">NWSA.O</Origin> , which owns\ntwo-thirds of Australian newspapers, have yet to announce Google\ndeals. A News spokesman was not immediately available for\ncomment on Wednesday.\n    Media outlets around the world are trying to find a way to\ncompensate for a slump in advertising revenue, traditionally\ntheir main source of income, which has resulted in widespread\nclosures.\n    In January, the Reuters news agency, a division of Thomson\nReuters Corp, struck a deal with Google to be the first global\nnews provider to Google News Showcase.\n\n (Reporting by Byron Kaye)\n ((byron.kaye@thomsonreuters.com; +612 9321 8164; @byronkaye;))</p>");
			hm4.put("ID", "urn:newsml:reuters.com:20210216:nL1N2KM2PZ");
			hm4.put("RT", "2021-02-16T23:58:50-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Australia&apos;s Nine strikes a Google media deal as licencing laws approach - media");
			hm4.put("CT", "2021-02-16T23:58:50-00:00");
			hm4.put("LT", "2021-02-16T23:58:50-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AAPL.O") || isinCode.trim().equalsIgnoreCase("AAPL.OQ")
				|| isinCode.trim().equalsIgnoreCase("AAPL.N")) {

			hm1.put("TE",
					"<p> (Adds European Commission confirmation)\n    By Foo Yun Chee\n    BRUSSELS, Feb 17 (Reuters) - Fortnite creator Epic Games has\ntaken its fight against Apple  <Origin Href=\"QuoteRef\">AAPL.O</Origin>  to European Union\nantitrust regulators, ramping up it dispute with the iPhone\nmaker over its App Store payment system and control over app\ndownloads.\n    The two companies have been locked in a legal dispute since\nlast August, when the game maker tried to avoid Apple&apos;s 30% fee\non some in-app purchases on the App Store by launching its own\nin-app payment system.\n    That prompted Apple to kick Epic&apos;s Fortnite game off the App\nStore and threaten to terminate an affiliated account that would\nhave effectively blocked distribution of Unreal Engine, a\nsoftware tool used by hundreds of app makers to create games.\n    Epic Games founder and Chief Executive Tim Sweeney said\nApple&apos;s control of its platform had tilted the level playing\nfield.\n    &quot;The 30% they charge as their app tax, they can make it 50%\nor 90% or 100%. Under their theory of how these markets are\nstructured, they have every right to do that,&quot; he told\nreporters.\n    &quot;Epic is not asking any court or regulator to change this\n30% to some other number, only to restore competition on IOS,&quot;\nhe said, referring to Apple&apos;s mobile operating system.\n    The company also accused Apple of barring rivals from\nlaunching their own gaming subscription service on its platform\nby preventing them from bundling several games together, even\nthough its own Apple Arcade service does that.\n    Apple said its rules applied equally to all developers and\nthat Epic had violated them.\n    &quot;In ways a judge has described as deceptive and clandestine,\nEpic enabled a feature in its app, which was not reviewed or\napproved by Apple, and they did so with the express intent of\nviolating the App Store guidelines that apply equally to every\ndeveloper and protect customers,&quot; the company said in a\nstatement.\n    &quot;Their reckless behaviour made pawns of customers, and we\nlook forward to making this clear to the European Commission,&quot;\nit said.\n    Apple has been taking small steps in recent months towards\nchanging its practices, including lower fees for some developers\nand giving them a way to challenge its rulings, both of which\nhave not satisfied the company&apos;s critics. \n    Fortnite is slated to come back to the iPhone at some point\nin the mobile Safari browser. Epic and Apple in recent weeks\nhave been trading documents and conducting depositions ahead of\na scheduled May trial in the Epic lawsuit filed last year.\n    The Commission, which is investigating Apple&apos;s mobile\npayment system Apple Pay and the App Store, confirmed receipt of\nthe complaint.\n    &quot;We will assess it based on our standard procedures,&quot; a\nCommission spokeswoman said.\n    Epic Games has also complained to the UK Competition Appeal\nTribunal and to the Australian watchdog. \n    Big companies such as Microsoft Corp  <Origin Href=\"QuoteRef\">MSFT.O</Origin> , Spotify\n <Origin Href=\"QuoteRef\">SPOT.N</Origin>  and Match Group Inc  <Origin Href=\"QuoteRef\">MTCH.O</Origin>  have also criticised\nApple&apos;s App Store fees and rules.\n\n (Reporting by Foo Yun Chee\nAdditional reporting by Stephen Nellis and Paresh Dave in San\nFrancisco\nEditing by Barbara Lewis, Edmund Blair and David Goodman\n)\n ((foo.yunchee@thomsonreuters.com; +32 2 287 6844; Reuters\nMessaging: foo.yunchee.thomsonreuters.com@reuters.net))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210217:nL1N2KN0MJ");
			hm1.put("RT", "2021-02-17T09:54:41-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 2-Epic Games takes Apple fight to EU antitrust regulators");
			hm1.put("CT", "2021-02-17T09:54:41-00:00");
			hm1.put("LT", "2021-02-17T09:54:41-00:00");

			hm2.put("TE",
					"<p> (Corrects paragraph 14 to show Epic Games is not seeking\ndamages in the UK and Australia)\n    By Foo Yun Chee\n    BRUSSELS, Feb 17 (Reuters) - Fortnite creator Epic Games has\ntaken its fight against Apple  <Origin Href=\"QuoteRef\">AAPL.O</Origin>  to European Union\nantitrust regulators after failing to make headway in a U.S.\ncourt in a dispute over the iPhone maker&apos;s payment system on its\nApp Store and control over apps downloads.\n    The two companies have been locked in a legal dispute since\nAugust when the game maker tried to get around Apple&apos;s 30% fee\non some in-app purchases on the App Store by launching its own\nin-app payment system.\n    That prompted Apple to kick Epic&apos;s Fortnite game off the App\nStore and threaten to terminate an affiliated account that would\nhave effectively blocked distribution of Unreal Engine, a\nsoftware tool used by hundreds of app makers to create games.\n    Epic Games founder and Chief Executive Tim Sweeney said\nApple&apos;s control of its platform had tilted the level playing\nfield.\n    &quot;The 30% they charge as their app tax, they can make it 50%\nor 90% or 100%. Under their theory of how these markets are\nstructured, they have every right to do that,&quot; he told\nreporters.\n    &quot;Epic is not asking any court or regulator to change this\n30% to some other number, only to restore competition on IOS,&quot;\nhe said, referring to Apple&apos;s mobile operating system.\n    The company also accused Apple of barring rivals from\nlaunching their own gaming subscription service on its platform\nby preventing them from bundling several games together, even\nthough its own service, called Apple Arcade, does that.\n    Apple said its rules applied equally to all developers and\nthat Epic had violated them.\n    &quot;In ways a judge has described as deceptive and clandestine,\nEpic enabled a feature in its app, which was not reviewed or\napproved by Apple, and they did so with the express intent of\nviolating the App Store guidelines that apply equally to every\ndeveloper and protect customers,&quot; the company said in a\nstatement.\n    &quot;Their reckless behaviour made pawns of customers, and we\nlook forward to making this clear to the European Commission,&quot;\nit said.\n    Apple has been taking small steps in recent months towards\nchanging its practices, including lower fees for some developers\nand giving them a way to challenge its rulings, both of which\nhave not satisfied the company&apos;s critics. \n    Fortnite is slated to come back to the iPhone at some point\nin the mobile Safari browser. Epic and Apple in recent weeks\nhave been trading documents and conducting depositions ahead of\na scheduled May trial in Epic&apos;s lawsuit filed last year.\n    The Commission, which is investigating Apple&apos;s mobile\npayment system Apple Pay and the App Store, declined to comment\non the complaint, saying it was aware of the concerns regarding\nApple&apos;s App Store rules.\n    Epic Games has also complained to the UK Competition Appeal\nTribunal and to the Australian watchdog. It has not asked the EU\nenforcers nor their peers in the UK and Australia for damages.\n    Big companies such as Microsoft Corp  <Origin Href=\"QuoteRef\">MSFT.O</Origin> , Spotify\n <Origin Href=\"QuoteRef\">SPOT.N</Origin>  and Match Group Inc  <Origin Href=\"QuoteRef\">MTCH.O</Origin>  have also criticised\nApple&apos;s App Store fees and rules.\n\n (Reporting by Foo Yun Chee, additional reporting by Stephen\nNellis and Paresh Dave in San Francisco; Editing by Barbara\nLewis and Edmund Blair)\n ((foo.yunchee@thomsonreuters.com; +32 2 287 6844; Reuters\nMessaging: foo.yunchee.thomsonreuters.com@reuters.net))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20210217:nL1N2KN0EF");
			hm2.put("RT", "2021-02-17T09:07:56-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "CORRECTED-UPDATE 1-Epic Games takes Apple fight to EU antitrust regulators");
			hm2.put("CT", "2021-02-17T07:41:57-00:00");
			hm2.put("LT", "2021-02-17T09:07:56-00:00");

			hm3.put("TE",
					"<p>    By Foo Yun Chee\n    BRUSSELS, Feb 17 (Reuters) - Fortnite creator Epic Games has\ntaken its fight against Apple  <Origin Href=\"QuoteRef\">AAPL.O</Origin>  to EU antitrust\nregulators after failing to make headway in a U.S. court in a\ndispute over the iPhone maker&apos;s payment system on its App Store\nand control over apps downloads.\n    The two companies have been locked in a legal dispute since\nAugust last year when the game maker tried to get around Apple&apos;s\n30% fee on some in-app purchases on the App Store by launching\nits own in-app payment system.\n    That prompted Apple to kick Epic&apos;s Fortnite game off the App\nStore and threaten to terminate an affiliated account that would\nhave effectively blocked distribution of Unreal Engine, a\nsoftware tool used by hundreds of app makers to create games.\n    Epic Games founder and CEO Tim Sweeney said Apple&apos;s control\nof its platform has tilted the level playing field.\n    &quot;The 30% they charge as their app tax, they can make it 50%\nor 90% or 100%. Under their theory of how these markets are\nstructured, they have every right to do that,&quot; he told\nreporters.\n    &quot;Epic is not asking any court or regulator to change this\n30% to some other number, only to restore competition on IOS,&quot;\nhe said, referring to Apple&apos;s mobile operating system.\n    The company also accused Apple of barring rivals from\nlaunching their own gaming subscription service on its platform\nby preventing them from bundling several games together - when \nits own service, called Apple Arcade, does that.\n    Apple said its rules apply equally to all developers and\nthat Epic had violated them.\n    &quot;In ways a judge has described as deceptive and clandestine,\nEpic enabled a feature in its app, which was not reviewed or\napproved by Apple, and they did so with the express intent of\nviolating the App Store guidelines that apply equally to every\ndeveloper and protect customers,&quot; the company said in a\nstatement.\n    &quot;Their reckless behaviour made pawns of customers, and we\nlook forward to making this clear to the European Commission,&quot;\nit said.\n    The Commission, which is investigating Apple&apos;s mobile\npayment system Apple Pay and the App Store, declined to comment\non the complaint, saying it was aware of the concerns regarding\nApple&apos;s App Store rules.\n    Epic Games has also complained to the UK Competition Appeal\nTribunal and to the Australian watchdog, at the same time\nseeking damages. It has not asked the EU enforcers for damages.\n\n (Reporting by Foo Yun Chee; editing by Barbara Lewis)\n ((foo.yunchee@thomsonreuters.com; +32 2 287 6844; Reuters\nMessaging: foo.yunchee.thomsonreuters.com@reuters.net))</p>");
			hm3.put("ID", "urn:newsml:reuters.com:20210217:nL8N2KM5M1");
			hm3.put("RT", "2021-02-17T07:00:00-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Epic Games takes Apple fight to EU antitrust regulators");
			hm3.put("CT", "2021-02-17T07:00:00-00:00");
			hm3.put("LT", "2021-02-17T07:00:00-00:00");

			hm4.put("TE",
					"<p> (Adds background)\n    By Andrea Shalal\n    WASHINGTON, Feb 16 (Reuters) - Treasury Secretary Janet\nYellen stressed the importance of cooperation with the European\nUnion in a call with the European Commission&apos;s vice president\nfor the economy, Valdis Dombrovskis, on Tuesday, the U.S.\nTreasury Department said.\n    Earlier on Tuesday, Yellen in a conversation with European\nCentral Bank President Christine Lagarde had also underscored\nways to deepen transatlantic cooperation on economic and\nfinancial issues.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KM2MB</Origin>\n    In her discussion with the EU executive, Yellen emphasized\nthe importance of the transatlantic partnership and said she\naimed to work more closely with Brussels on key challenges,\nincluding &quot;ending the pandemic, supporting a strong global\neconomic recovery, fighting income inequality, and forcefully\naddressing the threat of climate change,&quot; the Treasury said.\n    Yellen committed to re-engaging in discussions on\ninternational taxation to forge a timely international accord,\nand to seek solutions to key bilateral trade issues, it said.\n    The Biden administration is seeking to rebuild ties with\nEuropean allies on all fronts after the deep rifts that\ncharacterized Donald Trump&apos;s presidency, and want to work\ntogether to counter what Yellen has called China&apos;s &quot;abusive,\nunfair and illegal practices.&quot;\n    While Trump, a Republican, repeatedly took aim at European\nallies, threatening and imposing tariffs on a range of goods,\nthe administration of Democratic President Joe Biden has sent\nfar more positive signals on issues ranging from trade to taxes.\n    EU officials last week welcomed Washington&apos;s decision to\nrefrain from imposing additional tariffs on EU goods in a\nlong-running dispute over aircraft tariffs, and said they were\nready to work to resolve trade disputes.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KI02F</Origin>\n    The United States and European countries have also said they\nwill redouble efforts to reenergize and conclude talks on global\ntaxation being led by the Organization for Economic Cooperation\nand Development.\n    Nearly 140 countries are negotiating the first update in a\ngeneration to the rules for taxing cross-border commerce, to\naccount for the emergence of big tech companies like Alphabet\nInc&apos;s Google  <Origin Href=\"QuoteRef\">GOOGL.O</Origin> , Apple Inc  <Origin Href=\"QuoteRef\">AAPL.O</Origin>  and Facebook Inc\n <Origin Href=\"QuoteRef\">FB.O</Origin> .\n    The talks stalled under the Trump administration after\nYellen&apos;s predecessor suspended U.S. involvement, but OECD\nministers say they now hope to reach a deal by mid-2021.\n\n (Reporting by Eric Beech and Andrea Shalal, writing by Mohammad\nZargham; Editing by Himani Sarkar and Leslie Adler)\n ((mohammad.zargham@thomsonreuters.com;))</p>");
			hm4.put("ID", "urn:newsml:reuters.com:20210217:nL1N2KN03J");
			hm4.put("RT", "2021-02-17T02:54:38-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "UPDATE 1-U.S. Treasury&apos;s Yellen vows to boost transatlantic cooperation ");
			hm4.put("CT", "2021-02-17T02:54:38-00:00");
			hm4.put("LT", "2021-02-17T02:54:38-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("LVMH.PA")) {

			hm1.put("TE",
					"<p>    BERLIN, Feb 15 (Reuters) - ProSiebenSat.1 Media  <Origin Href=\"QuoteRef\">PSMGn.DE</Origin> \nis seeking a buyer for its online beauty retailer Flaconi, a\nsource familiar with the matter said on Monday, as Chief\nExecutive Rainer Beaujean seeks to streamline the German\nbroadcasting group.\n    Potential buyers include German fashion e-tailer Zalando\n <Origin Href=\"QuoteRef\">ZALG.DE</Origin> , perfumery store chain Douglas and French luxury goods\ngroup LVMH&apos;s  <Origin Href=\"QuoteRef\">LVMH.PA</Origin>  Sephora, added the source, who requested\nanonymity due to the sensitivity of the matter.\n    A sale process is just beginning, the source said, after\nManager Magazine reported that Zalando had expressed an interest\nin buying Flaconi. ProSieben flagged Flaconi as a sale candidate\nlast September.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2GR4KH</Origin>\n    Restructuring specialist Beaujean, in the job for just under\na year, has put ProSieben&apos;s entire array of e-commerce\ninvestments under the microscope to see whether they fit with\nits core broadcasting mission.\n    He sold online drugstore Windstar Medical last autumn for\n280 million euros ($339 million) and has stated his intention to\nfloat dating platform Parship next year.\n    Flaconi reported revenue of around 300 million euros last\nyear, achieving top-line growth of 50% and turning a profit,\naccording to the source, who also said ProSieben hoped to agree\na sale in the second quarter.\n    Zalando and Douglas declined to comment, while Sephora could\nnot immediately be reached for comment.\n    ProSieben took a major hit to its core advertising revenue\nin the early stages of the coronavirus pandemic, but a recovery\ntowards the end of 2020 helped it report better-than-expected\npreliminary results.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2JW2LR</Origin>\n    The company, in which Italian broadcaster owns a 12.4%\ndirect stake, is due to report full results in early March.\n ($1 = 0.8249 euros)\n\n (Reporting by Douglas Busvine; additional reporting by Klaus\nLauer, Nadine Schimroszik and Matthias Inverardi; editing by\nHans Seidenstuecker and Kirsten Donovan)\n ((douglas.busvine@tr.com; +49 30 220 133 562;))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210215:nL8N2KL3QY");
			hm1.put("RT", "2021-02-15T15:39:31-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "ProSieben seeks buyer for online beauty products unit Flaconi - source ");
			hm1.put("CT", "2021-02-15T15:39:31-00:00");
			hm1.put("LT", "2021-02-15T15:39:31-00:00");

			hm2.put("TE",
					"<p>    * Mustier was previously UniCredit chief executive\n    * Blank cheque vehicle to be listed in Amsterdam\n    * SPACs have grown in popularity on Wall St and in Europe\n    * SPACs have raised $45.7 billion so far this year\n\n (Adds quotes)\n    By Sudip Kar-Gupta and Abhinav Ramnarayan\n    PARIS, Feb 15 (Reuters) - Former UniCredit boss Jean-Pierre\nMustier is teaming up with France&apos;s richest man Bernard Arnault\nto launch a special purpose acquisition company (SPAC) aimed at\nEuropean financial deals, as a boom in blank cheque vehicles\nspreads from the United States. \n    French investment firm Tikehau Capital SCA  <Origin Href=\"QuoteRef\">TKOO.PA</Origin>  is\nsponsoring the SPAC alongside Mustier, Financiere Agache, a\ngroup belonging to Arnault, as well as banker Diego De Giorgi,\nformerly of UniCredit  <Origin Href=\"QuoteRef\">CRDI.MI</Origin>  and Bank of America Merrill\nLynch  <Origin Href=\"QuoteRef\">BAC.N</Origin> .\n    The four of them will be equal partners in the venture,\nsources familiar with the situation said. The SPAC will be\nlisted in Amsterdam, and is the first of several being planned\nby this group. \n    &quot;We see SPACs as a natural extension of our toolbox now. We\nplan for repeated issuance and not just a one-off opportunistic\none,&quot; said one of the sources. \n    A SPAC is a shell company that raises money in an initial\npublic offering (IPO) to merge with a privately held company,\nthat then becomes publicly traded as a result.\n    SPACs have emerged as a popular IPO alternative for\ncompanies, especially in the United States, providing a path to\ngoing public with less regulatory scrutiny and more certainty\nover the valuation that will be attained and funds raised.\n    Overall, 144 SPACs have raised $45.7 billion so far this\nyear, based on data from SPAC Research, with backers including\nhigh-profile investors, politicians and sports personalities.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KI3DH</Origin>\n    The huge roster of deals there has meant that there have\nbeen concerns over a saturation of that market, which helped\nfuel the decision to list in Europe instead. \n    &quot;We looked at the U.S. market and decided to do it in\nAmsterdam instead. We thought our proposal was differentiated by\ntaking it to Europe, particularly when we are looking to target\nEuropean companies,&quot; said the source. \n    &quot;But we have looked to replicate a U.S. structure so global\ninvestors understand it, and Amsterdam seemed best suited for\nthat. You may have also seen that Amsterdam has taken over\nLondon in terms of (stock market) flows,&quot; he added. \n    Bankers agreed that Amsterdam is emerging as the destination\nof choice in Europe, as regulations there are closest to the\nUnited States, where protections for SPAC investors are better\nthan in many jurisdictions. \n    For example, Dutch regulations allow investors to redeem\ntheir investment if the target eventually identified is not to\ntheir liking.\n    In the financial services arena, other big name sponsors are\nalso emerging. Former Credit Suisse CEO Tidjane Thiam is also\nlaunching a SPAC that will list in New York and focus on\nfinancial services, while Martin Blessing, a former chief\nexecutive officer of Commerzbank AG, is planning one listed in\nthe Netherlands.\n    The Tikehau SPAC will target areas such as financial\ntechnology or fintech, fund management, insurance and other\ndiversified financial services.         \n    This follows the first one launched in France in December by\nXavier Niel, former Lazard banker Matthieu Pigasse and\nentrepreneur Moez-Alexandre Zouari, aimed at deals in the health\nand food industries.   <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2IP41U</Origin>\n    Mustier, who also had a spell running French bank Societe\nGenerale&apos;s  <Origin Href=\"QuoteRef\">SOGN.PA</Origin>  investment banking arm, left UniCredit this\nmonth. \n    In nearly five years at the helm, Mustier rebuilt\nUniCredit&apos;s capital reserves and cleaned up its balance sheet,\nbut failed to find sustained profit drivers or lift the bank&apos;s\ndepressed stock price.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KG7F2</Origin>\n    Arnault is best known as the founder of luxury goods group\nand Louis Vuitton owner LVMH  <Origin Href=\"QuoteRef\">LVMH.PA</Origin> . His personal holding\ncompany, Financiere Agache, is a long-standing investor in\nTikehau, the investment firm said. \n\n (Reporting by Sudip Kar-Gupta, additional reporting by Sarah\nWhite and Abhinav Ramnarayan; Editing by Jane Merriman and David\nEvans)\n ((sudip.kargupta@thomsonreuters.com; +33 1 49 49 53 84;))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20210215:nL1N2KL0A8");
			hm2.put("RT", "2021-02-15T13:34:21-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "UPDATE 5-Ex-UniCredit CEO Mustier, LVMH&apos;s Arnault form SPAC for financial deals");
			hm2.put("CT", "2021-02-15T07:12:29-00:00");
			hm2.put("LT", "2021-02-15T13:34:21-00:00");

			hm3.put("TE",
					"<p>    * Q4 sales top estimates, China revenues boom\n    * L&apos;Oreal outperformed beauty peers in 2020 -CEO\n    * Surging online sales help mitigate COVID-19 hit\n\n (Adds details on online business, China, CEO comments)\n    By Sarah White\n    PARIS, Feb 11 (Reuters) - L&apos;Oreal  <Origin Href=\"QuoteRef\">OREP.PA</Origin> , the world&apos;s\nbiggest cosmetics group, posted higher-than-expected revenue\ngrowth for the fourth quarter on Thursday in part as  beauty\nshoppers flocked online during the coronavirus pandemic.  \n   Earnings at luxury retailers and beauty companies were dented\nby the closure of airport duty-free shops and high street\nstores, while the health crisis has changed consumer habits as\npeople stay home and face masks reduce demand for make-up.\n    But cosmetics groups have benefited from demand for skincare\nproducts as well as pampering treatments for use at home,\nincluding hair dyes. \n    France&apos;s L&apos;Oreal, which is behind the Maybelline and Lancome\nbrands, said an easing of lockdowns in the second half of 2020,\nwith the reopening of hair salons, had also helped sales of its\nprofessional products. \n    Its online revenues jumped 62% in 2020 as a whole, reaching\nover a quarter of total sales. \n    &quot;With the pandemic, we&apos;ve advanced by five years on the\ndigital side,&quot; Chairman and Chief Executive Jean-Paul Agon told\nLes Echos newspaper, ahead of a conference call with analysts on\nFriday. \n    L&apos;Oreal&apos;s sales reached 7.88 billion euros ($9.56 billion)\nin the October to December period, flat from a year earlier on a\nreported basis but a rise of 4.8% like-for-like, without\ncurrency effects and acquisitions. \n    That beat estimates that ranged from flat to 3% growth, and\nmarked a pick-up from a quarter earlier.\n    The group said sales in China in particular were\n&quot;spectacular,&quot; echoing rebounding demand noted by fashion groups\nsuch as Louis Vuitton owner LVMH  <Origin Href=\"QuoteRef\">LVMH.PA</Origin>  after lockdown\nrestrictions were eased. \n    \n    THE ROARING TWENTIES?\n    Agon - who is set to hand over the CEO role to his No. 2,\nNicolas Hieronimus, later this year - told Les Echos that\nL&apos;Oreal had outperformed an 8% fall in the broader beauty market\nin 2020, with the group&apos;s yearly sales down 4.1% like-for-like. \n    The company maintained all of its product launches for 2020,\nwhich helped, he added. \n    Agon said he remained prudent for 2021 but was confident\ndemand for beauty products would soar in the longer term.\n    &quot;I&apos;m convinced that when we come out of this crisis, it will\nbe like the 1920s,&quot; he told the newspaper. \n    &quot;After years of anxiety, there will be a feeling of freedom,\nof wanting to party and go out and socialise again, and to wear\nmake-up and perfume.&quot;\n    L&apos;Oreal posted a 5% drop in net profit for 2020 as a whole,\nat 3.75 billion euros, and said it would raise its dividend by\n3.9% to 4 euros a share. \n    L&apos;Oreal&apos;s operating margins reached 18.6%, unchanged from a\nyear earlier.   \n    ($1 = 0.8246 euros)\n\n (Reporting by Sarah White; Editing by Edmund Blair and Steve\nOrlofsky)\n ((sarah.white1@thomsonreuters.com; + 33 (0) 1 49 49 56 85;))</p>");
			hm3.put("ID", "urn:newsml:reuters.com:20210211:nL1N2KH23V");
			hm3.put("RT", "2021-02-11T18:41:36-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "UPDATE 2-L&apos;Oreal sales rebound as beauty business shifts online in pandemic");
			hm3.put("CT", "2021-02-11T17:38:00-00:00");
			hm3.put("LT", "2021-02-11T18:41:36-00:00");

			hm4.put("TE",
					"<p> (Adds details)\n    PARIS/MILAN, Feb 10 (Reuters) - Louis Vuitton owner LVMH\n <Origin Href=\"QuoteRef\">LVMH.PA</Origin>  and music star Rihanna have agreed to suspend her\nfashion line Fenty less than two years after its launch, the\nFrench luxury goods giant said on Wednesday.\n    LVMH said in a statement that Fenty&apos;s ready to wear\nactivity, based in Paris, would be &quot;put on hold&quot; pending better\nconditions - a rare setback for the luxury group, which has\nweathered the coronavirus crisis better than most rivals.\n    The R&amp;B singer and LVMH launched the Fenty fashion brand in\nMay 2019, only the second time the French group had set a label\nup from scratch as it looked to tap soaring demand for luxury\ncelebrity collaborations.\n    LVMH did not elaborate on the reasons for hitting the pause\nbutton but after a big launch and debut collection, the brand \nkept a low profile and never followed up with major marketing\nevents, even before the COVID-19 crisis. \n    Trade publication WWD, which first reported news that the\nlabel was being suspended, said on Wednesday that a skeleton\nstaff remained at the Paris headquarters to wind down remaining\noperations.  \n    Asked about the fashion venture during the group&apos;s\nthird-quarter earnings call last October, LVMH&apos;s finance chief\nJean-Jacques Guiony called it &quot;a work in progress&quot;: &quot;We are\nstill in a launching phase, and we have to figure out exactly\nwhat is the right offer. It&apos;s not something that is easy.&quot;\n     LVMH also said on Wednesday that private equity fund L\nCatterton, which is connected to the group, had taken a stake in\nSavage X Fenty, Rihanna&apos;s lingerie line.\n    &quot;LVMH and Rihanna reaffirm their ambition to concentrate on\nthe growth and the long-term development of Fenty ecosystem\nfocusing on cosmetics, skincare and lingerie,&quot; it added.\n    Fenty - after the &quot;Umbrella&quot; hitmaker&apos;s full name, Robyn\nRihanna Fenty - was meant to build on the singer&apos;s joint\ncosmetics venture with LVMH, with a full range of clothing,\nshoes and accessories.\n    It was the first label the acquisitive group - also home to\nstoried couture houses such as Christian Dior and Givenchy - had\nfully created since Christian Lacroix’s eponymous brand launched\nin 1987. It eventually sold that on in 2005 after it struggled\nto ever turn a profit. \n    \n    \n\n (Reporting by Silvia Aloisi and Sarah White; Editing by Kirsten\nDonovan)\n ((silvia.aloisi@thomsonreuters.com; +39 02 66129 723; Reuters\nMessaging: silvia.aloisi.thomsonreuters.com@reuters.net))</p>");
			hm4.put("ID", "urn:newsml:reuters.com:20210210:nL8N2KG4XL");
			hm4.put("RT", "2021-02-10T14:32:42-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "UPDATE 2-LVMH, Rihanna to pause Fenty fashion venture, focus on lingerie, cosmetics");
			hm4.put("CT", "2021-02-10T13:21:10-00:00");
			hm4.put("LT", "2021-02-10T14:32:42-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AMAG")) {

			hm1.put("TE",
					"<p>For best results when printing this announcement, please click on link below:\n<Origin Href=\"Link\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210209:nGNX2QQTmV&amp;default-theme=true</Origin>\n\n\nWARREN, N.J., Feb. 09, 2021 (GLOBE NEWSWIRE) -- Aquestive Therapeutics, Inc.\n(NASDAQ: AQST), a pharmaceutical company focused on developing and\ncommercializing differentiated products that address patients’ unmet needs\nand solve therapeutic problems, today announced the appointment of Julie Krop,\nM.D., Chief Medical Officer of Freeline Therapeutics (NASDAQ: FRLN), and Marco\nTaglietti, M.D., Director, President and Chief Executive Officer of SCYNEXIS\n(NASDAQ: SCYX), to the Board of Directors of the Company effective February\n10, 2021. Aquestive also announced the resignation of Douglas K. Bratton from\nthe Board of Directors after more than 17 years of service. Aquestive’s\nBoard of Directors will now be comprised of eight Directors, seven of whom are\nindependent. Dr. Krop will serve as a member of the Board’s Nominating and\nCorporate Governance Committee and Dr. Taglietti will serve as a member of the\nBoard’s Audit Committee.\n\n“We are delighted to welcome Julie and Marco as new independent directors to\nthe Aquestive Board of Directors,&quot; said Santo Costa, Aquestive’s Chairman of\nthe Board. &quot;Their significant expertise in the pharmaceutical and\nbiotechnology industry and impressive backgrounds complement our Board of\nDirectors’ skills and experiences. We are confident they will provide\nvaluable perspectives as we continue to execute our strategy and enhance value\nfor the Company’s shareholders.”\n\nDr. Krop stated, “I am thrilled to join the Board of Directors of Aquestive\nand look forward to working with the Board and the leadership team to advance\nthe Company’s mission of providing novel alternatives to invasively\nadministered standard of care therapies. I am particularly excited about\nhelping management guide Aquestive’s proprietary orally administered\nepinephrine through clinical development. This novel formulation of\nepinephrine has the potential to eliminate the burden of intramuscular or\nsubcutaneous injections for patients at risk of anaphylaxis due to severe\nallergies.”\n\nDr. Taglietti commented, “Aquestive is at an important stage of its\nevolution and I am delighted to join the Board of Directors during this\nexciting time. I look forward to contributing to Aquestive’s continued\ngrowth and success as the Company advances its Libervant™ (diazepam) Buccal\nFilm application through approval and executes on its innovative development\nactivities with the potential to change patients’ lives.”\n\n“Julie and Marco join Aquestive at an exciting time as we continue to focus\non developing and bringing to market valuable products in the CNS and allergy\nspaces. Julie and Marco’s extensive collective experience in leading\nclinical development, regulatory strategies and commercialization of key value\nassets is a great addition and complement to the skills already present on our\nboard,” remarked Keith Kendall, Director, President and Chief Executive\nOfficer of Aquestive. “We’ve experienced significant growth and\nstrengthened our capabilities as a commercial pharmaceutical company under\nDoug’s leadership as the original Chairman of the Board and then director. I\nwould like to thank him for his contributions and expert guidance that have\nwell positioned Aquestive for continued momentum and success.”\n\n“On behalf of the Board of Directors and the Company, I would like to thank\nDoug for his more than 17 years of service to our Board, including as former\nChairman, in guiding the Company from its start-up phase to a public\ncommercial pharmaceutical company and leader in the film-based therapeutics\nindustry,” said Mr. Costa.\n\n“It has been a great privilege to serve as a director and former Chairman of\nAquestive’s Board of Directors, the members of which I hold in very high\nregard. I’d like to thank Keith, his team, and the Board for many exciting\nand satisfying years, and look forward to watching them accomplish even more\ngreat things for Aquestive in the months and years to come,” stated Mr.\nBratton.\n\nAbout Douglas K. Bratton\nMr. Bratton has served as a member of the Company’s Board of Directors since\nJanuary 2004 and was the Chairman of the Board from January 2004 until August\n2018. Mr. Bratton is the Founder, President and Chief Investment Officer of\nCrestline Investors, an institutional alternative investment management firm.\nHe has been an investment professional specializing in alternative asset\nstrategies since 1983 and has managed assets on behalf of the Bass family of\nFort Worth, Texas since 1988.\n\nAbout Julie Krop, M.D.\nDr. Krop is a seasoned biotech executive with more than two decades of\nexperience successfully designing and executing clinical development programs\nfrom early stage development all the way through FDA approval. She has held\nsenior leadership roles across clinical development, regulatory affairs,\nclinical operations, pharmacovigilance, medical affairs and program management\nduring her career in the pharmaceutical and biotechnology industry. She\ncurrently serves as the Chief Medical Officer of Freeline Therapeutics\n(NASDAQ: FRLN). Prior to assuming her position with Freeline Therapeutics, Dr.\nKrop was the Chief Medical Officer and Executive Vice President, Development,\nat AMAG Pharmaceuticals (NASDAQ: AMAG) from 2015 to 2020. From 2012 to 2015,\nDr. Krop served as the Vice President, Clinical Development for Vertex\nPharmaceuticals (NASDAQ: VRTX). In addition, Dr. Krop was Vice President,\nClinical Development and Regulatory Affairs for Stryker Biotech (NYSE: SYK)\nfrom 2006 to 2012. Dr. Krop received a B.A. from Brown University and her M.D.\nfrom Brown University School of Medicine. She completed her fellowship in the\nDepartment of Endocrinology at the Johns Hopkins University School of Medicine\nwhere she was also a Robert Wood Johnson Foundation Clinical Scholar.\n\nAbout Marco Taglietti, M.D.\nDr. Taglietti has more than three decades of experience in the pharmaceutical\nand biotechnology industry. He currently serves as a Director and President\nand Chief Executive Officer of SCYNEXIS Inc. (NASDAQ: SCYX). Prior to joining\nSCYNEXIS, Dr. Taglietti held various executive positions with Forest\nLaboratories (now AbbVie (NYSE: ABBV)) from 2007 until 2014, including\nPresident, Forest Research Institute, Chief Medical Officer and Executive\nCorporate Vice President, Research &amp; Development. Dr. Taglietti was also the\nSenior Vice President, Head of Global Research and Development for Stiefel\nLaboratories, Inc. (now a GlaxoSmithKline company) from 2004 until 2007 and\nserved in a number of executive positions from 1992 to 2004 with\nSchering-Plough Research Institute, including Vice President, Clinical\nResearch Anti-Infectives, CNS, Dermatology and Endocrinology. From 1987 until\n1992, Dr. Taglietti served in a number of executive positions with Marion\nMerrell Dow Research Institute, including as the European Product Team Leader\n– Anti-Infectives. Dr. Taglietti previously served on the boards of\ndirectors of Delcath (NASDAQ: DCTH) from 2014 to 2020 and NephroGenex (NASDAQ:\nNRX) from 2014 to 2017. Dr. Taglietti also served as a director of Stiefel\nInternational, Ltd., a private company, from 2004 to 2007 and a director of\nTransCelerate BioPharma, a non-profit pharma coalition dedicated to\nstreamlining and accelerating the research and development of innovative new\ntherapies, from 2013 to 2014. Since 2011, Dr. Taglietti has served on the\nboard of directors of BioNJ, a life sciences trade association in New Jersey.\nIn addition, Dr. Taglietti served on the board of directors of HINJ, Health\nInstitute of New Jersey, a trade association for the leading research-based\nbiopharmaceutical and medical technology companies in New Jersey, from 2011 to\n2014, and is currently on the boards of directors of Orchestra of St. Luke, a\nNew York City based orchestra, and American Foundation for Suicide Prevention,\nthe largest non-profit organization dedicated to saving lives and bringing\nhope to those affected by suicide. Dr. Taglietti received his Degree in\nMedicine from the University of Pavia, Italy.\n\nAbout Aquestive Therapeutics\nAquestive Therapeutics is a pharmaceutical company that applies innovative\ntechnology to solve therapeutic problems and improve medicines for patients.\nThe Company has commercialized one internally-developed proprietary product to\ndate, Sympazan® (clobazam) oral film, has a commercial proprietary product\npipeline focused on the treatment of diseases of the central nervous system,\nor CNS, and other unmet needs, and is developing orally administered complex\nmolecules to provide alternatives to invasively administered standard of care\ntherapies. The Company also collaborates with other pharmaceutical companies\nto bring new molecules to market using proprietary, best-in-class\ntechnologies, like PharmFilm®, and has proven capabilities for drug\ndevelopment and commercialization.\n\nForward-Looking Statements\nCertain statements in this press release are “forward-looking statements”\nwithin the meaning of the Private Securities Litigation Reform Act of 1995.\nWords such as “believe,” “anticipate,” “plan,” “expect,”\n“estimate,” “intend,” “may,” “will,” or the negative of those\nterms, and similar expressions, are intended to identify forward-looking\nstatements. These forward-looking statements include, but are not limited to,\nstatements regarding the advancement of Libervant and other product candidates\nthrough the regulatory and development pipeline; and business strategies,\nmarket opportunities, and other statements that are not historical facts.\nThese forward-looking statements are subject to the uncertain impact of the\nCOVID-19 global pandemic on our business including with respect to our\nclinical trials including site initiation, patient enrollment and timing and\nadequacy of clinical trials; on regulatory submissions and regulatory reviews\nand approvals of our product candidates; pharmaceutical ingredient and other\nraw materials supply chain, manufacture, and distribution; sale of and demand\nfor our products; our liquidity and availability of capital resources;\ncustomer demand for our products and services; customers’ ability to pay for\ngoods and services; and ongoing availability of an appropriate labor force and\nskilled professionals. Given these uncertainties, the Company is unable to\nprovide assurance that operations can be maintained as planned prior to the\nCOVID-19 pandemic.\n\nThese forward-looking statements are based on our current expectations and\nbeliefs and are subject to a number of risks and uncertainties that could\ncause actual results to differ materially from those described in the\nforward-looking statements. Such risks and uncertainties include, but are not\nlimited to, risks associated with the Company&apos;s development work, including\nany delays or changes to the timing, cost and success of our product\ndevelopment activities and clinical trials and plans for AQST-108 and our\nother drug candidates; risk of delays in FDA approval of our drug candidate\nLibervant and AQST-108 and our other drug candidates or failure to receive\napproval; ability to address the concerns identified in the FDA’s Complete\nResponse Letter dated September 25, 2020 regarding the New Drug Application\nfor Libervant; risk of our ability to demonstrate to the FDA “clinical\nsuperiority” within the meaning of the FDA regulations of Libervant relative\nto FDA-approved diazepam rectal gel and nasal spray products including by\nestablishing a major contribution to patient care within the meaning of FDA\nregulations relative to the approved products as well as risks related to\nother potential pathways or positions which are or may in the future be\nadvanced to the FDA to overcome the seven year orphan drug exclusivity granted\nby the FDA for the approved nasal spray product of a competitor in the U.S.\nand there can be no assurance that we will be successful; risk that a\ncompetitor obtains FDA orphan drug exclusivity for a product with the same\nactive moiety as any of our other drug products for which we are seeking FDA\napproval and that such earlier approved competitor orphan drug blocks such\nother product candidates in the U.S. for seven years for the same indication;\nrisk inherent in commercializing a new product (including technology risks,\nfinancial risks, market risks and implementation risks and regulatory\nlimitations); risks and uncertainties concerning the royalty and other revenue\nstream of the KYNMOBI™ monetization transaction, achievement of royalty\ntargets worldwide or in any jurisdiction and certain other commercial targets\nrequired for contingent payments under the monetization transaction, and of\nsufficiency of net proceeds of the monetization transaction after satisfaction\nof and compliance with 12.5% Senior Notes obligations, as applicable, and for\nfunding the Company’s operations; risk of development of our sales and\nmarketing capabilities; risk of legal costs associated with and the outcome of\nour patent litigation challenging third party at risk generic sale of our\nproprietary products; risk of sufficient capital and cash resources, including\naccess to available debt and equity financing and revenues from operations, to\nsatisfy all of our short-term and longer term cash requirements and other cash\nneeds, at the times and in the amounts needed; risk of failure to satisfy all\nfinancial and other debt covenants and of any default; our and our\ncompetitors’ orphan drug approval and resulting drug exclusivity for our\nproducts or products of our competitors; short-term and long-term liquidity\nand cash requirements, cash funding and cash burn; risk related to government\nclaims against Indivior for which we license, manufacture and sell Suboxone®\nand which accounts for the substantial part of our current operating revenues;\nrisk associated with Indivior’s cessation of production of its authorized\ngeneric buprenorphine naloxone film product, including the impact from loss of\norders for the authorized generic product and risk of eroding market share for\nSuboxone and risk of sunsetting product; risks related to the outsourcing of\ncertain marketing and other operational and staff functions to third parties;\nrisk of the rate and degree of market acceptance of our product and product\ncandidates; the success of any competing products, including generics; risk of\nthe size and growth of our product markets; risks of compliance with all FDA\nand other governmental and customer requirements for our manufacturing\nfacilities; risks associated with intellectual property rights and\ninfringement claims relating to the Company&apos;s products; risk of unexpected\npatent developments; the impact of existing and future legislation and\nregulatory provisions on product exclusivity; legislation or regulatory\nactions affecting pharmaceutical product pricing, reimbursement or access;\nclaims and risks that may arise regarding the safety or efficacy of the\nCompany&apos;s products and product candidates; risk of loss of significant\ncustomers; risks related to legal proceedings, including patent infringement,\ninvestigative and antitrust litigation matters; changes in government laws and\nregulations; risk of product recalls and withdrawals; uncertainties related to\ngeneral economic, political, business, industry, regulatory and market\nconditions and other unusual items; and other uncertainties affecting the\nCompany described in the “Risk Factors” section and in other sections\nincluded in our Annual Report on Form 10 K, in our Quarterly Reports on Form\n10-Q, and in our Current Reports on Form 8-K filed with the Securities\nExchange Commission (SEC). Given those uncertainties, you should not place\nundue reliance on these forward-looking statements, which speak only as of the\ndate made. All subsequent forward-looking statements attributable to us or any\nperson acting on our behalf are expressly qualified in their entirety by this\ncautionary statement. The Company assumes no obligation to update\nforward-looking statements or outlook or guidance after the date of this press\nrelease whether as a result of new information, future events or otherwise,\nexcept as may be required by applicable law.\n\nPharmFilm®, Sympazan® and the Aquestive logo are registered trademarks of\nAquestive Therapeutics, Inc. All other registered trademarks referenced herein\nare the property of their respective owners.\n\nInvestor inquiries:\nWestwicke, an ICR Company\nStephanie Carrington\nstephanie.carington@westwicke.com\n646-277-1282\n\n \n\n(<Origin Href=\"Link\">https://www.globenewswire.com/NewsRoom/AttachmentNg/e0b65f93-9565-43d7-a6ff-88b8cbd6adea</Origin>)\n\n\n\nGlobeNewswire, Inc. 2021</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210209:nGNX2QQTmV");
			hm1.put("RT", "2021-02-09T12:30:00-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"Aquestive Therapeutics Strengthens Board of Directors with Appointments of Julie Krop, M.D., and Marco Taglietti, M.D., and Announces Resignation of Douglas K. Bratton from Board of Directors");
			hm1.put("CT", "2021-02-09T12:30:00-00:00");
			hm1.put("LT", "2021-02-09T12:30:00-00:00");

			hm2.put("TE",
					"<p>For best results when printing this announcement, please click on link below:\n<Origin Href=\"Link\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210209:nBw2lPwyXa&amp;default-theme=true</Origin>\n\n\nGemini Therapeutics Appoints Brian Piekos as Chief Financial Officer\n\nGemini Therapeutics, Inc., (Nasdaq: GMTX) a clinical stage precision medicine\ncompany developing innovative treatments for genetically defined age-related\nmacular degeneration (AMD), today announced that Brian Piekos has been\nappointed to serve as Chief Financial Officer. Mr. Piekos brings more than 20\nyears of experience in industry and finance to Gemini.\n\n“It is my pleasure to welcome Brian to the team at this exciting time in\nGemini’s history right on the heels of our debut as a public company,”\nsaid Jason Meyenburg, Chief Executive Officer of Gemini. “Brian’s deep\nfinancial experience and results-driven attitude will be invaluable to us as\nwe continue to develop first-in-class medicines, using insights in genetics\nand biology, to restore regulation of the complement system in the eye and\nthroughout the body.”\n\n“I look forward to bringing my unique perspective and industry experience to\nthis seasoned leadership team and supporting Gemini on their path to see\ndisease differently,” said Mr. Piekos. “With the promising safety profile\ndemonstrated by GEM103 to date, and more trials underway, I believe Gemini is\npositioned for incredible value.”\n\nMr. Piekos joins Gemini from AMAG Pharmaceuticals, where he was most recently\nExecutive Vice President, Chief Financial Officer and Treasurer. Prior to\njoining AMAG, he held leadership roles in Corporate Finance, Tax and Treasury\nat Cubist Pharmaceuticals. Mr. Piekos began his career as a healthcare\ninvestment banker at Needham &amp; Company and Leerink Partners, now SVB\nLeerink. Mr. Piekos earned his MBA from the Simon Business School at the\nUniversity of Rochester. He obtained an M.S. in molecular biology from the\nUniversity of Massachusetts Medical School and a B.A. in biochemistry from\nIthaca College.\n\nAbout GEM103\n\nGemini’s lead program, GEM103, is a pioneering precision medicine approach,\ntargeting trial enrichment with genetically-defined patients. GEM103 targets a\ngenetically-defined subset of age-related macular degeneration (AMD) patients\nwith complement dysregulation. Of the 15 million dry AMD patients,\napproximately 40% (or six million) have variants in the complement factor H\n(CFH) gene. Such loss-of-function variants are associated with increased dry\nAMD disease risk. GEM103 is believed to be the first ever recombinant native\ncomplement modulator, full-length recombinant complement factor H (rCFH)\nprotein. When delivered by intravitreal injection, GEM103 has the potential to\naddress unmet medical need in genetically-defined subsets of AMD patients by\ncircumventing dysfunctional CFH loss-of-function variants and slowing the\nprogression of their retinal disease.\n\nAbout Dry Age-Related Macular Degeneration (AMD)\n\nAge-related macular degeneration (AMD) is a progressive retinal disease\naffecting millions of older adults, and the leading cause of irreversible\nblindness in the western world. Symptoms, which include blurry vision, loss of\nnight vision and loss of central vision, make activities of daily living such\nas reading, driving and even recognizing faces progressively more difficult.\nThird-party reports indicate there are approximately 16 million patients with\nAMD in the United States alone. Dry AMD, which results from an interaction of\nenvironmental and genetic risk factors, represents about 90% of that\npopulation (or about 15 million) in the US compared to about 1.4 million with\nwet AMD. Genetic risk of developing dry AMD is significant, with approximately\n70% attributable risk of advanced disease to heritability, while aging and\nsmoking confer the strongest non-genetic risk. CFH risk variants occur in\napproximately 40% of patients with dry AMD and these patients have a\nsignificantly increased risk of developing the disease as well as progression\nfrom intermediate AMD to GA. The complement system, of which CFH is a\nmodulator, is dysregulated in patients with these risk variants, and results\nin amplification of aberrant inflammatory responses in the eye. Over time,\nthis dysregulation leads to damage to the macular region of the retina.\n\nAbout Gemini Therapeutics\n\nGemini Therapeutics is a clinical stage precision medicine company developing\nnovel therapeutic compounds to treat genetically defined age-related macular\ndegeneration (AMD). Gemini’s lead candidate, GEM103, is a recombinant form\nof the human complement factor H protein (CFH), and is designed to address\nboth complement hyperactivity and restore retinal health in patients with AMD.\nGEM103 is currently in a Phase 2a trial in dry AMD patients with a CFH risk\nvariant. The company has generated a rich pipeline including recombinant\nproteins, gene therapies, and monoclonal antibodies.\n\nFor more information, visit <Origin Href=\"Link\">www.geminitherapeutics.com</Origin>\n(<Origin Href=\"Link\">https://cts.businesswire.com/ct/CT?id=smartlink&amp;url=http%3A%2F%2Fwww.geminitherapeutics.com&amp;esheet=52375664&amp;newsitemid=20210209005149&amp;lan=en-US&amp;anchor=www.geminitherapeutics.com&amp;index=1&amp;md5=0a9d1e467fd35e289f0e948dac442c04</Origin>)\n.\n\nForward-Looking Statements\n\nCertain statements in this press release and the information incorporated\nherein by reference may constitute “forward-looking statements” for\npurposes of the federal securities laws. Our forward-looking statements\ninclude, but are not limited to, statements regarding our or our management\nteam’s expectations, hopes, beliefs, intentions or strategies regarding the\nfuture, including those relating to the success, cost and timing of our\nproduct development activities and clinical trials, including our estimates\nregarding when data will be reported from ongoing clinical trials and the\ntiming to commence future clinical trials, the potential attributes and\nbenefits of our product candidates, including GEM103, our ability to obtain\nand maintain regulatory approval for our product candidates and our ability to\nobtain funding for our operations when needed. Forward-looking statements\ninclude statements relating to our management team’s expectations, hopes,\nbeliefs, intentions or strategies regarding the future. In addition, any\nstatements that refer to projections, forecasts or other characterizations of\nfuture events or circumstances, including any underlying assumptions, are\nforward-looking statements. The words “anticipate,” “believe,”\n“contemplate,” “continue,” “could,” “estimate,” “expect,”\n“intends,” “may,” “might,” “plan,” “possible,”\n“potential,” “predict,” “project,” “should,” “will,”\n“would” and similar expressions may identify forward-looking statements,\nbut the absence of these words does not mean that a statement is not\nforward-looking. These forward-looking statements are based on current\nexpectations and beliefs concerning future developments and their potential\neffects. There can be no assurance that future developments affecting us will\nbe those that we have anticipated. These forward-looking statements involve a\nnumber of risks, uncertainties (some of which are beyond our control) or other\nassumptions that may cause actual results or performance to be materially\ndifferent from those expressed or implied by these forward-looking statements.\nThese risks and uncertainties include, but are not limited to, those factors\ndescribed under the heading “Risk Factors” in the final proxy/prospectus\nfor our recently completed business combination, and those that are included\nin any of our future filings with the SEC. Should one or more of these risks\nor uncertainties materialize, or should any of our assumptions prove\nincorrect, actual results may vary in material respects from those projected\nin these forward-looking statements. Some of these risks and uncertainties may\nin the future be amplified by the COVID-19 pandemic and there may be\nadditional risks that we consider immaterial or which are unknown. It is not\npossible to predict or identify all such risks. Our forward-looking statements\nonly speak as of the date they are made, and we do not undertake any\nobligation to update or revise any forward-looking statements, whether as a\nresult of new information, future events or otherwise, except as may be\nrequired under applicable securities laws.\n\n\n\nView source version on businesswire.com:\n<Origin Href=\"Link\">https://www.businesswire.com/news/home/20210209005149/en/</Origin>\n(<Origin Href=\"Link\">https://www.businesswire.com/news/home/20210209005149/en/</Origin>)\n\nGemini Investors: \n\nArgot Partners\n\nSherri Spear\n\n212-600-1902\n\ngemini@argotpartners.com (mailto:gemini@argotpartners.com)\n\nGemini Media: \n\nArgot Partners\n\nJoshua R. Mansbach\n\n212-600-1902\n\ngemini@argotpartners.com (mailto:gemini@argotpartners.com)\n\n\nCopyright Business Wire 2021</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20210209:nBw2lPwyXa");
			hm2.put("RT", "2021-02-09T12:00:00-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "Gemini Therapeutics Appoints Brian Piekos as Chief Financial Officer");
			hm2.put("CT", "2021-02-09T12:00:00-00:00");
			hm2.put("LT", "2021-02-09T12:00:00-00:00");

			hm3.put("TE",
					"<p>For best results when printing this announcement, please click on link below:\n<Origin Href=\"Link\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210202:nPre3JLhRa&amp;default-theme=true</Origin>\n\nIV and Oral Iron Drugs Market Size to Reach Revenues of around USD 10 Billion\nby 2026 - Arizton\nPR Newswire\n\nCHICAGO, Feb. 2, 2021\n\nCHICAGO, Feb. 2, 2021 /PRNewswire/ -- In-depth analysis and data-driven\ninsights on the impact of COVID-19 included in this global IV and oral iron\ndrugs market report.\n\nThe global IV and oral iron drugs market\n(<Origin Href=\"Link\">https://www.arizton.com/market-reports/iv-iron-oral-iron-drugs-market</Origin>) is\nexpected to grow at a CAGR of over 11.6% during the period 2020−2026.\n\nKey Highlights Offered in the Report:  \n1. The demand for prescription iron drugs is relatively high in developed\neconomies such as the US and few European countries. This high demand is one\nof the major reasons for the global market to grow at a CAGR of around 12%\nduring the forecast period. Approval of few branded IV drugs such as Feraccru,\nMonofer, and Injectafer in newer markets will further contribute to the\noverall growth of the market. \n2. The IV iron drugs market value is likely to increase by 2.5 times in 2026\ncompared to 2020 revenue and dominate the global IV &amp; oral iron drugs market\nwith a share of around 67% in 2026. \n3. Ferinject/Injectafer is the major branded iron drug approved in around 85\ncountries generating millions of revenues worldwide both for Vifor Pharma and\nDaiichi Sankyo.     \n4. The US is likely to witness faster incremental growth of around USD 2\nbillion due to high penetration of branded iron drugs over generic ones. \n5. Nephrology application segment will register an incremental growth of more\nthan USD 3.5 billion during the forecast period. This is attributable to the\navailability of commercial branded iron drugs for treating iron deficiency in\nchronic kidney disease patients. \n6. Vendors should rely on strategic collaborations for quick product access\nand to penetrate new markets and expand in existing markets. For instance,\nDaiichi Sankyo Company acquired commercial rights of Injectafer in the US and\nCanada from Vifor Pharma Group. Likewise, Shield Therapeutics gave commercial\nrights of Feraccru to Norgine, AOP Orphan Pharmaceuticals, and Ewopharma.  \nKey Offerings:\n* Market Size &amp; Forecast by Revenue | 2020−2026 \n* Market Dynamics – Leading trends, growth drivers, restraints, and\ninvestment opportunities \n* Market Segmentation – A detailed analysis by route of administration,\napplication, patient group, distribution, and geography \n* Competitive Landscape – 2 key vendors, 7 other prominent vendors, and 6\nother vendors\nGet your sample today!\n<Origin Href=\"Link\">https://www.arizton.com/market-reports/iv-iron-oral-iron-drugs-market</Origin> \n\nIV and Oral Iron Drugs Market – Segmentation\n* Expected launches of branded IV iron drugs in new markets and expanded\nindication approvals for existing commercially available brands are the key\nfactors influencing the IV iron drugs&apos; growth. The increased uptake of branded\nIV drugs in the US and European countries is also expected to contribute\nsignificantly during the forecast period. \n* The growing prevalence of chronic kidney disease (CKD) and nephrological\ndisorders across the world is increasing the application of iron drugs at a\nfaster rate, and the trend is likely to continue during the forecast period.\nAcross the globe, one in five men and one in four women have CKD among people\nabove 65 years of age. \n* Adults constitute over 81% share of the global IV and oral iron drugs\nmarket. Adults dominate the market due to the high incidence and prevalence of\nID and IDA in the elderly population across the globe. CKD is relatively\ncommon among adults, with a prevalence rate of up to 13%.\nIV and Oral Iron Drugs Market by Route of Administration\n* Oral \n* Intravenous (IV)\nIV and Oral Iron Drugs Market by Application\n* Nephrology \n* Obstetrics &amp; Gynecology (OBGYN) \n* Surgery \n* Gastroenterology \n* Oncology \n* Heart Failure (HF)\nIV and Oral Iron Drugs Market by Patient Group\n* Adult \n* Pediatric\nIV and Oral Iron Drugs Market by Distribution\n* Hospital Pharmacies \n* Offline Retail Pharmacies \n* Online Channels\nIV and Oral Iron Drugs Market – Dynamics\n\nVendors, especially key players, are increasingly focusing on pursuing\ninorganic growth strategies such as strategic collaborations &amp; licensing\nopportunities to expand their presence, enhance product portfolio, and improve\nexpertise in the market. Strategic collaboration agreements provide vendors\nwith an opportunity to expand the market reach as well as increase the sales\nvolume of drugs across the world. These strategic partnerships and\nin-licensing/out-licensing opportunities will allow players to gain a\ncompetitive advantage and vast geographical reach, which will drive their\ngrowth and profitability. These strategies will also allow vendors to reduce\ntheir R&amp;D expenses and offer scope for easy market access of products into\nwider geographies by leveraging the portfolio of the acquired companies.\nLeading vendors in the global IV &amp; oral iron drugs market are highly focused\non strategic collaborations with counterparts in various countries to expand\ntheir global footprint.\n\nKey Drivers and Trends fueling Market Growth:\n* Investigational Iron Drugs \n* Increasing Availability of Branded Iron Therapeutics and Expanded Indication\nApprovals \n* High Demand for Dextran-free IV Iron Therapeutics \n* Growing Demand Iron Replacement Therapies Among Kidney Disease Patients\nIV and Oral Iron Drugs Market – Geography\n\nNorth America obtains the largest position for the IV &amp; oral iron drugs market\nacross the world. The presence of a large proportion of the population with\nID, coupled with better treatment access to ID and IDA, especially branded\ndrugs, is the main factor for the high market share of the region. The strong\npresence of key vendors is another reason for high product uptake in the\nregion. As a result, the IV &amp; oral iron drugs market in North America is\nexpected to witness significant growth during the forecast period. The region\nis likely to witness an incremental growth of around USD 2 billion during the\nforecast period, which is the highest compared to other regions. It is also\nexpected to witness the highest absolute growth of over 105% during the\nforecast period.\n\nGet your sample today!\n<Origin Href=\"Link\">https://www.arizton.com/market-reports/iv-iron-oral-iron-drugs-market</Origin> \n\nIV and Oral Iron Drugs Market by Geography\n* North America \n* US \n* Canada\n* Europe \n* UK \n* Germany \n* France \n* Italy \n* Spain\n* APAC \n* China \n* India \n* Japan \n* South Korea \n* Australia\n* Latin America \n* Mexico \n* Brazil \n* Argentina\n* Middle East &amp; Africa \n* Saudi Arabia \n* Turkey \n* South Africa\nMajor Vendors\n* Vifor Pharma \n* Daiichi Sankyo Company\nOther Prominent Vendors\n* AMAG Pharmaceuticals \n* Akebia Therapeutics \n* Shield Therapeutics \n* PHARMACOSMOS \n* Allergan \n* Sanofi \n* AOP Orphan Pharmaceuticals\nOther Vendors\n* AZAD Pharma \n* Ciron Drugs &amp; Pharmaceuticals \n* Pfizer \n* Rockwell Medical \n* Salveo Lifecare \n* Sunny Pharmaceutical\nExplore our healthcare &amp; lifesciences\n(<Origin Href=\"Link\">https://www.arizton.com/market-reports/category/healthcare-lifesciences</Origin>) profile\nto know more about the industry.\n\nRead some of the top-selling reports:\n* Antimicrobial Wound Dressings Market - Global Outlook and Forecast 2021-2026\n(<Origin Href=\"Link\">https://www.arizton.com/market-reports/antimicrobial-wound-dressings-market-size-analysis</Origin>)\n\n* Hearing Aids Market - Global Outlook and Forecast 2021-2026\n(<Origin Href=\"Link\">https://www.arizton.com/market-reports/hearing-aids-market-analysis-2024</Origin>) \n* Digital PCR (dPCR) and Real-Time PCR (qPCR) Market - Global Outlook and\nForecast 2020-2025\n(<Origin Href=\"Link\">https://www.arizton.com/market-reports/digital-pcr-real-time-pcr-market-size</Origin>)\n\n* Benign Prostatic Hyperplasia (BPH) Devices Market - Global Outlook and\nForecast 2020-2025\n(<Origin Href=\"Link\">https://www.arizton.com/market-reports/benign-prostatic-hyperplasia-devices-market</Origin>)\nAbout Arizton:\n\nArizton Advisory and Intelligence is an innovation and quality-driven firm,\nwhich offers cutting-edge research solutions to clients across the world. We\nexcel in providing comprehensive market intelligence reports and advisory and\nconsulting services.\n\nWe offer comprehensive market research reports on industries such as consumer\ngoods &amp; retail technology, automotive and mobility, smart tech, healthcare,\nand life sciences, industrial machinery, chemicals and materials, IT and\nmedia, logistics and packaging. These reports contain detailed industry\nanalysis, market size, share, growth drivers, and trend forecasts.\n\nArizton comprises a team of exuberant and well-experienced analysts who have\nmastered in generating incisive reports. Our specialist analysts possess\nexemplary skills in market research. We train our team in advanced research\npractices, techniques, and ethics to outperform in fabricating impregnable\nresearch reports.\n\nMail: enquiry@arizton.com   \nCall: +1-312-235-2040\n        +1-302-469-0707\n\nLogo: <Origin Href=\"Link\">https://mma.prnewswire.com/media/818553/Arizton_Logo.jpg</Origin>\n\n\n\nPhoto: \n<Origin Href=\"Link\">https://mma.prnewswire.com/media/818553/Arizton_Logo.jpg</Origin>\n\nCopyright (c) 2021 PR Newswire Association,LLC. All Rights Reserved.</p>");
			hm3.put("ID", "urn:newsml:reuters.com:20210202:nPre3JLhRa");
			hm3.put("RT", "2021-02-02T15:00:33-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT",
					"IV and Oral Iron Drugs Market Size to Reach Revenues of around USD 10 Billion by 2026 - Arizton");
			hm3.put("CT", "2021-02-02T15:00:33-00:00");
			hm3.put("LT", "2021-02-02T15:00:33-00:00");

			hm4.put("TE",
					"<p>For best results when printing this announcement, please click on link below:\n<Origin Href=\"Link\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210121:nBwTfymma&amp;default-theme=true</Origin>\n\n\nOutlook on the Electronic Access Control Global Market to 2028 - Impact\nAnalysis of Drivers and Restraints - ResearchAndMarkets.com\n\nThe &quot;Electronic Access Control Market Size, Market Share, Application\nAnalysis, Regional Outlook, Growth Trends, Key Players, Competitive Strategies\nand Forecasts, 2020 To 2028&quot;\n(<Origin Href=\"Link\">https://www.researchandmarkets.com/reports/5237690/electronic-access-control-market-size-market?utm_source=BW&amp;utm_medium=PressRelease&amp;utm_code=5kz29w&amp;utm_campaign=1491024+-+Outlook+on+the+Electronic+Access+Control+Global+Market+to+2028+-+Impact+Analysis+of+Drivers+and+Restraints&amp;utm_exec=jamu273prd</Origin>)\nreport has been added to ResearchAndMarkets.com&apos;s offering.\n\nThis report offers strategic insights into the global electronic access\ncontrol market along with the market size and estimates for the duration 2018\nto 2028. The said research study covers in-depth analysis of multiple market\nsegments based on component, mode of access, end-use and cross-sectional study\nacross different geographies and sub-geographies. The study covers the\ncomparative analysis of different segments for the years 2019 &amp; 2028. The\nreport also provides a prolific view on market dynamics such as market\ndrivers, restraints and opportunities. In addition, the report covers a\nsection providing analysis on the latest innovative solutions based on sensor\ntechnologies and smartphones applications.\n\nElectronic access control refers to the governance and monitoring of visitors\ninto and within the premises. These systems make use of electronic components\nas opposed to the traditional mechanical locks in order to offer enhanced\nconvenience and personnel security. Electronic access control system comprises\nnumerous components including electronic locks, access devices and cards,\nreaders, control panels, access control management software and access control\nservers. Authorized personnel can make use of their access devices or cards at\nthe readers to gain access into a physical location.\n\nThe readers validate the card and send this information across to the control\npanel for the decision making process. Based on the rules set through the\naccess control management software, the control panel responds with the\nappropriate action as to allow the user and deny entrance. Every successful\nand unsuccessful access transaction is stored in the access control server for\nrecord maintenance and further analysis for optimization. Depending on the\nrequirement and expanse of the physical site, end-users can deploy large\naccess control systems with multiple readers and access devices thereby,\nmonitoring and controlling access to various physical locations within the\npremises.\n\nAccess control systems have become pivotal for various institutions and\norganization all across the world due to rising threats of physical attacks\nand data thefts. Organizations from various end-use industries including\nresidential, banking and finance, commercial, government, healthcare,\neducation, manufacturing and industrial, retail, utilities and energy, among\nothers seek to deploy enhanced access control solutions in order to ensure\neffective physical and data security.\n\nOne of the major factors driving the growth of the electronic access control\nmarket is the increasing need to secure various locations such as data centers\nand server rooms within organizations. It has become imperative for\norganizations to protect such sites against malicious attacks to ensure data\nsecurity and business continuity. In addition, increasing complexity within\norganizations with respect to the workforce clearance and authorization has\nled to growing popularity of electronic access control systems across the\nworld. Other factors such as ease of access control management and rise of\naccess control as a service (ACaaS) have led to the higher adoption of access\ncontrol solutions in the recent years. However, lack of technological\nawareness and costs associated with the systems have hampered the growth of\nthe electronic access control market in some of the emerging markets across\nthe world.\n\nCompanies Mentioned\n\n\n * Assa Abloy Group\n\n * Dormakaba Group\n\n * ADT LLC dba ADT Security Services\n\n * Allegion Plc.\n\n * Bosch Security Systems\n\n * 3M Cogent, Inc.\n\n * AMAG Technology, Inc.\n\n * Honeywell International, Inc.\n\n * United Technologies Corporation\n\n * Gunnebo Group\n\n * Safran Group\n\n * Stanley Security Solutions, Inc.\n\n * Tyco Integrated Security\n\nOther in-depth analysis provided in the report includes:\n\n\n * Current and future market trends to justify the forthcoming attractive markets\nwithin the access control industry\n\n * Market fuelers, market impediments, and their impact on the market growth\n\n * In-depth competitive environment analysis\n\n * Trailing 2-Year market size data (2018 - 2019)\n\n * SRC (Segment-Region-Country) Analysis\n\nKey Topics Covered:\n\nChapter 1 Preface\n\nChapter 2 Executive Summary\n\nChapter 3 Market Dynamics\n\n3.1 Introduction\n\n3.2 Market Dynamics\n\n3.2.1 Market Drivers\n\n3.2.2 Market Growth Inhibitors\n\n3.2.3 Impact Analysis of Market Drivers and Restraints\n\n3.3 Attractive Investment Proposition\n\nChapter 4 Market Inclination Insights: Electronic Access Control Market\n\n4.1 Overview\n\n4.1.1 Evolution of Electronic Access Control\n\n4.1.2 Key Trends Analysis\n\nChapter 5 Global Electronic Access Control Market Analysis, By Component\n\n5.1 Market Analysis\n\n5.2 Hardware\n\n5.3 Access Control Management Software\n\n5.4 Services\n\nChapter 6 Global Electronic Access Control Market Analysis, By Mode of Access\n\n6.1 Market Analysis\n\n6.2 Smart Cards\n\n6.3 Digital Passwords/Keys\n\n6.4 Biometric\n\n6.5 Smartphone Applications\n\n6.6 Sensors\n\nChapter 7 Global Electronic Access Control Market Analysis, By End-use\n\n7.1 Market Analysis\n\n7.2 Residential\n\n7.3 Commercial\n\n7.4 Industrial\n\n7.5 Government and Defense\n\nChapter 8 North America Electronic Access Control Market Analysis\n\nChapter 9 Europe Electronic Access Control Market Analysis\n\nChapter 10 Asia Pacific Electronic Access Control Market Analysis\n\nChapter 11 Middle East &amp; Africa (MEA) Electronic Access Control Market\nAnalysis\n\nChapter 12 Latin America Electronic Access Control Market Analysis\n\nChapter 13 Competitive Landscape\n\nChapter 14 Company Profiles\n\nFor more information about this report visit\n<Origin Href=\"Link\">https://www.researchandmarkets.com/r/xevgfb</Origin>\n(<Origin Href=\"Link\">https://www.researchandmarkets.com/reports/5237690/electronic-access-control-market-size-market?utm_source=BW&amp;utm_medium=PressRelease&amp;utm_code=5kz29w&amp;utm_campaign=1491024+-+Outlook+on+the+Electronic+Access+Control+Global+Market+to+2028+-+Impact+Analysis+of+Drivers+and+Restraints&amp;utm_exec=jamu273prd</Origin>)\n\n\n\nView source version on businesswire.com:\n<Origin Href=\"Link\">https://www.businesswire.com/news/home/20210121005462/en/</Origin>\n(<Origin Href=\"Link\">https://www.businesswire.com/news/home/20210121005462/en/</Origin>)\n\nResearchAndMarkets.com\n\nLaura Wood, Senior Press Manager\n\npress@researchandmarkets.com \n(mailto:press@researchandmarkets.com) \nFor E.S.T Office Hours Call 1-917-300-0470\n\nFor U.S./CAN Toll Free Call 1-800-526-8630\n\nFor GMT Office Hours Call +353-1-416-8900\n\n\nCopyright Business Wire 2021</p>");
			hm4.put("ID", "urn:newsml:reuters.com:20210121:nBwTfymma");
			hm4.put("RT", "2021-01-21T11:43:00-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT",
					"Outlook on the Electronic Access Control Global Market to 2028 - Impact Analysis of Drivers and Restraints - ResearchAndMarkets.com");
			hm4.put("CT", "2021-01-21T11:43:00-00:00");
			hm4.put("LT", "2021-01-21T11:43:00-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AMAL.O") || isinCode.trim().equalsIgnoreCase("AMAL.OQ")) {

			hm1.put("TE",
					"<p>    Jan 28 (Reuters) - Amalgamated Bank  <Origin Href=\"QuoteRef\">AMAL.O</Origin> :\n    * AMALGAMATED BANK REPORTS FOURTH QUARTER AND FULL YEAR 2020\nFINANCIAL RESULTS\n    * Q4 NON-GAAP CORE EARNINGS PER SHARE $0.44\n    * Q4 EARNINGS PER SHARE $0.44\n    * Q4 EARNINGS PER SHARE ESTIMATE $0.33 -- REFINITIV IBES\nDATA\n    * NET INTEREST INCOME WAS $45.7 MILLION FOR FOURTH QUARTER\nOF\n2020, COMPARED TO $45.2 MILLION FOR THIRD QUARTER OF 2020\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNX116z2W</Origin> \nFurther company coverage:  <Origin Href=\"QuoteRef\">AMAL.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210128:nASA01NES");
			hm1.put("RT", "2021-01-28T11:28:04-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-Amalgamated Bank Reports Fourth Quarter And Full Year 2020 Financial Results");
			hm1.put("CT", "2021-01-28T11:28:04-00:00");
			hm1.put("LT", "2021-01-28T11:28:04-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AMRN.O")) {

			hm1.put("TE",
					"<p>    Feb 9 (Reuters) - Amarin Corporation PLC  <Origin Href=\"QuoteRef\">AMRN.O</Origin> :\n    * AMARIN PROVIDES UPDATE ON VASCEPA® (ICOSAPENT ETHYL)\nREGULATORY\nREVIEW PROCESSES IN MAINLAND CHINA AND HONG KONG\n    * AMARIN CORPORATION - CORPORATE PARTNER IN CHINA PROGRESSED\nVASCEPA INTO COMMENCEMENT OF REGULATORY REVIEW PROCESSES IN\nMAINLAND CHINA, HONG KONG\n    * AMARIN CORPORATION PLC - CHINESE NATIONAL MEDICAL PRODUCTS\nADMINISTRATION ACCEPTED FOR REVIEW NDA FOR VASCEPA\n    * AMARIN CORPORATION PLC - EDDING CURRENTLY ANTICIPATES\nRECEIVING\nA DECISION IN MAINLAND CHINA NEAR END OF 2021\n    * AMARIN CORPORATION PLC - HONG KONG DEPARTMENT OF HEALTH\nEVALUATING VASCEPA BASED ON CURRENT APPROVALS IN U.S., CANADA\n    * AMARIN CORPORATION PLC - REVIEW PROCESS IN HONG KONG IS\nANTICIPATED TO CONCLUDE NEAR END OF 2021\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNX3c5gQc</Origin> \nFurther company coverage:  <Origin Href=\"NewsSearch\">AMRN.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210209:nFWN2KF0SR");
			hm1.put("RT", "2021-02-09T12:25:38-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"BRIEF-Amarin Corporation Says Hong Kong Department Of Health Evaluating Vascepa Based On Current Approvals In U.S.");
			hm1.put("CT", "2021-02-09T12:25:38-00:00");
			hm1.put("LT", "2021-02-09T12:25:38-00:00");

			hm2.put("TE",
					"<p>    Jan 29 (Reuters) - Amarin Corporation PLC  <Origin Href=\"QuoteRef\">AMRN.O</Origin> :\n    * AMARIN RECEIVES POSITIVE CHMP OPINION FOR ICOSAPENT ETHYL\nFOR\nCARDIOVASCULAR RISK REDUCTION\n    * AMARIN CORPORATION PLC -  EUROPEAN COMMISSION DECISION ON\nMARKETING AUTHORISATION APPLICATION FOR ICOSAPENT ETHYL EXPECTED\nIN APRIL 2021\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNXbYT1Lm</Origin> \nFurther company coverage:  <Origin Href=\"NewsSearch\">AMRN.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20210129:nFWN2K41AM");
			hm2.put("RT", "2021-01-29T19:40:10-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT",
					"BRIEF-Amarin Receives Positive CHMP Opinion For Icosapent Ethyl For Cardiovascular Risk Reduction");
			hm2.put("CT", "2021-01-29T19:40:10-00:00");
			hm2.put("LT", "2021-01-29T19:40:10-00:00");

			hm3.put("TE",
					"<p>    Jan 25 (Reuters) - Amarin Corporation Plc  <Origin Href=\"QuoteRef\">AMRN.O</Origin> :\n    * EXPANDS CARDIOVASCULAR RISK REDUCTION PATENT INFRINGEMENT\nLAWSUIT TO INCLUDE HEALTH CARE INSURANCE PROVIDER\n    * AMARIN ANNOUNCES EXPANSION OF SCOPE OF LAWSUIT AGAINST\nHIKMA\nPHARMACEUTICALS TO INCLUDE A HEALTH CARE INSURANCE PROVIDER IN\nUNITED STATES, HEALTH NET\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNXbQ6Z0t</Origin> \nFurther company coverage:  <Origin Href=\"QuoteRef\">AMRN.O</Origin>   <Origin Href=\"QuoteRef\">HIK.L</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm3.put("ID", "urn:newsml:reuters.com:20210126:nFWN2K00UA");
			hm3.put("RT", "2021-01-26T00:39:02-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT",
					"BRIEF-Amarin Expands Cardiovascular Risk Reduction Patent Infringement Lawsuit To Include Health Care Insurance Provider");
			hm3.put("CT", "2021-01-26T00:39:02-00:00");
			hm3.put("LT", "2021-01-26T00:39:02-00:00");

			hm4.put("TE",
					"<p>    Jan 7 (Reuters) - Amarin Corporation PLC  <Origin Href=\"QuoteRef\">AMRN.O</Origin> :\n    * PROVIDES PRELIMINARY 2020 RESULTS AND 2021 OUTLOOK\n    * SEES FY 2020 REVENUE UP ABOUT 42 PERCENT\n    * SEES FY 2020 REVENUE ABOUT $610 MILLION\n    * CORPORATION - INTENDS TO REDUCE SPENDING LEVELS FOR\nCERTAIN\nFORMS OF PROMOTION (E.G., TELEVISION ADVERTISEMENTS) IN EARLY\n2021\n    * CORPORATION - WILL CONTINUE TO WITHHOLD 2021 REVENUE\nGUIDANCE\nFOR VASCEPA IN U.S.\n    * CORPORATION PLC - ANTICIPATES 2021 OPERATING EXPENSES OF\nABOUT\n$550 MILLION TO $600 MILLION\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNX2G9hjz</Origin> \nFurther company coverage:  <Origin Href=\"QuoteRef\">AMRN.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm4.put("ID", "urn:newsml:reuters.com:20210107:nASA01KIM");
			hm4.put("RT", "2021-01-07T12:04:57-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "BRIEF-Amarin Provides Preliminary 2020 Results And 2021 Outlook");
			hm4.put("CT", "2021-01-07T12:04:57-00:00");
			hm4.put("LT", "2021-01-07T12:04:57-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AMBA.O")) {

			hm1.put("TE",
					"<p>    Dec 9 (Reuters) - Ambarella Inc  <Origin Href=\"QuoteRef\">AMBA.O</Origin> :\n    * EMOTION3D AND AMBARELLA COLLABORATE ON AI-BASED EDGE\nCAMERA\nSYSTEMS FOR DRIVER AND OCCUPANT MONITORING\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nBw6w8XGfa</Origin> \nFurther company coverage:  <Origin Href=\"NewsSearch\">AMBA.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20201209:nFWN2IP12Y");
			hm1.put("RT", "2020-12-09T14:08:36-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-emotion3D and Ambarella Collaborate on AI-Based Edge Camera Systems ");
			hm1.put("CT", "2020-12-09T14:08:36-00:00");
			hm1.put("LT", "2020-12-09T14:08:36-00:00");

			hm2.put("TE",
					"<p>    Nov 23 (Reuters) - Ambarella Inc  <Origin Href=\"QuoteRef\">AMBA.O</Origin> :\n    * AMBARELLA, INC. ANNOUNCES THIRD QUARTER FISCAL YEAR 2021\nFINANCIAL RESULTS\n    * Q3 LOSS PER SHARE $0.49\n    * Q3 REVENUE $56.1 MILLION VERSUS REFINITIV IBES ESTIMATE OF\n$55\nMILLION\n    * Q3 NON-GAAP EARNINGS PER SHARE $0.09\n    * Q3 EARNINGS PER SHARE ESTIMATE $0.05 -- REFINITIV IBES\nDATA\n    * Q4 REVENUE IS EXPECTED TO BE BETWEEN $56.0 MILLION AND\n$60.0\nMILLION\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNX2Ylk60</Origin> \nFurther company coverage:  <Origin Href=\"QuoteRef\">AMBA.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20201123:nASA01FRB");
			hm2.put("RT", "2020-11-23T21:15:00-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "BRIEF-Ambarella Reports Q3 Loss Per Share $0.49");
			hm2.put("CT", "2020-11-23T21:15:00-00:00");
			hm2.put("LT", "2020-11-23T21:15:00-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);

			JSONArray response = new JSONArray(al);
			return response;

		} else if (isinCode.trim().equalsIgnoreCase("AMCX.O")) {

			hm1.put("TE",
					"<p>    Feb 8 (Reuters) - AMC Networks Inc  <Origin Href=\"QuoteRef\">AMCX.O</Origin> :\n    * AMC NETWORKS INC - ON FEBRUARY 8, 2021, AMC NETWORKS INC.\nENTERED INTO AMENDMENT NO. 1 TO SECOND AMENDED AND RESTATED\nCREDIT AGREEMENT\n    * AMC NETWORKS - AMENDMENT EXTENDS MATURITY DATES OF\nBORROWERS&apos;\n$675 MILLION TERM LOAN A FACILITY, $500 MILLION REVOLVING CREDIT\nFACILITY TO FEB 8, 2026\n\nSource text for Eikon: [ID: <Origin Href=\"Link\">https://bit.ly/3jHrTLX]</Origin>\nFurther company coverage:  <Origin Href=\"NewsSearch\">AMCX.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210208:nFWN2KE1MU");
			hm1.put("RT", "2021-02-08T23:00:21-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"BRIEF-AMC Networks Inc - On February 8, 2021, AMC Networks Inc. Entered Into Amendment No. 1 To Second Amended And Restated Credit Agreement");
			hm1.put("CT", "2021-02-08T23:00:21-00:00");
			hm1.put("LT", "2021-02-08T23:00:21-00:00");

			hm2.put("TE",
					"<p>    Feb 2 (Reuters) - AMC Networks Inc  <Origin Href=\"QuoteRef\">AMCX.O</Origin> :\n    * AMC NETWORKS AND SHAFTESBURY ENTER INTO NEW STRATEGIC\nPARTNERSHIP, FURTHER EXPANDING THE GLOBAL AUDIENCE FOR\nSHAFTESBURY&apos;S CONTENT\n    * AMC NETWORKS - THROUGH ITS INVESTMENT IN SHAFTESBURY, CO\nWILL\nGAIN ACCESS TO SHAFTESBURY&apos;S SLATE\n\nSource text for Eikon: [ID: nPrePrvB1a]\nFurther company coverage:  <Origin Href=\"NewsSearch\">AMCX.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20210202:nFWN2K80S3");
			hm2.put("RT", "2021-02-02T12:35:49-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT",
					"BRIEF-AMC Networks And Shaftesbury Enter Into New Strategic Partnership, Further Expanding The Global Audience For Shaftesbury&apos;s Content");
			hm2.put("CT", "2021-02-02T12:35:49-00:00");
			hm2.put("LT", "2021-02-02T12:35:49-00:00");

			hm3.put("TE",
					"<p>    Jan 26 (Reuters) - AMC Networks Inc  <Origin Href=\"QuoteRef\">AMCX.O</Origin> :\n    * AMC NETWORKS INC. ANNOUNCES FULL REDEMPTION OF 4.75%\nSENIOR\nNOTES DUE 2022 AND $600 MILLION PARTIAL REDEMPTION OF 5.00%\nSENIOR NOTES DUE 2024\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNX5QCLxj</Origin> \nFurther company coverage:  <Origin Href=\"NewsSearch\">AMCX.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm3.put("ID", "urn:newsml:reuters.com:20210126:nASA01N3H");
			hm3.put("RT", "2021-01-26T22:38:22-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT",
					"BRIEF-Amc Networks Inc. Announces Full Redemption Of 4.75% Senior Notes Due 2022 And $600 Million Partial Redemption Of 5.00% Senior Notes Due 2024");
			hm3.put("CT", "2021-01-26T22:38:22-00:00");
			hm3.put("LT", "2021-01-26T22:38:22-00:00");

			hm4.put("TE",
					"<p>    Jan 25 (Reuters) - AMC Networks Inc  <Origin Href=\"QuoteRef\">AMCX.O</Origin> :\n    * AMC NETWORKS INC. ANNOUNCES PROPOSED OFFERING OF $500\nMILLION OF\nSENIOR NOTES\n    * AMC NETWORKS INC - PROPOSED PUBLIC OFFERING OF $500\nMILLION IN\nOF SENIOR NOTES DUE 2029\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNXf1TC9</Origin> \nFurther company coverage:  <Origin Href=\"NewsSearch\">AMCX.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm4.put("ID", "urn:newsml:reuters.com:20210125:nASA01MNT");
			hm4.put("RT", "2021-01-25T12:43:28-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "BRIEF-AMC Networks Inc Announces Proposed Offering Of $500 Million Of Senior Notes");
			hm4.put("CT", "2021-01-25T12:43:28-00:00");
			hm4.put("LT", "2021-01-25T12:43:28-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("MSFT.O")) {

			hm1.put("TE",
					"<p> (Adds European Commission confirmation)\n    By Foo Yun Chee\n    BRUSSELS, Feb 17 (Reuters) - Fortnite creator Epic Games has\ntaken its fight against Apple  <Origin Href=\"QuoteRef\">AAPL.O</Origin>  to European Union\nantitrust regulators, ramping up it dispute with the iPhone\nmaker over its App Store payment system and control over app\ndownloads.\n    The two companies have been locked in a legal dispute since\nlast August, when the game maker tried to avoid Apple&apos;s 30% fee\non some in-app purchases on the App Store by launching its own\nin-app payment system.\n    That prompted Apple to kick Epic&apos;s Fortnite game off the App\nStore and threaten to terminate an affiliated account that would\nhave effectively blocked distribution of Unreal Engine, a\nsoftware tool used by hundreds of app makers to create games.\n    Epic Games founder and Chief Executive Tim Sweeney said\nApple&apos;s control of its platform had tilted the level playing\nfield.\n    &quot;The 30% they charge as their app tax, they can make it 50%\nor 90% or 100%. Under their theory of how these markets are\nstructured, they have every right to do that,&quot; he told\nreporters.\n    &quot;Epic is not asking any court or regulator to change this\n30% to some other number, only to restore competition on IOS,&quot;\nhe said, referring to Apple&apos;s mobile operating system.\n    The company also accused Apple of barring rivals from\nlaunching their own gaming subscription service on its platform\nby preventing them from bundling several games together, even\nthough its own Apple Arcade service does that.\n    Apple said its rules applied equally to all developers and\nthat Epic had violated them.\n    &quot;In ways a judge has described as deceptive and clandestine,\nEpic enabled a feature in its app, which was not reviewed or\napproved by Apple, and they did so with the express intent of\nviolating the App Store guidelines that apply equally to every\ndeveloper and protect customers,&quot; the company said in a\nstatement.\n    &quot;Their reckless behaviour made pawns of customers, and we\nlook forward to making this clear to the European Commission,&quot;\nit said.\n    Apple has been taking small steps in recent months towards\nchanging its practices, including lower fees for some developers\nand giving them a way to challenge its rulings, both of which\nhave not satisfied the company&apos;s critics. \n    Fortnite is slated to come back to the iPhone at some point\nin the mobile Safari browser. Epic and Apple in recent weeks\nhave been trading documents and conducting depositions ahead of\na scheduled May trial in the Epic lawsuit filed last year.\n    The Commission, which is investigating Apple&apos;s mobile\npayment system Apple Pay and the App Store, confirmed receipt of\nthe complaint.\n    &quot;We will assess it based on our standard procedures,&quot; a\nCommission spokeswoman said.\n    Epic Games has also complained to the UK Competition Appeal\nTribunal and to the Australian watchdog. \n    Big companies such as Microsoft Corp  <Origin Href=\"QuoteRef\">MSFT.O</Origin> , Spotify\n <Origin Href=\"QuoteRef\">SPOT.N</Origin>  and Match Group Inc  <Origin Href=\"QuoteRef\">MTCH.O</Origin>  have also criticised\nApple&apos;s App Store fees and rules.\n\n (Reporting by Foo Yun Chee\nAdditional reporting by Stephen Nellis and Paresh Dave in San\nFrancisco\nEditing by Barbara Lewis, Edmund Blair and David Goodman\n)\n ((foo.yunchee@thomsonreuters.com; +32 2 287 6844; Reuters\nMessaging: foo.yunchee.thomsonreuters.com@reuters.net))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210217:nL1N2KN0MJ");
			hm1.put("RT", "2021-02-17T09:54:41-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 2-Epic Games takes Apple fight to EU antitrust regulators");
			hm1.put("CT", "2021-02-17T09:54:41-00:00");
			hm1.put("LT", "2021-02-17T09:54:41-00:00");

			hm2.put("TE",
					"<p> (Corrects paragraph 14 to show Epic Games is not seeking\ndamages in the UK and Australia)\n    By Foo Yun Chee\n    BRUSSELS, Feb 17 (Reuters) - Fortnite creator Epic Games has\ntaken its fight against Apple  <Origin Href=\"QuoteRef\">AAPL.O</Origin>  to European Union\nantitrust regulators after failing to make headway in a U.S.\ncourt in a dispute over the iPhone maker&apos;s payment system on its\nApp Store and control over apps downloads.\n    The two companies have been locked in a legal dispute since\nAugust when the game maker tried to get around Apple&apos;s 30% fee\non some in-app purchases on the App Store by launching its own\nin-app payment system.\n    That prompted Apple to kick Epic&apos;s Fortnite game off the App\nStore and threaten to terminate an affiliated account that would\nhave effectively blocked distribution of Unreal Engine, a\nsoftware tool used by hundreds of app makers to create games.\n    Epic Games founder and Chief Executive Tim Sweeney said\nApple&apos;s control of its platform had tilted the level playing\nfield.\n    &quot;The 30% they charge as their app tax, they can make it 50%\nor 90% or 100%. Under their theory of how these markets are\nstructured, they have every right to do that,&quot; he told\nreporters.\n    &quot;Epic is not asking any court or regulator to change this\n30% to some other number, only to restore competition on IOS,&quot;\nhe said, referring to Apple&apos;s mobile operating system.\n    The company also accused Apple of barring rivals from\nlaunching their own gaming subscription service on its platform\nby preventing them from bundling several games together, even\nthough its own service, called Apple Arcade, does that.\n    Apple said its rules applied equally to all developers and\nthat Epic had violated them.\n    &quot;In ways a judge has described as deceptive and clandestine,\nEpic enabled a feature in its app, which was not reviewed or\napproved by Apple, and they did so with the express intent of\nviolating the App Store guidelines that apply equally to every\ndeveloper and protect customers,&quot; the company said in a\nstatement.\n    &quot;Their reckless behaviour made pawns of customers, and we\nlook forward to making this clear to the European Commission,&quot;\nit said.\n    Apple has been taking small steps in recent months towards\nchanging its practices, including lower fees for some developers\nand giving them a way to challenge its rulings, both of which\nhave not satisfied the company&apos;s critics. \n    Fortnite is slated to come back to the iPhone at some point\nin the mobile Safari browser. Epic and Apple in recent weeks\nhave been trading documents and conducting depositions ahead of\na scheduled May trial in Epic&apos;s lawsuit filed last year.\n    The Commission, which is investigating Apple&apos;s mobile\npayment system Apple Pay and the App Store, declined to comment\non the complaint, saying it was aware of the concerns regarding\nApple&apos;s App Store rules.\n    Epic Games has also complained to the UK Competition Appeal\nTribunal and to the Australian watchdog. It has not asked the EU\nenforcers nor their peers in the UK and Australia for damages.\n    Big companies such as Microsoft Corp  <Origin Href=\"QuoteRef\">MSFT.O</Origin> , Spotify\n <Origin Href=\"QuoteRef\">SPOT.N</Origin>  and Match Group Inc  <Origin Href=\"QuoteRef\">MTCH.O</Origin>  have also criticised\nApple&apos;s App Store fees and rules.\n\n (Reporting by Foo Yun Chee, additional reporting by Stephen\nNellis and Paresh Dave in San Francisco; Editing by Barbara\nLewis and Edmund Blair)\n ((foo.yunchee@thomsonreuters.com; +32 2 287 6844; Reuters\nMessaging: foo.yunchee.thomsonreuters.com@reuters.net))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20210217:nL1N2KN0EF");
			hm2.put("RT", "2021-02-17T09:07:56-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "CORRECTED-UPDATE 1-Epic Games takes Apple fight to EU antitrust regulators");
			hm2.put("CT", "2021-02-17T07:41:57-00:00");
			hm2.put("LT", "2021-02-17T09:07:56-00:00");

			hm3.put("TE",
					"<p>    By Kate Abnett\n    BRUSSELS, Feb 17 (Reuters) - Most of the world&apos;s planned\nhydrogen projects and the biggest chunk of related investments\nthis decade are expected to be in Europe, an industry report\nsaid on Wednesday, as the continent races to scale up the\nlow-carbon fuel to meet climate goals.\n    The European Union has made hydrogen a key plank in its aim\nto eliminate its greenhouse gas emissions by 2050, with plans to\ninstall 40GW of electrolysers this decade - equipment to produce\nemissions-free hydrogen using water and renewable power.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2K7490</Origin>\n    The EU currently has less than 0.1GW of electrolysers, and\nis betting on a rapid scale-up to decarbonise steel, heavy\ntransport and chemicals, the latter of which already uses\nhydrogen produced from fossil fuels.\n    Of the 228 hydrogen projects announced globally, 55% of them\n- 126 projects - are in Europe, business group the Hydrogen\nCouncil said in its first tally of the global project pipeline,\ndone with consultancy McKinsey. \n    Most are due to launch this decade, focusing on renewable\nhydrogen or fossil fuel-based hydrogen using technology to\ncapture the resulting emissions.\n    If all planned projects were realised, the report said\nglobal investments would exceed $300 billion this decade -\naround 1.4% of total energy sector investments - with Europe\npulling in around 45% of the total. The majority of that funding\nhas not yet been secured.\n    Hydrogen Council executive director Daryl Wilson attributed\nEurope&apos;s lead to early investments in hydrogen supply chains and\nprojects like Germany&apos;s hydrogen-fuelled trains, plus climate\nchange policies.\n    &quot;That stable policy commitment environment allows the\nindustry to have the confidence to act,&quot; he said.\n    Hydrogen Council members - including Royal Dutch Shell Plc\n <Origin Href=\"QuoteRef\">RDSa.L</Origin> , BMW  <Origin Href=\"QuoteRef\">BMWG.DE</Origin> , Microsoft Corp  <Origin Href=\"QuoteRef\">MSFT.O</Origin>  and Sinopec\n <Origin Href=\"QuoteRef\">600028.SS</Origin>   - plan to increase hydrogen investments six-fold\nthrough 2025, from 2019 levels.\n    The report pointed to the huge challenges ahead to scale up\nhydrogen production, build transportation and storage\ninfrastructure, and massively expand renewable power capacity to\nproduce the fuel.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2CN1HW</Origin>  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2FS4VE</Origin>\n    If that happened, renewable hydrogen could hit cost parity\nwith fossil fuel-based versions by 2028 in regions with abundant\ncheap renewable power, like the Middle East, under Hydrogen\nCouncil members&apos; estimates.\n\n (Reporting by Kate Abnett in Brussels\nEditing by Matthew Lewis)\n ((Kate.Abnett@thomsonreuters.com;))</p>");
			hm3.put("ID", "urn:newsml:reuters.com:20210217:nL4N2KM3E9");
			hm3.put("RT", "2021-02-17T06:00:00-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Europe pulls ahead in race for hydrogen, as global project pipeline grows -report");
			hm3.put("CT", "2021-02-17T06:00:00-00:00");
			hm3.put("LT", "2021-02-17T06:00:00-00:00");

			hm4.put("TE",
					"<p>    Feb 16 (Reuters) - Salesforce.com Inc  <Origin Href=\"QuoteRef\">CRM.N</Origin>  and Slack\nTechnologies Inc  <Origin Href=\"QuoteRef\">WORK.N</Origin>  have received requests for additional\ninformation from the antitrust division of the U.S. Department\nof Justice related to their $27.7 billion merger, regulatory\nfilings showed on Tuesday.\n    The two companies were also asked to provide documentary\nmaterial, the filings <Origin Href=\"Link\">https://www.sec.gov/ix?doc=/Archives/edgar/data/1108524/000119312521045220/d125434d8k.htm</Origin>\n showed.\n    On Dec. 1, cloud computing company Salesforce.com agreed to\nbuy the workplace messaging app as it bets on an extended run\nfor remote working and sharpens its rivalry with Microsoft\n <Origin Href=\"QuoteRef\">MSFT.O</Origin> .  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2IH42V</Origin>\n    The deal is still expected to be completed during the fiscal\nquarter ending July 31.\n\n (Reporting by Sanjana Shivdas in Bengaluru; Editing by\nSubhranshu Sahu)\n ((SanjanaSitara.Shivdas@thomsonreuters.com; within U.S. +1 646\n223 8780, outside U.S. +91 80 6749 1642; Twitter:\n@SanjanaShivdas;))</p>");
			hm4.put("ID", "urn:newsml:reuters.com:20210217:nL4N2KN02E");
			hm4.put("RT", "2021-02-17T00:31:25-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Salesforce, Slack asked to provide additional information by DOJ&apos;s antitrust unit");
			hm4.put("CT", "2021-02-17T00:31:25-00:00");
			hm4.put("LT", "2021-02-17T00:31:25-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("TSLA.O") || isinCode.trim().equalsIgnoreCase("TSLA.OQ")) {

			hm1.put("TE",
					"<p> (Adds details of Toshiba statement)\n    By Makiko Yamazaki\n    TOKYO, Feb 17 (Reuters) - Toshiba Corp  <Origin Href=\"QuoteRef\">6502.T</Origin>  said on\nWednesday an internal investigation found no evidence it was\ninvolved in any effort to pressure the Harvard University\nendowment fund over voting at the company&apos;s annual shareholders\nmeeting (AGM).\n    The Japanese conglomerate also said it had concluded there\nwas no need to launch a third-party committee, demanded by its\ntop shareholder Effissimo Capital Management, to investigate if\nthe firm&apos;s AGM in July was conducted fairly.\n    Reuters reported that Hiromichi Mizuno, a Japanese\ngovernment adviser at the time, had told the Harvard fund that\nits vote at Toshiba&apos;s AGM could be subject to a regulatory probe\nshould it vote against the firm&apos;s management.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2J4052</Origin>\n    When contacted by Toshiba&apos;s audit committee, Harvard did not\nprovide &quot;any specific information as to whether or not there has\nbeen any undue pressure that Toshiba had taken part in,&quot; the\nJapanese company said.\n    The audit committee also interviewed Toshiba&apos;s CEO and two\nother top executives and screened their 30,000 email messages, \nand found no direct communication between them and Mizuno, a\nspokeswoman said. Mizuno is currently a board member of U.S.\nelectric vehicle maker Tesla Inc  <Origin Href=\"QuoteRef\">TSLA.O</Origin> . \n    The spokeswoman said Toshiba could not comment on whether\nanyone else had pressured Harvard, because it was &quot;not in a\nposition to address issues that the company is not involved in.&quot;\n    Toshiba will hold an extraordinary meeting on March 18 to\nvote on separate proposals from two large shareholders -\nEffissimo, which wants an independent probe into the AGM, and\nU.S. hedge fund Farallon Capital Management, which wants a vote\non the company&apos;s investment strategy.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KI0AU</Origin>\n    On Wednesday, Toshiba said it opposed both proposals and\nadvised other shareholders to vote against them. \n    Regarding Farallon&apos;s proposal, Toshiba said its investment\npolicy was not subject to approval from shareholders, according\nto the Japanese companies act.\n\n (Reporting by Makiko Yamazaki. Editing by Alex Richardson and\nMark Potter)\n ((Makiko.Yamazaki@thomsonreuters.com; +81-3-4563-2805;))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210217:nL1N2KN0N2");
			hm1.put("RT", "2021-02-17T11:19:09-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "UPDATE 1-Toshiba says no evidence it pressured Harvard over AGM vote");
			hm1.put("CT", "2021-02-17T11:19:09-00:00");
			hm1.put("LT", "2021-02-17T11:19:09-00:00");

			hm2.put("TE",
					"<p>    Feb 16 (Reuters) - Tesla  <Origin Href=\"QuoteRef\">TSLA.O</Origin>  boss Elon Musk&apos;s SpaceX\ncompleted an equity funding round of $850 million that sent its\nvaluation to about $74 billion last week, CNBC reported on\nTuesday, citing people familiar with the financing.\n    SpaceX raised the funds at $419.99 a share, CNBC reported <Origin Href=\"Link\">https://www.cnbc.com/2021/02/16/elon-musks-spacex-raised-850-million-at-419point99-a-share.html.</Origin>\n    The company did not immediately respond to a Reuters request\nfor comment.\n\n (Reporting by Derek Francis in Bengaluru; Editing by Devika\nSyamnath)\n ((derek.francis@thomsonreuters.com; +91-9986311363 and\n@derekfrancis089 on Twitter;))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20210217:nL4N2KN0A6");
			hm2.put("RT", "2021-02-17T01:06:39-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "Elon Musk&apos;s SpaceX raises $850 mln in fresh funding - CNBC");
			hm2.put("CT", "2021-02-17T01:06:39-00:00");
			hm2.put("LT", "2021-02-17T01:06:39-00:00");

			hm3.put("TE",
					"<p>    By David Randall and Svea Herbst-Bayliss\n    NEW YORK, Feb 16 (Reuters) - A handful of small hedge funds\nwere in a position to profit from the Reddit rally that sent\nshares of GameStop Corp  <Origin Href=\"QuoteRef\">GME.N</Origin>  and other out-of-favor stocks\nrocketing higher last month at the expense of prominent\ninvestors who had bet against the stocks, according to\nsecurities filings released Tuesday. \n    Hedge funds including Maverick Capital, Shellback Capital,\nLandscape Capital Management, and Engineers Gate Manager LP were\namong those that added a new position or increased their stakes\nin GameStop during the quarter that ended Dec. 30, according to\na regulatory filings known as 13-Fs. Had each fund sold its\nshares of GameStop near the record closing price of $347.15,\nthey would have banked gains of 1,600% or more.  \n    Shellback, for instance, could have seen its 200,000 shares\nreach a value as high as $69.5 million had it held through Jan.\n27, a gain of nearly 1,750% from its market value of $3,768,000\nat the end of December. \n    Hedge fund Senvest, which told the Wall Street Journal that\nit scored a $700 million profit on the GameStop position,\nincreased its position in the company by 56% when it bought 1.8\nmln shares.\n    Maverick, increased its stake by 164%, or 2.9 million\nshares, leaving it with 4.7 million shares, regulatory filings\nshow. \n    The filings do not include short positions and funds may\nalso have been short, which would have diminished profits from\nlong positions. \n    GameStop shares surged as investors following the Reddit\nforum WallStreetBets bought the stock hoping to punish hedge\nfunds such as Melvin Capital Management that had taken short\npositions in the stock. Melvin lost more than 50% in January,\nrequiring a $2.75 billion capital infusions from hedge funds\nPoint72 Asset Management and Citadel. \n    Other hedge funds that entered January with bearish bets\nagainst GameStop included Maplelane Capital and Sculptor\nCapital, according to securities filings. \n    Maverick, Shellback Capital, Landscape Capital Management,\nand Engineers Gate Manager LP and Senvest did not respond for\nrequests to comment for this story. \n    \n    OTHER BETS\n    Along with positions in GameStop, hedge fund managers\nnavigated a quarter that included the addition of electric car\nmaker Tesla Inc  <Origin Href=\"QuoteRef\">TSLA.O</Origin>  to the benchmark S&amp;P 500 and President\nDonald Trump&apos;s unsuccessful attempts to overturn the result of\nthe Nov. 3 presidential election. \n    Tesla&apos;s inclusion in the S&amp;P 500 forced index-tracking funds\nto buy shares, boosting its shares during the fourth quarter.\nSome hedge fund such as Coatue Management significantly cut back\ntheir stakes during the same time period, leaving them less\nlikely to gain from the company&apos;s 13.4% gain for the year to\ndate. \n    George Soros’ Soros Fund Management exited its position in\nTwitter Inc  <Origin Href=\"QuoteRef\">TWTR.N</Origin>  while Kerrisdale Capital, whose founder\ntold Reuters this year the company is enjoying a turnaround, cut\nits stake by 29%.\n    Shares of the company are up nearly 37% for the year to\ndate. \n    Tiger Global, meanwhile, appeared to be betting big on\nseveral newly-public companies, raising its stakes in GoodRx\nHoldings Inc, DoorDash, Snowflake Inc, Airbnb, and Tencent Music\nEntertainment Group.\n\n (Reporting by David Randall and Svea Herbst-Bayliss; editing by\nMegan Davies and David Gregorio)\n ((David.Randall@thomsonreuters.com; 646-223-6607; Reuters\nMessaging: david.randall.thomsonreuters.com@reuters.net))</p>");
			hm3.put("ID", "urn:newsml:reuters.com:20210216:nL1N2KM1HP");
			hm3.put("RT", "2021-02-16T20:52:05-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Handful of hedge funds bet big on GameStop before its wild ride");
			hm3.put("CT", "2021-02-16T20:52:05-00:00");
			hm3.put("LT", "2021-02-16T20:52:05-00:00");

			hm4.put("TE",
					"<p>    * Bitcoin breaks above $50,000 for first time\n    * Latest landmark of asset up around 70% YTD\n    * MicroStrategy to borrow $600 million to buy bitcoin \n\n (Adds information on exchange traded funds)\n    By Thyagaraju Adinarayan and Tom Wilson\n    LONDON, Feb 16 (Reuters) - Bitcoin rose above $50,000 on\nTuesday for the first time, adding steam to a rally fuelled by\nsigns that the world&apos;s biggest cryptocurrency is gaining\nacceptance among mainstream investors and companies.\n    Bitcoin  <Origin Href=\"QuoteRef\">BTC=BTSP</Origin>  hit a record $50,603 and was last up\n0.83% at $48,351. It has risen around 67% so far this year, with\nmost of the gains coming after electric carmaker Tesla  <Origin Href=\"QuoteRef\">TSLA.O</Origin> \nsaid it had bought $1.5 billion in bitcoin.\n    The move by Tesla, which also said it would accept bitcoin\nas payment, was the latest in a string of large investments that\nhave vaulted bitcoin from the fringes of finance to company\nbalance sheets and Wall Street, with U.S. firms and traditional\nmoney managers starting to buy the coin.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KF0L8</Origin>\n    &quot;The rally in bitcoin in part reflects the recent buoyancy\nof market confidence but also headlines suggesting an increase\nin corporate acceptability,&quot; said Jane Foley, head of FX\nStrategy at Rabobank.\n    Evolve Funds Group said on Tuesday it had applied to launch\na Bitcoin exchange-traded fund on the Toronto Stock Exchange.\nThat would be the second planned bitcoin ETF after Canada&apos;s main\nsecurities regulator approved a fund by Purpose Investments\nInc. <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nFWN2KM0WL</Origin> <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2KI1U7</Origin>\n    Such mainstream moves could help bitcoin become a widespread\nmeans of payment - having so far failed to achieve large scale\nadoption - and in turn bolster prices.\n    &quot;The more people that adopt it and use it as money, then the\ngreater the chances of it perhaps being taken on board as a\nmainstream currency,&quot; said Russ Mould, investment director of AJ\nBell. &quot;That would feed further speculative interest.&quot;\n    The rush in 2021 by retail and institutional investors comes\non top of a 300% rise last year as investors searched for\nhigh-yielding assets and dollar alternatives amid rock-bottom or\neven negative interest rates globally. \n    The meteoric rise of bitcoin, which traded at a few hundred\ndollars only five years earlier, has also led major investment\nbanks to warn of a speculative bubble.\n    Bitcoin&apos;s rise &quot;blows the doors off prior bubbles,&quot; BofA\nsaid last month.\n    Despite the mainstream interest, cryptocurrencies remain\nsubject to patchy oversight globally, with the lack of\nregulatory clarity and associations with crime keeping many\nlarger investors leery of exposure.  \n    U.S. Treasury Secretary Janet Yellen and European Central\nBank President Christine Lagarde have both called for tighter\noversight of bitcoin.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2JW2RC</Origin> <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL1N2JO0RM</Origin> \n    Some believe extreme volatility is a cause for concern.\n    &quot;Due to its volatility, bitcoin lacks many of the\nestablished qualities that make up &apos;money&apos;, such as being a\nstable store of value and unit of account,&quot; said George\nLagarias, chief economist at Mazars.\n    \n    \n    DIGITAL GOLD?\n    Also boosting bitcoin are suggestions that its limited\nsupply of 21 million could drive further gains for the virtual\nasset. \n    A narrative of bitcoin becoming &quot;digital gold&quot; has gained\ntraction as investors predict looming inflation amid massive\ncentral bank and government stimulus to counter COVID-19.     \n    St. Louis U.S. Federal Reserve President James Bullard told\nCNBC on Tuesday that bitcoin&apos;s claim to be a gold rival would\nnot threaten greenback dominance. \n    &quot;Investors want a safe haven, they want a stable store value\nand then they want to conduct their investments in that\ncurrency,&quot; he said. &quot;It&apos;s very hard to get a private currency -\nit&apos;s really more like gold - to play that role.&quot;\n    JPMorgan said in January that bitcoin emerged as a rival to\ngold and could trade as high as $146,000 if it becomes an\nestablished safe-haven.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2JG2MM</Origin>\n    U.S. business intelligence software firm MicroStrategy Inc\n <Origin Href=\"QuoteRef\">MSTR.O</Origin> , whose CEO is a strong bitcoin proponent, on Tuesday\nsaid it would issue $600 million in convertible notes to buy\nadditional bitcoin.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nASA01QOH</Origin>\n    Meanwhile, smaller cryptocurrency ethereum  <Origin Href=\"QuoteRef\">ETH=BTSP</Origin>  fell\n2.42%, after earlier rising to $1,826, just shy of its record\nhigh price of $1,875.\n    With cryptocurrencies collectively worth about $1.5\ntrillion, some investors caution about the value of owning them.\n    &quot;As an intangible asset with no yield or practical use, save\nfor a few organisations who accept it as payment, it is really\njust demand (against a predictable supply) which determines its\nprice,&quot; said Mazars&apos; Lagarias.\n    &quot;But whereas the price of bitcoin has risen to the skies,\nwhat value one gets from holding it in a long-term portfolio\nstill remains subject of much debate.&quot;\n    \n\n    &lt;^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\nBitcoin crosses $50K    <Origin Href=\"Link\">https://tmsnrt.rs/2N3uDaA</Origin>\nFACTBOX-Bitcoin&apos;s famous backers     <Origin Href=\"NewsSearchID\">ID:nL1N2KM0JU</Origin> \nFACTBOX-Bitcoin&apos;s journey towards the mainstream   \n <Origin Href=\"NewsSearchID\">ID:nL4N2KL16Q</Origin> \nINSTANT VIEW-Bitcoin tops $50,000 for the first time   \n <Origin Href=\"NewsSearchID\">ID:nL1N2KM14S</Origin> \n    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^&gt;\n (Reporting by Thyagaraju Adinarayan and Tom Wilson in London;\nadditional reporting by Anna Irrera and Joice Alves in London,\nSusan Heavey in Washington and Karen Brettell in New York;\nediting by David Evans, Dan Grebler and Sam Holmes)\n ((thyagaraju.adinarayan@tr.com; +44 20 7542 7015; Reuters\nMessaging: thyagaraju.adinarayan.thomsonreuters.com@reuters.net;\nTwitter @thyagu))</p>");
			hm4.put("ID", "urn:newsml:reuters.com:20210216:nL8N2KM3TC");
			hm4.put("RT", "2021-02-17T00:18:22-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "UPDATE 6-Bitcoin tops $50,000 as it wins more mainstream acceptance");
			hm4.put("CT", "2021-02-16T12:38:27-00:00");
			hm4.put("LT", "2021-02-17T00:18:22-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AMRN.O")) {

			hm1.put("TE",
					"<p>    Feb 9 (Reuters) - Amarin Corporation PLC  <Origin Href=\"QuoteRef\">AMRN.O</Origin> :\n    * AMARIN PROVIDES UPDATE ON VASCEPA® (ICOSAPENT ETHYL)\nREGULATORY\nREVIEW PROCESSES IN MAINLAND CHINA AND HONG KONG\n    * AMARIN CORPORATION - CORPORATE PARTNER IN CHINA PROGRESSED\nVASCEPA INTO COMMENCEMENT OF REGULATORY REVIEW PROCESSES IN\nMAINLAND CHINA, HONG KONG\n    * AMARIN CORPORATION PLC - CHINESE NATIONAL MEDICAL PRODUCTS\nADMINISTRATION ACCEPTED FOR REVIEW NDA FOR VASCEPA\n    * AMARIN CORPORATION PLC - EDDING CURRENTLY ANTICIPATES\nRECEIVING\nA DECISION IN MAINLAND CHINA NEAR END OF 2021\n    * AMARIN CORPORATION PLC - HONG KONG DEPARTMENT OF HEALTH\nEVALUATING VASCEPA BASED ON CURRENT APPROVALS IN U.S., CANADA\n    * AMARIN CORPORATION PLC - REVIEW PROCESS IN HONG KONG IS\nANTICIPATED TO CONCLUDE NEAR END OF 2021\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNX3c5gQc</Origin> \nFurther company coverage:  <Origin Href=\"NewsSearch\">AMRN.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210209:nFWN2KF0SR");
			hm1.put("RT", "2021-02-09T12:25:38-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"BRIEF-Amarin Corporation Says Hong Kong Department Of Health Evaluating Vascepa Based On Current Approvals In U.S.");
			hm1.put("CT", "2021-02-09T12:25:38-00:00");
			hm1.put("LT", "2021-02-09T12:25:38-00:00");

			hm2.put("TE",
					"<p>    Jan 29 (Reuters) - Amarin Corporation PLC  <Origin Href=\"QuoteRef\">AMRN.O</Origin> :\n    * AMARIN RECEIVES POSITIVE CHMP OPINION FOR ICOSAPENT ETHYL\nFOR\nCARDIOVASCULAR RISK REDUCTION\n    * AMARIN CORPORATION PLC -  EUROPEAN COMMISSION DECISION ON\nMARKETING AUTHORISATION APPLICATION FOR ICOSAPENT ETHYL EXPECTED\nIN APRIL 2021\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNXbYT1Lm</Origin> \nFurther company coverage:  <Origin Href=\"NewsSearch\">AMRN.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20210129:nFWN2K41AM");
			hm2.put("RT", "2021-01-29T19:40:10-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT",
					"BRIEF-Amarin Receives Positive CHMP Opinion For Icosapent Ethyl For Cardiovascular Risk Reduction");
			hm2.put("CT", "2021-01-29T19:40:10-00:00");
			hm2.put("LT", "2021-01-29T19:40:10-00:00");

			hm3.put("TE",
					"<p>    Jan 25 (Reuters) - Amarin Corporation Plc  <Origin Href=\"QuoteRef\">AMRN.O</Origin> :\n    * EXPANDS CARDIOVASCULAR RISK REDUCTION PATENT INFRINGEMENT\nLAWSUIT TO INCLUDE HEALTH CARE INSURANCE PROVIDER\n    * AMARIN ANNOUNCES EXPANSION OF SCOPE OF LAWSUIT AGAINST\nHIKMA\nPHARMACEUTICALS TO INCLUDE A HEALTH CARE INSURANCE PROVIDER IN\nUNITED STATES, HEALTH NET\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNXbQ6Z0t</Origin> \nFurther company coverage:  <Origin Href=\"QuoteRef\">AMRN.O</Origin>   <Origin Href=\"QuoteRef\">HIK.L</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm3.put("ID", "urn:newsml:reuters.com:20210126:nFWN2K00UA");
			hm3.put("RT", "2021-01-26T00:39:02-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT",
					"BRIEF-Amarin Expands Cardiovascular Risk Reduction Patent Infringement Lawsuit To Include Health Care Insurance Provider");
			hm3.put("CT", "2021-01-26T00:39:02-00:00");
			hm3.put("LT", "2021-01-26T00:39:02-00:00");

			hm4.put("TE",
					"<p>    Jan 7 (Reuters) - Amarin Corporation PLC  <Origin Href=\"QuoteRef\">AMRN.O</Origin> :\n    * PROVIDES PRELIMINARY 2020 RESULTS AND 2021 OUTLOOK\n    * SEES FY 2020 REVENUE UP ABOUT 42 PERCENT\n    * SEES FY 2020 REVENUE ABOUT $610 MILLION\n    * CORPORATION - INTENDS TO REDUCE SPENDING LEVELS FOR\nCERTAIN\nFORMS OF PROMOTION (E.G., TELEVISION ADVERTISEMENTS) IN EARLY\n2021\n    * CORPORATION - WILL CONTINUE TO WITHHOLD 2021 REVENUE\nGUIDANCE\nFOR VASCEPA IN U.S.\n    * CORPORATION PLC - ANTICIPATES 2021 OPERATING EXPENSES OF\nABOUT\n$550 MILLION TO $600 MILLION\n\nSource text for Eikon:  <Origin Href=\"NewsSearchID\">ID:nGNX2G9hjz</Origin> \nFurther company coverage:  <Origin Href=\"QuoteRef\">AMRN.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))</p>");
			hm4.put("ID", "urn:newsml:reuters.com:20210107:nASA01KIM");
			hm4.put("RT", "2021-01-07T12:04:57-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "BRIEF-Amarin Provides Preliminary 2020 Results And 2021 Outlook");
			hm4.put("CT", "2021-01-07T12:04:57-00:00");
			hm4.put("LT", "2021-01-07T12:04:57-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("WMT.N")) {

			hm1.put("TE",
					"<p> (Adds Air Canada, Capita, Sixth Street and Lucid Motors)\n    Feb 16 (Reuters) - The following bids, mergers, acquisitions\nand disposals were reported by 2100 GMT on Tuesday:\n    \n    **  Air Canada won&apos;t extend a Feb. 15 deadline for its\nC$188.7 million ($148.73 million) takeover of Transat A.T.\n <Origin Href=\"QuoteRef\">TRZ.TO</Origin> , the Canadian tour operator said, putting the agreement\nin limbo after European regulators failed to give their\napproval.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM35J</Origin>\n    \n    **  British outsourcer Capita  <Origin Href=\"QuoteRef\">CPI.L</Origin>  said the company and\nthe UK Cabinet Office were exploring options for their joint\nventure Axelos, including a potential sale of the business.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM380</Origin>\n    \n    ** Investment firm Sixth Street Partners has sued Dyal\nCapital Partners as it looks to block the asset manager&apos;s\nproposed merger with direct lender Owl Rock Capital and their\ndeal to go public through an acquisition by a blank-check\ncompany.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM3EH</Origin>\n    \n    ** Luxury electric vehicle maker Lucid Motors Inc is getting\nclose to a deal to go public at a roughly $12 billion valuation\nafter veteran dealmaker Michael Klein&apos;s blank-check acquisition\nfirm launched a financing effort to back the transaction, people\nfamiliar with the matter said.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KM058</Origin>\n    \n    ** German sportswear maker Adidas AG  <Origin Href=\"QuoteRef\">ADSGn.DE</Origin>  said it\nplans to divest the underperforming brand Reebok that it failed\nto revive 15 years after buying the U.S. fitness label to help\ncompete with archrival Nike Inc  <Origin Href=\"QuoteRef\">NKE.N</Origin> .  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nFWN2KM0QO</Origin>\n    \n    ** The Spanish government has extended until December 2023\nits deadline to offload its stake in Bankia  <Origin Href=\"QuoteRef\">BKIA.MC</Origin> , the\nSpanish government spokeswoman Maria Jesus Montero said on\nTuesday. The government had currently until 2021 to sell the\n61.8% it holds in Bankia.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nLUN2J900G</Origin>\n    \n    ** Mexican cement producer Cemex  <Origin Href=\"QuoteRef\">CMXCPO.MX</Origin>  said it had\nacquired a Beck Readymix Concete Co, a cement business in San\nAntonio, Texas.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nE6N2FN00B</Origin>\n    \n    ** Essity  <Origin Href=\"QuoteRef\">ESSITYa.ST</Origin>  confirmed that discussions to buy\nadditional shares in hygiene and personal products maker Asaleo\nwere at an advanced stage.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KM3HT</Origin>\n    \n    ** Commercial property data vendor CoStar Group Inc  <Origin Href=\"QuoteRef\">CSGP.O</Origin> \nhas offered to buy real-estate data provider CoreLogic Inc\n <Origin Href=\"QuoteRef\">CLGX.N</Origin>  for about $6.9 billion in an all-stock deal, weeks\nafter the real-estate data provider agreed to be acquired by two\nprivate equity firms.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM2II</Origin>\n        \n    ** Britain&apos;s Issa brothers and private equity firm TDR\nCapital said they had completed the acquisition of supermarket\ngroup Asda from Walmart  <Origin Href=\"QuoteRef\">WMT.N</Origin>  for an enterprise value of 6.8\nbillion pounds ($9.5 billion).  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KM47L</Origin>\n        \n    ** Li-Cycle Corp said it will go public through a merger\nwith blank-check acquisition company Peridot Acquisition Corp\n <Origin Href=\"QuoteRef\">PDAC.N</Origin>  in a deal valuing the recycler of lithium-ion batteries\nat $1.67 billion.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KI6LE</Origin>\n    \n    ** Rexnord Corp  <Origin Href=\"QuoteRef\">RXN.N</Origin>  said it would merge its industrial\nprocess and motion control business with larger rival Regal\nBeloit Corp  <Origin Href=\"QuoteRef\">RBC.N</Origin>  in a deal valued at about $3.69 billion\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM2CP</Origin>\n\n    ** Owlet Baby Care Inc, a maker of baby monitoring devices,\nwill go public through a merger with a blank-check firm backed\nby private equity firm Sandbridge Capital, in a deal that values\nthe equity of the combined entity at about $1.4 billion.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM283</Origin>\n    \n    ** Japanese refiner Idemitsu Kosan  <Origin Href=\"QuoteRef\">5019.T</Origin>  said it has\nwithdrawn its offer for a potential acquisition of smaller\nrival, Toa Oil Co  <Origin Href=\"QuoteRef\">5008.T</Origin> , in which a U.S. investment fund has\nbeen building up a stake. Idemitsu already owns 50.12% of Toa.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KL08H</Origin>\n    \n    ** The British competition authority (CMA) has expressed\nconcerns about the planned acquisition by Norway&apos;s Adevinta\n <Origin Href=\"QuoteRef\">ADEV.OL</Origin>  of eBay&apos;s  <Origin Href=\"QuoteRef\">EBAY.O</Origin>  classified ads business, Adevinta\nsaid. The deal, worth $9.2 billion and announced in July, would\ncreate the world&apos;s largest classifieds group if it went ahead.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KM1CO</Origin>\n    \n    **  British outsourcer Serco  <Origin Href=\"QuoteRef\">SRP.L</Origin>  said it would buy\nconsulting services provider Whitney, Bradley &amp; Brown Inc from\nan affiliate of H.I.G. Capital for $295 million to strengthen\nits services to the defence sector.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM1I9</Origin>\n    \n    ** Irish food and ingredients company Kerry  <Origin Href=\"QuoteRef\">KYGa.I</Origin>  is\nundertaking a strategic review of its dairy business in Britain\nand Ireland, which may lead to a transaction in the coming\nmonths, its chief executive said.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nS8N2JH087</Origin>\n    \n    ** Abu Dhabi&apos;s state-owned ADQ has lined up a small group of\nbanks for a loan of about $1 billion to back its acquisition of\na 45% stake in commodities trader Louis Dreyfus Co (LDC), three\nsources familiar with the matter said.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KM0L5</Origin>\n    \n    ** Indian conglomerate Tata Group will buy a 68% stake in\nAlibaba-backed  <Origin Href=\"QuoteRef\">BABA.N</Origin>  online grocery startup BigBasket for\nabout 95 billion rupees ($1.31 billion), television channel ET\nNow said.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM20L</Origin>  \n\n (Compiled by Vishwadha Chander and Niket Nishant in Bengaluru)\n ((Vishwadha.Chander@thomsonreuters.com; within U.S. +1 646 223\n8780, outside U.S. +91 80 6749 6132;))</p>");
			hm1.put("ID", "urn:newsml:reuters.com:20210216:nL4N2KM3ON");
			hm1.put("RT", "2021-02-16T21:00:01-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Deals of the day-Mergers and acquisitions");
			hm1.put("CT", "2021-02-16T21:00:01-00:00");
			hm1.put("LT", "2021-02-16T21:00:01-00:00");

			hm2.put("TE",
					"<p> (Adds statement from Issa brothers and TDR Capital)\n    LONDON, Feb 16 (Reuters) - The UK competition regulator has\nset an April 20 deadline for a decision on the acquisition of\nsupermarket chain Asda by Britain&apos;s billionaire Issa brothers\nand TDR Capital.\n    Private equity firm TDR Capital and brothers Zuber and\nMohsin Issa agreed in October to buy a majority stake in\nBritain&apos;s third largest grocer from U.S. group Walmart  <Origin Href=\"QuoteRef\">WMT.N</Origin> .\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2GT29W</Origin>\n    The brothers and TDR on Tuesday said they had completed the\nacquisition, which gave Asda an enterprise value of 6.8 billion\npounds ($9.5 billion), though it remains subject to regulatory\napproval.\n    The Competition and Markets Authority (CMA) launched an\ninquiry into the deal in December and initially set a Feb. 18\ndeadline for a so-called Phase 1 but then stopped the clock on\nthe process while it sought additional documentation.\n    Its inquiry has been complicated by the buyers&apos; plan to\noffload Asda&apos;s 322 petrol stations to their own petrol forecourt\nbusiness, EG Group, for 750 million pounds when the deal is\ncompleted.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2K925H</Origin>\n    The petrol forecourts sale, under which the stations will\ncontinue to be Asda-branded, will be considered by the CMA as\npart of its review of the Asda acquisition.\n    At the end of the Phase 1 stage, the CMA will either clear\nthe deal or ask for concessions to avoid a more detailed Phase 2\ninvestigation.\n    Competition lawyers expect the deal to be cleared at Phase 1\nbut with the combined group having to dispose of some petrol\nforecourts.\n    The brothers and TDR said they &quot;remain confident of a\npositive outcome&quot;.\n    In 2019 Walmart&apos;s attempt to sell Asda to rival Sainsbury&apos;s\n <Origin Href=\"QuoteRef\">SBRY.L</Origin>  for 7.3 billion pounds was thwarted by the CMA.\n    Asda is due to update on fourth-quarter trading on Thursday.\n($1 = 0.7175 pounds)\n\n (Reporting by James Davey\nEditing by Jason Neely and David Goodman)\n ((james.davey@thomsonreuters.com))</p>");
			hm2.put("ID", "urn:newsml:reuters.com:20210216:nL1N2KM100");
			hm2.put("RT", "2021-02-16T14:05:33-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "UPDATE 1-UK watchdog to rule on $9.5 bln Asda takeover by April 20");
			hm2.put("CT", "2021-02-16T14:05:33-00:00");
			hm2.put("LT", "2021-02-16T14:05:33-00:00");

			hm3.put("TE",
					"<p>    LONDON, Feb 16 (Reuters) - Britain&apos;s Issa brothers and\nprivate equity firm TDR Capital said on Tuesday they had\ncompleted the acquisition of supermarket group Asda from Walmart\n <Origin Href=\"QuoteRef\">WMT.N</Origin>  for an enterprise value of 6.8 billion pounds ($9.5\nbillion).\n    The brothers and TDR have bought a majority ownership stake\nin Asda, while Walmart will retain an equity investment in the\nbusiness, with an ongoing commercial relationship and a seat on\nthe board. The deal was announced in October.\n    The transaction is still subject to regulatory approval. The\nCompetition and Markets Authority has set a April 20 deadline\nfor a ruling. The brothers and TDR said they &quot;remain confident&quot;\nof a positive outcome.\n($1 = 0.7192 pounds)\n\n (Reporting by James Davey; editing by William James)\n ((james.davey@thomsonreuters.com;))</p>");
			hm3.put("ID", "urn:newsml:reuters.com:20210216:nL8N2KM47L");
			hm3.put("RT", "2021-02-16T13:21:47-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Issa bros and TDR Capital complete $9.5 bln Asda purchase");
			hm3.put("CT", "2021-02-16T13:10:01-00:00");
			hm3.put("LT", "2021-02-16T13:21:47-00:00");

			hm4.put("TE",
					"<p> (Adds Rexnord, Li-Cycle Corp, Issa, CoStar Group Inc, Essity,\nCemex, Bankia Adidas; updates Baby Care Inc)\n    Feb 16 (Reuters) - The following bids, mergers, acquisitions\nand disposals were reported by 1400 GMT on Tuesday:\n    \n    ** German sportswear maker Adidas AG  <Origin Href=\"QuoteRef\">ADSGn.DE</Origin>  said it\nplans to divest the underperforming brand Reebok that it failed\nto revive 15 years after buying the U.S. fitness label to help\ncompete with archrival Nike Inc  <Origin Href=\"QuoteRef\">NKE.N</Origin> .  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nFWN2KM0QO</Origin>\n    \n    ** The Spanish government has extended until December 2023\nits deadline to offload its stake in Bankia  <Origin Href=\"QuoteRef\">BKIA.MC</Origin> , the\nSpanish government spokeswoman Maria Jesus Montero said on\nTuesday. The government had currently until 2021 to sell the\n61.8% it holds in Bankia.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nLUN2J900G</Origin>\n    \n    ** Mexican cement producer Cemex  <Origin Href=\"QuoteRef\">CMXCPO.MX</Origin>  said it had\nacquired a Beck Readymix Concete Co, a cement business in San\nAntonio, Texas.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nE6N2FN00B</Origin>\n    \n    ** Essity  <Origin Href=\"QuoteRef\">ESSITYa.ST</Origin>  confirmed that discussions to buy\nadditional shares in hygiene and personal products maker Asaleo\nwere at an advanced stage.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KM3HT</Origin>\n    \n    ** Commercial property data vendor CoStar Group Inc  <Origin Href=\"QuoteRef\">CSGP.O</Origin> \nhas offered to buy real-estate data provider CoreLogic Inc\n <Origin Href=\"QuoteRef\">CLGX.N</Origin>  for about $6.9 billion in an all-stock deal, weeks\nafter the real-estate data provider agreed to be acquired by two\nprivate equity firms.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM2II</Origin>\n        \n    ** Britain&apos;s Issa brothers and private equity firm TDR\nCapital said they had completed the acquisition of supermarket\ngroup Asda from Walmart  <Origin Href=\"QuoteRef\">WMT.N</Origin>  for an enterprise value of 6.8\nbillion pounds ($9.5 billion).  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KM47L</Origin>\n        \n    ** Li-Cycle Corp said it will go public through a merger\nwith blank-check acquisition company Peridot Acquisition Corp\n <Origin Href=\"QuoteRef\">PDAC.N</Origin>  in a deal valuing the recycler of lithium-ion batteries\nat $1.67 billion.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KI6LE</Origin>\n    \n    ** Rexnord Corp  <Origin Href=\"QuoteRef\">RXN.N</Origin>  said it would merge its industrial\nprocess and motion control business with larger rival Regal\nBeloit Corp  <Origin Href=\"QuoteRef\">RBC.N</Origin>  in a deal valued at about $3.69 billion\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM2CP</Origin>\n\n    ** Owlet Baby Care Inc, a maker of baby monitoring devices,\nwill go public through a merger with a blank-check firm backed\nby private equity firm Sandbridge Capital, in a deal that values\nthe equity of the combined entity at about $1.4 billion.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM283</Origin>\n    \n    ** Japanese refiner Idemitsu Kosan  <Origin Href=\"QuoteRef\">5019.T</Origin>  said it has\nwithdrawn its offer for a potential acquisition of smaller\nrival, Toa Oil Co  <Origin Href=\"QuoteRef\">5008.T</Origin> , in which a U.S. investment fund has\nbeen building up a stake. Idemitsu already owns 50.12% of Toa.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KL08H</Origin>\n    \n    ** The British competition authority (CMA) has expressed\nconcerns about the planned acquisition by Norway&apos;s Adevinta\n <Origin Href=\"QuoteRef\">ADEV.OL</Origin>  of eBay&apos;s  <Origin Href=\"QuoteRef\">EBAY.O</Origin>  classified ads business, Adevinta\nsaid. The deal, worth $9.2 billion and announced in July, would\ncreate the world&apos;s largest classifieds group if it went ahead.\n <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KM1CO</Origin>\n    \n    **  British outsourcer Serco  <Origin Href=\"QuoteRef\">SRP.L</Origin>  said it would buy\nconsulting services provider Whitney, Bradley &amp; Brown Inc from\nan affiliate of H.I.G. Capital for $295 million to strengthen\nits services to the defence sector.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM1I9</Origin>\n    \n    ** Irish food and ingredients company Kerry  <Origin Href=\"QuoteRef\">KYGa.I</Origin>  is\nundertaking a strategic review of its dairy business in Britain\nand Ireland, which may lead to a transaction in the coming\nmonths, its chief executive said.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nS8N2JH087</Origin>\n    \n    ** Abu Dhabi&apos;s state-owned ADQ has lined up a small group of\nbanks for a loan of about $1 billion to back its acquisition of\na 45% stake in commodities trader Louis Dreyfus Co (LDC), three\nsources familiar with the matter said.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL8N2KM0L5</Origin>\n    \n    ** Indian conglomerate Tata Group will buy a 68% stake in\nAlibaba-backed  <Origin Href=\"QuoteRef\">BABA.N</Origin>  online grocery startup BigBasket for\nabout 95 billion rupees ($1.31 billion), television channel ET\nnow said.  <Origin Href=\"StoryRef\">urn:newsml:reuters.com:*:nL4N2KM20L</Origin>  \n\n (Compiled by Vishwadha Chander in Bengaluru)\n ((Vishwadha.Chander@thomsonreuters.com; within U.S. +1 646 223\n8780, outside U.S. +91 80 6749 6132;))</p>");
			hm4.put("ID", "urn:newsml:reuters.com:20210216:nL4N2KM28O");
			hm4.put("RT", "2021-02-16T14:09:39-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Deals of the day-Mergers and acquisitions");
			hm4.put("CT", "2021-02-16T11:27:04-00:00");
			hm4.put("LT", "2021-02-16T14:09:39-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("F.N") || isinCode.trim().equalsIgnoreCase("2YTD")
				|| isinCode.trim().equalsIgnoreCase("5YTD")) {
			hm1.put("TE",
					" (For a Reuters live blog on U.S., UK and European stock\nmarkets, click LIVE/ or type LIVE/ in a news window.)\n    * Discovery down after deal to merge with AT&amp;T&apos;s media unit\n    * ViacomCBS rises after report Soros scooped up stock\n    * Indexes down: Dow 0.23%, S&amp;P 0.42%, Nasdaq 0.83%\n\n (Adds details, comments; updates prices)\n    By Echo Wang\n    May 17 (Reuters) - Technology stocks pulled Wall Street&apos;s\nmain indexes lower on Monday, with the Nasdaq Composite index\n <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  falling about 1% as signs of growing inflationary\npressures raised concern about monetary policy tightening.\n    Six of the 11 major S&amp;P sectors declined, with technology\n <Origin Href=\\\"QuoteRef\\\">.SPLRCT</Origin>  shedding about 1.3%. Apple Inc  <Origin Href=\\\"QuoteRef\\\">AAPL.O</Origin>  and Microsoft\nCorp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin>  each fell more than 1%, weighing the most on the\nbenchmark S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  and the Nasdaq.\n    The S&amp;P 500 scored its biggest one-day jump in more than a\nmonth on Friday as investors picked up beaten-down stocks\nfollowing a pullback earlier in the week on worries about\ninflation and a sooner-than-expected tightening by the U.S.\nFederal Reserve.\n    &quot;What is causing the decline, no surprise to anybody, is the\nworry about inflation and interest rates,&quot; said Sam Stovall,\nchief investment strategist at CFRA Research in New York.\n    &quot;As a result that&apos;s causing the growth group, in particular\ntechnology and consumer discretionary stocks, to experience\nweakness, while some of the more value-oriented groups are\nholding up a bit better.&quot;\n    At 2:53 p.m. ET, the Dow Jones Industrial Average  <Origin Href=\\\"QuoteRef\\\">.DJI</Origin> \nfell 79.86 points, or 0.23%, to 34,302.27; the S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin> \nlost 17.38 points, or 0.42%, at 4,156.47; and the Nasdaq\nComposite  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  dropped 112.05 points, or 0.83%, to 13,317.93.\n    Earnings this week will be scrutinized for clues on whether\nrising prices had any impact on consumer demand and if retailers\ncan sustain their strong earnings momentum.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2N11NS</Origin>\n    Walmart Inc  <Origin Href=\\\"QuoteRef\\\">WMT.N</Origin> , home improvement chain Home Depot Inc\n <Origin Href=\\\"QuoteRef\\\">HD.N</Origin>  and department store operator Macy&apos;s Inc  <Origin Href=\\\"QuoteRef\\\">M.N</Origin>  are set to\nreport earnings on Tuesday, with Target Corp  <Origin Href=\\\"QuoteRef\\\">TGT.N</Origin>  Ralph\nLauren  <Origin Href=\\\"QuoteRef\\\">RL.N</Origin>  and TJX Cos  <Origin Href=\\\"QuoteRef\\\">TJX.N</Origin>  due later in the week.\n    With the earnings season at its tail end, overall earnings\nfor S&amp;P 500 companies are expected to have climbed 50.6% from a\nyear ago, according to Refinitiv IBES, the strongest pace in 11\nyears.\n    AT&amp;T Inc  <Origin Href=\\\"QuoteRef\\\">T.N</Origin> , owner of HBO and Warner Bros studios, and\nDiscovery Inc  <Origin Href=\\\"QuoteRef\\\">DISCA.O</Origin> , home to lifestyle TV networks such as\nHGTV and TLC, said on Monday they will combine their content\nassets to create a standalone global entertainment and media\nbusiness. AT&amp;T shares declined 0.4%, while Discovery fell about\n4.7%.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL2N2N308Q</Origin>\n    Cryptocurrency-related stocks like Marathon Digital\n <Origin Href=\\\"QuoteRef\\\">MARA.O</Origin> , Riot Blockchain  <Origin Href=\\\"QuoteRef\\\">RIOT.O</Origin>  and Coinbase  <Origin Href=\\\"QuoteRef\\\">COIN.O</Origin>  fell\nbetween 6% and 10% as bitcoin swung in volatile trading after\nTesla Inc  <Origin Href=\\\"QuoteRef\\\">TSLA.O</Origin>  boss Elon Musk tweeted about the carmaker&apos;s\nbitcoin holdings.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL2N2N408C</Origin>\n    Among the most active stocks on the NYSE were AT&amp;T  <Origin Href=\\\"QuoteRef\\\">T.N</Origin> ,\ndown 0.9% at $31.94; AMC Entertainment Holdings Inc  <Origin Href=\\\"QuoteRef\\\">AMC.N</Origin> , up\n7.2% at $13.92; and Ford Motor Co  <Origin Href=\\\"QuoteRef\\\">F.N</Origin> , up 1.9% at $12.07.\n    On the Nasdaq, the most active issues included Aerpio\nPharmaceuticals Inc  <Origin Href=\\\"QuoteRef\\\">ARPO.O</Origin> , up 37.1% at $1.59; Sundial Growers\n <Origin Href=\\\"QuoteRef\\\">SNDL.O</Origin> , up 4.5% at 74 cents; and Castor Maritime  <Origin Href=\\\"QuoteRef\\\">CTRM.O</Origin> , up\n10.9% at 44 cents.\n    Advancing issues outnumbered decliners on the NYSE by a\n1.02-to-1 ratio; on Nasdaq, a 1.20-to-1 ratio favored decliners.\n    The S&amp;P 500 posted 34 new 52-week highs and no new lows; the\nNasdaq Composite recorded 95 new highs and 49 new lows.  \n\n\n (Reporting by Echo Wang in New York; Additional reporting by\nMedha Singh and Sruthi Shankar in Bengaluru; Editing by\nSaumyadeb Chakrabarty, Maju Samuel and Richard Chang)\n ((e.wang@thomsonreuters.com))");
			hm1.put("ID", "urn:newsml:reuters.com:20210517:nL2N2N41RT");
			hm1.put("RT", "2021-05-17T19:06:22-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "US STOCKS-Wall St weighed down by falling tech stocks");
			hm1.put("CT", "2021-05-17T19:06:22-00:00");
			hm1.put("LT", "2021-05-17T19:06:22-00:00");

			hm2.put("TE",
					"For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210517:nPn2m2kqsa&amp;default-theme=true</Origin>\n\nFirst National Bank Expands in Charleston with Branch at Freshfields Village\nBuilds on Regional Presence with New Full-Service Branch and Adds ATMs in Charleston International Airport\n\nPR Newswire\n\nCHARLESTON, S.C., May 17, 2021\n\nCHARLESTON, S.C., May 17, 2021 /PRNewswire/ -- First National Bank, the\nlargest subsidiary of F.N.B. Corporation (NYSE: FNB), announced its continued\nexpansion in Charleston, SC, with an innovative branch at Freshfields Village,\nan open-air shopping and dining experience at the crossroads of Kiawah Island,\nSeabrook Island and Johns Island. FNB also deployed four ATMs in Charleston\nInternational Airport.\n\nThe new Freshfields Village location is FNB&apos;s third retail branch in the\nCharleston area. Along with the airport ATMs, the location builds on the\nCompany&apos;s successful expansion strategy, which leverages a strong commercial\nbanking presence and investments in technology to deliver a premium,\nfull-service customer experience. By the end of 2021, FNB has plans to have\nfive retail branch locations in the market in addition to its downtown\nregional hub.\n\n&quot;The exceptional team we have brought together in Charleston, coupled with\nprime retail locations, will enable us to continue to successfully execute on\nour expansion strategy in this dynamic market,&quot; said Vincent J. Delie Jr.,\nChairman, President and Chief Executive Officer of F.N.B. Corporation and\nFirst National Bank. &quot;We have already experienced considerable growth in the\nLowcountry over the last several years and are proud to further our commitment\nto the local community.&quot;\n\nSince entering the region in 2017, FNB has established significant commercial\nbanking and wealth management operations along with a retail network serving\nCharleston and upstate South Carolina. The Company was recently named a South\nCarolina Top Workplace\n(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&amp;l=en&amp;o=3165830-1&amp;h=4280450983&amp;u=https%3A%2F%2Fwww.fnb-online.com%2Fabout-us%2Fnewsroom%2Fpress-releases%2F2021%2Ffnb-corporations-banking-subsidiary-recognized-as-a-top-workplace--in-south-carolina-050321&amp;a=South+Carolina+Top+Workplace</Origin>)\nbased on employee feedback.\n\nLeonard &quot;Len&quot; L. Hutchison, III, Regional Market Executive and President of\nFNB&apos;s Charleston and South Carolina markets, added, &quot;FNB has established\nitself as a fixture in the Charleston community. The continued expansion of\nour physical network supplements our high-touch approach and even better\nequips our product specialists and bankers to deliver the full breadth of our\ncomprehensive consumer banking, commercial banking and wealth management\nsolutions.&quot;\n\nLocated at 398 Freshfields Drive, Johns Island, SC, FNB&apos;s new office utilizes\na modern concept branch design. Branch features include an ATM with\nTellerChat, which allows clients to use video chat technology to conduct\ntransactions with a representative during extended hours, as well as a remote\ndrive up ATM for added convenience. In addition, customers are able to shop\nand learn more about products and services using touchscreens and FNB&apos;s\ninteractive Solutions Center kiosk, which can also be explored online using\nthe Solutions Center e-store\n(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&amp;l=en&amp;o=3165830-1&amp;h=1682359195&amp;u=https%3A%2F%2Fwww.fnb-online.com%2Fsolutions-center&amp;a=Solutions+Center+e-store</Origin>)\n.\n\nCustomers and community members are invited to visit the Freshfields Village\nbranch to experience FNB&apos;s comprehensive products and solutions, including\npersonal and business banking, wealth management, private banking and\ninsurance services. Office hours are Monday to Thursday from 9:00 AM – 5:00\nPM and Friday from 9:00 AM – 6:00 PM. Plans for a ribbon-cutting and grand\nopening celebration will be communicated at a later date.\n\nAbout F.N.B. Corporation\n\nF.N.B. Corporation (NYSE: FNB), headquartered in Pittsburgh, Pennsylvania, is\na diversified financial services company operating in seven states and the\nDistrict of Columbia. FNB&apos;s market coverage spans several major metropolitan\nareas including: Pittsburgh, Pennsylvania; Baltimore, Maryland; Cleveland,\nOhio; Washington, D.C.; and Charlotte, Raleigh, Durham and the Piedmont Triad\n(Winston-Salem, Greensboro and High Point) in North Carolina. The Company has\ntotal assets of more than $38 billion and nearly 340 banking offices\nthroughout Pennsylvania, Ohio, Maryland, West Virginia, North Carolina, South\nCarolina, Washington, D.C. and Virginia.\n\nFNB provides a full range of commercial banking, consumer banking and wealth\nmanagement solutions through its subsidiary network which is led by its\nlargest affiliate, First National Bank of Pennsylvania, founded in 1864.\nCommercial banking solutions include corporate banking, small business\nbanking, investment real estate financing, government banking, business\ncredit, capital markets and lease financing. The consumer banking segment\nprovides a full line of consumer banking products and services, including\ndeposit products, mortgage lending, consumer lending and a complete suite of\nmobile and online banking services. FNB&apos;s wealth management services include\nasset management, private banking and insurance.\n\nThe common stock of F.N.B. Corporation trades on the New York Stock Exchange\nunder the symbol &quot;FNB&quot; and is included in Standard &amp; Poor&apos;s MidCap 400\nIndex with the Global Industry Classification Standard (GICS) Regional Banks\nSub-Industry Index. Customers, shareholders and investors can learn more about\nthis regional financial institution by visiting the F.N.B. Corporation website\nat <Origin Href=\\\"Link\\\">www.fnbcorporation.com</Origin>\n(<Origin Href=\\\"Link\\\">https://c212.net/c/link/?t=0&amp;l=en&amp;o=3165830-1&amp;h=3724422096&amp;u=http%3A%2F%2Fwww.fnbcorporation.com%2F&amp;a=www.fnbcorporation.com</Origin>)\n.\n\nView original\ncontent:<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/first-national-bank-expands-in-charleston-with-branch-at-freshfields-village-301292573.html</Origin>\n(<Origin Href=\\\"Link\\\">http://www.prnewswire.com/news-releases/first-national-bank-expands-in-charleston-with-branch-at-freshfields-village-301292573.html</Origin>)\n\nSOURCE F.N.B. Corporation\n\n\n\nMedia Contact:  Jennifer Reel, 724-983-4856, 724-699-6389 (cell), reel@fnb-corp.com or Analyst/Institutional Investor Contact: Matthew Lazzaro, 724-983-4254, 412-216-2510 (cell), lazzaro@fnb-corp.com\n\nWebsite: \n<Origin Href=\\\"Link\\\">http://www.fnbcorporation.com</Origin>\n\nCopyright (c) 2021 PR Newswire Association,LLC. All Rights Reserved.");
			hm2.put("ID", "urn:newsml:reuters.com:20210517:nPn2m2kqsa");
			hm2.put("RT", "2021-05-17T14:00:11-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "First National Bank Expands in Charleston with Branch at Freshfields Village");
			hm2.put("CT", "2021-05-17T14:00:11-00:00");
			hm2.put("LT", "2021-05-17T14:00:11-00:00");

			hm3.put("TE",
					"By Yilei Sun and Tony Munroe\n    BEIJING, May 17 (Reuters) - Chinese state-run automaker\nChongqing Changan Automobile  <Origin Href=\\\"QuoteRef\\\">000625.SZ</Origin>  plans to list its\nelectric vehicle (EV) unit on Shanghai&apos;s Nasdaq-style STAR\nMarket, three sources briefed on the matter said, to fund a\nrapid expansion of its business. \n    Changan, based in the southwestern city of Chongqing, holds\n48.95% of the unit, which makes entry-level and mass-market\nelectric vehicles. The unit aims to sell over 500,000 EVs a year\nin 2025 and one million in 2030, Changan said during a recent\nbriefing with investors. \n    This year Changan units plans to sell more than 70,000\nelectric cars. It plans to make its EV business profitable by\n2024 but has not set a time frame for the STAR listing. \n    Two of the sources attended the briefing and the third had\ndirect knowledge of the presentation. They all declined to be\nnamed as they are not allowed to speak to media. \n    Changan did not respond to a request for comment. \n    To reach its sales target, Changan will develop two or three\nmodel platforms for EVs including small cars to win market share\nin smaller Chinese cities. \n    General Motors Co&apos;s  <Origin Href=\\\"QuoteRef\\\">GM.N</Origin>  China venture with SAIC Motor\n <Origin Href=\\\"QuoteRef\\\">600104.SS</Origin>  dominates the small-car segment with its micro\ntwo-door Wuling Hong Guang MINI EV.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2M61I4</Origin>\n    Changan also plans to develop electric light commercial and\nhydrogen fuel cell vehicles, the three sources said. \n    Parent Changan group makes gasoline and electric cars\nthrough partnerships with Ford Motor Co  <Origin Href=\\\"QuoteRef\\\">F.N</Origin>  and Mazda\n <Origin Href=\\\"QuoteRef\\\">7261.T</Origin> . It also has a partnership with telecoms gear giant\nHuawei Technologies  <Origin Href=\\\"NewsSearch\\\">HWT.UL</Origin>  and battery maker CATL  <Origin Href=\\\"QuoteRef\\\">300750.SZ</Origin> ,\nand makes self-branded cars as well. \n    China, where more than 25 million vehicles were sold last\nyear, is the world&apos;s biggest auto market. It is also a big\npromoter of the EV sector. \n    Sales of electric, plug-in hybrid and hydrogen-powered\nvehicles in China are forecast to rise to 20% of all new car\nsales by 2025 from 5% last year.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2HO0KA</Origin> \n    Nio Inc  <Origin Href=\\\"QuoteRef\\\">NIO.N</Origin> , Xpeng Inc  <Origin Href=\\\"QuoteRef\\\">XPEV.N</Origin>  and Tesla Inc  <Origin Href=\\\"QuoteRef\\\">TSLA.O</Origin> \nare all expanding sales in China, encouraged by a state policy\nof promoting greener vehicles to cut pollution. \n\n (Reporting by Yilei Sun and Tony Munroe; Editing by Tom Hogue)\n ((Y.Sun@thomsonreuters.com; +86 10 66271262; Reuters Messaging:\ny.sun.thomsonreuters.com@reuters.net))");
			hm3.put("ID", "urn:newsml:reuters.com:20210517:nL3N2N41OT");
			hm3.put("RT", "2021-05-17T08:09:55-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Chinese automaker Changan aims to list EV unit on STAR Market -sources");
			hm3.put("CT", "2021-05-17T08:09:35-00:00");
			hm3.put("LT", "2021-05-17T08:09:56-00:00");

			hm4.put("TE",
					"By Nick Carey\n    May 17 (Reuters) - The European Union needs to dramatically\ntoughen weak CO2 targets for commercial vans to spur a shift to\nelectric models and phase out fossil-fuel sales entirely by\n2035, European campaign group Transport and Environment (T&amp;E)\nsaid on Monday.\n    T&amp;E said an analysis of van sales in 2020 showed no change\nin CO2 emissions from 2017 and found the EU&apos;s CO2 targets are so\nweak that most manufacturers can meet them without selling a\nsingle zero-emission van.\n    &quot;Standards which entered into force at the beginning of 2020\nwere supposed to make vans cleaner, but vanmakers have had to do\nalmost nothing to reach them,&quot; T&amp;E freight manager James Nix\nsaid in a statement. &quot;With pathetic CO2 targets, the boom in\ne-commerce is becoming a nightmare for our planet.&quot;\n    EU sales of electric and plug-in hybrid passenger cars\nalmost trebled to over 1 million vehicles last year, accounting\nfor more than 10% of overall sales, thanks to stringent CO2\ntargets and government subsidies.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2KA0T4</Origin>\n    But electric van sales have languished at about 2% of the\nmarket.\n    T&amp;E said the EU needs to bring forward its current CO2\nreduction target of 31% forward to 2027 from 2030 and aim for a\nfar more ambitious target of at least a 60% reduction by 2030.\n    The group said the EU should set a 100% CO2 reduction target\nby 2035, effectively banning combustion engine vans.\n    T&amp;E said the EU should prevent van makers from building\nplug-in hybrid (PHEV) vans. Groups like T&amp;E are pushing for PHEV\npassenger car models to be phased out in the next few years,\narguing that owners do not charge them properly and rely too\nmuch on the fossil-fuel engine.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2LK2OM</Origin>\n    Few automakers have developed PHEV commercial vans, but Ford\nMotor Co  <Origin Href=\\\"QuoteRef\\\">F.N</Origin>  said in March that the next iteration of its\nTransit van will include a plug-in hybrid version.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2LE0PU</Origin>\n    \n\n (Reporting By Nick Carey; editing by David Evans)\n ((nick.carey@thomsonreuters.com; +44 7385 414 954;))");
			hm4.put("ID", "urn:newsml:reuters.com:20210516:nL1N2N10NH");
			hm4.put("RT", "2021-05-16T22:01:00-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "European climate group says EU needs far tougher van CO2 targets");
			hm4.put("CT", "2021-05-16T22:01:00-00:00");
			hm4.put("LT", "2021-05-16T22:01:00-00:00");

			hm5.put("TE",
					"By Nick Carey\n    May 17 (Reuters) - The European Union needs to dramatically\ntoughen weak CO2 targets for commercial vans to spur a shift to\nelectric models and phase out fossil-fuel sales entirely by\n2035, European campaign group Transport and Environment (T&amp;E)\nsaid on Monday.\n    T&amp;E said an analysis of van sales in 2020 showed no change\nin CO2 emissions from 2017 and found the EU&apos;s CO2 targets are so\nweak that most manufacturers can meet them without selling a\nsingle zero-emission van.\n    &quot;Standards which entered into force at the beginning of 2020\nwere supposed to make vans cleaner, but vanmakers have had to do\nalmost nothing to reach them,&quot; T&amp;E freight manager James Nix\nsaid in a statement. &quot;With pathetic CO2 targets, the boom in\ne-commerce is becoming a nightmare for our planet.&quot;\n    EU sales of electric and plug-in hybrid passenger cars\nalmost trebled to over 1 million vehicles last year, accounting\nfor more than 10% of overall sales, thanks to stringent CO2\ntargets and government subsidies.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2KA0T4</Origin>\n    But electric van sales have languished at about 2% of the\nmarket.\n    T&amp;E said the EU needs to bring forward its current CO2\nreduction target of 31% forward to 2027 from 2030 and aim for a\nfar more ambitious target of at least a 60% reduction by 2030.\n    The group said the EU should set a 100% CO2 reduction target\nby 2035, effectively banning combustion engine vans.\n    T&amp;E said the EU should prevent van makers from building\nplug-in hybrid (PHEV) vans. Groups like T&amp;E are pushing for PHEV\npassenger car models to be phased out in the next few years,\narguing that owners do not charge them properly and rely too\nmuch on the fossil-fuel engine.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2LK2OM</Origin>\n    Few automakers have developed PHEV commercial vans, but Ford\nMotor Co  <Origin Href=\\\"QuoteRef\\\">F.N</Origin>  said in March that the next iteration of its\nTransit van will include a plug-in hybrid version.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2LE0PU</Origin>\n    \n\n (Reporting By Nick Carey; editing by David Evans)\n ((nick.carey@thomsonreuters.com; +44 7385 414 954;))");
			hm5.put("ID", "urn:newsml:reuters.com:20210514:nFWN2N10RL");
			hm5.put("RT", "2021-05-14T20:16:06-00:00");
			hm5.put("PR", "reuters.com");
			hm5.put("HT", "BRIEF-F.N.B. Corp Files For Potential Mixed Shelf Offering Size Not Disclosed");
			hm5.put("CT", "2021-05-14T20:16:06-00:00");
			hm5.put("LT", "2021-05-14T20:16:06-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);
			al.add(hm5);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("JPM.N")) {
			hm1.put("TE",
					"DUBAI, May 18 (Reuters) - Dubai&apos;s biggest lender Emirates\nNBD  <Origin Href=\\\"QuoteRef\\\">ENBD.DU</Origin>  has hired banks to arrange the sale of U.S.\ndollar-denominated Additional Tier 1 bonds that will be\nnon-callable for six years, a document showed on Tuesday.\n    Emirates NBD Capital, First Abu Dhabi Bank  <Origin Href=\\\"QuoteRef\\\">FAB.AD</Origin> ,\nJPMorgan  <Origin Href=\\\"QuoteRef\\\">JPM.N</Origin> , HSBC  <Origin Href=\\\"QuoteRef\\\">HSBA.L</Origin> , NCB Capital  <Origin Href=\\\"QuoteRef\\\">1180.SE</Origin>  and\nStandard Chartered  <Origin Href=\\\"QuoteRef\\\">STAN.L</Origin>  will arrange fixed-income investor\ncalls starting on Tuesday, the document from one of the banks\nshowed. A benchmark unrated issuance will follow, subject to\nmarket conditions.\n\n (Reporting by Yousef Saba; Editing by Andrew Heavens)\n ((Yousef.Saba@thomsonreuters.com; +971562166204; <Origin Href=\\\"Link\\\">https://twitter.com/YousefSaba</Origin>))");
			hm1.put("ID", "urn:newsml:reuters.com:20210518:nD5N2K201R");
			hm1.put("RT", "2021-05-18T06:16:30-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Emirates NBD hires banks for AT1 dollar bonds - document");
			hm1.put("CT", "2021-05-18T06:04:21-00:00");
			hm1.put("LT", "2021-05-18T06:16:30-00:00");

			hm2.put("TE",
					"May 17 (Reuters) - JPMorgan Chase &amp; Co  <Origin Href=\\\"QuoteRef\\\">JPM.N</Origin> :\n    * JPMORGAN CHASE DECLARES COMMON STOCK DIVIDEND\n    * JPMORGAN - DECLARED A QUARTERLY DIVIDEND OF 90 CENTS PER\nSHARE\nON THE OUTSTANDING SHARES OF THE COMMON STOCK OF JPMORGAN CHASE\n\nSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:nBw9Pdd5Na</Origin> \nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">JPM.N</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
			hm2.put("ID", "urn:newsml:reuters.com:20210517:nFWN2N41H0");
			hm2.put("RT", "2021-05-17T21:18:11-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "BRIEF-JPMorgan Chase Declares Common Stock Dividend");
			hm2.put("CT", "2021-05-17T21:18:11-00:00");
			hm2.put("LT", "2021-05-17T21:18:11-00:00");

			hm3.put("TE",
					"May 17 (Reuters) - JPMorgan Chase &amp; Co  <Origin Href=\\\"QuoteRef\\\">JPM.N</Origin> :\n    * JPMORGAN CHASE &amp; CO - CREDIT CARD DELINQUENCY RATE 0.78% \nAT\nAPRIL END VERSUS 0.89%  AT MARCH END\n    * JPMORGAN CHASE &amp; CO - CREDIT CARD CHARGE-OFF RATE 1.97% IN\nAPRIL\nVERSUS 2.03% IN MARCH - SEC FILING\n\nSource text fo(<Origin Href=\\\"Link\\\">https://bit.ly/3eSEVVR</Origin>)\nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">JPM.N</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
			hm3.put("ID", "urn:newsml:reuters.com:20210517:nFWN2N40SA");
			hm3.put("RT", "2021-05-17T10:44:52-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "BRIEF-Jpmorgan Chase Says Credit Card Charge-Off Rate 1.97% In April Versus 2.03% In March");
			hm3.put("CT", "2021-05-17T10:44:52-00:00");
			hm3.put("LT", "2021-05-17T10:44:52-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("BLK")) {

			hm1.put("TE",
					"For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210518:nRSR9606Ya&amp;default-theme=true</Origin>\n\nRNS Number : 9606Y  iShares III BLK E MAC £HGD £  18 May 2021    FUND:                       BlackRock ESG Multi-Asset Conservative Portfolio UCITS ETF GBP Hedged (Acc)\n DEALING DATE:               17-May-21\n NAV PER SHARE:              Official NAV GBP 5.156557\n NUMBER OF SHARES IN ISSUE:  133,506\n CODE:                       MACG LN\n ISIN:                       IE00BLP53N06\n DISCLAIMER:                 All information provided by BlackRock is purely of an indicative nature and\n                             subject to change without notice at any time. The information is for guidance\n                             only and does not represent an offer, investment advice or any kind of\n                             financial service. Although BlackRock has obtained the information provided\n                             from sources that should be considered reliable, BlackRock cannot guarantee\n                             its accuracy, completeness or that it is fit for any particular purpose. The\n                             information provided does not confer any rights.\n                             Past performance is not a guide to future performance.  The value of\n                             investments and the income from them can fall as well as rise and is not\n                             guaranteed.  You may not get back the amount originally invested.  Changes\n                             in the rates of exchange between currencies may cause the value of investments\n                             to diminish or increase.  Fluctuation may be particularly marked in the case\n                             of a higher volatility fund or segregated account and the value of an\n                             investment may fall suddenly and substantially.  Levels and basis of taxation\n                             may change from time to time. iShares® and BlackRock® are registered\n                             trademarks of BlackRock, Inc., or its subsidiaries in the United States and\n                             elsewhere.\n                             © 2021 BlackRock Advisors (UK) Limited, authorised and regulated by the\n                             Financial Conduct Authority.  Registered office: 12 Throgmorton Avenue,\n                             London, EC2N 2DL.  Tel: 020 7743 3000.  Registered in England No. 00796793.\n\nThis information is provided by RNS, the news service of the London Stock Exchange. RNS is approved by the Financial Conduct Authority to act as a Primary Information Provider in the United Kingdom. Terms and conditions relating to the use and distribution of this information may apply. For further information, please contact\nrns@lseg.com (mailto:rns@lseg.com)\n or visit\n<Origin Href=\\\"Link\\\">www.rns.com</Origin> (<Origin Href=\\\"Link\\\">http://www.rns.com/</Origin>)\n.\n\nRNS may use your IP address to confirm compliance with the terms and conditions, to analyse how you engage with the information contained in this communication, and to share such analysis on an anonymised basis with others as part of our commercial services. For further information about how RNS and the London Stock Exchange use the personal data you provide us, please see our\nPrivacy Policy (<Origin Href=\\\"Link\\\">https://www.lseg.com/privacy-and-cookie-policy</Origin>)\n.   END  NAVDZGMKZRFGMZG\n\n");
			hm1.put("ID", "urn:newsml:reuters.com:20210518:nRSR9606Ya");
			hm1.put("RT", "2021-05-18T06:10:06-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "REG - iShares III BLK E £  - Net Asset Value(s)");
			hm1.put("CT", "2021-05-18T06:10:06-00:00");
			hm1.put("LT", "2021-05-18T07:29:21-00:00");

			hm2.put("TE",
					"For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210518:nRSR9607Ya&amp;default-theme=true</Origin>\n\nRNS Number : 9607Y  iShares III BLK MAM£  18 May 2021\n FUND:                       BlackRock ESG Multi-Asset Moderate Portfolio UCITS ETF GBP Hedged (Acc)\n DEALING DATE:               17-May-21\n NAV PER SHARE:              Official NAV GBP 5.47538\n NUMBER OF SHARES IN ISSUE:  319,978\n CODE:                       MAMG LN\n ISIN:                       IE00BLLZQ797\n DISCLAIMER:                 All information provided by BlackRock is purely of an indicative nature and\n                             subject to change without notice at any time. The information is for guidance\n                             only and does not represent an offer, investment advice or any kind of\n                             financial service. Although BlackRock has obtained the information provided\n                             from sources that should be considered reliable, BlackRock cannot guarantee\n                             its accuracy, completeness or that it is fit for any particular purpose. The\n                             information provided does not confer any rights.\n                             Past performance is not a guide to future performance.  The value of\n                             investments and the income from them can fall as well as rise and is not\n                             guaranteed.  You may not get back the amount originally invested.  Changes\n                             in the rates of exchange between currencies may cause the value of investments\n                             to diminish or increase.  Fluctuation may be particularly marked in the case\n                             of a higher volatility fund or segregated account and the value of an\n                             investment may fall suddenly and substantially.  Levels and basis of taxation\n                             may change from time to time. iShares® and BlackRock® are registered\n                             trademarks of BlackRock, Inc., or its subsidiaries in the United States and\n                             elsewhere.\n                             © 2021 BlackRock Advisors (UK) Limited, authorised and regulated by the\n                             Financial Conduct Authority.  Registered office: 12 Throgmorton Avenue,\n                             London, EC2N 2DL.  Tel: 020 7743 3000.  Registered in England No. 00796793.\n\nThis information is provided by RNS, the news service of the London Stock Exchange. RNS is approved by the Financial Conduct Authority to act as a Primary Information Provider in the United Kingdom. Terms and conditions relating to the use and distribution of this information may apply. For further information, please contact\nrns@lseg.com (mailto:rns@lseg.com)\n or visit\n<Origin Href=\\\"Link\\\">www.rns.com</Origin> (<Origin Href=\\\"Link\\\">http://www.rns.com/</Origin>)\n.\n\nRNS may use your IP address to confirm compliance with the terms and conditions, to analyse how you engage with the information contained in this communication, and to share such analysis on an anonymised basis with others as part of our commercial services. For further information about how RNS and the London Stock Exchange use the personal data you provide us, please see our\nPrivacy Policy (<Origin Href=\\\"Link\\\">https://www.lseg.com/privacy-and-cookie-policy</Origin>)\n.   END  NAVGPUWCAUPGUPR\n\n");
			hm2.put("ID", "urn:newsml:reuters.com:20210518:nRSR9607Ya");
			hm2.put("RT", "2021-05-18T06:10:06-00:0");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "REG - iShares III BLK MAM£  - Net Asset Value(s)");
			hm2.put("CT", "2021-05-18T06:10:06-00:00");
			hm2.put("LT", "2021-05-18T07:41:25-00:00");

			hm3.put("TE",
					"For best results when printing this announcement, please click on link below:\n<Origin Href=\\\"Link\\\">http://newsfile.refinitiv.com/getnewsfile/v1/story?guid=urn:newsml:reuters.com:20210518:nRSR9608Ya&amp;default-theme=true</Origin>\n\nRNS Number : 9608Y  iShares III BLK E MAG £HGD £  18 May 2021    FUND:                       BlackRock ESG Multi-Asset Growth Portfolio UCITS ETF GBP Hedged (Acc)\n DEALING DATE:               17-May-21\n NAV PER SHARE:              Official NAV GBP 5.699912\n NUMBER OF SHARES IN ISSUE:  870,415\n CODE:                       MAGG LN\n ISIN:                       IE00BLLZQ912\n DISCLAIMER:                 All information provided by BlackRock is purely of an indicative nature and\n                             subject to change without notice at any time. The information is for guidance\n                             only and does not represent an offer, investment advice or any kind of\n                             financial service. Although BlackRock has obtained the information provided\n                             from sources that should be considered reliable, BlackRock cannot guarantee\n                             its accuracy, completeness or that it is fit for any particular purpose. The\n                             information provided does not confer any rights.\n                             Past performance is not a guide to future performance.  The value of\n                             investments and the income from them can fall as well as rise and is not\n                             guaranteed.  You may not get back the amount originally invested.  Changes\n                             in the rates of exchange between currencies may cause the value of investments\n                             to diminish or increase.  Fluctuation may be particularly marked in the case\n                             of a higher volatility fund or segregated account and the value of an\n                             investment may fall suddenly and substantially.  Levels and basis of taxation\n                             may change from time to time. iShares® and BlackRock® are registered\n                             trademarks of BlackRock, Inc., or its subsidiaries in the United States and\n                             elsewhere.\n                             © 2021 BlackRock Advisors (UK) Limited, authorised and regulated by the\n                             Financial Conduct Authority.  Registered office: 12 Throgmorton Avenue,\n                             London, EC2N 2DL.  Tel: 020 7743 3000.  Registered in England No. 00796793.\n\nThis information is provided by RNS, the news service of the London Stock Exchange. RNS is approved by the Financial Conduct Authority to act as a Primary Information Provider in the United Kingdom. Terms and conditions relating to the use and distribution of this information may apply. For further information, please contact\nrns@lseg.com (mailto:rns@lseg.com)\n or visit\n<Origin Href=\\\"Link\\\">www.rns.com</Origin> (<Origin Href=\\\"Link\\\">http://www.rns.com/</Origin>)\n.\n\nRNS may use your IP address to confirm compliance with the terms and conditions, to analyse how you engage with the information contained in this communication, and to share such analysis on an anonymised basis with others as part of our commercial services. For further information about how RNS and the London Stock Exchange use the personal data you provide us, please see our\nPrivacy Policy (<Origin Href=\\\"Link\\\">https://www.lseg.com/privacy-and-cookie-policy</Origin>)\n.   END  NAVGPUWCAUPGUPR\n\n");
			hm3.put("ID", "urn:newsml:reuters.com:20210518:nRSR9608Ya");
			hm3.put("RT", "2021-05-18T06:10:06-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "REG - iShares III BLK MAG£  - Net Asset Value(s)");
			hm3.put("CT", "2021-05-18T06:10:06-00:00");
			hm3.put("LT", "2021-05-18T07:41:25-00:00");

			hm4.put("TE",
					"The following factors could affect Italian markets on Tuesday.\n    Reuters has not verified the newspaper reports, and cannot\nvouch for their accuracy. New items are marked with (*).\n    \n    For a complete list of diary events in Italy please click on\n <Origin Href=\\\"QuoteRef\\\">IT/DIA</Origin> .\n    \n    GENERAL\n    Italy&apos;s government on Monday approved a decree pushing back\nwith immediate effect a nightly coronavirus curfew to 11 p.m.\nfrom 10 p.m. and easing other curbs in the regions where\ninfections are low.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL5N2N45AF</Origin>\n    Italy reported 140 coronavirus-related deaths on Monday\nagainst 93 the day before, the health ministry said, while the\ndaily tally of new infections fell to 3,455 from 5,753.\n <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nS8N2MD06X</Origin>\n    \n    DEBT\n    Italy&apos;s Treasury said on Monday it will kick off the planned\nrepurchase agreements (Repo) project on May 24 in a move meant\nto help it manage its liquidity and enrich cash management\ninstruments already in use.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nS8N2MD071</Origin>\n    \n    ECONOMY\n    ISTAT releases March foreign trade data (0800 GMT).\n    \n    COMPANIES\n    MEDIOBANCA  <Origin Href=\\\"QuoteRef\\\">MDBI.MI</Origin> \n    Former Italian Prime Minister Silvio Berlusconi&apos;s family has\nsold its 2% stake in Mediobanca, in the latest change affecting\nthe shareholder base of Italy&apos;s top investment bank.\n <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL5N2N45LX</Origin>\n    (*) The stake was bought by Leonardo Del Vecchio&apos;s holding\ncompany Delfin, various Italian dailies reported.\n    \n    ASSICURAZIONI GENERALI  <Origin Href=\\\"QuoteRef\\\">GASI.MI</Origin> \n    First-quarter profits at Italy&apos;s top insurer Generali beat\nanalysts&apos; expectations due to the positive contribution from the\nnon-life and asset management businesses and as the life segment\nproved resilient despite low interest rates.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL5N2N50UE</Origin>\n    (*) Generali could introduce the role of general manager\nfollowing requests to change its governance structure put\nforward by its second-largest investor Francesco Gaetano\nCaltagirone and divergences among shareholders over the renewal\nof the board, Il Sole 24 Ore reported. The paper said a chairman\nwith strong international ties could also be considered to\nfoster Generali&apos;s overseas growth.\n    The decision over whether Generali&apos;s outgoing board could\nsubmit a slate of nominees for its renewal could be taken at a\nboard meeting next month, Corriere della Sera reported.\n    Analyst call on first quarter earnings (1000 GMT)\n        \n    STELLANTIS  <Origin Href=\\\"QuoteRef\\\">STLA.MI</Origin> \n    The world&apos;s fourth largest carmaker and Foxconn said they\nplan to announce a strategic partnership on Tuesday, adding to a\nstring of such deals by the Taiwanese iPhone assembler.\n    The two companies will hold a call on Tuesday to present the\npartnership, with Stellantis CEO Carlos Tavares and Foxconn\nChairman Young Liu (0945 GMT). <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL5N2N41D9</Origin>\n    \n    MEDIASET  <Origin Href=\\\"QuoteRef\\\">MS.MI</Origin> \n    Leading French broadcaster TF1  <Origin Href=\\\"QuoteRef\\\">TFFP.PA</Origin>  will combine with\nsecond-ranked rival M6 under a deal announced on Monday by their\nrespective owners Bouygues  <Origin Href=\\\"QuoteRef\\\">BOUY.PA</Origin>  and RTL Group, a division\nof Germany&apos;s Bertelsmann  <Origin Href=\\\"QuoteRef\\\">BTGGg.F</Origin> .  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL2N2N42DK</Origin>\n        \n    SAIPEM  <Origin Href=\\\"QuoteRef\\\">SPMI.MI</Origin> \n    The Italian oil services group said on Monday it had entered\nexclusive talks with France&apos;s Naval Group for the acquisition of\nNaval Energies&apos; floating wind business.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nS8N2MD06Z</Origin>\n    \n    (*) TELECOM ITALIA  <Origin Href=\\\"QuoteRef\\\">TLIT.MI</Origin> \n    Telecoms group Iliad  <Origin Href=\\\"QuoteRef\\\">ILD.PA</Origin>  said on Tuesday it expected to\nmake a profit from its Italian activities sooner than expected,\nthanks to mobile subscriber gains and a quick network\nrollout. <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL5N2N50KU</Origin>\n    Iliad&apos;s subscribers in Italy totalled 7.54 million at the\nend of March up from 7.24 million three months earlier, lifting\nrevenues to 188 million euros from 150 million euros a year ago.\n <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nFWN2N41FD</Origin> \n    \n    CNH INDUSTRIAL  <Origin Href=\\\"QuoteRef\\\">CNHI.MI</Origin> \n    The group on Monday priced a $600 million 2026 bond at\n99.208.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nFWN2N41GZ</Origin>\n    \n    (*) INTESA SANPAOLO  <Origin Href=\\\"QuoteRef\\\">ISP.MI</Origin>  \n    The Italian banking group&apos;s asset management unit Eurizon\nreported a 60% increase in net income for the first quarter to\n161 million euros. CEO Saverio Perissinotto told Il Sole 24 Ore\ndaily that Eurizon is fully focused on completing the\nintegration of Pramerica - a joint-venture UBI, now part of\nIntesa, had with Prudential Financial  <Origin Href=\\\"QuoteRef\\\">PRU.N</Origin>  - in the second\nhalf of the year.\n    \n    CREDEM  <Origin Href=\\\"QuoteRef\\\">EMBI.MI</Origin> \n    The lender said on Monday it had set an exchange ratio for\nthe acquisition of smaller rival CR Cento at 0.64 of its shares\nfor each CR Cento share.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nFWN2N40QG</Origin>\n    \n    GUALA CLOSURES  <Origin Href=\\\"QuoteRef\\\">GCL.MI</Origin> \n    Investindustrial starts takeover bid on Guala Closures\nshares; ends on June 7.\n    The board of the Italian bottle cap maker on Monday said the\n8.20 euro per share price offered in Investindustrial&apos;s takeover\nbid was fair.\n    \n    DIARY\n    Paris, Italy&apos;s PM Draghi expected to attend Africa summit.\n    Brescia, Italian President Sergio Mattarella attends\n&apos;Brescia University&apos; new academic year inauguration ceremony\n(0900 GMT).\n    Rome, Industry Minister Giancarlo Giorgetti attends news\nconference to present the annual catering report (0900 GMT).\n    Online presentation &quot;The future vision of Lamborghini\nAutomobiles&quot; with Chairman and CEO Stephan Winkelmann (0900\nGMT).\n    Online event on &quot;Protagonists. Governance and women&apos;s\nempowerment&quot; with Leonardo  <Origin Href=\\\"QuoteRef\\\">LDOF.MI</Origin>  CEO Alessandro Profumo\n(1000 GMT).    \n    Rome, welfare institute INPS President Pasquale Tridico\nspeaks via videoconference before Senate Education Committee on\nlaw decree &apos;Status of arts and culture workers&apos; (1100 GMT).\n    Rome, Italian Medicines Agency (AIFA) Director General\nNicola Magrini and head of Italy&apos;s Higher Health Council (CTS)\nFranco Locatelli informally speak via videoconference before\nSenate Health Committee on method of administration of COVID\nm-RNA vaccines (1100 GMT).\n    Rome, Italian Medicines Agency (AIFA) President Giorgio Palu\ninformally speaks via videoconference before Senate Health\nCommittee on method of administration of COVID m-RNA vaccines\n(1300 GMT). \n    Industry Minister Giancarlo Giorgetti attends via\nvideoconference Healthcare policy presentation (1600 GMT).\n    \n    \n    ((Milan newsroom, +39 02 6612 9507, fax +39 02 801149, \nmilan.newsroom@news.reuters.com))\n    \n    For Italian market data and news, click on codes in \nbrackets:\n\n    20 biggest gainers (in percentage)............ <Origin Href=\\\"QuoteRef\\\">.PG.MI</Origin> \n    20 biggest losers (in percentage)............. <Origin Href=\\\"QuoteRef\\\">.PL.MI</Origin> \n    FTSE IT allshare index  <Origin Href=\\\"QuoteRef\\\">.FTITLMS</Origin> \n    FTSE Mib index........  <Origin Href=\\\"QuoteRef\\\">.FTMIB</Origin> \n    FTSE Allstars index...  <Origin Href=\\\"QuoteRef\\\">.FTSTAR</Origin> \n    FTSE Mid Cap index....  <Origin Href=\\\"QuoteRef\\\">.FTITMC</Origin> \n    Block trades..........  <Origin Href=\\\"QuoteRef\\\">.BLK.MI</Origin> \n    Stories on Italy......  <Origin Href=\\\"QuoteRef\\\">IT-LEN</Origin> \n    \n    For pan-European market data and news, click on codes in \n brackets:\n  European Equities speed guide................... <Origin Href=\\\"QuoteRef\\\">EUR/EQUITY</Origin> \n  FTSEurofirst 300 index.............................. <Origin Href=\\\"QuoteRef\\\">.FTEU3</Origin> \n  DJ STOXX index...................................... <Origin Href=\\\"QuoteRef\\\">.STOXX</Origin> \n  Top 10 STOXX sectors........................... <Origin Href=\\\"QuoteRef\\\">.PGL.STOXXS</Origin> \n  Top 10 EUROSTOXX sectors...................... <Origin Href=\\\"QuoteRef\\\">.PGL.STOXXES</Origin> \n  Top 10 Eurofirst 300 sectors................... <Origin Href=\\\"QuoteRef\\\">.PGL.FTEU3S</Origin> \n  Top 25 European pct gainers....................... <Origin Href=\\\"QuoteRef\\\">.PG.PEUR</Origin> \n  Top 25 European pct losers........................ <Origin Href=\\\"QuoteRef\\\">.PL.PEUR</Origin> \n \n  Main stock markets:\n  Dow Jones............... <Origin Href=\\\"QuoteRef\\\">.DJI</Origin>   Wall Street report ..... <Origin Href=\\\"NewsSearch\\\">.N</Origin> \n  Nikkei 225............. <Origin Href=\\\"QuoteRef\\\">.N225</Origin>   Tokyo report............ <Origin Href=\\\"NewsSearch\\\">.T</Origin> \n  FTSE 100............... <Origin Href=\\\"QuoteRef\\\">.FTSE</Origin>   London report........... <Origin Href=\\\"NewsSearch\\\">.L</Origin> \n  Xetra DAX............. <Origin Href=\\\"QuoteRef\\\">.GDAXI</Origin>   Frankfurt market stories <Origin Href=\\\"NewsSearch\\\">.F</Origin> \n  CAC-40................. <Origin Href=\\\"QuoteRef\\\">.FCHI</Origin>   Paris market stories... <Origin Href=\\\"NewsSearch\\\">.PA</Origin> \n  World Indices..................................... <Origin Href=\\\"QuoteRef\\\">0#.INDEX</Origin> \n  Reuters survey of world bourse outlook......... <Origin Href=\\\"QuoteRef\\\">EQUITYPOLL1</Origin> \n  Western European IPO diary.......................... <Origin Href=\\\"QuoteRef\\\">WEUIPO</Origin> \n  European Asset Allocation........................ <Origin Href=\\\"NewsSearch\\\">EUR/ASSET</Origin> \n  Reuters News at a Glance: Equities............... <Origin Href=\\\"NewsSearch\\\">TOP/EQE</Origin> \n  Main currency report:............................... <Origin Href=\\\"NewsSearch\\\">FRX/</Origin> \n");
			hm4.put("ID", "urn:newsml:reuters.com:20210518:nL5N2N43A7");
			hm4.put("RT", "2021-05-18T06:17:46-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Italy - Factors to watch on May 18");
			hm4.put("CT", "2021-05-18T04:00:00-00:00");
			hm4.put("LT", "2021-05-18T06:17:48-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("FIDELIT.LG")) {

			hm1.put("TE",
					"March 15 (Reuters) - Fidelity Bank PLC  <Origin Href=\\\"QuoteRef\\\">FIDELIT.LG</Origin> :\n    * NOTIFICATION OF LISTING OF 41.21 BILLION NAIRA 8.5% FIXED\nRATE\nUNSECURED SUBORDINATED BONDS DUE 2031 ON FMDQ SECURITIES\nEXCHANGE\n\nSource text for Eikon: [ID: <Origin Href=\\\"Link\\\">https://bit.ly/3rUjaZW]</Origin>\nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">FIDELIT.LG</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
			hm1.put("ID", "urn:newsml:reuters.com:20210315:nFWN2LD0VX");
			hm1.put("RT", "2021-03-15T15:17:41-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT",
					"BRIEF-Fidelity Bank Lists 41.21 Bln Naira 8.5% Fixed Rate Unsecured Subordinated Bonds Due 2031");
			hm1.put("CT", "2021-03-15T15:17:41-00:00");
			hm1.put("LT", "2021-03-15T15:17:41-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("BA.N") || isinCode.trim().equalsIgnoreCase("BA.NQ")) {

			hm1.put("TE",
					"By Patricia Zengerle\n    WASHINGTON, May 17 (Reuters) - President Joe Biden&apos;s\nadministration approved the potential sale of $735 million in\nprecision-guided weapons to Israel, and congressional sources\nsaid on Monday that U.S. lawmakers were not expected to object\nto the deal despite violence between Israel and Palestinian\nmilitants.\n    Three congressional aides said Congress was officially\nnotified of the intended commercial sale on May 5, as part of\nthe regular review process before major foreign weapons sales\nagreements can go ahead. \n    The sale was first reported by the Washington Post.\n    Congress was informed of the planned sale in April, as part\nof the normal informal review process before of the formal\nnotification on May 5. Under U.S. law, the formal notification\nopens up a 15-day window for Congress to object to the sale,\nwhich is not expected despite the ongoing violence.\n    The sale of Joint Direct Attack Munitions, or JDAMs, made by\nBoeing Co  <Origin Href=\\\"QuoteRef\\\">BA.N</Origin> , was considered routine at the time, before the\nstart last week of the fiercest hostilities in the region in\nyears.\n    There were no objections at the time by the Democratic and\nRepublican leaders of the congressional foreign affairs\ncommittees that review such sales, aides said.\n    Asked for comment, a State Department spokesperson noted\nthat the department is restricted under federal law and\nregulations from publicly commenting on or confirming details of\nlicensing activity related to direct commercial sales like the\nJDAMs agreement.\n    &quot;We remain deeply concerned about the current violence and\nare working towards achieving a sustainable calm,&quot; the\nspokesperson said.\n    Strong support for Israel is a core value for both\nDemocratic and Republican members of the U.S. Congress, despite\ncalls from a few of the most progressive Democrats to take a\ntougher stance against the government of Israeli President\nBenjamin Netanyahu.\n    U.S. law allows Congress to object to weapons sales, but it\nis unlikely to do so in this case. Because Israel is among a\nhandful of countries whose military deals are approved under an\nexpedited process, the typical window for objecting will close\nbefore lawmakers can pass a resolution of disapproval, even if\nthey were inclined to.\n\n (Reporting by Patricia Zengerle; editing by Grant McCool)\n ((patricia.zengerle@thomsonreuters.com,\n<Origin Href=\\\"Link\\\">www.twitter.com/ReutersZengerle;</Origin> 001-202-898-8390;))");
			hm1.put("ID", "urn:newsml:reuters.com:20210517:nL2N2N41GJ");
			hm1.put("RT", "2021-05-17T16:26:24-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "Biden administration approved $735 million arms sale to Israel - sources");
			hm1.put("CT", "2021-05-17T16:26:24-00:00");
			hm1.put("LT", "2021-05-17T16:26:25-00:00");

			hm2.put("TE",
					"(Adds details, quotes)\n    DUBAI, May 17 (Reuters) - Emirates could swap some of its\norder for 126 Boeing  <Origin Href=\\\"QuoteRef\\\">BA.N</Origin>  777X jets for smaller 787\nDreamliners as part of a sweeping review of its future fleet\nrequirements, its chairman said on Monday.\n    The airline is currently in talks with the U.S. planemaker\nover its fleet planes, a review brought on by the coronavirus\npandemic, which has devastated the travel industry.\n    Asked if the airline could swap its orders to take fewer\n777X jets and more Dreamliners, Sheikh Ahmed bin Saeed Al\nMaktoum told reporters: &quot;It is always a possibility.&quot;\n    &quot;We are assessing our fleet requirements as we speak.&quot;\n    Emirates, the world&apos;s biggest long-haul airline before the\npandemic, has recently expressed frustration with Boeing over\nthe 777X programme, which is three years behind schedule, urging\nthe planemaker to share more details on the in-production jet.\n    Sheikh Ahmed said the delays had been &quot;tough&quot; for Boeing,\nwhich is emerging from its worst crisis after fatal crashes of\n737 MAX jets and a 20-month safety ban that has since been\nlifted.\n    Emirates reduced its order for 150 777X to 126 jets as part\nof a deal that saw the airline order 30 Dreamliners in 2019.\n    Sheikh Ahmed, who has headed Emirates since it was founded\nin 1985, did not say when the airline would make a decision on\nit future fleet.\n    The airline is due to report its annual results for the\nfinancial year ended March 31. \n    Sheikh Ahmed said it had been a very tough year, with the\nairline carrying around 30% of the 56.2 million passengers it\ncarried in the previous year, without providing further details.\n    He said he was optimistic for the upcoming summer travel\nseason, even as the airline was reviewing its cash reserves on a\nmonthly basis due to the deterioration in demand caused by the\npandemic.\n    &quot;A lot of people (who have) stopped travelling for the last\nyear and a half ... want to travel.&quot;\n    However, Sheikh Ahmed suggested the airline was taking a\nconservative approach to restoring capacity, telling reporters\nEmirates would only operate flights that make commercial sense.\n    &quot;We don&apos;t just open a route for the sake of opening or just\nfor a publicity reason.&quot;\n\n (Reporting by Alexander Cornwell; editing by Jason Neely and\nSteve Orlofsky)\n ((Alexander.Cornwell@thomsonreuters.com;))");
			hm2.put("ID", "urn:newsml:reuters.com:20210517:nL2N2N40YK");
			hm2.put("RT", "2021-05-17T13:20:17-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "UPDATE 2-Emirates could swap Boeing 777X jets for smaller Dreamliners, chairman says");
			hm2.put("CT", "2021-05-17T13:05:44-00:00");
			hm2.put("LT", "2021-05-17T13:20:17-00:00");

			hm3.put("TE",
					"DUBAI, May 17 (Reuters) - Emirates could swap some of its\norder for 126 Boeing  <Origin Href=\\\"QuoteRef\\\">BA.N</Origin>  777X jets for smaller 787\nDreamliners as part of a sweeping review of its future fleet\nrequirements, its chairman said on Monday.\n    &quot;We are assessing our fleet requirement as we speak,&quot; Sheikh\nAhmed bin Saeed al-Maktoum told reporters in Dubai.\n\n (Reporting by Alexander Cornwell; editing by Jason Neely)\n ((Alexander.Cornwell@thomsonreuters.com;))");
			hm3.put("ID", "urn:newsml:reuters.com:20210517:nD5N2MD001");
			hm3.put("RT", "2021-05-17T12:22:27-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "Emirates could swap Boeing 777x for smaller Dreamliners, chairman says");
			hm3.put("CT", "2021-05-17T12:02:21-00:00");
			hm3.put("LT", "2021-05-17T12:22:28-00:00");

			hm4.put("TE",
					"(Adds background, CEO quotes; details on possible MAX 10 order)\n    By Conor Humphries\n    DUBLIN, May 17 (Reuters) - Ryanair  <Origin Href=\\\"QuoteRef\\\">RYA.I</Origin>  fears it may not\ntake delivery of its first 737 MAX aircraft until after its peak\nsummer period and the Irish airline is &quot;quite upset with Boeing&quot;\n <Origin Href=\\\"QuoteRef\\\">BA.N</Origin> , Group Chief Executive Michael O&apos;Leary said on Monday. \n    But he said he believed the production issues would resolve\nin the medium term and said the Irish airline, Europe&apos;s largest\nlow-cost carrier, was in talks with Boeing about a significant\norder of the larger, 230-seat, MAX 10 aircraft. \n    The largest European customer of the MAX with 210 firm\norders of the 197-seat MAX200 model, Ryanair in late March said\nit expected that before summer it would take delivery of 16 of\nthe aircraft, down from an earlier forecast of 40. \n    But Boeing is now promising the first delivery of the jet,\nwhich has been delayed in part due to a recent electrical\ngrounding issue, in late May.\n    &quot;We are now being told the first delivery will be in late\nMay. I am not sure we necessarily believe that,&quot; O&apos;Leary said in\na pre-recorded presentation following the release of the\ncompany&apos;s full-year results. \n    &quot;As the management team in Seattle continues to mismanage\nthat process I think there is a real risk we might not see any\nof these aircraft in advance of summer 2021,&quot; O&apos;Leary said. \n    Ryanair was initially due to take delivery of its first MAX\ntwo years ago before the jet was grounded for 20 months after\ntwo fatal crashes. \n    The airline has agreed &quot;reasonable and fair&quot; compensation\nfor that delay, it said in its results statement on Monday. \n    Ryanair is confident it will have 60 of the aircraft in\nplace for the summer of 2022, he said. \n    Ryanair remains in talks with Boeing for a significant order\nof the larger MAX 10 aircraft, but &quot;we are not quite there on\nprice yet,&quot; Chief Financial Officer Neil Sorahan said in an\ninterview.\n    Asked how big such a deal would be, Sorahan said it would\ncover both fleet renewal and growth in the 2026 to 2030 period. \n    &quot;We don&apos;t tend to do small deals,&quot; he said.  \n\n (Reporting by Conor Humphries; editing by Christopher Cushing\nand Jason Neely)\n ((conor.humphries@thomsonreuters.com; +353 1 236 1915;))");
			hm4.put("ID", "urn:newsml:reuters.com:20210517:nL2N2N40A4");
			hm4.put("RT", "2021-05-17T06:32:58-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "UPDATE 1-Ryanair &apos;upset&apos; with Boeing, fears no MAX deliveries before summer");
			hm4.put("CT", "2021-05-17T06:32:58-00:00");
			hm4.put("LT", "2021-05-17T06:32:59-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("AXP.N") || isinCode.trim().equalsIgnoreCase("FEURUSD")
				|| isinCode.trim().equalsIgnoreCase("FGBPUSD")) {

			hm1.put("TE",
					"May 17 (Reuters) - American Express Co  <Origin Href=\\\"QuoteRef\\\">AXP.N</Origin> :\n    * AMERICAN EXPRESS- USCS CARD MEMBER LOANS 30 DAYS PAST DUE\nLOANS\nAS A % OF TOTAL 0.8% AT APRIL END VERSUS 0.9% AT MARCH END\n    * AMERICAN EXPRESS- USCS CARD MEMBER LOANS NET WRITE-OFF\nRATE-PRINCIPAL ONLY 1.0% AT APRIL END VERSUS 1.1% AT MARCH END\n    * AMERICAN EXPRESS-U.S. SMALL BUSINESS CARD MEMBER LOANS 30\nDAYS\nPAST DUE LOANS AS A% OF TOTAL 0.5% AT APRIL END VERSUS 0.6% AT\nMARCH END\n    * AMERICAN EXPRESS- U.S. SMALL BUSINESS CARD MEMBER LOANS\nNET\nWRITE-OFF RATE - PRINCIPAL ONLY 0.7% AT APRIL END VERSUS 0.7% AT\nMARCH END\n\nSource text : (<Origin Href=\\\"Link\\\">https://bit.ly/3tTvzxA</Origin>)\nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">AXP.N</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
			hm1.put("ID", "urn:newsml:reuters.com:20210517:nFWN2N40WN");
			hm1.put("RT", "2021-05-17T16:05:51-00:00");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-American Express Reports Card Member Loan Stats for April");
			hm1.put("CT", "2021-05-17T16:05:51-00:00");
			hm1.put("LT", "2021-05-17T16:05:51-00:00");

			hm2.put("TE",
					"Stocks surge on strong outlook for earnings, economy\n    * Honeywell down on aerospace unit revenue miss \n    * Pinterest up as Credit Suisse raises price target\n    * Indexes close up: Dow 0.7%, S&amp;P 500 1.1%, Nasdaq 1.4%\n\n (Adds closing prices after 4 p.m. market close)\n    By Herbert Lash\n    April 23 (Reuters) - U.S. stocks rallied on Friday, driving\nthe S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  to a near-record closing high, after factory\ndata and new home sales underscored a booming economy while\nmegacap stocks rose in anticipation of strong earnings reports\nnext week.\n    The bounceback follows a sell-off on Thursday when reports\nthat U.S. President Joe Biden plans to almost double the capital\ngains tax spooked investors. Analysts dismissed the slide as a\nknee-jerk reaction and pointed to the strong outlook.\n    As the three major Wall Street indexes surged, the CBOE\nmarket volatility or &quot;fear&quot; index  <Origin Href=\\\"QuoteRef\\\">.VIX</Origin>  plunged almost 10% in a\nsign of tumbling investor anxiety about the risks ahead.\n    Companies are providing guidance after staying quiet during\nthe pandemic, while lower bond yields and results that beat\nestimates are driving the rally, said Tim Ghriskey, chief\ninvestment strategist at Inverness Counsel in New York.\n    &quot;There is a lot of anticipation of what&apos;s to come,&quot; he said.\n&quot;We&apos;ve seen actual reports beating these very high expectations.\nYields have come back down, which is very positive for tech.&quot;\n    Earnings take center stage next week when 40% of the S&amp;P\n500&apos;s market cap report on Tuesday through Thursday, including\nthe tech and related heavyweights of Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin> ,\nGoogle parent Alphabet Inc  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin> , Apple Inc  <Origin Href=\\\"QuoteRef\\\">AAPL.O</Origin>  and\nFacebook Inc  <Origin Href=\\\"QuoteRef\\\">FB.O</Origin> .\n    Those names, including Amazon.com Inc  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin> , supplied the\nbiggest upside to a broad-based rally in which advancing shares\neasily outpaced decliners.\n    Expectations for company results have steadily gained in\nrecent weeks as opposed to a typical decline as earnings season\napproaches. First-quarter earnings are expected to jump 33.9%\nfrom a year ago, the highest quarterly rate since the fourth\nquarter of 2010, according to IBES Refinitiv data.\n    U.S. factory activity powered ahead in early April. IHS\nMarkit&apos;s flash U.S. manufacturing PMI increased to 60.6 in the\nfirst half of this month, the highest reading since the series\nstarted in May 2007.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG17D</Origin>\n    In another sign of strong consumer demand, sales of new U.S.\nsingle-family homes rebounded more than expected in March,\nlikely boosted by an acute shortage of previously owned houses\non the market.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG0ZD</Origin>\n    All the 11 major S&amp;P 500 sectors were higher, with\ntechnology  <Origin Href=\\\"QuoteRef\\\">.SPLRCT</Origin>  and financials  <Origin Href=\\\"QuoteRef\\\">.SPSY</Origin>  leading gains.\n    Ron Temple, head of U.S. equity at Lazard Asset Management,\nsaid the U.S. economy is about to post the strongest growth in\n50 years, with more than 6% gains both this year and next.\n    The Federal Reserve will allow the economy to run hotter\nthan in the past, adding to the high-growth outlook.\n    &quot;Investors are gradually coming around to the sheer\nmagnitude of excess savings, pent-up demand and the implications\nof such a massive wave of fiscal stimulus,&quot; Temple said.\n    Stocks surged just before the bell, with the benchmark S&amp;P\n500 falling a bit to miss setting a record close.\n    The Dow Jones Industrial Average  <Origin Href=\\\"QuoteRef\\\">.DJI</Origin>  rose 0.67% to\n34,043.49 and the S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  gained 1.09% at 4,180.17, just\nbelow its previous closing high of 4,185.47 on April 16. The\nNasdaq Composite  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  added 1.44% at 14,016.81.\n    For the week, the S&amp;P 500 unofficially fell 0.13%, the Dow\nabout 0.46% and the Nasdaq 0.25%.\n    Some earnings reports on Friday were lackluster, with\nAmerican Express Co  <Origin Href=\\\"QuoteRef\\\">AXP.N</Origin>  sliding 1.9% after reporting a slump\nin credit spending and lower quarterly revenue.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG30U</Origin>\n    Honeywell International  <Origin Href=\\\"QuoteRef\\\">HON.N</Origin>  fell 2.1% after missing\nrevenue expectations in aerospace, its biggest business segment.\n    Naked Brand Group  <Origin Href=\\\"QuoteRef\\\">NAKD.O</Origin>  jumped 4.8% after shareholders\napproved the proposed divestiture of the company&apos;s Bendon\nbrick-and-mortar operations.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG36T</Origin>\n    Image sharing company Pinterest Inc  <Origin Href=\\\"QuoteRef\\\">PINS.N</Origin>  gained 4.2% as\nCredit Suisse raised its price target, saying newer product\nofferings and expanding footprint in markets abroad will yield\nhigher revenue and user growth.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG32O</Origin>\n    Advancing issues outnumbered declining ones on the NYSE by a\n3.62-to-1 ratio; on Nasdaq, a 2.82-to-1 ratio favored advancers.\n    The S&amp;P 500 posted 81 new 52-week highs and no new lows; the\nNasdaq Composite recorded 111 new highs and 20 new lows.  \n\n (Reporting by Herbert Lash; Additional reporting by Shivani\nKumaresan and Shreyashi Sanyal in Bengaluru; Editing by Sriraj\nKalluvila, Arun Koyyur and Richard Chang)\n ((herb.lash@thomsonreuters.com; 1-646-223-6019; Reuters\nMessaging: herb.lash.reuters.com@reuters.net))");
			hm2.put("ID", "urn:newsml:reuters.com:20210423:nL1N2MG2F8");
			hm2.put("RT", "2021-04-23T20:27:40-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT", "US STOCKS-Wall Street rallies on strong economic data; tech in focus");
			hm2.put("CT", "2021-04-23T20:27:40-00:00");
			hm2.put("LT", "2021-04-23T20:27:42-00:00");

			hm3.put("TE",
					"Stocks surge on strong outlook for earnings, economy\n    * Honeywell down on aerospace unit revenue miss \n    * Pinterest up as Credit Suisse raises price target\n\n (Adds market close at 4 p.m.)\n    By Herbert Lash\n    April 23 (Reuters) - U.S. stocks rallied on Friday, driving\nthe S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  to a near-record closing high, after factory\ndata and new home sales underscored a booming economy while big\ntech stocks rose in anticipation of strong earnings reports next\nweek.\n    The bounceback follows a sell-off on Thursday when reports\nthat U.S. President Joe Biden plans to almost double the capital\ngains tax spooked investors. Analysts dismissed the slide as a\nknee-jerk reaction and pointed to the strong outlook.\n    As the three major Wall Street indexes surged, the CBOE\nmarket volatility or &quot;fear&quot; index  <Origin Href=\\\"QuoteRef\\\">.VIX</Origin>  plunged about 10% in a\nsign of tumbling investor anxiety about the risks ahead.\n    Companies are providing guidance after staying quiet during\nthe pandemic, while lower bond yields and results that beat\nestimates are driving the rally, said Tim Ghriskey, chief\ninvestment strategist at Inverness Counsel in New York.\n    &quot;There is a lot of anticipation of what&apos;s to come,&quot; he said.\n&quot;We&apos;ve seen actual reports beating these very high expectations.\nYields have come back down, which is very positive for tech.&quot;\n    Earnings take center stage next week when 40% of the S&amp;P\n500&apos;s market cap report on Tuesday through Thursday, including\nthe tech heavyweights of Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin> , Google parent\nAlphabet Inc  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin> , Apple Inc  <Origin Href=\\\"QuoteRef\\\">AAPL.O</Origin>  and Facebook Inc\n <Origin Href=\\\"QuoteRef\\\">FB.O</Origin> .\n    Those names, including Amazon.com Inc  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin> , supplied the\nbiggest upside to a rally in which advancing shares easily\noutpaced decliners.\n    First-quarter earnings are expected to jump 33.9% from a\nyear ago, the highest quarterly rate since the fourth quarter of\n2010, according to IBES Refinitiv data.\n    U.S. factory activity powered ahead in early April. IHS\nMarkit&apos;s flash U.S. manufacturing PMI increased to 60.6 in the\nfirst half of this month, the highest reading since the series\nstarted in May 2007.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG17D</Origin>\n    In another sign of strong consumer demand, sales of new U.S.\nsingle-family homes rebounded more than expected in March,\nlikely boosted by an acute shortage of previously owned houses\non the market.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG0ZD</Origin>\n    All the 11 major S&amp;P 500 sectors were higher, with\ntechnology  <Origin Href=\\\"QuoteRef\\\">.SPLRCT</Origin>  and financials  <Origin Href=\\\"QuoteRef\\\">.SPSY</Origin>  leading gains.\n    Ron Temple, head of U.S. equity at Lazard Asset Management,\nsaid the U.S. economy is about to post the strongest growth in\n50 years, with more than 6% gains both this year and next.\n    The Federal Reserve will allow the economy to run hotter\nthan in the past, adding to the high-growth outlook.\n    &quot;Investors are gradually coming around to the sheer\nmagnitude of excess savings, pent-up demand and the implications\nof such a massive wave of fiscal stimulus,&quot; Temple said.\n    Unofficially, the Dow Jones Industrial Average  <Origin Href=\\\"QuoteRef\\\">.DJI</Origin>  rose\n0.66% to 34,040.28 and the S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  gained 1.08% to\n4,179.73. The Nasdaq Composite  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  added 1.43% to 14,016.06.\n    Some earnings reports on Friday were lackluster, with\nAmerican Express Co  <Origin Href=\\\"QuoteRef\\\">AXP.N</Origin>  sliding after reporting a slump in\ncredit spending and lower quarterly revenue.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG30U</Origin>\n    Honeywell International  <Origin Href=\\\"QuoteRef\\\">HON.N</Origin>  fell after missing revenue\nexpectations in aerospace, its biggest business segment.\n    Naked Brand Group  <Origin Href=\\\"QuoteRef\\\">NAKD.O</Origin>  jumped after shareholders\napproved the proposed divestiture of the company&apos;s Bendon\nbrick-and-mortar operations.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG36T</Origin>\n    Image sharing company Pinterest Inc  <Origin Href=\\\"QuoteRef\\\">PINS.N</Origin>  gained as\nCredit Suisse raised its price target, saying newer product\nofferings and expanding footprint in markets abroad will yield\nhigher revenue and user growth.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG32O</Origin>\n\n (Reporting by Herbert Lash; Additional reporting by Shivani\nKumaresan and Shreyashi Sanyal in Bengaluru; Editing by Sriraj\nKalluvila, Arun Koyyur and Richard Chang)\n ((herb.lash@thomsonreuters.com; 1-646-223-6019; Reuters\nMessaging: herb.lash.reuters.com@reuters.net))");
			hm3.put("ID", "urn:newsml:reuters.com:20210423:nL1N2MG22Q");
			hm3.put("RT", "2021-04-23T20:00:45-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "US STOCKS-Wall Street rallies on strong economic data; tech in focus");
			hm3.put("CT", "2021-04-23T20:00:45-00:00");
			hm3.put("LT", "2021-04-23T20:00:47-00:00");

			hm4.put("TE",
					"Stocks gain on strong outlook for earnings, economy\n    * Honeywell down on aerospace unit revenue miss \n    * Pinterest up as Credit Suisse raises price target\n    * Indexes up: Dow 0.7%, S&amp;P 500 1.2%, Nasdaq 1.6%\n\n (Adds mid-afternoon prices)\n    By Herbert Lash\n    April 23 (Reuters) - U.S. stocks rose in a broad rally on\nFriday as increased factory output and housing data supported\nexpectations of a swift economic recovery，while big tech stocks\nrose in anticipation of strong earnings reports next week.\n    The bounceback follows a sell-off on Thursday when reports\nthat U.S. President Joe Biden plans to almost double the capital\ngains tax spooked some investors. Analysts dismissed the\nsell-off as a knee-jerk reaction, saying equities are poised for\nnew highs.\n    The broad-based S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  rose more than 1%, trading\njust below what would be a new record close, while the\ntech-heavy Nasdaq  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  scored a bigger percentage gain.\n    Earnings take center stage next week when 40% of the S&amp;P\n500&apos;s market cap report on Tuesday through Thursday, including\nthe tech heavyweights of Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin> , Google parent\nAlphabet Inc  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin> , Apple Inc  <Origin Href=\\\"QuoteRef\\\">AAPL.O</Origin>  and Facebook Inc\n <Origin Href=\\\"QuoteRef\\\">FB.O</Origin> .\n    Those names, including Amazon.com Inc  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin> , supplied the\nbiggest upside to a rally where advancing shares easily outpaced\ndecliners.\n    Companies are providing guidance after staying quiet during\nthe pandemic, while lower bond yields and results that beat\nestimates are driving the rally, said Tim Ghriskey, chief\ninvestment strategist at Inverness Counsel in New York.\n    &quot;There is a lot of anticipation of what&apos;s to come,&quot; he said.\n&quot;We&apos;ve seen actual reports beating these very high expectations.\nYields have come back down, which is very positive for tech.&quot;\n    First-quarter earnings are expected to increase 33.9% from a\nyear ago, the highest quarterly rate since the fourth quarter of\n2010, according to IBES Refinitiv data.\n    U.S. factory activity powered ahead in early April. IHS\nMarkit&apos;s flash U.S. manufacturing PMI increased to 60.6 in the\nfirst half of this month, the highest reading since the series\nstarted in May 2007.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG17D</Origin>\n    In another sign of strong consumer demand, sales of new U.S.\nsingle-family homes rebounded more than expected in March,\nlikely boosted by an acute shortage of previously owned houses\non the market.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG0ZD</Origin>\n    All the 11 major S&amp;P 500 sectors were higher, with\ntechnology  <Origin Href=\\\"QuoteRef\\\">.SPLRCT</Origin>  and financials  <Origin Href=\\\"QuoteRef\\\">.SPSY</Origin>  leading gains.\n    Ron Temple, head of U.S. equity at Lazard Asset Management,\nsaid the U.S. economy is about to post the strongest growth in\nthe past 50 years, with more than 6% gains both this year and\nnext. The Federal Reserve, meanwhile, will allow the economy to\nrun hotter than in the past, adding to the high-growth outlook.\n    &quot;Investors are gradually coming around to the sheer\nmagnitude of excess savings, pent-up demand and the implications\nof such a massive wave of fiscal stimulus,&quot; Temple said.\n    The S&amp;P 500  <Origin Href=\\\"QuoteRef\\\">.SPX</Origin>  gained 1.16% at 4,182.88, and the Dow\nJones Industrial Average  <Origin Href=\\\"QuoteRef\\\">.DJI</Origin>  rose 0.69% to 34,047.78. The\nNasdaq Composite  <Origin Href=\\\"QuoteRef\\\">.IXIC</Origin>  added 1.56% at 14,034.01. \n    Earnings reports in the day were lackluster, with American\nExpress Co  <Origin Href=\\\"QuoteRef\\\">AXP.N</Origin>  sliding 2.1% after reporting a slump in\ncredit spending and lower quarterly revenue.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG30U</Origin>\n    Honeywell International  <Origin Href=\\\"QuoteRef\\\">HON.N</Origin>  fell 2.0% after it missed\nrevenue expectations for its aerospace division, its biggest\nbusiness segment.\n    Naked Brand Group  <Origin Href=\\\"QuoteRef\\\">NAKD.O</Origin> , jumped 5.9% after shareholders\napproved the proposed divestiture of the company&apos;s Bendon\nbrick-and-mortar operations.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG36T</Origin>\n    Image sharing company Pinterest Inc  <Origin Href=\\\"QuoteRef\\\">PINS.N</Origin>  gained 2.6% as\nCredit Suisse raised its price target, saying its newer product\nofferings and expanding footprint in markets abroad will yield\nhigher revenue and user growth.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL4N2MG32O</Origin>\n    Advancing issues outnumbered declining ones on the NYSE by a\n3.65-to-1 ratio; on Nasdaq, a 2.65-to-1 ratio favored advancers.\n    The S&amp;P 500 posted 71 new 52-week highs and no new lows; the\nNasdaq Composite recorded 94 new highs and 20 new lows.  \n\n (Reporting by Herbert Lash; Additional reporting by Shivani\nKumaresan and Shreyashi Sanyal in Bengaluru; Editing by Sriraj\nKalluvila, Arun Koyyur and Richard Chang)\n ((herb.lash@thomsonreuters.com; 1-646-223-6019; Reuters\nMessaging: herb.lash.reuters.com@reuters.net))");
			hm4.put("ID", "urn:newsml:reuters.com:20210423:nL4N2MG3KY");
			hm4.put("RT", "2021-04-23T19:06:44-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "US STOCKS-Wall Street rallies on strong economic data; tech in focus");
			hm4.put("CT", "2021-04-23T19:06:44-00:00");
			hm4.put("LT", "2021-04-23T19:06:46-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		} else if (isinCode.trim().equalsIgnoreCase("CSCO.O") || isinCode.trim().equalsIgnoreCase("CSCO.OQ")) {

			hm1.put("TE",
					"May 14 (Reuters) - Cisco Systems Inc  <Origin Href=\\\"QuoteRef\\\">CSCO.O</Origin> :\n    * CISCO - ON MAY 13, 2021, ENTERED SECOND AMENDED AND\nRESTATED\nCREDIT AGREEMENT\n    * CISCO - CREDIT AGREEMENT PROVIDES FOR A FIVE YEAR $3.0\nBILLION\nUNSECURED REVOLVING CREDIT FACILITY\n    * CISCO - CREDIT AGREEMENT IS AN AMENDMENT AND RESTATEMENT\nOF THAT\nCERTAIN AMENDED AND RESTATED CREDIT AGREEMENT, DATED AS OF MAY\n15, 2020\n\nSource: (<Origin Href=\\\"Link\\\">https://bit.ly/3y7xlhX</Origin>)\nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">CSCO.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
			hm1.put("ID", "urn:newsml:reuters.com:20210514:nFWN2N10X4");
			hm1.put("RT", "2021-05-14T21:47:16-00:0");
			hm1.put("PR", "reuters.com");
			hm1.put("HT", "BRIEF-Cisco Says On May 13 Entered Second Amended And Restated Credit Agreement");
			hm1.put("CT", "2021-05-14T21:47:16-00:00");
			hm1.put("LT", "2021-05-14T21:47:17-00:00");

			hm2.put("TE",
					"May 14 (Reuters) - Cisco Systems Inc  <Origin Href=\\\"QuoteRef\\\">CSCO.O</Origin> :\n    * CISCO ANNOUNCES INTENT TO ACQUIRE KENNA SECURITY TO\nDELIVER\nINDUSTRY LEADING VULNERABILITY MANAGEMENT\n    * CISCO - ACQUISITION IS EXPECTED TO CLOSE IN CISCO&apos;S Q4 OF\nFISCAL\n2021\n\nSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:nPn7kMh3sa</Origin> \nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">CSCO.O</Origin> \n\n ((Reuters.Briefs@thomsonreuters.com;))");
			hm2.put("ID", "urn:newsml:reuters.com:20210514:nASA026SG");
			hm2.put("RT", "2021-05-14T12:15:37-00:00");
			hm2.put("PR", "reuters.com");
			hm2.put("HT",
					"BRIEF-Cisco Announces Intent To Acquire Kenna Security To Deliver Industry Leading Vulnerability Management");
			hm2.put("CT", "2021-05-14T12:15:37-00:00");
			hm2.put("LT", "2021-05-14T12:15:38-00:00");

			hm3.put("TE",
					"May 12 (Reuters) - Cisco Systems Inc  <Origin Href=\\\"QuoteRef\\\">CSCO.O</Origin> :\n    * CISCO ANNOUNCES INTENT TO ACQUIRE SOCIO LABS TO POWER THE\nFUTURE\nOF HYBRID EVENTS\n    * CISCO - ACQUISITION IS EXPECTED TO CLOSE IN CISCO&apos;S Q4\nFISCAL\nYEAR 2021\n\nSource text for Eikon:  <Origin Href=\\\"NewsSearchID\\\">ID:nPn9vdWtSa</Origin> \nFurther company coverage:  <Origin Href=\\\"NewsSearch\\\">CSCO.O</Origin> \n\n ((reuters.briefs@thomsonreuters.com;))");
			hm3.put("ID", "urn:newsml:reuters.com:20210512:nASA0262A");
			hm3.put("RT", "2021-05-12T12:03:47-00:00");
			hm3.put("PR", "reuters.com");
			hm3.put("HT", "BRIEF-Cisco Announces Intent To Acquire Socio Labs To Power The Future Of Hybrid Events");
			hm3.put("CT", "2021-05-12T12:03:47-00:00");
			hm3.put("LT", "2021-05-12T12:03:47-00:00");

			hm4.put("TE",
					"By Stephen Nellis\n    May 11 (Reuters) - Some of the world&apos;s biggest chip buyers,\nincluding Apple Inc  <Origin Href=\\\"QuoteRef\\\">AAPL.O</Origin> , Microsoft Corp  <Origin Href=\\\"QuoteRef\\\">MSFT.O</Origin>  and\nAlphabet Inc&apos;s  <Origin Href=\\\"QuoteRef\\\">GOOGL.O</Origin>  Google, are joining top chip-makers\nsuch as Intel Corp  <Origin Href=\\\"QuoteRef\\\">INTC.O</Origin>  to create a new lobbying group to\npress for government chip manufacturing subsidies.\n    The newly formed Semiconductors in America Coalition, which\nalso includes Amazon.com&apos;s  <Origin Href=\\\"QuoteRef\\\">AMZN.O</Origin>  Amazon Web Services, said\nTuesday it has asked U.S. lawmakers to provide funding for the\nCHIPS for America Act, for which President Joe Biden has asked\nCongress to provide $50 billion. \n    &quot;Robust funding of the CHIPS Act would help America build\nthe additional capacity necessary to have more resilient supply\nchains to ensure critical technologies will be there when we\nneed them,&quot; the group said in a letter to Democratic and\nRepublican leaders in both houses of the U.S. Congress.\n    A global chip shortage has hit automakers hard, with Ford\nMotor Co  <Origin Href=\\\"QuoteRef\\\">F.N</Origin>  saying it could halve second-quarter production.\n <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2ML3FW</Origin>\n    Automotive industry groups have pressed the Biden\nadministration to secure chip supply for car factories. But\nReuters last week reported administration officials were\nreluctant to use a national security law to redirect computer\nchips to automakers because doing so could hurt other\nindustries.  <Origin Href=\\\"StoryRef\\\">urn:newsml:reuters.com:*:nL1N2MG2OJ</Origin>\n    The new coalition includes some of those other\nchip-consuming industries, with members such as AT&amp;T  <Origin Href=\\\"QuoteRef\\\">T.N</Origin> ,\nCisco Systems  <Origin Href=\\\"QuoteRef\\\">CSCO.O</Origin> , General Electric  <Origin Href=\\\"QuoteRef\\\">GE.N</Origin> , Hewlett Packard\nEnterprise  <Origin Href=\\\"QuoteRef\\\">HPE.N</Origin>  and Verizon Communications Inc  <Origin Href=\\\"QuoteRef\\\">VZ.N</Origin> . It\ncautioned against government actions to favor a single industry\nsuch as automakers.\n    &quot;Government should refrain from intervening as industry\nworks to correct the current supply-demand imbalance causing the\nshortage,&quot; the group said. \n    Tech companies such as Apple are also being hit by the chip\nshortage, but far less severely than automakers. \n    The iPhone maker said last month it will lose $3 billion to\n$4 billion in sales in the current quarter ending in June\nbecause of the chip shortage, but that equates to just a few\npercent of the $72.9 billion in sales analyst expect for Apple&apos;s\nfiscal third quarter, according to Refinitiv revenue estimates.\n\n (Reporting by Stephen Nellis in San Francisco; editing by\nRichard Pullin)\n ((Stephen.Nellis@thomsonreuters.com; (415) 344-4934;))");
			hm4.put("ID", "urn:newsml:reuters.com:20210511:nL1N2MX2MK");
			hm4.put("RT", "2021-05-11T09:00:00-00:00");
			hm4.put("PR", "reuters.com");
			hm4.put("HT", "Tech giants join call for funding U.S. chip production");
			hm4.put("CT", "2021-05-11T09:00:00-00:00");
			hm4.put("LT", "2021-05-11T09:00:00-00:00");

			ArrayList<LinkedHashMap> al = new ArrayList<LinkedHashMap>();
			al.add(hm1);
			al.add(hm2);
			al.add(hm3);
			al.add(hm4);

			JSONArray response = new JSONArray(al);
			return response;
		}

		return null;

	}

	public JSONObject mockViewInstrumentTransactions(Map<String, Object> inputMap) throws ParseException {
		TransactionsListBackendDelegateImpl transactionsListBackendDelegateImpl = new TransactionsListBackendDelegateImpl();
		String portfolioId = (String) inputMap.get(TemenosConstants.PORTFOLIOID);
		String startDate = (String) inputMap.get(TemenosConstants.STARTDATE);
		String sortBy = (String) inputMap.get(TemenosConstants.SORTBY);
		String searchVal = (String) inputMap.get(TemenosConstants.INSTRUMENTID);
		String endDate = (String) inputMap.get(TemenosConstants.ENDDATE);
		JSONObject response = new JSONObject();
		JSONArray transactionsArr = new JSONArray();
		String[] transactionId = null, description = null, ISIN = null, exchange = null, quantity = null,
				exchangeRate = null, orderType = null, RICCode = null, limitPrice = null, tradeDate = null, fees = null,
				valueDate = null, instrumentCurrency = null, netAmount = null, instrumentAmount = null, total = null,
				instrumentId = null, referenceCurrency = null, feesCurrency = null;
		String sortType = (String) inputMap.get(TemenosConstants.SORTORDER);
		int totalCount = 0;
		String limitVal = (String) inputMap.get(TemenosConstants.PAGESIZE);
		String offsetVal = (String) inputMap.get(TemenosConstants.PAGEOFFSET);
		int limit = (limitVal != null && limitVal.trim().length() > 0) ? Integer.parseInt(limitVal) : 0;
		int offset = (offsetVal != null && offsetVal.trim().length() > 0) ? Integer.parseInt(offsetVal) : 0;
		String search = (searchVal != null && searchVal.trim().length() > 0) ? searchVal : "";
		if (portfolioId.equalsIgnoreCase("100777-1")) {
			transactionId = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" };

			description = new String[] { "Alphabet", "Amazon.com Inc", "Apple", "LVMH", "Alphabet", "Coca-Cola Co",
					"American Express Company", "Boeing Co", "Bank of America Corp", "Citigroup Inc",
					"General Motors Company", "Pfizer Inc", "iShares Core S&P 500 UCITS ETF" };

			quantity = new String[] { "5", "4", "23", "5", "3", "10", "15", "10", "12", "15", "8", "10", "15" };

			/*
			 * instrumentId = new String[] { "100027-000", "100021-000", "100050-000",
			 * "100051-000", "100027-000", "100016-000", "100020-000", "100086-000",
			 * "100017-000", "100018-000", "100019-000", "100022-000", "100093-000" };
			 */

			fees = new String[] { "8.11", "10.39", "2.57", "2.46", "5.10", "0.45", "1.58", "1.90", "0.40", "0.83",
					"0.39", "0.34", "4.54" };

			exchangeRate = new String[] { "1", "1", "1", "1.21", "1", "1", "1", "1", "1", "1", "1", "1", "1.21" };

			limitPrice = new String[] { "1621.59", "2598.44", "111.59", "492.38", "1700.50", "45", "105", "190", "33",
					"55", "49", "34", "302.72" };

			orderType = new String[] { "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Sell Limit", "Buy Limit",
					"Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit" };

			ISIN = new String[] { "US02079K1079", "US0231351067", "US0378331005", "FR0000121014", "US02079K1079",
					"US1912161007", "US0258161092", "US0970231058", "US0605051046", "US1729671016", "US37045V1008",
					"INE182A01018", "IE00B5BMR087" };

			exchange = new String[] { "NYSE", "NYSE", "NYSE", "Euronext Paris", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE",
					"NYSE", "NYSE", "NYSE", "GER" };

			RICCode = new String[] { "GOOGL.OQ", "AMZN.OQ", "AAPL.OQ", "LVMH.PA", "GOOGL.OQ", "KO.N", "AXP.N", "BA.NQ",
					"BAC.N", "C.O", "GM.N", "PFE.N", "IXMO461.DE" };

			instrumentCurrency = new String[] { "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };

			referenceCurrency = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };
			instrumentId = new String[] { "100077-000", "100021-000", "100050-000", "100051-000", "100077-000",
					"100016-000", "100020-000", "100086-000", "100017-000", "100018-000", "100019-000", "100022-000",
					"100093-000" };
			
			feesCurrency = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };

			instrumentAmount = netAmountCalculation(limitPrice, quantity);
			total = totalAmountCalculation(instrumentAmount, fees, orderType);
			netAmount = instrumentAmountCalculation(total, exchangeRate);

			tradeDate = transactionsListBackendDelegateImpl
					.mockTradeDateViewInstrumentTransaction(transactionId.length);
			valueDate = transactionsListBackendDelegateImpl
					.mockTradeDateViewInstrumentTransaction(transactionId.length);
		} else if (portfolioId.equalsIgnoreCase("100777-2")) {
			transactionId = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" };

			description = new String[] { "Alphabet", "Amazon.com Inc", "Apple", "Walmart", "Alphabet", "Walmart",
					"Coca-Cola Co", "American Express Company", "Boeing Co", "Bank of America Corp", "Citigroup Inc",
					"General Motors Company", "Pfizer Inc" };

			quantity = new String[] { "5", "2", "7", "8", "3", "2", "6", "10", "8", "9", "12", "6", "9" };

			fees = new String[] { "8.11", "5.20", "0.78", "1.12", "5.10", "0.29", "0.25", "1.06", "1.47", "0.24",
					"0.73", "0.30", "0.28" };

			exchangeRate = new String[] { "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1" };

			limitPrice = new String[] { "1621.59", "2598.44", "111.59", "140.00", "1700.50", "146", "42", "106", "184",
					"27", "61", "50", "32" };

			orderType = new String[] { "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Sell Limit", "Sell Limit",
					"Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit" };

			ISIN = new String[] { "US02079K1079", "US0231351067", "US0378331005", "US0378331005", "US02079K1079",
					"US0378331005", "US1912161007", "US0258161092", "US0970231058", "US0605051046", "US1729671016",
					"US37045V1008", "INE182A01018" };

			exchange = new String[] { "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE",
					"NYSE", "NYSE", "NYSE" };

			RICCode = new String[] { "GOOGL.OQ", "AMZN.OQ", "AAPL.OQ", "WMT.N", "GOOGL.OQ", "WMT.N", "KO.N", "BA.NQ",
					"BAC.N", "C.O", "GM.N", "PFE.N", "IXMO461.DE" };

			instrumentCurrency = new String[] { "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };

			referenceCurrency = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };
			instrumentId = new String[] { "100077-000", "100021-000", "100050-000", "100014-000", "100077-000",
					"100014-000", "100016-000", "100020-000", "100086-000", "100017-000", "100018-000", "100019-000",
					"100022-000" };
			
			feesCurrency = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };

			instrumentAmount = netAmountCalculation(limitPrice, quantity);
			total = totalAmountCalculation(instrumentAmount, fees, orderType);
			netAmount = instrumentAmountCalculation(total, exchangeRate);

			tradeDate = transactionsListBackendDelegateImpl
					.mockTradeDateViewInstrumentTransaction(transactionId.length);
			valueDate = transactionsListBackendDelegateImpl
					.mockTradeDateViewInstrumentTransaction(transactionId.length);
		} else if (portfolioId.equalsIgnoreCase("100777-3")) {
			transactionId = new String[] { "1", "2", "3", "4", "5", "6", "7", "8" };
			description = new String[] { "Google LLC", "Amazon.com Inc", "Apple", "LVMH", "Google LLC", "Coca-Cola Co",
					"American Express Company", "Boeing Co" };
			ISIN = new String[] { "US02079K1079", "US0231351067", "US0378331005", "FR0000121014", "US02079K1079",
					"US1912161007", "US0258161092", "US0970231058" };
			exchange = new String[] { "NYSE", "NYSE", "NYSE", "PAR", "NYSE", "NYSE", "NYSE", "NYSE" };
			orderType = new String[] { "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit",
					"Buy Limit", "Buy Limit" };
			RICCode = new String[] { "GOOGL.O", "AMZN.O", "AAPL.OQ", "LVMH.PA", "GOOGL.O", "KO.N", "AXP.N", "BA.NQ" };
			instrumentCurrency = new String[] { "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD" };
			quantity = new String[] { "8", "5", "10", "5", "7", "8", "9", "10" };
			limitPrice = new String[] { "1621.59", "2598.44", "111.59", "492.38", "1700.50", "45.00", "105.00",
					"190.00" };

			referenceCurrency = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD" };
			feesCurrency = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD" };
			/*
			 * instrumentAmount = new String[] { "12972.72", "12992.20", "1155.90",
			 * "2461.90", "11930.50", "360.00", "945.00", "1900.00" };
			 */
			fees = new String[] { "12.97", "12.99", "1.12", "2.46", "11.90", "0.36", "0.95", "1.90" };
			/*
			 * total = new String[] { "12985.69", "13005.19", "1117.02", "2464.36",
			 * "11915.40", "360.36", "945.95", "1901.90" };
			 */
			exchangeRate = new String[] { "1", "1", "1", "1.21", "1", "1", "1", "1" };
			/*
			 * netAmount = new String[] { "12985.69", "13005.19", "117.02", "2981.88",
			 * "11915.40", "360.36", "945.95", "1901.90" };
			 */
			instrumentId = new String[] { "100077-000", "100021-000", "100050-000", "100051-000", "100077-000",
					"100016-000", "100020-000", "100086-000", "100017-000", "100018-000", "100019-000", "100022-000",
					"100093-000" };
			netAmount = netAmountCalculation(limitPrice, quantity);
			instrumentAmount = instrumentAmountCalculation(netAmount, exchangeRate);
			total = totalAmountCalculation(instrumentAmount, fees, orderType);
			tradeDate = transactionsListBackendDelegateImpl
					.mockTradeDateViewInstrumentTransaction(transactionId.length);
			valueDate = transactionsListBackendDelegateImpl
					.mockTradeDateViewInstrumentTransaction(transactionId.length);
		} else if (portfolioId.equalsIgnoreCase("100777-4")) {
			transactionId = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" };

			description = new String[] { "Alphabet", "Amazon.com Inc", "Apple", "LVMH", "Alphabet", "Coca-Cola Co",
					"American Express Company", "Boeing Co", "Bank of America Corp", "Citigroup Inc",
					"General Motors Company", "Pfizer Inc", "iShares Core S&P 500 UCITS ETF" };

			quantity = new String[] { "5", "4", "23", "5", "3", "10", "15", "10", "12", "15", "8", "10", "15" };

			fees = new String[] { "8.11", "10.39", "2.57", "2.46", "5.10", "0.45", "1.58", "1.90", "0.40", "0.83",
					"0.39", "0.34", "4.54" };

			exchangeRate = new String[] { "1", "1", "1", "1.21", "1", "1", "1", "1", "1", "1", "1", "1", "1.21" };

			limitPrice = new String[] { "1621.59", "2598.44", "111.59", "492.38", "1700.50", "45", "105", "190", "33",
					"55", "49", "34", "302.72" };

			orderType = new String[] { "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Sell Limit", "Buy Limit",
					"Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit" };

			ISIN = new String[] { "US02079K1079", "US0231351067", "US0378331005", "FR0000121014", "US02079K1079",
					"US1912161007", "US0258161092", "US0970231058", "US0605051046", "US1729671016", "US37045V1008",
					"INE182A01018", "IE00B5BMR087" };

			exchange = new String[] { "NYSE", "NYSE", "NYSE", "Euronext Paris", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE",
					"NYSE", "NYSE", "NYSE", "NYSE" };

			RICCode = new String[] { "GOOGL.OQ", "AMZN.OQ", "AAPL.OQ", "LVMH.PA", "GOOGL.OQ", "KO.N", "AXP.N", "BA.NQ",
					"BAC.N", "C.O", "GM.N", "PFE.N", "IXMO461.DE" };

			instrumentCurrency = new String[] { "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };

			referenceCurrency = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };
			instrumentId = new String[] { "100077-000", "100021-000", "100050-000", "100051-000", "100077-000",
					"100016-000", "100020-000", "100086-000", "100017-000", "100018-000", "100019-000", "100022-000",
					"100093-000" };
			feesCurrency = new String[] { "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };
			instrumentAmount = netAmountCalculation(limitPrice, quantity);
			total = totalAmountCalculation(instrumentAmount, fees, orderType);
			netAmount = instrumentAmountCalculation(total, exchangeRate);
			tradeDate = transactionsListBackendDelegateImpl
					.mockTradeDateViewInstrumentTransaction(transactionId.length);
			valueDate = transactionsListBackendDelegateImpl
					.mockTradeDateViewInstrumentTransaction(transactionId.length);
		} else if (portfolioId.equalsIgnoreCase("100777-5")) {
			transactionId = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" };

			description = new String[] { "Alphabet", "Amazon.com Inc", "Apple", "Walmart", "Alphabet", "Walmart",
					"Coca-Cola Co", "American Express Company", "Boeing Co", "Bank of America Corp", "Citigroup Inc",
					"General Motors Company", "Pfizer Inc" };

			quantity = new String[] { "5", "2", "7", "8", "3", "2", "6", "10", "8", "9", "12", "6", "9" };

			fees = new String[] { "8.11", "5.20", "0.78", "1.12", "5.10", "0.29", "0.25", "1.06", "1.47", "0.24",
					"0.73", "0.30", "0.28" };

			exchangeRate = new String[] { "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1" };

			limitPrice = new String[] { "1621.59", "2598.44", "111.59", "140.00", "1700.50", "146", "42", "106", "184",
					"27", "61", "50", "32" };

			orderType = new String[] { "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Sell Limit", "Sell Limit",
					"Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit", "Buy Limit" };

			ISIN = new String[] { "US02079K1079", "US0231351067", "US0378331005", "US0378331005", "US02079K1079",
					"US0378331005", "US1912161007", "US0258161092", "US0970231058", "US0605051046", "US1729671016",
					"US37045V1008", "INE182A01018" };

			exchange = new String[] { "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE", "NYSE",
					"NYSE", "NYSE", "NYSE" };

			RICCode = new String[] { "GOOGL.OQ", "AMZN.OQ", "AAPL.OQ", "LVMH.PA", "GOOGL.OQ", "KO.N", "AXP.N", "BA.NQ",
					"", "BAC.N", "C.O", "GM.N", "PFE.N" };

			referenceCurrency = new String[] { "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };
			
			feesCurrency = new String[] { "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };
			instrumentCurrency = new String[] { "USD", "USD", "USD", "EUR", "USD", "USD", "USD", "USD", "USD", "USD",
					"USD", "USD", "USD" };

			instrumentId = new String[] { "100077-000", "100021-000", "100050-000", "100014-000", "100077-000",
					"100014-000", "100016-000", "100020-000", "100086-000", "100017-000", "100018-000", "100019-000",
					"100022-000" };

			instrumentAmount = netAmountCalculation(limitPrice, quantity);
			total = totalAmountCalculation(instrumentAmount, fees, orderType);
			netAmount = instrumentAmountCalculation(total, exchangeRate);
			tradeDate = transactionsListBackendDelegateImpl
					.mockTradeDateViewInstrumentTransaction(transactionId.length);
			valueDate = transactionsListBackendDelegateImpl
					.mockTradeDateViewInstrumentTransaction(transactionId.length);
		}
		for (int i = 0; i < description.length; i++) {
			JSONObject transObj = new JSONObject();
			transObj.put(TemenosConstants.TRANSACTIONID, transactionId[i]);
			transObj.put(TemenosConstants.DESCRIPTION, description[i]);
			transObj.put("ISIN", ISIN[i]);
			transObj.put("holdingsType", exchange[i]);
			transObj.put(TemenosConstants.ORDERTYPE, orderType[i]);
			transObj.put(TemenosConstants.RICCODE, RICCode[i]);
			transObj.put(TemenosConstants.QUANTITY, quantity[i]);
			transObj.put(TemenosConstants.LIMITPRICE, limitPrice[i]);
			transObj.put(TemenosConstants.INSTRUMENTAMOUNT, instrumentAmount[i]);
			transObj.put(TemenosConstants.FEES, fees[i]);
			transObj.put("total", total[i]);
			transObj.put(TemenosConstants.EXCHANGERATE, exchangeRate[i]);
			transObj.put(TemenosConstants.NETAMOUNT, netAmount[i]);
			transObj.put(TemenosConstants.TRADEDATE, tradeDate[i]);
			transObj.put(TemenosConstants.VALUEDATE, valueDate[i]);
			transObj.put(TemenosConstants.INSTRUMENTCURRENCY, instrumentCurrency[i]);
			transObj.put(TemenosConstants.REFERENCECURRENCY, referenceCurrency[i]);
			transObj.put("feesCurrency", feesCurrency[i]);
			transObj.put(TemenosConstants.INSTRUMENTID, instrumentId[i]);
			transactionsArr.put(transObj);
		}
		JSONArray sortedJSON = new JSONArray();
		if (startDate.equals("") || endDate.equals("")) {
			sortedJSON = transactionsArr;
		} else {

			sortedJSON = transactionsListBackendDelegateImpl.filterDate(transactionsArr, startDate, endDate);
		}
		if (sortBy != null) {
			sortedJSON = transactionsListBackendDelegateImpl.sortArray(sortedJSON, sortBy, sortType);
		} else {
		}
		if (search.equals("")) {

		} else {
			sortedJSON = transactionsListBackendDelegateImpl.searchViewInstrumentTransactions(sortedJSON, search);
		}
		totalCount = sortedJSON.length();

		if (limit > 0 && offset >= 0) {
			sortedJSON = pagination(sortedJSON, limit, offset);
		}
		response.put("portfolioID", portfolioId);
		response.put("portfolioTransactions", sortedJSON);
		response.put(TemenosConstants.STARTDATE, startDate);
		response.put(TemenosConstants.ENDDATE, endDate);
		response.put(TemenosConstants.SORTBY, sortBy);
		response.put(TemenosConstants.REFERENCECURRENCY, "USD");
		response.put("totalCount", totalCount);
		response.put("opstatus", "0");
		response.put("httpStatusCode", "200");
		return response;

	}

	public JSONArray mockGetFavoriteInstruments(ArrayList<String> favoriteRICCodeList) {

		String code = "";
		JSONObject instrument = new JSONObject();

		HashMap<String, JSONObject> allInstruments = mockGetAllInstruments();

		JSONArray favoriteInstrumentsArr = new JSONArray();

		for (int i = 0; i < favoriteRICCodeList.size(); i++) {
			code = favoriteRICCodeList.get(i);
			if (allInstruments.containsKey(code)) {
				instrument = allInstruments.get(code);
				favoriteInstrumentsArr.put(instrument);
			}
		}

		return favoriteInstrumentsArr;
	}

	public String[] netAmountCalculation(String[] price, String[] quantity) throws ParseException {

		String[] netAmount = new String[price.length];
		double unitPrice = 0, amount = 0;
		int qty = 0;
		for (int i = 0; i < price.length; i++) {
			unitPrice = Double.parseDouble(price[i]);
			if (price[i] == "0") {
				unitPrice = 1;
			}
			qty = Integer.parseInt(quantity[i]);
			amount = unitPrice * qty;
			netAmount[i] = Double.toString(amount);
		}
		return netAmount;
	}

	public String[] instrumentAmountCalculation(String[] netAmount, String[] exchangeRate) throws ParseException {

		String[] instrumentAmount = new String[netAmount.length];
		double instrAmount = 0, rate = 0, amount = 0;
		for (int i = 0; i < netAmount.length; i++) {
			amount = Double.parseDouble(netAmount[i]);
			rate = Double.parseDouble(exchangeRate[i]);
			instrAmount = amount * rate;
			instrumentAmount[i] = Double.toString(instrAmount);
		}
		return instrumentAmount;
	}

	public String[] totalAmountCalculation(String[] instrumentAmount, String[] fees, String[] type)
			throws ParseException {

		String[] totalAmount = new String[instrumentAmount.length];
		double total = 0, fee = 0, amount = 0;
		for (int i = 0; i < instrumentAmount.length; i++) {
			amount = Double.parseDouble(instrumentAmount[i]);
			fee = Double.parseDouble(fees[i]);
			if (type[i].equalsIgnoreCase("Buy Limit")) {
				total = amount + fee;
			} else {
				total = amount - fee;
			}

			totalAmount[i] = Double.toString(total);
			;
		}
		return totalAmount;
	}

	public HashMap<String, JSONObject> mockGetAllInstruments() {

		HashMap<String, JSONObject> allInstruments = new HashMap<String, JSONObject>();

		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY");
		Date date = new Date();

		JSONObject instrumentDetails = new JSONObject();

		instrumentDetails.put("RIC", "AMZN.O");
		instrumentDetails.put("instrumentId", "100044-000");
		instrumentDetails.put("ISINCode", "US0231351067");
		instrumentDetails.put("instrumentName", "Amazon.com Inc");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "3203.53");
		instrumentDetails.put("netchange", "16.55");
		instrumentDetails.put("percentageChange", "0.51");
		instrumentDetails.put("timeReceived", "12:20:00");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "3201.12");
		instrumentDetails.put("askRate", "3204.32");
		instrumentDetails.put("volume", 132120);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("AMZN.O", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "GOOGL.O");
		instrumentDetails.put("instrumentId", "100027-000");
		instrumentDetails.put("ISINCode", "US02079K1079");
		instrumentDetails.put("instrumentName", "Google LLC");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "1776.84");
		instrumentDetails.put("netchange", "4.67");
		instrumentDetails.put("percentageChange", "0.26");
		instrumentDetails.put("timeReceived", "20:59:00");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("GOOGL.O", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "AAPL.OQ");
		instrumentDetails.put("instrumentId", "100050-000");
		instrumentDetails.put("ISINCode", "US0378331005");
		instrumentDetails.put("instrumentName", "Apple");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "123.08");
		instrumentDetails.put("netchange", "0.36");
		instrumentDetails.put("percentageChange", "0.29");
		instrumentDetails.put("timeReceived", "21:00:00");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.60");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("AAPL.OQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "PFE.N");
		instrumentDetails.put("instrumentId", "100022-000");
		instrumentDetails.put("ISINCode", "INE182A01018");
		instrumentDetails.put("instrumentName", "Pfizer Inc");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "37.31");
		instrumentDetails.put("netchange", "0.03");
		instrumentDetails.put("percentageChange", "0.08");
		instrumentDetails.put("timeReceived", "21:00:00");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "36.31");
		instrumentDetails.put("askRate", "38.31");
		instrumentDetails.put("volume", 20769880);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("PFE.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "LVMH.PA");
		instrumentDetails.put("instrumentId", "100051-000");
		instrumentDetails.put("ISINCode", "FR0000121014");
		instrumentDetails.put("instrumentName", "LVMH");
		instrumentDetails.put("exchange", "Euronext Paris");
		instrumentDetails.put("referenceCurrency", "EUR");
		instrumentDetails.put("lastRate", "497.95");
		instrumentDetails.put("netchange", "0.15");
		instrumentDetails.put("percentageChange", "0.03");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "478.45");
		instrumentDetails.put("askRate", "476.65");
		instrumentDetails.put("volume", 752);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("LVMH.PA", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "AMAG.OQ");
		instrumentDetails.put("instrumentId", "100052-000");
		instrumentDetails.put("ISINCode", "US02266311111");
		instrumentDetails.put("instrumentName", "AMAG PHARMACEUTICALS INC");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "13.75");
		instrumentDetails.put("netchange", "0.02");
		instrumentDetails.put("percentageChange", "0.15");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "13.74");
		instrumentDetails.put("askRate", "13.70");
		instrumentDetails.put("volume", 742572);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("AMAG.OQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "AMAL.OQ");
		instrumentDetails.put("instrumentId", "100053-000");
		instrumentDetails.put("ISINCode", "US0226631085");
		instrumentDetails.put("instrumentName", "AMALGAMTD BANK A");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "13.15");
		instrumentDetails.put("netchange", "0.14");
		instrumentDetails.put("percentageChange", "-1.05");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "13.3");
		instrumentDetails.put("askRate", "13.43");
		instrumentDetails.put("volume", 2108);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("AMAL.OQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "AMRN.OQ");
		instrumentDetails.put("instrumentId", "100054-000");
		instrumentDetails.put("ISINCode", "US0231112063");
		instrumentDetails.put("instrumentName", "AMARIN CORP");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "4.93");
		instrumentDetails.put("netchange", "0.09");
		instrumentDetails.put("percentageChange", "1.85");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "4.89");
		instrumentDetails.put("askRate", "5.04");
		instrumentDetails.put("volume", 1175064);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("AMRN.OQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "AMBA.OQ");
		instrumentDetails.put("instrumentId", "100055-000");
		instrumentDetails.put("ISINCode", "US00001210000");
		instrumentDetails.put("instrumentName", "AMBARELLA INC");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "60.72");
		instrumentDetails.put("netchange", "0.0050");
		instrumentDetails.put("percentageChange", "-0.09");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "60.87");
		instrumentDetails.put("askRate", "60.98");
		instrumentDetails.put("volume", 15445);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("AMBA.OQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "AMCX.O");
		instrumentDetails.put("instrumentId", "100059-000");
		instrumentDetails.put("ISINCode", "US00164V1035");
		instrumentDetails.put("instrumentName", "AMC NTWK CL A");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "28.745");
		instrumentDetails.put("netchange", "0.355");
		instrumentDetails.put("percentageChange", "-1.25");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "28.83");
		instrumentDetails.put("askRate", "28.85");
		instrumentDetails.put("volume", 245551);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("AMCX.O", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "MSFT.N");
		instrumentDetails.put("instrumentId", "100012-000");
		instrumentDetails.put("ISINCode", "US5949181045");
		instrumentDetails.put("instrumentName", "MICROSOFT CP");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "214.19");
		instrumentDetails.put("netchange", "-1.13");
		instrumentDetails.put("percentageChange", "-0.52");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "214.70");
		instrumentDetails.put("askRate", "214.85");
		instrumentDetails.put("volume", 804970);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("MSFT.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "TSLA.OQ");
		instrumentDetails.put("instrumentId", "100013-000");
		instrumentDetails.put("ISINCode", "US88160R1014");
		instrumentDetails.put("instrumentName", "TESLA INC");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "593.38");
		instrumentDetails.put("netchange", "24.56");
		instrumentDetails.put("percentageChange", "4.31");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "593.0");
		instrumentDetails.put("askRate", "593.48");
		instrumentDetails.put("volume", 8435006);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("TSLA.OQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "WMT.N");
		instrumentDetails.put("instrumentId", "100014-000");
		instrumentDetails.put("ISINCode", "US9311421039");
		instrumentDetails.put("instrumentName", "Walmart Inc");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "148.91");
		instrumentDetails.put("netchange", "0.15");
		instrumentDetails.put("percentageChange", "0.03");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "149.25");
		instrumentDetails.put("askRate", "149.80");
		instrumentDetails.put("volume", 2087843);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("WMT.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "IXM0461.DE");
		instrumentDetails.put("instrumentId", "100015-000");
		instrumentDetails.put("ISINCode", "IE00B5BMR087");
		instrumentDetails.put("instrumentName", "iShares Core S&P 500");
		instrumentDetails.put("exchange", "GER");
		instrumentDetails.put("referenceCurrency", "EUR");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "306.65");
		instrumentDetails.put("askRate", "307.5");
		instrumentDetails.put("volume", 2087843);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("IXM0461.DE", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "KO.N");
		instrumentDetails.put("instrumentId", "100016-000");
		instrumentDetails.put("ISINCode", "US1912161007");
		instrumentDetails.put("instrumentName", "Coca-Cola Co");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "49.29");
		instrumentDetails.put("netchange", "0.51");
		instrumentDetails.put("percentageChange", "1.05");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1739.77");
		instrumentDetails.put("askRate", "1785.6");
		instrumentDetails.put("volume", 13019);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("KO.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "BAC.N");
		instrumentDetails.put("instrumentId", "100017-000");
		instrumentDetails.put("ISINCode", "US0605051046");
		instrumentDetails.put("instrumentName", "Bank of America Corp");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "30.94");
		instrumentDetails.put("netchange", "-0.22");
		instrumentDetails.put("percentageChange", "-0.71");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1719.77");
		instrumentDetails.put("askRate", "1717.6");
		instrumentDetails.put("volume", 14409);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("BAC.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "AMZN.OQ");
		instrumentDetails.put("instrumentId", "100021-000");
		instrumentDetails.put("ISINCode", "US0231351067");
		instrumentDetails.put("instrumentName", "Amazon.com Inc");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "3203.53");
		instrumentDetails.put("netchange", "16.55");
		instrumentDetails.put("percentageChange", "0.51");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("AMZN.OQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "GOOGL.OQ");
		instrumentDetails.put("instrumentId", "100077-000");
		instrumentDetails.put("ISINCode", "US02079K1079");
		instrumentDetails.put("instrumentName", "Alphabet");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "1824.97");
		instrumentDetails.put("netchange", "29.61");
		instrumentDetails.put("percentageChange", "1.65");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("GOOGL.OQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "AAPL.O");
		instrumentDetails.put("instrumentId", "100070-000");
		instrumentDetails.put("ISINCode", "US0378331005");
		instrumentDetails.put("instrumentName", "Apple Inc");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "123.08");
		instrumentDetails.put("netchange", "0.36");
		instrumentDetails.put("percentageChange", "0.29");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("AAPL.O", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "TSLA.OQ");
		instrumentDetails.put("instrumentId", "100056-000");
		instrumentDetails.put("ISINCode", "USU8810LAA18");
		instrumentDetails.put("instrumentName", "TSLA 5.300% 15Aug2025 Corp (USD)");
		instrumentDetails.put("exchange", "XNYS");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("TSLA.OQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "GM.N");
		instrumentDetails.put("instrumentId", "100019-000");
		instrumentDetails.put("ISINCode", "US37045V1008");
		instrumentDetails.put("instrumentName", "General Motors Company");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "52.04");
		instrumentDetails.put("netchange", "-1.35");
		instrumentDetails.put("percentageChange", "-2.53");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("GM.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "F.N");
		instrumentDetails.put("instrumentId", "100091-000");
		instrumentDetails.put("ISINCode", "US345370BJ82");
		instrumentDetails.put("instrumentName", "Ford, 8.875% 15jan2022, USD");
		instrumentDetails.put("exchange", "XNYS");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		;
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("F.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "JPM.N");
		instrumentDetails.put("instrumentId", "100092-000");
		instrumentDetails.put("ISINCode", "LU0210536198");
		instrumentDetails.put("instrumentName", "JPMorgan Funds - US Growth Fund A (acc)");
		instrumentDetails.put("exchange", "MFAU");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("JPM.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "BLK");
		instrumentDetails.put("instrumentId", "100093-000");
		instrumentDetails.put("ISINCode", "IE00B5BMR087");
		instrumentDetails.put("instrumentName", "iShares Core S&P 500 UCITS ETF");
		instrumentDetails.put("exchange", "GER");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "308.75");
		instrumentDetails.put("netchange", "0.03");
		instrumentDetails.put("percentageChange", "0.08");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("BLK", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "FIDELIT.LG");
		instrumentDetails.put("instrumentId", "100094-000");
		instrumentDetails.put("ISINCode", "IE00BYX5MS15");
		instrumentDetails.put("instrumentName", "Fidelity S&P 500 Index USD P Acc");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("FIDELIT.LG", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "2YTD");
		instrumentDetails.put("instrumentId", "100101-000");
		instrumentDetails.put("ISINCode", "");
		instrumentDetails.put("instrumentName", "2 Year Term Deposit 1.5%");
		instrumentDetails.put("exchange", "");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "306.65");
		instrumentDetails.put("askRate", "307.5");
		instrumentDetails.put("volume", 2087843);
		instrumentDetails.put("isSecurityAsset", false);
		instrumentDetails.put("application", "DX");
		allInstruments.put("2YTD", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "5YTD");
		instrumentDetails.put("instrumentId", "100195-000");
		instrumentDetails.put("ISINCode", "");
		instrumentDetails.put("instrumentName", "5 Year Term Deposit 3.25%");
		instrumentDetails.put("exchange", "");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "306.65");
		instrumentDetails.put("askRate", "307.5");
		instrumentDetails.put("volume", 2087843);
		instrumentDetails.put("isSecurityAsset", false);
		instrumentDetails.put("application", "DX");
		allInstruments.put("5YTD", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "10YTD");
		instrumentDetails.put("instrumentId", "100295-000");
		instrumentDetails.put("ISINCode", "");
		instrumentDetails.put("instrumentName", "10 Year Term Deposit 4.75%");
		instrumentDetails.put("exchange", "");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "306.65");
		instrumentDetails.put("askRate", "307.5");
		instrumentDetails.put("volume", 2087843);
		instrumentDetails.put("isSecurityAsset", false);
		instrumentDetails.put("application", "DX");
		allInstruments.put("10YTD", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "BA.N");
		instrumentDetails.put("instrumentId", "100096-000");
		instrumentDetails.put("ISINCode", "US0378331005");
		instrumentDetails.put("instrumentName", "The Boeing Company (BA)");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("BA.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "BA.NQ");
		instrumentDetails.put("instrumentId", "100086-000");
		instrumentDetails.put("ISINCode", "US0970231058");
		instrumentDetails.put("instrumentName", "Boeing Co");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "202.06");
		instrumentDetails.put("netchange", "-1.3");
		instrumentDetails.put("percentageChange", "-0.64");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("BA.NQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "AXP.N");
		instrumentDetails.put("instrumentId", "100020-000");
		instrumentDetails.put("ISINCode", "US0258161092");
		instrumentDetails.put("instrumentName", "American Express Company");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("AXP.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "PNC.N");
		instrumentDetails.put("instrumentId", "100097-000");
		instrumentDetails.put("ISINCode", "US0258161092");
		instrumentDetails.put("instrumentName", "AXP American Express Co.");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "306.65");
		instrumentDetails.put("askRate", "307.5");
		instrumentDetails.put("volume", 2087843);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("PNC.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "CSCO.OQ");
		instrumentDetails.put("instrumentId", "100098-000");
		instrumentDetails.put("ISINCode", "US17275R1023");
		instrumentDetails.put("instrumentName", "Cisco Systems, Inc.");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", false);
		instrumentDetails.put("application", "SC");
		allInstruments.put("CSCO.OQ", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "AAPL.N");
		instrumentDetails.put("instrumentId", "100099-000");
		instrumentDetails.put("ISINCode", "US0378331005");
		instrumentDetails.put("instrumentName", "APPLE-CALL-115-16JUL");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", false);
		instrumentDetails.put("application", "DX");
		allInstruments.put("AAPL.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "GOOGL.N");
		instrumentDetails.put("instrumentId", "100028-000");
		instrumentDetails.put("ISINCode", "US02079K1079");
		instrumentDetails.put("instrumentName", "GOOGLE-PUT-2300-16JUL");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", false);
		instrumentDetails.put("application", "DX");
		allInstruments.put("GOOGL.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "C.N");
		instrumentDetails.put("instrumentId", "100018-000");
		instrumentDetails.put("ISINCode", "US1729671016");
		instrumentDetails.put("instrumentName", "Citigroup Inc");
		instrumentDetails.put("exchange", "NYSE");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "60.91");
		instrumentDetails.put("netchange", "0.35");
		instrumentDetails.put("percentageChange", "0.58");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "1769.77");
		instrumentDetails.put("askRate", "1787.6");
		instrumentDetails.put("volume", 14419);
		instrumentDetails.put("isSecurityAsset", true);
		instrumentDetails.put("application", "SC");
		allInstruments.put("C.N", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "FEURUSD");
		instrumentDetails.put("instrumentId", "100030-000");
		instrumentDetails.put("ISINCode", "");
		instrumentDetails.put("instrumentName", "Forward EURUSD 2901 2022");
		instrumentDetails.put("exchange", "");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "306.65");
		instrumentDetails.put("askRate", "307.5");
		instrumentDetails.put("volume", 2087843);
		instrumentDetails.put("isSecurityAsset", false);
		instrumentDetails.put("application", "DX");
		allInstruments.put("FEURUSD", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "FGBPUSD");
		instrumentDetails.put("instrumentId", "100130-000");
		instrumentDetails.put("ISINCode", "");
		instrumentDetails.put("instrumentName", "Forward GBPUSD 2901 2022");
		instrumentDetails.put("exchange", "");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "306.65");
		instrumentDetails.put("askRate", "307.5");
		instrumentDetails.put("volume", 2087843);
		instrumentDetails.put("isSecurityAsset", false);
		instrumentDetails.put("application", "DX");
		allInstruments.put("FGBPUSD", instrumentDetails);

		instrumentDetails = new JSONObject();
		instrumentDetails.put("RIC", "FCHFUSD");
		instrumentDetails.put("instrumentId", "100230-000");
		instrumentDetails.put("ISINCode", "");
		instrumentDetails.put("instrumentName", "Forward CHFUSD 2901 2022");
		instrumentDetails.put("exchange", "");
		instrumentDetails.put("referenceCurrency", "USD");
		instrumentDetails.put("lastRate", "306.67");
		instrumentDetails.put("netchange", "1.66");
		instrumentDetails.put("percentageChange", "0.55");
		instrumentDetails.put("timeReceived", "06:22:50");
		instrumentDetails.put("dateReceived", sdf.format(date));
		instrumentDetails.put("bidRate", "306.65");
		instrumentDetails.put("askRate", "307.5");
		instrumentDetails.put("volume", 2087843);
		instrumentDetails.put("isSecurityAsset", false);
		instrumentDetails.put("application", "DX");
		allInstruments.put("FCHFUSD", instrumentDetails);

		return allInstruments;
	}

}
