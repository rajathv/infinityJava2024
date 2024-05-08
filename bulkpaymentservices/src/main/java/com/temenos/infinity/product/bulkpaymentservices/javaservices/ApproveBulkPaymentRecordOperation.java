package com.temenos.infinity.product.bulkpaymentservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.product.bulkpaymentservices.resource.api.BulkPaymentRecordResource;

public class ApproveBulkPaymentRecordOperation implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(ApproveBulkPaymentRecordOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) {
		
		Result result;
		
		try {
			//Initializing of BulkPaymentRecordResource through Abstract factory method
			BulkPaymentRecordResource bulkPaymentRecordResource = DBPAPIAbstractFactoryImpl.getResource(BulkPaymentRecordResource.class);
			result = bulkPaymentRecordResource.approveBulkPaymentRecord(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking approveBulkPaymentRecord: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}

}
