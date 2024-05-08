package com.temenos.dbx.eum.product.usermanagement.javaservice;


import org.apache.log4j.Logger;


import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;

import com.kony.dbp.exception.ApplicationException;

import com.kony.dbputilities.util.ErrorCodeEnum;

import com.konylabs.middleware.common.JavaService2;

import com.konylabs.middleware.controller.DataControllerRequest;

import com.konylabs.middleware.controller.DataControllerResponse;

import com.konylabs.middleware.dataobject.Result;

import com.temenos.dbx.eum.product.usermanagement.resource.api.InfinityUserManagementResource;


public class CoreCustomerAccountsGetOperation implements JavaService2  {


	private static final Logger LOG = 

			Logger.getLogger(CoreCustomerAccountsGetOperation.class);

	@Override

	public Object invoke(String methodId, Object[] inputArray,

			DataControllerRequest request,

			DataControllerResponse response) throws Exception {

		Result result = new Result();

		

		try {

			InfinityUserManagementResource resource =

                    DBPAPIAbstractFactoryImpl.getResource(InfinityUserManagementResource.class);

			result = resource.getCoreCustomerAccounts(methodId, inputArray, request, response);

		} catch (ApplicationException e) {

            e.getErrorCodeEnum().setErrorCode(result);

            LOG.error("Exception occured while fetching the core customer accounts ", e);

        } catch (Exception e) {

        	LOG.error("Exception occured while fetching the core customer accounts " , e);

            ErrorCodeEnum.ERR_10762.setErrorCode(result);

        }

		return result;

	}

}
