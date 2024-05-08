package com.temenos.infinity.api.visaintegrationapi.resource.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.kony.dbputilities.exceptions.HttpCallException;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.visaintegrationapi.dto.VisaDTO;
import com.temenos.infinity.api.visaintegrationapi.resource.api.VisaIntegrationApiResource;
import com.temenos.infinity.api.visaintegrationapi.utils.VisaIntegrationUtils;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.FeatureAction;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.api.visaintegrationapi.businessdelegate.api.VisaIntegrationApiBusinessDelegate;
import com.temenos.infinity.api.visaintegrationapi.constants.ErrorCodeEnum;
import com.temenos.infinity.api.visaintegrationapi.constants.VisaIntegrationConstants;

public class VisaIntegrationApiResourceImpl implements VisaIntegrationApiResource {

	private static final Logger LOG = LogManager.getLogger(VisaIntegrationApiResourceImpl.class);

	@Override
	public Result linkCard(VisaDTO visaDTO, HashMap<String, Object> headerMap, DataControllerRequest request) {

		VisaDTO linkedCardDTO = new VisaDTO();
		Result result = new Result();
		String paymentService = visaDTO.getPaymentService();

		if ((paymentService.contentEquals(VisaIntegrationConstants.PARAM_GOOGLEPAY))
				|| (paymentService.contentEquals(VisaIntegrationConstants.PARAM_SAMSUNGPAY))) {

			if (StringUtils.isBlank(visaDTO.getCardID())) {
				return ErrorCodeEnum.ERR_20059.setErrorCode(new Result());
			}
			if (StringUtils.isBlank(visaDTO.getDeviceID())) {
				return ErrorCodeEnum.ERR_20060.setErrorCode(new Result());
			}
			if (StringUtils.isBlank(visaDTO.getClientCustomerId())) {
				return ErrorCodeEnum.ERR_20061.setErrorCode(new Result());
			}
		}
		if (paymentService.contentEquals(VisaIntegrationConstants.PARAM_APPLEPAY)) {
			
			List<String> actionIds = Arrays.asList(FeatureAction.CARD_MANAGEMENT_ADD_CARD_TO_APPLE_WALLET);
			
			String permittedCreateActionIds = CustomerSession.getPermittedActionIds(request, actionIds);
			if (StringUtils.isEmpty(permittedCreateActionIds)) {
				LOG.error("feature List is missing");
				return ErrorCodeEnum.ERR_20070.setErrorCode(new Result());
			}
			

			if (StringUtils.isBlank(visaDTO.getCardID())) {
				return ErrorCodeEnum.ERR_20059.setErrorCode(new Result());
			}
			if (StringUtils.isBlank(visaDTO.getDeviceCert())) {
				return ErrorCodeEnum.ERR_20062.setErrorCode(new Result());
			}
			if (StringUtils.isBlank(visaDTO.getNonceSignature())) {
				return ErrorCodeEnum.ERR_20063.setErrorCode(new Result());
			}
			if (StringUtils.isBlank(visaDTO.getNonce())) {
				return ErrorCodeEnum.ERR_20064.setErrorCode(new Result());
			}
		}
		try {
			VisaIntegrationApiBusinessDelegate visaIntegrationApiBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(VisaIntegrationApiBusinessDelegate.class);
			linkedCardDTO = visaIntegrationApiBusinessDelegate.linkCard(visaDTO, headerMap);

			if (StringUtils.isNotBlank(linkedCardDTO.getErrorMessage())) {
				String dbpErrCode = linkedCardDTO.getCode();
				String dbpErrMessage = linkedCardDTO.getErrorMessage();
				if (StringUtils.isNotBlank(dbpErrMessage)) {
					String msg = ErrorCodeEnum.ERR_20065.getMessage(dbpErrMessage, dbpErrCode);
					return ErrorCodeEnum.ERR_20065.setErrorCode(new Result(), msg);
				}
			}
			JSONObject linkCardDTO = new JSONObject(linkedCardDTO);
			result = JSONToResult.convert(linkCardDTO.toString());

		} catch (Exception e) {
			LOG.error(e);
			LOG.debug("Failed to update customer details in api" + e);
			return ErrorCodeEnum.ERR_20041.setErrorCode(new Result());
		}
		return result;
	}

	@Override
	public Result enrollCard(VisaDTO visaDTO, HashMap<String, Object> headerMap,DataControllerRequest request) throws ApplicationException {
		VisaDTO linkedCardDTO = new VisaDTO();
		Result result = new Result();
		String paymentService = visaDTO.getPaymentService();

		List<String> actionIds = null;

		if (paymentService.contentEquals(VisaIntegrationConstants.PARAM_APPLEPAY)) {
			actionIds = Arrays.asList(FeatureAction.CARD_MANAGEMENT_ADD_CARD_TO_APPLE_WALLET);
		}
		if (paymentService.contentEquals(VisaIntegrationConstants.PARAM_GOOGLEPAY)) {
			actionIds = Arrays.asList(FeatureAction.CARD_MANAGEMENT_ADD_CARD_TO_GOOGLE_PAY);
		}
		if (paymentService.contentEquals(VisaIntegrationConstants.PARAM_SAMSUNGPAY)) {
			actionIds = Arrays.asList(FeatureAction.CARD_MANAGEMENT_ADD_CARD_TO_SAMSUNG_PAY);
		}
		
		String permittedCreateActionIds = CustomerSession.getPermittedActionIds(request, actionIds);
		if (StringUtils.isEmpty(permittedCreateActionIds)) {
			LOG.error("feature List is missing");
			return ErrorCodeEnum.ERR_20070.setErrorCode(new Result());
		}
		
		try {
			if (!(VisaIntegrationUtils.checkUserAlreadyExists(visaDTO.getCardID(),request))) {
				LOG.error("userID and cardID not linked");
				return ErrorCodeEnum.ERR_20071.setErrorCode(new Result());
			}
		} catch (Exception e) {
			LOG.error(e);
			LOG.debug("Criteria check for creating card data failed" + e);
			return ErrorCodeEnum.ERR_20041.setErrorCode(new Result());
		}

		if (StringUtils.isBlank(visaDTO.getEncCard())) {
			return ErrorCodeEnum.ERR_20066.setErrorCode(new Result());
		}
		if (StringUtils.isBlank(visaDTO.getAccountNumber())) {
			return ErrorCodeEnum.ERR_20068.setErrorCode(new Result());
		}
		if (StringUtils.isBlank(visaDTO.getExpiryMonth()) || StringUtils.isBlank(visaDTO.getExpiryYear())) {
			return ErrorCodeEnum.ERR_20069.setErrorCode(new Result());
		}

		try {
			VisaIntegrationApiBusinessDelegate visaIntegrationApiBusinessDelegate = DBPAPIAbstractFactoryImpl
					.getBusinessDelegate(VisaIntegrationApiBusinessDelegate.class);

			linkedCardDTO = visaIntegrationApiBusinessDelegate.enrollCard(visaDTO, headerMap);

			if (StringUtils.isNotBlank(linkedCardDTO.getErrorMessage())) {
				String dbpErrCode = linkedCardDTO.getCode();
				String dbpErrMessage = linkedCardDTO.getErrorMessage();
				if (StringUtils.isNotBlank(dbpErrMessage)) {
					String msg = ErrorCodeEnum.ERR_20067.getMessage(dbpErrMessage, dbpErrCode);
					return ErrorCodeEnum.ERR_20067.setErrorCode(new Result(), msg);
				}
			}
			JSONObject linkCardDTO = new JSONObject(linkedCardDTO);
			result = JSONToResult.convert(linkCardDTO.toString());

		} catch (Exception e) {
			LOG.error(e);
			LOG.debug("Failed to update customer details in api" + e);
			return ErrorCodeEnum.ERR_20041.setErrorCode(new Result());
		}
		return result;
	}

}