package com.kony.scaintegration.resource.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.util.CryptoText;
import com.kony.scaintegration.businessdelegate.api.UpdateVerifyFlagBusinessDelegate;
import com.kony.scaintegration.helper.Constants;
import com.kony.scaintegration.helper.ErrorCodeEnum;
import com.kony.scaintegration.resource.api.UpdateVerfifyFlagResource;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.Result;

public class UpdateVerifyFlagResourceImpl implements UpdateVerfifyFlagResource {
	private static final Logger LOG = LogManager.getLogger(UpdateVerifyFlagResourceImpl.class);

	@Override
	public Result updateFlag(DataControllerRequest request) {
		Result result = new Result();
		if (request == null)
			return ErrorCodeEnum.ERR_39003.setErrorCode(result);
		String serviceKey = request.getParameter(Constants.SERVICEKEY);
		String customerId = com.kony.dbputilities.util.HelperMethods.getCustomerIdFromSession(request);
		LOG.debug("servicekey " + serviceKey);
		LOG.debug("customerId " + customerId);
		if (isNotValidPaylod(serviceKey, customerId))
			return ErrorCodeEnum.ERR_39003.setErrorCode(result);
		try {
			customerId = CryptoText.encrypt(customerId);
		} catch (Exception e) {
			LOG.error(e);
		}
		UpdateVerifyFlagBusinessDelegate verifyFlagBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(UpdateVerifyFlagBusinessDelegate.class);

		return verifyFlagBusinessDelegate.updateFlag(serviceKey, customerId);
	}

	private boolean isNotValidPaylod(String serviceKey, String customerId) {
		return (StringUtils.isBlank(serviceKey) || StringUtils.isBlank(customerId));
	}

}
