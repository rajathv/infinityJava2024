/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.javaservices;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradesupplyfinance.resource.api.CorporatePayeesResource;

import java.util.HashMap;

/**
 * @author k.meiyazhagan
 */
public class GetCorporatePayeesOperation implements JavaService2 {

    @Override
    public Object invoke(String s, Object[] objects, DataControllerRequest dataControllerRequest,
                         DataControllerResponse dataControllerResponse) throws Exception {
        CorporatePayeesResource payeesResource = DBPAPIAbstractFactoryImpl.getResource(CorporatePayeesResource.class);
        HashMap<String, Object> inputParams = (HashMap<String, Object>) objects[1];
        return payeesResource.getCorporatePayees(inputParams, dataControllerRequest);
    }
}
