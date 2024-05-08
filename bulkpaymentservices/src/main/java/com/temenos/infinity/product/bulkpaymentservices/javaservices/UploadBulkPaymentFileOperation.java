package com.temenos.infinity.product.bulkpaymentservices.javaservices;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.product.bulkpaymentservices.resource.api.BulkPaymentFileResource;
public class UploadBulkPaymentFileOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(UploadBulkPaymentFileOperation.class);
		
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();

		try {
			//Initializing of uploadBulkPaymentFileResource through Abstract factory method
			BulkPaymentFileResource bulkPaymentFileResource = DBPAPIAbstractFactoryImpl.getResource(BulkPaymentFileResource.class);

			result  = bulkPaymentFileResource.uploadBulkPaymentFile(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking uploadBulkPaymentFile: ", e);
			return ErrorCodeEnum.ERR_21225.setErrorCode(new Result());
		}

		return result;
	}

}


