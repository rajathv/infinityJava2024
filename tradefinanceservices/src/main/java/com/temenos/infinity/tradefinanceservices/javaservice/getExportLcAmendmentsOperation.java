/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.commons.dto.FilterDTO;
import com.temenos.infinity.tradefinanceservices.resource.api.ExportLCAmendmentResource;

public class getExportLcAmendmentsOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(getExportLcAmendmentsOperation.class);

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {
        ExportLCAmendmentResource drawingsResource = DBPAPIAbstractFactoryImpl
                .getResource(ExportLCAmendmentResource.class);
        @SuppressWarnings("unchecked")
        Map<String, Object> inputParamsMap = (HashMap<String, Object>) inputArray[1];
        FilterDTO filterDTO = null;
        Result result = null;
        try {
            filterDTO = JSONUtils.parse(new JSONObject(inputParamsMap).toString(), FilterDTO.class);
            result = (Result) drawingsResource.getExportAmendments(filterDTO, request, false);
        } catch (IOException e) {
            LOG.error("Exception occurred while fetching params: ", e);
        }

        return result;
    }

}
