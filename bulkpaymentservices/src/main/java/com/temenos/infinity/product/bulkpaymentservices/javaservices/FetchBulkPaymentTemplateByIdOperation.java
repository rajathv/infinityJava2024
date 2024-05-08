package com.temenos.infinity.product.bulkpaymentservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.product.bulkpaymentservices.resource.api.BulkPaymentTemplateResource;

public class FetchBulkPaymentTemplateByIdOperation implements JavaService2 {

private static final Logger LOG = LogManager.getLogger(FetchBulkPaymentTemplateByIdOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result;

		try {
			// Initializing of BulkPaymentPOResource through Abstract factory method
			BulkPaymentTemplateResource bulkPaymentTemplateResource = DBPAPIAbstractFactoryImpl.getResource(BulkPaymentTemplateResource.class);
			result = bulkPaymentTemplateResource.fetchTemplateByTemplateId(methodID, inputArray, request, response);
		} catch (Exception e) {
			LOG.error("Error occured while invoking fetchTemplateByTemplateId: ", e);
			return ErrorCodeEnum.ERR_28018.setErrorCode(new Result());
		}

		return result;
	}
}
