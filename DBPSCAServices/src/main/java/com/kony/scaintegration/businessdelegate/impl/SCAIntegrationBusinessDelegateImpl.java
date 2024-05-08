package com.kony.scaintegration.businessdelegate.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.CryptoText;
import com.kony.scaintegration.businessdelegate.api.SCAIntegrationBusinessDelegate;
import com.kony.scaintegration.helper.Constants;
import com.kony.scaintegration.helper.ErrorCodeEnum;
import com.kony.scaintegration.helper.GetConfigParams;
import com.kony.scaintegration.helper.Helper;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class SCAIntegrationBusinessDelegateImpl implements SCAIntegrationBusinessDelegate {
	private static final Logger LOG = LogManager.getLogger(SCAIntegrationBusinessDelegateImpl.class);

	@Override
	public Result getServiceKey(String payload, String userid) {
		Result result = new Result();
		Map<String, Object> inputmap = new HashMap<>();
		String servicekey = UUID.randomUUID().toString();
		inputmap.put(Constants.PAYLOAD, payload);
		inputmap.put(Constants.SERVICEKEY, servicekey);
		inputmap.put(Constants.CREATEDTS, Helper.getCurrentTimeStamp());
		if (StringUtils.isNotBlank(userid))
			inputmap.put(Constants.USERIDDB, userid);
		try {
			String responseString = DBPServiceExecutorBuilder.builder()
					.withOperationId(
							Helper.replaceSchemaName(Constants.MFASERVICE_CREATE, GetConfigParams.getSchemaName()))
					.withRequestParameters(inputmap).withServiceId(Constants.DBSERVICE).build().getResponse();
			LOG.debug("response " + responseString);
		} catch (DBPApplicationException e) {
			LOG.error(e);
			return ErrorCodeEnum.ERR_39004.setErrorCode(result);
		}
		result.addParam(new Param(Constants.SERVICEKEY, servicekey, "String"));
		return result;
	}

	@Override
	public Result getRequestPayload(String serviceKey, String userId, boolean includeIsVerifiedFlag) {
		Result result = new Result();
		Map<String, Object> inputmap = new HashMap<>();
		inputmap.put("$filter", getFilterForRequestPayload(serviceKey, userId, includeIsVerifiedFlag));
		try {
			String responseString = DBPServiceExecutorBuilder.builder()
					.withOperationId(
							Helper.replaceSchemaName(Constants.MFASERVICE_GET, GetConfigParams.getSchemaName()))
					.withRequestParameters(inputmap).withServiceId(Constants.DBSERVICE).build().getResponse();
			LOG.debug("response " + responseString);
			JsonArray mfaservice = new JsonParser().parse(responseString).getAsJsonObject().get("mfaservice")
					.getAsJsonArray();
			if (mfaservice.size() == 0) {
				return ErrorCodeEnum.ERR_39005.setErrorCode(result);
			}
			String payload = mfaservice.get(0).getAsJsonObject().get(Constants.PAYLOAD).getAsString();
			if (!includeIsVerifiedFlag) {
				payload = getDecryptedPayload(payload);
			}
			result.addParam(new Param(Constants.PAYLOAD, payload, "String"));
		} catch (DBPApplicationException e) {
			LOG.error(e);
			return ErrorCodeEnum.ERR_39006.setErrorCode(result);
		}
		return result;
	}

	private String getDecryptedPayload(String payload) {
		try {
			payload = CryptoText.decrypt(payload);
		} catch (Exception e) {
			LOG.error(e);
		}
		return payload;
	}

	private String getFilterForRequestPayload(String serviceKey, String userId, boolean includeIsVerifiedFlag) {
		String filter = Constants.SERVICEKEY + Constants.EQUAL + "'" + serviceKey + "'";

		if (StringUtils.isNotBlank(userId)) {
			filter += Constants.AND + Constants.USERIDDB + Constants.EQUAL + "'" + userId + "'";
		}
		if (includeIsVerifiedFlag) {
			filter += Constants.AND + Constants.ISVERIFIED + Constants.EQUAL + "'true'";
		}

		return filter;

	}
}
