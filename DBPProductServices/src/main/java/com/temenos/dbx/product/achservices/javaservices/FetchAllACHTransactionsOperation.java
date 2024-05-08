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

public class FetchAllACHTransactionsOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(FetchAllACHTemplatesOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		Result result = new Result();
		try {
			ACHTransactionResource achResource = DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
					.getResource(ACHTransactionResource.class);
			result = achResource.fetchAllACHTransactions(methodID, inputArray, request, response);
		}
		catch(Exception exp) {
			LOG.error("Exception occued while fetching transactions",exp);
			return null;
		}
		return result;
	}

}