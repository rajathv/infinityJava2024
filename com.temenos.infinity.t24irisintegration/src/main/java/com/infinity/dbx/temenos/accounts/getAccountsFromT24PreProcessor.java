/**
 * 
 */
package com.infinity.dbx.temenos.accounts;


import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.infinity.dbx.temenos.TemenosBasePreProcessor;
import com.infinity.dbx.temenos.constants.TemenosConstants;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.TokenUtils;
import com.kony.dbx.util.CommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;

public class getAccountsFromT24PreProcessor extends TemenosBasePreProcessor {
    private static final Logger logger = Logger.getLogger(getAccountsFromT24PreProcessor.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean execute(HashMap params, DataControllerRequest request, DataControllerResponse response,
            Result result) throws Exception {
    	logger.error("****************************************** GetAccountsByCoreCustomerIdListConcPreProcessor start ************************");
    	String userId = request.getParameter("loginUserId");
    	super.execute(params, request, response, result);
    	String customerId;
    	Boolean preLoginFlow = false;
    	if (StringUtils.isNotBlank(userId)) {
    		if(userId.contains("PreLogin-")) {
    			userId = userId.replace("PreLogin-", "");
    			preLoginFlow = true;
    			request.addRequestParam_(TemenosConstants.FLOW_TYPE, TemenosConstants.PRE_LOGIN_FLOW);
    			request.addRequestParam_("loginUserId", userId);
    		}
    	}
    	String authToken = TokenUtils.getT24AuthToken(request);
        logger.debug("*************************** GetAccountsByCoreCustomerIdListConcPreProcessor authToken :"+authToken);
        logger.debug("*************************** GetAccountsByCoreCustomerIdListConcPreProcessor params :"+params);
        logger.debug("*************************** GetAccountsByCoreCustomerIdListConcPreProcessor getHeaderMap :"+request.getHeaderMap());
        customerId = HelperMethods.getCustomerIdFromSession(request);
        String companyId=com.temenos.infinity.api.arrangements.utils.CommonUtils.getCompanyId(request);
        if(preLoginFlow)customerId=userId;
	    logger.debug("*********** customerId  from request **********" + customerId);
        request.addRequestParam_("loginUserId", customerId);
        logger.error("Logged in user id  : " + customerId);
        request.addRequestParam_(TemenosConstants.PARAM_AUTHORIZATION, authToken);
        String coreCustId = request.getParameter("coreCustomerIdList");
		if (StringUtils.isNotBlank(coreCustId)) {
            params.put("coreCustomerIdList",coreCustId );
            request.addRequestParam_("isRequestForActions", "true");
            request.addRequestParam_("coreCustomerIdList",coreCustId);            
            logger.error("****************************************** call with corecustomeridist ************************");
            return Boolean.TRUE.booleanValue();
        }
		request.addRequestParam_("isRequestForActions", "false");
        
        HashMap<String, Object> inputParams = new HashMap<String, Object>();
        inputParams.put("$filter", "customerId eq " + customerId +" and companyLegalUnit eq "+companyId);
        request.addRequestParam_("$filter", "customerId eq " + customerId+" and companyLegalUnit eq "+companyId);
        
        logger.error("Input params " + inputParams);
        Result coreCustomers = CommonUtils.callIntegrationService(request, inputParams, request.getHeaderMap(),
                SERVICE_BACKEND_CERTIFICATE, OP_CONTRACT_CUSTOMERS_GET, true);
        
        logger.error("****************************************** coreCustomers : "+ResultToJSON.convert(coreCustomers));
        StringBuilder coreCustomerIdList = new StringBuilder();
        StringBuilder explicitCoreCustomerIdList = new StringBuilder(" ");
        if (coreCustomers != null && coreCustomers.getAllDatasets().size() > 0
                && coreCustomers.getDatasetById("contractcustomers").getAllRecords().size() > 0) {
        	logger.error("Records in contractcustomers ::::");
            for (Record record : coreCustomers.getDatasetById("contractcustomers").getAllRecords()) {
                coreCustomerIdList.append(record.getParamValueByName("coreCustomerId"));
                coreCustomerIdList.append(" ");
                if(record.getParamValueByName("autoSyncAccounts") == "false") {
                	explicitCoreCustomerIdList.append(record.getParamValueByName("coreCustomerId")+" ");
                }
            }
            params.put("coreCustomerIdList",URLEncoder.encode(coreCustomerIdList.toString().substring(0, coreCustomerIdList.length() - 1),
                    "UTF-8"));
            request.addRequestParam_("coreCustomerIdList",URLEncoder.encode(coreCustomerIdList.toString().substring(0, coreCustomerIdList.length() - 1),
                    "UTF-8"));
            request.addRequestParam_("explicitCoreCustomerIdList",explicitCoreCustomerIdList.toString());
            logger.error("params  : " + params.toString());
            logger.error("****************************************** GetAccountsByCoreCustomerIdListConcPreProcessor true end ************************");
            return Boolean.TRUE;
        }
            result.addOpstatusParam(0);
            result.addHttpStatusCodeParam(200);
            return Boolean.FALSE;
    }
  
    
}
