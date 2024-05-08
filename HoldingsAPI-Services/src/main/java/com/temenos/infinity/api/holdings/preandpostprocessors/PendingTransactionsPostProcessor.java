package com.temenos.infinity.api.holdings.preandpostprocessors;

import static com.infinity.dbx.temenos.transactions.TransactionConstants.ERR_EMPTY_RESPONSE;
import static com.infinity.dbx.temenos.transactions.TransactionConstants.PARAM_VALUE_PENDING;
import static com.infinity.dbx.temenos.transactions.TransactionConstants.TRANSACTION;
import static com.infinity.dbx.temenos.transactions.TransactionConstants.TRANS_TYPE_OTHERS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.transactions.TransactionConstants;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.kony.dbx.BasePostProcessor;
import com.kony.dbx.util.CommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class PendingTransactionsPostProcessor extends BasePostProcessor {

	static final Map<Integer, String> TRANSACTION_TYPE = new HashMap<>();
	static {
		TRANSACTION_TYPE.put(1, "NA");
	}

	Logger logger = LogManager.getLogger(PendingTransactionsPostProcessor.class);

	@Override
	public Result execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		try {
			TemenosUtils temenosUtils = TemenosUtils.getInstance();
			temenosUtils.loadTransactionTypeProperties(request);
			Dataset transactionsDS = result.getDatasetById(TRANSACTION);
			List<Record> transactionRecords = transactionsDS != null ? transactionsDS.getAllRecords() : null;
			if (transactionRecords == null || transactionRecords.isEmpty()) {
				logger.debug(ERR_EMPTY_RESPONSE);
				Result transactionResult = TemenosUtils.getEmptyResult(TRANSACTION);
				if (StringUtils.isNotBlank(result.getParamValueByName(TransactionConstants.PARAM_PAGE_START_PENDING))) {
					transactionResult.addParam(TransactionConstants.PARAM_PAGE_START_PENDING,
							result.getParamValueByName(TransactionConstants.PARAM_PAGE_START_PENDING));
				}
				if (StringUtils.isNotBlank(result.getParamValueByName(TransactionConstants.PARAM_PAGE_SIZE_PENDING))) {
					transactionResult.addParam(TransactionConstants.PARAM_PAGE_SIZE_PENDING,
							result.getParamValueByName(TransactionConstants.PARAM_PAGE_SIZE_PENDING));
				}
				if (StringUtils.isNotBlank(result.getParamValueByName(TransactionConstants.PARAM_TOTAL_SIZE_PENDING))) {
					transactionResult.addParam(TransactionConstants.PARAM_TOTAL_SIZE_PENDING,
							result.getParamValueByName(TransactionConstants.PARAM_TOTAL_SIZE_PENDING));
				}
				return transactionResult;
			} else {
				if (transactionRecords.size() != 0) {
					for (Record record : transactionRecords) {
						record.addParam(PARAM_STATUS_DESCRIPTION, PARAM_VALUE_PENDING);
						String transactionType = record.getParamValueByName(PARAM_TRANSACTION_TYPE);
						if (transactionType != StringUtils.EMPTY) {
							transactionType = temenosUtils.transactionTypesMap.get(transactionType);
							if (transactionType == null) {
								transactionType = TRANS_TYPE_OTHERS;
							}
						}
						record.addParam(PARAM_TRANSACTION_TYPE, transactionType);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
			CommonUtils.setErrMsg(result, e.toString());
		}
		return result;
	}
}
