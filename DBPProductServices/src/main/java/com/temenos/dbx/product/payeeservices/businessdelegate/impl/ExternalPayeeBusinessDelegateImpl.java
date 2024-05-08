package com.temenos.dbx.product.payeeservices.businessdelegate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.BillPayPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.businessdelegate.api.ExternalPayeeBusinessDelegate;
import com.temenos.dbx.product.payeeservices.dto.ExternalPayeeBackendDTO;
import com.temenos.dbx.product.payeeservices.dto.IntraBankPayeeBackendDTO;

/**
 * @author KH2638
 * @version 1.0
 * implements {@link BillPayPayeeBusinessDelegate}
 */

public class ExternalPayeeBusinessDelegateImpl implements ExternalPayeeBusinessDelegate {
	
	private static final Logger LOG = LogManager.getLogger(ExternalPayeeBusinessDelegateImpl.class);
	
	@Override
	public List<ExternalPayeeBackendDTO> fetchPayeesFromDBXOrch(Map<String, Object> headerParams, DataControllerRequest dcRequest) {
		String serviceName = ServiceId.DBP_EXTERNAL_PAYEES_ORCH;
        String operationName = OperationName.GET_EXTERNAL_PAYEES;
	        
        Map<String, Object> requestParameters = new HashMap<String, Object>();
		List<ExternalPayeeBackendDTO> externalPayeeDTOs = new ArrayList<ExternalPayeeBackendDTO>();
		
		String payeeResponse = null;
		try {
			payeeResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParameters).
					withRequestHeaders(headerParams).
					withDataControllerRequest(dcRequest).
					build().getResponse();
			JSONObject responseObj = new JSONObject(payeeResponse);
			if(!responseObj.has(Constants.EXTERNALACCOUNT) && (responseObj.has("errmsg") || responseObj.has("dbpErrMsg") || responseObj.has("errorMessage"))) {
				LOG.error("Error occurred while fetching payees from orch service");
				JSONObject response = new JSONObject(payeeResponse);
				ExternalPayeeBackendDTO externalPayeeDTO = JSONUtils.parse(response.toString(), ExternalPayeeBackendDTO.class);
				externalPayeeDTOs.add(externalPayeeDTO);
				return externalPayeeDTOs;
			}
			JSONArray jsonArray = responseObj.getJSONArray(Constants.EXTERNALACCOUNT);
			externalPayeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), ExternalPayeeBackendDTO.class);
		}
		catch (JSONException e) {
			LOG.error("Failed to fetch payees: " + e);
			return null;
		}
		catch (Exception e) {
			LOG.error("Caught exception at fetchPayeesFromDBXOrch: " + e);
			return null;
		}
		
		return externalPayeeDTOs;
	}

}
