package com.temenos.infinity.api.chequemanagement.javaservice;
import com.temenos.infinity.api.chequemanagement.resource.api.GetCommandNameResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;


public class GetCommandName implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(GetCommandName.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		try {
			LOG.debug("Entering GetCommandName java layer");
			LOG.debug("input params "+request);
			GetCommandNameResource getCommandNameResource = DBPAPIAbstractFactoryImpl.getResource(GetCommandNameResource.class);
			Result result =  getCommandNameResource.getCommandName(request);	
			return result;
		}
		catch (Exception e) { 
			LOG.error("exception occured in the getcommandname java layer");
			LOG.error(e);
			return ErrorCodeEnum.ERR_26021.setErrorCode(new Result()); 
		}
	}
}
