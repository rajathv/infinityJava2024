package com.temenos.infinity.api.chequemanagement.resource.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;


import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.chequemanagement.businessdelegate.api.GetCommandNameBusinessDelegate;
import com.temenos.infinity.api.chequemanagement.resource.api.GetCommandNameResource;
import com.temenos.infinity.api.commons.exception.ApplicationException;

public class GetCommandNameResourceImpl implements GetCommandNameResource {
	private static final Logger LOG = LogManager.getLogger(GetCommandNameResourceImpl.class);

	@Override
	public Result getCommandName(DataControllerRequest request) {
		Result result = new Result();
		GetCommandNameBusinessDelegate getCommandNameBusinessDelegate= DBPAPIAbstractFactoryImpl.getBusinessDelegate(GetCommandNameBusinessDelegate.class);
		Result command=new Result();
		try {
			LOG.debug("Entering GetCommandName resource layer");
			LOG.debug("input params "+request);
			command = getCommandNameBusinessDelegate.getCommandName(request);
//			JSONObject json = new JSONObject(command);
//			result = JSONToResult.convert(json.toString());
			return command;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("exception occured in the getcommandnameresource layer");
			LOG.error(e);
			return ErrorCodeEnum.ERR_26021.setErrorCode(new Result());
		}
		
	}

}
