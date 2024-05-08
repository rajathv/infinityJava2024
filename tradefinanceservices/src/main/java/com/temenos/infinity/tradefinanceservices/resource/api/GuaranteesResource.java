/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.resource.api;

import com.dbp.core.api.Resource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.dto.GuranteesDTO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface GuaranteesResource extends Resource {
    Result createGuarantees(String methodId, Map<String, Object> inoutParams, DataControllerRequest request) throws IOException;

    Result getGuaranteesById(HashMap inputParams, DataControllerRequest request);

    Result getGurantees(Object[] inputArray, GuranteesDTO letterOfCredits, DataControllerRequest request);

    Result createClause(HashMap inputParams, DataControllerRequest request);

    Result updateGuaranteeLcByBank(GuranteesDTO requestGuaranteeDTO, DataControllerRequest request);
}
