package com.temenos.infinity.tradefinanceservices.javaservice;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.dto.LetterOfCreditsDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.UpdateLetterOfCreditsResource;

public class UpdateImportLCByBankOperation implements JavaService2 {
	 @Override
	    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
		 UpdateLetterOfCreditsResource requestResource = DBPAPIAbstractFactoryImpl.getResource(UpdateLetterOfCreditsResource.class);
	        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
	        LetterOfCreditsDTO inputDto = JSONUtils.parse(new JSONObject(inputParams).toString(), LetterOfCreditsDTO.class);
	        return requestResource.updateImportLCByBank(inputDto, request);
	    }
}
