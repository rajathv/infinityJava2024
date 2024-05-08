package com.temenos.dbx.transaction.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.dbx.transaction.resource.api.TransactionReportDownloadResource;

public class TransactionReportDownloadOperation implements JavaService2 {

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {

        TransactionReportDownloadResource transactionReportResource = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(ResourceFactory.class).getResource(TransactionReportDownloadResource.class);

        return transactionReportResource.downloadTransactionReport(methodID, inputArray, request, response);
    }

}