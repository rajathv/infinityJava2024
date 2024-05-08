package com.temenos.dbx.datamigrationservices.resource.api;

import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;

public interface MigrateInfinityUserResource extends Resource {
	public Object createUser(String methodId, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception;
	
	public Object linkUserToContract(String methodId, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception;

	public Object createVirtualUser(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response);
	
	public Object createSignatoryGroup(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response);
	
	public Object createPayee(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response);
}
