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

public class GetTransactionsByBulkWireTemplateExecutionId implements JavaService2
{
	private static final Logger LOG = LogManager.getLogger(GetTransactionsByBulkWireTemplateExecutionId.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			BulkWireTransactionsResource bulkWireTransactionsResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(BulkWireTransactionsResource.class);
			
			result  = bulkWireTransactionsResource.getTransactionsByBulkWireTemplateExecutionId(methodID, inputArray, request, response);
		}catch (Exception e) {
			LOG.error("Error occured while invoking getTransactionsByBulkWireTemplateExecutionId operation: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		return result;
	}

}
