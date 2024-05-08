package com.dbp.customerdetails.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.eum.product.usermanagement.resource.api.ProfileManagementResource;

public class GetCustomerDetails implements JavaService2 {

	enum Constants {
		SUCCESS("success"), FALSE("false"), CUSTOMERID("customerId"), STRING("String");
		private String name;

		private Constants(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final Logger LOG = LogManager.getLogger(GetCustomerDetails.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			ProfileManagementResource resource = DBPAPIAbstractFactoryImpl
					.getResource(ProfileManagementResource.class);
			result = resource.getUserResponseForAlerts(methodID, inputArray, request, response);
		} catch (Exception e) {
			result.addParam(
					new Param(Constants.SUCCESS.toString(), Constants.FALSE.toString(), Constants.STRING.toString()));
			LOG.error("exception occurred while fetching customer details ", e);
		}
		return result;
	}
}
