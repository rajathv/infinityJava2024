package com.temenos.dbx.transaction.javaservice;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.ResourceFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.infinity.dbx.dbp.jwt.auth.utils.CommonUtils;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.transaction.resource.api.DirectDebitsResource;
import com.temenos.infinity.api.commons.config.EnvironmentConfigurationsHandler;

/**
 * @author sribarani.vasthan
 */
public class GetDirectDebits implements JavaService2 {
	private static final Logger LOG = LogManager.getLogger(GetSwiftCode.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		Result result = new Result();
		try {
            String PAYMENT_BACKEND = EnvironmentConfigurationsHandler.getServerAppProperty("PAYMENT_BACKEND");
            if ("MOCK".equalsIgnoreCase(PAYMENT_BACKEND) || "SRMS_MOCK".equals(PAYMENT_BACKEND)) {
                // creating an instance for DirectDebitsResource class For Mock
                // Data
                DirectDebitsResource directDebitsResource = DBPAPIAbstractFactoryImpl.getInstance()
                        .getFactoryInstance(ResourceFactory.class).getResource(DirectDebitsResource.class);

                result = directDebitsResource.getDirectDebits(methodID, inputArray, request, response);
				return result;
            }
			//For T24 Service Call
			HashMap<String, Object> params = (HashMap<String, Object>) inputArray[1];
			HashMap<String, Object> serviceHeaders = new HashMap<String, Object>();
			String serviceName = "T24ISPaymentsView";
			String operationName = "getDirectDebits";
			result = CommonUtils.callIntegrationService(request, params, serviceHeaders, serviceName, operationName,
						true);
		} catch (Exception e) {
			LOG.error("Caught exception at invoke : " + e);
			return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
		}

		return result;

	}
}
