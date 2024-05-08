package com.temenos.dbx.product.approvalmatrixservices.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.temenos.dbx.product.approvalmatrixservices.resource.api.ApprovalMatrixResource;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * 
 * @author KH2387
 * @version 1.0 Implements the {@link JavaService2}
 * Java Service end point to fetch all the Approval matrix rules
 */
public class UpdateApprovalMatrixStatusOperation implements JavaService2  {

	private static final Logger LOG = LogManager.getLogger(UpdateApprovalMatrixStatusOperation.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response)
		 {
		Result result = new Result();
		try {
			ApprovalMatrixResource approvalMatrixResource = DBPAPIAbstractFactoryImpl.getInstance()
	                .getFactoryInstance(ResourceFactory.class).getResource(ApprovalMatrixResource.class);
	
	        result = approvalMatrixResource.updateApprovalMatrixStatus(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of java service : ", e);
			return ErrorCodeEnum.ERR_29005.setErrorCode(result);
		}
        return result;
	}
}
