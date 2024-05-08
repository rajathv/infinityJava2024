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
import com.temenos.dbx.product.payeeservices.resource.api.WireTransfersPayeeResource;

/**
 * 
 * @author KH2660
 * @version 1.0
 * Java Service end point to create wire transfer Payee*
 */
public class CreateWireTransfersPayeeOperation implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(CreateWireTransfersPayeeOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			//Initializing of wireTransfersPayeeResource through Abstract factory method
			WireTransfersPayeeResource wireTransfersPayeeResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(WireTransfersPayeeResource.class);;

			result  = wireTransfersPayeeResource.createPayee(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of CreateWireTransfersPayeeOperation: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;

	}
}
