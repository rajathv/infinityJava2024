package com.infinity.dbx.temenos.transactions;

import static com.infinity.dbx.temenos.transactions.TransactionConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbx.util.CommonUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Result;

public class GetAccountTransactionByType implements JavaService2 {

	@Override
	public Object invoke(String methodId, Object[] inputMap, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Logger logger = LogManager.getLogger(GetAccountTransactionByType.class);
		Result result = new Result();
		try {
			Dataset Transactions = new Dataset(TRANSACTION);
			String transactionType = request.getParameter(PARAM_TRANSACTION_TYPE);
			String isScheduled = request.getParameter(PARAM_IS_SCHEDULED);
			if (StringUtils.isNotBlank(isScheduled) && TRUE.equalsIgnoreCase(isScheduled))
				result = (Result) CommonUtils.callIntegrationService(request, null, null, SERVICE_ID_ORCH,
						OPER_ID_SCHEDULED, true);
			
			else if (StringUtils.isNotBlank(transactionType)) {
				if (transactionType.equalsIgnoreCase("loanschedule")) {
					Map<String,Object> inputparam=new HashMap<String, Object>();
					if(inputMap.length >1) {
						inputparam=(Map<String,Object>) inputMap[1];
					}
					request.addRequestParam_("isFutureRequired", "false");
					result = (Result) CommonUtils.callIntegrationService(request,inputparam, null, SERVICE_ID_TRANSACTIONS,
							OPER_ID_LOANSCHEDULE, true);
				} else {
					result = (Result) CommonUtils.callIntegrationService(request, null, null, SERVICE_ID_ORCH,
							OPER_ID_TRANSACTIONS, true);
				}
			} else {
				result.addDataset(Transactions);
			}

		} catch (Exception e) {
			logger.error(e);
			CommonUtils.setErrMsg(result, e.toString());
		}
		return result;
	}

}
