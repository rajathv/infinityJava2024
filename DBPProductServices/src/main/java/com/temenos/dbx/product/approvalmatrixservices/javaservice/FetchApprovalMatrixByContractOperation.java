package com.temenos.dbx.product.approvalmatrixservices.javaservice;

import com.temenos.dbx.product.approvalmatrixservices.resource.api.ApprovalMatrixResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author KH9450
 * @version 1.0 Implements the {@link JavaService2}
 * Java Service end point to fetch all the Approval matrix records
 */
public class FetchApprovalMatrixByContractOperation implements JavaService2  {

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response)
	{
		final Logger LOG = LogManager.getLogger(FetchApprovalMatrixByContractOperation.class);
		try 
		{	
			ApprovalMatrixResource approvalMatrixResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(ApprovalMatrixResource.class);
			Result result = approvalMatrixResource.fetchApprovalMatrixByContractId(methodID, inputArray, request, response);
			return result;
		} 
		catch(Exception e) 
		{
			LOG.error("Error occured while invoking FetchApprovalMatrixOperation: ",e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
	}
}