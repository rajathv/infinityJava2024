package com.temenos.dbx.MessageBinary;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
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

import java.util.Base64;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class MediaUploadOperation implements JavaService2 {
	private static final int MAX_ALLOWED_FILE_SIZE = 25;

	private static final Logger LOG = LogManager.getLogger(MediaUploadOperation.class);

	@Override
	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {

		ServicesManager serviceManager = ServicesManagerHelper.getServicesManager();
		ConfigurableParametersHelper configurableParametersHelper = serviceManager.getConfigurableParametersHelper();
		String isDMSIntegrationEnabled = configurableParametersHelper.getServerProperty("DMS_INTEGRATION_ENABLED");
		Result result = new Result();
		
		if(StringUtils.isBlank(isDMSIntegrationEnabled)) {
			isDMSIntegrationEnabled="false";
		}
		
		if(containsMultipleExtension(request.getParameter("documentName"))) {
			ErrorCodeEnum.ERR_12311.setErrorCode(result);
			return result;
		}
	
		if (isDMSIntegrationEnabled.equalsIgnoreCase("true")) {
			HashMap<String, Object> params = new HashMap<String, Object>();

		if(!isFileSizeValid(request.getParameter("content"))) {
			ErrorCodeEnum.ERR_12462.setErrorCode(result);
			return result;
		}
			params.put("documentName", request.getParameter("documentName"));
			params.put("version", "1.0");
			params.put("userId", request.getParameter("userId"));
			params.put("referenceId", request.getParameter("messageId"));
			params.put("content", request.getParameter("content"));
			params.put("category", "messages");
			result = HelperMethods.callApi(request, params, HelperMethods.getHeaders(request),
					URLConstants.DOCUMENT_STORAGE_UPLOAD);
			result.addParam("id", result.getParamValueByName("documentId"));
		}
		else {
			HashMap<String, Object> headerParams = (HashMap<String, Object>) request.getHeaderMap();
			headerParams.put("X-Kony-Authorization",request.getHeader("X-Kony-Authorization"));
			JSONObject params=new JSONObject();
			params.put("id",request.getParameter("id"));
			params.put("data",  request.getParameter("content"));
			params.put("fieldName", "Content");
			String hostURL = URLFinder.getPathUrl(URLConstants.DBP_HOST_URL, request);
			if (StringUtils.isBlank(hostURL)) {
				LOG.error("Error while fetching DBP_HOST_URL");
				result.addParam(new Param(ErrorCodeEnum.ERROR_CODE_KEY,
						String.valueOf(ErrorCodeEnum.ERR_28022.getErrorCode())));
				result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28022.getMessage()));
				return result;
			}
			String url = hostURL + "/services/data/v1/MessageBinary/binary/media";
			DBXResult uploadResponse = HTTPOperations.sendHttpRequest(HTTPOperations.operations.POST, url, params.toString(),
					headerParams);
			JsonObject jsonObject;
			jsonObject = new JsonParser().parse((String) uploadResponse.getResponse()).getAsJsonObject();
			if (jsonObject.has("opstatus") && !jsonObject.get("opstatus").getAsString().equals("0")) {
				LOG.error("Error while fetching base64 content");
				result.addParam(new Param(ErrorCodeEnum.ERROR_CODE_KEY,
						String.valueOf(ErrorCodeEnum.ERR_28023.getErrorCode())));
				result.addParam(new Param(ErrorCodeEnum.ERROR_MESSAGE_KEY, ErrorCodeEnum.ERR_28023.getMessage()));
				return result;
			}
			result.addParam("id",jsonObject.get("id").getAsString());
		}
		return result;
	}
	public static boolean containsMultipleExtension(String docName) {
		return docName.indexOf(".") != docName.lastIndexOf(".");
	}

	private static boolean isFileSizeValid(String base64string) {
		long fileSizeInMB = 0;
		try {
			byte[] decodedBytes = Base64.getDecoder().decode(base64string);
			fileSizeInMB = decodedBytes.length / (1024 * 1024);
		} catch (Exception e) {
			LOG.error("Error validating file:" + e);
		}
		return fileSizeInMB <= MAX_ALLOWED_FILE_SIZE;
	}

}
