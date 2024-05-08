package com.temenos.dbx.product.bulkpaymentservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.bulkpaymentservices.resource.api.BulkPaymentRecordResource;

public class FetchRecordsReviewedByMeAndInApprovalQueueOperation implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(FetchRecordsReviewedByMeAndInApprovalQueueOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = new Result();

		try {
			//Initializing of FetchRecordsWaitingForMyApproval through Abstract factory method
			BulkPaymentRecordResource bulkPaymentRecordResource = DBPAPIAbstractFactoryImpl.getResource(BulkPaymentRecordResource.class);

			result  = bulkPaymentRecordResource.fetchRecordsReviewedByMeAndInApprovalQueue(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking fetchRecordsReviewedByMeAndInApprovalQueue: ", e);
			return ErrorCodeEnum.ERR_21247.setErrorCode(new Result());
		}

		return result;
	}

}
