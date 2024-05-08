package com.temenos.infinity.api.wealth.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.ErrorCodeEnum;
import com.temenos.infinity.api.wealth.resource.api.UserPreferenceResource;


public class ModifyHoldingsPreferences implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(ModifyHoldingsPreferences.class);
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		// TODO Auto-generated method stub
		Result result = new Result();
		try {
			UserPreferenceResource userPreferenceResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(UserPreferenceResource.class);
					result  = userPreferenceResource.modifyHoldingsOrder(methodId, inputArray, request, response);
			}
		catch(Exception e){
			LOG.error("Caught exception at invoke of ModifyHoldingsPreferences: ", e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
		return result;
	}

}
