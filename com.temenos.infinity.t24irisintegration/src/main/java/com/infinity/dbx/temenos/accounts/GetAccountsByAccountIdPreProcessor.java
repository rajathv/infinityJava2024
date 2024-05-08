package com.infinity.dbx.temenos.accounts;

import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.temenos.TemenosBasePreProcessor;
import com.kony.dbx.util.CommonUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class GetAccountsByAccountIdPreProcessor extends TemenosBasePreProcessor {

    private static final Logger logger = LogManager.getLogger(GetAccountsByAccountIdPreProcessor.class);

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public boolean execute(HashMap params, DataControllerRequest request, DataControllerResponse response,
            Result result) throws Exception {

        super.execute(params, request, response, result);

        String loginUserId = null;
        String customerType_id = null;
        String organizationId = null;
        // Check Admin Permission when the request comes from micro services
        if (StringUtils.isNotBlank(request.getHeader(AccountsConstants.PARAM_ACCOUNT_PERMISSION))) {
            if (request.getHeader(AccountsConstants.CUSTOMER_ID) != null) {
                loginUserId = request.getHeader(AccountsConstants.CUSTOMER_ID);
                customerType_id = request.getHeader(AccountsConstants.TYPE_ID);
            }
        } else {
            loginUserId = params.get(PARAM_LOGINUSERID) != null ? params.get(PARAM_LOGINUSERID).toString() : "";
            customerType_id = params.get(AccountsConstants.TYPE_ID) != null
                    ? params.get(AccountsConstants.TYPE_ID).toString()
                    : "";
        }
        organizationId = params.get(PARAM_ORGANIZATIONID) != null ? params.get(PARAM_ORGANIZATIONID).toString() : "";
        request.addRequestParam_(PARAM_LOGINUSERID, loginUserId);
        request.addRequestParam_(PARAM_ORGANIZATIONID, organizationId);
        CommonUtils.setCompanyIdToRequest(request);

        // if (!"".equalsIgnoreCase(customerType_id) &&
        // !AccountsConstants.RETAIL_TYPE.equalsIgnoreCase(customerType_id)) {

        /*
         * Read accounts from DB Append accountId to new string with space separated
         */

        HashMap<String, Object> inputParams = new HashMap<String, Object>();
        inputParams.put("$filter", "Customer_id eq " + loginUserId);
        request.addRequestParam_("$filter", "Customer_id eq " + loginUserId);
        Result userAccounts = CommonUtils.callIntegrationService(request, inputParams, null,
                SERVICE_BACKEND_CERTIFICATE, OPERATION_CUSTOMER_ACCOUNTS_GET, true);
        String accounts = "";
        for (Record rec : userAccounts.getDatasetById(AccountsConstants.DS_CUSTOMERACCOUNTS).getAllRecords()) {
            accounts += rec.getParamValueByName(AccountsConstants.DB_ACCOUNTID);
            accounts += " ";
        }
        
        if(StringUtils.isNotBlank(accounts)) {
            params.put(AccountsConstants.DB_ACCOUNTID,
                URLEncoder.encode(accounts.substring(0, accounts.length() - 1), "UTF-8"));
            logger.error("Final Params " + params.toString());
            return Boolean.TRUE;
        }
        
        logger.error("Final Params " + params.toString());

        result.addOpstatusParam(0);
        result.addHttpStatusCodeParam(200);
        return Boolean.FALSE;
    }
}
