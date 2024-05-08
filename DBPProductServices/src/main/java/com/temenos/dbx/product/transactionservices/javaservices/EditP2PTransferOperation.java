package com.temenos.dbx.product.transactionservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.transactionservices.resource.api.P2PTransactionResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;


/**
 * 
 * @author KH1755
 * @version 1.0
 * implements {@link JavaService2}
 */
public class EditP2PTransferOperation implements JavaService2{

	private static final Logger LOG = LogManager.getLogger(EditP2PTransferOperation.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) throws Exception {

		Result result = new Result();
		
		try {
			//Initializing of TransactionResource through Abstract factory method
			P2PTransactionResource p2pTransactionResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(P2PTransactionResource.class);
			
			result  = p2pTransactionResource.editTransaction(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking EditP2PTransferOperation: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
	}
}
