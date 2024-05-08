/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.resource.api;

import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

import java.util.HashMap;

/**
 * @author k.meiyazhagan
 */
public interface CorporatePayeesResource extends Resource {
    Result getCorporatePayees(HashMap inputParams, DataControllerRequest request) throws Exception;
}
