package com.temenos.dbx.product.forexservices.resource.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.forexservices.backenddelegate.api.ForexBackendDelegate;
import com.temenos.dbx.product.forexservices.businessdelegate.api.ForexBusinessDelegate;
import com.temenos.dbx.product.forexservices.dto.CurrencyDTO;
import com.temenos.dbx.product.forexservices.resource.api.ForexResource;

public class ForexResourceImpl implements ForexResource {
	private static final Logger LOG = LogManager.getLogger(ForexResourceImpl.class);
	ForexBackendDelegate forexBackendDelegate = DBPAPIAbstractFactoryImpl.getBackendDelegate(ForexBackendDelegate.class);
	ForexBusinessDelegate forexBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ForexBusinessDelegate.class);
	
	@Override
	public Result fetchAllCurrencies(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public Result fetchBaseCurrency(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		//permission check
		List<String> requiredActionIds = Arrays.asList(FeatureAction.FX_RATES_VIEW,FeatureAction.FX_RATES_VIEW_CALCULATOR);
		String features = CustomerSession.getPermittedActionIds(request, requiredActionIds);
		if(features == null) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		
		Result result = null;
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		String CountryCode = inputParams.get("CountryCode") != null ? inputParams.get("CountryCode").toString() : null;
		String market = inputParams.get("market") != null ? inputParams.get("market").toString() : null;
		String companyCode = inputParams.get("companyCode") != null ? inputParams.get("companyCode").toString() : null;
		
		CurrencyDTO baseCurrency = forexBackendDelegate.getBaseCurrencyFromBackend(request, methodID, market, companyCode,  CountryCode);
		
		if(baseCurrency == null ) {
			return ErrorCodeEnum.ERR_27001.setErrorCode(new Result());
		}
		
		try {
			JSONObject baseCurrencyObject = new JSONObject(baseCurrency);
			result = JSONToResult.convert(baseCurrencyObject.toString());
		} catch (JSONException e) {
			LOG.error("Error occured while fetching base currency", e);
			return ErrorCodeEnum.ERR_27002.setErrorCode(result);			
		}

		return result;		
	}
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public Result fetchDashboardCurrencyList(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);
		String customerId = CustomerSession.getCustomerId(customer);
		//permission check
		List<String> requiredActionIds = Arrays.asList(FeatureAction.FX_RATES_VIEW,FeatureAction.FX_RATES_VIEW_CALCULATOR);
		String features = CustomerSession.getPermittedActionIds(dcRequest, requiredActionIds);
		if(features == null) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		
		
		Result result = null;
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		String baseCurrencyCode = inputParams.get("baseCurrencyCode") != null ? inputParams.get("baseCurrencyCode").toString() : null;
		
		if(baseCurrencyCode == null ) {
			return ErrorCodeEnum.ERR_27013.setErrorCode(new Result());
		}		
		
		//fetch baseCodeCurrency from backend to validate against request payload
		String companyId=String.valueOf(customer.get("companyId"));
		String countryCode=String.valueOf(customer.get("countrycode"));
		String marketCode="10 1";
		CurrencyDTO baseCurrency = forexBackendDelegate.getBaseCurrencyFromBackend(dcRequest, "fetchBaseCurrency", marketCode, companyId,  countryCode);

		if(baseCurrency == null ) {
			return ErrorCodeEnum.ERR_27001.setErrorCode(new Result());
		}
		if(!baseCurrency.getCode().equals(baseCurrencyCode))
			return ErrorCodeEnum.ERR_28002.setErrorCode(new Result());


		List<CurrencyDTO> currencyList = forexBusinessDelegate.getDashboardCurrencies(methodID, customerId, baseCurrencyCode, dcRequest);
		
		if(currencyList == null ) {
			return ErrorCodeEnum.ERR_27012.setErrorCode(new Result());
		}
		
		try {
			JSONArray resultRecords = new JSONArray(currencyList);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.CURRENCIES, resultRecords);
			result = JSONToResult.convert(resultObject.toString());			
		} catch (JSONException e) {
			LOG.error("Error occured while fetching dashboard currency list", e);
			return ErrorCodeEnum.ERR_27003.setErrorCode(result);			
		}

		return result;		
	}	

	@SuppressWarnings("unchecked")
	@Override
	public Result fetchCurrencyRates(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		//permission check
		List<String> requiredActionIds = Arrays.asList(FeatureAction.FX_RATES_VIEW);
		String features = CustomerSession.getPermittedActionIds(dcRequest, requiredActionIds);
		if(features == null) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		
		Result result = null;
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		String baseCurrencyCode = inputParams.get("baseCurrencyCode") != null ? inputParams.get("baseCurrencyCode").toString() : null;
		String quoteCurrencyCode = inputParams.get("quoteCurrencyCode") != null ? inputParams.get("quoteCurrencyCode").toString() : null;
		String market = inputParams.get("market") != null ? inputParams.get("market").toString() : null;				
		String companyCode = inputParams.get("companyCode") != null ? inputParams.get("companyCode").toString() : null; 

		if(baseCurrencyCode == null || quoteCurrencyCode == null) {
			return ErrorCodeEnum.ERR_27004.setErrorCode(new Result());
		}		
		
		CurrencyDTO currency = forexBackendDelegate.getCurrencyRatesFromBackend(dcRequest, methodID, baseCurrencyCode, quoteCurrencyCode, market, companyCode);

		if(currency == null) {
			return ErrorCodeEnum.ERR_27014.setErrorCode(new Result());
		}
		
		try {
			JSONObject baseCurrencyObject = new JSONObject(currency);
			result = JSONToResult.convert(baseCurrencyObject.toString());
		} catch (JSONException e) {
			LOG.error("Error occured while fetching currency rates", e);
			return ErrorCodeEnum.ERR_27005.setErrorCode(result);			
		}

		return result;	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Result fetchDashboardCurrencyRates(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		//permission check
		List<String> requiredActionIds = Arrays.asList(FeatureAction.FX_RATES_VIEW);
		String features = CustomerSession.getPermittedActionIds(dcRequest, requiredActionIds);
		if(features == null) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		
		Result result = null;
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		
		String baseCurrencyCode = inputParams.get("baseCurrencyCode") != null ? inputParams.get("baseCurrencyCode").toString() : null;
		String market = inputParams.get("market") != null ? inputParams.get("market").toString() : null;
		String companyCode = inputParams.get("companyCode") != null ? inputParams.get("companyCode").toString() : null;
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(dcRequest);			
		String customerId = CustomerSession.getCustomerId(customer);

		if(baseCurrencyCode == null) {
			return ErrorCodeEnum.ERR_27006.setErrorCode(new Result());
		}		
		
		List<CurrencyDTO> currencyList = forexBusinessDelegate.getDashboardCurrencyRates(methodID,customerId,baseCurrencyCode,market, companyCode, dcRequest);

		if(currencyList == null) {
			return ErrorCodeEnum.ERR_27015.setErrorCode(new Result());
		}		
		
		try {
			JSONArray resultRecords = new JSONArray(currencyList);
			JSONObject resultObject = new JSONObject();
			resultObject.put(Constants.CURRENCIES, resultRecords);
			result = JSONToResult.convert(resultObject.toString());	
		} catch (JSONException e) {
			LOG.error("Error occured while fetching dashboard currency rates", e);
			return ErrorCodeEnum.ERR_27007.setErrorCode(result);			
		}

		return result;	
	}	

	@SuppressWarnings("unchecked")
	@Override
	public Result fetchPopularCurrencies(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = null;	
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		String baseCurrencyCode = inputParams.get("baseCurrencyCode") != null ? inputParams.get("baseCurrencyCode").toString() : null;		
		List<CurrencyDTO> baseCurrency = forexBusinessDelegate.getPopularCurrencies(baseCurrencyCode,dcRequest);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result fetchRecentCurrencies(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = null;	
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		String baseCurrencyCode = inputParams.get("baseCurrencyCode") != null ? inputParams.get("baseCurrencyCode").toString() : null;		
		List<CurrencyDTO> baseCurrency = forexBusinessDelegate.getRecentCurrencies(baseCurrencyCode,dcRequest);
		return result;
	}

	@Override
	public Result updateRecentCurrencies(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		List<String> requiredActionIds = Arrays.asList(FeatureAction.FX_RATES_VIEW,FeatureAction.FX_RATES_VIEW_CALCULATOR);
		String features = CustomerSession.getPermittedActionIds(request, requiredActionIds);
		if(features == null) {
			return ErrorCodeEnum.ERR_12001.setErrorCode(new Result());
		}
		
		Map<String, Object> customer = CustomerSession.getCustomerMap(request);			
		String customerId = CustomerSession.getCustomerId(customer);
		String legalEntityId = (String) customer.get("legalEntityId");
		
		@SuppressWarnings("unchecked")
		Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
		String quoteCurrencyCode = inputParams.get("quoteCurrencyCode") != null ? inputParams.get("quoteCurrencyCode").toString() : null;
		
		if(quoteCurrencyCode == null) {
			return ErrorCodeEnum.ERR_27008.setErrorCode(new Result());
		}

		String message = null;
		boolean isSuccess = forexBusinessDelegate.updateRecentCurrencies(customerId, quoteCurrencyCode, legalEntityId);
		if(isSuccess) {
			message = "Recent currencies of the user updated successfully";
		}
		else {
			message = "Failed to update recent currencies of the user";
		}
		
		Result result = new Result();
		Param successParam = new Param("message", message);
		result.addParam(successParam);
		return result;
	}

}
