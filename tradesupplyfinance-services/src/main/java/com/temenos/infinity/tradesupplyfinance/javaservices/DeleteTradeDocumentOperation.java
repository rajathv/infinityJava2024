package com.temenos.infinity.tradesupplyfinance.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradesupplyfinance.resource.api.DocumentsResource;

/**
 * @author k.meiyazhagan
 */
public class DeleteTradeDocumentOperation implements JavaService2 {
    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request, DataControllerResponse response) throws Exception {
        DocumentsResource orderResource = DBPAPIAbstractFactoryImpl.getResource(DocumentsResource.class);
        return orderResource.deleteDocument(request);
    }
}
