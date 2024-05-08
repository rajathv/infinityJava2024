package com.temenos.dbx.product.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.resource.api.FeatureResource;

/**
 * 
 * @author sowmya.vandanapu
 * @version Fetches the features at the level of FI
 *
 */

public class FeaturesGetOperation implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(FeaturesGetOperation.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		Result result = new Result();
		try {
			FeatureResource approverResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(FeatureResource.class);
			result = approverResource.getFeatures(methodID, inputArray, dcRequest, dcResponse);
		} catch (Exception e) {
			LOG.error("Exception occured while fetching the features" + e.getMessage());
		}
		return result;
	}
}
