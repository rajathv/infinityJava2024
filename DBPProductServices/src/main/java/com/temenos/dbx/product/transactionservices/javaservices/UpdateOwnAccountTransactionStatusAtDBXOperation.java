package com.temenos.dbx.product.transactionservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.transactionservices.resource.api.OwnAccountFundTransferResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * @author KH2626 
 * @version 1.0
 * Java service end-point to update any transaction entry in ownaccounttransaction table.
 **/

public class UpdateOwnAccountTransactionStatusAtDBXOperation implements JavaService2  {

	private final static Logger LOG = LogManager.getLogger(UpdateOwnAccountTransactionStatusAtDBXOperation.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		Result result = new Result();
		
		try {
			//Initialize resource required to update the ownaccounttransfers table
			OwnAccountFundTransferResource ownaccountTransactionResource = 
					DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
					.getResource(OwnAccountFundTransferResource.class);
			
			result = ownaccountTransactionResource.updateStatus(methodID,inputArray,request,response);
		}
		catch(Exception exp) {
			LOG.error("Exception occured while updating ownaccounttransaction",exp);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		return result;
	}
}