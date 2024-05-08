package com.temenos.infinity.api.holdings.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.holdings.constants.ErrorCodeEnum;
import com.temenos.infinity.api.holdings.resource.api.AccountTransactionsResource;
import com.temenos.infinity.api.holdings.util.TransactionTypeProperties;

/**
 * 
 * @author KH2281
 * @version 1.0 Java Service end point to fetch all the transactions of a
 *          particular account
 */

public class GetAccountPendingTransactionsOperation implements JavaService2 {

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            // Initializing of AccountTransactions through Abstract factory
            // method
            AccountTransactionsResource AccountTransactionsResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(AccountTransactionsResource.class);

            String accountID = request.getParameter("accountID");
            String searchStartDate = request.getParameter("searchStartDate");
            // Load Transaction Types
            TransactionTypeProperties props = new TransactionTypeProperties(request);
            result = AccountTransactionsResource.getAccountPendingTransactions(accountID, searchStartDate, request);

        } catch (Exception e) {
            return ErrorCodeEnum.ERR_20041.setErrorCode(new Result());
        }

        return result;
    }

}
