package com.temenos.dbx.product.achservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.achservices.resource.api.ACHTemplateResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class DeleteACHTemplateOperation implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(DeleteACHTemplateOperation.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {

		Result result = new Result();
		
		try {
			//Initializing of ACHTemplateResource through Abstract factory method
			ACHTemplateResource achTemplateResource = DBPAPIAbstractFactoryImpl.getResource(ACHTemplateResource.class);
			result  = achTemplateResource.deleteACHTemplate(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking deleteACHTemplate: ", e);
			return ErrorCodeEnum.ERR_21013.setErrorCode(new Result());
		}
		
		return result;
	}
}
