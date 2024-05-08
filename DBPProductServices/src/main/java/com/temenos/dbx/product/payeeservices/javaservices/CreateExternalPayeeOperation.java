package com.temenos.dbx.product.payeeservices.javaservices;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.payeeservices.resource.api.ExternalPayeeResource;

/**
 * @author KH2638
 * @version 1.0
 * Java Service end point to Create External Payee
 */
public class CreateExternalPayeeOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(CreateExternalPayeeOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {

		Result result = new Result();
		try {
			//Initializing of ExternalPayeeResource through Abstract factory method
			ExternalPayeeResource externalPayeeResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(ExternalPayeeResource.class);;

					result  = externalPayeeResource.createPayee(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of CreateExternalPayeeOperation: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
	}
}