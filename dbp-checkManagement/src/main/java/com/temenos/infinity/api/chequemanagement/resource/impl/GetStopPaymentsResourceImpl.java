package com.temenos.infinity.api.chequemanagement.resource.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.chequemanagement.businessdelegate.api.GetStopPaymentBusinessDelegate;
import com.temenos.infinity.api.chequemanagement.constants.ChequeManagementConstants;
import com.temenos.infinity.api.chequemanagement.dto.StopPayment;
import com.temenos.infinity.api.chequemanagement.resource.api.GetStopPaymentResource;
import com.temenos.infinity.api.chequemanagement.utils.AccountUtilities;
import com.temenos.infinity.api.chequemanagement.utils.ChequeManagementUtilities;

/**
 * TODO: Document me!
 *
 * @author smugesh
 *
 */
public class GetStopPaymentsResourceImpl implements GetStopPaymentResource {
    private static final Logger LOG = LogManager.getLogger(GetStopPaymentsResourceImpl.class);

    @Override
    public Result getStopPaymentRequests(StopPayment stopPaymentInput, DataControllerRequest request) {
        List<StopPayment> stopPaymentsList = null;
        Result result = new Result();
        
        AccountUtilities ac = new AccountUtilities(); 
        String customerId = "";
        try {
            customerId = (String) request.getServicesManager().getIdentityHandler().getUserAttributes()
                    .get(ChequeManagementConstants.PARAM_CUSTOMER_ID);
        } catch (Exception e) {
            LOG.error("Unable to fetch the customer id from session" + e);
        }

        if (StringUtils.isBlank(customerId))
            return ErrorCodeEnum.ERR_26014.setErrorCode(new Result());

        if (!ac.validateInternalAccount(customerId, stopPaymentInput.getAccountID())) { 
            return ErrorCodeEnum.ERR_26008.setErrorCode(new Result());
        }
        
        String accountID = stopPaymentInput.getAccountID();
        if (accountID.contains("-")) {
            accountID = ChequeManagementUtilities.RemoveCompanyId(accountID);
            stopPaymentInput.setAccountID(accountID);
        }

        try {

            GetStopPaymentBusinessDelegate orderBusinessDelegate = DBPAPIAbstractFactoryImpl
                    .getBusinessDelegate(GetStopPaymentBusinessDelegate.class);
            stopPaymentsList = orderBusinessDelegate.getStopPaymentRequests(stopPaymentInput, request);

        } catch (Exception e) {
            LOG.error(e);
            LOG.debug("Failed to fetch get stop payment requests in OMS " + e);
            return ErrorCodeEnum.ERR_26013.setErrorCode(new Result());
        }
        
        JSONArray stopPaymentsArray = new JSONArray(stopPaymentsList);
        for (int i = 0; i < stopPaymentsArray.length(); i++) {
            JSONObject order = stopPaymentsArray.getJSONObject(i);
            if (order.has("id")) {
                String id = order.getString("id");
                order.put("Id", id);
                order.remove("id");
            }
        }

        JSONObject responseObj = new JSONObject();
        responseObj.put("accountransactionview", stopPaymentsArray);
        result = JSONToResult.convert(responseObj.toString());
        
        return result;
    }
}
