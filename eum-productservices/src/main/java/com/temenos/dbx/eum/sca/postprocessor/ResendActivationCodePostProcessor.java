package com.temenos.dbx.eum.sca.postprocessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dbp.core.fabric.extn.DBPServiceExecutor;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.dbx.eum.sca.ErrorCodeEnum;
import com.temenos.dbx.eum.sca.SCAConstants;

/**
 * This Postprocessor resends or updates activation code of an already existing user with a configured SCA vendor.
 * This is a postprocessor to services/eumProductServices/SendActivationCodeForEnroll integration service under External User Management Microapp
 *
 * @author Karthik Bhuvanagiri
 */
public class ResendActivationCodePostProcessor implements DataPostProcessor2 {
	
	LoggerUtil logger = new LoggerUtil(ResendActivationCodePostProcessor.class);

	public Object execute(Result result, DataControllerRequest request, DataControllerResponse response)
			throws Exception {
		boolean isSCAEnabled = Boolean.parseBoolean(EnvironmentConfigurationsHandler.getServerProperty(SCAConstants.ENV_IS_SCA_ENABLED));
		
		if(!isSCAEnabled)
			return result;
		
		String dbpErrMsg = result.getParamValueByName(ErrorCodeEnum.ERROR_MESSAGE_KEY);
		if(StringUtils.isNotBlank(dbpErrMsg)) {
			logger.debug(ResultToJSON.convert(result));
			return result;
		}
		
		String errMsg = result.getErrMsgParamValue();
		if(StringUtils.isNotBlank(errMsg)) {
			logger.debug(ResultToJSON.convert(result));
			return result;
		}
		
		Map<String, Object> requestParameters = buildRequestParameters(request, result);
		String userId = (String) requestParameters.get(SCAConstants.PAYLOAD_USER_ID);
		if(StringUtils.isBlank(userId)) {
			logger.debug("Cannot Resend Activation Code to SCA Vendor as User Id is Empty");
			return ErrorCodeEnum.ERR_99503.setErrorCode(result);
		}
		
		String activationCode = (String) requestParameters.get(SCAConstants.PAYLOAD_ACTIVATION_CODE);
		if(StringUtils.isBlank(activationCode)) {
			logger.debug("Cannot Resend Activation Code to SCA Vendor as Activation code is Empty");
			return ErrorCodeEnum.ERR_99504.setErrorCode(result);
		}
		
		DBPServiceExecutor serviceExecutor = DBPServiceExecutorBuilder.builder()
											.withServiceId(SCAConstants.INTEGRATION_SERVICE_SCA_SERVICE_ID)
											.withOperationId(SCAConstants.SCA_RESEND_ACTIVATION_CODE_OPERATION_ID)
											.withRequestParameters(requestParameters)
											.build();
		Result scaResult = serviceExecutor.getResult();
		logger.debug(ResultToJSON.convert(scaResult));
		String responseCode = scaResult.getParamValueByName("responseCode");
		if(StringUtils.isBlank(responseCode) || !responseCode.equals("0"))
			ErrorCodeEnum.ERR_99505.setErrorCode(result, scaResult.getParamValueByName("errorMessage"));
		return result;
	}
	
	private Map<String, Object> buildRequestParameters(DataControllerRequest request, Result result) throws IOException{
		String userId = request.getParameter("userId");
		String activationCode = request.getParameter("activationCode");
		
		logger.debug("UserId: "+userId);
		logger.debug("Activation Code: "+activationCode);
		
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put(SCAConstants.PAYLOAD_USER_ID, userId);
		requestParameters.put(SCAConstants.PAYLOAD_ACTIVATION_CODE, activationCode);		
		return requestParameters;
	}

}
