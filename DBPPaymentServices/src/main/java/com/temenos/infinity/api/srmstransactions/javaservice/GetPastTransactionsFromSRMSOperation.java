package com.temenos.infinity.api.srmstransactions.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.srmstransactions.constants.ErrorCodeEnum;
import com.temenos.infinity.api.srmstransactions.resource.api.SrmsTransactionsResource;

public class GetPastTransactionsFromSRMSOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(GetPastTransactionsFromSRMSOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {
            // Initializing of AccountTransactions through Abstract factory
            // method
            SrmsTransactionsResource srmstransactionsResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(SrmsTransactionsResource.class);

            String transactionPeriod = request.getParameter("transactionPeriod");
            String lastRecordNumber = request.getParameter("lastRecordNumber");
            String firstRecordNumber = request.getParameter("firstRecordNumber");

            result = srmstransactionsResource.getSrmsOneTimeTransactionsResource(transactionPeriod, firstRecordNumber,
                    lastRecordNumber, request);
        } catch (Exception e) {
            LOG.error("Exception Occured in get transactions from SRMS Operation");
            return ErrorCodeEnum.ERR_32044.setErrorCode(new Result());
        }

        return result;
    }
}
