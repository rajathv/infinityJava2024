package com.infinity.dbx.temenos.paylater;

import static com.infinity.dbx.temenos.transactions.TransactionConstants.ERR_EMPTY_RESPONSE;
import static com.infinity.dbx.temenos.transactions.TransactionConstants.PARAM_VALUE_SUCCESSFUL;
import static com.infinity.dbx.temenos.transactions.TransactionConstants.TRANSACTION;
import static com.infinity.dbx.temenos.transactions.TransactionConstants.TRANS_TYPE_OTHERS;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.transactions.PendingTransactionsPostProcessor;
import com.infinity.dbx.temenos.utils.TemenosUtils;
import com.kony.dbx.BasePostProcessor;
import com.kony.dbx.util.CommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetPostedTransactionsPostProcessor extends BasePostProcessor {
    @Override
    public Result execute(Result result, DataControllerRequest request, DataControllerResponse response)
            throws Exception {
        Logger logger = LogManager.getLogger(PendingTransactionsPostProcessor.class);

        try {
            TemenosUtils temenosUtils = TemenosUtils.getInstance();
            temenosUtils.loadTransactionTypeProperties(request);
            Dataset transactionsDS = result.getDatasetById(TRANSACTION);
            List<Record> transactionRecords = transactionsDS != null ? transactionsDS.getAllRecords() : null;
            if (transactionRecords.isEmpty()) {
                logger.debug(ERR_EMPTY_RESPONSE);
                return TemenosUtils.getEmptyResult(TRANSACTION);
            } else {
                if (transactionRecords.size() != 0) {
                    String coreResponse = response.getResponse();
                    // JSONObject coreJsonObject = new JSONObject(coreResponse);
                    for (int i = 0; i < transactionRecords.size(); i++) {
                        Record record = transactionRecords.get(i);
                        // JSONObject recordJsonObject = coreJsonObject.getJSONArray("body").getJSONObject(i);
                        // if (recordJsonObject.has(TransactionConstants.PARAM_DEBIT)) {
                        // record.addParam(TransactionConstants.PARAM_DEBIT,
                        // String.valueOf(recordJsonObject.getDouble(TransactionConstants.PARAM_DEBIT)));
                        // }
                        // if (recordJsonObject.has(TransactionConstants.PARAM_CREDIT)) {
                        // record.addParam(TransactionConstants.PARAM_CREDIT,
                        // String.valueOf(recordJsonObject.getDouble(TransactionConstants.PARAM_CREDIT)));
                        // }
                        record.addParam(PARAM_STATUS_DESCRIPTION, PARAM_VALUE_SUCCESSFUL);
                        String transactionType = record.getParamValueByName(PARAM_TRANSACTION_TYPE);
                        String description = StringUtils.EMPTY;
                        String transactionDate = record.getParamValueByName(PayLaterConstants.PARAM_TRANS_DATE);
                        transactionDate = temenosUtils.getDateFormat(transactionDate, PayLaterConstants.DATE_FORMAT);
                        record.addParam(PayLaterConstants.PARAM_TRANS_DATE, transactionDate);
                        String transactionDescription = record.getParamValueByName(PayLaterConstants.PARAM_REM_INF);
                        if (StringUtils.isNotBlank(transactionDescription)) {
                            description = transactionDescription;
                        }
                        String transactionDescription1 = record.getParamValueByName(PayLaterConstants.PARAM_REM_INF1);
                        if (StringUtils.isNotBlank(transactionDescription1)) {
                            description = description + " " + transactionDescription1;
                        }

                        record.addParam(PayLaterConstants.PARAM_DESCRIPTION, description);
                        record.removeParamByName(PayLaterConstants.PARAM_REM_INF);
                        record.removeParamByName(PayLaterConstants.PARAM_REM_INF1);
                        if (transactionType != StringUtils.EMPTY) {
                            transactionType = temenosUtils.transactionTypesMap.get(transactionType);
                            if (transactionType == null) {
                                transactionType = TRANS_TYPE_OTHERS;
                            }
                        }
                        // String debit = CommonUtils.getParamValue(record, TransactionConstants.PARAM_DEBIT);
                        // if(!"".equalsIgnoreCase(debit)) {
                        // record.addParam(PARAM_AMOUNT, debit);
                        // }
                        // else {
                        // record.addParam(PARAM_AMOUNT, CommonUtils.getParamValue(record,
                        // TransactionConstants.PARAM_CREDIT));
                        // }
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
