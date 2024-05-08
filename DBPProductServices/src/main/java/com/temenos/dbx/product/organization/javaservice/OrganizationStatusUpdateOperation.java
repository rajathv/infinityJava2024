package com.temenos.dbx.product.organization.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.organization.resource.api.OrganizationResource;
import com.temenos.dbx.product.organization.resource.impl.OrganizationResourceImpl;

/**
 * 
 * @author sowmya.vandanapu updates the organisation status by ADMIN
 *
 */
public class OrganizationStatusUpdateOperation implements JavaService2 {

    LoggerUtil logger = new LoggerUtil(OrganizationResourceImpl.class);

    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {

            OrganizationResource organizationResource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(OrganizationResource.class);

            result = organizationResource.updateOrganizationStatus(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            request.addRequestParam_(DBPUtilitiesConstants.IS_UPDATE_SUCEES, "false");
            e.getErrorCodeEnum().setErrorCode(result);
            logger.error("Excpetion occured while updating organisation status by admin", e);
        } catch (Exception e) {
            request.addRequestParam_(DBPUtilitiesConstants.IS_UPDATE_SUCEES, "false");
            logger.error("Excpetion occured while updating organisation status by admin", e);
        }

        return result;
    }
}
