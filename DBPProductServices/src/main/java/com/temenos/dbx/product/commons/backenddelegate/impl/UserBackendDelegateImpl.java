package com.temenos.dbx.product.commons.backenddelegate.impl;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ServiceCallHelper;
import com.kony.dbputilities.util.URLConstants;
import com.temenos.dbx.product.commons.backenddelegate.api.UserBackendDelegate;
import com.temenos.dbx.product.constants.Constants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.signatorygroupservices.backenddelegate.impl.SignatoryGroupBackendDelegateImpl;
import com.temenos.dbx.product.signatorygroupservices.dto.SignatoryGroupDTO;

public class UserBackendDelegateImpl implements UserBackendDelegate{
private static final Logger LOG = LogManager.getLogger(SignatoryGroupBackendDelegateImpl.class);
	
	@Override
	public String fetchUsernameFromCustomerId(String customerId,Map<String, Object> headersMap) throws ApplicationException {
		String username = null;
		try {
		Map<String,Object> requestParams = new HashMap <String,Object>();
        StringBuilder filterQuery = new StringBuilder();
         filterQuery.append(Constants.ID).append(DBPUtilitiesConstants.EQUAL).append(customerId);
		
         requestParams.put(DBPUtilitiesConstants.FILTER, filterQuery.toString());
         JsonObject dcresponse =
                 ServiceCallHelper.invokeServiceAndGetJson(requestParams, headersMap, URLConstants.USER_GET);
		if(dcresponse.get(Constants.OPSTATUS).getAsInt() == 0 && dcresponse.get(Constants.HTTP_STATUS).getAsInt() == 0) {
			JsonElement jsonElement  = dcresponse.get(Constants.CUSTOMER);
			JsonObject customer  = jsonElement.getAsJsonArray().get(0).getAsJsonObject(); 
		    username = customer.get(Constants.USERNAME).toString();
		}
	}catch (Exception e) {
		LOG.error("Exception occured while fetching user details", e);
		return null;
	}
	
	return username;
  }
	
	@Override
	public String fetchFirstnameAndLastnameFromCustomerId(String customerId,Map<String, Object> headersMap) throws ApplicationException {
		String username = null;
		String firstname = null;
		String lastname = null;
		try {
		Map<String,Object> requestParams = new HashMap <String,Object>();
        StringBuilder filterQuery = new StringBuilder();
         filterQuery.append(Constants.ID).append(DBPUtilitiesConstants.EQUAL).append(customerId);
		
         requestParams.put(DBPUtilitiesConstants.FILTER, filterQuery.toString());
         JsonObject dcresponse =
                 ServiceCallHelper.invokeServiceAndGetJson(requestParams, headersMap, URLConstants.USER_GET);
		if(dcresponse.get(Constants.OPSTATUS).getAsInt() == 0 && dcresponse.get(Constants.HTTP_STATUS).getAsInt() == 0) {
			JsonElement jsonElement  = dcresponse.get(Constants.CUSTOMER);
			JsonObject customer  = jsonElement.getAsJsonArray().get(0).getAsJsonObject(); 
		    firstname = customer.get(Constants.USERFIRSTNAME).toString();
		    lastname = customer.get(Constants.USERLASTNAME).toString();
		    username = firstname+" "+lastname;
		    username = username.replace("\" \"", " ");
		}
	}catch (Exception e) {
		LOG.error("Exception occured while fetching user details", e);
		return null;
	}
	
	return username;
  }
}
