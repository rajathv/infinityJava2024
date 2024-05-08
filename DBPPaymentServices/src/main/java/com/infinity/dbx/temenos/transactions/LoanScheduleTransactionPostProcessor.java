package com.infinity.dbx.temenos.transactions;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbx.BasePostProcessor;
import com.kony.dbx.util.CommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class LoanScheduleTransactionPostProcessor extends BasePostProcessor {
	private static final Logger LOG = LogManager.getLogger(LoanScheduleTransactionPostProcessor.class);
	@Override
	public Result execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		try {
			Dataset dataset = new Dataset();
			Record record = new Record();
			dataset.setId("Meta");
			if (StringUtils.isNotBlank(result.getParamValueByName(TransactionConstants.PARAM_TOTALSIZE)))
				record.addParam(new Param(TransactionConstants.PARAM_TOTALSIZE, result.getParamValueByName(TransactionConstants.PARAM_TOTALSIZE)));
			if (StringUtils.isNotBlank(result.getParamValueByName(TransactionConstants.PARAM_PAGESIZE)))
				record.addParam(new Param(TransactionConstants.PARAM_PAGESIZE, result.getParamValueByName(TransactionConstants.PARAM_PAGESIZE)));
			if (StringUtils.isNotBlank(result.getParamValueByName(TransactionConstants.PARAM_PAGESTART)))
				record.addParam(new Param(TransactionConstants.PARAM_PAGESTART, result.getParamValueByName(TransactionConstants.PARAM_PAGESTART)));
			dataset.addRecord(record);
			result.addDataset(dataset);
			return result;
		} catch (Exception e) {
			LOG.error(e);
			CommonUtils.setErrMsg(result, e.toString());
		}
		return result;

	}
}