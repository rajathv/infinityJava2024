package com.temenos.infinity.api.wealthOrder.resource.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.sessionmanager.SessionScope;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthOrder.businessdelegate.api.CurrencyConvertionBusinessDelegate;
import com.temenos.infinity.api.wealthOrder.resource.api.CurrencyConvertionResource;

public class CurrencyConvertionResourceImpl implements CurrencyConvertionResource {

	@Override
	public Result createCurrencyConvertion(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();
		Map<String, Object> headersMap = request.getHeaderMap();
		if (headersMap == null) {
			headersMap = new HashMap<String, Object>();
		}

		/*Set<String> userPermissions = SessionScope.getAllPermissionsFromIdentityScope(request);
		if (!userPermissions.contains("WEALTH_CASH_MGMT_CURRENCY_COVERSION_CREATE")) {
			ErrorCodeEnum.ERR_12001.setErrorCode(result);
			return result;
		}*/

		CurrencyConvertionBusinessDelegate currencyConvertionBusinessDelegate = DBPAPIAbstractFactoryImpl
				.getBusinessDelegate(CurrencyConvertionBusinessDelegate.class);
		result = currencyConvertionBusinessDelegate.createCurrencyConvertion(methodID, inputArray, request, response,
				headersMap);
		return result;

	}

}
