/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.resource.api.TradeFinanceDocumentsResource;

public class UploadTradeFinanceDocumentsOperation implements JavaService2 {

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {
        TradeFinanceDocumentsResource orderResource = DBPAPIAbstractFactoryImpl.getResource(TradeFinanceDocumentsResource.class);
        return orderResource.uploadDocument(request);
    }

}