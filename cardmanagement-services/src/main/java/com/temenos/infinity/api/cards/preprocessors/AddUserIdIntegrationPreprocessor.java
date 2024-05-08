package com.temenos.infinity.api.cards.preprocessors;

import java.util.HashMap;

import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class AddUserIdIntegrationPreprocessor implements DataPreProcessor2{
	@Override
	public boolean execute(HashMap params, DataControllerRequest request, DataControllerResponse response,
			Result result) throws Exception {
		String userId = (String)request.getServicesManager().getIdentityHandler().getUserAttributes().get("customer_id");
		params.put("userId", userId);
		return true;
	}
}
