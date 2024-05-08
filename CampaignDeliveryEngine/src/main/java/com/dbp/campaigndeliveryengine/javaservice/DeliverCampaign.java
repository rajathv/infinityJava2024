package com.dbp.campaigndeliveryengine.javaservice;

import com.dbp.campaigndeliveryengine.resource.api.DeliverCampaignResource;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;

public class DeliverCampaign implements JavaService2 {

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		DeliverCampaignResource dataupdateresource = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(ResourceFactory.class).getResource(DeliverCampaignResource.class);
		return dataupdateresource.deliverCampaign(methodID, inputArray, request, response);
	}
}
