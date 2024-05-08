package com.temenos.dbx.product.achservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.achservices.resource.api.ACHTransactionResource;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class FetchACHTransactionByIdOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(FetchAllACHTemplatesOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		Result result = new Result();
		try {
			ACHTransactionResource achResource = DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
					.getResource(ACHTransactionResource.class);
			result = achResource.fetchACHTransactionById(methodID, inputArray, request, response);
		}
		catch(Exception exp) {
			LOG.error("Exception occued while fetching an ach transaction",exp);
			return null;
		}
		return result;
	}

}