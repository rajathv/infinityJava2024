package com.temenos.infinity.api.docmanagement.businessdelegate.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.api.docmanagement.businessdelegate.api.DMSBusinessDelegate;
import com.temenos.infinity.api.transactionadvice.backenddelegate.api.TransactionAdviceAPIBackendDelegate;

import org.json.JSONObject;

public class DMSBusinessDelegateImpl implements DMSBusinessDelegate {
	private static final Logger LOG = LogManager.getLogger(DMSBusinessDelegateImpl.class);

	@Override
	public String download(String documentId, String reference, String authToken) {
		TransactionAdviceAPIBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl

				.getBackendDelegate(TransactionAdviceAPIBackendDelegate.class);
		byte[] output = backendDelegate.download(documentId, reference, authToken);
		String encoded = Base64.getEncoder().encodeToString(output);
		return encoded;
	}

	@Override
	public ArrayList<JSONObject> search(DataControllerRequest request,String page, String accountNnumber, String customerNumber, String year,
			String subType, String authToken)throws ApplicationException {
		TransactionAdviceAPIBackendDelegate backendDelegate = DBPAPIAbstractFactoryImpl
				.getBackendDelegate(TransactionAdviceAPIBackendDelegate.class);

		HashMap<String, Object> paramMap = new HashMap<>();
		paramMap.put("page", page);
		paramMap.put("accountNumber", accountNnumber);
		paramMap.put("customerNumber", customerNumber);
		paramMap.put("year", year);
		paramMap.put("subType", subType);

		ArrayList<JSONObject> output = new ArrayList<JSONObject>();
		try {
			output = backendDelegate.search(request,paramMap, authToken);
		} catch (ApplicationException e) {
			LOG.error(e);
			throw e;
		}
		catch (Exception e) {
			LOG.error(e);
			throw e;
		}
		return output;
	}
}