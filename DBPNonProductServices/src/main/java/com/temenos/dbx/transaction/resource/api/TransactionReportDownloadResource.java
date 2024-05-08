package com.temenos.dbx.transaction.resource.api;

import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public interface TransactionReportDownloadResource extends Resource {
    Result generateTransactionReport(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);

    Result downloadTransactionReport(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response);
}
