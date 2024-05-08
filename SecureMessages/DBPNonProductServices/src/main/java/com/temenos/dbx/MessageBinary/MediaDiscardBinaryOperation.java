package com.temenos.dbx.MessageBinary;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.HelperMethods;
import com.kony.dbputilities.util.URLConstants;
import com.kony.dbputilities.util.URLFinder;
import com.konylabs.middleware.api.ConfigurableParametersHelper;
import com.konylabs.middleware.api.ServicesManager;
import com.konylabs.middleware.api.ServicesManagerHelper;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Param;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.dbx.product.dto.DBXResult;
import com.temenos.dbx.product.utils.HTTPOperations;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class MediaDiscardBinaryOperation implements JavaService2 {

	private static final Logger LOG = LogManager.getLogger(MediaDiscardBinaryOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		
		ServicesManager serviceManager = ServicesManagerHelper.getServicesManager();
		ConfigurableParametersHelper configurableParametersHelper = serviceManager.getConfigurableParametersHelper();
		String isDMSIntegrationEnabled = configurableParametersHelper.getServerProperty("DMS_INTEGRATION_ENABLED");
		Result result = new Result();

		if (StringUtils.isBlank(isDMSIntegrationEnabled)) {
			isDMSIntegrationEnabled = "false";
		}

		if (isDMSIntegrationEnabled.equalsIgnoreCase("true")) {
			HashMap<String, Object> params = new HashMap<String, Object>();

			params.put("documentId", request.getParameter("mediaId"));
			result = HelperMethods.callApi(request, params, HelperMethods.getHeaders(request),
					URLConstants.DOCUMENT_STORAGE_DELETE);
			result.addParam("id", result.getParamValueByName("documentId"));
		} else {
			HashMap<String, Object> headerParams = (HashMap<String, Object>) request.getHeaderMap();
			headerParams.put("X-Kony-Authorization", request.getHeader("X-Kony-Authorization"));
			JSONObject params = new JSONObject();
			params.put("id", request.getParameter("mediaId"));
			String hostURL = URLFinder.getPathUrl(URLConstants.DBP_HOST_URL, request);
			if (StringUtils.isBlank(hostURL)) {
				LOG.error("Error while fetching DBP_HOST_URL");
				result.addParam(new Param(ErrorCodeEnum.ERROR_CODE_KEY,
						String.valueOf(ErrorCodeEnum.ERR_28023.getErrorCode())));
				result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28023.getMessage()));
				return result;
			}
			String url = hostURL + "/services/data/v1/MessageBinary/objects/media";
			DBXResult deleteResponse = HTTPOperations.sendHttpRequest(HTTPOperations.operations.DELETE, url,
					params.toString(), headerParams);
			JsonObject jsonObject;
			jsonObject = new JsonParser().parse((String) deleteResponse.getResponse()).getAsJsonObject();
			if (jsonObject.has("opstatus") && !jsonObject.get("opstatus").getAsString().equals("0")) {
				LOG.error("Error while deleting from mock database");
				result.addParam(new Param(ErrorCodeEnum.ERROR_CODE_KEY,
						String.valueOf(ErrorCodeEnum.ERR_28024.getErrorCode())));
				result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28024.getMessage()));
				return result;
			}
			if (jsonObject.has("id")) {
				result.addParam("id", jsonObject.get("id").getAsString());
			}
			else {
				result.addParam(new Param(ErrorCodeEnum.ERROR_CODE_KEY,
						String.valueOf(ErrorCodeEnum.ERR_28024.getErrorCode())));
				result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28024.getMessage()));
			}
		}
		return result;
	}
}
