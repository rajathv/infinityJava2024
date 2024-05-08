package com.temenos.infinity.api.holdings.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.holdings.resource.api.DisputeTransactionResource;

public class CreateDisputeCardTransaction implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(CreateDisputeCardTransaction.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Result result = new Result();
		try {
			// Initializing of DisputeResource through Abstract factory method
			DisputeTransactionResource ruleResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(DisputeTransactionResource.class);

			result = ruleResource.crearteDisputeCardTransaction(methodID, inputArray, request, response);
		} catch (Exception e) {
			LOG.error("Caught exception at invoke : " + e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
	}

}
