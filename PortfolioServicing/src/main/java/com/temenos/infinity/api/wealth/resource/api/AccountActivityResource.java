package com.temenos.infinity.api.wealth.resource.api;

import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public interface AccountActivityResource extends Resource {
	/**
	 * 
	 * @param methodID
	 * @param inputArray
	 * @param request
	 * @param response
	 * @return
	 */
	Result getAccountActivity(String methodID, Object[] inputArray,DataControllerRequest request,
			DataControllerResponse response);	
	/**
	 * 
	 * @param methodID
	 * @param inputArray
	 * @param request
	 * @param response
	 * @return
	 */
	Result getAccountActivityOperations(String methodID, Object[] inputArray,DataControllerRequest request,
			DataControllerResponse response);	

}
