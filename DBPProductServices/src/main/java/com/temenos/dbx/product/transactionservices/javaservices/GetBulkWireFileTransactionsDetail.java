package com.temenos.dbx.product.transactionservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.transactionservices.resource.api.BulkWireTransactionsResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * 
 * @author KH2264
 * @version 1.0
 * Java Service end point to fetch all the transactions of a file
 */
public class GetBulkWireFileTransactionsDetail implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(GetBulkWireFileTransactionsDetail.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) throws Exception {

		Result result = new Result();
		try {
			//Initializing of BulkWireTransactionsResource through Abstract factory method
			BulkWireTransactionsResource bulkWireTransactionsResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(BulkWireTransactionsResource.class);
			
		    result  = bulkWireTransactionsResource.getBulkWireFileTransactionsDetail(methodID, inputArray, request, response);
		}
		catch(Exception e)
		{
			LOG.error("Error occured while fetching  Bulk wire trasnactions for the file: ", e);
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		return result;
	}
}
