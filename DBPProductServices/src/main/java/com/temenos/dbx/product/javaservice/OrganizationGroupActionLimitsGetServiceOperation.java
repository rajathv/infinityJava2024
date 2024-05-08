package com.temenos.dbx.product.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.resource.api.OrganizationGroupActionLimitsResource;

/**
 * 
 * @author KH2627
 * @version 1.0 Java Service end point to get organization group action limits
 */

public class OrganizationGroupActionLimitsGetServiceOperation implements JavaService2 {

    private static final Logger LOG = LogManager.getLogger(OrganizationGroupActionLimitsGetServiceOperation.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {

        Result result = new Result();
        try {
            OrganizationGroupActionLimitsResource orgGroupActionLimits = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(OrganizationGroupActionLimitsResource.class);
            result = orgGroupActionLimits.getOrganizationGroupActionLimits(methodID, inputArray, dcRequest, dcResponse);
        } catch (Exception e) {
            LOG.error("Caught exception while creating Customer: " + e);
        }

        return result;
    }
}
