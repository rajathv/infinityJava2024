package com.temenos.infinity.tradefinanceservices.javaservice;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.dto.ExportLOCDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.CreateExportLCResource;

public class UpdateExportLCByBankOperation implements JavaService2{
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
		CreateExportLCResource requestResource = DBPAPIAbstractFactoryImpl.getResource(CreateExportLCResource.class);
	        Map<String, Object> inputParams = (HashMap<String, Object>) inputArray[1];
	        ExportLOCDTO inputDto = JSONUtils.parse(new JSONObject(inputParams).toString(), ExportLOCDTO.class);
	        return requestResource.updateExportLCByBank(inputDto, request);
	    }
}
