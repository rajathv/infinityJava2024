package com.temenos.dbx.datamigrationservices.javaservice;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.datamigrationservices.resource.api.MigrateInfinityUserResource;


public class CreateSignatoryGroup implements JavaService2 {

	LoggerUtil logger = new LoggerUtil(CreateSignatoryGroup.class);
			
	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		try {
            MigrateInfinityUserResource migrationResource =
                    DBPAPIAbstractFactoryImpl.getResource(MigrateInfinityUserResource.class);
            return migrationResource.createSignatoryGroup(methodId, inputArray, request, response);
        } catch (Exception e) {
        	logger.error("Caught exception at invoke of fetchSignatoryGroupsOperation: ", e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}
	}

}
