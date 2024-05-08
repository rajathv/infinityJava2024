package com.temenos.dbx.product.transactionservices.postprocessors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.transactionservices.resource.api.InterBankFundTransferResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class InterBankTransferPostProcessor implements DataPostProcessor2 {
	
	private static final Logger LOG = LogManager.getLogger(InterBankTransferPostProcessor.class);

	@Override
	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {

		try {
			//Initializing of TransactionResource through Abstract factory method
			InterBankFundTransferResource interbankTranscationResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(InterBankFundTransferResource.class);
			
			result  = interbankTranscationResource.processResponseFromLineOfBusiness(result, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking post processor for interbankTransaction: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}

}
