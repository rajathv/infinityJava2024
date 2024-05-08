    package com.temenos.dbx.product.javaservice;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.IntegrationTemplateURLFinder;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.resource.api.CustomerResource;

public class UpdateCustomerInParty implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(UpdateCustomerInParty.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		 Result result = new Result();
		 final String IS_Integrated = Boolean.toString(IntegrationTemplateURLFinder.isIntegrated);
		 if (StringUtils.isNotBlank(IS_Integrated) && IS_Integrated.equalsIgnoreCase("true")) {
			 
			 return result;
			 
		 }
	        try {
	            CustomerResource customerResource = DBPAPIAbstractFactoryImpl.getInstance()
	                    .getFactoryInstance(ResourceFactory.class).getResource(CustomerResource.class);
	     //       result = customerResource.updateInParty(methodID, inputArray, dcRequest, dcResponse);
	        } catch (Exception e) {
	            LOG.error("Caught exception while creating Customer: " + e);
	        }

	        return result;
	}
	
}