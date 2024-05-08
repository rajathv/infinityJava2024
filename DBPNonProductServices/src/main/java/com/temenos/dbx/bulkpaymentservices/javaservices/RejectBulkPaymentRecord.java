package com.temenos.dbx.bulkpaymentservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.bulkpaymentservices.utilities.BulkPaymentRecordDBOperations;

public class RejectBulkPaymentRecord implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(RejectBulkPaymentRecord.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result;

		try {
			BulkPaymentRecordDBOperations bulkPaymentRecordDBOperations = new BulkPaymentRecordDBOperations();
			String recordId = request.getParameter("recordId");					
			String comments = request.getParameter("comments");
			String rejectionreason = request.getParameter("rejectionreason");
			result  = bulkPaymentRecordDBOperations.updateRejectedBulkPaymentRecordStatus(recordId, comments, rejectionreason, TransactionStatusEnum.REJECTED.getStatus());
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking rejectBulkPaymentRecord: ", e);
			return ErrorCodeEnum.ERR_21237.setErrorCode(new Result());
		}	
		return result;
	}
}
