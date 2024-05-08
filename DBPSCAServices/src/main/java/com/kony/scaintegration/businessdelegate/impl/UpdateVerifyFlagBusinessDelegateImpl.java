package com.kony.scaintegration.businessdelegate.impl;

import java.util.HashMap;
import java.util.Map;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.error.DBPApplicationException;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.scaintegration.businessdelegate.api.UpdateVerifyFlagBusinessDelegate;
import com.kony.scaintegration.helper.Constants;
import com.kony.scaintegration.helper.ErrorCodeEnum;
import com.kony.scaintegration.helper.GetConfigParams;
import com.kony.scaintegration.helper.Helper;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;

public class UpdateVerifyFlagBusinessDelegateImpl implements UpdateVerifyFlagBusinessDelegate {
	private static final Logger LOG = LogManager.getLogger(UpdateVerifyFlagBusinessDelegateImpl.class);

	@Override
	public Result updateFlag(String serviceKey, String customerId) {
		if (GetConfigParams.getSchemaName() == null)
			try {
				GetConfigParams.setSchemaName(Helper.getConfigProperty("DBX_SCHEMA_NAME"));
			} catch (Exception e) {
				GetConfigParams.setSchemaName("dbxdb");
				LOG.error("Exception occured while fetching schema name", e);
			}
		Result result = new Result();
		Map<String, Object> inputmap = new HashMap<>();
		inputmap.put(Constants.SERVICEKEY, serviceKey);
		inputmap.put(Constants.USERIDDB, customerId);
		inputmap.put(Constants.ISVERIFIED, "true");
		try {
			String responseString = DBPServiceExecutorBuilder.builder()
					.withOperationId(
							Helper.replaceSchemaName(Constants.MFASERVICE_UPDATE, GetConfigParams.getSchemaName()))
					.withRequestParameters(inputmap).withServiceId(Constants.DBSERVICE).build().getResponse();
			LOG.debug("response " + responseString);
			JsonObject responseObject = new JsonParser().parse(responseString).getAsJsonObject();
			if (responseObject.get(Constants.MFASERVICE) == null)
				return ErrorCodeEnum.ERR_39007.setErrorCode(result);
		} catch (DBPApplicationException e) {
			LOG.error(e);
			return ErrorCodeEnum.ERR_39008.setErrorCode(result);
		}
		result.addParam(new Param(Constants.SUCCESS, Constants.TRUE, Constants.STRING));
		return result;
	}

}
