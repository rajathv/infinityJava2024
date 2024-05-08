package com.temenos.dbx.product.commons.backenddelegate.impl;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.commons.backenddelegate.api.TransactionLimitsBackendDelegate;

public class TransactionLimitsBackendDelegateImpl implements TransactionLimitsBackendDelegate{

	@Override
	public Double fetchConvertedAmount(String currency, String amount, DataControllerRequest request) {
		return Double.parseDouble(amount);
	}
	public Double fetchNewConvertedAmount(Double amount, String fromCurrency, String toCurrency, DataControllerRequest request) {
		return amount;
	}


}
