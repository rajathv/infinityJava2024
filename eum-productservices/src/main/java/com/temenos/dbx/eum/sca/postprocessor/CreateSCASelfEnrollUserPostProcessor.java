package com.temenos.dbx.eum.sca.postprocessor;

import com.dbp.core.fabric.extn.DBPServiceExecutor;
import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.EnvironmentConfigurationsHandler;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.konylabs.middleware.common.DataPostProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.konylabs.middleware.dataobject.ResultToJSON;
import com.temenos.dbx.eum.sca.ErrorCodeEnum;
import com.temenos.dbx.eum.sca.SCAConstants;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This Postprocessor creates a user along with activation code and user details in a configured SCA vendor.
 * This is a postprocessor to /services/eumProductServices/createInfinityUser integration service under External User Management Microapp
 *
 * @author Muthukkumar Sekar
 */
public class CreateSCASelfEnrollUserPostProcessor implements DataPostProcessor2 {

	LoggerUtil logger = new LoggerUtil(CreateSCASelfEnrollUserPostProcessor.class);
	
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
			logger.debug("Cannot Create User in SCA Vendor as User Id is Empty");
			return ErrorCodeEnum.ERR_99500.setErrorCode(result);
		}
		
		String activationCode = (String) requestParameters.get(SCAConstants.PAYLOAD_ACTIVATION_CODE);
		if(StringUtils.isBlank(activationCode)) {
			logger.debug("Cannot Create User in SCA Vendor as Activation Code is Empty");
			return ErrorCodeEnum.ERR_99501.setErrorCode(result);
		}
		
		DBPServiceExecutor serviceExecutor = DBPServiceExecutorBuilder.builder()
											.withServiceId(SCAConstants.INTEGRATION_SERVICE_SCA_SERVICE_ID)
											.withOperationId(SCAConstants.SCA_CREATE_USER_OPERATION_ID)
											.withRequestParameters(requestParameters)
											.build();
		Result scaResult = serviceExecutor.getResult();
		logger.debug(ResultToJSON.convert(scaResult));
		String responseCode = scaResult.getParamValueByName("responseCode");
		if(StringUtils.isBlank(responseCode) || !responseCode.equals("0"))
			ErrorCodeEnum.ERR_99502.setErrorCode(result, scaResult.getParamValueByName("errorMessage"));
		return result;
	}
	
	private Map<String, Object> buildRequestParameters(DataControllerRequest request, Result result) throws IOException {
		String userName = result.getParamValueByName("userName");
		String activationCode = result.getParamValueByName("activationCode");

		logger.debug("User Name: "+userName);
		logger.debug("Activation Code: "+activationCode);
		
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		requestParameters.put(SCAConstants.PAYLOAD_USER_ID, userName);
		requestParameters.put(SCAConstants.PAYLOAD_ACTIVATION_CODE, activationCode);
		
		boolean isSCAPIExtensionEnabled = Boolean.parseBoolean(EnvironmentConfigurationsHandler.getServerProperty(SCAConstants.ENV_SCA_PI_EXTENSION));
		
		if(isSCAPIExtensionEnabled) {
			logger.debug("firstName: "+result.getParamValueByName("firstName"));
			logger.debug("lastName: "+result.getParamValueByName("lastName"));
			logger.debug("email: "+result.getParamValueByName("email"));
			logger.debug("phoneNumber: "+result.getParamValueByName("phoneNumber"));
			requestParameters.put(SCAConstants.PAYLOAD_FIRST_NAME, result.getParamValueByName("firstName"));
			requestParameters.put(SCAConstants.PAYLOAD_LAST_NAME, result.getParamValueByName("lastName"));
			requestParameters.put(SCAConstants.PAYLOAD_MIDDLE_NAME, "");
			requestParameters.put(SCAConstants.PAYLOAD_EMAIL_ID, result.getParamValueByName("email"));
			requestParameters.put(SCAConstants.PAYLOAD_PHONE, result.getParamValueByName("phoneNumber"));

		}
		return requestParameters;
	}

}
