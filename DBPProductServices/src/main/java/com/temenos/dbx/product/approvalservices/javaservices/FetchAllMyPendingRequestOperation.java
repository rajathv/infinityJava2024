package com.temenos.dbx.product.approvalservices.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.approvalservices.resource.api.ApprovalQueueResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FetchAllMyPendingRequestOperation implements JavaService2{
	
	private static final Logger LOG = LogManager.getLogger(FetchAllMyPendingRequestOperation.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		Result result;
		try {
			ApprovalQueueResource approvalQueueResource = DBPAPIAbstractFactoryImpl.getResource(ApprovalQueueResource.class);
			result = approvalQueueResource.fetchAllMyPendingRequests(methodId, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Error occured while invoking fetchAllMyPendingRequests: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;
	}

}
