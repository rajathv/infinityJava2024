package com.kony.dbputilities.customersecurityservices.preprocessor;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.dbx.dbp.jwt.auth.Authentication;
import com.infinity.dbx.dbp.jwt.auth.utils.TemenosUtils;
import com.kony.dbputilities.Constants.DBPProductServicesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.TokenUtils;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;

public class DBPDataStorageAuthPreProcesser implements DataPreProcessor2 {
	private static final Logger logger = LogManager.getLogger(DBPDataStorageAuthPreProcesser.class);

	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute(HashMap inputMap, DataControllerRequest dcRequest, DataControllerResponse response,
			Result result) throws Exception {
		try {

			dcRequest.addRequestParam_("flowType", "PreLogin");
			String authToken = TokenUtils.getMSAuthToken(dcRequest);
			logger.info("### authToken :" + authToken);
			dcRequest.addRequestParam_("Authorization", authToken);

			String deploymentPlatform = TemenosUtils.getServerEnvironmentProperty(DBPProductServicesConstants.ODMS_DEPLOYMENT_PLATFORM, dcRequest);
			if (StringUtils.isNotBlank(deploymentPlatform)) {
				if (StringUtils.equals(deploymentPlatform, DBPProductServicesConstants.AWS))
					dcRequest.addRequestParam_("x-api-key",
							TemenosUtils.getServerEnvironmentProperty(DBPProductServicesConstants.ODMS_AUTHORIZATION_KEY, dcRequest));
				if (StringUtils.equals(deploymentPlatform, DBPProductServicesConstants.AZURE))
					dcRequest.addRequestParam_("x-functions-key",
							TemenosUtils.getServerEnvironmentProperty(DBPProductServicesConstants.ODMS_AUTHORIZATION_KEY, dcRequest));
			}
			return true;
		} catch (Exception exception) {
			String errorMsg = "Error : " + exception.toString() + "..." + exception.getStackTrace()[0].toString();
			logger.error(errorMsg);
			result = ErrorCodeEnum.ERR_29034.updateResultObject(result);
		}
		return false;
	}
}