package com.kony.achservices;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONObject;

import com.kony.dbputilities.sessionmanager.SessionScope;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

/**
 * Class FetchBBTransactions
 */
public class FetchBBTransactions implements JavaService2 {

    /**
     * @param s
     * @param objects
     * @param dataControllerRequest
     * @param dataControllerResponse
     * @return
     * @throws Exception
     * @description Base invoke method
     */
    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dcr,
            DataControllerResponse dataControllerResponse) throws Exception {

        HelperMethods.callApi(dcr, null, HelperMethods.getHeaders(dcr), URLConstants.UPDATE_ACH_TRANSACTIONS_STATUS);
        return fetchBBTransactionsHelper(dcr);
    }

    /**
     * @param dcr
     * @return
     * @throws Exception
     * @description Helper function for fetching transaction of a BB User
     */
    public Result fetchBBTransactionsHelper(DataControllerRequest dcr) throws Exception {
        try {
            HashSet<String> accounts = CommonUtils.getAllAccounts(dcr);
            String accountsFilter = CommonUtils.getFilterForAList(accounts, "DebitAccount");

            HashMap<String, JSONObject> viewMap = SessionScope.getUserViewServices(dcr);

            String filter = "";

            if (viewMap.containsKey("7")) {
                filter = " ( " + "TransactionTypeValue" + DBPUtilitiesConstants.EQUAL + "Collection";
            } else {
                filter = " ( " + "TransactionTypeValue" + DBPUtilitiesConstants.EQUAL + "0";
            }
            if (viewMap.containsKey("6")) {
                filter = filter + DBPUtilitiesConstants.OR + "TransactionTypeValue" + DBPUtilitiesConstants.EQUAL
                        + "Payment" + ")";
            } else {
                filter = filter + ")";
            }

            Map<String, String> dataMap = CommonUtils.getCompanyIdFilter(dcr);
            dataMap = CommonUtils.addSoftDeleteFilter(dataMap);
            CommonUtils.addFilter(dataMap, filter);
            CommonUtils.addFilter(dataMap, accountsFilter);

            return HelperMethods.callApi(dcr, dataMap, HelperMethods.getHeaders(dcr),
                    URLConstants.BB_TRANSACTION_DETAILS_VIEW_GET);
        } catch (Exception e) {
            return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
        }
    }
}
