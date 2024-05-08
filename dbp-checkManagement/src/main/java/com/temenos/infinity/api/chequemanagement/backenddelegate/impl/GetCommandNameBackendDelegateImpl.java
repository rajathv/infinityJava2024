package com.temenos.infinity.api.chequemanagement.backenddelegate.impl;

import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.infinity.api.chequemanagement.backenddelegate.api.GetCommandNameBackendDelegate;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.api.commons.invocation.Executor;
import com.temenos.infinity.api.chequemanagement.constants.GetCommandUrl;
import com.temenos.infinity.api.chequemanagement.javaservice.WithdrawChequeOperation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.LegalEntityUtil;
import com.kony.dbputilities.util.URLFinder;
import com.kony.dbx.util.CommonUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.config.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.LegalEntityUtil;

public class GetCommandNameBackendDelegateImpl implements GetCommandNameBackendDelegate {
	private static final Logger LOG = LogManager.getLogger(GetCommandNameBackendDelegateImpl.class);

	@Override
	public Result getCommandName(DataControllerRequest request) throws ApplicationException {
		// TODO Auto-generated method stub

		 final String command;
			Result result = new Result();
	try {
	    InputStream input = null;
		LOG.debug("Entering GetCommandNameBackenddelegate");
	    String type=request.getParameter("type");
	    String serviceName = new String();
		String operationName = new String();
		HashMap<String, Object> headerParams = new HashMap<String, Object>();
		HashMap<String, Object> inputParams = new HashMap<String, Object>();
		String legalEntityId = LegalEntityUtil.getLegalEntityIdFromSessionOrCache(request);
		inputParams.put("legalEntityId",legalEntityId);
		request.getHeaderMap().put("legalEntityId", legalEntityId);
		request.addRequestParam_("legalEntityId", legalEntityId);
		request.getHeaderMap().put("companyId",legalEntityId);		
		 LOG.debug("input params in check management"+inputParams);
		 LOG.debug("request in check management"+request);
		 try {
   		  command=GetCommandUrl.getURL(type);
   		  String[] api = null;
			api = command.split("#");
				if (api != null && api.length > 1) {
					serviceName = api[0];
					operationName = api[1];
				}
				 LOG.debug("command in getCommandNameBackendDelegate"+command);
				result = (CommonUtils.callIntegrationService(request, inputParams, headerParams, serviceName, operationName,
						true));
				 LOG.debug("response fetched from the integration service"+result);
				return result;
   		 
        } catch (Exception e) {
        	LOG.error("exception occured while invoking the integration service "+e);
        	result.addErrMsgParam("exception occured while invoking Integration service");
        	return result;
		} 
	} catch (Exception e1) {
		LOG.error("exception occured in the getcommandnamebackenddelegate "+e1);
    	result.addErrMsgParam("exception occured in getcommandnamebackenddelegate");
    	return result;
	}
		
	}

}
