package com.temenos.auth.security.operation;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.auth.security.resource.api.CaptchaResource;

public class CaptchaVerifyOperation implements JavaService2 {

    LoggerUtil logger = new LoggerUtil(CaptchaVerifyOperation.class);

    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) throws Exception {
        Result result = new Result();
        try {

            CaptchaResource resource = DBPAPIAbstractFactoryImpl.getInstance()
                    .getFactoryInstance(ResourceFactory.class).getResource(CaptchaResource.class);

            result = resource.verifyCaptcha(methodID, inputArray, request, response);
        } catch (ApplicationException e) {
            e.getErrorCodeEnum().setErrorCode(result);
            logger.error("Caught exception while verifying captcha: ", e);
        } catch (Exception e) {
            ErrorCodeEnum.ERR_10347.setErrorCode(result);
            logger.error("Caught exception while verifying captcha: ", e);
        }

        return result;
    }

}
