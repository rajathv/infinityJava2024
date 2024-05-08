package com.dbp.campaigndeliveryengine.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.campaigndeliveryengine.resource.api.ServeEventResource;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;

public class ServeEvent implements JavaService2 {
	private static final Logger logger = LogManager.getLogger(ServeEvent.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		ServeEventResource serveeveneresource = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(ResourceFactory.class).getResource(ServeEventResource.class);
		return serveeveneresource.serveEvent(methodID, inputArray, request, response);
	}

}
