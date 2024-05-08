package com.temenos.infinity.api.arrangements.javaservice;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.error.DBPErrorCodeSetter;
import com.kony.dbputilities.util.HelperMethods;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.resource.api.MortgageFacilityDetailsResource;
import com.temenos.infinity.api.commons.exception.ApplicationException;

public class MortgageFacilityDetailsOperation implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(MortgageFacilityDetailsOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        @SuppressWarnings("unchecked")
        Map<String, String> params = HelperMethods.getInputParamMap(inputArray);
        LOG.error("params >> "+params);
        logDCRequestParams(request, LOG);
        inputArray[1] = params;
        Map<String, String> inputParams = (Map<String, String>) inputArray[1];
        try {
        	String methodId="";
        	MortgageFacilityDetailsResource accountsResource =
                    DBPAPIAbstractFactoryImpl.getResource(MortgageFacilityDetailsResource.class);
            result = accountsResource.getMortageFacilityDetails(methodId, inputArray, request, response);
        } catch (ApplicationException e) {
        	LOG.error("Unable to fetch records from Mortage Facility" + e);
            DBPErrorCodeSetter.setError(e.getErrorCodeEnum(), result);
        }
        return result;
    }
    
    public static void logDCRequestParams(DataControllerRequest dcRequest, Logger classLogger) {
		classLogger.error("Priting DC Request PARAMS ===>");
		StringBuilder sb = new StringBuilder();
		dcRequest.getParameterNames().forEachRemaining((param)->{
			sb.append(param).append(" = ").append(dcRequest.getParameter(param)).append(" | ");
		});
		classLogger.error(sb.toString());
		sb.setLength(0);
		classLogger.error("Priting DC Request ATTRS ===>");
		dcRequest.getAttributeNames().forEachRemaining((attr)->{
			sb.append(attr).append(" = ").append(dcRequest.getParameter(attr)).append(" | ");
		});
		classLogger.error(sb.toString());
	}

}
