package com.temenos.dbx.usermanagement.javaservice;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.usermanagement.resource.api.PartyRelationsUserManagementResource;


public class PartyRelationsCreateOperation implements JavaService2
{
	private LoggerUtil logger;
	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest dcRequest,
			DataControllerResponse dcResponse) throws Exception {
		
	    logger = new LoggerUtil(PartyRelationsCreateOperation.class);
        
        Result result = new Result();
           try {
               PartyRelationsUserManagementResource customerResource = DBPAPIAbstractFactoryImpl.getInstance()
                       .getFactoryInstance(ResourceFactory.class).getResource(PartyRelationsUserManagementResource.class);
              result = customerResource.partyRelationCreate(methodID, inputArray, dcRequest, dcResponse);
           } catch (Exception e) {
               logger.error("Caught exception while creating partyRelation: ",  e);
           }

           return result;
	}

}
