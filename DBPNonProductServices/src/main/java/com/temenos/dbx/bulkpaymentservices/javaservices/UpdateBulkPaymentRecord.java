package com.temenos.dbx.bulkpaymentservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.bulkpaymentservices.utilities.BulkPaymentRecordDBOperations;

public class UpdateBulkPaymentRecord implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(UpdateBulkPaymentRecord.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result;

		try {
			BulkPaymentRecordDBOperations bulkPaymentRecordDBOperations = new BulkPaymentRecordDBOperations();
			String recordId = request.getParameter("recordId");
			String description = request.getParameter("description");
			String fromAccount = request.getParameter("fromAccount");
			
			result  = bulkPaymentRecordDBOperations.updateBulkPaymentRecord(recordId, description ,fromAccount);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking updateBulkPaymentRecord: ", e);
			return ErrorCodeEnum.ERR_21237.setErrorCode(new Result());
		}	
		return result;
	}
}
