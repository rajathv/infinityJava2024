package com.temenos.infinity.api.QRPayments.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.QRPayments.constants.ErrorCodeEnum;
import com.temenos.infinity.api.QRPayments.resource.api.QRPaymentResource;

public class CreateQRPaymentOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(CreateQRPaymentOperation.class);
	Result result = new Result();

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		try {
			// Initializing of QRPaymentResource through Abstract factory method
			QRPaymentResource QRPayment = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(QRPaymentResource.class);
			result = QRPayment.createQRPayment(methodID, inputArray, request, response);
			return result;
		}

		catch (Exception e) {
			LOG.error(e);
			return ErrorCodeEnum.ERR_20041.setErrorCode(new Result());
		}
	}
}
