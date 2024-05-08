package com.temenos.infinity.api.savingspot.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.savingspot.resource.api.SavingsPotResource;

/**
 * @author muthukumarv-19459
 *
 */
public class GetAllCategoriesOperation implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(GetAllSavingsPotOperation.class);

	 @Override
	    public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
	            DataControllerResponse response) throws Exception {
		    Result result = new Result();
		    try {
		      SavingsPotResource savingsPotResource =
              DBPAPIAbstractFactoryImpl.getResource(SavingsPotResource.class);
	          result = savingsPotResource.getCategories(methodID, inputArray, request, response);
		    }
			catch(Exception e) {
				LOG.error("Caught exception at invoke of getCategories: ", e);
				return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
			}

			return result;
		}
}
