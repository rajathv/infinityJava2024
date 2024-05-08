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


public class DownloadBulkPaymentAckOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(DownloadBulkPaymentAckOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();
		try {
			BulkPaymentFileResource bulkPaymentFileResource = DBPAPIAbstractFactoryImpl.getResource(BulkPaymentFileResource.class);

			result  = bulkPaymentFileResource.downloadBulkPaymentAckFile(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking download Bulk payment details pdf: ", e);
			return ErrorCodeEnum.ERR_21252.setErrorCode(new Result());
		}
		return result;
	}
}