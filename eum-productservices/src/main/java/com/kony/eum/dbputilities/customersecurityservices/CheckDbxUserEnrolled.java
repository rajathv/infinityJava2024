package com.kony.eum.dbputilities.customersecurityservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.kony.eum.dbputilities.customersecurityservices.CheckDbxUserEnrolled;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.dbx.eum.product.usermanagement.javaservice.CheckUserEnrolledOperation;

public class CheckDbxUserEnrolled implements JavaService2 {
    private static final Logger LOG = LogManager.getLogger(CheckDbxUserEnrolled.class);
    @Override
    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
            DataControllerResponse dcResponse) throws Exception {
       return new CheckUserEnrolledOperation().invoke(methodID, inputArray, dcRequest, dcResponse);
    }
}