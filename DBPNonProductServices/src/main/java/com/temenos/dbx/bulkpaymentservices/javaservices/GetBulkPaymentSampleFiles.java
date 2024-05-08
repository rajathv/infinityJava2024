package com.temenos.dbx.bulkpaymentservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.bulkpaymentservices.utilities.BulkPaymentSampleFilesDBOperation;

public class GetBulkPaymentSampleFiles implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(GetBulkPaymentSampleFiles.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		
		Result result;	
		
		try {
			BulkPaymentSampleFilesDBOperation bulkPaymentSampleFilesDBOperation = new BulkPaymentSampleFilesDBOperation();
			result = bulkPaymentSampleFilesDBOperation.getBulkPaymentSampleFiles();
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking getBulkPaymentSampleFiles", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}

}
