/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradesupplyfinance.resource.api.ReceivableCsvImportResource;

/**
 * @author k.meiyazhagan
 */
public class GetCsvImportByIdOperation implements JavaService2 {
    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        ReceivableCsvImportResource requestResource = DBPAPIAbstractFactoryImpl.getResource(ReceivableCsvImportResource.class);
        return requestResource.getCsvImportById(request);
    }
}
