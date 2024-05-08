package com.temenos.dbx.product.signatorygroupservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.signatorygroupservices.resource.api.SignatoryGroupResource;

public class IsSignatoryGroupEligibleForDelete implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(IsSignatoryGroupEligibleForDelete.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		Result result = new Result();
		try {
			//Initializing of signatoryGroupResource through Abstract factory method
			SignatoryGroupResource signatoryGroupResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(SignatoryGroupResource.class);;

			result  = signatoryGroupResource.isEligibleforDelete(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of IsSignatoryGroupEligibleForDelete: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
		
	}

}
