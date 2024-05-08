package com.temenos.infinity.api.wealth.preandpostprocessors;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.kony.dbputilities.util.CommonUtils;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.utils.Utilities;
import com.temenos.infinity.api.wealth.backenddelegate.impl.AccountActivityBackendDelegateImpl;
import com.temenos.infinity.api.wealth.config.PortfolioWealthAPIServices;
import com.temenos.infinity.api.wealthservices.constants.TemenosConstants;
import com.temenos.infinity.api.wealth.util.WealthMockUtil;

public class GetAccountsActivityPostProcessor implements DataPostProcessor2 {

	WealthMockUtil wealthMockUtil = new WealthMockUtil();
	AccountActivityBackendDelegateImpl accountActivityBackendDelegateImpl = new AccountActivityBackendDelegateImpl();
	private static final Logger LOG = LogManager.getLogger(GetAccountsActivityPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		try {
			JSONObject accountsActivityJSON = new JSONObject();

			Record headerRecord = result.getRecordById("header");
			String status = headerRecord.getParamValueByName("status");

			if (status != null && status.trim().equalsIgnoreCase("success")) {

				Dataset bodyDataset = result.getDatasetById("body");
				List<Record> records = bodyDataset.getAllRecords();
				JSONArray bodyArray = new JSONArray();
				for (int j = 0; j < records.size(); j++) {
					JSONObject transactionListJSONObject = new JSONObject();
					Record record = records.get(j);
					transactionListJSONObject = CommonUtils.convertRecordToJSONObject(record);
					bodyArray.put(transactionListJSONObject);
				}

				String[] responseFields = new String[] { TemenosConstants.ACCID, TemenosConstants.AMOUNT,
						TemenosConstants.QUANTITY, TemenosConstants.BALANCE, TemenosConstants.DISPLAYNAME,
						TemenosConstants.BOOKINGDATE, TemenosConstants.VALUEDATE, TemenosConstants.SHORTNAME,
						TemenosConstants.CURRENCYID,TemenosConstants.HOLDINGS_TYPE, TemenosConstants.TRANSACTIONREFERENCE, "ISIN" };

				JSONArray accountsActivityArr = new JSONArray();
				for (int i = 0; i < bodyArray.length(); i++) {
					JSONObject responseObject = bodyArray.getJSONObject(i);
					for (String field : responseFields) {
						if (responseObject.has(field)) {
							responseObject.put(field, responseObject.get(field));
						} else {
							responseObject.put(field, "");
						}

					}
					accountsActivityArr.put(responseObject);

				}

				accountsActivityJSON.put("accountActivityList", accountsActivityArr);
				accountsActivityJSON.put("portfolioID", request.getParameter("portfolioId"));
				accountsActivityJSON.put("accountId", request.getParameter("accountId"));
				accountsActivityJSON.put(TemenosConstants.DATETO, request.getParameter("dateTo"));
				accountsActivityJSON.put(TemenosConstants.DATEFROM, request.getParameter("dateFrom"));

				Result accountsActivityResult = Utilities.constructResultFromJSONObject(accountsActivityJSON);
				accountsActivityResult.addOpstatusParam("0");
				accountsActivityResult.addHttpStatusCodeParam("200");
				accountsActivityResult.addParam(TemenosConstants.STATUS, TemenosConstants.SUCCESS);
				return accountsActivityResult;
			} else {
				Record errorRecord = result.getRecordById("error");
				accountsActivityJSON.put("errormessage", errorRecord.getParamValueByName("message"));
				accountsActivityJSON.put("errorcode", errorRecord.getParamValueByName("code"));
				Result errorResponse = Utilities.constructResultFromJSONObject(accountsActivityJSON);
				return errorResponse;	
			}
		} catch (Exception e) {
			LOG.error("Error while invoking GetTransactionDetailsPostProcessor - "
					+ PortfolioWealthAPIServices.WEALTHSERVICESORCHESTRATION.getOperationName() + "  : " + e);
		}
		return response;
	}
}
