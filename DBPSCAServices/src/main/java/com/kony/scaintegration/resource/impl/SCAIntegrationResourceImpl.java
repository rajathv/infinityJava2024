package com.kony.scaintegration.resource.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.CryptoText;
import com.kony.scaintegration.businessdelegate.api.SCAIntegrationBusinessDelegate;
import com.kony.scaintegration.helper.Constants;
import com.kony.scaintegration.helper.ErrorCodeEnum;
import com.kony.scaintegration.helper.GetConfigParams;
import com.kony.scaintegration.helper.Helper;
import com.kony.scaintegration.resource.api.SCAIntegrationResource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.utils.ThreadExecutor;

public class SCAIntegrationResourceImpl implements SCAIntegrationResource {
	private static final Logger LOG = LogManager.getLogger(SCAIntegrationResourceImpl.class);

	@Override
	public Result processPayload(String methodId, DataControllerRequest request) {
		setSchema();
		Result result = new Result();
		if (request == null || methodId == null)
			return ErrorCodeEnum.ERR_39003.setErrorCode(result);
		SCAIntegrationBusinessDelegate scaBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(SCAIntegrationBusinessDelegate.class);
		if (methodId.equals("getServiceKey")) {
			String payload = request.getParameter(Constants.PAYLOAD);
			if (isNotValidRequest(request, payload))
				return ErrorCodeEnum.ERR_39003.setErrorCode(result);
			return scaBusinessDelegate.getServiceKey(payload, request.getParameter(Constants.USERID));
		} else if (methodId.equals("getRequestPayload")) {
			String serviceKey = request.getParameter(Constants.SERVICEKEY);
			if (isNotValidRequest(request, serviceKey))
				return ErrorCodeEnum.ERR_39003.setErrorCode(result);
			result = scaBusinessDelegate.getRequestPayload(serviceKey, request.getParameter(Constants.USERID), true);
			if (result.getNameOfAllParams().contains(Constants.PAYLOAD))
				deleteRecord(serviceKey, request);
		} else if (methodId.equals("getPayload")) {
			String serviceKey = request.getParameter(Constants.SERVICEKEY);
			String userId = com.kony.dbputilities.util.HelperMethods.getCustomerIdFromSession(request);
			if (isNotValidRequest(request, serviceKey))
				return ErrorCodeEnum.ERR_39003.setErrorCode(result);
			try {
				userId = CryptoText.encrypt(userId);
			} catch (Exception e) {
				LOG.error(e);
			}
			result = scaBusinessDelegate.getRequestPayload(serviceKey, userId, false);
		}
		return result;
	}

	private static void setSchema() {
		if (GetConfigParams.getSchemaName() == null)
			try {
				GetConfigParams.setSchemaName(Helper.getConfigProperty("DBX_SCHEMA_NAME"));
			} catch (Exception e) {
				GetConfigParams.setSchemaName("dbxdb");
				LOG.error("Exception occured while fetching schema name", e);
			}
	}

	private void deleteRecord(String serviceKey, DataControllerRequest request) {

		Callable<String> callable = new Callable<String>() {

			@Override
			public String call() throws Exception {
				String responseString = "";
				Map<String, Object> map = new HashMap<>();
				map.put(Constants.SERVICEKEY, serviceKey);
				try {
					responseString = DBPServiceExecutorBuilder.builder()
							.withOperationId(Helper.replaceSchemaName(Constants.MFASERVICE_DELETE,
									GetConfigParams.getSchemaName()))
							.withRequestParameters(map).withServiceId(Constants.DBSERVICE).build().getResponse();
					LOG.debug("delete response " + responseString);
				} catch (DBPApplicationException e) {
					LOG.error(e);
				}
				return responseString;
			}
		};

		try {
			ThreadExecutor.getExecutor(request).execute(callable);
		} catch (InterruptedException e) {
			LOG.error(e.getMessage());
			Thread.currentThread().interrupt();

		}

	}

	private boolean isNotValidRequest(DataControllerRequest request, String param) {
		return (request == null || StringUtils.isBlank(param));
	}

}
