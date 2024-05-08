package com.temenos.dbx.product.approvalservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.approvalservices.resource.api.ApprovalRuleResource;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * 
 * @author KH2174
 * @version 1.0
 * Java Service end point to fetch all the Approval matrix rule
 */

public class GetApprovalRulesOperation implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetApprovalRulesOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		Result result = new Result();
		try {
			//Initializing of ApprovalRuleResource through Abstract factory method
			ApprovalRuleResource ruleResource = DBPAPIAbstractFactoryImpl.getInstance()
	                .getFactoryInstance(ResourceFactory.class).getResource(ApprovalRuleResource.class);
			
			result  = ruleResource.getRules(methodID, inputArray, request, response);
		}
		catch(Exception e) {
			LOG.error("Caught exception at invoke of GetApprovalRulesOperation: ", e);
		}
		
		return result;
	}
	
}
