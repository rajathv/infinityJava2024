package com.temenos.dbx.product.bulkpaymentservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.bulkpaymentservices.resource.api.BulkPaymentTemplateResource;

public class DeleteBulkPaymentTemplateOperation implements JavaService2 {
	
	private static final Logger LOG = LogManager.getLogger(DeleteBulkPaymentTemplateOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		Result result;
		
		try {
			//Initializing of BulkPaymentTemplateResource through Abstract factory method
			BulkPaymentTemplateResource bulkPaymentTemplateResource = DBPAPIAbstractFactoryImpl.getResource(BulkPaymentTemplateResource.class);
			result  = bulkPaymentTemplateResource.deleteBulkPaymentTemplate(methodId, inputArray, request, response);
		}
		catch(Exception e){
			LOG.error("Error occured while invoking deleteBulkPaymentTemplate: ", e);
			return ErrorCodeEnum.ERR_28013.setErrorCode(new Result());
		}
		return result;
	}

}
