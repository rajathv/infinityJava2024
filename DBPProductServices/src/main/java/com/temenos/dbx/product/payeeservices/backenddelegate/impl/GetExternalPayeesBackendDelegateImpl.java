package com.temenos.dbx.product.payeeservices.backenddelegate.impl;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.temenos.dbx.product.commonsutils.CommonUtils;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.temenos.dbx.product.payeeservices.backenddelegate.api.GetExternalPayeesBackendDelegate;
import com.temenos.dbx.product.payeeservices.dto.ExternalPayeeListDTO;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author naveen.yerra
 */
public class GetExternalPayeesBackendDelegateImpl implements GetExternalPayeesBackendDelegate {
    private static final Logger LOG = Logger.getLogger(GetExternalPayeesBackendDelegateImpl.class);

    public List<ExternalPayeeListDTO> fetchPayees(Map<String, Object> requestParameters, DataControllerRequest dcRequest) {

        String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
        String operationName = OperationName.GET_EXTERNAL_PAYEES_PROC;

        String payeeResponse;
        List<ExternalPayeeListDTO> ExternalPayeeDTOs;
        try {
            payeeResponse = DBPServiceExecutorBuilder.builder().
                    withServiceId(serviceName).
                    withObjectId(null).
                    withOperationId(operationName).
                    withRequestParameters(requestParameters).
                    build().getResponse();
            JSONObject responseObj = new JSONObject(payeeResponse);
            JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
            ExternalPayeeDTOs = JSONUtils.parseAsList(jsonArray.toString(), ExternalPayeeListDTO.class);

        }
        catch(JSONException e) {
            LOG.error("Failed to fetch external payees: " + e);
            return null;
        }
        catch(Exception e) {
            LOG.error("Caught exception at fetchPayeesFromBackend: " + e);
            return null;
        }
        return ExternalPayeeDTOs;
    }
}
