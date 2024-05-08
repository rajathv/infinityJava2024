package com.temenos.dbx.bulkpaymentservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.bulkpaymentservices.utilities.BulkPaymentSampleFilesDBOperation;
import com.temenos.dbx.bulkpaymentservices.utilities.CancellationReasonDBOperation;

public class FetchCancellationReasons implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(FetchCancellationReasons.class);
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		// TODO Auto-generated method stub
		Result result;	
		
		try {
			CancellationReasonDBOperation bulkPaymentCancellationReasonsDBOperation = new CancellationReasonDBOperation();
			result = bulkPaymentCancellationReasonsDBOperation.fetchCancellationReasons();
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking fetchCancellationReasons", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}

}
