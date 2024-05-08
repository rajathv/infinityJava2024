package com.temenos.infinity.api.arrangements.javaservice;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.memorymanagement.MemoryManager;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.resource.api.DocumentStorageResource;


public class SearchDocumentOperation implements JavaService2 {

	private static final Logger logger = LogManager.getLogger(SearchDocumentOperation.class);
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		try{
		DocumentStorageResource documentStorageResource = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(ResourceFactory.class).getResource(DocumentStorageResource.class);
		String userId=request.getServicesManager().getIdentityHandler().getUserId();
		String accountsInCache = (String)MemoryManager.getFromCache(
				DBPUtilitiesConstants.ACCOUNTS_POSTLOGIN_CACHE_KEY + userId);
		if(StringUtils.isNotBlank(accountsInCache)) {
			JSONObject accountFeatureActionsObj = new JSONObject(accountsInCache);
			if(accountFeatureActionsObj.has("Accounts") && 
					accountFeatureActionsObj.get("Accounts") != null) {
				JSONArray accountFeatureActionsList = accountFeatureActionsObj
						.getJSONArray("Accounts");
				boolean valid=false;
				for (int i = 0; i < accountFeatureActionsList.length(); i++) {
					String arrangementId=null;
					JSONObject accountActions = accountFeatureActionsList.getJSONObject(i);
					if(StringUtils.isNotBlank(accountActions.optString("accountType"))) {
						if("mortgageFacility".equalsIgnoreCase(accountActions.optString("accountType"))) {
							arrangementId = accountActions.optString("arrangementId");
						    if(request.getParameter("arrangementId").equalsIgnoreCase(arrangementId)) {
						    	valid=true;
						    	break;
						    }
						}
					}
					
				}
				if(valid==false) {
					Result errorResult =new Result();
		        	errorResult.addErrMsgParam("Logged in User is not authorized to perform this action");
		        	errorResult.addOpstatusParam(0);
		        	errorResult.addHttpStatusCodeParam(0);
		            return errorResult; 
				}
			}
		}
		Result result = documentStorageResource.searchDocument(methodID, inputArray, request, response);
		return result;
				
			}
				catch(Exception e) {
					return e;
				}
	}

}
