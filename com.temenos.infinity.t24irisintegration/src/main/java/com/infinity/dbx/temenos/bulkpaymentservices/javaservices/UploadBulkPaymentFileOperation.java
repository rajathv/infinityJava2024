package com.infinity.dbx.temenos.bulkpaymentservices.javaservices;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
public class UploadBulkPaymentFileOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(UploadBulkPaymentFileOperation.class);
		
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {	

		return null;
	}

}


