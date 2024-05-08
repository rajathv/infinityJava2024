package com.temenos.infinity.api.holdings.javaservice;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.TokenUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.holdings.config.ServerConfigurations;
import com.temenos.infinity.api.holdings.constants.ErrorCodeEnum;
import com.temenos.infinity.api.holdings.constants.MSCertificateConstants;
import com.temenos.infinity.api.holdings.resource.api.AccountTransactionsResource;
import com.temenos.infinity.api.holdings.util.HoldingsUtils;
import com.temenos.infinity.api.holdings.util.TransactionTypeProperties;
import com.temenos.infinity.transact.tokenmanager.jwt.TokenGenerator;

/**
 * 
 * @author KH2281
 * @version 1.0 Java Service end point to fetch all the transactions of a
 *          particular account
 */

public class GetAccountPostedTransactionsOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(GetAccountPostedTransactionsOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            // Initializing of AccountTransactions through Abstract factory
            // method
            AccountTransactionsResource AccountTransactionsResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(AccountTransactionsResource.class);

            String order = request.getParameter("order");
            String offset = request.getParameter("offset");
            String limit = request.getParameter("limit");
            String accountID = request.getParameter("accountID");
            String transactionType = request.getParameter("transactionType");
            String sortBy = request.getParameter("sortBy");
            Map<String, String> inputMap = new HashMap<>();
			inputMap.put("customerId",HoldingsUtils.getUserAttributeFromIdentity(request, "customer_id"));
			String authToken = TokenUtils.getHoldingsMSAuthToken(inputMap);
    		HoldingsUtils.setCompanyIdToRequest(request);
            // Load Transaction Types
            TransactionTypeProperties props = new TransactionTypeProperties(request);
            String accIdWithCompanyId = StringUtils.EMPTY;
            try {
                if (!accountID.contains("-"))
                    accIdWithCompanyId = HoldingsUtils.getAccountIdWithCompanyFromCache(accountID, request);
                else
                    accIdWithCompanyId = accountID;
            } catch (Exception e) {
                LOG.error("Unable to fetch account id with company id from cache" + e);
                return ErrorCodeEnum.ERR_20046.setErrorCode(new Result());
            }
            result = AccountTransactionsResource.getAccountTransactions(order, offset, limit, accIdWithCompanyId,
                    transactionType, sortBy, authToken, request);
        } catch (Exception e) {
            return ErrorCodeEnum.ERR_20041.setErrorCode(new Result());
        }

        return result;
    }

}
