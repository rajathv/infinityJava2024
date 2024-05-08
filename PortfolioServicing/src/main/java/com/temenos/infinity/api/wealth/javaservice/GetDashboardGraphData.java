package com.temenos.infinity.api.wealth.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.wealthservices.constants.ErrorCodeEnum;
import com.temenos.infinity.api.wealth.resource.api.WealthDashboardResource;

public class GetDashboardGraphData implements JavaService2 {

	private static final Logger log = LogManager.getLogger(GetDashboardGraphData.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
			WealthDashboardResource wealthDashboardResource = DBPAPIAbstractFactoryImpl.getInstance().
					getFactoryInstance(ResourceFactory.class).getResource(WealthDashboardResource.class);
			result = wealthDashboardResource.getDashboardGraphData(methodId, inputArray, request, response);
		}
		catch(Exception e) {
			log.error("Exception in invoke of GetDashboardGraphData:",e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
		return result;
	}


}
