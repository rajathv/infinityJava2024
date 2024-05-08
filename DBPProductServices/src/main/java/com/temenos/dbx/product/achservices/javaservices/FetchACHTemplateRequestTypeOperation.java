package com.temenos.dbx.product.achservices.javaservices;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.achservices.resource.api.ACHCommonsResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;


public class FetchACHTemplateRequestTypeOperation implements JavaService2 {

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		final Logger LOG = LogManager.getLogger(FetchACHTransactionTypesOperation.class);
		try {
			ACHCommonsResource achCommonResource = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(ResourceFactory.class).getResource(ACHCommonsResource.class);
			Result result = achCommonResource.fetchTemplateRequestType(methodID, inputArray, request, response);
			return result;
		} catch (Exception e) {
			LOG.error("Error occured while invoking FetchBBTransactionTypesOperation: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
	}
}
