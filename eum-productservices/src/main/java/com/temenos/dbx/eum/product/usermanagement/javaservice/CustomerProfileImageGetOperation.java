package com.temenos.dbx.eum.product.usermanagement.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.javaservice.CustomerProfileImageGetOperation;
import com.temenos.dbx.eum.product.usermanagement.resource.api.CustomerImageResource;

public class CustomerProfileImageGetOperation implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(CustomerProfileImageGetOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			CustomerImageResource customerImageResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(CustomerImageResource.class);
			result = customerImageResource.getCustomerImage(methodID, inputArray, dcRequest, dcResponse);
		} catch (Exception e) {
			LOG.error("Exception occured while calling the customerImageResouce" + e.getMessage());
		}
		return result;
	}

}