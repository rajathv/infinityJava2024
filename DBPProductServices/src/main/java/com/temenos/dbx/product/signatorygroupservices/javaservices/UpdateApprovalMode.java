package com.temenos.dbx.product.signatorygroupservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.signatorygroupservices.resource.api.ApprovalModeResource;
import com.temenos.dbx.product.signatorygroupservices.resource.api.SignatoryGroupResource;

public class UpdateApprovalMode implements JavaService2 {

private static final Logger LOG = LogManager.getLogger(UpdateApprovalMode.class);
	
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		Result result = new Result();
		try {
			//Initializing of savingsPotResource through Abstract factory method
			ApprovalModeResource approvalModeResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(ApprovalModeResource.class);
			result  = approvalModeResource.updateApprovalMode(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of getAllSavingsPot: ", e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
		return result;
		
	}

}
