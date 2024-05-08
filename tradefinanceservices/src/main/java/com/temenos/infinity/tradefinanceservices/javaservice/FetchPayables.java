package com.temenos.infinity.tradefinanceservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.resource.api.DashboardResource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * @author naveen.yerra
 */
public class FetchPayables implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(FetchReceivables.class);

    @Override
    public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
                         DataControllerResponse response) throws Exception {
        try {
            DashboardResource dashboardResource = DBPAPIAbstractFactoryImpl.getResource(
                    DashboardResource.class);
            Map<String, Object> input = (Map<String, Object>) inputArray[1];
            return dashboardResource.fetchPayables(input, request);
        }
        catch (Exception e) {
            return ErrorCodeEnum.ERR_26021.setErrorCode(new Result());
        }
    }

}