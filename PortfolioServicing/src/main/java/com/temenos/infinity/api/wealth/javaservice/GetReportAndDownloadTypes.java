/**
 * 
 */
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
import com.temenos.infinity.api.wealth.resource.api.ReportAndDownloadTypesResource;

/**
 * @author m.ashwath
 *
 */
public class GetReportAndDownloadTypes implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetReportAndDownloadTypes.class);

	@Override
	public Object invoke(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		// TODO Auto-generated method stub
		Result result = new Result();
		try {
			ReportAndDownloadTypesResource reportAndDownloadTypesResource = DBPAPIAbstractFactoryImpl.getInstance()
			.getFactoryInstance(ResourceFactory.class).getResource(ReportAndDownloadTypesResource.class);

			result = reportAndDownloadTypesResource.getReportAndDownloadTypes(methodId, inputArray, request, response);
		} catch (Exception e) {
			LOG.error("Caught exception at invoke of GetReportAndDownloadTypes: ", e);
			return ErrorCodeEnum.ERR_20040.setErrorCode(new Result());
		}
		return result;
	}
}
