package com.temenos.msArrangement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.msArrangement.resource.api.AccountTransactionsResource;


/**
 * 
 * @author KH2281
 * @version 1.0
 * Java Service end point to fetch all the transactions of a particular account
 */

public class GetAccountTransactionsOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(GetAccountTransactionsOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			//Initializing of AccountTransactions through Abstract factory method
			AccountTransactionsResource AccountTransactionsResource = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(ResourceFactory.class).getResource(AccountTransactionsResource.class);

			result = AccountTransactionsResource.getAccountTransactions(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of GetAccountTransactionsOperation: "+e);
		}

        return result; 
	}

}
