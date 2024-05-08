package com.temenos.dbx.product.approvalmatrixservices.javaservice;

import com.kony.dbp.exception.ApplicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.approvalmatrixservices.resource.api.ApprovalMatrixResource;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class UpdateApprovalMatrixOperation  implements JavaService2  {
	
	private static final Logger LOG = LogManager.getLogger(UpdateApprovalMatrixOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request, DataControllerResponse response){
		Result result = null;
		try {
			ApprovalMatrixResource approvalMatrixResource = DBPAPIAbstractFactoryImpl.getInstance()
			        .getFactoryInstance(ResourceFactory.class).getResource(ApprovalMatrixResource.class);
			result = approvalMatrixResource.updateApprovalMatrixEntry(methodID, inputArray, request, response);			
		} catch (ApplicationException ae) {
			return ae.getErrorCodeEnum().setErrorCode(result);
		}
		catch (Exception e) {
			LOG.error("UpdateApprovalMatrixOperation failed :", e);
		}
		return result;
	}
}
