package com.infinity.dbx.temenos.commons.backenddelegate.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.commons.backenddelegate.impl.TransactionLimitsBackendDelegateImpl;

public class TransactionLimitsBackendDelegateImplExtn extends TransactionLimitsBackendDelegateImpl{

	private static final Logger LOG = LogManager.getLogger(TransactionLimitsBackendDelegateImplExtn.class);

	@Override
	public Double fetchConvertedAmount(String currency,String amount, DataControllerRequest request) {
		try {
			String response = getConversionRate(currency, request);
			JSONObject jsonRsponse = new JSONObject(response);
		    JSONArray arr = jsonRsponse.getJSONArray("rates");
			if(arr != null) {
				JSONObject json = (arr.getJSONObject(0));
				String midRate = json.optString("midRevalRate");
				String quotationCode = json.optString("quotationCode");
				double amountValue = Double.parseDouble(amount);
				double midRateValue = Double.parseDouble(midRate);
				
				if(StringUtils.isBlank(quotationCode)) {
					return amountValue/midRateValue;
				}else if(quotationCode.equals("0")) {
					return amountValue * midRateValue;
				}
			}
		}
		catch (Exception e) {
			LOG.error("Failed to fetch converted amount: ", e);
			return null;
		}
		return null;
	}
	@Override
	public Double fetchNewConvertedAmount(Double amount, String fromCurrency, String toCurrency, DataControllerRequest dcr){
		try {
			Double fromCurrencyRatio = fetchConversionRatio(fromCurrency, dcr);
			Double toCurrencyRatio = fetchConversionRatio(toCurrency, dcr);

			if(fromCurrencyRatio == null || toCurrencyRatio == null){
				return amount;
			}
			else{
				return (toCurrencyRatio/fromCurrencyRatio) * amount;
			}
		}
		catch (Exception e) {
			LOG.error("Failed to fetch converted amount: ", e);
			return null;
		}
	}
	
	private Double fetchConversionRatio(String currency, DataControllerRequest dcr){
		try {
			String response = getConversionRate(currency, dcr);
			JSONObject jsonRsponse = new JSONObject(response);
			JSONArray arr = jsonRsponse.getJSONArray("rates");
			if(arr != null) {
				JSONObject json = arr.getJSONObject(0);
				String midRate = json.optString("midRevalRate");
				String quotationCode = json.optString("quotationCode");
				double midRateValue = Double.parseDouble(midRate);

				if(StringUtils.isBlank(quotationCode)) {
					return (1.0/midRateValue);
				}else if(quotationCode.equals("0")) {
					return midRateValue;
				}
			}
		}
		catch (Exception e) {
			LOG.error("Failed to fetch converted amount: ", e);
			return null;
		}
		return null;
	}

	private String getConversionRate(String currency, DataControllerRequest request) {
		try {
			Map<String, Object> requestParameters = new HashMap<String, Object>();
			requestParameters.put("currency",currency);
			LOG.error("INPUT to BACKEND: "+new JSONObject(requestParameters).toString());
			return DBPServiceExecutorBuilder.builder().
					withServiceId(TemenosConstants.SERVICE_T24IS_FOREX_DETAILS).
					withObjectId(null).
					withOperationId(TemenosConstants.OP_GET_CONVERTEDRATE).
					withRequestParameters(requestParameters).
					withRequestHeaders(request.getHeaderMap()).
					withDataControllerRequest(request).
					build().getResponse();
		}
		catch (Exception e) {
			LOG.error("Caught exception at  get converted value: ", e);
			return "{\"errormsg\":\""+e.getMessage()+"\"}";
		}
	}
}
