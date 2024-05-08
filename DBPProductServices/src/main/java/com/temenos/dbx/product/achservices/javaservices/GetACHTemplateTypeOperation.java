
package com.temenos.dbx.product.achservices.javaservices;

import com.dbp.core.api.factory.ResourceFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.achservices.resource.api.ACHTemplateResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class GetACHTemplateTypeOperation implements JavaService2 {

	@Override
	public Object invoke(String methodId, Object[] inputParams, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		final Logger LOG = LogManager.getLogger(GetACHTemplateTypeOperation.class);
		Result result;
		try 
		{	
			ACHTemplateResource achResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(ACHTemplateResource.class);
			result = achResource.getACHTemplateType(inputParams, request);
		} 
		catch(Exception e) 
		{
			LOG.error("Error occured while invoking getBBTemplatesType: ",e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
	}

}
