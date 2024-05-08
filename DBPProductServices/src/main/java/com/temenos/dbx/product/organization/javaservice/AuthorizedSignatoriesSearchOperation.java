package com.temenos.dbx.product.organization.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.organization.resource.api.AuthorizedSignatoriesResource;

public class AuthorizedSignatoriesSearchOperation implements JavaService2 {
    LoggerUtil logger = new LoggerUtil(AuthorizedSignatoriesSearchOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {

            AuthorizedSignatoriesResource resource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(AuthorizedSignatoriesResource.class);

            result = resource.serachAuthorizedSignatories(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            logger.error("Caught exception while getting authorized signatories info: ", e);
        } catch (Exception e) {
            logger.error("Caught exception while getting authorized signatories info: ", e);
        }

        return result;
    }

}