package com.temenos.dbx.product.usermanagement.resource.api;

import com.dbp.core.api.Resource;
import com.kony.dbp.exception.ApplicationException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public interface CustomerSecurityQuestionsResource extends Resource {

    public Result get(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws ApplicationException;

	public Object getAreSecurityQuestionsConfigured(String methodID, Object[] inputArray,
			DataControllerRequest dcRequest, DataControllerResponse dcResponse);

}
