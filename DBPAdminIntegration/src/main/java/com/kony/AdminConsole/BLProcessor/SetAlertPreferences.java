
package com.kony.AdminConsole.BLProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.constants.DBPConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.kony.AdminConsole.Utilities.AdminConsoleOperations;
import com.kony.AdminConsole.Utilities.CommonUtilities;
import com.kony.AdminConsole.Utilities.ServiceConfig;
import com.kony.dbputilities.utils.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.utils.ConvertJsonToResult;
import com.kony.dbputilities.utils.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.commons.config.EnvironmentConfigurationsHandler;

public class SetAlertPreferences implements JavaService2 {
	
	

   
	private static final String ALERT_SUBSCRIPTION = "alertSubscription";
	private static final Logger LOG = LogManager.getLogger(SetAlertPreferences.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest requestInstance,
            DataControllerResponse responseInstance) throws Exception {

        LOG.info("SetAlertPreferences started");
        Result processedResult = new Result();
        JSONObject alertSubscription = null;
        String AlertCategoryId = null;
        JSONArray accounts = null;
        String accountId = null;
        String isSubscribed = null;
        String getResponse = null;
        String customerTypeStr = null;
        try {
            isSubscribed = requestInstance.getParameter("isSubscribed");
            AlertCategoryId = requestInstance.getParameter("alertCategoryId");
            accountId = requestInstance.getParameter("accountId");
                        
            if (StringUtils.isNotBlank(accountId)) {
            	accounts = GetCustomerAlertTypePreference.getAccountsForLoggedinUser(requestInstance, processedResult);
            	if (accounts == null) {
            		ErrorCodeEnum.ERR_11025.setErrorCode(processedResult);
            		return processedResult;
            	}

            	if(!isCurrentUserAccount(accountId, accounts)) {
            		if (processedResult.getParamByName(ErrorCodeEnum.ERROR_CODE_KEY) == null) {
            			ErrorCodeEnum.ERR_11030.setErrorCode(processedResult);
            		}
            		return processedResult;
            	}
            }
            
            String userId = HelperMethods.getUserIdFromSession(requestInstance);
            if (StringUtils.isNotBlank(userId)) {
                customerTypeStr = GetCustomerAlertTypePreference.getUserTypeForLoggedinUser(requestInstance, processedResult,userId);
                if (customerTypeStr == null) {
                    ErrorCodeEnum.ERR_10335.setErrorCode(processedResult);
                    return processedResult;
                }
            }else {        	
            	     ErrorCodeEnum.ERR_10530.setErrorCode(processedResult);
                    return processedResult;            
            }

            if (requestInstance.getParameter(ALERT_SUBSCRIPTION) != null) {
            	alertSubscription = new JSONObject(requestInstance.getParameter(ALERT_SUBSCRIPTION));
            }

           
        } catch (Exception e) {
            processedResult = getErrorResult(e);
            return processedResult;
        }

        String accountTypeId = requestInstance.getParameter("accountTypeId");

        getResponse = setAlertPreferences(requestInstance, AlertCategoryId, accountId, accountTypeId, isSubscribed,
                accounts,
                alertSubscription,customerTypeStr);

        JSONObject getResponseAsJSON = CommonUtilities.getStringAsJSONObject(getResponse);
        if (getResponseAsJSON.getInt(DBPConstants.FABRIC_OPSTATUS_KEY) != 0) {
            String authToken = AdminConsoleOperations.login(requestInstance);
            ServiceConfig.setValue("Auth_Token", authToken);
            getResponse = setAlertPreferences(requestInstance, AlertCategoryId, accountId, accountTypeId, isSubscribed,
            		accounts, alertSubscription, customerTypeStr);
        }
        processedResult = ConvertJsonToResult.convert(getResponse);
        return processedResult;

    }
    
    private boolean isCurrentUserAccount(String accountId, JSONArray accounts) {
    	  for (Object accountObject : accounts) {
              JSONObject account = (JSONObject) accountObject;
              if (account.getString("accountID").equalsIgnoreCase(accountId)) {
                  return true;
              }
          }
          
          return false;
	}
    
    private static Result getErrorResult(Throwable ex) {
        String errorMsg = ex.getMessage();
        if (errorMsg == null) {
            StackTraceElement ste = ex.getStackTrace()[0];
            errorMsg = ex.getClass().getName() + "' thrown in " + ste.getClassName() + "." + ste.getMethodName();
        }
        return getErrorResult(9001, errorMsg);
    }

    private static Result getErrorResult(int errorNumber, String errorMsg) {
        Result result = new Result();
        result.addParam(new Param("errornumber", Integer.toString(errorNumber)));
        result.addParam(new Param("errormsg", errorMsg));
        result.addParam(new Param("success", "false"));
        return result;
    }

    public String setAlertPreferences(DataControllerRequest dcRequest, String AlertCategoryId, String accountId,
            String accountTypeId,
            String isSubscribed, JSONArray accounts, JSONObject alertSubscription, String customerTypeStr) {

        String userId = HelperMethods.getUserIdFromSession(dcRequest);
        HashMap<String, Object> customHeaderParameters = new HashMap<>();
        customHeaderParameters.put("X-Kony-AC-API-Access-By", dcRequest.getHeader("X-Kony-AC-API-Access-By"));

        Map<String, Object> postParametersMap = new HashMap<>();
        postParametersMap.put("customerId", userId);
        if (StringUtils.isBlank(accountId)) {
            accountId = "";
        }
        if (StringUtils.isBlank(accountTypeId)) {
            accountTypeId = "";
        }
        postParametersMap.put("alertCategoryId", AlertCategoryId);
        postParametersMap.put("accountId", accountId);
        postParametersMap.put("accountTypeId", accountTypeId);
        postParametersMap.put("isSubscribed", isSubscribed);
        postParametersMap.put(ALERT_SUBSCRIPTION, alertSubscription);
        postParametersMap.put(DBPUtilitiesConstants.PARAM_CUSTOMER_TYPE_STR, customerTypeStr);
        
        if (accounts != null) {
            postParametersMap.put("accounts", accounts.toString());
        }
      
        String backendId = getCoreBackendId(dcRequest);
        postParametersMap.put("backendId", backendId);

        String getResponseString = HelperMethods.invokeC360ServiceAndGetString(dcRequest, postParametersMap,
                customHeaderParameters, "setAlertPreferences");

        return getResponseString;
    }
    public String getCoreBackendId(DataControllerRequest dcreq)  {		
    	String backendId = null;
		try {
			
			if (dcreq.getServicesManager().getIdentityHandler() != null) {
				Map<String, Object> userAttributesMap = dcreq.getServicesManager().getIdentityHandler().getUserAttributes();               
				String backendIdentifier = (String)userAttributesMap.get("backendIdentifiers");
				if(LOG.isDebugEnabled()){
					LOG.debug("backendIdentifier is" + backendIdentifier);
				}
				if(StringUtils.isNotBlank(backendIdentifier)) { 				
					backendId = getCoreIDFromJson(backendIdentifier);				
				}
			}
			else
			{
				LOG.error("NULL IDENTITYHANDLER");
			}
			
		} catch (Exception e) {
			LOG.error(e);	
			
		}
		if(LOG.isDebugEnabled()){
			LOG.debug("backendId is" + backendId);
		}
		return backendId;

	}
    protected static String getCoreIDFromJson(String backendIdentifier) {
		String backendId = null;
		JsonObject backendIdentifiersJSON = new JsonParser().parse(backendIdentifier).getAsJsonObject();
		if(backendIdentifiersJSON.entrySet().size() == 1) {
			for ( Entry<String, JsonElement> entry : backendIdentifiersJSON.entrySet()) {
				backendId = getBackendIdFromCoreType(backendIdentifiersJSON, entry.getKey());
			}
			if(LOG.isDebugEnabled()){
				LOG.debug("backendId is" + backendId);
			}
		}else {
			String coreType = null;
			try {
				coreType = EnvironmentConfigurationsHandler.getServerAppProperty("ALERTS_CORETYPE");
			} catch (Exception e) {
				LOG.error("ALERTS_CORETYPE is not available" + e);
				LOG.error(e);
			}
			if(coreType == null)
			{
				LOG.error("ALERTS_CORETYPE is not available");
			}
			else
			{
				if(StringUtils.isNotEmpty(coreType) && backendIdentifiersJSON.has(coreType)){					
					backendId = getBackendIdFromCoreType(backendIdentifiersJSON,coreType);
				}
				if(LOG.isDebugEnabled()){
					LOG.debug("backendId is" + backendId);
				}
			}
			
		}
		return backendId;
	}

	protected static String getBackendIdFromCoreType(JsonObject backendIdentifiersJSON, String key) {
		JsonArray backendTypeObj = backendIdentifiersJSON.get(key).getAsJsonArray();
		String backendId = null;
		if(backendTypeObj.size() > 0) {
			backendId  =	backendTypeObj.get(0).getAsJsonObject().get("BackendId").getAsString();
		}
		if(LOG.isDebugEnabled()){
			LOG.debug("backendId is" + backendId);
		}
		return backendId;
	}	

}
