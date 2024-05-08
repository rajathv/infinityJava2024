package com.temenos.dbx.product.transactionservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.transactionservices.resource.api.RDCResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class CreateRDCOperation implements JavaService2{

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		Logger LOG = LogManager.getLogger(CreateRDCOperation.class);
		
		Result result = null;
		
		try {
		
			RDCResource rdcResource = 
					DBPAPIAbstractFactoryImpl.getInstance().getFactoryInstance(ResourceFactory.class)
					.getResource(RDCResource.class);
			result = rdcResource.createRDC(methodID,inputArray,request,response);
			
		}
		catch(Exception exp) {
			LOG.error("Exception occured in CreateRDCOpertaion", exp);
			return ErrorCodeEnum.ERR_12611.setErrorCode(new Result());
		}
		
		return result;
		
	}

}
