package com.temenos.dbx.bulkpaymentservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.temenos.dbx.product.constants.TransactionStatusEnum;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.bulkpaymentservices.utilities.BulkPaymentPODBOperations;

public class ApproveBulkPaymentOrder implements JavaService2  {

	private static final Logger LOG = LogManager.getLogger(ApproveBulkPaymentOrder.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) {
		Result result = new Result();
		try {		
			BulkPaymentPODBOperations bulkPaymentPODBOperations = new BulkPaymentPODBOperations();			
			String paymentOrderId = dcRequest.getParameter("paymentOrderId");		
			
			result  = bulkPaymentPODBOperations.updateBulkPaymentOrderStatus(paymentOrderId, TransactionStatusEnum.APPROVED.getStatus());				
			
		} catch (Exception e) {
			LOG.error("Error occured while invoking updatePaymentOrder at Backend: ", e);
			return ErrorCodeEnum.ERR_21212.setErrorCode(new Result());
		}
		return result;
	}

}
