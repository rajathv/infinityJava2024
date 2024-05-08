package com.temenos.infinity.api.docmanagement.javaservices;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.api.docmanagement.resource.api.TransactionReportDownloadResource;

public class TransactionReportGenerateOperation implements JavaService2 {

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {

        TransactionReportDownloadResource transactionReportResource = DBPAPIAbstractFactoryImpl.getInstance()
                .getFactoryInstance(ResourceFactory.class).getResource(TransactionReportDownloadResource.class);

        return transactionReportResource.generateTransactionReport(methodID, inputArray, request, response);
    }

}
