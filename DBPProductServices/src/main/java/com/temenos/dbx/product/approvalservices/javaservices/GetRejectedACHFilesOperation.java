package com.temenos.dbx.product.approvalservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.approvalservices.resource.api.ApprovalRequestResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * 
 * @author KH2638
 * @version 1.0
 * Java Service end point to fetch rejected ach files
 * @param <ApprovalServiceResource>
 */

public class GetRejectedACHFilesOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetRejectedACHFilesOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		
		Result result = new Result();
		try {
			//Initializing of ApprovalRuleResource through Abstract factory method
			ApprovalRequestResource approvalResource = DBPAPIAbstractFactoryImpl.getInstance()
	                .getFactoryInstance(ResourceFactory.class).getResource(ApprovalRequestResource.class);
			
			result  = approvalResource.fetchRejectedACHFiles(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of fetchRejectedACHFiles: "+e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
		
		return result;
	}
	
}
