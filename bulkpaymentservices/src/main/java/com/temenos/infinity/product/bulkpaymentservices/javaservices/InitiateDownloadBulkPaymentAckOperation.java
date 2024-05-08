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


public class InitiateDownloadBulkPaymentAckOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(InitiateDownloadBulkPaymentAckOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();
		try {
			BulkPaymentFileResource bulkPaymentFileResource = DBPAPIAbstractFactoryImpl.getResource(BulkPaymentFileResource.class);

			result  = bulkPaymentFileResource.initiateBulkPaymentAckFile(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking initiate download for Bulk payment details pdf: ", e);			
			return ErrorCodeEnum.ERR_21253.setErrorCode(new Result());
		}
		return result;
	}
}