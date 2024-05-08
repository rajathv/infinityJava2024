package com.temenos.dbx.product.achservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.achservices.resource.api.ACHTemplateResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class EditACHTemplateOperation implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(EditACHTemplateOperation.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, 
			DataControllerResponse response) {

		Result result = new Result();
		try {
			//Initializing of ACHTemplateResource through Abstract factory method
			ACHTemplateResource achResource = DBPAPIAbstractFactoryImpl.getResource(ACHTemplateResource.class);
			result  = achResource.editACHTemplate(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking updateACHTemplate: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		return result;
	}

}
