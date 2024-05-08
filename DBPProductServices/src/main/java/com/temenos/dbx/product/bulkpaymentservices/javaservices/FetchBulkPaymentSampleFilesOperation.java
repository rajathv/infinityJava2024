package com.temenos.dbx.product.bulkpaymentservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.bulkpaymentservices.resource.api.BulkPaymentFileResource;

public class FetchBulkPaymentSampleFilesOperation implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(FetchBulkPaymentSampleFilesOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) throws Exception {
		
		Result result;
		
		try {
			//Initializing of BulkPaymentFileResource through Abstract factory method
			BulkPaymentFileResource bulkPaymentFileResource = DBPAPIAbstractFactoryImpl.getResource(BulkPaymentFileResource.class);
			result = bulkPaymentFileResource.fetchBulkPaymentSampleFiles(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking fetchBulkPaymentSampleFiles: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}

}
