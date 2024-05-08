package com.temenos.infinity.api.wealthOrder.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.ErrorCodeEnum;
import com.temenos.infinity.api.wealthOrder.resource.api.UserFavouriteInstrumentsResource;


public class UpdateUserFavouriteInstruments implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(UpdateUserFavouriteInstruments.class);
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		// TODO Auto-generated method stub
		Result result = new Result();
		try {
			UserFavouriteInstrumentsResource userInstruments = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(UserFavouriteInstrumentsResource.class);
					result  = userInstruments.updateFavouriteInstruments(methodId, inputArray, request, response);
			}
		catch(Exception e){
			LOG.error("Caught exception at invoke of GetUserFavouriteInstruments: ", e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
		return result;
	}

}
