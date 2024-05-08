package com.kony.dbputilities.extaccountservices;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.kony.dbputilities.util.DBPInputConstants;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.GetAccountTransHelper;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Dataset;
import com.konylabs.middleware.dataobject.Record;
import com.konylabs.middleware.dataobject.Result;

public class EditExternalAccount implements JavaService2 {
    @SuppressWarnings("rawtypes")
    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
        Result result = new Result();
        Map<String,String> inputParams = HelperMethods.getInputParamMap(inputArray);
        
        if (preProcess(inputParams, dcRequest, result)) {
            GetAccountTransHelper helper = new GetAccountTransHelper();
            helper.constructQuery(inputParams, dcRequest, result);
            result = HelperMethods.callApi(dcRequest, inputParams, HelperMethods.getHeaders(dcRequest),
                    URLConstants.EXT_ACCOUNTS_UPDATE);
        }
        if(HelperMethods.hasRecords(result)) {
    		Result rs = new Result();
    		rs.addAllParams(result.getAllDatasets().get(0).getRecord(0).getAllParams());
    		return rs;
        }

        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private boolean preProcess(Map<String, String> inputParams, DataControllerRequest dcRequest, Result result)
            throws HttpCallException {
        boolean status = true;
        HelperMethods.removeOnlyNullValues(inputParams);
        String userId = inputParams.get(DBPInputConstants.USER_ID);
        String companyId = inputParams.get(DBPUtilitiesConstants.COMPANYID);
        String acctNum = inputParams.get(DBPInputConstants.ACCT_NUM);
        String iBAN = inputParams.get(DBPInputConstants.IBAN);
        if(StringUtils.isBlank(iBAN))
        	iBAN = inputParams.get("iban");
        String payeeId = inputParams.get(DBPInputConstants.ID);

        if(StringUtils.isBlank(userId))
            userId = HelperMethods.getUserIdFromSession(dcRequest);
        if(StringUtils.isBlank(companyId))
            companyId = HelperMethods.getOrganizationIDForUser(userId,dcRequest);
        
        String id = getId(dcRequest, acctNum, userId, iBAN, companyId, payeeId);

        if (StringUtils.isBlank(id)) {
            HelperMethods.setValidationMsg("No records to display", dcRequest, result);
            status = false;
        }
        if (status) {
            inputParams.put(DBPUtilitiesConstants.EXT_ID, id);
            HelperMethods.addInputParam(inputParams, DBPInputConstants.BANK_ID, DBPUtilitiesConstants.BANK_ID);
            //HelperMethods.addInputParam(inputParams, DBPInputConstants.USER_ID, DBPUtilitiesConstants.USER_ID);
            inputParams.put(DBPUtilitiesConstants.USER_ID, userId);
            HelperMethods.addInputParam(inputParams, DBPInputConstants.USR_ACCNT, DBPUtilitiesConstants.USR_ACCNT);
            HelperMethods.addInputParam(inputParams, DBPInputConstants.IBAN, DBPUtilitiesConstants.IBAN);
        }
        return status;
    }

    private String getId(DataControllerRequest dcRequest, String acctNum, String userId, String iBAN, String companyId, String payeeId)
            throws HttpCallException {
        String id = null;
        String query = "";

        if (StringUtils.isNotBlank(userId)) {
            query = query + DBPUtilitiesConstants.USER_ID + DBPUtilitiesConstants.EQUAL + userId;
        }

        if (StringUtils.isNotBlank(companyId)) {
            if(StringUtils.isNotBlank(query))
                query = query + DBPUtilitiesConstants.OR;
            query = query + DBPUtilitiesConstants.P_ORGANIZATION_ID + DBPUtilitiesConstants.EQUAL + companyId;
            query = "(" + query + ") ";
        }

        if (StringUtils.isNotBlank(payeeId)) {
            query = query + DBPUtilitiesConstants.AND + DBPUtilitiesConstants.EXT_ID + DBPUtilitiesConstants.EQUAL + payeeId;
        }
        
        if (StringUtils.isNotBlank(acctNum)) {
            query = query + DBPUtilitiesConstants.AND + DBPUtilitiesConstants.ACCNT_NUM + DBPUtilitiesConstants.EQUAL + acctNum;
        } else {
            query = query + DBPUtilitiesConstants.AND + DBPUtilitiesConstants.IBAN + DBPUtilitiesConstants.EQUAL + iBAN;
        }

        Result result = HelperMethods.callGetApi(dcRequest, query, HelperMethods.getHeaders(dcRequest),
                URLConstants.EXT_ACCOUNTS_GET);
        Dataset ds = result.getAllDatasets().get(0);
        if (null != ds && 0 != ds.getAllRecords().size()) {
            Record temp = ds.getRecord(0);
            id = temp.getParam(DBPUtilitiesConstants.EXT_ID).getValue();
        }
        return id;
    }
}
